/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.jface.text.source.projection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class UnifiedProjectionAnnotation extends ProjectionAnnotation
{

	/**
	 * The type of projection annotations.
	 */
	public static final String TYPE = "org.eclipse.projection"; //$NON-NLS-1$

	private static final int COLOR = SWT.COLOR_GRAY;
	private Image fgCollapsedImage;
	private Image fgExpandedImage;

	private Color color;
	/** The state of this annotation */
	private boolean fIsCollapsed = false;
	/** Indicates whether this annotation should be painted as range */
	private boolean fIsRangeIndication = false;

	/**
	 * Creates a new expanded projection annotation.
	 */
	public UnifiedProjectionAnnotation()
	{
		this(false);
	}

	/**
	 * Creates a new projection annotation. When <code>isCollapsed</code> is <code>true</code> the annotation is
	 * initially collapsed.
	 * 
	 * @param isCollapsed
	 *            <code>true</code> if the annotation should initially be collapsed, <code>false</code> otherwise
	 */
	public UnifiedProjectionAnnotation(boolean isCollapsed)
	{
		super(isCollapsed);
		fIsCollapsed = isCollapsed;
	}

	/**
	 * Enables and disables the range indication for this annotation.
	 * 
	 * @param rangeIndication
	 *            the enable state for the range indication
	 */
	public void setRangeIndication(boolean rangeIndication)
	{
		fIsRangeIndication = rangeIndication;
	}

	private void drawRangeIndication(GC gc, Canvas canvas, Rectangle r)
	{
		final int MARGIN = 3;
		Color fg = gc.getForeground();
		gc.setForeground(color == null ? canvas.getDisplay().getSystemColor(COLOR) : color);

		gc.setLineWidth(1);
		/*
		 * cap the height - at least on GTK, large numbers are converted to negatives at some point
		 */
		int height = Math.min(r.y + r.height - MARGIN, canvas.getSize().y);
		gc.drawLine(r.x + 4, r.y + 12, r.x + 4, height);
		gc.drawLine(r.x + 4, height, r.x + r.width - MARGIN, height);
		gc.setForeground(fg);
	}

	/**
	 * @see org.eclipse.jface.text.source.IAnnotationPresentation#paint(org.eclipse.swt.graphics.GC,
	 *      org.eclipse.swt.widgets.Canvas, org.eclipse.swt.graphics.Rectangle)
	 */
	public void paint(GC gc, Canvas canvas, Rectangle rectangle)
	{

		Image image = getImage(canvas);
		if (image != null)
		{
			// int halign = SWT.LEFT;
			int valign = SWT.TOP;
			if (image != null)
			{

				Rectangle bounds = image.getBounds();

				// int x = 0;
				// switch (halign)
				// {
				// case SWT.LEFT:
				// break;
				// case SWT.CENTER:
				// x = (rectangle.width - bounds.width) / 2;
				// break;
				// case SWT.RIGHT:
				// x = rectangle.width - bounds.width;
				// break;
				// }

				int y = 0;
				switch (valign)
				{
					case SWT.TOP:
					{
						FontMetrics fontMetrics = gc.getFontMetrics();
						y = (fontMetrics.getHeight() - bounds.height) / 2;
						break;
					}
					case SWT.CENTER:
						y = (rectangle.height - bounds.height) / 2;
						break;
					case SWT.BOTTOM:
					{
						FontMetrics fontMetrics = gc.getFontMetrics();
						y = rectangle.height - (fontMetrics.getHeight() + bounds.height) / 2;
						break;
					}
				}

				gc.drawImage(image, 2, rectangle.y + y + 2);
			}
			if (fIsRangeIndication)
			{
				FontMetrics fontMetrics = gc.getFontMetrics();
				int delta = (fontMetrics.getHeight() - image.getBounds().height) / 2;
				rectangle.y += delta;
				rectangle.height -= delta;
				drawRangeIndication(gc, canvas, rectangle);
			}
		}
	}

	/**
	 * @see org.eclipse.jface.text.source.IAnnotationPresentation#getLayer()
	 */
	public int getLayer()
	{
		return IAnnotationPresentation.DEFAULT_LAYER;
	}

	private Image getImage(Canvas canvas)
	{
		initializeImages(canvas);
		return isCollapsed() ? fgCollapsedImage : fgExpandedImage;
	}

	/**
	 * Gets the collapse image descriptor
	 * 
	 * @return - collapsed descriptor
	 */
	public ImageDescriptor getCollapsedImage()
	{
		return ImageDescriptor.createFromFile(UnifiedProjectionAnnotation.class, "images/collapsed.png"); //$NON-NLS-1$
	}

	/**
	 * Gets the expand image descriptor
	 * 
	 * @return - expanded descriptor
	 */
	public ImageDescriptor getExpandedImage()
	{
		return ImageDescriptor.createFromFile(UnifiedProjectionAnnotation.class, "images/expanded.png"); //$NON-NLS-1$
	}

	private void initializeImages(Canvas canvas)
	{
		if (fgCollapsedImage == null)
		{

			ImageDescriptor descriptor = getCollapsedImage();
			fgCollapsedImage = descriptor.createImage(canvas.getDisplay());
			descriptor = getExpandedImage();
			fgExpandedImage = descriptor.createImage(canvas.getDisplay());
			canvas.addDisposeListener(new DisposeListener()
			{

				public void widgetDisposed(DisposeEvent e)
				{
					if (fgCollapsedImage != null)
					{
						fgCollapsedImage.dispose();
						fgCollapsedImage = null;
					}
					if (fgExpandedImage != null)
					{
						fgExpandedImage.dispose();
						fgExpandedImage = null;
					}
				}

			});
		}
	}

	/**
	 * Returns the state of this annotation.
	 * 
	 * @return <code>true</code> if collapsed
	 */
	public boolean isCollapsed()
	{
		return fIsCollapsed;
	}

	/**
	 * Marks this annotation as being collapsed.
	 */
	public void markCollapsed()
	{
		fIsCollapsed = true;
	}

	/**
	 * Marks this annotation as being unfolded.
	 */
	public void markExpanded()
	{
		fIsCollapsed = false;
	}

	/**
	 * Sets the color of this annotation
	 * 
	 * @param color
	 */
	public void setColor(Color color)
	{
		this.color = color;
	}

}
