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

import com.aptana.ide.lexer.Lexeme;

/**
 * @author Kevin Lindsey
 */
public class RuleMatcher extends AbstractLexemeMatcher
{
	private String _name;

	/**
	 * RuleElement
	 */
	public RuleMatcher()
	{
	}

	/**
	 * @see com.aptana.ide.parsing.matcher.AbstractLexemeMatcher#addChildTypes()
	 */
	public void addChildTypes()
	{
		this.addChildType(ILexemeMatcher.class);
	}

	/**
	 * getName
	 * 
	 * @return String
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * @see com.aptana.ide.parsing.matcher.ILexemeMatcher#match(com.aptana.ide.lexer.Lexeme[], int, int)
	 */
	public int match(Lexeme[] lexemes, int offset, int eofOffset)
	{
		int result = offset;
		
		this.reset();
		
		for (int i = 0; i < this.getChildCount(); i++)
		{
			ILexemeMatcher matcher = (ILexemeMatcher) this.getChild(i);
			
			result = matcher.match(lexemes, result, eofOffset);
			
			if (result == -1)
			{
				break;
			}
			else
			{
				this.addChildParseNode(matcher.getParseResults());
			}
		}
		
		if (result != -1)
		{
			this.accept(lexemes, offset, result);
		}
		
		return result;
	}

	/**
	 * setName
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this._name = name;
	}
}
