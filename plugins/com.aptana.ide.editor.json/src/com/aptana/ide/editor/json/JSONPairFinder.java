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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.editor.json;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;

import com.aptana.ide.editor.json.lexing.JSONTokenTypes;
import com.aptana.ide.editor.json.parsing.JSONMimeType;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.AbstractPairFinder;
import com.aptana.ide.editors.unified.PairMatch;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.parsing.IParseState;

/**
 * @author Kevin Lindsey
 */
public class JSONPairFinder extends AbstractPairFinder
{
	/**
	 * PairMatch
	 * 
	 * @param offset
	 * @param parseState
	 * @param cursorLexeme
	 * @param loopCount
	 * @return PairMatch
	 */
	public PairMatch findPairMatch(int offset, IParseState parseState, Lexeme cursorLexeme, int loopCount)
	{
		LexemeList lexemes = parseState.getLexemeList();
		PairMatch result = null;
		
		while (loopCount > 0 && cursorLexeme != null)
		{
			int index = lexemes.getLexemeIndex(cursorLexeme);
			Lexeme matchingLexeme = null;
			
			switch (cursorLexeme.typeIndex)
			{
				case JSONTokenTypes.LCURLY:
					matchingLexeme = UnifiedEditor.findBalancingLexeme(
						lexemes,
						index,
						JSONMimeType.MimeType,
						cursorLexeme.typeIndex,
						JSONTokenTypes.RCURLY,
						1
					);
					break;
			
				case JSONTokenTypes.RCURLY:
					matchingLexeme = UnifiedEditor.findBalancingLexeme(
						lexemes,
						index,
						JSONMimeType.MimeType,
						cursorLexeme.typeIndex,
						JSONTokenTypes.LCURLY,
						-1
					);
					break;
					
				case JSONTokenTypes.LBRACKET:
					matchingLexeme = UnifiedEditor.findBalancingLexeme(
						lexemes,
						index,
						JSONMimeType.MimeType,
						cursorLexeme.typeIndex,
						JSONTokenTypes.RBRACKET,
						1
					);
					break;
					
				case JSONTokenTypes.RBRACKET:
					matchingLexeme = UnifiedEditor.findBalancingLexeme(
						lexemes,
						index,
						JSONMimeType.MimeType,
						cursorLexeme.typeIndex,
						JSONTokenTypes.LBRACKET,
						-1
					);
					break;
					
				case JSONTokenTypes.REFERENCE:
				case JSONTokenTypes.PROPERTY:
				case JSONTokenTypes.STRING:
					if (cursorLexeme.getCategoryIndex() != TokenCategories.ERROR)
					{
						int start = cursorLexeme.getStartingOffset();
						int end = cursorLexeme.getEndingOffset();
						
						if (offset - 1 <= start && start <= offset || offset - 1 <= end && end <= offset)
						{
							result = new PairMatch();
	
							result.beginStart = start;
							result.beginEnd = result.beginStart + 1;
							result.endStart = end - 1;
							result.endEnd = result.endStart + 1;
	
							loopCount = 0;
						}
					}
					break;
					
				default:
					break;
			}
			
			if (matchingLexeme != null)
			{
				result = new PairMatch();

				result.beginStart = cursorLexeme.getStartingOffset();
				result.beginEnd = cursorLexeme.getEndingOffset();
				result.endStart = matchingLexeme.getStartingOffset();
				result.endEnd = matchingLexeme.getEndingOffset();

				if (result.beginEnd == result.endStart || result.endEnd == result.beginStart)
				{
					// favor selecting non-touching pairs
					loopCount--;
					
					if (loopCount > 0 && offset > 0)
					{
						cursorLexeme = lexemes.getLexemeFromOffset(offset - 1);
					}
					else
					{
						// break out of loop
						loopCount = 0;
					}
				}
				else
				{
					// break out of loop
					loopCount = 0;
				}
			}
			else
			{
				loopCount--;

				if (loopCount > 0 && offset > 0)
				{
					cursorLexeme = lexemes.getLexemeFromOffset(offset - 1);
				}
				else
				{
					// break out of loop
					loopCount = 0;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.IPairFinder#getPairFinderColor()
	 */
	public Color getPairFinderColor()
	{
		return UnifiedColorManager.getInstance().getColor(
			PreferenceConverter.getColor(
				Activator.getDefault().getPreferenceStore(),
				IPreferenceConstants.PAIR_MATCHING_COLOR
			)
		);
	}

	private String getDisplayPreference()
	{
		return Activator.getDefault().getPreferenceStore().getString(IPreferenceConstants.SHOW_PAIR_MATCHES);
	}

	/**
	 * @see com.aptana.ide.editors.unified.AbstractPairFinder#displayOnlyMatch()
	 */
	public boolean displayOnlyMatch()
	{
		return getDisplayPreference().equals(IPreferenceConstants.MATCHING);
	}

	/**
	 * @see com.aptana.ide.editors.unified.AbstractPairFinder#doNotDisplay()
	 */
	public boolean doNotDisplay()
	{
		return getDisplayPreference().equals(IPreferenceConstants.NONE);
	}
}
