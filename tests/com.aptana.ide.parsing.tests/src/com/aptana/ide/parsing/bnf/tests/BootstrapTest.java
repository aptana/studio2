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
package com.aptana.ide.parsing.bnf.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.aptana.ide.io.StreamUtils;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.Token;
import com.aptana.ide.lexer.matcher.MatcherLexer;
import com.aptana.ide.lexer.matcher.MatcherLexerBuilder;
import com.aptana.ide.parsing.bnf.BNFGrammar;
import com.aptana.ide.parsing.bnf.BNFHandler;
import com.aptana.ide.parsing.bnf.nodes.GrammarNode;
import com.aptana.ide.parsing.experimental.Parser;

/**
 * @author Kevin Lindsey
 */
public final class BootstrapTest extends TestCase
{
	public void testBootStrap()
	{
		GrammarNode grammar = BNFGrammar.getGrammar();
		Parser parser = new Parser();

		parser.generateTable(grammar);

		ILexer lexer = getLexer();
		String source = getSource("/Resources/BNF.bnf");
		lexer.setSource(source);
		Lexeme[] lexemes = getLexemes(lexer);

		BNFHandler handler = new BNFHandler();
		parser.addHandler(handler);

		// parse
		if (parser.parse(lexemes))
		{
			GrammarNode newGrammar = handler.getGrammar();
			String source1 = newGrammar.getSource();
			lexer.setSource(source1);
			lexemes = getLexemes(lexer);
			handler = new BNFHandler();
			Parser newParser = new Parser();
			newParser.addHandler(handler);
			newParser.generateTable(newGrammar);

			if (newParser.parse(lexemes))
			{
				GrammarNode newGrammar2 = handler.getGrammar();
				String source2 = newGrammar2.getSource();

				assertEquals(source1, source2);
			}
			else
			{
				fail("2nd pass failed: " + newParser.getMessage());
			}
		}
		else
		{
			fail("1st pass failed: " + parser.getMessage());
		}
	}

	/**
	 * getLexer
	 * 
	 * @return
	 */
	private static ILexer getLexer()
	{
		MatcherLexerBuilder builder = new MatcherLexerBuilder();
		InputStream in = BootstrapTest.class.getResourceAsStream("/Resources/BNF.lxr");
		MatcherLexer lexer = null;

		builder.loadXML(in);

		try
		{
			lexer = (MatcherLexer) builder.buildLexer();
			lexer.setLanguageAndGroup("text/bnf", "default");
		}
		catch (LexerException e)
		{
			e.printStackTrace();
		}

		return lexer;
	}

	/**
	 * getSource
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	private static String getSource(String resource)
	{
		InputStream bnf = BootstrapTest.class.getResourceAsStream(resource);
		String source = "";

		try
		{
			source = StreamUtils.getText(bnf);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return source;
	}

	/**
	 * getTypes
	 * 
	 * @param lexer
	 * @return
	 */
	private static Lexeme[] getLexemes(ILexer lexer)
	{
		List<Lexeme> types = new ArrayList<Lexeme>();
		Lexeme lexeme = lexer.getNextLexeme();

		while (lexeme != null)
		{
			if (lexeme.getCategory().equals("WHITESPACE") == false)
			{
				types.add(lexeme);
			}

			lexeme = lexer.getNextLexeme();
		}

		types.add(new Lexeme(new Token(null)
		{
			/**
			 * @see com.aptana.ide.lexer.Token#getTypeIndex()
			 */
			public int getTypeIndex()
			{
				return 0;
			}

			/**
			 * @see com.aptana.ide.lexer.Token#getType()
			 */
			public String getType()
			{
				return "$";
			}
		}, "$", -1));

		return types.toArray(new Lexeme[types.size()]);
	}
}
