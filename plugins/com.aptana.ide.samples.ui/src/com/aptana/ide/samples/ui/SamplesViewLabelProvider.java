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
package com.aptana.ide.samples.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.EclipseUIUtils;
import com.aptana.ide.core.ui.ImageUtils;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.samples.model.SamplesEntry;
import com.aptana.ide.samples.model.SamplesInfo;

/**
 * @author Kevin Lindsey
 */
public class SamplesViewLabelProvider extends LabelProvider
{

	private IEditorRegistry registry = EclipseUIUtils.getWorkbenchEditorRegistry();

	private HashMap<Object, Image> images = new HashMap<Object, Image>();
	private Image folder = null;

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#dispose()
	 */
	public void dispose()
	{
		super.dispose();
		Iterator<Image> iter = images.values().iterator();
		while (iter.hasNext())
		{
			iter.next().dispose();
		}
	}

	/**
	 * getImage
	 * 
	 * @param element
	 * @return Image
	 */
	public Image getImage(Object element)
	{
		if (element == SamplesViewContentProvider.LOADING)
		{
			return SamplesUIPlugin.getImage("icons/hourglass.png"); //$NON-NLS-1$
		}
		Image result = null;

		if (element instanceof SamplesInfo)
		{
			String iconFile = ((SamplesInfo) element).getIconFile();
			if (iconFile != null)
			{
				File file = new File(iconFile);
				if (file.exists())
				{
					result = new Image(Display.getDefault(), file.getAbsolutePath());
					images.put(file.getAbsoluteFile(), result);
				}
				else
				{
					result = SamplesUIPlugin.getImage("icons/folder.gif"); //$NON-NLS-1$
					IdeLog.logInfo(SamplesUIPlugin.getDefault(), StringUtils.format(Messages.SamplesViewLabelProvider_INF_ImageNotFound, file
							.getAbsolutePath()));
				}
			}
		}
		else if (element instanceof SamplesEntry)
		{
			SamplesEntry entry = (SamplesEntry) element;
			File file = entry.getFile();
			if (file != null)
			{
				IEditorDescriptor desc = registry.getDefaultEditor(file.getName());
				if (desc == null || desc.getImageDescriptor() == null)
				{
					if (file.isDirectory())
					{
						if (folder == null)
						{
							folder = SamplesUIPlugin.getImage("icons/folder.gif"); //$NON-NLS-1$
						}
						result = folder;
					}
					else
					{
						result = ImageUtils.getIcon(file, UnifiedColorManager.getInstance().getColor(
								new RGB(255, 255, 255)));

					}
				}
				else
				{
					if (images.containsKey(desc))
					{
						result = (Image) images.get(desc);
					}
					else
					{
						result = desc.getImageDescriptor().createImage();
						images.put(desc, result);
					}
				}
			}
		}

		return result;
	}

	/**
	 * getText
	 * 
	 * @param element
	 * @return String
	 */
	public String getText(Object element)
	{
		if (element == SamplesViewContentProvider.LOADING)
		{
			return Messages.SamplesViewLabelProvider_TXT_Loading;
		}
		String result = ""; //$NON-NLS-1$

		if (element instanceof SamplesEntry)
		{
			File file = ((SamplesEntry) element).getFile();
			if (file != null)
			{
				result = file.getName();
			}
		}
		else if (element instanceof SamplesInfo)
		{
			SamplesInfo snippet = (SamplesInfo) element;

			result = snippet.getName();
		}
		else
		{
			result = element.toString();
		}

		return result;
	}
}
