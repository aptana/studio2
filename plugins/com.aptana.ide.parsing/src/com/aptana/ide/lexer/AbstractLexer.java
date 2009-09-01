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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Kevin Lindsey
 */
public abstract class AbstractLexer implements ILexer
{
	private Map<String,ITokenList> _tokensByLanguage;
	private ITokenList _currentTokenList;
	private int _currentGroupIndex;
	private LexemeList _lexemeCache;
	
	public int hitCount;
	public int missCount;

	/**
	 * The source code being lexed
	 */
	protected char[] source;
	
	/**
	 * The current offset that indicates the end of file
	 */
	protected int eofOffset;

	/**
	 * The token index of the last match. This value will be -1 if getNextLexeme did not find a new lexeme
	 */
	protected int lastMatchedTokenIndex;

	/**
	 * Current offset within the source code where the next match will begin
	 */
	protected int currentOffset;

	/**
	 * Create a new instance of Lexer
	 */
	public AbstractLexer()
	{
		this._tokensByLanguage = new HashMap<String,ITokenList>();
		this.setSource(new char[0]);
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#setLexemeCache(com.aptana.ide.lexer.LexemeList)
	 */
	public void setLexemeCache(LexemeList lexemeCache)
	{
		this._lexemeCache = lexemeCache;
	}

	/**
	 * getCachedLexeme
	 * 
	 * @return Lexeme
	 */
	protected Lexeme getCachedLexeme()
	{
		Lexeme result = null;

		if (this._lexemeCache != null)
		{
			// search for lexeme at the current offset in our lexeme cache
			int index = this._lexemeCache.getLexemeIndex(this.currentOffset);

			// the lexeme already exists, if the resulting index is positive
			if (index >= 0)
			{
				// grab the result
				result = this._lexemeCache.get(index);

				// update our current position
				this.currentOffset += result.length;
			}
			else
			{
				// get range
				Range range = this._lexemeCache.getAffectedRegion();

				// make sure we're not in the affected region
				if (range.containsOffset(this.currentOffset) == false)
				{
					// we're aren't in the affected region, so adjust
					// the index to the next item in our cache
					index = -(index + 1);

					// make sure our index is not off the end of the
					// cache list
					if (index < this._lexemeCache.size())
					{
						// get the starting offset of the affected
						// region
						int startingOffset = range.getStartingOffset();

						// get our candidate lexeme from the cache
						Lexeme candidate = this._lexemeCache.get(index);

						// make sure we're either already past the
						// affected region OR that candidate in the
						// cache does not cross through the affected
						// region
						if (this.currentOffset >= range.getEndingOffset()
								|| (this.currentOffset < startingOffset && candidate.getEndingOffset() <= startingOffset))
						{
							result = candidate;

							this.currentOffset = result.getEndingOffset();
						}
					}
				}
			}
		}
		
		if (result == null)
		{
			missCount++;
		}
		else
		{
			hitCount++;
		}

		return result;
	}
	
	/**
	 * @see com.aptana.ide.lexer.ILexer#getCharacterAt(int)
	 */
	public char getCharacterAt(int offset)
	{
		char result = '\0';
		
		if (offset < this.source.length)
		{
			result = this.source[offset];
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#getCurrentOffset()
	 */
	public int getCurrentOffset()
	{
		return this.currentOffset;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#getCurrentTokenList()
	 */
	public ITokenList getCurrentTokenList()
	{
		return this._currentTokenList;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#setCurrentOffset(int)
	 */
	public void setCurrentOffset(int offset)
	{
		this.currentOffset = offset;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#getEOFOffset()
	 */
	public int getEOFOffset()
	{
		return this.eofOffset;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#setEOFOffset(int)
	 */
	public void setEOFOffset(int offset)
	{
		this.eofOffset = offset;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#getGroup()
	 */
	public String getGroup()
	{
		return this._currentTokenList.getCurrentGroup();
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#setGroup(java.lang.String)
	 */
	public void setGroup(String groupName) throws LexerException
	{
		this._currentTokenList.setCurrentGroup(groupName);
		this._currentGroupIndex = -1;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#setIgnoreSet(java.lang.String, int[])
	 */
	public void setIgnoreSet(String language, int[] set)
	{
		ITokenList tokens = this.getTokenList(language);

		tokens.setIgnoreSet(set);
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#getLanguage()
	 */
	public String getLanguage()
	{
		return this._currentTokenList.getLanguage();
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#getLanguages()
	 */
	public String[] getLanguages()
	{
		Set<String> keySet = this._tokensByLanguage.keySet();

		return keySet.toArray(new String[keySet.size()]);
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#setLanguage(java.lang.String)
	 */
	public void setLanguage(String language) throws LexerException
	{
		ITokenList tokenList = this._tokensByLanguage.get(language);
		
		if (tokenList == null)
		{
			throw new LexerException(Messages.Lexer_Unrecognized_Language + language, null);
		}

		this._currentTokenList = tokenList;
		
		// reset stats for this language. We may want to emit the old values to the log
		this.hitCount = 0;
		this.missCount = 0;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#setLanguageAndGroup(java.lang.String, java.lang.String)
	 */
	public void setLanguageAndGroup(String language, String group) throws LexerException
	{
		this.setLanguage(language);
		this.setGroup(group);
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#getSource()
	 */
	public String getSource()
	{
		return new String(this.source);
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#getSourceLength()
	 */
	public int getSourceLength()
	{
		return this.source.length;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#setSource(char[])
	 */
	public void setSource(char[] value)
	{
		this.source = value;
		this.currentOffset = 0;
		this.setEOFOffset(this.source.length);
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#setSource(java.lang.String)
	 */
	public void setSource(String value)
	{
		this.setSource(value.toCharArray());
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#getTokenList(java.lang.String)
	 */
	public ITokenList getTokenList(String language)
	{
		ITokenList result = null;

		if (this._tokensByLanguage.containsKey(language))
		{
			result = this._tokensByLanguage.get(language);
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#isEOS()
	 */
	public boolean isEOS()
	{
		// return this.getSourceLength() == 0 || this.currentOffset >= this._eofOffset;
		return this.currentOffset >= this.eofOffset;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#addLanguage(com.aptana.ide.lexer.ITokenList)
	 */
	public void addLanguage(ITokenList tokens)
	{
		this._tokensByLanguage.put(tokens.getLanguage(), tokens);
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#seal()
	 */
	public void seal() throws LexerException
	{
		String[] languages = this.getLanguages();

		for (int i = 0; i < languages.length; i++)
		{
			String language = languages[i];
			ITokenList tokens = this.getTokenList(language);

			tokens.seal();
		}
	}

	/**
	 * Create a new lexeme. Sub-classes will need to override this method to create their own lexeme sub-classes
	 * 
	 * @param token
	 *            The token class for this lexeme
	 * @param text
	 *            The token's associated text
	 * @param offset
	 *            The token's offset within the source file
	 * @return Returns a newly created lexeme
	 */
	protected Lexeme createLexeme(IToken token, String text, int offset)
	{
		return new Lexeme(token, text, offset);
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#find(java.lang.String)
	 */
	public abstract Range find(String groupName) throws LexerException;

	/**
	 * match
	 * 
	 * @return Returns the position of the last failed or successful match
	 */
	protected abstract int match();

	/**
	 * @see com.aptana.ide.lexer.ILexer#setLexerState(java.lang.String, int)
	 */
	public void setLexerState(String group, int offset) throws LexerException
	{
		this.setGroup(group);
		this.setCurrentOffset(offset);
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#setLexerState(java.lang.String, char[], int, com.aptana.ide.lexer.LexemeList)
	 */
	public void setLexerState(String group, char[] source, int offset, LexemeList cache) throws LexerException
	{
		this.setGroup(group);
		this.setSource(source);
		this.setCurrentOffset(offset);
		this._lexemeCache = cache;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#getNextLexeme()
	 */
	public Lexeme getNextLexeme()
	{
		Lexeme result = null;

		// Start out as if we've found a token we want to ignore
		boolean inIgnoreSet = true;

		// keep advancing until we find a token that is not in our ignore
		// set
		// while (inIgnoreSet && this.isEOS() == false)
		while (inIgnoreSet && this.currentOffset < this.eofOffset)
		{
			result = this.getCachedLexeme();

			if (result != null)
			{
				break;
			}

			// cache our current "start" position
			int start = this.currentOffset;

			// perform a match at the current offset
			int position = this.match();

			// get the index of the token that matched, if any
			int tokenIndex = this.lastMatchedTokenIndex;

			// process token type, if we had match
			if (tokenIndex != -1)
			{
				ITokenList tokenList = this.getCurrentTokenList();
				IToken token = tokenList.get(tokenIndex);
				int[] ignoreSet = tokenList.getIgnoreSet();
				boolean desirableType = true;
				
				// NOTE: These sets should be small (<= 3) and linear search wins over
				// binary search with those sizes. Plus our lists are sorted, so we can
				// stop as soon as we reach values greater than what we're looking for
				if (ignoreSet != null)
				{
					int typeIndex = token.getTypeIndex();
					
					for (int i = 0; i < ignoreSet.length; i++)
					{
						int current = ignoreSet[i];
						
						if (current >= typeIndex)
						{
							desirableType = (current > typeIndex);
							break;
						}
					}
				}

				// See if this token is in our set of tokens to ignore
				if (desirableType)
				{
					// determine text length of this token instance
					int lexemeLength = position - start;

					// grab lexeme text
					String text = new String(this.source, start, lexemeLength);

					// create resulting lexeme
					result = this.createLexeme(token, text, start);

					// flag to exit loop
					inIgnoreSet = false;
				}

				// update current position in the source text
				this.currentOffset = position;

				// switch to new lexer group associated with the matched token
				int groupIndex = token.getNewLexerGroupIndex();
				
				if (groupIndex != this._currentGroupIndex)
				{
					tokenList.setCurrentGroup(groupIndex);
					this._currentGroupIndex = groupIndex;
				}
			}
			else
			{
				inIgnoreSet = false;
			}
		}

		return result;
	}
}
