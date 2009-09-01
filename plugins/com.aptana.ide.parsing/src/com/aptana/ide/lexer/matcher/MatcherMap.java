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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kevin Lindsey
 */
public class MatcherMap
{
	/**
	 * @author Kevin Lindsey
	 */
	private class CharacterClassMap
	{
		private List<ITextMatcher> _matchers;
		private ITextMatcher[] _cache;
		private boolean _sealed;

		/**
		 * CharacterClassMap
		 */
		public CharacterClassMap()
		{
		}

		/**
		 * addAllMatchers
		 *
		 * @param matchers
		 */
		public void addAllMatchers(List<ITextMatcher> matchers)
		{
			if (this._sealed == false && matchers != null)
			{
				this._matchers.addAll(matchers);
			}
		}
		
		/**
		 * addMatcher
		 * 
		 * @param matcher
		 */
		public void addMatcher(ITextMatcher matcher)
		{
			if (this._sealed == false)
			{
				// make sure we have a container for our matchers
				if (this._matchers == null)
				{
					this._matchers = new ArrayList<ITextMatcher>();
				}

				if (this._matchers.contains(matcher) == false)
				{
					this._matchers.add(matcher);

					// clear cache
					this._cache = null;

					_hasBuiltinCharacterClass = true;
				}
			}
		}

		/**
		 * getMatcherList
		 *
		 * @return List or null
		 */
		public List<ITextMatcher> getMatcherList()
		{
			return this._matchers;
		}
		
		/**
		 * getMatchers
		 * 
		 * @return IMatcher[] or null
		 */
		public ITextMatcher[] getMatchers()
		{
			if (this._cache == null && this._matchers != null)
			{
				this._cache = this._matchers.toArray(new ITextMatcher[this._matchers.size()]);
				Arrays.sort(this._cache);
			}

			return this._cache;
		}

		/**
		 * hasMatcher
		 *
		 * @return boolean
		 */
		public boolean hasMatchers()
		{
			return ((this._matchers != null && this._matchers.size() > 0) || (this._cache != null && this._cache.length > 0));
		}
		
		/**
		 * setSealed
		 */
		public void setSealed()
		{
			if (this._sealed == false)
			{
				this._sealed = true;

				// fill cache
				this._cache = null;
				this.getMatchers();

				// release array list
				this._matchers = null;
			}
		}
	}

	private static final ITextMatcher[] NO_MATCHERS = new ITextMatcher[0];

	private Map<Character,List<ITextMatcher>> _map;
	private Map<Character,ITextMatcher[]> _sealedMap;
	private CharacterClassMap _uncategorizedMatchers;
	private boolean _hasBuiltinCharacterClass;
	private boolean _sealed;

	private CharacterClassMap _digitMatchers;
	private CharacterClassMap _letterMatchers;
	private CharacterClassMap _whitespaceMatchers;
	private CharacterClassMap _negatedDigitMatchers;
	private CharacterClassMap _negatedLetterMatchers;
	private CharacterClassMap _negatedWhitespaceMatchers;

	/**
	 * MatcherMap
	 */
	public MatcherMap()
	{
	}

	/**
	 * addMatcher
	 * 
	 * @param c
	 * @param matcher
	 */
	public void addCharacterMatcher(char c, ITextMatcher matcher)
	{
		if (this._sealed == false)
		{
			if (matcher != null)
			{
				// create map for character, if needed
				if (this._map == null)
				{
					this._map = new HashMap<Character,List<ITextMatcher>>();
				}

				// create list for character, if needed
				if (this._map.containsKey(c) == false)
				{
					this._map.put(c, new ArrayList<ITextMatcher>());
				}

				// grab list for character
				List<ITextMatcher> list = this._map.get(c);

				// add this matcher if it's not already in the list
				if (list.contains(matcher) == false)
				{
					list.add(matcher);
				}
			}
		}
	}

	/**
	 * addDigitMatcher
	 * 
	 * @param matcher
	 */
	public void addDigitMatcher(ITextMatcher matcher)
	{
		if (this._sealed == false)
		{
			if (this._digitMatchers == null)
			{
				this._digitMatchers = new CharacterClassMap();
			}

			this._digitMatchers.addMatcher(matcher);
		}
	}

	/**
	 * addLetterMatcher
	 * 
	 * @param matcher
	 */
	public void addLetterMatcher(ITextMatcher matcher)
	{
		if (this._sealed == false)
		{
			if (this._letterMatchers == null)
			{
				this._letterMatchers = new CharacterClassMap();
			}

			this._letterMatchers.addMatcher(matcher);
		}
	}
	
