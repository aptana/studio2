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

import java.util.Arrays;

import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.ITokenList;

/**
 * @author Kevin Lindsey
 */
public class QuotedStringMatcher extends AbstractTextMatcher
{
	private char[] _quotes;
	private boolean _escapeCharacters;
	private boolean _multiLine;
	private IToken _errorToken;
	
	/**
	 * QuotedStringMatcher
	 */
	public QuotedStringMatcher()
	{
		this("\""); //$NON-NLS-1$
	}

	/**
	 * QuotedStringMatcher
	 * 
	 * @param quotes
	 */
	public QuotedStringMatcher(String quotes)
	{
		this.appendText(quotes);
		this._escapeCharacters = true;
		this._multiLine = false;
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addChildTypes()
	 */
	public void addChildTypes()
	{
		// no children
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addFirstCharacters(com.aptana.ide.lexer.matcher.MatcherMap, com.aptana.ide.lexer.matcher.ITextMatcher)
	 */
	public void addFirstCharacters(MatcherMap map, ITextMatcher target)
	{
		char[] quotes = this.getCharacters();
		
		if (quotes != null)
		{
			for (int i = 0; i < quotes.length; i++)
			{
				map.addCharacterMatcher(quotes[i], target);
			}
		}
	}
	
	/**
	 * @see com.aptana.xml.NodeBase#appendText(java.lang.String)
	 */
	public void appendText(String text)
	{
		if (text != null && text.length() > 0)
		{
			this._quotes = text.toCharArray();
			
			Arrays.sort(this._quotes);
		}
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#createToken(com.aptana.ide.lexer.ITokenList)
	 */
	protected void createToken(ITokenList tokenList)
	{
		super.createToken(tokenList);
		
		String type = this.getType();

		if (type != null && type.length() > 0)
		{
			String group = this.getGroup();
			String category = "ERROR"; //$NON-NLS-1$
			String newGroup = this.getSwitchTo();

			// create lexer token
			IToken token = tokenList.createToken();

			// set group, category, type, and new lexer group
			token.setLexerGroup(group);
			token.setCategory(category);
			token.setType(type);
			token.setNewLexerGroup(newGroup);

			// add to token list
			tokenList.add(token);

			// set token as matcher's return value
			this._errorToken = token;
		}
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#match(char[], int, int)
	 */
	public int match(char[] source, int offset, int eofset)
	{
		int result = offset;
		
		if (result < eofset)
		{
			char[] quotes = this.getCharacters();
			int index = Arrays.binarySearch(quotes, source[result]);
			
			if (index >= 0)
			{
				// get matching quote
				char quote = quotes[index];
				
				switch (quote)
				{
					case '(':
						quote = ')';
						break;
						
					case '{':
						quote = '}';
						break;
						
					case '[':
						quote = ']';
						break;
						
					case '<':
						quote = '>';
						break;
						
					default:
						break;
				}
				
				// advance
				result++;
				
				while (result < eofset)
				{
					char c = source[result];
					
					if (source[result] == quote)
					{
						// advance
						result++;
						
						break;
					}
					else if (c == '\\' && this._escapeCharacters)
					{
						// advance over '\'
						result++;
						
						// advance over following character
						if (result < eofset)
						{
							c = source[result];
							
							// advance
							result++;
							
							if (c == '\r')
							{
								if (result < eofset)
								{
									c = source[result];
									
									if (c == '\n')
									{
										// advance
										result++;
									}
								}
							}
						}
					}
					else if (this._multiLine == false && (c == '\r' || c == '\n'))
					{
						break;
					}
					else
					{
						// advance
						result++;
					}
				}
			}
		}
		
		if (result != offset)
		{
			IToken token;
			
			if (result - offset >= 2 && source[result - 1] == source[offset])
			{
				token = this.token;
			}
			else
			{
				token = this._errorToken;
			}
			
			this.accept(source, offset, result, token);
		}
		else
		{
			result = -1;
		}
		
		return result;
	}

	/**
	 * getCharacters
	 *
	 * @return char[]
	 */
	private char[] getCharacters()
	{
		if (this._quotes == null)
		{
			this._quotes = this.getText().toCharArray();
		}
		
		return this._quotes;
	}
	
	/**
	 * setEscapeCharacters
	 *
	 * @param flag
	 */
	public void setEscapeCharacters(boolean flag)
	{
		this._escapeCharacters = flag;
	}
	
	/**
	 * setMultiLine
	 *
	 * @param flag
	 */
	public void setMultiLine(boolean flag)
	{
		this._multiLine = flag;
	}
}
