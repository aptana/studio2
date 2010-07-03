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
package com.aptana.ide.parsing.experimental;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.bnf.Action;
import com.aptana.ide.parsing.bnf.IReductionContext;
import com.aptana.ide.parsing.bnf.IReductionHandler;
import com.aptana.ide.parsing.bnf.Item;
import com.aptana.ide.parsing.bnf.ReductionContext;
import com.aptana.ide.parsing.bnf.State;
import com.aptana.ide.parsing.bnf.TerminalList;
import com.aptana.ide.parsing.bnf.nodes.GrammarNode;
import com.aptana.ide.parsing.bnf.nodes.GrammarNodeTypes;
import com.aptana.ide.parsing.bnf.nodes.IGrammarNode;
import com.aptana.ide.parsing.bnf.nodes.ProductionNode;
import com.aptana.ide.parsing.bnf.nodes.SequenceNode;

/**
 * @author Kevin Lindsey
 */
public class Parser
{
	private State[] _states;
	private GrammarNode _grammar;
	private String _message;
	private List<IReductionHandler> _handlers;

	/**
	 * addHandler
	 * 
	 * @param handler
	 */
	public void addHandler(IReductionHandler handler)
	{
		if (this._handlers == null)
		{
			this._handlers = new ArrayList<IReductionHandler>();
		}

		this._handlers.add(handler);
	}

	/**
	 * generateTable
	 * 
	 * @param grammar
	 */
	public void generateTable(GrammarNode grammar)
	{
		GrammarNode expandedGrammar = grammar.getExpandedGrammar();
		this._grammar = expandedGrammar;

		Item startItem = new Item((ProductionNode) expandedGrammar.getChild(0));

		// state cache used to prevent the creation of duplicate states
			Map<String,State> stateMap = new HashMap<String,State>();

		List<State> states = new ArrayList<State>();
		states.add(new State(expandedGrammar, startItem));

		int index = 0;

		while (index < states.size())
		{
			State currentState = states.get(index);
			List<IGrammarNode> transitions = currentState.getTransitionInputs();

			for (int i = 0; i < transitions.size(); i++)
			{
				IGrammarNode transition = transitions.get(i);
				String name = transition.getName();
				State newState = currentState.getTransitionState(transition, stateMap);

				if (newState.getIndex() == -1)
				{
					// state is new
					newState.setIndex(states.size());
					states.add(newState);
				}

				if (transition.getTypeIndex() == GrammarNodeTypes.NONTERMINAL)
				{
					currentState.addGoto(name, newState.getIndex());
				}
				else
				{
					currentState.addShift(name, newState.getIndex());
				}
			}

			index++;
		}

		for (int i = 0; i < states.size(); i++)
		{
			State state = states.get(i);
			Item[] items = state.getItems();

			for (int j = 0; j < items.length; j++)
			{
				Item item = items[j];

				if (item.isCompletedItem())
				{
					String name = item.getName();

					if (expandedGrammar.getStartingName().equals(name))
					{
						state.addAccept("$");
					}
					else
					{
						int newState = item.getIndex();
						TerminalList terminals = expandedGrammar.getFollow(name);

						for (int k = 0; k < terminals.size(); k++)
						{
							String terminalName = terminals.get(k).getName();

							state.addReduce(terminalName, newState);
						}
					}
				}
			}
		}

		this._states = states.toArray(new State[states.size()]);
	}

	/**
	 * getGrammar
	 * 
	 * @return
	 */
	public GrammarNode getGrammar()
	{
		return this._grammar;
	}

	/**
	 * getMessage
	 * 
	 * @return
	 */
	public String getMessage()
	{
		return this._message;
	}

	/**
	 * getStates
	 * 
	 * @return
	 */
	public State[] getStates()
	{
		return this._states;
	}

	/**
	 * parse
	 * 
	 * @param lexemes
	 * @return boolean
	 */
	public boolean parse(Lexeme[] lexemes)
	{
		boolean accept = false;
		boolean error = false;
		int lexemeIndex = 0;
		Stack<State> states = new Stack<State>();

		State currentState = this._states[0];
		states.push(currentState);
		Lexeme currentLexeme = lexemes[lexemeIndex];
		String currentSymbol = currentLexeme.getType();

		this._message = "";
		this.resetHandlers();

		while (accept == false && error == false && lexemeIndex < lexemes.length)
		{
			Action action = currentState.getAction(currentSymbol);

			switch (action.type)
			{
				case SHIFT:
					this.pushCurrentLexeme(currentLexeme);

					lexemeIndex++;

					if (lexemeIndex < lexemes.length)
					{
						currentLexeme = lexemes[lexemeIndex];
						currentSymbol = currentLexeme.getType();
					}
					else
					{
						currentLexeme = null;
						currentSymbol = null;
					}

					currentState = this._states[action.newState];
					states.push(currentState);
					break;

				case REDUCE:
					ProductionNode production = (ProductionNode) this._grammar.getChild(action.newState);
					String name = production.getName();
					int symbolCount = production.getSymbolCount();

					for (int i = 0; i < symbolCount; i++)
					{
						states.pop();
					}

					callHandlers(production, symbolCount);

					Action newAction = states.peek().getAction(name);
					currentState = this._states[newAction.newState];
					states.push(currentState);
					break;

				case ACCEPT:
					accept = true;
					break;

				case GOTO:
					this._message = "Unexpected goto at " + currentLexeme + " in state " + currentState.getIndex();
					error = true;
					break;

				case ERROR:
					this._message = "No transition for " + currentLexeme + " in state " + currentState.getIndex();
					error = true;
					break;

				default:
					break;
			}

		}

		return accept;
	}

	/**
	 * callHandlers
	 * 
	 * @param production
	 * @param symbolCount
	 */
	private void callHandlers(ProductionNode production, int symbolCount)
	{
		if (this._handlers != null)
		{
			SequenceNode rule = (SequenceNode) production.getChild(0);
			IReductionContext context = new ReductionContext(production.getName(), rule);
			Iterator<IReductionHandler> iter = this._handlers.iterator();

			while (iter.hasNext())
			{
				iter.next().reduce(context);
			}
		}
	}

	/**
	 * pushCurrentLexeme
	 * 
	 * @param currentLexeme
	 */
	private void pushCurrentLexeme(Lexeme currentLexeme)
	{
		if (this._handlers != null)
		{
			Iterator<IReductionHandler> iter = this._handlers.iterator();

			while (iter.hasNext())
			{
				iter.next().push(currentLexeme);
			}
		}
	}

	/**
	 * removeHandler
	 * 
	 * @param handler
	 */
	public void removeHandler(IReductionHandler handler)
	{
		if (this._handlers != null)
		{
			this._handlers.remove(handler);
		}
	}

	/**
	 * resetHandlers
	 */
	public void resetHandlers()
	{
		if (this._handlers != null)
		{
			Iterator<IReductionHandler> iter = this._handlers.iterator();

			while (iter.hasNext())
			{
				iter.next().beforeParse(null, null);
			}
		}
	}
}
