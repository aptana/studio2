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

import java.util.Stack;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.ParseNodeBase;

/**
 * @author Kevin Lindsey
 */
public class ParseTreeHandler implements IReductionHandler
{
	private Stack<Object> _values = new Stack<Object>();
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#afterParse(com.aptana.ide.parsing.IParseState, com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void afterParse(IParseState parseState, IParseNode parentNode)
	{
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#beforeParse(com.aptana.ide.parsing.IParseState, com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void beforeParse(IParseState parseState, IParseNode parentNode)
	{
		this._values.clear();
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#getValues()
	 */
	public Object[] getValues()
	{
		return this._values.toArray();
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#push(java.lang.Object)
	 */
	public void push(Object value)
	{
		this._values.push(value);
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#reduce(com.aptana.ide.parsing.bnf.IReductionContext)
	 */
	public void reduce(IReductionContext context)
	{
		String ruleName = context.getProductionName();
		
		// create node
		IParseNode result = new ParseNodeBase(0, "text/parse-tree"); //$NON-NLS-1$
		result.setName(ruleName);
		
		// collect the matching rule's items from the stack
		int count = context.getNodeCount();
		Object[] nodes = new Object[count];
		
		for (int i = 0; i < count; i++)
		{
			nodes[count - i - 1] = this._values.pop();
		}
		
		// process nodes
		for (Object item : nodes)
		{
			if (item instanceof Lexeme)
			{
				Lexeme lexeme = (Lexeme) item;
				String value = lexeme.getText();
				
				IParseNode node = new ParseNodeBase(1, "text/parse-tree"); //$NON-NLS-1$
				node.setName("Lexeme"); //$NON-NLS-1$
				node.setAttribute("value", value); //$NON-NLS-1$
				
				result.appendChild(node);
			}
			else if (item instanceof IParseNode)
			{
				result.appendChild((IParseNode) item);
			}
		}
		
		// save result
		this._values.push(result);
	}
}
