/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package org.eclipse.jface.text.source;

import org.eclipse.jface.internal.text.revisions.RevisionPainter;
import org.eclipse.jface.internal.text.source.DiffPainter;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.revisions.IRevisionRulerColumn;
import org.eclipse.jface.text.revisions.RevisionInformation;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * Taken from org.eclipse.jface.text.source.LineNumberChangeRulerColumn
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class UnifiedLineNumberChangeRulerColumn extends LineNumberRulerColumn implements IVerticalRulerInfo,
		IVerticalRulerInfoExtension, IChangeRulerColumn, IRevisionRulerColumn
{
	/** The ruler's annotation model. */
	private IAnnotationModel fAnnotationModel;
	/** <code>true</code> if changes should be displayed using character indications instead of background colors. */
	private boolean fCharacterDisplay;

	private StyledText textWidget;
	private ISourceViewer sourceViewer;

	/**
	 * The revision painter strategy.
	 * 
	 * @since 3.2
	 */
	private final RevisionPainter fRevisionPainter;
	/**
	 * The diff information painter strategy.
	 * 
	 * @since 3.2
	 */
	private final DiffPainter fDiffPainter;

	/**
	 * Creates a new instance.
	 * 
	 * @param sharedColors
	 *            the shared colors provider to use
	 */
	public UnifiedLineNumberChangeRulerColumn(ISharedTextColors sharedColors)
	{
		Assert.isNotNull(sharedColors);
		fRevisionPainter = new RevisionPainter(this, sharedColors);
		fDiffPainter = new DiffPainter(this, sharedColors);
	}

	/**
	 * @see org.eclipse.jface.text.source.LineNumberRulerColumn#createControl(org.eclipse.jface.text.source.CompositeRuler,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	public Control createControl(CompositeRuler parentRuler, Composite parentControl)
	{
		Control control = super.createControl(parentRuler, parentControl);
		fRevisionPainter.setParentRuler(parentRuler);
		fDiffPainter.setParentRuler(parentRuler);
		return control;
	}

	/**
	 * @see org.eclipse.jface.text.source.IVerticalRulerInfo#getLineOfLastMouseButtonActivity()
	 */
	public int getLineOfLastMouseButtonActivity()
	{
		return getParentRuler().getLineOfLastMouseButtonActivity();
	}

	/**
	 * @see org.eclipse.jface.text.source.IVerticalRulerInfo#toDocumentLineNumber(int)
	 */
	public int toDocumentLineNumber(int y_coordinate)
	{
		return getParentRuler().toDocumentLineNumber(y_coordinate);
	}

	/**
	 * @see IVerticalRulerColumn#setModel(IAnnotationModel)
	 */
	public void setModel(IAnnotationModel model)
	{
		setAnnotationModel(model);
		fRevisionPainter.setModel(model);
		fDiffPainter.setModel(model);
		updateNumberOfDigits();
		computeIndentations();
		layout(true);
		postRedraw();
	}

	private void setAnnotationModel(IAnnotationModel model)
	{
		if (fAnnotationModel != model)
			fAnnotationModel = model;
	}

	/**
	 * Sets the display mode of the ruler. If character mode is set to <code>true</code>, diff information will be
	 * displayed textually on the line number ruler.
	 * 
	 * @param characterMode
	 *            <code>true</code> if diff information is to be displayed textually.
	 */
	public void setDisplayMode(boolean characterMode)
	{
		if (characterMode != fCharacterDisplay)
		{
			fCharacterDisplay = characterMode;
			updateNumberOfDigits();
			computeIndentations();
			layout(true);
		}
	}

	/**
	 * @see org.eclipse.jface.text.source.IVerticalRulerInfoExtension#getModel()
	 */
	public IAnnotationModel getModel()
	{
		return fAnnotationModel;
	}

	/**
	 * @see org.eclipse.jface.text.source.LineNumberRulerColumn#createDisplayString(int)
	 */
	protected String createDisplayString(int line)
	{
		if (fCharacterDisplay && getModel() != null)
			return super.createDisplayString(line) + fDiffPainter.getDisplayCharacter(line);
		return super.createDisplayString(line);
	}

	/**
	 * @see org.eclipse.jface.text.source.LineNumberRulerColumn#computeNumberOfDigits()
	 */
	protected int computeNumberOfDigits()
	{
		if (fCharacterDisplay && getModel() != null)
			return super.computeNumberOfDigits() + 1;
		return super.computeNumberOfDigits();
	}

	/**
	 * @see org.eclipse.jface.text.source.IVerticalRulerInfoExtension#addVerticalRulerListener(org.eclipse.jface.text.source.IVerticalRulerListener)
	 */
	public void addVerticalRulerListener(IVerticalRulerListener listener)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.eclipse.jface.text.source.IVerticalRulerInfoExtension#removeVerticalRulerListener(org.eclipse.jface.text.source.IVerticalRulerListener)
	 */
	public void removeVerticalRulerListener(IVerticalRulerListener listener)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.eclipse.jface.text.source.LineNumberRulerColumn#doPaint(org.eclipse.swt.graphics.GC)
	 */
	void doPaint(GC gc, ILineRange visibleLines)
	{
		Color foreground = gc.getForeground();
		if (visibleLines != null)
		{
			fRevisionPainter.paint(gc, visibleLines);
			if (!fRevisionPainter.hasInformation()) // don't paint quick diff colors if revisions are painted
				fDiffPainter.paint(gc, visibleLines);
		}
		gc.setForeground(foreground);

		Display display = textWidget.getDisplay();

		// draw diff info
		int y = -JFaceTextUtil.getHiddenTopLinePixels(textWidget);

		int lastLine = end(visibleLines);
		for (int line = visibleLines.getStartLine(); line < lastLine; line++)
		{
			int widgetLine = JFaceTextUtil.modelLineToWidgetLine(sourceViewer, line);
			if (widgetLine == -1)
				continue;

			int lineHeight = 0;
			if (widgetLine + 1 < textWidget.getLineCount())
			{
				int start = textWidget.getOffsetAtLine(widgetLine);
				int next = textWidget.getOffsetAtLine(widgetLine + 1) - 1;
				Rectangle bounds = textWidget.getTextBounds(start, next);
				lineHeight = Math.max(bounds.height, textWidget.getLineHeight(widgetLine));
			}
			else
			{
				lineHeight = textWidget.getLineHeight(textWidget.getOffsetAtLine(widgetLine));
			}
			paintLine(line, y, lineHeight, gc, display);
			y += lineHeight;
		}
	}

	private static int end(ILineRange range)
	{
		return range.getStartLine() + range.getNumberOfLines();
	}

	/**
	 * @see org.eclipse.jface.text.source.IVerticalRulerInfoExtension#getHover()
	 */
	public IAnnotationHover getHover()
	{
		int activeLine = getParentRuler().getLineOfLastMouseButtonActivity();
		if (fRevisionPainter.hasHover(activeLine))
			return fRevisionPainter.getHover();
		if (fDiffPainter.hasHover(activeLine))
			return fDiffPainter.getHover();
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.source.IChangeRulerColumn#setHover(org.eclipse.jface.text.source.IAnnotationHover)
	 */
	public void setHover(IAnnotationHover hover)
	{
		fRevisionPainter.setHover(hover);
		fDiffPainter.setHover(hover);
	}

	/**
	 * @see org.eclipse.jface.text.source.IChangeRulerColumn#setBackground(org.eclipse.swt.graphics.Color)
	 */
	public void setBackground(Color background)
	{
		super.setBackground(background);
		fRevisionPainter.setBackground(background);
		fDiffPainter.setBackground(background);
	}

	/**
	 * @see org.eclipse.jface.text.source.IChangeRulerColumn#setAddedColor(org.eclipse.swt.graphics.Color)
	 */
	public void setAddedColor(Color addedColor)
	{
		fDiffPainter.setAddedColor(addedColor);
	}

	/**
	 * @see org.eclipse.jface.text.source.IChangeRulerColumn#setChangedColor(org.eclipse.swt.graphics.Color)
	 */
	public void setChangedColor(Color changedColor)
	{
		fDiffPainter.setChangedColor(changedColor);
	}

	/**
	 * @see org.eclipse.jface.text.source.IChangeRulerColumn#setDeletedColor(org.eclipse.swt.graphics.Color)
	 */
	public void setDeletedColor(Color deletedColor)
	{
		fDiffPainter.setDeletedColor(deletedColor);
	}

	/**
	 * @see org.eclipse.jface.text.revisions.IRevisionRulerColumn#setRevisionInformation(org.eclipse.jface.text.revisions.RevisionInformation)
	 */
	public void setRevisionInformation(RevisionInformation info)
	{
		fRevisionPainter.setRevisionInformation(info);
	}

	/**
	 * Returns the revision selection provider.
	 * 
	 * @return the revision selection provider
	 * @since 3.2
	 */
	public ISelectionProvider getRevisionSelectionProvider()
	{
		return fRevisionPainter.getRevisionSelectionProvider();
	}

	/**
	 * @return the sourceViewer
	 */
	public ISourceViewer getSourceViewer()
	{
		return sourceViewer;
	}

	/**
	 * @param sourceViewer
	 *            the sourceViewer to set
	 */
	public void setSourceViewer(ISourceViewer sourceViewer)
	{
		this.sourceViewer = sourceViewer;
	}

	/**
	 * @return the textWidget
	 */
	public StyledText getTextWidget()
	{
		return textWidget;
	}

	/**
	 * @param textWidget
	 *            the textWidget to set
	 */
	public void setTextWidget(StyledText textWidget)
	{
		this.textWidget = textWidget;
	}
}
