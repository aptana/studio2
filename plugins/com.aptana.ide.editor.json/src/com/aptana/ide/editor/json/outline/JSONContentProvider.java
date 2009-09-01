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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.editor.json.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.ide.editor.json.parsing.nodes.JSONParseNode;
import com.aptana.ide.editor.json.parsing.nodes.JSONParseNodeTypes;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class JSONContentProvider implements ITreeContentProvider
{
	/**
	 * JSONContentProvider
	 */
	public JSONContentProvider()
	{
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		List elements = new ArrayList();
		
		if (parentElement instanceof JSONParseNode)
		{
			IParseNode node = (IParseNode) parentElement;
			
			switch (node.getTypeIndex())
			{
				case JSONParseNodeTypes.ARRAY:
				case JSONParseNodeTypes.OBJECT:
					for (int i = 0; i < node.getChildCount(); i++)
					{
						elements.add(node.getChild(i));
					}
					break;
					
				case JSONParseNodeTypes.NAME_VALUE_PAIR:
					IParseNode child = node.getChild(1);
					int type = child.getTypeIndex();
					
					if (type == JSONParseNodeTypes.ARRAY || type == JSONParseNodeTypes.OBJECT)
					{
						for (int i = 0; i < child.getChildCount(); i++)
						{
							elements.add(child.getChild(i));
						}
					}
					else
					{
						elements.add(child);
					}
					break;
			}
		}
		
		return elements.toArray(new Object[elements.size()]);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		Object result = null;
		
		if (element instanceof JSONParseNode)
		{
			IParseNode node = (IParseNode) element;
			
			result = node.getParent();
		}
		
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		boolean result = false;
		
		if (element instanceof JSONParseNode)
		{
			IParseNode node = (IParseNode) element;
		
			if (node.getTypeIndex() == JSONParseNodeTypes.NAME_VALUE_PAIR)
			{
				IParseNode child = node.getChild(1);
				int childType = child.getTypeIndex();
				
				if (childType == JSONParseNodeTypes.ARRAY || childType == JSONParseNodeTypes.OBJECT)
				{
					result = child.hasChildren();
				}
			}
			else
			{
				result = node.hasChildren();
			}
		}
		
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		List elements = new ArrayList();
		
		if (inputElement instanceof IParseNode)
		{
			IParseNode node = (IParseNode) inputElement;
			
			for (int i = 0; i < node.getChildCount(); i++)
			{
				IParseNode child = node.getChild(i);
				
				switch (child.getTypeIndex())
				{
					case JSONParseNodeTypes.ARRAY:
						elements.add(child);
						break;
				
					case JSONParseNodeTypes.OBJECT:
						elements.add(child);
						break;
				
					case JSONParseNodeTypes.SCALAR:
						elements.add(child);
						break;
				}
			}
		}
		
		return elements.toArray(new Object[elements.size()]);
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}
}
