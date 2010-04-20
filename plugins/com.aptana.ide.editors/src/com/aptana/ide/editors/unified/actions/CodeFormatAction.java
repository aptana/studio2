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
package com.aptana.ide.editors.unified.actions;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.formatting.BasicCodeFormatterConstants;
import com.aptana.ide.editors.unified.ICodeFormatter;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Pavel Petrochenko
 */
public class CodeFormatAction extends Action implements IEditorActionDelegate, IWorkbenchWindowActionDelegate
{

	private static final String TEXT_HTML = "text/html"; //$NON-NLS-1$

	private IUnifiedEditor part = null;

	private String lineSeparator = System.getProperty("line.separator"); //$NON-NLS-1$

	private int tabWitdh = 4;

	private String tabChar = ""; //$NON-NLS-1$

	/**
	 * 
	 */
	public CodeFormatAction()
	{
		this.setText(Messages.CodeFormatAction_CodeFormatTitle);
		this.setActionDefinitionId(UnifiedActionContributor.CODE_FORMAT_ACTION_ID);
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{
		// checking selection
		ISelectionProvider selectionProvider = part.getViewer().getSelectionProvider();
		TextSelection selection = (TextSelection) selectionProvider.getSelection();
		LexemeList list = part.getFileContext().getParseState().getLexemeList();
		StyledText textWidget = part.getViewer().getTextWidget();
		int caretOffset = textWidget.getCaretOffset();
		int lsIndex = list.getLexemeFloorIndex(caretOffset);
		int offsetFromLex = 0;
		if (lsIndex != -1)
		{
			offsetFromLex = caretOffset - list.get(lsIndex).getEndingOffset();
		}

		// saves the line offset to be restored after formatting
		int lineOffset = textWidget.getOffsetAtLine(textWidget.getTopIndex());
		int lineIndex = list.getLexemeFloorIndex(lineOffset);
		int lineOffsetFromLex = 0;
		if (lineIndex != -1)
		{
			lineOffsetFromLex = lineOffset - list.get(lineIndex).getEndingOffset();
		}

		IDocument document = part.getViewer().getDocument();

		if (document instanceof IDocumentExtension4)
		{
			IDocumentExtension4 ext = (IDocumentExtension4) document;
			lineSeparator = ext.getDefaultLineDelimiter();
		}
		try
		{
			lineSeparator = document.getLineDelimiter(0);
		}
		catch (BadLocationException e1)
		{
			// silently ignore
		}
		adjustTabwidthAndTabChar();
		IEditorInput editorInput = part.getEditorInput();
		IProject project = null;
		if (editorInput instanceof IFileEditorInput)
		{
			IFileEditorInput fl = (IFileEditorInput) editorInput;
			project = fl.getFile().getProject();
		}

		if (selection.getLength() == 0 || selection.getLength() == document.getLength())
		{
			formatWhole(selection, document, project);
		}
		else
		{
			formatSelection(selection, document, project);
		}
		super.run();
		LexemeList lexemeList = part.getFileContext().getParseState().getLexemeList();
		if (lexemeList.size() > lsIndex)
		{
			if (lsIndex != -1)
			{
				Lexeme lexeme = lexemeList.get(lsIndex);
				int initialOffset = lexeme.getEndingOffset();
				int endOffset = initialOffset;
				if (lsIndex < lexemeList.size() - 1)
				{
					endOffset = lexemeList.get(lsIndex + 1).getStartingOffset();
				}

				initialOffset = Math.min(initialOffset + offsetFromLex, endOffset);
				if (lineSeparator != null && lineSeparator.length() > 1)
				{
					try
					{
						char charAtOffset = document.getChar(initialOffset);
						if (charAtOffset == lineSeparator.charAt(1))
						{
							// we are inside of multiline line delimeter
							if (initialOffset > 0)
							{
								initialOffset--;
							}
							else
							{
								initialOffset++;
							}
						}
					}
					catch (BadLocationException e)
					{
						// should not ever happen
						IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), e.getMessage(), e);
					}
				}
				textWidget.setCaretOffset(initialOffset);
			}
		}

		if (lineIndex > -1 && lineIndex < lexemeList.size())
		{
			// scrolls back the original top line index
			Lexeme lexeme = lexemeList.get(lineIndex);
			int initialOffset = lexeme.getEndingOffset();
			int endOffset = initialOffset;
			if (lineIndex < lexemeList.size() - 1)
			{
				endOffset = lexemeList.get(lineIndex + 1).getStartingOffset();
			}
			initialOffset = Math.min(initialOffset + lineOffsetFromLex, endOffset);
			textWidget.setTopIndex(textWidget.getLineAtOffset(initialOffset));
		}

