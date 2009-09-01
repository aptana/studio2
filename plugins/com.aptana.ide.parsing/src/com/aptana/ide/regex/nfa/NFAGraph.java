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

import java.util.Stack;

import com.aptana.ide.regex.Closure;
import com.aptana.ide.regex.inputs.Input;
import com.aptana.ide.regex.sets.CharacterSet;

/**
 * @author Kevin Lindsey
 */
public class NFAGraph
{
	static Stack<Integer> recycle = new Stack<Integer>();
	static NFANode[] nodes = new NFANode[0];

	int _start;
	int _end;

	/**
	 * Return the AcceptState for the given node index
	 * 
	 * @param nodeIndex
	 *            The node index we wish to retrieve
	 * @return The AcceptState for the given node index
	 */
	public int getAcceptState(int nodeIndex)
	{
		return this.getItem(nodeIndex).getAcceptState();
	}

	/**
	 * Returns the NFA node at the specified index
	 * 
	 * @param index
	 *            The index of the node to retrieve
	 * @return The NFA node at the specified index
	 */
	public NFANode getItem(int index)
	{
		return nodes[index];
	}

	/**
	 * Returns the index of the node that is the exit point for this NFA graph
	 * 
	 * @return The NFA graph's exit node index
	 */
	public int getEnd()
	{
		return _end;
	}

	/**
	 * Returns the set of all input characters referenced by this NFA graph
	 * 
	 * @return A CharacterSet of characters used by this NFA graph
	 */
	public CharacterSet getInputSet()
	{
		CharacterSet result = new CharacterSet();

		for (int i = 0; i < nodes.length; i++)
		{
			Input input = this.getItem(i).getInput();

			if (input != null)
			{
				result.addMembers(input.getCharacters());
			}
		}

		return result;
	}

