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

import org.eclipse.swt.graphics.Color;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ColorizationStyle
{

	private Color foregroundColor;
	private Color backgroundColor;
	private boolean underline;
	private boolean strikethrough;
	private int offset;
	private int length;
	private String direction;
	private String name;
	private boolean bold;
	private boolean italic;

	/**
	 * Creates a new colorization style
	 * 
	 * @param foregroundColor -
	 *            foreground color
	 * @param backgroundColor -
	 *            background color
	 * @param bold
	 * @param italic
	 * @param underline -
	 *            underline text
	 * @param strikethrough -
	 *            strike out text
	 */
	public ColorizationStyle(Color foregroundColor, Color backgroundColor, boolean bold, boolean italic,
			boolean underline, boolean strikethrough)
	{
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;
		this.underline = underline;
		this.strikethrough = strikethrough;
		this.offset = -1;
		this.length = -1;
		this.direction = null;
		this.bold = bold;
		this.italic = italic;
	}

	/**
	 * Creates a colorization style by copying values from another colorization style
	 * 
	 * @param style -
	 *            style to clone
	 */
	public ColorizationStyle(ColorizationStyle style)
	{
		if (style != null)
		{
			this.foregroundColor = style.getForegroundColor();
			this.backgroundColor = style.getBackgroundColor();
			this.underline = style.isUnderline();
			this.bold = style.isBold();
			this.italic = style.isItalic();
			this.name = style.getName();
		}
		else
		{
			this.foregroundColor = null;
			this.backgroundColor = null;
			this.underline = false;
			this.bold = false;
			this.italic = false;
		}
	}

	/**
	 * Creates a new colorization style
	 */
	public ColorizationStyle()
	{
		this.foregroundColor = null;
		this.backgroundColor = null;
		this.underline = false;
		this.strikethrough = false;
		this.offset = -1;
		this.length = -1;
		this.direction = null;
		this.bold = false;
		this.italic = false;
	}

	/**
	 * Gets the background color
	 * 
	 * @return - background Color object
	 */
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}

	/**
	 * Sets the background color
	 * 
	 * @param backgroundColor -
	 *            new background color
	 */
	public void setBackgroundColor(Color backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Gets the foreground color
	 * 
	 * @return - foreground Color object
	 */
	public Color getForegroundColor()
	{
		return foregroundColor;
	}

	/**
	 * Sets the foreground color
	 * 
	 * @param foregroundColor -
	 *            new foreground color
	 */
	public void setForegroundColor(Color foregroundColor)
	{
		this.foregroundColor = foregroundColor;
	}

	/**
	 * Gets the direction that this style should be applied to
	 * 
	 * @return - direction value
	 */
	public String getDirection()
	{
		return direction;
	}

	/**
	 * Sets the direction value
	 * 
	 * @param direction -
	 *            new direction
	 */
	public void setDirection(String direction)
	{
		this.direction = direction;
	}

	/**
	 * Gets the length of this colorization
	 * 
	 * @return - length of colorization
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * Sets the length of this colorization
	 * 
	 * @param length -
	 *            new length
	 */
	public void setLength(int length)
	{
		this.length = length;
	}

	/**
	 * Gets the offset of this colorization
	 * 
	 * @return - offset value
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * Sets the offset of this colorization
	 * 
	 * @param offset -
	 *            new offset
	 */
	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	/**
	 * Gets the strikehrough value
	 * 
	 * @return - true if stikethrough, false otherwise
	 */
	public boolean isStrikethrough()
	{
		return strikethrough;
	}

	/**
	 * Sets the strikethrough value
	 * 
	 * @param strikethrough -
	 *            new strikethrough value
	 */
	public void setStrikethrough(boolean strikethrough)
	{
		this.strikethrough = strikethrough;
	}

	/**
	 * Gets the underline value
	 * 
	 * @return - true if underlined, false otherwise
	 */
	public boolean isUnderline()
	{
		return underline;
	}

	/**
	 * Sets the underline value
	 * 
	 * @param underline -
	 *            new underline value
	 */
	public void setUnderline(boolean underline)
	{
		this.underline = underline;
	}

	/**
	 * getName
	 * 
	 * @return name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * setName
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * isBold
	 * 
	 * @return boolean
	 */
	public boolean isBold()
	{
		return bold;
	}

	/**
	 * setBold
	 * 
	 * @param bold
	 */
	public void setBold(boolean bold)
	{
		this.bold = bold;
	}

	/**
	 * isItalic
	 * 
	 * @return boolean
	 */
	public boolean isItalic()
	{
		return this.italic;
	}

	/**
	 * setItalic
	 * 
	 * @param italic
	 */
	public void setItalic(boolean italic)
	{
		this.italic = italic;
	}
}
