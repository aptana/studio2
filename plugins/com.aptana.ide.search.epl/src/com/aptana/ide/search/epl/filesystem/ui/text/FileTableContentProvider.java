/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.search.epl.filesystem.ui.text;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.internal.ui.text.IFileSearchContentProvider;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

import com.aptana.ide.search.epl.FileSystemSearchResult;

/**
 * @author Pavel Petrochenko
 *
 */
public class FileTableContentProvider implements IStructuredContentProvider, IFileSearchContentProvider
{

	private final Object[] EMPTY_ARR = new Object[0];

	private FileSystemSearchPage fPage;
	private AbstractTextSearchResult fResult;

	/**
	 * @param page
	 */
	public FileTableContentProvider(FileSystemSearchPage page)
	{
		this.fPage = page;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
		// nothing to do
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof FileSystemSearchResult)
		{
			int elementLimit = this.getElementLimit();
			Object[] elements = ((FileSystemSearchResult) inputElement).getElements();
			if ((elementLimit != -1) && (elements.length > elementLimit))
			{
				Object[] shownElements = new Object[elementLimit];
				System.arraycopy(elements, 0, shownElements, 0, elementLimit);
				return shownElements;
			}
			return elements;
		}
		return this.EMPTY_ARR;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (newInput instanceof FileSystemSearchResult)
		{
			this.fResult = (FileSystemSearchResult) newInput;
		}

	}

	/**
	 * @see com.aptana.ide.search.epl.filesystem.ui.text.IFileSearchContentProvider#elementsChanged(java.lang.Object[])
	 */
	public void elementsChanged(Object[] updatedElements)
	{
		TableViewer viewer = this.getViewer();

		int elementLimit = this.getElementLimit();
		boolean tableLimited = elementLimit != -1;
		for (int i = 0; i < updatedElements.length; i++)
		{
			if (this.fResult.getMatchCount(updatedElements[i]) > 0)
			{
				if (viewer.testFindItem(updatedElements[i]) != null)
				{
					viewer.update(updatedElements[i], null);
				}
				else
				{
					if (!tableLimited || (viewer.getTable().getItemCount() < elementLimit))
					{
						viewer.add(updatedElements[i]);
					}
				}
			}
			else
			{
				viewer.remove(updatedElements[i]);
			}
		}
	}

	private int getElementLimit()
	{
		Integer elementLimit1 = this.fPage.getElementLimit1();
		if (elementLimit1==null){
			return -1;
		}
		return elementLimit1.intValue();
	}

	private TableViewer getViewer()
	{
		return (TableViewer) this.fPage.getViewer();
	}

	/**
	 * @see com.aptana.ide.search.epl.filesystem.ui.text.IFileSearchContentProvider#clear()
	 */
	public void clear()
	{
		this.getViewer().refresh();
	}
}
