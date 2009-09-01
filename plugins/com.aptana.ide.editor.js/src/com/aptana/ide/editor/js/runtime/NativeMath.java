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
import com.aptana.ide.lexer.Range;

/**
 * @author Kevin Lindsey
 * @author Robin Debreuil
 */
public class NativeMath extends ObjectBase
{
	/*
	 * Fields
	 */

	/**
	 * The environment that owns this instance
	 */
	protected Environment owningEnvironment;

	/*
	 * Properties
	 */

	/**
	 * @return The environment that owns this instance
	 */
	public Environment getOwningEnvironment()
	{
		return this.owningEnvironment;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.ObjectBase#getClassName()
	 */
	public String getClassName()
	{
		return "Math"; //$NON-NLS-1$
	}

	/*
	 * Constructors
	 */

	/**
	 * Initialize this type into the global object
	 * 
	 * @param owningEnvironment
	 *            The environment that owns this instance
	 */
	public NativeMath(Environment owningEnvironment)
	{
		this.owningEnvironment = owningEnvironment;
		int attributes = Property.DONT_DELETE | Property.DONT_ENUM;
		int fileIndex = FileContextManager.BUILT_IN_FILE_INDEX; // -1
		int offset = 0;

		// get global
		IScope global = owningEnvironment.getGlobal();

		// create Math.[[prototype]]
		this.setPrototype(global.getPropertyValue("Object", fileIndex, offset).getPropertyValue("prototype", fileIndex, //$NON-NLS-1$ //$NON-NLS-2$
				offset));

		// Add the object called Math to global
		global.putPropertyValue("Math", this, fileIndex, attributes); //$NON-NLS-1$

		// add properties to Math
		this.putPropertyValue("E", owningEnvironment.createNumber(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("LN10", owningEnvironment.createNumber(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("LN2", owningEnvironment.createNumber(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("LOG2E", owningEnvironment.createNumber(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("LOG10E", owningEnvironment.createNumber(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("PI", owningEnvironment.createNumber(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("SQRT1_2", owningEnvironment.createNumber(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("SQRT2", owningEnvironment.createNumber(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("abs", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("acos", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("asin", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("atan", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("atan2", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("ceil", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("cos", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("exp", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("floor", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("log", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("max", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("min", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("pow", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("random", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("round", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("sin", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("sqrt", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
		this.putPropertyValue("tan", owningEnvironment.createFunction(fileIndex, Range.Empty), fileIndex, attributes); //$NON-NLS-1$
	}
}
