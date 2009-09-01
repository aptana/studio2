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
package com.aptana.ide.editor.html.formatting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.resources.IProject;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParseState;
import com.aptana.ide.editors.unified.BaseFormatter;
import com.aptana.ide.editors.unified.ICodeFormatter;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.IParser;

/**
 * @author Pavel Petrochenko
 */
public class HTMLCodeFormatter extends BaseFormatter
{

	private Stack tagNames = new Stack();
	private Stack stillWrap = new Stack();
	private HTMLCodeFormatterOptions codeoptions;

	private boolean isSelfClosing(int offset, LexemeList list)
	{
		for (int a = offset; a < list.size(); a++)
		{
			Lexeme lexeme = list.get(a);
			if (lexeme.getToken().getTypeIndex() == HTMLTokenTypes.GREATER_THAN)
			{
				return false;
			}
			if (lexeme.getToken().getTypeIndex() == HTMLTokenTypes.SLASH_GREATER_THAN)
			{
				return true;
			}
		}
		return false;
	}

	boolean hasType(LexemeList list, int offset, int tokenType)
	{
		if (list.size() > offset)
		{
			return list.get(offset).getToken().getTypeIndex() == tokenType;
		}
		return false;
	}

	private boolean isSmartNoWrap(LexemeList lexemeList, int a,
			HTMLCodeFormatterOptions codeoptions2)
	{
		if (codeoptions2.doNotWrapSimple)
		{
			if (a > 0)
			{
				Lexeme fL = lexemeList.get(a - 1);
				int typeIndex = fL.getToken().getTypeIndex();
				if (typeIndex == HTMLTokenTypes.END_TAG)
				{
					return false;
				}
			} else
			{
				return false;
			}
			a++;

			while (a < lexemeList.size())
			{
				Lexeme lexeme = lexemeList.get(a);
				int typeIndex = lexeme.getToken().getTypeIndex();
				if (typeIndex != HTMLTokenTypes.TEXT)
				{
					if (typeIndex == HTMLTokenTypes.END_TAG)
					{
						return true;
					}
					return false;
				}
				a++;
			}
		}
		return false;
	}

	private String[] splitOnLines(String comment)
	{
		String[] result = null;
		ArrayList lines = new ArrayList();
		boolean lastSlash = false;
		int lastPos = 0;
		for (int a = 0; a < comment.length(); a++)
		{
			char c = comment.charAt(a);
			if (c == '\n' || c == '\r')
			{
				if (!lastSlash)
				{
					lastSlash = true;
					lines.add(comment.substring(lastPos, a));
					lastPos = a + 1;
				}
				continue;
			}
			lastSlash = false;
		}
		lines.add(comment.substring(lastPos, comment.length()));
		result = new String[lines.size()];
		lines.toArray(result);
		return result;
	}

	/**
	 * @param string
	 * @return indentation string as it is on previous line;
	 */
	public String getCurrentIndentationString(String string)
	{
		int pos = string.length();
		int startLine = 0;
		for (int a = string.length() - 1; a >= 0; a--)
		{

			char charAt = string.charAt(a);
			if (charAt == '\n' || charAt == '\r')
			{
				startLine = a + 1;
				break;
			}
			if (!Character.isWhitespace(charAt))
			{
				pos = a;
			}
		}
		if (string.length() == 0)
		{
			return ""; //$NON-NLS-1$
		}
		return string.substring(startLine, pos);
	}

