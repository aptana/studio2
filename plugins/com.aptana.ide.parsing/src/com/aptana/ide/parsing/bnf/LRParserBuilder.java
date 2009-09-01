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
package com.aptana.ide.parsing.bnf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.aptana.ide.io.StreamUtils;
import com.aptana.ide.lexer.IEnumerationMap;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenList;
import com.aptana.ide.lexer.matcher.MatcherLexerBuilder;
import com.aptana.ide.lexer.matcher.MatcherTokenList;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.ParsingPlugin;
import com.aptana.ide.parsing.bnf.nodes.GrammarNode;

/**
 * @author Kevin Lindsey
 */
public class LRParserBuilder
{
	private static LRParser bnfParser;
	private static BNFHandler handler;
	
	/**
	 * BNFParser
	 */
	public LRParserBuilder()
	{
	}

	/**
	 * @see com.aptana.ide.lexer.IParserBuilder#buildParser()
	 */
	public IParser buildParser(InputStream bnfStream, InputStream lexerStream)
	{
		IParser result = null;
		
		if (bnfStream != null && lexerStream != null)
		{
			// create grammar
			final GrammarNode grammar = createGrammar(bnfStream);
			
			// create new parser's token list
			final TokenList tokenList = createTokenList(lexerStream);

			if (grammar != null && tokenList != null)
			{
				// create parser
				try
				{
					result = new LRParser(tokenList, grammar);
				}
				catch (ParserInitializationException e)
				{
				}
			}
		}
		
		return result;
	}
	
	/**
	 * createBNFParser
	 * 
	 * @return LRParser
	 */
	private static LRParser createBNFParser()
	{
		LRParser result = null;
		
		try
		{
			InputStream input = LRParserBuilder.class.getResourceAsStream("/com/aptana/ide/parsing/bnf/resources/BNF.lxr"); //$NON-NLS-1$
			TokenList tokenList = createTokenList(input);
			
			result = new LRParser(tokenList, BNFGrammar.getGrammar())
			{
				/**
				 * @see com.aptana.ide.parsing.AbstractParser#initializeLexer()
				 */
				public void initializeLexer() throws LexerException
				{
					ILexer lexer = this.getLexer();
					String language = this.getLanguage();
					MatcherTokenList tokenList = (MatcherTokenList) lexer.getTokenList(language);
					IEnumerationMap typeMap = tokenList.getTypeMap();

					int[] set = new int[] {
						typeMap.getIntValue("WHITESPACE"), //$NON-NLS-1$
						typeMap.getIntValue("COMMENT") //$NON-NLS-1$
					};
					Arrays.sort(set);
					lexer.setIgnoreSet(language, set);
					lexer.setLanguageAndGroup(language, "default"); //$NON-NLS-1$
				}
				
			};
			
			handler = new BNFHandler();
			
			result.addHandler(handler);
		}
		catch (ParserInitializationException e)
		{
		}
		
		return result;
	}
	
	/**
	 * createGrammar
	 * 
	 * @return GrammarNode
	 */
	public static GrammarNode createGrammar(InputStream bnfStream)
	{
		GrammarNode result = null;
		
		if (bnfStream != null)
		{
			try
			{
				String bnfSource = StreamUtils.getText(bnfStream);
				
				// create parser
				if (bnfParser == null)
				{
					bnfParser = createBNFParser();
				}
				
				// parse
				IParseState parseState = bnfParser.createParseState(null);
				parseState.setEditState(bnfSource, bnfSource, 0, 0);
				bnfParser.parse(parseState);
				
				result = handler.getGrammar();
			}
			catch (LexerException e)
			{
			}
			catch (IOException e)
			{
			}
		}
		
		return result;
	}
	
	/**
	 * createTokenList
	 * 
	 * @param input
	 * @return TokenList or null
	 */
	private static TokenList createTokenList(InputStream input)
	{
		TokenList result = null;

		if (input != null)
		{
			try
			{
				// create lexer builder
				MatcherLexerBuilder builder = new MatcherLexerBuilder();

				// read input stream
				builder.loadXML(input);

				// finalize lexer
				ILexer lexer = builder.buildLexer();

				// get a list of languages
				String[] languages = lexer.getLanguages();

				// grab the first (and only) language token list
				if (languages != null && languages.length > 0)
				{
					result = (TokenList) lexer.getTokenList(languages[0]);
				}
			}
			catch (Exception e)
			{
				ParsingPlugin.logError(Messages.getString("LRParserBuilder.Cannot_create_token_list"), e); //$NON-NLS-1$
			}
		}

		return result;
	}
}
