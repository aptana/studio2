/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.server.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.impl.servers.AbstractExternalServer;
import com.aptana.ide.server.ui.ServerUIPlugin;

/**
 * @author Pavel Petrochenko
 */
public class MySqlServer extends AbstractExternalServer
{

	private boolean running;

	private String launchArgs;
	private ILaunch launch;

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#installConfig(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void installConfig(IAbstractConfiguration configuration)
	{
		setLaunchArgs(configuration.getStringAttribute(MySqlServerTypeDelegate.LAUNCHARRGS));
		super.installConfig(configuration);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#storeConfiguration(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void storeConfiguration(IAbstractConfiguration config)
	{
		config.setStringAttribute(MySqlServerTypeDelegate.LAUNCHARRGS, getLaunchArgs());
		super.storeConfiguration(config);

	}

	/**
	 * @param type
	 * @param configuration
	 */
	public MySqlServer(IServerType type, IAbstractConfiguration configuration)
	{
		super(type, configuration);
		if (launchArgs==null||launchArgs.length()==0){
			launchArgs= "--standalone"; //$NON-NLS-1$
		}
	}

	private ILaunchesListener launchesListener2 = new ILaunchesListener2()
	{

		public void launchesAdded(ILaunch[] launches)
		{
			for (int a = 0; a < launches.length; a++)
			{
				if (launches[a] == launch)
				{
					setServerState(IServer.STATE_STARTED);
					setMode("run"); //$NON-NLS-1$
					serverChanged();
				}
			}
		}

		public void launchesChanged(ILaunch[] launches)
		{

		}

		public void launchesRemoved(ILaunch[] launches)
		{

		}

		public void launchesTerminated(ILaunch[] launches)
		{
			for (int a = 0; a < launches.length; a++)
			{
				if (launches[a] == launch)
				{
					running = false;
					launch = null;
					setMode(null);
					setServerState(IServer.STATE_STOPPED);
					serverChanged();
				}
			}
		}

	};

	/**
	 * is running
	 * 
	 * @return - true if running
	 */
	public boolean isRunning()
	{
		return running;
	}

	/**
	 * @throws CoreException
	 */
	public void start() throws CoreException
	{
		IProcess process = LaunchUtils.exec(getPath(), new String[] { getLaunchArgs() }, null);
		launch = process.getLaunch();
		registerProcess(process);
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchesListener2);
		running = true;
	}

	/**
	 * Stops the server
	 * 
	 * @throws CoreException
	 */
	public void stop() throws CoreException
	{
		running = false;
		if (launch != null)
		{
			launch.terminate();
		}
		launch = null;
	}

	/**
	 * @return launch args
	 */
	public String getLaunchArgs()
	{
		return launchArgs;
	}

	/**
	 * @param args
	 */
	public void setLaunchArgs(String args)
	{
		this.launchArgs = args;
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#getConfigurationDescription()
	 */
	public String getConfigurationDescription()
	{
		return super.getPath() + " " + launchArgs; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractExternalServer#start(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus start(String mode, IProgressMonitor monitor)
	{
		try
		{
			start();
			setServerState(IServer.STATE_STARTED);
			return Status.OK_STATUS;
		}
		catch (CoreException e)
		{
			return new Status(IStatus.ERROR, ServerUIPlugin.ID, IStatus.ERROR, e.getMessage(), e);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractExternalServer#stop(boolean,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus stop(boolean force, IProgressMonitor monitor)
	{
		try
		{
			stop();
			setServerState(IServer.STATE_STOPPED);
			serverChanged();
			setMode(null);
			return Status.OK_STATUS;
		}
		catch (CoreException e)
		{
			return new Status(IStatus.ERROR, ServerUIPlugin.ID, IStatus.ERROR, e.getMessage(), e);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getHost()
	 */
	public String getHost()
	{
		Properties properties2 = getProperties();
		return getMySqlHost(properties2);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractExternalServer#loadProperties()
	 */
	protected Properties loadProperties()
	{
		File path = new File(new File(getPath()).getParentFile(), "my.cnf"); //$NON-NLS-1$
		Properties properties = new Properties();
		try
		{
			properties.load(new FileInputStream(path));
		}
		catch (FileNotFoundException e)
		{
			IdeLog.logError(ServerUIPlugin.getDefault(), "Configuration file not found", e); //$NON-NLS-1$
		}
		catch (IOException e)
		{
			IdeLog.logError(ServerUIPlugin.getDefault(), "IO Error while reading server properties", e); //$NON-NLS-1$
		}
		return properties;
	}

	static String getMySqlHost(Properties properties2)
	{
		String host = properties2.getProperty("port"); //$NON-NLS-1$
		if (host != null)
		{
			int indexOf = host.indexOf(':');
			if (indexOf == -1)
			{
				return StringUtils.format("127.0.0.1:{0}", host); //$NON-NLS-1$
			}
		}
		return host;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getHostname()
	 */
	public String getHostname()
	{
		return "127.0.0.1"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getPort()
	 */
	public int getPort()
	{
		return 0;
	}

}
