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
package com.aptana.ide.server.jetty.server;

import java.net.URI;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mortbay.jetty.NCSARequestLog;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.ILog;
import com.aptana.ide.server.core.IModule;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.impl.servers.AbstractServer;
import com.aptana.ide.server.core.impl.servers.ServerManager;
import com.aptana.ide.server.http.HttpServer;
import com.aptana.ide.server.jetty.JettyPlugin;
import com.aptana.ide.server.jetty.builder.JettyServerBuilder;
import com.aptana.ide.server.jetty.preferences.IPreferenceConstants;
import com.aptana.jaxer.connectors.servlet.interfaces.IStatisticsHandler;
import com.aptana.jaxer.connectors.servlet.interfaces.IStatisticsProvider;

/**
 * @author Pavel Petrochenko
 */
public class PreviewServer extends AbstractServer implements IStatisticsProvider
{

	private Server previewServer;
	private Context context;
	private int port;
	private String serverAddress;
	private String boundName;
	private HTMLPreviewHandler servlet;
	private IStatisticsHandler handler;

	/**
	 * SERVER_ID
	 */
	public static final String SERVER_ID = "com.aptana.ide.editor.html.preview.server"; //$NON-NLS-1$

	/**
	 * LOG_PATH
	 */
	public static final IPath LOG_PATH = new Path(FileUtils.systemTempDir).append("jetty_preview_server.log"); //$NON-NLS-1$

	/**
	 * ASSOCIATED_PORT_START
	 */
	public static final int ASSOCIATED_PORT_START = 5374;

	/**
	 * ASSOCIATE_PORT_END
	 */
	public static final int ASSOCIATE_PORT_END = 5383;

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#canDelete()
	 */
	public IStatus canDelete()
	{
		return new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, IStatus.ERROR, Messages.PreviewServer_Status_InternalDeletion,
				null);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#installConfig(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	protected void installConfig(IAbstractConfiguration configuration)
	{
		this.port = configuration.getIntAttribute(IServer.KEY_PORT);
		this.boundName = configuration.getStringAttribute(IServer.KEY_ASSOCIATION_SERVER_ID);
		super.installConfig(configuration);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#storeConfiguration(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void storeConfiguration(IAbstractConfiguration config)
	{
		config.setIntAttribute(IServer.KEY_PORT, port);
		config.setStringAttribute(IServer.KEY_ASSOCIATION_SERVER_ID, boundName);
		super.storeConfiguration(config);
	}

	/**
	 * Preview server constructor
	 * 
	 * @param type
	 * @param configuration
	 * @param state
	 */
	public PreviewServer(IServerType type, IAbstractConfiguration configuration, int state)
	{
		super(type, configuration);
		setServerState(state);
	}

	private void createContext()
	{
		if (servlet == null)
		{
			servlet = new HTMLPreviewHandler();
		}
		servlet.setStatisticsHandler(handler);
		context = new Context(this.previewServer, "/", Context.SESSIONS); //$NON-NLS-1$
		context.addServlet(new ServletHolder(servlet), "/"); //$NON-NLS-1$

		if (this.boundName.length() > 0)
		{
			IServer server = ServerManager.getInstance().findServer(this.boundName);
			// Use the bound server hostname and port when building this server (the bound server could be Jaxer in
			// which case the filters need to know the jaxer port)
			if (server != null)
			{
				JettyServerBuilder.getInstance().buildServer(context, SERVER_ID, this.boundName, server.getHostname(),
						server.getPort(), new ProjectDocumentResolver());
			}
			// Fall back to the preview server hostname and port so that building will always take place and extension
			// point servlets and filters will be added to this server's context
			else
			{
				JettyServerBuilder.getInstance().buildServer(context, SERVER_ID, this.boundName, this.getHostname(),
						this.getPort(), new ProjectDocumentResolver());
			}
		}
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#restart(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus restart(String mode, IProgressMonitor monitor)
	{
		getPreferenceStore().setValue(IPreferenceConstants.ENABLE_BUILTIN_PREVIEW, false);
		IStatus status = Status.OK_STATUS;
		if (context != null)
		{
			try
			{
				context.stop();
				setServerState(IServer.STATE_STOPPED);
				serverChanged();
			}
			catch (Exception e)
			{
				IdeLog.logError(JettyPlugin.getDefault(), Messages.PreviewServer_ERR_StopContext, e);
			}
			context.destroy();
		}
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
		}
		createContext();
		try
		{
			context.start();
			setServerState(IServer.STATE_STARTED);
			getPreferenceStore().setValue(IPreferenceConstants.ENABLE_BUILTIN_PREVIEW, true);
			serverChanged();
		}
		catch (Exception e)
		{
			if (previewServer != null)
			{
				try
				{
					previewServer.stop();
					setServerState(IServer.STATE_STOPPED);
					serverChanged();
				}
				catch (Exception e1)
				{
					IdeLog.logError(JettyPlugin.getDefault(), Messages.PreviewServer_ERR_StopServer, e);
				}
			}
			getPreferenceStore().setValue(IPreferenceConstants.ENABLE_BUILTIN_PREVIEW, false);
			status = new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, IStatus.ERROR,
					Messages.PreviewServer_Status_RestartException, e);
		}
		return status;
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#canModify()
	 */
	public IStatus canModify()
	{
		return new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, IStatus.ERROR,
				Messages.PreviewServer_Status_InternalModification, null);
	}

