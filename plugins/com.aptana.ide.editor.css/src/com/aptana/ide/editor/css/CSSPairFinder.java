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
package com.aptana.ide.editor.css;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;

import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
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
 * @author Pavel Petrochenko
 */
public class CSSPairFinder extends AbstractPairFinder
{

	/**
	 * @param offset
	 * @param parseState
	 * @param cursorLexeme
	 * @param loopCount
	 * @return PairMatch
	 */
	public PairMatch findPairMatch(int offset, IParseState parseState, Lexeme cursorLexeme, int loopCount)
	{
		LexemeList lexemeList = parseState.getLexemeList();
		PairMatch result = null;
		
		while (loopCount > 0 && cursorLexeme != null)
		{
			int index = lexemeList.getLexemeIndex(cursorLexeme);
			Lexeme matchingLexeme = null;

			switch (cursorLexeme.typeIndex)
			{
				case CSSTokenTypes.LCURLY:
					matchingLexeme = UnifiedEditor.findBalancingLexeme(lexemeList, index, CSSMimeType.MimeType,
							cursorLexeme.typeIndex, CSSTokenTypes.RCURLY, 1);
					break;

				case CSSTokenTypes.RCURLY:
					matchingLexeme = UnifiedEditor.findBalancingLexeme(lexemeList, index, CSSMimeType.MimeType,
							cursorLexeme.typeIndex, CSSTokenTypes.LCURLY, -1);
					break;

				case CSSTokenTypes.STRING:
					if (cursorLexeme.getCategoryIndex() != TokenCategories.ERROR
							&& (offset - 1 == cursorLexeme.getStartingOffset() || offset == cursorLexeme
									.getEndingOffset()))
					{
						result = new PairMatch();

						result.beginStart = cursorLexeme.getStartingOffset();
						result.beginEnd = result.beginStart + 1;
						result.endStart = cursorLexeme.getEndingOffset() - 1;
						result.endEnd = result.endStart + 1;

						loopCount = 0;
					}
					break;

				default:
					if (index - 1 >= 0 && offset == cursorLexeme.offset)
					{
						Lexeme previousLexeme = lexemeList.get(index - 1);
						String language = previousLexeme.getToken().getLanguage();
						String text = previousLexeme.getText();
						
						// NOTE: hack for case where we're inside an HTML style attribute
						if ("text/html".equals(language) && ("\"".equals(text) || "'".equals(text))) //$NON-NLS-1$ //$NON-NLS-2$
						{
							result = findEndingQuote(index, parseState, previousLexeme, lexemeList);
							
							loopCount = 0;
						}
					}
					break;
			}
			
			if (matchingLexeme != null)
			{
				result = new PairMatch();

				result.beginStart = cursorLexeme.getStartingOffset();
				result.beginEnd = cursorLexeme.getEndingOffset();
				result.endStart = matchingLexeme.getStartingOffset();
				result.endEnd = matchingLexeme.getEndingOffset();

				// break out of loop
				loopCount = 0;
			}
			else
			{
				loopCount--;

				if (loopCount > 0 && offset > 0)
				{
					cursorLexeme = lexemeList.getLexemeFromOffset(offset - 1);
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
				PreferenceConverter.getColor(CSSPlugin.getDefault().getPreferenceStore(),
						IPreferenceConstants.PAIR_MATCHING_COLOR));
	}

	private String getDisplayPreference()
	{
		return CSSPlugin.getDefault().getPreferenceStore().getString(IPreferenceConstants.SHOW_PAIR_MATCHES);
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

	/**
	 * Finds ending quote.
	 * @param offset
	 * @param parseState
	 * @param cursorLexeme
	 * @param lexemeList
	 * @return
	 */
	private PairMatch findEndingQuote(int index, IParseState parseState, Lexeme cursorLexeme, LexemeList lexemeList)
	{
		Lexeme endingLexeme = null;
		
		for (int i = index + 1; i < lexemeList.size(); i++)
		{
			Lexeme currentLexeme = lexemeList.get(i);
			
			if (!currentLexeme.getToken().getLanguage().equals("text/css")) //$NON-NLS-1$
			{
				//if first lexeme met after css area is a quote, it is the ending lexeme
				if (currentLexeme.getText().equals("\"")) //$NON-NLS-1$
				{
					endingLexeme = currentLexeme;
				}
				
				break;
			}
		}
		
		if (endingLexeme == null)
		{
			return null;
		}
		
		PairMatch match = new PairMatch();

		match.beginStart = cursorLexeme.getStartingOffset();
		match.beginEnd = cursorLexeme.getEndingOffset();
		match.endStart = endingLexeme.getStartingOffset();
		match.endEnd = endingLexeme.getEndingOffset();
		
		return match;
	}
}
