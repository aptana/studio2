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
package com.aptana.ide.regex.nfa;

import com.aptana.ide.regex.IRegexRunner;
import com.aptana.ide.regex.dfa.DFAGraph;
import com.aptana.ide.regex.dfa.DFANode;
import com.aptana.ide.regex.sets.CharacterSet;
import com.aptana.ide.regex.sets.NumberSet;

/**
 * @author Kevin Lindsey
 */
public class NFAConverter
{
	/*
	 * Fields
	 */
	NFAGraph _nfa;
	DFAGraph _dfa;
	CharacterSet _transitionSet;

	/*
	 * Methods
	 */

	/**
	 * Convert the specified NFA into an equivalent DFA
	 * 
	 * @param nfaGraph
	 *            The NFA Graph to convert
	 * @return Returns a DFA that expresses the same regex pattern as the specified NFA
	 */
	public IRegexRunner toDFA(NFAGraph nfaGraph)
	{
		this._dfa = new DFAGraph();
		this._nfa = nfaGraph;

		this._transitionSet = nfaGraph.getInputSet();
		this._dfa._transitionSet = this._transitionSet; // temp
		this._dfa.add(this.epsilonReachable(nfaGraph.getStart()));

		for (int index = 0; index < this._dfa.getSize(); index++)
		{
			DFANode node = (DFANode) this._dfa.getItem(index);

			this.processTransitions(node);
		}

		return this._dfa;
	}

	/**
	 * Find all epsilon nodes that can be reached from the specified node
	 * 
	 * @param startIndex
	 *            The node from which to find epsilons
	 * @return A NumberSet of all nodes that are reachable from the specified node
	 */
	private NumberSet epsilonReachable(int startIndex)
	{
		NumberSet result = new NumberSet();

		// add the starting point
		result.addMember(startIndex);

		// add epsilons for each member of the resulting set
		for (int i = 0; i < result.getSize(); i++)
		{
			int moveIndex = result.getItem(i);
			NFANode node = this._nfa.getItem(moveIndex);

			result.addMembers(node.getEpsilons());
		}

		return result;
	}

	/**
	 * Find the name index for the specified NumberSet
	 * 
	 * @param numberSet
	 *            The NumberSet to locate
	 * @return The index of the DFA Node that matches the NumberSet
	 */
	private int nameIndex(NumberSet numberSet)
	{
		int result = -1;
		String testSet = numberSet.toString();

		for (int i = 0; i < this._dfa.getSize(); i++)
		{
			DFANode move = (DFANode) this._dfa.getItem(i);
			String testString = move.getTransitionSet().toString();

			if (testSet.equals(testString))
			{
				result = i;
				break;
			}
		}

		return result;
	}

	/**
	 * Process all transitions for the given DFA node
	 * 
	 * @param state
	 *            The DFA to process
	 */
	private void processTransitions(DFANode state)
	{
		char[] allTransitions = this._transitionSet.getMembers();
		int[] nfaStateIndexes = state.getTransitionSet().getMembers();

		for (int i = 0; i < allTransitions.length; i++)
		{
			NumberSet reachableStates = new NumberSet();
			char inputChar = allTransitions[i];

			for (int j = 0; j < nfaStateIndexes.length; j++)
			{
				int nfaStateIndex = nfaStateIndexes[j];
				NumberSet reachable = this.transitionReachable(nfaStateIndex, inputChar);

				reachableStates.union(reachable);

				int acceptState = this._nfa.getAcceptState(nfaStateIndex);

				if (acceptState != -1)
				{
					state.setAcceptState(acceptState);
				}
			}

			if (reachableStates.getSize() != 0)
			{
				int match = this.nameIndex(reachableStates);

				if (match == -1)
				{
					match = this._dfa.add(reachableStates);
				}

				state.setItem(i, match);
			}
			else
			{
				state.setItem(i, -1);
			}
		}
	}

	/**
	 * Find all nodes that are reachable from the given NFA Node for the specified input character
	 * 
	 * @param moveIndex
	 *            The NFA node to test
	 * @param inputChar
	 *            The input character to test
	 * @return The set of all NFA nodes that are reachable from the given NFA Node for the specified input character
	 */
	private NumberSet transitionReachable(int moveIndex, char inputChar)
	{
		NFANode move = this._nfa.getItem(moveIndex);
		NumberSet result;

		if (move.getInput().hasInput(inputChar))
		{
			int nextIndex = move.getNext();

			result = this.epsilonReachable(nextIndex);
		}
		else
		{
			result = new NumberSet();
		}

		return result;
	}
}
