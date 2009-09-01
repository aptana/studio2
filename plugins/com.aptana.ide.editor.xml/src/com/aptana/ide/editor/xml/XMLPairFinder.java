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
package com.aptana.ide.editor.xml;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;

import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.xml.lexing.XMLTokenTypes;
import com.aptana.ide.editor.xml.parsing.XMLMimeType;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.AbstractPairFinder;
import com.aptana.ide.editors.unified.PairMatch;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Pavel Petrochenko
 */
public class XMLPairFinder extends AbstractPairFinder
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
		PairMatch result = null;
		LexemeList lexemeList = parseState.getLexemeList();
		while (loopCount > 0 && cursorLexeme != null)
		{
			int index = lexemeList.getLexemeIndex(cursorLexeme);
			Lexeme matchingLexeme = null;
			Lexeme candidate;

			IParseNode root;
			IParseNode node;

			switch (cursorLexeme.typeIndex)
			{
				case XMLTokenTypes.CDATA_START:
					candidate = this.findLexeme(lexemeList, index + 1, XMLTokenTypes.CDATA_END, 1);

					if (candidate != null)
					{
						matchingLexeme = candidate;
					}
					break;

				case XMLTokenTypes.CDATA_END:
					candidate = this.findLexeme(lexemeList, index - 1, XMLTokenTypes.CDATA_START, -1);

					if (candidate != null)
					{
						matchingLexeme = candidate;
					}
					break;

				case XMLTokenTypes.DOCTYPE_DECL:
					candidate = this.findLexeme(lexemeList, index + 1, HTMLTokenTypes.GREATER_THAN, 1);

					if (candidate != null)
					{
						matchingLexeme = candidate;
					}
					break;

				case XMLTokenTypes.XML_DECL:
					candidate = this.findLexeme(lexemeList, index + 1, XMLTokenTypes.QUESTION_GREATER_THAN, 1);

					if (candidate != null)
					{
						result = new PairMatch();

						result.beginStart = cursorLexeme.getStartingOffset();
						result.beginEnd = cursorLexeme.getEndingOffset();
						result.endStart = candidate.getStartingOffset();
						result.endEnd = candidate.getEndingOffset();

						// break out of loop
						loopCount = 0;
					}
					break;

				case XMLTokenTypes.QUESTION_GREATER_THAN:
					candidate = this.findLexeme(lexemeList, index - 1, XMLTokenTypes.XML_DECL, -1);

					if (candidate != null)
					{
						result = new PairMatch();

						result.beginStart = cursorLexeme.getStartingOffset();
						result.beginEnd = result.beginStart + 2;
						result.endStart = candidate.getStartingOffset();
						result.endEnd = candidate.getEndingOffset();

						// break out of loop
						loopCount = 0;
					}
					break;

				case XMLTokenTypes.ENCODING:
					int encodingLength = "encoding=".length(); //$NON-NLS-1$

					if (offset - cursorLexeme.getStartingOffset() >= encodingLength)
					{
						result = new PairMatch();

						result.beginStart = cursorLexeme.getStartingOffset() + encodingLength;
						result.beginEnd = result.beginStart + 1;
						result.endStart = cursorLexeme.getEndingOffset() - 1;
						result.endEnd = result.endStart + 1;

						loopCount = 0;
					}
					break;

				case XMLTokenTypes.VERSION:
					int versionLength = "version=".length(); //$NON-NLS-1$

					if (offset - cursorLexeme.getStartingOffset() >= versionLength)
					{
						result = new PairMatch();

						result.beginStart = cursorLexeme.getStartingOffset() + versionLength;
						result.beginEnd = result.beginStart + 1;
						result.endStart = cursorLexeme.getEndingOffset() - 1;
						result.endEnd = result.endStart + 1;

						loopCount = 0;
					}
					break;

				case XMLTokenTypes.COMMENT:
					int openCommentLength = "<!--".length(); //$NON-NLS-1$
					int closeCommentLength = "-->".length(); //$NON-NLS-1$
					int start = cursorLexeme.getStartingOffset();
					int end = cursorLexeme.getEndingOffset();

					if (offset - start <= openCommentLength || end - offset <= closeCommentLength)
					{
						result = new PairMatch();

						result.beginStart = cursorLexeme.getStartingOffset();
						result.beginEnd = result.beginStart + 4;
						result.endStart = cursorLexeme.getEndingOffset() - 3;
						result.endEnd = result.endStart + 3;

						loopCount = 0;
					}
					break;

				case XMLTokenTypes.START_TAG:
					root = parseState.getParseResults();
					node = root.getNodeAtOffset(offset);

					result = processOpenTag(cursorLexeme, result, lexemeList, node);

					break;

				case XMLTokenTypes.END_TAG:
					root = parseState.getParseResults();
					node = root.getNodeAtOffset(offset);

					result = processCloseTag(cursorLexeme, result, lexemeList, index, node);
					break;

				case XMLTokenTypes.GREATER_THAN:
					candidate = this.findFirstLexeme(lexemeList, index - 1, XMLTokenTypes.START_TAG,
							XMLTokenTypes.END_TAG, XMLTokenTypes.DOCTYPE_DECL, -1);

					if (candidate != null && candidate.typeIndex == XMLTokenTypes.DOCTYPE_DECL)
					{
						matchingLexeme = candidate;
					}
					break;

				// case XMLTokenTypes.SLASH_GREATER_THAN:
				// candidate = this.findLexeme(index - 1, XMLTokenTypes.START_TAG, -1);
				//					
				// if (candidate != null)
				// {
				// matchingLexeme = candidate;
				// }
				// break;

				case XMLTokenTypes.STRING:
					if (cursorLexeme.getCategoryIndex() != TokenCategories.ERROR
							&& (offset - 1 == cursorLexeme.getStartingOffset() || offset == cursorLexeme
									.getEndingOffset()))
					{
						result = new PairMatch();

						result.beginStart = cursorLexeme.getStartingOffset();
						result.beginEnd = result.beginStart + 1;
						result.endStart = cursorLexeme.getEndingOffset() - 1;
						result.endEnd = result.endStart + 1;
					}
					loopCount = 0;
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

	private PairMatch processCloseTag(Lexeme cursorLexeme, PairMatch result, LexemeList lexemeList, int index,
			IParseNode node)
	{
		Lexeme candidate;
		if (node != null)
		{
			Lexeme openLexeme = node.getStartingLexeme();
			int openIndex = lexemeList.getLexemeIndex(openLexeme);

			if (openIndex > 0)
			{
				candidate = lexemeList.get(openIndex);

				if (candidate.typeIndex == XMLTokenTypes.START_TAG)
				{
					result = new PairMatch();

					result.beginStart = candidate.getStartingOffset();
					result.beginEnd = candidate.getEndingOffset();
					result.endStart = cursorLexeme.getStartingOffset();

					if (index + 1 < lexemeList.size())
					{
						Lexeme bracket = lexemeList.get(index + 1);

						if (bracket.typeIndex == XMLTokenTypes.GREATER_THAN)
						{
							result.endEnd = bracket.getEndingOffset();
						}
						else
						{
							result.endEnd = cursorLexeme.getEndingOffset();
						}
					}
					else
					{
						result.endEnd = cursorLexeme.getEndingOffset();
					}
				}
			}
		}
		return result;
	}

	private PairMatch processOpenTag(Lexeme cursorLexeme, PairMatch result, LexemeList lexemeList, IParseNode node)
	{
		Lexeme candidate;
		if (node != null)
		{
			Lexeme closeLexeme = node.getEndingLexeme();
			int closeIndex = lexemeList.getLexemeIndex(closeLexeme);

			if (closeIndex > 0)
			{
				candidate = lexemeList.get(closeIndex - 1);

				if (candidate.typeIndex == XMLTokenTypes.END_TAG)
				{
					result = new PairMatch();

					result.beginStart = cursorLexeme.getStartingOffset();
					result.beginEnd = cursorLexeme.getEndingOffset();
					result.endStart = candidate.getStartingOffset();
					result.endEnd = closeLexeme.getEndingOffset();
				}
			}
		}
		return result;
	}

	/**
	 * findLexeme
	 * 
	 * @param startIndex
	 * @param type
	 * @param direction
	 * @return Lexeme
	 */
	private Lexeme findLexeme(LexemeList lexemeList, int startIndex, int type, int direction)
	{
		Lexeme result = null;

		while (0 <= startIndex && startIndex < lexemeList.size())
		{
			Lexeme candidate = lexemeList.get(startIndex);

			if (candidate.typeIndex == type && candidate.getLanguage().equals(XMLMimeType.MimeType))
			{
				result = candidate;
				break;
			}

			startIndex += direction;
		}

		return result;
	}

	/**
	 * findLexeme
	 * 
	 * @param startIndex
	 * @param type1
	 * @param type2
	 * @param type3
	 * @param direction
	 * @return Lexeme
	 */
	private Lexeme findFirstLexeme(LexemeList lexemeList, int startIndex, int type1, int type2, int type3, int direction)
	{
		Lexeme result = null;

		while (0 <= startIndex && startIndex < lexemeList.size())
		{
			Lexeme candidate = lexemeList.get(startIndex);

			if ((candidate.typeIndex == type1 || candidate.typeIndex == type2 || candidate.typeIndex == type3)
					&& candidate.getLanguage().equals(XMLMimeType.MimeType))
			{
				result = candidate;
				break;
			}

			startIndex += direction;
		}

		return result;
	}

	private String getDisplayPreference()
	{
		return XMLPlugin.getDefault().getPreferenceStore().getString(IPreferenceConstants.SHOW_PAIR_MATCHES);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IPairFinder#getPairFinderColor()
	 */
	public Color getPairFinderColor()
	{
		return UnifiedColorManager.getInstance().getColor(
				PreferenceConverter.getColor(XMLPlugin.getDefault().getPreferenceStore(),
						IPreferenceConstants.PAIR_MATCHING_COLOR));
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
