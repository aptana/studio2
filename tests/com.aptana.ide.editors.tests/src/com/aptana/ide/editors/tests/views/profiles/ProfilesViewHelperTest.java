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
package com.aptana.ide.editors.tests.views.profiles;

import java.io.File;

import junit.framework.TestCase;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.html.parsing.HTMLParseState;
import com.aptana.ide.editor.html.parsing.HTMLParser;
import com.aptana.ide.editors.junit.TestUtils;
import com.aptana.ide.editors.views.profiles.ProfilesViewHelper;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.ParserInitializationException;

/**
 * 
 * @author Ingo Muschenetz
 *
 */
public class ProfilesViewHelperTest extends TestCase {

	public void testAddSDocFromJavaScriptSource() {

		File file = TestUtils.createFileFromString("test", ".js", ""); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		String src = "/**\n * @sdoc " + file.getName() + "\n*/"; //$NON-NLS-1$ //$NON-NLS-2$
		String[] sdocs = ProfilesViewHelper.addSDocFromJavaScriptSource(file.getParentFile(), src);
		assertEquals("One sdoc file found", 1, sdocs.length); //$NON-NLS-1$
		assertEquals(CoreUIUtils.getURI(file), sdocs[0]);
	}

	public void testAddScriptTagsFromHTMLSource() {

		String jsURL = "http://www.aptana.com/lib/swfobject.js"; //$NON-NLS-1$
		File file1 = TestUtils.createFileFromString("test1", ".js", ""); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		File file2 = TestUtils.createFileFromString("test2", ".js", ""); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		File file3 = TestUtils.createFileFromString("test2", ".js", ""); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		
		String src = "<html><head><script src=\"" + jsURL + "\"/><script src=\"" + file1.getName() + "\" /><script src=\"" + file2.getName() + "\"><script src=\"" + file3.getName() + "\"></script></head><body></body></html>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		File parentFile = file1.getParentFile();

		HTMLParser parser = null;
		HTMLParseState parseState = null;

		try {
			parser = new HTMLParser();
			parseState = new HTMLParseState();
			parseState.setEditState(src, src, 0, 0);
			parser.parse(parseState);
		} catch (ParserInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] sdocs = ProfilesViewHelper.addScriptTagsFromHTMLSource(
				parentFile, src, parseState);
		assertEquals("Three JS files found", 3, sdocs.length); //$NON-NLS-1$
		assertEquals(jsURL, sdocs[0]);		
		assertEquals(CoreUIUtils.getURI(file1), sdocs[1]);
		assertEquals(CoreUIUtils.getURI(file2), sdocs[2]);
	}
	

	public void testAddSDocFromJavaScriptSourceFile() {
		File file1 = TestUtils.createFileFromString("test", ".js", ""); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		String src = "/**\n * @sdoc " + file1.getName() + "\n*/"; //$NON-NLS-1$ //$NON-NLS-2$
		File file = TestUtils.createFileFromString("base", ".js", src); //$NON-NLS-1$//$NON-NLS-2$
		String uri = CoreUIUtils.getURI(file);
		String[] sdocs = ProfilesViewHelper
				.addSDocFromJavaScriptSource(FileUtils.uriToURL(uri));
		assertEquals("One sdoc file found", 1, sdocs.length); //$NON-NLS-1$
		assertEquals(CoreUIUtils.getURI(file1), sdocs[0]);
	}

	public void testAddScriptFromJavaScriptSourceFile() {

		File file1 = TestUtils.createFileFromString("test", ".js", ""); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		File mochiBase = TestUtils.createFileFromString("MochiBase", ".js", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		String src = "/**\n * @sdoc " + file1.getName() + "\n*/\nMochiKit.MochiKit.SUBMODULES = [\r\n\"" + FileUtils.stripExtension(mochiBase.getName()) + "\"];"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		File file = TestUtils.createFileFromString("base", ".js", src); //$NON-NLS-1$ //$NON-NLS-2$

		String uri = CoreUIUtils.getURI(file);

		String[] sdocs;
		sdocs = ProfilesViewHelper.addScriptFromJavaScriptSource(FileUtils.uriToURL(uri));
		
		assertEquals("Two sdoc files found", 2, sdocs.length); //$NON-NLS-1$
		assertEquals(
				CoreUIUtils.getURI(file1), sdocs[0]);
		assertEquals(
				CoreUIUtils.getURI(mochiBase), sdocs[1]);

		String[] sdocs2 = ProfilesViewHelper.addScriptFromJavaScriptSource(
				file.getParentFile(), src);

		assertEquals("Two sdoc files found", 2, sdocs2.length); //$NON-NLS-1$
		assertEquals(
				CoreUIUtils.getURI(file1), sdocs2[0]);
		assertEquals(
				CoreUIUtils.getURI(mochiBase), sdocs2[1]);

	}

	public void testFindScriptDocFile() {

		File file = TestUtils.createFileFromString("base", ".js", "a"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		// No sdoc file, returns false
		assertEquals(null, ProfilesViewHelper.findScriptDocFile(file
				.getAbsolutePath()));

		// create sdoc file, now finds path
		String filePath = file.getAbsolutePath();
		filePath = StringUtils.replace(filePath, ".js", ".sdoc"); //$NON-NLS-1$ //$NON-NLS-2$
		File sdocFile = TestUtils.createFileFromString(filePath, "b"); //$NON-NLS-1$
		assertEquals(CoreUIUtils.getURI(sdocFile.getAbsolutePath()), CoreUIUtils.getURI(ProfilesViewHelper
				.findScriptDocFile(file.getAbsolutePath())));
	}

	public void testStripQuerystring() {
		assertEquals("test.js", ProfilesViewHelper //$NON-NLS-1$
				.stripQuerystring("test.js?query=q")); //$NON-NLS-1$
	}

}
