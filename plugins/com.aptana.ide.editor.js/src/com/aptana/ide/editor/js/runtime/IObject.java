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

import com.aptana.ide.lexer.IRange;
import com.aptana.ide.metadata.IDocumentationContainer;

/**
 * @author Kevin Lindsey
 */
public interface IObject extends IRange, IDocumentationContainer
{
	/**
	 * Determines if the specified property name can be set. This implements [[CanPut]] from the ECMA specification
	 * 
	 * @param propertyName
	 *            The name of the property to test
	 * @return Returns true if the specified property name can be set
	 */
	boolean canPut(String propertyName);

	/**
	 * Get the Property object for the associated property name
	 * 
	 * @param propertyName
	 *            The name of the Property to return
	 * @return The Property object associated with the specified name
	 */
	Property getProperty(String propertyName);

	/**
	 * Returns the value of the name property. This method will crawl up the prototype chain if the current object does
	 * not contain the specified property name. This implements [[Get]] from the ECMA specification.
	 * 
	 * @param propertyName
	 *            The name of the property to retrieve
	 * @param fileIndex
	 *            The index of the file where this property is to be retrieved
	 * @param offset
	 *            the offset within the file
	 * @return The object associated with the specified property name
	 */
	IObject getPropertyValue(String propertyName, int fileIndex, int offset);

	/**
	 * Sets the value of the specified property name. This implements [[Put]] from the ECMA specification
	 * 
	 * @param propertyName
	 *            The name of the property to set
	 * @param value
	 *            The value to associate with the given property name
	 * @param fileIndex
	 *            The index of the file where this property is to be set
	 */
	void putPropertyValue(String propertyName, IObject value, int fileIndex);

	/**
	 * Create a property and set its attributes
	 * 
	 * @param propertyName
	 *            The property name to create
	 * @param value
	 *            The value of the property
	 * @param fileIndex
	 *            The index of the file where this property is to be set
	 * @param attributes
	 *            The attributes for the property
	 */
	void putPropertyValue(String propertyName, IObject value, int fileIndex, int attributes);

	/**
	 * Get all enumerable property names on this object
	 * 
	 * @return An array of all visible property names
	 */
	String[] getPropertyNames();

	/**
	 * Get all property names on this object
	 * 
	 * @param getAll
	 *            If true, then all properties, including properties with the DONT_ENUM attribute, will be returned
	 * @return An array of all property names on this object
	 */
	String[] getPropertyNames(boolean getAll);

	/**
	 * Determine if this object has the specified property name. This method will crawl up the prototype chain if the
	 * current object does not contain the specified property name. This implements [[CanPut]] from the ECMA
	 * specification.
	 * 
	 * @param propertyName
	 *            The name of the property to test
	 * @return Returns true if this object contains the specified property name
	 */
	boolean hasProperty(String propertyName);

	/**
	 * Returns a local property defined on an object. This does not follow the prototype chain if the property does not
	 * exist locally
	 * 
	 * @param propertyName
	 *            The name of the property to retrieve
	 * @return The property
	 */
	Property getLocalProperty(String propertyName);

	/**
	 * Set the Property object with the associated name
	 * 
	 * @param propertyName
	 *            The name of the property to set
	 * @param property
	 *            The property instance
	 */
	void putLocalProperty(String propertyName, Property property);

	/**
	 * Get all enumerable property names that exist on this object only (does not follow prototype chain)
	 * 
	 * @return An array of property names
	 */
	String[] getLocalPropertyNames();

	/**
	 * Determine if this object has the specified property name. This method will crawl up the prototype chain if the
	 * current object does not contain the specified property name. This implements [[CanPut]] from the ECMA
	 * specification.
	 * 
	 * @param propertyName
	 *            The name of the property to test
	 * @return Returns true if this object contains the specified property name
	 */
	boolean hasLocalProperty(String propertyName);

	/**
	 * Remove a reference to the specified property name. If the property no longer has references, it will be removed
	 * from this object
	 * 
	 * @param propertyName
	 *            The name of the property to unput
	 * @param fileIndex
	 *            The file index of the property to unput
	 * @param offset
	 *            The file offset
	 */
	void unputPropertyName(String propertyName, int fileIndex, int offset);

	/**
	 * Remove the specified property name from this object. This implements [[Delete]] from the ECMA specification
	 * 
	 * @param propertyName
	 *            The property name to remove from this object
	 * @return Returns true if the property name was successfully removed from this object
	 */
	boolean deletePropertyName(String propertyName);

	/**
	 * Returns the prototype of this object. This implements [[Prototype]] from the ECMA specification.
	 * 
	 * @return This object's prototype object
	 */
	IObject getPrototype();

	/**
	 * Set this object's prototype ([[Prototype]])
	 * 
	 * @param prototype
	 *            This object's new prototype object
	 */
	void setPrototype(IObject prototype);

	/**
	 * Returns the class name of this object. This implements [[Class]] from the ECMA specification
	 * 
	 * @return This object's class name
	 */
	String getClassName();

	/**
	 * Returns the underlying instance contained by this object. Built-in types and primitive types will simply return
	 * themselves. This mechanism is needed more for handling CommandNodes that are in the environment.
	 * 
	 * @param environment
	 *            The environment
	 * @param fileIndex
	 *            The file index
	 * @param offset
	 *            The file offset
	 * @return Returns a built-in or primitive type contained by this IObject
	 */
	IObject getInstance(Environment environment, int fileIndex, int offset);
	
	/**
	 * Returns the number of porperties defined locally to this object.
	 * @return Returns the number of porperties defined locally to this object.
	 */
	int getLocalPropertyCount();
}