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
package com.aptana.ide.server.ui.decorators;

import java.util.HashMap;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.ui.ServerUIPlugin;

/**
 * @author Pavel Petrochenko
 */
public class WebServerDecorator extends LabelDecorator
{

	private static final String WEB_CATEGORY_NAME = "Web"; //$NON-NLS-1$
	ImageDescriptor proxy = ServerUIPlugin.getImageDescriptor("/icons/server/small_globe.png"); //$NON-NLS-1$

	/**
	 * @see org.eclipse.jface.viewers.LabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object,
	 *      org.eclipse.jface.viewers.IDecorationContext)
	 */
	@Override
	public Image decorateImage(Image image, Object element, IDecorationContext context)
	{

		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelDecorator#decorateText(java.lang.String, java.lang.Object,
	 *      org.eclipse.jface.viewers.IDecorationContext)
	 */
	@Override
	public String decorateText(String text, Object element, IDecorationContext context)
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelDecorator#prepareDecoration(java.lang.Object, java.lang.String,
	 *      org.eclipse.jface.viewers.IDecorationContext)
	 */
	@Override
	public boolean prepareDecoration(Object element, String originalText, IDecorationContext context)
	{
		return false;
	}

	private HashMap<Image, Image> decoratedImages;

	/**
	 * 
	 */
	public WebServerDecorator()
	{

	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
	 */
	public Image decorateImage(final Image image, Object element)
	{
		IServer srv = (IServer) element;
		if (shouldDecorate(srv))
		{
			if (decoratedImages == null)
			{
				decoratedImages = new HashMap<Image, Image>();
				Display.getDefault().disposeExec(new Runnable()
				{

					public void run()
					{
						for (Image i : decoratedImages.keySet())
						{
							Image image = decoratedImages.get(i);
							if (image != null)
							{
								image.dispose();
							}
						}
					}

				});
			}
			Image image2 = decoratedImages.get(image);
			if (image2 != null)
			{
				return image2;
			}
			CompositeImageDescriptor ma = new CompositeImageDescriptor()
			{

				@Override
				protected void drawCompositeImage(int width, int height)
				{
					ImageData bg = image.getImageData();
					drawImage(bg, 0, 0);
					drawBottomRight();
				}

				protected void drawBottomRight()
				{
					ImageData bg = proxy.getImageData();
					drawImage(bg, 8, 8);
				}

				@Override
				protected Point getSize()
				{
					Rectangle bounds = image.getBounds();
					return new Point(bounds.width, image.getBounds().height);
				}

			};
			Image createImage = ma.createImage();
			decoratedImages.put(image, createImage);
			return createImage;
		}
		return null;
	}

	private boolean shouldDecorate(IServer srv)
	{
		return srv.getServerType().getCategory().equals(WEB_CATEGORY_NAME);
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String, java.lang.Object)
	 */
	public String decorateText(String text, Object element)
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener)
	{

	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose()
	{

	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener)
	{

	}

}
