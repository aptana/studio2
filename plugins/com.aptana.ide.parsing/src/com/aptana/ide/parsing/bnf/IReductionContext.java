/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.parsing.bnf;

import com.aptana.ide.parsing.IParser;

/**
 * @author Kevin Lindsey
 */
public interface IReductionContext
{
	/**
	 * Returns the name of the action associated with this rule that caused a reduction handler to fire
	 * 
	 * @return
	 */
	String getAction();

	/**
	 * Returns the node at the given index. Indexes correspond to the position of the terminals and non-terminals for
	 * the rule that matched. Nodes are zero-indexed
	 * 
	 * @param index
	 * @return Returns null if the index is invalid
	 */
	Object getNode(int index);

	/**
	 * Returns a node with the given alias. If more than one terminal or non-terminal have the same alias, the item
	 * furthest to the left will be returned. Use getNode(String, int) to nodes to the right of that node. Returns null
	 * 
	 * @param name
	 * @return Returns null if the name does not exist
	 */
	Object getNode(String name);

	/**
	 * Returns the nth node with the given alias. Index corresponds to the nth terminal or non-terminal in the rule
	 * having the name specified. The first item with the alias begins with a zero index.
	 * 
	 * @param name
	 * @param index
	 * @return Returns null if the name does not exist of if the index is invalid
	 */
	Object getNode(String name, int index);

	/**
	 * Returns the number of nodes in this context
	 * 
	 * @return
	 */
	int getNodeCount();

	/**
	 * Returns all nodes in this context
	 * 
	 * @return
	 */
	Object[] getNodes();

	/**
	 * Returns a reference to the parser associated with this handler
	 * 
	 * @return
	 */
	IParser getParser();

	/**
	 * Returns the name of the production in the BNF grammar that has matched
	 * 
	 * @return
	 */
	String getProductionName();
	
	/**
	 * Set the nodes that are active within this context
	 * 
	 * @param nodes
	 */
	void setNodes(Object[] nodes);
}