	/**
	 * addNegatedCharacterMatcher
	 *
	 * @param c
	 * @param matcher
	 */
	public void addNegatedCharacterMatcher(char c, ITextMatcher matcher)
	{
		// TODO: not implemented. Setting all matchers as uncategorized
		this.addUncategorizedMatcher(matcher);
	}
	
	/**
	 * addNegatedDigitMatcher
	 * 
	 * @param matcher
	 */
	public void addNegatedDigitMatcher(ITextMatcher matcher)
	{
		if (this._sealed == false)
		{
			if (this._negatedDigitMatchers == null)
			{
				this._negatedDigitMatchers = new CharacterClassMap();
			}

			this._negatedDigitMatchers.addMatcher(matcher);
		}
	}

	/**
	 * addNegatedLetterMatcher
	 * 
	 * @param matcher
	 */
	public void addNegatedLetterMatcher(ITextMatcher matcher)
	{
		if (this._sealed == false)
		{
			if (this._negatedLetterMatchers == null)
			{
				this._negatedLetterMatchers = new CharacterClassMap();
			}

			this._negatedLetterMatchers.addMatcher(matcher);
		}
	}

	/**
	 * addNegatedWhitespaceMatcher
	 * 
	 * @param matcher
	 */
	public void addNegatedWhitespaceMatcher(ITextMatcher matcher)
	{
		if (this._sealed == false)
		{
			if (this._negatedWhitespaceMatchers == null)
			{
				this._negatedWhitespaceMatchers = new CharacterClassMap();
			}

			this._negatedWhitespaceMatchers.addMatcher(matcher);
		}
	}

	/**
	 * addUncategorizedMatcher
	 *
	 * @param matcher
	 */
	public void addUncategorizedMatcher(ITextMatcher matcher)
	{
		if (this._sealed == false)
		{
			if (this._uncategorizedMatchers == null)
			{
				this._uncategorizedMatchers = new CharacterClassMap();
			}
			
			this._uncategorizedMatchers.addMatcher(matcher);
		}
	}

	/**
	 * addWhitespaceMatcher
	 * 
	 * @param matcher
	 */
	public void addWhitespaceMatcher(ITextMatcher matcher)
	{
		if (this._sealed == false)
		{
			if (this._whitespaceMatchers == null)
			{
				this._whitespaceMatchers = new CharacterClassMap();
			}

			this._whitespaceMatchers.addMatcher(matcher);
		}
	}

	/**
	 * appendMatchers
	 *
	 * @param matchers
	 * @param map
	 */
	private void appendToList(boolean isInCharacterClass, CharacterClassMap map, List<ITextMatcher> matchers)
	{
		if (isInCharacterClass && map != null)
		{
			matchers.addAll(map.getMatcherList());
		}
	}

	/**
	 * appendToMap
	 *
	 * @param isInCharacteClass
	 * @param matcher
	 * @param map
	 */
	private void appendToMap(boolean isInCharacteClass, List<ITextMatcher> matcher, CharacterClassMap map)
	{
		if (isInCharacteClass && map != null)
		{
			map.addAllMatchers(matcher);
		}
	}
	
	/**
	 * getMatchers
	 * 
	 * @param c
	 * @return IMatcher[]
	 */
	public ITextMatcher[] getMatchers(char c)
	{
		if (this._sealed == false)
		{
			throw new IllegalStateException(Messages.MatcherMap_Call_SetSeal_Before_GetMatchers);
		}

		ITextMatcher[] result = null;

		if (this._sealedMap != null)
		{
			result = this._sealedMap.get(c);
		}
		
		if (result == null)
		{
			result = NO_MATCHERS;
			
			if (this._hasBuiltinCharacterClass)
			{
				if (this._letterMatchers != null && Character.isLetter(c))
				{
					result = this._letterMatchers.getMatchers();
				}
				else if (this._digitMatchers != null && Character.isDigit(c))
				{
					result = this._digitMatchers.getMatchers();
				}
				else if (this._whitespaceMatchers != null && Character.isWhitespace(c))
				{
					result = this._whitespaceMatchers.getMatchers();
				}
				else if (this._negatedDigitMatchers != null && Character.isDigit(c) == false)
				{
					result = this._negatedDigitMatchers.getMatchers();
				}
				else if (this._negatedLetterMatchers != null && Character.isLetter(c) == false)
				{
					result = this._negatedLetterMatchers.getMatchers();
				}
				else if (this._negatedWhitespaceMatchers != null && Character.isWhitespace(c) == false)
				{
					result = this._negatedWhitespaceMatchers.getMatchers();
				}
				else if (this._uncategorizedMatchers != null)
				{
					result = this._uncategorizedMatchers.getMatchers();
				}
			}
			else if (this._uncategorizedMatchers != null)
			{
				result = this._uncategorizedMatchers.getMatchers();
			}
		}
		
		return result;
	}

