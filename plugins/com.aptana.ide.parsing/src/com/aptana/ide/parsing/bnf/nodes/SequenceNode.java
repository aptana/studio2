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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.parsing.bnf.Item;
import com.aptana.ide.parsing.bnf.TerminalList;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class SequenceNode extends GrammarNodeBase
{
	private String _actionName;
	private Map<String,List<Integer>> _nameIndex = new HashMap<String,List<Integer>>();

	/**
	 * SequenceNode
	 */
	public SequenceNode(GrammarNode owningGrammar)
	{
		super(owningGrammar, GrammarNodeTypes.SEQUENCE, "#sequence"); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.ParseNodeBase#appendChild(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void appendChild(IParseNode child)
	{
		super.appendChild(child);
		
		if (child instanceof TerminalNode)
		{
			TerminalNode terminal = (TerminalNode) child;
			
			this.setNameIndex(terminal.getName());
			this.setNameIndex(terminal.getAlias());
		}
		else if (child instanceof NonTerminalNode)
		{
			NonTerminalNode nonTerminal = (NonTerminalNode) child;
			
			this.setNameIndex(nonTerminal.getName());
		}
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

			if (child.getTypeIndex() == GrammarNodeTypes.NONTERMINAL)
			{
				if (child.getName().equals(name))
				{
					result = new Item((ProductionNode) this.getParent());
					result.setPosition(i);
					break;
				}
			}
		}

		return result;
	}

	/**
	 * getActionName
	 * 
	 * @return String or null
	 */
	public String getActionName()
	{
		return this._actionName;
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.GrammarNodeBase#getFirst(com.aptana.ide.parsing.bnf.TerminalList)
	 */
	public void getFirst(TerminalList terminals)
	{
		boolean allNullable = true;

		for (int i = 0; i < this.getChildCount(); i++)
		{
			IGrammarNode child = (IGrammarNode) this.getChild(i);

			child.getFirst(terminals);

			if (child.isNullable() == false)
			{
				allNullable = false;
				break;
			}
		}

		if (allNullable)
		{
			terminals.addEpsilon();
		}
	}

	/**
	 * getNameIndex
	 * 
	 * @param name
	 * @param index
	 * @return
	 */
	public Map<String,List<Integer>> getNameIndexMap()
	{
		return this._nameIndex;
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.GrammarNodeBase#getSource(com.kevlindev.io.SourceWriter)
	 */
	public void getSource(SourceWriter writer)
	{
		if (this.hasChildren())
		{
			this.getChild(0).getSource(writer);

			for (int i = 1; i < this.getChildCount(); i++)
			{
				writer.print(" "); //$NON-NLS-1$
				this.getChild(i).getSource(writer);
			}
		}

		String actionName = this.getActionName();

		if (actionName != null && actionName.length() > 0)
		{
			writer.print(" {").print(actionName).print("}"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * @see com.kevlindev.bnf.nodes.IGrammarNode#getSymbols(List<IGrammarNode>)
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
	 * @see com.aptana.ide.parsing.bnf.nodes.GrammarNodeBase#isNullable()
	 */
	public boolean isNullable()
	{
		boolean result = false;

		if (this.getChildCount() == 1)
		{
			result = ((IGrammarNode) this.getChild(0)).isNullable();
		}

		return result;
	}

	/**
	 * setActionName
	 * 
	 * @param name
	 */
	public void setActionName(String name)
	{
		this._actionName = name;
	}

	/**
	 * addChildName
	 * 
	 * @param name
	 */
	private void setNameIndex(String name)
	{
		int lastIndex = this.getChildCount() - 1;
		
		if (name != null && name.length() > 0 && lastIndex != -1)
		{
			if (this._nameIndex == null)
			{
				this._nameIndex = new HashMap<String,List<Integer>>();
			}
			
			if (this._nameIndex.containsKey(name) == false)
			{
				this._nameIndex.put(name, new ArrayList<Integer>());
			}
			
			List<Integer> indexes = this._nameIndex.get(name);
			
			indexes.add(lastIndex);
		}
	}
}
