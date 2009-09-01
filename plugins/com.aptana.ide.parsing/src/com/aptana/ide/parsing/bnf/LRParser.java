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
 * with certain Eclipse Public Licensed code and certain additional terms
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

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenList;
import com.aptana.ide.parsing.AbstractParser;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.bnf.nodes.GrammarNode;
import com.aptana.ide.parsing.bnf.nodes.GrammarNodeTypes;
import com.aptana.ide.parsing.bnf.nodes.IGrammarNode;
import com.aptana.ide.parsing.bnf.nodes.ProductionNode;
import com.aptana.ide.parsing.bnf.nodes.SequenceNode;
import com.aptana.ide.parsing.bnf.nodes.TerminalNode;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class LRParser extends AbstractParser
{
	/**
	 * The original grammar that defines this parser's language
	 */
	protected GrammarNode _grammar;
	
	private GrammarNode _expandedGrammar;
	private State[] _states;
	private List<IReductionHandler> _handlers;
	private String _message;
	
	/**
	 * LRParser
	 * 
	 * @param grammar
	 * @throws ParserInitializationException
	 */
	protected LRParser(String language) throws ParserInitializationException
	{
		super(language);
		
		this._states = this.createStateTable();
	}
	
	/**
	 * LRParser
	 * 
	 * @param language
	 * @param grammar
	 * @throws ParserInitializationException
	 */
	protected LRParser(TokenList tokenList, GrammarNode grammar) throws ParserInitializationException
	{
		super(tokenList);
		
		this._grammar = grammar;
		this._states = this.createStateTable();
	}

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
	 * createReductionContext
	 * 
	 * @param rule
	 * @return
	 */
	protected IReductionContext createReductionContext(String productionName, SequenceNode rule)
	{
		return new ReductionContext(productionName, rule, this);
	}
	
	/**
	 * createStateTable
	 * 
	 * @param grammar
	 */
	private State[] createStateTable()
	{
		List<State> states = new ArrayList<State>();
		GrammarNode grammar = this.getGrammar();

		if (grammar != null)
		{
			// remove alterations and replace with individual rules
			GrammarNode expandedGrammar = grammar.getExpandedGrammar();

			// store expanded grammar for later when we're parsing
			this._expandedGrammar = grammar.getExpandedGrammar();

			// get start item
			Item startItem = new Item((ProductionNode) expandedGrammar.getChild(0));

			// state cache used to prevent the creation of duplicate states
			Map<String,State> stateCache = new HashMap<String,State>();

			// add start item to state list to begin state creation phase
			states.add(new State(expandedGrammar, startItem));

			// process all states noting that the states list may grow with each iteration.
			// This outer loop will terminate once the list stops growing, i.e., all states
			// have been enumerated/created
			for (int index = 0; index < states.size(); index++)
			{
				State currentState = states.get(index);

				// process all transitions in this state
				for (IGrammarNode transition : currentState.getTransitionInputs())
				{
					String name = transition.getName();
					State newState = currentState.getTransitionState(transition, stateCache);

					// Add this state if it is a new state
					if (newState.getIndex() == -1)
					{
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
			}

			// Make a final pass of the resulting states to tag the accept and reduction states
			for (State state : states)
			{
				for (Item item : state.getItems())
				{
					if (item.isCompletedItem())
					{
						String name = item.getName();

						if (expandedGrammar.getStartingName().equals(name))
						{
							state.addAccept("$"); //$NON-NLS-1$
						}
						else
						{
							int newState = item.getIndex();

							for (TerminalNode terminal : expandedGrammar.getFollow(name))
							{
								state.addReduce(terminal.getName(), newState);
							}
						}
					}
				}
			}
		}

		return states.toArray(new State[states.size()]);
	}
	
	/**
	 * fireOnAfterParse
	 */
	private void fireOnAfterParse(IParseNode parentNode)
	{
		if (this._handlers != null)
		{
			IParseState parseState = this.getParseState();
			
			for (IReductionHandler handler : this._handlers)
			{
				handler.afterParse(parseState, parentNode);
			}
		}
	}
	
	/**
	 * fireOnBeforeParse
	 */
	private void fireOnBeforeParse(IParseNode parentNode)
	{
		if (this._handlers != null)
		{
			IParseState parseState = this.getParseState();
			
			for (IReductionHandler handler : this._handlers)
			{
				handler.beforeParse(parseState, parentNode);
			}
		}
	}
	
	/**
	 * fireReductions
	 * 
	 * @param production
	 */
	private void fireReductions(ProductionNode production)
	{
		if (this._handlers != null)
		{
			if (production != null)
			{
				String productionName = production.getName();
				
				// NOTE: expanded grammars have only one rule per production
				SequenceNode rule = (SequenceNode) production.getChild(0);
				
				// create context
				IReductionContext context = this.createReductionContext(productionName, rule);
				
				// reduce all handlers
				for (IReductionHandler handler : this._handlers)
				{
					handler.reduce(context);
				}
			}
		}
	}
	
	/**
	 * getExpandedGramamr
	 * 
	 * @return GrammarNode
	 */
	public GrammarNode getExpandedGrammar()
	{
		return this._expandedGrammar;
	}
	
	/**
	 * getGrammar
	 * 
	 * @return GrammarNode
	 */
	public GrammarNode getGrammar()
	{
		return this._grammar;
	}
	
	/**
	 * getMessage
	 * 
	 * @return String
	 */
	public String getMessage()
	{
		return this._message;
	}
	
	/**
	 * @see com.aptana.ide.parsing.AbstractParser#parseAll(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void parseAll(IParseNode parentNode) throws ParseException, LexerException
	{
		IParseNode rootNode = this.getParseRootNode(parentNode, IParseNode.class);
		
		boolean accept = false;
		boolean error = false;
		Stack<State> states = new Stack<State>();
		
		// let handlers know we're about to parse so they can initialize
		// or reset themselves
		this.fireOnBeforeParse(rootNode);

		if (this._states != null && this._states.length > 0)
		{
			State currentState = this._states[0];
			states.push(currentState);
			
			this.advance();
			this._message = ""; //$NON-NLS-1$
	
			while (accept == false && error == false)
			{
				Action action = currentState.getAction(this.currentLexeme.getType());
	
				switch (action.type)
				{
					case SHIFT:
						this.pushCurrentLexeme(currentLexeme);
	
						if (this.isEOS())
						{
							break;
						}
						else
						{
							this.advance();
						}
	
						currentState = this._states[action.newState];
						states.push(currentState);
						break;
	
					case REDUCE:
						ProductionNode production = (ProductionNode) this._expandedGrammar.getChild(action.newState);
						String name = production.getName();
						int symbolCount = production.getSymbolCount();
	
						for (int i = 0; i < symbolCount; i++)
						{
							states.pop();
						}
	
						this.fireReductions(production);
	
						Action newAction = states.peek().getAction(name);
						currentState = this._states[newAction.newState];
						states.push(currentState);
						break;
	
					case ACCEPT:
						accept = true;
						break;
	
					case GOTO:
						this._message = MessageFormat.format(Messages.getString("LRParser.Unexpected_goto_at_0_in_state_1"), currentLexeme, currentState.getIndex()); //$NON-NLS-1$
						error = true;
						break;
	
					case ERROR:
						if (this.recover())
						{
							currentState = this._states[0];
							states.clear();
							states.push(currentState);
						}
						else
						{
							this._message = MessageFormat.format(Messages.getString("LRParser.No_transition_for_0_in_state_1"), currentLexeme, currentState.getIndex()); //$NON-NLS-1$
							error = true;
						}
						break;
	
					default:
						break;
				}
			}
		}
		
		// let handlers know we're done parsing so they can cleanup or
		// finish up their processing
		this.fireOnAfterParse(rootNode);
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
			for (IReductionHandler handler : this._handlers)
			{
				handler.push(currentLexeme);
			}
		}
	}
	
	/**
	 * Try to recover from a parse error. The return value determines if the parser will continue
	 * or not. Returning a value of true will cause the parser to continue from the top-level
	 * 
	 * @return
	 */
	protected boolean recover()
	{
		return false;
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
}
