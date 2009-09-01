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
package com.aptana.ide.lexer.matcher;

import java.util.HashMap;
import java.util.Map;

import com.aptana.ide.lexer.ICodeBasedTokenList;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.lexer.TokenList;

/**
 * @author Kevin Lindsey
 * @author Kevin Sawicki
 */
public class MatcherTokenList extends TokenList implements ICodeBasedTokenList
{
	private Map<String,OrMatcher> _matchersByName = new HashMap<String,OrMatcher>();
	private OrMatcher _currentMatcher;
	private int _lastMatchedTokenIndex;

	/**
	 * MatcherTokenList
	 */
	public MatcherTokenList()
	{
		super();
	}

	/**
	 * MatcherTokenList
	 * 
	 * @param language
	 */
	public MatcherTokenList(String language)
	{
		super(language);
	}

	/**
	 * addMatcher
	 * 
	 * @param matcher
	 * @param group
	 */
	public void addMatcherToGroup(ITextMatcher matcher, String group)
	{
		OrMatcher matchers = null;
		
		if (this._matchersByName.containsKey(group))
		{
			matchers = this._matchersByName.get(group);
		}
		else
		{
			matchers = new OrMatcher();
			
			this._matchersByName.put(group, matchers);
		}
		
		matchers.appendChild(matcher);
	}

	/**
	 * @see com.aptana.ide.lexer.ICodeBasedTokenList#find(char[], int, int)
	 */
	public Range find(char[] source, int startingPosition, int eofOffset)
	{
		Range result = Range.Empty;
		
		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.ICodeBasedTokenList#getLastMatchedTokenIndex()
	 */
	public int getLastMatchedTokenIndex()
	{
		return this._lastMatchedTokenIndex;
	}

	/**
	 * @see com.aptana.ide.lexer.ICodeBasedTokenList#match(char[], int, int)
	 */
	public int match(char[] source, int startingPosition, int eofOffset)
	{
		int result = -1;
		
		if (startingPosition < eofOffset && this._currentMatcher != null)
		{
			result = this._currentMatcher.match(source, startingPosition, eofOffset);
			
			if (result != -1)
			{
				IToken matchedToken = this._currentMatcher.getMatchedToken();
				
				if (matchedToken != null)
				{
					this._lastMatchedTokenIndex = matchedToken.getIndex();
				}
				else
				{
					result = -1;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @see com.aptana.ide.lexer.TokenList#seal()
	 */
	public void seal() throws LexerException
	{
		super.seal();
		
		// find first characters for each group
		int groupCount = this.getGroupCount();
		
		for (int i = 0; i < groupCount; i++)
		{
			String group = this.getGroup(i);
			
			if (this._matchersByName.containsKey(group))
			{
				OrMatcher or = this._matchersByName.get(group);
				
				or.buildFirstCharacterMap();
			}
		}
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#setCurrentGroup(java.lang.String)
	 */
	public void setCurrentGroup(String groupName) throws LexerException
	{
		super.setCurrentGroup(groupName);
		
		OrMatcher newGroup = this._matchersByName.get(groupName);

		if (newGroup == null)
		{
			throw new LexerException(Messages.MatcherTokenList_Unrecognzied_Group_Name + groupName, null);
		}

		this._currentMatcher = newGroup;
	}

	/**
	 * @see com.aptana.ide.lexer.ITokenList#setCurrentGroup(int)
	 */
	public void setCurrentGroup(int index)
	{
		super.setCurrentGroup(index);

		this._currentMatcher = this._matchersByName.get(this.getCurrentGroup());
	}

	/**
	 * getCurrentMatcher
	 *
	 * @return IMatcher
	 */
	public OrMatcher getCurrentMatcher()
	{
		return this._currentMatcher;
	}
	
	/**
	 * @see com.aptana.ide.lexer.ITokenList#hasGroup(java.lang.String)
	 */
	public boolean hasGroup(String groupName)
	{
		return this._matchersByName.containsKey(groupName);
	}
}
