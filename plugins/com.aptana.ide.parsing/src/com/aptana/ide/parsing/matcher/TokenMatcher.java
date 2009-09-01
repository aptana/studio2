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
public class TokenMatcher extends AbstractLexemeMatcher
{
	private String _category;
	private String _type;
	private String _attribute;

	/**
	 * TokenMatcher
	 */
	public TokenMatcher()
	{
	}

	/**
	 * @see com.aptana.ide.parsing.matcher.AbstractLexemeMatcher#addChildTypes()
	 */
	public void addChildTypes()
	{
	}

	/**
	 * getAttribute
	 *
	 * @return String
	 */
	public String getAttribute()
	{
		return this._attribute;
	}
	
	/**
	 * getCategory
	 *
	 * @return String
	 */
	public String getCategory()
	{
		return this._category;
	}
	
	/**
	 * getType
	 * 
	 * @return String
	 */
	public String getType()
	{
		return this._type;
	}

	/**
	 * setAttribute
	 *
	 * @param attribute
	 */
	public void setAttribute(String attribute)
	{
		this._attribute = attribute;
	}
	
	/**
	 * setCategory
	 *
	 * @param category
	 */
	public void setCategory(String category)
	{
		this._category = category;
	}
	
	/**
	 * setType
	 * 
	 * @param type
	 */
	public void setType(String type)
	{
		this._type = type;
	}

	/**
	 * @see com.aptana.ide.parsing.matcher.AbstractLexemeMatcher#accept(com.aptana.ide.lexer.Lexeme[], int, int)
	 */
	protected void accept(Lexeme[] lexemes, int startingIndex, int endingIndex)
	{
		if (this._attribute != null && this._attribute.length() > 0)
		{
			String value = lexemes[startingIndex].getText();
			
			// [KEL]: temporarily stripping leading and trailing quotes. This should be done
			// by some other mechanism
			if (value.startsWith("\"")) //$NON-NLS-1$
			{
				value = value.substring(1);
			}
			if (value.endsWith("\"")) //$NON-NLS-1$
			{
				value = value.substring(0, value.length() - 1);
			}
			
			this.addAttribute(this._attribute, value);
		}
		
		super.accept(lexemes, startingIndex, endingIndex);
	}

	/**
	 * @see com.aptana.ide.parsing.matcher.ILexemeMatcher#match(com.aptana.ide.lexer.Lexeme[], int, int)
	 */
	public int match(Lexeme[] lexemes, int offset, int eofOffset)
	{
		int result = -1;
		
		this.reset();
		
		if (offset < eofOffset)
		{
			String language = this.getOwningParser().getLanguage();
			Lexeme lexeme = lexemes[offset];
			
			if (lexeme.getLanguage().equals(language))
			{
				String category = this.getCategory();
				String type = this.getType();
				boolean match = true;
				
				if (category != null && category.length() > 0)
				{
					match = lexeme.getCategory().equals(category);
				}
				
				if (match && type != null && type.length() > 0)
				{
					match = lexeme.getType().equals(type);
				}
				
				if (match)
				{
					result = offset + 1;
				}
			}
		}
		
		if (result != -1)
		{
			this.accept(lexemes, offset, result);
		}
		
		return result;
	}
}
