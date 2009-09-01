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
package com.aptana.ide.editors.unified.parsing;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.bnf.IReductionContext;
import com.aptana.ide.parsing.bnf.IReductionHandler;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeFactory;

/**
 * @author Kevin Lindsey
 */
public abstract class UnifiedReductionHandler<T extends IParseNode> implements IReductionHandler
{
	private transient Object[] _values;
	private int _size;
	
	protected IParseNodeFactory nodeFactory;
	protected LexemeList lexemes;
	
	/**
	 * UnifiedReductionHandler
	 */
	public UnifiedReductionHandler()
	{
		this._values = new Object[16];
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#afterParse(com.aptana.ide.parsing.IParseState, com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void afterParse(IParseState parseState, IParseNode parentNode)
	{
		this.nodeFactory = null;
		this.lexemes = null;
		
		for (int i = 0; i < this._size; i++)
		{
			this._values[i] = null;
		}
		
		this._size = 0;
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#beforeParse(com.aptana.ide.parsing.IParseState, com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void beforeParse(IParseState parseState, IParseNode parentNode)
	{
		if (parseState != null)
		{
			this.nodeFactory = parseState.getParseNodeFactory();
			this.lexemes = parseState.getLexemeList();
		}
	}

	/**
	 * createNode
	 * @param type
	 * @param startingLexeme
	 * 
	 * @return IParseNode
	 */
	@SuppressWarnings("unchecked")
	protected T createNode(int type, Lexeme startingLexeme)
	{
		T result = null;
		
		if (this.nodeFactory != null)
		{
			result = (T) this.nodeFactory.createParseNode(type, startingLexeme);
		}
		
		return result;
	}
	
	/**
	 * Make sure our internal value array has enough room for the given index 
	 * 
	 * @param index
	 */
	private void ensureCapacity(int index)
	{
		int currentLength = this._values.length;
		
		// see if the index we want is within our buffer
		if (index > currentLength)
		{
			// it's not, add about 50% to our current buffer size
			int newLength = (currentLength * 3) / 2 + 1;
			
			// if that's still not big enough, then use what was requested
			if (newLength < index)
			{
				newLength = index;
			}

			// create a new empty list
			Object[] newList = new Object[newLength];
			
			// move the current contents to our new list
			System.arraycopy(this._values, 0, newList, 0, this._size);
			
			// set out current list to the new list
			this._values = newList;
		}
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#getValues()
	 */
	public Object[] getValues()
	{
		Object[] result = new Object[this._size];
		
		if (this._size > 0)
		{
			System.arraycopy(this._values, 0, result, 0, this._size);
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#push(Object)
	 */
	public void push(Object value)
	{
		this.ensureCapacity(this._size + 1);
		this._values[this._size++] = value;
	}
	
	/**
	 * pop
	 * 
	 * @param count
	 * @return
	 */
	protected Object[] pop(int count)
	{
		Object[] nodes = new Object[count];
		
		System.arraycopy(this._values, this._size - count, nodes, 0, count);
		
		this._size -= count;
		
		return nodes;
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#reduce(com.aptana.ide.parsing.bnf.IReductionContext)
	 */
	public abstract void reduce(IReductionContext context);
}
