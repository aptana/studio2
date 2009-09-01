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
package com.aptana.ide.editor.js;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;

import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.AbstractPairFinder;
import com.aptana.ide.editors.unified.IPairFinder;
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
public class JSPairFinder extends AbstractPairFinder implements IPairFinder
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
				case JSTokenTypes.LPAREN:
					matchingLexeme = UnifiedEditor.findBalancingLexeme(lexemeList, index, JSMimeType.MimeType,
							cursorLexeme.typeIndex, JSTokenTypes.RPAREN, 1);
					break;

				case JSTokenTypes.RPAREN:
					matchingLexeme = UnifiedEditor.findBalancingLexeme(lexemeList, index, JSMimeType.MimeType,
							cursorLexeme.typeIndex, JSTokenTypes.LPAREN, -1);
					break;

				case JSTokenTypes.LCURLY:
					matchingLexeme = UnifiedEditor.findBalancingLexeme(lexemeList, index, JSMimeType.MimeType,
							cursorLexeme.typeIndex, JSTokenTypes.RCURLY, 1);
					break;

				case JSTokenTypes.RCURLY:
					matchingLexeme = UnifiedEditor.findBalancingLexeme(lexemeList, index, JSMimeType.MimeType,
							cursorLexeme.typeIndex, JSTokenTypes.LCURLY, -1);
					break;
				case JSTokenTypes.LBRACKET:
					matchingLexeme = UnifiedEditor.findBalancingLexeme(lexemeList, index, JSMimeType.MimeType,
							cursorLexeme.typeIndex, JSTokenTypes.RBRACKET, -1);
				case JSTokenTypes.RBRACKET:
					matchingLexeme = UnifiedEditor.findBalancingLexeme(lexemeList, index, JSMimeType.MimeType,
							cursorLexeme.typeIndex, JSTokenTypes.LBRACKET, -1);
				case JSTokenTypes.STRING:
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
				PreferenceConverter.getColor(JSPlugin.getDefault().getPreferenceStore(),
						IPreferenceConstants.PAIR_MATCHING_COLOR));
	}

	private String getDisplayPreference()
	{
		return JSPlugin.getDefault().getPreferenceStore().getString(IPreferenceConstants.SHOW_PAIR_MATCHES);
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
