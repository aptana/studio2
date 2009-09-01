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
package com.aptana.ide.parsing.ast.nodes;

import com.aptana.ide.lexer.Lexeme;

/**
 * @author Kevin Lindsey
 */
public class ListNode extends ASTParseNode
{
	private String _listName;
	
	/**
	 * ListNode
	 * 
	 * @param startingLexeme
	 */
	public ListNode()
	{
		this(null, null);
	}
	
	/**
	 * ListNode
	 * 
	 * @param startingLexeme
	 */
	public ListNode(Lexeme startingLexeme)
	{
		this(startingLexeme, startingLexeme);
	}
	
	/**
	 * ListNode
	 * 
	 * @param startingLexeme
	 * @param endingLexeme
	 */
	public ListNode(Lexeme startingLexeme, Lexeme endingLexeme)
	{
		super(ASTParseNodeTypes.LIST, startingLexeme, endingLexeme);
	}
	
	/**
	 * getListName
	 * 
	 * @return
	 */
	public String getListName()
	{
		return this._listName;
	}
	
	/**
	 * @see com.aptana.ide.parsing.ast.nodes.ASTParseNode#getName()
	 */
	public String getName()
	{
		String result = this._listName;
		
		if (result == null || result.length() == 0)
		{
			result = super.getName();
		}
		
		return result;
	}

	/**
	 * setListName
	 * 
	 * @param name
	 */
	public void setListName(String name)
	{
		this._listName = name;
	}
}
