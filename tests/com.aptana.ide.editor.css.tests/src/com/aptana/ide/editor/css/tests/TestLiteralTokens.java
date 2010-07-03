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
public class TestLiteralTokens extends TestTokenBase
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
	 * testDoubleQuotedString
	 */
	public void testDoubleQuotedString()
	{
		this.lexemeTest("\"this is a string\"", TokenCategories.LITERAL, CSSTokenTypes.STRING); //$NON-NLS-1$
	}
	
	/**
	 * testSingleQuotedString
	 */
	public void testSingleQuotedString()
	{
		this.lexemeTest("'this is a string'", TokenCategories.LITERAL, CSSTokenTypes.STRING); //$NON-NLS-1$
	}
	
	/**
	 * testUnclosedString
	 */
	public void testUnclosedString()
	{
		this.lexemeTest("'this is an unclosed string", TokenCategories.ERROR, CSSTokenTypes.STRING); //$NON-NLS-1$
	}
	
	/**
	 * testSingleQuotedString
	 */
	public void testNumber()
	{
		this.lexemeTest("10", TokenCategories.LITERAL, CSSTokenTypes.NUMBER); //$NON-NLS-1$
	}
	
	/**
	 * testAtKeyword
	 */
	public void testAtKeyword()
	{
		this.lexemeTest("@keyword", TokenCategories.LITERAL, CSSTokenTypes.AT_KEYWORD); //$NON-NLS-1$
	}
	
	/**
	 * testClass
	 */
	public void testClass()
	{
		this.lexemeTest(".class", TokenCategories.LITERAL, CSSTokenTypes.CLASS); //$NON-NLS-1$
	}
	
	/**
	 * testClass
	 */
	public void testClassWithDashes()
	{
		this.lexemeTest(".class-with-dashes", TokenCategories.LITERAL, CSSTokenTypes.CLASS); //$NON-NLS-1$
	}
	
	/**
	 * testHash
	 */
	public void testHash()
	{
		this.lexemeTest("#hash", TokenCategories.LITERAL, CSSTokenTypes.HASH); //$NON-NLS-1$
	}
	
	/**
	 * testHashLikeRGB
	 */
	public void testHashLikeRGB1()
	{
		this.lexemeTest("#a", TokenCategories.LITERAL, CSSTokenTypes.HASH); //$NON-NLS-1$
	}
	
	/**
	 * testHashLikeRGB2
	 */
	public void testHashLikeRGB2()
	{
		this.lexemeTest("#ab", TokenCategories.LITERAL, CSSTokenTypes.HASH); //$NON-NLS-1$
	}

	/**
	 * testHashLikeRGB3
	 */
	public void testHashLikeRGB3()
	{
		this.lexemeTest("#abcd", TokenCategories.LITERAL, CSSTokenTypes.HASH); //$NON-NLS-1$
	}
	
	/**
	 * testHashLikeRGB4
	 */
	public void testHashLikeRGB4()
	{
		this.lexemeTest("#abcde", TokenCategories.LITERAL, CSSTokenTypes.HASH); //$NON-NLS-1$
	}
	
	/**
	 * testHashLikeRGB5
	 */
	public void testHashLikeRGB5()
	{
		this.lexemeTest("#abcdefa", TokenCategories.LITERAL, CSSTokenTypes.HASH); //$NON-NLS-1$
	}
	
	/**
	 * testHashLikeRGB6
	 */
	public void testHashLikeRGB6()
	{
		this.lexemeTest("#abcx", TokenCategories.LITERAL, CSSTokenTypes.HASH); //$NON-NLS-1$
	}
	
	/**
	 * testHashLikeRGB7
	 */
	public void testHashLikeRGB7()
	{
		this.lexemeTest("#abc-", TokenCategories.LITERAL, CSSTokenTypes.HASH); //$NON-NLS-1$
	}
	
	/**
	 * testFunction
	 */
	public void testFunction()
	{
		this.lexemeTest("function(", TokenCategories.LITERAL, CSSTokenTypes.FUNCTION); //$NON-NLS-1$
	}
	
	/**
	 * testDimension
	 */
	public void testDimension()
	{
		this.lexemeTest("10units", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testPercentage
	 */
	public void testPercentage()
	{
		this.lexemeTest("10%", TokenCategories.LITERAL, CSSTokenTypes.PERCENTAGE); //$NON-NLS-1$
	}
	
	/**
	 * testEms
	 */
	public void testEms()
	{
		this.lexemeTest("10em", TokenCategories.LITERAL, CSSTokenTypes.EMS); //$NON-NLS-1$
		this.lexemeTest("10ems", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testExs
	 */
	public void testExs()
	{
		this.lexemeTest("10ex", TokenCategories.LITERAL, CSSTokenTypes.EXS); //$NON-NLS-1$
		this.lexemeTest("10exs", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testPixels
	 */
	public void testPixels()
	{
		this.lexemeTest("10px", TokenCategories.LITERAL, CSSTokenTypes.LENGTH); //$NON-NLS-1$
		this.lexemeTest("10pxs", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testCentimeters
	 */
	public void testCentimeters()
	{
		this.lexemeTest("10cm", TokenCategories.LITERAL, CSSTokenTypes.LENGTH); //$NON-NLS-1$
		this.lexemeTest("10cms", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testMillimeters
	 */
	public void testMillimeters()
	{
		this.lexemeTest("10mm", TokenCategories.LITERAL, CSSTokenTypes.LENGTH); //$NON-NLS-1$
		this.lexemeTest("10mms", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testInches
	 */
	public void testInches()
	{
		this.lexemeTest("10in", TokenCategories.LITERAL, CSSTokenTypes.LENGTH); //$NON-NLS-1$
		this.lexemeTest("10ins", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testPoints
	 */
	public void testPoints()
	{
		this.lexemeTest("10pt", TokenCategories.LITERAL, CSSTokenTypes.LENGTH); //$NON-NLS-1$
		this.lexemeTest("10pts", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testPicas
	 */
	public void testPicas()
	{
		this.lexemeTest("10pc", TokenCategories.LITERAL, CSSTokenTypes.LENGTH); //$NON-NLS-1$
		this.lexemeTest("10pcs", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testDegrees
	 */
	public void testDegrees()
	{
		this.lexemeTest("10deg", TokenCategories.LITERAL, CSSTokenTypes.ANGLE); //$NON-NLS-1$
		this.lexemeTest("10degs", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testRads
	 */
	public void testRads()
	{
		this.lexemeTest("10rad", TokenCategories.LITERAL, CSSTokenTypes.ANGLE); //$NON-NLS-1$
		this.lexemeTest("10rads", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testGrads
	 */
	public void testGrads()
	{
		this.lexemeTest("10grad", TokenCategories.LITERAL, CSSTokenTypes.ANGLE); //$NON-NLS-1$
		this.lexemeTest("10grads", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testMilliseconds
	 */
	public void testMilliseconds()
	{
		this.lexemeTest("10ms", TokenCategories.LITERAL, CSSTokenTypes.TIME); //$NON-NLS-1$
		this.lexemeTest("10mss", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testSeconds
	 */
	public void testSeconds()
	{
		this.lexemeTest("10s", TokenCategories.LITERAL, CSSTokenTypes.TIME); //$NON-NLS-1$
		this.lexemeTest("10ss", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testHertz
	 */
	public void testHertz()
	{
		this.lexemeTest("10Hz", TokenCategories.LITERAL, CSSTokenTypes.FREQUENCY); //$NON-NLS-1$
		this.lexemeTest("10Hzs", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testKiloHertz
	 */
	public void testKiloHertz()
	{
		this.lexemeTest("10kHz", TokenCategories.LITERAL, CSSTokenTypes.FREQUENCY); //$NON-NLS-1$
		this.lexemeTest("10kHzs", TokenCategories.LITERAL, CSSTokenTypes.DIMENSION); //$NON-NLS-1$
	}
	
	/**
	 * testUnicodeLetter
	 */
	public void testUnicodeLetter()
	{
		this.lexemeTest("U+0A", TokenCategories.LITERAL, CSSTokenTypes.UNICODE_RANGE); //$NON-NLS-1$
	}
	
	/**
	 * testUnicodeRange
	 */
	public void testUnicodeRange()
	{
		this.lexemeTest("U+0A-FF", TokenCategories.LITERAL, CSSTokenTypes.UNICODE_RANGE); //$NON-NLS-1$
	}
	
	/**
	 * testColor
	 */
	public void testColor()
	{
		this.lexemeTest("#808080", TokenCategories.LITERAL, CSSTokenTypes.COLOR); //$NON-NLS-1$
	}
}
