/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.core.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.aptana.ide.core.xpath.XPathUtils;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @param <T>
 */
public abstract class CoreGroupObject<T extends CoreModelObject> extends CoreModelObject
{

	/**
	 * Children in this group
	 */
	protected List<T> children;

	/**
	 * Does this group require child items to have unique ids to be added to the group?
	 */
	protected boolean requireUniqueIds;

	/**
	 * Child template object
	 */
	protected T childTemplate;

	/**
	 * Creates a new grouped object
	 */
	public CoreGroupObject()
	{
		children = new ArrayList<T>();
		childTemplate = createItem();
		this.requireUniqueIds = true;
	}

	/**
	 * Gets an item with the passed in id
	 * 
	 * @param itemId
	 * @return - item or null if no item found with the id passed in
	 */
	public T getItem(String itemId)
	{
		if (itemId != null)
		{
			for (T item : getItems())
			{
				if (itemId.equals(item.getId()))
				{
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * Should the new item be added to this group
	 * 
	 * @param item
	 * @return - true if the new item should be added
	 */
	public abstract boolean shouldAdd(T item);

	/**
	 * Gets the items in this group
	 * 
	 * @return - array of core model objects
	 */
	public T[] getItems()
	{
		return this.children.toArray(createEmptyArray());
	}

	/**
	 * Clears all items in this group
	 */
	public synchronized void clear()
	{
		if (this.children != null)
		{
			isLoading = true;
			for (T item : getItems())
			{
				item.clear();
			}
			this.children.clear();
		}
	}

	/**
	 * @see com.aptana.ide.core.model.BaseModelObject#addListener(com.aptana.ide.core.model.IModelListener)
	 */
	public void addListener(IModelListener listener)
	{
		super.addListener(listener);
		for (T item : getItems())
		{
			item.addListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#fromNode(org.w3c.dom.Node)
	 */
	public void fromNode(Node node)
	{
		try
		{
			NodeList set = (NodeList) XPATH.evaluate(getItemString(), node, XPathConstants.NODESET);
			updateGroup(set);
		}
		catch (XPathExpressionException e1)
		{
			isLoading = false;
			String message = MessageFormat.format(
				Messages.getString("CoreGroupObject.XPath_Error_During_Parse"), //$NON-NLS-1$
				new Object[] {
					getLoggingPrefix(),
					e1.getMessage()
				}
			);
			logError(message);
		}
	}

	/**
	 * Creates an empty array
	 * 
	 * @return - empty array
	 */
	protected abstract T[] createEmptyArray();

	/**
	 * Creates a new item
	 * 
	 * @return - item
	 */
	public abstract T createItem();

	/**
	 * Configures the item with some initial state such as logging, service provider, and request builder taken from
	 * this group object
	 * 
	 * @param item
	 */
	protected void configureItem(T item)
	{
		configurationLocation(item);
		item.setRequestBuilder(getRequestBuilder());
		item.setServiceProvider(getServiceProvider());
	}

	/**
	 * Configures the location for the item from the id and location of the group object
	 * 
	 * @param item
	 */
	protected void configurationLocation(T item)
	{
		if (item.getId() != null && getLocation() != null)
		{
			String itemLocation = getLocation().toExternalForm();
			if (!itemLocation.endsWith("/")) //$NON-NLS-1$
			{
				itemLocation += "/"; //$NON-NLS-1$
			}
			itemLocation += item.getId();
			try
			{
				URL itemURL = new URL(itemLocation);
				item.setLocation(itemURL);
			}
			catch (MalformedURLException e)
			{
				String message = MessageFormat.format(
					Messages.getString("CoreGroupObject.Error_During_Build"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						e.getMessage()
					}
				);
				logError(message);
			}
		}
	}

	/**
	 * Is the group loading?
	 */
	protected boolean isLoading = true;

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#update()
	 */
	public IServiceErrors update()
	{
		IServiceErrors errors = null;
		isLoading = true;
		try
		{
			errors = super.update();
		}
		finally
		{
			isLoading = false;
		}
		return errors;
	}

	/**
	 * True if the group is loading
	 * 
	 * @return - true if loading
	 */
	public boolean isLoading()
	{
		return isLoading;
	}

	private void updateGroup(NodeList set)
	{
		try
		{
			List<T> newChildren = new ArrayList<T>();
			if (requireUniqueIds)
			{
				synchronized (this)
				{
					newChildren.addAll(this.children);
				}
			}
			List<String> ids = new ArrayList<String>();
			for (int i = 0; i < set.getLength(); i++)
			{
				T groupItem = null;
				boolean newItem = true;
				if (requireUniqueIds)
				{
					String id = getTextContent(ID_ELEMENT, set.item(i));
					if (id != null && id.length() > 0)
					{
						ids.add(id);

						for (T item : this.children)
						{
							if (id.equals(item.getId()))
							{
								groupItem = item;
								newItem = false;
								break;
							}
						}
						if (groupItem == null)
						{
							groupItem = createItem();
							if (requireUniqueIds)
							{
								groupItem.setId(id);
							}
						}
					}
				}
				else
				{
					groupItem = createItem();
				}
				if (groupItem != null)
				{
					groupItem.suspendEvents();
					groupItem.setLogger(getLogger());
					groupItem.addListeners(getListeners());
					groupItem.fromNode(set.item(i));
					configureItem(groupItem);
					if (newItem && shouldAdd(groupItem))
					{
						newChildren.add(groupItem);
						groupItem.fireChange();
					}
					groupItem.resumeEvents();
				}
			}
			if (requireUniqueIds)
			{
				Iterator<T> iterator = newChildren.iterator();
				while (iterator.hasNext())
				{
					T removedItem = iterator.next();
					if (!ids.contains(removedItem.getId()))
					{
						iterator.remove();
						removedItem.clear();
					}
				}
			}
			synchronized (this)
			{
				this.children = newChildren;
			}
		}
		catch (Exception e1)
		{
			String message = MessageFormat.format(
				Messages.getString("CoreGroupObject.XPath_Error_During_Parse"), //$NON-NLS-1$
				new Object[] {
					getLoggingPrefix(),
					e1.getMessage()
				}
			);
			logError(message);
		}
		finally
		{
			isLoading = false;
		}
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#fromXML(java.lang.String)
	 */
	public void fromXML(String xml)
	{
		try
		{
			NodeList set = (NodeList) XPATH.evaluate("/" + getGroupString() + "/" + getItemString(), XPathUtils //$NON-NLS-1$ //$NON-NLS-2$
					.createSource(xml), XPathConstants.NODESET);
			updateGroup(set);
		}
		catch (XPathExpressionException e1)
		{
			isLoading = false;
			String message = MessageFormat.format(
				Messages.getString("CoreGroupObject.XPath_Error_During_Parse"), //$NON-NLS-1$
				new Object[] {
					getLoggingPrefix(),
					e1.getMessage()
				}
			);
			logError(message);
		}
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#toNode()
	 */
	public Node toNode()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#toXML()
	 */
	public String toXML()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<" + getGroupString() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		for (T item : getItems())
		{
			buffer.append(item.toXML());
		}
		buffer.append("</" + getGroupString() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		return buffer.toString();
	}

	/**
	 * Gets the group string to use for the XPath
	 * 
	 * @return - group element string
	 */
	protected abstract String getGroupString();

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#getItemString()
	 */
	protected String getItemString()
	{
		if (childTemplate != null)
		{
			return childTemplate.getItemString();
		}
		return null;
	}

	/**
	 * @return the requireUniqueIds
	 */
	public boolean isRequireUniqueIds()
	{
		return requireUniqueIds;
	}

	/**
	 * @param requireUniqueIds
	 *            the requireUniqueIds to set
	 */
	public void setRequireUniqueIds(boolean requireUniqueIds)
	{
		this.requireUniqueIds = requireUniqueIds;
	}
	
	/**
	 * Adds the child item
	 * @param items
	 *            the child to add
	 */
	public void addChild(T item)
	{
		this.children.add(item);
	}

}
