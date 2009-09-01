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
package com.aptana.ide.parsing.matcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.aptana.ide.lexer.DynamicEnumerationMap;
import com.aptana.ide.lexer.IEnumerationMap;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.ParseFragment;
import com.aptana.ide.parsing.nodes.ParseNodeBase;
import com.aptana.xml.INode;
import com.aptana.xml.NodeBase;

/**
 * @author Kevin Lindsey
 */
public abstract class AbstractLexemeMatcher extends NodeBase implements ILexemeMatcher
{
	private String _node;
	private ParseFragment _childNodes;
	private Map<String,String> _attributes;
	
	/**
	 * result
	 */
	protected IParseNode result;
	
	/**
	 * AbstractLexemeMatcher
	 */
	public AbstractLexemeMatcher()
	{
		this.addChildTypes();
	}

	/**
	 * accept
	 * 
	 * @param lexemes
	 * @param startingIndex
	 * @param endingIndex
	 */
	protected void accept(Lexeme[] lexemes, int startingIndex, int endingIndex)
	{
		if (this._node != null && this._node.length() > 0)
		{
			// get starting lexeme
			Lexeme startingLexeme = lexemes[startingIndex];
			
			// use lexeme's language for node's language
			String language = startingLexeme.getLanguage();
			
			// calculate the node type index from the node type (name)
			int type = this.getOwningParser().getNodeTypeIndex(this._node);
			
			// create the parse node
			IParseNode node = new ParseNodeBase(this._node, type, language, startingLexeme);
			
			// set the display name
			node.setName(this._node.toLowerCase());

			// include the ending lexeme, if there is one
			if (startingIndex <= endingIndex - 1)
			{
				Lexeme endingLexeme = lexemes[endingIndex - 1];
				node.includeLexemeInRange(endingLexeme);
			}
			
			// save the result for potential future appendChild operations from this node's
			// ancestors
			this.result = node;
			
			// add any accumulated child nodes to the result, if we have any
			if (this._childNodes != null)
			{
				this.result.appendChild(this._childNodes);
			}
		}
		else
		{
			// no node definition, so pass along any accumulated nodes from this node's
			// descendants
			this.result = this._childNodes;
		}
		
		// add any accumulated attributes
		if (this.result != null && this._attributes != null && this._attributes.size() > 0)
		{
			Set<Map.Entry<String,String>> entries = this._attributes.entrySet();
			Iterator<Map.Entry<String,String>> entryIterator = entries.iterator();
			
			while (entryIterator.hasNext())
			{
				Map.Entry<String,String> entry = entryIterator.next();
				
				this.result.setAttribute(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * addAttribute
	 *
	 * @param name
	 * @param value
	 */
	protected void addAttribute(String name, String value)
	{
		if (name != null && name.length() > 0 && value != null && value.length() > 0)
		{
			if (this._attributes == null)
			{
				this._attributes = new HashMap<String,String>();
			}
			
			this._attributes.put(name, value);
			
			// make sure we have a parse tree fragment that the attribute can ride on
			if (this._childNodes == null)
			{
				this._childNodes = new ParseFragment();
			}
		}
	}
	
	/**
	 * addChildNode
	 *
	 * @param childParseNode
	 */
	protected void addChildParseNode(IParseNode childParseNode)
	{
		if (childParseNode != null)
		{
			if (this._childNodes == null)
			{
				this._childNodes = new ParseFragment();
			}
			
			this._childNodes.appendChild(childParseNode);
		}
	}
	
	/**
	 * addChildTypes
	 */
	public abstract void addChildTypes();
	
		
	/**
	 * addTypesToMap
	 *
	 * @param indexMap
	 */
	protected void addTypesToMap(IEnumerationMap indexMap)
	{
		if (indexMap instanceof DynamicEnumerationMap)
		{
			DynamicEnumerationMap map = (DynamicEnumerationMap) indexMap;
			
			if (this._node != null && this._node.length() > 0)
			{
				map.getIntValue(this._node);
			}
			
			for (int i = 0; i < this.getChildCount(); i++)
			{
				INode child = this.getChild(i);
				
				if (child instanceof AbstractLexemeMatcher)
				{
					((AbstractLexemeMatcher) child).addTypesToMap(indexMap);
				}
			}
		}
	}
	
	/**
	 * reset
	 */
	protected void reset()
	{
		this._childNodes = null;
		
		if (this._attributes != null)
		{
			this._attributes.clear();
		}
		
		this.result = null;
	}
	
	/**
	 * getNode
	 * 
	 * @return String
	 */
	public String getNode()
	{
		return this._node;
	}

	/**
	 * getOwningParser
	 * 
	 * @return ParserElement
	 */
	public ParserMatcher getOwningParser()
	{
		ParserMatcher result = null;
		INode current = this;

		while (current != null)
		{
			if (current instanceof ParserMatcher)
			{
				result = (ParserMatcher) current;
				break;
			}
			else
			{
				current = current.getParent();
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.matcher.ILexemeMatcher#getParseResults()
	 */
	public IParseNode getParseResults()
	{
		return this.result;
	}
	
	/**
	 * setNode
	 * 
	 * @param node
	 */
	public void setNode(String node)
	{
		this._node = node;
	}
}
