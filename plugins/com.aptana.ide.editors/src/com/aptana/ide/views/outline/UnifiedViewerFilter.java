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
package com.aptana.ide.views.outline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.aptana.ide.editors.unified.InstanceCreator;
import com.aptana.ide.views.IViewerFilterContributor;
import com.aptana.ide.views.ViewerFilterContributorsProvider;

/**
 * @author Kevin Lindsey
 */
public class UnifiedViewerFilter extends ViewerFilter
{
	static final String ID = "com.aptana.ide.views.outline.UnifiedViewerFilter"; //$NON-NLS-1$
	private IUnifiedOutlinePage _page;
	private HashMap<String, List<IViewerFilterContributor>> languageToFiltersMap;

	/**
	 * JSViewerFilter
	 * 
	 * @param page
	 */
	public UnifiedViewerFilter(IUnifiedOutlinePage page)
	{
		this._page = page;
		loadContributingFilters();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		boolean result = true;

		if (this._page.hidePrivateMembers())
		{
			if (element instanceof OutlineItem)
			{
				OutlineItem item = (OutlineItem) element;
				String prefix = UnifiedOutlineProvider.getInstance().getPrivateMemberPrefix(item.getLanguage());

				if (prefix != null && prefix.length() > 0 && item.getLabel().startsWith(prefix))
				{
					result = false;
				}
				else
				{
					// Traverse the contributed filter to discover any other filter that decide
					// to filter this element
					List<IViewerFilterContributor> filters = languageToFiltersMap.get(item.getLanguage());
					if (filters != null)
					{
						for (IViewerFilterContributor filter : filters)
						{
							result = filter.select(viewer, parentElement, element, this._page);
							if (!result)
							{
								break;
							}
						}
					}
				}
			}
		}
		return result;
	}

	/*
	 * load the ViewerFilter that were set for this filter ID. This method loads all the filters into a map between a
	 * language and a list of filters.
	 */
	private void loadContributingFilters()
	{
		Map<String, List<InstanceCreator>> viewerFiltersContributors = ViewerFilterContributorsProvider.getInstance()
				.getViewerFiltersContributors(ID);
		languageToFiltersMap = new HashMap<String, List<IViewerFilterContributor>>();
		Set<String> languages = viewerFiltersContributors.keySet();
		for (String language : languages)
		{
			List<InstanceCreator> creators = viewerFiltersContributors.get(language);
			if (creators == null)
			{
				continue; // should not happen
			}
			for (InstanceCreator creator : creators)
			{
				IViewerFilterContributor filter = (IViewerFilterContributor) creator.createInstance();
				if (filter != null)
				{
					List<IViewerFilterContributor> filters = languageToFiltersMap.get(language);
					if (filters == null)
					{
						filters = new ArrayList<IViewerFilterContributor>(1);
						languageToFiltersMap.put(language, filters);
					}
					filters.add(filter);
				}
			}
		}
	}
}
