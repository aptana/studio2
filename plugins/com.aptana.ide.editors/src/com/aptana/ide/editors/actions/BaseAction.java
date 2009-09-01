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
package com.aptana.ide.editors.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.editors.unified.IUnifiedEditor;

/**
 * BaseAction
 * @author Ingo Muschenetz
 *
 */
public abstract class BaseAction extends Action
{
	
	/**
	 * getActiveEditor
	 * 
	 * @return IEditorPart
	 */
	public IEditorPart getActiveEditor()
	{
		/**
		 * ActiveEditorRef
		 * @author Ingo Muschenetz
		 *
		 */
		class ActiveEditorRef
		{
			public IEditorPart activeEditor;
		}

		final IWorkbench workbench = PlatformUI.getWorkbench();
		final ActiveEditorRef activeEditor = new ActiveEditorRef();
		Display display = workbench.getDisplay();
		IEditorPart result;

		display.syncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				
				// this can be null if you close all perspectives
				if(window.getActivePage() != null)
				{
					activeEditor.activeEditor = window.getActivePage().getActiveEditor();
				}
			}
		});

		if (activeEditor.activeEditor instanceof IUnifiedEditor)
		{
			IUnifiedEditor editor = (IUnifiedEditor) activeEditor.activeEditor;

			result = editor.getEditor();
		}
		else
		{
			result = activeEditor.activeEditor;
		}

		return result;
	}
}
