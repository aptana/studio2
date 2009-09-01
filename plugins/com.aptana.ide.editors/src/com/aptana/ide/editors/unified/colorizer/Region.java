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
package com.aptana.ide.editors.unified.colorizer;

/**
 * Class reprenting a sub region of colorization for a token
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class Region
{

	private int offset;
	private int length;
	private ColorizationStyle style;
	private boolean relativeOffset;
	private boolean relativeLength;
	private String name;
	private String offsetString;
	private String lengthString;

	/**
	 * Creates a new region
	 * 
	 * @param offset
	 * @param relativeOffset
	 * @param length
	 * @param relativeLength
	 * @param style
	 */
	public Region(int offset, boolean relativeOffset, int length, boolean relativeLength, ColorizationStyle style)
	{
		this.offset = offset;
		this.length = length;
		this.style = style;
		this.relativeLength = relativeLength;
		this.relativeOffset = relativeOffset;
	}

	/**
	 * Creates a new region from a region
	 * 
	 * @param region -
	 *            region to copy
	 */
	public Region(Region region)
	{
		this.offset = region.offset;
		this.length = region.length;
		this.style = region.style;
		this.relativeLength = region.relativeLength;
		this.relativeOffset = region.relativeOffset;
		this.offsetString = region.offsetString;
		this.lengthString = region.lengthString;
	}

	/**
	 * Gets the length of the region
	 * 
	 * @param lexemeLength
	 * @return - length of region
	 */
	public int getLength(int lexemeLength)
	{
		if (relativeLength)
		{
			return lexemeLength + length;
		}
		else
		{
			return length;
		}
	}

	/**
	 * Sets the length of the region
	 * 
	 * @param length
	 */
	public void setLength(int length)
	{
		this.length = length;
	}

	/**
	 * Gets the offset of the region
	 * 
	 * @param lexemeLength
	 * @return - offset of region
	 */
	public int getOffset(int lexemeLength)
	{
		if (relativeOffset)
		{
			return lexemeLength + offset;
		}
		else
		{
			return offset;
		}
	}

	/**
	 * Sets the offset of the region
	 * 
	 * @param offset
	 */
	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	/**
	 * Gets the style of the region
	 * 
	 * @return - style of region
	 */
	public ColorizationStyle getStyle()
	{
		return style;
	}

	/**
	 * Sets the style of the region
	 * 
	 * @param style
	 */
	public void setStyle(ColorizationStyle style)
	{
		this.style = style;
	}

	/**
	 * Get name
	 * 
	 * @return - name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set name
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the length string
	 * 
	 * @return - length
	 */
	public String getLengthString()
	{
		return lengthString;
	}

	/**
	 * Sets the length string
	 * 
	 * @param lengthString
	 */
	public void setLengthString(String lengthString)
	{
		this.lengthString = lengthString;
	}

	/**
	 * Gets the offset string
	 * 
	 * @return - offset
	 */
	public String getOffsetString()
	{
		return offsetString;
	}

	/**
	 * Sets the offset string
	 * 
	 * @param offsetString
	 */
	public void setOffsetString(String offsetString)
	{
		this.offsetString = offsetString;
	}

	/**
	 * Is relative length
	 * 
	 * @return - relative length boolean
	 */
	public boolean isRelativeLength()
	{
		return relativeLength;
	}

	/**
	 * Set relative length
	 * 
	 * @param relativeLength
	 */
	public void setRelativeLength(boolean relativeLength)
	{
		this.relativeLength = relativeLength;
	}

	/**
	 * Is relative offset
	 * 
	 * @return - relative offset boolean
	 */
	public boolean isRelativeOffset()
	{
		return relativeOffset;
	}

	/**
	 * Set relative offset
	 * 
	 * @param relativeOffset
	 */
	public void setRelativeOffset(boolean relativeOffset)
	{
		this.relativeOffset = relativeOffset;
	}
}
