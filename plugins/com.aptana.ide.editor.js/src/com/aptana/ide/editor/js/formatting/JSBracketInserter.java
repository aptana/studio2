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
package com.aptana.ide.editor.js.formatting;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.events.VerifyEvent;

import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editors.formatting.UnifiedBracketInserter;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.lexer.Lexeme;

/**
 * Insert brackets, parens, string etc
 *
 */
public class JSBracketInserter extends UnifiedBracketInserter
{ 
	/**
	 * Controls the insertion and deletion of quotes in HTML.
	 * @param sourceViewer
	 * @param context 
	 */
	public JSBracketInserter(ISourceViewer sourceViewer, IFileService context)
	{
		super(sourceViewer, context);
	}

	/**
	 * getLanguageService
	 * 
	 * @return IFileLanguageService
	 */
	protected IFileLanguageService getLanguageService()
	{
		return context.getLanguageService(JSMimeType.MimeType);
	}
	
	/**
	 * getPreferenceStore
	 */
	protected IPreferenceStore getPreferenceStore() {
		return JSPlugin.getDefault().getPreferenceStore();
	}

	
	/**
	 * @see com.aptana.ide.editors.formatting.UnifiedBracketInserter#isValidAutoInsertLocation(char, com.aptana.ide.lexer.Lexeme, com.aptana.ide.lexer.Lexeme)
	 */
	protected boolean isValidAutoInsertLocation(char character, Lexeme previous, Lexeme next) {
		switch (character) {
		case '{':
		case '(':
			if (!fCloseBrackets
//					|| previous != null && previous.typeIndex == JSTokenTypes.LPAREN
					|| next != null && next.typeIndex == JSTokenTypes.IDENTIFIER
					|| next != null && next.getLength() > 1) {
				return false;
			}
			break;

		case '<':
			if (!(fCloseAngularBrackets && fCloseBrackets)
					|| next != null && next.typeIndex == JSTokenTypes.LESS
					|| previous != null && previous.typeIndex != JSTokenTypes.LCURLY
							&& previous.typeIndex != JSTokenTypes.RCURLY
							&& previous.typeIndex != JSTokenTypes.SEMICOLON
							&& (previous.typeIndex != JSTokenTypes.IDENTIFIER || !isAngularIntroducer(previous.getText()))) {
				return false;
			}
			break;

		case '[':
			if (!fCloseBrackets
					|| next != null && next.typeIndex == JSTokenTypes.IDENTIFIER
					|| next != null && next.getLength() > 1) {
				return false;
			}
			break;

		case '\'':				
		case '"':
			if (!fCloseStrings
					|| next != null && next.typeIndex == JSTokenTypes.IDENTIFIER
					|| previous != null && previous.typeIndex == JSTokenTypes.IDENTIFIER
					|| next != null && next.getLength() > 1
					|| previous != null && previous.getLength() > 1
					|| character == '\'' && previous != null && previous.getText().equals("'") //$NON-NLS-1$
					|| character == '"' && previous != null && previous.getText().equals("\"")) { //$NON-NLS-1$
				return false;
			}
			break;

		default:
			return true;
	}
	return true;
	}

	/**
	 * triggerAssistPopup
	 */
	protected void triggerAssistPopup(VerifyEvent event)
	{
		if(event.character == '(')
		{
			triggerContextAssistPopup(event);
		}
		else if(event.character == '[')
		{
			triggerContentAssistPopup(event);
		}
	}

}
