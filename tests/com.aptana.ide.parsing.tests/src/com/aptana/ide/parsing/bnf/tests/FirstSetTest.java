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

import com.aptana.ide.parsing.bnf.TerminalList;
import com.aptana.ide.parsing.bnf.nodes.GrammarNode;
import com.aptana.ide.parsing.bnf.nodes.ProductionNode;

/**
 * @author Kevin Lindsey
 *
 */
public class FirstSetTest {
	/**
	 * main
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		showFirstSets(FactoredGrammar.getGrammar());
		showFirstSets(SampleGrammar.getGrammar());
	}
	
	private static void showFirstSets(GrammarNode grammar) {
		GrammarNode expandedGrammar = grammar.getExpandedGrammar();
		
		System.out.println("First Sets");
		System.out.println("==========");
		showFirst(grammar);
		System.out.println();
		
		System.out.println("Expanded First Sets");
		System.out.println("===================");
		showFirst(expandedGrammar);
		System.out.println();
	}
	
	private static void showFirst(GrammarNode grammar) {
		String[] productionNames = grammar.getProductionNames();
		
		for (int i = 0; i < productionNames.length; i++) {
			String name = productionNames[i];
			ProductionNode[] productions = grammar.getProductionsByName(name);
			
			for (int j = 0; j < productions.length; j++) {
				TerminalList terminals = new TerminalList();
				
				productions[j].getFirst(terminals);
				
				System.out.println("FIRST(" + name + ") = " + terminals);
			}
			
//			TerminalList terminals = grammar.getFirst(name);
//			System.out.println("FIRST(" + name + ") = " + terminals);
		}
	}
}
