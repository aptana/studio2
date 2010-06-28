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

import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.parsing.JSParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.lexer.tests.TestTokenBase;

/**
 * @author Kevin Lindsey
 */
public class TestPunctuatorTokens extends TestTokenBase
{
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#createLexer()
	 */
	protected ILexer createLexer() throws Exception
	{
		JSParser parser = new JSParser();

		return parser.getLexer();
	}
	
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#getLanguage()
	 */
	protected String getLanguage()
	{
		return JSMimeType.MimeType;
	}
	
	/**
	 * testLeftCurlyBrace
	 */
	public void testLeftCurlyBrace()
	{
		this.lexemeTest("{", TokenCategories.PUNCTUATOR, JSTokenTypes.LCURLY); //$NON-NLS-1$
	}

	/**
	 * testRightCurlyBrace
	 */
	public void testRightCurlyBrace()
	{
		this.lexemeTest("}", TokenCategories.PUNCTUATOR, JSTokenTypes.RCURLY); //$NON-NLS-1$
	}

	/**
	 * testLeftSquareBracket
	 */
	public void testLeftSquareBracket()
	{
		this.lexemeTest("[", TokenCategories.PUNCTUATOR, JSTokenTypes.LBRACKET); //$NON-NLS-1$
	}

	/**
	 * testRightSquareBracket
	 */
	public void testRightSquareBracket()
	{
		this.lexemeTest("]", TokenCategories.PUNCTUATOR, JSTokenTypes.RBRACKET); //$NON-NLS-1$
	}

	/**
	 * testLeftParenthesis
	 */
	public void testLeftParenthesis()
	{
		this.lexemeTest("(", TokenCategories.PUNCTUATOR, JSTokenTypes.LPAREN); //$NON-NLS-1$
	}

	/**
	 * testRightParenthesis
	 */
	public void testRightParenthesis()
	{
		this.lexemeTest(")", TokenCategories.PUNCTUATOR, JSTokenTypes.RPAREN); //$NON-NLS-1$
	}

	/**
	 * testTilde
	 */
	public void testTilde()
	{
		this.lexemeTest("~", TokenCategories.PUNCTUATOR, JSTokenTypes.TILDE); //$NON-NLS-1$
	}

	/**
	 * testDot
	 */
	public void testDot()
	{
		this.lexemeTest(".", TokenCategories.PUNCTUATOR, JSTokenTypes.DOT); //$NON-NLS-1$
	}

	/**
	 * testSemicolon
	 */
	public void testSemicolon()
	{
		this.lexemeTest(";", TokenCategories.PUNCTUATOR, JSTokenTypes.SEMICOLON); //$NON-NLS-1$
	}

	/**
	 * testComma
	 */
	public void testComma()
	{
		this.lexemeTest(",", TokenCategories.PUNCTUATOR, JSTokenTypes.COMMA); //$NON-NLS-1$
	}

	/**
	 * testQuestionMark
	 */
	public void testQuestionMark()
	{
		this.lexemeTest("?", TokenCategories.PUNCTUATOR, JSTokenTypes.QUESTION); //$NON-NLS-1$
	}

	/**
	 * testColon
	 */
	public void testColon()
	{
		this.lexemeTest(":", TokenCategories.PUNCTUATOR, JSTokenTypes.COLON); //$NON-NLS-1$
	}

	/**
	 * testPlus
	 */
	public void testPlus()
	{
		this.lexemeTest("+", TokenCategories.PUNCTUATOR, JSTokenTypes.PLUS); //$NON-NLS-1$
	}

	/**
	 * testPlusPlus
	 */
	public void testPlusPlus()
	{
		this.lexemeTest("++", TokenCategories.PUNCTUATOR, JSTokenTypes.PLUS_PLUS); //$NON-NLS-1$
	}

