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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;

import com.aptana.ide.lexer.IToken;

/**
 * Colorizer for a token object.
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class TokenColorizer implements IColorizer
{
	private IToken token;
	private ColorizationStyle style;
	private Map<String, Region> regions;

	/**
	 * Creates a new token colorizer
	 */
	public TokenColorizer()
	{
		regions = new HashMap<String, Region>();
		this.style = null;
		this.token = null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.colorizer.IColorizer#colorize(java.util.List, int, int)
	 */
	public void colorize(List styles, int offset, int length)
	{
		int baseOffset = -1;
		int baseLength = 0;
		Collection regions = getRegions();
		for (int i = 0; i < length; i++)
		{
			Region region = null;
			Iterator iter = regions.iterator();
			while (iter.hasNext())
			{
				region = (Region) iter.next();
				if (region.getOffset(length) == i)
				{
					break;
				}
				else
				{
					region = null;
				}
			}
			if (region != null)
			{
				if (baseLength != 0 && baseOffset != -1)
				{
					int bold = style.isBold() ? SWT.BOLD : 0;
					int italic = style.isItalic() ? SWT.ITALIC : 0;
					StyleRange sr = new StyleRange(baseOffset, baseLength, style.getForegroundColor(), style
							.getBackgroundColor(), bold | italic);
					sr.underline = style.isUnderline();
					styles.add(sr);
					baseLength = 0;
					baseOffset = -1;
				}
				ColorizationStyle regionStyle = region.getStyle();
				int bold = regionStyle.isBold() ? SWT.BOLD : 0;
				int italic = regionStyle.isItalic() ? SWT.ITALIC : 0;
				int regionLength = region.getLength(length);

				if (regionLength > 0)
				{
					StyleRange sr = new StyleRange(offset + region.getOffset(length), regionLength, regionStyle
							.getForegroundColor(), regionStyle.getBackgroundColor(), bold | italic);
					sr.underline = regionStyle.isUnderline();
					styles.add(sr);
				}

				int nextPosition = regionLength - 1;
				if (nextPosition > 0)
				{
					i += nextPosition;
				}
			}
			else
			{
				baseLength++;
				if (baseOffset == -1)
				{
					baseOffset = offset + i;
				}
			}

		}
		if (baseLength != 0 && baseOffset != -1)
		{
			int bold = style.isBold() ? SWT.BOLD : 0;
			int italic = style.isItalic() ? SWT.ITALIC : 0;
			StyleRange sr = new StyleRange(baseOffset, baseLength, style.getForegroundColor(), style
					.getBackgroundColor(), bold | italic);
			sr.underline = style.isUnderline();
			styles.add(sr);
			baseLength = 0;
			baseOffset = -1;
		}
	}

	/**
	 * Adds a sub token colorizer.
	 * 
	 * @param stc -
	 *            sub token colorizer
	 */
	public void addSubTokenColorization(SubTokenColorizer stc)
	{

	}

	/**
	 * @see com.aptana.ide.editors.unified.colorizer.IColorizer#getToken()
	 */
	public IToken getToken()
	{
		return this.token;
	}

	/**
	 * @see com.aptana.ide.editors.unified.colorizer.IColorizer#setToken(com.aptana.ide.lexer.IToken)
	 */
	public void setToken(IToken token)
	{
		this.token = token;
	}

	/**
	 * @see com.aptana.ide.editors.unified.colorizer.IColorizer#setBaseColorization(com.aptana.ide.editors.unified.colorizer.ColorizationStyle)
	 */
	public void setBaseColorization(ColorizationStyle style)
	{
		this.style = style;
	}

	/**
	 * getBaseColorization
	 * 
	 * @return ColorizationStyle
	 */
	public ColorizationStyle getBaseColorization()
	{
		return this.style;
	}

	/**
	 * @see com.aptana.ide.editors.unified.colorizer.IColorizer#addColorization(com.aptana.ide.editors.unified.colorizer.Region)
	 */
	public void addColorization(Region region)
	{
		regions.put(region.getName(), region);
	}

	/**
	 * @see com.aptana.ide.editors.unified.colorizer.IColorizer#removeColorization(java.lang.String)
	 */
	public void removeColorization(String name)
	{
		regions.remove(name);
	}

	/**
	 * Gets the regions
	 * 
	 * @return - regions
	 */
	public Collection getRegions()
	{
		return regions.values();
	}

	/**
	 * Gets a region by a name
	 * 
	 * @param name
	 * @return - Region
	 */
	public Region getRegion(String name)
	{
		return (Region) regions.get(name);
	}
}
