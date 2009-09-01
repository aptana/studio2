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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IDecoratorManager;

import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.sync.SyncManager;
import com.aptana.ide.core.io.sync.VirtualFileManagerSyncPair;
import com.aptana.ide.core.ui.io.file.LocalProtocolManager;

/**
 * Allows the user to create a new "sync" action
 * 
 * @author Ingo Muschenetz
 */
public class FileCreateSyncConnectionAction extends Action implements IActionDelegate
{
	private IVirtualFile _folder;

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run()
	{
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		if (_folder == null)
		{
			return;
		}

		VirtualFileManagerSyncPair conf = new VirtualFileManagerSyncPair();
		IVirtualFileManager local = LocalProtocolManager.getInstance().createFileManager();
		local.setBasePath(_folder.getAbsolutePath());
		conf.setNickName(_folder.getName());
		conf.setSourceFileManager(local);
		SyncInfoDialog dialog = new SyncInfoDialog(Display.getCurrent().getActiveShell());
		dialog.setItem(conf, true);
		dialog.open();
		conf = dialog.getItem();
		if (conf != null)
		{
			SyncManager.getSyncManager().addItem(conf);
		}
		else
		{
			LocalProtocolManager.getInstance().removeFileManager(local);
		}
		updateSyncLabels();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		action.setEnabled(false);
		if (selection instanceof IStructuredSelection == false)
		{
			return;
		}

		IStructuredSelection structuredSelection = ((IStructuredSelection) selection);
		if (structuredSelection.size() > 1)
		{
			return;
		}

		Object obj = structuredSelection.getFirstElement();

		if (obj instanceof IVirtualFile)
		{
			IVirtualFile file = (IVirtualFile) obj;
			if (file.isDirectory())
			{
				_folder = file;
				action.setEnabled(true);
			}
			else
			{
				_folder = null;
			}
		}
		else if(obj instanceof IVirtualFileManager)
		{
			IVirtualFileManager manager = (IVirtualFileManager) obj;
			if (manager.getBasePath() != null && manager.getBaseFile().isDirectory())
			{
				_folder = manager.getBaseFile();
				action.setEnabled(true);
			}
			else
			{
				_folder = null;
			}
		}
	}

	/**
	 * updateSyncLabels
	 */
	protected void updateSyncLabels()
	{
		IDecoratorManager dm = SyncingPlugin.getDefault().getWorkbench().getDecoratorManager();
		dm.update("com.aptana.ide.syncing.SyncConnectionDecorator"); //$NON-NLS-1$
		dm.update("com.aptana.ide.syncing.VirtualFileManagerSyncDecorator"); //$NON-NLS-1$
	}
}
