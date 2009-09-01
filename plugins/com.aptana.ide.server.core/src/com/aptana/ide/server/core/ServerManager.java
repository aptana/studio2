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
package com.aptana.ide.server.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.aptana.ide.server.IServerListener;
import com.aptana.ide.server.IServerRunnable;

/**
 * @author Ingo Muschenetz
 */
public final class ServerManager implements IServerListener
{
	private static ServerManager instance;
	private List<IServerRunnable> serverList;
	private Map<String, IServerRunnable> serverTable;

	/**
	 * listeners
	 */
	public List<IServerListener> listeners;

	/*
	 * Constructors
	 */

	/**
	 * ServerManager
	 */
	private ServerManager()
	{
		serverList = new ArrayList<IServerRunnable>();
		serverTable = new HashMap<String, IServerRunnable>();
		listeners = new ArrayList<IServerListener>();
	}

	/*
	 * Methods
	 */

	/**
	 * getInstance
	 * 
	 * @return ServerManager
	 */
	public static ServerManager getInstance()
	{
		if (instance == null)
		{
			instance = new ServerManager();
		}
		return instance;
	}

	/**
	 * launchServer
	 * 
	 * @param server
	 * @param configuration
	 * @param mode
	 * @param launch
	 * @param monitor
	 */
	public void launchServer(IServerRunnable server, ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor)
	{
		server.addServerListener(this);
		server.start(configuration, mode, launch, monitor);
	}

	/**
	 * getRunningServers
	 * 
	 * @return IServerRunnable[]
	 */
	public IServerRunnable[] getRunningServers()
	{
		return serverList.toArray(new IServerRunnable[serverList.size()]);
	}

	/**
	 * getServer
	 * 
	 * @param serverName
	 * @return IServerRunnable
	 */
	public IServerRunnable getServer(String serverName)
	{
		return serverTable.get(serverName);
	}

	/**
	 * addServerListener
	 * 
	 * @param listener
	 */
	public void addServerListener(IServerListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * removeServerListener
	 * 
	 * @param listener
	 */
	public void removeServerListener(IServerListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * fireServerStarted
	 * 
	 * @param server
	 */
	private void fireServerStarted(IServerRunnable server)
	{
		for (IServerListener listener : listeners)
		{
			listener.serverStarted(server);
		}
	}

	/**
	 * fireServerStopped
	 * 
	 * @param server
	 */
	private void fireServerStopped(IServerRunnable server)
	{
		for (IServerListener listener : listeners)
		{
			listener.serverStopped(server);
		}
	}

	/**
	 * fireServerChanged
	 * 
	 * @param server
	 */
	private void fireServerChanged(IServerRunnable server)
	{
		for (IServerListener listener : listeners)
		{
			listener.serverChanged(server);
		}
	}

	/**
	 * @see com.aptana.ide.server.IServerListener#serverStarted(com.aptana.ide.server.IServerRunnable)
	 */
	public void serverStarted(final IServerRunnable server)
	{
		// register the started server
		serverList.add(server);
		serverTable.put(server.getLaunchConfiguration().getName(), server);

		// notify listeners that the server was started
		fireServerStarted(server);
	}

	/**
	 * @see com.aptana.ide.server.IServerListener#serverStopped(com.aptana.ide.server.IServerRunnable)
	 */
	public void serverStopped(final IServerRunnable server)
	{
		// unregister the stopped server
		server.removeServerListener(this);
		serverList.remove(server);
		serverTable.remove(server.getLaunchConfiguration().getName());

		// notify listeners that the server was stopped
		fireServerStopped(server);
	}

	/**
	 * @see com.aptana.ide.server.IServerListener#serverChanged(com.aptana.ide.server.IServerRunnable)
	 */
	public void serverChanged(final IServerRunnable server)
	{
		// notify listeners that the server was change
		fireServerChanged(server);
	}
}
