package com.aptana.ide.syncing.tests;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.LocalConnectionPoint;

public class LocalSyncingTests extends SyncingTests
{
	protected IPath clientRootDirectory;
	protected IPath serverRootDirectory;

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
		((LocalConnectionPoint)serverManager).setPath(serverRootDirectory.append(getServerDirectory()));

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
		File baseTempFile = File.createTempFile("test", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		baseTempFile.deleteOnExit();

		File baseDirectory = baseTempFile.getParentFile();

		LocalConnectionPoint lcp = new LocalConnectionPoint();
		serverRootDirectory = new Path(baseDirectory.getAbsolutePath());
		lcp.setPath(serverRootDirectory);

		return lcp;
	}
}