	/**
	 * @see com.aptana.ide.editors.unified.ICodeFormatter#format(java.lang.String,
	 *      boolean, java.util.Map, org.eclipse.core.resources.IProject,
	 *      java.lang.String)
	 */
	public String format(String notFormatted, boolean isSelection, Map options,
			IProject project, String linedelimeters)
	{
		String doc = null;
		doc = notFormatted;
		level = 0;
		tagNames.clear();
		try
		{
			IParser parser = LanguageRegistry.getParser(HTMLMimeType.MimeType);
			HTMLParseState createParseState = (HTMLParseState) parser
					.createParseState(null);
			createParseState.setEditState(doc, doc, 0, 0);
			parser.parse(createParseState);
			LexemeList lexemeList = createParseState.getLexemeList();
			Stack clevel = new Stack();
			HashSet notClosed = new HashSet();
			for (int a = 0; a < lexemeList.size(); a++)
			{
				Lexeme lexeme = lexemeList.get(a);
				if (lexeme.getToken().getTypeIndex() == HTMLTokenTypes.START_TAG)
				{
					if (!isSelfClosing(a, lexemeList))
					{
						clevel.push(lexeme);
					}
				}
				if (lexeme.getToken().getTypeIndex() == HTMLTokenTypes.END_TAG)
				{
					if (!clevel.isEmpty())
					{
						Lexeme lex = (Lexeme) clevel.pop();
						String ltagName = lex.getText().substring(1);
						String ktagName = lexeme.getText().substring(2);
						if (!ltagName.equals(ktagName))
						{
							clevel.push(lex);
							notClosed.add(lex);
						}
					}
				}
			}
			codeoptions = new HTMLCodeFormatterOptions(options, project);
			if (!codeoptions.doFormatting)
			{
				return notFormatted;
			}
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
			SourceWriter buf = new SourceWriter(0, indent, codeoptions.tabSize);
			if (linedelimeters != null)
			{
				buf.setLineDelimeter(linedelimeters);
			}
			boolean inOtherLanguage = false;
			int startOtherLanguage = -1;
			String language = null;
			boolean isInAttr = false;
			boolean isSelfClosing = false;
			int quoteStart = 0;
			boolean printLine = false;
			boolean lastTextEndsWithSpace = false;
			boolean inQuote = false;
			boolean isInPre = false;
			boolean inConditional = false;
			boolean isSmartNoWrap = false;
			int prePositon = -1;
			boolean lastError = false;
			boolean isInTag = false;
			int preCount = 0;
			String tString = null;
			int htmlPosition = -1;
			String lastTagName = ""; //$NON-NLS-1$
			String realLastTagName = ""; //$NON-NLS-1$
			for (int a = 0; a < lexemeList.size(); a++)
			{

				Lexeme lexeme = lexemeList.get(a);
				int i = lexeme.getStartingOffset() - 1;
				int typeIndex = lexeme.getToken().getTypeIndex();

				if (inOtherLanguage
						&& lexeme.getLanguage().equals(HTMLMimeType.MimeType))
				{
					inOtherLanguage = false;
					String substring = doc.substring(startOtherLanguage, lexeme
							.getStartingOffset());

					// only format non-attribute text
					if (!isInAttr)
					{
						formatAnotherLanguage(isSelection, options, project,
								linedelimeters, buf, language, substring);

						if (tString != null)
						{
							buf.print(tString);
							tString = null;
						}
					}
				}

				if (isInPre)
				{
					if (typeIndex == HTMLTokenTypes.START_TAG)
					{
						String tagName = lexeme.getText().substring(1).trim();
						if (tagName.equals("pre")) { //$NON-NLS-1$
							preCount++;
						}
						continue;
					}
					if (typeIndex == HTMLTokenTypes.END_TAG)
					{

						String tagName = lexeme.getText().substring(2).trim();
						if (tagName.equals("pre")) { //$NON-NLS-1$
							if (preCount == 0)
							{
								if (buf.getCurrentIndentLevel() == 0)
								{
									buf.printIndent();
								}
								buf.print(notFormatted.substring(prePositon,
										lexeme.getStartingOffset()));
								isInPre = false;
								buf.print(lexeme.getText());
							} else
							{
								preCount--;
							}
							continue;
						}
						continue;
					} else
					{
						continue;
					}
				}

				if (!lexeme.getLanguage().equals(HTMLMimeType.MimeType))
				{
					if (!inOtherLanguage)
					{
						if (!codeoptions.doFormatting)
						{
							String txt = notFormatted.substring(htmlPosition,
									lexeme.getStartingOffset());
							tString = getCurrentIndentationString(txt);
							buf.print(txt);
							htmlPosition = -1;
						}
						inOtherLanguage = true;

						Lexeme previous = a > 0 ? lexemeList.get(a - 1) : null;
						isInAttr = previous != null ? (previous.getToken()
								.getTypeIndex() == HTMLTokenTypes.QUOTE)
								: false;

						if (previous != null)
						{
							startOtherLanguage = previous.getEndingOffset();
						} else
						{
							startOtherLanguage = lexeme.getStartingOffset();
						}

						language = lexeme.getLanguage();

						continue;
					} else
					{
						continue;
					}
				}

				if (typeIndex == HTMLTokenTypes.PI_TEXT)
				{
					// if (!codeoptions.doFormatting)
					// {
					// buf.println(notFormatted.substring(htmlPosition,
					// lexeme.getStartingOffset()));
					// }
					// buf.print(' ');
					if (!isInAttr)
					{
						formatAnotherLanguage(false, options, project,
								linedelimeters, buf,
								"text/php", lexeme.getText()); //$NON-NLS-1$
					}
					// buf.println();
					htmlPosition = -1;
					continue;
				}
				if (!codeoptions.doFormatting)
				{
					if (htmlPosition == -1)
					{
						htmlPosition = lexeme.getStartingOffset();
					}
					continue;
				}
				if (typeIndex == HTMLTokenTypes.PI_OPEN)
				{
					int iLevel = buf.getCurrentIndentLevel();
					if (iLevel != 0)
					{
						buf.println();
					}
					buf.printIndent();

					buf.print(lexeme.getText());
					// interior text will handle own carriage returns
					// buf.println();
					continue;
				}
				if (typeIndex == HTMLTokenTypes.QUESTION_GREATER_THAN)
				{
					// interior text will handle own carriage returns
					// buf.println();
					// buf.printIndent();
					if (buf.getCurrentIndentLevel() == 0)
					{
						buf.printIndent();
					}
					buf.print(lexeme.getText());
					buf.println();
					continue;
				}

				if (typeIndex == HTMLTokenTypes.ERROR
						|| typeIndex == HTMLTokenTypes.STRING
						&& lexeme.getCategoryIndex() == HTMLTokenTypes.ERROR)
				{
					// TODO REMOVE ME AFTER #STU-276 will be fixed
					if (lexeme.getText().equals("<![endif]")) { //$NON-NLS-1$
						buf.print(lexeme.getText());
						inConditional = true;
						buf.decreaseIndent();
						continue;
					}
					if (inConditional)
					{
						if (lexeme.getText().endsWith("]")) //$NON-NLS-1$
						{
							buf.print(lexeme.getText());
							inConditional = false;
							continue;
						}
					}
					if (lexeme.getText().startsWith("<![if")) { //$NON-NLS-1$
						buf.print(lexeme.getText());
						buf.increaseIndent();
						continue;
					}
					if (a > 0)
					{
						Lexeme pl = lexemeList.get(a - 1);
						String substring = notFormatted.substring(pl
								.getEndingOffset(), lexeme.getStartingOffset());
						if (substring.length() > 0)
						{
							if (substring.contains("\r") //$NON-NLS-1$
									|| substring.contains("\n")) //$NON-NLS-1$
							{
								buf.println();
							} else if (substring.contains(" ") //$NON-NLS-1$
									|| substring.contains("\t")) //$NON-NLS-1$
							{
								buf.print(' ');
							}
						}

					}
					if (!isInTag)
					{
						if (!codeoptions.notWrappingTags.contains(lastTagName))
						{
							if (buf.getCurrentIndentLevel() != 0)
							{
								buf.println();
							}
							buf.printIndent();
						}
					}
					lastError = true;
					buf.print(lexeme.getText());
					if (a < lexemeList.size() - 1)
					{
						Lexeme l1 = lexemeList.get(a + 1);
						if (l1.getLanguage().equals(HTMLMimeType.MimeType)
								&& l1.typeIndex != HTMLTokenTypes.ERROR)
						{
							String substring = notFormatted.substring(lexeme
									.getEndingOffset(), l1.getStartingOffset());
							if (substring.length() > 0)
							{
								if (substring.contains("\r") //$NON-NLS-1$
										|| substring.contains("\n")) //$NON-NLS-1$
								{
									buf.println();
								} else if (substring.contains(" ") //$NON-NLS-1$
										|| substring.contains("\t")) //$NON-NLS-1$
								{
									buf.print(' ');
								}
							}

						}
					}
					if (!isInTag)
					{
						if (!codeoptions.notWrappingTags.contains(lastTagName))
						{
							if (a < lexemeList.size() - 1
									&& lexemeList.get(a + 1).getToken()
											.getTypeIndex() == HTMLTokenTypes.ERROR)
							{
								continue;
							} else
							{
								buf.println();
							}
						} else
						{
							if (a < lexemeList.size() - 1
									&& lexemeList.get(a + 1).getToken()
											.getTypeIndex() != HTMLTokenTypes.ERROR)
							{
								buf.print(' ');
							}
						}
					}
					continue;
				}
				lastError = false;

				if (typeIndex == HTMLTokenTypes.COMMENT)
				{
					appendComment(lexemeList, codeoptions, buf, a, lexeme);
					continue;
				}
				if (typeIndex == HTMLTokenTypes.TEXT)
				{
					if (lexeme.getText().startsWith("<![endif]>")) { //$NON-NLS-1$
						buf.print(lexeme.getText());
						inConditional = true;
						buf.decreaseIndent();
						continue;
					}
					if (inConditional)
					{
						if (lexeme.getText().endsWith("]")) //$NON-NLS-1$
						{
							buf.print(lexeme.getText());
							inConditional = false;
							continue;
						}
					}
					if (lexeme.getText().startsWith("<![if")) { //$NON-NLS-1$
						buf.print(lexeme.getText());
						buf.increaseIndent();
						continue;
					}
					
					String[] splitOnLines = splitOnLines(lexeme.getText());
					boolean hasNotEmpty = false;
					ArrayList withoutEmpty = new ArrayList();
					boolean lastHasSpace = false;
					if (isSmartNoWrap)
					{
						if (splitOnLines.length == 1)
						{
							buf.print(splitOnLines[0]);
							continue;
						}
						if (splitOnLines.length == 2
								&& splitOnLines[1].trim().length() == 0)
						{
							buf.print(splitOnLines[0]);
							continue;
						}
					}
					for (int b = 0; b < splitOnLines.length; b++)
					{
						String string = splitOnLines[b];
						String text = string.trim();

						boolean hasSpace = false;
						for (int j = string.length() - 1; j > 0; j--)
						{
							char c = string.charAt(j);
							if (c == ' ' || c == '\t')
							{
								hasSpace = true;
							}
							if (!Character.isWhitespace(c))
							{
								break;
							}
						}

						if (hasSpace)
						{
							String trim = text.trim();
							int length = trim.length();
							if (length > 0)
							{
								text = text + ' ';
							}
							if (lastHasSpace)
							{
								hasSpace = false;
							}
							if (hasSpace)
							{
								if (!withoutEmpty.isEmpty())
								{
									String last = (String) withoutEmpty
											.get(withoutEmpty.size() - 1);
									if (length > 0)
									{
										last = last + ' ';
									}
									withoutEmpty.set(withoutEmpty.size() - 1,
											last);
								}
							}
						}
						if (text.length() > 0)
						{
							if (!hasNotEmpty)
							{
								if (!lastTextEndsWithSpace)
								{
									if (buf.getCurrentIndentLevel() != 0)
									{
										if (Character.isWhitespace(string
												.charAt(0)))
										{
											text = ' ' + text;
										}
									}
								}
							}
							withoutEmpty.add(text);
							hasNotEmpty = true;
						}
						lastHasSpace = hasSpace;
					}
					lastTextEndsWithSpace = lastHasSpace;
					splitOnLines = new String[withoutEmpty.size()];
					withoutEmpty.toArray(splitOnLines);
					if (!hasNotEmpty)
					{
						continue;
					}
					boolean nextIndent = true;
					if (buf.getCurrentIndentLevel() == 0)
					{
						buf.printIndent();
						nextIndent = false;
					}
					if (splitOnLines[0].length() > 0)
					{
						buf.print(splitOnLines[0]);
						if (splitOnLines.length > 1)
						{
							buf.println();
							buf.printIndent();
							nextIndent = false;
						}
					}
					for (int b = 1; b < splitOnLines.length; b++)
					{
						String text = splitOnLines[b];
						if (text.length() == 0)
						{
							nextIndent = false;
							continue;
						}
						if (nextIndent || buf.getCurrentIndentLevel() == 0)
						{
							buf.printIndent();
						}
						if (b == splitOnLines.length - 1)
						{
							boolean needContinue = false;
							needContinue = shouldInsertNewLine(lexemeList,
									codeoptions, a, needContinue);
							if (needContinue)
							{
								buf.print(text);
								continue;
							}
						}
						buf.println(text);
						nextIndent = true;
					}
					continue;
				}
				if (typeIndex == HTMLTokenTypes.PERCENT_TEXT)
				{
					appendPercentText(lexemeList, codeoptions, buf, a, lexeme);
					continue;
				}
				if (typeIndex == HTMLTokenTypes.GREATER_THAN)
				{
					buf.print('>');

					boolean keepWithNext = false;
					if (!isInTag && a + 1 < lexemeList.size())
					{
						Lexeme next = lexemeList.get(a + 1);
						if (next != null
								&& isLexemeOfType(next, HTMLTokenTypes.TEXT)
								&& (!next.getText().startsWith(" ") && !next.getText().startsWith("\t") //$NON-NLS-1$ //$NON-NLS-2$
										&& !next.getText().startsWith("\r") && !next.getText().startsWith("\n"))) //$NON-NLS-1$ //$NON-NLS-2$
						{
							keepWithNext = true;
						}
					}

					if (isInTag && a + 1 < lexemeList.size())
					{
						Lexeme next = lexemeList.get(a + 1);
						keepWithNext = switchingLanguages(lexeme, next);
					}

					boolean wrapContent = !codeoptions.notWrappingTags
							.contains(lastTagName);
					boolean alwaysWrapAfterwards = codeoptions.allwaysWrap
							.contains(realLastTagName);
					isSmartNoWrap = isSmartNoWrap(lexemeList, a, codeoptions);
					if (!isSmartNoWrap)
					{
						// if I am just finishing a start tag and I don't wrap
						// interior content, or I should print a
						// line
						if ((wrapContent || printLine) && !keepWithNext)
						{
							// if I am not self closing, or I must always wrap
							if (!isSelfClosing || alwaysWrapAfterwards)
							{
								buf.println();
							}
						} else if (alwaysWrapAfterwards)
						{
							buf.println();
							stillWrap.push(this);
						}
					}
					// No longer inside a start tag (if we were inside one
					// before)
					isInTag = false;
					continue;
				}
				if (typeIndex == HTMLTokenTypes.SLASH_GREATER_THAN)
				{
					checkPreviousTrail(notFormatted, lexemeList, buf, a, lexeme);
					buf.print('/');
					buf.print('>');

					if (codeoptions.allwaysWrap.contains(realLastTagName))
					{
						if (stillWrap.size() > 0)
						{
							stillWrap.pop();
						}
						stillWrap.push(this);
						buf.println();
					}

					// No longer inside a start tag (if we were inside one
					// before)
					isInTag = false;
					continue;
				}
				if (typeIndex == HTMLTokenTypes.START_TAG)
				{
					isInTag = true;
					String tagName = lexeme.getText().substring(1).trim()
							.toLowerCase();

					int iL = buf.getCurrentIndentLevel();
					if (tagName.equals("pre")) { //$NON-NLS-1$
						isInPre = true;
						prePositon = lexeme.getStartingOffset();
						if (iL == 0)
						{
							buf.printIndent();
						}
						continue;
					}
					realLastTagName = tagName;
					boolean selfClosing = isSelfClosing(a, lexemeList);
					isSelfClosing = selfClosing;

					boolean wrap = !codeoptions.notWrappingTags
							.contains(tagName)
							&& !selfClosing
							&& !createParseState.isEmptyTagType(tagName);
					wrap |= codeoptions.allwaysWrap.contains(tagName);
					if (wrap)
					{
						if (iL != 0)
						{
							buf.println();
							iL = 0;
						}
					}
					if (iL == 0)
					{
						buf.printIndent();
					}
					buf.print(lexeme.getText());
					if (!selfClosing)
					{
						if (!createParseState.isEmptyTagType(tagName))
						{
							if (!notClosed.contains(lexeme))
							{
								if (!codeoptions.doNotIndent
										.contains(realLastTagName))
								{
									buf.increaseIndent();
								}
								tagNames.push(lastTagName);
								stillWrap.push(null);
								lastTagName = tagName;
							}
						} else
						{
							isSelfClosing = true;
						}
					}
					printLine = false;
					continue;
				} else if (typeIndex == HTMLTokenTypes.END_TAG)
				{
					String pTag = lastTagName;

					int iLevel = buf.getCurrentIndentLevel();
					String tagName = lexeme.getText().substring(2).trim()
							.toLowerCase();
					boolean isBroken = !(pTag.equals(tagName) || pTag.length() == 0);
					lastTagName = tagName;

					boolean contains = codeoptions.notWrappingTags
							.contains(tagName);
					contains &= (stillWrap.size() == 0 || stillWrap.peek() == null);

					if (iLevel != 0)
					{
						if (!contains || printLine)
						{
							if (!isSmartNoWrap)
							{
								buf.println();
								iLevel = 0;
							}
						}
					}
					printLine = isSmartNoWrap;

					if (!createParseState.isEmptyTagType(tagName))
					{
						// if
						// (!codeoptions.notWrappingTags.contains(lastTagName)){
						if (!isBroken)
						{
							if (!codeoptions.doNotIndent.contains(pTag))
							{
								buf.decreaseIndent();
							}
						}
						// }
					}
					if (iLevel == 0)
					{
						buf.printIndent();
					}
					buf.print(lexeme.getText());
					if (!tagNames.isEmpty() && !isBroken)
					{
						lastTagName = (String) tagNames.pop();
						if (!stillWrap.isEmpty())
						{
							stillWrap.pop();
						}
						boolean lastNotWrap = codeoptions.notWrappingTags
								.contains(lastTagName);

						if (lastNotWrap && !contains)
						{
							printLine = true;
						} else if (isSmartNoWrap && tagName.length() >= 0)
						{
							printLine = codeoptions.notWrappingTags
									.contains((String) tagNames.peek());
						}
					} else
					{
						lastTagName = ""; //$NON-NLS-1$
					}
					isSmartNoWrap = false;
					continue;
				} else if (typeIndex == HTMLTokenTypes.PERCENT_OPEN)
				{
					if (buf.getCurrentIndentLevel() == 0)
					{
						buf.printIndent();
					}
					buf.print(lexeme.getText());
					if (notFormatted.length() > lexeme.getEndingOffset())
					{
						char charAt = notFormatted.charAt(lexeme
								.getEndingOffset());
						if (Character.isWhitespace(charAt))
						{
							buf.print(charAt);
						}
					}
					continue;
				} else if (typeIndex == HTMLTokenTypes.CDATA_START)
				{
					buf.print(lexeme.getText());
					continue;
				} else if (typeIndex == HTMLTokenTypes.CDATA_END)
				{
					buf.print(lexeme.getText());
					continue;
				} else if (typeIndex == HTMLTokenTypes.PERCENT_GREATER)
				{
					buf.print(' ');
					buf.print(lexeme.getText());
					int pos = lexeme.getEndingOffset();
					while (pos < notFormatted.length())
					{
						char charAt = notFormatted.charAt(pos);
						if (!Character.isWhitespace(charAt))
						{
							break;
						}
						if (charAt == '\r' || charAt == '\n')
						{
							buf.println();
							break;
						}
						pos++;
					}
					continue;
				} else if (typeIndex == HTMLTokenTypes.QUOTE)
				{
					if (isInAttr)
					{
						isInAttr = false;
					}

					inQuote = !inQuote;
					if (inQuote)
					{
						quoteStart = lexeme.getEndingOffset();
					}
					if (!inQuote)
					{
						buf.print(notFormatted.substring(quoteStart, lexeme
								.getStartingOffset()));
					}
					buf.print(lexeme.getText());
					continue;
				} else if (i > 0)
				{
					char charAt = doc.charAt(i);
					if (Character.isWhitespace(charAt))
					{
						// if (charAt != '\n' && charAt != '\r')
						// {
						buf.print(' ');
						// break;
						// }
					}
				}

				buf.print(lexeme.getText());
			}

			if (isInPre)
			{
				String substring = doc.substring(prePositon, doc.length());
				buf.print(substring);
			}

			if (inOtherLanguage)
			{
				String substring = doc.substring(startOtherLanguage, doc
						.length());
				if (!isInAttr)
				{
					formatAnotherLanguage(isSelection, options, project,
							linedelimeters, buf, language, substring);
				}
			}

			if (!codeoptions.doFormatting)
			{
				if (htmlPosition != -1)
				{
					String substring = doc
							.substring(htmlPosition, doc.length());
					buf.print(substring);
				}
			}

			String end = buf.toString();
			if (!isFormattingCorrect(lexemeList, parser, notFormatted, end,
					new int[] { HTMLTokenTypes.TEXT },
					new int[] { HTMLTokenTypes.TEXT }))
			{
				end = notFormatted;
			}

			return end;
		} catch (Exception e)
		{
			IdeLog
					.logError(HTMLPlugin.getDefault(),
							"Unable to format code", e); //$NON-NLS-1$
			return notFormatted;
		}
	}

