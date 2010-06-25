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
package com.aptana.ide.lexer.matchers;

import junit.framework.TestCase;

import com.aptana.ide.lexer.matcher.AbstractTextMatcher;
import com.aptana.ide.lexer.matcher.ITextMatcher;
import com.aptana.ide.lexer.matcher.MatcherMap;

/**
 * @author Kevin Lindsey
 *
 */
public class MatcherMapTest extends TestCase
{
	private MatcherMap _map;
	private ITextMatcher _matcher1;
	private ITextMatcher _matcher2;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this._map = new MatcherMap();
		this._matcher1 = new AbstractTextMatcher()
		{
			/**
			 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#match(char[], int, int)
			 */
			public int match(char[] source, int offset, int eofset)
			{
				return 0;
			}

			/**
			 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addChildTypes()
			 */
			public void addChildTypes()
			{
			}
		};
		this._matcher2 = new AbstractTextMatcher()
		{
			/**
			 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#match(char[], int, int)
			 */
			public int match(char[] source, int offset, int eofset)
			{
				return 0;
			}

			/**
			 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addChildTypes()
			 */
			public void addChildTypes()
			{
			}
		};
	}

	/**
	 * compareCounts
	 *
	 * @param digitCount
	 * @param letterCount
	 * @param whitespaceCount
	 * @param punctuationCount
	 */
	private void compareCounts(int digitCount, int letterCount, int whitespaceCount, int punctuationCount)
	{
		this._map.setSealed();
		
		assertEquals(digitCount, this._map.getMatchers('0').length);
		assertEquals(letterCount, this._map.getMatchers('a').length);
		assertEquals(whitespaceCount, this._map.getMatchers(' ').length);
		assertEquals(punctuationCount, this._map.getMatchers('!').length);
	}
	
	/**
	 * testDigitMatcher
	 */
	public void testDigitMatcher()
	{
		this._map.addDigitMatcher(this._matcher1);
		
		this.compareCounts(1, 0, 0, 0);
	}
	
	/**
	 * testLetterMatcher
	 */
	public void testLetterMatcher()
	{
		this._map.addLetterMatcher(this._matcher1);
		
		this.compareCounts(0, 1, 0, 0);
	}
	
	/**
	 * testWhitespaceMatcher
	 */
	public void testWhitespaceMatcher()
	{
		this._map.addWhitespaceMatcher(this._matcher1);
		
		this.compareCounts(0, 0, 1, 0);
	}
	
	/**
	 * testNegatedDigitMatcher
	 */
	public void testNegatedDigitMatcher()
	{
		this._map.addNegatedDigitMatcher(this._matcher1);
		
		this.compareCounts(0, 1, 1, 1);
	}
	
	/**
	 * testNegatedLetterMatcher
	 */
	public void testNegatedLetterMatcher()
	{
		this._map.addNegatedLetterMatcher(this._matcher1);
		
		this.compareCounts(1, 0, 1, 1);
	}
	
	/**
	 * testNegatedWhitespaceMatcher
	 */
	public void testNegatedWhitespaceMatcher()
	{
		this._map.addNegatedWhitespaceMatcher(this._matcher1);
		
		this.compareCounts(1, 1, 0, 1);
	}
	
	/**
	 * testCharacterMatcher
	 */
	public void testCharacterMatcher()
	{
		this._map.addCharacterMatcher('a', this._matcher1);
		
		this.compareCounts(0, 1, 0, 0);
	}
	
	/**
	 * testUncategorizedMatcher
	 */
	public void testUncategorizedMatcher()
	{
		this._map.addUncategorizedMatcher(this._matcher1);
		
		this.compareCounts(1, 1, 1, 1);
	}
	
	/**
	 * testUncategorizedAndDigitMatcher
	 */
	public void testUncategorizedAndDigitMatcher()
	{
		this._map.addUncategorizedMatcher(this._matcher1);
		this._map.addDigitMatcher(this._matcher2);
		
		this.compareCounts(2, 1, 1, 1);
	}
	
	/**
	 * testUncategorizedAndLetterMatcher
	 */
	public void testUncategorizedAndLetterMatcher()
	{
		this._map.addUncategorizedMatcher(this._matcher1);
		this._map.addLetterMatcher(this._matcher2);
		
		this.compareCounts(1, 2, 1, 1);
	}
	
	/**
	 * testUncategorizedAndWhitespaceMatcher
	 */
	public void testUncategorizedAndWhitespaceMatcher()
	{
		this._map.addUncategorizedMatcher(this._matcher1);
		this._map.addWhitespaceMatcher(this._matcher2);
		
		this.compareCounts(1, 1, 2, 1);
	}
	
	/**
	 * testUncategorizedAndNegatedDigitMatcher
	 */
	public void testUncategorizedAndNegatedDigitMatcher()
	{
		this._map.addUncategorizedMatcher(this._matcher1);
		this._map.addNegatedDigitMatcher(this._matcher2);
		
		this.compareCounts(1, 2, 2, 2);
	}
	
	/**
	 * testUncategorizedAndNegatedLetterMatcher
	 */
	public void testUncategorizedAndNegatedLetterMatcher()
	{
		this._map.addUncategorizedMatcher(this._matcher1);
		this._map.addNegatedLetterMatcher(this._matcher2);
		
		this.compareCounts(2, 1, 2, 2);
	}
	
	/**
	 * testUncategorizedAndNegatedWhitespaceMatcher
	 */
	public void testUncategorizedAndNegatedWhitespaceMatcher()
	{
		this._map.addUncategorizedMatcher(this._matcher1);
		this._map.addNegatedWhitespaceMatcher(this._matcher2);
		
		this.compareCounts(2, 2, 1, 2);
	}
	
	/**
	 * testUncategorizedAndCharacterMatcher
	 */
	public void testUncategorizedAndCharacterMatcher()
	{
		this._map.addUncategorizedMatcher(this._matcher1);
		this._map.addCharacterMatcher('a', this._matcher2);
		
		this.compareCounts(1, 2, 1, 1);
	}
}
