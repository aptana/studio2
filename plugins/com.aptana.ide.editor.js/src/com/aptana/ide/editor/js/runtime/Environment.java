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

import java.util.Collections;
import java.util.List;

import com.aptana.ide.lexer.IRange;
import com.aptana.ide.parsing.IRuntimeEnvironment;
import com.aptana.ide.parsing.nodes.ParseNodeBase;

/**
 * @author Kevin Lindsey
 * @author Robin Debreuil
 */
public class Environment implements com.aptana.ide.parsing.IRuntimeEnvironment
{
	private JSScope _global;
	private JSFunctionConstructor _jsFunctionConstructor;
	private JSObjectConstructor _jsObjectConstructor;
	private JSArrayConstructor _jsArrayConstructor;
	private JSBooleanConstructor _jsBooleanConstructor;
	private JSDateConstructor _jsDateConstructor;
	private JSErrorConstructor _jsErrorConstructor;
	private JSNumberConstructor _jsNumberConstructor;
	private JSRegExpConstructor _jsRegExpConstructor;
	private JSStringConstructor _jsStringConstructor;

	/**
	 * Environment
	 */
	public Environment()
	{
	}

	/**
	 * Get the global object for this Environment
	 * 
	 * @return Returns the global scope for this environment
	 */
	public JSScope getGlobal()
	{
		return this._global;
	}

	/**
	 * createInstance
	 * 
	 * @param name
	 * @param fileIndex
	 * @param sourceRegion
	 * @return IObject
	 */
	private IObject createInstance(String name, int fileIndex, IRange sourceRegion)
	{
		// make sure name is defined
		if (name == null || name.length() == 0)
		{
			throw new IllegalArgumentException(Messages.Environment_CtorNameMustBeDefined);
		}
		if (sourceRegion == null)
		{
			throw new IllegalArgumentException(Messages.Environment_RegionMustNotBeNull);
		}

		int offset = sourceRegion.getStartingOffset();
		IFunction constructor = (IFunction) this._global.getPropertyValue(name, fileIndex, offset);

		return constructor.construct(this, new IObject[0], fileIndex, sourceRegion);
	}

	/**
	 * Create a runtime array object
	 * 
	 * @param fileIndex
	 *            The file index
	 * @param sourceRegion
	 *            The region within the source file that creates this object
	 * @return A runtime array object
	 */
	public IObject createArray(int fileIndex, IRange sourceRegion)
	{
		return this.createInstance("Array", fileIndex, sourceRegion); //$NON-NLS-1$
	}

	/**
	 * Create a runtime boolean object
	 * 
	 * @param fileIndex
	 *            The file index
	 * @param sourceRegion
	 *            The region within the source file that creates this object
	 * @return A runtime boolean object
	 */
	public IObject createBoolean(int fileIndex, IRange sourceRegion)
	{
		return this.createInstance("Boolean", fileIndex, sourceRegion); //$NON-NLS-1$
	}

	/**
	 * Create a runtime date object
	 * 
	 * @param fileIndex
	 *            The file index
	 * @param sourceRegion
	 *            The region within the source file that creates this object
	 * @return A runtime date object
	 */
	public IObject createDate(int fileIndex, IRange sourceRegion)
	{
		return this.createInstance("Date", fileIndex, sourceRegion); //$NON-NLS-1$
	}

	/**
	 * Create a runtime error object
	 * 
	 * @param fileIndex
	 *            The file index
	 * @param sourceRegion
	 *            The region within the source file that creates this object
	 * @return A runtime error object
	 */
	public IObject createError(int fileIndex, IRange sourceRegion)
	{
		return this.createInstance("Error", fileIndex, sourceRegion); //$NON-NLS-1$
	}

	/**
	 * Create a runtime function object
	 * 
	 * @param fileIndex
	 *            The file index
	 * @param sourceRegion
	 *            the region within the source file that creates this object
	 * @return A runtime function object
	 */
	public IObject createFunction(int fileIndex, IRange sourceRegion)
	{
		return this.createInstance("Function", fileIndex, sourceRegion); //$NON-NLS-1$
	}

