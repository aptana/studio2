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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kevin Lindsey
 */
public class TokenList implements ITokenList
{
	private static final String[] NO_STRINGS = new String[0];
	
	private String _language;
	private List<IToken> _tokens;
	private int[] _ignoreSet;
	private String _currentGroupName;
	private IEnumerationMap _categories;
	private IEnumerationMap _types;
	private Map<String,Integer> _groups;
	private Map<Integer,String> _groupByIndex;
	private boolean _isSealed;

	/**
	 * TokenList
	 * 
	 * @param language
	 */
	public TokenList(String language)
	{
		this();

		this._language = language;
		this._currentGroupName = ""; //$NON-NLS-1$
	}

	/**
	 * TokenList
	 */
	public TokenList()
	{
		this._tokens = new ArrayList<IToken>();
		this._groups = new HashMap<String,Integer>();
		this._groupByIndex = new HashMap<Integer,String>();
	}

	/**
	 * getCategoryMap
	 *
	 * @return IEnumerationMap or null
	 */
	public IEnumerationMap getCategoryMap()
	{
		return this._categories;
	}
	
	/**
	 * setCategoryMap
	 *
	 * @param map
	 */
	public void setCategoryMap(IEnumerationMap map)
	{
		this._categories = map;
	}
	
	/**
	 * getTypeMap
	 *
	 * @return IEnumerationMap or null
	 */
	public IEnumerationMap getTypeMap()
	{
		return this._types;
	}
	
	/**
	 * setTypeMap
	 *
	 * @param map
	 */
	public void setTypeMap(IEnumerationMap map)
	{
		this._types = map;
	}

	/**
	 * getTokenCategoriesByName
	 * 
	 * @return String[]
	 */
	public String[] getTokenCategoriesByName()
	{
		String[] result = NO_STRINGS;
		
		if (this._categories != null)
		{
			result = this._categories.getNames();
		}
		
		return result;
	}

	/**
	 * getTokenTypesByName
	 * 
	 * @return String[]
	 */
	public String[] getTokenTypesByName()
	{
		String[] result = NO_STRINGS;
		
		if (this._types != null)
		{
			result = this._types.getNames();
		}
		
		return result;
	}

	/**
	 * Get the lexer group for the associated index
	 * 
	 * @param index
	 *            The index of the lexer group to look up
	 * @return The name of the lexer group at the specified index
	 */
	public String getGroup(int index)
	{
		String result = ""; //$NON-NLS-1$

		if (this._groupByIndex.containsKey(index))
		{
			result = this._groupByIndex.get(index);
		}

		return result;
	}

	/**
	 * Get the number of lexer groups used in this list
	 * 
	 * @return The lexer group count
	 */
	protected int getGroupCount()
	{
		return this._groups.size();
	}

	/**
	 * getGroupIndex
	 * 
	 * @param groupName
	 * @return int
	 */
	protected int getGroupIndex(String groupName)
	{
		int result = -1;

		if (this._groups.containsKey(groupName))
		{
			result = this._groups.get(groupName);
		}

		return result;
	}

