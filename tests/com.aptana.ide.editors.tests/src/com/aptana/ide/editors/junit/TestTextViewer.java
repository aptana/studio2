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
package com.aptana.ide.editors.junit;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IAutoIndentStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEventConsumer;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.IViewportListener;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;

import com.aptana.ide.editors.unified.IUnifiedViewer;

/**
 * TestTextViewer
 * 
 * @author Ingo Muschenetz
 */
public class TestTextViewer implements IUnifiedViewer
{
	/**
	 * TestTextViewer
	 * 
	 * @param documentSource
	 */
	public TestTextViewer(String documentSource)
	{
		document = new Document(documentSource);
	}

	private IDocument document;

	/**
	 * @see org.eclipse.jface.text.ITextViewer#getTextWidget()
	 */
	public StyledText getTextWidget()
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setUndoManager(org.eclipse.jface.text.IUndoManager)
	 */
	public void setUndoManager(IUndoManager undoManager)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setTextDoubleClickStrategy(org.eclipse.jface.text.ITextDoubleClickStrategy,
	 *      java.lang.String)
	 */
	public void setTextDoubleClickStrategy(ITextDoubleClickStrategy strategy, String contentType)
	{
	}

	/**
	 * setAutoIndentStrategy
	 * @param strategy 
	 * @param contentType 
	 */
	public void setAutoIndentStrategy(IAutoIndentStrategy strategy, String contentType)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setTextHover(org.eclipse.jface.text.ITextHover, java.lang.String)
	 */
	public void setTextHover(ITextHover textViewerHover, String contentType)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#activatePlugins()
	 */
	public void activatePlugins()
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#resetPlugins()
	 */
	public void resetPlugins()
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#addViewportListener(org.eclipse.jface.text.IViewportListener)
	 */
	public void addViewportListener(IViewportListener listener)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#removeViewportListener(org.eclipse.jface.text.IViewportListener)
	 */
	public void removeViewportListener(IViewportListener listener)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#addTextListener(org.eclipse.jface.text.ITextListener)
	 */
	public void addTextListener(ITextListener listener)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#removeTextListener(org.eclipse.jface.text.ITextListener)
	 */
	public void removeTextListener(ITextListener listener)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#addTextInputListener(org.eclipse.jface.text.ITextInputListener)
	 */
	public void addTextInputListener(ITextInputListener listener)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#removeTextInputListener(org.eclipse.jface.text.ITextInputListener)
	 */
	public void removeTextInputListener(ITextInputListener listener)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document)
	{
		this.document = document;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#getDocument()
	 */
	public IDocument getDocument()
	{
		return document;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setEventConsumer(org.eclipse.jface.text.IEventConsumer)
	 */
	public void setEventConsumer(IEventConsumer consumer)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setEditable(boolean)
	 */
	public void setEditable(boolean editable)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#isEditable()
	 */
	public boolean isEditable()
	{
		return false;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setDocument(org.eclipse.jface.text.IDocument, int, int)
	 */
	public void setDocument(IDocument document, int modelRangeOffset, int modelRangeLength)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setVisibleRegion(int, int)
	 */
	public void setVisibleRegion(int offset, int length)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#resetVisibleRegion()
	 */
	public void resetVisibleRegion()
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#getVisibleRegion()
	 */
	public IRegion getVisibleRegion()
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#overlapsWithVisibleRegion(int, int)
	 */
	public boolean overlapsWithVisibleRegion(int offset, int length)
	{
		return false;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#changeTextPresentation(org.eclipse.jface.text.TextPresentation, boolean)
	 */
	public void changeTextPresentation(TextPresentation presentation, boolean controlRedraw)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#invalidateTextPresentation()
	 */
	public void invalidateTextPresentation()
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setTextColor(org.eclipse.swt.graphics.Color)
	 */
	public void setTextColor(Color color)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setTextColor(org.eclipse.swt.graphics.Color, int, int, boolean)
	 */
	public void setTextColor(Color color, int offset, int length, boolean controlRedraw)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#getTextOperationTarget()
	 */
	public ITextOperationTarget getTextOperationTarget()
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#getFindReplaceTarget()
	 */
	public IFindReplaceTarget getFindReplaceTarget()
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setDefaultPrefixes(java.lang.String[], java.lang.String)
	 */
	public void setDefaultPrefixes(String[] defaultPrefixes, String contentType)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setIndentPrefixes(java.lang.String[], java.lang.String)
	 */
	public void setIndentPrefixes(String[] indentPrefixes, String contentType)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setSelectedRange(int, int)
	 */
	public void setSelectedRange(int offset, int length)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#getSelectedRange()
	 */
	public Point getSelectedRange()
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#getSelectionProvider()
	 */
	public ISelectionProvider getSelectionProvider()
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#revealRange(int, int)
	 */
	public void revealRange(int offset, int length)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#setTopIndex(int)
	 */
	public void setTopIndex(int index)
	{
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#getTopIndex()
	 */
	public int getTopIndex()
	{
		return 0;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#getTopIndexStartOffset()
	 */
	public int getTopIndexStartOffset()
	{
		return 0;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#getBottomIndex()
	 */
	public int getBottomIndex()
	{
		return 0;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#getBottomIndexEndOffset()
	 */
	public int getBottomIndexEndOffset()
	{
		return 0;
	}

	/**
	 * @see org.eclipse.jface.text.ITextViewer#getTopInset()
	 */
	public int getTopInset()
	{
		return 0;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedViewer#isHotkeyActivated()
	 */
	public boolean isHotkeyActivated()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedViewer#setHotkeyActivated(boolean)
	 */
	public void setHotkeyActivated(boolean value)
	{
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedViewer#setNextIdleActivated(boolean)
	 */
	public void setNextIdleActivated(boolean value)
	{
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedViewer#isNextIdleActivated()
	 */
	public boolean isNextIdleActivated()
	{
		return false;
	}

	/**
	 * @see org.eclipse.jface.text.source.ISourceViewer#configure(org.eclipse.jface.text.source.SourceViewerConfiguration)
	 */
	public void configure(SourceViewerConfiguration configuration)
	{
	}

	/**
	 * @see org.eclipse.jface.text.source.ISourceViewer#setAnnotationHover(org.eclipse.jface.text.source.IAnnotationHover)
	 */
	public void setAnnotationHover(IAnnotationHover annotationHover)
	{
	}

	/**
	 * @see org.eclipse.jface.text.source.ISourceViewer#setDocument(org.eclipse.jface.text.IDocument,
	 *      org.eclipse.jface.text.source.IAnnotationModel)
	 */
	public void setDocument(IDocument document, IAnnotationModel annotationModel)
	{
	}

	/**
	 * @see org.eclipse.jface.text.source.ISourceViewer#setDocument(org.eclipse.jface.text.IDocument,
	 *      org.eclipse.jface.text.source.IAnnotationModel, int, int)
	 */
	public void setDocument(IDocument document, IAnnotationModel annotationModel, int modelRangeOffset,
			int modelRangeLength)
	{
	}

	/**
	 * @see org.eclipse.jface.text.source.ISourceViewer#getAnnotationModel()
	 */
	public IAnnotationModel getAnnotationModel()
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.source.ISourceViewer#setRangeIndicator(org.eclipse.jface.text.source.Annotation)
	 */
	public void setRangeIndicator(Annotation rangeIndicator)
	{
	}

	/**
	 * @see org.eclipse.jface.text.source.ISourceViewer#setRangeIndication(int, int, boolean)
	 */
	public void setRangeIndication(int offset, int length, boolean moveCursor)
	{
	}

	/**
	 * @see org.eclipse.jface.text.source.ISourceViewer#getRangeIndication()
	 */
	public IRegion getRangeIndication()
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.source.ISourceViewer#removeRangeIndication()
	 */
	public void removeRangeIndication()
	{
	}

	/**
	 * @see org.eclipse.jface.text.source.ISourceViewer#showAnnotations(boolean)
	 */
	public void showAnnotations(boolean show)
	{
	}
}
