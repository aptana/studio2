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

import com.aptana.ide.regex.inputs.Input;

/**
 * @author Kevin Lindsey
 */
public class NFANode
{
	/*
	 * Fields
	 */
	Input _input;
	int _acceptState;
	int _next;
	int[] _epsilon;

	/*
	 * Properties
	 */

	/**
	 * get AcceptState
	 * 
	 * @return Returns the accept state associated with this node
	 */
	public int getAcceptState()
	{
		return this._acceptState;
	}

	/**
	 * set AcceptState
	 * 
	 * @param state
	 *            New accept state
	 */
	public void setAcceptState(int state)
	{
		this._acceptState = state;
	}

	/**
	 * get Epsilons
	 * 
	 * @return epsilons
	 */
	public int[] getEpsilons()
	{
		return this._epsilon;
	}

	/**
	 * get Input
	 * 
	 * @return input
	 */
	public Input getInput()
	{
		return this._input;
	}

	/**
	 * set Input
	 * 
	 * @param input
	 *            The input for this node
	 */
	public void setInput(Input input)
	{
		this._input = input;
	}

	/**
	 * get Next state
	 * 
	 * @return The next state
	 */
	public int getNext()
	{
		return this._next;
	}

	/**
	 * set Next state
	 * 
	 * @param next
	 *            The next state
	 */
	public void setNext(int next)
	{
		this._next = next;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of NFANode
	 */
	public NFANode()
	{
		this.reset();
	}

	/*
	 * Methods
	 */

	/**
	 * Add a new epsilon to this node's epsilon list
	 * 
	 * @param next
	 *            The index of the an epsilon transition leaving this node
	 */
	public void addEpsilon(int next)
	{
		int length = this._epsilon.length;
		int[] epsilons = new int[length + 1];

		System.arraycopy(this._epsilon, 0, epsilons, 0, length);
		epsilons[length] = next;

		this._epsilon = epsilons;
	}

	/**
	 * Copy the specified node's values to this node
	 * 
	 * @param node
	 *            The node from which to copy
	 */
	public void copy(NFANode node)
	{
		this._input = node._input;
		this._acceptState = node._acceptState;
		this._next = node._next;
		this._epsilon = node._epsilon;
	}

	/**
	 * Determine if a character is valid transition from this node
	 * 
	 * @param input
	 *            The character to test
	 * @return Returns true if this node contains a transition for the given character
	 */
	public boolean hasInputChar(char input)
	{
		boolean result = false;

		if (this._input != null)
		{
			result = this._input.hasInput(input);
		}

		return result;
	}

	/**
	 * Reset this NFA Node's state
	 */
	public void reset()
	{
		this._input = new Input();
		this._acceptState = -1;
		this._next = -1;
		this._epsilon = new int[0];
	}

	/**
	 * Return a string representation of this node
	 * 
	 * @return Returns a string representation of this node
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("next("); //$NON-NLS-1$

		if (this._input != null)
		{
			sb.append(this._input.toString());
		}
		else
		{
			sb.append("-"); //$NON-NLS-1$
		}

		sb.append(" -> "); //$NON-NLS-1$

		if (this._input != null && this._next != -1)
		{
			sb.append(this._next);
		}
		else
		{
			sb.append("-"); //$NON-NLS-1$
		}

		sb.append(")"); //$NON-NLS-1$

		if (this._acceptState != -1)
		{
			sb.append("+ "); //$NON-NLS-1$
		}
		else
		{
			sb.append("  "); //$NON-NLS-1$
		}

		// append epsilons
		sb.append("["); //$NON-NLS-1$

		if (this._epsilon.length > 0)
		{
			StringBuffer sb2 = new StringBuffer();
			sb2.append(this._epsilon[0]);

			for (int i = 1; i < this._epsilon.length; i++)
			{
				sb2.append(",").append(this._epsilon[i]); //$NON-NLS-1$
			}

			sb.append(sb2.toString());
		}

		sb.append("]"); //$NON-NLS-1$

		return sb.toString();
	}
}
