/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.js.docgen.actions;

import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import sun.misc.MessageUtils;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.editor.js.JSEditor;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.js.docgen.DocgenPlugin;
import com.aptana.ide.js.docgen.GenerateDocs;
import com.aptana.ide.parsing.IParseState;

/**
 * Generates HTML documentation from the ScriptDoc of the current editor.
 */
public class GenerateHTMLDocsAction implements IEditorActionDelegate
{
	/**
	 * The action has been activated. The argument of the method represents the 'real' action
	 * sitting in the workbench UI.
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		final JSEditor editor = (JSEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		.getActiveEditor();

		Job job = new Job(Messages.GenerateHTMLDocsAction_Job_GenerateDoc) {
		
			protected IStatus run(IProgressMonitor monitor) {

				IParseState pstate = editor.getFileContext().getParseState();
				IEditorInput input = editor.getEditorInput();
				
				final IFile file = (input instanceof IFileEditorInput) ? ((IFileEditorInput) input).getFile() : null;

				if (file == null)
				{
					UIJob job = new UIJob(Messages.GenerateHTMLDocsAction_Job_DisplayError){
						/**
						 * 
						 */
						public IStatus runInUIThread(IProgressMonitor monitor) {
							MessageDialog.openInformation(Display
									.getDefault().getActiveShell(), Messages.GenerateHTMLDocsAction_ErrorTitle,
									Messages.GenerateHTMLDocsAction_ErrorMessage);
							return Status.OK_STATUS;
						}
					};
					job.setSystem(true);
					job.schedule();

					return Status.CANCEL_STATUS;
				}
				
				if(pstate instanceof JSParseState)
				{
					String xml = GenerateDocs.generateXML((JSParseState)pstate, file.getName());
					InputStream schemaStream = DocgenPlugin.class.getResourceAsStream("/com/aptana/ide/js/docgen/resources/docs.xsl"); //$NON-NLS-1$
					IPath location = file.getParent().getLocation();
					location = location.append(FileUtils.stripExtension(file.getName()) + "_docs"); //$NON-NLS-1$
					File f = new File(location.toOSString());
					f.mkdirs();

					String folderPath = location.append("/images/").toOSString(); //$NON-NLS-1$ //$NON-NLS-2$
					GenerateDocs.exportImage(folderPath, "shared.css"); //$NON-NLS-1$
					GenerateDocs.exportImage(folderPath, "doc_background.gif"); //$NON-NLS-1$

					GenerateDocs.generateHTMLFromXML(xml, f.getAbsolutePath() + "/", file.getName(), schemaStream); //$NON-NLS-1$
					//  Does not appear to work in Vista
					//	if(url != null)
					//	{
					//		WorkbenchHelper.launchBrowser(url.toExternalForm());
					//	}
					
					IContainer project = file.getParent();
					try
					{
						project.refreshLocal(1, null);
					}
					catch (CoreException e)
					{
					}

					UIJob job = new UIJob(Messages.GenerateHTMLDocsAction_Job_DocCompleted){
						/**
						 * 
						 */
						public IStatus runInUIThread(IProgressMonitor monitor) {
							MessageDialog.openInformation(Display
									.getDefault().getActiveShell(), Messages.GenerateHTMLDocsAction_DocCompletedTitle,
									MessageFormat
                                                    .format(
                                                            Messages.GenerateHTMLDocsAction_DocCompletedMessage,
                                                            file.getFullPath()
                                                                    .toString()));
							return Status.OK_STATUS;
						}
					};
					job.setSystem(true);
					job.schedule();

				}

				return Status.OK_STATUS;
			}
		
		};
		job.setSystem(false);
		job.schedule();
	}

	/**
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor)
	{
		// No code necessary
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		// No code necessary
	}
}