	/**
	 * Returns the index of the node that is the entry point for this NFA graph
	 * 
	 * @return The NFA graph's entry node index
	 */
	public int getStart()
	{
		return _start;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of NFAGraph
	 * 
	 * @param acceptState
	 *            The accept state associated with this node
	 */
	public NFAGraph(int acceptState)
	{
		// create a new node and set the starting and ending points of this NFA
		// graph to point to that node
		this._start = this.createNewState();
		this._end = this._start;

		// set the initial node state to the specified AcceptState
		this.getItem(this._end).setAcceptState(acceptState);
	}

	/*
	 * Methods
	 */

	/**
	 * Add a new node to the end of this NFA graph
	 * 
	 * @param input
	 *            The set of valid transitions to add to this node
	 */
	public void add(Input input)
	{
		// get current end node to copy its properties later
		NFANode previousEnd = this.getItem(this._end);

		// create a new end node
		this._end = this.createNewState();

		// move accept state to new end node
		this.getItem(this._end).setAcceptState(previousEnd.getAcceptState());

		// set inputs that fire a transition and set new state
		previousEnd.setInput(input);
		previousEnd.setNext(this._end);

		// clear old end's accept state
		previousEnd.setAcceptState(-1);
	}

	/**
	 * Join this NFA graph with the specified graph using an And operation
	 * 
	 * @param rhs
	 *            The NFA graph to And to this graph
	 */
	public void andMachines(NFAGraph rhs)
	{
		this.getItem(this._end).copy(rhs.getItem(rhs._start));
		rhs.recycleNode(rhs._start);
		rhs._start = this._end;
		this._end = rhs._end;
	}

	/**
	 * Add the specified closure type to this NFA graph
	 * 
	 * @param type
	 */
	private void applyClosure(int type)
	{
		int front = this.createNewState();
		int back = this.createNewState();

		this.getItem(front).addEpsilon(this._start);

		if (type == Closure.KLEENE || type == Closure.OPTION)
		{
			this.getItem(front).addEpsilon(back);
		}
		if (type == Closure.KLEENE || type == Closure.POSITIVE)
		{
			this.getItem(this._end).addEpsilon(this._start);
		}

		// copy previous last state's AcceptState to the new last state
		this.getItem(back).setAcceptState(this.getItem(this._end).getAcceptState());

		// connect previous last state to new last state
		this.getItem(this._end).addEpsilon(back);

		// clear previous last state's AcceptState
		this.getItem(this._end).setAcceptState(-1);

		// update pointers to the beginning and end of this machine
		this._start = front;
		this._end = back;
	}

	/**
	 * Create a new node.
	 * 
	 * @return The index to the newly created node
	 */
	public int createNewState()
	{
		int result;

		if (recycle.size() > 0)
		{
			result = recycle.pop().intValue();
		}
		else
		{
			result = nodes.length;

			// add new node
			NFANode[] newNodes = new NFANode[result + 1];
			System.arraycopy(nodes, 0, newNodes, 0, result);
			newNodes[result] = new NFANode();

			// assign new array
			nodes = newNodes;
		}

		return result;
	}

	/**
	 * Create a Kleene closure around this NFA graph
	 */
	public void kleeneClosure()
	{
		this.applyClosure(Closure.KLEENE);
	}

	/**
	 * Create a Option around this NFA graph
	 */
	public void option()
	{
		this.applyClosure(Closure.OPTION);
	}

	/**
	 * Join this NFA graph with the specified graph using an Or operation
	 * 
	 * @param rhs
	 *            The NFA graph to Or to this graph
	 */
	public void orMachines(NFAGraph rhs)
	{
		int front = this.createNewState();
		int back = this.createNewState();

		// connect front to the starts of each machine
		this.getItem(front).addEpsilon(this._start);
		this.getItem(front).addEpsilon(rhs._start);

		// // make sure both machines are for the same AcceptState
		// if (this.getAcceptState(this._end) != rhs.getAcceptState(rhs._end))
		// {
		// // String msg = "Cannot 'or' two machines with differing accept
		// // states";
		// // throw new Exception(msg);
		// }

		// copy AcceptState from previous last state
		this.getItem(back).setAcceptState(this.getItem(this._end).getAcceptState());

		// connect ends of each machine to new last state
		this.getItem(this._end).addEpsilon(back);
		rhs.getItem(rhs._end).addEpsilon(back);

		// clear previous AcceptStates
		this.getItem(this._end).setAcceptState(-1);
		rhs.getItem(rhs._end).setAcceptState(-1);

		// update pointers to the start and end of this machine
		this._start = front;
		this._end = back;
	}

	/**
	 * Create a Positive closure around this NFA graph
	 */
	public void positiveClosure()
	{
		this.applyClosure(Closure.POSITIVE);
	}

	/**
	 * Add the specified node into the recycle bin
	 * 
	 * @param index
	 *            The index of the node to recycle
	 */
	public void recycleNode(int index)
	{
		this.getItem(index).reset();
		recycle.push(new Integer(index));
	}

	/**
	 * Globally reset the NFA machine
	 */
	public static void reset()
	{
		nodes = new NFANode[0];
		recycle.clear();
	}

	/**
	 * Return a string representation of this NFA graph
	 * 
	 * @return Returns a string representation of this NFA Graph
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("NFA\n===\n"); //$NON-NLS-1$

		for (int i = 0; i < nodes.length; i++)
		{
			if (i == this._start && i == this._end)
			{
				sb.append("<->").append(i).append(" : "); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if (i == this._start)
			{
				sb.append(" ->").append(i).append(" : "); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if (i == this._end)
			{
				sb.append("<- ").append(i).append(" : "); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				sb.append("   ").append(i).append(" : "); //$NON-NLS-1$ //$NON-NLS-2$
			}

			// output state
			sb.append(this.getItem(i));

			// epsilon reachable
			sb.append("\n"); //$NON-NLS-1$
		}

		// show the input characters
		sb.append("\nInputs\n======\n").append(this.getInputSet()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$

		return sb.toString();
	}
}
