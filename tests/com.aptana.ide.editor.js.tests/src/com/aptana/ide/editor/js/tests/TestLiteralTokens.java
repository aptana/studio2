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
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.lexer.tests.TestTokenBase;

/**
 * @author Kevin Lindsey
 */
public class TestLiteralTokens extends TestTokenBase
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
	 * testNullLiteral
	 */
	public void testNullLiteral()
	{
		this.lexemeTest("null", TokenCategories.LITERAL, JSTokenTypes.NULL); //$NON-NLS-1$
		this.lexemeTest("nulls", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testTrueLiteral
	 */
	public void testTrueLiteral()
	{
		this.lexemeTest("true", TokenCategories.LITERAL, JSTokenTypes.TRUE); //$NON-NLS-1$
		this.lexemeTest("trues", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testFalseLiteral
	 */
	public void testFalseLiteral()
	{
		this.lexemeTest("false", TokenCategories.LITERAL, JSTokenTypes.FALSE); //$NON-NLS-1$
		this.lexemeTest("falses", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testInteger
	 */
	public void testInteger()
	{
		this.lexemeTest("1", TokenCategories.LITERAL, JSTokenTypes.NUMBER); //$NON-NLS-1$
	}
	
	/**
	 * testIntegerWithDecimalPoint
	 */
	public void testIntegerWithDecimalPoint()
	{
		this.lexemeTest("1.", TokenCategories.LITERAL, JSTokenTypes.NUMBER); //$NON-NLS-1$
	}
	
	/**
	 * testNegativeInteger
	 */
	public void testNegativeInteger()
	{
		this.lexemeTest("-1", TokenCategories.LITERAL, JSTokenTypes.NUMBER); //$NON-NLS-1$
	}
	
	/**
	 * testNegativeIntegerWithDecimalPoint
	 */
	public void testNegativeIntegerWithDecimalPoint()
	{
		this.lexemeTest("-1.", TokenCategories.LITERAL, JSTokenTypes.NUMBER); //$NON-NLS-1$
	}
	
	/**
	 * testPositiveInteger
	 */
	public void testPositiveInteger()
	{
		this.lexemeTest("+1", TokenCategories.LITERAL, JSTokenTypes.NUMBER); //$NON-NLS-1$
	}
	
	/**
	 * testPositiveIntegerWithDecimalPoint
	 */
	public void testPositiveIntegerWithDecimalPoint()
	{
		this.lexemeTest("+1.", TokenCategories.LITERAL, JSTokenTypes.NUMBER); //$NON-NLS-1$
	}
	
	/**
	 * testHexNumber
	 */
	public void testHexNumber()
	{
		this.lexemeTest("0xabcdef", TokenCategories.LITERAL, JSTokenTypes.NUMBER);	//$NON-NLS-1$
	}
	
	/**
	 * testHexNumber
	 */
	public void testHexNumberError()
	{
		this.lexemeTest("0x", TokenCategories.ERROR, JSTokenTypes.NUMBER);	//$NON-NLS-1$
	}
	
	/**
	 * testFloat
	 */
	public void testFloat()
	{
		this.lexemeTest("1.0", TokenCategories.LITERAL, JSTokenTypes.NUMBER); //$NON-NLS-1$
	}
	
	/**
	 * testExponentialNotation
	 */
	public void testExponentialNotation()
	{
		this.lexemeTest("1.0e6", TokenCategories.LITERAL, JSTokenTypes.NUMBER); //$NON-NLS-1$
	}
	
	/**
	 * testExponentialNotationWithNegativeExponent
	 */
	public void testExponentialNotationWithNegativeExponent()
	{
		this.lexemeTest("1.0e-6", TokenCategories.LITERAL, JSTokenTypes.NUMBER); //$NON-NLS-1$
	}
	
	/**
	 * testExponentialNotationWithPositiveExponent
	 */
	public void testExponentialNotationWithPositiveExponent()
	{
		this.lexemeTest("1.0e+6", TokenCategories.LITERAL, JSTokenTypes.NUMBER); //$NON-NLS-1$
	}

	/**
	 * testEmptyDoubleQuotedString
	 */
	public void testEmptyDoubleQuotedString()
	{
		this.lexemeTest("\"\"", TokenCategories.LITERAL, JSTokenTypes.STRING); //$NON-NLS-1$
	}

	/**
	 * testEmptySingleQuotedString
	 */
	public void testEmptySingleQuotedString()
	{
		this.lexemeTest("''", TokenCategories.LITERAL, JSTokenTypes.STRING); //$NON-NLS-1$
	}

	/**
	 * testSimpleRegex
	 * 
	 * @throws LexerException
	 */
	public void testSimpleRegex() throws LexerException
	{
		this.lexer.setGroup("regex"); //$NON-NLS-1$
		this.lexemeTest("/a/", TokenCategories.LITERAL, JSTokenTypes.REGEX); //$NON-NLS-1$
	}
}
