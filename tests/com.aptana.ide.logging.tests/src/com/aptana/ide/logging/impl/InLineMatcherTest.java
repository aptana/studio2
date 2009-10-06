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
package com.aptana.ide.logging.impl;

import junit.framework.TestCase;

import com.aptana.ide.lexer.IEnumerationMap;
import com.aptana.ide.lexer.TokenList;
import com.aptana.ide.lexer.matcher.RegexMatcher;
import com.aptana.ide.lexer.matcher.StringMatcher;

/**
 * Tests for InLineMatcher.
 * 
 * @author Denis Denisenko
 */
public class InLineMatcherTest extends TestCase
{
	private static final String CATEGORY_NAME = "Category";

	private InLineMatcher _matcher;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		this._matcher = new InLineMatcher();
	}

	@Override
	protected void tearDown() throws Exception
	{
		this._matcher = null;
		super.tearDown();
	}

	/**
	 * Test 1.
	 */
	public void test1()
	{
		TokenList list = createTokenList(new String[] { "aaa", "bbb", "ccc" });
		appendStringMatcher("aaa", list);
		appendStringMatcher("bbb", list);
		appendStringMatcher("ccc", list);
		initMatcher();

		char[] source = "cccddbbbsdfadfsaaa".toCharArray();
		int result = this._matcher.match(source, 0, source.length);

		assertResultType("aaa");
		assertResultIsWholeLine(result, source);
	}

	/**
	 * Test 2.
	 */
	public void test2()
	{
		TokenList list = createTokenList(new String[] { "aaa", "bbb", "ccc" });
		appendStringMatcher("aaa", list);
		appendStringMatcher("bbb", list);
		appendStringMatcher("ccc", list);
		initMatcher();

		char[] source = "cccddbbbsdf\r\nadfsaaa".toCharArray();
		int result = this._matcher.match(source, 0, source.length);

		assertResultType("bbb");
		assertEquals(13, result);
	}

	/**
	 * Test 3.
	 */
	public void test3()
	{
		TokenList list = createTokenList(new String[] { "aaa", "bbb", "ccc" });
		appendStringMatcher("aaa", list);
		appendStringMatcher("bbb", list);
		appendStringMatcher("ccc", list);
		initMatcher();

		char[] source = "ccdcdbgbsdf\r\nadfsaaa".toCharArray();
		int result = this._matcher.match(source, 0, source.length);

		assertEquals(-1, result);
	}

	/**
	 * Test 4.
	 */
	public void test4()
	{
		TokenList list = createTokenList(new String[] { "a.a", "bbb", "ccc" });
		appendRegexMatcher("a.a", list);
		appendStringMatcher("bbb", list);
		appendStringMatcher("ccc", list);
		initMatcher();

		char[] source = "cccDbbbaba\radfsaaa".toCharArray();
		int result = this._matcher.match(source, 0, source.length);

		assertResultType("a.a");
		assertEquals(11, result);
	}

	/**
	 * Appends string matcher with text child.
	 * 
	 * @param text
	 *            - text.
	 * @param list
	 *            - token list.
	 */
	private void appendStringMatcher(String text, TokenList list)
	{
		StringMatcher stringMatcher = new StringMatcher();
		stringMatcher.setType(text);
		stringMatcher.setCategory(CATEGORY_NAME);
		stringMatcher.createTokens(list);
		_matcher.appendChild(stringMatcher);
		stringMatcher.appendText(text);
	}

	/**
	 * Appends regex matcher with text child.
	 * 
	 * @param text
	 *            - text.
	 * @param list
	 *            - token list.
	 */
	private void appendRegexMatcher(String text, TokenList list)
	{
		RegexMatcher regexMatcher = new RegexMatcher();
		regexMatcher.setType(text);
		regexMatcher.setCategory(CATEGORY_NAME);
		regexMatcher.createTokens(list);
		_matcher.appendChild(regexMatcher);
		regexMatcher.appendText(text);
	}

	/**
	 * Asserts that result matches the whole line.
	 * 
	 * @param result
	 *            - result offset.
	 * @param originalSource
	 *            - original source.
	 */
	private void assertResultIsWholeLine(int result, char[] originalSource)
	{
		assertEquals(originalSource.length, result);
	}

	/**
	 * Asserts result type.
	 * 
	 * @param text
	 *            - type text.
	 */
	private void assertResultType(String text)
	{
		assertEquals(text, _matcher.getMatchedToken().getType());
	}

	/**
	 * Initializes matcher.
	 */
	private void initMatcher()
	{
		_matcher.buildFirstCharacterMap();
	}

	/**
	 * Creates token list.
	 * 
	 * @return token list.
	 */
	private TokenList createTokenList(final String[] types)
	{
		TokenList list = new TokenList();
		IEnumerationMap categoryMap = new IEnumerationMap()
		{
			public int getIntValue(String name)
			{
				if (CATEGORY_NAME.equals(name))
				{
					return 1;
				}

				return -1;
			}

			public String getName(int index)
			{
				if (index == 1)
				{
					return CATEGORY_NAME;
				}
				return null;
			}

			public String[] getNames()
			{
				return new String[] { CATEGORY_NAME };
			}
		};

		list.setCategoryMap(categoryMap);

		IEnumerationMap typeMap = new IEnumerationMap()
		{
			public int getIntValue(String name)
			{
				for (int i = 0; i < types.length; i++)
				{
					if (types[i].equals(name))
					{
						return i;
					}
				}

				return -1;
			}

			public String getName(int index)
			{
				if (index >= 0 && index <= types.length)
				{
					return types[index];
				}
				return null;
			}

			public String[] getNames()
			{
				return types;
			}
		};
		list.setTypeMap(typeMap);
		return list;
	}
}
