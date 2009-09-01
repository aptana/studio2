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
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerEnvironmentConfigurator;
import com.aptana.ide.server.core.IServerLauncher;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.ServerLaunchers;
import com.aptana.ide.server.core.ServerPathUtils;
import com.aptana.ide.server.core.impl.servers.AbstractExternalServer;
import com.aptana.ide.server.ui.ServerLauncherConfigurationWizard;
import com.aptana.ide.server.ui.ServerUIPlugin;

/**
 * @author Pavel Petrochenko
 */
public class XAMPPServer extends AbstractExternalServer
{
	
	/**
	 * Runnable that displays wizard.
	 * @author Denis Denisenko
	 *
	 */
	private static class WizardRunable implements Runnable
	{
		/**
		 * Wizard.
		 */
		private IWizard wizard;
		
		/**
		 * Return code.
		 */
		private int returnCode;
		
		/**
		 * WizardRunable constructor.
		 * @param wizard - wizard.
		 */
		public WizardRunable(IWizard wizard)
		{
			this.wizard = wizard;
		}

		public void run()
		{
			WizardDialog wizardDialog = new WizardDialog(Display.getDefault().getActiveShell(),
					(IWizard) wizard);
			wizardDialog.create();
			returnCode = wizardDialog.open();
		}
		
