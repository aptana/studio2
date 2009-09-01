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

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.ide.core.ui.editors.ISaveEventListener;
import com.aptana.ide.editors.unified.context.IContextAwareness;
import com.aptana.ide.views.outline.UnifiedOutlinePage;
import com.aptana.ide.views.outline.UnifiedQuickOutlinePage;

/**
 * @author Paul Colton
 */
public interface IUnifiedEditor extends ISaveEventListener
{
	/**
	 * getFileContext
	 * 
	 * @return EditorFileContext
	 */
	EditorFileContext getFileContext();

	/**
	 * getViewer
	 * 
	 * @return ISourceViewer
	 */
	ISourceViewer getViewer();

	/**
	 * getConfiguration
	 * 
	 * @return ISourceViewer
	 */
	SourceViewerConfiguration getConfiguration();

	/**
	 * Gets the contributor for this editor
	 * 
	 * @return - base contributor
	 */
	IUnifiedEditorContributor getBaseContributor();

	/**
	 * Gets the default file extension associated with this editor
	 * 
	 * @return - file extension string
	 */
	String getDefaultFileExtension();

	/**
	 * getContextAwareness
	 * 
	 * @return IContextAwareness
	 */
	IContextAwareness getContextAwareness();

	/**
	 * getDocumentProvider
	 * 
	 * @return IDocumentProvider
	 */
	IDocumentProvider getDocumentProvider();

	/**
	 * close
	 * 
	 * @param save
	 */
	void close(boolean save);

	/**
	 * getSite
	 * 
	 * @return IWorkbenchPartSite
	 */
	IWorkbenchPartSite getSite();

	/**
	 * getEditorInput
	 * 
	 * @return IEditorInput
	 */
	IEditorInput getEditorInput();

	/**
	 * getEditor
	 * 
	 * @return IEditorPart
	 */
	IEditorPart getEditor();

	/**
	 * selectAndReveal
	 * 
	 * @param offset
	 * @param length
	 */
	void selectAndReveal(int offset, int length);

	/**
	 * showWhitespace
	 * 
	 * @param state
	 */
	void showWhitespace(boolean state);
	
	/**
	 * Show the piano key style line backgrounds
	 * @param state
	 */
	void showPianoKeys(boolean state);

	// void addBracketMatchListener(IUnifiedBracketMatcherListener obj);
	//
	// void removeBracketMatchListener(IUnifiedBracketMatcherListener obj);

	/**
	 * Gets outline page.
	 * Should cache the page when possible.
	 * 
	 * @return outline page.
	 */
	UnifiedOutlinePage getOutlinePage();
	
	/**
     * Creates new quick outline page.
     * Should return new outline page instance every time called.
     * 
     * @return outline page.
     */
	UnifiedQuickOutlinePage createQuickOutlinePage();

	// void matchPair();
	//
	// void selectPair();
	//
	// void selectContentPair();

	/**
	 * getPairMatch
	 * 
	 * @param offset
	 * @return PairMatch
	 */
	PairMatch getPairMatch(int offset);

	/**
	 * The directory we'd like to save this file into if possible
	 * 
	 * @return String
	 */
	String getParentDirectoryHint();

	/**
	 * The directory we'd like to save this file into if possible
	 * 
	 * @param hint
	 */
	void setParentDirectoryHint(String hint);

	/**
	 * Adds a file service change listener to this editor
	 * 
	 * @param listener
	 */
	void addFileServiceChangeListener(IFileServiceChangeListener listener);

	/**
	 * Removes a file service change listener from this editor
	 * 
	 * @param listener
	 */
	void removeFileServiceChangeListener(IFileServiceChangeListener listener);

}
