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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Kevin Lindsey
 */
public class CharacterClassMatcher extends AbstractCharacterMatcher
{
	// NOTE: We may want to use an array if the number characters is typically below 10
	private Set<Character> _characters;

	/**
	 * CharacterClassMatcher
	 */
	public CharacterClassMatcher()
	{
	}

	/**
	 * CharacterClassMatcher
	 * 
	 * @param characters
	 */
	public CharacterClassMatcher(String characters)
	{
		this(characters.toCharArray());
	}

	/**
	 * CharacterClassMatcher
	 * 
	 * @param characters
	 */
	public CharacterClassMatcher(char[] characters)
	{
		this.addCharacters(characters);
	}

	/**
	 * addCharacter
	 * 
	 * @param character
	 */
	public void addCharacter(char character)
	{
		// make sure we have an list
		if (this._characters == null)
		{
			this._characters = new HashSet<Character>();
		}
		
		this._characters.add(character);
	}

	/**
	 * addCharacters
	 * 
	 * @param characters
	 */
	public void addCharacters(char[] characters)
	{
		for (int i = 0; i < characters.length; i++)
		{
			char c = characters[i];

			if (c == '\\' && i < characters.length - 1)
			{
				// advance
				i++;

				char c2 = characters[i];

				switch (c2)
				{
					case 'f':
						c = '\f';
						break;

					case 'n':
						c = '\n';
						break;

					case 'r':
						c = '\r';
						break;

					case 't':
						c = '\t';
						break;

					case 'v':
						c = '\u000B';
						break;

					default:
						c = c2;
						break;
				}
			}

			this.addCharacter(c);
		}
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addFirstCharacters(com.aptana.ide.lexer.matcher.MatcherMap,
	 *      com.aptana.ide.lexer.matcher.ITextMatcher)
	 */
	public void addFirstCharacters(MatcherMap map, ITextMatcher target)
	{
		if (this._characters != null)
		{
			Iterator<Character> characters = this._characters.iterator();
			
			while (characters.hasNext())
			{
				char c = characters.next();

				if (this.getNegate())
				{
					map.addNegatedCharacterMatcher(c, target);
				}
				else
				{
					map.addCharacterMatcher(c, target);
				}
			}
		}
	}

	/**
	 * @see com.aptana.xml.NodeBase#appendText(java.lang.String)
	 */
	public void appendText(String text)
	{
		super.appendText(text);

		this.addCharacters(text.toCharArray());
	}

	/**
	 * getCharacters
	 * 
	 * @return
	 */
	public char[] getCharacters()
	{
		char[] result = new char[0];

		if (this._characters != null)
		{
			result = new char[this._characters.size()];

			Iterator<Character> characters = this._characters.iterator();
			int i = 0;
			
			while (characters.hasNext())
			{
				result[i++] = characters.next();
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractCharacterMatcher#matchCharacter(char)
	 */
	protected boolean matchCharacter(char c)
	{
		boolean result = false;

		if (this._characters != null)
		{
			result = this._characters.contains(c);
		}

		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("["); //$NON-NLS-1$
		
		// get sorted list of character
		char[] chars = this.getCharacters();
		Arrays.sort(chars);

		buffer.append(chars);

		buffer.append("]"); //$NON-NLS-1$

		return buffer.toString();
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#validateLocal()
	 */
	protected void validateLocal()
	{
		super.validateLocal();

		if (this._characters == null || this._characters.size() == 0)
		{
			this.getDocument().sendError(Messages.CharacterClassMatcher_No_Text_Content, this);
		}
	}
}
