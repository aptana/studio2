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

package com.aptana.ide.editor.junit.profiles;

import junit.framework.TestCase;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.editor.html.HTMLEditor;
import com.aptana.ide.editor.html.HTMLSourceEditor;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.IScope;
import com.aptana.ide.editor.js.runtime.JSArray;
import com.aptana.ide.editor.js.runtime.JSNumber;
import com.aptana.ide.editor.js.runtime.JSObject;
import com.aptana.ide.editor.js.runtime.JSRegExp;
import com.aptana.ide.editor.js.runtime.JSString;
import com.aptana.ide.editor.js.runtime.JSUndefined;
import com.aptana.ide.editor.scriptdoc.parsing.FunctionDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.TypedDescription;
import com.aptana.ide.editors.junit.ProjectTestUtils;
import com.aptana.ide.editors.junit.TestProject;
import com.aptana.ide.editors.junit.TestUtils;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.ILanguageEnvironment;
import com.aptana.ide.metadata.IDocumentation;

/**
 * ProfileManagerTest
 */
public class ProfileManagerTest extends TestCase
{
	private TestProject project;
	private ILanguageEnvironment env;
	private Environment jsEnv;
	private IScope global;
	private IObject undef;

	/**
	 * ProfileManagerTest
	 */
	public ProfileManagerTest()
	{
		project = TestProject.getInstance();

		env = JSLanguageEnvironment.getInstance();
		jsEnv = (Environment) env.getRuntimeEnvironment();
		global = jsEnv.getGlobal();
		undef = JSUndefined.getSingletonInstance();
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * testEnvironment
	 */
	public void testEnvironment()
	{
		// ***********************************************************************
		// open test0.html from project
		IEditorPart test0Editor = ProjectTestUtils.openInEditor(project.test0_html_file,
				ProjectTestUtils.HTML_EDITOR_ID);
		TestUtils.waitForParse(4000);

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart part = page.getActiveEditor();
		if (part instanceof HTMLEditor)
		{
			HTMLEditor htmlSourceEditor = (HTMLEditor) part;
			HTMLSourceEditor se = htmlSourceEditor.getSourceEditor();
			SourceViewer viewer = (SourceViewer) se.getViewer();
			viewer.doOperation(SourceViewer.CONTENTASSIST_CONTEXT_INFORMATION);
		}
		TestUtils.waitForParse(10000);
		// /**
		// * Description for test0.
		// * @param {Number} arg0 Description for arg0.
		// * @param {RegExp} arg1 Description for arg1.
		// * @return {Number};
		// */
		// function test0(arg0, arg1)
		// {
		// this.arg0 = arg0;
		// this.arg1 = arg1;
		// return 5;
		// }
		//
		// test0.prototype.numProp = 5;
		// test0.prototype.stringProp = "hello";
		// test0.prototype.arrayProp = [3,4,5];
		// test0.prototype.pointProp = {x:1, y:2};
		//
		// var test0Inst = new test0();
		//
		// /**
		// * @id idFunction
		// */
		// function idFunction()
		// {
		// return [];
		// }

		IObject test0 = global.getPropertyValue("test0", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertNotNull(test0);
		assertNotSame(test0, undef);
		IDocumentation test0Doc = test0.getDocumentation();
		assertEquals(test0Doc.getDescription().trim(), "Description for test0."); //$NON-NLS-1$

		IObject test0Inst = global.getPropertyValue(
				"test0Inst", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertNotNull(test0Inst);
		assertNotSame(test0Inst, undef);

		IObject arg0 = test0Inst.getPropertyValue("arg0", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertTrue(arg0 instanceof JSNumber);
		IObject arg1 = test0Inst.getPropertyValue("arg1", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertTrue(arg1 instanceof JSRegExp);

		IObject numProp = test0Inst.getPropertyValue(
				"numProp", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertTrue(numProp instanceof JSNumber);
		IObject stringProp = test0Inst.getPropertyValue(
				"stringProp", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertTrue(stringProp instanceof JSString);
		IObject pointProp = test0Inst.getPropertyValue(
				"pointProp", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertTrue(pointProp instanceof JSObject);
		IObject pointProp_x = pointProp.getPropertyValue("x", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertTrue(pointProp_x instanceof JSNumber);
		IObject pointProp_y = pointProp.getPropertyValue("y", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertTrue(pointProp_y instanceof JSNumber);

		IObject idFunction = global.getPropertyValue(
				"idFunction", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertNotNull(idFunction);
		assertNotSame(idFunction, undef);
		IDocumentation idFunctionDoc = idFunction.getDocumentation();
		assertEquals(idFunctionDoc.getDescription().trim(), "Description for idFunction."); //$NON-NLS-1$
		IObject idFunctionCall = global.getPropertyValue(
				"idFunctionCall", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertTrue(idFunctionCall instanceof JSArray);

		// failing!
		// IObject arrayProp = test0Inst.getPropertyValue("arrayProp ", FileContextManager.CURRENT_FILE_INDEX,
		// Integer.MAX_VALUE);
		// assertTrue(arrayProp instanceof JSArray);

		// ***********************************************************************
		// open test1.html from project
		IEditorPart test1Editor = ProjectTestUtils.openInEditor(project.test1_html_file,
				ProjectTestUtils.HTML_EDITOR_ID);
		TestUtils.waitForParse(10000);

		// /**
		// * @namespace Aptana.Tests
		// */
		//
		// /**
		// * @id test1
		// */
		// function test1(arg0)
		// {
		// return "result";
		// }

		test0 = global.getPropertyValue("test0", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertEquals(test0, undef);
		IObject test1 = global.getPropertyValue("test1", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertNotNull(test1);
		assertNotSame(test1, undef);
		FunctionDocumentation test1Doc = (FunctionDocumentation) test1.getDocumentation();
		assertEquals(test1Doc.getDescription().trim(), "Unbound test1 description."); //$NON-NLS-1$
		TypedDescription test1_arg0 = test1Doc.getParams()[0];
		assertEquals(test1_arg0.getDescription().trim(), "Description for arg0."); //$NON-NLS-1$
		assertEquals(test1_arg0.getTypes()[0], "String"); //$NON-NLS-1$

		// ***********************************************************************
		// close test1.html, this reverts back to test0.html
		ProjectTestUtils.closeEditor(test1Editor);
		TestUtils.waitForParse(2000);

		test0 = global.getPropertyValue("test0", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertNotNull(test0);
		assertNotSame(test0, undef);

		test0Inst = global.getPropertyValue("test0Inst", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertNotNull(test0Inst);
		assertNotSame(test0Inst, undef);

		test1 = global.getPropertyValue("test1", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertEquals(test1, undef);

		// ***********************************************************************
		// close test0.html - all editors closed now
		ProjectTestUtils.closeEditor(test0Editor);
		TestUtils.waitForParse(2000);

		test0 = global.getPropertyValue("test0", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE); //$NON-NLS-1$
		assertEquals(test0, undef);

	}

	/**
	 * testDelay
	 */
	public void testDelay()
	{
		// TestUtils.waitForParse(10000); // just delay 60 seconds so we can use UI
	}

	// /**
	// * testRefreshEnvironment
	// *
	// */
	// public void testRefreshEnvironment() {
	//		
	//
	// ProfileManager pm = TestUtils.createProfileManager();
	//		
	// Profile p = ProfileManagerTest.createProfile0();
	// pm.addProfile(p);
	// pm.setCurrentProfile(p);
	//		
	// TestUtils.waitForParse();
	//		
	// File temp = TestUtils.createFileFromString("test", ".js", StringUtils.EMPTY);
	// EditorHelper.openInEditor(temp);
	// FileService fileService = FileContextManager.get(temp);
	// IFileLanguageService fls = fileService.getLanguageService(JSMimeType.MimeType);
	// IOffsetMapper mapper = fls.getOffsetMapper();
	//		
	// IScope globalScope = ((JSOffsetMapper)mapper).getGlobal();
	// String[] props = JSContentAssistProcessor.getAllPropertyNamesInScope(globalScope, true);
	// assertTrue("foo", TestUtils.find(props, "foo"));
	// assertTrue("foo_alias", TestUtils.find(props, "foo_alias"));
	// assertTrue("bar", TestUtils.find(props, "bar"));
	//		
	// // remove bar from profile. Should no longer show up in content assist
	// ProfileURI[] paths = p.getURIs();
	// p.removeURIs(new ProfileURI[] {paths[1]});
	// pm.setCurrentProfile(p);
	//		
	// TestUtils.waitForParse();
	//		
	// IScope globalScope2 = ((JSOffsetMapper)mapper).getGlobal();
	// String[] props2 = JSContentAssistProcessor.getAllPropertyNamesInScope(globalScope2, true);
	// assertTrue("foo", TestUtils.find(props2, "foo"));
	// assertTrue("foo_alias", TestUtils.find(props2, "foo_alias"));
	// assertFalse("bar", TestUtils.find(props2, "bar"));
	// assertFalse("bar_alias", TestUtils.find(props2, "bar_alias"));
	//
	//		
	// }
	//	
	// /**
	// * testFileIndexer
	// *
	// */
	// public void testFileIndexer()
	// {
	// ILanguageEnvironment env = JSLanguageEnvironment.getInstance();
	// env.loadEnvironment();
	//		
	// Environment jsEnv = (Environment)env.getRuntimeEnvironment();
	// IScope global = jsEnv.getGlobal();
	// IObject undef = JSUndefined.getSingletonInstance();
	//		
	// ProfileManager pm = TestUtils.createProfileManager();
	//		
	// // set up two profiles
	// Profile p0 = this.createProfile0();
	// pm.addProfile(p0);
	// String[] p0Paths = p0.getURIsAsStrings();
	//
	//		
	// Profile p1 = this.createProfile1();
	// pm.addProfile(p1);
	// String[] p1Paths = p1.getURIsAsStrings();
	//		
	// // set the first to be currentProfile
	// pm.setCurrentProfile(p0); // this calls apply profiles
	//
	// TestUtils.waitForParse();
	//
	// IObject obj1 = global.getPropertyValue("x", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	//		
	// // test for env population of profile0
	// IObject foo0 = global.getPropertyValue("foo", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertNotNull(foo0);
	// assertNotSame(foo0, undef);
	// IObject bar0 = global.getPropertyValue("bar", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertNotNull(bar0);
	// assertNotSame(bar0, undef);
	//		
	// // find the file indexes of the two files in the current profile
	// IFileContext context00a = FileContextManager.get(p0Paths[0]);
	// int fi00a = context00a.getParseState().getFileIndex();
	// IFileContext context01a = FileContextManager.get(p0Paths[1]);
	// int fi01a = context01a.getParseState().getFileIndex();
	//		
	// // switch to second profile
	// pm.setCurrentProfile(p1); // this calls apply profiles
	//
	// TestUtils.waitForParse();
	//
	// // test for env population of profile1
	// IObject foo1 = global.getPropertyValue("foo1", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertNotNull(foo1);
	// assertNotSame(foo1, undef);
	// IObject bar1 = global.getPropertyValue("bar1", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertNotNull(bar1);
	// assertNotSame(bar1, undef);
	// // and old profile0 props should be gone
	// foo0 = global.getPropertyValue("foo", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertEquals(foo0, undef);
	// bar0 = global.getPropertyValue("bar", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertEquals(bar0, undef);
	//		
	// // get second profiles fileIndexes
	// IFileContext context10a = FileContextManager.get(p1Paths[0]);
	// int fi10a = context10a.getParseState().getFileIndex();
	// IFileContext context11a = FileContextManager.get(p1Paths[1]);
	// int fi11a = context11a.getParseState().getFileIndex();
	//
	// // now switch back, indexes should be the same
	// pm.setCurrentProfile(p0); // this calls apply profiles
	//
	// TestUtils.waitForParse();
	//		
	// // test for env population of profile0 again
	// foo0 = global.getPropertyValue("foo", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertNotNull(foo0);
	// assertNotSame(foo0, undef);
	// bar0 = global.getPropertyValue("bar", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertNotNull(bar0);
	// assertNotSame(bar0, undef);
	// // and old profile1 props should be gone
	// foo1 = global.getPropertyValue("foo1", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertEquals(foo1, undef);
	// bar1 = global.getPropertyValue("bar1", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertEquals(bar1, undef);
	//		
	// // get first profiles indexes
	// IFileContext context00b = FileContextManager.get(p0Paths[0]);
	// int fi00b = context00b.getParseState().getFileIndex();
	// IFileContext context01b = FileContextManager.get(p0Paths[1]);
	// int fi01b = context01b.getParseState().getFileIndex();
	//
	// // file indexes do change here, as we dispose the contents of profile files when switching profiles
	// // this is to save memory, and avoid the potential index issues conflicts when a file is in two profiles
	// assertTrue(fi00b > fi00a);
	// assertTrue(fi01b > fi01a);
	// // renumber index to reflect this change
	// fi00a = fi00b;
	// fi01a = fi01b;
	//
	// // remove bar from profile. This shouldn't trigger a renumbering
	// p0.removeURIs(new ProfileURI[] {p0.getURIs()[1]});
	//
	// TestUtils.waitForParse();
	//		
	// // test for profile0 env for removal of bar
	// foo0 = global.getPropertyValue("foo", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertNotNull(foo0);
	// assertNotSame(foo0, undef);
	// bar0 = global.getPropertyValue("bar", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertEquals(bar0, undef);
	//		
	// context00b = FileContextManager.get(p0Paths[0]);
	// fi00b = context00b.getParseState().getFileIndex();
	//		
	// // insure the delete didn't trigger a renumbering
	// assertEquals(fi00b, fi00a);
	//		
	// // switch back to second profile
	// pm.setCurrentProfile(p1);
	//		
	// TestUtils.waitForParse();
	//		
	// // test for env population of profile1 again
	// foo1 = global.getPropertyValue("foo1", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertNotNull(foo1);
	// assertNotSame(foo1, undef);
	// bar1 = global.getPropertyValue("bar1", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertNotNull(bar1);
	// assertNotSame(bar1, undef);
	// // and old profile0 props should be gone
	// foo0 = global.getPropertyValue("foo", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertEquals(foo0, undef);
	// bar0 = global.getPropertyValue("bar", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertEquals(bar0, undef);
	//		
	// // get the file indexes
	// IFileContext context10b = FileContextManager.get(p1Paths[0]);
	// int fi10b = context10b.getParseState().getFileIndex();
	// IFileContext context11b = FileContextManager.get(p1Paths[1]);
	// int fi11b = context11b.getParseState().getFileIndex();
	//
	// // again switching back to a profile will trigger a renumbering
	// assertTrue(fi10b > fi10a);
	// assertTrue(fi11b > fi11a);
	// fi10a = fi10b;
	// fi11a = fi11b;
	//
	// // now cause a reordering - this should trigger a renumbering
	// p1.moveURIsUp( new ProfileURI[]{p1.getURIs()[1]} );
	//		
	// TestUtils.waitForParse();
	//		
	// // test for env population of profile1 again, order shouldn't matter here, but both should still be in
	// foo1 = global.getPropertyValue("foo1", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertNotNull(foo1);
	// assertNotSame(foo1, undef);
	// bar1 = global.getPropertyValue("bar1", FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
	// assertNotNull(bar1);
	// assertNotSame(bar1, undef);
	//		
	// context10b = FileContextManager.get(p1Paths[0]);
	// fi10b = context10b.getParseState().getFileIndex();
	// context11b = FileContextManager.get(p1Paths[1]);
	// fi11b = context11b.getParseState().getFileIndex();
	//
	// assertTrue(fi10b > fi10a);
	// assertTrue(fi11b > fi11a);
	// }

	// this runs the test on a separate thread, however it still seems to eventually use and lock the UI thread
	// using readAndDispatch (above) instead

	// public void runOnThread(final TestResult result)
	// {
	// final ProfileManagerTest self = this;
	// Thread t = new Thread(new Runnable()
	// {
	// public void run()
	// {
	// try
	// {
	// self.run(result);
	// }
	// catch (Exception e){}
	// }
	// }, "Aptana: ProfileManagerTest");
	//
	// t.setPriority(Thread.MIN_PRIORITY);
	// t.setDaemon(true);
	// t.start();
	// }
	// /**
	// * @see junit.framework.TestCase#run(junit.framework.TestResult)
	// */
	// public boolean isRunning = false;
	// public void run(TestResult result)
	// {
	// if(isRunning)
	// {
	// isRunning = false;
	// super.run(result);
	// }
	// else
	// {
	// isRunning = true;
	// runOnThread(result);
	// }
	// }

}
