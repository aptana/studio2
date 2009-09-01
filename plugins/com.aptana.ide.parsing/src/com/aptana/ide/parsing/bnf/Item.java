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
package com.aptana.ide.parsing.bnf;

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.parsing.bnf.nodes.GrammarNode;
import com.aptana.ide.parsing.bnf.nodes.IGrammarNode;
import com.aptana.ide.parsing.bnf.nodes.GrammarNodeTypes;
import com.aptana.ide.parsing.bnf.nodes.NonTerminalNode;
import com.aptana.ide.parsing.bnf.nodes.ProductionNode;
import com.aptana.ide.parsing.bnf.nodes.TerminalNode;

/**
 * @author Kevin Lindsey
 */
public class Item
{
	private ProductionNode _production;
	private IGrammarNode _definition;
	private int _position;

	/**
	 * Item
	 * 
	 * @param definition
	 */
	public Item(ProductionNode production)
	{
		this._production = production;

		IGrammarNode child = (IGrammarNode) this._production.getChild(0);

		if (child.getTypeIndex() == GrammarNodeTypes.SEQUENCE)
		{
			this._definition = child;
		}
		else
		{
			this._definition = production;
		}
	}

	/**
	 * Clone and reset position
	 * 
	 * @param item
	 */
	public Item(Item item)
	{
		this._production = item._production;
		this._definition = item._definition;
		this._position = item._position;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		boolean result = false;

		if (this == obj)
		{
			result = true;
		}
		else if (obj instanceof Item)
		{
			Item that = (Item) obj;

			result = (this._production == that._production && this._position == that._position);
		}

		return result;
	}

	/**
	 * isAtNonTerminal
	 * 
	 * @return boolean
	 */
	public boolean isAtNonTerminal()
	{
		IGrammarNode currentNode = this.getCurrentNode();

		return (currentNode != null && currentNode instanceof NonTerminalNode);
	}

	/**
	 * isInitialItem
	 * 
	 * @return boolean
	 */
	public boolean isInitialItem()
	{
		return (this._position == 0);
	}

	/**
	 * isCompletedItem
	 * 
	 * @return boolean
	 */
	public boolean isCompletedItem()
	{
		return (this._position == this._definition.getChildCount());
	}

	/**
	 * advance
	 */
	public void advance()
	{
		if (this.isCompletedItem() == false)
		{
			this._position++;
		}
	}

	/**
	 * getCurrentNode
	 * 
	 * @return INode or null
	 */
	public IGrammarNode getCurrentNode()
	{
		IGrammarNode result = null;

		if (this.isCompletedItem() == false)
		{
			result = (IGrammarNode) this._definition.getChild(this._position);
		}

		return result;
	}

	/**
	 * getFollow
	 * 
	 * @param terminals
	 */
	public void getFollow(TerminalList terminals)
	{
		IGrammarNode currentNode = this.getCurrentNode();

		this.advance();

		if (this.isCompletedItem())
		{
			currentNode.getOwningProduction().getFollow(terminals);
		}
		else
		{
			IGrammarNode sibling = this.getCurrentNode();

			switch (sibling.getTypeIndex())
			{
				case GrammarNodeTypes.TERMINAL:
					// 2a.
					terminals.add((TerminalNode) sibling);
					break;

				case GrammarNodeTypes.NONTERMINAL:
					NonTerminalNode nonTerminal = (NonTerminalNode) sibling;
					TerminalList first = new TerminalList();

					// 2b.
					nonTerminal.getFirst(first);
					first.removeEpsilon();
					terminals.add(first);

					// 2c. (B is nullable)
					if (nonTerminal.isNullable())
					{
						sibling.getOwningProduction().getFollow(terminals);
					}
					break;

				default:
					break;
			}
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this._production.hashCode() ^ this._position;
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public int getIndex()
	{
		GrammarNode grammar = this._production.getOwningGrammar();
		int result = -1;

		for (int i = 0; i < grammar.getChildCount(); i++)
		{
			if (grammar.getChild(i) == this._production)
			{
				result = i;
				break;
			}
		}

		return result;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._production.getName();
	}

	/**
	 * getPosition
	 * 
	 * @return int
	 */
	public int getPosition()
	{
		return this._position;
	}

	/**
	 * setPosition
	 * 
	 * @param position
	 */
	public void setPosition(int position)
	{
		if (this._definition != null)
		{
			if (0 <= position && position <= this._definition.getChildCount())
			{
				this._position = position;
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		SourceWriter writer = new SourceWriter();

		writer.print(this._production.getName()).print(" : "); //$NON-NLS-1$

		for (int i = 0; i < this._definition.getChildCount(); i++)
		{
			if (i == this._position)
			{
				writer.print(". "); //$NON-NLS-1$
			}

			this._definition.getChild(i).getSource(writer);

			if (i < this._definition.getChildCount() - 1)
			{
				writer.print(" "); //$NON-NLS-1$
			}
		}

		if (this._position == this._definition.getChildCount())
		{
			writer.print("."); //$NON-NLS-1$
		}

		return writer.toString();
	}
}
