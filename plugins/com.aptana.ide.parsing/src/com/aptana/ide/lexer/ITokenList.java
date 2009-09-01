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
public interface ITokenList
{
	/**
	 * Get the token at the specified index
	 * 
	 * @param index
	 *            The index of the token to retrieve
	 * @return The Token at the specified index
	 */
	IToken get(int index);

	/**
	 * Returns the name of the group that is current active
	 * 
	 * @return Returns the current group name
	 */
	String getCurrentGroup();

	/**
	 * Set the currently active lexer group
	 * 
	 * @param groupName
	 *            The name of the group to activate
	 * @throws LexerException
	 */
	void setCurrentGroup(String groupName) throws LexerException;

	/**
	 * Does the specified active lexer group exist?
	 * @param groupName The name of the group to inspect
	 * @return true if the group exists, false otherwise
	 */
	boolean hasGroup(String groupName);
	
	/**
	 * @param index
	 */
	void setCurrentGroup(int index);

	/**
	 * getGroupNames
	 * 
	 * @return String[]
	 */
	String[] getGroupNames();

	/**
	 * Get the list of Token types to skip when scanning the source text
	 * 
	 * @return The set of token type to skip
	 */
	int[] getIgnoreSet();

	/**
	 * Set the list of Token types to skip when scanning the source text
	 * 
	 * @param set
	 *            The set of token type to skip
	 */
	void setIgnoreSet(int[] set);

	/**
	 * Get the language associated with this token list
	 * 
	 * @return The token list's language
	 */
	String getLanguage();

	/**
	 * Add a new token to the list
	 * 
	 * @param token
	 *            the token to add to this list
	 * @return The new token index
	 */
	int add(IToken token);

	/**
	 * Create a new token
	 * 
	 * @return IToken
	 */
	IToken createToken();

	/**
	 * Build the regexes for each token in this list
	 * 
	 * @throws LexerException
	 */
	void seal() throws LexerException;

	/**
	 * Get the number of tokens in this list
	 * 
	 * @return The token count
	 */
	int size();

	/**
	 * This indicates whether this token list can be modified or if it has been sealed
	 * 
	 * @return Returns true if this list is no longer modifiable
	 */
	boolean isSealed();
}