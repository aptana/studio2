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
package com.aptana.ide.editor.css.contentassist;

import java.util.Arrays;

import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * @author Robin Debreuil
 */
public final class CSSUtils
{

	/**
	 * Private constructor for utility class
	 */
	private CSSUtils()
	{

	}

	/**
	 * Returns the "location" we are currently in. What this means in the end is that it helps us
	 * figure out which of three states we are in. This also sets the current name hash (which
	 * should be moved to CSSOffsetMapper eventually)
	 * 
	 * @param offset
	 *            The current offset
	 * @param lexemeList
	 * @return One of the location enumerations
	 */
	public static String getLocation(int offset, LexemeList lexemeList)
	{

		if (offset == 0)
		{
			return CSSContentAssistProcessor.OUTSIDE_RULE;
		}

		String location = CSSContentAssistProcessor.OUTSIDE_RULE;
		int position = lexemeList.getLexemeFloorIndex(offset);

		boolean foundColon = false;

		// backtrack over lexemes to find name - we are really just
		// searching for the last OPEN_ELEMENT
		while (position >= 0)
		{

			Lexeme curLexeme = lexemeList.get(position);

			if (!curLexeme.getLanguage().equals(CSSMimeType.MimeType))
			{
				break;
			}

			if (curLexeme.typeIndex == CSSTokenTypes.RCURLY)
			{
				location = CSSContentAssistProcessor.OUTSIDE_RULE;
				break;
			}

			if (curLexeme.typeIndex == CSSTokenTypes.COLON)
			{
				if (curLexeme.offset != offset)
				{
					foundColon = true;
				}
			}

			if (curLexeme.typeIndex == CSSTokenTypes.SEMICOLON || curLexeme.typeIndex == CSSTokenTypes.LCURLY)
			{

				if (offset != curLexeme.offset)
				{
					if (foundColon)
					{
						location = CSSContentAssistProcessor.ARG_ASSIST;
					}
					else
					{
						location = CSSContentAssistProcessor.INSIDE_RULE;
					}

					break;
				}
			}

			position--;
		}

		/*
		 * if (currentHash.size() > 0) { lexemeHash = StringUtils.join(":", (String[]) currentHash
		 * .toArray(new String[currentHash.size()])); // We add a trailing ":" as that indicates
		 * we've finished typing // propertyName if (location.equals(ARG_ASSIST) &&
		 * lexemeHash.indexOf(":") < 0) lexemeHash += ":"; } else lexemeHash = "";
		 */

		return location;
	}

	/**
	 * We don't insert a : if there is already one there
	 * 
	 * @param offset
	 * @param ll
	 * @return boolean
	 */
	public static boolean isColonAlreadyInserted(int offset, LexemeList ll)
	{
		Lexeme sibling = ll.getFloorLexeme(offset);
		Lexeme colon = getNextLexemeOfType(sibling, new int[] { CSSTokenTypes.COLON }, new int[] {
				CSSTokenTypes.SEMICOLON, CSSTokenTypes.RCURLY }, ll);

		return colon != null;
	}

	/**
	 * getNextLexemeOfType
	 * 
	 * @param startLexeme
	 * @param lexemeTypes
	 * @param lexemeList
	 * @return Lexeme
	 */
	public static Lexeme getNextLexemeOfType(Lexeme startLexeme, int[] lexemeTypes, LexemeList lexemeList)
	{
		return getNextLexemeOfType(startLexeme, lexemeTypes, new int[0], lexemeList);
	}

	/**
	 * getNextLexemeOfType
	 * 
	 * @param startLexeme
	 * @param lexemeTypes
	 * @param lexemeTypesToBail
	 * @param lexemeList
	 * @return Lexeme
	 */
	public static Lexeme getNextLexemeOfType(Lexeme startLexeme, int[] lexemeTypes, int[] lexemeTypesToBail,
			LexemeList lexemeList)
	{
		Arrays.sort(lexemeTypes);
		Arrays.sort(lexemeTypesToBail);

		int index = lexemeList.getLexemeIndex(startLexeme);

		for (int i = index; i < lexemeList.size(); i++)
		{
			Lexeme l = lexemeList.get(i);

			if (Arrays.binarySearch(lexemeTypes, l.typeIndex) >= 0)
			{
				return l;
			}

			if (Arrays.binarySearch(lexemeTypesToBail, l.typeIndex) >= 0)
			{
				return null;
			}

		}

		return null;
	}

}
