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

// import org.eclipse.jface.text.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.ide.editor.xml.XMLFileLanguageService;
import com.aptana.ide.editor.xml.XMLPlugin;
import com.aptana.ide.editor.xml.lexing.XMLTokenTypes;
import com.aptana.ide.editor.xml.parsing.XMLMimeType;
import com.aptana.ide.editor.xml.parsing.XMLParseState;
import com.aptana.ide.editor.xml.preferences.IPreferenceConstants;
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
public class XMLAutoIndentStrategy extends UnifiedAutoIndentStrategy
{
	/**
	 * XMLAutoIndentStrategy
	 * 
	 * @param context
	 * @param configuration
	 * @param sourceViewer
	 */
	public XMLAutoIndentStrategy(EditorFileContext context, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer)
	{
		super(context, configuration, sourceViewer);
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy#customizeDocumentCommand(org.eclipse.jface.text.IDocument,
	 *      org.eclipse.jface.text.DocumentCommand)
	 */
	public void customizeDocumentCommand(IDocument document, DocumentCommand command)
	{
		IPreferenceStore store = XMLPlugin.getDefault().getPreferenceStore();

		IParseState parseState = context.getParseState();
		XMLParseState xmlParseState = (XMLParseState) parseState.getParseState(XMLMimeType.MimeType);

		// makes the correct indentation when new line is created
		indentOnNewLine(document, command, store, xmlParseState);

		super.customizeDocumentCommand(document, command);
	}

	/**
	 * Makes an indent on new line.
	 * 
	 * @param document -
	 *            document.
	 * @param command -
	 *            document command.
	 * @param store -
	 *            preference store.
	 * @param xmlParseState -
	 *            parse state.
	 */
	private void indentOnNewLine(IDocument document, DocumentCommand command, IPreferenceStore store,
			XMLParseState xmlParseState)
	{
		if (command.text == null || command.length > 0)
		{
			return;
		}

		if (store != null && store.getBoolean(IPreferenceConstants.AUTO_INDENT_ON_CARRIAGE_RETURN))
		{
			if (UnifiedConfiguration.isNewlineString(command.text) && indentNextTag(document, command, xmlParseState))
			{
				return;
			}
		}
	}

	/**
	 * Gets the xml language service
	 * 
	 * @return - file language server
	 */
	protected IFileLanguageService getLanguageService()
	{
		IFileLanguageService ls = (XMLFileLanguageService) context.getLanguageService(XMLMimeType.MimeType);
		return ls;
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy#getLexemeList()
	 */
	protected LexemeList getLexemeList()
	{
		XMLFileLanguageService ls = (XMLFileLanguageService) context.getLanguageService(XMLMimeType.MimeType);
		return ls.getFileContext().getLexemeList();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IPreferenceClient#getPreferenceStore()
	 */
	public IPreferenceStore getPreferenceStore()
	{
		return XMLPlugin.getDefault().getPreferenceStore();
	}

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

			if (l != null && l.typeIndex == XMLTokenTypes.STRING)
			{
				isString = true;
			}
		}

		return XMLUtils.insideOpenTag(c.offset, ll) && !isString;
	}

	private boolean indentNextTag(IDocument d, DocumentCommand c, XMLParseState parseState)
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

			if (lexeme.typeIndex == XMLTokenTypes.GREATER_THAN || lexeme.typeIndex == XMLTokenTypes.SLASH_GREATER_THAN
					|| lexeme.typeIndex == XMLTokenTypes.END_TAG)
			{
				return false;
			}

			if (lexeme.typeIndex == XMLTokenTypes.START_TAG)
			{
				break;
			}

			position--;
		}

		// if (lexeme != null && parseState != null && parseState.isEmptyTagType(replaceText))
		// {
		// return false;
		// }

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

			if (nextLexeme.typeIndex != XMLTokenTypes.END_TAG)
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
}
