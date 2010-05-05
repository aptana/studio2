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
package com.aptana.ide.syncing.ui.ingo;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import com.aptana.ide.core.io.ingo.IVirtualFileManager;
import com.aptana.ide.core.io.ingo.ProjectProtocolManager;
import com.aptana.ide.core.io.preferences.CloakingUtils;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;

/**
 * A class to decorate any items which are sync targets
 * 
 * @author Ingo Muschenetz
 */
public class VirtualProjectCloakedDecorator implements ILightweightLabelDecorator
{
	private static final ImageDescriptor SYNC_FOLDER;

	static
	{
		SYNC_FOLDER = SyncingUIPlugin.getImageDescriptor("icons/full/obj16/cloaked_overlay.gif"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object,
	 *      org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration)
	{
		if (!(element instanceof IResource))
		{
			return;
		}

		IResource container = (IResource) element;
		IVirtualFileManager[] tempManagers = ProjectProtocolManager.getInstance().getFileManagers();
		for (int i = 0; i < tempManagers.length; i++)
		{
			IVirtualFileManager manager = tempManagers[i];
			if (manager != null)
			{
				IFileStore file = container instanceof IContainer ?
						manager.createVirtualDirectory(container.getFullPath().toPortableString()) :
							manager.createVirtualFile(container.getFullPath().toPortableString());
				if(CloakingUtils.isFileCloaked(file))
				{
					decoration.addOverlay(SYNC_FOLDER);
					break;
				}
			}				
		}
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
		// TODO Auto-generated method stub
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener)
	{
	}
}
