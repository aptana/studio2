/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.syncing.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;

import junit.framework.TestCase;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.core.io.ingo.ConnectionException;
import com.aptana.ide.core.io.ingo.IVirtualFile;
import com.aptana.ide.core.io.ingo.IVirtualFileManager;
import com.aptana.ide.core.io.ingo.LocalProtocolManager;
import com.aptana.ide.core.io.ingo.VirtualFileSyncPair;
import com.aptana.ide.core.io.syncing.SyncState;
import com.aptana.ide.syncing.core.ingo.Synchronizer;

/**
 * @author Kevin Lindsey
 */
public class SyncingTests extends TestCase
{
	private IVirtualFileManager clientManager;
	private IVirtualFileManager serverManager;
	private File clientDirectory;
	private File serverDirectory;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		// we'll use the local file system to make the testing a bit easier
		LocalProtocolManager protocolManager = new LocalProtocolManager();

		// setup the client file manager
		clientManager = protocolManager.createFileManager();

		// setup the server file manager
		serverManager = protocolManager.createFileManager();

		File baseTempFile = File.createTempFile("test", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		File baseClientDirectory = baseTempFile.getParentFile();
		if (!baseTempFile.delete())
		{ // remove the temp file...
			baseTempFile.deleteOnExit();
		}

		clientDirectory = new File(baseClientDirectory.getAbsolutePath() + clientManager.getFileSeparator() + "client"); //$NON-NLS-1$
		serverDirectory = new File(baseClientDirectory.getAbsolutePath() + clientManager.getFileSeparator() + "server"); //$NON-NLS-1$

		// make sure they don't exist first
		removeDirectory(clientDirectory);
		removeDirectory(serverDirectory);

		// then recreate them so we know they're empty
		clientDirectory.mkdir();
		serverDirectory.mkdir();

		clientManager.setBasePath(clientDirectory.getAbsolutePath());
		serverManager.setBasePath(serverDirectory.getAbsolutePath());

	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		clientManager = null;
		serverManager = null;
		
		// clean up directories created for each test
		removeDirectory(clientDirectory);
		removeDirectory(serverDirectory);
		clientDirectory = null;
		serverDirectory = null;
		
		super.tearDown();
	}

	/**
	 * Recursively deletes a directory and its contents
	 * 
	 * @param file
	 */
	protected void removeDirectory(File file)
	{
		if (file.exists())
		{
			if (file.isDirectory())
			{
				File[] files = file.listFiles();

				for (int i = 0; i < files.length; i++)
				{
					File f = files[i];

					if (f.isDirectory())
					{
						removeDirectory(f);
					}
					else
					{
						f.delete();
					}
				}
			}

			file.delete();
		}
	}

	/**
	 * createServerDirectory
	 * 
	 * @param manager
	 * @param path
	 * @return IVirtualFile
	 * @throws CoreException 
	 */
	protected IVirtualFile getDirectory(IVirtualFileManager manager, String path) throws CoreException
	{
		return manager.createVirtualDirectory(EFSUtils.getAbsolutePath(manager.getRoot())
				+ serverManager.getFileSeparator() + path);
	}

	/**
	 * createServerDirectory
	 * 
	 * @param manager
	 * @param path
	 * @return IVirtualFile
	 * @throws CoreException 
	 */
	protected IVirtualFile getFile(IVirtualFileManager manager, String path) throws CoreException
	{
		return manager.createVirtualFile(EFSUtils.getAbsolutePath(manager.getRoot()) + serverManager.getFileSeparator()
				+ path);
	}

	/**
	 * createClientDirectory
	 * 
	 * @param path
	 * @param modificationTime
	 * @return File
	 * @throws CoreException 
	 */
	protected File createClientDirectory(String path, long modificationTime) throws CoreException
	{
		return this.createDirectory(EFSUtils.getAbsolutePath(clientManager.getRoot()) + clientManager.getFileSeparator()
				+ path, modificationTime);
	}

