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
package com.aptana.ide.core.io.file.tests;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.core.io.efs.LocalFile;
import com.aptana.ide.core.io.ingo.ConnectionException;
import com.aptana.ide.core.io.ingo.IVirtualFile;
import com.aptana.ide.core.io.ingo.IVirtualFileManager;
import com.aptana.ide.core.io.ingo.LocalFileShell;
import com.aptana.ide.core.io.ingo.ProjectFileManager;
import com.aptana.ide.core.io.ingo.ProjectProtocolManager;
import com.aptana.ide.core.io.preferences.CloakingUtils;

/**
 * Test case for local file manager
 * 
 * @author Ingo Muschenetz
 */
public class ProjectFileManagerTest extends TestCase
{
	ProjectFileManager _manager;

	/**
	 * onMac
	 */
	protected static boolean onMac = System.getProperty("os.name").startsWith("Mac OS"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		this._manager = new ProjectFileManager(null);
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		this._manager = null;
		super.tearDown();
	}

	/**
	 * testProjectFileManager
	 * 
	 * @throws IOException
	 * @throws ConnectionException 
	 */
	public void testProjectFileManager() throws IOException, ConnectionException
	{
		File f = File.createTempFile("localTest", ".js"); //$NON-NLS-1$ //$NON-NLS-2$
		String root = f.getParent() + this._manager.getFileSeparator();
		String name0 = f.getName();
		String name0_b = "localTest_b.js"; //$NON-NLS-1$
		String name0_copy = "localTest_copy.js"; //$NON-NLS-1$

		String dir0 = "dir0"; //$NON-NLS-1$
		String dir1 = "dir1"; //$NON-NLS-1$

		// check if temp exists, so we can clean up
		File froot = new File(root);
		boolean rootExists = froot.exists();

		// create file via manager
		IVirtualFile vf0 = this._manager.createVirtualFile(root + name0);
		assertNotNull(vf0);
		assertTrue(vf0 instanceof LocalFile);
		File f0 = new File(root + name0);
		f0.createNewFile();
		assertTrue(f0.exists());

		// LocalFile tests
		// check extension
		//assertTrue(vf0.getExtension().equals(".js")); //$NON-NLS-1$
		// check name
		assertEquals(name0, vf0.getName());
		assertEquals(f.getCanonicalPath(), EFSUtils.getAbsolutePath(vf0));
		assertEquals(f.getAbsolutePath(), EFSUtils.getPath(vf0));
		assertFalse(vf0.fetchInfo().isDirectory());
		assertEquals(froot.getCanonicalPath(), EFSUtils.getAbsolutePath(EFSUtils.getParentFile(vf0)));

		// create 2 folders
		IVirtualFile vd0 = this._manager.createVirtualDirectory(root + dir0);
		IVirtualFile vd1 = this._manager.createVirtualDirectory(root + dir1);
		File d0 = new File(root + dir0);
		File d1 = new File(root + dir1);
		d0.mkdir();
		assertTrue(d0.exists());
		d1.mkdir();
		assertTrue(d1.exists());

		// copy file to new name
		File dest_f0_b = new File(root + name0_copy);
		IVirtualFile vdest_f0_b = new LocalFileShell(this._manager, dest_f0_b);
		// this._manager.copyFile(vf0, vdest_f0_b);
		f0.createNewFile();
		assertTrue(f0.exists());
		dest_f0_b.createNewFile();
		assertTrue(dest_f0_b.exists());

		// copy file to folder
		File dest_f0 = new File(root + dir0 + this._manager.getFileSeparator() + name0);
		IVirtualFile vdest_f0 = new LocalFileShell(this._manager, dest_f0);
		// this._manager.copyFile(vf0, vdest_f0);
		f0.createNewFile();
		assertTrue(f0.exists());
		dest_f0.createNewFile();
		assertTrue(dest_f0.exists());

		// move file
		File dest_f1 = new File(root + dir1 + this._manager.getFileSeparator() + name0);
		IVirtualFile vdest_f1 = new LocalFileShell(this._manager, dest_f1);
		this._manager.moveFile(vf0, vdest_f1);
		assertFalse(f0.exists());
		dest_f1.createNewFile();
		assertTrue(dest_f1.exists());

		// rename file *** Note, how can we get an IVirtualFile here?
		boolean rename = this._manager.renameFile(vdest_f1, root + dir1 + "/" + name0_b); //$NON-NLS-1$
		assertTrue(rename);
		File f0b = new File(root + dir1 + "/" + name0_b); //$NON-NLS-1$
		f0b.createNewFile();
		assertTrue(f0b.exists());
		assertEquals(f0b.getCanonicalPath(), EFSUtils.getAbsolutePath(vdest_f1));

		// Check file cloaking
		assertFalse(CloakingUtils.isFileCloaked(vdest_f0_b));
		CloakingUtils.cloakFileName(vdest_f0_b);
		assertTrue(CloakingUtils.isFileCloaked(vdest_f0_b));
		assertEquals(1, this._manager.getCloakedFiles().length);
		CloakingUtils.cloakFileName(vdest_f0_b);
		assertFalse(CloakingUtils.isFileCloaked(vdest_f0_b));
		assertEquals(0, this._manager.getCloakedFiles().length);

		this._manager.addCloakExpression(".*\\.js"); //$NON-NLS-1$
		assertTrue(CloakingUtils.isFileCloaked(vdest_f0_b));
		this._manager.removeCloakExpression(".*\\.js"); //$NON-NLS-1$
		assertFalse(CloakingUtils.isFileCloaked(vdest_f0_b));

		// remove files via manager
		this._manager.deleteFile(vdest_f0_b);
		assertFalse(dest_f0_b.exists());
		this._manager.deleteFile(vdest_f0);
		assertFalse(dest_f0.exists());

		// remove folders via manager
		this._manager.deleteFile(vd0);
		this._manager.deleteFile(vd1);
		assertFalse(d0.exists());
		assertFalse(d1.exists());

		// clean up temp if we created it
		if (!rootExists)
		{
			froot.delete();
		}
	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.fromSerializableString(String)'
	 * 
	 * @throws ConnectionException
	 */
	public void testFromSerializableString() throws ConnectionException
	{
		IVirtualFileManager fileManager = ProjectProtocolManager.getInstance().createFileManager();
		ProjectFileManager ftp = (ProjectFileManager) fileManager;
		ftp.setNickName("nickname"); //$NON-NLS-1$
		ftp.setBasePath("/base/path"); //$NON-NLS-1$
		// ftp.setId(0); // Id is auto-calculated
		ftp.setAutoCalculateServerTimeOffset(!ftp.isAutoCalculateServerTimeOffset());
		ftp.setTimeOffset(100000); // requires a valid server, so we have to fake a value

		IVirtualFile f1 = ftp.createVirtualFile("/base/path/test.txt"); //$NON-NLS-1$
		IVirtualFile f2 = ftp.createVirtualDirectory("/base/path/test_directory"); //$NON-NLS-1$
		ftp.addCloakedFile(f1);
		ftp.addCloakedFile(f2);

		ftp.addCloakExpression(".*\\.js"); //$NON-NLS-1$
		ftp.addCloakExpression("\\.svn"); //$NON-NLS-1$

		String serialized = ftp.getHashString();
		IVirtualFileManager fileManager2 = ProjectProtocolManager.getInstance().createFileManager();
		ProjectFileManager ftp2 = (ProjectFileManager) fileManager2;
		ftp2.fromSerializableString(serialized);

		String[] strings = serialized.split(ProjectFileManager.DELIMITER);
		assertEquals(7, strings.length);

		assertEquals(ftp.getNickName(), ftp2.getNickName());
		assertEquals(ftp.getBasePath(), ftp2.getBasePath());
		assertEquals(ftp.isAutoCalculateServerTimeOffset(), ftp2.isAutoCalculateServerTimeOffset());
		//assertEquals(ftp.getId(), ftp2.getId());
		assertEquals(ftp.getTimeOffset(), ftp2.getTimeOffset());
		assertEquals(ftp.getCloakedFiles().length, ftp2.getCloakedFiles().length);
		assertEquals(ftp.getCloakedFileExpressions().length, ftp2.getCloakedFileExpressions().length);
	}

}
