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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.update.portal.clients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.Version;

import com.aptana.ide.server.jetty.comet.CometConstants;
import com.aptana.ide.server.jetty.comet.CometResponderClient;
import com.aptana.ide.update.Activator;
import com.aptana.ide.update.manager.IPlugin;
import com.aptana.ide.update.manager.IPluginManager;
import com.aptana.ide.update.manager.Plugin;

/**
 * @author Chris Williams (cwilliams@aptana.com)
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ListPluginsClient extends CometResponderClient
{

	/**
	 * LIST_PLUGINS
	 */
	public static final String LIST_PLUGINS = "/portal/plugins/list"; //$NON-NLS-1$

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (LIST_PLUGINS.equals(toChannel))
		{
			if (getPluginManager() == null)
				return Collections.emptyMap();
			List<Plugin> remotes = getPluginManager().getRemotePlugins();
			List<IPlugin> installed = getPluginManager().getInstalledPlugins();
			Map<String, Object> returnMap = new HashMap<String, Object>();
			List<Map<String, Object>> pluginListing = createPluginListing(remotes, installed);
			for (Map<String, Object> map : pluginListing)
			{
				String category = (String) map.get("category"); //$NON-NLS-1$
				List<Map<String, Object>> listOfCategoryPlugins = (List<Map<String, Object>>) returnMap.get(category);
				if (listOfCategoryPlugins == null)
				{
					listOfCategoryPlugins = new LinkedList<Map<String, Object>>();
					returnMap.put(category, listOfCategoryPlugins);
				}
				listOfCategoryPlugins.add(map);
			}
			returnMap.put(CometConstants.RESPONSE, "listPlugins"); //$NON-NLS-1$
			return returnMap;
		}
		return null;
	}

	protected IPluginManager getPluginManager()
	{
		return Activator.getDefault().getPluginManager();
	}

	private List<Map<String, Object>> createPluginListing(List<Plugin> remotes, List<IPlugin> installed)
	{
		List<Map<String, Object>> pluginListing = new ArrayList<Map<String, Object>>();
		for (Plugin plugin : remotes)
		{
			try
			{
				Map<String, Object> map = createMap(plugin, installed);
				pluginListing.add(map);
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
		}
		Collections.sort(pluginListing, new Comparator<Map<String, Object>>()
		{

			public int compare(Map<String, Object> o1, Map<String, Object> o2)
			{
				Integer value = (Integer) o1.get("sortweight"); //$NON-NLS-1$
				Integer value2 = (Integer) o2.get("sortweight"); //$NON-NLS-1$
				if (value.intValue() == value2.intValue())
				{
					String name = (String) o1.get("name"); //$NON-NLS-1$
					String name2 = (String) o2.get("name"); //$NON-NLS-1$
					return name.compareTo(name2);
				}
				return value.compareTo(value2);
			}
		});
		return pluginListing;
	}

	private Map<String, Object> createMap(Plugin plugin, List<IPlugin> installed) throws CoreException
	{
		Map<String, Object> map = new HashMap<String, Object>();
		String id = plugin.getId();
		map.put("id", id); //$NON-NLS-1$
		map.put("more", plugin.getMore()); //$NON-NLS-1$
		map.put("name", plugin.getName()); //$NON-NLS-1$
		map.put("description", plugin.getDescription()); //$NON-NLS-1$
		map.put("link", plugin.getURL().toString()); //$NON-NLS-1$
		String category = plugin.getCategory();
		if (category == null)
		{
			category = "utilities"; //$NON-NLS-1$
		}
		if (category.equalsIgnoreCase("Platforms")) //$NON-NLS-1$
		{
			// TODO Remove if Ian makes Portal smarter (since it explicitly looks for the exact category name)
			category = "platform"; //$NON-NLS-1$
		}
		map.put("category", category); //$NON-NLS-1$
		int sortweight = plugin.getSortweight();
		if (sortweight == Plugin.UNKNOWN_WEIGHT)
		{
			sortweight = 9; // Based on discussion with Ian
		}
		map.put("sortweight", sortweight); //$NON-NLS-1$
		Boolean isInstalled = isInstalled(id, installed);
		map.put("installed", isInstalled); //$NON-NLS-1$
		if (isInstalled)
		{
			// Now check if the version > what we have installed
			IPlugin installedFeature = getInstalled(id, installed);
			Boolean updateAvailable = new Version(plugin.getVersion()).compareTo(new Version(installedFeature
					.getVersion())) > 0;
			map.put("update", updateAvailable); //$NON-NLS-1$
		}
		else
		{
			map.put("update", Boolean.FALSE); //$NON-NLS-1$
		}
		return map;
	}

	private IPlugin getInstalled(String id, List<IPlugin> installed)
	{
		for (IPlugin ref : installed)
		{
			String refId = ref.getId();
			if (refId.equals(id))
				return ref;
		}
		return null;
	}

	private Boolean isInstalled(String id, List<IPlugin> installed)
	{
		return getInstalled(id, installed) != null;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getSubscriptionIDs()
	 */
	protected String[] getSubscriptionIDs()
	{
		return new String[] { LIST_PLUGINS };
	}

}
