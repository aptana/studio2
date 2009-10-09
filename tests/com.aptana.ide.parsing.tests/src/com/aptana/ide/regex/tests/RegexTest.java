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
package com.aptana.ide.regex.tests;

import java.io.IOException;

import com.aptana.ide.io.StreamUtils;
import com.aptana.ide.lexer.matcher.ITextMatcher;
import com.aptana.ide.lexer.matcher.RegexMatcherHandler;
import com.aptana.ide.parsing.bnf.IReductionHandler;
import com.aptana.ide.parsing.experimental.BNFRunner;
import com.aptana.ide.regex.IRegexRunner;
import com.aptana.ide.regex.RegexHandler;
import com.aptana.ide.regex.nfa.NFAConverter;
import com.aptana.ide.regex.nfa.NFAGraph;

/**
 * @author Kevin Lindsey
 */
public final class RegexTest
{
	private static IRegexRunner regexRunner;
	private static ITextMatcher matcher;

	private RegexTest()
	{
	}
	
	/**
	 * main
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		testAgainstPattern(
			StreamUtils.getText(RegexTest.class.getResourceAsStream("/Resources/pattern.regex")),
			new String[] {
				"abc",
				"def",
				"aabc",
				"ddef",
				"abcdef",
				"defghi"
			}
		);
		testAgainstPattern(
			"[a-ce-g]+",
			new String[] {
				"aaa",
				"bbb",
				"ccc",
				"eee",
				"fff",
				"ggg",
				"ddd"
			}
		);
		testAgainstPattern(
			"test|text",
			new String[] {
				"test",
				"text",
				"temp"
			}
		);
	}

	/**
	 * testAgainstPattern
	 * 
	 * @param pattern
	 * @param testStrings
	 * @throws IOException
	 */
	private static void testAgainstPattern(String pattern, String[] testStrings) throws IOException
	{
		BNFRunner runner = new BNFRunner("/Resources/Regex.bnf", "/Resources/Regex.lxr", "text/regex");
		RegexHandler handler = new RegexHandler(1);
		RegexMatcherHandler handler2 = new RegexMatcherHandler();
		IReductionHandler[] handlers = new IReductionHandler[] { handler, handler2 };

		if (runner.parse(pattern, handlers))
		{
			Object[] values = handler.getValues();
			NFAGraph graph = (NFAGraph) values[0];
			NFAConverter converter = new NFAConverter();
			regexRunner = converter.toDFA(graph);

			values = handler2.getValues();
			matcher = (ITextMatcher) values[0];

			for (int i = 0; i < testStrings.length; i++)
			{
				test(testStrings[i]);
			}
		}
		else
		{
			System.out.println("failed: " + runner.getMessage());
		}
	}

	private static void test(String source)
	{
		int position = regexRunner.match(source, 0, source.length());
		int accept = regexRunner.getAcceptState();
		System.out.println("  REGEX: " + source + ": pos = " + position + ", accept = " + accept);

		position = matcher.match(source.toCharArray(), 0, source.length());
		System.out.println("MATCHER: " + source + ": pos = " + position);
	}
}
