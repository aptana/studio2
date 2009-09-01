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
package com.aptana.ide.editor.html;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;

import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLUtils;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.AbstractPairFinder;
import com.aptana.ide.editors.unified.IPairFinder;
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
public class HTMLPairFinder extends AbstractPairFinder implements IPairFinder
{

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

			if (candidate.typeIndex == type && candidate.getLanguage().equals(HTMLMimeType.MimeType))
			{
				result = candidate;
				break;
			}

			startIndex += direction;
		}

		return result;
	}

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

		while (loopCount > 0 && cursorLexeme != null && cursorLexeme.getLanguage().equals(HTMLMimeType.MimeType))
		{
			int index = lexemeList.getLexemeIndex(cursorLexeme);
			Lexeme matchingLexeme = null;
			Lexeme candidate;
			switch (cursorLexeme.typeIndex)
			{
				case HTMLTokenTypes.CDATA_START:
					candidate = findLexeme(lexemeList, index + 1, HTMLTokenTypes.CDATA_END, 1);

					if (candidate != null)
					{
						matchingLexeme = candidate;
					}
					break;

				case HTMLTokenTypes.CDATA_END:
					candidate = this.findLexeme(lexemeList, index - 1, HTMLTokenTypes.CDATA_START, -1);

					if (candidate != null)
					{
						matchingLexeme = candidate;
					}
					break;

				case HTMLTokenTypes.DOCTYPE_DECL:
					candidate = this.findLexeme(lexemeList, index + 1, HTMLTokenTypes.GREATER_THAN, 1);

					if (candidate != null)
					{
						matchingLexeme = candidate;
					}
					break;

				case HTMLTokenTypes.XML_DECL:
					candidate = this.findLexeme(lexemeList, index + 1, HTMLTokenTypes.QUESTION_GREATER_THAN, 1);

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

				case HTMLTokenTypes.QUESTION_GREATER_THAN:
					candidate = this.findLexeme(lexemeList, index - 1, HTMLTokenTypes.XML_DECL, -1);

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

				case HTMLTokenTypes.ENCODING:
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

				case HTMLTokenTypes.VERSION:
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

				case HTMLTokenTypes.COMMENT:
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

				case HTMLTokenTypes.START_TAG:
					result = processStartTag(offset, parseState, cursorLexeme, lexemeList, result);
					break;

				case HTMLTokenTypes.END_TAG:
					result = processEndTag(offset, parseState, cursorLexeme, lexemeList, result, index);
					break;

				case HTMLTokenTypes.GREATER_THAN:
					candidate = this.findFirstLexeme(lexemeList, index - 1, HTMLTokenTypes.START_TAG,
							HTMLTokenTypes.END_TAG, HTMLTokenTypes.DOCTYPE_DECL, -1);

					if (candidate != null && candidate.typeIndex == HTMLTokenTypes.DOCTYPE_DECL)
					{
						matchingLexeme = candidate;
					}
					break;

				// case HTMLTokenTypes.SLASH_GREATER_THAN:
				// candidate = this.findLexeme(index - 1, HTMLTokenTypes.START_TAG, -1);
				//					
				// if (candidate != null)
				// {
				// matchingLexeme = candidate;
				// }
				// break;

				case HTMLTokenTypes.STRING:
					if (cursorLexeme.getCategoryIndex() != TokenCategories.ERROR)
					{
						if (cursorLexeme.getCategoryIndex() != TokenCategories.ERROR
								&& (offset - 1 == cursorLexeme.getStartingOffset()
										|| offset == cursorLexeme.getStartingOffset()
										|| offset == cursorLexeme.getEndingOffset() || offset == cursorLexeme
										.getEndingOffset() - 1))
						{
							String text = cursorLexeme.getText();

							if (text != null && text.length() > 1)
							{
								char first = text.charAt(0);
								char last = text.charAt(text.length() - 1);

								if ((first == '"' || first == '\'') && first == last)
								{
									result = new PairMatch();

									result.beginStart = cursorLexeme.getStartingOffset();
									result.beginEnd = result.beginStart + 1;
									result.endStart = cursorLexeme.getEndingOffset() - 1;
									result.endEnd = result.endStart + 1;
								}
							}
						}
					}
					loopCount = 0;
					break;
				case HTMLTokenTypes.QUOTE:
					if (index + 1 < lexemeList.size()
							&& lexemeList.get(index + 1).getToken().getLanguage().equals("text/css")) //$NON-NLS-1$
					{
						result = findEndingQuote(index, parseState, cursorLexeme, lexemeList);
					}
					else if (index - 1 > 0 && lexemeList.get(index - 1).getToken().getLanguage().equals("text/css")) //$NON-NLS-1$
					{
						result = findStartingQuote(index, parseState, cursorLexeme, lexemeList);
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

	private PairMatch processStartTag(int offset, IParseState parseState, Lexeme cursorLexeme, LexemeList lexemeList,
			PairMatch result)
	{
		Lexeme candidate;
		IParseNode root = parseState.getParseResults();
		IParseNode node = root.getNodeAtOffset(offset);

		if (node != null)
		{
			Lexeme closeLexeme = node.getEndingLexeme();
			int closeIndex = lexemeList.getLexemeIndex(closeLexeme);

			if (closeIndex > 0)
			{
				candidate = lexemeList.get(closeIndex - 1);

				if (candidate.typeIndex == HTMLTokenTypes.END_TAG)
				{
					if (HTMLUtils.stripTagEndings(candidate.getText()).equalsIgnoreCase(
							HTMLUtils.stripTagEndings(cursorLexeme.getText())))
					{
						result = new PairMatch();

						result.beginStart = cursorLexeme.getStartingOffset();
						result.beginEnd = getStartTagEndOffset(node, lexemeList, cursorLexeme);
						result.endStart = candidate.getStartingOffset();
						result.endEnd = closeLexeme.getEndingOffset();
					}
				}
			}
		}
		return result;
	}

	private int getStartTagEndOffset(IParseNode node, LexemeList lexemeList, Lexeme cursorLexeme)
	{
		if (node.hasAttributes())
			return cursorLexeme.getEndingOffset();

		int beginIndex = lexemeList.getLexemeIndex(cursorLexeme);
		if (beginIndex != -1)
		{
			Lexeme next = lexemeList.get(beginIndex + 1);
			if (next.getText().trim().equals(">"))
			{
				return next.getEndingOffset();
			}
		}
		return cursorLexeme.getEndingOffset() + 1;
	}

	private PairMatch processEndTag(int offset, IParseState parseState, Lexeme cursorLexeme, LexemeList lexemeList,
			PairMatch result, int index)
	{
		Lexeme candidate;
		IParseNode root = parseState.getParseResults();
		IParseNode node = root.getNodeAtOffset(offset);

		if (node != null)
		{
			Lexeme openLexeme = node.getStartingLexeme();
			int openIndex = lexemeList.getLexemeIndex(openLexeme);

			if (openIndex >= 0)
			{
				candidate = lexemeList.get(openIndex);

				if (candidate.typeIndex == HTMLTokenTypes.START_TAG)
				{
					if (HTMLUtils.stripTagEndings(candidate.getText()).equalsIgnoreCase(
							HTMLUtils.stripTagEndings(cursorLexeme.getText())))
					{
						result = new PairMatch();

						result.beginStart = candidate.getStartingOffset();
						result.beginEnd = getStartTagEndOffset(node, lexemeList, candidate);
						result.endStart = cursorLexeme.getStartingOffset();

						if (index + 1 < lexemeList.size())
						{
							Lexeme bracket = lexemeList.get(index + 1);

							if (bracket.typeIndex == HTMLTokenTypes.GREATER_THAN)
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
					&& candidate.getLanguage().equals(HTMLMimeType.MimeType))
			{
				result = candidate;
				break;
			}

			startIndex += direction;
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IPairFinder#getPairFinderColor()
	 */
	public Color getPairFinderColor()
	{
		return UnifiedColorManager.getInstance().getColor(
				PreferenceConverter.getColor(HTMLPlugin.getDefault().getPreferenceStore(),
						IPreferenceConstants.PAIR_MATCHING_COLOR));
	}

	private String getDisplayPreference()
	{
		return HTMLPlugin.getDefault().getPreferenceStore().getString(IPreferenceConstants.SHOW_PAIR_MATCHES);
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
	 * 
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
				// if first lexeme met after css area is a quote, it is the ending lexeme
				if (currentLexeme.typeIndex == HTMLTokenTypes.QUOTE)
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

	/**
	 * Finds starting quote.
	 * 
	 * @param index
	 * @param parseState
	 * @param cursorLexeme
	 * @param lexemeList
	 * @return
	 */
	private PairMatch findStartingQuote(int index, IParseState parseState, Lexeme cursorLexeme, LexemeList lexemeList)
	{
		Lexeme endingLexeme = null;
		for (int i = index - 1; i >= 0; i--)
		{
			Lexeme currentLexeme = lexemeList.get(i);
			if (!currentLexeme.getToken().getLanguage().equals("text/css")) //$NON-NLS-1$
			{
				// if first lexeme met after css area is a quote, it is the ending lexeme
				if (currentLexeme.typeIndex == HTMLTokenTypes.QUOTE)
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
