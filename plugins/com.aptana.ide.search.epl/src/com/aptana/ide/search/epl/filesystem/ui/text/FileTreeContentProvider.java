/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Juerg Billeter, juergbi@ethz.ch - 47136 Search view should show match objects
 *     Ulrich Etter, etteru@ethz.ch - 47136 Search view should show match objects
 *     Roman Fuchs, fuchsro@ethz.ch - 47136 Search view should show match objects
 *******************************************************************************/
package com.aptana.ide.search.epl.filesystem.ui.text;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.internal.ui.text.IFileSearchContentProvider;

import com.aptana.ide.search.epl.FileSearchQuery;
import com.aptana.ide.search.epl.FileSystemSearchResult;

/**
 * @author Pavel Petrochenko
 */
public class FileTreeContentProvider implements ITreeContentProvider, IFileSearchContentProvider
{

	private final Object[] EMPTY_ARR = new Object[0];

	private FileSystemSearchResult fResult;
	private FileSystemSearchPage fPage;
	private AbstractTreeViewer fTreeViewer;
	private Map fChildrenMap;

	FileTreeContentProvider(FileSystemSearchPage page, AbstractTreeViewer viewer)
	{
		this.fPage = page;
		this.fTreeViewer = viewer;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		Object[] children = this.getChildren(inputElement);
		int elementLimit = this.getElementLimit();
		if ((elementLimit != -1) && (elementLimit < children.length))
		{
			Object[] limitedChildren = new Object[elementLimit];
			System.arraycopy(children, 0, limitedChildren, 0, elementLimit);
			return limitedChildren;
		}
		return children;
	}

	private int getElementLimit()
	{
		Integer elementLimit = this.fPage.getElementLimit1();
		if (elementLimit!=null){
		return elementLimit.intValue();
		}
		return -1;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
		// nothing to do
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (newInput instanceof FileSystemSearchResult)
		{
			this.initialize((FileSystemSearchResult) newInput);
		}
	}

	private synchronized void initialize(FileSystemSearchResult result)
	{
		this.fResult = result;
		this.fChildrenMap = new HashMap();
		if (result != null)
		{
			Object[] elements = result.getElements();
			for (int i = 0; i < elements.length; i++)
			{
				//{
					this.insert(elements[i], false);
				//}
			}
		}
	}

	private void insert(Object child, boolean refreshViewer)
	{
		Object parent = this.getParent(child);
		while (parent != null)
		{
			if (this.insertChild(parent, child))
			{
				if (refreshViewer)
				{
					this.fTreeViewer.add(parent, child);
				}
			}
			else
			{
				if (refreshViewer)
				{
					this.fTreeViewer.refresh(parent);
				}
				return;
			}
			child = parent;
			parent = this.getParent(child);
		}
		if (this.insertChild(this.fResult, child))
		{
			if (refreshViewer)
			{
				this.fTreeViewer.add(this.fResult, child);
			}
		}
	}

	/**
	 * returns true if the child already was a child of parent.
	 * 
	 * @param parent
	 * @param child
	 * @return Returns <code>trye</code> if the child was added
	 */
	private boolean insertChild(Object parent, Object child)
	{
		Set children = (Set) this.fChildrenMap.get(parent);
		if (children == null)
		{
			children = new HashSet();
			this.fChildrenMap.put(parent, children);
		}
		return children.add(child);
	}

//	private boolean hasChild(Object parent, Object child)
//	{
//		Set children = (Set) this.fChildrenMap.get(parent);
//		return (children != null) && children.contains(child);
//	}

	private void remove(Object element, boolean refreshViewer)
	{
		// precondition here: fResult.getMatchCount(child) <= 0

		if (this.hasChildren(element))
		{
			if (refreshViewer)
			{
				this.fTreeViewer.refresh(element);
			}
		}
		else
		{
			if (!this.hasMatches(element))
			{
				this.fChildrenMap.remove(element);
				Object parent = this.getParent(element);
				if (parent != null)
				{
					this.removeFromSiblings(element, parent);
					this.remove(parent, refreshViewer);
				}
				else
				{
					this.removeFromSiblings(element, this.fResult);
					if (refreshViewer)
					{
						this.fTreeViewer.refresh();
					}
				}
			}
			else
			{
				if (refreshViewer)
				{
					this.fTreeViewer.refresh(element);
				}
			}
		}
	}

	private boolean hasMatches(Object element)
	{
		return this.fResult.getMatchCount(element) > 0;
	}

	private void removeFromSiblings(Object element, Object parent)
	{
		Set siblings = (Set) this.fChildrenMap.get(parent);
		if (siblings != null)
		{
			siblings.remove(element);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		Set children = (Set) this.fChildrenMap.get(parentElement);
		if (children == null)
		{
			return this.EMPTY_ARR;
		}
		return children.toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		return this.getChildren(element).length > 0;
	}

	
	/**
	 * @see com.aptana.ide.search.epl.filesystem.ui.text.IFileSearchContentProvider#elementsChanged(java.lang.Object[])
	 */
	public synchronized void elementsChanged(Object[] updatedElements)
	{
		for (int i = 0; i < updatedElements.length; i++)
		{
			if (this.fResult.getMatchCount(updatedElements[i]) > 0)
			{
				this.insert(updatedElements[i], true);
			}
			else
			{
				this.remove(updatedElements[i], true);
			}
		}
	}

	/**
	 * @see com.aptana.ide.search.epl.filesystem.ui.text.IFileSearchContentProvider#clear()
	 */
	public void clear()
	{
		this.initialize(this.fResult);
		this.fTreeViewer.refresh();
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		if (element instanceof File)
		{
			File file = ((File) element);
			if (file.getAbsolutePath().equals(((FileSearchQuery) this.fResult.getQuery()).getDirectory()))
			{
				return null;
			}
			return file.getParentFile();
		}
		return null;
	}
}
