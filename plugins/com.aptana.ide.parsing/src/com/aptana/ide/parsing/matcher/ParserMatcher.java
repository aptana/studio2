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
import java.util.Map;

import com.aptana.ide.lexer.DynamicEnumerationMap;
import com.aptana.ide.lexer.IEnumerationMap;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.xml.INode;

/**
 * @author Kevin Lindsey
 */
public class ParserMatcher extends AbstractLexemeMatcher
{
	private String _language;
	private Map<String,INode> _rules;
	private RuleMatcher _firstRule;
	private boolean _sealed;
	private IEnumerationMap _typeIndexMap;

	/**
	 * ParserElement
	 */
	public ParserMatcher()
	{
	}

	/**
	 * @see com.aptana.xml.NodeBase#appendChild(com.aptana.xml.INode)
	 */
	public void appendChild(INode child)
	{
		super.appendChild(child);

		if (child != null && child instanceof RuleMatcher)
		{
			RuleMatcher rule = (RuleMatcher) child;
			String name = rule.getName();

			if (this.getChildCount() == 1)
			{
				this._firstRule = rule;
			}

			if (this._rules == null)
			{
				this._rules = new HashMap<String,INode>();
			}

			if (this._rules.containsKey(name) == false)
			{
				this._rules.put(name, child);
			}
		}
	}

	/**
	 * getLanguage
	 * 
	 * @return String
	 */
	public String getLanguage()
	{
		return this._language;
	}

	/**
	 * getNodeTypeIndex
	 *
	 * @param name
	 * @return int
	 */
	public int getNodeTypeIndex(String name)
	{
		return this._typeIndexMap.getIntValue(name);
	}
	
	/**
	 * getRuleByName
	 * 
	 * @param name
	 * @return RuleElement
	 */
	public RuleMatcher getRuleByName(String name)
	{
		RuleMatcher result = null;

		if (this._rules != null && this._rules.containsKey(name))
		{
			result = (RuleMatcher) this._rules.get(name);
		}

		return result;
	}

	/**
	 * match
	 * 
	 * @param lexemes
	 * @param offset
	 * @param eofOffset
	 * @return int
	 */
	public int match(Lexeme[] lexemes, int offset, int eofOffset)
	{
		int result = -1;

		if (this._firstRule != null)
		{
			result = this._firstRule.match(lexemes, offset, eofOffset);
		}

		if (result != -1)
		{
			this.result = this._firstRule.getParseResults();
		}

		return result;
	}

	/**
	 * seal
	 */
	public void seal()
	{
		if (this._sealed == false)
		{
			this._sealed = true;
			
			// create dynamic type index map
			this._typeIndexMap = new DynamicEnumerationMap();
			
			// walk tree adding in all nodes
			this.addTypesToMap(this._typeIndexMap);
		}
	}

	/**
	 * setLanguage
	 * 
	 * @param language
	 */
	public void setLanguage(String language)
	{
		this._language = language;
	}

	/**
	 * validate
	 */
	public void validate()
	{
	}

	/**
	 * @see com.aptana.ide.parsing.matcher.AbstractLexemeMatcher#addChildTypes()
	 */
	public void addChildTypes()
	{
		this.addChildType(RuleMatcher.class);
	}
}
