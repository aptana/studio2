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
package com.aptana.ide.parsing.bnf.nodes;

import java.util.List;

import com.aptana.ide.parsing.bnf.TerminalList;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.ParseNodeBase;

/**
 * @author Kevin Lindsey
 */
public class GrammarNodeBase extends ParseNodeBase implements IGrammarNode
{
	private GrammarNode _owningGrammar;

	/**
	 * NodeBase
	 * 
	 * @param name
	 */
	protected GrammarNodeBase(GrammarNode owningGrammar, int type, String name)
	{
		super(type, "text/bnf", null); //$NON-NLS-1$
		
		this._owningGrammar = owningGrammar;
		
		this.setName(name);
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.IGrammarNode#getFirst(com.aptana.ide.parsing.bnf.TerminalList)
	 */
	public void getFirst(TerminalList terminals)
	{
		// sub-classes should override this method
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.IGrammarNode#getOwningGrammar()
	 */
	public GrammarNode getOwningGrammar()
	{
		return this._owningGrammar;
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.IGrammarNode#getOwningProduction()
	 */
	public ProductionNode getOwningProduction()
	{
		ProductionNode result = null;
		IGrammarNode current = this;

		while (current != null)
		{
			if (current.getTypeIndex() == GrammarNodeTypes.PRODUCTION)
			{
				result = (ProductionNode) current;
				break;
			}
			else
			{
				IParseNode candidate = current.getParent();
				
				if (candidate instanceof IGrammarNode)
				{
					current = (IGrammarNode) candidate;
				}
				else
				{
					current = null;
				}
			}
		}

		return result;
	}

	/**
	 * @see com.kevlindev.bnf.nodes.IGrammarNode#getSymbols(List<IGrammarNode>)
	 */
	public void getSymbols(List<IGrammarNode> symbols)
	{
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.IGrammarNode#isNullable()
	 */
	public boolean isNullable()
	{
		return false;
	}
}
