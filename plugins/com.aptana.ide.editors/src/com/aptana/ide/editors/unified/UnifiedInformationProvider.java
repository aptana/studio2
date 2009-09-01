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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.hover.LexemeHoverRegion;
import com.aptana.ide.editors.unified.utils.HTMLTextPresenter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * Provides information related to the content of a text viewer.
 */
public class UnifiedInformationProvider implements IInformationProvider, IInformationProviderExtension2
{
	ISourceViewer sourceViewer;
	EditorFileContext context;
	
	/**
	 * Creates a new instance of UnifiedInformationProvider
	 * @param sourceViewer 
	 * @param context 
	 */
	public UnifiedInformationProvider(ISourceViewer sourceViewer, EditorFileContext context)
	{
		this.sourceViewer = sourceViewer;
		this.context = context;
	}

	/**
	 * @see IInformationProviderExtension2#getInformationPresenterControlCreator()
	 * @since 3.1
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new UnifiedInformationControl(parent, SWT.RESIZE | SWT.TOOL, SWT.NONE, 
						new HTMLTextPresenter(false));
			}
		};
	}
	
	/**
	 * @see org.eclipse.jface.text.information.IInformationProvider#getSubject(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getSubject(ITextViewer textViewer, int offset)
	{
		LexemeList lexemes = context.getLexemeList();
		if(lexemes == null)
		{
			return null;
		}
		
		Lexeme currentLexeme = lexemes.getLexemeFromOffset(offset);

		return new LexemeHoverRegion(currentLexeme);
	}

	/**
	 * This is overriding a deprecated method.
	 * @param textViewer 
	 * @param subject 
	 * @return Returnsinfo string 
	 */
	public String getInformation(ITextViewer textViewer, IRegion subject)
	{
		LexemeHoverRegion hr = (LexemeHoverRegion) subject;
		Lexeme lexeme = hr.getLexeme();
		if(lexeme == null)
		{
			return StringUtils.EMPTY;
		}
		
		IFileLanguageService langService = context.getLanguageService(lexeme.getToken().getLanguage());
		boolean showDebug = false;
		try
		{
			IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
			if(store.getBoolean(IPreferenceConstants.SHOW_DEBUG_HOVER))
			{
				showDebug = true;
			}
		}
		catch(Exception ex)
		{
			
		}
		
		if(langService != null)
		{
			if(showDebug)
			{
				return DocumentationHelper.generateLexemeDebugInfo(langService.getFileContext(), lexeme);
			}
			else
			{
				return langService.getDocumentationFromLexeme(lexeme);				
			}
		}
		else
		{
			return StringUtils.EMPTY;
		}
	}
}
