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
package com.aptana.ide.editors.unified;

import java.util.Arrays;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.formatting.UnifiedBracketInserterBase;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.lexer.LexemeList;

/**
 * UnifiedAutoIndentStrategy
 */
public abstract class UnifiedAutoIndentStrategy implements IAutoEditStrategy, IPreferenceClient
{
	/**
	 * configuration
	 */
	protected SourceViewerConfiguration configuration;

	/**
	 * sourceViewer
	 */
	protected ISourceViewer sourceViewer;

	/**
	 * context
	 */
	protected EditorFileContext context;

	/**
	 * spaces
	 */
	private String spaces = "                                                                            "; //$NON-NLS-1$

	/**
	 * Creates a new instance of the JSAutoEditStrategy
	 * 
	 * @param context
	 * @param configuration
	 * @param sourceViewer
	 */
	public UnifiedAutoIndentStrategy(EditorFileContext context, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer)
	{
		this.context = context;
		this.configuration = configuration;
		this.sourceViewer = sourceViewer;
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

		String[] lineDelimiters = document.getLegalLineDelimiters();
		int index = TextUtilities.endsWith(lineDelimiters, command.text);
		if (index > -1)
		{
			// ends with line delimiter
			if (lineDelimiters[index].equals(command.text))
			{
				indentAfterNewLine(document, command);
			}
			return;
		}
		// todo: ensure we actually need this here
		else if (command.text.equals("\t")) //$NON-NLS-1$
		{
			if (configuration instanceof UnifiedConfiguration)
			{
				UnifiedConfiguration uc = (UnifiedConfiguration) configuration;
				if (uc.useSpacesAsTabs())
				{
					command.text = uc.getTabAsSpaces();
				}
			}
		}
		else if (command.text.length() == 1 && isAutoInsertCharacter(command.text.charAt(0)) && isAutoInsertEnabled() && isValidAutoInsertLocation(document, command))
		{
			char current = command.text.charAt(0);

			if (overwriteBracket(current, document, command, getLexemeList()))
			{
				return;
			}
		}
	}

	/**
	 * 
	 * @param c
	 * @return
	 */
	private boolean isAutoInsertCharacter(char c) {
		int val = Arrays.binarySearch(getAutoInsertCharacters(), c);
		return val >= 0;
	}

	/**
	 * getAutoInsertCharacters
	 * 
	 * @return char[]
	 */
	protected char[] getAutoInsertCharacters()
	{
		return new char[] { '(', '<', '[', '"', '\'', '{' };
	}

	/**
	 * getAutoOverwriteCharacters
	 * 
	 * @return char[]
	 */
	protected char[] getAutoOverwriteCharacters()
	{
		return new char[] { ')', '>', ']', '"', '\'', '}' };
	}

	/**
	 * isAutoInsertEnabled
	 * 
	 * @return boolean
	 */
	protected boolean isAutoInsertEnabled()
	{
		IPreferenceStore store = getPreferenceStore();
		String abi = com.aptana.ide.editors.preferences.IPreferenceConstants.AUTO_BRACKET_INSERTION;

		return (store == null || store.getString(abi).equals("NONE") == false); //$NON-NLS-1$
		
//		if (store != null && store.getString(abi).equals("NONE"))
//		{
//			return false;
//		}
//		else
//		{
//			return true;
//		}
	}

	/**
	 * isValidAutoInsertLocation
	 * 
	 * @param d
	 * @param c
	 * @return boolean
	 */
	protected boolean isValidAutoInsertLocation(IDocument d, DocumentCommand c)
	{
		return true;
	}

	/**
	 * indentAfterNewLine
	 * 
	 * @param d
	 * @param c
	 */
	protected void indentAfterNewLine(IDocument d, DocumentCommand c)
	{
		String indentString = getIndentString();

		// nothing to add if nothing to add
		if (indentString.equals(StringUtils.EMPTY))
		{
			return;
		}

		int offset = c.offset;

		if (offset == -1 || d.getLength() == 0)
		{
			return;
		}

		c.text += getIndentationAtOffset(d, offset);

		return;
	}

	/**
	 * overwriteBracket
	 * 
	 * @param bracket
	 * @param document
	 * @param command
	 * @param ll
	 * @return boolean
	 */
	public boolean overwriteBracket(char bracket, IDocument document, DocumentCommand command, LexemeList ll)
	{
		// if next character is "closing" char, overwrite
		if (canOverwriteBracket(bracket, command.offset, document, ll))
		{
			command.text = StringUtils.EMPTY;
			command.shiftsCaret = false;
			command.caretOffset = command.offset + 1;
			return true;
		}

		return false;
	}

