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



/**
 * @author Kevin Lindsey
 */
public class Reference
{
	/*
	 * Fields
	 */

	/**
	 * The empty reference
	 */
	public static final Reference Empty = new Reference();

	private IObject _objectBase;
	private String _propertyName;

	/*
	 * Properties
	 */

	/**
	 * Get the base object where this referenced property lives
	 * 
	 * @return The base object of this reference
	 */
	public IObject getObjectBase()
	{
		return this._objectBase;
	}

	/**
	 * Get the name of the property this reference refers to
	 * 
	 * @return The property name of this reference
	 */
	public String getPropertyName()
	{
		return this._propertyName;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of Reference
	 */
	private Reference()
	{
		// this is here so we can define static Empty
	}

	/**
	 * Create a new instance of Reference
	 * 
	 * @param objectBase
	 *            The base object for this reference
	 * @param propertyName
	 *            The base object property name this reference refers to
	 */
	public Reference(IObject objectBase, String propertyName)
	{
		if (objectBase == null)
		{
			throw new NullPointerException(Messages.Reference_ObjectBaseMustBeDefined);
		}
		if (propertyName == null || propertyName.length() == 0)
		{
			throw new NullPointerException(Messages.Reference_PropertyNameMustBeDefined);
		}

		this._objectBase = objectBase;
		this._propertyName = propertyName;
	}

	/*
	 * Methods
	 */

	/**
	 * Retrieve the property that this references points to
	 * 
	 * @return The property this reference points to
	 */
	public Property getProperty()
	{
		return this._objectBase.getProperty(this._propertyName);
	}

	/**
	 * Get the value pointed to by this reference
	 * 
	 * @param fileIndex
	 *            The index of the file where this reference points
	 * @param offset
	 *            The offset within the file
	 * @return Returns the IObject that this reference points to
	 */
	public IObject getValue(int fileIndex, int offset)
	{
		return this._objectBase.getPropertyValue(this._propertyName, fileIndex, offset);
	}

	/**
	 * Set the value of this reference
	 * 
	 * @param value
	 *            The new value for this reference
	 * @param fileIndex
	 *            The index of the file where this reference points
	 */
	public void setValue(IObject value, int fileIndex)
	{
		this._objectBase.putPropertyValue(this._propertyName, value, fileIndex);
	}

	/**
	 * Unset the property this reference points to. This will only remove the associated property if the reference count
	 * of that property goes to zero
	 * 
	 * @param value
	 *            The value to unset
	 * @param fileIndex
	 *            The index of the file where this reference points
	 */
	public void unsetValue(IObject value, int fileIndex)
	{
		this._objectBase.unputPropertyName(this._propertyName, fileIndex, value.getStartingOffset());
	}
}
