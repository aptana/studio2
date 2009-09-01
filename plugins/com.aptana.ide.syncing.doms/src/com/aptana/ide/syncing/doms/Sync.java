/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.syncing.doms;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.io.file.LocalFile;
import com.aptana.ide.core.ui.io.file.ProjectFileManager;
import com.aptana.ide.syncing.FileDownloadAction;
import com.aptana.ide.syncing.FileUploadAction;

/**
 * @author Ingo Muschenetz
 */
public final class Sync
{
	/**
	 * uploadCurrentEditor
	 */
	public static void uploadCurrentEditor()
	{
		IEditorPart editor = CoreUIUtils.getActiveEditor();
		if(editor == null)
		{
			MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.Sync_TTL_UnableToUpload, Messages.Sync_ERR_YouMustHaveACurrentlyOpenEditorToUpload);
			return;
		}
		
		IEditorInput input = editor.getEditorInput();

		if (input instanceof FileEditorInput)
		{
			IFile file = ((FileEditorInput) input).getFile();
			upload(file);
		}
		else if (input instanceof IPathEditorInput)
		{
			IPathEditorInput pin = (IPathEditorInput) input;
			upload(pin);
		}
		
	}
	
	/**
	 * upload
	 * @param file
	 */
	public static void upload(IFile file)
	{
		FileUploadAction action = new FileUploadAction();
		IVirtualFile[] selectedFiles = null;

		Object[] selectedObjects = new Object[] { file };
		IVirtualFile[] convertedResources = ProjectFileManager.convertResourcesToFiles(selectedObjects);
		selectedFiles = action.extractIVirtualFilesFromSelection(convertedResources);

		if(selectedFiles != null && selectedFiles.length > 0)
		{
			action.setSelectedObjects(selectedObjects);
			action.setSelectedFiles(selectedFiles);
			action.run(null);
		}
	}
	
	/**
	 * upload
	 * @param file
	 */
	public static void upload(IPathEditorInput file)
	{
		FileUploadAction action = new FileUploadAction();		
		IVirtualFile vFile = new LocalFile(null, new File(file.getPath().toOSString()));
		
		IVirtualFile[] convertedResources = new IVirtualFile[]{vFile};

		if(convertedResources != null && convertedResources.length > 0)
		{
			action.setSelectedObjects(new Object[] { file.getPath() });
			action.setSelectedFiles(convertedResources);
			action.run(null);
		}
	}
	
	/**
	 * downloadCurrentEditor
	 */
	public static void downloadCurrentEditor()
	{
		IEditorPart editor = CoreUIUtils.getActiveEditor();
		if(editor == null)
		{
			MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.Sync_TTL_UnableToDownload, Messages.Sync_ERR_YouMustHaveACurrentlyOpenEditorToDownload);
			return;
		}
		IEditorInput input = editor.getEditorInput();

		if (input instanceof FileEditorInput)
		{
			IFile file = ((FileEditorInput) input).getFile();
			download(file);
		}
		else if (input instanceof IPathEditorInput)
		{
			IPathEditorInput pin = (IPathEditorInput) input;
			download(pin);
		}
		
	}

	/**
	 * download
	 * @param file
	 */
	public static void download(IFile file)
	{
		FileDownloadAction action = new FileDownloadAction();
		IVirtualFile[] selectedFiles = null;

		Object[] selectedObjects = new Object[] { file };
		IVirtualFile[] convertedResources = ProjectFileManager.convertResourcesToFiles(selectedObjects);
		selectedFiles = action.extractIVirtualFilesFromSelection(convertedResources);

		if(selectedFiles != null && selectedFiles.length > 0)
		{
			action.setSelectedObjects(selectedObjects);
			action.setSelectedFiles(selectedFiles);
			action.run(null);
		}
	}
	
	/**
	 * download
	 * @param file
	 */
	public static void download(IPathEditorInput file)
	{
		FileDownloadAction action = new FileDownloadAction();		
		IVirtualFile vFile = new LocalFile(null, new File(file.getPath().toOSString()));
		
		IVirtualFile[] convertedResources = new IVirtualFile[]{vFile};

		if(convertedResources != null && convertedResources.length > 0)
		{
			action.setSelectedObjects(new Object[] { file.getPath() });
			action.setSelectedFiles(convertedResources);
			action.run(null);
		}
	}

}
