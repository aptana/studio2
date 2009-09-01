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
package com.aptana.ide.editors.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;

import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Pavel Petrochenko
 */
public abstract class AbstractGotoMemberAction extends UnifiedEditorAction
{

	/**
	 * part
	 */
	protected IUnifiedEditor part;

	/**
	 * @param actionId
	 * @param actionText
	 */
	public AbstractGotoMemberAction(String actionId, String actionText)
	{
		super(actionId, actionText);
	}

	/**
	 * @param node
	 * @param caretOffset
	 * @return
	 */
	protected IParseNode findDeepestNode(IParseNode node, int caretOffset)
	{
		int startingOffset = node.getStartingOffset();
		int endingOffset = node.getEndingOffset();
		IParseNode[] navigatableChilds = getNavigatableChilds(node);
		for (IParseNode n : navigatableChilds)
		{
			IParseNode findDeepestNode = findDeepestNode(n, caretOffset);
			if (findDeepestNode != null)
			{
				return findDeepestNode;
			}
		}
		if (startingOffset > caretOffset || endingOffset < caretOffset)
		{
			return null;
		}
		return node;
	}

	/**
	 * @param action
	 * @param targetEditor
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor)
	{
		part = (IUnifiedEditor) targetEditor;
	}

	/**
	 * @param node
	 * @return
	 */
	protected int getNavigatableIndex(IParseNode node)
	{
		IParseNode navigatableParent = getNavigatableParent(node);
		if (navigatableParent == null)
		{
			return 0;
		}
		List<IParseNode> asList = Arrays.asList(getNavigatableChilds(navigatableParent));
		int indexOf = asList.indexOf(node);
		return indexOf;
	}

	/**
	 * @param parseNode
	 * @return
	 */
	protected IParseNode getFirstNavigatableChild(IParseNode parseNode)
	{
		parseNode = getNavigatableChilds(parseNode)[0];
		return parseNode;
	}

	/**
	 * @param parseNode
	 * @return
	 */
	protected boolean hasNavigatableChilds(IParseNode parseNode)
	{
		return getNavigatableChilds(parseNode).length != 0;
	}

	/**
	 * @param node
	 * @return
	 */
	protected IParseNode[] getNavigatableChilds(IParseNode node)
	{
		IParseNode[] children = node.getChildren();
		ArrayList<IParseNode> ns = new ArrayList<IParseNode>();
		for (IParseNode n : children)
		{
			if (n instanceof ICanBeNotNavigatable)
			{
				ICanBeNotNavigatable m = (ICanBeNotNavigatable) n;
				if (m.isNavigatable())
				{
					ns.add(n);
				}
			}
			else
			{
				ns.add(n);
			}
		}
		children = new IParseNode[ns.size()];
		ns.toArray(children);
		return children;
	}

	/**
	 * @param node
	 * @return navigatable parent
	 */
	protected IParseNode getNavigatableParent(IParseNode node)
	{
		IParseNode parent = node.getParent();
		if (parent instanceof ICanBeNotNavigatable)
		{
			ICanBeNotNavigatable m = (ICanBeNotNavigatable) parent;
			if (m.isNavigatable())
			{
				return parent;
			}
			return getNavigatableParent(parent);
		}
		return parent;
	}

	/**
	 * @param startingOffset
	 */
	protected void placeCursor(int startingOffset)
	{
		part.getViewer().getTextWidget().setCaretOffset(startingOffset);
		part.getViewer().revealRange(startingOffset, 0);
	}

}