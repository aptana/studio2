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
package com.aptana.ide.editor.js.lexing;

import com.aptana.ide.lexer.matcher.AbstractTextMatcher;
import com.aptana.ide.lexer.matcher.ITextMatcher;
import com.aptana.ide.lexer.matcher.MatcherMap;

/**
 * @author Kevin Lindsey
 */
public class RegExpMatcher extends AbstractTextMatcher
{
	/**
	 * RegExpMatcher
	 */
	public RegExpMatcher()
	{
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addChildTypes()
	 */
	public void addChildTypes()
	{
		// no children
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addFirstCharacters(com.aptana.ide.lexer.matcher.MatcherMap,
	 *      com.aptana.ide.lexer.matcher.ITextMatcher)
	 */
	public void addFirstCharacters(MatcherMap map, ITextMatcher target)
	{
		map.addCharacterMatcher('/', target);
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.ITextMatcher#match(char[], int, int)
	 */
	public int match(char[] source, int offset, int eofset)
	{
		int result = offset;

		if (result < eofset && source[result] == '/')
		{
			// advance
			result++;

			while (result < eofset && source[result] != '/')
			{
				char c = source[result];

				if (c == '\\')
				{
					// advance over '\'
					result++;

					// advance over following character
					if (result < eofset)
					{
						result++;
					}
				}
				else if (c == '\r' || c == '\n')
				{
					// don't allow carriage returns or newlines in regexes
					break;
				}
				else
				{
					// advance
					result++;
				}
			}

			if (result < eofset && source[result] == '/')
			{
				// advance
				result++;

				while (result < eofset && Character.isLetter(source[result]))
				{
					// advance
					result++;
				}
			}
			else
			{
				// indicate error
				result = offset;
			}
		}

		if (result != offset)
		{
			if (this.token != null)
			{
				this.setMatchedToken(this.token);
			}
		}
		else
		{
			result = -1;
		}

		return result;
	}
}