	/**
	 * canOverwriteBracket
	 * 
	 * @param bracket
	 * @param offset
	 * @param document
	 * @param ll
	 * @return boolean
	 */
	public boolean canOverwriteBracket(char bracket, int offset, IDocument document, LexemeList ll)
	{
		if (offset < document.getLength())
		{
			char[] autoOverwriteChars = getAutoOverwriteCharacters();
			Arrays.sort(autoOverwriteChars);

			if (Arrays.binarySearch(autoOverwriteChars, bracket) < 0)
			{
				return false;
			}

			// If the next char is a ">", our tag is already closed
			try
			{
				char sibling = document.getChar(offset);
				return sibling == bracket;
			}
			catch(BadLocationException ex)
			{
				return false;
			}
		}

		return false;
	}

	/**
	 * closeBracket
	 * 
	 * @param bracket
	 * @param document
	 * @param command
	 * @return boolean
	 */
	public boolean closeBracket(char bracket, IDocument document, DocumentCommand command)
	{

		if (!canCloseBracket(bracket, document))
		{
			return false;
		}
		else
		{
			command.text = Character.toString(bracket)
					+ Character.toString(UnifiedBracketInserterBase.getPeerCharacter(bracket));
			command.shiftsCaret = false;
			command.caretOffset = command.offset + 1;
			return true;
		}
	}

	/**
	 * canCloseBracket
	 * 
	 * @param bracket
	 * @param document
	 * @return boolean
	 */
	public boolean canCloseBracket(char bracket, IDocument document)
	{
		if (!UnifiedBracketInserterBase.hasPeerCharacter(bracket))
		{
			return false;
		}

		char[] autoInsertChars = getAutoInsertCharacters();
		Arrays.sort(autoInsertChars);

		if (Arrays.binarySearch(autoInsertChars, bracket) < 0)
		{
			return false;
		}

		return !UnifiedBracketInserterBase.isStringBalanced(document.get(), bracket, true);
	}

	/**
	 * getIndentationAtOffset
	 * 
	 * @param d
	 * @param offset
	 * @return String
	 */
	protected String getIndentationAtOffset(IDocument d, int offset)
	{
		String indentation = StringUtils.EMPTY;
		try
		{
			int p = (offset == d.getLength() ? offset - 1 : offset);
			IRegion line = d.getLineInformationOfOffset(p);

			int lineOffset = line.getOffset();
			int firstNonWS = findEndOfWhiteSpace(d, lineOffset, offset);

			indentation = getIndentationString(d, lineOffset, firstNonWS);

		}
		catch (BadLocationException excp)
		{
			// stop work
		}

		return indentation;
	}

	/**
	 * getIndentString
	 * 
	 * @return String
	 */
	protected String getIndentString()
	{
		String[] indents = this.configuration.getIndentPrefixes(this.sourceViewer, this.context.getDefaultLanguage());
		boolean hasIndents = !((indents == null) || (indents.length == 0));
		String indentString = hasIndents ? indents[0] : "\t"; //$NON-NLS-1$
		return indentString;

	}

	/**
	 * Calculates the whitespace prefix based on user prefs and the existing line. Eg: if the line prefix is five
	 * spaces, and user pref is tabs of width 4, then the result is "/t ".
	 * 
	 * @param d
	 * @param lineOffset
	 * @param firstNonWS
	 * @return Returns the whitespace prefix based on user prefs and the existing line.
	 */
	protected String getIndentationString(IDocument d, int lineOffset, int firstNonWS)
	{
		String lineIndent = StringUtils.EMPTY;
		try
		{
			lineIndent = d.get(lineOffset, firstNonWS - lineOffset);
		}
		catch (BadLocationException e1)
		{
		}
		if (lineIndent.equals(StringUtils.EMPTY))
		{
			return lineIndent;
		}

		int indentSize = 0;
		int tabWidth = this.configuration.getTabWidth(sourceViewer);
		char[] indentChars = lineIndent.toCharArray();
		for (int i = 0; i < indentChars.length; i++)
		{
			char e = indentChars[i];
			if (e == '\t')
			{
				indentSize += tabWidth - (indentSize % tabWidth);
			}
			else
			{
				indentSize++;
			}
		}
		String indentString = getIndentString();
		int indentStringWidth = (indentString.equals("\t")) ? tabWidth : indentString.length(); //$NON-NLS-1$
		// return in case tab width is zero
		if (indentStringWidth == 0)
		{
			return StringUtils.EMPTY;
		}
		int indentCount = (int) Math.floor(indentSize / indentStringWidth); // assume no dived by zero from above tests

		String indentation = StringUtils.EMPTY;
		for (int i = 0; i < indentCount; i++)
		{
			indentation += indentString;
		}
		// here we might want to allow one tab when there are three spaces on the previous line when tabwdith = 4
		// logic is just get the ending from the previous line
		int extra = indentSize % indentStringWidth;
		indentation += spaces.substring(0, extra);// lineIndent.substring(lineIndent.length() - extra);

		return indentation;
	}

