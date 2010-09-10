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
package com.aptana.ide.editor.css.formatting;

import java.text.ParseException;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editor.css.parsing.CSSParseState;
import com.aptana.ide.editors.unified.BaseFormatter;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.parsing.IParser;

/**
 * @author Ingo Muschenetz
 */
public class CSSCodeFormatter extends BaseFormatter
{
	/**
	 * format
	 * 
	 * @param notFormatted
	 * @param isSelection
	 * @param options
	 * @param project
	 * @param separator
	 * @return formatted lineDelimeters
	 */
	public String format(String notFormatted, boolean isSelection, Map options, IProject project, String lineDelimeters)
	{
		CSSCodeFormatterOptions codeoptions = new CSSCodeFormatterOptions(options, project);

		if (!codeoptions.doFormatting)
		{
			return notFormatted;
		}

		return doLexerBasedFormat(notFormatted, codeoptions, lineDelimeters);
	}

	/**
	 * doLexerBasedFormat
	 * 
	 * @param notFormatted
	 * @param codeoptions
	 * @param lineDelimeters
	 * @return
	 */
	private String doLexerBasedFormat(String notFormatted, CSSCodeFormatterOptions codeoptions, String lineDelimeters)
	{
		IParser parser = LanguageRegistry.getParser(CSSMimeType.MimeType);
		CSSParseState parseState = (CSSParseState) parser.createParseState(null);

		parseState.setEditState(notFormatted, notFormatted, 0, 0);

		try
		{
			parser.parse(parseState);
			LexemeList ll = parseState.getLexemeList();

			String indent = codeoptions.formatterTabChar;

			if (indent.length() == 0)
			{
				indent = " "; //$NON-NLS-1$
			}

			if (indent.charAt(0) == ' ')
			{
				StringBuffer bf = new StringBuffer();

				for (int a = 0; a < codeoptions.tabSize; a++)
				{
					bf.append(' ');
				}

				indent = bf.toString();
			}

			SourceWriter writer = new SourceWriter(0, indent, codeoptions.tabSize);

			if (lineDelimeters != null)
			{
				writer.setLineDelimeter(lineDelimeters);
			}

			String formatted = format(notFormatted, writer, ll, codeoptions);

			String start = getStartLineBreaks(writer, notFormatted, formatted);
			String end = getEndLineBreaks(writer, notFormatted, formatted);
			formatted = start + formatted + end;

			if (!isFormattingCorrect(ll, parser, notFormatted, formatted, new int[] { CSSTokenTypes.COMMENT }, null))
			{
				formatted = notFormatted;
			}

			return formatted;
		}
		catch (LexerException e)
		{
			return null;
		}
		catch (ParseException e)
		{
			return null;
		}
	}

