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
package com.aptana.ide.syncing;

import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IViewActionDelegate;

import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.sync.SyncEventHandlerAdapter;
import com.aptana.ide.core.io.sync.VirtualFileManagerSyncPair;
import com.aptana.ide.core.io.sync.VirtualFileSyncPair;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.syncing.views.SmartSyncDialog;

/**
 * Allows the user to create a new "sync" action
 * 
 * @author Ingo Muschenetz
 */
public class FileSynchronizeAction extends BaseSyncAction implements IViewActionDelegate
{

	public FileSynchronizeAction()
	{
	}

	/**
	 * Opens a new Sync Dialog
	 * 
	 * @param conf
	 * @param file
	 */
	protected void openSyncDialog(VirtualFileManagerSyncPair conf, IVirtualFile[] files)
	{
		SmartSyncDialog dialog = new SmartSyncDialog(CoreUIUtils.getActiveShell(), conf, files);
		dialog.open();
		dialog.setHandler(new SyncEventHandlerAdapter()
		{
			public void syncDone(VirtualFileSyncPair item)
			{
				refresh();
			}
		});
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		if (selectedFiles == null || selectedFiles.length == 0)
		{
			return;
		}

		VirtualFileManagerSyncPair conf = getVirtualFileManagerSyncPair(selectedFiles);
		if (conf != null)
		{
			openSyncDialog(conf, selectedFiles);
		}
	}

	/**
	 * Do any post-sync-action refreshing
	 */
	protected void refresh()
	{
		if (selectedObjects != null && selectedObjects.length > 0)
		{
			if (selectedObjects[0] instanceof IResource)
			{
				IResource res = (IResource) selectedObjects[0];
				try
				{
					res.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
				catch (CoreException e)
				{
					//Unable to refresh resource
				}
			}
		}
	}

	@Override
	protected void displayDone(Synchronizer sm)
	{
	}

	@Override
	protected VirtualFileSyncPair[] getItems(Synchronizer sm, VirtualFileManagerSyncPair conf, IVirtualFile[] files)
			throws ConnectionException, IOException
	{
		return new VirtualFileSyncPair[0];
	}

	@Override
	protected void syncItems(Synchronizer sm, VirtualFileSyncPair[] items) throws ConnectionException, IOException
	{	
	}

}
