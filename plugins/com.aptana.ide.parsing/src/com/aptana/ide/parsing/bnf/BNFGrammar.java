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

import com.aptana.ide.parsing.bnf.nodes.GrammarNode;
import com.aptana.ide.parsing.bnf.nodes.ProductionNode;
import com.aptana.ide.parsing.bnf.nodes.SequenceNode;

/**
 * @author Kevin Lindsey
 */
public final class BNFGrammar
{
	/**
	 * BNFGrammar
	 */
	private BNFGrammar()
	{
	}
	
	/**
	 * addGrammarProduction
	 * 
	 * @param grammar
	 */
	private static void addGrammarProduction(GrammarNode grammar)
	{
		ProductionNode production = grammar.createProductionNode("Grammar"); //$NON-NLS-1$

		SequenceNode s = grammar.createSequenceNode();
		s.appendChild(grammar.createNonTerminalNode("Productions")); //$NON-NLS-1$

		production.appendChild(s);
		grammar.appendChild(production);
	}

	/**
	 * addProductionsProduction
	 * 
	 * @param grammar
	 */
	private static void addProductionsProduction(GrammarNode grammar)
	{
		ProductionNode production = grammar.createProductionNode("Productions"); //$NON-NLS-1$

		SequenceNode s1 = grammar.createSequenceNode();
		s1.setActionName("AddProduction"); //$NON-NLS-1$
		s1.appendChild(grammar.createNonTerminalNode("Productions")); //$NON-NLS-1$
		s1.appendChild(grammar.createNonTerminalNode("Production")); //$NON-NLS-1$

		SequenceNode s2 = grammar.createSequenceNode();
		s2.setActionName("FirstProduction"); //$NON-NLS-1$
		s2.appendChild(grammar.createNonTerminalNode("Production")); //$NON-NLS-1$

		production.appendChild(s1);
		production.appendChild(s2);
		grammar.appendChild(production);
	}

	/**
	 * addProductionProduction
	 * 
	 * @param grammar
	 */
	private static void addProductionProduction(GrammarNode grammar)
	{
		ProductionNode production = grammar.createProductionNode("Production"); //$NON-NLS-1$

		SequenceNode s = grammar.createSequenceNode();
		s.setActionName("Production"); //$NON-NLS-1$
		s.appendChild(grammar.createTerminalNode("NONTERMINAL")); //$NON-NLS-1$
		s.appendChild(grammar.createTerminalNode("COLON")); //$NON-NLS-1$
		s.appendChild(grammar.createNonTerminalNode("Statements")); //$NON-NLS-1$
		s.appendChild(grammar.createTerminalNode("SEMICOLON")); //$NON-NLS-1$

		production.appendChild(s);
		grammar.appendChild(production);
	}

	/**
	 * addStatementsProduction
	 * 
	 * @param grammar
	 */
	private static void addStatementsProduction(GrammarNode grammar)
	{
		ProductionNode production = grammar.createProductionNode("Statements"); //$NON-NLS-1$

		SequenceNode s1 = grammar.createSequenceNode();
		s1.setActionName("AddSequence"); //$NON-NLS-1$
		s1.appendChild(grammar.createNonTerminalNode("Statements")); //$NON-NLS-1$
		s1.appendChild(grammar.createTerminalNode("PIPE")); //$NON-NLS-1$
		s1.appendChild(grammar.createNonTerminalNode("Symbols")); //$NON-NLS-1$

		SequenceNode s2 = grammar.createSequenceNode();
		s2.setActionName("AddNamedSequence"); //$NON-NLS-1$
		s2.appendChild(grammar.createNonTerminalNode("Statements")); //$NON-NLS-1$
		s2.appendChild(grammar.createTerminalNode("PIPE")); //$NON-NLS-1$
		s2.appendChild(grammar.createNonTerminalNode("Symbols")); //$NON-NLS-1$
		s2.appendChild(grammar.createTerminalNode("NAME")); //$NON-NLS-1$

		SequenceNode s3 = grammar.createSequenceNode();
		s3.setActionName("FirstSequence"); //$NON-NLS-1$
		s3.appendChild(grammar.createNonTerminalNode("Symbols")); //$NON-NLS-1$

		SequenceNode s4 = grammar.createSequenceNode();
		s4.setActionName("NamedSequence"); //$NON-NLS-1$
		s4.appendChild(grammar.createNonTerminalNode("Symbols")); //$NON-NLS-1$
		s4.appendChild(grammar.createTerminalNode("NAME")); //$NON-NLS-1$

		production.appendChild(s1);
		production.appendChild(s2);
		production.appendChild(s3);
		production.appendChild(s4);
		grammar.appendChild(production);
	}