	/**
	 * Create a runtime null value. This returns a singleton instance of JSNull
	 * 
	 * @return A runtime JSNull object
	 */
	public JSNull createNull()
	{
		return ObjectBase.NULL;
	}

	/**
	 * Create a runtime number object
	 * 
	 * @param fileIndex
	 *            The file index
	 * @param sourceRegion
	 *            The region within the source file that creates this object
	 * @return A runtime number object
	 */
	public IObject createNumber(int fileIndex, IRange sourceRegion)
	{
		return this.createInstance("Number", fileIndex, sourceRegion); //$NON-NLS-1$
	}

	/**
	 * Create a runtime object object
	 * 
	 * @param fileIndex
	 *            The file index
	 * @param sourceRegion
	 *            TODO
	 * @return A runtime object object
	 */
	public IObject createObject(int fileIndex, IRange sourceRegion)
	{
		return this.createInstance("Object", fileIndex, sourceRegion); //$NON-NLS-1$
	}

	/**
	 * Create a runtime regExp object
	 * 
	 * @param fileIndex
	 *            The file index
	 * @param sourceRegion
	 *            The region within the source file that creates this object
	 * @return A runtime regExp object
	 */
	public IObject createRegExp(int fileIndex, IRange sourceRegion)
	{
		return this.createInstance("RegExp", fileIndex, sourceRegion); //$NON-NLS-1$
	}

	/**
	 * Create a runtime string object
	 * 
	 * @param fileIndex
	 *            The file index
	 * @param sourceRegion
	 *            The region within the source file that creates this object
	 * @return A runtime string object
	 */
	public IObject createString(int fileIndex, IRange sourceRegion)
	{
		return this.createInstance("String", fileIndex, sourceRegion); //$NON-NLS-1$
	}

	/**
	 * Create a runtime undefined value. This returns a singleton instance of JSNull
	 * 
	 * @return A runtime JSUndefined object
	 */
	public JSUndefined createUndefined()
	{
		return ObjectBase.UNDEFINED;
	}

	/**
	 * Initialize the built-in objects and return the global object
	 * 
	 * @return The global object
	 */
	public JSScope initBuiltInObjects()
	{
		// create our global object/scope
		this._global = new JSScope();

		// Create built-in objects in two passes to avoid contruction order when
		// building properties on the built-in functions
		_jsObjectConstructor = new JSObjectConstructor(this);
		_jsFunctionConstructor = new JSFunctionConstructor(this);
		_jsArrayConstructor = new JSArrayConstructor(this);
		_jsBooleanConstructor = new JSBooleanConstructor(this);
		_jsDateConstructor = new JSDateConstructor(this);
		_jsErrorConstructor = new JSErrorConstructor(this);
		_jsNumberConstructor = new JSNumberConstructor(this);
		_jsRegExpConstructor = new JSRegExpConstructor(this);
		_jsStringConstructor = new JSStringConstructor(this);

		// The second pass initializes all properties on the built-in functions.
		_jsObjectConstructor.initializeProperties();
		_jsFunctionConstructor.initializeProperties();
		_jsArrayConstructor.initializeProperties();
		_jsBooleanConstructor.initializeProperties();
		_jsDateConstructor.initializeProperties();
		_jsErrorConstructor.initializeProperties();
		_jsNumberConstructor.initializeProperties();
		_jsRegExpConstructor.initializeProperties();
		_jsStringConstructor.initializeProperties();

		// init Math
		new NativeMath(this);

		return this._global;
	}

	/**
	 * @see IRuntimeEnvironment#addId(String, int, ParseNodeBase)
	 */
	public void addId(String id, int fileIndex, ParseNodeBase hn) {
	}

	/**
	 * @see IRuntimeEnvironment#getAllIds()
	 */
	public String[] getAllIds() {
		return new String[0];
	}

	/**
	 * @see IRuntimeEnvironment#removeFileIds(int)
	 */
	public void removeFileIds(int fileIndex) {
		
	}

	public void addClass(String cssClass, int fileIndex, ParseNodeBase hn)
	{
		// do nothing		
	}

	public List<String> getAllClasses()
	{
		return Collections.emptyList();
	}
}
