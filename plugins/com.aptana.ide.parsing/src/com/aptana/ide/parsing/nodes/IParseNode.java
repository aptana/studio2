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
package com.aptana.ide.parsing.nodes;

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.IRange;
import com.aptana.ide.lexer.Lexeme;

/**
 * @author Kevin Lindsey
 */
public interface IParseNode extends IRange
{
	/**
	 * Adds a child to the node.
	 * 
	 * @param child
	 */
	void appendChild(IParseNode child);

	/**
	 * getAttributeValue
	 * 
	 * @param attributeName
	 * @return String
	 */
	String getAttribute(String attributeName);

	/**
	 * getAttributeNode
	 * 
	 * @param attributeName
	 * @return IParseNodeAttribute
	 */
	IParseNodeAttribute getAttributeNode(String attributeName);

	/**
	 * getAttributes
	 * 
	 * @return IParseNodeAttribute[]
	 */
	IParseNodeAttribute[] getAttributes();

	/**
	 * Gets the child at the specified index
	 * 
	 * @param index
	 *            The index of the child to retrieve
	 * @return Returns the child at the specified index
	 */
	IParseNode getChild(int index);

	/**
	 * Gets the number of children the node has.
	 * 
	 * @return Returns the number of children the node has.
	 */
	int getChildCount();

	/**
	 * Gets the index of this node in relation to its siblings. The first child begins with index zero
	 * 
	 * @return Returns this nodes child index
	 */
	int getChildIndex();

	/**
	 * Gets the children of the node.
	 * 
	 * @return Returns the children of the node.
	 */
	IParseNode[] getChildren();

	/**
	 * Gets the ending lexeme of the node.
	 * 
	 * @return Returns the ending lexeme of the node.
	 */
	Lexeme getEndingLexeme();

	/**
	 * Gets the language of the node.
	 * 
	 * @return Returns the language of the node.
	 */
	String getLanguage();

	/**
	 * Gets the name of the node, if any.
	 * 
	 * @return Returns the name of the node, if any.
	 */
	String getName();

	/**
	 * Find the node that contains the given offset. This method will keep descending the tree until it finds the
	 * deepest node that contains the offset
	 * 
	 * @param offset
	 *            The offset within the source files
	 * @return Returns the deepest node that contains the given offset. Returns null if no node contains the offset
	 */
	IParseNode getNodeAtOffset(int offset);

	/**
	 * Gets the parent of the node.
	 * 
	 * @return Returns the parent of the node.
	 */
	IParseNode getParent();

	/**
	 * Return the path to this element from the root node using the forward slash, '/', as a delimiter between
	 * elements in the path. Note the value returned by this method may not be unique. For example, if two sibling
	 * elements have the same name, then their paths will be identical.
	 * 
	 * @return String
	 */
	String getPath();
	
	/**
	 * Gets the source code representation of this node and its descendants
	 * 
	 * @return Returns a string representation of this node as source code
	 */
	String getSource();

	/**
	 * getSource
	 * 
	 * @param writer
	 */
	void getSource(SourceWriter writer);

	/**
	 * Gets the starting lexeme of the node.
	 * 
	 * @return Returns the starting lexeme of the node.
	 */
	Lexeme getStartingLexeme();

	/**
	 * Gets this node's text value.
	 * 
	 * @return Returns the text value of this node
	 */
	String getText();

	/**
	 * Gets the node index type
	 * 
	 * @return Returns the unique node index types
	 */
	int getTypeIndex();

	/**
	 * Return the path to this element from the root node using the forward slash, '/', as a delimiter between
	 * elements in the path. Note the value returned by this method is unique. For example, if two sibling
	 * elements have the same name, then their paths will differ by their element index in the last part of
	 * the calculated path.
	 * 
	 * @return String
	 */
	String getUniquePath();
	
	/**
	 * Gets the xml representation of this node and its descendants
	 * 
	 * @return Returns a string representation of this node as source code
	 */
	String getXML();

	/**
	 * getXML
	 * 
	 * @param writer
	 */
	void getXML(SourceWriter writer);

	/**
	 * hasAttribute
	 * 
	 * @param attributeName
	 * @return boolean
	 */
	boolean hasAttribute(String attributeName);

	/**
	 * hasAttributes
	 * 
	 * @return boolean
	 */
	boolean hasAttributes();

	/**
	 * Determine if this node has any child nodes
	 * 
	 * @return Returns true if this node has one or more children
	 */
	boolean hasChildren();

	/**
	 * Makes sure that this node (and its ancestors) include the given
	 * lexeme's range into their range
	 * 
	 * @param lexeme
	 * @return boolean
	 */
	boolean includeLexemeInRange(Lexeme lexeme);
	
	/**
	 * Makes sure that this node (and its ancestors) include the given
	 * lexemes' range into their range
	 * 
	 * @param startingLexeme
	 * @param endingLexeme
	 * @return boolean
	 */
	boolean includeLexemesInRange(Lexeme startingLexeme, Lexeme endingLexeme);

	/**
	 * setAttributeValue
	 * 
	 * @param name
	 * @param value
	 */
	void setAttribute(String name, String value);

	/**
	 * Sets the ending lexeme of the node. This has been deprecated. Use includeLexemeInRange
	 * in place of this method
	 * 
	 * @deprecated
	 * @param endLexeme
	 */
	void setEndingLexeme(Lexeme endLexeme);
	
	/**
	 * Sets the name of the node.
	 * 
	 * @param name
	 */
	void setName(String name);
}
