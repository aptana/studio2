/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */package com.aptana.ide.editor.js.tests.outline;


/**
 * @author Kevin Lindsey
 */
public class TestSimpleItems extends TestOutlineItemBase
{
	/**
	 * testGlobalVarArray
	 */
	public void testGlobalVarArray()
	{
		this.testItem("var x = [1, 2, 3];", "/outline/array-literal", "x");
	}

	/**
	 * testGlobalVarBoolean
	 */
	public void testGlobalVarBoolean()
	{
		this.testItem("var x = true;", "/outline/boolean", "x");
	}

	/**
	 * testGlobalVarFunction
	 */
	public void testGlobalVarFunction()
	{
		this.testItem("var x = function() {};", "/outline/function", "x()");
	}

	/**
	 * testGlobalVarNull
	 */
	public void testGlobalVarNull()
	{
		this.testItem("var x = null;", "/outline/null", "x");
	}
	
	/**
	 * testGlobalVarNumber
	 */
	public void testGlobalVarNumber()
	{
		this.testItem("var x = 10;", "/outline/number", "x");
	}

	/**
	 * testGlobalVarObject
	 */
	public void testGlobalVarObject()
	{
		this.testItem("var x = {};", "/outline/object-literal", "x");
	}

	/**
	 * testGlobalVarRegex
	 */
	public void testGlobalVarRegex()
	{
		this.testItem("var x = /abc/;", "/outline/regex", "x");
	}

	/**
	 * testGlobalVarString
	 */
	public void testGlobalVarString()
	{
		this.testItem("var x = \"10\";", "/outline/string", "x");
	}

	/**
	 * testGlobalFunctionDeclaration
	 */
	public void testGlobalFunctionDeclaration()
	{
		this.testItem("function abc() {}", "/outline/function", "abc()");
	}
	
	/**
	 * testGlobalAssignObject
	 */
	public void testGlobalAssignObject()
	{
		this.testItem("abc = {};", "/outline/object-literal", "abc");
	}
	
	/**
	 * testGlobalAssignInvocation
	 */
	public void testGlobalAssignInvocation()
	{
		this.testItem("abc = Object();", "/outline/property", "abc");
	}
	
	/**
	 * testGlobalAssignDottedInvocation
	 */
	public void testGlobalAssignDottedInvocation()
	{
		this.testItem("abc = a.b.c.d();", "/outline/property", "abc");
	}
	
	/**
	 * testReturnObjectInFunction
	 */
	public void testReturnObjectInFunction()
	{
		String source = "xyz = function() { return { abc: true }; }";
		
		this.testItem(
			source,
			"/outline/function",
			"xyz()",
			1
		);
		this.testItem(
			source,
			"/outline/function/boolean",
			"abc"
		);
	}
}
