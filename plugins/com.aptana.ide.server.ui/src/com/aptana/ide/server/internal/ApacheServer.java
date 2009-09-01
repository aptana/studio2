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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IOperationListener;
import com.aptana.ide.server.core.IPausableServer;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.impl.servers.AbstractExternalServer;
import com.aptana.ide.server.ui.ServerUIPlugin;

/**
 * @author Pavel Petrochenko
 */
public class ApacheServer extends AbstractExternalServer
{

	private String startApache ;
	private String stopApache ;
	private String restartApache ;
	private String etcHosts ;
	private String hostName;

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#installConfig(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void installConfig(IAbstractConfiguration configuration)
	{
		setEtcHosts(configuration.getStringAttribute(ApacheServerTypeDelegate.ETCHOSTS));
		setDocumentRoot(configuration.getStringAttribute(IServer.KEY_DOCUMENT_ROOT));
		setRestartApache(configuration.getStringAttribute(ApacheServerTypeDelegate.RESTARTAPACHE));
		setStartApache(configuration.getStringAttribute(ApacheServerTypeDelegate.STARTAPACHE));
		setStopApache(configuration.getStringAttribute(ApacheServerTypeDelegate.STOPAPACHE));
		this.hostName = configuration.getStringAttribute(ApacheServerTypeDelegate.HOSTNAME);
		super.installConfig(configuration);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#storeConfiguration(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void storeConfiguration(IAbstractConfiguration config)
	{
		super.storeConfiguration(config);
		config.setStringAttribute(ApacheServerTypeDelegate.ETCHOSTS, getEtcHosts());
		config.setStringAttribute(IServer.KEY_DOCUMENT_ROOT, getDocumentRootStr());
		config.setStringAttribute(ApacheServerTypeDelegate.RESTARTAPACHE, getRestartApache());
		config.setStringAttribute(ApacheServerTypeDelegate.STARTAPACHE, getStartApache());
		config.setStringAttribute(ApacheServerTypeDelegate.STOPAPACHE, getStopApache());
		config.setStringAttribute(ApacheServerTypeDelegate.HOSTNAME, hostName);
	}

	/**
	 * @param type
	 * @param configuration
	 */
	public ApacheServer(IServerType type, IAbstractConfiguration configuration)
	{
		super(type, configuration);
		if (startApache==null||startApache.length()==0){
			startApache="-k start"; //$NON-NLS-1$
		}
		if (stopApache==null||stopApache.length()==0){
			stopApache="-k stop"; //$NON-NLS-1$
		}
		if (restartApache==null||restartApache.length()==0){
			restartApache = "-k restart"; //$NON-NLS-1$
		}
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#getConfigurationDescription()
	 */
	public String getConfigurationDescription()
	{
		return getPath() + " " + getEtcHosts(); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractExternalServer#restart(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus restart(String mode, IProgressMonitor monitor)
	{
		setServerState(IServer.STATE_STOPPED);
		serverChanged();
		try
		{
			Thread.sleep(200);
		}
		catch (InterruptedException e)
		{
			
		}
		setServerState(IServer.STATE_STARTING);
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
		IStatus doLaunch = doLaunch(getRestartApache());
		if (doLaunch.isOK())
		{
			setServerState(IServer.STATE_STARTED);
			setMode("run"); //$NON-NLS-1$
		}
		else
		{
			setServerState(IServer.STATE_STARTING);
		}
		try
		{
			Thread.sleep(200);
		}
		catch (InterruptedException e)
		{
			
		}
		serverChanged();
		return doLaunch;
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractExternalServer#start(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus start(String mode, IProgressMonitor monitor)
	{
		String arg = getStartApache();
		IStatus doLaunch = doLaunch(arg);
		if (doLaunch.isOK())
		{
			setServerState(IServer.STATE_STARTED);
			setMode("run"); //$NON-NLS-1$
		}
		return doLaunch;
	}
	
	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#getDefaultLogPath()
	 */
	@Override
	protected IPath getDefaultLogPath()
	{
		return new Path(new File(this.getPath()).getParentFile().getParent()+"/logs/error.log"); //$NON-NLS-1$
	}

	private IStatus doLaunch(String arg)
	{
		try
		{
			String[] split = arg.split(" "); //$NON-NLS-1$
			ArrayList<String> bf=new ArrayList<String>();
			for (int a=0;a<split.length;a++){
				String sm=split[a].trim();
				if (!"".equals(sm)){ //$NON-NLS-1$
					bf.add(sm);
				}
			}
			String[] args=new String[bf.size()];
			bf.toArray(args);
			IProcess exec = LaunchUtils.exec(getPath(), args, null);
			// IProcess exec =LaunchUtils.exec("C:\\windows\\system32\\net.exe", new String[]{"start","Apache"},null);
			if (exec != null)
			{

				registerProcess(exec);
				return Status.OK_STATUS;
			}
			return new Status(IStatus.ERROR, ServerUIPlugin.ID, IStatus.ERROR, StringUtils.format(
					"could not create process {0}", getPath()), null); //$NON-NLS-1$
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
		IStatus doLaunch = doLaunch(getStopApache());
		if (doLaunch.isOK())
		{
			setMode(null);
			setServerState(IServer.STATE_STOPPED);
		}
		
		return doLaunch;
	}



	/**
	 * @return start
	 */
	public String getStartApache()
	{
		return startApache;
	}

	/**
	 * @param startApache
	 */
	public void setStartApache(String startApache)
	{
		this.startApache = startApache;
	}

	/**
	 * @return stop
	 */
	public String getStopApache()
	{
		return stopApache;
	}

	/**
	 * @param stopApache
	 */
	public void setStopApache(String stopApache)
	{
		this.stopApache = stopApache;
	}

	/**
	 * @return restart
	 */
	public String getRestartApache()
	{
		return restartApache;
	}

	/**
	 * @param restartApache
	 */
	public void setRestartApache(String restartApache)
	{
		this.restartApache = restartApache;
	}

	/**
	 * @return etcHosts
	 */
	public String getEtcHosts()
	{
		return etcHosts;
	}

	/**
	 * @param etcHosts
	 */
	public void setEtcHosts(String etcHosts)
	{
		this.etcHosts = etcHosts;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getHost()
	 */
	public String getHost()
	{
		return this.hostName;
	}

	static String getApacheHost(Properties properties2, int port)
	{
		String host = properties2.getProperty("listen"); //$NON-NLS-1$
		if (port != 0)
		{
			host = Integer.toString(port);
		}
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
	 * @see com.aptana.ide.server.core.impl.servers.AbstractExternalServer#loadProperties()
	 */
	protected Properties loadProperties()
	{
		File apachePath = new File(this.getPath()).getParentFile().getParentFile();
		Properties readServerProperties = readServerProperties(apachePath);
		return readServerProperties;
	}

	/**
	 * @param apachePath
	 * @return - properties
	 */
	public static Properties readServerProperties(File apachePath)
	{
		Properties properties2 = new Properties();
		File configurationFolder = new File(apachePath, "conf"); //$NON-NLS-1$
		File configFile = new File(configurationFolder, "httpd.conf"); //$NON-NLS-1$
		try
		{
			FileReader rrs = new FileReader(configFile);
			BufferedReader bs = new BufferedReader(rrs);
			while (true)
			{
				String readLine = bs.readLine();
				if (readLine == null)
				{
					break;
				}
				readLine = readLine.trim();
				if (readLine.startsWith("#")) { //$NON-NLS-1$
					continue;
				}
				for (int a = 0; a < readLine.length(); a++)
				{
					char c = readLine.charAt(a);
					if (Character.isWhitespace(c))
					{
						String key = readLine.substring(0, a).toLowerCase();
						String value = readLine.substring(a).trim();
						properties2.put(key, value);
					}
				}
			}
		}
		catch (FileNotFoundException e)
		{
			IdeLog.logError(ServerUIPlugin.getDefault(), "Configuration file not found", e); //$NON-NLS-1$
		}
		catch (IOException e)
		{
			IdeLog.logError(ServerUIPlugin.getDefault(), "IO Error while reading server properties", e); //$NON-NLS-1$
		}
		return properties2;
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
		return this.hostName;
	}

	/**
	 * @return status
	 * @see com.aptana.ide.server.core.IPausableServer#canPause()
	 */
	public IStatus canPause()
	{
		if (this.getServerState() != IPausableServer.STATE_STARTED)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, Messages.ApacheServer_ONLY_RUNNING_MAY_BE_PAUSED,
					null);
		}
		if (this.getServerState() == IPausableServer.STATE_PAUSED)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, Messages.ApacheServer_ALREADY_PAUSED, null);
		}
		return Status.OK_STATUS;
	}

	/**
	 * @return status
	 * @see com.aptana.ide.server.core.IPausableServer#canResume()
	 */
	public IStatus canResume()
	{
		if (this.getServerState() != IPausableServer.STATE_PAUSED)
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, Messages.ApacheServer_ONLY_PAUSED_MAY_BE_RESUMED,
					null);
		}
		return Status.OK_STATUS;
	}

	/**
	 * @param listener
	 * @param monitor
	 */
	public void pause(IOperationListener listener, IProgressMonitor monitor)
	{
		stop(true, monitor);
		setServerState(IPausableServer.STATE_PAUSED);
		serverChanged();
	}

	/**
	 * @param listener
	 * @param monitor
	 */
	public void resume(IOperationListener listener, IProgressMonitor monitor)
	{
		setServerState(IPausableServer.STATE_STOPPED);
		start("run", monitor); //$NON-NLS-1$
		serverChanged();
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getPort()
	 */
	public int getPort()
	{
		return 0;
	}
}
