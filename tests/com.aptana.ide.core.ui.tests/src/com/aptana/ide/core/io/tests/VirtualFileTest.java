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
package com.aptana.ide.core.io.tests;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.core.io.ingo.IVirtualFile;
import com.aptana.ide.core.io.ingo.LocalFileManager;
import com.aptana.ide.core.io.ingo.LocalProtocolManager;
import com.aptana.ide.core.io.ingo.VirtualFile;

/**
 * VirtualFileTest
 * 
 * @author Ingo Muschenetz
 */
public class VirtualFileTest extends TestCase
{

	/**
	 * Test method for 'com.aptana.ide.core.io.VirtualFile.getParentDirectories(IVirtualFile, IVirtualFileManager)'
	 * 
	 * @throws IOException
	 */
	public void testGetParentDirectories() throws IOException
	{
		File baseFile = File.createTempFile("test", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		File baseDirectory = baseFile.getParentFile();

		LocalFileManager fm = new LocalFileManager(LocalProtocolManager.getInstance());
		fm.setBasePath(baseDirectory.getAbsolutePath());

		// temp directory itself. should return no parents
		IVirtualFile[] dirs = VirtualFile.getParentDirectories(fm.getBaseFile(), fm);
		assertEquals(0, dirs.length);

		// parent directory of temp directory
		dirs = VirtualFile.getParentDirectories(EFSUtils.getParentFile(fm.getBaseFile()), fm);
		assertEquals(0, dirs.length);

		// sub directory
		File subDir = new File(baseDirectory.getAbsolutePath() + fm.getFileSeparator() + "testsub"); //$NON-NLS-1$
		subDir.mkdir();
		IVirtualFile testsub = fm.createVirtualFile(subDir.getAbsolutePath());

		dirs = VirtualFile.getParentDirectories(testsub, fm);
		assertEquals(1, dirs.length);
	}
}