	/**
	 * setGroupIndex
	 * 
	 * @param groupName
	 * @param index
	 */
	protected void setGroupIndex(String groupName, int index)
	{
		this._groups.put(groupName, index);
		this._groupByIndex.put(index, groupName);
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#seal()
	 */
	public void seal() throws LexerException
	{
		this.setSealed();

		// seal all tokens
		for (int i = 0; i < this._tokens.size(); i++)
		{
			IToken token = this.get(i);

			token.seal();
		}
	}

	/**
	 * setSealed
	 */
	protected void setSealed()
	{
		this._isSealed = true;
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#get(int)
	 */
	public IToken get(int index)
	{
		return this._tokens.get(index);
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#getCurrentGroup()
	 */
	public String getCurrentGroup()
	{
		return this._currentGroupName;
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#setCurrentGroup(java.lang.String)
	 */
	public void setCurrentGroup(String groupName) throws LexerException
	{
		this._currentGroupName = groupName;
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#setCurrentGroup(int)
	 */
	public void setCurrentGroup(int index)
	{
		String groupName = this._groupByIndex.get(index);
		
		if (groupName != null)
		{
			this._currentGroupName = groupName;
		}
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#getGroupNames()
	 */
	public String[] getGroupNames()
	{
		Set<String> keys = this._groups.keySet();

		return keys.toArray(new String[keys.size()]);
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#getIgnoreSet()
	 */
	public int[] getIgnoreSet()
	{
		return this._ignoreSet;
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#setIgnoreSet(int[])
	 */
	public void setIgnoreSet(int[] set)
	{
		if (set != null)
		{
			Arrays.sort(set);
		}

		this._ignoreSet = set;
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#getLanguage()
	 */
	public String getLanguage()
	{
		return this._language;
	}

	/**
	 * Set this list's language type
	 * 
	 * @param language
	 *            The language type this list represents
	 */
	protected void setLanguage(String language)
	{
		this._language = language;
	}

	/**
	 * internalAdd
	 * 
	 * @param token
	 */
	protected void internalAdd(IToken token)
	{
		this._tokens.add(token);
	}

	/**
	 * add
	 * 
	 * @param category
	 * @param type
	 * @param displayName
	 * @return token index
	 */
	public int add(String category, String type, String displayName)
	{
		IToken token = this.createToken();

		token.setCategory(category);
		token.setType(type);
		token.setDisplayName(displayName);

		return this.add(token);
	}

	/**
	 * add
	 * 
	 * @param category
	 * @param type
	 * @param displayName
	 * @param group
	 * @return token index
	 */
	public int add(String category, String type, String displayName, String group)
	{
		IToken token = this.createToken();

		token.setCategory(category);
		token.setType(type);
		token.setDisplayName(displayName);
		token.setLexerGroup(group);
		token.setNewLexerGroup(group);

		return this.add(token);
	}

	/**
	 * add
	 * 
	 * @param category
	 * @param type
	 * @param displayName
	 * @param group
	 * @param newGroup
	 * @return token index
	 */
	public int add(String category, String type, String displayName, String group, String newGroup)
	{
		IToken token = this.createToken();

		token.setCategory(category);
		token.setType(type);
		token.setDisplayName(displayName);
		token.setLexerGroup(group);
		token.setNewLexerGroup(newGroup);

		return this.add(token);
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#add(com.aptana.ide.lexer.IToken)
	 */
	public int add(IToken token)
	{
		String category = token.getCategory();
		String type = token.getType();
		String lexerGroup = token.getLexerGroup();
		String newLexerGroup = token.getNewLexerGroup();

		// set token's category index
		int categoryIndex = this._categories.getIntValue(category);
		if (categoryIndex == -1)
		{
			String message = MessageFormat.format(Messages.TokenList_Category_Not_Defined, new Object[] { category });

			throw new IllegalArgumentException(message);
		}
		token.setCategoryIndex(categoryIndex);

		// set token's type index
		int typeIndex = this._types.getIntValue(type);
		if (typeIndex == -1)
		{
			String message = MessageFormat.format(Messages.TokenList_Type_Not_Defined, new Object[] { type });

			throw new IllegalArgumentException(message);
		}
		token.setTypeIndex(typeIndex);

		// determine token's lexer group index
		if (this._groups.containsKey(lexerGroup))
		{
			int groupIndex = this._groups.get(lexerGroup).intValue();

			token.setLexerGroupIndex(groupIndex);
		}
		else
		{
			int groupIndex = this._groups.size();

			this.setGroupIndex(lexerGroup, groupIndex);
			token.setLexerGroupIndex(groupIndex);
		}

		// determine token's new lexer group index
		if (this._groups.containsKey(newLexerGroup))
		{
			int newGroupIndex = this._groups.get(newLexerGroup).intValue();

			token.setNewLexerGroupIndex(newGroupIndex);
		}
		else
		{
			int newGroupIndex = this._groups.size();

			this.setGroupIndex(newLexerGroup, newGroupIndex);
			token.setNewLexerGroupIndex(newGroupIndex);
		}

		// add token to our list
		this.internalAdd(token);

		// set index
		token.setIndex(this._tokens.size() - 1);

		// return new token index
		return token.getIndex();
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#size()
	 */
	public int size()
	{
		return this._tokens.size();
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#isSealed()
	 */
	public boolean isSealed()
	{
		return this._isSealed;
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#createToken()
	 */
	public IToken createToken()
	{
		return new Token(this);
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#hasGroup()
	 */
	public boolean hasGroup(String groupName)
	{
		Set<String> keys = this._groups.keySet();
		return keys.contains(groupName);
	}
}
