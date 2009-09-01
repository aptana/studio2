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
package com.aptana.ide.parsing.bnf.nodes;

import java.util.List;

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.parsing.bnf.TerminalList;

/**
 * @author Kevin Lindsey
 */
public final class NonTerminalNode extends GrammarNodeBase
{
	private int _index;
	private String _alias;

	/**
	 * TerminalNode
	 * 
	 * @param owningGrammar
	 * @param name
	 * @param index
	 */
	public NonTerminalNode(GrammarNode owningGrammar, String name, int index)
	{
		super(owningGrammar, GrammarNodeTypes.NONTERMINAL, name);

		this._index = index;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		boolean result = false;

		if (this == obj)
		{
			result = true;
		}
		else if (obj instanceof NonTerminalNode)
		{
			NonTerminalNode that = (NonTerminalNode) obj;

			result = this.getName().equals(that.getName());
		}

		return result;
	}

	/**
	 * getAlias
	 * 
	 * @return
	 */
	public String getAlias()
	{
		return this._alias;
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.GrammarNodeBase#getFirst(com.aptana.ide.parsing.bnf.TerminalList)
	 */
	public void getFirst(TerminalList terminals)
	{
		ProductionNode[] productions = this.getOwningGrammar().getProductionsByName(this.getName());

		for (int i = 0; i < productions.length; i++)
		{
			ProductionNode production = productions[i];

			production.getFirst(terminals);
		}
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public int getIndex()
	{
		return this._index;
	}

	/**
	 * getProductions
	 * 
	 * @return
	 */
	public ProductionNode[] getProductions()
	{
		return this.getOwningGrammar().getProductionsByName(this.getName());
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.nodes.GrammarNodeBase#getSource(com.kevlindev.io.SourceWriter)
	 */
	public void getSource(SourceWriter writer)
	{
		writer.print(this.getName());
		
		if (this._alias != null && this._alias.length() > 0)
		{
			writer.print("=").print(this._alias); //$NON-NLS-1$
		}
	}

	/**
	 * @see com.kevlindev.bnf.nodes.GrammarNodeBase#getSymbols(List<IGrammarNode>)
	 */
	public void getSymbols(List<IGrammarNode> symbols)
	{
		if (symbols.contains(this) == false)
		{
			symbols.add(this);
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this.getName().hashCode();
	}

	/**
	 * isNullable
	 * 
	 * @return boolean
	 */
	public boolean isNullable()
	{
		ProductionNode[] productions = this.getOwningGrammar().getProductionsByName(this.getName());
		boolean result = false;

		for (int i = 0; i < productions.length; i++)
		{
			ProductionNode production = productions[i];

			if (production.isNullable())
			{
				result = true;
				break;
			}
		}

		return result;
	}
	
	/**
	 * setAlias
	 * 
	 * @param alias
	 */
	public void setAlias(String alias)
	{
		this._alias = alias;
	}
}
