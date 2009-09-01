/*******************************************************************************
 * Copyright (c) 2005, 2006 Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bjorn Freeman-Benson - initial implementation
 *     Ward Cunningham - initial implementation
 *******************************************************************************/

package org.eclipse.eclipsemonkey.actions;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.eclipsemonkey.EclipseMonkeyPlugin;
import org.eclipse.eclipsemonkey.dom.Utilities;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * PublishScript
 */
public class PublishScript implements IWorkbenchWindowActionDelegate, IObjectActionDelegate
{

	/**
	 * 
	 */
	public PublishScript()
	{
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		String result = ""; //$NON-NLS-1$

		IStructuredSelection sel = (IStructuredSelection) this.selection;
		List selectedObjects = sel.toList();
		for (Iterator iter = selectedObjects.iterator(); iter.hasNext();)
		{
			IFile element = (IFile) iter.next();

			try
			{
				String contents = Utilities.getFileContents(element.getLocation());

				result += decorateText(contents);
			}
			catch (IOException x)
			{
				MessageDialog.openInformation(shell,
						Messages.PublishScript_INF_TTL_error_copying_script_for_publication, MessageFormat.format(
								Messages.PublishScript_INF_MSG_error_copying_script_for_publication, x.toString()));
			}
			catch (CoreException x)
			{
				MessageDialog.openInformation(shell,
						Messages.PublishScript_INF_TTL_error_copying_script_for_publication, MessageFormat.format(
								Messages.PublishScript_INF_MSG_error_copying_script_for_publication, x.toString()));
			}
		}

		Clipboard clipboard = new Clipboard(shell.getDisplay());
		try
		{
			TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new Object[] { result }, new Transfer[] { textTransfer });
		}
		finally
		{
			clipboard.dispose();
		}
	}

	/**
	 * decorateText
	 * 
	 * @param contents
	 * @return The decorated text
	 */
	protected String decorateText(String contents)
	{
		return EclipseMonkeyPlugin.PUBLISH_BEFORE_MARKER + "\n" + contents + "\n" //$NON-NLS-1$ //$NON-NLS-2$
				+ EclipseMonkeyPlugin.PUBLISH_AFTER_MARKER;
	}

	private ISelection selection;

	/**
	 * @param action
	 * @param selection
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selection = selection;
	}

	/**
	 * 
	 */
	public void dispose()
	{
	}

	/**
	 * @param window
	 */
	public void init(IWorkbenchWindow window)
	{
		shell = window.getShell();
	}

	private Shell shell;

	/**
	 * @param action
	 * @param targetPart
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		shell = targetPart.getSite().getShell();
	}
}