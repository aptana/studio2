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
package com.aptana.ide.core.ui.tests;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;

/**
 * Tests com.aptana.ide.ui.CoreUIUtils
 * 
 * @author Ingo Muschenetz
 */
public class CoreUIUtilsTest extends TestCase
{
	
	/**
	 * Test method for 'com.aptana.ide.core.ui.CoreUIUtils.appendProtocol(File)'
	 */
	public void testAppendProtocol()
	{
		assertEquals("file://C:/Program Files/Aptana", CoreUIUtils.appendProtocol("C:/Program Files/Aptana")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("http://www.google.com", CoreUIUtils.appendProtocol("http://www.google.com")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Test method for 'com.aptana.ide.core.ui.CoreUIUtils.getURI(File)'
	 */
	public void testGetURIFile()
	{

		// check UNC paths
		if (CoreUIUtils.onWindows)
		{
			String result = "file://C:/Documents%20and%20Settings/All%20Users/NTUSER%5B1%5D$#&%2B@!()-%7B%7D'%60_~.DAT"; //$NON-NLS-1$

			File f1 = new File("C:\\Documents and Settings\\All Users\\NTUSER[1]$#&+@!()-{}'`_~.DAT"); //$NON-NLS-1$
			File f2 = new File("C:/Documents and Settings/All Users/NTUSER[1]$#&+@!()-{}'`_~.DAT"); //$NON-NLS-1$
			File f3 = new File("C:\\DOCUME~1\\ALLUSE~1\\NTUSER[1]$#&+@!()-{}'`_~.DAT"); //$NON-NLS-1$
			File f4 = new File("C:/Documents and Settings\\Default User\\..\\All Users\\NTUSER[1]$#&+@!()-{}'`_~.DAT"); //$NON-NLS-1$

			assertEquals(result, CoreUIUtils.getURI(f1));
			assertEquals(result, CoreUIUtils.getURI(f2));
			assertEquals(result, CoreUIUtils.getURI(f3));
			assertEquals(result, CoreUIUtils.getURI(f4));

			assertEquals(f1.getPath(), CoreUIUtils.getPathFromURI(result).replace('/', '\\'));

			// assertEquals("http://C:/temp/My%20Documents/test.txt", uri4);
			// assertEquals("file://Bob/temp/test.txt", CoreUIUtils.getURI(new
			// File("//Bob/temp/test.txt")));
			// assertEquals("file://Bob/temp/test.txt", CoreUIUtils.getURI(new
			// File("\\\\Bob\\temp\\test.txt")));
			// assertEquals("file://C:/Documents%20and%20Settings/All%20Users/NTUSER.DAT",
			// CoreUIUtils.getURI(new File("C:\\DOCUME~1\\ALLUSE~1\\NTUSER.DAT")));

			// Multiple passes
			assertEquals(result, CoreUIUtils.getURI(CoreUIUtils.getURI(CoreUIUtils.getURI(CoreUIUtils.getURI(f1)))));
		}
	}

	/**
	 * Test method for 'com.aptana.ide.core.ui.CoreUIUtils.getURI(IPath)'
	 */
	public void testGetURIIPath()
	{

		if (CoreUIUtils.onWindows)
		{
			String result = "file://C:/Documents%20and%20Settings/All%20Users/NTUSER%5B1%5D$#&%2B@!()-%7B%7D'%60_~.DAT"; //$NON-NLS-1$

			Path f1 = new Path("C:\\Documents and Settings\\All Users\\NTUSER[1]$#&+@!()-{}'`_~.DAT"); //$NON-NLS-1$
			Path f2 = new Path("C:/Documents and Settings/All Users/NTUSER[1]$#&+@!()-{}'`_~.DAT"); //$NON-NLS-1$
			Path f3 = new Path("C:\\DOCUME~1\\ALLUSE~1\\NTUSER[1]$#&+@!()-{}'`_~.DAT"); //$NON-NLS-1$
			Path f4 = new Path("C:/Documents and Settings\\Default User\\..\\All Users\\NTUSER[1]$#&+@!()-{}'`_~.DAT"); //$NON-NLS-1$

			assertEquals(result, CoreUIUtils.getURI(f1));
			assertEquals(result, CoreUIUtils.getURI(f2));
			assertEquals(result, CoreUIUtils.getURI(f3));
			assertEquals(result, CoreUIUtils.getURI(f4));

			assertEquals(f1.toPortableString(), CoreUIUtils.getPathFromURI(result));
		}
	}

	/**
	 * Test method for 'com.aptana.ide.core.ui.CoreUIUtils.getURI(IEditorInput)'
	 */
	public void testGetURIIEditorInput()
	{
		if (CoreUIUtils.onWindows)
		{
			String test = "file://C:/temp/test.txt"; //$NON-NLS-1$

			NonExistingFileEditorInput nef1 = (NonExistingFileEditorInput) CoreUIUtils
					.createNonExistingFileEditorInput(new File("C:\\temp\\test.txt"), ""); //$NON-NLS-1$ //$NON-NLS-2$
			assertEquals(test, CoreUIUtils.getURI(nef1));

			NonExistingFileEditorInput nef2 = (NonExistingFileEditorInput) CoreUIUtils
					.createNonExistingFileEditorInput(new File("C:/temp/test.txt"), ""); //$NON-NLS-1$ //$NON-NLS-2$
			assertEquals(test, CoreUIUtils.getURI(nef2));

			/*
			 * On windows, this would return file://c:/Users/bob/test.txt String test2 = "file:///Users/bob/test.txt";
			 * NonExistingFileEditorInput nef3 = new NonExistingFileEditorInput(new File("/Users/bob/test.txt"), "");
			 * assertEquals(test2, CoreUIUtils.getURI(nef3));
			 */

			/*
			 * Won't work, as you can't pass a file:// to a file constructor NonExistingFileEditorInput nef3 = new
			 * NonExistingFileEditorInput(new File(test), ""); assertEquals(test, CoreUIUtils.getURI(nef3));
			 */

//			BaseFileEditorInput jfei1 = (BaseFileEditorInput) CoreUIUtils.createJavaFileEditorInput(new File(
//					"C:\\temp\\test.txt")); //$NON-NLS-1$
//			assertEquals(test, CoreUIUtils.getURI(jfei1));
//
//			BaseFileEditorInput jfei2 = (BaseFileEditorInput) CoreUIUtils.createJavaFileEditorInput(new File(
//					"C:/temp/test.txt")); //$NON-NLS-1$
//			assertEquals(test, CoreUIUtils.getURI(jfei2));

			/*
			 * Workspace w = new Workspace(); IProject p = w.getRoot().getProject("test"); IFile file =
			 * p.getFile(org.eclipse.core.runtime.Path.fromOSString("c:\\temp\\test.txt")); FileEditorInput fei = new
			 * FileEditorInput(file);
			 */
		}
	}

	/**
	 * Test method for 'com.aptana.ide.core.ui.CoreUIUtils.trimURLSegments(URL, int)'
	 */
	public void testTrimURLSegments()
	{
		try
		{
			URL newURL = CoreUIUtils
					.trimURLSegments(
							new URL(
									"http://build.aptana.com/builds/aptana_ide/build.12805/update/features/com.aptana.ide.feature.rcp_0.2.9.12805.jar"), 2); //$NON-NLS-1$
			assertEquals("http://build.aptana.com/builds/aptana_ide/build.12805/update/", newURL.toExternalForm()); //$NON-NLS-1$

			newURL = CoreUIUtils.trimURLSegments(new URL("http://www.aptana.com/"), 5); //$NON-NLS-1$
			assertEquals(null, newURL);

			newURL = CoreUIUtils.trimURLSegments(new URL("http://www.aptana.com/"), 4); //$NON-NLS-1$
			assertEquals(null, newURL);

		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.EMPTY, e);
		}
	}

	/**
	 * Test method for 'replaceBundleNameWithId(URL)'
	 */
	public void testReplaceBundleNameWithId()
	{
		try
		{
			String google = "http://www.google.com";
			URI external = new URI(google);
			assertEquals(google, CoreUIUtils.replaceBundleNameWithId(external).toString());

			// Bundle b = CoreUIPlugin.getDefault().getBundle();
			// assertNotNull(b);

			// external = new URI("bundleentry://com.aptana.ide.core.ui/");
			// assertEquals("bundleentry://" + b.getBundleId() + "/", CoreUIUtils
			// .replaceBundleNameWithId(external).toString());
		}
		catch (URISyntaxException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.EMPTY, e);
		}
	}

}
