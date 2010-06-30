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
 */package com.aptana.ide.editors.junit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.editor.js.JSEditor;
import com.aptana.ide.editors.unified.actions.CodeFormatAction;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class TestSelectionFormattingAction extends Action implements IWorkbenchWindowActionDelegate
{

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		try
		{
			IProject project = ProjectTestUtils.createProject("test format");
			Path path = ProjectTestUtils.findFileInPlugin("com.aptana.ide.editors.junit", //$NON-NLS-1$
					"format/dojo.js");
			IFile file = ProjectTestUtils.addFileToProject(path, project);
			final JSEditor editor = (JSEditor) ProjectTestUtils.openInEditor(file, ProjectTestUtils.JS_EDITOR_ID);
			final StyledText text = editor.getViewer().getTextWidget();
			final IDocument document = editor.getViewer().getDocument();

			UIJob cpJob = new UIJob("Copy Pasting")
			{

				int currentIndex = 0;
				int currentLenght = 1;
				int errors = 0;

				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					final String original = document.get();

					CodeFormatAction action = new CodeFormatAction();
					action.setActiveEditor(null, editor);
					try
					{
						String finalString = null;
						if (currentIndex < document.getLength())
						{
							text.setSelection(currentIndex, currentLenght);
							action.run();
							finalString = text.getText();
							if (!finalString.equals(original))
							{
								document.set(original);
								System.out.println("Selection Formatting Error:" + errors + " Position:" + currentIndex
										+ " Length:" + currentLenght);
								errors++;
							}
							if (currentLenght < document.getLength())
							{
								currentLenght++;
							}
							else
							{
								currentIndex++;
								currentLenght = 1;
							}
						}
					}
					catch (Exception e)
					{
					}
					this.schedule(100);
					return Status.OK_STATUS;
				}

			};
			cpJob.schedule();

		}
		catch (Exception e)
		{
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error formatting", e.getMessage());
		}
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		// TODO Auto-generated method stub

	}

}