	/**
	 * getUncategorizedMatchers
	 *
	 * @return IMatcher[]
	 */
	public ITextMatcher[] getUncategorizedMatchers()
	{
		if (this._sealed == false)
		{
			throw new IllegalStateException(Messages.MatcherMap_Call_SetSeal_Before_GetMatchers);
		}
		
		ITextMatcher[] result = NO_MATCHERS;
		
		if (this._uncategorizedMatchers != null)
		{
			result = this._uncategorizedMatchers.getMatchers();
		}
		
		return result;
	}

	/**
	 * hasUncategorizedMatchers
	 * 
	 * @return Returns true if this map included matchers that couldn't determine their first characters
	 */
	public boolean hasUncategorizedMatchers()
	{
		boolean result = false;
		
		if (this._uncategorizedMatchers != null)
		{
			result = this._uncategorizedMatchers.hasMatchers();
		}
		
		return result;
	}
	
	/**
	 * sealCharacterClass
	 *
	 * @param map
	 */
	private void sealCharacterClass(CharacterClassMap map)
	{
		if (map != null)
		{
			map.setSealed();
		}
	}
	
	/**
	 * setSealed
	 */
	public void setSealed()
	{
		// join all special character classes to our map as needed
		if (this._map != null)
		{
			Set<Character> keys = this._map.keySet();
			Iterator<Character> iter = keys.iterator();
			
			// create new sealed map container
			this._sealedMap = new HashMap<Character,ITextMatcher[]>();
			
			while (iter.hasNext())
			{
				char c = iter.next();
				List<ITextMatcher> matcherList = this._map.get(c);
				
				// add all character classes that include this character
				this.appendToList(Character.isLetter(c), this._letterMatchers, matcherList);
				this.appendToList(Character.isDigit(c), this._digitMatchers, matcherList);
				this.appendToList(Character.isWhitespace(c), this._whitespaceMatchers, matcherList);
				this.appendToList(Character.isLetter(c) == false, this._negatedLetterMatchers, matcherList);
				this.appendToList(Character.isDigit(c) == false, this._negatedDigitMatchers, matcherList);
				this.appendToList(Character.isWhitespace(c) == false, this._negatedWhitespaceMatchers, matcherList);
				
				// add all uncategorized matchers since they could possibly match
				this.appendToList(true, this._uncategorizedMatchers, matcherList);
				
				// create matcher array
				ITextMatcher[] matchers = matcherList.toArray(new ITextMatcher[matcherList.size()]);
				
				// make sure we try matchers in document order
				Arrays.sort(matchers);
				
				// place array into sealed map
				this._sealedMap.put(c, matchers);
			}
			
			// release map
			this._map = null;
		}
		
		// add all uncategorized matchers to characters classes since they could possibly match
		if (this.hasUncategorizedMatchers())
		{
			List<ITextMatcher> matcherList = this._uncategorizedMatchers.getMatcherList();
			
			this.appendToMap(true, matcherList, this._letterMatchers);
			this.appendToMap(true, matcherList, this._digitMatchers);
			this.appendToMap(true, matcherList, this._whitespaceMatchers);
			this.appendToMap(true, matcherList, this._negatedLetterMatchers);
			this.appendToMap(true, matcherList, this._negatedDigitMatchers);
			this.appendToMap(true, matcherList, this._negatedWhitespaceMatchers);
		}
		
		// seal uncategorized matchers
		this.sealCharacterClass(this._uncategorizedMatchers);
		
		// seal all character classes
		this.sealCharacterClass(this._letterMatchers);
		this.sealCharacterClass(this._digitMatchers);
		this.sealCharacterClass(this._whitespaceMatchers);
		this.sealCharacterClass(this._negatedDigitMatchers);
		this.sealCharacterClass(this._negatedLetterMatchers);
		this.sealCharacterClass(this._negatedWhitespaceMatchers);
		
		// set sealed flag
		this._sealed = true;
	}
}
