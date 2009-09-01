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
package com.aptana.ide.server.core.impl.servers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IStatusHandler;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IModule;
import com.aptana.ide.server.core.IModuleType;
import com.aptana.ide.server.core.IPublishOperation;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerLocator;
import com.aptana.ide.server.core.IServerManager;
import com.aptana.ide.server.core.IServerManagerListener;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.ServerManagerEvent;
import com.aptana.ide.server.core.impl.Configuration;
import com.aptana.ide.server.core.impl.PreferencesConfiguration;
import com.aptana.ide.server.core.impl.RegistryLazyObject;
import com.aptana.ide.server.core.impl.RegistryObjectCollection;
import com.aptana.ide.server.core.model.IServerProviderDelegate;

/**
 * @author Pavel Petrochenko
 */
public final class ServerManager implements IServerManager
{

	private static final int EXCEPTION_WHILE_LOADING_SERVERS_STATUS_CODE = 333;
	private static final String SERVER_COUNT = "serverCount"; //$NON-NLS-1$
	private static final String KEY_SERVERS = "servers"; //$NON-NLS-1$

	static ServerManager instance;
	private ArrayList<IServerManagerListener> listeners = new ArrayList<IServerManagerListener>();
	private ArrayList<IServer> servers = new ArrayList<IServer>();
	private ArrayList<IServer> externalservers = new ArrayList<IServer>();
	private HashMap<IServer, IServerProviderDelegate> fSrvToProvider = new HashMap<IServer, IServerProviderDelegate>();

	private ArrayList<RegistryLazyObject> serverTypes = new ArrayList<RegistryLazyObject>();
	private ArrayList<RegistryLazyObject> moduleTypes = new ArrayList<RegistryLazyObject>();
	private ArrayList<RegistryLazyObject> serverLocators = new ArrayList<RegistryLazyObject>();
	private ArrayList<RegistryLazyObject> publishOperations = new ArrayList<RegistryLazyObject>();

	static PreferencesConfiguration config = new PreferencesConfiguration(ServerCore.getDefault()
			.getPluginPreferences(), "serverManager"); //$NON-NLS-1$

	/**
	 * 
	 */
	private ServerManager()
	{

	}

	/**
	 * loads server info from preferences
	 */
	void load()
	{
		serverTypes.addAll(Arrays.asList(ServerTypeRegistry.getServerTypeRegistry().getAll()));
		moduleTypes.addAll(Arrays.asList(ServerTypeRegistry.getServerTypeRegistry().getAll()));
		serverLocators.addAll(Arrays.asList(ServerTypeRegistry.getServerTypeRegistry().getAll()));
		publishOperations.addAll(Arrays.asList(ServerTypeRegistry.getServerTypeRegistry().getAll()));
		loadExternals();
		loadServers();
	}

	private void loadExternals()
	{
		RegistryLazyObject[] all = new RegistryObjectCollection("com.aptana.ide.server.serverProvider") { //$NON-NLS-1$

			@Override
			protected RegistryLazyObject createObject(IConfigurationElement configurationElement)
			{
				return new ServerProvider(configurationElement);
			}

		}.getAll();
		List<RegistryLazyObject> objects = new ArrayList<RegistryLazyObject>();
		for (RegistryLazyObject o : all)
		{
			objects.add(o);
		}
		Collections.sort(objects, new Comparator<RegistryLazyObject>()
		{

			public int compare(RegistryLazyObject o1, RegistryLazyObject o2)
			{
				return o1.getId().compareTo(o2.getId());
			}

		});
		all = objects.toArray(new RegistryLazyObject[0]);
		for (RegistryLazyObject o : all)
		{
			try
			{
				final IServerProviderDelegate pd = ((ServerProvider) o).getDelegate();

				if (pd != null)
				{
					pd.addServerChangeListener(new IServerManagerListener()
					{

						public void serversChanged(ServerManagerEvent event)
						{
							IServer server = event.getServer();
							if (event.getKind() == ServerManagerEvent.KIND_ADDED)
							{

								externalservers.add(server);
								fSrvToProvider.put(server, pd);
							}
							if (event.getKind() == ServerManagerEvent.KIND_REMOVED)
							{
								externalservers.remove(server);
								fSrvToProvider.remove(server);
							}
							ServerManager.this.fireChange(event);
						}

					});
					IServer[] servers2 = pd.getServers();
					for (IServer s : servers2)
					{
						this.externalservers.add(s);
						fSrvToProvider.put(s, pd);
					}
				}
			}
			catch (Throwable e)
			{
				handleError(e, o);
			}
		}
	}

	/**
	 * reloads server manager
	 */
	public void reload()
	{
		servers.clear();
		loadServers();
	}

