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
package com.aptana.ide.editors.unified.folding;

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeAttribute;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class GenericCommentNode implements IParseNode
{
	private static IParseNode[] EMPTY = new IParseNode[0];
	
	private String _language;
	private String _name;
	private int _startingOffset;
	private int _endingOffset;

	/**
	 * Creates a new generic comment node
	 * 
	 * @param startingIndex
	 * @param endingIndex
	 * @param name
	 * @param language
	 */
	public GenericCommentNode(int startingIndex, int endingIndex, String name, String language)
	{
		this._language = language;
		this._name = name;
		this._startingOffset = startingIndex;
		this._endingOffset = endingIndex;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#appendChild(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void appendChild(IParseNode child)
	{
		// Does nothing
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#containsOffset(int)
	 */
	public boolean containsOffset(int offset)
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getAttribute(java.lang.String)
	 */
	public String getAttribute(String attributeName)
	{
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getAttributeNode(java.lang.String)
	 */
	public IParseNodeAttribute getAttributeNode(String attributeName)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getAttributes()
	 */
	public IParseNodeAttribute[] getAttributes()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getChild(int)
	 */
	public IParseNode getChild(int index)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getChildCount()
	 */
	public int getChildCount()
	{
		return 0;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getChildIndex()
	 */
	public int getChildIndex()
	{
		return 0;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getChildren()
	 */
	public IParseNode[] getChildren()
	{
		return EMPTY;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getEndingLexeme()
	 */
	public Lexeme getEndingLexeme()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getEndingOffset()
	 */
	public int getEndingOffset()
	{
		return this._endingOffset;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getLanguage()
	 */
	public String getLanguage()
	{
		return this._language;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getLength()
	 */
	public int getLength()
	{
		return this._endingOffset - this._startingOffset;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getName()
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getNodeAtOffset(int)
	 */
	public IParseNode getNodeAtOffset(int offset)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getParent()
	 */
	public IParseNode getParent()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getPath()
	 */
	public String getPath()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getSource()
	 */
	public String getSource()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getSource(com.aptana.ide.io.SourceWriter)
	 */
	public void getSource(SourceWriter writer)
	{

	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getStartingLexeme()
	 */
	public Lexeme getStartingLexeme()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getStartingOffset()
	 */
	public int getStartingOffset()
	{
		return this._startingOffset;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getText()
	 */
	public String getText()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getTypeIndex()
	 */
	public int getTypeIndex()
	{
		return 0;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getUniquePath()
	 */
	public String getUniquePath()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getXML()
	 */
	public String getXML()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getXML(com.aptana.ide.io.SourceWriter)
	 */
	public void getXML(SourceWriter writer)
	{

	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#hasAttribute(java.lang.String)
	 */
	public boolean hasAttribute(String attributeName)
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#hasAttributes()
	 */
	public boolean hasAttributes()
	{
		return false;
	}
	
	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#hasChildren()
	 */
	public boolean hasChildren()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#includeLexemeInRange(com.aptana.ide.lexer.Lexeme)
	 */
	public boolean includeLexemeInRange(Lexeme endLexeme)
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#includeLexemesInRange(com.aptana.ide.lexer.Lexeme, Lexeme)
	 */
	public boolean includeLexemesInRange(Lexeme startingLexeme, Lexeme endingLexeme)
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#isEmpty()
	 */
	public boolean isEmpty()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#setAttribute(java.lang.String, java.lang.String)
	 */
	public void setAttribute(String name, String value)
	{

	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#setEndingLexeme(com.aptana.ide.lexer.Lexeme)
	 * @deprecated
	 */
	public void setEndingLexeme(Lexeme endLexeme)
	{
		
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#setName(java.lang.String)
	 */
	public void setName(String name)
	{

	}
}
