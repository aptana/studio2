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
package com.aptana.ide.editor.css.parsing.nodes;

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class CSSListNode extends CSSParseNode
{
	/*
	 * Fields
	 */
	private String _delimiter;
	private String _listName;
	
	/*
	 * Properties
	 */
	
	/**
	 * 
	 * getDelimiter
	 *
	 * @return String
	 */
	public String getDelimiter()
	{
		return this._delimiter;
	}
	
	/**
	 * setDelimiter
	 *
	 * @param delimiter
	 */
	public void setDelimiter(String delimiter)
	{
		this._delimiter = delimiter;
	}
	
	/*
	 * Constructors
	 */
	
	/**
	 * CSSListNode
	 * @param startLexeme
	 */
	public CSSListNode(Lexeme startLexeme)
	{
		super(CSSParseNodeTypes.LIST, startLexeme);
	}
	
	/**
	 * setListName
	 *
	 * @param listName
	 */
	public void setListName(String listName)
	{
		this._listName = listName;
	}
	
	/**
	 * @see com.aptana.ide.editor.css.parsing.nodes.CSSParseNode#getName()
	 */
	public String getName()
	{
		String result = this._listName;
		
		if (result == null)
		{
			result = super.getName();
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.ParseNodeBase#getSource(com.aptana.ide.io.SourceWriter)
	 */
	public void getSource(SourceWriter writer)
	{
		for (int i = 0; i < this.getChildCount(); i++)
		{
			IParseNode child = this.getChild(i);
			
			if (child != CSSParseNode.Empty)
			{
				if (i > 0 && this._delimiter != null)
				{
					if (this._delimiter.equals("\n")) //$NON-NLS-1$
					{
						writer.println();
					}
					else
					{
						writer.print(this._delimiter);
					}
				}
				
				child.getSource(writer);
			}
		}
	}
}
