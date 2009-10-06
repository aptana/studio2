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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.lexer.matchers;

import junit.framework.TestCase;

import com.aptana.ide.lexer.matcher.RegexMatcher;

/**
 * @author Kevin Lindsey
 */
public class RegexMatcherTest extends TestCase
{
	private RegexMatcher _matcher;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this._matcher = new RegexMatcher();
	}

	/**
	 * testNothing
	 */
	public void testNothing()
	{
		char[] source = "".toCharArray();
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(-1, result);
	}
	
	/**
	 * testAnd
	 */
	public void testAnd()
	{
		char[] source1 = "test".toCharArray();
		
		this._matcher.appendText("test");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
	}
	
	/**
	 * testBeginning
	 */
	public void testBeginning()
	{
		char[] source1 = "test".toCharArray();
		char[] source2 = "atest".toCharArray();
		
		this._matcher.appendText("^test");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 1, source2.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testEnding
	 */
	public void testEnding()
	{
		char[] source1 = "test".toCharArray();
		char[] source2 = "testing".toCharArray();
		
		this._matcher.appendText("test$");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testBeginningAndEnding
	 */
	public void testBeginningAndEnding()
	{
		char[] source1 = "test".toCharArray();
		char[] source2 = "tests".toCharArray();
		char[] source3 = "atest".toCharArray();
		
		this._matcher.appendText("^test$");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testOr
	 */
	public void testOr()
	{
		char[] source1 = "abc".toCharArray();
		char[] source2 = "def".toCharArray();
		
		this._matcher.appendText("abc|def");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
	}
	
	/**
	 * testOption
	 */
	public void testOption()
	{
		char[] source1 = "abc".toCharArray();
		char[] source2 = "abcd".toCharArray();
		
		this._matcher.appendText("abcd?");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
	}
	
	/**
	 * testPositiveClosure
	 */
	public void testPositiveClosure()
	{
		char[] source1 = "ba".toCharArray();
		char[] source2 = "a".toCharArray();
		char[] source3 = "aa".toCharArray();
		
		this._matcher.appendText("a+");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
	}
	
	/**
	 * testKleeneClosure
	 */
	public void testKleeneClosure()
	{
		char[] source1 = "".toCharArray();
		char[] source2 = "a".toCharArray();
		char[] source3 = "aa".toCharArray();
		
		this._matcher.appendText("a*");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
	}
	
	/**
	 * testRepeat
	 */
	public void testRepeat()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "aa".toCharArray();
		char[] source3 = "aaa".toCharArray();
		
		this._matcher.appendText("a{2}");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(2, result);
	}
	
	/**
	 * testRepeatLowerBound
	 */
	public void testRepeatLowerBound()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "aa".toCharArray();
		char[] source3 = "aaa".toCharArray();
		
		this._matcher.appendText("a{2,}");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
	}
	
	/**
	 * testRepeatUpperBound
	 */
	public void testRepeatUpperBound()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "aa".toCharArray();
		char[] source3 = "aaa".toCharArray();
		
		this._matcher.appendText("a{,2}");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(2, result);
	}
	
	/**
	 * testRepeatRange
	 */
	public void testRepeatRange()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "aa".toCharArray();
		char[] source3 = "aaa".toCharArray();
		char[] source4 = "aaaa".toCharArray();
		
		this._matcher.appendText("a{2,3}");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(3, result);
	}
	
	/**
	 * testGroup
	 */
	public void testGroup()
	{
		char[] source1 = "ac".toCharArray();
		char[] source2 = "ad".toCharArray();
		char[] source3 = "bc".toCharArray();
		char[] source4 = "bd".toCharArray();
		char[] source5 = "ab".toCharArray();
		
		this._matcher.appendText("(a|b)(c|d)");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(source4.length, result);
		
		result = this._matcher.match(source5, 0, source5.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testGroup2
	 */
	public void testGroup2()
	{
		char[] source1 = "ac".toCharArray();
		char[] source2 = "ad".toCharArray();
		char[] source3 = "bc".toCharArray();
		char[] source4 = "bd".toCharArray();
		char[] source5 = "ab".toCharArray();
		
		this._matcher.appendText("(?:a|b)(?:c|d)");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(source4.length, result);
		
		result = this._matcher.match(source5, 0, source5.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testPositiveLookahead
	 */
	public void testPositiveLookahead()
	{
		char[] source1 = "ab".toCharArray();
		char[] source2 = "ac".toCharArray();
		
		this._matcher.appendText("a(?=b)");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testNegativeLookahead
	 */
	public void testNegativeLookahead()
	{
		char[] source1 = "ab".toCharArray();
		char[] source2 = "ac".toCharArray();
		
		this._matcher.appendText("a(?!b)");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(1, result);
	}
	
	/**
	 * testDot
	 */
	public void testDot()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "\r".toCharArray();
		char[] source3 = "\n".toCharArray();
		
		this._matcher.appendText(".");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testEmptyCharacterClass
	 */
	public void testEmptyCharacterClass()
	{
		char[] source1 = "".toCharArray();
		char[] source2 = "a".toCharArray();
		
		this._matcher.appendText("[]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testNegatedEmptyCharacterClass
	 */
	public void testNegatedEmptyCharacterClass()
	{
		char[] source1 = "".toCharArray();
		char[] source2 = "a".toCharArray();
		
		this._matcher.appendText("[^]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
	}
	
	/**
	 * testMinusOnlyCharacterClass
	 */
	public void testMinusOnlyCharacterClass()
	{
		char[] source = "-".toCharArray();
		
		this._matcher.appendText("[-]");
		
		int result = this._matcher.match(source, 0, source.length);
		assertEquals(source.length, result);
	}
	
	/**
	 * testNegatedMinusOnlyCharacterClass
	 */
	public void testNegatedMinusOnlyCharacterClass()
	{
		char[] source1 = "-".toCharArray();
		char[] source2 = "a".toCharArray();
		
		this._matcher.appendText("[^-]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
	}

	/**
	 * testSimpleCharacterClass
	 */
	public void testSimpleCharacterClass()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "b".toCharArray();
		char[] source3 = "c".toCharArray();
		char[] source4 = "d".toCharArray();
		
		this._matcher.appendText("[abc]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(-1, result);
	}

	/**
	 * testSimpleRangeCharacterClass
	 */
	public void testSimpleRangeCharacterClass()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "b".toCharArray();
		char[] source3 = "c".toCharArray();
		char[] source4 = "d".toCharArray();
		
		this._matcher.appendText("[a-c]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(-1, result);
	}

	/**
	 * testNegatedSimpleCharacterClass
	 */
	public void testNegatedSimpleCharacterClass()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "b".toCharArray();
		char[] source3 = "c".toCharArray();
		char[] source4 = "d".toCharArray();
		
		this._matcher.appendText("[^abc]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(source4.length, result);
	}

	/**
	 * testNegatedSimpleRangeCharacterClass
	 */
	public void testNegatedSimpleRangeCharacterClass()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "b".toCharArray();
		char[] source3 = "c".toCharArray();
		char[] source4 = "d".toCharArray();
		
		this._matcher.appendText("[^a-c]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(source4.length, result);
	}

	/**
	 * testMinusCharacterClass
	 */
	public void testMinusCharacterClass()
	{
		char[] source1 = "-".toCharArray();
		char[] source2 = "a".toCharArray();
		char[] source3 = "b".toCharArray();
		char[] source4 = "c".toCharArray();
		char[] source5 = "d".toCharArray();
		
		this._matcher.appendText("[-abc]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(source4.length, result);
		
		result = this._matcher.match(source5, 0, source5.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testMinusRangeCharacterClass
	 */
	public void testMinusRangeCharacterClass()
	{
		char[] source1 = "-".toCharArray();
		char[] source2 = "a".toCharArray();
		char[] source3 = "b".toCharArray();
		char[] source4 = "c".toCharArray();
		char[] source5 = "d".toCharArray();
		
		this._matcher.appendText("[-a-c]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(source4.length, result);
		
		result = this._matcher.match(source5, 0, source5.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testNegatedMinusCharacterClass
	 */
	public void testNegatedMinusCharacterClass()
	{
		char[] source1 = "-".toCharArray();
		char[] source2 = "a".toCharArray();
		char[] source3 = "b".toCharArray();
		char[] source4 = "c".toCharArray();
		char[] source5 = "d".toCharArray();
		
		this._matcher.appendText("[^-abc]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source5, 0, source5.length);
		assertEquals(source5.length, result);
	}
	
	/**
	 * testNegatedMinusRangeCharacterClass
	 */
	public void testNegatedMinusRangeCharacterClass()
	{
		char[] source1 = "-".toCharArray();
		char[] source2 = "a".toCharArray();
		char[] source3 = "b".toCharArray();
		char[] source4 = "c".toCharArray();
		char[] source5 = "d".toCharArray();
		
		this._matcher.appendText("[^-a-c]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source5, 0, source5.length);
		assertEquals(source5.length, result);
	}
	
	/**
	 * testEscapedCharacters
	 */
	public void testEscapedCharacters()
	{
		char[] source1 = "eclipse[293]".toCharArray();
		
		this._matcher.appendText("eclipse\\[\\d+\\]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
	}
	
	/**
	 * testDigitCharacterClass
	 */
	public void testDigitCharacterClass()
	{
		char[] source1 = "1234567890".toCharArray();
		char[] source2 = "abc123".toCharArray();
		
		this._matcher.appendText("\\d+");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testNegatedDigitCharacterClass
	 */
	public void testNegatedDigitCharacterClass()
	{
		char[] source1 = "1234567890".toCharArray();
		char[] source2 = "abc123".toCharArray();
		
		this._matcher.appendText("\\D+");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length - 3, result);
	}
	
	/**
	 * testFormFeedCharacter
	 */
	public void testFormFeedCharacter()
	{
		char[] source1 = "\f".toCharArray();
		char[] source2 = "\t".toCharArray();
		
		this._matcher.appendText("\\f");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testNewlineCharacter
	 */
	public void testNewlineCharacter()
	{
		char[] source1 = "\n".toCharArray();
		char[] source2 = "\t".toCharArray();
		
		this._matcher.appendText("\\n");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testReturnCharacter
	 */
	public void testReturnCharacter()
	{
		char[] source1 = "\r".toCharArray();
		char[] source2 = "\t".toCharArray();
		
		this._matcher.appendText("\\r");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testWhitespaceCharacterClass
	 */
	public void testWhitespaceCharacterClass()
	{
		char[] source1 = " \t\r\n\f\u000B".toCharArray();
		char[] source2 = "abc".toCharArray();
		
		this._matcher.appendText("\\s+");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
	}

	/**
	 * testNegatedWhitespaceCharacterClass
	 */
	public void testNegatedWhitespaceCharacterClass()
	{
		char[] source1 = " \t\r\n\f\u000B".toCharArray();
		char[] source2 = "abc".toCharArray();
		
		this._matcher.appendText("\\S+");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
	}
	
	/**
	 * testTabCharacter
	 */
	public void testTabCharacter()
	{
		char[] source1 = "\t".toCharArray();
		char[] source2 = "\n".toCharArray();
		
		this._matcher.appendText("\\t");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testVerticalTabCharacter
	 */
	public void testVerticalTabCharacter()
	{
		char[] source1 = "\u000B".toCharArray();
		char[] source2 = "\t".toCharArray();
		
		this._matcher.appendText("\\v");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testSpecialOutsideCharacterClass
	 */
	public void testSpecialOutsideCharacterClass()
	{
		char[] source1 = ",-".toCharArray();
		
		this._matcher.appendText(",-");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
	}
	
	/**
	 * testSpecialInsideCharacterClass
	 */
	public void testSpecialInsideCharacterClass()
	{
		char[] source1 = ",".toCharArray();
		char[] source2 = "$".toCharArray();
		char[] source3 = ".".toCharArray();
		char[] source4 = "|".toCharArray();
		char[] source5 = "*".toCharArray();
		char[] source6 = "?".toCharArray();
		
		this._matcher.appendText("[,$.|*?]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(source4.length, result);
		
		result = this._matcher.match(source5, 0, source5.length);
		assertEquals(source5.length, result);
		
		result = this._matcher.match(source6, 0, source6.length);
		assertEquals(source6.length, result);
	}
	
	/**
	 * testWordCharacterClass
	 */
	public void testWordCharacterClass()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "0".toCharArray();
		char[] source3 = "_".toCharArray();
		char[] source4 = "#".toCharArray();
		
		this._matcher.appendText("\\w");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testNegatedWordCharacterClass
	 */
	public void testNegatedWordCharacterClass()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "0".toCharArray();
		char[] source3 = "_".toCharArray();
		char[] source4 = "#".toCharArray();
		
		this._matcher.appendText("\\W");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(source4.length, result);
	}
	
	/**
	 * testStartOfFile
	 */
	public void testStartOfFile()
	{
		char[] source1 = "abc".toCharArray();
		
		this._matcher.appendText("\\A");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(0, result);
		
		result = this._matcher.match(source1, 1, source1.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testEndOfFile
	 */
	public void testEndOfFile()
	{
		char[] source1 = "abc".toCharArray();
		
		this._matcher.appendText("\\z");
		
		int result = this._matcher.match(source1, source1.length, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testWordBoundary
	 */
	public void testWordBoundary()
	{
		char[] source1 = "#ab#".toCharArray();
		
		this._matcher.appendText("\\b");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(0, result);
		
		result = this._matcher.match(source1, 1, source1.length);
		assertEquals(1, result);
		
		result = this._matcher.match(source1, 2, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source1, 3, source1.length);
		assertEquals(3, result);
	}
	
	/**
	 * testNegatedWordBoundary
	 */
	public void testNegatedWordBoundary()
	{
		char[] source1 = "#ab#".toCharArray();
		
		this._matcher.appendText("\\B");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source1, 1, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source1, 2, source1.length);
		assertEquals(2, result);
		
		result = this._matcher.match(source1, 3, source1.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testWhitespaceSignficant
	 */
	public void testWhitespaceSignficant()
	{
		char[] source1 = " abc ".toCharArray();
		char[] source2 = "abc".toCharArray();
		
		this._matcher.appendText(" abc ");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testIgnoreWhitespace
	 */
	public void testIgnoreWhitespace()
	{
		char[] source1 = " abc ".toCharArray();
		char[] source2 = "abc".toCharArray();
		
		this._matcher.setIgnoreWhitespace(true);
		this._matcher.appendText(" abc ");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(-1, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
	}
	
	/**
	 * testCaseInsensitiveCharacter
	 */
	public void testCaseInsensitiveCharacter()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "A".toCharArray();
		char[] source3 = "b".toCharArray();
		
		this._matcher.setCaseInsensitive(true);
		this._matcher.appendText("a");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testCaseInsensitiveCharacterClass
	 */
	public void testCaseInsensitiveCharacterClass()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "A".toCharArray();
		char[] source3 = "c".toCharArray();
		char[] source4 = "C".toCharArray();
		char[] source5 = "b".toCharArray();
		
		this._matcher.setCaseInsensitive(true);
		this._matcher.appendText("[ac]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(source4.length, result);
		
		result = this._matcher.match(source5, 0, source5.length);
		assertEquals(-1, result);
	}
	
	/**
	 * testCaseInsensitiveRangeCharacterClass
	 */
	public void testCaseInsensitiveRangeCharacterClass()
	{
		char[] source1 = "a".toCharArray();
		char[] source2 = "A".toCharArray();
		char[] source3 = "b".toCharArray();
		char[] source4 = "B".toCharArray();
		char[] source5 = "c".toCharArray();
		char[] source6 = "C".toCharArray();
		char[] source7 = "d".toCharArray();
		
		this._matcher.setCaseInsensitive(true);
		this._matcher.appendText("[a-c]");
		
		int result = this._matcher.match(source1, 0, source1.length);
		assertEquals(source1.length, result);
		
		result = this._matcher.match(source2, 0, source2.length);
		assertEquals(source2.length, result);
		
		result = this._matcher.match(source3, 0, source3.length);
		assertEquals(source3.length, result);
		
		result = this._matcher.match(source4, 0, source4.length);
		assertEquals(source4.length, result);
		
		result = this._matcher.match(source5, 0, source5.length);
		assertEquals(source5.length, result);
		
		result = this._matcher.match(source6, 0, source6.length);
		assertEquals(source6.length, result);
		
		result = this._matcher.match(source7, 0, source7.length);
		assertEquals(-1, result);
	}
}
