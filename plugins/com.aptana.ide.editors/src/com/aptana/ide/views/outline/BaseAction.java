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
package com.aptana.ide.views.outline;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;

import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * @author Kevin Lindsey
 */
public class BaseAction extends Action
{
	/**
	 * outline page
	 */
	protected IUnifiedOutlinePage page;
	
	/**
	 * tree viewer
	 */
	protected TreeViewer treeViewer;

	/**
	 * JSBaseAction
	 * 
	 * @param page
	 * @param icon
	 * @param text
	 * @param style
	 */
	public BaseAction(IUnifiedOutlinePage page, ImageDescriptor icon, String text, int style)
	{
		super(text, style);

		this.page = page;
		this.treeViewer = page.getTreeViewer();
		this.setImageDescriptor(icon);
		this.setToolTipText(this.getText());
	}

	/**
	 * Retrieves the image descriptor associated with resource from the image descriptor registry. If the image
	 * descriptor cannot be retrieved, attempt to find and load the image descriptor at the location specified in
	 * resource.
	 * 
	 * @param imageFilePath
	 *            the image descriptor to retrieve
	 * @return The image descriptor associated with resource or the default "missing" image descriptor if one could not
	 *         be found
	 */
	protected static ImageDescriptor getImageDescriptor(String imageFilePath)
	{
		ImageDescriptor imageDescriptor = UnifiedEditorsPlugin.getImageDescriptor(imageFilePath);

		if (imageDescriptor == null)
		{
			imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
		}

		return imageDescriptor;
	}
}
