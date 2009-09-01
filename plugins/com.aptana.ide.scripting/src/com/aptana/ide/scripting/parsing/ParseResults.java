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
package com.aptana.ide.scripting.parsing;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Paul Colton
 */
public class ParseResults extends ScriptableObject
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = 4226400255466317094L;

	private IParseNode[] _nodes = null;
	private IParseState _parseState = null;

	/*
	 * Properties
	 */

	/**
	 * get
	 * 
	 * @param index
	 * @param start
	 * @return Object
	 */
	public Object get(int index, Scriptable start)
	{
		if (this._nodes != null && 0 <= index && index < this._nodes.length)
		{
			return new ParseNode(this.getParentScope(), this._nodes[index]);
		}
		else
		{
			return super.get(index, start);
		}
	}

	/**
	 * @see org.mozilla.javascript.Scriptable#getClassName()
	 */
	public String getClassName()
	{
		return "ParseResults"; //$NON-NLS-1$
	}

	/**
	 * getLength
	 * 
	 * @return int
	 */
	public int getLength()
	{
		int result = 0;

		if (this._nodes != null)
		{
			result = this._nodes.length;
		}

		return result;
	}
	
	/**
	 * getXml
	 *
	 * @return XML representation of these parse results
	 */
	public String getXml()
	{
		SourceWriter writer = new SourceWriter();
		
		for (int i = 0; i < this._nodes.length; i++)
		{
			this._nodes[i].getXML(writer);
		}
		
		return writer.toString();
	}

	/**
	 * getSource
	 *
	 * @return string
	 */
	public String getSource()
	{
		return this._parseState.getParseResults().getSource();
	}
	
	/**
	 * ParseResults
	 * 
	 * @param scope
	 * @param parseState
	 */
	public ParseResults(Scriptable scope, IParseState parseState)
	{
		this.setParentScope(scope);
		
		IParseNode root = null;
		
		if (parseState != null)
		{
			root = parseState.getParseResults();
		}

		this._parseState = parseState;
		
		if (root != null)
		{
			this._nodes = root.getChildren();
		}
		else
		{
			this._nodes = new IParseNode[0];
		}

		this.defineProperty("length", ParseResults.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("source", ParseResults.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("xml", ParseResults.class, READONLY | PERMANENT); //$NON-NLS-1$

		this.defineFunctionProperties(new String[] { "getNodeByIndex" }, ParseResults.class, READONLY | PERMANENT); //$NON-NLS-1$
	}
}
