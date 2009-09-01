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
package com.aptana.ide.editor.js.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Kevin Lindsey
 */
public class OrderedObjectCollection
{
	private List<OrderedObject> _items;

	/**
	 * Retrieve the IObject at the specified index
	 * 
	 * @param index
	 *            The index to retrieve from this collection
	 * @return Returns the IObject at the specified index
	 */
	public OrderedObject get(int index)
	{
		OrderedObject result = null;

		if (this._items != null)
		{
			result = this._items.get(index);
		}

		return result;
	}

	/**
	 * Get the number of command nodes in this collection
	 * 
	 * @return Returns the size of this collection
	 */
	public int size()
	{
		int result = 0;

		if (this._items != null)
		{
			result = this._items.size();
		}

		return result;
	}

	/**
	 * Add the specified command node to the collection
	 * 
	 * @param object
	 *            The new node to add to this collection
	 * @param fileIndex
	 *            The file index
	 */
	public void add(IObject object, int fileIndex)
	{
		if (object == null)
		{
			throw new NullPointerException(Messages.OrderedObjectCollection_ObjectMustBeDefined);
		}

		// make sure we have an ArrayList to add to
		if (this._items == null)
		{
			this._items = new ArrayList<OrderedObject>();
		}

		// get offset
		int offset = object.getStartingOffset();

		// make search key
		Location key = new Location(fileIndex, offset);

		// find where we need to insert the node into our list
		int position = Collections.binarySearch(this._items, key);

		if (position < 0)
		{
			// calculate insertion point
			position = -(position + 1);

			// add new value to our collection
			this._items.add(position, new OrderedObject(object, fileIndex));
		}
		else
		{
			// We don't allow overwriting of existing values
			throw new IllegalStateException(Messages.OrderedObjectCollection_CanNotOverwriteExistingObject);
		}
	}

	/**
	 * Return the object at or just before the specified file index and offset
	 * 
	 * @param fileIndex
	 *            The file index
	 * @param offset
	 *            The offset within the file
	 * @return Returns the object at or just before the specified file index and offset
	 */
	public IObject getFloorObject(int fileIndex, int offset)
	{
		int size = this.size();
		IObject result = ObjectBase.UNDEFINED;

		if (size > 0)
		{
			// find index in our collection
			Location key = new Location(fileIndex, offset);
			int position = Collections.binarySearch(this._items, key);

			// see if we're in between values
			if (position < 0)
			{
				// we are in between values, so find the value index to our left
				position = -(position + 1) - 1;
			}

			// grab the object from the collection
			if (0 <= position && position < size)
			{
				OrderedObject item = this._items.get(position);

				result = item.object;
			}
		}

		return result;
	}

	/**
	 * Return the file index of the object at or just before the specified file index and offset
	 * 
	 * @param fileIndex
	 *            The file index
	 * @param offset
	 *            The offset within the file
	 * @return Returns the file index of the object at or just before the specified file index and offset
	 */
	public int getFloorFileIndex(int fileIndex, int offset)
	{
		int size = this.size();
		int result = -1;

		if (size > 0)
		{
			// find index in our collection
			Location key = new Location(fileIndex, offset);
			int position = Collections.binarySearch(this._items, key);

			// see if we're in between values
			if (position < 0)
			{
				// we are in between values, so find the value index to our left
				position = -(position + 1) - 1;
			}

			// grab the object from the collection
			if (0 <= position && position < size)
			{
				OrderedObject item = this._items.get(position);

				result = item.fileIndex;
			}
		}

		return result;
	}
	
	/**
	 * Remove the OrderedObject at the specified index
	 * 
	 * @param index
	 *            The index of the element to remove from this collection
	 */
	public void remove(int index)
	{
		this._items.remove(index);
	}

	/**
	 * Remove the command node at the specified file index and offset.
	 * 
	 * @param fileIndex
	 *            The file index
	 * @param offset
	 *            The file offset
	 */
	public void remove(int fileIndex, int offset)
	{
		if (this._items != null)
		{
			Location key = new Location(fileIndex, offset);
			int position = Collections.binarySearch(this._items, key);

			if (position >= 0)
			{
				this._items.remove(position);
			}
			else
			{
				throw new IllegalStateException(Messages.OrderedObjectCollection_NoObjectExists);
			}
		}
		else
		{
			throw new IllegalStateException(Messages.OrderedObjectCollection_CanNotRemoveFromEmpty);
		}
	}
}
