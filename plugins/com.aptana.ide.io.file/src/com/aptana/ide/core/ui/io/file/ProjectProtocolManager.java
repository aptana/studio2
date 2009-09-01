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
package com.aptana.ide.core.ui.io.file;

import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.IVirtualFileManagerDialog;
import com.aptana.ide.core.io.ProtocolManager;
import com.aptana.ide.core.io.sync.SyncManager;
import com.aptana.ide.core.ui.CoreUIPlugin;

/**
 * @author Kevin Lindsey
 */
public class ProjectProtocolManager extends ProtocolManager
{
	/*
	 * Fields
	 */
	private static Image fProjectIcon;

	/**
	 * static constructor
	 */
	static
	{
		ImageDescriptor imageDescriptor = CoreUIPlugin.getImageDescriptor("icons/project_protocol.gif"); //$NON-NLS-1$

		if (imageDescriptor != null)
		{
			fProjectIcon = imageDescriptor.createImage();
		}
	}

	/**
	 * FileSystemRoots
	 */
	public static String FileSystemRoots = "::"; //$NON-NLS-1$

	/**
	 * LocalProtocolManager
	 */
	static ProjectProtocolManager _localProtocolManager = new ProjectProtocolManager();

	/**
	 * @see com.aptana.ide.core.io.ProtocolManager#createFileManager()
	 */
	public IVirtualFileManager createFileManager()
	{
		return createFileManager(true);
	}

	/**
	 * Creates a file manager, but does not add it to the list
	 * 
	 * @param addManager
	 * @return IVirtualFileManager
	 */
	public IVirtualFileManager createFileManager(boolean addManager)
	{
		ProjectFileManager lfm = new ProjectFileManager(this);
		lfm.setHidden(true);
		if (addManager)
		{
			this.addFileManager(lfm);
		}
		return lfm;
	}

	/**
	 * Creates a file manager, but does not add it to the list
	 * 
	 * @param temporary
	 * @return IVirtualFileManager
	 */
	public IVirtualFileManager createTemporaryFileManager(boolean temporary)
	{
		ProjectFileManager lfm = new ProjectFileManager(this);
		lfm.setHidden(true);
		if (!temporary)
		{
			this.addFileManager(lfm);
		}
		return lfm;
	}

	/**
	 * getInstance
	 * 
	 * @return ProjectProtocolManager
	 */
	public static ProjectProtocolManager getInstance()
	{
		return _localProtocolManager;
	}

	/**
	 * @see ProtocolManager#getStaticInstance()
	 */
	public ProtocolManager getStaticInstance()
	{
		return getInstance();
	}

	/**
	 * @see ProtocolManager#createPropertyDialog(Shell, int)
	 */
	public IVirtualFileManagerDialog createPropertyDialog(Shell parent, int style)
	{
	    ProjectLocationDialog dialog = new ProjectLocationDialog(parent);
	    return new ProjectLocationDialogWrapper(dialog);
	}

	/**
	 * getImage
	 * 
	 * @return Image
	 */
	public Image getImage()
	{
		return fProjectIcon;
	}

	/**
	 * getFileManager
	 * 
	 * @param relativePath
	 *            the local path
	 * @return Returns the file manager that matches this base path
	 */
	public IVirtualFileManager[] getFileManagers(String relativePath)
	{
		IVirtualFileManager[] fms = getFileManagers();
		ArrayList<IVirtualFileManager> newManagers = new ArrayList<IVirtualFileManager>();
		for (int i = 0; i < fms.length; i++)
		{
			ProjectFileManager manager = (ProjectFileManager) fms[i];
			if (relativePath.equals(manager.getRelativePath()))
			{
				newManagers.add(manager);
			}
		}
		return newManagers.toArray(new IVirtualFileManager[0]);
	}

	/**
	 * @see com.aptana.ide.core.io.ProtocolManager#getManagedType()
	 */
	public String getManagedType()
	{
		return ProjectFileManager.class.getName();
	}

	/**
	 * @see com.aptana.ide.core.io.ProtocolManager#getFileManagers()
	 */
	public IVirtualFileManager[] getFileManagers()
	{
		return (IVirtualFileManager[]) SyncManager.getSyncManager().getItems(ProjectFileManager.class);
	}

	/**
	 * @see com.aptana.ide.core.io.ProtocolManager#fromString(java.lang.String)
	 */
	// public IVirtualFileManager fromString(String hash)
	// {
	// return ProjectFileManager.fromStringStatic(hash);
	// }
}
