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
package com.aptana.ide.lexer;

/**
 * @author Kevin Lindsey
 */
public class Token implements IToken
{
	/**
	 * The default category name
	 */
	public static String defaultTokenCategory = "NO_CATEGORY"; //$NON-NLS-1$

	/**
	 * The default lexer group name
	 */
	public static String defaultLexerGroup = "default"; //$NON-NLS-1$

	private ITokenList _owningTokenList;

	private int _index;
	private int _categoryIndex;
	private int _typeIndex;
	private int _lexerGroupIndex;
	private int _newLexerGroupIndex;

	private String _category;
	private String _type;
	private String _lexerGroup;
	private String _newLexerGroup;
	private String _language;

	private String _displayName;
	private boolean _sealed;

	/**
	 * TokenBase
	 * 
	 * @param owningTokenList
	 */
	public Token(ITokenList owningTokenList)
	{
		this._owningTokenList = owningTokenList;

		if (owningTokenList != null)
		{
			this._language = owningTokenList.getLanguage();
		}
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#getCategory()
	 */
	public String getCategory()
	{
		String result = this._category;

		if (result == null || result.length() == 0)
		{
			result = defaultTokenCategory;
		}

		return result;
	}

	/**
	 * setCategory
	 * 
	 * @param categoryName
	 */
	public void setCategory(String categoryName)
	{
		if (this._sealed == false)
		{
			this._category = categoryName;
		}
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#getCategoryIndex()
	 */
	public int getCategoryIndex()
	{
		return this._categoryIndex;
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#setCategoryIndex(int)
	 */
	public void setCategoryIndex(int value)
	{
		if (this._sealed == false)
		{
			this._categoryIndex = value;
		}
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#getIndex()
	 */
	public int getIndex()
	{
		return this._index;
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#setIndex(int)
	 */
	public void setIndex(int index)
	{
		if (this._sealed == false)
		{
			this._index = index;
		}
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#getLanguage()
	 */
	public String getLanguage()
	{
		return this._language;
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#getLexerGroup()
	 */
	public String getLexerGroup()
	{
		String result = this._lexerGroup;

		if (result == null || result.length() == 0)
		{
			result = defaultLexerGroup;
		}

		return result;
	}

	/**
	 * setLexerGroup
	 * 
	 * @param lexerGroupName
	 */
	public void setLexerGroup(String lexerGroupName)
	{
		if (this._sealed == false)
		{
			this._lexerGroup = lexerGroupName;
		}
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#getLexerGroupIndex()
	 */
	public int getLexerGroupIndex()
	{
		return this._lexerGroupIndex;
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#setLexerGroupIndex(int)
	 */
	public void setLexerGroupIndex(int value)
	{
		if (this._sealed == false)
		{
			this._lexerGroupIndex = value;
		}
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#getNewLexerGroup()
	 */
	public String getNewLexerGroup()
	{
		String result = this._newLexerGroup;

		if (result == null || result.length() == 0)
		{
			result = this.getLexerGroup();
		}

		return result;
	}

	/**
	 * setNewLexerGroup
	 * 
	 * @param newLexerGroupName
	 */
	public void setNewLexerGroup(String newLexerGroupName)
	{
		if (this._sealed == false)
		{
			this._newLexerGroup = newLexerGroupName;
		}
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#getNewLexerGroupIndex()
	 */
	public int getNewLexerGroupIndex()
	{
		return this._newLexerGroupIndex;
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#setNewLexerGroupIndex(int)
	 */
	public void setNewLexerGroupIndex(int value)
	{
		if (this._sealed == false)
		{
			this._newLexerGroupIndex = value;
		}
	}

	/**
	 * getOwningTokenList
	 * 
	 * @return The token list that owns this token
	 */
	public ITokenList getOwningTokenList()
	{
		return this._owningTokenList;
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#getType()
	 */
	public String getType()
	{
		return this._type;
	}

	/**
	 * setType
	 * 
	 * @param typeName
	 */
	public void setType(String typeName)
	{
		if (this._sealed == false)
		{
			this._type = typeName;

		}
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#getTypeIndex()
	 */
	public int getTypeIndex()
	{
		return this._typeIndex;
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#setTypeIndex(int)
	 */
	public void setTypeIndex(int value)
	{
		if (this._sealed == false)
		{
			this._typeIndex = value;
		}
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#isSealed()
	 */
	public boolean isSealed()
	{
		return this._sealed;
	}

	/**
	 * @see com.aptana.ide.lexer.IToken#seal()
	 */
	public void seal()
	{
		this._sealed = true;
	}

	/**
	 * Returns either the set display name or a created display name take from the type value if the display name has
	 * not been set.
	 * 
	 * @see com.aptana.ide.lexer.IToken#getDisplayName()
	 */
	public String getDisplayName()
	{
		if (this._displayName == null)
		{
			if (this._type != null)
			{
				String name = this._type.toLowerCase();
				StringBuffer sb = new StringBuffer(""); //$NON-NLS-1$
				boolean toUpper = true;

				for (int i = 0; i < name.length(); i++)
				{
					char c = name.charAt(i);

					if (c == '_')
					{
						toUpper = true;
						sb.append(' ');
					}
					else
					{
						if (toUpper)
						{
							// add uppercase version of current letter
							sb.append(Character.toUpperCase(c));

							// reset flag
							toUpper = false;
						}
						else
						{
							// add current letter
							sb.append(c);
						}
					}
				}
				return sb.toString();
			}
		}
		return this._displayName;
	}

	/**
	 * Sets the display name to the parameter value if it is not null, ignores it otherwise.
	 * 
	 * @see com.aptana.ide.lexer.IToken#setDisplayName(java.lang.String)
	 */
	public void setDisplayName(String displayName)
	{
		if (this._sealed == false && displayName != null)
		{
			this._displayName = displayName;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("(").append(this._lexerGroup).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("[").append(this._language).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append(this._category).append(":").append(this._type); //$NON-NLS-1$

		return sb.toString();
	}
}
