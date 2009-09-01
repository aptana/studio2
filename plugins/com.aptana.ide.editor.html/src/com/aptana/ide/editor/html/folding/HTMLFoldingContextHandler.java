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
package com.aptana.ide.editor.html.folding;

import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.parsing.nodes.HTMLParseNodeTypes;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.folding.IFoldingContextHandler;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class HTMLFoldingContextHandler implements IFoldingContextHandler
{

	/**
	 * HTMLFoldingContextHandler
	 */
	public HTMLFoldingContextHandler()
	{

	}

	/**
	 * @see com.aptana.ide.editors.unified.folding.IFoldingContextHandler#nodeIsFoldable(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public boolean nodeIsFoldable(IParseNode node)
	{
		if (node.getTypeIndex() == HTMLParseNodeTypes.ELEMENT)
		{
			String nodeList = HTMLPlugin.getDefault().getPreferenceStore().getString(
					IPreferenceConstants.FOLDING_HTML_NODE_LIST);
			String[] foldableNodes = nodeList.split(","); //$NON-NLS-1$
			for (int i = 0; i < foldableNodes.length; i++)
			{
				if (node.getName() != null && node.getName().equals(foldableNodes[i]))
				{
					return true;
				}
			}
		}
		return false;
	}
}
