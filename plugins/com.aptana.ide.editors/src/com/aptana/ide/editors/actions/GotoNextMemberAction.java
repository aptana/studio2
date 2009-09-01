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

import com.aptana.ide.editors.unified.actions.UnifiedActionContributor;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Pavel Petrochenko
 */
public class GotoNextMemberAction extends AbstractGotoMemberAction
{

	/**
	 * 
	 */
	public GotoNextMemberAction()
	{
		super(UnifiedActionContributor.GOTO_NEXT_ACTIONID, Messages.GotoNextMemberAction_TITLE);
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
		IParseState parseState = part.getFileContext().getParseState();
		IParseNode node = parseState.getParseResults();
		int caretOffset = part.getViewer().getTextWidget().getCaretOffset();
		node = findDeepestNode(node, caretOffset);
		if (node != null)
		{
			if (node.getEndingOffset() == caretOffset)
			{
				IParseNode parent = getNavigatableParent(node);
				if (parent != null)
				{
					IParseNode[] children = getNavigatableChilds(parent);
					int childIndex = getNavigatableIndex(node);
					if (childIndex < children.length - 1)
					{
						IParseNode parseNode = children[getNavigatableIndex(node) + 1];
						int startingOffset = parseNode.getStartingOffset();
						while (startingOffset == caretOffset)
						{
							if (hasNavigatableChilds(parseNode))
							{
								parseNode = getFirstNavigatableChild(parseNode);
								startingOffset = parseNode.getStartingOffset();

							}
							else
							{
								break;
							}
						}
						if (startingOffset == caretOffset)
						{
							startingOffset = parseNode.getEndingOffset();
						}
						if (startingOffset != -1)
						{

							placeCursor(startingOffset);

						}
					}
					else
					{
						int endingOffset = parent.getEndingOffset();
						while (endingOffset == node.getEndingOffset())
						{
							parent = getNavigatableParent(parent);
							if (parent == null)
							{
								return;
							}
							endingOffset = parent.getEndingOffset();
						}
						if (endingOffset != -1)
						{
							placeCursor(endingOffset);
						}
					}
				}
			}
			else
			{
				IParseNode[] children = getNavigatableChilds(node);
				for (int a = 0; a < children.length; a++)
				{
					int chStart = children[a].getStartingOffset();

					if (chStart > caretOffset)
					{
						placeCursor(chStart);
						return;
					}
				}
				placeCursor(node.getEndingOffset());
			}
		}
	}

}
