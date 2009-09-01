package com.aptana.ide.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.Bundle;

/**
 * A Job which replaces what an old install handler did: set the binaries to be executable for a given bundle (using
 * it's permissions.properties file).
 * 
 * @author cwilliams
 */
public class SetExecutableBits extends Job
{

	private Bundle bundle;

	public SetExecutableBits(Bundle bundle)
	{
		super(Messages.SetExecutableBits_Set_Binary_As_Executable);
		this.bundle = bundle;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		if (Platform.getOS().equals(Platform.OS_WIN32))
			return Status.OK_STATUS;
		// FIXME Add a shortcut that doesn't run all this stuff if the files
		// have already had the flag set!
		try
		{
			Properties props = new Properties();
			InputStream inStream = FileLocator.openStream(bundle, new Path("permissions.properties"), false); //$NON-NLS-1$
			props.load(inStream);
			String raw = props.getProperty("permissions.executable"); //$NON-NLS-1$
			String[] paths = raw.split(","); //$NON-NLS-1$
			for (int i = 0; i < paths.length; i++)
			{
				URL bundleURL = FileLocator.find(bundle, new Path(paths[i]), null);
				if (bundleURL == null)
				{
					String message = MessageFormat.format(
						Messages.SetExecutableBits_File_Not_In_Bundle,
						new Object[] {
							paths[i],
							bundle.getSymbolicName()
						}
					);
					IdeLog.logImportant(AptanaCorePlugin.getDefault(), message);
					continue;
				}
				URL fileURL = FileLocator.toFileURL(bundleURL);
				if (fileURL == null)
				{
					continue;
				}
				String fullPath = fileURL.getPath();
				setExecutableBit(fullPath);
			}
		}
		catch (IOException e)
		{
			return new Status(IStatus.ERROR, AptanaCorePlugin.ID, 1, e.getMessage(), e);
		}

		return Status.OK_STATUS;
	}

	private void setExecutableBit(String filePath)
	{
		if (filePath == null)
			return;
		try
		{
			Process pr = Runtime.getRuntime().exec(new String[] { "chmod", "a+x", filePath }); //$NON-NLS-1$ //$NON-NLS-2$
			Thread chmodOutput = new StreamConsumer(pr.getInputStream());
			chmodOutput.setName("chmod output reader"); //$NON-NLS-1$
			chmodOutput.start();
			Thread chmodError = new StreamConsumer(pr.getErrorStream());
			chmodError.setName("chmod error reader"); //$NON-NLS-1$
			chmodError.start();
		}
		catch (IOException ioe)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), ioe.getMessage(), ioe);
		}
	}

	public static class StreamConsumer extends Thread
	{
		InputStream is;
		byte[] buf;

		public StreamConsumer(InputStream inputStream)
		{
			super();
			this.setDaemon(true);
			this.is = inputStream;
			buf = new byte[512];
		}

		public void run()
		{
			try
			{
				int n = 0;
				while (n >= 0)
					n = is.read(buf);
			}
			catch (IOException ioe)
			{
			}
		}
	}
}