	private void checkPreviousTrail(String notFormatted, LexemeList lexemeList,
			SourceWriter buf, int a, Lexeme lexeme)
	{
		if (a > 0)
		{
			Lexeme prev = lexemeList.get(a - 1);
			if (prev.getToken().getTypeIndex() == HTMLTokenTypes.STRING)
			{
				if (!Character.isWhitespace(buf.getBuffer().charAt(
						buf.getBuffer().length() - 1)))
				{
					if (Character.isWhitespace(notFormatted
							.charAt(lexeme.offset - 1)))
					{
						buf.print(' ');
					}
				}
			}
		}
	}

	private boolean switchingLanguages(Lexeme lexeme, Lexeme next)
	{
		return next != null && !lexeme.getLanguage().equals(next.getLanguage());
	}

	private void appendPercentText(LexemeList lexemeList,
			HTMLCodeFormatterOptions codeoptions, SourceWriter buf, int a,
			Lexeme lexeme)
	{
		ArrayList withoutEmpty = new ArrayList();
		String[] splitOnLines = splitOnLines(lexeme.getText());
		for (int b = 0; b < splitOnLines.length; b++)
		{
			String string = splitOnLines[b];
			String text = string.trim();
			if (text.length() > 0)
			{
				withoutEmpty.add(text);
			}
		}
		splitOnLines = new String[withoutEmpty.size()];
		withoutEmpty.toArray(splitOnLines);

		if (buf.getCurrentIndentLevel() == 0)
		{
			buf.printIndent();

		}
		if (splitOnLines[0].length() > 0)
		{
			buf.print(splitOnLines[0]);
			if (splitOnLines.length > 1)
			{
				buf.println();
				buf.printIndent();
			}
		}
		for (int b = 1; b < splitOnLines.length; b++)
		{
			String text = splitOnLines[b].trim();
			if (text.length() == 0)
			{

				continue;
			}
			if (b == splitOnLines.length - 1)
			{
				boolean needContinue = false;
				needContinue = shouldInsertNewLine(lexemeList, codeoptions, a,
						needContinue);
				if (needContinue)
				{
					buf.print(text);
					continue;
				}
			}
			if (b != splitOnLines.length - 1)
			{
				buf.println(text);
				buf.printIndent();
			} else
			{
				buf.print(text);
			}
		}
	}

