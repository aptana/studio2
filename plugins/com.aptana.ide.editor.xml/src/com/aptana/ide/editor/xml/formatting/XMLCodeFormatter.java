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
package com.aptana.ide.editor.xml.formatting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.resources.IProject;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.xml.XMLPlugin;
import com.aptana.ide.editor.xml.lexing.XMLTokenTypes;
import com.aptana.ide.editor.xml.parsing.XMLMimeType;
import com.aptana.ide.editor.xml.parsing.XMLParseState;
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
public class XMLCodeFormatter extends BaseFormatter
{

	private Stack<String> tagNames = new Stack<String>();
	private Stack<XMLCodeFormatter> stillWrap = new Stack<XMLCodeFormatter>();
	private XMLCodeFormatterOptions codeoptions;
	private boolean inOtherLanguage;
	private int startOtherLanguage;
	private boolean isInAttr;
	private boolean isSelfClosing;
	private boolean printLine;
	private boolean lastTextEndsWithSpace;
	private boolean isSmartNoWrap;
	private boolean isInTag;
	private boolean incIndent;
	private String string;
	private int position;
	private String lastTagName;
	private String realLastTagName;
	private boolean inElementContent = false;

	private boolean isSelfClosing(int offset, LexemeList list)
	{
		for (int a = offset; a < list.size(); a++)
		{
			Lexeme lexeme = list.get(a);
			if (lexeme.getToken().getTypeIndex() == XMLTokenTypes.GREATER_THAN)
			{
				return false;
			}
			if (lexeme.getToken().getTypeIndex() == XMLTokenTypes.SLASH_GREATER_THAN)
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

	private String[] splitOnLines(String comment, XMLCodeFormatterOptions codeoptions)
	{
		String[] result = null;
		ArrayList<String> lines = new ArrayList<String>();
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
				else
				{
					if (codeoptions != null && codeoptions.preserveReturns)
					{
						if (a > 0 && comment.charAt(a - 1) == '\r')
						{
							continue;
						}
						else
						{
							lines.add(""); //$NON-NLS-1$
						}
					}
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
	 * returns indent on previous line of text
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
	 * @see com.aptana.ide.editors.unified.ICodeFormatter#format(java.lang.String, boolean, java.util.Map,
	 *      org.eclipse.core.resources.IProject, java.lang.String)
	 *  does all actual work
	 */
	public String format(String notFormatted, boolean isSelection, Map options, IProject project, String linedelimeters)
	{
		String doc = null;
		doc = notFormatted;

		tagNames.clear();
		try
		{
			//initializing options
			IParser parser = LanguageRegistry.getParser(XMLMimeType.MimeType);
			XMLParseState createParseState = (XMLParseState) parser.createParseState(null);
			createParseState.setEditState(doc, doc, 0, 0);
			parser.parse(createParseState);
			LexemeList lexemeList = createParseState.getLexemeList();
			Stack<Lexeme> clevel = new Stack<Lexeme>();
			HashSet<Lexeme> notClosed = new HashSet<Lexeme>();
			fillNotClosed(lexemeList, clevel, notClosed);
			codeoptions = new XMLCodeFormatterOptions(options, project);
			// if we should not format returning
			if (!codeoptions.doFormatting)
			{
				return notFormatted;
			}
			SourceWriter buf = createSourceWriter(linedelimeters);
			String language = init();
			for (int a = 0; a < lexemeList.size(); a++)
			{
				Lexeme lexeme = lexemeList.get(a);
				int i = lexeme.getStartingOffset() - 1;
				int ti = lexeme.getToken().getTypeIndex();
				int typeIndex = ti;
				boolean isInMultiline = false;
				Lexeme oldLexeme = null;
				//preserving lines if needed
				if (a > 0 && codeoptions.preserveReturns)
				{
					oldLexeme = lexemeList.get(a - 1);
					int count = calcLines(notFormatted, oldLexeme.getEndingOffset(), lexeme.getStartingOffset());
					isInMultiline = printEmptyLines(buf, ti, isInMultiline, count);
				}
				//check if we are returning from other language - unused now.
				processOtherLanguage(isSelection, options, project, linedelimeters, doc, buf, language, lexeme);
				//check if we need to switch to other language
				if (!lexeme.getLanguage().equals(XMLMimeType.MimeType))
				{
					language = onOtherLanguage(notFormatted, lexemeList, buf, language, a, lexeme);
					continue;
				}				
				if (typeIndex == XMLTokenTypes.NAME)
				{
					onName(buf, lexeme, isInMultiline);
					continue;
				}
				if (typeIndex == XMLTokenTypes.LBRACKET)
				{
					buf.print(' ');
					buf.print(lexeme.getText());
					buf.println();
					buf.increaseIndent();				
					continue;
				}
				if (typeIndex == XMLTokenTypes.RBRACKET)
				{
					buf.print(lexeme.getText());
					buf.decreaseIndent();				
					continue;
				}
				if (typeIndex == XMLTokenTypes.ELEMENT_DECL)
				{
					buf.printIndent();
					buf.print(lexeme.getText());
					continue;
				}
				if (typeIndex == XMLTokenTypes.LPAREN)
				{
					buf.print(' ');
					buf.print(lexeme.getText());
					inElementContent  = true;
					continue;
				}
				if (typeIndex == XMLTokenTypes.RPAREN)
				{
					buf.print(lexeme.getText());
					inElementContent = false;
					continue;
				}
				//will be never called
				if (typeIndex == XMLTokenTypes.PI_TEXT)
				{
					onPItext(options, project, linedelimeters, buf, lexeme);
					continue;
				}
				if (typeIndex == XMLTokenTypes.PI_OPEN)
				{
					onPIOpen(buf, lexeme);
					continue;
				}
				if (typeIndex == XMLTokenTypes.QUESTION_GREATER_THAN)
				{
					onQuestionGreater(buf, lexeme);
					isInTag = false;
					continue;
				}
				if (typeIndex == XMLTokenTypes.EQUAL)
				{
					onEqual(buf, lexeme);
					continue;
				}
				if (typeIndex == XMLTokenTypes.STRING)
				{
					onString(buf, lexeme, oldLexeme);
					continue;
				}
				if (typeIndex == XMLTokenTypes.ERROR)
				{
					onError(lexemeList, buf, a, lexeme);
					continue;
				}
				if (typeIndex == XMLTokenTypes.COMMENT)
				{
					appendComment(lexemeList, codeoptions, buf, a, lexeme, notFormatted);
					continue;
				}
				if (typeIndex == XMLTokenTypes.CDATA_TEXT)
				{
					onCData(lexemeList, buf, a, lexeme);
					continue;
				}
				if (typeIndex == XMLTokenTypes.TEXT)
				{
					onText(lexemeList, buf, a, lexeme);
					continue;
				}
				if (typeIndex == XMLTokenTypes.GREATER_THAN)
				{
					onGreaterThan(lexemeList, buf, a, lexeme);
					// No longer inside a start tag (if we were inside one before)
					isInTag = false;
					continue;
				}
				if (typeIndex == XMLTokenTypes.SLASH_GREATER_THAN)
				{
					conSlashGreaterThan(buf);
					continue;
				}
				if (typeIndex == XMLTokenTypes.START_TAG)
				{
					onStarTag(lexemeList, notClosed, buf, a, lexeme);
					continue;
				}
				else if (typeIndex == XMLTokenTypes.END_TAG)
				{
					onEndTag(buf, lexeme);
					continue;
				}
				else if (typeIndex == XMLTokenTypes.CDATA_START)
				{
					int iLevel = buf.getCurrentIndentLevel();
					if (iLevel == 0)
					{
						buf.printIndent();
					}
					buf.print(lexeme.getText());
					continue;
				}
				else if (typeIndex == XMLTokenTypes.XML_DECL)
				{
					isInTag = true;
				}
				else if (typeIndex == XMLTokenTypes.CDATA_END)
				{
					int iLevel = buf.getCurrentIndentLevel();
					if (iLevel == 0 && !codeoptions.preserveWhitespacesInCDATA)
					{
						buf.printIndent();
					}
					buf.print(lexeme.getText());
					continue;
				}
				else if (i > 0)
				{
					onOther(doc, buf, i, typeIndex);
				}

				buf.print(lexeme.getText());
			}
			String end = postProcessing(notFormatted, isSelection, options, project, linedelimeters, doc, parser,
					lexemeList, buf, language);
			return end;
		}
		catch (Exception e)
		{
			IdeLog.logError(XMLPlugin.getDefault(), "Unable to format code", e); //$NON-NLS-1$
			return notFormatted;
		}
	}

	private SourceWriter createSourceWriter(String linedelimeters)
	{
		String indent = createInitialIndent();						
		SourceWriter buf = new SourceWriter(0, indent, codeoptions.tabSize);
		if (linedelimeters != null)
		{
			buf.setLineDelimeter(linedelimeters);
		}
		return buf;
	}

	/**
	 * creates intent string
	 * @return
	 */
	private String createInitialIndent()
	{
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
		return indent;
	}

	private void onText(LexemeList lexemeList, SourceWriter buf, int a, Lexeme lexeme)
	{
		boolean isPreserving = codeoptions != null && !codeoptions.preserveReturns;
		boolean needContinue = false;
		needContinue = processSmartNoWrapIfNeeded(buf, lexeme, needContinue);
		if (!needContinue)
		{

			if (isPreserving)
			{
				String[] splitOnLines = splitOnLines(lexeme.getText(), codeoptions);
				boolean hasNotEmpty = false;
				ArrayList<String> withoutEmpty = new ArrayList<String>();
				boolean lastHasSpace = false;
				hasNotEmpty = wormWithoutEmptyStrings(buf, splitOnLines, hasNotEmpty, withoutEmpty,
						lastHasSpace);
				splitOnLines = new String[withoutEmpty.size()];
				withoutEmpty.toArray(splitOnLines);
				if (hasNotEmpty)
				{
					printPreservingHelper(lexemeList, buf, a, splitOnLines);
				}							
			}
			else
			{
				printTextPreserving(lexemeList, buf, a, lexeme);
			}
		}
	}

	private boolean wormWithoutEmptyStrings(SourceWriter buf, String[] splitOnLines, boolean hasNotEmpty,
			ArrayList<String> withoutEmpty, boolean lastHasSpace)
	{
		for (int b = 0; b < splitOnLines.length; b++)
		{
			String string = splitOnLines[b];
			String text = string.trim();
			boolean hasSpace = testHasSpace(string);
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
						String last = withoutEmpty.get(withoutEmpty.size() - 1);
						if (length > 0)
						{
							last = last + ' ';
						}
						withoutEmpty.set(withoutEmpty.size() - 1, last);
					}
				}
			}
			if (text.length() > 0 || (codeoptions.preserveReturns && b != 0))
			{
				if (!hasNotEmpty)
				{
					if (!lastTextEndsWithSpace)
					{
						if (buf.getCurrentIndentLevel() != 0)
						{
							if (Character.isWhitespace(string.charAt(0)))
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
		return hasNotEmpty;
	}

	private boolean processSmartNoWrapIfNeeded(SourceWriter buf, Lexeme lexeme, boolean needContinue)
	{
		if (isSmartNoWrap)
		{
			String txt = lexeme.getText();
			String[] splitOnLines = splitOnLines(txt, null);
			if (splitOnLines.length == 1)
			{
				buf.print(splitOnLines[0]);
				needContinue = true;

			}
			else if (splitOnLines.length == 2 && splitOnLines[1].trim().length() == 0)
			{
				buf.print(splitOnLines[0]);
				needContinue = true;
			}
		}
		return needContinue;
	}

	private void onPItext(Map options, IProject project, String linedelimeters, SourceWriter buf, Lexeme lexeme)
	{
		if (!isInAttr)
		{
			formatAnotherLanguage(false, options, project, linedelimeters, buf, "text/php", lexeme.getText()); //$NON-NLS-1$
		}
		position = -1;
	}

	private boolean testHasSpace(String string)
	{
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
		return hasSpace;
	}

	private boolean printPreservingHelper(LexemeList lexemeList, SourceWriter buf, int a, String[] splitOnLines)
	{
		boolean nextIndent = true;
		if (buf.getCurrentIndentLevel() == 0 && !codeoptions.preserveReturns)
		{
			buf.printIndent();
			nextIndent = false;
		}
		if (splitOnLines[0].length() > 0 || codeoptions.preserveReturns)
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
			if (text.length() == 0 && !codeoptions.preserveReturns)
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
				needContinue = shouldInsertNewLine(lexemeList, codeoptions, a, needContinue);
				needContinue = true;
				if (needContinue)
				{
					buf.print(text);
					continue;
				}
			}
			buf.println(text);
			nextIndent = true;
		}
		return nextIndent;
	}

	private void printTextPreserving(LexemeList lexemeList, SourceWriter buf, int a, Lexeme lexeme)
	{
		String[] splitOnLines = splitOnLines(lexeme.getText(), codeoptions);
		boolean bl = buf.getCurrentIndentLevel() == 0;
		for (int b = 0; b < splitOnLines.length; b++)
		{
			String trim = splitOnLines[b].trim();
			if (b == 0 && trim.length() == 0 && buf.getCurrentIndentLevel() == 0 && a != 0)
			{
				//empty line
				continue;
			}
			if (b == splitOnLines.length - 1 && trim.length() == 0 && buf.getCurrentIndentLevel() == 0)
			{
				//last line
				continue;
			}
			if (bl)
			{
				buf.printIndent();
			}
			bl = true;
			if (b == splitOnLines.length - 1)
			{
				boolean needContinue = false;
				needContinue = shouldInsertNewLine(lexemeList, codeoptions, a, needContinue);
				//we should just print it
				if (needContinue)
				{
					buf.print(trim);
					continue;
				}
			}
			buf.println(trim);
		}
	}

	private void onName(SourceWriter buf, Lexeme lexeme, boolean isInMultiline)
	{
		if (isInMultiline)
		{
			for (int b = 0; b < codeoptions.spacesInMultiline; b++)
			{
				buf.print(' ');
			}
		}
		else
		{
			if (!inElementContent) 
				buf.print(' ');
		}
		buf.print(lexeme.getText());
	}

	private void onPIOpen(SourceWriter buf, Lexeme lexeme)
	{
		int iLevel = buf.getCurrentIndentLevel();
		if (iLevel != 0)
		{
			buf.println();
		}
		buf.printIndent();
		buf.print(lexeme.getText());
		// interior text will handle own carriage returns
	}

	private void onQuestionGreater(SourceWriter buf, Lexeme lexeme)
	{
		// interior text will handle own carriage returns
		buf.print(lexeme.getText());
		buf.println();
	}

	private void onEqual(SourceWriter buf, Lexeme lexeme)
	{
		if (buf.getCurrentIndentLevel() == buf.getCurrentIndentationLevel())
		{
			buf.print(' ');
		}
		buf.print(lexeme.getText());
	}

	private void onString(SourceWriter buf, Lexeme lexeme, Lexeme oldLexeme)
	{
		boolean shouldPrintSpace = buf.getCurrentIndentLevel() == buf.getCurrentIndentationLevel() || oldLexeme != null
				&& oldLexeme.getToken().getTypeIndex() != XMLTokenTypes.EQUAL;
		if (shouldPrintSpace)
		{
			buf.print(' ');
		}
		buf.print(lexeme.getText());
	}

	private void onCData(LexemeList lexemeList, SourceWriter buf, int a, Lexeme lexeme)
	{
		if (codeoptions.preserveWhitespacesInCDATA)
		{
			buf.print(lexeme.getText());
		}
		else
		{
			appendCDATA(lexemeList, codeoptions, buf, a, lexeme);
		}
	}

	private void onError(LexemeList lexemeList, SourceWriter buf, int a, Lexeme lexeme)
	{
		if (!isInTag)
		{
			if (!shouldNotWrap(lastTagName))
			{
				if (buf.getCurrentIndentLevel() != 0)
				{
					buf.println();
				}
				buf.printIndent();
			}

		}
		buf.print(lexeme.getText());
		if (!isInTag)
		{
			if (!shouldNotWrap(lastTagName))
			{
				if (a < lexemeList.size() - 1 && lexemeList.get(a + 1).getToken().getTypeIndex() != XMLTokenTypes.ERROR)
				{
					buf.println();
				}
			}
			else
			{
				if (a < lexemeList.size() - 1 && lexemeList.get(a + 1).getToken().getTypeIndex() != XMLTokenTypes.ERROR)
				{
					buf.print(' ');
				}
			}
		}
	}

	private String onOtherLanguage(String notFormatted, LexemeList lexemeList, SourceWriter buf, String language,
			int a, Lexeme lexeme)
	{
		if (!inOtherLanguage)
		{
			if (!codeoptions.doFormatting)
			{
				String txt = notFormatted.substring(position, lexeme.getStartingOffset());
				string = getCurrentIndentationString(txt);
				buf.print(txt);
				position = -1;
			}
			inOtherLanguage = true;

			Lexeme previous = a > 0 ? lexemeList.get(a - 1) : null;
			// isInAttr = previous != null ? (previous.getToken().getTypeIndex() == XMLTokenTypes.QUOTE)
			// : false;
			//						
			if (previous != null)
			{
				startOtherLanguage = previous.getEndingOffset();
			}
			else
			{
				startOtherLanguage = lexeme.getStartingOffset();
			}

			language = lexeme.getLanguage();

		}
		return language;
	}

	private void conSlashGreaterThan(SourceWriter buf)
	{
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

		// No longer inside a start tag (if we were inside one before)
		isInTag = false;
	}

	private void processOtherLanguage(boolean isSelection, Map options, IProject project, String linedelimeters,
			String doc, SourceWriter buf, String language, Lexeme lexeme)
	{
		if (inOtherLanguage && lexeme.getLanguage().equals(XMLMimeType.MimeType))
		{
			inOtherLanguage = false;
			String substring = doc.substring(startOtherLanguage, lexeme.getStartingOffset());

			// only format non-attribute text
			if (!isInAttr)
			{
				formatAnotherLanguage(isSelection, options, project, linedelimeters, buf, language, substring);

				if (string != null)
				{
					buf.print(string);
					string = null;
				}
			}
		}
	}

	private String init()
	{
		inOtherLanguage = false;
		startOtherLanguage = -1;
		String language = null;
		isInAttr = false;
		isSelfClosing = false;
		printLine = false;
		lastTextEndsWithSpace = false;
		isSmartNoWrap = false;
		isInTag = false;
		incIndent = false;
		string = null;
		position = -1;
		lastTagName = ""; //$NON-NLS-1$
		realLastTagName = ""; //$NON-NLS-1$
		return language;
	}

	private void onOther(String doc, SourceWriter buf, int i, int typeIndex)
	{
		if (inElementContent) return;
		char charAt = doc.charAt(i);
		if (Character.isWhitespace(charAt))
		{
			if (typeIndex != XMLTokenTypes.DOCTYPE_DECL && typeIndex != XMLTokenTypes.ATTLIST_DECL
					&& typeIndex != XMLTokenTypes.ELEMENT_DECL && typeIndex != XMLTokenTypes.ENTITY_DECL
					&& typeIndex != XMLTokenTypes.XML_DECL)
			{
				buf.print(' ');
			}
		}
	}

	private String postProcessing(String notFormatted, boolean isSelection, Map options, IProject project,
			String linedelimeters, String doc, IParser parser, LexemeList lexemeList, SourceWriter buf, String language)
	{
		if (inOtherLanguage)
		{
			String substring = doc.substring(startOtherLanguage, doc.length());
			if (!isInAttr)
			{
				formatAnotherLanguage(isSelection, options, project, linedelimeters, buf, language, substring);
			}
		}

		if (!codeoptions.doFormatting)
		{
			if (position != -1)
			{
				String substring = doc.substring(position, doc.length());
				buf.print(substring);
			}
		}

		String end = buf.toString();
		if (!isFormattingCorrect(lexemeList, parser, notFormatted, end, new int[] { XMLTokenTypes.TEXT,
				XMLTokenTypes.CDATA_TEXT }, new int[] { XMLTokenTypes.TEXT, XMLTokenTypes.CDATA_TEXT }))
		{
			end = notFormatted;
		}
		return end;
	}

	private void onEndTag(SourceWriter buf, Lexeme lexeme)
	{
		String pTag = lastTagName;

		int iLevel = buf.getCurrentIndentLevel();
		String tagName = lexeme.getText().substring(2).trim();
		boolean isBroken = !(pTag.equals(tagName) || pTag.length() == 0);
		lastTagName = tagName;

		boolean contains = shouldNotWrap(tagName);
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

		// if
		// (!codeoptions.notWrappingTags.contains(lastTagName)){
		if (!isBroken)
		{
			buf.decreaseIndent();
		}
		// }

		if (iLevel == 0)
		{
			buf.printIndent();
		}
		buf.print(lexeme.getText());
		if (!tagNames.isEmpty() && !isBroken)
		{
			lastTagName = tagNames.pop();
			if (!stillWrap.isEmpty())
			{
				stillWrap.pop();
			}
			boolean lastNotWrap = shouldNotWrap(lastTagName);

			if (lastNotWrap && !contains)
			{
				printLine = true;
			}
			else if (isSmartNoWrap && (tagName.length() > 0))
			{
				printLine = codeoptions.notWrappingTags.contains(tagNames.peek());
			}
		}
		else
		{
			lastTagName = ""; //$NON-NLS-1$
		}
		isSmartNoWrap = false;
	}

	private void onStarTag(LexemeList lexemeList, HashSet<Lexeme> notClosed, SourceWriter buf, int a, Lexeme lexeme)
	{
		isInTag = true;
		String tagName = lexeme.getText().substring(1).trim();

		int iL = buf.getCurrentIndentLevel();

		realLastTagName = tagName;
		boolean selfClosing = isSelfClosing(a, lexemeList);
		isSelfClosing = selfClosing;

		boolean wrap = !shouldNotWrap(tagName) && !selfClosing;
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
			if (true)
			{
				if (!notClosed.contains(lexeme))
				{
					incIndent = true;
					tagNames.push(lastTagName);
					stillWrap.push(null);
					lastTagName = tagName;
				}
			}
			else
			{
				isSelfClosing = true;
			}
		}
		printLine = false;
	}

	private void onGreaterThan(LexemeList lexemeList, SourceWriter buf, int a, Lexeme lexeme)
	{
		buf.print('>');
		if (incIndent)
		{
			buf.increaseIndent();
			incIndent = false;
		}
		boolean keepWithNext = false;
		if (!isInTag && a + 1 < lexemeList.size())
		{
			Lexeme next = lexemeList.get(a + 1);
			if (next != null && isLexemeOfType(next, XMLTokenTypes.TEXT)
					&& (!next.getText().startsWith(" ") && !next.getText().startsWith("\t") //$NON-NLS-1$ //$NON-NLS-2$
							&& !next.getText().startsWith("\r") && !next.getText().startsWith("\n"))) //$NON-NLS-1$ //$NON-NLS-2$
			{
				if (isInTag)
				{
					keepWithNext = true;
				}
			}
		}

		if (isInTag && a + 1 < lexemeList.size())
		{
			Lexeme next = lexemeList.get(a + 1);
			keepWithNext = switchingLanguages(lexeme, next);
		}

		boolean wrapContent = !shouldNotWrap(lastTagName);
		boolean alwaysWrapAfterwards = codeoptions.allwaysWrap.contains(realLastTagName);
		isSmartNoWrap = isSmartNoWrap(lexemeList, a, codeoptions);
		if (!isSmartNoWrap)
		{
			// if I am just finishing a start tag and I don't wrap interior content, or I should print a
			// line
			if ((wrapContent || printLine) && !keepWithNext)
			{
				// if I am not self closing, or I must always wrap
				if (!isSelfClosing || alwaysWrapAfterwards)
				{
					buf.println();
				}
			}
			else if (alwaysWrapAfterwards)
			{
				buf.println();
				stillWrap.push(this);
			}
		}
	}

	private void fillNotClosed(LexemeList lexemeList, Stack<Lexeme> clevel, HashSet<Lexeme> notClosed)
	{
		for (int a = 0; a < lexemeList.size(); a++)
		{
			Lexeme lexeme = lexemeList.get(a);
			if (lexeme.getToken().getTypeIndex() == XMLTokenTypes.START_TAG)
			{
				if (!isSelfClosing(a, lexemeList))
				{
					clevel.push(lexeme);
				}
			}
			if (lexeme.getToken().getTypeIndex() == XMLTokenTypes.END_TAG)
			{
				if (!clevel.isEmpty())
				{
					Lexeme lex = clevel.pop();
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
	}

	private boolean printEmptyLines(SourceWriter buf, int ti, boolean isInMultiline, int count)
	{
		for (int b = 0; b < count; b++)
		{
			isInMultiline = true;
			if (b == 0 && buf.getCurrentIndentLevel() == 0)
			{
				continue;
			}
			buf.println();
			if (b != count - 1 || (ti != XMLTokenTypes.START_TAG && ti != XMLTokenTypes.END_TAG))
			{
				buf.printIndent();
			}
		}
		return isInMultiline;
	}

	private boolean isSmartNoWrap(LexemeList lexemeList, int a, XMLCodeFormatterOptions codeoptions2)
	{
		if (codeoptions2.doNotWrapSimple)
		{
			if (a > 0)
			{
				Lexeme fL = lexemeList.get(a - 1);
				int typeIndex = fL.getToken().getTypeIndex();
				if (typeIndex == XMLTokenTypes.END_TAG)
				{
					return false;
				}
			}
			else
			{
				return false;
			}
			a++;

			while (a < lexemeList.size())
			{
				Lexeme lexeme = lexemeList.get(a);
				int typeIndex = lexeme.getToken().getTypeIndex();
				if (typeIndex != XMLTokenTypes.TEXT)
				{
					if (typeIndex == XMLTokenTypes.END_TAG)
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

	private boolean shouldNotWrap(String lastTagName)
	{
		return codeoptions.notWrappingTags.contains(lastTagName);
	}

	private int calcLines(String notFormatted, int endingOffset, int startingOffset)
	{
		char lp = ' ';
		int count = 0;
		for (int a = endingOffset; a < startingOffset; a++)
		{
			char c = notFormatted.charAt(a);
			if (c == '\r')
			{
				count++;
			}
			if (c == '\n')
			{
				if (lp != '\r')
				{
					count++;
				}
			}
			lp = c;
		}
		return count;
	}

	private boolean switchingLanguages(Lexeme lexeme, Lexeme next)
	{
		return next != null && !lexeme.getLanguage().equals(next.getLanguage());
	}


	private void appendComment(LexemeList lexemeList, XMLCodeFormatterOptions codeoptions, SourceWriter buf, int a,
			Lexeme lexeme, String notFormatted)
	{
		if (a > 0 && !codeoptions.preserveReturns)
		{
			Lexeme prev = lexemeList.get(a - 1);
			if (prev.getToken().getTypeIndex() == XMLTokenTypes.TEXT)
			{
				String text = prev.getText();
				for (int b = text.length() - 1; b > 0; b--)
				{
					char c = text.charAt(b);
					if (!Character.isWhitespace(c))
					{
						break;
					}
					if (c == '\r' || c == '\n')
					{
						buf.println();
						break;
					}
				}
			}
		}
		String[] splitOnLines = splitOnLines(lexeme.getText(), codeoptions);
		for (int b = 0; b < splitOnLines.length; b++)
		{

			String text = splitOnLines[b].trim();
			if (text.length() == 0 && !codeoptions.preserveReturns)
			{
				continue;
			}
			if (buf.getCurrentIndentLevel() == 0)
			{
				buf.printIndent();
			}
			if (b == splitOnLines.length - 1)
			{
				boolean needContinue = codeoptions.preserveReturns;
				if (!needContinue)
				{
					needContinue = shouldInsertNewLine(lexemeList, codeoptions, a, needContinue);
					if (a < lexemeList.size() - 1)
					{
						Lexeme ll = lexemeList.get(a + 1);
						if (ll.getToken().getTypeIndex() == XMLTokenTypes.TEXT)
						{
							String txt = ll.getText();
							for (int i = 0; i < txt.length(); i++)
							{
								char c = txt.charAt(i);
								if (!Character.isWhitespace(c))
								{
									needContinue = true;
									break;
								}
								if (c == '\r' || c == '\n')
								{
									break;
								}
							}
						}
					}
				}
				if (needContinue)
				{
					buf.print(text);
					continue;
				}
			}
			buf.println(text);
		}
	}

	private void appendCDATA(LexemeList lexemeList, XMLCodeFormatterOptions codeoptions, SourceWriter buf, int a,
			Lexeme lexeme)
	{
		String[] splitOnLines = splitOnLines(lexeme.getText(), null);

		for (int b = 0; b < splitOnLines.length; b++)
		{
			String text = splitOnLines[b].trim();
			if (b == 0)
			{
				if (splitOnLines[b].length() > 0)
				{
					if (Character.isWhitespace(splitOnLines[b].charAt(0)))
					{
						text = ' ' + text;
					}
				}
				else
				{
					buf.println(" "); //$NON-NLS-1$
				}
			}
			if (text.length() == 0)
			{
				continue;
			}
			StringBuffer copy = new StringBuffer();
			boolean lSpace = false;
			for (int i = 0; i < text.length(); i++)
			{
				char c = text.charAt(i);
				if (c == ' ' || c == '\t')
				{
					if (lSpace)
					{
						continue;
					}
					else
					{
						lSpace = true;
					}
				}
				else
				{
					lSpace = false;
				}
				copy.append(c);
			}
			text = copy.toString();
			if (buf.getCurrentIndentLevel() == 0)
			{
				buf.printIndent();
			}
			if (b == splitOnLines.length - 1)
			{
				boolean needContinue = false;
				needContinue = shouldInsertNewLine(lexemeList, codeoptions, a, needContinue);
				if (needContinue)
				{
					buf.print(text);
					continue;
				}
			}
			buf.println(text);
		}
	}

	private boolean shouldInsertNewLine(LexemeList lexemeList, XMLCodeFormatterOptions codeoptions, int a,
			boolean needContinue)
	{
		int ii = a + 1;
		while (ii < lexemeList.size())
		{
			Lexeme lexeme2 = lexemeList.get(ii);
			if (lexeme2.getToken().getTypeIndex() == XMLTokenTypes.COMMENT && codeoptions.preserveReturns)
			{
				needContinue = true;
				break;
			}
			if (lexeme2.getToken().getTypeIndex() == XMLTokenTypes.START_TAG)
			{
				String tagName = lexeme2.getText().substring(1).trim();
				if (codeoptions.notWrappingTags.contains(tagName) || isSelfClosing(a + 1, lexemeList))
				{
					needContinue = true;
					break;
				}
			}
			if (lexeme2.getToken().getTypeIndex() == XMLTokenTypes.END_TAG)
			{
				String tagName = lexeme2.getText().substring(2).trim();
				if (codeoptions.notWrappingTags.contains(tagName) || isSelfClosing(a + 1, lexemeList))
				{
					needContinue = true;
					break;
				}
			}
			ii++;
		}
		return needContinue;
	}

	
	/**
	 * Currently unused but keeped here because it may be reused later. 
	 * @param isSelection
	 * @param options
	 * @param project
	 * @param linedelimeters
	 * @param buf
	 * @param language
	 * @param substring
	 */
	void formatAnotherLanguage(boolean isSelection, Map options, IProject project, String linedelimeters,
			SourceWriter buf, String language, String substring)
	{
		ICodeFormatter formatter = LanguageRegistry.getCodeFormatter(language);
		if (formatter != null)
		{
			String formatted = formatter.format(substring, isSelection, options, project, linedelimeters);
			formatted = normalizeCarriageReturns(formatted);
			String[] splitOnLines = formatted.split("\n", -1); //$NON-NLS-1$

			buf.println(splitOnLines[0]);
			for (int b = 1; b < splitOnLines.length - 1; b++)
			{
				buf.printlnWithIndent(splitOnLines[b]);
			}
			if (splitOnLines.length >= 2 && !splitOnLines[splitOnLines.length - 1].equals("")) //$NON-NLS-1$
			{
				buf.printlnWithIndent(splitOnLines[splitOnLines.length - 1]);
			}
		}
		else
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
