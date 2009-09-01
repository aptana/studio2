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

import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.lexer.IRange;
import com.aptana.ide.lexer.Range;

/**
 * @author Kevin Lindsey
 * @author Robin Debreuil
 */
public class JSFunctionConstructor extends NativeConstructorBase
{
	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of NativeFunction
	 * 
	 * @param owningEnvironment
	 *            The environment in which this native function was created
	 */
	public JSFunctionConstructor(Environment owningEnvironment)
	{
		super(owningEnvironment);

		int fileIndex = FileContextManager.BUILT_IN_FILE_INDEX;

		// get global
		IScope global = owningEnvironment.getGlobal();

		// put self into environment
		global.putPropertyValue("Function", this, fileIndex, Property.DONT_DELETE | Property.DONT_ENUM); //$NON-NLS-1$

		// create public prototype object
		IObject prototype = owningEnvironment.createObject(fileIndex, Range.Empty);

		// store our public prototype
		this.putPropertyValue("prototype", prototype, fileIndex); //$NON-NLS-1$

		// set Function.[[prototype]]
		this.setPrototype(prototype);
	}

	/*
	 * Methods
	 */

	/**
	 * Initialize the properties on this object
	 */
	public void initializeProperties()
	{
		Environment environment = this.owningEnvironment;
		int attributes = Property.DONT_DELETE | Property.DONT_ENUM;
		int fileIndex = FileContextManager.BUILT_IN_FILE_INDEX;
		int offset = Range.Empty.getStartingOffset();

		// add built in memebers to Function
		this.putPropertyValue("length", environment.createNumber(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$

		// add built in memebers to Function.prototype
		IObject prototype = this.getPropertyValue("prototype", fileIndex, offset); //$NON-NLS-1$

		prototype.putPropertyValue("constructor", this, fileIndex, attributes); //$NON-NLS-1$
		prototype.putPropertyValue("toString", environment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		prototype.putPropertyValue("apply", environment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		prototype.putPropertyValue("call", environment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
	}

	/*
	 * IFunction implementation
	 */

	/**
	 * @see com.aptana.ide.editor.js.runtime.FunctionBase#construct(com.aptana.ide.editor.js.runtime.Environment, com.aptana.ide.editor.js.runtime.IObject[], int,
	 *      IRange)
	 */
	public IObject construct(Environment environment, IObject[] arguments, int fileIndex, IRange sourceRegion)
	{
		int attributes = Property.DONT_DELETE | Property.DONT_ENUM;

		// Create new instance
		JSFunction instance = new JSFunction(sourceRegion);

		// Point instance's [[proto]] to our public "prototype"
		instance.setPrototype(this.getPropertyValue("prototype", fileIndex, sourceRegion.getStartingOffset())); //$NON-NLS-1$

		// create public prototype
		IObject prototype = environment.createObject(fileIndex, sourceRegion);
		
		// set constructor
		prototype.putPropertyValue("constructor", instance, fileIndex, attributes); //$NON-NLS-1$

		// apply public prototype and other instance properties
		instance.putPropertyValue("length", environment.createNumber(fileIndex, sourceRegion), fileIndex, attributes); //$NON-NLS-1$
		instance.putPropertyValue("prototype", prototype, fileIndex, attributes); //$NON-NLS-1$

		return instance;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.FunctionBase#invoke(com.aptana.ide.editor.js.runtime.Environment, com.aptana.ide.editor.js.runtime.IObject[], int, IRange)
	 */
	public IObject invoke(Environment environment, IObject[] arguments, int fileIndex, IRange sourceRegion)
	{
		return environment.createFunction(fileIndex, sourceRegion);
	}
}
