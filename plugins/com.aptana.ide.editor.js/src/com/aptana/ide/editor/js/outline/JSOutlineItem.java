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
package com.aptana.ide.editor.js.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IEditorInput;

import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.lexer.IRange;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.views.outline.IResolvableItem;
import com.aptana.ide.views.outline.OutlineItem;

/**
 * @author Kevin Lindsey
 */
public class JSOutlineItem extends OutlineItem implements IResolvableItem
{
	private List<IParseNode> _virtualChildren;

	
	private IEditorInput input;


	private IResolvableItem parent;

	/**
	 * JSOutlineItem
	 * 
	 * @param label
	 * @param type
	 * @param sourceRange
	 * @param referenceNode
	 */
	public JSOutlineItem(String label, int type, IRange sourceRange, IParseNode referenceNode)
	{
		super(label, JSMimeType.MimeType, type, sourceRange, referenceNode, 0);
	}

	/**
	 * JSOutlineItem
	 * 
	 * @param label
	 * @param type
	 * @param sourceRange
	 * @param referenceNode
	 * @param childCount
	 */
	public JSOutlineItem(String label, int type, IRange sourceRange, IParseNode referenceNode, int childCount)
	{
		super(label, JSMimeType.MimeType, type, sourceRange, referenceNode, childCount);
	}

	/**
	 * addVirtualChild
	 * 
	 * @param target
	 */
	public void addVirtualChild(IParseNode target)
	{
		if (target == null)
		{
			throw new IllegalArgumentException(Messages.JSOutlineItem_Target_Not_Defined);
		}

		if (this._virtualChildren == null)
		{
			this._virtualChildren = new ArrayList<IParseNode>();
		}

		this._virtualChildren.add(target);
	}

	/**
	 * getChildCount
	 * 
	 * @return child count
	 */
	public int getChildCount()
	{
		int result = super.getChildCount();

		if (this._virtualChildren != null)
		{
			result += this._virtualChildren.size();
		}

		return result;
	}

	/**
	 * getReferenceNodes
	 * 
	 * @return returns an array composed of the reference node and any virtual child nodes
	 */
	public IParseNode[] getReferenceNodes()
	{
		IParseNode[] result = null;

		if (this.hasVirtualChildren())
		{
			result = new IParseNode[this._virtualChildren.size() + 1];

			result = this._virtualChildren.toArray(result);
			result[result.length - 1] = this.getReferenceNode();
		}
		else
		{
			result = new IParseNode[] { this.getReferenceNode() };
		}

		return result;
	}

	/**
	 * getVirtualChildren
	 * 
	 * @return virtual children array
	 */
	public IParseNode[] getVirtualChildren()
	{
		IParseNode[] result = null;

		if (this._virtualChildren != null)
		{
			result = this._virtualChildren.toArray(new IParseNode[this._virtualChildren.size()]);
		}

		return result;
	}

	/**
	 * hasVirtualChildren
	 * 
	 * @return returns true if this item has virtual children
	 */
	public boolean hasVirtualChildren()
	{
		return (this._virtualChildren != null && this._virtualChildren.size() > 0);
	}

	

	/**
	 * @see com.aptana.ide.views.outline.IResolvableItem#isResolvable()
	 */
	public boolean isResolvable()
	{
		return input != null;
	}

	/**
	 * 
	 * @param input 
	 */
	public void setResolveInformation(IEditorInput input)
	{
		this.input=input;
	}
	
	/**
	 * @param parent
	 */
	public void setParent(IResolvableItem parent){
		this.parent=parent;
	}

	/**
	 * @see com.aptana.ide.views.outline.IResolvableItem#getEditorInput()
	 */
	public IEditorInput getEditorInput()
	{
		return this.input;
	}

	/**
	 * @see com.aptana.ide.views.outline.IResolvableItem#stillHighlight()
	 */
	public boolean stillHighlight()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.views.outline.IResolvableItem#getParentItem()
	 */
	public IResolvableItem getParentItem()
	{
		return parent;
	}
}
