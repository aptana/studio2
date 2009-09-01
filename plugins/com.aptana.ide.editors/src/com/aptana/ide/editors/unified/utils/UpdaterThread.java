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
package com.aptana.ide.editors.unified.utils;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * UpdaterThread
 * 
 * @author Ingo Muschenetz
 */
public class UpdaterThread extends Thread
{
	/**
	 * DEFAULT_COUNTDOWN
	 */
	public static final int DEFAULT_COUNTDOWN = 200;

	int countdown = 0;
	boolean dirty = false;
	boolean running = false;
	int countdownStart;

	IUpdaterThreadUpdateable updater = null;

	/**
	 * UpdaterThread
	 * 
	 * @param updater
	 */
	public UpdaterThread(IUpdaterThreadUpdateable updater)
	{
		this(updater, DEFAULT_COUNTDOWN);
	}

	/**
	 * UpdaterThread
	 * 
	 * @param updater
	 * @param delay
	 */
	public UpdaterThread(IUpdaterThreadUpdateable updater, int delay)
	{
		this(updater, delay, "UpdaterThread"); //$NON-NLS-1$
	}

	/**
	 * UpdaterThread
	 * 
	 * @param updater
	 * @param delay
	 * @param name
	 */
	public UpdaterThread(IUpdaterThreadUpdateable updater, int delay, String name)
	{
		super(name + " - (" + delay + "ms)"); //$NON-NLS-1$ //$NON-NLS-2$
		this.updater = updater;
		this.countdownStart = delay;
		this.countdown = delay;
	}

	/**
	 * setDirty
	 */
	public void setDirty()
	{
		dirty = true;
		countdown = countdownStart;
	}

	/**
	 * @see java.lang.Thread#start()
	 */
	public void start()
	{
		running = true;
		super.start();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		while (running)
		{
			while (countdown > 0)
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.UpdaterThread_ThreadInterrupted, e);
				}
				
				countdown -= 100;
			}

			countdown = 0;

			if (dirty)
			{
				dirty = false;
				
				try
				{
					updater.onUpdaterThreadUpdate();
				}
				catch (Exception e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.UpdaterThread_ErrorThreadUpdate, e);
				}
			}
			else
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.UpdaterThread_ThreadInterrupted, e);
				}
			}
		}
	}
}
