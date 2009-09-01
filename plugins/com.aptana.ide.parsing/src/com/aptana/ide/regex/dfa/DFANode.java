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
package com.aptana.ide.regex.dfa;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.aptana.ide.regex.IRegexRunnerNode;
import com.aptana.ide.regex.sets.NumberSet;

/**
 * @author Kevin Lindsey
 */
public class DFANode implements IRegexRunnerNode
{
	/*
	 * Fields
	 */
	private NumberSet _transitionSet;
	private int[] _allTransitions;
	private int _acceptState;

	// temp
	private int _group;

	/*
	 * Properties
	 */

	/**
	 * Set the AcceptState for this node
	 * 
	 * @param state
	 *            The new AcceptState for this node
	 */
	public void setAcceptState(int state)
	{
		this._acceptState = state;
	}

	/**
	 * Get the group associated with this node
	 * 
	 * @return The group associated with this node
	 */
	public int getGroup()
	{
		return this._group;
	}

	/**
	 * Set the group associated with this node
	 * 
	 * @param group
	 *            The new group with which to associate this node
	 */
	public void setGroup(int group)
	{
		this._group = group;
	}

	/**
	 * Set the transition for the specified index
	 * 
	 * @param index
	 *            The transition index
	 * @param value
	 *            The index of the node to which the index will transition
	 */
	public void setItem(int index, int value)
	{
		this._allTransitions[index] = value;
	}

	/**
	 * Get the set of transitions associated with this node
	 * 
	 * @return Returns the transition set associate with this node
	 */
	public NumberSet getTransitionSet()
	{
		return this._transitionSet;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of DFANode
	 */
	DFANode()
	{
		this._transitionSet = null;
		this._allTransitions = new int[0];
		this._acceptState = -1;
	}

	/**
	 * Create a new instance of DFANode
	 * 
	 * @param inputs
	 *            The inputs that cause transitions from this node
	 */
	public DFANode(NumberSet inputs)
	{
		this._transitionSet = inputs;
		this._allTransitions = new int[256];
		this._acceptState = -1;

		for (int i = 0; i < 256; i++)
		{
			this._allTransitions[i] = -1;
		}
	}

	/*
	 * Methods
	 */

	/**
	 * Determine if this node and the specified node are in the same partition
	 * 
	 * @param parent
	 *            The parent graph for this node and the test node
	 * @param testState
	 *            The node to test against this node
	 * @return Returns true if this node and the specified node are in the same partition
	 */
	public boolean inSamePartition(DFAGraph parent, DFANode testState)
	{
		int transitionSize = parent._transitionSet.getSize();
		boolean result = this.getAcceptState() == testState.getAcceptState();

		if (result)
		{
			// compare all transitions in both the master and the test node
			for (int j = 0; j < transitionSize; j++)
			{
				// get the master's new state for this input
				int stateNext = this.getItem(j);

				// get the test state's new state for the same input
				int testStateNext = testState.getItem(j);

				// if they're the same, the we're done
				if (stateNext != testStateNext)
				{
					// still not convinced these states are considered different

					// determine in which group the master's next state lives
					int stateNextGroup = (stateNext == -1) ? -1 : ((DFANode) parent.getItem(stateNext)).getGroup();

					// determine in which group the test state's next state
					// lives
					int testStateNextGroup = (testStateNext == -1) ? -1 : ((DFANode) parent.getItem(testStateNext))
							.getGroup();

					// if the groups are the same, then we're done
					if (stateNextGroup != testStateNextGroup)
					{
						result = false;
						break;
					}
				}
			}
		}

		return result;
	}

	/**
	 * Return a string representation of this node
	 * 
	 * @return Returns a string representation of this node
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < this._allTransitions.length; i++)
		{
			int value = this._allTransitions[i];

			if (value == -1)
			{
				sb.append("-"); //$NON-NLS-1$
			}
			else
			{
				sb.append(value);
			}

			sb.append(" "); //$NON-NLS-1$
		}

		sb.append(" : ").append(this.getAcceptState()); //$NON-NLS-1$

		return sb.toString();
	}

	/*
	 * IRegexRunnerNode implementation
	 */

	/**
	 * @see com.aptana.ide.regex.IRegexRunnerNode#getAcceptState()
	 */
	public int getAcceptState()
	{
		return this._acceptState;
	}

	/**
	 * @see com.aptana.ide.regex.IRegexRunnerNode#getItem(int)
	 */
	public int getItem(int index)
	{
		return this._allTransitions[index];
	}

	/**
	 * @see com.aptana.ide.regex.IRegexRunnerNode#read(java.io.DataInput)
	 */
	public void read(DataInput input) throws IOException
	{
		// get transition count (should always be 256)
		int transitionCount = input.readInt();

		// create transition array
		this._allTransitions = new int[transitionCount];

		// read all transition values
		for (int i = 0; i < transitionCount; i++)
		{
			this._allTransitions[i] = input.readInt();
		}

		// read accept state
		this._acceptState = input.readInt();
	}

	/**
	 * @see com.aptana.ide.regex.IRegexRunnerNode#write(java.io.DataOutput)
	 */
	public void write(DataOutput output) throws IOException
	{
		int transitionCount = this._allTransitions.length;

		// write transition count (should always be 256)
		output.writeInt(transitionCount);

		// write transition values
		for (int i = 0; i < transitionCount; i++)
		{
			output.writeInt(this._allTransitions[i]);
		}

		// write accept state
		output.writeInt(this._acceptState);
	}

//	/**
//	 * toAssembly
//	 * 
//	 * @param writer
//	 * @param stateLabelBase
//	 * @param noMatchLabel
//	 */
//	public void toAssembly(SourceWriter writer, String stateLabelBase, String noMatchLabel)
//	{
//		boolean hasOutboundTransition = false;
//		
//		for (int i = 0; i < this._allTransitions.length; i++)
//		{
//			if (this._allTransitions[i] != -1)
//			{
//				hasOutboundTransition = true;
//				break;
//			}
//		}
//		
//		if (hasOutboundTransition)
//		{
//			// todo: emit current character
//			writer.printlnWithIndent("ldc \"a\"");
//			
//			writer.printlnWithIndent("lookupswitch");
//			writer.increaseIndent();
//	
//			for (int i = 0; i < this._allTransitions.length; i++)
//			{
//				int target = this._allTransitions[i];
//	
//				if (target != -1)
//				{
//					writer.printlnWithIndent(i + ": " + stateLabelBase + target);
//				}
//			}
//	
//			writer.printlnWithIndent("default: " + noMatchLabel);
//			writer.decreaseIndent();
//		}
//		else
//		{
//			writer.printlnWithIndent("goto_w " + noMatchLabel);
//		}
//		
//		writer.println();
//	}
}
