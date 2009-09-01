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
package com.aptana.ide.server.portal.server;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.mortbay.cometd.AbstractBayeux;
import org.mortbay.cometd.continuation.ContinuationBayeux;
import org.mortbay.cometd.continuation.ContinuationCometdServlet;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.preferences.IPreferenceConstants;
import com.aptana.ide.server.core.impl.servers.ServerManager;
import com.aptana.ide.server.jetty.ResourceBaseServlet;
import com.aptana.ide.server.jetty.comet.ClientLoader;
import com.aptana.ide.server.jetty.comet.ICometClient;
import com.aptana.ide.server.jetty.portal.ClientLister;
import com.aptana.ide.server.jetty.portal.PortalProxyServlet;
import com.aptana.ide.server.jetty.portal.PortalService;
import com.aptana.ide.server.jetty.portal.PortalServiceLoader;
import com.aptana.ide.server.jetty.portal.PortletServlet;
import com.aptana.ide.server.portal.PortalPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class MyAptanaServer
{

	/**
	 * START_PORT
	 */
	private static final int HTTP_START_PORT = 8500;

	/**
	 * END_PORT
	 */
	private static final int HTTP_END_PORT = 8599;

	/**
	 * COMET_START_PORT
	 */
	private static final int COMET_START_PORT = 8600;

	/**
	 * COMET_END_PORT
	 */
	private static final int COMET_END_PORT = 8699;

	private static final String LOCAL_HOST = "127.0.0.1"; //$NON-NLS-1$

	private static MyAptanaServer server;

	private Server jettyServer;
	private URL startURL;
	private AbstractBayeux bayeux;
	private ClientLoader loader;
	private ClientLister lister;
	private int lastHTTPPort = HTTP_START_PORT - 1;
	private int lastCometPort = COMET_START_PORT - 1;

	private MyAptanaServer()
	{
	}

	private void start()
	{
		if (lastHTTPPort == HTTP_END_PORT - 1)
		{
			lastHTTPPort = HTTP_START_PORT - 1;
		}
		lastHTTPPort = ServerManager.findFreePort(lastHTTPPort + 1, HTTP_END_PORT);

		if (lastCometPort == COMET_END_PORT - 1)
		{
			lastCometPort = COMET_START_PORT - 1;
		}
		lastCometPort = ServerManager.findFreePort(lastCometPort + 1, COMET_END_PORT);

		jettyServer = new Server(lastHTTPPort);
		jettyServer.getConnectors()[0].setHost(LOCAL_HOST);
		Connector connector = new SocketConnector();
		connector.setPort(lastCometPort);
		connector.setHost(LOCAL_HOST);
		jettyServer.addConnector(connector);
		Context httpContext = new Context(jettyServer, "/", Context.SESSIONS); //$NON-NLS-1$

		try
		{
		    StringBuilder url = new StringBuilder("http://127.0.0.1:"); //$NON-NLS-1$
		    url.append(lastHTTPPort).append("/index.html"); //$NON-NLS-1$
			url.append("?port=").append(lastCometPort); //$NON-NLS-1$
			url.append("&pv=").append(PortalServiceLoader.getLoader().getLatestPortalVersion()); //$NON-NLS-1$
			startURL = new URL(url.toString());

			PortalServiceLoader.getLoader().switchPortalLocations();

			ResourceBaseServlet servlet;
			String id;
            for (PortalService p : PortalServiceLoader.getLoader().getPortlets())
            {
                id = p.getId();
                if (id.equals("aptana")) //$NON-NLS-1$
                {
                    // the root content
                    servlet = new ResourceBaseServlet(p.getFolder());
                    servlet.setNoCache(true);
                    httpContext.addServlet(new ServletHolder(servlet), "/"); //$NON-NLS-1$
                }
                else if (!id.equals("")) //$NON-NLS-1$
                {
                    httpContext.addServlet(new ServletHolder(new PortletServlet(p)), "/portlets/" + id + "/*");  //$NON-NLS-1$//$NON-NLS-2$
                }
            }
			httpContext.addServlet(new ServletHolder(new PortalProxyServlet()), "/proxy"); //$NON-NLS-1$

			ContinuationCometdServlet cometServlet = new ContinuationCometdServlet();
			ServletHolder cometHolder = new ServletHolder(cometServlet);
			cometHolder.setInitParameter("timeout", "30000");  //$NON-NLS-1$//$NON-NLS-2$
			cometHolder.setInitParameter("multi-timeout", "1500");  //$NON-NLS-1$//$NON-NLS-2$
			cometHolder.setInitParameter("verbose", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			cometHolder.setInitParameter("rpcSupport", "true");  //$NON-NLS-1$//$NON-NLS-2$
			if (Platform.getPreferencesService().getBoolean(AptanaCorePlugin.ID,
					IPreferenceConstants.PREF_ENABLE_DEBUGGING, false, null))
			{
				cometHolder.setInitParameter("logLevel", "1"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				cometHolder.setInitParameter("logLevel", "0");  //$NON-NLS-1$//$NON-NLS-2$
			}
			httpContext.addServlet(cometHolder, "/cometd"); //$NON-NLS-1$

			jettyServer.setStopAtShutdown(true);
			jettyServer.start();

			this.bayeux = cometServlet.getBayeux();
			if (loader != null) {
				loader.destroy();
			}
			loader = ClientLoader.loadClients(this.bayeux);
			lister = new ClientLister(loader);
			lister.init(this.bayeux);
		}
		catch (IOException e)
		{
			IdeLog.logError(PortalPlugin.getDefault(), Messages.MyAptanaServer_ERR_FindRoot, e);
		}
		catch (Exception e)
		{
			IdeLog.logError(PortalPlugin.getDefault(), Messages.MyAptanaServer_ERR_StartServer, e);
		}
	}

	/**
	 * Gets the start url for the cloud page server
	 * 
	 * @return - start url
	 */
	public URL getStartURL()
	{
		return this.startURL;
	}

	/**
	 * Gets the cloud server
	 * 
	 * @return - server instance
	 */
	public static MyAptanaServer getServer()
	{
		if (server == null)
		{
			server = new MyAptanaServer();
			server.start();
		}
		return server;
	}

	/**
	 * Restarts the portal server
	 */
	public void restart()
	{
		PortalServiceLoader.getLoader().copyPortalContents();
		Job job = new Job("Downloading portal contents") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				PortalServiceLoader.getLoader().downloadPortalContents();
				return Status.OK_STATUS;
			}

		};
		job.setSystem(true);
		job.schedule();

		try
		{
			jettyServer.stop();
			jettyServer.setStopAtShutdown(false);
			if (loader != null)
			{
				for (ICometClient client : loader.getClients())
				{
					client.destroy();
				}
			}
			if (lister != null) {
				lister.destroy();
			}
			if (bayeux instanceof ContinuationBayeux)
			{
				((ContinuationBayeux) bayeux).destroy();
			}
			jettyServer.destroy();
			start();
		}
		catch (IllegalStateException e)
		{
		    IdeLog.logInfo(PortalPlugin.getDefault(), e.getLocalizedMessage());
		}
		catch (Exception e)
		{
			IdeLog.logError(PortalPlugin.getDefault(), Messages.MyAptanaServer_ERR_RestartServer, e);
		}
	}

}
