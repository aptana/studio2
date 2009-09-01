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
package com.aptana.ide.regex.inputs;

/**
 * @author Kevin Lindsey
 */
public class Input
{
	/*
	 * Fields
	 */

	/**
	 * All characters in this Input
	 */
	protected static char[] allCharacters;

	/**
	 * A flag that determines if the characters in this set are inclusive or exclusive
	 */
	protected boolean complement;

	/*
	 * Properties
	 */

	/**
	 * Get all characters for this Input
	 * 
	 * @return Returns all characters for this Input as a character array
	 */
	protected char[] getAllCharacters()
	{
		return allCharacters;
	}

	/**
	 * Get all character for this Input
	 * 
	 * @return Returns all characters for this Input as a string
	 */
	public String getCharacters()
	{
		return ""; //$NON-NLS-1$
	}

	/**
	 * Determines if the set of characters in this input should be included or excluded
	 * 
	 * @return Returns true if this input should be complemented
	 */
	public boolean getComplement()
	{
		return this.complement;
	}

	/**
	 * Sets the complement state for this input
	 * 
	 * @param value
	 *            The new complement state
	 */
	public void setComplement(boolean value)
	{
		this.complement = value;
	}

	/*
	 * Constructors
	 */
	static
	{
		allCharacters = new char[256];

		int i = 0;
		for (char c = '\0'; c <= '\u00FF'; c++, i++)
		{
			allCharacters[i] = c;
		}
	}

	/**
	 * Create a new instance of Input
	 */
	public Input()
	{
		this.complement = false;
	}

	/*
	 * Methods
	 */

	/**
	 * Determines if this Input type includes the specified character
	 * 
	 * @param input
	 *            The character to test for membership within this Input
	 * @return Returns true if this Input contains the specified character
	 */
	public boolean hasInput(char input)
	{
		return false;
	}

	/**
	 * Return a string representation of this Input
	 * 
	 * @return Returns a string representation of this Input
	 */
	public String toString()
	{
		return "-"; //$NON-NLS-1$
	}
}
