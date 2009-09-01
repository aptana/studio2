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
public interface IToken
{
	/**
	 * Get the name of this token's category.
	 * 
	 * @return Returns the token's category name
	 */
	String getCategory();

	/**
	 * Set the name of this token's category.
	 * 
	 * @param category
	 */
	void setCategory(String category);

	/**
	 * Get the index of this token's lexer class
	 * 
	 * @return The token lexer class index
	 */
	int getCategoryIndex();

	/**
	 * Set the index of this token's class
	 * 
	 * @param value
	 *            The new token class index value
	 */
	void setCategoryIndex(int value);

	/**
	 * Get the unique index for this token
	 * 
	 * @return This token's unique index
	 */
	int getIndex();

	/**
	 * Set the token's unique index
	 * 
	 * @param index
	 *            The token's index
	 */
	void setIndex(int index);

	/**
	 * Get the language that defined this token type
	 * 
	 * @return Returns the name of the language where this token was defined
	 */
	String getLanguage();

	/**
	 * Get the name of the lexer group this token is contained by
	 * 
	 * @return The lexer state name
	 */
	String getLexerGroup();

	/**
	 * Set the name of the lexer group this token is contained by
	 * 
	 * @param lexerGroup
	 */
	void setLexerGroup(String lexerGroup);

	/**
	 * Get the index of the lexer state this token is contained by
	 * 
	 * @return The lexer state index
	 */
	int getLexerGroupIndex();

	/**
	 * Set the index of the lexer state this token is contained by
	 * 
	 * @param value
	 *            The new lexer state index
	 */
	void setLexerGroupIndex(int value);

	/**
	 * Get the name associated with this token
	 * 
	 * @return The token's name
	 */
	String getType();

	/**
	 * Set the type name for this token
	 * 
	 * @param type
	 */
	void setType(String type);

	/**
	 * Get the index of this token
	 * 
	 * @return The token index
	 */
	int getTypeIndex();

	/**
	 * Set the index of this token
	 * 
	 * @param value
	 *            The new token index value
	 */
	void setTypeIndex(int value);

	/**
	 * Get the name of the lexer state to which this token transitions
	 * 
	 * @return The new lexer state name
	 */
	String getNewLexerGroup();

	/**
	 * Set the name of the lexer group to which this token transitions
	 * 
	 * @param group
	 */
	void setNewLexerGroup(String group);

	/**
	 * Get the index of the lexer state to which this token transitions
	 * 
	 * @return The new lexer state index
	 */
	int getNewLexerGroupIndex();

	/**
	 * Set the index of the lexer state to which this token transitions
	 * 
	 * @param value
	 *            The new lexer state index
	 */
	void setNewLexerGroupIndex(int value);

	/**
	 * Determine if this Token can be changed
	 * 
	 * @return Returns true if this token is sealed and cannot be altered
	 */
	boolean isSealed();

	/**
	 * Seal this token so no more changes can be made to it's properties
	 */
	void seal();

	/**
	 * Sets the display name for this token (for the preference page display). This method will not set the display name
	 * if the parameter is null.
	 * 
	 * @param displayName -
	 *            name to display for this token
	 */
	void setDisplayName(String displayName);

	/**
	 * Gets the display name for this token. This method should never return null and always a readable and
	 * understandable name for the token it represents.
	 * 
	 * @return - display name
	 */
	String getDisplayName();
}