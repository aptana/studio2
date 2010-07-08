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
package com.aptana.ide.installer.wizard;

import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.update.manager.IPlugin;
import com.aptana.ide.update.manager.InstallerCategory;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class PluginTreeNode
{

	private Object data;
	private boolean installed;
	private PluginTreeNode parent;
	private List<PluginTreeNode> children;

	public PluginTreeNode(Object data)
	{
		this.data = data;
		children = new ArrayList<PluginTreeNode>();
	}

	/**
	 * Adds a child to the node.
	 * 
	 * @param child
	 *            the child node
	 */
	public void add(PluginTreeNode child)
	{
		children.add(child);
	}

	/**
	 * Removes all elements that belong to this node.
	 */
	public void clear()
	{
		data = null;
		installed = false;
		parent = null;
		for (PluginTreeNode child : children)
		{
			child.clear();
		}
		children.clear();
	}

	/**
	 * Returns the data object that the node contains.
	 * 
	 * @return the data object
	 */
	public Object getData()
	{
		return data;
	}

	/**
	 * Returns the id of the node.
	 * 
	 * @return the id
	 */
	public String getID()
	{
		if (data == null)
		{
			return ""; //$NON-NLS-1$
		}
		if (data instanceof InstallerCategory)
		{
			return ((InstallerCategory) data).getID();
		}
		if (data instanceof IPlugin)
		{
			return ((IPlugin) data).getId();
		}
		return data.toString();
	}

	/**
	 * Returns whether or not the node represents a plug-in that is already installed.
	 * 
	 * @return true if the plug-in is installed, false otherwise
	 */
	public boolean isInstalled()
	{
		return installed;
	}

	/**
	 * Returns the direct child of this node that has the specific id.
	 * 
	 * @param id
	 *            the id
	 * @return the child that matches the id
	 */
	public PluginTreeNode getChild(String id)
	{
		for (PluginTreeNode child : children)
		{
			if (child.getID().equals(id))
			{
				return child;
			}
		}
		return null;
	}

	/**
	 * Returns all the direct children of this node.
	 * 
	 * @return an array of all the children
	 */
	public PluginTreeNode[] getChildren()
	{
		return children.toArray(new PluginTreeNode[children.size()]);
	}

	/**
	 * Returns the parent of this node.
	 * 
	 * @return the parent node
	 */
	public PluginTreeNode getParent()
	{
		return parent;
	}

	/**
	 * Sets the data object this node represents.
	 * 
	 * @param data
	 *            the data object
	 */
	public void setData(Object data)
	{
		this.data = data;
	}

	/**
	 * Sets whether or not the plug-in this node represents is installed.
	 * 
	 * @param installed
	 *            true if the plug-in is installed, false otherwise
	 */
	public void setInstalled(boolean installed)
	{
		this.installed = installed;
	}

	/**
	 * Sets the parent of this node.
	 * 
	 * @param parent
	 *            the parent node
	 */
	public void setParent(PluginTreeNode parent)
	{
		this.parent = parent;
	}

}
