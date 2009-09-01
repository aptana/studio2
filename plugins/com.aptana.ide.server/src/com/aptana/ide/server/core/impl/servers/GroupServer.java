/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.ILog;
import com.aptana.ide.server.core.IModule;
import com.aptana.ide.server.core.IOperationListener;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerListener;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.internal.core.Messages;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class GroupServer implements IServer
{

	private static final IProcess[] NO_PROCESS = new IProcess[0];

	/**
	 * The set of servers in this group
	 */
	protected Set<IServer> servers;

	private String name;
	private String host;
	private String id;
	private IServerType type;

	/**
	 * Creates a new group server
	 * 
	 * @param id
	 * @param type
	 */
	public GroupServer(String id, IServerType type)
	{
		this.servers = new TreeSet<IServer>(new Comparator<IServer>()
		{

			public int compare(IServer o1, IServer o2)
			{
				return o1.getId().compareToIgnoreCase(o2.getId());
			}

		});
		this.id = id;
		this.type = type;
	}

	/**
	 * Adds a server
	 * 
	 * @param server
	 */
	public void addServer(IServer server)
	{
		this.servers.add(server);
	}

	/**
	 * Removes a server
	 * 
	 * @param server
	 */
	public void removeServer(IServer server)
	{
		this.servers.remove(server);
	}

	/**
	 * Clears the servers in this group
	 */
	public void clearServers()
	{
		this.servers.clear();
	}

	/**
	 * Returns true if the group is empty
	 * 
	 * @return - true if empty
	 */
	public boolean isEmpty()
	{
		return this.servers.isEmpty();
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#addOperationListener(com.aptana.ide.server.core.IOperationListener)
	 */
	public void addOperationListener(IOperationListener listener)
	{
		for (IServer server : this.servers)
		{
			server.addOperationListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#addServerListener(com.aptana.ide.server.core.IServerListener)
	 */
	public void addServerListener(IServerListener listener)
	{
		for (IServer server : this.servers)
		{
			server.addServerListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canDelete()
	 */
	public IStatus canDelete()
	{
		return Status.OK_STATUS;
	}
	
	/**
	 * @see com.aptana.ide.server.core.IServer#askStopBeforeDelete()
	 */
	public IStatus askStopBeforeDelete() {
		return Status.OK_STATUS;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canHaveModule(com.aptana.ide.server.core.IModule)
	 */
	public IStatus canHaveModule(IModule module)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canModify()
	 */
	public IStatus canModify()
	{
		return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR, Messages.GroupServer_Status_CannotModify, null);
	}
	
	/**
	 * @see com.aptana.ide.server.core.IServer#canModifyInStoppedStateOnly()
	 */
	public IStatus canModifyInStoppedStateOnly() {
		return Status.OK_STATUS;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canPublish()
	 */
	public IStatus canPublish()
	{
		return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR, StringUtils.format(
				Messages.AbstractServer_DOES_NOT_SUPPORTS_PUBLISH, getName()), null);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canRestart(java.lang.String)
	 */
	public IStatus canRestart(String mode)
	{
		for (IServer server : this.servers)
		{
			if (Status.OK_STATUS.equals(server.canRestart(mode)))
			{
				return Status.OK_STATUS;
			}
		}
		return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR,
				Messages.GroupServer_Status_NoServerRestart, null);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canStart(java.lang.String)
	 */
	public IStatus canStart(String launchMode)
	{
		for (IServer server : this.servers)
		{
			if (Status.OK_STATUS.equals(server.canStart(launchMode)))
			{
				return Status.OK_STATUS;
			}
		}
		return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR,
				Messages.GroupServer_Status_NoServerStart, null);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canStop()
	 */
	public IStatus canStop()
	{
		for (IServer server : this.servers)
		{
			if (Status.OK_STATUS.equals(server.canStop()))
			{
				return Status.OK_STATUS;
			}
		}
		return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR,
				Messages.GroupServer_Status_NoServerStop, null);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#configureModule(com.aptana.ide.server.core.IModule,
	 *      com.aptana.ide.server.core.IOperationListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void configureModule(IModule module, IOperationListener listener, IProgressMonitor monitor)
	{

	}

	/**
	 * @see com.aptana.ide.server.core.IServer#configureModule(com.aptana.ide.server.core.IAbstractConfiguration,
	 *      com.aptana.ide.server.core.IOperationListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void configureModule(IAbstractConfiguration config, IOperationListener listener, IProgressMonitor monitor)
	{

	}

	/**
	 * @see com.aptana.ide.server.core.IServer#fetchStatistics()
	 */
	public String fetchStatistics()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getAssociatedServers()
	 */
	public IServer[] getAssociatedServers()
	{
		return new IServer[0];
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getDescription()
	 */
	public String getDescription()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getDocumentRoot()
	 */
	public IPath getDocumentRoot()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getHost()
	 */
	public String getHost()
	{
		return this.host;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getHostname()
	 */
	public String getHostname()
	{
		return this.host;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getId()
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getLaunch()
	 */
	public ILaunch getLaunch()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getLog()
	 */
	public ILog getLog()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getMode()
	 */
	public String getMode()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getModules()
	 */
	public IModule[] getModules()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getName()
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getPort()
	 */
	public int getPort()
	{
		return -1;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getProcesses()
	 */
	public IProcess[] getProcesses()
	{
		return NO_PROCESS;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getServerRoot()
	 */
	public IPath getServerRoot()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getServerState()
	 */
	public int getServerState()
	{
		return IServer.STATE_NOT_APPLICABLE;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getServerType()
	 */
	public IServerType getServerType()
	{
		return this.type;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getStreamsProxy()
	 */
	public IStreamsProxy getStreamsProxy()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#isConfigured(com.aptana.ide.server.core.IModule)
	 */
	public boolean isConfigured(IModule module)
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#isExternal()
	 */
	public boolean isExternal()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#isWebServer()
	 */
	public boolean isWebServer()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#publish(int, com.aptana.ide.server.core.IModule[],
	 *      com.aptana.ide.server.core.IOperationListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void publish(int kind, IModule[] modules, IOperationListener listener, IProgressMonitor monitor)
	{

	}

	/**
	 * @see com.aptana.ide.server.core.IServer#reconfigure(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void reconfigure(IAbstractConfiguration configuration) throws CoreException
	{

	}

	/**
	 * @see com.aptana.ide.server.core.IServer#removeOperationListener(com.aptana.ide.server.core.IServerListener)
	 */
	public void removeOperationListener(IServerListener listener)
	{
		for (IServer server : this.servers)
		{
			server.removeOperationListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#removeServerListener(com.aptana.ide.server.core.IServerListener)
	 */
	public void removeServerListener(IServerListener listener)
	{
		for (IServer server : this.servers)
		{
			server.removeServerListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#restart(java.lang.String, com.aptana.ide.server.core.IOperationListener,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void restart(String mode, IOperationListener listener, IProgressMonitor monitor)
	{
		for (IServer server : this.servers)
		{
			if (Status.OK_STATUS.equals(server.canRestart(mode)))
			{
				server.restart(mode, listener, monitor);
			}
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#showStatisticsInterface()
	 */
	public void showStatisticsInterface()
	{

	}

	/**
	 * @see com.aptana.ide.server.core.IServer#start(java.lang.String, com.aptana.ide.server.core.IOperationListener,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void start(String mode, IOperationListener listener, IProgressMonitor monitor)
	{

	}

	/**
	 * @see com.aptana.ide.server.core.IServer#stop(boolean, com.aptana.ide.server.core.IOperationListener,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void stop(boolean force, IOperationListener listener, IProgressMonitor monitor)
	{

	}

	/**
	 * @see com.aptana.ide.server.core.IServer#storeConfiguration(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void storeConfiguration(IAbstractConfiguration configuration)
	{

	}

	/**
	 * @see com.aptana.ide.server.core.IServer#suppliesStatistics()
	 */
	public boolean suppliesStatistics()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#suppliesStatisticsInterface()
	 */
	public boolean suppliesStatisticsInterface()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#unconfigureModule(com.aptana.ide.server.core.IModule,
	 *      com.aptana.ide.server.core.IOperationListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void unconfigureModule(IModule module, IOperationListener listener, IProgressMonitor monitor)
	{

	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	/**
	 * @return the servers
	 */
	public Set<IServer> getServers()
	{
		return servers;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#isTransient()
	 */
	public boolean isTransient()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getAllLogs()
	 */
	public ILog[] getAllLogs()
	{
		return null;
	}

}
