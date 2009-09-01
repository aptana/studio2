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

import java.util.ResourceBundle;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension4;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.ui.texteditor.TextOperationAction;

import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.UnifiedConfiguration;

/**
 * InformationDispatchAction
 * 
 * @author Ingo Muschenetz
 */
public class InformationDispatchAction extends TextEditorAction
{
	/** The wrapped text operation action. */
	private TextOperationAction fTextOperationAction;

	/** The information presenter that shows all information with scroll bars */
	private InformationPresenter fInformationPresenter;

	/**
	 * Creates a dispatch action.
	 * 
	 * @param resourceBundle
	 *            the resource bundle
	 * @param prefix
	 *            the prefix
	 * @param textOperationAction
	 *            the text operation action
	 * @param editor
	 */
	public InformationDispatchAction(ResourceBundle resourceBundle, String prefix,
			TextOperationAction textOperationAction, ITextEditor editor)
	{
		super(resourceBundle, prefix, editor);
		if (textOperationAction == null)
		{
			throw new IllegalArgumentException();
		}
		fTextOperationAction = textOperationAction;
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run()
	{

		/**
		 * Information provider used to present the information.
		 * 
		 * @since 3.0
		 */
		class InformationProvider implements IInformationProvider, IInformationProviderExtension2
		{

			private IRegion fHoverRegion;
			private String fHoverInfo;
			private IInformationControlCreator fControlCreator;

			InformationProvider(IRegion hoverRegion, String hoverInfo, IInformationControlCreator controlCreator)
			{
				fHoverRegion = hoverRegion;
				fHoverInfo = hoverInfo;
				fControlCreator = controlCreator;
			}

			/*
			 * @see org.eclipse.jface.text.information.IInformationProvider#getSubject(org.eclipse.jface.text.ITextViewer,
			 *      int)
			 */
			public IRegion getSubject(ITextViewer textViewer, int invocationOffset)
			{
				return fHoverRegion;
			}

			/*
			 * @see org.eclipse.jface.text.information.IInformationProvider#getInformation(org.eclipse.jface.text.ITextViewer,
			 *      org.eclipse.jface.text.IRegion)
			 */
			public String getInformation(ITextViewer textViewer, IRegion subject)
			{
				return fHoverInfo;
			}

			/*
			 * @see org.eclipse.jface.text.information.IInformationProviderExtension2#getInformationPresenterControlCreator()
			 * @since 3.0
			 */
			public IInformationControlCreator getInformationPresenterControlCreator()
			{
				return fControlCreator;
			}
		}

		ISourceViewer sourceViewer = ((IUnifiedEditor) getTextEditor()).getViewer();
		if (sourceViewer == null)
		{
			fTextOperationAction.run();
			return;
		}

		if (sourceViewer instanceof ITextViewerExtension4)
		{
			ITextViewerExtension4 extension4 = (ITextViewerExtension4) sourceViewer;
			if (extension4.moveFocusToWidgetToken())
			{
				return;
			}
		}

		if (!(sourceViewer instanceof ITextViewerExtension2))
		{
			fTextOperationAction.run();
			return;
		}

		ITextViewerExtension2 textViewerExtension2 = (ITextViewerExtension2) sourceViewer;

		// does a text hover exist?
		ITextHover textHover = textViewerExtension2.getCurrentTextHover();
		if (textHover == null)
		{
			fTextOperationAction.run();
			return;
		}

		Point hoverEventLocation = textViewerExtension2.getHoverEventLocation();
		int offset = computeOffsetAtLocation(sourceViewer, hoverEventLocation.x, hoverEventLocation.y);
		if (offset == -1)
		{
			fTextOperationAction.run();
			return;
		}

		try
		{
			// get the text hover content
			String contentType = TextUtilities.getContentType(sourceViewer.getDocument(),
					UnifiedConfiguration.UNIFIED_PARTITIONING, offset, true);

			IRegion hoverRegion = textHover.getHoverRegion(sourceViewer, offset);
			if (hoverRegion == null)
			{
				return;
			}

			String hoverInfo = textHover.getHoverInfo(sourceViewer, hoverRegion);

			IInformationControlCreator controlCreator = null;
			if (textHover instanceof IInformationProviderExtension2)
			{
				controlCreator = ((IInformationProviderExtension2) textHover).getInformationPresenterControlCreator();
			}

			IInformationProvider informationProvider = new InformationProvider(hoverRegion, hoverInfo, controlCreator);
			InformationPresenter presenter = getInformationPresenter();
			presenter.setOffset(offset);
			presenter.setDocumentPartitioning(UnifiedConfiguration.UNIFIED_PARTITIONING);
			presenter.setInformationProvider(informationProvider, contentType);
			presenter.showInformation();

		}
		catch (BadLocationException e)
		{
		}
	}

	// modified version from TextViewer
	private int computeOffsetAtLocation(ITextViewer textViewer, int x, int y)
	{

		StyledText styledText = textViewer.getTextWidget();
		IDocument document = textViewer.getDocument();

		if (document == null)
		{
			return -1;
		}

		try
		{
			int widgetLocation = styledText.getOffsetAtLocation(new Point(x, y));
			if (textViewer instanceof ITextViewerExtension5)
			{
				ITextViewerExtension5 extension = (ITextViewerExtension5) textViewer;
				return extension.widgetOffset2ModelOffset(widgetLocation);
			}
			IRegion visibleRegion = textViewer.getVisibleRegion();
			return widgetLocation + visibleRegion.getOffset();
		}
		catch (IllegalArgumentException e)
		{
			return -1;
		}
	}

	private InformationPresenter getInformationPresenter()
	{
		if (fInformationPresenter == null)
		{
			IInformationControlCreator informationControlCreator = UnifiedConfiguration
					.getInformationPresenterControlCreator();
			fInformationPresenter = new InformationPresenter(informationControlCreator);
			fInformationPresenter.setSizeConstraints(60, 10, true, true);
			fInformationPresenter.install(((IUnifiedEditor) getTextEditor()).getViewer());

		}
		return fInformationPresenter;
	}

	/**
	 * dispose
	 */
	public void dispose()
	{
		fTextOperationAction = null;
		if (fInformationPresenter != null)
		{
			fInformationPresenter.dispose();
			fInformationPresenter = null;
		}
	}
}