	/**
	 * createServerDirectory
	 * 
	 * @param path
	 * @param modificationTime
	 * @return File
	 * @throws CoreException 
	 */
	protected File createServerDirectory(String path, long modificationTime) throws CoreException
	{
		return this.createDirectory(EFSUtils.getAbsolutePath(serverManager.getRoot()) + serverManager.getFileSeparator()
				+ path, modificationTime);
	}

	/**
	 * createDirectory
	 * 
	 * @param path
	 * @param modificationTime
	 * @return File
	 */
	protected File createDirectory(String path, long modificationTime)
	{
		File file = new File(path);

		if (file.mkdir())
		{
			file.setLastModified(modificationTime);
		}

		return file;
	}

	/**
	 * createClientFile
	 * 
	 * @param path
	 * @param modificationTime
	 * @throws IOException
	 * @throws IOException
	 * @return File
	 * @throws CoreException 
	 */
	protected File createClientFile(String path, long modificationTime) throws IOException, CoreException
	{
		return this.createClientFile(path, modificationTime, null);
	}

	/**
	 * createClientFile
	 * 
	 * @param path
	 * @param modificationTime
	 * @param content
	 * @throws IOException
	 * @return File
	 * @throws CoreException 
	 */
	protected File createClientFile(String path, long modificationTime, String content) throws IOException, CoreException
	{
		String fullpath = EFSUtils.getAbsolutePath(clientManager.getRoot()) + clientManager.getFileSeparator() + path;

		return this.createFile(fullpath, modificationTime, content);
	}

	/**
	 * createClientFile
	 * 
	 * @param path
	 * @param modificationTime
	 * @throws IOException
	 * @throws IOException
	 * @return File
	 * @throws CoreException 
	 */
	protected File createServerFile(String path, long modificationTime) throws IOException, CoreException
	{
		return this.createServerFile(path, modificationTime, null);
	}

	/**
	 * createServerFile
	 * 
	 * @param path
	 * @param modificationTime
	 * @param content
	 * @throws IOException
	 * @return File
	 * @throws CoreException 
	 */
	protected File createServerFile(String path, long modificationTime, String content) throws IOException, CoreException
	{
		String fullpath = EFSUtils.getAbsolutePath(serverManager.getRoot()) + serverManager.getFileSeparator() + path;

		return this.createFile(fullpath, modificationTime, content);
	}

	/**
	 * createFile
	 * 
	 * @param path
	 * @param modificationTime
	 * @param content
	 * @throws IOException
	 * @return File
	 */
	protected File createFile(String path, long modificationTime, String content) throws IOException
	{
		File file = new File(path);

		if (file.createNewFile())
		{
			if (content != null && content.length() > 0)
			{
				FileWriter writer = new FileWriter(file);

				writer.write(content);

				writer.close();
			}

			// update modification date/time
			file.setLastModified(modificationTime);
		}

		return file;
	}

	/**
	 * getSyncItems
	 * 
	 * @return SyncItem[]
	 * @throws IOException
	 * @throws ConnectionException
	 */
	protected VirtualFileSyncPair[] getSyncItems() throws IOException, ConnectionException, CoreException
	{
		return this.getSyncItems(false, 0);
	}

