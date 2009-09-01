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
public class NumberSet
{

	/*
	 * Fields
	 */
	int[] _members;
	String _stringCache;

	/*
	 * Properties
	 */

	/**
	 * Get the number at the specified index
	 * 
	 * @param index
	 *            The index to retrieve
	 * @return Returns a number at the specified index
	 */
	public int getItem(int index)
	{
		return this._members[index];
	}

	/**
	 * Returns the members of this set
	 * 
	 * @return The numbers in this set
	 */
	public int[] getMembers()
	{
		int length = this._members.length;
		int[] members = new int[length];

		System.arraycopy(this._members, 0, members, 0, length);
		Arrays.sort(members);

		return members;
	}

	/**
	 * Returns the number of elements in this set
	 * 
	 * @return The element count of this set
	 */
	public int getSize()
	{
		return this._members.length;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of NumberSet
	 */
	public NumberSet()
	{
		this._members = new int[0];
		this._stringCache = null;
	}

	/*
	 * Methods
	 */

	/**
	 * Add a new member to this set
	 * 
	 * @param member
	 *            The new member to add to the set
	 */
	public void addMember(int member)
	{
		boolean inList = false;

		for (int i = 0; i < this._members.length; i++)
		{
			if (this._members[i] == member)
			{
				inList = true;
				break;
			}
		}

		// int position = Arrays.binarySearch(this._members, member);

		// if (position < 0)
		if (inList == false)
		{
			int length = this._members.length;
			int[] members = new int[length + 1];

			System.arraycopy(this._members, 0, members, 0, length);
			members[length] = member;

			// NOTE: Cannot sort here since NfaConverter.toDfa depends on the
			// order numbers are added to the set. Now sorting in getMembers.
			// Arrays.sort(members);

			this._members = members;

			// clear string cache
			this._stringCache = null;
		}
	}

	/**
	 * Add all members to this set
	 * 
	 * @param members
	 *            The members to add to this set
	 */
	public void addMembers(int[] members)
	{
		for (int i = 0; i < members.length; i++)
		{
			this.addMember(members[i]);
		}
	}

	/**
	 * Determines if the specified number is in this set
	 * 
	 * @param member
	 *            The member to test
	 * @return Returns true if the member is in the set
	 */
	public boolean inSet(int member)
	{
		return Arrays.binarySearch(this._members, member) != -1;
	}

	/**
	 * Create a string representation of this set
	 * 
	 * @return Returns a string representation of this set
	 */
	public String toString()
	{
		if (this._stringCache == null)
		{
			StringBuffer sb = new StringBuffer();
			int[] members = this.getMembers();

			if (members.length > 0)
			{
				sb.append(members[0]);

				for (int i = 1; i < members.length; i++)
				{
					sb.append(",").append(members[i]); //$NON-NLS-1$
				}
			}

			this._stringCache = sb.toString();
		}

		return this._stringCache;
	}

	/**
	 * Combines all numbers in the specified set into this set
	 * 
	 * @param rhs
	 *            The set to combine with this set
	 */
	public void union(NumberSet rhs)
	{
		int[] members = rhs.getMembers();

		for (int i = 0; i < members.length; i++)
		{
			this.addMember(members[i]);
		}
	}
}
