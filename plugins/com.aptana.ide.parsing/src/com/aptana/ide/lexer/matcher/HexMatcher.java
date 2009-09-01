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

/**
 * @author Kevin Lindsey
 *
 */
public class HexMatcher extends AbstractCharacterMatcher
{
	/**
	 * HexMatcher
	 */
	public HexMatcher()
	{
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addFirstCharacters(com.aptana.ide.lexer.matcher.MatcherMap, com.aptana.ide.lexer.matcher.ITextMatcher)
	 */
	public void addFirstCharacters(MatcherMap map, ITextMatcher target)
	{
		if (this.getNegate())
		{
			map.addNegatedCharacterMatcher('a', target);
			map.addNegatedCharacterMatcher('b', target);
			map.addNegatedCharacterMatcher('c', target);
			map.addNegatedCharacterMatcher('d', target);
			map.addNegatedCharacterMatcher('e', target);
			map.addNegatedCharacterMatcher('f', target);
			map.addNegatedCharacterMatcher('A', target);
			map.addNegatedCharacterMatcher('B', target);
			map.addNegatedCharacterMatcher('C', target);
			map.addNegatedCharacterMatcher('D', target);
			map.addNegatedCharacterMatcher('E', target);
			map.addNegatedCharacterMatcher('F', target);
			map.addNegatedCharacterMatcher('0', target);
			map.addNegatedCharacterMatcher('1', target);
			map.addNegatedCharacterMatcher('2', target);
			map.addNegatedCharacterMatcher('3', target);
			map.addNegatedCharacterMatcher('4', target);
			map.addNegatedCharacterMatcher('5', target);
			map.addNegatedCharacterMatcher('6', target);
			map.addNegatedCharacterMatcher('7', target);
			map.addNegatedCharacterMatcher('8', target);
			map.addNegatedCharacterMatcher('9', target);
		}
		else
		{
			map.addCharacterMatcher('a', target);
			map.addCharacterMatcher('b', target);
			map.addCharacterMatcher('c', target);
			map.addCharacterMatcher('d', target);
			map.addCharacterMatcher('e', target);
			map.addCharacterMatcher('f', target);
			map.addCharacterMatcher('A', target);
			map.addCharacterMatcher('B', target);
			map.addCharacterMatcher('C', target);
			map.addCharacterMatcher('D', target);
			map.addCharacterMatcher('E', target);
			map.addCharacterMatcher('F', target);
			map.addCharacterMatcher('0', target);
			map.addCharacterMatcher('1', target);
			map.addCharacterMatcher('2', target);
			map.addCharacterMatcher('3', target);
			map.addCharacterMatcher('4', target);
			map.addCharacterMatcher('5', target);
			map.addCharacterMatcher('6', target);
			map.addCharacterMatcher('7', target);
			map.addCharacterMatcher('8', target);
			map.addCharacterMatcher('9', target);
		}
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractCharacterMatcher#matchCharacter(char)
	 */
	protected boolean matchCharacter(char c)
	{
		boolean result;

		switch (c)
		{
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				result = true;
				break;

			default:
				result = false;
		}

		return result;
	}
}
