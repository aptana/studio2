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
package com.aptana.ide.server.jetty;

import java.net.URI;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.IProcess;
import org.mortbay.jetty.NCSARequestLog;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.ILog;
import com.aptana.ide.server.core.IModule;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.impl.servers.AbstractServer;
import com.aptana.ide.server.core.impl.servers.ServerManager;
import com.aptana.ide.server.http.HttpServer;
import com.aptana.ide.server.jetty.builder.JettyServerBuilder;

/**
 * @author Pavel Petrochenko
 */
public class JettyServer extends AbstractServer
{

	private static final IProcess[] NO_PROCESS = new IProcess[0];
	private int port;
	private String serverAddress;
	private Server server;
	private String boundName;
	private String documentRoot;
	private IPath log;

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#installConfig(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	@Override
	protected void installConfig(IAbstractConfiguration configuration)
	{
		this.port = configuration.getIntAttribute(IServer.KEY_PORT);
		this.documentRoot = configuration.getStringAttribute(JettyServerTypeDelegate.KEY_SERVERID);
		this.boundName = configuration.getStringAttribute(IServer.KEY_ASSOCIATION_SERVER_ID);
		String id = configuration.getStringAttribute(IServer.KEY_ID);
		this.log = new Path(FileUtils.systemTempDir).append("jetty_server_" + id + ".log"); //$NON-NLS-1$ //$NON-NLS-2$
		super.installConfig(configuration);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#storeConfiguration(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void storeConfiguration(IAbstractConfiguration config)
	{
		config.setIntAttribute(IServer.KEY_PORT, port);
		config.setStringAttribute(JettyServerTypeDelegate.KEY_SERVERID, documentRoot);
		config.setStringAttribute(IServer.KEY_ASSOCIATION_SERVER_ID, boundName);
		super.storeConfiguration(config);
	}

	/**
	 * @param type
	 * @param configuration
	 */
	public JettyServer(IServerType type, IAbstractConfiguration configuration)
	{
		super(type, configuration);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#restart(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus restart(String mode, IProgressMonitor monitor)
	{
		try
		{
			stop(true, monitor);
			serverChanged();
			// wait a little
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				IdeLog.log(JettyPlugin.getDefault(), IStatus.WARNING, "interrupted while sleeping", e); //$NON-NLS-1$
			}
			start(mode, monitor);
			return Status.OK_STATUS;
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, IStatus.ERROR, StringUtils.format(
					Messages.JettyServer_START_EXCEPTION, getName()), e);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#start(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus start(String mode, IProgressMonitor monitor)
	{
		IResource findMember = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(new Path(documentRoot));
		server = new Server(port);
		RequestLogHandler logger = new RequestLogHandler();
		NCSARequestLog log = new NCSARequestLog(this.log.toFile().getAbsolutePath());
		log.setLogCookies(true);
		log.setLogLatency(true);
		log.setRetainDays(90);
		log.setAppend(true);
		log.setExtended(true);
		log.setLogTimeZone("GMT"); //$NON-NLS-1$
		logger.setRequestLog(log);
		server.addHandler(logger);
		server.getConnectors()[0].setHost(HttpServer.getServerAddress());
		try
		{
			Context context = new Context(server, "/", Context.SESSIONS); //$NON-NLS-1$
			ResourceBaseServlet servlet = new ResourceBaseServlet(findMember.getLocation().toFile().getAbsolutePath());
			servlet.setNoCache(true);
			context.addServlet(new ServletHolder(servlet), "/"); //$NON-NLS-1$

			if (this.boundName.length() > 0)
			{
				IServer server = ServerManager.getInstance().findServer(this.boundName);
				if (server != null)
				{
					JettyServerBuilder.getInstance().buildServer(context, JettyServerTypeDelegate.ID, this.boundName,
							server.getHostname(), server.getPort(), new JettyDocumentRootResolver(documentRoot));
				}
			}
			server.setStopAtShutdown(true);
			server.start();
			setServerState(IServer.STATE_STARTED);
			return Status.OK_STATUS;
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, IStatus.ERROR, StringUtils.format(
					Messages.JettyServer_Status_Exception, getName()), e);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#stop(boolean,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus stop(boolean force, IProgressMonitor monitor)
	{
		try
		{
			server.stop();
			server.destroy();
			server = null;
			setServerState(IServer.STATE_STOPPED);
			return Status.OK_STATUS;
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, IStatus.ERROR, StringUtils.format(
					Messages.JettyServer_STOP_EXCEPTION, getName()), e);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canHaveModule(com.aptana.ide.server.core.IModule)
	 */
	public IStatus canHaveModule(IModule module)
	{
		return new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, IStatus.ERROR, StringUtils.format(
				Messages.JettyServer_Status_Exception, getName()), null);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#getConfigurationDescription()
	 */
	public String getConfigurationDescription()
	{
		return StringUtils.format(Messages.JettyServer_DESCRIPTION, new Object[] { port, documentRoot });
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getLog()
	 */
	public ILog getLog()
	{
		return new ILog()
		{

			public URI getURI()
			{
				return log.toFile().toURI();
			}

			public boolean exists()
			{
				return log.toFile().exists();
			}

		};
	}

	/**
	 * @return no processes
	 */
	public IProcess[] getProcesses()
	{
		return NO_PROCESS;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getHost()
	 */
	public String getHost()
	{
		return getHostname()+":" + this.port; //$NON-NLS-1$
	}

	/**
	 * @return Can we serve web content?
	 */
	public boolean isWebServer()
	{
		return true;
	}

	/**
	 * @see IServer#getAssociatedServers()
	 */
	public IServer[] getAssociatedServers()
	{
		if (this.boundName.length() > 0)
		{
			IServer server = ServerCore.getServerManager().findServer(this.boundName);
			if (server != null)
			{
				return new IServer[] { server };
			}
		}

		return new IServer[0];
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getHostname()
	 */
	public String getHostname()
	{
		return serverAddress == null ? "127.0.0.1" : serverAddress; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getPort()
	 */
	public int getPort()
	{
		return this.port;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getDocumentRoot()
	 */
	public IPath getDocumentRoot()
	{
		return new Path(documentRoot);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#fetchStatistics()
	 */
	public String fetchStatistics()
	{
		return null;
	}
}
