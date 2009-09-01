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
package com.aptana.ide.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.InstanceCreator;
import com.aptana.ide.views.outline.UnifiedViewerFilter;

/**
 * A registry class for the viewerFilters extension point.
 * 
 * @author Shalom Gibly
 * @since Aptana Studio 1.2.6
 */
public class ViewerFilterContributorsProvider
{
	private static final String TAG_FILTER = "filter"; //$NON-NLS-1$
	private static final String ATTR_CONTRIBUTING_TO_ID = "contributingToID"; //$NON-NLS-1$
	private static final String VIEWER_FILTERS_EXTENSION_NAME = "viewerFilters"; //$NON-NLS-1$
	private static final String ATTR_LANGUAGE = "language"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static ViewerFilterContributorsProvider instance;

	private HashMap<String, Map<String, List<InstanceCreator>>> _viewerFilterContributors;

	// Private constructor
	private ViewerFilterContributorsProvider()
	{
		loadFilters();
	}

	/**
	 * Returns a singleton instance of the ViewerFilterContributorProvider.
	 * 
	 * @return a ViewerFilterContributorProvider instance
	 */
	public static ViewerFilterContributorsProvider getInstance()
	{
		if (instance == null)
		{
			instance = new ViewerFilterContributorsProvider();
		}
		return instance;
	}

	/**
	 * Return a map for a creation of IViewerFilterContributors via InstanceCreators. The map will contain mapping
	 * between the required language and its assigned filters. The contributedID filed allows a better screening for the
	 * map that will be returned. Only contributions that were assigned to the given id via the extension point will be
	 * added. The created ViewerFilters contributions can be added to a viewer that wish to filter its items.
	 * 
	 * @param contributedID
	 *            the ID to screen the returned item by.
	 * @return A map from a language type to an ViewerFilter instance creator that was set for the given contributed
	 *         item id.
	 * @see UnifiedViewerFilter
	 */
	public Map<String, List<InstanceCreator>> getViewerFiltersContributors(String contributedID)
	{
		Map<String, List<InstanceCreator>> map = _viewerFilterContributors.get(contributedID);
		if (map == null)
		{
			return new HashMap<String, List<InstanceCreator>>(0);
		}
		return map;
	}

	/*
	 * Load the filters from the extension.
	 */
	private void loadFilters()
	{
		this._viewerFilterContributors = new HashMap<String, Map<String, List<InstanceCreator>>>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(UnifiedEditorsPlugin.ID,
					VIEWER_FILTERS_EXTENSION_NAME);
			IExtension[] extensions = extensionPoint.getExtensions();

			for (int i = 0; i < extensions.length; i++)
			{
				IExtension extension = extensions[i];
				IConfigurationElement[] elements = extension.getConfigurationElements();
				loadFilters(elements);
			}
		}
	}

	/*
	 * load the filters from the given elements.
	 * @param elements a IConfigurationElement array
	 */
	private void loadFilters(IConfigurationElement[] elements)
	{
		for (int i = 0; i < elements.length; i++)
		{
			IConfigurationElement element = elements[i];

			if (element.getName().equals(TAG_FILTER))
			{
				String language = element.getAttribute(ATTR_LANGUAGE);
				String contributingToId = element.getAttribute(ATTR_CONTRIBUTING_TO_ID);
				InstanceCreator filterCreator = new InstanceCreator(element, ATTR_CLASS);
				Map<String, List<InstanceCreator>> map = _viewerFilterContributors.get(contributingToId);
				if (map == null)
				{
					map = new HashMap<String, List<InstanceCreator>>();
					_viewerFilterContributors.put(contributingToId, map);
					List<InstanceCreator> creatorList = new ArrayList<InstanceCreator>(1);
					creatorList.add(filterCreator);
					map.put(language, creatorList);
				}
				else
				{
					List<InstanceCreator> list = map.get(language);
					list.add(filterCreator);
				}
			}
		}
	}
}
