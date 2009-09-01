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

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.ide.editors.formatting.IUnifiedBracketInserter;

/**
 * IUnifiedEditorContributor
 */
public interface IUnifiedEditorContributor
{
	/**
	 * Gets the text hover for this file type (
	 * 
	 * @param sourceViewer
	 * @param contentType
	 * @return LocalTextHover
	 */
	ITextHover getLocalTextHover(ISourceViewer sourceViewer, String contentType);

	/**
	 * Gets the mimetype of the local content
	 * 
	 * @return mimetype
	 */
	String getLocalContentType();

	/**
	 * Gets the AutoEditStrategies for the local language type
	 * 
	 * @param sourceViewer
	 * @param contentType
	 * @return IAutoEditStrategy[]
	 */
	IAutoEditStrategy[] getLocalAutoEditStrategies(ISourceViewer sourceViewer, String contentType);

	/**
	 * Gets the local bracket isnerter for all the various languages in the editor
	 * 
	 * @param sourceViewer
	 * @param contentType
	 * @return IUnifiedBracketInserter
	 */
	IUnifiedBracketInserter getLocalBracketInserter(ISourceViewer sourceViewer, String contentType);

	/**
	 * Gets the ContentAssistProcessor for the local language type
	 * 
	 * @param sourceViewer
	 * @param contentType
	 * @return IContentAssistProcessor
	 */
	IContentAssistProcessor getLocalContentAssistProcessor(ISourceViewer sourceViewer, String contentType);

	/**
	 * Configures the PresentationReconcilers for all the various languages in the editor
	 * 
	 * @param reconciler
	 */
	void configPresentationReconciler(PresentationReconciler reconciler);

	/**
	 * Gets the AutoEditStrategies for all the various languages in the editor
	 * 
	 * @param sourceViewer
	 * @param contentType
	 * @return IAutoEditStrategy[]
	 */
	IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType);

	/**
	 * Gets the language contributors for all the various languages in the editor
	 * 
	 * @return IUnifiedEditorContributor[]
	 */
	IUnifiedEditorContributor[] getChildContributors();

	/**
	 * Gets the language contributors for the specified mime type
	 * 
	 * @param contentType
	 * @return IUnifiedEditorContributor
	 */
	IUnifiedEditorContributor findChildContributor(String contentType);

	/**
	 * Gets the ContentAssistProcessor for all the various languages in the editor
	 * 
	 * @param sourceViewer
	 * @param contentType
	 * @return IContentAssistProcessor
	 */
	IContentAssistProcessor getContentAssistProcessor(ISourceViewer sourceViewer, String contentType);
	
	/**
	 * Gets the bracket inserter for all the various languages in the editor
	 * 
	 * @param sourceViewer
	 * @param contentType
	 * @return IUnifiedBracketInserter
	 */
	IUnifiedBracketInserter getBracketInserter(ISourceViewer sourceViewer, String contentType);

	/**
	 * Should content assist auto activate
	 * @return true to show, false to not
	 */
	boolean isAutoActivateContentAssist();

	/**
	 * Gets the MimeTypes for all the languages that exist in the editor
	 * 
	 * @return String[] of mime types
	 */
	String[] getContentTypes();

	/**
	 * Gets the FileContext for the editor
	 * 
	 * @return EditorFileContext
	 */
	EditorFileContext getFileContext();

	/**
	 * Gets the Parent SourceViewerConfiguration
	 * 
	 * @return SourceViewerConfiguration
	 */
	SourceViewerConfiguration getParentConfiguration();

	/**
	 * Gets the ReconcilingStrategy that is used by the various languages in the editor
	 * 
	 * @return UnifiedReconcilingStrategy
	 */
	UnifiedReconcilingStrategy getReconcilingStrategy();

	/**
	 * Gets the text hover for the given content type
	 * 
	 * @param sourceViewer
	 * @param contentType
	 * @return ITextHover
	 */
	ITextHover getTextHover(ISourceViewer sourceViewer, String contentType);

	/**
	 * Sets the file context
	 * 
	 * @param fileContext
	 */
	void setFileContext(EditorFileContext fileContext);

	/**
	 * Sets the parent contributor
	 * 
	 * @param parent
	 */
	void setParent(IUnifiedEditorContributor parent);

	/**
	 * Sets the parent configuration.
	 * 
	 * @param parentConfiguration
	 */
	void setParentConfiguration(SourceViewerConfiguration parentConfiguration);

	/**
	 * This is for the way eclipse handles things like auto indent and multi line tabbing. The string are all possible
	 * chars that will make up the inital 'whitespace' to indent. The first string is used when right tabbing, it is
	 * what is inserted (nice of them to mention that). If you want to add to a specific language you should override
	 * this method for that language.
	 * 
	 * @param sourceViewer
	 * @param contentType
	 * @return A string[] of indent prefixes to be used in autoedits
	 */
	String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType);

	/**
	 * A helper method that returns the indent string that should be used to prefix lines. In the case of 'space' this
	 * will be four spaces in the case of tabsize = 4 (rather than a single space).
	 * 
	 * @param sourceViewer
	 * @param contentType
	 * @return the indent string.
	 */
	String getIndentString(ISourceViewer sourceViewer, String contentType);

	/**
	 * The indent strings for a language, use this method to change the defaults at runtime for a specific language in
	 * an editor. Note: if you wish to use four spaces as in indent, then the first character should be a srting of four
	 * spaces - eclipse does not look up the tab width in such situations.
	 * 
	 * @param prefixes
	 * @param sourceViewer
	 * @param contentType
	 */
	void setIndentPrefixes(String[] prefixes, ISourceViewer sourceViewer, String contentType);

	/**
	 * The indent strings for a language, use this method to change the defaults at runtime for all languages in an
	 * editor. Note: if you wish to use four spaces as in indent, then the first character should be a srting of four
	 * spaces - eclipse does not look up the tab width in such situations.
	 * 
	 * @param prefixes
	 * @param sourceViewer
	 */
	void setAllIndentPrefixes(String[] prefixes, ISourceViewer sourceViewer);

	/**
	 * Sets the default double-click strategy
	 * 
	 * @param sourceViewer
	 * @param contentType
	 * @return ITextDoubleClickStrategy
	 */
	ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType);
	
	/**
     * Gets triple click strategy.
     * @param sourceViewer - source viewer.
     * @param contentType - content type.
     * @return triple click strategy
     */
    public ITextTripleClickStrategy getTripleClickStrategy(ISourceViewer sourceViewer, String contentType);

	/**
	 * Disposes the contributor
	 */
	void dispose();

	/**
	 * isValidIdentifier
	 * 
	 * @param c
	 * @param keyCode
	 * @return boolean
	 */
	boolean isValidIdentifier(char c, int keyCode);

	/**
	 * isValidActivationCharacter
	 * 
	 * @param c
	 * @param keyCode
	 * @return boolean
	 */
	boolean isValidActivationCharacter(char c, int keyCode);

}
