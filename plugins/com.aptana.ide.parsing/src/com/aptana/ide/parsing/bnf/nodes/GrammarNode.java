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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.DynamicEnumerationMap;
import com.aptana.ide.lexer.IEnumerationMap;
import com.aptana.ide.parsing.bnf.Item;
import com.aptana.ide.parsing.bnf.TerminalList;

/**
 * @author Kevin Lindsey
 */
public class GrammarNode extends GrammarNodeBase
{
	/**
	 * @author Kevin Lindsey
	 */
	private static class NodeKey
	{
		public int type;
		public String name;

		/**
		 * @param node
		 */
		public NodeKey(IGrammarNode node)
		{
			this(node.getTypeIndex(), node.getName());
		}

		/**
		 * @param type
		 * @param name
		 */
		public NodeKey(int type, String name)
		{
			this.type = type;
			this.name = name;
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
			else if (obj instanceof NodeKey)
			{
				NodeKey nodeKey = (NodeKey) obj;

				result = (this.type == nodeKey.type && this.name == nodeKey.name);
			}

			return result;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode()
		{
			return this.type ^ this.name.hashCode();
		}
	}

	private Map<String, List<ProductionNode>> _productions;
	private String _startingName;
	private TerminalNode _eofNode;
	private IEnumerationMap _terminalMap;
	private IEnumerationMap _nonTerminalMap;
	private Map<NodeKey,IGrammarNode> _nodeCache;