	private void loadServers()
	{
		String[] stringArrayAttribute = config.getStringArrayAttribute(KEY_SERVERS);
		for (int a = 0; a < stringArrayAttribute.length; a++)
		{
			try
			{
				IAbstractConfiguration subConfiguration = config.getSubConfiguration(stringArrayAttribute[a]);

				try
				{
					String stringAttribute = subConfiguration.getStringAttribute(IServer.KEY_TYPE);
					IServerType object = (IServerType) ServerTypeRegistry.getServerTypeRegistry().getObject(
							stringAttribute);
					if (object != null)
					{

						IServer create = object.create(subConfiguration);
						servers.add(create);
					}

					else
					{
						IdeLog.logError(ServerCore.getDefault(), StringUtils.format(
								"Server with id {0} not found", stringAttribute)); //$NON-NLS-1$
					}
				}
				catch (Throwable e)
				{
					internalHandleError(e, subConfiguration.getStringAttribute(IServer.KEY_NAME));
				}
			}
			catch (Throwable e)
			{
				internalHandleError(e, "Server Storage"); //$NON-NLS-1$
			}
		}
	}

	private void handleError(Throwable e, RegistryLazyObject o)
	{
		internalHandleError(e, o.getName());
	}

	private void internalHandleError(Throwable e, String name)
	{
		if (e instanceof CoreException)
		{
			CoreException ee = (CoreException) e;
			IStatusHandler statusHandler = DebugPlugin.getDefault().getStatusHandler(ee.getStatus());
			if (statusHandler != null)
			{
				try
				{
					statusHandler.handleStatus(ee.getStatus(), name);
				}
				catch (Throwable e1)
				{
					IdeLog.logError(ServerCore.getDefault(), e.getMessage(), e1);
				}
			}
		}

		Status ee = new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, EXCEPTION_WHILE_LOADING_SERVERS_STATUS_CODE,
				"Exception while loading servers", e); //$NON-NLS-1$
		IStatusHandler statusHandler = DebugPlugin.getDefault().getStatusHandler(ee);
		try
		{
			statusHandler.handleStatus(ee, name);
		}
		catch (Throwable e1)
		{
			IdeLog.logError(ServerCore.getDefault(), e.getMessage(), e1);
		}

