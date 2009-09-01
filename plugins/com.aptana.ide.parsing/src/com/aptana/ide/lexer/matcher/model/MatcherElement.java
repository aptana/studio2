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
package com.aptana.ide.lexer.matcher.model;

import com.aptana.ide.lexer.ITokenList;
import com.aptana.xml.INode;
import com.aptana.xml.NodeBase;

/**
 * @author Kevin Lindsey
 */
public class MatcherElement extends NodeBase implements IMatcherElement
{
	private String _group;
	private String _category;
	private String _type;
	private String _switchTo;
	
	private boolean _typeDefinedInSubtree;

	/**
	 * MatcherElement
	 */
	public MatcherElement()
	{
	}

	/**
	 * createToken
	 * 
	 * @param tokenList
	 */
	protected void createToken(ITokenList tokenList)
	{
		// do nothing
	}

	/**
	 * @see com.aptana.xml.NodeBase#appendChild(com.aptana.xml.INode)
	 */
	public void appendChild(INode child)
	{
		super.appendChild(child);
		
		if (child instanceof MatcherElement)
		{
			if (((MatcherElement) child).getTypeDefinedInSubtree())
			{
				this.setTypeDefineInTree();
			}
		}
	}

	/**
	 * createChildrenTokens
	 * 
	 * @param tokenList
	 */
	protected void createChildrenTokens(ITokenList tokenList)
	{
		for (int i = 0; i < this.getChildCount(); i++)
		{
			INode child = this.getChild(i);

			if (child instanceof IMatcherElement)
			{
				((IMatcherElement) child).createTokens(tokenList);
			}
		}
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.IMatcherElement#createTokens(ITokenList)
	 */
	public void createTokens(ITokenList tokenList)
	{
		// NOTE: nodes may be reparented as they are added to the token list. We need to
		// process children before their parent to maintain the original parse tree context
		// when a child is processed

		this.createChildrenTokens(tokenList);
		this.createToken(tokenList);
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.IMatcherElement#getCategory()
	 */
	public String getCategory()
	{
		String result = NodeBase.EMPTY_STRING;

		if (this._category != null)
		{
			result = this._category;
		}
		else
		{
			INode parent = this.getParent();

			if (parent != null && parent instanceof IMatcherElement)
			{
				result = ((IMatcherElement) parent).getCategory();
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.IMatcherElement#getGroup()
	 */
	public String getGroup()
	{
		String result = NodeBase.EMPTY_STRING;

		if (this._group != null)
		{
			result = this._group;
		}
		else
		{
			INode parent = this.getParent();

			if (parent != null && parent instanceof IMatcherElement)
			{
				result = ((IMatcherElement) parent).getGroup();
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.IMatcherElement#getSwitchTo()
	 */
	public String getSwitchTo()
	{
		String result = NodeBase.EMPTY_STRING;

		if (this._switchTo != null)
		{
			result = this._switchTo;
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.IMatcherElement#getType()
	 */
	public String getType()
	{
		String result = NodeBase.EMPTY_STRING;

		if (this._type != null)
		{
			result = this._type;
		}

		return result;
	}
	
	/**
	 * getTypeInSelfOrDescendents
	 *
	 * @return boolean
	 */
	public boolean getTypeDefinedInSubtree()
	{
		return this._typeDefinedInSubtree;
	}

	/**
	 * setCategory
	 * 
	 * @param category
	 */
	public void setCategory(String category)
	{
		this._category = category;
	}

	/**
	 * setGroup
	 * 
	 * @param group
	 */
	public void setGroup(String group)
	{
		this._group = group;
	}

	/**
	 * setSwitchTo
	 * 
	 * @param switchTo
	 */
	public void setSwitchTo(String switchTo)
	{
		this._switchTo = switchTo;
	}

	/**
	 * setType
	 * 
	 * @param type
	 */
	public void setType(String type)
	{
		this._type = type;
		
		this.setTypeDefineInTree();
	}

	/**
	 * setTypeDefineInTree
	 */
	private void setTypeDefineInTree()
	{
		// tag this element as having a type
		this._typeDefinedInSubtree = true;
		
		// make sure to propagate this flag up the ancestor chain
		INode parent = this.getParent();
		
		while (parent != null && parent instanceof MatcherElement)
		{
			((MatcherElement) parent)._typeDefinedInSubtree = true;
			
			parent = parent.getParent();
		}
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.model.IMatcherElement#validate()
	 */
	public void validate()
	{
		this.validateLocal();
		this.validateChildren();
	}

	/**
	 * validateChildAttributes
	 */
	protected void validateChildren()
	{
		for (int i = 0; i < this.getChildCount(); i++)
		{
			INode child = this.getChild(i);

			if (child instanceof IMatcherElement)
			{
				((IMatcherElement) child).validate();
			}
		}
	}

	/**
	 * validateLocalAttributes
	 */
	protected void validateLocal()
	{
		// signal success by not reporting any errors or warnings
	}
}