		/**
		 * Gets wizard return code.
		 * @return return code.
		 */
		public int getReturnCode()
		{
			return returnCode;
		}
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
			setMode("run"); //$NON-NLS-1$
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
			return Status.OK_STATUS;
		}
		catch (CoreException e)
		{
			return new Status(IStatus.ERROR, ServerUIPlugin.ID, IStatus.ERROR, e.getMessage(), e);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#installConfig(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	protected void installConfig(IAbstractConfiguration configuration)
	{
		setStopPath(configuration.getStringAttribute(XAMPPServerTypeDelegate.STOPPATH));
		super.installConfig(configuration);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#storeConfiguration(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void storeConfiguration(IAbstractConfiguration config)
	{
		config.setStringAttribute(XAMPPServerTypeDelegate.STOPPATH, getStopPath());
		super.storeConfiguration(config);
	}

	/**
	 * @param type
	 * @param configuration
	 */
	public XAMPPServer(IServerType type, IAbstractConfiguration configuration)
	{
		super(type, configuration);
	}

	private boolean running;
	private String stopPath;

	/**
	 * Is running
	 * 
	 * @return - true if running
	 */
	public boolean isRunning()
	{
		return running;
	}

	/**
	 * Starts the server
	 * 
	 * @throws CoreException
	 */
	public void start() throws CoreException
	{
		String executable = getPath();
		String workingDirectory = null;
		if (executable != null && executable.length() > 0)
		{
			int index = executable.lastIndexOf(File.separatorChar);
			if (index > 0)
			{
				workingDirectory = executable.substring(0, index);
			}
		}
		
		IProcess exec = null;
		
		List<IServerLauncher> launchers = ServerLaunchers.getLaunchers(this.getServerType().getId());
		if (launchers != null && launchers.size() != 0)
		{
			IServerLauncher launcher = launchers.get(0);
			if (!launcher.isConfigured())
			{
				IServerEnvironmentConfigurator configurator = launcher.getConfigurator();
				if(configurator.requiresAdditionalInformation())
				{
					List<IServerLauncher> toConfigure = new ArrayList<IServerLauncher>();
					toConfigure.add(launcher);
					ServerLauncherConfigurationWizard wizard = new ServerLauncherConfigurationWizard(
							getName(), toConfigure);
					WizardRunable runnable = new WizardRunable(wizard);
					Display.getDefault().syncExec(runnable);
					
					int result = runnable.getReturnCode();
					if (result != Window.OK)
					{
						return;
					}
				}
			}
			
			exec = launcher.exec(executable, getPathParameters(), workingDirectory);
			if (exec == null)
			{
				MessageDialog.openError(null, Messages.XAMPPServer_Error_Title, Messages.XAMPPServer_Error_Start);
			}
		}
		else
		{
			exec = LaunchUtils.exec(executable, getPathParameters(), workingDirectory);
		}
		if (exec != null)
		{
			registerProcess(exec);
		}
		running = true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IPath getServerRoot()
	{
		String startPathString = getPath();
		if (startPathString == null)
		{
			return null;
		}
		
		Path startPath = new Path(startPathString);
		
		//getting executable directory path by removing the last (file) segment
		IPath executableDirectoryPath  = startPath.removeLastSegments(1);
		
		
		
//		//now checking, if the last segment is "bin" (for Linux-based systems), then
//		//we should remove it too.
//		IPath result = executableDirectoryPath;
//		if ("bin".equals(result.lastSegment()))
//		{
//			result = result.removeLastSegments(1);
//		}
		
		String currentOS = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		
		IPath rootPath = null;
		if (currentOS.startsWith("windows")) //$NON-NLS-1$
		{
			rootPath = executableDirectoryPath;
		}
		else if (currentOS.startsWith("linux")) //$NON-NLS-1$
		{
			rootPath = executableDirectoryPath;
		}
		else if (currentOS.startsWith("mac")) //$NON-NLS-1$
		{
			//going one directory up
			rootPath = executableDirectoryPath.removeLastSegments(1);
		}
		else
		{
			rootPath = executableDirectoryPath;
		}
		
		return rootPath;
	}

	/**
	 * Stops the server
	 * 
	 * @throws CoreException
	 */
	public void stop() throws CoreException
	{
		String executable = getStopPath();
		String workingDirectory = null;
		if (executable != null && executable.length() > 0)
		{
			int index = executable.lastIndexOf(File.separatorChar);
			if (index > 0)
			{
				workingDirectory = executable.substring(0, index);
			}
		}
		
		IProcess exec = null;
		
		List<IServerLauncher> launchers = ServerLaunchers.getLaunchers(this.getServerType().getId());
		if (launchers != null && launchers.size() != 0)
		{
			IServerLauncher launcher = launchers.get(0);
			if (!launcher.isConfigured())
			{
				IServerEnvironmentConfigurator configurator = launcher.getConfigurator();
				if(configurator.requiresAdditionalInformation())
				{
					List<IServerLauncher> toConfigure = new ArrayList<IServerLauncher>();
					toConfigure.add(launcher);
					ServerLauncherConfigurationWizard wizard = new ServerLauncherConfigurationWizard(
							getName(), toConfigure);
					WizardRunable runnable = new WizardRunable(wizard);
					Display.getDefault().syncExec(runnable);
					int result = runnable.getReturnCode();
					
					if (result != Window.OK)
					{
						return;
					}
				}
			}
			
			exec = launcher.exec(executable, getStopPathParameters(), workingDirectory);
			if (exec == null)
			{
				MessageDialog.openError(null, Messages.XAMPPServer_Error_Title, Messages.XAMPPServer_Error_Stop);
			}
		}
		else
		{
			exec = LaunchUtils.exec(executable, getStopPathParameters(), workingDirectory);
		}
		
		if (exec != null)
		{
			registerProcess(exec);
		}
		running = false;
	}

	/**
	 * @return stop path
	 */
	public String getStopPath()
	{
		return ServerPathUtils.getFileNameByPathWithParameters(stopPath);
	}
	
	/**
	 * Gets stop path parameters.
	 * @return stop path parameters
	 */
	public String[] getStopPathParameters()
	{
		return ServerPathUtils.getParameters(stopPath);
	}

	/**
	 * @param args
	 */
	public void setStopPath(String args)
	{
		this.stopPath = args;
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#getConfigurationDescription()
	 */
	public String getConfigurationDescription()
	{
		return super.getPath() + " " + stopPath; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getHost()
	 */
	public String getHost()
	{
		return ApacheServer.getApacheHost(getProperties(), 0);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractExternalServer#loadProperties()
	 */
	protected Properties loadProperties()
	{
		Properties readServerProperties = readServerProperties(new File(getPath()).getParentFile());
		return readServerProperties;
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
		return "127.0.0.1"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getPort()
	 */
	public int getPort()
	{
		return 0;
	}
	
	/**
	 * Reads server properties.
	 * @param apacheStartPath - Apache start executable directory.
	 * @return - properties
	 */
	public static Properties readServerProperties(File apacheStartPath)
	{
		Properties properties2 = new Properties();
		
		Path executablePath = new Path(apacheStartPath.getAbsolutePath());
		
		String currentOS = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		
		IPath confPath = null;
		if (currentOS.startsWith("windows")) //$NON-NLS-1$
		{
			confPath = executablePath.append("/apache/conf/httpd.conf"); //$NON-NLS-1$
		}
		else if (currentOS.startsWith("linux")) //$NON-NLS-1$
		{
			confPath = executablePath.append("/etc/httpd.conf"); //$NON-NLS-1$
		}
		else if (currentOS.startsWith("mac")) //$NON-NLS-1$
		{
			confPath = executablePath.removeLastSegments(1).append("/etc/httpd.conf"); //$NON-NLS-1$
		}
		else
		{
			IdeLog.logError(ServerUIPlugin.getDefault(), "Unknown OS: " + currentOS); //$NON-NLS-1$
			return properties2;
		}
		
		File configFile = confPath.toFile();
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
}