	/**
	 * GrammarNode
	 * 
	 * @param name
	 */
	public GrammarNode(String name)
	{
		super(null, GrammarNodeTypes.GRAMMAR, name);

		this._terminalMap = new DynamicEnumerationMap();
		this._nonTerminalMap = new DynamicEnumerationMap();

		// make sure EOF is 0
		this._terminalMap.getIntValue("$"); //$NON-NLS-1$

		this._nodeCache = new HashMap<NodeKey,IGrammarNode>();
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.GrammarNodeBase#appendChild(com.aptana.ide.parsing.bnf.nodes.IGrammarNode)
	 */
	public void appendChild(IGrammarNode child)
	{
		super.appendChild(child);

		if (child.getTypeIndex() == GrammarNodeTypes.PRODUCTION)
		{
			ProductionNode production = (ProductionNode) child;
			String name = production.getName();

			if (this._startingName == null)
			{
				this._startingName = production.getName();
			}

			if (this._productions == null)
			{
				this._productions = new HashMap<String, List<ProductionNode>>();
			}

			if (this._productions.containsKey(name) == false)
			{
				this._productions.put(name, new ArrayList<ProductionNode>());
			}

			this._productions.get(name).add(production);
		}
	}

	/**
	 * createAndNode
	 */
	public SequenceNode createSequenceNode()
	{
		return new SequenceNode(this);
	}

	/**
	 * createEOFNode
	 * 
	 * @return
	 */
	public TerminalNode createEOFNode()
	{
		if (this._eofNode == null)
		{
			this._eofNode = this.createTerminalNode("$"); //$NON-NLS-1$
		}

		return this._eofNode;
	}

	/**
	 * createEmptyNode
	 * 
	 * @return
	 */
	public EmptyNode createEmptyNode()
	{
		return new EmptyNode(this);
	}

	/**
	 * NonTerminalNode
	 * 
	 * @param name
	 */
	public NonTerminalNode createNonTerminalNode(String name)
	{
		int index = this._nonTerminalMap.getIntValue(name);
		// NodeKey key = new NodeKey(NodeTypes.NONTERMINAL, name);
		// NonTerminalNode result;
		//		
		// if (this._nodeCache.containsKey(key)) {
		// result = (NonTerminalNode) this._nodeCache.get(key);
		// } else {
		// result = new NonTerminalNode(this, name, index);
		// this._nodeCache.put(key, result);
		// }
		//		
		// return result;
		return new NonTerminalNode(this, name, index);
	}

	/**
	 * createProductionNode
	 * 
	 * @param name
	 * @return
	 */
	public ProductionNode createProductionNode(String name)
	{
		return new ProductionNode(this, name);
	}

	/**
	 * createTerminalNode
	 * 
	 * @param name
	 * @return
	 */
	public TerminalNode createTerminalNode(String name)
	{
		int index = this._terminalMap.getIntValue(name);
		NodeKey key = new NodeKey(GrammarNodeTypes.TERMINAL, name);
		TerminalNode result;

		if (this._nodeCache.containsKey(key))
		{
			result = (TerminalNode) this._nodeCache.get(key);
		}
		else
		{
			result = new TerminalNode(this, name, index);
			this._nodeCache.put(key, result);
		}

		return result;
	}

	/**
	 * getExpandedProductions
	 * 
	 * @return GrammarNode
	 */
	public GrammarNode getExpandedGrammar()
	{
		GrammarNode result = new GrammarNode(this.getName());

		for (int i = 0; i < this.getChildCount(); i++)
		{
			IGrammarNode child = (IGrammarNode) this.getChild(i);

			if (child.getTypeIndex() == GrammarNodeTypes.PRODUCTION)
			{
				ProductionNode production = (ProductionNode) child;
				String name = production.getName();

				for (int j = 0; j < production.getChildCount(); j++)
				{
					ProductionNode newProduction = result.createProductionNode(name);

					newProduction.appendChild(production.getChild(j));

					result.appendChild(newProduction);
				}
			}
		}

		result.setStartingName(this.getStartingName());

		return result;
	}

	/**
	 * getFirst
	 * 
	 * @param name
	 * @return TerminalList
	 */
	public TerminalList getFirst(String name)
	{
		ProductionNode[] productions = this.getProductionsByName(name);
		TerminalList result = new TerminalList();

		for (int i = 0; i < productions.length; i++)
		{
			ProductionNode production = productions[i];

			production.getFirst(result);
		}

		return result;
	}

	/**
	 * getFollow
	 * 
	 * @param name
	 * @return TerminalList
	 */
	public TerminalList getFollow(String name)
	{
		TerminalList terminals = new TerminalList();

		this.getFollow(name, terminals);

		return terminals;
	}

	/**
	 * getFollow
	 * 
	 * @param name
	 * @param terminals
	 */
	public void getFollow(String name, TerminalList terminals)
	{
		if (name.equals(this._startingName))
		{
			terminals.add(this.createEOFNode());
		}

		for (Item item : this.getProductionsWithNonTerminal(name))
		{
			item.getFollow(terminals);
		}
	}

	/**
	 * getProductionsWithNonTerminal
	 * 
	 * @param name
	 * @return
	 */
	private Item[] getProductionsWithNonTerminal(String name)
	{
		List<Item> result = new ArrayList<Item>();

		for (int i = 0; i < this.getChildCount(); i++)
		{
			ProductionNode productionNode = (ProductionNode) this.getChild(i);
			Item item = productionNode.findNonTerminal(name);

			if (item != null)
			{
				result.add(item);
			}
		}

		return result.toArray(new Item[result.size()]);
	}

	/**
	 * getProductionNames
	 * 
	 * @return String[]
	 */
	public String[] getProductionNames()
	{
		List<String> names = new ArrayList<String>();

		// NOTE: We want the production names in the order in which they were
		// added, so we traverse the child nodes instead of using the keys from
		// the productions Map
		for (int i = 0; i < this.getChildCount(); i++)
		{
			IGrammarNode child = (IGrammarNode) this.getChild(i);

			if (child.getTypeIndex() == GrammarNodeTypes.PRODUCTION)
			{
				ProductionNode production = (ProductionNode) child;
				String name = production.getName();

				if (names.contains(name) == false)
				{
					names.add(name);
				}
			}
		}

		return names.toArray(new String[names.size()]);
	}

	/**
	 * getProductions
	 * 
	 * @param name
	 * @return
	 */
	public ProductionNode[] getProductionsByName(String name)
	{
		List<ProductionNode> result;

		if (this._productions.containsKey(name))
		{
			result = this._productions.get(name);
		}
		else
		{
			result = new ArrayList<ProductionNode>();
		}

		return result.toArray(new ProductionNode[result.size()]);
	}

	// /**
	// * getSortedGrammar
	// *
	// * @return GrammarNode
	// */
	// public GrammarNode getSortedGrammar() {
	// GrammarNode result = new GrammarNode(this.getName());
	//		
	// result.setStartingName(this.getStartingName());
	//		
	// return result;
	// }

	/**
	 * getStartingProduction
	 * 
	 * @return
	 */
	public String getStartingName()
	{
		return this._startingName;
	}

	/**
	 * getSymbols
	 * 
	 * @return
	 */
	public IGrammarNode[] getSymbols()
	{
		List<IGrammarNode> symbols = new ArrayList<IGrammarNode>();

		// add EOF
		symbols.add(this.createEOFNode());

		// add all descendant symbols
		for (int i = 0; i < this.getChildCount(); i++)
		{
			IGrammarNode child = (IGrammarNode) this.getChild(i);

			child.getSymbols(symbols);
		}

		GrammarNodeBase[] result = symbols.toArray(new GrammarNodeBase[symbols.size()]);
		Arrays.sort(result, new Comparator<GrammarNodeBase>()
		{
			public int compare(GrammarNodeBase o1, GrammarNodeBase o2)
			{
				int type1 = o1.getTypeIndex();
				int type2 = o2.getTypeIndex();
				int result = 0;

				if (type1 == type2)
				{
					if (type1 == GrammarNodeTypes.TERMINAL)
					{
						int index1 = ((TerminalNode) o1).getIndex();
						int index2 = ((TerminalNode) o2).getIndex();

						result = index1 - index2;
					}
					else
					{
						result = o1.getName().compareTo(o2.getName());
					}
				}
				else
				{
					if (type1 == GrammarNodeTypes.TERMINAL)
					{
						result = -1;
					}
					else
					{
						result = 1;
					}
				}

				return result;
			}
		});

		return result;
	}

	/**
	 * setStartingName
	 * 
	 * @param startingName
	 */
	public void setStartingName(String startingName)
	{
		this._startingName = startingName;
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
				writer.println();
				this.getChild(i).getSource(writer);
			}
		}
	}
}
