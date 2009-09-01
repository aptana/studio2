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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.ide.parsing.bnf.nodes.GrammarNode;
import com.aptana.ide.parsing.bnf.nodes.IGrammarNode;
import com.aptana.ide.parsing.bnf.nodes.ProductionNode;

/**
 * @author Kevin Lindsey
 */
public class State
{
	private static final Action ERROR_ACTION = new Action(ActionType.ERROR, -1);

	private GrammarNode _grammar;
	private List<Item> _items;
	private Map<String,Action> _transitions;
	private int _index;

	/**
	 * State
	 * 
	 * @param grammar
	 */
	private State(GrammarNode grammar)
	{
		this._grammar = grammar;
		this._items = new ArrayList<Item>();
		this._index = -1;
		this._transitions = new HashMap<String,Action>();
	}

	/**
	 * State
	 * 
	 * @param grammar
	 * @param item
	 */
	public State(GrammarNode grammar, Item item)
	{
		this(grammar);

		this._items.add(item);
		this.buildSet();
	}

	/**
	 * addAccept
	 * 
	 * @param name
	 */
	public void addAccept(String name)
	{
		Action action = new Action(ActionType.ACCEPT, -1);

		if (this._transitions.containsKey(name))
		{
			System.out.print("Accept state " + action + " not added because "); //$NON-NLS-1$ //$NON-NLS-2$
			System.out.println(name + " is already defined as " + this._transitions.get(name) + ". "); //$NON-NLS-1$ //$NON-NLS-2$
			System.out.println("  " + this.getIndex() + ": " + this.getHashKey()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			this._transitions.put(name, action);
		}
	}

	/**
	 * addError
	 * 
	 * @param name
	 * @param errorNumber
	 */
	public void addError(String name, int errorNumber)
	{
		Action action = new Action(ActionType.ERROR, errorNumber);

		if (this._transitions.containsKey(name))
		{
			System.out.print("Error state " + action + " not added because. "); //$NON-NLS-1$ //$NON-NLS-2$
			System.out.println(name + " is already defined as " + this._transitions.get(name) + ". "); //$NON-NLS-1$ //$NON-NLS-2$
			System.out.println("  " + this.getIndex() + ": " + this.getHashKey()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			this._transitions.put(name, action);
		}
	}

	/**
	 * addGoto
	 * 
	 * @param name
	 * @param newState
	 */
	public void addGoto(String name, int newState)
	{
		Action action = new Action(ActionType.GOTO, newState);

		if (this._transitions.containsKey(name))
		{
			System.out.print("Goto state " + action + " not added because "); //$NON-NLS-1$ //$NON-NLS-2$
			System.out.println(name + " is already defined as " + this._transitions.get(name) + ". "); //$NON-NLS-1$ //$NON-NLS-2$
			System.out.println("  " + this.getIndex() + ": " + this.getHashKey()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			this._transitions.put(name, action);
		}
	}

	/**
	 * addReduce
	 * 
	 * @param name
	 * @param newState
	 */
	public void addReduce(String name, int newState)
	{
		Action action = new Action(ActionType.REDUCE, newState);

		if (this._transitions.containsKey(name))
		{
//			System.out.print("Reduce state " + action + " not added because ");
//			System.out.println(name + " is already defined as " + this._transitions.get(name) + ". ");
//			System.out.println("  " + this.getIndex() + ": " + this.getHashKey());
		}
		else
		{
			this._transitions.put(name, action);
		}
	}

	/**
	 * addShift
	 * 
	 * @param name
	 * @param newState
	 */
	public void addShift(String name, int newState)
	{
		Action action = new Action(ActionType.SHIFT, newState);

		if (this._transitions.containsKey(name))
		{
			System.out.print("Shift state " + action + " not added because "); //$NON-NLS-1$ //$NON-NLS-2$
			System.out.println(name + " is already defined as " + this._transitions.get(name)); //$NON-NLS-1$
			System.out.println("  " + this.getIndex() + ": " + this.getHashKey()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			this._transitions.put(name, action);
		}
	}

	/**
	 * buildSet
	 * 
	 * @param set
	 * @param item
	 */
	private void buildSet()
	{
		int index = 0;

		while (index < this._items.size())
		{
			Item currentItem = this._items.get(index);

			if (currentItem.isAtNonTerminal() && currentItem.isCompletedItem() == false)
			{
				IGrammarNode current = currentItem.getCurrentNode();
				String name = current.getName();
				ProductionNode[] productions = this._grammar.getProductionsByName(name);

				for (int i = 0; i < productions.length; i++)
				{
					Item candidate = new Item(productions[i]);

					if (this._items.contains(candidate) == false)
					{
						this._items.add(candidate);
					}
				}
			}

			index++;
		}
	}

	/**
	 * getAction
	 * 
	 * @param name
	 * @return
	 */
	public Action getAction(String name)
	{
		Action result = this._transitions.get(name);

		if (result == null)
		{
			result = ERROR_ACTION;
		}

		return result;
	}

	/**
	 * getHashKey
	 * 
	 * @return
	 */
	private String getHashKey()
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < this._items.size(); i++)
		{
			Item item = this._items.get(i);

			if (i > 0)
			{
				buffer.append(", "); //$NON-NLS-1$
			}

			buffer.append(item.toString());
		}

		return buffer.toString();
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public int getIndex()
	{
		return this._index;
	}

	/**
	 * getItems
	 * 
	 * @return
	 */
	public Item[] getItems()
	{
		return this._items.toArray(new Item[this._items.size()]);
	}

	/**
	 * Generate a list of all transitions out of this state. The resulting
	 * list will not contain duplicates
	 * 
	 * @return
	 */
	public List<IGrammarNode> getTransitionInputs()
	{
		List<IGrammarNode> result = new ArrayList<IGrammarNode>();

		for (Item item : this._items)
		{
			IGrammarNode currentNode = item.getCurrentNode();

			if (currentNode != null && result.contains(currentNode) == false)
			{
				result.add(currentNode);
			}
		}

		return result;
	}

	/**
	 * getTransitionState
	 * 
	 * @param input
	 * @return
	 */
	public State getTransitionState(IGrammarNode input, Map<String,State> stateMap)
	{
		State state = new State(this._grammar);

		for (Item item : this._items)
		{
			if (item.isCompletedItem() == false && item.getCurrentNode().equals(input))
			{
				// make a clone of the item so each can advance independently
				// of the other
				Item newItem = new Item(item);

				// advance over matching input
				newItem.advance();

				// add cloned item to resulting states item set
				state._items.add(newItem);
			}
		}

		// find e-closures
		state.buildSet();

		// NOTE: this should never be null
		if (stateMap != null)
		{
			// get the state's unique name
			String name = state.getHashKey();
	
			// so we can see if we have one for this state already
			if (stateMap.containsKey(name))
			{
				// we do, so use the state we already have
				state = stateMap.get(name);
			}
			else
			{
				// otherwise, store in the cache for possible future references
				stateMap.put(name, state);
			}
		}

		return state;
	}

	/**
	 * setIndex
	 * 
	 * @param index
	 */
	public void setIndex(int index)
	{
		this._index = index;
	}

	/**
	 * toString
	 */
	public String toString()
	{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);

		for (int i = 0; i < this._items.size(); i++)
		{
			writer.println(this._items.get(i).toString());
		}

		return sw.toString();
	}
}
