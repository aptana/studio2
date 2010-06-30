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

import com.aptana.ide.parsing.bnf.nodes.GrammarNode;
import com.aptana.ide.parsing.bnf.nodes.NonTerminalNode;
import com.aptana.ide.parsing.bnf.nodes.ProductionNode;
import com.aptana.ide.parsing.bnf.nodes.SequenceNode;
import com.aptana.ide.parsing.bnf.nodes.TerminalNode;

/**
 * @author Kevin Lindsey
 */
public final class SampleGrammar
{
	/**
	 * SampleGrammar
	 */
	private SampleGrammar()
	{
	}

	/**
	 * getGrammar
	 * 
	 * @return GrammarNode
	 */
	public static GrammarNode getGrammar()
	{
		// create grammar
		GrammarNode grammar = new GrammarNode("text/expression");

		ProductionNode state0 = grammar.createProductionNode("Z");
		state0.appendChild(grammar.createNonTerminalNode("E"));
		grammar.appendChild(state0);

		grammar.appendChild(createExpressionProductions(grammar));
		grammar.appendChild(createTermProductions(grammar));
		grammar.appendChild(createFactorProductions(grammar));

		return grammar;
	}

	private static ProductionNode createExpressionProductions(GrammarNode grammar)
	{
		// make production
		ProductionNode production = grammar.createProductionNode("E");

		// add Expression '+' Term
		NonTerminalNode e1 = grammar.createNonTerminalNode("E");
		TerminalNode plus = grammar.createTerminalNode("+");
		NonTerminalNode t1 = grammar.createNonTerminalNode("T");
		SequenceNode def1 = grammar.createSequenceNode();
		def1.appendChild(e1);
		def1.appendChild(plus);
		def1.appendChild(t1);
		production.appendChild(def1);

		// add Expression '-' Term
		NonTerminalNode e2 = grammar.createNonTerminalNode("E");
		TerminalNode minus = grammar.createTerminalNode("-");
		NonTerminalNode t2 = grammar.createNonTerminalNode("T");
		SequenceNode def2 = grammar.createSequenceNode();
		def2.appendChild(e2);
		def2.appendChild(minus);
		def2.appendChild(t2);
		production.appendChild(def2);

		// add Term
		NonTerminalNode t3 = grammar.createNonTerminalNode("T");
		production.appendChild(t3);

		return production;
	}

	private static ProductionNode createTermProductions(GrammarNode grammar)
	{
		ProductionNode production = grammar.createProductionNode("T");

		// add Term '*' Factor
		NonTerminalNode t1 = grammar.createNonTerminalNode("T");
		TerminalNode times = grammar.createTerminalNode("*");
		NonTerminalNode f1 = grammar.createNonTerminalNode("F");
		SequenceNode def1 = grammar.createSequenceNode();
		def1.appendChild(t1);
		def1.appendChild(times);
		def1.appendChild(f1);
		production.appendChild(def1);

		// add Term '/' Factor
		NonTerminalNode t2 = grammar.createNonTerminalNode("T");
		TerminalNode divide = grammar.createTerminalNode("/");
		NonTerminalNode f2 = grammar.createNonTerminalNode("F");
		SequenceNode def2 = grammar.createSequenceNode();
		def2.appendChild(t2);
		def2.appendChild(divide);
		def2.appendChild(f2);
		production.appendChild(def2);

		// add Factor
		NonTerminalNode f3 = grammar.createNonTerminalNode("F");
		production.appendChild(f3);

		return production;
	}

	private static ProductionNode createFactorProductions(GrammarNode grammar)
	{
		ProductionNode production = grammar.createProductionNode("F");

		// add '(' Expression ')'
		TerminalNode lparen = grammar.createTerminalNode("(");
		NonTerminalNode e = grammar.createNonTerminalNode("E");
		TerminalNode rparen = grammar.createTerminalNode(")");
		SequenceNode def1 = grammar.createSequenceNode();
		def1.appendChild(lparen);
		def1.appendChild(e);
		def1.appendChild(rparen);
		production.appendChild(def1);

		// add id
		TerminalNode id = grammar.createTerminalNode("i");
		production.appendChild(id);

		return production;
	}
}
