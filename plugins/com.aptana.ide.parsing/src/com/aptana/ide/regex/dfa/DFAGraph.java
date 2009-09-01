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

import com.aptana.ide.regex.IRegexRunner;
import com.aptana.ide.regex.IRegexRunnerNode;
import com.aptana.ide.regex.sets.CharacterSet;
import com.aptana.ide.regex.sets.NumberSet;

/**
 * @author Kevin Lindsey
 */
public class DFAGraph implements IRegexRunner
{
	/*
	 * Fields
	 */
	private IRegexRunnerNode[] _nodes;
	private int _acceptState;

	/**
	 * temp
	 */
	public CharacterSet _transitionSet;

	/*
	 * Properties
	 */

	/**
	 * Get the node associated with the given index
	 * 
	 * @param index
	 *            The index of the node to return
	 * @return The node at the specified index
	 */
	public IRegexRunnerNode getItem(int index)
	{
		return this._nodes[index];
	}

	/**
	 * Returns the number of nodes in this graph
	 * 
	 * @return The number of nodes in this graph
	 */
	public int getSize()
	{
		return this._nodes.length;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance DFAGraph
	 */
	public DFAGraph()
	{
		this._transitionSet = null;
		this._nodes = new DFANode[0];
	}

	/*
	 * Methods
	 */

	/**
	 * Add a node to this graph
	 * 
	 * @param node
	 *            The node to add to this graph
	 * @return The index of the node in this graph
	 */
	public int add(DFANode node)
	{
		int length = this._nodes.length;
		DFANode[] newNodes = new DFANode[length + 1];

		// copy array
		System.arraycopy(this._nodes, 0, newNodes, 0, length);

		// add node
		newNodes[length] = node;

		// assign new array
		this._nodes = newNodes;

		return length;
	}

	/**
	 * Create a new node based on the given set and add it to this graph
	 * 
	 * @param inputs
	 *            The set to associate with the new node
	 * @return The index of the new node in this graph
	 */
	public int add(NumberSet inputs)
	{
		return this.add(new DFANode(inputs));
	}

	// /**
	// * Try to match this DFA against the specified string
	// *
	// * @param source
	// * The string to run this DFA on
	// * @return The accept state value. This will be -1 if there was no match
	// */
	// public int match(String source)
	// {
	// return this.match(source.toCharArray(), 0);
	// }
	//
	// /**
	// * Try to match this DFA against the specified string
	// *
	// * @param source
	// * The character array to run this DFA on
	// * @return The accept state value. This will be -1 if there was no match
	// */
	// public int match(char[] source)
	// {
	// return this.match(source, 0, source.length);
	// }
	//
	// /**
	// * Try to match this DFA against the specified string starting at the given index
	// *
	// * @param source
	// * @param startPosition
	// * @return The accept state value. This will be -1 if there was no match
	// */
	// public int match(char[] source, int startPosition)
	// {
	// return this.match(source, startPosition, source.length);
	// }

	/*
	 * IRegexRunner implementation
	 */

	/**
	 * @see com.aptana.ide.regex.IRegexRunner#getAcceptState()
	 */
	public int getAcceptState()
	{
		return this._acceptState;
	}

	/**
	 * @see com.aptana.ide.regex.IRegexRunner#match(java.lang.String, int, int)
	 */
	public int match(String source, int startPosition, int endPosition)
	{
		return this.match(source.toCharArray(), startPosition, endPosition);
	}

	/**
	 * @see com.aptana.ide.regex.IRegexRunner#match(char[], int, int)
	 */
	public int match(char[] source, int startPosition, int endPosition)
	{
		int currentState = 0;

		this._acceptState = this._nodes[0].getAcceptState();

		int lastAccept = (this._acceptState == -1) ? -2 : startPosition;

		for (int i = startPosition; i < endPosition; i++)
		{
			int index = this._transitionSet.inputIndex(source[i]);

			if (index == -1)
			{
				// this character does not exist as an input
				break;
			}
			else
			{
				IRegexRunnerNode move = this._nodes[currentState];

				if (move != null)
				{
					int nextState = move.getItem(index);

					if (nextState != -1)
					{
						// advance to next sate
						currentState = nextState;

						int acceptState = this._nodes[currentState].getAcceptState();

						// remember this position, if this is an accept state
						if (acceptState != -1)
						{
							lastAccept = i;
							this._acceptState = acceptState;
						}
					}
					else
					{
						// no transition in this state for this input
						break;
					}
				}
				else
				{
					// throw new Exception("internal inconsistency");
				}
			}
		}

		return lastAccept + 1;
	}

	/**
	 * read
	 * 
	 * @param input
	 * @throws IOException
	 */
	public void read(DataInput input) throws IOException
	{
		// read transition set
		this._transitionSet = new CharacterSet();
		this._transitionSet.setMembers(input.readUTF());

		// read node count
		int nodeCount = input.readInt();

		// read nodes
		this._nodes = new IRegexRunnerNode[nodeCount];

		for (int i = 0; i < nodeCount; i++)
		{
			IRegexRunnerNode node = new DFANode();

			node.read(input);
			this._nodes[i] = node;
		}
	}

	/**
	 * write
	 * 
	 * @param output
	 * @throws IOException
	 */
	public void write(DataOutput output) throws IOException
	{
		// write transition set
		String members = new String(this._transitionSet.getMembers());
		output.writeUTF(members);

		// write node count
		int nodeCount = this._nodes.length;
		output.writeInt(nodeCount);

		// write nodes
		for (int i = 0; i < nodeCount; i++)
		{
			this._nodes[i].write(output);
		}
	}

	// /**
	// * toAssembly
	// *
	// * @param className
	// * @return String
	// */
	// public String toAssembly(String className)
	// {
	// SourceWriter writer = new SourceWriter();
	//
	// // declare class
	// writer.printlnWithIndent(".class public " + className);
	// writer.printlnWithIndent(".super java/lang/Object");
	// writer.printlnWithIndent(".implements com/aptana/ide/regex/IRegexRunner");
	// writer.println();
	//
	// // define fields
	// writer.printlnWithIndent(".field private _acceptState I");
	// writer.println();
	//		
	// // define constructor
	// writer.printlnWithIndent(".method public <init>()V");
	// writer.increaseIndent();
	// writer.printlnWithIndent("aload_0");
	// writer.printlnWithIndent("invokenonvirtual java/lang/Object/<init>()V");
	// writer.printlnWithIndent("return");
	// writer.decreaseIndent();
	// writer.printlnWithIndent(".end method");
	// writer.println();
	//		
	// // define getAcceptState
	// writer.printlnWithIndent(".method public getAcceptState()I");
	// writer.increaseIndent();
	// writer.printlnWithIndent("aload_0");
	// writer.printlnWithIndent("getfield " + className + "/_acceptState I");
	// writer.printlnWithIndent("return");
	// writer.decreaseIndent();
	// writer.printlnWithIndent(".end method");
	// writer.println();
	//		
	// // define setGroup
	// writer.printlnWithIndent(".method public setGroup(Ljava/lang/String;)V");
	// writer.increaseIndent();
	// writer.printlnWithIndent("return");
	// writer.printlnWithIndent(".limit stack 1");
	// writer.printlnWithIndent(".limit locals 2");
	// writer.decreaseIndent();
	// writer.printlnWithIndent(".end method");
	// writer.println();
	//		
	// // output match(char[], int, int)I
	// writer.printlnWithIndent(".method public match([CII)I");
	// writer.increaseIndent();
	// writer.printlnWithIndent(".limit locals 6");
	//		
	// for (int i = 0; i < this._nodes.length; i++)
	// {
	// DFANode node = (DFANode) this._nodes[i];
	//			
	// writer.printlnWithIndent("Label" + i + ":");
	// node.toAssembly(writer, "Label", "ReturnNoMatch");
	// }
	//
	// writer.printlnWithIndent("ReturnNoMatch:");
	// writer.increaseIndent();
	// writer.printlnWithIndent("bipush -1");
	// writer.printlnWithIndent("return");
	// writer.decreaseIndent();
	// writer.decreaseIndent();
	// writer.printlnWithIndent(".end method");
	// writer.println();
	//		
	// // define read
	// writer.printlnWithIndent(".method public read(Ljava/io/DataInput;)V");
	// writer.increaseIndent();
	// writer.printlnWithIndent(".limit stack 2");
	// writer.printlnWithIndent(".limit locals 1");
	// writer.printlnWithIndent("return");
	// writer.decreaseIndent();
	// writer.printlnWithIndent(".end method");
	// writer.println();
	//		
	// // define write
	// writer.printlnWithIndent(".method public write(Ljava/io/DataOutput;)V");
	// writer.increaseIndent();
	// writer.printlnWithIndent(".limit stack 2");
	// writer.printlnWithIndent(".limit locals 1");
	// writer.printlnWithIndent("return");
	// writer.decreaseIndent();
	// writer.printlnWithIndent(".end method");
	// writer.println();
	//
	// return writer.toString();
	// }
}
