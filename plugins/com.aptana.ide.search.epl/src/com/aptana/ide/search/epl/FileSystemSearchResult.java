/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.search.epl;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.internal.ui.SearchPluginImages;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorPart;

/**
 * @author Pavel Petrochenko
 */
public class FileSystemSearchResult extends AbstractTextSearchResult implements IEditorMatchAdapter
{
	private final Match[] EMPTY_ARR = new Match[0];

	private FileSearchQuery fQuery;

	/**
	 * @param job
	 */
	public FileSystemSearchResult(FileSearchQuery job)
	{
		this.fQuery = job;
	}

	/**
	 * @see org.eclipse.search.ui.ISearchResult#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor()
	{
		return SearchPluginImages.DESC_OBJ_TSEARCH_DPDN;
	}

	/**
	 * @see org.eclipse.search.ui.ISearchResult#getLabel()
	 */
	public String getLabel()
	{
		return this.fQuery.getResultLabel(this.getMatchCount());
	}

	/**
	 * @see org.eclipse.search.ui.ISearchResult#getTooltip()
	 */
	public String getTooltip()
	{
		return this.getLabel();
	}

	/**
	 * @param result
	 * @param file
	 * @return
	 */
	public Match[] computeContainedMatches(AbstractTextSearchResult result, File file)
	{
		return this.getMatches(file);
	}

	/**
	 * @param element
	 * @return
	 */
	public File getFile(Object element)
	{
		if (element instanceof File)
		{
			return (File) element;
		}
		return null;
	}

	/**
	 * @see org.eclipse.search.ui.text.IEditorMatchAdapter#isShownInEditor(org.eclipse.search.ui.text.Match,
	 *      org.eclipse.ui.IEditorPart)
	 */
	public boolean isShownInEditor(Match match, IEditorPart editor)
	{
		//IEditorInput ei = editor.getEditorInput();
		return false;
	}

	/**
	 * @see org.eclipse.search.ui.text.IEditorMatchAdapter#computeContainedMatches(org.eclipse.search.ui.text.AbstractTextSearchResult,
	 *      org.eclipse.ui.IEditorPart)
	 */
	public Match[] computeContainedMatches(AbstractTextSearchResult result, IEditorPart editor)
	{
		//IEditorInput ei = editor.getEditorInput();
		return this.EMPTY_ARR;
	}

	/**
	 * @see org.eclipse.search.ui.ISearchResult#getQuery()
	 */
	public ISearchQuery getQuery()
	{
		return this.fQuery;
	}

	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getEditorMatchAdapter()
	 */
	public IEditorMatchAdapter getEditorMatchAdapter()
	{
		return this;
	}

	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getFileMatchAdapter()
	 */
	public IFileMatchAdapter getFileMatchAdapter()
	{
		return new IFileMatchAdapter()
		{

			public Match[] computeContainedMatches(AbstractTextSearchResult result, IFile file)
			{
				return new Match[0];

			}

			public IFile getFile(Object element)
			{
				return null;
			}

		};
	}
}
