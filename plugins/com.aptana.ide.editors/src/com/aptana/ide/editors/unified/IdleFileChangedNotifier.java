/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.editors.unified;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.ide.editors.unified.utils.IUpdaterThreadUpdateable;
import com.aptana.ide.editors.unified.utils.UpdaterThread;

/**
 * Singleton for managing the delayed firing of file change events.
 * 
 * @author Spike Washburn
 */
class IdleFileChangedNotifier implements IUpdaterThreadUpdateable
{
	private static IdleFileChangedNotifier instance;
	
	/**
	 * updaterThread
	 */
	protected UpdaterThread updaterThread;

	/**
	 * events
	 */
	protected Map<FileService,FileContextContentEvent> events = new HashMap<FileService,FileContextContentEvent>();

	/**
	 * sourceList
	 */
	protected List<FileService> sourceList = new ArrayList<FileService>();

	/**
	 * IdleFileChangedNotifier
	 */
	protected IdleFileChangedNotifier()
	{
		init();
	}

	/**
	 * init
	 */
	protected void init()
	{
		updaterThread = new UpdaterThread(this, UpdaterThread.DEFAULT_COUNTDOWN, "IdleFileChangedNotifier"); //$NON-NLS-1$
		updaterThread.start();
	}

	/**
	 * @see com.aptana.ide.editors.unified.utils.IUpdaterThreadUpdateable#onUpdaterThreadUpdate()
	 */
	public void onUpdaterThreadUpdate()
	{
		List<FileContextContentEvent> delayedEvents = getEventsList();

		// fire the events
		for (int i = 0; i < delayedEvents.size(); i++)
		{
			FileContextContentEvent evt = delayedEvents.get(i);
			
			if (evt != null)
			{
				IFileService source = evt.getSource();
				
				if (source instanceof FileService)
				{
					fireContentChangedEvent(evt, source);
				}
				else
				{
					throw new IllegalStateException(Messages.IdleFileChangedNotifier_SourceNotFileService);
				}
			}
		}
	}

	/**
	 * getEventsList
	 * 
	 * @return ArrayList
	 */
	protected List<FileContextContentEvent> getEventsList()
	{
		List<FileContextContentEvent> delayedEvents = new ArrayList<FileContextContentEvent>();
		// HashMap sourceToEventsTable = events;
		
		// gather up the list of events we need to fire
		synchronized (this)
		{
			for (int i = 0; i < sourceList.size(); i++)
			{
				delayedEvents.add(events.get(sourceList.get(i)));
			}

			events = new HashMap<FileService,FileContextContentEvent>();
			sourceList.clear();

		}
		
		return delayedEvents;
	}

	/**
	 * fireContentChangedEvent
	 * 
	 * @param evt
	 * @param source
	 */
	protected void fireContentChangedEvent(FileContextContentEvent evt, IFileService source)
	{
		((FileService) source).fireDelayedContentChangedEvent(evt);
	}

	/**
	 * queueContentChangedEvent
	 * 
	 * @param fileService
	 * @param evt
	 */
	public synchronized void queueContentChangedEvent(FileService fileService, FileContextContentEvent evt)
	{
		FileContextContentEvent oldEvent = events.get(fileService);
		
		if (oldEvent == null)
		{
			sourceList.add(fileService);
		}
		
		events.put(fileService, evt);

		updaterThread.setDirty();
	}

	/**
	 * removeContentChangedEvent
	 * 
	 * @param fileService
	 */
	public synchronized void removeContentChangedEvent(FileService fileService)
	{
		if (events.containsKey(fileService))
		{
			events.remove(fileService);
		}
	}

	/**
	 * instance
	 * 
	 * @return IdleFileChangedNotifier
	 */
	public static synchronized IdleFileChangedNotifier instance()
	{
		if (instance == null)
		{
			instance = new IdleFileChangedNotifier();
		}
		
		return instance;
	}

	/**
	 * IChangeListener
	 * 
	 * @author Ingo Muschenetz
	 */
	interface IChangeListener
	{
		/**
		 * onChangeListenerEvent
		 */
		void onChangeListenerEvent();
	}
}
