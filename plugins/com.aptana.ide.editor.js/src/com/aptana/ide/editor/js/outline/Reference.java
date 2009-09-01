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
import java.util.Collections;
import java.util.List;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.parsing.nodes.JSParseNodeTypes;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class Reference
{
	private String _scope;
	private String _name;
	private String _type;
	
	/**
	 * Reference
	 * 
	 * @param node
	 * @param name
	 * @param type
	 */
	public Reference(IParseNode node, String name, String type)
	{
		if (node == null)
		{
			throw new IllegalArgumentException(Messages.Reference_Node_Not_Defined);
		}
		if (name == null)
		{
			throw new IllegalArgumentException(Messages.Reference_Name_Not_Defined);
		}
		if (type == null)
		{
			throw new IllegalArgumentException(Messages.Reference_Type_Not_Defined);
		}
		
		this._scope = createScopeString(node);
		this._name = name;
		this._type = type;
	}
	
	/**
	 * Reference
	 * 
	 * @param scope
	 * @param name
	 * @param type
	 */
	public Reference(String scope, String name, String type)
	{
		if (scope == null)
		{
			throw new IllegalArgumentException(Messages.Reference_Scope_Not_Defined);
		}
		if (name == null)
		{
			throw new IllegalArgumentException(Messages.Reference_Name_Not_Defined);
		}
		if (type == null)
		{
			throw new IllegalArgumentException(Messages.Reference_Type_Not_Defined);
		}
		
		this._scope = scope;
		this._name = name;
		this._type = type;
	}
	
	/**
	 * getName
	 *
	 * @return name
	 */
	public String getName()
	{
		return this._name;
	}
	
	/**
	 * getScope
	 *
	 * @return scope string
	 */
	public String getScope()
	{
		return this._scope;
	}
	
	/**
	 * getScopeString
	 *
	 * @param node
	 * @return scope string
	 */
	public static String createScopeString(IParseNode node)
	{
		List<String> parts = new ArrayList<String>();
		IParseNode currentNode = node;
		IParseNode parent;
		
		switch (currentNode.getTypeIndex())
		{
			case JSParseNodeTypes.IDENTIFIER:
			case JSParseNodeTypes.THIS:
				parent = currentNode.getParent();
				
				if (parent.getTypeIndex() == JSParseNodeTypes.GET_PROPERTY)
				{
					if (parent.getChild(1) == currentNode)
					{
						parts.add(parent.getSource());
					}
				}
				else
				{
					parts.add(currentNode.getText());
				}
				
				currentNode = parent;
				break;
				
			case JSParseNodeTypes.GET_PROPERTY:
				parts.add(currentNode.getChild(0).getSource());
				currentNode = currentNode.getParent();
				break;
				
			case JSParseNodeTypes.FUNCTION:
				parent = currentNode.getParent();
				
				// NOTE: The following block is for 'dojo.lang.extend', 'MochiKit.Base.update',
				// and 'Object.extend' support
				if (parent != null && parent.getTypeIndex() == JSParseNodeTypes.NAME_VALUE_PAIR)
				{
					IParseNode grandparent = parent.getParent();
					
					if (grandparent != null && grandparent.getTypeIndex() == JSParseNodeTypes.OBJECT_LITERAL)
					{
						IParseNode greatgrandparent = grandparent.getParent();
						
						if (greatgrandparent != null && greatgrandparent.getTypeIndex() == JSParseNodeTypes.ARGUMENTS)
						{
							parts.add(greatgrandparent.getChild(0).getSource() + "."); //$NON-NLS-1$
						}
					}
				}
				
				currentNode = parent;
				break;
				
			default:
				break;
		}
		
		while (currentNode != null)
		{
			if (currentNode.getTypeIndex() == JSParseNodeTypes.FUNCTION)
			{
				if (currentNode.hasAttribute("name")) //$NON-NLS-1$
				{
					parts.add(currentNode.getAttribute("name")); //$NON-NLS-1$
				}
				else
				{
					// check for the case where we are inside a self invoking function, where the scope is of the form
					// invoke / group / function
					IParseNode parentNode = currentNode.getParent();
					IParseNode grandParentNode = null;
					
					if (parentNode != null)
					{
						grandParentNode = parentNode.getParent();						
					}

					if (parentNode != null && grandParentNode != null && parentNode.getTypeIndex() == JSParseNodeTypes.GROUP && grandParentNode.getTypeIndex() == JSParseNodeTypes.INVOKE)
					{
						currentNode = grandParentNode;
					}
					else
					{
						// calculate name for anonymous function
						String path = ""; //$NON-NLS-1$
						IParseNode p = currentNode;
						
						while (p != null)
						{
							path = "[" + p.getChildIndex() + "]" + p.getName() + path; //$NON-NLS-1$ //$NON-NLS-2$
							
							p = p.getParent();
						}
						
						// add part
						parts.add(path);
					}
				}
			}
			else if (currentNode.getTypeIndex() == JSParseNodeTypes.NAME_VALUE_PAIR)
			{
				IParseNode property = currentNode.getChild(0);
				String name = property.getText();
				
				parts.add(name); //$NON-NLS-1$
			}
			else if (currentNode.getTypeIndex() == JSParseNodeTypes.DECLARATION)
			{
				IParseNode identifier = currentNode.getChild(0);
				IParseNode assignedValue = currentNode.getChild(1);
				String name = identifier.getText();
				
				if (assignedValue.getTypeIndex() == JSParseNodeTypes.OBJECT_LITERAL)
				{
					parts.add(name);
				}
			}
			
			currentNode = currentNode.getParent();
		}
		
		Collections.reverse(parts);		
		
		return "/" + StringUtils.join("/", parts.toArray(new String[parts.size()])); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * getPartsString
	 *
	 * @return parts string
	 */
	public String getPartsString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("('"); //$NON-NLS-1$
		sb.append(this._scope).append("','"); //$NON-NLS-1$
		sb.append(this._type).append("','"); //$NON-NLS-1$
		sb.append(this._name);
		sb.append("')"); //$NON-NLS-1$
		
		return sb.toString();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String result;
		
		if (this._scope.equals(this._type))
		{
			result = this._scope + this._name;
		}
		else
		{
			result = this._scope + this._type + this._name;
		}
		
		return result;
	}
}
