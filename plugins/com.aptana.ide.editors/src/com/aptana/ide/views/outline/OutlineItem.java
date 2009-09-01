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
package com.aptana.ide.views.outline;

import com.aptana.ide.lexer.IRange;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class OutlineItem
{
	private String _label;
	private String _language;
	private int _type;
	private IRange _sourceRange;
	private int _childCount;
	private IParseNode _referenceNode;

	/**
	 * JSOutlineItem
	 * 
	 * @param label 
	 * @param language
	 * @param type 
	 * @param sourceRange 
	 * @param referenceNode 
	 * @param childCount 
	 */
	public OutlineItem(String label, String language, int type, IRange sourceRange, IParseNode referenceNode, int childCount)
	{
		if (label == null || label.length() == 0)
		{
			throw new IllegalArgumentException(Messages.JSOutlineItem_Label_Not_Defined);
		}
		if (sourceRange == null)
		{
			throw new IllegalArgumentException(Messages.JSOutlineItem_Source_Range_Not_Defined);
		}
		
		this._label = label;
		this._language = language;
		this._type = type;
		this._sourceRange = sourceRange;
		this._referenceNode = referenceNode;
		this._childCount = childCount;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		boolean result;
		
		if (obj instanceof OutlineItem)
		{
			OutlineItem that = (OutlineItem) obj;
			
			result = this.getLabel().equals(that.getLabel());
		}
		else
		{
			result = super.equals(obj);
		}
		
		return result;
	}

	/**
	 * getChildCount
	 *
	 * @return child count
	 */
	public int getChildCount()
	{
		return this._childCount;
	}
	
	/**
	 * getReferenceNode
	 *
	 * @return reference parse node
	 */
	public IParseNode getReferenceNode()
	{
		return this._referenceNode;
	}
	
	/**
	 * getImage
	 *
	 * @return type
	 */
	public int getType()
	{
		return this._type;
	}
	
	/**
	 * getLabel
	 * 
	 * @return label
	 */
	public String getLabel()
	{
		return this._label;
	}

	/**
	 * getLanguage
	 *
	 * @return String
	 */
	public String getLanguage()
	{
		return this._language;
	}
	
	/**
	 * getEndingOffset
	 *
	 * @return ending offset
	 */
	public int getEndingOffset()
	{
		return this._sourceRange.getEndingOffset();
	}

	/**
	 * getStartingOffset
	 *
	 * @return starting offset
	 */
	public int getStartingOffset()
	{
		return this._sourceRange.getStartingOffset();
	}
	
	/**
	 * hasChildren
	 *
	 * @return returns true if this item has children
	 */
	public boolean hasChildren()
	{
		return (this.getChildCount() > 0);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return super.hashCode();
	}
}
