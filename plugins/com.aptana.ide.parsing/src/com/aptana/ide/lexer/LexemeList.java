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
public class LexemeList
{
	private static final Lexeme[] NO_LEXEMES = new Lexeme[0];
	
	private transient Lexeme[] _lexemes;
	private int _size;
	private Range _affectedRegion;

	/**
	 * Create a new instance of LexemeList
	 */
	public LexemeList()
	{
		this._lexemes = new Lexeme[128];
		this._affectedRegion = new Range();
	}
	
	/**
	 * Create a new instance of LexemeList
	 */
	public LexemeList(Lexeme[] lexemes)
	{
		this.setContents(lexemes);
	}

	/**
	 * add
	 * 
	 * @param index
	 * @param lexeme
	 */
	private void add(int index, Lexeme lexeme)
	{
		// NOTE: not range checking nor checking for nulls since
		// we can only call this method internally. We assume we
		// know what we're doing
		
		int currentLength = this._lexemes.length;
		int size = this._size + 1;
		
		// see if the size we need is within our current buffer size
		if (size > currentLength)
		{
			// it's not, add about 50% to our current buffer size
			int newLength = (currentLength * 3) / 2 + 1;

			// create a new empty list
			Lexeme[] newList = new Lexeme[newLength];
			
			// move the current contents to our new list
			System.arraycopy(this._lexemes, 0, newList, 0, this._size);
			
			// set out current list to the new list
			this._lexemes = newList;
		}
		
		// shift the contents over by one leaving a hole where the new lexeme will go
		System.arraycopy(this._lexemes, index, this._lexemes, index + 1, this._size - index);
		
		// place the lexeme into the hole
		this._lexemes[index] = lexeme;
		
		// update the current size
		this._size++;
	}
	
	/**
	 * Add a lexeme to this list. If a lexeme already exists within the range
	 * of the lexeme being added, then it is assume the lexeme is the same and
	 * it will not be added to the list.
	 * 
	 * When the lexeme is added to the list, items following the newly added
	 * lexeme are removed if they overlap the new lexeme's range.
	 * 
	 * @param lexeme
	 *            The lexeme to add to the end of this list
	 */
	synchronized public void add(Lexeme lexeme)
	{
		if (lexeme == null || lexeme.offset < 0)
		{
			throw new IllegalArgumentException(Messages.LexemeList_Lexeme_Must_Be_Defined);
		}

		int lexemeIndex = this.getLexemeIndex(lexeme.offset);

		// only process if we don't have the specified lexeme in our list already
		if (lexemeIndex < 0)
		{
			int insertIndex = -(lexemeIndex + 1);

			// insert into our list
			this.add(insertIndex, lexeme);
			
			// update our affected region
			this._affectedRegion.includeInRange(lexeme);

			// clear out any following lexemes that have been incorporated into this new lexeme
			int followingIndex = insertIndex + 1;

			while (followingIndex < this._size)
			{
				if (this._lexemes[followingIndex].isOverlapping(lexeme))
				{
					// include removed lexeme in affected region
					this._affectedRegion.includeInRange(this._lexemes[followingIndex]);

					// remove lexeme
					this.remove(followingIndex);
				}
				else
				{
					break;
				}
			}
		}
	}
	
	/**
	 * Clear this lexeme list
	 */
	synchronized public void clear()
	{
		// clear our lexeme array list
		for (int i = 0; i < this._size; i++)
		{
			this._lexemes[i] = null;
		}
		
		// reset our size
		this._size = 0;

		// clear affected range
		this._affectedRegion.clear();
	}

