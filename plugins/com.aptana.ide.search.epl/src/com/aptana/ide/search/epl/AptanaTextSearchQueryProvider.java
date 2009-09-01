/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.search.epl;

import org.eclipse.core.resources.IResource;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.TextSearchQueryProvider;
import org.eclipse.ui.IWorkingSet;

import com.aptana.ide.search.epl.AptanaTextSearchPage.TextSearchPageInput;

/**
 * @author Pavel Petrochenko
 */
public class AptanaTextSearchQueryProvider extends TextSearchQueryProvider
{

    public static final String ID = "com.aptana.ide.search.AptanaTextSearchQueryProvider"; //$NON-NLS-1$

	/**
	 * @see org.eclipse.search.ui.text.TextSearchQueryProvider#createQuery(org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput)
	 */
	public ISearchQuery createQuery(TextSearchInput input)
	{
		AptanaTextSearchPage.TextSearchPageInput ainput = (TextSearchPageInput) input;
		FileTextSearchScope scope = input.getScope();
		String text = input.getSearchText();
		boolean regEx = input.isRegExSearch();
		boolean caseSensitive = input.isCaseSensitiveSearch();
		FileSearchQuery fileSearchQuery = new FileSearchQuery(text, regEx, caseSensitive, scope, ainput.isOpenFiles(),
				ainput.isDirectory() ? ainput.getDirectory() : null,ainput.isIgnoreLineEndings());
		fileSearchQuery.setRefresh(ainput.isRefresh());
		return fileSearchQuery;
	}

	/**
	 * @see org.eclipse.search.ui.text.TextSearchQueryProvider#createQuery(java.lang.String)
	 */
	public ISearchQuery createQuery(String searchForString)
	{
		FileTextSearchScope scope = FileTextSearchScope.newWorkspaceScope(this.getPreviousFileNamePatterns(), false);
		return new FileSearchQuery(searchForString, false, true, scope, false, null,false);
	}

	/**
	 * @see org.eclipse.search.ui.text.TextSearchQueryProvider#createQuery(java.lang.String,
	 *      org.eclipse.core.resources.IResource[])
	 */

	public ISearchQuery createQuery(String selectedText, IResource[] resources)
	{
		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(resources, this.getPreviousFileNamePatterns(),
				false);
		return new FileSearchQuery(selectedText, false, true, scope, false, null,false);
	}

	/**
	 * @see org.eclipse.search.ui.text.TextSearchQueryProvider#createQuery(java.lang.String,
	 *      org.eclipse.ui.IWorkingSet[])
	 */
	public ISearchQuery createQuery(String selectedText, IWorkingSet[] ws)
	{
		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(ws, this.getPreviousFileNamePatterns(), false);
		return new FileSearchQuery(selectedText, false, true, scope, false, null,false);
	}

	private String[] getPreviousFileNamePatterns()
	{
		return new String[] { "*" }; //$NON-NLS-1$
	}

}