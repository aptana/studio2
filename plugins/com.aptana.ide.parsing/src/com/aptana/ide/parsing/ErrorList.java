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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.ide.lexer.IRange;
import com.aptana.ide.lexer.Offset;

/**
 * @author Kevin Lindsey
 */
public class ErrorList
{
	private List<IErrorMessage> _errors;

	/**
	 * Create a new error list
	 */
	public ErrorList()
	{
		this._errors = new ArrayList<IErrorMessage>();
	}
	
	/**
	 * Get the error node at the specified index
	 * 
	 * @param index
	 *            The index of the node to retrieve from this list
	 * @return Returns the node at the specified index
	 */
	public IErrorMessage get(int index)
	{
		return this._errors.get(index);
	}

	/**
	 * Return this list of errors as an IErrorMessage array
	 * 
	 * @return Returns an array of the error nodes in this collection
	 */
	public IErrorMessage[] getErrors()
	{
		return this._errors.toArray(new IErrorMessage[0]);
	}

	/**
	 * Return the size of this list
	 * 
	 * @return Returns the size of this list
	 */
	public int size()
	{
		return this._errors.size();
	}

	/**
	 * Temporary for testing only
	 */
	public void sanityCheck()
	{
		// int size = this._errors.size();
		//		
		// for (int i = 0; i < size; i++)
		// {
		// IErrorMessage error = this.get(i);
		//			
		// if (error.size() == 0)
		// {
		// throw new IllegalStateException("empty error node in error list");
		// }
		//			
		// // make sure this doesn't overlap with the following lexemes
		// for (int j = i + 1; j < size; j++)
		// {
		// IErrorMessage test = this.get(j);
		//				
		// if (error.isOverlapping(test))
		// {
		// throw new IllegalStateException("An error list cannot contain overlapping error nodes");
		// }
		// }
		//			
		// error.sanityCheck();
		// }
	}

	/**
	 * Add an error to this error list
	 * 
	 * @param error
	 *            The error to add to this list
	 */
	public void add(IErrorMessage error)
	{
		if (error == null)
		{
			throw new IllegalArgumentException(Messages.ErrorList_CannotAddNullErrorNode);
		}
		// if (error.isAtEOF())
		// {
		// throw new IllegalArgumentException("Cannot add an EOF error node to an error list");
		// }

		int insertIndex = Collections.binarySearch(this._errors, error);

		if (insertIndex >= 0)
		{
			throw new IllegalArgumentException(Messages.ErrorList_ListAlreadyContainsErrorInSameRange);
		}

		// find insertion point
		insertIndex = -(insertIndex + 1);

		// insert to list
		this._errors.add(insertIndex, error);

		// set error node's owner
		error.setOwningList(this);
	}

	/**
	 * Clear all errors from this list
	 */
	public void clear()
	{
		while (this._errors.size() > 0)
		{
			// This will clear all lexemes in the referenced IErrorMessage and as a side-effect, the error node will be
			// deleted from this collection
			this.get(0).clear();
		}
	}

	/**
	 * Determine the index of this
	 * 
	 * @param offset
	 *            The offset that must be contained by an error in this list
	 * @return Returns the index of the error that contains the specified index. -1 is returned if the offset is not
	 *         contained by any of the errors in this list
	 */
	public int indexOf(int offset)
	{
		return Collections.binarySearch(this._errors, new Offset(offset));
	}

	/**
	 * Remove the specified error node
	 * 
	 * @param error
	 *            The error to remove
	 */
	public void remove(IErrorMessage error)
	{
		int index = this._errors.indexOf(error);

		if (0 <= index && index < this._errors.size() && this.get(index) == error)
		{
			// remove node
			this._errors.remove(index);

			// clear out reference to parent
			error.setOwningList(null);
		}
		else
		{
			throw new IllegalStateException(Messages.ErrorList_ListDoesNotContainErrorBeingDeleted);
		}
	}

	/**
	 * Remove all errors in the specified range
	 * 
	 * @param range
	 *            The source offset range to remove from this list
	 */
	public void remove(IRange range)
	{
		this.remove(range.getStartingOffset(), range.getEndingOffset());
	}

	/**
	 * Remove all errors in the specified offset range
	 * 
	 * @param startingOffset
	 *            The starting offset to remove
	 * @param endingOffset
	 *            The ending offset to remove
	 */
	public void remove(int startingOffset, int endingOffset)
	{
		int startingIndex = this.indexOf(startingOffset);
		int endingIndex = this.indexOf(endingOffset);

		if (startingIndex < 0)
		{
			startingIndex = -(startingIndex + 1);
		}

		if (endingIndex < 0)
		{
			endingIndex = -(endingIndex + 1);
		}
		else
		{
			endingIndex++;
		}

		int count = endingIndex - startingIndex;

		while (count > 0)
		{
			// This will end up calling remove(IErrorMessage) above which will to the actual delete. We need to call
			// clear
			// to null out the lexeme parent nodes
			this.get(startingIndex).clear();

			count--;
		}
	}
}
