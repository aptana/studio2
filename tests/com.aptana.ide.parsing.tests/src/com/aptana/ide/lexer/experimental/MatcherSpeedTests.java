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
package com.aptana.ide.lexer.experimental;

import com.aptana.ide.lexer.matcher.ITextMatcher;
import com.aptana.ide.lexer.matcher.OneOrMoreMatcher;
import com.aptana.ide.lexer.matcher.OptionalMatcher;
import com.aptana.ide.lexer.matcher.OrMatcher;
import com.aptana.ide.lexer.matcher.StringMatcher;
import com.aptana.ide.lexer.matcher.WhitespaceMatcher;
import com.aptana.ide.lexer.matcher.ZeroOrMoreMatcher;

/**
 * @author Kevin Lindsey
 */
public final class MatcherSpeedTests
{
	private static final int LOOP_COUNT = 5000000;

	/**
	 * @author Kevin Lindsey
	 */
	public interface IMatcherTest
	{
		/**
		 * getName
		 * 
		 * @return String
		 */
		String getName();

		/**
		 * createMatcher
		 * 
		 * @return IMatcher
		 */
		ITextMatcher createMatcher();

		/**
		 * getCharacters
		 * 
		 * @return char[]
		 */
		char[] getCharacters();
	}

	/**
	 * MatcherSpeedTests
	 */
	private MatcherSpeedTests()
	{
	}

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		testSpeed(createZeroOrMoreTest());
		testSpeed(createOptionalTest());
		testSpeed(createOneOrMoreTest());

		testSpeed(createStringOrGroup());
	}

	/**
	 * createStringOrGroup
	 * 
	 * @return IMatcherTest
	 */
	private static IMatcherTest createStringOrGroup()
	{
		return new IMatcherTest()
		{
			/**
			 * @see com.aptana.ide.lexer.experimental.MatcherSpeedTests.IMatcherTest#createMatcher()
			 */
			public ITextMatcher createMatcher()
			{
				OrMatcher or = new OrMatcher();

				or.appendChild(new StringMatcher("abc"));
				or.appendChild(new StringMatcher("def"));
				or.appendChild(new StringMatcher("ghi"));
				or.appendChild(new StringMatcher("jkl"));
				or.appendChild(new StringMatcher("mno"));
				or.appendChild(new StringMatcher("pqr"));
				or.appendChild(new StringMatcher("stu"));
				or.appendChild(new StringMatcher("vwx"));
				or.appendChild(new StringMatcher("yz"));

				or.buildFirstCharacterMap();

				return or;
			}

			/**
			 * @see com.aptana.ide.lexer.experimental.MatcherSpeedTests.IMatcherTest#getCharacters()
			 */
			public char[] getCharacters()
			{
				return "yz".toCharArray();
			}

			/**
			 * @see com.aptana.ide.lexer.experimental.MatcherSpeedTests.IMatcherTest#getName()
			 */
			public String getName()
			{
				return "<or>...</or>";
			}
		};
	}

	/**
	 * createOneOrMoreTest
	 * 
	 * @return IMatcherTest
	 */
	private static IMatcherTest createOneOrMoreTest()
	{
		return new IMatcherTest()
		{
			/**
			 * @see com.aptana.ide.lexer.experimental.MatcherSpeedTests.IMatcherTest#createMatcher()
			 */
			public ITextMatcher createMatcher()
			{
				OneOrMoreMatcher oorm = new OneOrMoreMatcher();
				WhitespaceMatcher ws = new WhitespaceMatcher();
				oorm.appendChild(ws);

				return oorm;
			}

			/**
			 * @see com.aptana.ide.lexer.experimental.MatcherSpeedTests.IMatcherTest#getCharacters()
			 */
			public char[] getCharacters()
			{
				return " \t\r\n\f".toCharArray();
			}

			/**
			 * @see com.aptana.ide.lexer.experimental.MatcherSpeedTests.IMatcherTest#getName()
			 */
			public String getName()
			{
				return "<one-or-more><whitespace/></one-or-more>";
			}

		};
	}

	/**
	 * createOptionalTest
	 * 
	 * @return IMatcherTest
	 */
	private static IMatcherTest createOptionalTest()
	{
		return new IMatcherTest()
		{
			/**
			 * @see com.aptana.ide.lexer.experimental.MatcherSpeedTests.IMatcherTest#createMatcher()
			 */
			public ITextMatcher createMatcher()
			{
				OptionalMatcher om = new OptionalMatcher();
				WhitespaceMatcher ws = new WhitespaceMatcher();
				om.appendChild(ws);

				return om;
			}

			/**
			 * @see com.aptana.ide.lexer.experimental.MatcherSpeedTests.IMatcherTest#getCharacters()
			 */
			public char[] getCharacters()
			{
				return " \t\r\n\f".toCharArray();
			}

			/**
			 * @see com.aptana.ide.lexer.experimental.MatcherSpeedTests.IMatcherTest#getName()
			 */
			public String getName()
			{
				return "<optional><whitespace/></optional>";
			}
		};
	}

	/**
	 * createZeroOrMoreTest
	 * 
	 * @return IMatcherTest
	 */
	private static IMatcherTest createZeroOrMoreTest()
	{
		return new IMatcherTest()
		{
			/**
			 * @see com.aptana.ide.lexer.experimental.MatcherSpeedTests.IMatcherTest#createMatcher()
			 */
			public ITextMatcher createMatcher()
			{
				ZeroOrMoreMatcher zorm = new ZeroOrMoreMatcher();
				WhitespaceMatcher ws = new WhitespaceMatcher();
				zorm.appendChild(ws);

				return zorm;
			}

			/**
			 * @see com.aptana.ide.lexer.experimental.MatcherSpeedTests.IMatcherTest#getCharacters()
			 */
			public char[] getCharacters()
			{
				return " \t\r\n\f".toCharArray();
			}

			/**
			 * @see com.aptana.ide.lexer.experimental.MatcherSpeedTests.IMatcherTest#getName()
			 */
			public String getName()
			{
				return "<zero-or-more><whitespace/></zero-or-more>";
			}
		};
	}

	/**
	 * testZeroOrMore
	 */
	private static void testSpeed(IMatcherTest test)
	{
		ITextMatcher matcher = test.createMatcher();
		char[] source = test.getCharacters();
		long startMillis = System.currentTimeMillis();
		int matches = 0;
		int errors = 0;

		for (int i = 0; i < LOOP_COUNT; i++)
		{
			int result = matcher.match(source, 0, source.length);

			if (result != -1)
			{
				matches++;
			}
			else
			{
				errors++;
			}
		}

		long diff = System.currentTimeMillis() - startMillis;
		long mps = Math.round((double) LOOP_COUNT * 1000.0 / (double) diff);
		long cps = mps * source.length;

		// CHECKSTYLE:OFF
		System.out.println(test.getName());
		System.out.println(LOOP_COUNT + " iterations in " + diff + "ms");
		System.out.println(mps + " matches/s");
		System.out.println(cps + " characters/s");
		System.out.println();
		// CHECKSTYLE:ON
	}
}
