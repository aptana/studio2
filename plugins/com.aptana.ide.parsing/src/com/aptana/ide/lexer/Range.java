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
public class Range implements IRange
{
	/**
	 * An empty range singleton
	 */
	public static final Range Empty = new Range(0, 0);
	
	private int _startingOffset;
	private int _endingOffset;

	/**
	 * Create a new instance of Range
	 */
	public Range()
	{
		this.clear();
	}

	/**
	 * Create a new instance of Range
	 * 
	 * @param startingOffset
	 *            The range's starting offset
	 * @param endingOffset
	 *            The range's ending offset
	 */
	public Range(int startingOffset, int endingOffset)
	{
		this.setRange(startingOffset, endingOffset);
	}

	/**
	 * Clear this range to an empty range
	 */
	public void clear()
	{
		this._startingOffset = Integer.MAX_VALUE;
		this._endingOffset = Integer.MIN_VALUE;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#containsOffset(int)
	 */
	public boolean containsOffset(int offset)
	{
		boolean result = false;
		
		if (this.isEmpty() == false)
		{
			result = (this._startingOffset <= offset && offset <= this._endingOffset);
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getEndingOffset()
	 */
	public int getEndingOffset()
	{
		return this._endingOffset;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getLength()
	 */
	public int getLength()
	{
		return this._endingOffset - this._startingOffset;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getStartingOffset()
	 */
	public int getStartingOffset()
	{
		return this._startingOffset;
	}

	/**
	 * Make sure the given offset is included within this range
	 * 
	 * @param offset
	 *            The offset to include within this range
	 */
	public void includeInRange(int offset)
	{
		if (this._startingOffset > offset)
		{
			this._startingOffset = offset;
		}

		if (this._endingOffset < offset)
		{
			this._endingOffset = offset;
		}
	}

	/**
	 * Make sure the region defined by the given lexeme is included within this range
	 * 
	 * @param lexeme
	 *            The lexeme whose range needs to be included within this range
	 */
	public void includeInRange(Lexeme lexeme)
	{
		if (this.isEmpty())
		{
			this._startingOffset = lexeme.offset;
			this._endingOffset = lexeme.getEndingOffset();
		}
		else
		{
			this.includeInRange(lexeme.offset);
			this.includeInRange(lexeme.getEndingOffset());
		}
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#isEmpty()
	 */
	public boolean isEmpty()
	{
		return (this._startingOffset >= this._endingOffset);
	}

	/**
	 * Set the offsets of this range
	 * 
	 * @param startingOffset
	 *            The starting offset of this range
	 * @param endingOffset
	 *            The ending offset of this range
	 */
	public void setRange(int startingOffset, int endingOffset)
	{
		// sanity check
		if (startingOffset > endingOffset)
		{
			throw new IllegalArgumentException(Messages.Range_Swapped_Endpoints);
		}

		this._startingOffset = startingOffset;
		this._endingOffset = endingOffset;
	}

	/**
	 * Set this range to match the specified range
	 * 
	 * @param range
	 *            The range to copy to this range
	 */
	public void setRange(IRange range)
	{
		this.setRange(range.getStartingOffset(), range.getEndingOffset());
	}
}
