/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.desktop.integration.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.intro.impl.IntroPlugin;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.MutexJobRule;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.WorkbenchHelper;
import com.aptana.ide.update.Activator;
import com.aptana.ide.update.manager.IPlugin;
import com.aptana.ide.update.manager.PluginManagerException;

/**
 * Manages the launching of the workbench files from the command line.
 * 
 * @author Paul Colton
 */
public class LaunchHelper
{
	private static final String HANDLE_URL_FLAG = "-handleURL"; //$NON-NLS-1$
	private String dotAptanaFile = null;
	private String[] initialFiles;

    private static class FeatureURL implements IPlugin
    {

        private final URL url;
        private final String id;

        FeatureURL(URL url, String id)
        {
            this.url = url;
            this.id = id;
        }

        public String getId()
        {
            return id;
        }

        public String getName()
        {
            return id;
        }

        public URL getURL()
        {
            return url;
        }

        public String getVersion()
        {
            return null;
        }

    }

	private List<FeatureURL> featuresToInstall = new LinkedList<FeatureURL>();

	/**
	 * openStartupFiles
	 * 
	 * @param window
	 */
	public void openStartupFiles(IWorkbenchWindow window)
	{	
		if (initialFiles != null)
		{
			if (initialFiles.length > 0)
			{
				try
				{
					IntroPlugin.closeIntro();
				}
				catch (Exception ex)
				{
					IdeLog.logError(DesktopIntegrationServerActivator.getDefault(), Messages.LaunchHelper_UnableToCLoseWelcome, ex);
				}
			}

			for (int i = 0; i < initialFiles.length; i++)
			{
				File file = new File(initialFiles[i]);
				try
				{
					if (file.exists())
					{
						String editorID = getEditorID(file);

						if (editorID == null)
						{
							WorkbenchHelper.openFile(file, window);
						}
						else
						{
							WorkbenchHelper.openFile(editorID, file, window);
						}
					}
				}
				catch (Exception e)
				{
					IdeLog.logError(DesktopIntegrationServerActivator.getDefault(), StringUtils.format(
							Messages.LaunchHelper_ErrorOpeningFileOnStartup, initialFiles[i]), e);
				}

			}
			initialFiles = null;
		}
		
		// Install queued up features
		if (featuresToInstall.size() > 0) {
			UIJob uiJob = new UIJob("Installing features") { //$NON-NLS-1$
				public IStatus runInUIThread(
						IProgressMonitor monitor) {
					try {
						Activator.getDefault().getPluginManager().install(featuresToInstall.toArray(new FeatureURL[0]), new NullProgressMonitor());
					} catch (PluginManagerException e) {
						IdeLog.logError(DesktopIntegrationServerActivator.getDefault(), e.getMessage());
					}
					return Status.OK_STATUS;
				}
			};
			uiJob.setSystem(true);
			uiJob.setRule(MutexJobRule.getInstance());
			uiJob.schedule(10000);
		}
	}

	private String getEditorID(File file)
	{
		String name = file.getName().toLowerCase();

		if (name.endsWith(".js") || name.endsWith(".css")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			return null;
		}

		String contents = getFileContents(file);

		final String HTML_EDITOR = "com.aptana.ide.editors.HTMLEditor"; //$NON-NLS-1$

		if (contents == null)
		{
			return null;
		}
		else
		{
			contents = contents.toLowerCase();

			if (contents.indexOf("<!doctype html") != -1 || contents.indexOf("<html") != -1) //$NON-NLS-1$ //$NON-NLS-2$
			{
				return HTML_EDITOR;
			}
		}

		return null;
	}

	private String getFileContents(File file)
	{
		int fileLength = (int) file.length();

		if (fileLength == 0)
		{
			return null;
		}

		if (fileLength > 100)
		{
			fileLength = 100;
		}

		char[] chars = new char[fileLength];

		try
		{
			FileReader fr = new FileReader(file);
			fr.read(chars);
			fr.close();
		}
		catch (Exception e)
		{
			IdeLog.logError(DesktopIntegrationServerActivator.getDefault(), StringUtils.format(Messages.LaunchHelper_UnableToGetFileContents,
					file.getAbsolutePath()), e);
			return null;
		}

		return new String(chars);
	}

	private static LaunchHelper _instance;

