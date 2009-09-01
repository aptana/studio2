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

// import org.eclipse.jface.text.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.html.HTMLFileLanguageService;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParseState;
import com.aptana.ide.editor.html.parsing.HTMLUtils;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy;
import com.aptana.ide.editors.unified.UnifiedConfiguration;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.IParseState;

/**
 * @author Robin Debreuil
 */
public class HTMLAutoIndentStrategy extends UnifiedAutoIndentStrategy
{
	/**
	 * @see com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy#isValidAutoInsertLocation(org.eclipse.jface.text.IDocument,
	 *      org.eclipse.jface.text.DocumentCommand)
	 */
	protected boolean isValidAutoInsertLocation(IDocument d, DocumentCommand c)
	{
		boolean isString = false;
		LexemeList ll = getLexemeList();
		if (c.offset > 0 && ll != null)
		{ 
			Lexeme l = ll.getLexemeFromOffset(c.offset);

			if (l != null && l.typeIndex == HTMLTokenTypes.STRING)
			{
				isString = true;
			}
		}

		return HTMLUtils.insideOpenTag(c.offset, ll) && !isString;
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy#getAutoInsertCharacters()
	 */
	protected char[] getAutoInsertCharacters()
	{
		return new char[] { '"', '\'' };
	}

	/**
	 * HTMLAutoIndentStrategy
	 * 
	 * @param context
	 * @param configuration
	 * @param sourceViewer
	 */
	public HTMLAutoIndentStrategy(EditorFileContext context, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer)
	{
		super(context, configuration, sourceViewer);
	}

	/**
	 * @see org.eclipse.jface.text.IAutoEditStrategy#customizeDocumentCommand(org.eclipse.jface.text.IDocument,
	 *      org.eclipse.jface.text.DocumentCommand)
	 */
	public void customizeDocumentCommand(IDocument document, DocumentCommand command)
	{
		if (command.text == null || command.length > 0)
		{
			return;
		}

		IParseState parseState = context.getParseState();
		HTMLParseState htmlParseState = (HTMLParseState) parseState.getParseState(HTMLMimeType.MimeType);

		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		if (store != null)
		{
			if (store.getString(IPreferenceConstants.AUTO_COMPLETE_CLOSE_TAGS).equals("CLOSE") && command.text.equals("/")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				try
				{
					LexemeList lexemeList = getLexemeList();
					Lexeme current = lexemeList.getFloorLexeme(command.offset - 1);
					if (current != null && current.getText().trim().equals("<")) //$NON-NLS-1$
					{
						Lexeme open = HTMLUtils.getPreviousUnclosedTag(current, lexemeList, htmlParseState);
						int currentIndex = lexemeList.getLexemeIndex(current);
						String tag = HTMLUtils.createCloseTag(open, false); //$NON-NLS-1$
						
						Lexeme next = null;
						if(currentIndex + 1 < lexemeList.size())
						{
							next = lexemeList.get(currentIndex + 1);
						}
						
						if(next == null || next != null && !next.getText().equals(">")) //$NON-NLS-1$
						{
							tag += ">"; //$NON-NLS-1$
						}

						if (tag != null)
						{
							tag = tag.substring(1);
							command.text = tag;
							command.shiftsCaret = false;
							command.caretOffset = command.offset + tag.length();
							hideContentAssistPopup();
							return;
						}
					}
				}
				catch(Exception ex)
				{
					IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLAutoIndentStrategy_ERR_ClosingTag, ex);
					return;
				}
			}
			else if (store.getString(IPreferenceConstants.AUTO_COMPLETE_CLOSE_TAGS).equals("OPEN")  //$NON-NLS-1$
					&& command.text.equals(">") && closeTag(command, getLexemeList(), htmlParseState)) //$NON-NLS-1$
			{
				return;
			}
		}

		if (store != null && store.getBoolean(IPreferenceConstants.AUTO_INDENT_ON_CARRIAGE_RETURN))
		{
			if (UnifiedConfiguration.isNewlineString(command.text) && indentNextTag(document, command, htmlParseState))
			{
				return;
			}
		}

		super.customizeDocumentCommand(document, command);
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy#getLexemeList()
	 */
	protected LexemeList getLexemeList()
	{
		IFileLanguageService ls = getLanguageService();
		return ls.getFileContext().getLexemeList();
	}

	/**
	 * closeTag
	 * 
	 * @param command
	 * @param ll
	 * @param parseState
	 * @return boolean
	 */
	public static boolean closeTag(DocumentCommand command, LexemeList ll, HTMLParseState parseState)
	{
		Lexeme lexeme = ll.getFloorLexeme(command.offset);

		if (lexeme == null)
		{
			return false;
		}

		if (lexeme.getText().equals("/")) //$NON-NLS-1$
		{
			return false;
		}

		if (lexeme.getText().equals(">")) //$NON-NLS-1$
		{
			return false;
		}

		// If I am inside an open tag element, but not inside an attribute
		if (HTMLUtils.insideQuotedString(lexeme, command.offset))
		{
			return false;
		}
			
		Lexeme startLexeme = HTMLUtils.getTagOpenLexeme(command.offset, ll);

		if (startLexeme == null)
		{
			return false;
		}

		String replaceText = HTMLUtils.getOpenTagName(startLexeme, command.offset);

		if (HTMLUtils.isStartTagBalanced(startLexeme, ll, parseState))
		{
			return false;
		}

		if (parseState == null || !parseState.isEmptyTagType(replaceText))
		{
			command.text = ">" + HTMLUtils.createCloseTag(replaceText, true); //$NON-NLS-1$
			command.shiftsCaret = false;
			command.caretOffset = command.offset + 1;
		}
		return true;
	}

	/**
	 * getLanguageService
	 * 
	 * @return IFileLanguageService
	 */
	protected IFileLanguageService getLanguageService()
	{
		HTMLFileLanguageService ls = (HTMLFileLanguageService) context.getLanguageService(HTMLMimeType.MimeType);
		return ls;
	}

	private boolean indentNextTag(IDocument d, DocumentCommand c, HTMLParseState parseState)
	{
		// not using the following helper method until we commit to not using other types of source
		// viewer configuation files
		// this.configuration.getIndentString(this.sourceViewer, JSMimeType.MimeType);
		IFileLanguageService ls = getLanguageService();

		// if the previous lexeme is a >, go backwards until you find the next open element before
		// finding another >
		// and then copy that indent + one more indent's worth.

		LexemeList ll = ls.getFileContext().getLexemeList();

		if (ll == null)
		{
			return false;
		}

		int floorIndex = ll.getLexemeFloorIndex(c.offset - 1);

		if (floorIndex < 0)
		{
			return false;
		}

		Lexeme lexeme = ll.get(floorIndex);

		if (lexeme == null)
		{
			return false;
		}

		if (!lexeme.getText().equals(">")) //$NON-NLS-1$
		{
			return false;
		}

		int position = floorIndex - 1;

		while (position >= 0)
		{
			lexeme = ll.get(position);

			if (lexeme == null)
			{
				return false;
			}

			if (lexeme.typeIndex == HTMLTokenTypes.GREATER_THAN
					|| lexeme.typeIndex == HTMLTokenTypes.SLASH_GREATER_THAN
					|| lexeme.typeIndex == HTMLTokenTypes.END_TAG)
			{
				return false;
			}

			if (lexeme.typeIndex == HTMLTokenTypes.START_TAG)
			{
				break;
			}

			position--;
		}

		String replaceText = lexeme.getText().substring(1);

		if (lexeme != null && parseState != null && parseState.isEmptyTagType(replaceText))
		{
			return false;
		}

		String returnVal = c.text;
		String spaces = getIndentForCurrentLine(d, c);
		String newIndent = "\t"; //$NON-NLS-1$
		if (configuration instanceof UnifiedConfiguration)
		{
			UnifiedConfiguration uc = (UnifiedConfiguration) configuration;
			newIndent = uc.getIndent();
		}

		String nextText = returnVal + spaces;

		int ceilingIndex = ll.getLexemeCeilingIndex(c.offset);

		if (ceilingIndex < 0)
        {
            nextText = ""; //$NON-NLS-1$
        }
        else 
        {
            Lexeme nextLexeme = ll.get(ceilingIndex);

            if (nextLexeme.typeIndex != HTMLTokenTypes.END_TAG)
            {
                nextText = ""; //$NON-NLS-1$
            }
        }

		String beginText = returnVal + spaces + newIndent;
		c.text = beginText + nextText;
		c.shiftsCaret = false;
		c.caretOffset = c.offset + beginText.length();

		return true;
	}

	/**
	 * @see UnifiedAutoIndentStrategy#getPreferenceStore()
	 */
	public IPreferenceStore getPreferenceStore()
	{
		return HTMLPlugin.getDefault().getPreferenceStore();
	}
}