	private void launchServer() throws Exception
	{
		this.previewServer = new Server(port);
		RequestLogHandler logger = new RequestLogHandler();
		NCSARequestLog log = new NCSARequestLog(LOG_PATH.toFile().getAbsolutePath());
		log.setLogCookies(true);
		log.setLogLatency(true);
		log.setRetainDays(90);
		log.setAppend(true);
		log.setExtended(true);
		log.setLogTimeZone("GMT"); //$NON-NLS-1$
		logger.setRequestLog(log);
		this.previewServer.addHandler(logger);
		this.previewServer.getConnectors()[0].setHost(serverAddress = HttpServer.getServerAddress());
		createContext();
		this.previewServer.setStopAtShutdown(true);
		this.previewServer.start();
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#start(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus start(String mode, IProgressMonitor monitor)
	{
		try
		{
			try
			{
				try
				{
					if (this.previewServer != null)
					{
						this.previewServer.destroy();
					}
					if (this.servlet != null)
					{
						this.servlet.setStatisticsHandler(null);
					}
				}
				catch (Exception e)
				{
					IdeLog.logInfo(JettyPlugin.getDefault(), Messages.PreviewServer_INF_DestroyError, e);
				}
				launchServer();
			}
			catch (Exception e)
			{
				IdeLog.logInfo(JettyPlugin.getDefault(), Messages.PreviewServer_INF_BindException + port, e);
				try
				{
					int[] portRange = HttpServer.getPortRange();
					port = ServerManager.findFreePort(portRange[0], portRange[1]);
					launchServer();
				}
				catch (Exception e1)
				{
					IdeLog.logInfo(JettyPlugin.getDefault(),
							Messages.PreviewServer_INF_BindPortException + port, e1);
					throw e1;
				}
			}
			setServerState(IServer.STATE_STARTED);
			serverChanged();
			getPreferenceStore().setValue(IPreferenceConstants.ENABLE_BUILTIN_PREVIEW, true);
			return Status.OK_STATUS;
		}
		catch (Exception e)
		{
			getPreferenceStore().setValue(IPreferenceConstants.ENABLE_BUILTIN_PREVIEW, false);
			return new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, IStatus.ERROR,
					Messages.PreviewServer_INF_StartException, e);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#stop(boolean,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus stop(boolean force, IProgressMonitor monitor)
	{
		getPreferenceStore().setValue(IPreferenceConstants.ENABLE_BUILTIN_PREVIEW, false);
		try
		{
			if (previewServer != null && previewServer.isRunning())
			{
				previewServer.stop();
			}
			setServerState(IServer.STATE_STOPPED);
			serverChanged();
			return Status.OK_STATUS;
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, IStatus.ERROR,
					Messages.PreviewServer_Status_StopException, e);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canHaveModule(com.aptana.ide.server.core.IModule)
	 */
	public IStatus canHaveModule(IModule module)
	{
		return new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, IStatus.ERROR,
				Messages.PreviewServer_Status_Unsupported, null);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#getConfigurationDescription()
	 */
	public String getConfigurationDescription()
	{
		return Messages.PreviewServer_Description;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getLog()
	 */
	public ILog getLog()
	{
		return new ILog()
		{

			public boolean exists()
			{
				return LOG_PATH.toFile().exists();
			}

			public URI getURI()
			{
				return LOG_PATH.toFile().toURI();
			}

		};
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getHost()
	 */
	public String getHost()
	{
		return StringUtils.format("{0}:{1}", new String[] { getHostname(), Integer.toString(getPort()) }); //$NON-NLS-1$
	}

	/**
	 * @return Can we serve web content?
	 */
	public boolean isWebServer()
	{
		return true;
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
		return port;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getDocumentRoot()
	 */
	public IPath getDocumentRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot().getLocation();
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#fetchStatistics()
	 */
	public String fetchStatistics()
	{
		return null;
	}

	/**
	 * @see com.aptana.jaxer.connectors.servlet.interfaces.IStatisticsProvider#getStatisticsHandler()
	 */
	public IStatisticsHandler getStatisticsHandler()
	{
		return this.handler;
	}

	/**
	 * @see com.aptana.jaxer.connectors.servlet.interfaces.IStatisticsProvider#setStatisticsHandler(com.aptana.jaxer.connectors.servlet.interfaces.IStatisticsHandler)
	 */
	public void setStatisticsHandler(IStatisticsHandler handler)
	{
		this.handler = handler;
		if (this.servlet != null)
		{
			this.servlet.setStatisticsHandler(this.handler);
		}
	}

	private static IPreferenceStore getPreferenceStore()
	{
		return JettyPlugin.getDefault().getPreferenceStore();
	}

}
