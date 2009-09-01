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

import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.ITokenList;

/**
 * @author Kevin Lindsey
 */
public class ToDelimiterMatcher extends AbstractTextMatcher
{
	private ITextMatcher _delimiter;
	private boolean _includeDelimiter;
	private boolean _matchEndOfFile;
	private boolean _allowEmpty;
	private IToken _errorToken;

	/**
	 * ToDelimiterMatcher
	 */
	public ToDelimiterMatcher()
	{
		this(null);
	}

	/**
	 * ToDelimiterMatcher
	 * 
	 * @param delimiter
	 */
	public ToDelimiterMatcher(String delimiter)
	{
		this.appendText(delimiter);

		this._includeDelimiter = true;
		this._allowEmpty = true;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addChildTypes()
	 */
	public void addChildTypes()
	{
		this.addChildType(ITextMatcher.class);
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addFirstCharacters(com.aptana.ide.lexer.matcher.MatcherMap, com.aptana.ide.lexer.matcher.ITextMatcher)
	 */
	public void addFirstCharacters(MatcherMap map, ITextMatcher target)
	{
		// any character may precede a delimiter
		map.addUncategorizedMatcher(target);
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#canMatchNothing()
	 */
	public boolean canMatchNothing()
	{
		return this._allowEmpty || this._matchEndOfFile;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.MatcherElement#createChildrenTokens(com.aptana.ide.lexer.ITokenList)
	 */
	protected void createChildrenTokens(ITokenList tokenList)
	{
		// wrap multiple children in an <and> element
		this.wrapChildrenInAndElement();
		
		// process as usual
		super.createChildrenTokens(tokenList);
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#match(char[], int, int)
	 */
	public int match(char[] source, int offset, int eofOffset)
	{
		int result = -1;
		ITextMatcher delimiter = this.getDelimiter();

		if (delimiter != null)
		{
			int i;
			boolean foundDelimiter = false;

			for (i = offset; i < eofOffset; i++)
			{
				int endResult = delimiter.match(source, i, eofOffset);

				if (endResult != -1)
				{
					foundDelimiter = true;
					
					if (this._includeDelimiter)
					{
						result = endResult;
					}
					else
					{
						result = i;
					}

					break;
				}
			}

			// update result to eof offset, if we allow EOF matching
			if (result == -1 && this._matchEndOfFile && i == eofOffset)
			{
				result = eofOffset;
			}

			if (result != -1)
			{
				if (result != offset || this._allowEmpty)
				{
					if (foundDelimiter)
					{
						this.accept(source, offset, result, this.token);
					}
					else
					{
						this.accept(source, offset, result, this._errorToken);
					}
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
	 * getDelimiter
	 * 
	 * @return IMatcher
	 */
	public ITextMatcher getDelimiter()
	{
		if (this._delimiter == null)
		{
			if (this.getChildCount() > 0)
			{
				this._delimiter = (ITextMatcher) this.getChild(0);
			}
			else
			{
				this._delimiter = new StringMatcher(this.getText());
			}
		}

		return this._delimiter;
	}

	/**
	 * getIncludeDelimiter
	 * 
	 * @return boolean
	 */
	public boolean getIncludeDelimiter()
	{
		return this._includeDelimiter;
	}

	/**
	 * getMatchEndOfFile
	 * 
	 * @return boolean
	 */
	public boolean getMatchEndOfFile()
	{
		return this._matchEndOfFile;
	}

	/**
	 * setAllowEmpty
	 * 
	 * @param value
	 */
	public void setAllowEmpty(boolean value)
	{
		this._allowEmpty = value;
	}
	
	/**
	 * setIncludeDelimiter
	 * 
	 * @param value
	 */
	public void setIncludeDelimiter(boolean value)
	{
		this._includeDelimiter = value;
	}

	/**
	 * setMatchEndOfFile
	 * 
	 * @param value
	 */
	public void setMatchEndOfFile(boolean value)
	{
		this._matchEndOfFile = value;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#validateLocal()
	 */
	protected void validateLocal()
	{
		super.validateLocal();
		
		String text = this.getText();
		
		if (this._delimiter == null && (text == null || text.length() == 0))
		{
			this.getDocument().sendError(Messages.ToDelimiterMatcher_No_Text_Or_Child_Matcher, this);
		}
		else if (this.getChildCount() > 1)
		{
			this.getDocument().sendInfo(Messages.ToDelimiterMatcher_Wrapping_Children, this);
		}
	}
}
