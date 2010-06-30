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

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.editor.html.HTMLEditor;
import com.aptana.ide.editor.html.HTMLSourceEditor;
import com.aptana.ide.editors.junit.TestUtils;

/**
 * @author Robin
 *
 */
public class JSEdgeCaseTests extends TestCase 
{
	//private JSEdgeCasesProject project;
	//private ILanguageEnvironment env;
	//private Environment jsEnv;
	//private IScope global;
	//private IObject undef;
	
	/**
	 * ProfileManagerTest
	 */
	public JSEdgeCaseTests()
	{
		//project = JSEdgeCasesProject.getInstance();
		//env = JSLanguageEnvironment.getInstance();
		//jsEnv = (Environment)env.getRuntimeEnvironment();		
		//global = jsEnv.getGlobal();
		//undef = JSUndefined.getSingletonInstance();
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
	 * testEdgeCases
	 */
	public void testEdgeCases()
	{	 
		// ***********************************************************************
		// open test0.html from project
		//IEditorPart test0Editor = ProjectTestUtils.openInEditor(project.edgeCases_js_file);
		TestUtils.waitForParse(4000);	
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart part = page.getActiveEditor();
		if(part instanceof HTMLEditor)
		{
			HTMLEditor htmlSourceEditor = (HTMLEditor)part;
			HTMLSourceEditor se = htmlSourceEditor.getSourceEditor();
			SourceViewer viewer = (SourceViewer)se.getViewer();
			viewer.doOperation(SourceViewer.CONTENTASSIST_CONTEXT_INFORMATION);
		}
		TestUtils.waitForParse(10000);
	}
}
