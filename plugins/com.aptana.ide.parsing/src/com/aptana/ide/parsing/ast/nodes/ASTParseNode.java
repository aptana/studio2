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
import com.aptana.ide.parsing.nodes.ParseNodeBase;

/**
 * @author Kevin Lindsey
 *
 */
public class ASTParseNode extends ParseNodeBase
{
	/**
	 * ASTNode
	 * 
	 * @param typeIndex
	 * @param startingLexeme
	 */
	public ASTParseNode(int typeIndex, Lexeme startingLexeme)
	{
		super(typeIndex, "text/ast", startingLexeme); //$NON-NLS-1$
	}
	
	/**
	 * ASTNode
	 * 
	 * @param typeIndex
	 * @param startingLexeme
	 * @param endingLexeme
	 */
	public ASTParseNode(int typeIndex, Lexeme startingLexeme, Lexeme endingLexeme)
	{
		super(typeIndex, "test/ast", startingLexeme); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.ParseNodeBase#getName()
	 */
	public String getName()
	{
		String result;
		
		switch (this.getTypeIndex())
		{
			case ASTParseNodeTypes.ROOT:
				result = "ast"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.IMPORT:
				result = "import"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.DOT:
				result = "dot"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.HANDLER:
				result = "handler"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.IDENTIFIER:
				result = "identifier"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.PARAMETER:
				result = "parameter"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.APPEND:
				result = "append"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.INSTANTIATION:
				result = "new"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.SWITCH:
				result = "switch"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.CASE:
				result = "case"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.INVOCATION:
				result = "invoke"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.ASSIGN:
				result = "assign"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.STRING:
				result = "string"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.NUMBER:
				result = "number"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.TRUE:
				result = "boolean"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.FALSE:
				result = "boolean"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.NULL:
				result = "null"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.ARGUMENT:
				result = "argument"; //$NON-NLS-1$
				break;
				
			case ASTParseNodeTypes.LIST:
				result = "list"; //$NON-NLS-1$
				break;
				
			default:
				result = super.getName();
		}
		
		return result;
	}
}
