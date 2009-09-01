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
package com.aptana.ide.core.ui;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * A ColorPair is a combination of a foreground and a background color.
 * 
 * @author Ingo Muschenetz
 */
public class ColorPair
{

	private RGB _foregroundColor;
	private RGB _backgroundColor;

	/**
	 * The default-default value for color preferences (black, <code>RGB(0,0,0)</code>).
	 */
	public static final ColorPair COLOR_DEFAULT_DEFAULT = new ColorPair(new RGB(0, 0, 0), null);

	/**
	 * Returns the background color of this pair
	 * 
	 * @return The background color
	 */
	public RGB getBackgroundColor()
	{
		return _backgroundColor;
	}

	/**
	 * Set the background color
	 * 
	 * @param backgroundColor
	 */
	public void setBackgroundColor(RGB backgroundColor)
	{
		this._backgroundColor = backgroundColor;
	}

	/**
	 * Returns the foreground color of this pair
	 * 
	 * @return The foreground color
	 */
	public RGB getForegroundColor()
	{
		return _foregroundColor;
	}

	/**
	 * Sets the foreground color
	 * 
	 * @param foregroundColor
	 */
	public void setForegroundColor(RGB foregroundColor)
	{
		this._foregroundColor = foregroundColor;
	}

	/**
	 * Creates a new instance of ColorPair
	 * 
	 * @param foregroundColor
	 * @param backgroundColor
	 */
	public ColorPair(RGB foregroundColor, RGB backgroundColor)
	{
		this._foregroundColor = foregroundColor;
		this._backgroundColor = backgroundColor;
	}

	/**
	 * Helper method to construct a color from the given string.
	 * 
	 * @param value
	 *            the indentifier for the color
	 * @return ColorPair
	 */
	private static ColorPair basicGetColorPair(String value)
	{

		ColorPair color = asColorPair(value);
		if (color == null)
		{
			return COLOR_DEFAULT_DEFAULT;
		}
		return color;
	}

	/**
	 * Returns the current value of the color-valued preference with the given name in the given
	 * preference store. Returns the default-default value (<code>COLOR_DEFAULT_DEFAULT</code>)
	 * if there is no preference with the given name, or if the current value cannot be treated as a
	 * color.
	 * 
	 * @param store
	 *            the preference store
	 * @param name
	 *            the name of the preference
	 * @return the color-valued preference
	 */
	public static ColorPair getColorPair(IPreferenceStore store, String name)
	{
		return basicGetColorPair(store.getString(name));
	}

	/**
	 * Returns the default value for the color-valued preference with the given name in the given
	 * preference store. Returns the default-default value (<code>COLOR_DEFAULT_DEFAULT</code>)
	 * is no default preference with the given name, or if the default value cannot be treated as a
	 * color.
	 * 
	 * @param store
	 *            the preference store
	 * @param name
	 *            the name of the preference
	 * @return the default value of the preference
	 */
	public static ColorPair getDefaultColorPair(IPreferenceStore store, String name)
	{
		return basicGetColorPair(store.getDefaultString(name));
	}

	/**
	 * Sets the default value of the preference with the given name in the given preference store.
	 * 
	 * @param store
	 *            the preference store
	 * @param name
	 *            the name of the preference
	 * @param value
	 *            the new default value of the preference
	 */
	public static void setDefault(IPreferenceStore store, String name, ColorPair value)
	{
		store.setDefault(name, asString(value));
	}

	/**
	 * Converts the given SWT RGB color pair value object to a string.
	 * <p>
	 * The string representation of an RGB color pair value has the form
	 * <code><it>red</it>,<it>green</it></code>,<it>blue</it></code> where <code><it>red</it></code>,
	 * <it>green</it></code>, and <code><it>blue</it></code> are string representations of
	 * integers. The two colors are separated by the form foreground;background
	 * </p>
	 * 
	 * @param value
	 *            the RGB color pair value object
	 * @return the string representing the given RGB color value pair
	 */
	public static String asString(ColorPair value)
	{
//		Assert.isNotNull(value);
		StringBuffer buffer = new StringBuffer();
		buffer.append(StringConverter.asString(value.getForegroundColor()));
		if (value.getBackgroundColor() != null)
		{
			buffer.append(';');
			buffer.append(StringConverter.asString(value.getBackgroundColor()));
		}
		return buffer.toString();
	}

	/**
	 * Converts the given value into an SWT RGB color value. This method fails if the value does not
	 * represent an RGB color value.
	 * <p>
	 * A valid RGB color value representation is a string of the form
	 * <code><it>red</it>,<it>green</it></code>,<it>blue</it></code> where <code><it>red</it></code>,
	 * <it>green</it></code>, and <code><it>blue</it></code> are valid ints. ColorPairs are a
	 * combination of two colors, separated (in string form), be a ';'
	 * </p>
	 * 
	 * @param value
	 *            the value to be converted
	 * @return the value as an RGB color value
	 * @exception DataFormatException
	 *                if the given value does not represent an RGB color value
	 */
	public static ColorPair asColorPair(String value) throws DataFormatException
	{
		if (value == null)
		{
			throw new DataFormatException("Null doesn't represent a valid ColorPair"); //$NON-NLS-1$
		}

		StringTokenizer stok = new StringTokenizer(value, ";"); //$NON-NLS-1$

		try
		{
			String color1 = stok.nextToken();
			String color2 = null;
			if (stok.hasMoreTokens())
			{
				color2 = stok.nextToken();
				return new ColorPair(StringConverter.asRGB(color1), StringConverter.asRGB(color2));
			}
			else
			{
				return new ColorPair(StringConverter.asRGB(color1), null);
			}

		}
		catch (NoSuchElementException e)
		{
			throw new DataFormatException(e.getMessage());
		}
	}

	/**
	 * Sets the current value of the preference with the given name in the given preference store.
	 * 
	 * @param store
	 *            the preference store
	 * @param name
	 *            the name of the preference
	 * @param value
	 *            the new current value of the preference
	 */
	public static void setValue(IPreferenceStore store, String name, ColorPair value)
	{
		ColorPair oldValue = getColorPair(store, name);
		if (oldValue == null || !oldValue.equals(value))
		{
			store.putValue(name, ColorPair.asString(value));
			store.firePropertyChangeEvent(name, oldValue, value);
		}
	}

}
