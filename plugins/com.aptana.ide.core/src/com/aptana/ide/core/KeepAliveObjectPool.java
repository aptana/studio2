/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.core;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class KeepAliveObjectPool<T> implements IObjectPool<T>
{

	private static final int RELEASE_TIME = 15 * 60 * 1000; // 15 minutes

	private List<T> locked;
	private Map<T, Long> unlocked;
	private ConnectionReaper reaper;

	public KeepAliveObjectPool()
	{
		locked = new ArrayList<T>();
		unlocked = new LinkedHashMap<T, Long>();

		reaper = new ConnectionReaper();
		reaper.start();
	}

	public synchronized void checkIn(T t)
	{
		locked.remove(t);
		unlocked.put(t, System.currentTimeMillis());
	}

	public synchronized T checkOut()
	{
		// returns the first valid connection from the unused queue
		for (T c : unlocked.keySet())
		{
			if (validate(c))
			{
				unlocked.remove(c);
				locked.add(c);
				return c;
			}
		}

		// creates a new connection
		T c = create();
		locked.add(c);
		return c;
	}

	public synchronized void dispose()
	{
		for (T c : unlocked.keySet())
		{
			expire(c);
		}
		unlocked.clear();
		if (locked.size() > 0)
		{
			IdeLog.logImportant(AptanaCorePlugin.getDefault(),
					MessageFormat.format("Killed a connection pool that still has {0} locked items", locked.size())); //$NON-NLS-1$
			locked.clear();
		}
		reaper.exit();
	}

	protected synchronized void reap()
	{
		long now = System.currentTimeMillis();
		for (T c : unlocked.keySet())
		{
			if ((now - unlocked.get(c)) > timeToRelease())
			{
				// time to release the connection
				unlocked.remove(c);
				expire(c);
			}
			else
			{
				// keeps the connection alive unless it no longer validates
				if (!validate(c))
				{
					unlocked.remove(c);
					expire(c);
				}
			}
		}
	}

	private int timeToRelease()
	{
		return RELEASE_TIME / ((unlocked.size() + locked.size()) ^ 2);
	}

	private class ConnectionReaper extends Thread
	{

		private final long INTERVAL = 15000; // 15 seconds

		private boolean isRunning;

		public ConnectionReaper()
		{
			isRunning = true;
		}

		public void run()
		{
			while (isRunning)
			{
				try
				{
					sleep(INTERVAL);
				}
				catch (InterruptedException e)
				{
				}
				reap();
			}
		}

		public void exit()
		{
			isRunning = false;
		}
	}
}
