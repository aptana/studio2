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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IDecoratorManager;

import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.VirtualManagerBase;
import com.aptana.ide.core.ui.actions.ActionDelegate;
import com.aptana.ide.core.ui.io.file.LocalFileManager;

/**
 * Uploads an item
 * 
 * @author Ingo Muschenetz
 */
public class FileTypeCloakAction extends ActionDelegate
{

	private List<IVirtualFile> selectedFiles;

	public FileTypeCloakAction()
	{
		selectedFiles = new ArrayList<IVirtualFile>();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(IAction)
	 */
	public void run(IAction action)
	{
		IVirtualFile element;
		String expression;
		for (Iterator<IVirtualFile> iter = selectedFiles.iterator(); iter.hasNext();)
		{
			element = iter.next();
			expression = VirtualManagerBase.getFileTypeCloakExpression(element);
			LocalFileManager.addGlobalSyncCloakExpression(expression);
		}

		IDecoratorManager dm = SyncingPlugin.getDefault().getWorkbench().getDecoratorManager();
		dm.update("com.aptana.ide.syncing.VirtualFileCloakedDecorator"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		action.setEnabled(false);

		setSelectedFiles(getValidSelection(selection));

		action.setEnabled(getNumFiles() > 0);
	}

	/**
	 * @param objects
	 */
	protected void setSelectedFiles(Object[] objects)
	{
		selectedFiles.clear();

		Object object;
		for (int i = 0; i < objects.length; i++)
		{
			object = objects[i];
			if (!(object instanceof IVirtualFile))
			{
				return;
			}
			IVirtualFile f = (IVirtualFile) object;
			if (f.isCloaked())
			{
				return;
			}
			selectedFiles.add(f);
		}
	}

	/**
	 * @return the number of selected files
	 */
	protected int getNumFiles()
	{
		return selectedFiles.size();
	}

}
