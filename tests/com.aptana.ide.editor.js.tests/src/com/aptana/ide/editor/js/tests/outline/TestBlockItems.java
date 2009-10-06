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
public class TestBlockItems extends TestOutlineItemBase
{
	/**
	 * testVarArrayInFunction
	 */
	public void testVarArrayInFunction()
	{
		this.testItem("function xyz() { var abc = []; }", "/outline/function/array-literal", "abc");
	}
	
	/**
	 * testVarBooleanInFunction
	 */
	public void testVarBooleanInFunction()
	{
		this.testItem("function xyz() { var abc = true; }", "/outline/function/boolean", "abc");
	}
	
	/**
	 * testVarFunctionInFunction
	 */
	public void testVarFunctionInFunction()
	{
		this.testItem("function xyz() { var abc = function() {}; }", "/outline/function/function", "abc()");
	}
	
	/**
	 * testVarNullInFunction
	 */
	public void testVarNullInFunction()
	{
		this.testItem("function xyz() { var abc = null; }", "/outline/function/null", "abc");
	}
	
	/**
	 * testVarNumberInFunction
	 */
	public void testVarNumberInFunction()
	{
		this.testItem("function xyz() { var abc = 10; }", "/outline/function/number", "abc");
	}
	
	/**
	 * testVarObjectInFunction
	 */
	public void testVarObjectInFunction()
	{
		this.testItem("function xyz() { var abc = {}; }", "/outline/function/object-literal", "abc");
	}
	
	/**
	 * testVarRegexInFunction
	 */
	public void testVarRegexInFunction()
	{
		this.testItem("function xyz() { var abc = /abc/; }", "/outline/function/regex", "abc");
	}
	
	/**
	 * testVarStringInFunction
	 */
	public void testVarStringInFunction()
	{
		this.testItem("function xyz() { var abc = \"abc\"; }", "/outline/function/string", "abc");
	}
	
	/**
	 * testFunctionInFunction
	 */
	public void testFunctionInFunction()
	{
		this.testItem("function xyz() { function abc() {} }", "/outline/function/function", "abc()");
	}
	
	/**
	 * testGlobalAssignFunction
	 */
	public void testGlobalAssignFunction()
	{
		this.testItem("abc = function() {};", "/outline/function", "abc()");
	}
	
	/**
	 * testAssignFunctionInFunction
	 */
	public void testAssignFunctionInFunction()
	{
		this.testItem("function xyz() { abc = function() {}; }", "/outline/function/function", "abc()");
	}
	
	/**
	 * testAssignObjectInFunction
	 */
	public void testAssignObjectInFunction()
	{
		this.testItem("function xyz() { abc = {}; }", "/outline/function/object-literal", "abc");
	}
	
	/**
	 * testAssignInvocationInFunction
	 */
	public void testAssignInvocationInFunction()
	{
		this.testItem("function xyz() { abc = Object(); }", "/outline/function/property", "abc");
	}
	
	/**
	 * testAssignDottedInvocationInFunction
	 */
	public void testAssignDottedInvocationInFunction()
	{
		this.testItem("function xyz() { abc = a.b.c.d(); }", "/outline/function/property", "abc");
	}
	
