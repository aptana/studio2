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
package com.aptana.ide.parsing.matcher;

import java.text.ParseException;

import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.ILexerBuilder;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.ILanguageChangeListener;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.Messages;
import com.aptana.ide.parsing.ParseStateChild;
import com.aptana.ide.parsing.ParsingPlugin;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeFactory;

/**
 * @author Kevin Lindsey
 */
public class MatcherParser implements IParser
{
	private static final String DEFAULT_GROUP = "default"; //$NON-NLS-1$

	private String _language;
	private ILanguageChangeListener _languageChangeListener;
	private ILexer _lexer;
	private IParseState _parseState;

	private RuleMatcher _startRule;

	/**
	 * MatcherParser
	 * 
	 * @param language
	 */
	public MatcherParser(String language)
	{
		this._language = language;
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#addLexerGrammar(com.aptana.ide.lexer.ILexerBuilder)
	 */
	public void addLexerGrammar(ILexerBuilder builder) throws LexerException
	{
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#changeLanguage(java.lang.String, int,
	 *      com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void changeLanguage(String mimeType, int offset, IParseNode parentNode) throws LexerException, ParseException
	{
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#createParseState(com.aptana.ide.parsing.IParseState)
	 */
	public IParseState createParseState(IParseState parent)
	{
		IParseState result;

		if (parent == null)
		{
			result = new ParseStateChild(this.getLanguage());
		}
		else
		{
			result = new ParseStateChild(this.getLanguage(), parent);
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#getLanguage()
	 */
	public String getLanguage()
	{
		return this._language;
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#getLanguageChangeListener()
	 */
	public ILanguageChangeListener getLanguageChangeListener()
	{
		return this._languageChangeListener;
	}

	/**
	 * getLexemList
	 * 
	 * @return _lexemeList
	 */
	protected LexemeList getLexemeList()
	{
		IParseState parseState = this.getParseState();
		LexemeList result = null;

		if (parseState != null)
		{
			result = parseState.getLexemeList();
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#getLexer()
	 */
	public ILexer getLexer()
	{
		return this._lexer;
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#getParserForMimeType(java.lang.String)
	 */
	public IParser getParserForMimeType(String language)
	{
		IParser result = null;

		if (this._language.equals(language))
		{
			result = this;
		}

		return result;
	}

	/**
	 * getParseNodeFactory
	 * 
	 * @return IParseNodeFactory
	 */
	protected IParseNodeFactory getParseNodeFactory()
	{
		IParseState parseState = this.getParseState();
		IParseNodeFactory result = null;

		if (parseState != null)
		{
			result = parseState.getParseNodeFactory();
		}

		return result;
	}

	/**
	 * getParseState
	 * 
	 * @return IParseState
	 */
	protected IParseState getParseState()
	{
		return this._parseState;
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#initializeLexer()
	 */
	public void initializeLexer() throws LexerException
	{
		// get lexer
		ILexer lexer = this.getLexer();
		String language = this.getLanguage();

		// ignore whitespace
		// lexer.setIgnoreSet(language, new int[] { JSTokenTypes.WHITESPACE });

		// make sure we're in the default group
		lexer.setLanguageAndGroup(language, DEFAULT_GROUP);
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#parse(com.aptana.ide.parsing.IParseState)
	 */
	public IParseNode parse(IParseState parseState) throws ParseException, LexerException
	{
		IParseNode result = null;

		synchronized (parseState.getLexemeList())
		{
			// cache parse state so other methods can use it
			this._parseState = parseState;

			// create root node
			IParseNodeFactory nodeFactory = this.getParseNodeFactory();

			if (nodeFactory != null)
			{
				result = nodeFactory.createRootNode();
			}

			// move the source over to the lexer and set the lexing starting position
			ILexer lexer = this.getLexer();
			lexer.setLexemeCache(this.getLexemeList());
			lexer.setSource(parseState.getSource());
			lexer.setCurrentOffset(0);

			// pre-parse call
			parseState.onBeforeParse();

			// perform parse
			try
			{
				this.parseAll(result);
			}
			catch (ParseException e)
			{
			}
			catch (Exception e)
			{
				// something really unexpected happened, so report it
				ParsingPlugin.logInfo(Messages.ParserBase_UnexpectedErrorDuringParse, e);

				// CHECKSTYLE:OFF
				System.err.println(Messages.ParserBase_UnexpectedErrorDuringParse);
				e.printStackTrace();
				// CHECKSTYLE:ON
			}

			this._parseState.setParseResults(result);

			// post-parse call
			parseState.onAfterParse();
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#parseAll(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void parseAll(IParseNode parentNode) throws ParseException, LexerException
	{
		if (this._startRule != null)
		{
			LexemeList lexemeList = this.getLexemeList();

			if (lexemeList != null && lexemeList.size() > 0)
			{
				Lexeme[] lexemes = lexemeList.copyRange(0, lexemeList.size() - 1);

				this._startRule.match(lexemes, 0, lexemes.length);

				if (parentNode != null)
				{
					IParseNode result = this._startRule.getParseResults();

					if (result != null)
					{
						parentNode.appendChild(result);
					}
				}
			}
		}
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#setLanguageChangeListener(com.aptana.ide.parsing.ILanguageChangeListener)
	 */
	public void setLanguageChangeListener(ILanguageChangeListener eventHandler)
	{
		this._languageChangeListener = eventHandler;
	}
}
