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

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.server.IServerListener;
import com.aptana.ide.server.IServerRunnable;
import com.aptana.ide.server.core.HttpServerLaunchConfiguration;
import com.aptana.ide.server.core.ServerCorePlugin;
import com.aptana.ide.server.core.ServerManager;
import com.aptana.ide.server.logging.IHttpLog;
import com.aptana.ide.server.resolvers.IHttpResourceResolver;
import com.aptana.ide.server.resources.IHttpResource;
import com.aptana.ide.server.resources.WorkspaceHttpFolderResource;
import com.aptana.ide.server.resources.WorkspaceHttpResource;

/**
 * Implements a HttpServer configuration
 * 
 * @author Kevin Lindsey
 */
public class HttpServerRunnable implements IServerRunnable, IHttpResourceResolver, IHttpLog
{
	/*
	 * Fields
	 */
	private ILaunchConfiguration _launchConfiguration;
	private HttpServerLaunchConfiguration _httpConfiguration;
	private ArrayList _listeners;
	private HttpServer _httpServer;
	private String _myAddress;
	
	/*
	 * Properties
	 */

	/**
	 * Returns the current base URL
	 * 
	 * @return String
	 */
	public String getBaseURL()
	{
		return "http://" + _myAddress + ":" + this._httpServer.getPort() + "/"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Returns the current server launch configuration
	 * 
	 * @return The current configuration
	 */
	public HttpServerLaunchConfiguration getServerLaunchConfiguration()
	{
		return this._httpConfiguration;
	}

	/**
	 * Returns the current launch configuration
	 * 
	 * @return The current configuration
	 */
	public ILaunchConfiguration getLaunchConfiguration()
	{
		return this._launchConfiguration;
	}

	/*
	 * Constructors
	 */

	/**
	 * Creates a new HttpServerRunnable
	 */
	public HttpServerRunnable()
	{
		this._listeners = new ArrayList();
		_myAddress = getFirstNonLocalhostIP();
	}

	/*
	 * Methods
	 */

	/**
	 * Returns the first non-localhost IP (127.0.0.1), if not found,
	 * it will return "127.0.0.1"
	 */
	private String getFirstNonLocalhostIP()
	{
		Enumeration e1;
		
		try
		{
			e1 = NetworkInterface.getNetworkInterfaces();
		}
		catch (SocketException e)
		{
			IdeLog.logError(ServerCorePlugin.getDefault(), "getFirstNonLocalhostIP: " + e.getMessage(), e); //$NON-NLS-1$
			return "127.0.0.1"; //$NON-NLS-1$
		}
		
		while(e1.hasMoreElements())
		{
			NetworkInterface ni = (NetworkInterface) e1.nextElement();
			
			Enumeration e2 = ni.getInetAddresses();
			
			while(e2.hasMoreElements())
			{
				InetAddress ia = (InetAddress) e2.nextElement();
				String addr = ia.getHostAddress();
				String[] parts = addr.split("\\."); //$NON-NLS-1$
				if(addr.equals("127.0.0.1") == false && parts.length == 4) //$NON-NLS-1$
				{
					return addr;
				}
			}
		}
		
		return "127.0.0.1"; //$NON-NLS-1$
	}
	
	/**
	 * Adds a listener to the server
	 * 
	 * @param listener
	 */
	public void addServerListener(IServerListener listener)
	{
		this._listeners.add(listener);
	}

	/**
	 * fireServerStarted
	 */
	private void fireServerStarted()
	{
		IServerListener[] listenerArray = (IServerListener[]) this._listeners.toArray(new IServerListener[0]);

		for (int i = 0; i < listenerArray.length; i++)
		{
			listenerArray[i].serverStarted(this);
		}
	}

	/**
	 * fireServerStopped
	 */
	private void fireServerStopped()
	{
		IServerListener[] listenerArray = (IServerListener[]) _listeners.toArray(new IServerListener[0]);

		for (int i = 0; i < listenerArray.length; i++)
		{
			listenerArray[i].serverStopped(this);
		}
	}

	/**
	 * getHttpResourceFromWorkpace
	 * 
	 * @param path
	 * @param workspace
	 * @return IHttpResource
	 */
	private static IHttpResource getHttpResourceFromWorkpace(IPath path, IWorkspaceRoot workspace)
	{
		IResource resource = workspace.findMember(path);
		IHttpResource result = null;

		if (resource != null)
		{
			if (resource instanceof IFile)
			{
				result = new WorkspaceHttpResource((IFile) resource);
			}
			else if (resource instanceof IContainer)
			{
				result = new WorkspaceHttpFolderResource((IContainer) resource);
			}
			else
			{
				result = new WorkspaceHttpFolderResource(workspace);
			}
		}

		return result;
	}

	/**
	 * Returns the resource from a URI
	 * 
	 * @param requestLine
	 * @return The IHttpResource to return
	 * @throws HttpServerException
	 *             If the resource cannot be returned
	 */
	public IHttpResource getResource(RequestLineParser requestLine) throws HttpServerException
	{
		// Collect all the projects in the workspace except the given project
		Path resourcePath = new Path(requestLine.getUri());

		// get the file from the workspace
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		return getHttpResourceFromWorkpace(resourcePath, workspaceRoot);
	}

	/**
	 * Log an error
	 * 
	 * @param message
	 *            The message to log
	 */
	public void logError(String message)
	{
		this.logError(message, null);
	}

	/**
	 * Log an error
	 * 
	 * @param message
	 *            The message to log
	 * @param th
	 *            The error that was thrown
	 */
	public void logError(String message, Throwable th)
	{
		ServerCorePlugin.logError(message, th);
	}

	/**
	 * Log a trace message
	 * 
	 * @param message
	 *            The message to log
	 */
	public void logTrace(String message)
	{
		IdeLog.logInfo(ServerCorePlugin.getDefault(), message);
	}

	/**
	 * Removes a listener from the server
	 * 
	 * @param listener
	 */
	public void removeServerListener(IServerListener listener)
	{
		this._listeners.remove(listener);
	}

	/**
	 * Starts the server based on particular configuration
	 * 
	 * @param configuration
	 * @param mode
	 * @param launch
	 * @param monitor
	 */
	public void start(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
	{
		this._launchConfiguration = configuration;
		this._httpConfiguration = new HttpServerLaunchConfiguration(configuration);

		if (this.startHttpServer())
		{
			this.fireServerStarted();
		}
	}

	/**
	 * Starts an HttpServer based on the launch configuration (if one is not already running)
	 * 
	 * @param launch
	 * @return true if a new server was started, false if the server was already running
	 */
	private boolean startHttpServer()
	{
		// determine if the server for this launch configuration is already running, if so, just reuse it
		String name = this._launchConfiguration.getName();
		IServerRunnable server = ServerManager.getInstance().getServer(name);

		if (server != null)
		{
			if ((server instanceof HttpServerRunnable) == false)
			{
				// how can we have 2 different launch types configured with the same name?
				throw new IllegalStateException("unexpected launch type"); //$NON-NLS-1$
			}

			this._httpServer = ((HttpServerRunnable) server)._httpServer;
		}
		else
		{
			// create a new server
			this._httpServer = new HttpServer(this, 8000, 8500);
	
			try
			{
				this._httpServer.start();
			}
			catch (IOException e)
			{
				this.logError(e.getMessage(), e);
			}
		}
		
		return (server == null);
	}

	/**
	 * Stops the server
	 */
	public void stop()
	{
		try
		{
			this._httpServer.stop();
		}
		catch (Exception e)
		{
			logError("Error stopping server", e); //$NON-NLS-1$
		}
		fireServerStopped();
	}
}
