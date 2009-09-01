/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.search.epl.filesystem.ui.text;
import org.eclipse.jface.action.Action;



/**
 * @author Pavel Petrochenko
 *
 */
public class SortAction extends Action
{
	private int fSortOrder;
	private FileSystemSearchPage fPage;

	/**
	 * @param label
	 * @param page
	 * @param sortOrder
	 */
	public SortAction(String label, FileSystemSearchPage page, int sortOrder)
	{
		super(label);
		this.fPage = page;
		this.fSortOrder = sortOrder;
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{
		this.fPage.setSortOrder(this.fSortOrder);
	}

	/**
	 * @return
	 */
	public int getSortOrder()
	{
		return this.fSortOrder;
	}
}
