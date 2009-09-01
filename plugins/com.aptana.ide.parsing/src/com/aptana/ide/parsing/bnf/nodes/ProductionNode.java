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

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.parsing.bnf.Item;
import com.aptana.ide.parsing.bnf.TerminalList;

/**
 * @author Kevin Lindsey
 */
public class ProductionNode extends GrammarNodeBase
{
	/**
	 * ProductionNode
	 * 
	 * @param name
	 */
	public ProductionNode(GrammarNode owningGrammar, String name)
	{
		super(owningGrammar, GrammarNodeTypes.PRODUCTION, name);
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.GrammarNodeBase#getFirst(com.aptana.ide.parsing.bnf.TerminalList)
	 */
	public void getFirst(TerminalList terminals)
	{
		// make sure we haven't processed this production already
		if (terminals.hasProduction(this) == false)
		{
			// prevent infinite loops from cycles in the BNF graph
			terminals.addProduction(this);

			for (int i = 0; i < this.getChildCount(); i++)
			{
				IGrammarNode child = (IGrammarNode) this.getChild(i);

				child.getFirst(terminals);
			}
		}
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.GrammarNodeBase#getFollow(java.lang.String, com.aptana.ide.parsing.bnf.TerminalList)
	 */
	public void getFollow(TerminalList terminals)
	{
		// make sure we haven't processed this production already
		if (terminals.hasProduction(this) == false)
		{
			// prevent infinite loops from cycles in the BNF graph
			terminals.addProduction(this);

			this.getOwningGrammar().getFollow(this.getName(), terminals);
		}
	}

	/**
	 * @see com.kevlindev.bnf.nodes.GrammarNodeBase#getSymbols(List<IGrammarNode>)
	 */
	public void getSymbols(List<IGrammarNode> symbols)
	{
		for (int i = 0; i < this.getChildCount(); i++)
		{
			IGrammarNode child = (IGrammarNode) this.getChild(i);

			child.getSymbols(symbols);
		}
	}

	/**
	 * getSymbolCount
	 * 
	 * @return
	 */
	public int getSymbolCount()
	{
		int result = 0;

		for (int i = 0; i < this.getChildCount(); i++)
		{
			IGrammarNode child = (IGrammarNode) this.getChild(i);

			switch (child.getTypeIndex())
			{
				case GrammarNodeTypes.SEQUENCE:
					result += ((SequenceNode) child).getChildCount();
					break;

				case GrammarNodeTypes.NONTERMINAL:
				case GrammarNodeTypes.TERMINAL:
					result++;

				default:
					break;
			}
		}

		return result;
	}

	/**
	 * hasNonTerminal
	 * 
	 * @param name
	 * @return Item
	 */
	public Item findNonTerminal(String name)
	{
		Item result = null;

		for (int i = 0; i < this.getChildCount(); i++)
		{
			IGrammarNode child = (IGrammarNode) this.getChild(i);

			switch (child.getTypeIndex())
			{
				case GrammarNodeTypes.SEQUENCE:
					result = ((SequenceNode) child).findNonTerminal(name);
					break;

				case GrammarNodeTypes.NONTERMINAL:
					if (child.getName().equals(name))
					{
						result = new Item(this);
					}
					break;

				default:
					break;
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.GrammarNodeBase#isNullable()
	 */
	public boolean isNullable()
	{
		boolean result = false;

		for (int i = 0; i < this.getChildCount(); i++)
		{
			IGrammarNode child = (IGrammarNode) this.getChild(i);

			if (child.isNullable())
			{
				result = true;
				break;
			}
		}

		return result;
	}

	/**
	 * isStartingProduction
	 * 
	 * @return
	 */
	public boolean isStartingProduction()
	{
		return (this.getOwningGrammar().getStartingName().equals(this.getName()));
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.GrammarNodeBase#getSource(com.kevlindev.io.SourceWriter)
	 */
	public void getSource(SourceWriter writer)
	{
		writer.print(this.getName()).println().increaseIndent();

		if (this.hasChildren())
		{
			writer.printWithIndent(": "); //$NON-NLS-1$

			this.getChild(0).getSource(writer);

			for (int i = 1; i < this.getChildCount(); i++)
			{
				IGrammarNode child = (IGrammarNode) this.getChild(i);

				writer.println();
				writer.printWithIndent("| "); //$NON-NLS-1$

				child.getSource(writer);
			}
		}

		writer.println().printlnWithIndent(";").decreaseIndent(); //$NON-NLS-1$
	}
}
