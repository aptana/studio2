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
package com.aptana.ide.parsing.bnf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.aptana.ide.parsing.bnf.nodes.ProductionNode;
import com.aptana.ide.parsing.bnf.nodes.TerminalNode;

/**
 * @author Kevin Lindsey
 */
public class TerminalList implements Iterable<TerminalNode>
{
	private List<TerminalNode> _terminals;
	private boolean _epsilon;
	private Set<ProductionNode> _visitedProductions;

	/**
	 * add
	 * 
	 * @param terminal
	 */
	public void add(TerminalNode terminal)
	{
		if (this._terminals == null)
		{
			this._terminals = new ArrayList<TerminalNode>();
		}

		if (this._terminals.contains(terminal) == false)
		{
			this._terminals.add(terminal);
		}
	}

	/**
	 * add
	 * 
	 * @param terminals
	 */
	public void add(TerminalList terminals)
	{
		List<TerminalNode> list = terminals._terminals;

		if (list != null)
		{
			for (int i = 0; i < list.size(); i++)
			{
				this.add(list.get(i));
			}
		}

		if (terminals.hasEpsilon())
		{
			this.addEpsilon();
		}
	}

	/**
	 * addEpsilon
	 */
	public void addEpsilon()
	{
		this._epsilon = true;
	}

	/**
	 * addProduction
	 * 
	 * @param production
	 */
	public void addProduction(ProductionNode production)
	{
		if (this._visitedProductions == null)
		{
			this._visitedProductions = new HashSet<ProductionNode>();
		}

		this._visitedProductions.add(production);
	}

	/**
	 * get
	 * 
	 * @param index
	 * @return
	 */
	public TerminalNode get(int index)
	{
		TerminalNode result = null;

		if (this._terminals != null)
		{
			if (0 <= index && index < this.size())
			{
				result = this._terminals.get(index);
			}
		}

		return result;
	}

	/**
	 * hasEpsilon
	 * 
	 * @return
	 */
	public boolean hasEpsilon()
	{
		return this._epsilon;
	}

	/**
	 * hasProduction
	 * 
	 * @param production
	 * @return
	 */
	public boolean hasProduction(ProductionNode production)
	{
		boolean result = false;

		if (this._visitedProductions != null)
		{
			result = this._visitedProductions.contains(production);
		}

		return result;
	}

	/**
	 * removeEpsilon
	 */
	public void removeEpsilon()
	{
		this._epsilon = false;
	}

	/**
	 * size
	 * 
	 * @return
	 */
	public int size()
	{
		int result = 0;

		if (this._terminals != null)
		{
			result = this._terminals.size();
		}

		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("{"); //$NON-NLS-1$

		if (this._terminals != null && this._terminals.size() > 0)
		{
			buffer.append(" "); //$NON-NLS-1$
			buffer.append(this._terminals.get(0).getName());

			for (int i = 1; i < this._terminals.size(); i++)
			{
				buffer.append(", "); //$NON-NLS-1$
				buffer.append(this._terminals.get(i).getName());
			}

			if (this.hasEpsilon())
			{
				buffer.append(", "); //$NON-NLS-1$
				buffer.append("<e>"); //$NON-NLS-1$
			}
		}
		else
		{
			if (this.hasEpsilon())
			{
				buffer.append(" <e>"); //$NON-NLS-1$
			}
		}

		buffer.append(" }"); //$NON-NLS-1$

		return buffer.toString();
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<TerminalNode> iterator()
	{
		return this._terminals.iterator();
	}
}
