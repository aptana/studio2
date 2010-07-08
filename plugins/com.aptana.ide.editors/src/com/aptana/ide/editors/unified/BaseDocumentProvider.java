/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.editors.unified;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput;

import com.aptana.ide.core.StreamUtils;

/**
 * Overrides the base implementation for saving for saving. Used by UntitledTextFileEditor. non-project-based files
 * 
 * @author Ingo Muschenetz
 */
public class BaseDocumentProvider extends TextFileDocumentProvider
{

	/**
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#createFileInfo(java.lang.Object)
	 */
	protected FileInfo createFileInfo(Object element) throws CoreException
	{
		FileInfo info = super.createFileInfo(element);
		if (info != null)
			return info;
		// for SVN remote history
		if (element instanceof IPathEditorInput)
		{
			IPath path = ((IPathEditorInput) element).getPath();
			try
			{
				ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
				manager.connect(path, LocationKind.NORMALIZE, getProgressMonitor());
				ITextFileBuffer fileBuffer = manager.getTextFileBuffer(path, LocationKind.NORMALIZE);
				fileBuffer.requestSynchronizationContext();

				info = createEmptyFileInfo();
				info.fTextFileBuffer = fileBuffer;
				info.fCachedReadOnlyState = true;
				path.toFile().setReadOnly();
				IFile file = FileBuffers.getWorkspaceFileAtLocation(path);
				if (file != null)
				{
					info.fModel = createAnnotationModel(file);
				}
			}
			catch (Exception e)
			{
				return null;
			}
			return info;
		}
		// For local history
		if (element instanceof IStorageEditorInput)
		{
			IPath path = ((IStorageEditorInput) element).getStorage().getFullPath();
			String segment = path.lastSegment();
			path = path.removeLastSegments(1).append(segment + ".tmp." + System.currentTimeMillis()); //$NON-NLS-1$
			try
			{
				ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
				manager.connect(path, LocationKind.NORMALIZE, getProgressMonitor());
				ITextFileBuffer fileBuffer = manager.getTextFileBuffer(path, LocationKind.NORMALIZE);
				fileBuffer.getDocument().set(
						StreamUtils.readContent(((IStorageEditorInput) element).getStorage().getContents(),
								getEncoding(element)));
				fileBuffer.setDirty(false);

				info = createEmptyFileInfo();
				info.fTextFileBuffer = fileBuffer;
				info.fCachedReadOnlyState = true;
				path.toFile().setReadOnly();
				IFile file = FileBuffers.getWorkspaceFileAtLocation(path);
				if (file != null)
				{
					info.fModel = createAnnotationModel(file);
				}
				return info;
			}
			catch (Exception e)
			{
				return null;
			}
		}
		return null;
	}

	protected String getEncoding(IStorageEditorInput element)
	{
		IStorage storage;
		try
		{
			storage = element.getStorage();
			if (storage instanceof IEncodedStorage)
				return ((IEncodedStorage) storage).getCharset();
		}
		catch (CoreException e)
		{
			// ignore
		}
		return getDefaultEncoding();
	}

	/**
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#canSaveDocument(java.lang.Object)
	 */
	public boolean canSaveDocument(Object element)
	{
		if (element instanceof NonExistingFileEditorInput)
		{
			// force untitled editors to be saveable (without requiring the user to make an edit)
			return true;
		}
		else
		{
			return super.canSaveDocument(element);
		}
	}
}
