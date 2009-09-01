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
package com.aptana.ide.server.generic;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IOperationListener;
import com.aptana.ide.server.core.IPausableServer;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.OperationCompletionEvent;
import com.aptana.ide.server.core.ServerCorePlugin;
import com.aptana.ide.server.core.impl.servers.AbstractExternalServer;

/**
 * @author Pavel Petrochenko
 */
public class GenericServer extends AbstractExternalServer implements IPausableServer
{

	private static final String SERVER_IS_NOT_PAUSED = Messages.GenericServer_NOT_PAUSED;
	private static final String ALREADY_PAUSED = Messages.GenericServer_ALREADY_PAUSED;
	private static final String ONLY_RUNNING_SERVER_MAY_BE_PAUSED = Messages.GenericServer_ONLY_RUNNING_CAN_BE_PAUSED;
	private static final String PAUSE_COMMAND_IS_NOT_SPECIFIED = Messages.GenericServer_PAUSE_IS_NOT_SPECIFIED;
	private static final String RESUME_COMMAND_IS_NOT_SPECIFIED = Messages.GenericServer_RESUME_IS_NOT_SPECIFIED;
	private static final String ONLY_LOCAL_GENERIC_SERVERS_ARE_OPERABLE = Messages.GenericServer_ONLY_LOCALS_ARE_OPERABLE;
	private static final IProcess[] NO_PROCESS = new IProcess[0];
	String hostName;
	int port;

	private String boundName;
	private String path;
	private String startCommand;
	private String stopCommand;
	private String resumeCommand;
	private String pauseCommand;
	private String healthURL;
	private int pollingInterval;
	private boolean isLocal;

	private Job healthJob;

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#installConfig(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	@Override
	protected void installConfig(IAbstractConfiguration configuration)
	{
		this.port = configuration.getIntAttribute(IServer.KEY_PORT);
		this.hostName = configuration.getStringAttribute(IServer.KEY_HOST);
		this.boundName = configuration.getStringAttribute(IServer.KEY_ASSOCIATION_SERVER_ID);
		this.path = configuration.getStringAttribute(IServer.KEY_PATH);
		this.startCommand = configuration.getStringAttribute(GenericServerTypeDelegate.START_SERVER_COMMAND);
		this.stopCommand = configuration.getStringAttribute(GenericServerTypeDelegate.STOP_SERVER_COMMAND);
		this.stopCommand = configuration.getStringAttribute(GenericServerTypeDelegate.STOP_SERVER_COMMAND);
		this.resumeCommand = configuration.getStringAttribute(GenericServerTypeDelegate.RESUME_SERVER_COMMAND);
		this.pauseCommand = configuration.getStringAttribute(GenericServerTypeDelegate.PAUSE_SERVER_COMMAND);
		this.isLocal = configuration.getBooleanAttribute(GenericServerTypeDelegate.IS_LOCAL);
		this.healthURL = configuration.getStringAttribute(GenericServerTypeDelegate.HEALTH_URL);
		this.pollingInterval = configuration.getIntAttribute(GenericServerTypeDelegate.POLLING_INTERVAL);
		setDocumentRoot(configuration.getStringAttribute(IServer.KEY_DOCUMENT_ROOT));
		super.installConfig(configuration);

		configureHealthJob();
		if (!this.isLocal)
		{
			setServerState(IServer.STATE_NOT_APPLICABLE);
		}
	}

