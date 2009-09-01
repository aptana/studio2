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
package com.aptana.ide.editors.formatting;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.events.VerifyEvent;

import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.UnifiedViewer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * @author Paul Colton
 */
public abstract class UnifiedBracketInserter extends UnifiedBracketInserterBase implements IUnifiedBracketInserter
{
	/**
	 * sourceViewer
	 */
	protected ISourceViewer sourceViewer;

	/**
	 * context
	 */
	protected IFileService context;

	/**
	 * UnifiedBracketInserter
	 * 
	 * @param sourceViewer
	 * @param context
	 * @param configuration
	 */
	public UnifiedBracketInserter(ISourceViewer sourceViewer, IFileService context)
	{
		super(sourceViewer);
		this.sourceViewer = sourceViewer;
		this.context = context;
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy#getLexemeList()
	 */
	protected LexemeList getLexemeList()
	{
		return context.getLexemeList();
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
	}
	
	/**
	 * getPreferenceStore
	 * @return
	 */
	protected abstract IPreferenceStore getPreferenceStore();
	
	/**
	 * Is this location a valid place to insert the specified character?
	 * @param character The character inserted
	 * @param offset The offset of the insert
	 * @param length The length of the insertion
	 * @return
	 */
	protected boolean isValidAutoInsertLocation(char character, int offset,
			int length) {
		
		LexemeList list = getLexemeList();
		Lexeme next = list.getLexemeFromOffset(offset + length);
		Lexeme previous = list.getLexemeFromOffset(offset - 1);

		return isValidAutoInsertLocation(character, previous, next);
	}

	/**
	 * Is this location a valid place to inserts the specified character?
	 * @param character The character inserted
	 * @param previous The previous lexeme
	 * @param next The next lexeme
	 * @return
	 */
	protected boolean isValidAutoInsertLocation(char character, Lexeme previous, Lexeme next)
	{
		if (next != null && next.getLength() > 1
			    || previous != null && previous.getLength() > 1
			    || character == '\'' && previous != null && previous.getText().equals("'") //$NON-NLS-1$
				|| character == '"' && previous != null && previous.getText().equals("\"")) { //$NON-NLS-1$
				return false;
			}
			else
			{
				return true;
			}
	}
	
	/**
	 * Forces code assist to appear
	 */
	protected void triggerContentAssistPopup(VerifyEvent event)
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
	 * Forces context assist to appear
	 */
	protected void triggerContextAssistPopup(VerifyEvent event)
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
	 * Forces code assist to appear on next idle state assuming it can be shown
	 */
	protected void triggerAssistClose(VerifyEvent event)
	{
		if (sourceViewer instanceof UnifiedViewer)
		{
			((UnifiedViewer) sourceViewer).closeContentAssist();
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
