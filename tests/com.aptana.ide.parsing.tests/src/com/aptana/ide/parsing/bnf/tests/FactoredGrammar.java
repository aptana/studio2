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
package com.aptana.ide.parsing.bnf.tests;

import com.aptana.ide.parsing.bnf.nodes.EmptyNode;
import com.aptana.ide.parsing.bnf.nodes.GrammarNode;
import com.aptana.ide.parsing.bnf.nodes.NonTerminalNode;
import com.aptana.ide.parsing.bnf.nodes.ProductionNode;
import com.aptana.ide.parsing.bnf.nodes.SequenceNode;
import com.aptana.ide.parsing.bnf.nodes.TerminalNode;

/**
 * @author Kevin Lindsey
 */
public class FactoredGrammar {
	/**
	 * SampleGrammar
	 */
	private FactoredGrammar() {
	}
	
	/**
	 * getGrammar
	 *
	 * @return GrammarNode
	 */
	public static GrammarNode getGrammar() {
		// create grammar
		GrammarNode grammar = new GrammarNode("text/expression");
		
		grammar.appendChild(createEProductions(grammar));
		grammar.appendChild(createQProductions(grammar));
		grammar.appendChild(createTProductions(grammar));
		grammar.appendChild(createRProductions(grammar));
		grammar.appendChild(createFProductions(grammar));
		
		return grammar;
	}
	
	private static ProductionNode createEProductions(GrammarNode grammar) {
		// make production
		ProductionNode production = grammar.createProductionNode("E");

		// add Expression '+' Term
		SequenceNode and = grammar.createSequenceNode();
		NonTerminalNode t = grammar.createNonTerminalNode("T");
		NonTerminalNode q = grammar.createNonTerminalNode("Q");
		and.appendChild(t);
		and.appendChild(q);

		// add to production
		production.appendChild(and);

		return production;
	}
	
	private static ProductionNode createQProductions(GrammarNode grammar) {
		ProductionNode production = grammar.createProductionNode("Q");
		
		// add +TQ
		SequenceNode and1 = grammar.createSequenceNode();
		TerminalNode plus = grammar.createTerminalNode("'+'");
		NonTerminalNode t1 = grammar.createNonTerminalNode("T");
		NonTerminalNode q1 = grammar.createNonTerminalNode("Q");
		and1.appendChild(plus);
		and1.appendChild(t1);
		and1.appendChild(q1);
		production.appendChild(and1);

		// add -TQ
		SequenceNode and2 = grammar.createSequenceNode();
		TerminalNode minus = grammar.createTerminalNode("'-'");
		NonTerminalNode t2 = grammar.createNonTerminalNode("T");
		NonTerminalNode q2 = grammar.createNonTerminalNode("Q");
		and2.appendChild(minus);
		and2.appendChild(t2);
		and2.appendChild(q2);
		production.appendChild(and2);
		
		// add Factor
		EmptyNode e = grammar.createEmptyNode();
		production.appendChild(e);

		return production;
	}
	
	private static ProductionNode createTProductions(GrammarNode grammar) {
		// make production
		ProductionNode production = grammar.createProductionNode("T");

		// add Expression '+' Term
		SequenceNode and = grammar.createSequenceNode();
		NonTerminalNode f = grammar.createNonTerminalNode("F");
		NonTerminalNode r = grammar.createNonTerminalNode("R");
		and.appendChild(f);
		and.appendChild(r);

		// add to production
		production.appendChild(and);

		return production;
	}
	
	private static ProductionNode createRProductions(GrammarNode grammar) {
		ProductionNode production = grammar.createProductionNode("R");
		
		// add *FR
		SequenceNode and1 = grammar.createSequenceNode();
		TerminalNode times = grammar.createTerminalNode("'*'");
		NonTerminalNode f1 = grammar.createNonTerminalNode("F");
		NonTerminalNode r1 = grammar.createNonTerminalNode("R");
		and1.appendChild(times);
		and1.appendChild(f1);
		and1.appendChild(r1);
		production.appendChild(and1);

		// add /FR
		SequenceNode and2 = grammar.createSequenceNode();
		TerminalNode divide = grammar.createTerminalNode("'/'");
		NonTerminalNode f2 = grammar.createNonTerminalNode("F");
		NonTerminalNode r2 = grammar.createNonTerminalNode("R");
		and2.appendChild(divide);
		and2.appendChild(f2);
		and2.appendChild(r2);
		production.appendChild(and2);
		
		// add Factor
		EmptyNode e = grammar.createEmptyNode();
		production.appendChild(e);

		return production;
	}
	
	private static ProductionNode createFProductions(GrammarNode grammar) {
		ProductionNode production = grammar.createProductionNode("F");
		
		// add (E)
		SequenceNode and1 = grammar.createSequenceNode();
		TerminalNode lparen = grammar.createTerminalNode("'('");
		NonTerminalNode e = grammar.createNonTerminalNode("E");
		TerminalNode rparen = grammar.createTerminalNode("')'");
		and1.appendChild(lparen);
		and1.appendChild(e);
		and1.appendChild(rparen);
		production.appendChild(and1);
		
		// add i
		TerminalNode i = grammar.createTerminalNode("IDENTIFIER");
		production.appendChild(i);

		return production;
	}
}
