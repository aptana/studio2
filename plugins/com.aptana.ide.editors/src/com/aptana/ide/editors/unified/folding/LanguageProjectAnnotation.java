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
package com.aptana.ide.editors.unified.folding;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.projection.UnifiedProjectionAnnotation;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.ImageData;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class LanguageProjectAnnotation extends UnifiedProjectionAnnotation
{

	private String language;
	private String nodeType;
	private ImageDescriptor collapsed;
	private ImageDescriptor expanded;

	/**
	 * Creates a new LanguageProjectAnnotation for a language and node type
	 * 
	 * @param language -
	 *            mime type
	 * @param type -
	 *            node type
	 */
	public LanguageProjectAnnotation(String language, String type)
	{
		super();
		this.language = language;
		this.nodeType = type;
	}

	/**
	 * Gets the language for this annotation.
	 * 
	 * @return - language mime type
	 */
	public String getLanguage()
	{
		return language;
	}

	/**
	 * Sets the language of this annotation
	 * 
	 * @param language -
	 *            mime type
	 */
	public void setLanguage(String language)
	{
		this.language = language;
	}

	/**
	 * Gets the node type for this annotation
	 * 
	 * @return - node type name
	 */
	public String getNodeType()
	{
		return nodeType;
	}

	/**
	 * Sets the node type of this annotation
	 * 
	 * @param type -
	 *            node type name
	 */
	public void setNodeType(String type)
	{
		this.nodeType = type;
	}

	/**
	 * @see org.eclipse.jface.text.source.projection.UnifiedProjectionAnnotation#getCollapsedImage()
	 */
	public ImageDescriptor getCollapsedImage()
	{
		if (collapsed != null)
		{
			return collapsed;
		}
		return super.getCollapsedImage();
	}

	/**
	 * @see org.eclipse.jface.text.source.projection.UnifiedProjectionAnnotation#getExpandedImage()
	 */
	public ImageDescriptor getExpandedImage()
	{
		if (expanded != null)
		{
			return expanded;
		}
		return super.getExpandedImage();
	}

	/**
	 * @param collapsed
	 *            the collapsed to set
	 */
	public void setCollapsed(ImageDescriptor collapsed)
	{
		this.collapsed = collapsed;
	}

	/**
	 * @param expanded
	 *            the expanded to set
	 */
	public void setExpanded(ImageDescriptor expanded)
	{
		this.expanded = expanded;
	}

	/**
	 * Sets collapsed image with color overdrawn
	 * 
	 * @param collapsed
	 * @param bgcolor
	 * @param fgcolor
	 */
	public void setCollapsedImage(ImageDescriptor collapsed, Color bgcolor, Color fgcolor)
	{
		this.setCollapsed(collapsed);
		this.setCollapsedImage(bgcolor, fgcolor);
	}

	private int getColorValue(Color color)
	{
		String redHex = Integer.toHexString(color.getRed());
		if (redHex.length() == 1)
		{
			redHex = "0" + redHex; //$NON-NLS-1$
		}
		String greenHex = Integer.toHexString(color.getGreen());
		if (greenHex.length() == 1)
		{
			greenHex = "0" + greenHex; //$NON-NLS-1$
		}
		String blueHex = Integer.toHexString(color.getBlue());
		if (blueHex.length() == 1)
		{
			blueHex = "0" + blueHex; //$NON-NLS-1$
		}
		return Integer.parseInt(redHex + greenHex + blueHex, 16);
	}

	/**
	 * Overdraws color on collapsed image
	 * 
	 * @param bgcolor
	 * @param fgcolor
	 */
	public void setCollapsedImage(Color bgcolor, Color fgcolor)
	{
		if (this.collapsed != null)
		{
			int colorValue = getColorValue(bgcolor);
			int fgValue = fgcolor != null ? getColorValue(fgcolor) : -1;
			ImageData data = this.collapsed.getImageData();
			int redMask = data.palette.redMask;
			int blueMask = data.palette.blueMask;
			int greenMask = data.palette.greenMask;
			int[] lineData = new int[data.width];
			for (int y = 0; y < data.height; y++)
			{
				data.getPixels(0, y, data.width, lineData, 0);
				// Analyze each pixel value in the line
				for (int x = 0; x < lineData.length; x++)
				{
					// Extract the red, green and blue component
					int pixelValue = lineData[x];
					int r, g, b;
					if (data.depth >= 24)
					{
						r = (pixelValue & redMask) >> 16;
						g = (pixelValue & greenMask) >> 8;
						b = (pixelValue & blueMask);
					}
					else
					{
						r = pixelValue & redMask;
						g = (pixelValue & greenMask) >> 8;
						b = (pixelValue & blueMask) >> 16;
					}
					if (r == 158 && g == 158 && b == 158)
					{
						data.setPixel(x, y, colorValue);
					}
					else if (r == 255 && g == 255 && b == 255 && fgValue > -1)
					{
						data.setPixel(x, y, fgValue);
					}
				}
			}
			this.collapsed = ImageDescriptor.createFromImageData(data);
		}
	}

	/**
	 * Sets expanded image with color overdrawn
	 * 
	 * @param expanded
	 * @param bgcolor
	 * @param fgcolor
	 */
	public void setExpandedImage(ImageDescriptor expanded, Color bgcolor, Color fgcolor)
	{
		this.setExpanded(expanded);
		this.setExpandedImage(bgcolor, fgcolor);
	}

	/**
	 * Overdraws color on expanded image
	 * 
	 * @param bgcolor
	 * @param fgcolor
	 */
	public void setExpandedImage(Color bgcolor, Color fgcolor)
	{
		if (this.expanded != null)
		{
			int colorValue = getColorValue(bgcolor);
			int fgValue = fgcolor != null ? getColorValue(fgcolor) : -1;
			ImageData data = this.expanded.getImageData();
			int redMask = data.palette.redMask;
			int blueMask = data.palette.blueMask;
			int greenMask = data.palette.greenMask;
			int[] lineData = new int[data.width];
			for (int y = 0; y < data.height; y++)
			{
				data.getPixels(0, y, data.width, lineData, 0);
				// Analyze each pixel value in the line
				for (int x = 0; x < lineData.length; x++)
				{
					// Extract the red, green and blue component
					int pixelValue = lineData[x];
					int r, g, b;
					if (data.depth >= 24)
					{
						r = (pixelValue & redMask) >> 16;
						g = (pixelValue & greenMask) >> 8;
						b = (pixelValue & blueMask);
					}
					else
					{
						r = pixelValue & redMask;
						g = (pixelValue & greenMask) >> 8;
						b = (pixelValue & blueMask) >> 16;
					}
					if (r == 158 && g == 158 && b == 158)
					{
						data.setPixel(x, y, colorValue);
					}
					else if (r == 255 && g == 255 && b == 255 && fgValue > -1)
					{
						data.setPixel(x, y, fgValue);
					}
				}
			}
			this.expanded = ImageDescriptor.createFromImageData(data);
		}
	}

}
