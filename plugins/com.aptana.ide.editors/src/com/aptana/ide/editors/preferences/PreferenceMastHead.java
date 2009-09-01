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
package com.aptana.ide.editors.preferences;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.editors.unified.UnifiedColorManager;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PreferenceMastHead
{

	/**
	 * HEADER_BG_COLOR
	 */
	public static final Color HEADER_BG_COLOR = UnifiedColorManager.getInstance().getColor(new RGB(43, 49, 60));

	/**
	 * HEADER_FG_COLOR
	 */
	public static final Color HEADER_FG_COLOR = UnifiedColorManager.getInstance().getColor(new RGB(196, 196, 196));

	/**
	 * FOOTER_BG_COLOR
	 */
	public static final Color FOOTER_BG_COLOR = UnifiedColorManager.getInstance().getColor(new RGB(110, 110, 110));

	/**
	 * FOOTER_FG_COLOR
	 */
	public static final Color FOOTER_FG_COLOR = UnifiedColorManager.getInstance().getColor(new RGB(196, 196, 196));

	private Composite top;
	private Composite parentVisible;
	private Image image;
	private Label editorDescription;

	/**
	 * Creates a new preference mast head
	 * 
	 * @param parent -
	 *            parent composite
	 * @param description -
	 *            text description of pref page
	 * @param parentLevel -
	 *            parent level to paint to (use -1 to turn off)
	 * @param imageDescriptor -
	 *            image to display for pref page
	 */
	public PreferenceMastHead(Composite parent, String description, int parentLevel, ImageDescriptor imageDescriptor)
	{
		top = new Composite(parent, SWT.NONE);
		GridLayout topLayout = new GridLayout(2, false);
		topLayout.marginHeight = 0;
		topLayout.marginWidth = 0;
		top.setLayout(topLayout);
		GridData topData = new GridData(SWT.FILL, SWT.FILL, true, true);
		top.setBackground(HEADER_BG_COLOR);
		if (imageDescriptor != null)
		{
			image = imageDescriptor.createImage();
			topData.heightHint = image.getImageData().height + 10;
		}
		Composite currParent = top.getParent();
		int parents = 0;
		while (currParent != null && parents <= parentLevel)
		{
			currParent = currParent.getParent();
			parents++;
		}
		if (currParent != null && parentLevel > 0)
		{
			parentVisible = currParent;
		}
		top.setLayoutData(topData);
		top.addPaintListener(new PaintListener()
		{

			public void paintControl(PaintEvent e)
			{
				if (top.isVisible())
				{
					if (parentVisible != null)
					{
						GC gc = new GC(parentVisible);
						gc.setBackground(parentVisible.getBackground());
						gc.fillRectangle(0, 0, parentVisible.getSize().x, parentVisible.getSize().y);
						gc.setBackground(HEADER_BG_COLOR);
						gc.fillRectangle(0, 0, parentVisible.getSize().x, top.getSize().y + 10);
						gc.setBackground(FOOTER_BG_COLOR);
						gc.fillRectangle(0, top.getSize().y + 10, parentVisible.getSize().x, 5);
						gc.dispose();
					}
					if (image != null)
					{
						GC gc = new GC(top);
						gc.drawImage(image, 5, 10);
						gc.dispose();
					}
				}
			}

		});
		Composite descComp = new Composite(top, SWT.NONE);
		GridLayout descCompLayout = new GridLayout(1, true);
		descCompLayout.marginHeight = 13;
		descCompLayout.marginWidth = 0;
		descComp.setLayout(descCompLayout);
		descComp.setBackground(HEADER_BG_COLOR);
		editorDescription = new Label(descComp, SWT.LEFT | SWT.WRAP);
		Font font = editorDescription.getFont();
		FontData[] fd = SWTUtils.resizeFont(font, 2);
		Font newFont = new Font(top.getDisplay(), fd);
		top.setFont(newFont);
		editorDescription.setFont(newFont);
		editorDescription.setBackground(HEADER_BG_COLOR);
		GridData descData = new GridData(SWT.FILL, SWT.BOTTOM, true, true);
		editorDescription.setLayoutData(descData);
		GridData descCompData = new GridData(SWT.FILL, SWT.FILL, true, true);
		if (image != null)
		{
			descCompData.horizontalIndent = image.getImageData().width + 15;
		}
		descComp.setLayoutData(descCompData);
		if (description != null)
		{
			editorDescription.setForeground(HEADER_FG_COLOR);
			editorDescription.setText(description);
		}
	}

	/**
	 * Gets the mast head composite
	 * 
	 * @return - mast head composite
	 */
	public Composite getControl()
	{
		return top;
	}

	/**
	 * Sets the masthead to be visible or not
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		if (!visible && parentVisible != null)
		{
			GC gc = new GC(parentVisible);
			gc.setBackground(parentVisible.getBackground());
			gc.fillRectangle(0, 0, parentVisible.getSize().x, parentVisible.getSize().y);
			gc.dispose();
		}
	}

}