	/**
	 * testReturnObjectInFunction
	 */
	public void testReturnObjectInFunction()
	{
		String source = "function xyz() { return { abc: true }; }";
		
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
	
	/**
	 * testNumberInSelfInvokingFunction
	 */
	public void testSelfInvokingFunctionLiteral()
	{
		this.testItem("(function() { var x = 10; })()", "/outline/number", "x");
	}
	
	/**
	 * testVarArrayInIf
	 */
	public void testVarArrayInIf()
	{
		this.testItem("if (true) { var abc = []; }", "/outline/array-literal", "abc");
	}
	
	/**
	 * testVarBooleanInIf
	 */
	public void testVarBooleanInIf()
	{
		this.testItem("if (true) { var abc = true; }", "/outline/boolean", "abc");
	}
	
	/**
	 * testVarFunctionInIf
	 */
	public void testVarFunctionInIf()
	{
		this.testItem("if (true) { var abc = function() {}; }", "/outline/function", "abc()");
	}
	
	/**
	 * testVarNullInIf
	 */
	public void testVarNullInIf()
	{
		this.testItem("if (true) { var abc = null; }", "/outline/null", "abc");
	}
	
	/**
	 * testVarNumberInIf
	 */
	public void testVarNumberInIf()
	{
		this.testItem("if (true) { var abc = 10; }", "/outline/number", "abc");
	}
	
	/**
	 * testVarObjectInIf
	 */
	public void testVarObjectInIf()
	{
		this.testItem("if (true) { var abc = {}; }", "/outline/object-literal", "abc");
	}
	
	/**
	 * testVarRegexInIf
	 */
	public void testVarRegexInIf()
	{
		this.testItem("if (true) { var abc = /abc/; }", "/outline/regex", "abc");
	}
	
	/**
	 * testVarStringInIf
	 */
	public void testVarStringInIf()
	{
		this.testItem("if (true) { var abc = \"abc\"; }", "/outline/string", "abc");
	}
	
	/**
	 * testFunctionInIf
	 */
	public void testFunctionInIf()
	{
		this.testItem("if (true) { function abc() {} }", "/outline/function", "abc()");
	}
	
	/**
	 * testAssignFunctionInIf
	 */
	public void testAssignFunctionInIf()
	{
		this.testItem("if (true) { abc = function() {}; }", "/outline/function", "abc()");
	}
	
	/**
	 * testAssignObjectInIf
	 */
	public void testAssignObjectInIf()
	{
		this.testItem("if (true) { abc = {}; }", "/outline/object-literal", "abc");
	}
	
	/**
	 * testAssignInvocationInIf
	 */
	public void testAssignInvocationInIf()
	{
		this.testItem("if (true) { abc = Object(); }", "/outline/property", "abc");
	}
	
	/**
	 * testAssignDottedInvocationInIf
	 */
	public void testAssignDottedInvocationInIf()
	{
		this.testItem("if (true) { abc = a.b.c.d(); }", "/outline/property", "abc");
	}
	
	/**
	 * testVarArrayInIfElse
	 */
	public void testVarArrayInIfElse()
	{
		this.testItem("if (true) { } else { var abc = []; }", "/outline/array-literal", "abc");
	}
	
	/**
	 * testVarBooleanInIfElse
	 */
	public void testVarBooleanInIfElse()
	{
		this.testItem("if (true) { } else { var abc = true; }", "/outline/boolean", "abc");
	}
	
	/**
	 * testVarFunctionInIfElse
	 */
	public void testVarFunctionInIfElse()
	{
		this.testItem("if (true) { } else { var abc = function() {}; }", "/outline/function", "abc()");
	}
	
	/**
	 * testVarNullInIfElse
	 */
	public void testVarNullInIfElse()
	{
		this.testItem("if (true) { } else { var abc = null; }", "/outline/null", "abc");
	}
	
	/**
	 * testVarNumberInIfElse
	 */
	public void testVarNumberInIfElse()
	{
		this.testItem("if (true) { } else { var abc = 10; }", "/outline/number", "abc");
	}
	
	/**
	 * testVarObjectInIfElse
	 */
	public void testVarObjectInIfElse()
	{
		this.testItem("if (true) { } else { var abc = {}; }", "/outline/object-literal", "abc");
	}
	
	/**
	 * testVarRegexInIfElse
	 */
	public void testVarRegexInIfElse()
	{
		this.testItem("if (true) { } else { var abc = /abc/; }", "/outline/regex", "abc");
	}
	
	/**
	 * testVarStringInIfElse
	 */
	public void testVarStringInIfElse()
	{
		this.testItem("if (true) { } else { var abc = \"abc\"; }", "/outline/string", "abc");
	}
	
	/**
	 * testFunctionInIfElse
	 */
	public void testFunctionInIfElse()
	{
		this.testItem("if (true) { } else { function abc() {} }", "/outline/function", "abc()");
	}
	
	/**
	 * testAssignFunctionInIfElse
	 */
	public void testAssignFunctionInIfElse()
	{
		this.testItem("if (true) { } else { abc = function() {}; }", "/outline/function", "abc()");
	}
	
	/**
	 * testAssignObjectInIfElse
	 */
	public void testAssignObjectInIfElse()
	{
		this.testItem("if (true) { } else { abc = {}; }", "/outline/object-literal", "abc");
	}
	
	/**
	 * testAssignInvocationInIfElse
	 */
	public void testAssignInvocationInIfElse()
	{
		this.testItem("if (true) { } else { abc = Object(); }", "/outline/property", "abc");
	}
	
	/**
	 * testAssignDottedInvocationInIfElse
	 */
	public void testAssignDottedInvocationInIfElse()
	{
		this.testItem("if (true) { } else { abc = a.b.c.d(); }", "/outline/property", "abc");
	}
	
	/**
	 * testVarArrayInTry
	 */
	public void testVarArrayInTry()
	{
		this.testItem("try { var abc = []; } catch(e) {}", "/outline/array-literal", "abc");
	}
	
	/**
	 * testVarBooleanInTry
	 */
	public void testVarBooleanInTry()
	{
		this.testItem("try { var abc = true; } catch(e) {}", "/outline/boolean", "abc");
	}
	
	/**
	 * testVarFunctionInTry
	 */
	public void testVarFunctionInTry()
	{
		this.testItem("try { var abc = function() {}; } catch(e) {}", "/outline/function", "abc()");
	}
	
	/**
	 * testVarNullInTry
	 */
	public void testVarNullInTry()
	{
		this.testItem("try { var abc = null; } catch(e) {}", "/outline/null", "abc");
	}
	
	/**
	 * testVarNumberInTry
	 */
	public void testVarNumberInTry()
	{
		this.testItem("try { var abc = 10; } catch(e) {}", "/outline/number", "abc");
	}
	
	/**
	 * testVarObjectInTry
	 */
	public void testVarObjectInTry()
	{
		this.testItem("try { var abc = {}; } catch(e) {}", "/outline/object-literal", "abc");
	}
	
	/**
	 * testVarRegexInTry
	 */
	public void testVarRegexInTry()
	{
		this.testItem("try { var abc = /abc/; } catch(e) {}", "/outline/regex", "abc");
	}
	
	/**
	 * testVarStringInTry
	 */
	public void testVarStringInTry()
	{
		this.testItem("try { var abc = \"abc\"; } catch(e) {}", "/outline/string", "abc");
	}
	
	/**
	 * testFunctionInTry
	 */
	public void testFunctionInTry()
	{
		this.testItem("try { function abc() {} } catch(e) {}", "/outline/function", "abc()");
	}
	
	/**
	 * testAssignFunctionInTry
	 */
	public void testAssignFunctionInTry()
	{
		this.testItem("try { abc = function() {}; } catch(e) {}", "/outline/function", "abc()");
	}
	
	/**
	 * testAssignObjectInTry
	 */
	public void testAssignObjectInTry()
	{
		this.testItem("try { abc = {}; } catch(e) {}", "/outline/object-literal", "abc");
	}
	
	/**
	 * testAssignInvocationInTry
	 */
	public void testAssignInvocationInTry()
	{
		this.testItem("try { abc = Object(); } catch(e) {}", "/outline/property", "abc");
	}
	
	/**
	 * testAssignDottedInvocationInTry
	 */
	public void testAssignDottedInvocationInTry()
	{
		this.testItem("try { abc = a.b.c.d(); } catch(e) {}", "/outline/property", "abc");
	}
	
	/**
	 * testVarArrayInTryCatch
	 */
	public void testVarArrayInTryCatch()
	{
		this.testItem("try { } catch(e) { var abc = []; }", "/outline/array-literal", "abc");
	}
	
	/**
	 * testVarBooleanInTryCatch
	 */
	public void testVarBooleanInTryCatch()
	{
		this.testItem("try { } catch(e) { var abc = true; }", "/outline/boolean", "abc");
	}
	
	/**
	 * testVarFunctionInTryCatch
	 */
	public void testVarFunctionInTryCatch()
	{
		this.testItem("try { } catch(e) { var abc = function() {}; }", "/outline/function", "abc()");
	}
	
	/**
	 * testVarNullInTryCatch
	 */
	public void testVarNullInTryCatch()
	{
		this.testItem("try { } catch(e) { var abc = null; }", "/outline/null", "abc");
	}
	
	/**
	 * testVarNumberInTryCatch
	 */
	public void testVarNumberInTryCatch()
	{
		this.testItem("try { } catch(e) { var abc = 10; }", "/outline/number", "abc");
	}
	
	/**
	 * testVarObjectInTryCatch
	 */
	public void testVarObjectInTryCatch()
	{
		this.testItem("try { } catch(e) { var abc = {}; }", "/outline/object-literal", "abc");
	}
	
	/**
	 * testVarRegexInTryCatch
	 */
	public void testVarRegexInTryCatch()
	{
		this.testItem("try { } catch(e) { var abc = /abc/; }", "/outline/regex", "abc");
	}
	
	/**
	 * testVarStringInTryCatch
	 */
	public void testVarStringInTryCatch()
	{
		this.testItem("try { } catch(e) { var abc = \"abc\"; }", "/outline/string", "abc");
	}
	
	/**
	 * testFunctionInTryCatch
	 */
	public void testFunctionInTryCatch()
	{
		this.testItem("try { } catch(e) { function abc() {} }", "/outline/function", "abc()");
	}
	
	/**
	 * testAssignFunctionInTryCatch
	 */
	public void testAssignFunctionInTryCatch()
	{
		this.testItem("try { } catch(e) { abc = function() {}; }", "/outline/function", "abc()");
	}
	
	/**
	 * testAssignObjectInTryCatch
	 */
	public void testAssignObjectInTryCatch()
	{
		this.testItem("try { } catch(e) { abc = {}; }", "/outline/object-literal", "abc");
	}
	
	/**
	 * testAssignInvocationInTryCatch
	 */
	public void testAssignInvocationInTryCatch()
	{
		this.testItem("try { } catch(e) { abc = Object(); }", "/outline/property", "abc");
	}
	
	/**
	 * testAssignDottedInvocationInTryCatch
	 */
	public void testAssignDottedInvocationInTryCatch()
	{
		this.testItem("try { } catch(e) { abc = a.b.c.d(); }", "/outline/property", "abc");
	}
}
