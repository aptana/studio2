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
package com.aptana.ide.editor.xml.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.ide.editor.xml.parsing.nodes.XMLDocumentNode;
import com.aptana.ide.editor.xml.parsing.nodes.XMLElementNode;
import com.aptana.ide.editor.xml.parsing.nodes.XMLParseNodeTypes;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class XMLContentProvider implements ITreeContentProvider
{
	private static final Object[] NO_OBJECTS = new Object[0];

	/**
	 * XMLContentProvider
	 */
	public XMLContentProvider()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		Object[] result = NO_OBJECTS;
		
		if (parentElement instanceof XMLElementNode)
		{
			XMLElementNode item = (XMLElementNode) parentElement;
			
			result = item.getChildren();
		}
		
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		Object result = null;
		
		if (element instanceof XMLElementNode)
		{
			XMLElementNode item = (XMLElementNode) element;
			
			result = item.getParent();
		}
		
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		boolean result = false;
		
		if (element instanceof XMLElementNode)
		{
			result = ((XMLElementNode) element).hasChildren();
		}
		
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		List elements = new ArrayList();
		
		if (inputElement instanceof XMLDocumentNode)
		{
			// process root node
			this.processNode(elements, (IParseNode) inputElement);
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
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}
	
	/**
	 * processNode
	 *
	 * @param elements
	 * @param node
	 */
	private void processNode(List elements, IParseNode node)
	{
		switch (node.getTypeIndex())
		{
			case XMLParseNodeTypes.DOCUMENT:
				for (int i = 0; i < node.getChildCount(); i++)
				{
					this.processNode(elements, node.getChild(i));
				}
				break;
				
			case XMLParseNodeTypes.ELEMENT:
				elements.add(node);
				break;
				
			default:
				break;
		}
	}
}
