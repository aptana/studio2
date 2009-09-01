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

import com.aptana.ide.metadata.IDocumentation;

/**
 * @author Kevin Lindsey
 */
public class JSUndefined implements IObject
{
	/*
	 * Fields
	 */
	private static final JSUndefined instance = new JSUndefined();

	/*
	 * Properties
	 */

	/**
	 * Return the JSNull singleton for this environment
	 * 
	 * @return Returns the JSNull singleton
	 */
	public static JSUndefined getSingletonInstance()
	{
		return instance;
	}

	/*
	 * IObject implementation
	 */

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#canPut(java.lang.String)
	 */
	public boolean canPut(String propertyName)
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getProperty(java.lang.String)
	 */
	public Property getProperty(String propertyName)
	{
		// do nothing
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getPropertyValue(java.lang.String, int, int)
	 */
	public IObject getPropertyValue(String propertyName, int fileIndex, int offset)
	{
		// do nothing
		return ObjectBase.UNDEFINED;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#putPropertyValue(java.lang.String, com.aptana.ide.editor.js.runtime.IObject, int)
	 */
	public void putPropertyValue(String propertyName, IObject value, int fileIndex)
	{
		// do nothing
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#putPropertyValue(java.lang.String, com.aptana.ide.editor.js.runtime.IObject, int, int)
	 */
	public void putPropertyValue(String propertyName, IObject value, int fileIndex, int attributes)
	{
		// do nothing
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getPropertyNames()
	 */
	public String[] getPropertyNames()
	{
		// do nothing
		return new String[0];
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getPropertyNames(boolean)
	 */
	public String[] getPropertyNames(boolean getAll)
	{
		// do nothing
		return new String[0];
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#hasProperty(java.lang.String)
	 */
	public boolean hasProperty(String propertyName)
	{
		// do nothing
		return false;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getLocalProperty(java.lang.String)
	 */
	public Property getLocalProperty(String propertyName)
	{
		// do nothing
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#putLocalProperty(java.lang.String, com.aptana.ide.editor.js.runtime.Property)
	 */
	public void putLocalProperty(String propertyName, Property property)
	{
		// do nothing
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getLocalPropertyNames()
	 */
	public String[] getLocalPropertyNames()
	{
		// do nothing
		return new String[0];
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#hasLocalProperty(java.lang.String)
	 */
	public boolean hasLocalProperty(String propertyName)
	{
		// do nothing
		return false;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#unputPropertyName(java.lang.String, int, int)
	 */
	public void unputPropertyName(String propertyName, int fileIndex, int offset)
	{
		// do nothing
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#deletePropertyName(java.lang.String)
	 */
	public boolean deletePropertyName(String propertyName)
	{
		// do nothing
		return false;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getPrototype()
	 */
	public IObject getPrototype()
	{
		// do nothing
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#setPrototype(com.aptana.ide.editor.js.runtime.IObject)
	 */
	public void setPrototype(IObject prototype)
	{
		// do nothing
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getClassName()
	 */
	public String getClassName()
	{
		return "undefined"; //$NON-NLS-1$
	}

	/**
	 * Return the unerlying primitive type
	 * 
	 * @param environment
	 *            The environment
	 * @param fileIndex
	 *            The file index
	 * @param offset
	 *            The file offset
	 * @return Returns the unerlying primitive type for this object
	 */
	public IObject getInstance(Environment environment, int fileIndex, int offset)
	{
		return instance;
	}

	/*
	 * IRange implementation
	 */

	/**
	 * @see com.aptana.ide.lexer.IRange#getEndingOffset()
	 */
	public int getEndingOffset()
	{
		return -1;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getLength()
	 */
	public int getLength()
	{
		return 0;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getStartingOffset()
	 */
	public int getStartingOffset()
	{
		return -1;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#isEmpty()
	 */
	public boolean isEmpty()
	{
		return true;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#containsOffset(int)
	 */
	public boolean containsOffset(int offset)
	{
		return false;
	}

	/*
	 * IDocumentation implementation
	 */

	/**
	 * @see com.aptana.ide.metadata.IDocumentationContainer#getDocumentation()
	 */
	public IDocumentation getDocumentation()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentationContainer#hasDocumentation()
	 */
	public boolean hasDocumentation()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentationContainer#setDocumentation(com.aptana.ide.metadata.IDocumentation)
	 */
	public void setDocumentation(IDocumentation documentation)
	{
		// do nothing
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getLocalPropertyCount()
	 */
	public int getLocalPropertyCount()
	{
		return 0;
	}
}