	int level = 0;

	private void appendComment(LexemeList lexemeList,
			HTMLCodeFormatterOptions codeoptions, SourceWriter buf, int a,
			Lexeme lexeme)
	{
		String[] splitOnLines = splitOnLines(lexeme.getText());
		for (int b = 0; b < splitOnLines.length; b++)
		{
			String text = splitOnLines[b].trim();
			if (text.length() == 0)
			{
				continue;
			}
			int eif = countConditionalEnd(text);
			int iif = countConditionalStart(text);
			int delta = iif - eif;
			for (int i = delta; i < 0; i++)
			{
				if (level > 0)
				{
					buf.decreaseIndent();
				}
				level--;
			}
			if (delta != 0 && buf.getCurrentIndentLevel() != 0 && b == 0)
			{
				buf.println();
			}
			if (buf.getCurrentIndentLevel() == 0)
			{
				buf.printIndent();
			}
			for (int i = 0; i < delta; i++)
			{
				level++;
				buf.increaseIndent();
			}

			if (b == splitOnLines.length - 1)
			{
				boolean needContinue = false;
				needContinue = shouldInsertNewLine(lexemeList, codeoptions, a,
						needContinue);
				if (needContinue)
				{
					buf.print(text);
					continue;
				}
			}
			buf.println(text);
		}
	}

