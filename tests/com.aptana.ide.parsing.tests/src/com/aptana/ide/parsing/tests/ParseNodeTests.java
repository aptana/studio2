/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.parsing.tests;

import junit.framework.TestCase;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.Token;
import com.aptana.ide.parsing.nodes.ParseNodeBase;

/**
 * @author Kevin Lindsey
 */
public class ParseNodeTests extends TestCase
{
	private static final String LANGUAGE = "test/text";
	private static final Token TOKEN;

	/**
	 * static constructor
	 */
	static
	{
		TOKEN = new Token(null);
		TOKEN.setTypeIndex(1);
	}
	
	/**
	 * testRangeAfterInstantiation
	 */
	public void testRangeAfterInstantiation()
	{
		Lexeme lexeme = new Lexeme(TOKEN, "abc", 0);
		ParseNodeBase node = new ParseNodeBase(1, LANGUAGE, lexeme);
		
		assertEquals(0, node.getStartingOffset());
		assertEquals(3, node.getEndingOffset());
		assertSame(lexeme, node.getStartingLexeme());
		assertSame(lexeme, node.getEndingLexeme());
	}
	
	/**
	 * testRangeChildEndingOffsetIsGreater
	 */
	public void testRangeChildEndingOffsetIsGreater()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 0);
		Lexeme childLexeme = new Lexeme(TOKEN, "def", 4);
		
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		ParseNodeBase childNode = new ParseNodeBase(1, LANGUAGE, childLexeme);
		
		parentNode.appendChild(childNode);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(7, parentNode.getEndingOffset());
		assertSame(parentLexeme, parentNode.getStartingLexeme());
		assertSame(childLexeme, parentNode.getEndingLexeme());
	}
	
	/**
	 * testRangeChildStartingOffsetIsLess
	 */
	public void testRangeChildStartingOffsetIsLess()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 4);
		Lexeme childLexeme = new Lexeme(TOKEN, "def", 0);
		
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		ParseNodeBase childNode = new ParseNodeBase(1, LANGUAGE, childLexeme);
		
		parentNode.appendChild(childNode);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(7, parentNode.getEndingOffset());
		assertSame(childLexeme, parentNode.getStartingLexeme());
		assertSame(parentLexeme, parentNode.getEndingLexeme());
	}
	
	/**
	 * testRangeDescendantOffsetsAreGreaterBottomUp
	 */
	public void testRangeDescendantOffsetsAreGreaterBottomUp()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 0);
		Lexeme childLexeme = new Lexeme(TOKEN, "def", 4);
		Lexeme grandchildLexeme = new Lexeme(TOKEN, "ghi", 8);
		
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		ParseNodeBase childNode = new ParseNodeBase(1, LANGUAGE, childLexeme);
		ParseNodeBase grandchildNode = new ParseNodeBase(1, LANGUAGE, grandchildLexeme);
		
		childNode.appendChild(grandchildNode);
		parentNode.appendChild(childNode);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(11, parentNode.getEndingOffset());
		assertSame(parentLexeme, parentNode.getStartingLexeme());
		assertSame(grandchildLexeme, parentNode.getEndingLexeme());
	}
	
	/**
	 * testRangeDescendantOffsetsAreLessBottomUp
	 */
	public void testRangeDescendantOffsetsAreLessBottomUp()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 8);
		Lexeme childLexeme = new Lexeme(TOKEN, "def", 4);
		Lexeme grandchildLexeme = new Lexeme(TOKEN, "ghi", 0);
		
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		ParseNodeBase childNode = new ParseNodeBase(1, LANGUAGE, childLexeme);
		ParseNodeBase grandchildNode = new ParseNodeBase(1, LANGUAGE, grandchildLexeme);
		
		childNode.appendChild(grandchildNode);
		parentNode.appendChild(childNode);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(11, parentNode.getEndingOffset());
		assertSame(grandchildLexeme, parentNode.getStartingLexeme());
		assertSame(parentLexeme, parentNode.getEndingLexeme());
	}
	
	/**
	 * testRangeDescendantOffsetsAreGreaterTopDown
	 */
	public void testRangeDescendantOffsetsAreGreaterTopDown()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 0);
		Lexeme childLexeme = new Lexeme(TOKEN, "def", 4);
		Lexeme grandchildLexeme = new Lexeme(TOKEN, "ghi", 8);
		
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		ParseNodeBase childNode = new ParseNodeBase(1, LANGUAGE, childLexeme);
		ParseNodeBase grandchildNode = new ParseNodeBase(1, LANGUAGE, grandchildLexeme);
		
		parentNode.appendChild(childNode);
		childNode.appendChild(grandchildNode);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(11, parentNode.getEndingOffset());
		assertSame(parentLexeme, parentNode.getStartingLexeme());
		assertSame(grandchildLexeme, parentNode.getEndingLexeme());
	}
	
	/**
	 * testRangeDescendantOffsetsAreLessTopDown
	 */
	public void testRangeDescendantOffsetsAreLessTopDown()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 8);
		Lexeme childLexeme = new Lexeme(TOKEN, "def", 4);
		Lexeme grandchildLexeme = new Lexeme(TOKEN, "ghi", 0);
		
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		ParseNodeBase childNode = new ParseNodeBase(1, LANGUAGE, childLexeme);
		ParseNodeBase grandchildNode = new ParseNodeBase(1, LANGUAGE, grandchildLexeme);
		
		parentNode.appendChild(childNode);
		childNode.appendChild(grandchildNode);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(11, parentNode.getEndingOffset());
		assertSame(grandchildLexeme, parentNode.getStartingLexeme());
		assertSame(parentLexeme, parentNode.getEndingLexeme());
	}
	
	/**
	 * testIncludeLexemeBeforeRange
	 */
	public void testIncludeLexemeBeforeRange()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 4);
		Lexeme childLexeme = new Lexeme(TOKEN, "def", 0);
		
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		assertEquals(4, parentNode.getStartingOffset());
		assertEquals(7, parentNode.getEndingOffset());
		
		parentNode.includeLexemeInRange(childLexeme);
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(7, parentNode.getEndingOffset());
	}
	
	/**
	 * testIncludeLexemeAfterRange
	 */
	public void testIncludeLexemeAfterRange()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 0);
		Lexeme childLexeme = new Lexeme(TOKEN, "def", 4);
		
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(3, parentNode.getEndingOffset());
		
		parentNode.includeLexemeInRange(childLexeme);
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(7, parentNode.getEndingOffset());
	}
	
	/**
	 * testIncludeLexemeAfterRange
	 */
	public void testIncludeLexemeInRange()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 0);
		Lexeme childLexeme = new Lexeme(TOKEN, "def", 4);
		Lexeme grandchildLexeme = new Lexeme(TOKEN, "ghi", 8);
		
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		ParseNodeBase grandchildNode = new ParseNodeBase(1, LANGUAGE, grandchildLexeme);
		parentNode.appendChild(grandchildNode);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(11, parentNode.getEndingOffset());
		
		parentNode.includeLexemeInRange(childLexeme);
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(11, parentNode.getEndingOffset());
	}
	
	/**
	 * testIncludeNullLexeme
	 */
	public void testIncludeNullLexeme()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 0);
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(3, parentNode.getEndingOffset());
		
		parentNode.includeLexemeInRange(null);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(3, parentNode.getEndingOffset());
	}
	
	/**
	 * testIncludeNullStartingLexeme
	 */
	public void testIncludeNullStartingLexeme()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 0);
		Lexeme childLexeme = new Lexeme(TOKEN, "def", 4);
		
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(3, parentNode.getEndingOffset());
		
		parentNode.includeLexemesInRange(null, childLexeme);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(3, parentNode.getEndingOffset());
	}
	
	/**
	 * testIncludeNullEndingLexeme
	 */
	public void testIncludeNullEndingLexeme()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 0);
		Lexeme childLexeme = new Lexeme(TOKEN, "def", 4);
		
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(3, parentNode.getEndingOffset());
		
		parentNode.includeLexemesInRange(childLexeme, null);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(3, parentNode.getEndingOffset());
	}
	
	/**
	 * testIncludeOutOfOrderLexemes
	 */
	public void testIncludeOutOfOrderLexemes()
	{
		Lexeme parentLexeme = new Lexeme(TOKEN, "abc", 0);
		Lexeme childLexeme = new Lexeme(TOKEN, "def", 4);
		Lexeme grandchildLexeme = new Lexeme(TOKEN, "ghi", 8);
		
		ParseNodeBase parentNode = new ParseNodeBase(1, LANGUAGE, parentLexeme);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(3, parentNode.getEndingOffset());
		
		parentNode.includeLexemesInRange(grandchildLexeme, childLexeme);
		
		assertEquals(0, parentNode.getStartingOffset());
		assertEquals(3, parentNode.getEndingOffset());
	}
}