	/**
	 * addSymbolsProduction
	 * 
	 * @param grammar
	 */
	private static void addSymbolsProduction(GrammarNode grammar)
	{
		ProductionNode production = grammar.createProductionNode("Symbols"); //$NON-NLS-1$

		SequenceNode s1 = grammar.createSequenceNode();
		s1.setActionName("AddSymbol"); //$NON-NLS-1$
		s1.appendChild(grammar.createNonTerminalNode("Symbols")); //$NON-NLS-1$
		s1.appendChild(grammar.createNonTerminalNode("Symbol")); //$NON-NLS-1$

		SequenceNode s2 = grammar.createSequenceNode();
		s2.setActionName("FirstSymbol"); //$NON-NLS-1$
		s2.appendChild(grammar.createNonTerminalNode("Symbol")); //$NON-NLS-1$

		production.appendChild(s1);
		production.appendChild(s2);
		grammar.appendChild(production);
	}

	/**
	 * addSymbolProduction
	 * 
	 * @param grammar
	 */
	private static void addSymbolProduction(GrammarNode grammar)
	{
		ProductionNode production = grammar.createProductionNode("Symbol"); //$NON-NLS-1$

		SequenceNode s1 = grammar.createSequenceNode();
		s1.setActionName("Terminal"); //$NON-NLS-1$
		s1.appendChild(grammar.createTerminalNode("TERMINAL")); //$NON-NLS-1$
		
		SequenceNode s2 = grammar.createSequenceNode();
		s2.setActionName("AliasedTerminal"); //$NON-NLS-1$
		s2.appendChild(grammar.createTerminalNode("TERMINAL")); //$NON-NLS-1$
		s2.appendChild(grammar.createTerminalNode("EQUAL")); //$NON-NLS-1$
		s2.appendChild(grammar.createTerminalNode("ALIAS")); //$NON-NLS-1$

		SequenceNode s3 = grammar.createSequenceNode();
		s3.setActionName("NonTerminal"); //$NON-NLS-1$
		s3.appendChild(grammar.createTerminalNode("NONTERMINAL")); //$NON-NLS-1$
		
		SequenceNode s4 = grammar.createSequenceNode();
		s4.setActionName("AliasedNonTerminal"); //$NON-NLS-1$
		s4.appendChild(grammar.createTerminalNode("NONTERMINAL")); //$NON-NLS-1$
		s4.appendChild(grammar.createTerminalNode("EQUAL")); //$NON-NLS-1$
		s4.appendChild(grammar.createTerminalNode("ALIAS")); //$NON-NLS-1$
		
		SequenceNode s5 = grammar.createSequenceNode();
		s5.setActionName("Empty"); //$NON-NLS-1$
		s5.appendChild(grammar.createTerminalNode("EMPTY")); //$NON-NLS-1$

		production.appendChild(s1);
		production.appendChild(s2);
		production.appendChild(s3);
		production.appendChild(s4);
//		production.appendChild(s5);
		grammar.appendChild(production);
	}

	/**
	 * getGrammar
	 * 
	 * @return GrammarNode
	 */
	public static GrammarNode getGrammar()
	{
		// create grammar
		GrammarNode grammar = new GrammarNode("text/bnf"); //$NON-NLS-1$

		addGrammarProduction(grammar);
		addProductionsProduction(grammar);
		addProductionProduction(grammar);
		addStatementsProduction(grammar);
		addSymbolsProduction(grammar);
		addSymbolProduction(grammar);

		return grammar;
	}
}
