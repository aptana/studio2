/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL youelect, is prohibited.
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
package com.aptana.ide.editor.yml;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.resources.IUniformResource;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.BaseDocumentProvider;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.UniformResourceMarkerAnnotationModel;

/**
 * Allows us to add annotations to non-project-based files
 * 
 * @author Ingo Muschenetz
 */
public final class YMLDocumentProvider extends BaseDocumentProvider
{
	/**
	 * Bundle of all required informations to allow working copy management.
	 */
	protected static class YMLFileInfo extends FileInfo
	{
		/**
		 * sourceProvider
		 */
		public IFileSourceProvider sourceProvider;
	}

	private static YMLDocumentProvider _unifiedDocumentProvider = null;

	/**
	 * getInstance
	 * 
	 * @return UnifiedDocumentProvider
	 */
	public static YMLDocumentProvider getInstance()
	{
		if (_unifiedDocumentProvider == null)
		{
			_unifiedDocumentProvider = new YMLDocumentProvider();
		}

		return _unifiedDocumentProvider;
	}

	private YMLDocumentProvider()
	{
	}

	/**
	 * getFileInfoPublic
	 * 
	 * @param element
	 * @return FileInfo
	 */
	public FileInfo getFileInfoPublic(Object element)
	{
		return getFileInfo(element);
	}

	/**
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#createEmptyFileInfo()
	 */
	protected FileInfo createEmptyFileInfo()
	{
		return new YMLFileInfo();
	}

	/**
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#getAnnotationModel(java.lang.Object)
	 */
	public IAnnotationModel getAnnotationModel(Object element)
	{
		IAnnotationModel annotationModel = super.getAnnotationModel(element);
		if (annotationModel == null)
		{
			FileInfo fileInfo = getFileInfo(element);
			if (fileInfo != null)
			{
				annotationModel = fileInfo.fModel;
			}
		}
		return annotationModel;
	}

	/**
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#createFileInfo(java.lang.Object)
	 */
	protected FileInfo createFileInfo(Object element) throws CoreException
	{
		if (!(element instanceof IEditorInput))
		{
			return null;
		}

		try
		{
			FileInfo info = super.createFileInfo(element);

			if (!(info instanceof YMLFileInfo))
			{
				return null;
			}

			YMLFileInfo cuInfo = (YMLFileInfo) info;
			if (element instanceof IAdaptable)
			{
				// If this is check is not added, workspace files (those in projects) lose their annotations (like
				// debugger markers)
				cuInfo.fModel = cuInfo.fTextFileBuffer.getAnnotationModel();
				if (cuInfo.fModel == null || cuInfo.fModel.getClass().equals(AnnotationModel.class))
				{
					IUniformResource uniformResource = (IUniformResource) ((IAdaptable) element)
							.getAdapter(IUniformResource.class);
					if (uniformResource != null)
					{
						cuInfo.fModel = new UniformResourceMarkerAnnotationModel(uniformResource);
					}
					else if (cuInfo.fModel == null)
					{
						cuInfo.fModel = new AnnotationModel();
					}
				}
			}

			return info;
		}
		catch (RuntimeException ex)
		{
			// TODO:
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.YMLDocumentProvider_CantCreateFileInfo, ex);
		}
		return null;
	}

	/**
	 * disconnects the doc provider.
	 * 
	 * @param element
	 */
	public void disconnect(Object element)
	{
		YMLFileInfo cuInfo = (YMLFileInfo) getFileInfo(element);

		if (cuInfo != null && cuInfo.fCount == 1)
		{
			String uri = cuInfo.sourceProvider.getSourceURI();

			if (uri != null)
			{
				FileContextManager.disconnectSourceProvider(uri, cuInfo.sourceProvider);
			}
		}

		// This has to be after the call to getFileInfo() because otherwise what we're
		// looking for will be gone.
		try
		{
			super.disconnect(element);
		}
		catch (RuntimeException ex)
		{
			// TODO:
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
					Messages.YMLDocumentProvider_ErrorDisconnectingDocumentProvider, ex);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#connect(java.lang.Object)
	 */
	public void connect(Object element) throws CoreException
	{
		// TODO Auto-generated method stub
		super.connect(element);
	}

	/*
	 * public IUnifiedEditorContributor getBaseContributor() { return baseContributor; }
	 */
}
