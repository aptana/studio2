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
package com.aptana.ide.editor.css.tests;

import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editor.css.parsing.CSSParser;
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
		CSSParser parser = new CSSParser();

		return parser.getLexer();
	}
	
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#getLanguage()
	 */
	protected String getLanguage()
	{
		return CSSMimeType.MimeType;
	}
	
	/**
	 * testCDO
	 */
	public void testCDO()
	{
		this.lexemeTest("<!--", TokenCategories.PUNCTUATOR, CSSTokenTypes.CDO); //$NON-NLS-1$
	}

	/**
	 * testCDC
	 */
	public void testCDC()
	{
		this.lexemeTest("-->", TokenCategories.PUNCTUATOR, CSSTokenTypes.CDC); //$NON-NLS-1$
	}

	/**
	 * testColon
	 */
	public void testColon()
	{
		this.lexemeTest(":", TokenCategories.PUNCTUATOR, CSSTokenTypes.COLON); //$NON-NLS-1$
	}

	/**
	 * testSemicolon
	 */
	public void testSemicolon()
	{
		this.lexemeTest(";", TokenCategories.PUNCTUATOR, CSSTokenTypes.SEMICOLON); //$NON-NLS-1$
	}

	/**
	 * testLCurly
	 */
	public void testLCurly()
	{
		this.lexemeTest("{", TokenCategories.PUNCTUATOR, CSSTokenTypes.LCURLY); //$NON-NLS-1$
	}

	/**
	 * testRCurly
	 */
	public void testRCurly()
	{
		this.lexemeTest("}", TokenCategories.PUNCTUATOR, CSSTokenTypes.RCURLY); //$NON-NLS-1$
	}

	/**
	 * testRParen
	 */
	public void testRParen()
	{
		this.lexemeTest(")", TokenCategories.PUNCTUATOR, CSSTokenTypes.RPAREN); //$NON-NLS-1$
	}

	/**
	 * testLBracket
	 */
	public void testLBracket()
	{
		this.lexemeTest("[", TokenCategories.PUNCTUATOR, CSSTokenTypes.LBRACKET); //$NON-NLS-1$
	}

	/**
	 * testRBracket
	 */
	public void testRBracket()
	{
		this.lexemeTest("]", TokenCategories.PUNCTUATOR, CSSTokenTypes.RBRACKET); //$NON-NLS-1$
	}

	/**
	 * testIncludes
	 */
	public void testIncludes()
	{
		this.lexemeTest("~=", TokenCategories.PUNCTUATOR, CSSTokenTypes.INCLUDES); //$NON-NLS-1$
	}

	/**
	 * testDashMatch
	 */
	public void testDashMatch()
	{
		this.lexemeTest("|=", TokenCategories.PUNCTUATOR, CSSTokenTypes.DASHMATCH); //$NON-NLS-1$
	}

	/**
	 * testComma
	 */
	public void testComma()
	{
		this.lexemeTest(",", TokenCategories.PUNCTUATOR, CSSTokenTypes.COMMA); //$NON-NLS-1$
	}

	/**
	 * testPlus
	 */
	public void testPlus()
	{
		this.lexemeTest("+", TokenCategories.PUNCTUATOR, CSSTokenTypes.PLUS); //$NON-NLS-1$
	}

	/**
	 * testMinus
	 */
	public void testMinus()
	{
		this.lexemeTest("-", TokenCategories.PUNCTUATOR, CSSTokenTypes.MINUS); //$NON-NLS-1$
	}

	/**
	 * testStar
	 */
	public void testStar()
	{
		this.lexemeTest("*", TokenCategories.PUNCTUATOR, CSSTokenTypes.STAR); //$NON-NLS-1$
	}

	/**
	 * testCaretEqual
	 */
	public void testCaretEqual()
	{
		this.lexemeTest("^=", TokenCategories.PUNCTUATOR, CSSTokenTypes.CARET_EQUAL); //$NON-NLS-1$
	}

	/**
	 * testGreater
	 */
	public void testGreater()
	{
		this.lexemeTest(">", TokenCategories.PUNCTUATOR, CSSTokenTypes.GREATER); //$NON-NLS-1$
	}

	/**
	 * testForwardSlash
	 */
	public void testForwardSlash()
	{
		this.lexemeTest("/", TokenCategories.PUNCTUATOR, CSSTokenTypes.FORWARD_SLASH); //$NON-NLS-1$
	}

	/**
	 * testEqual
	 */
	public void testEqual()
	{
		this.lexemeTest("=", TokenCategories.PUNCTUATOR, CSSTokenTypes.EQUAL); //$NON-NLS-1$
	}
}
