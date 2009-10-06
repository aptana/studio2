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
package com.aptana.ide.editor.js.tests.outline;

import java.util.List;

import junit.framework.TestCase;

import org.jaxen.JaxenException;
import org.jaxen.XPath;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.outline.Reference;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.parsing.JSParser;
import com.aptana.ide.editor.js.tests.TestingPlugin;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.xpath.ParseNodeXPath;

/**
 * @author Kevin Lindsey
 */
public class TestScopeStrings extends TestCase
{
	private JSParser _parser;
	private JSParseState _parseState;
	
	/**
	 * getParseResults
	 * 
	 * @param source
	 * @return IParseNode
	 */
	private IParseNode getParseResults(String source)
	{
		IParseNode result = null;
		
		this._parseState.setEditState(source, source, 0, 0);
		
		try
		{
			this._parser.parse(this._parseState);
			
			result = this._parseState.getParseResults();
		}
		catch (Exception e)
		{
			IdeLog.logInfo(TestingPlugin.getDefault(), "getParseResults failed", e); //$NON-NLS-1$
		}
		
		return result;
	}
	
	/**
	 * getXPathResults
	 * 
	 * @param expression
	 * @return List
	 */
	private List getXPathResults(String expression, IParseNode root)
	{
		List result = null;
		
		try
		{
			XPath xpath = new ParseNodeXPath(expression);
			
			result = (List) xpath.evaluate(root);
		}
		catch (JaxenException e)
		{
			IdeLog.logInfo(TestingPlugin.getDefault(), "getXPathResults failed", e); //$NON-NLS-1$
		}
		
		return result;
	}
	
	/**
	 * testScopeString
	 * 
	 * @param source
	 * 		The source code to parse into an AST
	 * @param xpath
	 * 		The xpath used to locate the node to convert to a scope string
	 * @param expected
	 * 		The expected value of the scope string
	 */
	private void testScopeString(String source, String xpath, String expected)
	{
		IParseNode root = this.getParseResults(source);
		List assignment = this.getXPathResults(xpath, root);
		
		// make sure we got something
		assertNotNull(assignment);
		assertEquals(1, assignment.size());
		
		// grab node and create a scope string for it
		IParseNode node = (IParseNode) assignment.get(0);
		String scopeString = Reference.createScopeString(node);
		
		assertEquals(expected, scopeString);
	}
	
	/**
	 * setUp
	 */
	protected void setUp() throws Exception
	{
		this._parser = new JSParser();
		this._parseState = new JSParseState();
	}
	
	/**
	 * testGlobalVar
	 */
	public void testGlobalVar()
	{
		this.testScopeString(
			"var x = 10;",
			"//var/declaration/identifier",
			"/x"
		);
	}
	
	/**
	 * testGlobalFunctionDeclaration
	 */
	public void testGlobalFunctionDeclaration()
	{
		this.testScopeString(
			"function abc() {}",
			"//function/*[1]",
			"/abc"
		);
	}
	
	public void testGlobalFunctionAssignment()
	{
		this.testScopeString(
			"abc = function() {}",
			"//function",
			"/"
		);
	}
	
	/**
	 * testGlobalProperty
	 */
	public void testGlobalProperty()
	{
		this.testScopeString(
			"x.y = 10;",
			"//assignment/get-property/*[2]",
			"/x.y"
		);
	}
	
	/**
	 * testVarInFunction
	 */
	public void testVarInFunctionDeclaration()
	{
		this.testScopeString(
			"function abc() { var x = 10; }",
			"//var/declaration/identifier",
			"/abc/x"
		);
	}
	
	/**
	 * testVarInFunction
	 */
	public void testVarInFunctionLiteral()
	{
		this.testScopeString(
			"abc = function () { var x = 10; }",
			"//var/declaration/identifier",
			"/[0]statements[0]assignment[1]function/x"
		);
	}
	
	/**
	 * testFunctionInObject
	 */
	public void testFunctionInObject()
	{
		this.testScopeString(
			"a = { b: function() {} };",
			"//function/*[2]",
			//"/a.b"
			"/b/[0]statements[0]assignment[1]object-literal[0]name-value-pair[1]function"
		);
	}
	
	/**
	 * testFunctionInObject
	 */
	public void testVarInFunctionInObject()
	{
		this.testScopeString(
			"a = { b: function() { var c = 10; } };",
			"//var/declaration/identifier",
			//"/a.b.c
			"/b/[0]statements[0]assignment[1]object-literal[0]name-value-pair[1]function/c"
		);
	}
}
