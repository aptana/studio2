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
package com.aptana.ide.editors.untitled;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;

/**
 * The base text editor from which other editors are derived. This class manages the Save As of different file types
 * (external or project files). The save operation is then delegated to the document provider, which is the
 * UnifiedDocumentProvider, which is implemented in BaseDocumentProvider
 * 
 * @author Ingo Muschenetz
 */
public class BaseTextEditor extends TextEditor
{
	private String directoryHint;

	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#performSave(boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void performSave(boolean overwrite, IProgressMonitor progressMonitor)
	{
		// TODO Auto-generated method stub
		super.performSave(overwrite, progressMonitor);

		onSaveComplete();
	}

	/**
	 * Hook to allow editors to deal with save operation being completed.
	 */
	protected void onSaveComplete()
	{
	}

	/**
	 * The <code>TextEditor</code> implementation of this <code>AbstractTextEditor</code> method asks the user for
	 * the workspace path of a file resource and saves the document there.
	 * 
	 * @param progressMonitor
	 *            the progress monitor to be used
	 */
	protected void performSaveAs(IProgressMonitor progressMonitor)
	{
		IEditorInput input = getEditorInput();

		String oldPath = CoreUIUtils.getPathFromEditorInput(input);

		File newFile = null;
		File oldFile = oldPath == null ? null : new File(oldPath);

		IFile file = (input instanceof IFileEditorInput) ? ((IFileEditorInput) input).getFile() : null;

		if (file != null)
		{
			super.performSaveAs(progressMonitor);
			String newPath = CoreUIUtils.getPathFromEditorInput(getEditorInput());
			newFile = new File(newPath);
		}
		else
		{
			// the file being edited is not part of the project, so save the file as an external
			// file.
			newFile = doExternalSaveAs(progressMonitor);
		}

		onSaveAsComplete(oldFile, newFile);

	}

	/**
	 * Hook to allow editors to deal with save as operation being completed.
	 * 
	 * @param oldFile
	 * @param newFile
	 *            The new file being saved
	 */
	protected void onSaveAsComplete(final File oldFile, final File newFile)
	{
	}

	private File doExternalSaveAs(IProgressMonitor progressMonitor)
	{
		Shell shell = getSite().getShell();
		IDocumentProvider provider = getDocumentProvider();
		IEditorInput input = getEditorInput();
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);

		String fileName = getDefaultSaveAsFile();
		fileDialog.setFileName(getBaseFilename(fileName));

		FileDialogFilterInfo filterInfo = getFileDialogFilterInformation(fileName);
		String [] fileExtensions = filterInfo.getFilterExtensions();
		if (fileExtensions != null && fileExtensions.length >0)
		{
			fileDialog.setFilterExtensions(fileExtensions);
			fileDialog.setFilterNames(filterInfo.getFilterNames());
		}

		// [IM] This appears to have no effect on OSX. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=101948
		if (directoryHint != null)
		{
			File f = new File(directoryHint);
			if (f.exists())
			{
				fileDialog.setFilterPath(directoryHint);
			}
		}

		String text = fileDialog.open();
		if (text == null)
		{
			if (progressMonitor != null)
			{
				progressMonitor.setCanceled(true);
			}
			return null;
		}

		File file = new File(text);
		final IEditorInput newInput = CoreUIUtils.createJavaFileEditorInput(file);

		boolean success = false;
		try
		{

			provider.aboutToChange(newInput);
			provider.saveDocument(progressMonitor, newInput, provider.getDocument(input), true);
			success = true;

		}
		catch (CoreException x)
		{
			IStatus status = x.getStatus();
			if (status == null || status.getSeverity() != IStatus.CANCEL)
			{
				String title = Messages.BaseTextEditor_SaveFileError;
				String msg = StringUtils.format(Messages.BaseTextEditor_ErrorSaving, new Object[] { x.getMessage() });

				if (status != null)
				{
					switch (status.getSeverity())
					{
						case IStatus.INFO:
							MessageDialog.openInformation(shell, title, msg);
							break;
						case IStatus.WARNING:
							MessageDialog.openWarning(shell, title, msg);
							break;
						default:
							MessageDialog.openError(shell, title, msg);
					}
				}
				else
				{
					MessageDialog.openError(shell, title, msg);
				}
			}
		}
		finally
		{
			provider.changed(newInput);
			if (success)
			{
				setInput(newInput);
			}
		}

