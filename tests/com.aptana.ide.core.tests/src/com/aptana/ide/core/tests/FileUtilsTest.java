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
package com.aptana.ide.core.tests;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.FileUtils;

import junit.framework.TestCase;

/**
 * FileUtilsTest
 * 
 * @author Ingo Muschenetz
 */
public class FileUtilsTest extends TestCase
{
	
	/**
	 * Test method for 'com.aptana.ide.core.FileUtils.getExtension(String)'
	 */
	public void testGetExtension()
	{
		assertEquals("cs", FileUtils.getExtension("test.aspx.cs")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("cs", FileUtils.getExtension("test.cs")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(null, FileUtils.getExtension(null));
		assertEquals("", FileUtils.getExtension("")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Test method for 'com.aptana.ide.core.FileUtils.ensureExtension(String)'
	 */
	public void testEnsureExtension()
	{
		assertEquals(".aspx.cs", FileUtils.ensureExtension("aspx.cs")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(".cs", FileUtils.ensureExtension(".cs")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(null, FileUtils.ensureExtension(null));
		assertEquals("", FileUtils.ensureExtension("")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Test method for 'com.aptana.ide.core.FileUtils.stripExtensionPeriod(String)'
	 */
	public void testStripExtensionPeriod()
	{
		assertEquals("aspx.cs", FileUtils.stripExtensionPeriod(".aspx.cs")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("cs", FileUtils.stripExtensionPeriod("cs")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(null, FileUtils.stripExtensionPeriod(null));
		assertEquals("", FileUtils.stripExtensionPeriod("")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Test method for 'com.aptana.ide.core.FileUtils.compressPath(String, int)'
	 */
	public void testCompressPath()
	{
		assertEquals("c:/.../My Documents/test.txt", FileUtils.compressPath("c:\\Documents and Settings\\username\\My Documents\\test.txt", 20)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("c:/.../test_this_file_name_is_longer_than_20_characters.txt", FileUtils.compressPath("c:\\Documents and Settings\\username\\My Documents\\test_this_file_name_is_longer_than_20_characters.txt", 20)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("c:/.../test_this_file_name_is_longer_than_20_characters.txt", FileUtils.compressPath("c:\\test_this_file_name_is_longer_than_20_characters.txt", 20)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("c:/.../test_this_file_name_is_longer_than_20_characters.txt", FileUtils.compressPath("c:\\remove_me\\test_this_file_name_is_longer_than_20_characters.txt", 20)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("/users/.../username/", FileUtils.compressPath("/users/remove_me/username/", 20)); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testUrlToFile()
	{
		String homeDir = "file:/C:/Documents and Settings/Ingo Muschenetz/My Documents/config/";
		URL fileURL = FileUtils.uriToURL(homeDir);
		assertEquals("file:/C:/Documents%20and%20Settings/Ingo%20Muschenetz/My%20Documents/config/", fileURL.toString());
		
		if(Platform.OS_WIN32.equals(Platform.getOS()))
		{
			File file = FileUtils.urlToFile(fileURL);
			assertEquals("C:\\Documents and Settings\\Ingo Muschenetz\\My Documents\\config", file.toString());
		}
	}

}