	/**
	 * Clone a range of lexemes from this list and return an array of those elements.
	 * Note that if a lexeme throws a CloneNotSupportedException, then that array
	 * element will have a reference to the lexeme as if a copyRange had been called.
	 * 
	 * @param startingIndex
	 *            The starting offset of the range to copy
	 * @param endingIndex
	 *            The ending offset of the range to copy. The item at this index is included in the result
	 * @return Returns an array of the specified elements. An empty array is returned if the range is invalid for this
	 *         list
	 */
	synchronized public Lexeme[] cloneRange(int startingIndex, int endingIndex)
	{
		Lexeme[] result = NO_LEXEMES;
		
		if
		(
				0 <= startingIndex && startingIndex < this._size
			&&	0 <= endingIndex && endingIndex < this._size
			&&	startingIndex <= endingIndex
		)
		{
			int size = endingIndex - startingIndex + 1;
			result = new Lexeme[size];
			
			for (int i = startingIndex; i <= endingIndex; i++)
			{
				Lexeme lexeme = this._lexemes[i];
				
				try
				{
					result[i - startingIndex] = (Lexeme) lexeme.clone();
				}
				catch (Exception e)
				{
					result[i - startingIndex] = lexeme;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Copy a range of lexemes from this list and return an array of those elements.
	 * 
	 * 
	 * @param startingIndex
	 *            The starting offset of the range to copy
	 * @param endingIndex
	 *            The ending offset of the range to copy. The item at this index is included in the result
	 * @return Returns an array of the specified elements. An empty array is returned if the range is invalid for this
	 *         list
	 */
	synchronized public Lexeme[] copyRange(int startingIndex, int endingIndex)
	{
		Lexeme[] result = NO_LEXEMES;
		
		if
		(
				0 <= startingIndex && startingIndex < this._size
			&&	0 <= endingIndex && endingIndex < this._size
			&&	startingIndex <= endingIndex
		)
		{
			int size = endingIndex - startingIndex + 1;
			result = new Lexeme[size];
			
			System.arraycopy(this._lexemes, startingIndex, result, 0, size);
		}
		
		return result;
	}

	/**
	 * Get a lexeme at the specified index. This method will return null if the index
	 * is not within the range of this lexeme list
	 * 
	 * @param index
	 *            The index to retrieve
	 * @return The lexeme at the specified index
	 */
	synchronized public Lexeme get(int index)
	{
		Lexeme result = null;
		
		if (0 <= index && index < this._size)
		{
			result = this._lexemes[index];
		}
		
		return result;
	}

	/**
	 * Get the range of offsets that have been affected in this lexeme list
	 * 
	 * @return Returns the offsets that have been affected in this list
	 */
	synchronized public Range getAffectedRegion()
	{
		return this._affectedRegion;
	}

	/**
	 * Gets the lexeme at the specified offset. If it is a whitespace character it will return the next (higher) lexeme
	 * if one exists. If not found it will return null.
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset or the lexeme immediately following the offset if none exists at
	 *         the given offset
	 */
	synchronized public Lexeme getCeilingLexeme(int offset)
	{
		int index = this.getLexemeCeilingIndex(offset);
		Lexeme result = null;

		if (index >= 0)
		{
			result = this._lexemes[index];
		}

		return result;
	}

	/**
	 * Gets the lexeme at the specified offset. If it is a whitespace character it will return the previous (lower)
	 * lexeme if one exists. If not found it will return null.
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset or the lexeme immediately preceding the offset if none exists at
	 *         the given offset
	 */
	synchronized public Lexeme getFloorLexeme(int offset)
	{
		int index = this.getLexemeFloorIndex(offset);
		Lexeme result = null;

		if (index >= 0)
		{
			result = this._lexemes[index];
		}

		return result;
	}

	/**
	 * Get the index of the lexeme at the specified offset. If it is a whitespace character it will return the next
	 * (higher) lexeme if one exists. If not found it will return -1.
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset or the lexeme immediately following the offset if none exists at
	 *         the given offset
	 */
	synchronized public int getLexemeCeilingIndex(int offset)
	{
		int length = this._size;
		int result = -1;

		if (length > 0)
		{
			// find index in our collection
			result = this.getLexemeIndex(offset);

			// see if we're in between lexemes
			if (result < 0)
			{
				// we are in between lexemes, so find the lexeme index to our right
				result = -(result + 1);

				// make sure we're in a valid range
				if (result >= length)
				{
					// we're past the end of our list, so return -1
					result = -1;
				}
			}
		}

		return result;
	}

	/**
	 * Get the index of the lexeme at the specified offset. If it is a whitespace character it will return the previous
	 * (lower) lexeme if one exists. If not found it will return -1.
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset or the lexeme immediately preceding the offset if none exists at
	 *         the given offset
	 */
	synchronized public int getLexemeFloorIndex(int offset)
	{
		int result = -1;

		if (this._size > 0)
		{
			// find index in our collection
			result = this.getLexemeIndex(offset);

			// see if we're in between lexemes
			if (result < 0)
			{
				// we are in between lexemes, so find the lexeme index to our left
				result = -(result + 1) - 1;

				// make sure we're in a valid range
				if (result < 0)
				{
					// we're before the start of our list, so return -1
					result = -1;
				}
			}
		}

		return result;
	}

	/**
	 * Get the index of the lexeme at the specified offset
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset. Returns null if no lexeme is at the given offset.
	 */
	synchronized public Lexeme getLexemeFromOffset(int offset)
	{
		int index = this.getLexemeIndex(offset);
		Lexeme result = null;
		
		if ( 0 <= index && index < this._size)
		{
			result = this._lexemes[index];
		}

		return result;
	}

	/**
	 * Get the index of the lexeme at the specified offset
	 * 
	 * @param offset
	 * @return Returns the index of the lexeme at the given offset. A negative value will be returned if there is no
	 *         lexeme at the given offset
	 */
	synchronized public int getLexemeIndex(int offset)
	{
		int low = 0;
		int high = this._size - 1;
		
		while (low <= high)
		{
			int mid = (low + high) >>> 1;
			Lexeme candidate = this._lexemes[mid];
			
			if (offset < candidate.offset)
			{
				high = mid - 1;
			}
			else if (candidate.offset + candidate.length <= offset)
			{
				low = mid + 1;
			}
			else
			{
				return mid;
			}
		}
		
		return -(low + 1);
	}

	/**
	 * Get the index of the specified lexeme. Returns -1 if the lexeme list is not
	 * in this list
	 * 
	 * @param lexeme
	 * @return Returns the index of the specified lexeme
	 */
	synchronized public int getLexemeIndex(Lexeme lexeme)
	{
		int result = -1;
		
		if (lexeme != null)
		{
			int candidate = this.getLexemeIndex(lexeme.offset);
			
			if (candidate >= 0 && this._lexemes[candidate] == lexeme)
			{
				result = candidate;
			}
		}
		
		return result;
	}

	/**
	 * Remove the specified index from our list of lexemes
	 * 
	 * @param index
	 *            The index to remove from this list
	 */
	synchronized public void remove(int index)
	{
		if (0 <= index && index < this._size)
		{
			int remainder = this._size - index - 1;
	
			// if we have content after the index being remove, then shift that over one slot
			if (remainder > 0)
			{
				System.arraycopy(this._lexemes, index + 1, this._lexemes, index, remainder);
			}
			
			// reduce our buffer size
			this._size--;
			
			// free up the last reference that is no longer part of the active region
			this._lexemes[this._size] = null;
		}
	}

	/**
	 * Remove the specified range of lexemes from our list
	 * 
	 * @param startingIndex
	 *            The starting index to remove
	 * @param endingIndex
	 *            The ending index to remove
	 */
	synchronized public void remove(int startingIndex, int endingIndex)
	{
		if
		(
				0 <= startingIndex && startingIndex < this._size
			&&	0 <= endingIndex   && endingIndex   < this._size
			&&	startingIndex <= endingIndex
		)
		{
			for (int i = startingIndex; i <= endingIndex; i++)
			{
				// NOTE: Always remove the current starting index since lexemes
				// shift left after each remove
				this.remove(startingIndex);
			}
		}
	}

	/**
	 * Remove the specified lexeme from our list
	 * 
	 * @param lexeme
	 *            The lexeme to remove from this list
	 */
	synchronized public void remove(Lexeme lexeme)
	{
		if (lexeme != null)
		{
			int index = this.getLexemeIndex(lexeme.offset);
	
			if (index >= 0 && this._lexemes[index] == lexeme)
			{
				this.remove(index);
			}
		}
	}

	/**
	 * Remove the specified range of lexemes from our list
	 * 
	 * @param startingLexeme
	 *            The starting lexeme of the range to remove from this list
	 * @param endingLexeme
	 *            The ending lexeme of the range to remove form this list
	 */
	synchronized public void remove(Lexeme startingLexeme, Lexeme endingLexeme)
	{
		if (startingLexeme != null && endingLexeme != null)
		{
			int startingIndex = this.getLexemeIndex(startingLexeme.offset);
			int endingIndex = this.getLexemeIndex(endingLexeme.offset);
	
			if
			(		startingIndex	>= 0
				&&	endingIndex		>= 0
				&&	startingIndex	<= endingIndex
				&&	startingLexeme	== this._lexemes[startingIndex]
				&&	endingLexeme	== this._lexemes[endingIndex]
			)
			{
				// NOTE: Always remove the current starting index since lexemes
				// shift left after each remove
				for (int i = startingIndex; i <= endingIndex; i++)
				{
					this.remove(startingIndex);
				}
			}
		}
	}

	/**
	 * Shift the offset of all lexemes beginning with the specified index
	 * 
	 * @param startingIndex
	 *            The beginning lexeme to shift
	 * @param offsetDelta
	 *            The amount by which to shift each lexeme's offset
	 */
	synchronized public void shiftLexemeOffsets(int startingIndex, int offsetDelta)
	{
		if (0 <= startingIndex)
		{
			for (int i = startingIndex; i < this._size; i++)
			{
				this._lexemes[i].adjustOffset(offsetDelta);
			}
		}
	}

	/**
	 * Return the size of this list
	 * 
	 * @return The list size
	 */
	synchronized public int size()
	{
		return this._size;
	}

	/**
	 * converts lexeme list to plain array
	 * @return array
	 */
	synchronized public Lexeme[] toArray()
	{
		Lexeme[] result = NO_LEXEMES;
		
		if (this._size > 0)
		{
			result = this.copyRange(0, this._size - 1);
		}
		
		return result;
	}

	/**
	 * Allows to initialize lexeme list from array of lexemes
	 * @param array
	 */
	synchronized public void setContents(Lexeme[] lexemes)
	{
		if (lexemes == null)
		{
			throw new IllegalArgumentException(Messages.LexemeList_Lexeme_Must_Be_Defined);
		}
		for (Lexeme lexeme : lexemes)
		{
			if (lexeme == null)
			{
				throw new IllegalArgumentException(Messages.LexemeList_Lexeme_Must_Be_Defined);
			}
		}
		
		this._lexemes = lexemes;
		this._size = lexemes.length;
		this._affectedRegion = new Range();
	}
}