		if (progressMonitor != null)
		{
			progressMonitor.setCanceled(!success);
		}

		if (success)
		{
			return file;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Returns name and extension filters for display in file dialog
	 * @param fileName
	 * @return FileDialogFilterInfo
	 */
	protected FileDialogFilterInfo getFileDialogFilterInformation(String fileName)
	{
		FileDialogFilterInfo filterInfo = null;
		
		String fileExtension = getFileExtension(fileName);
		if(!fileExtension.equals(StringUtils.EMPTY))
		{
			filterInfo = new FileDialogFilterInfo();
			filterInfo.setFilterExtensions(new String[] { "*" + fileExtension, "All Files (*.*)" }); //$NON-NLS-1$ //$NON-NLS-2$
			filterInfo.setFilterNames(new String[] { "*" + fileExtension, Messages.BaseTextEditor_AllFiles }); //$NON-NLS-1$
		}
		else
		{
			filterInfo = new FileDialogFilterInfo();
			filterInfo.setFilterExtensions(new String[] { "*.*" }); //$NON-NLS-1$			
			filterInfo.setFilterNames(new String[] { Messages.BaseTextEditor_AllFiles }); //$NON-NLS-1$			
		}
				
		return filterInfo;
	}

	/**
	 * Returns the default file.
	 * 
	 * @return The path to the newly saved file
	 */
	protected String getDefaultSaveAsFile()
	{
		IEditorInput input = this.getEditorInput();
		if (input instanceof IFileEditorInput)
		{
			IFileEditorInput fileInput = (IFileEditorInput) input;
			return fileInput.getFile().getLocation().toOSString();
		}
		else if (input instanceof IPathEditorInput)
		{
			IPathEditorInput pathInput = (IPathEditorInput) input;
			return pathInput.getPath().toOSString();
		}
		else if (input instanceof IURIEditorInput)
		{
		    IURIEditorInput uriInput = (IURIEditorInput) input;
		    return uriInput.getURI().getPath();
		}
		else if (input instanceof NonExistingFileEditorInput)
		{
			NonExistingFileEditorInput nonExist = (NonExistingFileEditorInput) input;
			IPath path = nonExist.getPath(input);
			String fileName = getTitle();
			String lastSegment = path.lastSegment();
			int extIndex = lastSegment.lastIndexOf("."); //$NON-NLS-1$
			if (extIndex != -1)
			{
				fileName = fileName + lastSegment.substring(extIndex);
			}

			return fileName;
		}
		return null;
	}

	/**
	 * Returns the extension of a file
	 * 
	 * @param fileName
	 * @return String
	 */
	protected String getFileExtension(String fileName)
	{
		String lastSegment = fileName;
		int extIndex = lastSegment.lastIndexOf("."); //$NON-NLS-1$
		if (extIndex != -1)
		{
			return lastSegment.substring(extIndex);
		}
		else
		{
			return StringUtils.EMPTY;
		}
	}

	private static String getBaseFilename(String filepath)
	{
	    int index = filepath.lastIndexOf(File.separator);
	    return (index < 0) ? filepath : filepath.substring(index + 1);
	}

	/**
	 * getParentDirectoryHint
	 * 
	 * @return String
	 */
	public String getParentDirectoryHint()
	{
		return directoryHint;
	}

	/**
	 * setParentDirectoryHint
	 * 
	 * @param hint
	 */
	public void setParentDirectoryHint(String hint)
	{
		directoryHint = hint;
	}

	/**
	 * Holds information used for File Save As dialog box
	 * @author Samir
	 *
	 */
	protected class FileDialogFilterInfo
	{
		private String[] filterNames;
		private String[] filterExtensions;
		
		/**
		 * Default construction is public
		 *
		 */
		public FileDialogFilterInfo()
		{
		}
		
		/**
		 * @return Array of filter extensions
		 */
		public String[] getFilterExtensions()
		{
			return filterExtensions;
		}
		/**
		 * @param filterExtensions
		 */
		public void setFilterExtensions(String[] filterExtensions)
		{
			this.filterExtensions = filterExtensions;
		}
		/**
		 * @return array of filter names;
		 */
		public String[] getFilterNames()
		{
			return filterNames;
		}
		/**
		 * @param filterNames
		 */
		public void setFilterNames(String[] filterNames)
		{
			this.filterNames = filterNames;
		}
			
	}
	
}