	/**
	 * isOnSameLine
	 * 
	 * @param source
	 * @param list
	 * @param first
	 * @param second
	 * @return
	 */
	private boolean isOnSameLine(String source, LexemeList list, int first, int second)
	{
		if (first < 0)
		{
			return true;
		}

		if (second >= list.size())
		{
			return true;
		}

		int endingOffset = list.get(first).getEndingOffset();
		int startingOffset = list.get(second).getStartingOffset();

		for (int a = endingOffset; a < startingOffset; a++)
		{
			char c = source.charAt(a);

			if (c == '\r' || c == '\n')
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * hasSpaceBetween
	 * 
	 * @param source
	 * @param list
	 * @param first
	 * @param second
	 * @return
	 */
	private boolean hasSpaceBetween(String source, LexemeList list, int first, int second)
	{
		int endingOffset = 0;
		int startingOffset = 0;

		if (first >= 0)
		{
			endingOffset = list.get(first).getEndingOffset();
		}

		if (second < list.size())
		{
			startingOffset = list.get(second).getStartingOffset();
		}

		for (int a = endingOffset; a < startingOffset; a++)
		{
			char c = source.charAt(a);

			if (Character.isWhitespace(c))
			{
				if (c != '\r' && c != '\n')
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * format
	 * 
	 * @param source
	 * @param writer
	 * @param ll
	 * @param codeoptions
	 * @return
	 */
	private String format(String source, SourceWriter writer, LexemeList ll, CSSCodeFormatterOptions codeoptions)
	{
		Lexeme previousLexeme = null;
		boolean isInProperty = false;

		boolean newLine = false;

		// state variables for dealing with errors

		boolean hasSpace = false;
		boolean sameLine = true;
		boolean lastError = true;

		for (int i = 0; i < ll.size(); i++)
		{
			Lexeme lex = ll.get(i);

			if (lex.getCategoryIndex() == TokenCategories.ERROR)
			{
				sameLine = isOnSameLine(source, ll, i - 1, i);
				// preserving space and new line before error;
				if (!sameLine)
				{
					if (writer.getCurrentIndentLevel() != 0)
					{
						writer.println();
						writer.printIndent();
					}
				}

				if (sameLine)
				{
					hasSpace = hasSpaceBetween(source, ll, i - 1, i);
					if (hasSpace)
					{
						writer.print(' ');
					}
				}

				// preserving space and new line after error;
				// doing it here because multiple tokens may require this values.
				boolean bsLine = isOnSameLine(source, ll, i, i + 1);
				sameLine = bsLine;
				hasSpace = hasSpaceBetween(source, ll, i, i + 1);

				if (i < ll.size() - 1)
				{
					Lexeme nextLexeme = ll.get(i + 1);
					bsLine |= isLexemeOfType(nextLexeme, CSSTokenTypes.RCURLY);
					bsLine |= isLexemeOfType(nextLexeme, CSSTokenTypes.LCURLY);
				}

				if (!bsLine)
				{
					writer.println(lex.getText());
				}
				else
				{
					writer.print(lex.getText());
				}

				lastError = true;
				// FIXME For now give up here and retain the source as is from this point forward! We shoudl be able to
				// have the parser handle errors more gracefully in the future though (it resets to top-level rule
				// regardless of context)
				int start = lex.getEndingOffset();
				writer.print(source.substring(start));
				return writer.toString();
			}

			if (lastError)
			{
				lastError = false;
			}
			else
			{
				hasSpace = false;
				sameLine = false;
			}

			switch (lex.typeIndex)
			{
				case CSSTokenTypes.LCURLY:
				{
					if (codeoptions.formatterBracePositionForBlock == CSSCodeFormatterOptions.NEXT_LINE)
					{
						writer.println();
						writer.printIndent();
					}
					else if (codeoptions.formatterBracePositionForBlock == CSSCodeFormatterOptions.NEXT_LINE_SHIFTED)
					{
						writer.println();
						writer.increaseIndent();
						writer.printIndent();

					}
					else
					{
						writer.print(" ");//$NON-NLS-1$
					}

					writer.print(lex.getText());
					writer.increaseIndent();
					newLine = true;

					break;
				}

				case CSSTokenTypes.COMMENT:
				{
					boolean addedLine = false;

					if (previousLexeme != null)
					{
						if (isLexemeOfType(previousLexeme, CSSTokenTypes.RCURLY))
						{
							writer.println();
						}

						boolean split = splitByNewline(source, previousLexeme, lex);

						if (split)
						{
							writer.println();
							writer.printIndent();
							addedLine = true;
						}
					}

					if (previousLexeme != null && !addedLine)
					{
						writer.print(" "); //$NON-NLS-1$
						printComment(writer, lex.getText());
					}
					else
					{
						printComment(writer, lex.getText());
					}
					break;
				}

				case CSSTokenTypes.RCURLY:
					writer.println();

					if (codeoptions.formatterBracePositionForBlock == CSSCodeFormatterOptions.NEXT_LINE_SHIFTED)
					{
						writer.decreaseIndent();
					}

					if (codeoptions.formatterBracePositionForBlock == CSSCodeFormatterOptions.NEXT_LINE_SHIFTED)
					{
						writer.printIndent();
					}

					writer.print(lex.getText());
					writer.decreaseIndent();
					newLine = true;

					break;

				case CSSTokenTypes.SEMICOLON:
					writer.print(lex.getText());
					newLine = true;
					isInProperty = false;
					break;

				case CSSTokenTypes.AT_KEYWORD:
				case CSSTokenTypes.SELECTOR:
				{
					// case of h1, h1 + h2 {
					boolean isCommaBefore = isLexemeOfType(previousLexeme, CSSTokenTypes.COMMA);
					boolean isCommentBefore = isLexemeOfType(previousLexeme, CSSTokenTypes.COMMENT);

					if (previousLexeme != null && !isCommaBefore && !isCommentBefore)
					{
						if (!sameLine && isLexemeOfType(previousLexeme, CSSTokenTypes.RCURLY))
						{
							// if previous error was on same line does not print lines
							writer.println();
							writer.println();
						}
						else if (isLexemeOfType(previousLexeme, CSSTokenTypes.SEMICOLON))
						{
							lex.typeIndex = CSSTokenTypes.PROPERTY;
							writer.println();
							writer.printIndent();
						}
					}

					if (isCommentBefore)
					{
						writer.println();
					}
					else if (isCommaBefore && !codeoptions.newlinesBetweenSelectors)
					{
						writer.print(" "); //$NON-NLS-1$
					}

					// checking for space
					if (hasSpace)
					{
						writer.print(" "); //$NON-NLS-1$
					}
					writer.print(lex.getText());
					sameLine = false;
					hasSpace = false;
					break;
				}

				case CSSTokenTypes.COMMA:
				{
					writer.print(lex.getText());
					if (codeoptions.newlinesBetweenSelectors
							&& (isLexemeOfType(previousLexeme, CSSTokenTypes.CLASS)
									|| isLexemeOfType(previousLexeme, CSSTokenTypes.SELECTOR)
									|| isLexemeOfType(previousLexeme, CSSTokenTypes.IDENTIFIER) || isLexemeOfType(
									previousLexeme, CSSTokenTypes.HASH)))
					{
						writer.println();
					}
					break;
				}

				case CSSTokenTypes.LBRACKET:
				case CSSTokenTypes.RBRACKET:
				{
					writer.print(lex.getText());
					break;
				}

				case CSSTokenTypes.HASH:
				case CSSTokenTypes.CLASS:
				{

					boolean isCommaBefore = isLexemeOfType(previousLexeme, CSSTokenTypes.COMMA);
					boolean isCommentBefore = isLexemeOfType(previousLexeme, CSSTokenTypes.COMMENT);

					if (lexemeIsEndOfBlock(previousLexeme))
					{
						writer.println();
						writer.println();
						writer.print(lex.getText());
					}
					else
					{
						if (isCommentBefore)
						{
							writer.println();
						}
						else if (isCommaBefore
								|| ((previousLexeme != null && lex != null) && previousLexeme.getEndingOffset() < lex
										.getStartingOffset()))
						{
							if (!codeoptions.newlinesBetweenSelectors)
								writer.print(" "); //$NON-NLS-1$
						}
						writer.print(lex.getText());
					}
					break;
				}

				case CSSTokenTypes.PLUS:
				case CSSTokenTypes.GREATER:
				case CSSTokenTypes.STAR:
				case CSSTokenTypes.EQUAL:
				{
					if (lex.typeIndex == CSSTokenTypes.STAR
							&& (previousLexeme == null || lexemeIsEndOfBlock(previousLexeme)))
					{
						if (newLine)
						{
							writer.println();
							writer.printIndent();
							newLine = false;
						}
						writer.print(lex.getText());
					}
					else
					{
						writer.print(" " + lex.getText()); //$NON-NLS-1$
					}
					break;
				}

				case CSSTokenTypes.COLON:
				{
					isInProperty = isLexemeOfType(previousLexeme, CSSTokenTypes.PROPERTY);
					if (!isInProperty)
					{
						if (hasSpaceBetween(source, ll, i - 1, i))
							writer.print(" ");
					}
					writer.print(lex.getText());
					break;
				}
				case CSSTokenTypes.IDENTIFIER:
				{
					if (((isLexemeOfType(previousLexeme, CSSTokenTypes.COLON) || isLexemeOfType(previousLexeme,
							CSSTokenTypes.LBRACKET)) && !isInProperty)
							|| isLexemeOfType(previousLexeme, CSSTokenTypes.FUNCTION))
					{
						writer.print(lex.getText());
					}
					else
					{
						if (!(codeoptions.newlinesBetweenSelectors && isLexemeOfType(previousLexeme, CSSTokenTypes.COMMA)))
							writer.print(" "); //$NON-NLS-1$
						writer.print(lex.getText()); //$NON-NLS-1$
					}
					break;
				}
				case CSSTokenTypes.IMPORT:
				{
					if (newLine)
					{
						writer.println();
						newLine = false;
					}
					writer.printWithIndent(lex.getText());
					break;
				}
				case CSSTokenTypes.RPAREN:
					writer.print(lex.getText());
					break;
				default:
				{
					if (newLine)
					{
						writer.println();
						writer.printIndent();
						newLine = false;
					}
					else if (previousLexeme != null && previousLexeme.typeIndex != CSSTokenTypes.FUNCTION)
					{
						writer.print(" "); //$NON-NLS-1$
					}

					writer.print(lex.getText());
					break;
				}
			}
			previousLexeme = lex;
		}

		return writer.toString();
	}

	private boolean lexemeIsEndOfBlock(Lexeme previousLexeme)
	{
		return isLexemeOfType(previousLexeme, CSSTokenTypes.RCURLY)
				|| isLexemeOfType(previousLexeme, CSSTokenTypes.SEMICOLON);
	}

	/**
	 * Prints the comment across multiple lines
	 * 
	 * @param writer
	 * @param text
	 */
	private void printComment(SourceWriter writer, String text)
	{
		String[] split = text.split("\r\n|\r|\n"); //$NON-NLS-1$

		if (split.length == 1)
		{
			writer.print(split[0]);
		}
		else
		{
			writer.println(split[0].trim());

			for (int i = 1; i < split.length - 1; i++)
			{
				String string = split[i];
				writer.printlnWithIndent(" " + string.trim()); //$NON-NLS-1$
			}

			writer.printWithIndent(" " + split[split.length - 1].trim()); //$NON-NLS-1$
		}
	}

	/**
	 * Are these two lexemes split by a carriage return?
	 * 
	 * @param source
	 * @param previousLexeme
	 * @param lex
	 * @return - boolean if split by new line
	 */
	private boolean splitByNewline(String source, Lexeme previousLexeme, Lexeme lex)
	{
		String sourceText = source.substring(previousLexeme.getEndingOffset(), lex.getStartingOffset());

		return (sourceText.indexOf("\r") >= 0 || sourceText.indexOf("\n") >= 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see com.aptana.ide.editors.unified.ICodeFormatter#createNestedMark()
	 */
	public String createNestedMark()
	{
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.editors.unified.ICodeFormatter#handlesNested()
	 */
	public boolean handlesNested()
	{
		return true;
	}
}
