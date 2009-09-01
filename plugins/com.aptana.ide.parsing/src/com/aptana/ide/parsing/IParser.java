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
package com.aptana.ide.parsing;

import java.text.ParseException;

import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.ILexerBuilder;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public interface IParser
{
	/**
	 * Add this parser's lexer grammar to the specified lexer builder. This allows multiple parsers that are used as a
	 * group to share the same lexer
	 * 
	 * @param builder
	 * @throws LexerException
	 */
	void addLexerGrammar(ILexerBuilder builder) throws LexerException;

	/**
	 * Create a single parse state for this language. Note that sub-language parse states will be included in the
	 * returned parse state
	 * 
	 * @param parent
	 * @return IParseState
	 */
	IParseState createParseState(IParseState parent);

	/**
	 * Retrieve the language change listener
	 *
	 * @return IChangeLanguageEventHandler
	 */
	ILanguageChangeListener getLanguageChangeListener();
	
	/**
	 * Set the language change listener. This value can be null.
	 *
	 * @param eventHandler
	 */
	void setLanguageChangeListener(ILanguageChangeListener eventHandler);
	
	/**
	 * Retrieve the lexer used by this parser
	 * 
	 * @return ILexer
	 */
	ILexer getLexer();

	/**
	 * Search this parser's child parsers for the parser that handles the given language. Note this visits children
	 * only and does not descend beyond that level
	 * 
	 * @param language
	 * @return IParser
	 */
	IParser getParserForMimeType(String language);

	/**
	 * Initialize the parser's lexer. Typically, this method is used to define the token ignoreSet and the current group
	 * 
	 * @throws LexerException
	 */
	void initializeLexer() throws LexerException;

	/**
	 * Parse the edit to the source document encapsulated within the IParseState
	 * 
	 * @param parseState
	 * @return IParseNode
	 * @throws ParseException
	 * @throws LexerException
	 */
	IParseNode parse(IParseState parseState) throws ParseException, LexerException;

	/**
	 * Parse the entire content of the source for this language. This is the entry point from parse
	 * and for parent languages transitioning into this language
	 * 
	 * @param parentNode
	 * @throws ParseException
	 * @throws LexerException
	 */
	void parseAll(IParseNode parentNode) throws ParseException, LexerException;

	/**
	 * Transition to the nested language.
	 * 
	 * @param mimeType
	 * @param offset
	 * @param parentNode
	 * @throws LexerException
	 * @throws ParseException
	 */
	void changeLanguage(String mimeType, int offset, IParseNode parentNode) throws LexerException, ParseException;

	/**
	 * Get the MIME type for this parser
	 * 
	 * @return Parser language MIME type
	 */
	String getLanguage();
}