	/**
	 * getSyncItems
	 * 
	 * @param useCRC
	 * @param timeTolerance
	 * @return SyncItem[]
	 * @throws IOException
	 * @throws ConnectionException
	 * @throws CoreException 
	 */
	protected VirtualFileSyncPair[] getSyncItems(boolean useCRC, int timeTolerance) throws IOException,
			ConnectionException, CoreException
	{
		Synchronizer syncManager = new Synchronizer(useCRC, timeTolerance);

		return syncManager.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());
	}

	/*
	 * Sync Item Tests
	 */

	/**
	 * testClientFileOnly
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientFileOnly() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile("test.txt", currentTime); //$NON-NLS-1$

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertEquals(1, items.length);
		assertEquals(SyncState.ClientItemOnly, items[0].getSyncState());
	}

	/**
	 * testClientDirectoryOnly
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientDirectoryOnly() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory("test", currentTime); //$NON-NLS-1$

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertEquals(1, items.length);
		assertEquals(SyncState.ClientItemOnly, items[0].getSyncState());
	}

	/**
	 * testClientFileIsNewer
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientFileIsNewer() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = "test.txt"; //$NON-NLS-1$
		this.createClientFile(filename, currentTime);
		this.createServerFile(filename, currentTime - 1000);

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertEquals(1, items.length);
		assertEquals(SyncState.ClientItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testClientDirectoryIsNewer
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientDirectoryIsNewer() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String directoryName = "test"; //$NON-NLS-1$
		this.createClientDirectory(directoryName, currentTime);
		this.createServerDirectory(directoryName, currentTime - 1000);

		VirtualFileSyncPair[] items = this.getSyncItems();

		// we now delete remote directories
		assertEquals(0, items.length);
		// assertEquals(SyncState.ClientItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testServerFileOnly
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerFileOnly() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createServerFile("test.txt", currentTime); //$NON-NLS-1$

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertEquals(1, items.length);
		assertEquals(SyncState.ServerItemOnly, items[0].getSyncState());
	}

	/**
	 * testServerDirectoryOnly
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerDirectoryOnly() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createServerDirectory("test", currentTime); //$NON-NLS-1$

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertEquals(1, items.length);
		assertEquals(SyncState.ServerItemOnly, items[0].getSyncState());
	}

	/**
	 * testServerFileIsNewer
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerFileIsNewer() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = "test.txt"; //$NON-NLS-1$
		this.createClientFile(filename, currentTime - 1000);
		this.createServerFile(filename, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertEquals(1, items.length);
		assertEquals(SyncState.ServerItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testServerDirectoryIsNewer
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerDirectoryIsNewer() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String directoryName = "test"; //$NON-NLS-1$
		this.createClientDirectory(directoryName, currentTime - 1000);
		this.createServerDirectory(directoryName, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems();

		// we now delete directories from sync
		assertEquals(0, items.length);
		// assertEquals(SyncState.ServerItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testFileTimesMatch
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFileTimesMatch() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = "test.txt"; //$NON-NLS-1$
		this.createClientFile(filename, currentTime);
		this.createServerFile(filename, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertEquals(1, items.length);
		assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testDirectoryTimesMatch
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testDirectoryTimesMatch() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String directoryName = "test"; //$NON-NLS-1$
		this.createClientDirectory(directoryName, currentTime);
		this.createServerDirectory(directoryName, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems();

		// we now delete directories
		assertEquals(0, items.length);
		// assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testFileTimesWithinTolerance1
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFileTimesWithinTolerance1() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = "test.txt"; //$NON-NLS-1$
		this.createClientFile(filename, currentTime - 1000);
		this.createServerFile(filename, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 1000);

		assertEquals(1, items.length);
		assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testFileTimesWithinTolerance2
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFileTimesWithinTolerance2() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = "test.txt"; //$NON-NLS-1$
		this.createClientFile(filename, currentTime);
		this.createServerFile(filename, currentTime - 1000);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 1000);

		assertEquals(1, items.length);
		assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testFileTimesOutsideTolerance1
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFileTimesOutsideTolerance1() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = "test.txt"; //$NON-NLS-1$
		this.createClientFile(filename, currentTime - 1000);
		this.createServerFile(filename, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 999);

		assertEquals(1, items.length);
		assertEquals(SyncState.ServerItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testFileTimesOutsideTolerance2
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFileTimesOutsideTolerance2() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = "test.txt"; //$NON-NLS-1$
		this.createClientFile(filename, currentTime);
		this.createServerFile(filename, currentTime - 1000);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 999);

		assertEquals(1, items.length);
		assertEquals(SyncState.ClientItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testDirectoryTimesWithinTolerance1
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testDirectoryTimesWithinTolerance1() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String dirname = "test"; //$NON-NLS-1$
		this.createClientDirectory(dirname, currentTime - 1000);
		this.createServerDirectory(dirname, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 1000);

		// we now delete directories
		assertEquals(0, items.length);
		// assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testDirectoryTimesWithinTolerance2
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testDirectoryTimesWithinTolerance2() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String dirname = "test"; //$NON-NLS-1$
		this.createClientDirectory(dirname, currentTime);
		this.createServerDirectory(dirname, currentTime - 1000);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 1000);

		// we now delete directories
		assertEquals(0, items.length);
		// assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testDirectoryTimesOutsideTolerance1
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testDirectoryTimesOutsideTolerance1() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String dirname = "test"; //$NON-NLS-1$
		this.createClientDirectory(dirname, currentTime - 1000);
		this.createServerDirectory(dirname, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 999);

		// we now delete directories
		assertEquals(0, items.length);
		// assertEquals(SyncState.ServerItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testDirectoryTimesOutsideTolerance2
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testDirectoryTimesOutsideTolerance2() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String dirname = "test"; //$NON-NLS-1$
		this.createClientDirectory(dirname, currentTime);
		this.createServerDirectory(dirname, currentTime - 1000);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 999);

		// we now delete directories
		assertEquals(0, items.length);
		// assertEquals(SyncState.ClientItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testFilesCRCsDiffer()
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFilesCRCsDiffer() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = "test.txt"; //$NON-NLS-1$
		String content = "abc123"; //$NON-NLS-1$
		this.createClientFile(filename, currentTime, content);
		this.createServerFile(filename, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems(true, 0);

		assertEquals(1, items.length);
		assertEquals(SyncState.CRCMismatch, items[0].getSyncState());
	}

	/**
	 * testFilesCRCsMatch()
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFilesCRCsMatch() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = "test.txt"; //$NON-NLS-1$
		String content = "abc123"; //$NON-NLS-1$
		this.createClientFile(filename, currentTime, content);
		this.createServerFile(filename, currentTime, content);

		VirtualFileSyncPair[] items = this.getSyncItems(true, 0);

		assertEquals(1, items.length);
		assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testDirectoryCRCsMatch This confirms that turning on CRC checking doesn't involve directories
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testDirectoryCRCsMatch() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String directoryName = "test"; //$NON-NLS-1$
		this.createClientDirectory(directoryName, currentTime);
		this.createServerDirectory(directoryName, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems(true, 0);

		// we now delete directories
		assertEquals(0, items.length);
		// assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testTypeMismatch1
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testTypeMismatch1() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String name = "test"; //$NON-NLS-1$
		this.createClientFile(name, currentTime);
		this.createServerDirectory(name, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertEquals(1, items.length);
		assertEquals(SyncState.IncompatibleFileTypes, items[0].getSyncState());
	}

	/**
	 * testTypeMismatch2
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testTypeMismatch2() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		String name = "test"; //$NON-NLS-1$
		this.createClientDirectory(name, currentTime);
		this.createServerFile(name, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertEquals(1, items.length);
		assertEquals(SyncState.IncompatibleFileTypes, items[0].getSyncState());
	}

	/**
	 * testClientOnlyFileUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientOnlyFileUpload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile("test.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		IVirtualFile clientFileOnServer = getFile(serverManager, clientDirectory.getName());
		assertFalse("Server file: " + EFSUtils.getAbsolutePath(clientFileOnServer) + " exists.", clientFileOnServer.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$

		// sync
		syncManager.upload(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(1, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse(
				"Server file: " + EFSUtils.getAbsolutePath(clientFileOnServer) + " does not exist.", clientFileOnServer.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientOnlyDirectoryUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientOnlyDirectoryUpload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File clientDirectory = this.createClientDirectory("test", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		IVirtualFile clientFileOnServer = getDirectory(serverManager, clientDirectory.getName());
		assertFalse("Server file: " + EFSUtils.getAbsolutePath(clientFileOnServer) + " exists.", clientFileOnServer.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$

		// sync
		syncManager.upload(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(1, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(clientFileOnServer) + " does not exist.", clientFileOnServer.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientNewerFileUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientNewerFileUpload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile("test.txt", currentTime); //$NON-NLS-1$
		this.createServerFile("test.txt", currentTime - 1000); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.upload(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(1, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testClientNewerDirectoryUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientNewerDirectoryUpload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory("test", currentTime); //$NON-NLS-1$
		this.createServerDirectory("test", currentTime - 1000); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.upload(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testFileCRCsDifferUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFileCRCsDifferUpload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile("test.txt", currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$
		this.createServerFile("test.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.upload(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(1, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testFileCRCsMatchUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFileCRCsMatchUpload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile("test.txt", currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$
		this.createServerFile("test.txt", currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.upload(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testDirectoryCRCsMatchUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testDirectoryCRCsMatchUpload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory("test", currentTime); //$NON-NLS-1$
		this.createServerDirectory("test", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.upload(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testServerOnlyFileUploadAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerOnlyFileUploadAndDelete() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File serverFile = this.createServerFile("delete.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.uploadAndDelete(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(1, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse("Server file: " + serverFile.getAbsolutePath() + " should be deleted.", serverFile.exists()); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * testServerOnlyDirectoryUploadAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerOnlyDirectoryUploadAndDelete() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File serverDir = this.createServerDirectory("delete", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.uploadAndDelete(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(1, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse("Server file: " + serverDir.getAbsolutePath() + " should be deleted.", serverDir.exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testServerOnlyFileDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerOnlyFileDownload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File serverFile = this.createServerFile("test.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.download(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(1, syncManager.getServerFileTransferedCount());

		IVirtualFile serverFileOnClient = getFile(clientManager, serverFile.getName());
		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(serverFileOnClient) + " does not exist.", serverFileOnClient.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testServerOnlyDirectoryDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerOnlyDirectoryDownload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File serverDir = this.createServerDirectory("test", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.download(items);

		// check client counts
		assertEquals(1, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		IVirtualFile serverFileOnClient = getDirectory(clientManager, serverDir.getName());
		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(serverFileOnClient) + " does not exist.", serverFileOnClient.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * testServerNewerFileDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerNewerFileDownload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile("test.txt", currentTime - 1000); //$NON-NLS-1$
		this.createServerFile("test.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.download(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(1, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testServerNewerDirectoryDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerNewerDirectoryDownload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory("test", currentTime - 1000); //$NON-NLS-1$
		this.createServerDirectory("test", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.download(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testFileCRCsDifferDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFileCRCsDifferDownload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile("test.txt", currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$
		this.createServerFile("test.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.download(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(1, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testFileCRCsMatchDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFileCRCsMatchDownload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile("test.txt", currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$
		this.createServerFile("test.txt", currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.download(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testDirectoryCRCsMatchDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testDirectoryCRCsMatchDownload() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory("test", currentTime); //$NON-NLS-1$
		this.createServerDirectory("test", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.download(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testClientOnlyFileDownloadAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientOnlyFileDownloadAndDelete() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File clientFile = this.createClientFile("delete.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.downloadAndDelete(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(1, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse("Client file: " + clientFile.getAbsolutePath() + " should be deleted.", clientFile.exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientOnlyDirectoryDownloadAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientOnlyDirectoryDownloadAndDelete() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File clientFile = this.createClientDirectory("delete", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.downloadAndDelete(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(1, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse("Client directory: " + clientFile.getAbsolutePath() + " should be deleted.", clientFile.exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientOnlyFileFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientOnlyFileFullSync() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File clientFile = this.createClientFile("test.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSync(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(1, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		IVirtualFile clientFileOnServer = getFile(serverManager, clientFile.getName());
		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(clientFileOnServer) + " does not exist.", clientFileOnServer.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * testClientOnlyDirectoryFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientOnlyDirectoryFullSync() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File clientFile = this.createClientDirectory("test", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSync(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(1, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		IVirtualFile clientFileOnServer = getDirectory(serverManager, clientFile.getName());
		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(clientFileOnServer) + " does not exist.", clientFileOnServer.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * testClientOnlyFileFullSyncAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientOnlyFileFullSyncAndDelete() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File clientFile = this.createClientFile("delete.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSyncAndDelete(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(1, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse("Client file: " + clientFile.getAbsolutePath() + " should be deleted.", clientFile.exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientOnlyDirectoryFullSyncAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientOnlyDirectoryFullSyncAndDelete() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File clientFile = this.createClientDirectory("delete", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSyncAndDelete(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(1, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse("Client directory: " + clientFile.getAbsolutePath() + " should be deleted.", clientFile.exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientNewerFileFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientNewerFileFullSync() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile("test.txt", currentTime); //$NON-NLS-1$
		this.createServerFile("test.txt", currentTime - 1000); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSync(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(1, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testClientNewerDirectoryFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testClientNewerDirectoryFullSync() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory("test", currentTime); //$NON-NLS-1$
		this.createServerDirectory("test", currentTime - 1000); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSync(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testServerOnlyFileFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerOnlyFileFullSync() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File serverFile = this.createServerFile("test.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSync(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(1, syncManager.getServerFileTransferedCount());

		IVirtualFile serverFileOnClient = getFile(clientManager, serverFile.getName());
		assertTrue("Server file: " + serverFile.getAbsolutePath() + " does not exist.", serverFileOnClient.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testServerOnlyDirectoryFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerOnlyDirectoryFullSync() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File file = this.createServerDirectory(FileUtils.getRandomFileName("test", null), currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSync(items);

		// check client counts
		assertEquals(1, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		IVirtualFile directory = getDirectory(serverManager, file.getName());
		assertTrue("Server file: " + EFSUtils.getAbsolutePath(directory) + " does not exist.", directory.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testServerOnlyFileFullSyncAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerOnlyFileFullSyncAndDelete() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File serverFile = this.createServerFile("delete.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSyncAndDelete(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(1, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse("Server file: " + serverFile.getAbsolutePath() + " should be deleted.", serverFile.exists()); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * testServerOnlyDirectoryFullSyncAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerOnlyDirectoryFullSyncAndDelete() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File serverDir = this.createServerDirectory("delete", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSyncAndDelete(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(1, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse("Server file: " + serverDir.getAbsolutePath() + " should be deleted.", serverDir.exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * testServerNewerFileFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerNewerFileFullSync() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		File clientFile = this.createClientFile(FileUtils.getRandomFileName("test", ".txt"), currentTime - 1000); //$NON-NLS-1$ //$NON-NLS-2$
		File serverFile = this.createServerFile(clientFile.getName(), currentTime);

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSync(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(1, syncManager.getServerFileTransferedCount());

		IVirtualFile clientFileOnServer = getFile(serverManager, clientFile.getName());
		assertTrue(clientFileOnServer.fetchInfo().exists());

		IVirtualFile serverFileOnClient = getFile(clientManager, serverFile.getName());
		assertTrue("Server file: " + serverFile.getAbsolutePath() + " does not exist.", serverFileOnClient.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testServerNewerDirectoryFullSync()
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testServerNewerDirectoryFullSync() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory("test", currentTime - 1000); //$NON-NLS-1$
		this.createServerDirectory("test", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSync(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testFileCRCsDifferFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFileCRCsDifferFullSync() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile("test.txt", currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$
		this.createServerFile("test.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSync(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testFileCRCsMatchFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testFileCRCsMatchFullSync() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile("test.txt", currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$
		this.createServerFile("test.txt", currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSync(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testDirectoryCRCsMatchFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void testDirectoryCRCsMatchFullSync() throws IOException, ConnectionException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory("test", currentTime); //$NON-NLS-1$
		this.createServerDirectory("test", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager
				.getSyncItems(clientManager, serverManager, clientManager.getRoot(), serverManager.getRoot());

		// sync
		syncManager.fullSync(items);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}
}
