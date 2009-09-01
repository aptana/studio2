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
package com.aptana.ide.editors.unified.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.eclipse.swt.graphics.Image;

/**
 * ContextItem
 * 
 * @author Ingo Muschenetz
 */
public class ContextItem
{
	private static LexemeOffsetComparator lexemeOffsetComparator = new LexemeOffsetComparator();
	private ArrayList children = null;
	private ContextItem[] currentItems = null;

	/**
	 * 
	 */
	public String name = null;

	/**
	 * 
	 */
	public int offset = -1;

	/**
	 * 
	 */
	public int length = -1;

	/**
	 * 
	 */
	public Hashtable values = null;

	/**
	 * 
	 */
	public Image icon = null;

	/**
	 * 
	 */
	public String doc = null;

	/**
	 * getName
	 * 
	 * @return String
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * setName
	 * 
	 * @param s
	 */
	public void setName(String s)
	{
		name = s;
	}

	/**
	 * getOffset
	 * 
	 * @return int
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * setOffset
	 * 
	 * @param i
	 */
	public void setOffset(int i)
	{
		offset = i;
	}

	/**
	 * getLength
	 * 
	 * @return int
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * setLength
	 * 
	 * @param i
	 */
	public void setLength(int i)
	{
		length = i;
	}

	/**
	 * ContextItem
	 */
	public ContextItem()
	{
		this(null);
	}

	/**
	 * ContextItem
	 * 
	 * @param name
	 */
	public ContextItem(String name)
	{
		this.name = name;
		this.values = new Hashtable();
		this.children = new ArrayList();
	}

	/**
	 * getItems
	 * 
	 * @return ContextItem[]
	 */
	public ContextItem[] getItems()
	{
		ContextItem[] items = getItems(null);

		Arrays.sort(items, lexemeOffsetComparator);

		return items;
	}

	/**
	 * getItems
	 * 
	 * @param startsWith
	 * @return ContextItem[]
	 */
	public ContextItem[] getItems(String startsWith)
	{
		if (currentItems == null)
		{
			currentItems = buildCurrentItems();
		}

		if (startsWith == null)
		{
			return currentItems;
		}

		ArrayList results = new ArrayList();

		for (int i = 0; i < currentItems.length; i++)
		{
			if (currentItems[i].name.startsWith(startsWith))
			{
				results.add(currentItems[i]);
			}
		}

		return (ContextItem[]) results.toArray(new ContextItem[0]);
	}

	/**
	 * containsItem
	 * 
	 * @param name
	 * @return ContextItem
	 */
	public ContextItem containsItem(String name)
	{
		for (int i = 0; i < children.size(); i++)
		{
			ContextItem item = (ContextItem) children.get(i);
			if (item.name.equals(name))
			{
				return item;
			}
		}

		return null;
	}

	/**
	 * buildCurrentItems
	 * 
	 * @return ContextItem[]
	 */
	private ContextItem[] buildCurrentItems()
	{
		ArrayList results = new ArrayList();

		ContextItem[] items = (ContextItem[]) children.toArray(new ContextItem[0]);

		for (int i = 0; i < items.length; i++)
		{
			results.add(items[i]);
		}

		return (ContextItem[]) results.toArray(new ContextItem[0]);
	}

	/**
	 * addItem
	 * 
	 * @param item
	 */
	public void addItem(ContextItem item)
	{
		children.add(item);
		currentItems = null;
	}

	/**
	 * clearAll
	 */
	public void clearAll()
	{
		children.clear();
		currentItems = null;
	}

	/**
	 * dispose
	 */
	public void dispose()
	{
		if (icon != null)
		{
			icon.dispose();
		}
	}
}
