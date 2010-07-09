package com.aptana.ide.syncing.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.filesystem.ftp.FTPConnectionPoint;

public class FTPSyncingTests extends SyncingTests
{
	protected IPath clientRootDirectory;

	@Override
	protected void setUp() throws Exception
	{
		clientManager = getClientConnectionPoint();
		IFileStore cs = clientManager.getRoot().getFileStore(getClientDirectory());
		if(!cs.fetchInfo().exists())
			cs.mkdir(EFS.NONE, null);
		clientManager.disconnect(null);
		((LocalConnectionPoint)clientManager).setPath(clientRootDirectory.append(getClientDirectory()));
		
		serverManager = getServerConnectionPoint();
		IFileStore ss = serverManager.getRoot().getFileStore(getServerDirectory());
		if(!ss.fetchInfo().exists())
			ss.mkdir(EFS.NONE, null);
		serverManager.disconnect(null);
		((FTPConnectionPoint)serverManager).setPath(getServerDirectory());

		super.setUp();
	}

	@Override
	public IConnectionPoint getClientConnectionPoint() throws IOException
	{
		File baseTempFile = File.createTempFile("test", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		baseTempFile.deleteOnExit();

		File baseDirectory = baseTempFile.getParentFile();

		LocalConnectionPoint lcp = new LocalConnectionPoint();
		clientRootDirectory = new Path(baseDirectory.getAbsolutePath());
		lcp.setPath(clientRootDirectory);

		return lcp;
	}
	
	@Override
	public IConnectionPoint getServerConnectionPoint() throws IOException
	{
		Properties props = new Properties();
		FileInputStream inStream = new FileInputStream(System.getenv().get("junit.properties"));
		props.load(inStream);

		FTPConnectionPoint ftpcp = new FTPConnectionPoint();
		ftpcp.setHost(props.getProperty("ftp.host")); //$NON-NLS-1$
		ftpcp.setLogin(props.getProperty("ftp.username")); //$NON-NLS-1$
		ftpcp.setPassword(props.getProperty("ftp.password").toCharArray());

		return ftpcp;
	}
}