	private int countConditionalStart(String text)
	{
		int count = 0;
		int indexOf = text.indexOf("[if"); //$NON-NLS-1$
		while (indexOf != -1)
		{
			boolean shouldInd = isTrueConditional(text, indexOf);
			if (shouldInd)
			{
				count++;
			}
			indexOf = text.indexOf("[if", indexOf + 3); //$NON-NLS-1$
		}
		return count;
	}

	private boolean isTrueConditional(String text, int indexOf)
	{
		boolean shouldInd = false;

		for (int i = indexOf - 1; i > 0; i--)
		{
			char c = text.charAt(i);
			if (c == '!')
			{
				if (i > 0)
				{
					if (text.charAt(i - 1) != '<')
					{
						shouldInd = false;
					} else
					{
						shouldInd = true;
					}
				}
				break;
			}
			if (c == '<')
			{
				if (text.charAt(i + 1) == '[')
				{
					shouldInd = true;
					break;
				}
			}

			if (c != '-')
			{
				shouldInd = false;
				break;
			}

		}
		return shouldInd;
	}

	private int countConditionalEnd(String text)
	{
		int count = 0;
		int indexOf = text.indexOf("[endif]"); //$NON-NLS-1$
		while (indexOf != -1)
		{
			boolean shouldInd = isTrueConditional(text, indexOf);
			if (shouldInd)
			{
				count++;
			}
			indexOf = text.indexOf("[endif]", indexOf + 3); //$NON-NLS-1$
		}
		return count;
	}

