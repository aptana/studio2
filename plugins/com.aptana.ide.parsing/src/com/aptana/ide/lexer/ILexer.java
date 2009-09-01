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
package com.aptana.ide.lexer;

/**
 * @author Kevin Lindsey
 */
public interface ILexer
{
	/**
	 * Set the lexeme cache
	 * 
	 * @param lexemeCache
	 *            The list of previously scanned lexemes
	 */
	void setLexemeCache(LexemeList lexemeCache);

	/**
	 * Return the character at the given offset
	 * 
	 * @param offset
	 *            The offset within the source
	 * @return The character at the given offset
	 */
	char getCharacterAt(int offset);

	/**
	 * Return the current offset within the source text where the next token will be matched
	 * 
	 * @return The current offset within the source text
	 */
	int getCurrentOffset();

	/**
	 * Set the current offset within the source text where the next token will be matched
	 * 
	 * @param offset
	 *            The new offset value
	 */
	void setCurrentOffset(int offset);

	/**
	 * Get the token list that is currently active in this lexer
	 * 
	 * @return ITokenList
	 */
	ITokenList getCurrentTokenList();

	/**
	 * Returns the offset that is considered as the EOF
	 * 
	 * @return The EOF offset
	 */
	int getEOFOffset();

	/**
	 * Set the offset that is considered the end of the input stream. The offset will not be included in the stream
	 * 
	 * @param offset
	 *            The new EOF offset for the current stream of text being processed by this lexer
	 */
	void setEOFOffset(int offset);

	/**
	 * Returns the name of the group that is current active
	 * 
	 * @return Returns the current group name
	 */
	String getGroup();

	/**
	 * Set the currently active lexer group
	 * 
	 * @param groupName
	 *            The name of the group to activate
	 * @throws LexerException
	 */
	void setGroup(String groupName) throws LexerException;

	/**
	 * Set the list of Token types to skip when scanning the source text
	 * 
	 * @param language
	 *            The target language to apply this ignore set
	 * @param set
	 *            The set of token type to skip
	 */
	void setIgnoreSet(String language, int[] set);

	/**
	 * Get the language type this lexer is targeting
	 * 
	 * @return The language this lexer is targeting
	 */
	String getLanguage();

	/**
	 * Get all languages that are contained in this Lexer
	 * 
	 * @return Returns a string array of all languages in this lexer
	 */
	String[] getLanguages();

	/**
	 * Set the language name this lexer targets
	 * 
	 * @param language
	 *            The language this lexer will target
	 * @throws LexerException
	 */
	void setLanguage(String language) throws LexerException;

	/**
	 * Set the current language and the group within that language
	 * 
	 * @param language
	 *            The new language
	 * @param group
	 *            The group within the language
	 * @throws LexerException
	 */
	void setLanguageAndGroup(String language, String group) throws LexerException;

	/**
	 * Get the next token from the source text
	 * 
	 * @return The next token in Token stream
	 */
	Lexeme getNextLexeme();

	/**
	 * Get the text being processed by this lexer
	 * 
	 * @return The source text
	 */
	String getSource();

	/**
	 * Return the number of characters in this lexer's source code
	 * 
	 * @return Returns the source code character count
	 */
	int getSourceLength();

	/**
	 * Set the text to be processed by this lexer
	 * 
	 * @param value
	 *            The new source text
	 */
	void setSource(char[] value);

	/**
	 * Set the text to be processed by this lexer
	 * 
	 * @param value
	 *            The new source text
	 */
	void setSource(String value);

	/**
	 * Get the token list for the specified language
	 * 
	 * @param language
	 * @return Returns the token list for the specified language
	 */
	ITokenList getTokenList(String language);

	/**
	 * Determine if we've processed all the source text
	 * 
	 * @return Returns true if we have processed all of the source text
	 */
	boolean isEOS();

	/**
	 * Add the given language tokens to this lexer
	 * 
	 * @param tokens
	 */
	void addLanguage(ITokenList tokens);

	/**
	 * Build all of the token regexes and build the lexer group state machines
	 * 
	 * @throws LexerException
	 */
	void seal() throws LexerException;

	/**
	 * Use the currently active language and group to locate the first position that matches that group. Note that token
	 * switchTo's are ignored by this method
	 * 
	 * @param groupName
	 *            The current language's group name that contains the patterns to find
	 * @return Returns an Range where the starting offset is the point where the match occurred and the ending offset is
	 *         the position where the match ended. If there is no match, then an empty Range will be returned
	 * @throws LexerException
	 */
	Range find(String groupName) throws LexerException;

	/**
	 * Set properties of the lexer to prepare for rescanning of unmodified source
	 * 
	 * @param group
	 *            The group to switch to before beginning the rescan
	 * @param offset
	 *            The offset within the source where to begin rescanning
	 * @throws LexerException
	 */
	void setLexerState(String group, int offset) throws LexerException;

	/**
	 * Set properties of the lexer to prepare for rescanning of modified source
	 * 
	 * @param group
	 *            The group to switch to before beginning the next lex
	 * @param source
	 *            The new source code to lex
	 * @param offset
	 *            The offset within the source where to begin re-lexing
	 * @param cache
	 *            The lexeme cache from the last lex
	 * @throws LexerException
	 */
	void setLexerState(String group, char[] source, int offset, LexemeList cache) throws LexerException;

	
	/**
	 * TODO Remove this method later.
	 * @author Pavel Petrochenko
	 * @return reference to underlying char array
	 */
	char[] getSourceUnsafe();
}