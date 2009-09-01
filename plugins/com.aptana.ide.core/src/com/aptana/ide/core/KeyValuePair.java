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
package com.aptana.ide.core;

/**
 * KeyValuePair
 */
public class KeyValuePair
{
	/*
	 * Fields
	 */

	/**
	 * key
	 */
	private Object key;

	/**
	 * value
	 */
	private Object value;

	private int location = -1;

	/*
	 * Constructors
	 */

	/**
	 * KeyValuePair
	 * 
	 * @param k
	 *            the key
	 * @param v
	 *            the value
	 */
	public KeyValuePair(Object k, Object v)
	{
		this.setKey(k);
		this.setValue(v);
	}

	/**
	 * KeyValuePair
	 * 
	 * @param k
	 * @param v
	 * @param location
	 */
	public KeyValuePair(Object k, Object v, int location)
	{
		this(k, v);
		this.location = location;
	}

	/*
	 * Methods
	 */

	/**
	 * Checks the equality of this object
	 * 
	 * @param pair
	 * @return boolean
	 */
	public final boolean equals(Object pair)
	{
		if (pair instanceof KeyValuePair && pair instanceof KeyValuePair)
		{
			return ((KeyValuePair) pair).getKey() == this.getKey();
		}
		else
		{
			return false;
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return super.hashCode();
	}

	/**
	 * Sets the key.
	 * 
	 * @param k
	 *            the key value to set
	 */
	public void setKey(Object k)
	{
		this.key = k;
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key value
	 */
	public Object getKey()
	{
		return key;
	}

	/**
	 * Sets the value of the pair.
	 * 
	 * @param v
	 *            the value
	 */
	public void setValue(Object v)
	{
		this.value = v;
	}

	/**
	 * Gets the value of the pair.
	 * 
	 * @return the value
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * @return the location
	 */
	public int getLocation()
	{
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(int location)
	{
		this.location = location;
	}
}
