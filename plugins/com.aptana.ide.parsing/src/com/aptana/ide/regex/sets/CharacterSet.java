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
package com.aptana.ide.regex.sets;

import java.util.Arrays;

/**
 * @author Kevin Lindsey
 */
public class CharacterSet
{
	/*
	 * Fields
	 */
	private String _members;

	/*
	 * Properties
	 */

	/**
	 * Get the members of this character set
	 * 
	 * @return Return all members of this character set as an array
	 */
	public char[] getMembers()
	{
		return this._members.toCharArray();
	}

	/**
	 * Set the members of this character set. Note that the members must be sorted. This is primarily used when reading
	 * in a serialized version of this set
	 * 
	 * @param members
	 *            The characters in this set, in sorted order
	 */
	public void setMembers(String members)
	{
		this._members = members;
	}

	/**
	 * Returns the number of characters in this set
	 * 
	 * @return The number of characters in this set
	 */
	public int getSize()
	{
		return this._members.length();
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of CharacterSet
	 */
	public CharacterSet()
	{
		this._members = ""; //$NON-NLS-1$
	}

	/*
	 * Methods
	 */

	/**
	 * Add a member to this set
	 * 
	 * @param member
	 *            The character to add to this set
	 */
	public void addMember(char member)
	{
		if (this._members.indexOf(member) == -1)
		{
			String newString = this._members + member;
			char[] chars = newString.toCharArray();
			Arrays.sort(chars);

			this._members = new String(chars);
		}
	}

	/**
	 * Add the members of a string to the set
	 * 
	 * @param members
	 */
	public void addMembers(String members)
	{
		char[] chars = members.toCharArray();

		for (int i = 0; i < chars.length; i++)
		{
			this.addMember(chars[i]);
		}
	}

	/**
	 * Find the input index number for the given character
	 * 
	 * @param c
	 *            The character to look for in the set
	 * @return The specified character's index in the set
	 */
	public int inputIndex(char c)
	{
		return this._members.indexOf(c);
	}

	/**
	 * Determines if the given character is in the set
	 * 
	 * @param c
	 *            The character to look for in the set
	 * @return Returns true if the specified character is in the set
	 */
	public boolean inSet(char c)
	{
		return this._members.indexOf(c) != -1;
	}

	/**
	 * Return a string representation of this set
	 * 
	 * @return Return the characters in this character set as a string
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		char[] chars = this.getMembers();

		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];

			if (c < ' ' || c > '\u007F')
			{
				sb.append('.');
			}
			else
			{
				sb.append(c);
			}
		}

		return sb.toString();
	}

	/**
	 * Combines all characters in the specified set into this set
	 * 
	 * @param rhs
	 *            The set to combine with this set
	 */
	public void union(CharacterSet rhs)
	{
		char[] chars = rhs.getMembers();

		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];

			if (this._members.indexOf(c) == -1)
			{
				this._members += c;
			}
		}
	}
}
