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
package com.aptana.ide.editor.js.tests;

import java.text.ParseException;

import junit.framework.TestCase;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.parsing.JSParser;
import com.aptana.ide.editor.js.parsing.JSScanner;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class TestStatements extends TestCase
{
	private static final String EOL = System.getProperty("line.separator"); //$NON-NLS-1$

	private JSScanner _scanner;
	private JSParser _parser;
//	private JSParser2 _parser2;
	
	private JSParseState _parseState;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this._scanner = new JSScanner();
		this._parser = new JSParser();
//		this._parser2 = new JSParser2();
		this._parseState = (JSParseState) this._parser.createParseState(null);
	}

	/**
	 * typingTests
	 * 
	 * @param source
	 */
	protected void parseTest(String source)
	{
		this.parseTest(source, source);
	}
	
	/**
	 * parseTest
	 *  
	 * @param source
	 * @param expected
	 */
	protected void parseTest(String source, String expected)
	{
		// do full parse
		this.singlePass(this._parser, source, expected);
//		this.singlePass(this._parser2, source, expected);
		
		if (UnifiedEditorsPlugin.getDefault().useFastScan())
		{
			// do two-phase parse
			this.twoPass(source, expected);
		}
	}
	
	/**
	 * singlePass
	 * 
	 * @param source
	 * @param expected
	 */
	protected void singlePass(IParser parser, String source, String expected)
	{
		this._parseState.setEditState(source, source, 0, 0);

		try
		{
			parser.parse(this._parseState);
			IParseNode parseResult = this._parseState.getParseResults();
			String result = parseResult.getSource();

			assertEquals(expected, result);
		}
		catch (LexerException e)
		{
			IdeLog.logInfo(TestingPlugin.getDefault(), "parseTest failed", e); //$NON-NLS-1$
		}
		catch (ParseException e)
		{
			IdeLog.logInfo(TestingPlugin.getDefault(), "parseTest failed", e); //$NON-NLS-1$
		}
	}
	
	/**
	 * twoPass
	 * 
	 * @param source
	 * @param expected
	 */
	protected void twoPass(String source, String expected)
	{
		this._parseState.getRoot().reset();
		this._parseState.setEditState(source, source, 0, 0);
		
		try
		{
			this._scanner.parse(this._parseState);
			this._parser.parse(this._parseState);
			IParseNode parseResult = this._parseState.getParseResults();
			String result = parseResult.getSource();
			
			assertEquals(expected, result);
		}
		catch (LexerException e)
		{
			IdeLog.logInfo(TestingPlugin.getDefault(), "parseTest failed", e); //$NON-NLS-1$
		}
	}

	/**
	 * Test empty statement
	 * 
	 * @throws Exception
	 */
	public void testEmptyStatement() throws Exception
	{
		this.parseTest(";" + EOL); //$NON-NLS-1$
	}
	
	/**
	 * Test an empty block
	 * 
	 * @throws Exception
	 */
	public void testEmptyBlock() throws Exception
	{
		this.parseTest("{}" + EOL); //$NON-NLS-1$
	}
	
	/**
	 * Test simple assignment
	 * 
	 * @throws Exception
	 */
	public void testAssign() throws Exception
	{
		this.parseTest("a = 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test add and assignment
	 * 
	 * @throws Exception
	 */
	public void testAddAndAssign() throws Exception
	{
		this.parseTest("a += 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test arithmetic shift right and assignment
	 * 
	 * @throws Exception
	 */
	public void testArithmeticShiftRightAndAssign() throws Exception
	{
		this.parseTest("a >>>= 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test bitwise-and and assignment
	 * 
	 * @throws Exception
	 */
	public void testBitwiseAndAndAssign() throws Exception
	{
		this.parseTest("a &= 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test bitwise-or and assignment
	 * 
	 * @throws Exception
	 */
	public void testBitwiseOrAndAssign() throws Exception
	{
		this.parseTest("a |= 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test bitwise-xor and assignment
	 * 
	 * @throws Exception
	 */
	public void testBitwiseXorAndAssign() throws Exception
	{
		this.parseTest("a ^= 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test divide and assignment
	 * 
	 * @throws Exception
	 */
	public void testDivideAndAssign() throws Exception
	{
		this.parseTest("a /= 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test mod and assignment
	 * 
	 * @throws Exception
	 */
	public void testModAndAssign() throws Exception
	{
		this.parseTest("a %= 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test multiply and assignment
	 * 
	 * @throws Exception
	 */
	public void testMultiplyAndAssign() throws Exception
	{
		this.parseTest("a *= 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test shift left and assignment
	 * 
	 * @throws Exception
	 */
	public void testShiftLeftAndAssign() throws Exception
	{
		this.parseTest("a <<= 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test shift right and assignment
	 * 
	 * @throws Exception
	 */
	public void testShiftRightAndAssign() throws Exception
	{
		this.parseTest("a >>= 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test subtract and assignment
	 * 
	 * @throws Exception
	 */
	public void testSubtractAndAssign() throws Exception
	{
		this.parseTest("a -= 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test add
	 * 
	 * @throws Exception
	 */
	public void testAdd() throws Exception
	{
		this.parseTest("5 + 5;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test get element
	 * 
	 * @throws Exception
	 */
	public void testGetElement() throws Exception
	{
		this.parseTest("abc[10];" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test get property
	 * 
	 * @throws Exception
	 */
	public void testGetProperty() throws Exception
	{
		this.parseTest("abc.def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test equal
	 * 
	 * @throws Exception
	 */
	public void testEqual() throws Exception
	{
		this.parseTest("abc = def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test greater than
	 * 
	 * @throws Exception
	 */
	public void testGreaterThan() throws Exception
	{
		this.parseTest("abc > def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test greater than
	 * 
	 * @throws Exception
	 */
	public void testGreaterThanOrEqual() throws Exception
	{
		this.parseTest("abc >= def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test identity
	 * 
	 * @throws Exception
	 */
	public void testIdentity() throws Exception
	{
		this.parseTest("abc === def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test in
	 * 
	 * @throws Exception
	 */
	public void testIn() throws Exception
	{
		this.parseTest("\"abc\" in def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test instance of
	 * 
	 * @throws Exception
	 */
	public void testInstanceOf() throws Exception
	{
		this.parseTest("abc instanceof def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test less than
	 * 
	 * @throws Exception
	 */
	public void testLessThan() throws Exception
	{
		this.parseTest("abc < def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test less than or equal
	 * 
	 * @throws Exception
	 */
	public void testLessThanOrEqual() throws Exception
	{
		this.parseTest("abc <= def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test logical and
	 * 
	 * @throws Exception
	 */
	public void testLogicalAnd() throws Exception
	{
		this.parseTest("abc && def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test logical or
	 * 
	 * @throws Exception
	 */
	public void testLogicalOr() throws Exception
	{
		this.parseTest("abc || def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test not equal
	 * 
	 * @throws Exception
	 */
	public void testNotEqual() throws Exception
	{
		this.parseTest("abc != def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test not identity
	 * 
	 * @throws Exception
	 */
	public void testNotIdentity() throws Exception
	{
		this.parseTest("abc !== def;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test arithmetic shift right
	 * 
	 * @throws Exception
	 */
	public void testArithmeticShiftRight() throws Exception
	{
		this.parseTest("abc >>> 5;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test bitwise-and
	 * 
	 * @throws Exception
	 */
	public void testBitwiseAnd() throws Exception
	{
		this.parseTest("abc & 5;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test bitwise-or
	 * 
	 * @throws Exception
	 */
	public void testBitwiseOr() throws Exception
	{
		this.parseTest("abc | 5;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test bitwise-xor
	 * 
	 * @throws Exception
	 */
	public void testBitwiseXor() throws Exception
	{
		this.parseTest("abc ^ 5;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test divide
	 * 
	 * @throws Exception
	 */
	public void testDivide() throws Exception
	{
		this.parseTest("abc / 5;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test mod
	 * 
	 * @throws Exception
	 */
	public void testMod() throws Exception
	{
		this.parseTest("abc % 5;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test multiply
	 * 
	 * @throws Exception
	 */
	public void testMultiply() throws Exception
	{
		this.parseTest("abc * 5;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test shift left
	 * 
	 * @throws Exception
	 */
	public void testShiftLeft() throws Exception
	{
		this.parseTest("abc << 5;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test shift right
	 * 
	 * @throws Exception
	 */
	public void testShiftRight() throws Exception
	{
		this.parseTest("abc >> 5;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test subtract
	 * 
	 * @throws Exception
	 */
	public void testSubtract() throws Exception
	{
		this.parseTest("abc - 5;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test try/catch
	 * 
	 * @throws Exception
	 */
	public void testTryCatch() throws Exception
	{
		this.parseTest("try {} catch (e) {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test conditional
	 * 
	 * @throws Exception
	 */
	public void testConditional() throws Exception
	{
		this.parseTest("(abc) ? true : false;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test do
	 * 
	 * @throws Exception
	 */
	public void testDo() throws Exception
	{
		this.parseTest("do {" + EOL + "    a++;" + EOL + "} while (a < 10)" + EOL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Test try/finally
	 * 
	 * @throws Exception
	 */
	public void testTryFinally() throws Exception
	{
		this.parseTest("try {} finally {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test for-in
	 * 
	 * @throws Exception
	 */
	public void testForIn() throws Exception
	{
		this.parseTest("for (a in obj) {" + EOL + "    show(a);" + EOL + "}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Test for
	 * 
	 * @throws Exception
	 */
	public void testFor() throws Exception
	{
		this.parseTest("for (var a = 0; a < 10; a++) {" + EOL + "    show(a);" + EOL + "}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test empty function
	 * 
	 * @throws Exception
	 */
	public void testEmptyFunction() throws Exception
	{
		this.parseTest("function abc () {}" + EOL); //$NON-NLS-1$
	}
	
	/**
	 * Test empty function with one parameter
	 * 
	 * @throws Exception
	 */
	public void testEmptyFunctionWithOneParameter() throws Exception
	{
		this.parseTest("function abc (a) {}" + EOL); //$NON-NLS-1$
	}
	
	/**
	 * Test empty function with multiple parameters
	 * 
	 * @throws Exception
	 */
	public void testEmptyFunctionWithParameters() throws Exception
	{
		this.parseTest("function abc (a, b, c) {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test function
	 * 
	 * @throws Exception
	 */
	public void testFunction() throws Exception
	{
		this.parseTest("function abc () {" + EOL + "    return true;" + EOL + "}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Test identifier
	 * 
	 * @throws Exception
	 */
	public void testIdentifier() throws Exception
	{
		this.parseTest("abc;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test if
	 * 
	 * @throws Exception
	 */
	public void testIf() throws Exception
	{
		this.parseTest("if (a < b) {" + EOL + "    a = 10;" + EOL + "} else {" + EOL + "    a = 20;" + EOL + "}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	/**
	 * Test invoke
	 * 
	 * @throws Exception
	 */
	public void testInvoke() throws Exception
	{
		this.parseTest("abc();" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test labeled statement
	 * 
	 * @throws Exception
	 */
	public void testLabeledStatement() throws Exception
	{
		this.parseTest("myLabel: while (true) {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test break
	 * 
	 * @throws Exception
	 */
	public void testBreak() throws Exception
	{
		this.parseTest("while (true) {" + EOL + "    break;" + EOL + "}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Test continue
	 * 
	 * @throws Exception
	 */
	public void testContinue() throws Exception
	{
		this.parseTest("while (true) {" + EOL + "    continue;" + EOL + "}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Test arguments
	 * 
	 * @throws Exception
	 */
	public void testArguments() throws Exception
	{
		this.parseTest("abc(a, b, c);" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test array literal
	 * 
	 * @throws Exception
	 */
	public void testArrayLiteral() throws Exception
	{
		this.parseTest("abc = [1, 2, 3];" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test comma
	 * 
	 * @throws Exception
	 */
	public void testComma() throws Exception
	{
		this.parseTest("abc = 10, def = 20;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test default
	 * 
	 * @throws Exception
	 */
	public void testDefault() throws Exception
	{
		this.parseTest("switch (abc) {" + EOL + "    default:" + EOL + "        break;" + EOL + "}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	/**
	 * Test case
	 * 
	 * @throws Exception
	 */
	public void testCase() throws Exception
	{
		this.parseTest("switch (abc) {" + EOL + "    case 10:" + EOL + "        break;" + EOL + "}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	/**
	 * Test switch
	 * 
	 * @throws Exception
	 */
	public void testSwitch() throws Exception
	{
		this.parseTest("switch (abc) {" + EOL + "    case 10:" + EOL + "        break;" + EOL + "    default:" + EOL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ "        break;" + EOL + "}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Test object literal
	 * 
	 * @throws Exception
	 */
	public void testObjectLiteral() throws Exception
	{
		this.parseTest("abc = {" + EOL + "    name: \"Name\"," + EOL + "    index: 2," + EOL + "    id: 10" + EOL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ "};" + EOL); //$NON-NLS-1$
	}
	
	/**
	 * Test object literal with getter
	 */
	public void testObjectLiteralGetter()
	{
		this.parseTest(
			"abc = {" + EOL + "    get testing() {}" + EOL + "};" + EOL, //$NON-NLS-1$
			"abc = {" + EOL + "    testing: function testing () {}" + EOL + "};" + EOL //$NON-NLS-1$
		);
	}
	
	/**
	 * Test object literal with getter
	 */
	public void testObjectLiteralGetProperty()
	{
		this.parseTest("abc = {" + EOL + "    get: 10" + EOL + "};" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test parameters
	 * 
	 * @throws Exception
	 */
	public void testParameters() throws Exception
	{
		this.parseTest("function abc (a, b, c) {" + EOL + "    return true;" + EOL + "}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Test var
	 * 
	 * @throws Exception
	 */
	public void testVar() throws Exception
	{
		this.parseTest("var abc = 10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test false
	 * 
	 * @throws Exception
	 */
	public void testFalse() throws Exception
	{
		this.parseTest("false;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test null
	 * 
	 * @throws Exception
	 */
	public void testNull() throws Exception
	{
		this.parseTest("null;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test number
	 * 
	 * @throws Exception
	 */
	public void testNumber() throws Exception
	{
		this.parseTest("10.3;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test regex
	 * 
	 * @throws Exception
	 */
	public void testRegex() throws Exception
	{
		this.parseTest("/abc/;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test string
	 * 
	 * @throws Exception
	 */
	public void testString() throws Exception
	{
		this.parseTest("\"this is a string\";" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test unclosed string
	 * 
	 * @throws Exception
	 */
	public void testUnclosedString() throws Exception
	{
		this.parseTest("\"this is a string\\" + EOL + "Second line;" + EOL); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Test true
	 * 
	 * @throws Exception
	 */
	public void testTrue() throws Exception
	{
		this.parseTest("true;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test this
	 * 
	 * @throws Exception
	 */
	public void testThis() throws Exception
	{
		this.parseTest("this;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test try
	 * 
	 * @throws Exception
	 */
	public void testTry() throws Exception
	{
		this.parseTest("try {} catch (e) {} finally {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test construct
	 * 
	 * @throws Exception
	 */
	public void testConstruct() throws Exception
	{
		this.parseTest("a = new Object(\"test\");" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test delete
	 * 
	 * @throws Exception
	 */
	public void testDelete() throws Exception
	{
		this.parseTest("delete obj.prop;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test group
	 * 
	 * @throws Exception
	 */
	public void testGroup() throws Exception
	{
		this.parseTest("a = (3 + 4) * 5;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test logical not
	 * 
	 * @throws Exception
	 */
	public void testLogicalNot() throws Exception
	{
		this.parseTest("a = !false;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test bitwise-not
	 * 
	 * @throws Exception
	 */
	public void testBitwiseNot() throws Exception
	{
		this.parseTest("a = ~10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test negate
	 * 
	 * @throws Exception
	 */
	public void testNegate() throws Exception
	{
		this.parseTest("a = -10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test positive
	 * 
	 * @throws Exception
	 */
	public void testPositive() throws Exception
	{
		this.parseTest("a = +10;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test post-decrement
	 * 
	 * @throws Exception
	 */
	public void testPostDecrement() throws Exception
	{
		this.parseTest("a--;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test post-increment
	 * 
	 * @throws Exception
	 */
	public void testPostIncrement() throws Exception
	{
		this.parseTest("a++;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test pre-decrement
	 * 
	 * @throws Exception
	 */
	public void testPreDecrement() throws Exception
	{
		this.parseTest("--a;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test pre-increment
	 * 
	 * @throws Exception
	 */
	public void testPreIncrement() throws Exception
	{
		this.parseTest("++a;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test return
	 * 
	 * @throws Exception
	 */
	public void testReturn() throws Exception
	{
		this.parseTest("function abc () {" + EOL + "    return false;" + EOL + "}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Test throw
	 * 
	 * @throws Exception
	 */
	public void testThrow() throws Exception
	{
		this.parseTest("throw new Error(\"error\");" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test typeof
	 * 
	 * @throws Exception
	 */
	public void testTypeof() throws Exception
	{
		this.parseTest("a = typeof(object);" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test void
	 * 
	 * @throws Exception
	 */
	public void testVoid() throws Exception
	{
		this.parseTest("void (abc());" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test while
	 * 
	 * @throws Exception
	 */
	public void testWhile() throws Exception
	{
		this.parseTest("while (true) {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test with
	 * 
	 * @throws Exception
	 */
	public void testWith() throws Exception
	{
		this.parseTest("with (a.b) {" + EOL + "    a = c;" + EOL + "}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/*
	 * Miscellaneous Tests
	 */

	/**
	 * dojo error case
	 * 
	 * @throws Exception
	 */
	public void testDojoErrorCase() throws Exception
	{
		String source = "return arr.join(\"/\").replace(/\\/{2,}/g, \"/\").replace(/((https*|ftps*):)/i, \"$1/\");" //$NON-NLS-1$
				+ EOL;

		this.parseTest(source);
	}
}
