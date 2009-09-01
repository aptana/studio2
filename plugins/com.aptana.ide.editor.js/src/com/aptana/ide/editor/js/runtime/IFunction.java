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

/**
 * @author Kevin Lindsey
 */
public interface IFunction
{
	/**
	 * Return the scope that is in affect within
	 * 
	 * @return Returns the IFunction body's scope
	 */
	IScope getBodyScope();

	/**
	 * Set the scope to be used in this IFunction's body
	 * 
	 * @param scope
	 *            The new scope to use in this function's body
	 */
	void setBodyScope(IScope scope);

	/**
	 * Return a string array of the parameters for this IFunction
	 * 
	 * @return Returns a string array of parameter names
	 */
	String[] getParameterNames();

	/**
	 * Set the parameter names used to define this IFunction
	 * 
	 * @param parameters
	 *            A string array of parameters names
	 */
	void setParameterNames(String[] parameters);

	/**
	 * Get this functions invoke type provider
	 * 
	 * @return Returns the associated type provider or null
	 */
	IInvokeTypeProvider getInvokeTypeProvider();

	/**
	 * Set a type provider to be used to determine this function's return type when this function is not documented
	 * 
	 * @param typeProvider
	 *            The type provider
	 */
	void setInvokeTypeProvider(IInvokeTypeProvider typeProvider);
	
	/**
	 * Constructs a new Object with using the passed arguments.
	 * 
	 * @param environment
	 * @param arguments
	 *            The arguments to use in constructing the new object.
	 * @param fileIndex
	 *            The file index
	 * @param sourceRegion
	 *            The region of text within the source file that represents the construction of this instance
	 * @return Returns a newly constructed object.
	 */
	IObject construct(Environment environment, IObject[] arguments, int fileIndex, IRange sourceRegion);

	/**
	 * Invoke this method with the specified parameters
	 * 
	 * @param environment
	 * @param arguments
	 *            The arguments to use in constructing the new object.
	 * @param fileIndex
	 *            The file index
	 * @param sourceRegion
	 *            The region of text within the source file that represents the construction of this instance
	 * @return Returns a newly constructed object.
	 */
	IObject invoke(Environment environment, IObject[] arguments, int fileIndex, IRange sourceRegion);
}