		part.getViewer().getTextWidget().redraw();
	}

	private void adjustTabwidthAndTabChar()
	{
		String defaultLanguage = part.getFileContext().getDefaultLanguage();
		String pluginId = null;
		// THIS IS WRONG BY DESIGN SHOULD BE CHANGED LATER
		if (defaultLanguage.equals("text/html")) { //$NON-NLS-1$
			pluginId = "com.aptana.ide.editor.html"; //$NON-NLS-1$
		}
		else if (defaultLanguage.equals("text/javascript")) { //$NON-NLS-1$
			pluginId = "com.aptana.ide.editor.js"; //$NON-NLS-1$
		}
		else if (defaultLanguage.equals("text/css")) { //$NON-NLS-1$
			pluginId = "com.aptana.ide.editor.css"; //$NON-NLS-1$
		}
		else if (defaultLanguage.equals("text/php")) { //$NON-NLS-1$
			pluginId = "com.aptana.ide.editor.php"; //$NON-NLS-1$
		}		
		
		if (pluginId != null)
		{
			tabWitdh = Platform.getPreferencesService().getInt(pluginId, BasicCodeFormatterConstants.FORMATTER_TAB_SIZE, 4, null);
			tabChar = Platform.getPreferencesService().getString(pluginId, BasicCodeFormatterConstants.FORMATTER_TAB_CHAR, "", null); //$NON-NLS-1$
		}
	}

	private void formatWhole(TextSelection selection, IDocument document, IProject project)
	{
		IParseNode parseResults = part.getFileContext().getParseState().getParseResults();
		if (parseResults == null)
		{
			return;
		}
		String language = parseResults.getLanguage();
		ICodeFormatter codeFormatter = LanguageRegistry.getCodeFormatter(language);

		if (codeFormatter == null)
		{
			return;
		}
		String content = document.get();

		if (!codeFormatter.handlesNested())
		{
			ArrayList languageList = new ArrayList();
			checkNodes(languageList, parseResults);
			// DIRTY HACK
			// TODO TO BE REMOVED AFTER REPLACING TIDY

			boolean hasPHP = false;
			for (int a = 0; a < languageList.size(); a++)
			{
				IParseNode object = (IParseNode) languageList.get(a);
				if (object.getLanguage().equals("text/php")) { //$NON-NLS-1$
					hasPHP = true;
					break;
				}
			}
			StringBuffer clearedContent = new StringBuffer();
			int prev = 0;
			String nestedMark = codeFormatter.createNestedMark();
			for (int a = 0; a < languageList.size(); a++)
			{
				IParseNode pnode = (IParseNode) languageList.get(a);
				if (pnode.getStartingOffset() <= prev)
				{
					IdeLog
							.logError(
									UnifiedEditorsPlugin.getDefault(),
									StringUtils
											.format(
													"Nodes seems to have wrong offset while formatting following content: {0} Node offset:{1} Prev offset:{2}", new String[] { content, pnode.getStartingOffset() + "", prev + "" })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					return;
				}
				clearedContent.append(content.substring(prev, pnode.getStartingOffset()));
				prev = pnode.getEndingOffset();
				clearedContent.append(nestedMark);
			}
			if (prev < content.length())
			{
				clearedContent.append(content.substring(prev));
			}
			String formattedContentNotMixed = codeFormatter.format(clearedContent.toString(), hasPHP, null, project,
					lineSeparator);
			for (int a = 0; a < languageList.size(); a++)
			{
				IParseNode pnode = (IParseNode) languageList.get(a);

				int indexOf = formattedContentNotMixed.indexOf(nestedMark);
				int iLevel = calcindent(formattedContentNotMixed, indexOf);
				if (indexOf == -1)
				{
					IdeLog
							.logError(
									UnifiedEditorsPlugin.getDefault(),
									StringUtils
											.format(
													"Nested mark not found, during formatting nested language Old:{0} New:{1}", new String[] { content, formattedContentNotMixed })); //$NON-NLS-1$
					return;
				}
				String languageContent = formatNode(content, pnode, null, project);
				languageContent = indent(iLevel + 2, languageContent, lineSeparator);
				formattedContentNotMixed = formattedContentNotMixed.substring(0, indexOf) + languageContent
						+ formattedContentNotMixed.substring(indexOf + nestedMark.length());

			}
			actualReplace(selection, content, formattedContentNotMixed);
		}
		else
		{
			String formattedContent = codeFormatter.format(content, false, null, project, lineSeparator);
			actualReplace(selection, content, formattedContent);
		}
	}

	private void formatSelection(TextSelection selection, IDocument document, IProject project)
	{
		IParseNode parseResults = part.getFileContext().getParseState().getParseResults();
		LexemeList lexemeList = part.getFileContext().getLexemeList();

		int selectionStart = selection.getOffset();
		int selectionEnd = selection.getOffset() + selection.getLength();

		if (!checkSplitting(lexemeList, selectionStart, selectionEnd))
		{
			return;
		}

		IParseNode old = parseResults;
		parseResults = getSmallestNode(parseResults, selection.getOffset(), selection.getLength());
		if (parseResults == null)
		{
			parseResults = old;
		}
		String language = determineLanguage(selection, document, parseResults, lexemeList);

		ICodeFormatter codeFormatter = LanguageRegistry.getCodeFormatter(language);
		String content;
		try
		{
			// adjusting selection if needed and calculating indentation levels.
			IRegion lineInformationOfOffset = document.getLineInformationOfOffset(selection.getOffset());
			int offset = lineInformationOfOffset.getOffset();
			String string = document.get(offset, lineInformationOfOffset.getLength());
			int internalOffset = selection.getOffset() - offset;
			int indentLevel = 0;
			int lineOffset = calcLineOffset(string);
			int lineOfOffset = document.getLineOfOffset(selection.getOffset());
			boolean isLeading = false;
			boolean isHtml = language.equals("text/html"); //$NON-NLS-1$
			indentLevel = calculateIndentation(document, isHtml ? lineOfOffset - 1 : lineOfOffset - 1, isHtml);

			// if (old.getLanguage().equals("text/html")){ //$NON-NLS-1$
			// if (lineOfOffset>0){
			// // this is wrong but this seems to be not the worsest solution for now
			// indentLevel+=tabWitdh;
			// }
			// }
			if (lineOffset >= internalOffset)
			{
				int length = selection.getLength() + (selection.getOffset() - offset);
				if (document.getLength() <= offset + length)
				{
					length = document.getLength() - offset;
				}
				selection = new TextSelection(offset, length);
				isLeading = true;
			}
			content = document.get(selection.getOffset(), selection.getLength());
			// does not format empty content now.
			if (content.trim().length() == 0)
			{
				return;
			}
			if (!codeFormatter.handlesNested())
			{
				ArrayList languageList = new ArrayList();
				checkNodes(languageList, parseResults);
				StringBuffer clearedContent = new StringBuffer();
				int prev = 0;
				String nestedMark = codeFormatter.createNestedMark();
				for (int a = 0; a < languageList.size(); a++)
				{
					IParseNode pnode = (IParseNode) languageList.get(a);
					int i = pnode.getStartingOffset() - selection.getOffset();
					if (i < 0 || i < prev)
					{
						IdeLog.logError(UnifiedEditorsPlugin.getDefault(), StringUtils.format(
								"Format failed bad ast positions {0},", new String[] { content, pnode.toString() })); //$NON-NLS-1$
						return;
					}
					clearedContent.append(content.substring(prev, i));
					prev = pnode.getEndingOffset() - selection.getOffset();
					clearedContent.append(nestedMark);
				}
				if (prev < content.length())
				{
					clearedContent.append(content.substring(prev));
				}
				String formattedContentNotMixed = doActualFormat(selection, project, codeFormatter, content,
						languageList, clearedContent, nestedMark);
				formattedContentNotMixed = indent(indentLevel, formattedContentNotMixed, lineSeparator);
				actualReplace(selection, content, formattedContentNotMixed);
			}
			else
			{
				if (content.length() > 0)
				{
					String formattedContent = codeFormatter.format(content, true, null, project, lineSeparator);
					if (formattedContent.equals(content))
					{
						return;
					}
					formattedContent = indent(indentLevel, formattedContent, lineSeparator);

					formattedContent = removeTrailingReturnsIfNeeded(content, formattedContent);
					if (!isLeading)
					{
						formattedContent = removeLeadingWhiteSpaces(content, formattedContent);
					}

					actualReplace(selection, content, formattedContent);
				}
			}
		}
		catch (BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}

	private String removeLeadingWhiteSpaces(String content, String formattedContent)
	{
		int pos = 0;
		for (int a = 0; a < content.length(); a++)
		{
			char c = content.charAt(a);
			if (Character.isWhitespace(c))
			{
				pos++;
			}
			else
			{
				break;
			}
		}
		if (pos > 0)
		{
			content = content.substring(pos);
		}
		return content;
	}

	private String doActualFormat(TextSelection selection, IProject project, ICodeFormatter codeFormatter,
			String content, ArrayList languageList, StringBuffer clearedContent, String nestedMark)
	{
		String formattedContentNotMixed = codeFormatter.format(clearedContent.toString(), true, null, project,
				lineSeparator);
		for (int a = 0; a < languageList.size(); a++)
		{
			IParseNode pnode = (IParseNode) languageList.get(a);

			int indexOf = formattedContentNotMixed.indexOf(nestedMark);
			int iLevel = calcindent(formattedContentNotMixed, indexOf);

			String languageContent = formatNode(content, pnode, selection, project);
			languageContent = indent(iLevel + 2, languageContent, lineSeparator);
			formattedContentNotMixed = formattedContentNotMixed.substring(0, indexOf) + languageContent
					+ formattedContentNotMixed.substring(indexOf + nestedMark.length());
		}
		return formattedContentNotMixed;
	}

	/**
	 * @param content
	 * @param formattedContent
	 * @return cleared string
	 */
	public String removeTrailingReturnsIfNeeded(String content, String formattedContent)
	{
		// Check for carriage return at end of original content
		boolean isTrailingOriginal = false;
		char charAt = content.charAt(content.length() - 1);
		if (charAt == '\r' || charAt == '\n')
		{
			isTrailingOriginal = true;
		}

		// Check for carriage return at end of formatted content
		boolean isTrailingFormatted = false;
		if (formattedContent.length() > 0)
		{
			char charAt1 = formattedContent.charAt(formattedContent.length() - 1);
			if (charAt1 == '\r' || charAt1 == '\n')
			{
				isTrailingFormatted = true;
			}
		}

		// If old content ends in a carriage return and new content doesn't
		if (isTrailingOriginal && !isTrailingFormatted)
		{
			formattedContent += lineSeparator;
		}
		if (!isTrailingOriginal && isTrailingFormatted)
		{
			int pos = formattedContent.length();
			for (int a = formattedContent.length() - 1; a > 0; a--)
			{
				char charAt1 = formattedContent.charAt(a);
				if (charAt1 == '\r' || charAt1 == '\n')
				{
					pos--;
				}
				else
				{
					break;
				}
			}
			if (pos != formattedContent.length())
			{
				formattedContent = formattedContent.substring(0, pos);
			}
		}
		return formattedContent;
	}

	private int calcLineOffset(String string)
	{
		int lineOffset = 0;
		for (int a = 0; a < string.length(); a++)
		{
			char c = string.charAt(a);
			if (Character.isWhitespace(c))
			{
				lineOffset++;
			}
			else
			{
				break;
			}
		}
		return lineOffset;
	}

	/**
	 * returns prefered indentation level for a given offset in document
	 * 
	 * @param document
	 * @param lineOfOffset
	 * @param isHtml
	 * @return indentation
	 * @throws BadLocationException
	 */
	public int calculateIndentation(IDocument document, int lineOfOffset, boolean isHtml) throws BadLocationException
	{

		int indentLevel;
		String string = determineCorrectString(document, lineOfOffset);
		indentLevel = 0;
		boolean isInLeadingCode = true;
		int braceCount = 0;
		boolean leading = true;
		for (int a = 0; a < string.length(); a++)
		{
			char c = string.charAt(a);
			if (isInLeadingCode && Character.isWhitespace(c))
			{
				if (c == ' ')
				{
					indentLevel++;
				}
				if (c == '\t')
				{
					indentLevel += tabWitdh;
				}
			}
			else
			{
				if (c == '{' && !isHtml)
				{
					braceCount++;
					indentLevel += tabWitdh;
					leading = false;
				}
				else if (c == '}' && !isHtml)
				{
					if (!leading)
					{
						if (braceCount > 0)
						{
							indentLevel -= tabWitdh;
							braceCount--;
						}
					}
				}
				else
				{
					leading = false;
				}
				isInLeadingCode = false;
			}
		}

		return indentLevel;
	}

	/*******************************************************************************************************************
	 * @param document
	 * @param lineOfOffset
	 * @return string
	 * @throws BadLocationException
	 */
	public String determineCorrectString(IDocument document, int lineOfOffset) throws BadLocationException
	{
		String string = ""; //$NON-NLS-1$
		boolean inComment = false;
		while (lineOfOffset >= 0)
		{
			IRegion lineInformation = document.getLineInformation(lineOfOffset);
			string = document.get(lineInformation.getOffset(), lineInformation.getLength());
			if (string.trim().length() == 0)
			{
				lineOfOffset--;

				continue;
			}
			else
			{
				if (string.trim().endsWith("*/")) { //$NON-NLS-1$
					if (!string.contains("/*")) { //$NON-NLS-1$
						inComment = true;
						lineOfOffset--;
						continue;
					}
				}
				if (!inComment)
				{
					break;
				}
				if (string.contains("/*")) { //$NON-NLS-1$
					inComment = false;
				}
				lineOfOffset--;
			}
		}
		if (inComment)
		{
			string = ""; //$NON-NLS-1$
		}
		return string;
	}

	private boolean checkSplitting(LexemeList lexemeList, int selectionStart, int selectionEnd)
	{
		// get lexeme right before selection
		Lexeme lexemeFromOffset = lexemeList.getLexemeFromOffset(selectionStart);

		// get lexeme right after selection
		Lexeme lexemeFromSelection = lexemeList.getLexemeFromOffset(selectionEnd);

		// basically, don't format if the selection cuts through the middle of a
		// lexeme, except in HTML
		if (lexemeFromOffset != null || lexemeFromSelection != null)
		{
			// is start of selection HTML. If so, we consider the lexeme not
			// split
			boolean startSplit = lexemeSplitByOffset(lexemeFromOffset, selectionStart);
			// is end of selection HTML. If so, we consider the lexeme not split
			boolean endSplit = lexemeSplitByOffset(lexemeFromSelection, selectionEnd);
			// do not break lexemes in selection (except HTML)
			if (startSplit || endSplit)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * determines top level language that should be formatted for a selection.
	 * 
	 * @param selection
	 * @param document
	 * @param parseResults
	 * @param lexemeList
	 * @return - language
	 */
	private String determineLanguage(TextSelection selection, IDocument document, IParseNode parseResults,
			LexemeList lexemeList)
	{
		String language = parseResults.getLanguage();
		// get lexeme right at selection. There is the case where the selection
		// is right at a language
		// transition, and we want to ensure that the selection chooses the
		// proper parser
		Lexeme lexemeFromOffsetStart = lexemeList.getCeilingLexeme(selection.getOffset());
		if (lexemeFromOffsetStart != null && !lexemeFromOffsetStart.getLanguage().equals(language)
				&& language.equals(TEXT_HTML))
		{
			language = lexemeFromOffsetStart.getLanguage();
		}

		// TODO FIX THIS LATER
		if (language.equals("text/php")) { //$NON-NLS-1$
			String string;
			if (selection.getOffset() <= parseResults.getStartingOffset())
			{
				try
				{
					string = document.get(parseResults.getStartingOffset(), 3);
				}
				catch (BadLocationException e)
				{
					throw new RuntimeException(e);
				}
				if (string.startsWith("<?")) { //$NON-NLS-1$
					language = "text/html"; //$NON-NLS-1$
				}
			}
		}
		return language;
	}

	/**
	 * Is this lexeme split by the offset? If HTML, we assume no
	 * 
	 * @param lexeme
	 * @param offset
	 * @return - true if split
	 */
	private boolean lexemeSplitByOffset(Lexeme lexeme, int offset)
	{
		if (lexeme == null)
		{
			return false;
		}

		if (lexeme != null && lexeme.containsOffset(offset) && lexeme.getLanguage().equals(TEXT_HTML))
		{
			return false;
		}

		boolean condition = lexeme != null && lexeme.containsOffset(offset) && offset != lexeme.getStartingOffset();
		return condition;
	}

	/**
	 * Verify that the old content and the new content at least both contain some content
	 * 
	 * @param oldContent
	 * @param newContent
	 * @return - true if content preserved
	 */
	private boolean verifyContentPreserved(String oldContent, String newContent)
	{
		Pattern pattern = Pattern.compile("\\S"); //$NON-NLS-1$
		Matcher oldMatcher = pattern.matcher(oldContent);
		Matcher newMatcher = pattern.matcher(newContent);

		// if old content has some non-whitespace content, and new content has
		// only whitespace
		// we must have deleted something important.
		boolean condition = oldMatcher.find() && !newMatcher.find();
		return !condition;
	}

	private IParseNode getSmallestNode(IParseNode parseResults, int offset, int length)
	{
		if (parseResults.getStartingOffset() <= offset && parseResults.getEndingOffset() >= offset + length)
		{
			for (int a = 0; a < parseResults.getChildCount(); a++)
			{
				IParseNode child = parseResults.getChild(a);
				IParseNode smallestNode = getSmallestNode(child, offset, length);
				if (smallestNode != null)
				{
					return smallestNode;
				}
			}
			return parseResults;
		}
		return null;
	}

	private int calcindent(String formattedContentNotMixed, int indexOf)
	{
		int pos = 0;
		for (int a = indexOf - 1; a >= 0; a--)
		{
			pos++;
			char charAt = formattedContentNotMixed.charAt(a);
			if (charAt == '\n')
			{
				break;
			}
			if (charAt == '\r')
			{
				break;
			}
		}
		return pos;
	}

	/**
	 * indents string
	 * 
	 * @param level
	 * @param languageContent
	 * @param lineSeparator
	 * @return indented string
	 */
	public String indent(int level, String languageContent, String lineSeparator)
	{
		if (lineSeparator == null)
		{
			return languageContent;
		}
		String[] split = languageContent.split(lineSeparator);
		SourceWriter result = new SourceWriter(level, tabChar, tabWitdh);
		if (split.length == 1)
		{
			result.printIndent();
			result.print(split[0]);
			return result.toString();
		}
		for (int a = 0; a < split.length; a++)
		{
			result.printlnWithIndent(split[a]);
		}
		return result.toString();
	}

	private String formatNode(String content, IParseNode pnode, TextSelection selection, IProject project)
	{
		int start = selection == null ? pnode.getStartingOffset() : pnode.getStartingOffset() - selection.getOffset();

		int end = selection == null ? pnode.getEndingOffset() : pnode.getEndingOffset() - selection.getOffset();
		String nodeContent = content.substring(start, end);

		ICodeFormatter codeFormatter = LanguageRegistry.getCodeFormatter(pnode.getLanguage());
		if (codeFormatter != null)
		{

			String formattedContent = codeFormatter.format(nodeContent, false, null, project, lineSeparator).trim();

			return formattedContent;

		}
		return nodeContent;
	}

	private void actualReplace(TextSelection selection, String content, String formattedContent)
	{
		try
		{
			// If we somehow removed all content, don't format
			if (!verifyContentPreserved(content, formattedContent))
			{
				IdeLog
						.logError(
								UnifiedEditorsPlugin.getDefault(),
								StringUtils
										.format(
												"Format failed and removed non-whitespace content. old \"{0}\", new \"{1}\"", new String[] { content, formattedContent })); //$NON-NLS-1$
				return;
			}

			int offset = 0;
			if (selection.getLength() != 0)
			{
				offset = selection.getOffset();
			}
			IDocument document = part.getViewer().getDocument();
			if (document.get(offset, content.length()).equals(formattedContent))
			{
				return;
			}
			part.getViewer().getTextWidget().setRedraw(false);
			document.replace(offset, content.length(), formattedContent);
			part.getViewer().setSelectedRange(selection.getOffset(), 0);
			part.getViewer().getTextWidget().setRedraw(true);

		}
		catch (BadLocationException e)
		{

		}
	}

	private void checkNodes(ArrayList languageList, IParseNode parseResults)
	{
		for (int a = 0; a < parseResults.getChildCount(); a++)
		{
			IParseNode child = parseResults.getChild(a);

			if (child.getLanguage().equals(parseResults.getLanguage()))
			{
				checkNodes(languageList, child);
			}
			else
			{
				if (child.getStartingOffset() != -1 && child.getEndingOffset() != -1)
				{
					languageList.add(child);
				}
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor)
	{
		part = (IUnifiedEditor) targetEditor;
		if (part != null && part.getViewer() != null && part.getViewer().getDocument() != null)
		{
			String[] legalLineDelimiters = part.getViewer().getDocument().getLegalLineDelimiters();
			if (legalLineDelimiters != null && legalLineDelimiters.length > 0)
			{
				this.lineSeparator = legalLineDelimiters[0];
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{

	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose()
	{
		// do nothing
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window)
	{
		// do nothing
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		// do nothing
	}
}
