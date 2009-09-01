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
package com.aptana.ide.server.jetty;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.http.HttpServer;
import com.aptana.ide.server.logging.IHttpLog;
import com.aptana.ide.server.logging.SystemHttpLog;
import com.aptana.ide.server.resolvers.FolderResourceResolver;
import com.aptana.ide.server.resolvers.IHttpResourceResolver;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class JettyHttpServer
{

	/*
	 * Fields
	 */
	private ServerSocket _socketServer;
	private int _portStart;
	private int portUsed;
	private int _portEnd;
	private IHttpResourceResolver _resourceResolver;
	private IHttpLog _logger;
	private int _timeout = 5000;
	private String path;
	private Server jettyServer;

	/*
	 * Properties
	 */

	/**
	 * getLogger
	 * 
	 * @return IHttpLog
	 */
	public IHttpLog getLogger()
	{
		return this._logger;
	}

	/**
	 * Sets the current log to write to
	 * 
	 * @param log
	 *            The log to which to write
	 */
	public void setLogger(IHttpLog log)
	{
		this._logger = log;
	}

	/**
	 * getResourceResolver
	 * 
	 * @return IHttpResourceResolver
	 */
	public IHttpResourceResolver getResourceResolver()
	{
		return this._resourceResolver;
	}

	/**
	 * getSocketServer
	 * 
	 * @return ServerSocket
	 */
	public ServerSocket getSocketServer()
	{
		return this._socketServer;
	}

	/**
	 * Returns the port the server is listening to
	 * 
	 * @return The port listened to
	 */
	public int getPort()
	{
		return this.portUsed;
	}

	/*
	 * Constructors
	 */

	/**
	 * Creates a new HttpServer
	 * 
	 * @param path
	 *            The path to the default directory
	 * @param port
	 *            The port on which to listen for requests
	 */
	public JettyHttpServer(String path, int port)
	{
		this(new FolderResourceResolver(new File(path)), port, port);
		this.path = path;
	}

	/**
	 * Creates a new HttpServer
	 * 
	 * @param path
	 *            The path to the default directory
	 * @param port
	 *            The start of a range of ports on which to listen for requests
	 * @param endPort
	 *            The end of a range of ports
	 */
	public JettyHttpServer(String path, int port, int endPort)
	{
		this(new FolderResourceResolver(new File(path)), port, endPort);
		this.path = path;
	}

	/**
	 * Creates a new HttpServer
	 * 
	 * @param resourceResolver
	 *            The resolver for resources
	 * @param port
	 *            The port to listen on
	 */
	public JettyHttpServer(IHttpResourceResolver resourceResolver, int port)
	{
		this(resourceResolver, port, port);
	}

	/**
	 * Creates a new HttpServer
	 * 
	 * @param resourceResolver
	 *            The resolver for resources
	 * @param portStartRange
	 *            The start of a range of ports on which to listen for requests
	 * @param portEndRange
	 *            The end of a range of ports
	 */
	public JettyHttpServer(IHttpResourceResolver resourceResolver, int portStartRange, int portEndRange)
	{
		this(resourceResolver, portStartRange, portEndRange, 5000);
	}

	/**
	 * Creates a new HttpServer
	 * 
	 * @param resourceResolver
	 *            The resolver for resources
	 * @param portStartRange
	 *            The start of a range of ports on which to listen for requests
	 * @param portEndRange
	 *            The end of a range of ports
	 * @param timeout
	 *            The default timeout for requests
	 */
	public JettyHttpServer(IHttpResourceResolver resourceResolver, int portStartRange, int portEndRange, int timeout)
	{
		this._resourceResolver = resourceResolver;
		this._portStart = portStartRange;
		this._portEnd = portEndRange;
		this._logger = SystemHttpLog.getInstance();
		this._timeout = timeout;
		this.portUsed = -1;
	}

	/*
	 * Methods
	 */

	/**
	 * Starts the server
	 * 
	 * @throws IOException
	 *             Thrown if the server is unable to start
	 */
	public void start() throws IOException
	{
		for (int i = this._portStart; i <= this._portEnd; i++)
		{
			try
			{
				jettyServer = new Server(i);
				jettyServer.getConnectors()[0].setHost(HttpServer.getServerAddress());
				HandlerList handlers = new HandlerList();
				ResourceHandler resource_handler = new ResourceHandler();
				resource_handler.setResourceBase(path);
				handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
				jettyServer.setHandler(handlers);
				jettyServer.setStopAtShutdown(true);
				jettyServer.start();
				this.portUsed = i;
				break;
			}
			catch (Exception e)
			{
				throw new IOException(e.getMessage());
			}
		}

		// log where we're listening
		this._logger.logTrace(StringUtils.format("HttpServer listening on port {0}", this.getPort())); //$NON-NLS-1$
	}

	/**
	 * Stops the server
	 * 
	 * @throws IOException
	 */
	public void stop() throws IOException
	{
		if (this.jettyServer != null)
		{
			try
			{
				this.jettyServer.stop();

				for (int i = 0; i < 10; i++)
				{
					if (this.jettyServer.isStopping())
					{
						Thread.sleep(500);
					}
					if (this.jettyServer.isStopped())
					{
						break;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			this.jettyServer.destroy();
		}
	}

}