	/**
	 * Copies the indentation of the previous line.
	 * 
	 * @param d
	 *            the document to work on
	 * @param c
	 *            the command to deal with
	 * @return String
	 */
	protected String getIndentForCurrentLine(IDocument d, DocumentCommand c)
	{

		if (c.offset == -1 || d.getLength() == 0)
		{
			return StringUtils.EMPTY;
		}

		try
		{
			// find start of line
			int p = (c.offset == d.getLength() ? c.offset - 1 : c.offset);
			IRegion info = d.getLineInformationOfOffset(p);
			int start = info.getOffset();

			// find white spaces
			int end = findEndOfWhiteSpace(d, start, c.offset);

			StringBuffer buf = new StringBuffer();
			if (end > start)
			{
				// append to input
				buf.append(d.get(start, end - start));
			}

			return buf.toString();

		}
		catch (BadLocationException excp)
		{
		}

		return StringUtils.EMPTY;

	}

	/**
	 * Hmm, this is copied from eclipses DefaultIndentAutoIndentStategy, but only accounts for spaces or tabs (unlike
	 * the whole insert strings allowing any string).
	 * 
	 * @param document
	 * @param offset
	 * @param end
	 * @return end of whitespace
	 * @throws BadLocationException
	 */
	protected int findEndOfWhiteSpace(IDocument document, int offset, int end) throws BadLocationException
	{
		while (offset < end)
		{
			char c = document.getChar(offset);
			if (c != ' ' && c != '\t')
			{
				return offset;
			}
			offset++;
		}
		return end;
	}

	/**
	 * Returns the current preference store
	 * 
	 * @return The current preference store, or null if not found
	 */
	public abstract IPreferenceStore getPreferenceStore();

	/**
	 * Creates a lexeme list
	 * 
	 * @return LexemeList
	 */
	protected abstract LexemeList getLexemeList();

	/**
	 * Handles the case where we just added a brace, and we want to return and indent to the next line
	 * 
	 * @param d
	 *            Document
	 * @param command
	 *            DocumentCommand
	 * @return True if it succeeded
	 */
	protected boolean indentAfterOpenBrace(IDocument d, DocumentCommand command)
	{
		int offset = command.offset;
		boolean result = false;

		if (offset != -1 && d.getLength() != 0)
		{
			String indent = getIndentForCurrentLine(d, command);
			String newline = command.text;
			String tab = "\t"; //$NON-NLS-1$

			if (configuration instanceof UnifiedConfiguration)
			{
				UnifiedConfiguration uc = (UnifiedConfiguration) configuration;

				tab = uc.getIndent();
			}

			try
			{
				if (command.offset > 0)
				{
					char c = d.getChar(command.offset - 1);

					if (c == '{')
					{
						String startIndent = newline + indent + tab;

						if (command.offset < d.getLength() && d.getChar(command.offset) == '}')
						{
							command.text = startIndent + newline + indent;
						}
						else
						{
							command.text = startIndent;
						}

						command.shiftsCaret = false;
						command.caretOffset = command.offset + startIndent.length();

						result = true;
					}
				}
			}
			catch (BadLocationException e)
			{
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), StringUtils.format(Messages.UnifiedAutoIndentStrategy_InvalidOffset, offset), e);
			}
		}

		return result;
	}
	
	/**
	 * Forces code assist to appear
	 */
	protected void triggerContentAssistPopup()
	{
		if (sourceViewer instanceof SourceViewer)
		{
			if(!autoTriggerAssist())
			{
				return;
			}

			((SourceViewer) sourceViewer).doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
		}
	}
	
	/**
	 * Forces code assist to appear
	 */
	protected void hideContentAssistPopup()
	{
		if (sourceViewer instanceof UnifiedViewer)
		{
			((UnifiedViewer) sourceViewer).closeContentAssist();
		}
	}

	/**
	 * Forces context assist to appear
	 */
	protected void triggerContextAssistPopup()
	{
		if (sourceViewer instanceof SourceViewer)
		{
			if(!autoTriggerAssist())
			{
				return;
			}

			((SourceViewer) sourceViewer).doOperation(ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION);
		}
	}
	
	/**
	 * Do we auto-trigger content/context assist?
	 * @return
	 */
	protected boolean autoTriggerAssist()
	{
		// only auto-pop if the preferences allow it
		return (getPreferenceStore() != null && getPreferenceStore().getBoolean(IPreferenceConstants.CODE_ASSIST_AUTO_ACTIVATION));
	}

}
