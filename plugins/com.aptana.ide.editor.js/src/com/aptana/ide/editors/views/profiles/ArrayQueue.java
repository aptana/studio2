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
package com.aptana.ide.editors.views.profiles;

/**
 * @author Jason Vowell
 */
public class ArrayQueue implements IQueue
{
	private Object[] theArray;
	private int currentSize;
	private int front;
	private int back;

	private static final int DEFAULT_CAPACITY = 10;

	/**
	 * Construct the queue.
	 */
	public ArrayQueue()
	{
		theArray = new Object[DEFAULT_CAPACITY];
		makeEmpty();
	}

	/**
	 * Test if the queue is logically empty.
	 * 
	 * @return true if empty, false otherwise.
	 */
	public boolean isEmpty()
	{
		return currentSize == 0;
	}

	/**
	 * @see com.aptana.ide.editors.views.profiles.IQueue#size()
	 */
	public int size()
	{
		return currentSize;
	}

	/**
	 * Make the queue logically empty.
	 */
	public void makeEmpty()
	{
		currentSize = 0;
		front = 0;
		back = -1;
	}

	/**
	 * Return and remove the least recently inserted item from the queue.
	 * 
	 * @return the least recently inserted item in the queue.
	 * @throws UnderflowException
	 *             if the queue is empty.
	 */
	public Object dequeue()
	{
		if (isEmpty())
		{
			throw new UnderflowException("ArrayQueue dequeue"); //$NON-NLS-1$
		}
		
		currentSize--;

		Object returnValue = theArray[front];
		front = increment(front);
		return returnValue;
	}

	/**
	 * Get the least recently inserted item in the queue. Does not alter the queue.
	 * 
	 * @return the least recently inserted item in the queue.
	 * @throws UnderflowException
	 *             if the queue is empty.
	 */
	public Object getFront()
	{
		if (isEmpty())
		{
			throw new UnderflowException("ArrayQueue getFront"); //$NON-NLS-1$
		}
		
		return theArray[front];
	}

	/**
	 * Insert a new item into the queue.
	 * 
	 * @param x
	 *            the item to insert.
	 */
	public void enqueue(Object x)
	{
		if (currentSize == theArray.length)
		{
			doubleQueue();
		}
		
		back = increment(back);
		theArray[back] = x;
		currentSize++;
	}

	/**
	 * Internal method to increment with wraparound.
	 * 
	 * @param x
	 *            any index in theArray's range.
	 * @return x+1, or 0 if x is at the end of theArray.
	 */
	private int increment(int x)
	{
		if (++x == theArray.length)
		{
			x = 0;
		}
		
		return x;
	}

	/**
	 * Internal method to expand theArray.
	 */
	private void doubleQueue()
	{
		Object[] newArray;

		newArray = new Object[theArray.length * 2];

		// Copy elements that are logically in the queue
		for (int i = 0; i < currentSize; i++, front = increment(front))
		{
			newArray[i] = theArray[front];
		}

		theArray = newArray;
		front = 0;
		back = currentSize - 1;
	}

	/**
	 * @author Jason Vowell
	 */
	public final class UnderflowException extends RuntimeException
	{
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -8415336858737771428L;

		/**
		 * Construct this exception object.
		 * 
		 * @param message
		 *            the error message.
		 */
		public UnderflowException(String message)
		{
			super(message);
		}
	}
}