	/**
	 * testPlusEqual
	 */
	public void testPlusEqual()
	{
		this.lexemeTest("+=", TokenCategories.PUNCTUATOR, JSTokenTypes.PLUS_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testMinus
	 */
	public void testMinus()
	{
		this.lexemeTest("-", TokenCategories.PUNCTUATOR, JSTokenTypes.MINUS); //$NON-NLS-1$
	}

	/**
	 * testMinusMinus
	 */
	public void testMinusMinus()
	{
		this.lexemeTest("--", TokenCategories.PUNCTUATOR, JSTokenTypes.MINUS_MINUS); //$NON-NLS-1$
	}

	/**
	 * testMinusEqual
	 */
	public void testMinusEqual()
	{
		this.lexemeTest("-=", TokenCategories.PUNCTUATOR, JSTokenTypes.MINUS_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testAsterisk
	 */
	public void testAsterisk()
	{
		this.lexemeTest("*", TokenCategories.PUNCTUATOR, JSTokenTypes.STAR); //$NON-NLS-1$
	}

	/**
	 * testAsteriskEqual
	 */
	public void testAsteriskEqual()
	{
		this.lexemeTest("*=", TokenCategories.PUNCTUATOR, JSTokenTypes.STAR_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testForwardSlash
	 */
	public void testForwardSlash()
	{
		this.lexemeTest("/", TokenCategories.PUNCTUATOR, JSTokenTypes.FORWARD_SLASH); //$NON-NLS-1$
	}

	/**
	 * testForwardSlashEqual
	 */
	public void testForwardSlashEqual()
	{
		this.lexemeTest("/=", TokenCategories.PUNCTUATOR, JSTokenTypes.FORWARD_SLASH_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testCaret
	 */
	public void testCaret()
	{
		this.lexemeTest("^", TokenCategories.PUNCTUATOR, JSTokenTypes.CARET); //$NON-NLS-1$
	}

	/**
	 * testCaretEqual
	 */
	public void testCaretEqual()
	{
		this.lexemeTest("^=", TokenCategories.PUNCTUATOR, JSTokenTypes.CARET_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testExclamation
	 */
	public void testExclamation()
	{
		this.lexemeTest("!", TokenCategories.PUNCTUATOR, JSTokenTypes.EXCLAMATION); //$NON-NLS-1$
	}

	/**
	 * testExclamationEqual
	 */
	public void testExclamationEqual()
	{
		this.lexemeTest("!=", TokenCategories.PUNCTUATOR, JSTokenTypes.EXCLAMATION_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testExclamationEqualEqual
	 */
	public void testExclamationEqualEqual()
	{
		this.lexemeTest("!==", TokenCategories.PUNCTUATOR, JSTokenTypes.EXCLAMATION_EQUAL_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testPercent
	 */
	public void testPercent()
	{
		this.lexemeTest("%", TokenCategories.PUNCTUATOR, JSTokenTypes.PERCENT); //$NON-NLS-1$
	}

	/**
	 * testPercentEqual
	 */
	public void testPercentEqual()
	{
		this.lexemeTest("%=", TokenCategories.PUNCTUATOR, JSTokenTypes.PERCENT_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testAmpersand
	 */
	public void testAmpersand()
	{
		this.lexemeTest("&", TokenCategories.PUNCTUATOR, JSTokenTypes.AMPERSAND); //$NON-NLS-1$
	}

	/**
	 * testAmpersandEqual
	 */
	public void testAmpersandEqual()
	{
		this.lexemeTest("&=", TokenCategories.PUNCTUATOR, JSTokenTypes.AMPERSAND_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testAmpersandAmpersand
	 */
	public void testAmpersandAmpersand()
	{
		this.lexemeTest("&&", TokenCategories.PUNCTUATOR, JSTokenTypes.AMPERSAND_AMPERSAND); //$NON-NLS-1$
	}

	/**
	 * testPipe
	 */
	public void testPipe()
	{
		this.lexemeTest("|", TokenCategories.PUNCTUATOR, JSTokenTypes.PIPE); //$NON-NLS-1$
	}

	/**
	 * testPipePipe
	 */
	public void testPipePipe()
	{
		this.lexemeTest("||", TokenCategories.PUNCTUATOR, JSTokenTypes.PIPE_PIPE); //$NON-NLS-1$
	}

	/**
	 * testPipeEqual
	 */
	public void testPipeEqual()
	{
		this.lexemeTest("|=", TokenCategories.PUNCTUATOR, JSTokenTypes.PIPE_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testGreater
	 */
	public void testGreater()
	{
		this.lexemeTest(">", TokenCategories.PUNCTUATOR, JSTokenTypes.GREATER); //$NON-NLS-1$
	}

	/**
	 * testGreaterGreater
	 */
	public void testGreaterGreater()
	{
		this.lexemeTest(">>", TokenCategories.PUNCTUATOR, JSTokenTypes.GREATER_GREATER); //$NON-NLS-1$
	}

	/**
	 * testGreaterGreaterGreater
	 */
	public void testGreaterGreaterGreater()
	{
		this.lexemeTest(">>>", TokenCategories.PUNCTUATOR, JSTokenTypes.GREATER_GREATER_GREATER); //$NON-NLS-1$
	}

	/**
	 * testGreaterEqual
	 */
	public void testGreaterEqual()
	{
		this.lexemeTest(">=", TokenCategories.PUNCTUATOR, JSTokenTypes.GREATER_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testGreaterGreaterEqual
	 */
	public void testGreaterGreaterEqual()
	{
		this.lexemeTest(">>=", TokenCategories.PUNCTUATOR, JSTokenTypes.GREATER_GREATER_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testGreaterGreaterGreaterEqual
	 */
	public void testGreaterGreaterGreaterEqual()
	{
		this.lexemeTest(">>>=", TokenCategories.PUNCTUATOR, JSTokenTypes.GREATER_GREATER_GREATER_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testLess
	 */
	public void testLess()
	{
		this.lexemeTest("<", TokenCategories.PUNCTUATOR, JSTokenTypes.LESS); //$NON-NLS-1$
	}

	/**
	 * testLessLess
	 */
	public void testLessLess()
	{
		this.lexemeTest("<<", TokenCategories.PUNCTUATOR, JSTokenTypes.LESS_LESS); //$NON-NLS-1$
	}

	/**
	 * testLessEqual
	 */
	public void testLessEqual()
	{
		this.lexemeTest("<=", TokenCategories.PUNCTUATOR, JSTokenTypes.LESS_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testLessLessEqual
	 */
	public void testLessLessEqual()
	{
		this.lexemeTest("<<=", TokenCategories.PUNCTUATOR, JSTokenTypes.LESS_LESS_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testEqual
	 */
	public void testEqual()
	{
		this.lexemeTest("=", TokenCategories.PUNCTUATOR, JSTokenTypes.EQUAL); //$NON-NLS-1$
	}

	/**
	 * testEqualEqual
	 */
	public void testEqualEqual()
	{
		this.lexemeTest("==", TokenCategories.PUNCTUATOR, JSTokenTypes.EQUAL_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testEqualEqualEqual
	 */
	public void testEqualEqualEqual()
	{
		this.lexemeTest("===", TokenCategories.PUNCTUATOR, JSTokenTypes.EQUAL_EQUAL_EQUAL); //$NON-NLS-1$
	}
}
