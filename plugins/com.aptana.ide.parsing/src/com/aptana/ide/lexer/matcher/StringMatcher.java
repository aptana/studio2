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

import java.util.Locale;

import com.aptana.ide.lexer.matcher.model.UseElement;

/**
 * @author Kevin Lindsey
 */
public class StringMatcher extends AbstractTextMatcher
{
	private char[] _chars;
	private boolean _caseInsensitive;

	/**
	 * StringMatcher
	 */
	public StringMatcher()
	{
		this(null);
	}

	/**
	 * StringMatcher
	 * 
	 * @param text
	 */
	public StringMatcher(String text)
	{
		this.appendText(text);
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addChildTypes()
	 */
	public void addChildTypes()
	{
		this.addChildType(UseElement.class);
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addFirstCharacters(com.aptana.ide.lexer.matcher.MatcherMap,
	 *      com.aptana.ide.lexer.matcher.ITextMatcher)
	 */
	public void addFirstCharacters(MatcherMap map, ITextMatcher target)
	{
		if (this.getChildCount() > 0)
		{
			map.addUncategorizedMatcher(target);
		}
		else
		{
			char[] chars = this.getCharacters();
	
			if (chars.length > 0)
			{
				char c = chars[0];
				
				if (this._caseInsensitive && Character.isLetter(c))
				{
					map.addCharacterMatcher(Character.toLowerCase(c), target);
					map.addCharacterMatcher(Character.toUpperCase(c), target);
				}
				else
				{
					map.addCharacterMatcher(c, target);
				}
			}
		}
	}

	/**
	 * getCaseInsensitive
	 * 
	 * @return boolean
	 */
	public boolean getCaseInsensitive()
	{
		return this._caseInsensitive;
	}

	/**
	 * getCharacters
	 * 
	 * @return char[]
	 */
	private char[] getCharacters()
	{
		if (this.getChildCount() > 0)
		{
			UseElement use = (UseElement) this.getChild(0);
			String text = use.getText();
			
			if (this._caseInsensitive)
			{
				this._chars = text.toLowerCase(Locale.getDefault()).toCharArray();
			}
			else
			{
				this._chars = text.toCharArray();
			}
		}
		else
		{
			if (this._chars == null)
			{
				if (this._caseInsensitive)
				{
					this._chars = this.getText().toLowerCase(Locale.getDefault()).toCharArray();
				}
				else
				{
					this._chars = this.getText().toCharArray();
				}
			}
		}

		return this._chars;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#match(char[], int, int)
	 */
	public int match(char[] source, int offset, int eofset)
	{
		int result = offset;
		char[] chars = this.getCharacters();
		int index = 0;

		while (result < eofset && index < chars.length)
		{
			char c = (this._caseInsensitive) ? Character.toLowerCase(source[result]) : source[result];

			if (c != chars[index])
			{
				break;
			}
			else
			{
				// advance
				result++;
				index++;
			}
		}

		if (index != chars.length || chars.length == 0)
		{
			result = -1;
		}
		else
		{
			this.accept(source, offset, result, this.token);
		}

		return result;
	}

	/**
	 * setCaseInsensitive
	 * 
	 * @param caseInsensitive
	 */
	public void setCaseInsensitive(boolean caseInsensitive)
	{
		this._caseInsensitive = caseInsensitive;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return this.getText();
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#validateLocal()
	 */
	protected void validateLocal()
	{
		super.validateLocal();
		
		String text = this.getText();
		
		if ((text == null || text.length() == 0) && this.getChildCount() == 0)
		{
			this.getDocument().sendError(Messages.StringMatcher_No_Text_Or_Use_Element, this);
		}
		else if (this.getChildCount() > 1)
		{
			this.getDocument().sendWarning(Messages.StringMatcher_Only_Recognize_First_Child, this);
		}
	}
}
