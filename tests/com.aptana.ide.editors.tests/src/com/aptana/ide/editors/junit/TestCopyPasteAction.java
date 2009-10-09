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
package com.aptana.ide.editors.junit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.editor.html.HTMLEditor;
import com.aptana.ide.editors.junit.ProjectTestUtils;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class TestCopyPasteAction extends Action implements IWorkbenchWindowActionDelegate
{

	public void dispose()
	{
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow window)
	{
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action)
	{
		try
		{
			IProject project = ProjectTestUtils.createProject("test copy paste");
			Path path = ProjectTestUtils.findFileInPlugin("com.aptana.ide.editors.junit", //$NON-NLS-1$
					"copyPaste/yahooui_sample.htm");
			IFile file = ProjectTestUtils.addFileToProject(path, project);
			HTMLEditor editor = (HTMLEditor) ProjectTestUtils.openInEditor(file, ProjectTestUtils.HTML_EDITOR_ID);
			final StyledText text = editor.getSourceEditor().getViewer().getTextWidget();
			final IDocument document = editor.getSourceEditor().getViewer().getDocument();
			final Clipboard cb = new Clipboard(Display.getDefault());
			UIJob cpJob = new UIJob("Copy Pasting")
			{

				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					try
					{
						int copyPosition = (int) (Math.random() * document.getLength());
						int pastePosition = (int) (Math.random() * document.getLength());
						int characters = (int) (Math.random() * document.getLength());
						if (copyPosition + characters < document.getLength())
						{
							String copyText;
							copyText = document.get(copyPosition, characters);
							cb.setContents(new Object[] { copyText }, new Transfer[] { TextTransfer.getInstance() });
							text.setSelection(copyPosition, characters);
							text.cut();
							text.setCaretOffset(pastePosition);
							text.paste();
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
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		// TODO Auto-generated method stub
		
	}

}
