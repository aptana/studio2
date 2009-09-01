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

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.bnf.nodes.GrammarNode;
import com.aptana.ide.parsing.bnf.nodes.IGrammarNode;
import com.aptana.ide.parsing.bnf.nodes.NonTerminalNode;
import com.aptana.ide.parsing.bnf.nodes.ProductionNode;
import com.aptana.ide.parsing.bnf.nodes.SequenceNode;
import com.aptana.ide.parsing.bnf.nodes.TerminalNode;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class BNFHandler extends AbstractHandler
{
	private GrammarNode _grammar;

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
	 * onAddProduction
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onAddProduction(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		this._grammar.appendChild((ProductionNode) nodes[1]);

		return null;
	}

	/**
	 * onAliasedNonTerminal
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onAliasedNonTerminal(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme nonTerminalLexeme = (Lexeme) nodes[0];
		String nonTerminalName = nonTerminalLexeme.getText();
		Lexeme aliasLexeme = (Lexeme) nodes[2];
		String nonTerminalAlias = aliasLexeme.getText();
		NonTerminalNode nonTerminal = this._grammar.createNonTerminalNode(nonTerminalName);
		
		nonTerminal.setAlias(nonTerminalAlias);
		
		return nonTerminal;
	}
	
	/**
	 * onAliasedTerminal
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onAliasedTerminal(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme terminalLexeme = (Lexeme) nodes[0];
		String terminalName = terminalLexeme.getText();
		Lexeme aliasLexeme = (Lexeme) nodes[2];
		String terminalAlias = aliasLexeme.getText();
		TerminalNode terminal = this._grammar.createTerminalNode(terminalName);
		
		terminal.setAlias(terminalAlias);
		
		return terminal;
	}
	
	/**
	 * onEmpty
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onEmpty(IReductionContext context)
	{
		return this._grammar.createEmptyNode();
	}
	
	/**
	 * onFirstProduction
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onFirstProduction(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		this._grammar.appendChild((ProductionNode) nodes[0]);

		return null;
	}

	/**
	 * onProduction
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onProduction(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		String productionName = ((Lexeme) nodes[0]).getText();
		ProductionNode placeholder = (ProductionNode) nodes[2];
		ProductionNode production = this._grammar.createProductionNode(productionName);

		for (int i = 0; i < placeholder.getChildCount(); i++)
		{
			production.appendChild(placeholder.getChild(i));
		}

		return production;
	}

	/**
	 * onAddSequence
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onAddSequence(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ProductionNode placeholder = (ProductionNode) nodes[0];
		SequenceNode sequence = (SequenceNode) nodes[2];

		placeholder.appendChild(sequence);

		return placeholder;
	}

	/**
	 * onAddNamedSequence
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onAddNamedSequence(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ProductionNode placeholder = (ProductionNode) nodes[0];
		SequenceNode sequence = (SequenceNode) nodes[2];
		String sequenceName = ((Lexeme) nodes[3]).getText();

		sequenceName = sequenceName.substring(1, sequenceName.length() - 1);
		sequence.setActionName(sequenceName);

		placeholder.appendChild(sequence);

		return placeholder;
	}

	/**
	 * onFirstSequence
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onFirstSequence(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ProductionNode placeholder = this._grammar.createProductionNode("#placeholder"); //$NON-NLS-1$

		placeholder.appendChild((SequenceNode) nodes[0]);

		return placeholder;
	}

	/**
	 * onNamedSequence
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onNamedSequence(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ProductionNode placeholder = this._grammar.createProductionNode("#placeholder"); //$NON-NLS-1$
		SequenceNode sequence = (SequenceNode) nodes[0];
		String sequenceName = ((Lexeme) nodes[1]).getText();

		sequenceName = sequenceName.substring(1, sequenceName.length() - 1);
		sequence.setActionName(sequenceName);

		placeholder.appendChild(sequence);

		return placeholder;
	}

	/**
	 * onAddSymbol
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onAddSymbol(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		SequenceNode sequence = (SequenceNode) nodes[0];

		sequence.appendChild((IGrammarNode) nodes[1]);

		return sequence;
	}

	/**
	 * onFirstSymbol
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onFirstSymbol(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		SequenceNode sequence = this._grammar.createSequenceNode();

		sequence.appendChild((IGrammarNode) nodes[0]);

		return sequence;
	}

	/**
	 * onTerminal
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onTerminal(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		return this._grammar.createTerminalNode(((Lexeme) nodes[0]).getText());
	}

	/**
	 * onNonTerminal
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onNonTerminal(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		return this._grammar.createNonTerminalNode(((Lexeme) nodes[0]).getText());
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.AbstractHandler#beforeParse(com.aptana.ide.parsing.IParseState, com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void beforeParse(IParseState parseState, IParseNode parentNode)
	{
		super.beforeParse(parseState, parentNode);
		
		this._grammar = new GrammarNode("test"); //$NON-NLS-1$
	}
}