	private void configureHealthJob()
	{
		if (healthJob != null)
		{
			healthJob.cancel();
			healthJob = null;
		}
		if (!isLocal && healthURL != null && healthURL.length() > 0 && pollingInterval > 0)
		{
			try
			{
				final URL url = new URL(healthURL);
				final String host = url.getHost();
				final String path = url.getPath().length() > 0 ? url.getPath() : "/"; //$NON-NLS-1$

				String headLine = "HEAD " + path + " HTTP/1.1"; //$NON-NLS-1$ //$NON-NLS-2$
				String hostLine = "Host: " + host; //$NON-NLS-1$
				final String request = headLine + "\n" + hostLine + "\n\n"; //$NON-NLS-1$ //$NON-NLS-2$
				final int port = url.getPort() != -1 ? url.getPort() : url.getDefaultPort() != -1 ? url
						.getDefaultPort() : 80;
				healthJob = new Job("Heartbeating to generic server URL") //$NON-NLS-1$
				{

					protected IStatus run(IProgressMonitor monitor)
					{
						try
						{
							if (monitor.isCanceled())
							{
								return Status.CANCEL_STATUS;
							}
							Socket socket = new Socket();
							socket.setKeepAlive(false);
							socket.setSoLinger(false, 0);
							socket.setTcpNoDelay(true);
							socket.setSoTimeout(10000);
							socket.connect(new InetSocketAddress(host, port), 10000);
							boolean connected = socket.isConnected();
							if (connected)
							{
								OutputStream os = socket.getOutputStream();
								PrintWriter writer = new PrintWriter(os, true);
								writer.println(request);
								writer.flush();
								InputStream is = socket.getInputStream();
								BufferedReader reader = new BufferedReader(new InputStreamReader(is));
								String line = reader.readLine();
								if (line != null)
								{
									setServerState(IServer.STATE_STARTED);
									serverChanged();
								}
								else
								{
									setServerState(IServer.STATE_UNKNOWN);
									serverChanged();
								}
								reader.close();
								writer.close();
								socket.close();
							}
							else
							{
								setServerState(IServer.STATE_UNKNOWN);
								serverChanged();
							}
						}
						catch (Exception e)
						{
							setServerState(IServer.STATE_UNKNOWN);
							serverChanged();
						}
						this.schedule(pollingInterval);
						return Status.OK_STATUS;
					}
				};
				healthJob.setSystem(true);
				healthJob.schedule();
			}
			catch (MalformedURLException e)
			{
				IdeLog.logError(ServerCorePlugin.getDefault(), Messages.GenericServer_ERR_AddChecking, e);
			}
		}
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#storeConfiguration(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	@Override
	public void storeConfiguration(IAbstractConfiguration config)
	{
		config.setIntAttribute(IServer.KEY_PORT, port);
		config.setStringAttribute(IServer.KEY_HOST, hostName);
		config.setStringAttribute(IServer.KEY_ASSOCIATION_SERVER_ID, boundName);
		config.setStringAttribute(IServer.KEY_PATH, path);
		config.setStringAttribute(GenericServerTypeDelegate.START_SERVER_COMMAND, startCommand);
		config.setStringAttribute(GenericServerTypeDelegate.STOP_SERVER_COMMAND, stopCommand);
		config.setStringAttribute(GenericServerTypeDelegate.RESUME_SERVER_COMMAND, resumeCommand);
		config.setStringAttribute(GenericServerTypeDelegate.PAUSE_SERVER_COMMAND, pauseCommand);
		config.setBooleanAttribute(GenericServerTypeDelegate.IS_LOCAL, isLocal);
		config.setStringAttribute(GenericServerTypeDelegate.HEALTH_URL, healthURL);
		config.setIntAttribute(GenericServerTypeDelegate.POLLING_INTERVAL, pollingInterval);
		config.setStringAttribute(IServer.KEY_DOCUMENT_ROOT, getDocumentRootStr());
		super.storeConfiguration(config);
	}

	/**
	 * @param type
	 * @param configuration
	 */
	public GenericServer(IServerType type, IAbstractConfiguration configuration)
	{
		super(type, configuration);
		if (this.isLocal)
		{
			setServerState(IServer.STATE_STOPPED);
		}
		else
		{
			setServerState(IServer.STATE_NOT_APPLICABLE);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#getConfigurationDescription()
	 */
	public String getConfigurationDescription()
	{
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#canStart(java.lang.String)
	 */
	public synchronized IStatus canStart(String launchMode)
	{
		if (!this.isLocal)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ONLY_LOCAL_GENERIC_SERVERS_ARE_OPERABLE, null);
		}
		if (getServerState() == IServer.STATE_UNKNOWN)
		{
			return Status.OK_STATUS;
		}
		return super.canStart(launchMode);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#canStop()
	 */
	public synchronized IStatus canStop()
	{
		if (!this.isLocal)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ONLY_LOCAL_GENERIC_SERVERS_ARE_OPERABLE, null);
		}
		if (getServerState() == IServer.STATE_UNKNOWN || getServerState() == IPausableServer.STATE_PAUSED)
		{
			return Status.OK_STATUS;
		}
		return super.canStop();
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#canRestart(java.lang.String)
	 */
	public synchronized IStatus canRestart(String mode)
	{
		if (!this.isLocal)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ONLY_LOCAL_GENERIC_SERVERS_ARE_OPERABLE, null);
		}
		if (getServerState() == IServer.STATE_UNKNOWN)
		{
			return Status.OK_STATUS;
		}
		return super.canRestart(mode);
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
		return hostName + ":" + this.port; //$NON-NLS-1$
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
	 * @see com.aptana.ide.server.core.impl.servers.AbstractExternalServer#loadProperties()
	 */
	protected Properties loadProperties()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getHostname()
	 */
	public String getHostname()
	{
		return this.hostName;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getPort()
	 */
	public int getPort()
	{
		return this.port;
	}

	private IStatus doLaunch(String arg)
	{
		try
		{

			String[] split = arg.split(" "); //$NON-NLS-1$
			ArrayList<String> bf = new ArrayList<String>();
			for (int a = 0; a < split.length; a++)
			{
				String sm = split[a].trim();
				if (!"".equals(sm)) { //$NON-NLS-1$
					bf.add(sm);
				}
			}
			String[] args = new String[bf.size()];
			bf.toArray(args);
			IProcess exec = exec(getPath(), args, null);

			if (exec != null)
			{
				registerProcess(exec);
				return Status.OK_STATUS;
			}
			return new Status(IStatus.ERROR, ServerCorePlugin.PLUGIN_ID, IStatus.ERROR, StringUtils.format(
					"could not create process {0}", getPath()), null); //$NON-NLS-1$
		}
		catch (CoreException e)
		{
			return new Status(IStatus.ERROR, ServerCorePlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e);
		}
	}

	/**
	 * @param program
	 * @param arguments
	 * @param workingDirectory
	 * @return created process
	 * @throws CoreException
	 */
	public static IProcess exec(String program, String[] arguments, String workingDirectory) throws CoreException
	{
		int cmdLineLength = arguments.length + 1;
		String[] cmdLine = new String[cmdLineLength];
		cmdLine[0] = program;
		if (arguments != null)
		{
			System.arraycopy(arguments, 0, cmdLine, 1, arguments.length);
		}

		File workingDir = null;
		if (workingDirectory != null)
		{
			workingDir = new File(workingDirectory);
		}

		Process p = DebugPlugin.exec(cmdLine, workingDir);
		IProcess process = null;
		if (p != null)
		{
			Launch launch = new Launch(null, "run", null); //$NON-NLS-1$
			process = DebugPlugin.newProcess(launch, p, program);
			// DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);
			process.setAttribute(IProcess.ATTR_CMDLINE, renderCommandLine(cmdLine));

		}

		return process;
	}

	/**
	 * @param commandLine
	 * @return rendered command line
	 */
	protected static String renderCommandLine(String[] commandLine)
	{
		if (commandLine.length < 1)
		{
			return ""; //$NON-NLS-1$
		}
		StringBuffer buf = new StringBuffer(commandLine[0]);
		for (int i = 1; i < commandLine.length; i++)
		{
			buf.append(' ');
			buf.append(commandLine[i]);
		}
		return buf.toString();
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractExternalServer#stop(boolean,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus stop(boolean force, IProgressMonitor monitor)
	{
		IStatus doLaunch = doLaunch(stopCommand);
		if (doLaunch.isOK())
		{
			setMode(null);
			setServerState(IServer.STATE_STOPPED);
		}
		return doLaunch;
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractExternalServer#start(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus start(String mode, IProgressMonitor monitor)
	{
		String arg = startCommand;
		IStatus doLaunch = doLaunch(arg);
		if (doLaunch.isOK())
		{
			setServerState(IServer.STATE_STARTED);
			setMode("run"); //$NON-NLS-1$
		}
		return doLaunch;
	}

	/**
	 * @return status
	 * @see com.aptana.ide.server.core.IPausableServer#canPause()
	 */
	public IStatus canPause()
	{
		if (!this.isLocal)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ONLY_LOCAL_GENERIC_SERVERS_ARE_OPERABLE, null);
		}
		if (this.resumeCommand.length() == 0)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, RESUME_COMMAND_IS_NOT_SPECIFIED, null);
		}
		if (this.pauseCommand.length() == 0)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, PAUSE_COMMAND_IS_NOT_SPECIFIED, null);
		}
		if (this.getServerState() != IPausableServer.STATE_STARTED)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ONLY_RUNNING_SERVER_MAY_BE_PAUSED, null);
		}
		if (this.getServerState() == IPausableServer.STATE_PAUSED)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ALREADY_PAUSED, null);
		}
		return Status.OK_STATUS;
	}

	/**
	 * @return status
	 * @see com.aptana.ide.server.core.IPausableServer#canResume()
	 */
	public IStatus canResume()
	{
		if (!this.isLocal)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ONLY_LOCAL_GENERIC_SERVERS_ARE_OPERABLE, null);
		}
		if (this.resumeCommand.length() == 0)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, RESUME_COMMAND_IS_NOT_SPECIFIED, null);
		}
		if (this.pauseCommand.length() == 0)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, PAUSE_COMMAND_IS_NOT_SPECIFIED, null);
		}
		if (this.getServerState() != IPausableServer.STATE_PAUSED)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, SERVER_IS_NOT_PAUSED, null);
		}
		return Status.OK_STATUS;
	}

	/**
	 * @see com.aptana.ide.server.core.IPausableServer#pause(com.aptana.ide.server.core.IOperationListener,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void pause(IOperationListener listener, IProgressMonitor monitor)
	{
		String arg = pauseCommand;
		IStatus doLaunch = doLaunch(arg);
		if (doLaunch.isOK())
		{
			setServerState(IPausableServer.STATE_PAUSED);
			setMode("run"); //$NON-NLS-1$
		}
		serverChanged();
		if (listener != null)
		{
			listener.done(new OperationCompletionEvent(this, "pause", Status.OK_STATUS)); //$NON-NLS-1$
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IPausableServer#resume(com.aptana.ide.server.core.IOperationListener,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void resume(IOperationListener listener, IProgressMonitor monitor)
	{
		String arg = resumeCommand;
		IStatus doLaunch = doLaunch(arg);
		if (doLaunch.isOK())
		{
			setServerState(IServer.STATE_STARTED);
			setMode("run"); //$NON-NLS-1$
		}
		serverChanged();
		if (listener != null)
		{
			listener.done(new OperationCompletionEvent(this, "resume", Status.OK_STATUS)); //$NON-NLS-1$
		}
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractExternalServer#restart(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus restart(String mode, IProgressMonitor monitor)
	{
		IStatus stop = stop(true, monitor);
		if (!stop.isOK())
		{
			return stop;
		}
		try
		{
			Thread.sleep(200);
		}
		catch (InterruptedException e)
		{

		}
		serverChanged();
		try
		{
			Thread.sleep(200);
		}
		catch (InterruptedException e)
		{

		}
		IStatus start = start(mode, monitor);
		try
		{
			Thread.sleep(200);
		}
		catch (InterruptedException e)
		{

		}
		serverChanged();
		return start;
	}
}
