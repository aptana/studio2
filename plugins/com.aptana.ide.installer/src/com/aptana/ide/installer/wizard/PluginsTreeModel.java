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
package com.aptana.ide.installer.wizard;

import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.installer.Activator;
import com.aptana.ide.update.manager.IPlugin;
import com.aptana.ide.update.manager.IPluginManager;
import com.aptana.ide.update.manager.InstallerCategory;
import com.aptana.ide.update.manager.Plugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class PluginsTreeModel
{

	private static final PluginTreeNode ROOT = new PluginTreeNode("root"); //$NON-NLS-1$

	/**
	 * Constructor.
	 */
	public PluginsTreeModel()
	{
		buildPluginTree();
	}

	/**
	 * Clears the data in the model.
	 */
	public void clear()
	{
		ROOT.clear();
	}

	/**
	 * Returns the category node with a specific ID.
	 * 
	 * @param id
	 *            the category id
	 * @return the corresponding category node
	 */
	public PluginTreeNode getCategory(String id)
	{
		PluginTreeNode[] categories = getCategories();
		for (PluginTreeNode category : categories)
		{
			if (category.getID().equals(id))
			{
				return category;
			}
		}
		return null;
	}

	/**
	 * Returns all the root categories the model contains.
	 * 
	 * @return an array of all the root categories
	 */
	public PluginTreeNode[] getCategories()
	{
		return ROOT.getChildren();
	}

	/**
	 * Returns the list of categories that should be expanded by default.
	 * 
	 * @return the list as an array
	 */
	public PluginTreeNode[] getExpandedCategories()
	{
		List<PluginTreeNode> nodes = new ArrayList<PluginTreeNode>();
		PluginTreeNode[] categories = ROOT.getChildren();
		Object data;
		InstallerCategory category;
		for (PluginTreeNode node : categories)
		{
			data = node.getData();
			if (data != null && data instanceof InstallerCategory)
			{
				category = (InstallerCategory) data;
				if (!category.shouldCollapse())
				{
					nodes.add(node);
				}
			}
		}
		return nodes.toArray(new PluginTreeNode[nodes.size()]);
	}

	/**
	 * Builds the proper tree structure from the plugins.xml file.
	 */
	private static void buildPluginTree()
	{
		ROOT.clear();

		List<Plugin> plugins = getPluginManager().getRemotePlugins();
		InstallerCategory category;
		for (Plugin plugin : plugins)
		{
			// finds the category the plug-in belongs in and adds it
			category = plugin.getInstallerCategory();
			if (category != null)
			{
				PluginTreeNode catNode = generateNodePath(category);
				catNode.add(new PluginTreeNode(plugin));
			}
		}
		updateStates();
	}

	protected static IPluginManager getPluginManager()
	{
		return Activator.getDefault().getPluginManager();
	}

	private static PluginTreeNode generateNodePath(InstallerCategory category)
	{
		List<InstallerCategory> path = new ArrayList<InstallerCategory>();
		path.add(category);
		InstallerCategory parentCategory = category.getParent();
		while (parentCategory != null)
		{
			path.add(parentCategory);
			parentCategory = parentCategory.getParent();
		}
		PluginTreeNode parentNode = ROOT;
		InstallerCategory childCategory;
		int index = path.size() - 1;
		while (index > -1)
		{
			childCategory = path.get(index);
			PluginTreeNode catNode = parentNode.getChild(childCategory.getID());
			if (catNode == null)
			{
				catNode = new PluginTreeNode(childCategory);
				parentNode.add(catNode);
			}
			parentNode = catNode;
			index--;
		}
		return parentNode;
	}

	/**
	 * Finds the list of all installed plug-ins and updates the model to disable the corresponding nodes in the tree.
	 */
	private static void updateStates()
	{
		List<PluginTreeNode> availablePlugins = new ArrayList<PluginTreeNode>();
		PluginTreeNode[] categories = ROOT.getChildren();
		updatePluginsList(availablePlugins, categories);

		List<IPlugin> installedPlugins = getPluginManager().getInstalledPlugins();
		for (PluginTreeNode plugin : availablePlugins)
		{
			plugin.setInstalled(isInstalled(plugin, installedPlugins));
		}
	}

	private static boolean isInstalled(PluginTreeNode node, List<IPlugin> installedPlugins)
	{
		Object data = node.getData();
		if (!(data instanceof IPlugin))
		{
			return false;
		}
		IPlugin plugin = (IPlugin) data;
		for (IPlugin installed : installedPlugins)
		{
			if (installed.getId().equals(plugin.getId()))
			{
				return true;
			}
		}
		return false;
	}

	private static void updatePluginsList(List<PluginTreeNode> plugins, PluginTreeNode[] nodes)
	{
		Object data;
		for (PluginTreeNode node : nodes)
		{
			data = node.getData();
			if (data != null)
			{
				plugins.add(node);
			}
			updatePluginsList(plugins, node.getChildren());
		}
	}

}
