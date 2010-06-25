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
package com.aptana.ide.editors.junit.js;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.editor.js.JSEditor;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.IScope;
import com.aptana.ide.editor.js.runtime.JSFunction;
import com.aptana.ide.editor.js.runtime.JSString;
import com.aptana.ide.editor.scriptdoc.parsing.FunctionDocumentation;
import com.aptana.ide.editors.junit.GenericProject;
import com.aptana.ide.editors.junit.ProjectTestUtils;
import com.aptana.ide.editors.junit.TestProject;
import com.aptana.ide.editors.junit.TestUtils;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.ILanguageEnvironment;
import com.aptana.ide.metadata.IDocumentation;

/**
 * @author Robin
 */
public class JSTodoTests extends TestCase
{

	private GenericProject project;
	private ILanguageEnvironment env;
	private Environment jsEnv;
	private IScope global;
	// private IObject undef;
	private SourceViewer viewer;
	private IDocument document;
	private IContentAssistProcessor caProcessor;
	private ICompletionProposal[] props;

	private String[] fileList = new String[] { "globalVsLocal.js", "reinitObjects.js", "conditionals.js", "arrays.js", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"multipleTypes.js", "multipleDocs.js", "expressions.js", "dynamicCreation.js", "selfInvokingFns.js", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			"doubleNamedFns.js", "conditionalUsage.js", "specializedMethods.js", "dataHiding.js", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"multipleAssignments.js", "builtIns.js" }; //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * ProfileManagerTest
	 */
	public JSTodoTests()
	{
		project = GenericProject.getInstance();

		env = JSLanguageEnvironment.getInstance();
		jsEnv = (Environment) env.getRuntimeEnvironment();
		global = jsEnv.getGlobal();
		// undef = JSUndefined.getSingletonInstance();
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
	 * testAllCases
	 */
	public void testAllCases()
	{
		for (int i = 0; i < fileList.length; i++)
		{
			String name = fileList[i];

			Path path = ProjectTestUtils.findFileInPlugin(TestProject.PLUGIN_ID, "jsTodo/" + name); //$NON-NLS-1$ //$NON-NLS-2$
			IFile curFile = ProjectTestUtils.addFileToProject(path, this.project.getProject());
			ProjectTestUtils.openInEditor(curFile, ProjectTestUtils.JS_EDITOR_ID);
			TestUtils.waitForParse(2000);

			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart part = page.getActiveEditor();
			if (part instanceof JSEditor)
			{
				JSEditor jsSourceEditor = (JSEditor) part;
				viewer = (SourceViewer) jsSourceEditor.getViewer();
				caProcessor = jsSourceEditor.getBaseContributor()
						.getContentAssistProcessor(viewer, JSMimeType.MimeType);
				document = viewer.getDocument();
			}
			TestUtils.waitForParse(2500);

			runTest(i);

			ProjectTestUtils.closeEditor(part);

		}
		// ***********************************************************************
		// open testObj.html from project
		// globalVsLocal();

	}

	private ICompletionProposal getProposal(String name)
	{
		ICompletionProposal result = null;
		if (props == null)
		{
			return null;
		}
		for (int i = 0; i < props.length; i++)
		{
			ICompletionProposal prop = props[i];
			if (prop.getDisplayString().equals(name))
			{
				result = prop;
				break;
			}
		}
		return result;
	}

	private void runTest(int index)
	{
		switch (index)
		{
			case 0:
				globalVsLocal();
				break;

			case 1:
				reinitObjects();
				break;

			case 2:
				conditionals();
				break;

			case 3:
				arrays();
				break;

			case 4:
				multipleTypes();
				break;

			case 5:
				multipleDocs();
				break;

			case 6:
				expressions();
				break;

			case 7:
				dynamicCreation();
				break;

			case 8:
				selfInvokingFns();
				break;

			case 9:
				doubleNamedFns();
				break;

			case 10:
				conditionalUsage();
				break;

			case 11:
				specializedMethods();
				break;

			case 12:
				dataHiding();
				break;

			case 13:
				multipleAssignments();
				break;

			case 14:
				builtIns();
				break;

			default:
				break;
		}

	}

	private void globalVsLocal()
	{
		// /** global x */
		// var x = 5;
		// /** global y */
		// var y = 1;
		// (58)
		// /**
		// *
		// * @param {Object} y arg y
		// */
		// function foo(y)
		// {
		// y = 2; // arg
		// x = 6; // global
		// (161)
		// var x = "test";
		// x = "test2"; // local
		// (209)
		// }

		int probeLocation = getTestLocation(0);
		invokeCodeAssistAt(probeLocation);

		int fileIndex = FileContextManager.CURRENT_FILE_INDEX;

		IObject xobj = global.getPropertyValue("x", fileIndex, probeLocation); //$NON-NLS-1$
		String xdoc = xobj.getDocumentation().getDescription().trim();
		assertEquals(xdoc, "global x"); //$NON-NLS-1$

		IObject yobj = global.getPropertyValue("y", fileIndex, probeLocation); //$NON-NLS-1$
		String ydoc = yobj.getDocumentation().getDescription().trim();
		assertEquals(ydoc, "global y"); //$NON-NLS-1$

		probeLocation = getTestLocation(1);
		invokeCodeAssistAt(probeLocation);

		JSFunction fn = (JSFunction) global.getPropertyValue("foo", fileIndex, probeLocation); //$NON-NLS-1$
		IScope scope = fn.getBodyScope();

		// IObject yarg = scope.getLocalProperty("y").getValue(fileIndex, probeLocation);
		// IObject yarg = scope.getPropertyValue("y", fileIndex, probeLocation);
		// fails: this doc shouldn't be null
		// String yargdoc = yarg.getDocumentation().getDescription().trim();
		String yargdoc = ((FunctionDocumentation) fn.getDocumentation()).getParams()[0].getDescription().trim();
		assertEquals(yargdoc, "arg y"); //$NON-NLS-1$

		// test directly on CA
		// ICompletionProposal prop = getProposal("y");
		// String displayInfo = prop.getAdditionalProposalInfo();
		// assertTrue(displayInfo.indexOf("arg y") > -1); // this fails, should be arg info

		// test directly off CA
		// prop = getProposal("x");
		// String xdisplayInfo = prop.getAdditionalProposalInfo();
		// fails - no doc available
		// assertTrue(xdisplayInfo.indexOf("global x") > -1);

		probeLocation = getTestLocation(2);
		invokeCodeAssistAt(probeLocation);

		IObject localx = scope.getPropertyValue("x", fileIndex, probeLocation); //$NON-NLS-1$
		IDocumentation localxdoc = localx.getDocumentation();
		assertNull(localxdoc);
		assertTrue(localx instanceof JSString);
		IObject type = localx.getPropertyValue("constructor", fileIndex, probeLocation); //$NON-NLS-1$
		assertEquals(type.getClassName(), "Function"); //$NON-NLS-1$

		// prop = getProposal("x");
		// xdisplayInfo = prop.getAdditionalProposalInfo();
		// this fails, it gets the global doc instead
		// assertTrue(displayInfo.indexOf("global x") == -1);

	}

	private void reinitObjects()
	{
		// var obj = {};
		// obj.x = 5;
		// obj = {};
		// obj.y = 6;
		// // obj only contains a y property, x is undefined

		int probeLocation = getTestLocation(0);
		invokeCodeAssistAt(probeLocation);
		// ICompletionProposal propx = getProposal("x");
		ICompletionProposal propy = getProposal("y"); //$NON-NLS-1$
		// assertNull(propx); This Fails! should be null
		assertNotNull(propy);
	}

	private void conditionals()
	{
		// // file 1
		// /** doc for ns */
		// var ns = {};
		// ns.x = 5;
		//		 
		// // file 2
		// if(ns == null)
		// {
		// var ns = {};
		// }
		// ns.y = 6;
		//		 
		// // in this case ns should have both x and y
		// // problem is, compare with the last case, and now...
		//		
		// if(true)
		// {
		// var ns = {};
		// }
		// ns.z = 7;
		// now ns should only have a 'z' property

		int probeLocation = getTestLocation(0);
		invokeCodeAssistAt(probeLocation);
		ICompletionProposal propx = getProposal("x"); //$NON-NLS-1$
		ICompletionProposal propy = getProposal("y"); //$NON-NLS-1$
		ICompletionProposal propz = getProposal("y"); //$NON-NLS-1$
		assertNotNull(propx);
		// assertNull(propy); //This Fails! should be null
		// assertNull(propz); //This Fails! should be null

		probeLocation = getTestLocation(1);
		invokeCodeAssistAt(probeLocation);
		propx = getProposal("x"); //$NON-NLS-1$
		propy = getProposal("y"); //$NON-NLS-1$
		propz = getProposal("y"); //$NON-NLS-1$
		assertNotNull(propx);
		assertNotNull(propy);
		// assertNull(propz); //This Fails! should be null

		probeLocation = getTestLocation(2);
		invokeCodeAssistAt(probeLocation);
		propx = getProposal("x"); //$NON-NLS-1$
		propy = getProposal("y"); //$NON-NLS-1$
		propz = getProposal("y"); //$NON-NLS-1$
		// assertNull(propx); //This Fails! should be null
		// assertNull(propy); //This Fails! should be null
		assertNotNull(propz);

	}

	private void arrays()
	{
		// var ar = [1,"two",obj];
		// ar[2].
		// // how can we even sdoc this?
		// // also these types can be changed, created dynamically etc.

		int probeLocation = getTestLocation(0);
		invokeCodeAssistAt(probeLocation);
		// ICompletionProposal ar2 = getProposal("charAt"); // should be string
		// assertNotNull(ar2); // fails! can't get string.
	}

	private void multipleTypes()
	{
		// var x = 5;
		// x = "string";
		// x.
		// // we support multiple types in the sdoc format
		// // we can derive there are multiple types from the info we have
		// // we need to actually add this the the code assist dialog
		int probeLocation = getTestLocation(0);
		invokeCodeAssistAt(probeLocation);
		// ICompletionProposal isNum = getProposal("toExponential"); // should be string

		// assertNotNull(isNum); // fails! can't get string.
	}

	private void multipleDocs()
	{
		// /** doc1 */
		// var x = true;
		// x ;
		// /** doc2 */
		// x = false;
		// x ;
		// /** doc3 */
		// var y = x;
		// y ;
		// function foo(arg)
		// {
		// /** doc4 */
		// y = arg;
		// y ;
		// }
		// // these docs all point to the same object
		// // there are various ways docs can be assigned multiple times like this
		// // this is esp tricky with namespaces, where conditional reassignments are common
		//		
		//		
		//		
		//		
		//		
		// /** doc1 */
		// var x = 5;
		// /** doc2 */
		// var y = 6;
		// var z = (test) ? x : y;
		// z ;

		int probeLocation = getTestLocation(0);
		invokeCodeAssistAt(probeLocation);
		ICompletionProposal prop = getProposal("x"); // should be doc1 //$NON-NLS-1$
		assertTrue(prop.getAdditionalProposalInfo().indexOf("doc1") > -1); //$NON-NLS-1$

		probeLocation = getTestLocation(1);
		invokeCodeAssistAt(probeLocation);
		prop = getProposal("x"); // should be doc2, and doc1 //$NON-NLS-1$
		assertTrue(prop.getAdditionalProposalInfo().indexOf("doc2") > -1); //$NON-NLS-1$
		// assertTrue(prop.getAdditionalProposalInfo().indexOf("doc1") > -1); // fails

		probeLocation = getTestLocation(2);
		invokeCodeAssistAt(probeLocation);
		prop = getProposal("y"); // should be doc3,2,1, but not doc4 //$NON-NLS-1$
		// we actually end up with doc4
		// assertTrue(prop.getAdditionalProposalInfo().indexOf("doc4") == -1);
		// assertTrue(prop.getAdditionalProposalInfo().indexOf("doc1") > -1);
		// assertTrue(prop.getAdditionalProposalInfo().indexOf("doc2") > -1);
		// assertTrue(prop.getAdditionalProposalInfo().indexOf("doc3") > -1);

		// assertTrue(prop.getAdditionalProposalInfo().indexOf("doc1") > -1); // fails

		probeLocation = getTestLocation(3);
		invokeCodeAssistAt(probeLocation);
		prop = getProposal("y"); // should be doc4,3,2,1 //$NON-NLS-1$
		// assertTrue(prop.getAdditionalProposalInfo().indexOf("doc1") > -1);
		// assertTrue(prop.getAdditionalProposalInfo().indexOf("doc2") > -1);
		// assertTrue(prop.getAdditionalProposalInfo().indexOf("doc3") > -1);
		assertTrue(prop.getAdditionalProposalInfo().indexOf("doc4") > -1); //$NON-NLS-1$

		probeLocation = getTestLocation(4);
		invokeCodeAssistAt(probeLocation);
		prop = getProposal("z"); // should be doc2, and doc1 //$NON-NLS-1$
		// instead it is null
		// assertTrue(prop.getAdditionalProposalInfo().indexOf("doc1") > -1);
		// assertTrue(prop.getAdditionalProposalInfo().indexOf("doc2") > -1);
	}

	private void expressions()
	{
		// // we currently don't support expressions at all
		// /** doc */
		// var x = 5 + 6;
		// var y = x + x;

		// neither x nor y will show up in the environment"
		int probeLocation = getTestLocation(0);
		invokeCodeAssistAt(probeLocation);

		// ICompletionProposal propx = getProposal("x");
		// ICompletionProposal propy = getProposal("y");

		// assertNotNull(propx); // fails! x is null
		// assertNotNull(propy); // fails! y is null
	}

	private void dynamicCreation()
	{
		// var obj = {};
		// obj.foo = function (){};
		// obj.foo.prototype.x = 5;
		//		
		// var a = new obj["foo"](); // key line
		// a.
		//		
		//		
		// var obj = {};
		// var index = 1;
		// obj.x0 = 5;
		// obj.x1 = 5;
		// obj.x2 = 5;
		// var z = obj["x" + index];
		// z.

		int probeLocation = getTestLocation(0);
		invokeCodeAssistAt(probeLocation);
		// ICompletionProposal xprop = getProposal("toExponential"); // should be number

		probeLocation = getTestLocation(1);
		invokeCodeAssistAt(probeLocation);
		// ICompletionProposal zprop = getProposal("toExponential"); // should be number

		// note: testing for number doesn't work here - test for
		// assertNotNull(xprop); // fails! x not number
		// assertNotNull(zprop); // fails! z not number
	}

	private void selfInvokingFns()
	{
		// var f = function foo(arg1, {obj:4})
		// {
		// return 5;
		// }(); // note invocation at end
		//		 
		// // f should be 5

		int probeLocation = getTestLocation(0);
		invokeCodeAssistAt(probeLocation);
		// ICompletionProposal isNum = getProposal("toExponential"); // should be number

		// assertNotNull(isNum); // fails! f not a number
	}

	private void doubleNamedFns()
	{
		// /** doc */
		// var x = function foo()
		// {
		// }
		// // need to be sure both cases are handled, and both can access the doc (they are now)

		int probeLocation = getTestLocation(0);
		invokeCodeAssistAt(probeLocation);
		ICompletionProposal xprop = getProposal("x"); //$NON-NLS-1$
		// ICompletionProposal fooprop = getProposal("foo");

		assertNotNull(xprop);
		// hmm, this shouldn't be null - it shows up in CA in the ide...
		// assertNotNull(fooprop);
	}

	private void conditionalUsage()
	{
		int probeLocation = 0;
		invokeCodeAssistAt(probeLocation);
		// ICompletionProposal isNum = getProposal("toExponential");
	}

	private void specializedMethods()
	{
		int probeLocation = 0;
		invokeCodeAssistAt(probeLocation);
		// ICompletionProposal isNum = getProposal("toExponential");
	}

	private void dataHiding()
	{
		int probeLocation = 0;
		invokeCodeAssistAt(probeLocation);
		// ICompletionProposal isNum = getProposal("toExponential");
	}

	private void multipleAssignments()
	{
		// /** doc */
		// var A = B = function(iterable) {}
		//		

		int probeLocation = getTestLocation(0);
		invokeCodeAssistAt(probeLocation);
		ICompletionProposal prop1 = getProposal("A"); // should be fn //$NON-NLS-1$
		// ICompletionProposal prop2 = getProposal("B"); // should be fn

		assertTrue(prop1.getAdditionalProposalInfo().indexOf("doc") > -1); //$NON-NLS-1$

		// fails, we don't get second assignment
		// assertTrue(prop2.getAdditionalProposalInfo().indexOf("doc") > -1);
	}

	private void builtIns()
	{
		int probeLocation = 0;
		invokeCodeAssistAt(probeLocation);
		// ICompletionProposal isNum = getProposal("toExponential");
	}

	private void invokeCodeAssistAt(int position)
	{
		viewer.setSelectedRange(position, 0);
		viewer.doOperation(SourceViewer.CONTENTASSIST_CONTEXT_INFORMATION);
		props = caProcessor.computeCompletionProposals(viewer, position);
	}

	/**
	 * @param i
	 * @return int
	 */
	private int getTestLocation(int i)
	{
		return document.get().indexOf("/* #" + i + " */") - 1; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
