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
package com.aptana.ide.syncing.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.VirtualFileManagerException;
import com.aptana.ide.io.sftp.SftpProtocolManager;
import com.aptana.ide.io.sftp.SftpVirtualFileManager;

/**
 * @author Kevin Lindsey
 */
public class SftpTests extends TestCase
{
	/*
	 * Fields
	 */
	private static final String server = "build.aptana.com"; //$NON-NLS-1$
	private static final String user = "aptana_unit_tests"; //$NON-NLS-1$
	private static final String pass = System.getProperty("sftp.password"); //$NON-NLS-1$
	private static final String home = "/home/aptana_unit_tests"; //$NON-NLS-1$

	// private static final String server = "lemur";
	// private static final String user = "kevinl";
	// private static final String pass = System.getProperty("sftp.password");
	// private static final String home = "/Users/kevinl/Sites/";

	private SftpVirtualFileManager _manager;

	/*
	 * Tests
	 */

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		SftpProtocolManager protocolManager = new SftpProtocolManager();

		this._manager = (SftpVirtualFileManager) protocolManager.createFileManager();
		this._manager.setServer(server);
		this._manager.setUser(user);
		this._manager.setPassword(pass);
		this._manager.setBasePath(home);
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		this._manager.disconnect();
	}

	/**
	 * testGetFiles
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 * @throws InterruptedException 
	 */
	public void testGetFiles() throws IOException, ConnectionException, InterruptedException
	{
		String path = home + this._manager.getFileSeparator() + FileUtils.getRandomFileName("unit_test_getFiles", null); //$NON-NLS-1$

		IVirtualFile folder = this._manager.createVirtualDirectory(path);
		this._manager.createLocalDirectory(folder);

		IVirtualFile[] files = this._manager.getFiles(folder, true, true);
		assertEquals(files.length, 0);

		// Create some files
		IVirtualFile file1 = this._manager.createVirtualFile(path + this._manager.getFileSeparator() + FileUtils.getRandomFileName("test1", ".txt")); //$NON-NLS-1$ //$NON-NLS-2$
		IVirtualFile file2 = this._manager.createVirtualFile(path + this._manager.getFileSeparator() + FileUtils.getRandomFileName("test2", ".txt")); //$NON-NLS-1$ //$NON-NLS-2$
		createFile(file1);
		createFile(file2);
		
		// assert that they are now there
		files = this._manager.getFiles(folder, true, true);
		Arrays.sort(files);
		assertEquals(2, files.length);
		// since names are random, they may not be in the same order as we put them in
		assertTrue(Arrays.binarySearch(files, file1) >= 0);
		assertTrue(Arrays.binarySearch(files, file2) >= 0);

		this._manager.addCloakExpression("(?i).*\\.txt"); //$NON-NLS-1$
		assertTrue(file1.isCloaked());
		assertTrue(file2.isCloaked());
		assertEquals(0, this._manager.getFiles(folder, true, false).length);
		this._manager.removeCloakExpression("(?i).*\\.txt"); //$NON-NLS-1$
		assertEquals(2, this._manager.getFiles(folder, true, false).length);

		// delete them
		Thread.sleep(1000); // not adding these seems to cause sporadic exceptions. need to debug
		assertTrue(deleteFile(file1));
		assertTrue(deleteFile(file2));
		Thread.sleep(1000);
		assertTrue(deleteFile(folder));

		// assert that they are gone
		assertFalse(folder.exists());
	}
	/**
	 * testDoesNotExist
	 * 
	 * @throws ConnectionException
	 * @throws IOException
	 */
	public void testDoesNotExist() throws ConnectionException, IOException
	{
		String path = home + this._manager.getFileSeparator() + FileUtils.getRandomFileName("nothing", null) //$NON-NLS-1$
				+ this._manager.getFileSeparator() + "non-existant.txt"; //$NON-NLS-1$
		IVirtualFile file = this._manager.createVirtualFile(path);

		assertFalse(file.exists());
	}

	/**
	 * testExists
	 * 
	 * @throws ConnectionException
	 * @throws IOException
	 */
	public void testExists() throws ConnectionException, IOException
	{
		String path = home + this._manager.getFileSeparator() + FileUtils.getRandomFileName("index_test", ".html"); //$NON-NLS-1$ //$NON-NLS-2$
		IVirtualFile file = this._manager.createVirtualFile(path);
		assertFalse(file.exists());
		createFile(file);
		assertTrue(file.exists());
		deleteFile(file);
	}

	/**
	 * testCreateDirectory
	 * 
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	public void testCreateDirectory() throws ConnectionException, VirtualFileManagerException
	{
		String path = home + this._manager.getFileSeparator() + FileUtils.getRandomFileName("unit_tests", null); //$NON-NLS-1$
		IVirtualFile dir = this._manager.createVirtualDirectory(path);

		// make sure we successfully created a directory
		assertTrue(this._manager.createLocalDirectory(dir));

		// delete created directory
		assertTrue(this._manager.deleteFile(dir));
	}

	/**
	 * testDeleteFile
	 * 
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	public void testDeleteFile() throws ConnectionException, VirtualFileManagerException
	{
		String path = home + this._manager.getFileSeparator() + FileUtils.getRandomFileName("delete", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		IVirtualFile file = this._manager.createVirtualFile(path);
		String content = "This is a unit test file that should be deleted."; //$NON-NLS-1$
		ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());

		// create temp file to delete
		this._manager.putStream(input, file);

		// delete file
		assertTrue(this._manager.deleteFile(file));
	}

	/**
	 * testRenameFile
	 * 
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	public void testRenameFile() throws ConnectionException, VirtualFileManagerException
	{
		String path = home + this._manager.getFileSeparator() + FileUtils.getRandomFileName("rename_temp", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		String newPath = home + this._manager.getFileSeparator() + FileUtils.getRandomFileName("new_name_temp", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$

		IVirtualFile file = this._manager.createVirtualFile(path);
		IVirtualFile newFile = this._manager.createVirtualFile(newPath);

		String content = "This is a unit test file that should be deleted."; //$NON-NLS-1$
		ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());

		// create temp file to delete
		this._manager.putStream(input, file);

		// should fail as we need a name, not a path
		try
		{
			file.rename(newPath);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}
		
		// rename file
		assertTrue(file.rename(newFile.getName()));

		// Prove that it is now in the new space
		assertEquals(file.getAbsolutePath(), newFile.getAbsolutePath());

		// delete file
		assertTrue(file.delete());
	}

	/**
	 * testDeleteFile
	 * 
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	public void testGetStream() throws ConnectionException, VirtualFileManagerException
	{
		String path = home + this._manager.getFileSeparator() + FileUtils.getRandomFileName("delete", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		IVirtualFile file = this._manager.createVirtualFile(path);
		createFile(file);

		// make sure we can get a stream
		InputStream s = this._manager.getStream(file);
		assertNotNull(s);

		// delete file
		assertTrue(deleteFile(file));
	}

	private void createFile(IVirtualFile file) throws VirtualFileManagerException, ConnectionException
	{
		String content = "This is a unit test file that should be deleted."; //$NON-NLS-1$
		ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());

		// create temp file to delete
		this._manager.putStream(input, file);
	}

	private boolean deleteFile(IVirtualFile file) throws ConnectionException, VirtualFileManagerException
	{
		return file.delete();
	}
}
