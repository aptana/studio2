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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.parsing.bnf;

import java.util.List;
import java.util.Map;

import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.bnf.nodes.SequenceNode;

/**
 * @author Kevin Lindsey
 */
public class ReductionContext implements IReductionContext
{
	private static final Object[] NO_NODES = new Object[0];
	
	private IParser _parser;
	private String _productionName;
	private String _action;
	private Map<String,List<Integer>> _nameIndex;
	private int _nodeCount;
	private Object[] _nodes;
	
	/**
	 * ReductionContext
	 * @param rule
	 * @param parser
	 * @param parseState
	 */
	public ReductionContext(String productionName, SequenceNode rule, IParser parser)
	{
		this(productionName, rule);
		
		this._parser = parser;
	}
	
	/**
	 * ReductionContext
	 * 
	 * @param rule
	 */
	public ReductionContext(String productionName, SequenceNode rule)
	{
		this._productionName = productionName;
		this._action = rule.getActionName();
		this._nameIndex = rule.getNameIndexMap();
		this._nodeCount = rule.getChildCount();
		this._nodes = NO_NODES;
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionContext#getAction()
	 */
	public String getAction()
	{
		return this._action;
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionContext#getNode(int)
	 */
	public Object getNode(int index)
	{
		Object result = null;
		
		if (0 <= index && index < this._nodes.length)
		{
			result = this._nodes[index];
		}
		
		return result;
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionContext#getNode(java.lang.String)
	 */
	public Object getNode(String name)
	{
		return this.getNode(name, 0);
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionContext#getNode(java.lang.String, int)
	 */
	public Object getNode(String name, int index)
	{
		int nodeIndex = this.getNodeIndex(name, index);
		Object result = null;
		
		if (nodeIndex != -1)
		{
			result = this._nodes[nodeIndex];
		}
		
		return result;
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionContext#getNodeCount()
	 */
	public int getNodeCount()
	{
		return this._nodeCount;
	}
	
	/**
	 * getNodeIndex
	 * 
	 * @param name
	 * @param index
	 * @return
	 */
	private int getNodeIndex(String name, int index)
	{
		int result = -1;
		
		if (this._nameIndex != null && this._nameIndex.containsKey(name))
		{
			List<Integer> indexes = this._nameIndex.get(name);
			
			if (0 <= index && index < indexes.size())
			{
				result = indexes.get(index);
			}
		}
		
		return result;
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionContext#getNodes()
	 */
	public Object[] getNodes()
	{
		return this._nodes;
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionContext#getParser()
	 */
	public IParser getParser()
	{
		return this._parser;
	}
	
	/**
	 * getRuleName
	 * 
	 * @return
	 */
	public String getProductionName()
	{
		return this._productionName;
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionContext#setNodes(java.lang.Object[])
	 */
	public void setNodes(Object[] nodes)
	{
		if (nodes == null)
		{
			this._nodes = NO_NODES;
		}
		else
		{
			this._nodes = nodes;
		}
	}
}