	/**
	 * getInstance
	 * 
	 * @return LaunchHelper
	 */
	public LaunchHelper getInstance()
	{
		if (_instance == null)
		{
			_instance = new LaunchHelper();
		}
		return _instance;
	}
	
	/**
	 * setLaunchFileCmdLineArgs
	 * 
	 * @param args
	 */
	public void setLaunchFileCmdLineArgs(String[] args)
	{
		int startIndex = 0;

		// if any file args were passed to the application, pass them onto the WindowAdvisor
		// so that the files can be opened once the window is open.
		String[] fileList;

		String[] argList = args;

		if (argList.length > 0)
		{
			if (argList[0].toLowerCase().matches(".*?(aptana.exe|aptanastudio.exe)")) //$NON-NLS-1$
			{
				startIndex = 1;
				// String newDotAptanaFile = argList[0].replaceFirst("(?i)(aptana.exe|aptanastudio.exe)", ".aptana"); //$NON-NLS-1$ //$NON-NLS-2$
				String newDotAptanaFile = computeDotAptanaFileName();
				
				if (dotAptanaFile == null)
				{
					dotAptanaFile = newDotAptanaFile;
					IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), StringUtils.format(
							Messages.LaunchHelper_AptanaPortCachedInFile, dotAptanaFile));
				}
				else
				{
					IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), StringUtils.format(Messages.LaunchHelper_PortCacheFile,
							new String[] { dotAptanaFile, newDotAptanaFile }));
				}
			}
		}
		
		featuresToInstall.clear();

		List<String> filesList = new ArrayList<String>();
		for (int i = startIndex; i < argList.length; i++)
		{
			// IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), "received arg '" + argList[i] + "'");
			if (argList[i].startsWith("-")) //$NON-NLS-1$
			{
				if (argList[i].equals(HANDLE_URL_FLAG)) {
					if (i < (argList.length - 1)) {
						IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), "Received install request : " + argList[i+1]); //$NON-NLS-1$
						try {
							String urlString = argList[i+1];
							// Strip off prefix
							if (urlString.startsWith("aptanaplugininstaller:")) { //$NON-NLS-1$
								urlString = urlString.substring("aptanaplugininstaller:".length()); //$NON-NLS-1$
							}
							URL url = new URL(urlString);
							final String id = url.getQuery();
							if (id != null && id.length() > 0) {
								// Skip over next arg
								i++;
								URL updateSiteUrl = new URL(url.getProtocol(),
                                        url.getHost(), url.getPort(), url
                                                .getPath());
                                featuresToInstall.add(new FeatureURL(
                                        updateSiteUrl, id));
							}
						} catch (MalformedURLException e) {
							IdeLog.logError(DesktopIntegrationServerActivator.getDefault(), e.getMessage());
						}
					}
				}
			} else {
				File file = new File(argList[i]);
				if (file.exists())
				{
					filesList.add(argList[i]);
				}
			}
		}
		fileList = (String[]) filesList.toArray(new String[0]);

		initialFiles = fileList;
	}

    /**
     * checkForRunningInstance
     * 
     * @return the port number the application is running on, or -1 if there is
     *         no running instance
     */
	public int checkForRunningInstance()
	{
		int port = readCurrentPort();
		// If the .aptana file did not exist or contained a bogus port number
		// simply return
		if (port < 0) {
			return port;
		}
		// Now check if the port is actually in use
		ServerSocket serverSocket = null;
		try
		{
			serverSocket = new ServerSocket(port, 0, null);
			// Socket not in use. Assume that the .aptana file was left over from
			// a abnormal exit the last time.
			return -1;
		}
		catch (IOException e)
		{
			// Threw an exception. Assume that the port is in use by a running instance of the IDE.
			IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), e.getMessage());
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
				}
			}
		}
		return port;
	}

	public void startServer() {
	    if (dotAptanaFile == null)
	    {
	        dotAptanaFile = computeDotAptanaFileName();
	    }
		try
		{
			CommandLineArgsServer server = new CommandLineArgsServer(this);
			server.start();
		}
		catch (IOException e)
		{
			IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), Messages.LaunchHelper_ErrorInChdeckingForCurrentInstance, e);
		}
	}
	
	public void doShutdownCleanup()
	{
	    if (dotAptanaFile != null)
	    {
	        (new File(dotAptanaFile)).delete();
	    }
	}

	private int readCurrentPort()
	{
		FileReader fr = null;

		if (dotAptanaFile == null)
		{
			dotAptanaFile = computeDotAptanaFileName();
		}

		try
		{
			fr = new FileReader(dotAptanaFile);
			BufferedReader br = new BufferedReader(fr);
			String sPort = br.readLine().trim();
			if (sPort.length() == 0)
			{
				return -1;
			}
			return Integer.parseInt(sPort);
		}
		catch (FileNotFoundException e)
		{
			return -1;
		}
		catch (Exception e)
		{
			IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), Messages.LaunchHelper_UnableToFindCurrentPort, e);
			return CommandLineArgsServer.STARTING_PORT;
		}
		finally
		{
			if (fr != null)
			{
				try
				{
					fr.close();
				}
				catch (IOException e)
				{
					IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), Messages.LaunchHelper_ErrorInClosingFileReader, e);
				}
			}
		}
	}
	
	private static String computeDotAptanaFileName() {
		// Attempt to locate .aptana file in a predicatable place
		// even when launched any directory or folder
		File dotAptanaParent = new File(System.getProperty("user.dir")); //$NON-NLS-1$

		// Prefer install location
		Location location = Platform.getInstallLocation();
		if (location == null || location.isReadOnly()) {
			// If install location is null (?) or read only - try configuration location
			location = Platform.getConfigurationLocation();
			if (location == null || location.isReadOnly()) {
				// If configuration location is null (?) or read only - try configuration location
				location = Platform.getUserLocation();
				if (location == null || location.isReadOnly()) {
					// If user location is null or read-only - too bad.
					location = null;
				}
			}
		}
		if (location != null) {
			URL locationURL = location.getURL();
			if (locationURL != null && "file".equals(locationURL.getProtocol())) { //$NON-NLS-1$
				try {
					dotAptanaParent = new File(locationURL.toURI());
				} catch (URISyntaxException e) {
					dotAptanaParent = new File(locationURL.getPath());
				}
			}
		}
		
		String computedDotAptanaFile = new File(dotAptanaParent, ".aptana").getAbsolutePath(); //$NON-NLS-1$
		IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), ".aptana file " + computedDotAptanaFile); //$NON-NLS-1$
		
		return computedDotAptanaFile;
	}

	public boolean sendInitialFilesAndInstallFeatures(int port, String[] args)
	{
		Socket socket = null;
		DataOutputStream os = null;

		try
		{
			socket = new Socket(InetAddress.getByName(null), port);
			os = new DataOutputStream(socket.getOutputStream());
		}
		catch (UnknownHostException e)
		{
			IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), Messages.LaunchHelper_UnknownLocalHost);
			return false;
		}
		catch (IOException e)
		{
			IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), Messages.LaunchHelper_CouldNotGetIOConnection);
			return false;
		}

		if (socket != null && os != null)
		{
			try
			{
				StringBuilder sb = new StringBuilder();
				for (String arg : args) {
					if (sb.length() > 0) {
						sb.append(StringUtils.SPACE);
					}
					sb.append("\"" + arg + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
				PrintWriter ps = new PrintWriter(os);
				ps.println(sb.toString());
				ps.flush();
				ps.close();
				socket.close();
				return true;
			}
			catch (UnknownHostException e)
			{
				IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), Messages.LaunchHelper_TryingToConnectToUnknownHost);
			}
			catch (IOException e)
			{
				IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), Messages.LaunchHelper_IOExceptionEncountered);
			}
		}

		return false;
	}

	/**
	 * CommandLineArgsServer
	 * 
	 * @author Ingo Muschenetz
	 */
	class CommandLineArgsServer extends Thread
	{
		/**
		 * STARTING_PORT
		 */
		public static final int STARTING_PORT = 9980;

		LaunchHelper helper;
		ServerSocket server = null;
		String line;
		DataInputStream is;
		PrintStream os;
		Socket clientSocket = null;

		/**
		 * CommandLineArgsServer
		 * 
		 * @param helper
		 * @throws IOException
		 */
		public CommandLineArgsServer(LaunchHelper helper) throws IOException
		{
			super("CommandLineArgsServer"); //$NON-NLS-1$

			this.helper = helper;

			int port = getPort();

			if (port == -1)
			{
				throw new IOException(StringUtils.format(Messages.LaunchHelper_CouldNotFindOpenPort, new String[] {
						String.valueOf(STARTING_PORT), String.valueOf(STARTING_PORT + 10) }));
			}
			else
			{
				IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), StringUtils.format(Messages.LaunchHelper_BoundAptanaToPort,
						port));
			}

			try
			{
				FileWriter f = new FileWriter(dotAptanaFile);
				BufferedWriter out = new BufferedWriter(f);
				out.write(StringUtils.EMPTY + port);
				out.close();
				new File(dotAptanaFile).deleteOnExit();
			}
			catch (IOException e)
			{
			}
		}

		/**
		 * getPort
		 * 
		 * @return int
		 */
		public int getPort()
		{
			int tries = 10;
			int port = STARTING_PORT;

			while (tries > 0)
			{
				try
				{
					server = new ServerSocket(port, 0, null);
					server.setSoTimeout(1000);
					return port;
				}
				catch (IOException e)
				{
					IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), StringUtils.format(
							Messages.LaunchHelper_UnableToBindToPort, port));
					tries--;
					port++;
				}
			}

			return -1;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			while (server.isClosed() == false)
			{
				try
				{
					clientSocket = server.accept();

					BufferedReader r = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					BufferedWriter w = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

					line = r.readLine().trim();

					w.write("pong"); //$NON-NLS-1$
					w.flush();

					clientSocket.close();

					if (line.length() > 0)
					{
						helper.startupPerformed(line);
					}

				}
				catch (SocketTimeoutException e)
				{
				}
				catch (Exception e)
				{
					IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), CoreStrings.ERROR, e);
				}
			}
		}
	}

	/**
	 * hookStartupListener
	 */
	public void hookStartupListener()
	{
		// hook the exe4j launcher using reflection so that our plugin does not
		// have a compiler dependency on exe4j. The WorkbenchStartupManager is
		// a wrapper for binding to the exe4j interfaces and is injected into the
		// IDE's main startup.jar file.= during our build process.
		try
		{
			Class cls = ClassLoader.getSystemClassLoader().loadClass("com.aptana.ide.startup.WorkbenchStartupManager"); //$NON-NLS-1$
			Method startupListener;
			startupListener = cls.getMethod("setStartupListener", new Class[] { Object.class }); //$NON-NLS-1$
			startupListener.invoke(null, new Object[] { this });
		}
		catch (ClassNotFoundException e)
		{
			IdeLog.logInfo(DesktopIntegrationServerActivator.getDefault(), Messages.LaunchHelper_TheStartupListenerClassIsNotAvailable);
		}
		catch (Throwable e)
		{
			IdeLog.logError(DesktopIntegrationServerActivator.getDefault(), Messages.LaunchHelper_ErrorHookingStartupListener, e);
		}
	}

	/**
	 * startupPerformed
	 * 
	 * @param args
	 */
	public void startupPerformed(String args)
	{
		String[] startupArgs = parseCommandLineArgs(args);
		setLaunchFileCmdLineArgs(startupArgs);

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				openStartupFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			}
		});
	}

	private String[] parseCommandLineArgs(String cmdLine)
	{
		char quote = '"';
		char flagStart = '-';
		List<String> args = new ArrayList<String>();
		StringBuffer word = new StringBuffer();
		try
		{

			char[] chars = cmdLine.toCharArray();
			boolean quoteMode = false;
			for (int i = 0; i < chars.length; i++)
			{
				boolean endWord = false;

				char ch = chars[i];
				if (ch == quote)
				{
					if (quoteMode)
					{
						// this is the end quote, so end the word
						quoteMode = false;
						endWord = true;
					}
					else
					{
						quoteMode = true;
					}
				}
				else if (Character.isWhitespace(ch) && !quoteMode)
				{
					endWord = true;
				}
				else
				{
					word.append(ch);
				}

				if (endWord)
				{
					if (word.length() > 0 && (word.toString().equals(HANDLE_URL_FLAG) || word.charAt(0) != flagStart))
					{
						args.add(word.toString());
					}
					word.setLength(0);
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(DesktopIntegrationServerActivator.getDefault(), StringUtils.format(
					Messages.LaunchHelper_UnableToRecognizeCommandLineLaunchArguments, cmdLine));
		}

		if (word.length() > 0 && (word.toString().equals(HANDLE_URL_FLAG) || word.charAt(0) != flagStart))
		{
			args.add(word.toString());
		}

		String[] argArray = (String[]) args.toArray(new String[0]);
		return argArray;
	}

}
