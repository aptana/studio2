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
package com.aptana.ide.lexer.matcher.model;

import com.aptana.ide.lexer.DynamicEnumerationMap;
import com.aptana.ide.lexer.IEnumerationMap;
import com.aptana.ide.lexer.ITokenList;
import com.aptana.ide.lexer.LexerPlugin;
import com.aptana.ide.lexer.StaticEnumerationMap;
import com.aptana.ide.lexer.matcher.MatcherTokenList;
import com.aptana.xml.Bundle;

/**
 * @author Kevin Lindsey
 */
public class LexerElement extends MatcherElement
{
	private String _language;
	private String _categoryEnumeration;
	private String _typeEnumeration;

	/**
	 * Lexer
	 */
	public LexerElement()
	{
		this.addChildType(TokenGroupElement.class);
		this.addChildType(Bundle.class);
		this.addChildType(com.aptana.xml.Package.class);
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
	 * setLanguage
	 * 
	 * @param language
	 */
	public void setLanguage(String language)
	{
		this._language = language;
	}

	/**
	 * getCategoryEnumeration
	 * 
	 * @return String
	 */
	public String getCategoryEnumeration()
	{
		return this._categoryEnumeration;
	}

	/**
	 * setCategoryEnumeration
	 * 
	 * @param categoryEnumeration
	 */
	public void setCategoryEnumeration(String categoryEnumeration)
	{
		this._categoryEnumeration = categoryEnumeration;
	}

	/**
	 * getTypeEnumeration
	 * 
	 * @return String
	 */
	public String getTypeEnumeration()
	{
		return this._typeEnumeration;
	}

	/**
	 * setTypeEnumeration
	 * 
	 * @param typeEnumeration
	 */
	public void setTypeEnumeration(String typeEnumeration)
	{
		this._typeEnumeration = typeEnumeration;
	}

	/**
	 * getTokenList
	 * 
	 * @param loader
	 * @return ITokenList
	 */
	public ITokenList getTokenList(ClassLoader loader)
	{
		MatcherTokenList result = new MatcherTokenList(this.getLanguage());

		// apply enumeration maps
		result.setCategoryMap(this.createCategoryEnumeration(loader));
		result.setTypeMap(this.createTypeEnumeration(loader));

		// process groups
		this.createTokens(result);

		return result;
	}

	/**
	 * createCategoryEnumeration
	 * 
	 * @param tokenList
	 */
	private IEnumerationMap createCategoryEnumeration(ClassLoader loader)
	{
		IEnumerationMap map = null;

		if (this._categoryEnumeration != null && this._categoryEnumeration.length() > 0)
		{
			try
			{
				Class<?> typeClass = Class.forName(this._categoryEnumeration, true, loader);

				map = new StaticEnumerationMap(typeClass);
			}
			catch (ClassNotFoundException e)
			{
				LexerPlugin.logInfo(Messages.LexerElement_Cannot_Find_Category_Enumeration + this._categoryEnumeration, e);
			}
		}

		if (map == null)
		{
			map = new DynamicEnumerationMap();
		}

		return map;
	}

	/**
	 * createTypeEnumeration
	 * 
	 * @param tokenList
	 */
	private IEnumerationMap createTypeEnumeration(ClassLoader loader)
	{
		IEnumerationMap map = null;

		if (this._typeEnumeration != null && this._typeEnumeration.length() > 0)
		{
			try
			{
				Class<?> typeClass = Class.forName(this._typeEnumeration, true, loader);

				map = new StaticEnumerationMap(typeClass);
			}
			catch (ClassNotFoundException e)
			{
				LexerPlugin.logInfo(Messages.LexerElement_Cannot_Find_Type_Enumeration + this._typeEnumeration, e);
			}
		}

		if (map == null)
		{
			map = new DynamicEnumerationMap();
		}

		return map;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.MatcherElement#validateLocal()
	 */
	protected void validateLocal()
	{
		if (this._language == null || this._language.length() == 0)
		{
			this.getDocument().sendError(Messages.LexerElement_Missing_Language, this);
		}
	}
}
