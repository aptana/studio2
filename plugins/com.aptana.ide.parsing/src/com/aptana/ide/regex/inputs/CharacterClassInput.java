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

import java.util.Arrays;

/**
 * @author Kevin Lindsey
 */
public class CharacterClassInput extends Input
{
	/*
	 * Fields
	 */

	/**
	 * A flag determining if this character class can receive new entries
	 */
	protected boolean locked;
	private String _characters;

	/*
	 * Properties
	 */

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.aptana.ide.regex.inputs.Input#getCharacters()
	 */
	public String getCharacters()
	{
		char[] chars;
		String result;

		if (this.complement)
		{
			char[] allCharacters = this.getAllCharacters();
			String ccCharacters = this._characters;
			char[] resultCharacters = new char[allCharacters.length];
			int index = 0;

			for (int i = 0; i < allCharacters.length; i++)
			{
				char c = allCharacters[i];

				if (ccCharacters.indexOf(c) == -1)
				{
					resultCharacters[index] = c;
					index++;
				}
			}

			result = new String(resultCharacters, 0, index);

			/*
			 * result = new String(this.getAllCharacters()); chars = this._characters.toCharArray(); for (int i = 0; i <
			 * chars.length; i++) { String c = String.valueOf(chars[i]); result = result.replaceAll("\\" + c, ""); }
			 */
		}
		else
		{
			chars = this._characters.toCharArray();
			Arrays.sort(chars);

			result = new String(chars);
		}

		return result;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of CharacterClassInput
	 */
	public CharacterClassInput()
	{
		this.locked = false;
		this._characters = ""; //$NON-NLS-1$
	}

	/*
	 * Methods
	 */

	/**
	 * Add the specified character to the character class
	 * 
	 * @param input
	 *            The input character to add to this Input
	 */
	public void addInput(char input)
	{
		// if (this.locked)
		// {
		// // throw new Exception("Cannot add input characters to a locked
		// // character class");
		// }

		if (this._characters.indexOf(input) == -1)
		{
			this._characters += input;
		}
	}

	/**
	 * Add the specified range of characters to the character class
	 * 
	 * @param start
	 *            The starting character to add to the character class
	 * @param end
	 *            The ending character to add to the character class
	 */
	public void addInputs(char start, char end)
	{
		for (char c = start; c <= end; c++)
		{
			this.addInput(c);
		}
	}

	/**
	 * Add the characters from the specified string to the character class
	 * 
	 * @param inputs
	 *            A string of characters to add to the character class
	 */
	public void addInputs(String inputs)
	{
		char[] c = inputs.toCharArray();

		for (int i = 0; i < inputs.length(); i++)
		{
			this.addInput(c[i]);
		}
	}

	/**
	 * Determines if the specified input character is in the character class
	 * 
	 * @param input
	 *            The character to test for membership within this Input
	 * @return Returns true if the specified character is in the character class
	 */
	public boolean hasInput(char input)
	{
		boolean exists = this._characters.indexOf(input) != -1;

		return (this.complement) ? !exists : exists;
	}

	/**
	 * Return a string representation of this input
	 * 
	 * @return Returns a string representation of this input
	 */
	public String toString()
	{
		String result;

		if (this.complement)
		{
			result = "^" + this._characters; //$NON-NLS-1$
		}
		else
		{
			result = this._characters;
		}

		return result;
	}
}