	private boolean shouldInsertNewLine(LexemeList lexemeList,
			HTMLCodeFormatterOptions codeoptions, int a, boolean needContinue)
	{
		int ii = a + 1;
		while (ii < lexemeList.size())
		{
			Lexeme lexeme2 = lexemeList.get(ii);
			if (lexeme2.getToken().getTypeIndex() == HTMLTokenTypes.START_TAG)
			{
				String tagName = lexeme2.getText().substring(1).trim();
				if (codeoptions.notWrappingTags.contains(tagName.toLowerCase())
						|| isSelfClosing(a + 1, lexemeList))
				{
					needContinue = true;
					break;
				}
			}
			if (lexeme2.getToken().getTypeIndex() == HTMLTokenTypes.END_TAG)
			{
				String tagName = lexeme2.getText().substring(2).trim();
				if (codeoptions.notWrappingTags.contains(tagName.toLowerCase())
						|| isSelfClosing(a + 1, lexemeList))
				{
					needContinue = true;
					break;
				}
			}
			ii++;
		}
		return needContinue;
	}

	private void formatAnotherLanguage(boolean isSelection, Map options,
			IProject project, String linedelimeters, SourceWriter buf,
			String language, String substring)
	{
		ICodeFormatter formatter = LanguageRegistry.getCodeFormatter(language);
		if (formatter != null)
		{
			String formatted = formatter.format(substring, isSelection,
					options, project, linedelimeters);
			formatted = normalizeCarriageReturns(formatted);
			String[] splitOnLines = formatted.split("\n", -1); //$NON-NLS-1$

			if (splitOnLines.length == 1)
			{
				buf.print(splitOnLines[0]);
			} else
			{
				if (splitOnLines[0].length() > 0)
				{
					if (!Character.isWhitespace(splitOnLines[0].charAt(0)))
					{
						buf.println();
						buf.printIndent();
					}
				}

				buf.println(splitOnLines[0]);

				for (int b = 1; b < splitOnLines.length - 1; b++)
				{
					if (b == splitOnLines.length - 1
							&& splitOnLines[b].trim().length() == 0)
					{
						continue;
					}
					buf.printlnWithIndent(splitOnLines[b]);
				}
				if (splitOnLines.length >= 2
						&& !splitOnLines[splitOnLines.length - 1].equals("")) //$NON-NLS-1$
				{
					buf
							.printlnWithIndent(splitOnLines[splitOnLines.length - 1]);
				}
			}
		} else
		{
			buf.print(substring);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ICodeFormatter#handlesNested()
	 */
	public boolean handlesNested()
	{
		return true;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ICodeFormatter#createNestedMark()
	 */
	public String createNestedMark()
	{
		return "nested" + System.currentTimeMillis(); //$NON-NLS-1$
	}

}