		IdeLog.logError(ServerCore.getDefault(), e.getMessage(), e);
	}

	/**
	 * stores servers
	 * 
	 * @param allInfo
	 */
	void storeServers(boolean allInfo)
	{
		try
		{
			String[] ids = new String[servers.size()];
			List<String> savedIds = new ArrayList<String>();
			for (int a = 0; a < ids.length; a++)
			{
				IServer server = ((IServer) servers.get(a));
				if (!server.isTransient())
				{
					ids[a] = server.getId();
					savedIds.add(ids[a]);
					if (allInfo)
					{
						IAbstractConfiguration configuration = getConfiguration(server);
						server.storeConfiguration(configuration);
						configuration.setStringAttribute(IServer.KEY_TYPE, server.getServerType().getId());
					}
				}
			}
			config.setStringArrayAttribute(KEY_SERVERS, savedIds.toArray(new String[0]));
			ServerCore.getDefault().savePluginPreferences();
		}
		catch (Throwable e)
		{
			IdeLog.log(ServerCore.getDefault(), 0, "exception while storing servers", e); //$NON-NLS-1$
		}
	}

	/**
	 * @param srv
	 * @return returns new configuration for the server
	 */
	IAbstractConfiguration getConfiguration(IServer srv)
	{
		IAbstractConfiguration subConfiguration = config.getSubConfiguration(srv.getId());
		return subConfiguration;
	}

	/**
	 * callback on server change
	 * 
	 * @param server
	 */
	void serverChanged(IServer server)
	{
		if (server != null)
		{
			if (!servers.contains(server))
			{
				fireChange(new ServerManagerEvent(server, ServerManagerEvent.KIND_CHANGED));
				return;
			}
			IAbstractConfiguration configuration = getConfiguration(server);
			server.storeConfiguration(configuration);
			ServerCore default1 = ServerCore.getDefault();
			if (default1 != null)
			{
				default1.savePluginPreferences();
			}
			fireChange(new ServerManagerEvent(server, ServerManagerEvent.KIND_CHANGED));
		}
	}

	/**
	 * @param server
	 */
	public void addServer(IServer server)
	{
		if (server == null)
		{
			throw new IllegalArgumentException("server should not be null"); //$NON-NLS-1$
		}
		if (servers.contains(server))
		{
			throw new IllegalArgumentException("servers should  be unique"); //$NON-NLS-1$
		}
		servers.add(server);

		IAbstractConfiguration configuration = getConfiguration(server);
		server.storeConfiguration(configuration);
		configuration.setStringAttribute(IServer.KEY_TYPE, server.getServerType().getId());
		storeServers(false);

		fireChange(new ServerManagerEvent(server, ServerManagerEvent.KIND_ADDED));
	}

	/**
	 * @param server
	 */
	public void removeServer(IServer server)
	{
		IServerProviderDelegate serverProviderDelegate = fSrvToProvider.get(server);
		if (serverProviderDelegate != null)
		{
			serverProviderDelegate.removeServer(server);
		}
		IAbstractConfiguration configuration = getConfiguration(server);
		String[] propertyNames = configuration.propertyNames();
		for (int a = 0; a < propertyNames.length; a++)
		{
			configuration.removeAttribute(propertyNames[a]);
		}
		boolean remove = servers.remove(server);
		storeServers(false);
		if (remove)
		{
			fireChange(new ServerManagerEvent(server, ServerManagerEvent.KIND_REMOVED));
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#addServerManagerListener(com.aptana.ide.server.core.IServerManagerListener)
	 */
	public synchronized void addServerManagerListener(IServerManagerListener listener)
	{
		if (listener == null)
		{
			throw new IllegalArgumentException("listener should not be null"); //$NON-NLS-1$
		}
		if (listeners.contains(listener))
		{
			return;
		}
		listeners.add(listener);
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#getProjects(com.aptana.ide.server.core.IServer)
	 */
	public synchronized IProject[] getProjects(IServer server)
	{
		HashSet<IProject> resultSet = new HashSet<IProject>();
		IModule[] modules = server.getModules();
		for (int b = 0; b < modules.length; b++)
		{
			resultSet.add(modules[b].getProject());
		}
		IProject[] result = new IProject[resultSet.size()];
		resultSet.toArray(result);
		return result;
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#getServerTypes()
	 */
	public synchronized IServerType[] getServerTypes()
	{
		IServerType[] result = new IServerType[serverTypes.size()];
		serverTypes.toArray(result);
		return result;
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#getServers()
	 */
	public synchronized IServer[] getServers()
	{
		ArrayList<IServer> combined = new ArrayList<IServer>();
		combined.addAll(servers);
		combined.addAll(externalservers);
		IServer[] result = new IServer[combined.size()];
		combined.toArray(result);
		return result;
	}

	/**
	 * @see IServerManager#findServer(String)
	 */
	public synchronized IServer findServer(String id)
	{
		IServer[] servers = ServerCore.getServerManager().getServers();
		for (int i = 0; i < servers.length; i++)
		{
			IServer server = servers[i];
			if (server.getId().equals(id))
			{
				return server;
			}
		}

		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#getServers(org.eclipse.core.resources.IProject)
	 */
	public synchronized IServer[] getServers(IProject project)
	{
		ArrayList<IServer> resultList = new ArrayList<IServer>();
		l2: for (int a = 0; a < servers.size(); a++)
		{
			IServer srv = (IServer) servers.get(a);
			IModule[] modules = srv.getModules();
			for (int b = 0; b < modules.length; b++)
			{
				if (modules[b].getProject().equals(project))
				{
					resultList.add(srv);
					continue l2;
				}
			}
		}
		IServer[] result = new IServer[resultList.size()];
		resultList.toArray(result);
		return result;
	}

	/**
	 * fires event to listeners
	 * 
	 * @param event
	 */
	protected void fireChange(ServerManagerEvent event)
	{
		for (int a = 0; a < listeners.size(); a++)
		{
			IServerManagerListener listener = (IServerManagerListener) listeners.get(a);
			try
			{
				listener.serversChanged(event);
			}
			catch (Exception e)
			{
				IdeLog.logError(ServerCore.getDefault(), StringUtils.format(
						"error while notifying listener {0}", listener.toString()), e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#removeServerManagerListener(com.aptana.ide.server.core.IServerManagerListener)
	 */
	public synchronized void removeServerManagerListener(IServerManagerListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#getModules(org.eclipse.core.resources.IProject)
	 */
	public synchronized IModule[] getModules(IProject project)
	{
		ArrayList<IModule> resultList = new ArrayList<IModule>();
		for (int a = 0; a < servers.size(); a++)
		{
			IServer srv = (IServer) servers.get(a);
			IModule[] modules = srv.getModules();
			for (int b = 0; b < modules.length; b++)
			{
				if (modules[b].getProject().equals(project))
				{
					resultList.add(modules[b]);
				}
			}
		}
		IModule[] result = new IModule[resultList.size()];
		resultList.toArray(result);
		return result;
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#exists(com.aptana.ide.server.core.IServer)
	 */
	public synchronized boolean exists(IServer server)
	{
		return servers.contains(server);
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#getModuleTypes()
	 */
	public IModuleType[] getModuleTypes()
	{
		IModuleType[] result = new IModuleType[moduleTypes.size()];
		moduleTypes.toArray(result);
		return result;
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#getServerLocators()
	 */
	public synchronized IServerLocator[] getServerLocators()
	{
		IServerLocator[] result = new IServerLocator[serverLocators.size()];
		serverLocators.toArray(result);
		return result;
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#getPublishOperation(com.aptana.ide.server.core.IServerType,
	 *      com.aptana.ide.server.core.IModuleType, java.lang.String)
	 */
	public synchronized IPublishOperation getPublishOperation(IServerType server, IModuleType type, String id)
	{
		for (int a = 0; a < publishOperations.size(); a++)
		{
			IPublishOperation op = (IPublishOperation) publishOperations.get(a);
			if (op.supports(server, type))
			{
				return op;
			}
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#getPublishOperations()
	 */
	public synchronized IPublishOperation[] getPublishOperations()
	{
		IPublishOperation[] result = new IPublishOperation[publishOperations.size()];
		publishOperations.toArray(result);
		return result;
	}

	/**
	 * @return - free id
	 */
	public static String getFreeId()
	{
		int intAttribute = config.getIntAttribute(SERVER_COUNT);
		DecimalFormat dFormat = new DecimalFormat("000000000"); // force leading zeros as padding //$NON-NLS-1$
		String format = StringUtils.format("server{0}", dFormat.format(intAttribute)); //$NON-NLS-1$
		config.setIntAttribute(SERVER_COUNT, ++intAttribute);
		ServerCore.getDefault().savePluginPreferences();
		return format;
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#addServer(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public IServer addServer(IAbstractConfiguration configuration) throws CoreException
	{
		try
		{
			String stringAttribute = configuration.getStringAttribute(IServer.KEY_TYPE);
			IServerType tpe = (IServerType) ServerTypeRegistry.getServerTypeRegistry().getObject(stringAttribute);
			IServer create = tpe.create(configuration);
			addServer(create);
			return create;
		}
		catch (CoreException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR,
					"exception while adding server", e)); //$NON-NLS-1$
		}
	}

	/**
	 * removes all servers
	 */
	public void clearAll()
	{
		servers.clear();
		config.setStringArrayAttribute(KEY_SERVERS, new String[] {});
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#getInitialServerConfiguration(java.lang.String)
	 */
	public IAbstractConfiguration getInitialServerConfiguration(String serverTypeId)
	{
		Configuration config = new Configuration();
		InitializerLazyObject initializer = InitializerRegistry.getInstance().getInitializer(serverTypeId);
		config.setStringAttribute(IServer.KEY_ID, ServerManager.getFreeId());
		config.setStringAttribute(IServer.KEY_TYPE, serverTypeId);
		if (initializer != null)
		{
			initializer.initializeConfiguration(config);
		}
		return config;
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManager#getServerType(java.lang.String)
	 */
	public IServerType getServerType(String id)
	{
		return (IServerType) ServerTypeRegistry.getServerTypeRegistry().getObject(id);
	}

	/**
	 * @return instance
	 */
	public static ServerManager getInstance()
	{
		if (instance == null)
		{
			instance = new ServerManager();
			instance.load();
		}
		return instance;
	}

	/**
	 * Finds a free port in this range, inclusively on both ends of the range.
	 * 
	 * @param portRange
	 * @return - port number or -1 for no available in this range
	 */
	public static int findFreePort(int[] portRange)
	{
		return findFreePort(portRange[0], portRange[1]);
	}

	/**
	 * Finds a free port in this range, inclusively on both ends of the range.
	 * 
	 * @param startRange
	 * @param endRange
	 * @return - port number or -1 for no available in this range
	 */
	public static int findFreePort(int startRange, int endRange)
	{
		return findFreePort(startRange, endRange, null);
	}

	/**
	 * Finds a free port in this range, inclusively on both ends of the range. Also allow optional array of ports to
	 * avoid checking in the range
	 * 
	 * @param startRange
	 * @param endRange
	 * @param excluding -
	 *            ports to exclude in range
	 * @return - port number or -1 for no available in this range
	 */
	public static int findFreePort(int startRange, int endRange, int[] excluding)
	{
		int port = -1;
		ServerSocket socket = null;
		for (int i = startRange; i <= endRange; i++)
		{
			boolean valid = true;
			if (excluding != null && excluding.length > 0)
			{
				for (int e = 0; e < excluding.length; e++)
				{
					if (i == excluding[e])
					{
						valid = false;
						break;
					}
				}
			}

			if (valid)
			{
				try
				{
					socket = new ServerSocket(i);
					socket.close();
					socket = new ServerSocket();
					socket.setReuseAddress(false);
					socket.bind(new InetSocketAddress("127.0.0.1", i)); //$NON-NLS-1$
					socket.close();
					port = i;
					break;
				}
				catch (IOException e)
				{
					// Proceed to next port if opening the server socket or closing the server socket fails
				}
			}
		}
		return port;
	}
}
