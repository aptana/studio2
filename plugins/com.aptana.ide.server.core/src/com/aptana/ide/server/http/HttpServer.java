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
package com.aptana.ide.server.http;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Preferences;

import com.aptana.ide.core.SocketUtil;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.IHttpLaunchConfigurationConstants;
import com.aptana.ide.server.core.ServerCorePlugin;
import com.aptana.ide.server.logging.IHttpLog;
import com.aptana.ide.server.logging.SystemHttpLog;
import com.aptana.ide.server.resolvers.FolderResourceResolver;
import com.aptana.ide.server.resolvers.IHttpResourceResolver;

/**
 * An HttpServer for serving up files
 * 
 * @author Kevin Lindsey
 */
public class HttpServer
{
	/*
	 * Fields
	 */
	private ServerSocket _socketServer;
	private int _portStart;
	private int _portEnd;
	private IHttpResourceResolver _resourceResolver;
	private ServerThreadRunnable _serverThreadRunnable;
	private IHttpLog _logger;
	private int _timeout = 5000;

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
		int result = -1;

		if (this._socketServer != null)
		{
			result = this._socketServer.getLocalPort();
		}

		return result;
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
	public HttpServer(String path, int port)
	{
		this(new FolderResourceResolver(new File(path)), port, port);
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
	public HttpServer(String path, int port, int endPort)
	{
		this(new FolderResourceResolver(new File(path)), port, endPort);
	}

	/**
	 * Creates a new HttpServer
	 * 
	 * @param resourceResolver
	 *            The resolver for resources
	 * @param port
	 *            The port to listen on
	 */
	public HttpServer(IHttpResourceResolver resourceResolver, int port)
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
	public HttpServer(IHttpResourceResolver resourceResolver, int portStartRange, int portEndRange)
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
	 * 			  The default timeout for requests
	 */
	public HttpServer(IHttpResourceResolver resourceResolver, int portStartRange, int portEndRange, int timeout)
	{
		this._resourceResolver = resourceResolver;
		this._portStart = portStartRange;
		this._portEnd = portEndRange;
		this._logger = SystemHttpLog.getInstance();
		this._timeout = timeout;
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
		// create socket server
		this._socketServer = this.createServerSocket(this._portStart, this._portEnd);

		// start listening on another thread
		this._serverThreadRunnable = new ServerThreadRunnable(this, this._timeout);
		Thread th = new Thread(this._serverThreadRunnable, "Aptana: HTTP Server"); //$NON-NLS-1$
		th.setDaemon(true);
		th.start();

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
		this._serverThreadRunnable.stop();
	}

	/**
	 * createServerSocket
	 * 
	 * @param startRange
	 * @param endRange
	 * @return ServerSocket
	 * @throws IOException
	 */
	private ServerSocket createServerSocket(int startRange, int endRange) throws IOException
	{
		ServerSocket result = null;
		SocketException exception = null;
		int failCount = 3;
		
		for (int i = startRange; i <= endRange; i++)
		{
			try
			{
				result = new ServerSocket(i, 0, null);
				break;
			}
			catch (BindException e)
			{
				exception = e;
			}
			catch(SocketException e)
			{
				if(--failCount == 0) {
					exception = e;
					break;
				}
			}
		}

		if (result == null)
		{
			if (exception == null)
			{
				exception = new BindException("Unable to bind to a port in the specified range: " + startRange + "-" //$NON-NLS-1$ //$NON-NLS-2$
						+ endRange);
			}

			throw exception;
		}

		return result;
	}
	
	public static String getServerAddress() {
		Preferences store = ServerCorePlugin.getDefault().getPluginPreferences();
		String serverAddress = "127.0.0.1"; //$NON-NLS-1$
		String address = store.getString(IHttpLaunchConfigurationConstants.PREF_HTTP_SERVER_ADDRESS);
		// find valid address
		InetAddress[] addrs = SocketUtil.getLocalAddresses();
		for(int i = 0; i < addrs.length; ++i)
		{
			if(addrs[i].getHostAddress().equals(address)) {
				serverAddress = address;
				break;
			}
		}
		return serverAddress;
	}

	public static int[] getPortRange() {
		Preferences store = ServerCorePlugin.getDefault().getPluginPreferences();
		int portsStart = 8000;
		int portsEnd = 8500;
		String ports = store.getString(IHttpLaunchConfigurationConstants.PREF_HTTP_SERVER_PORTS);
		if ( ports.length() > 0 )
		{
			Matcher matcher = Pattern.compile("^(\\d+)(-(\\d+))?$").matcher(ports); //$NON-NLS-1$
			if ( matcher.matches() )
			{
				try {
					int start = Integer.parseInt(matcher.group(1));
					int end = start;
					if ( matcher.group(2) != null )
					{
						end = Integer.parseInt(matcher.group(3));
					}
					if ( start < end )
					{
						portsStart = start;
						portsEnd = end;
					}
				} catch (NumberFormatException e) {
				}
			}
		}

		return new int[] { portsStart, portsEnd };
	}

}
