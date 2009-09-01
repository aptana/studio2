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
public interface IScope extends IObject
{
	/**
	 * Get the Property object for the associated variable name
	 * 
	 * @param variableName
	 *            The name of the Property to return
	 * @return The Property object associated with the specified name
	 */
	Property getVariable(String variableName);

	/**
	 * Get the named variable. If necessary, the current scope chain will be traversed until either the scope is null or
	 * the identifier is located. This implements [[Scope]] from the ECMA specification
	 * 
	 * @param variableName
	 *            The name of the variable to lookup in the current scope
	 * @param fileIndex
	 *            The file index
	 * @param offset
	 *            The file offset
	 * @return The value of the specified variable name
	 */
	IObject getVariableValue(String variableName, int fileIndex, int offset);

	/**
	 * Determine if the specified variable name is in the current scope chain
	 * 
	 * @param variableName
	 *            The name of the variable to look up
	 * @return Returns true if the variable exists in this scope object or in any of its ancestor scope objects
	 */
	boolean hasVariable(String variableName);

	/**
	 * Return all variable names in this scope and in all parent scopes.
	 * 
	 * @return An array of variable names
	 */
	String[] getVariableNames();

	/**
	 * Get all variable names that exist on this scope only (does not follow scope chain)
	 * 
	 * @return An array of variable names
	 */
	String[] getLocalVariableNames();

	/**
	 * Get a reference object that points to the specified property name
	 * 
	 * @param variableName
	 *            The name of the property to refer to
	 * @return Returns a reference object that points to the specified property name
	 */
	Reference getVariableReference(String variableName);

	/**
	 * Set the named variable to the specified value. The scope chain will be traversed to locate
	 * 
	 * @param variableName
	 *            The name of the variable to set
	 * @param value
	 *            The value of the variable
	 * @param fileIndex
	 *            The file index
	 */
	void putVariableValue(String variableName, IObject value, int fileIndex);

	/**
	 * Remove a reference to the specified variable name. If the variable no longer has references, it will be removed
	 * from this scope
	 * 
	 * @param variableName
	 *            The name of the variable to unput
	 */
	void unputVariableName(String variableName);

	/**
	 * Returns the IScope object that holds the specified variable name
	 * 
	 * @param variableName
	 *            The name of the variable to look up
	 * @return Returns the IScope object that contains the specified variable name
	 */
	IScope getOwningScope(String variableName);

	/**
	 * Returns the parent scope of this object.
	 * 
	 * @return This object's parent scope
	 */
	IScope getParentScope();

	/**
	 * Sets the parent scope of this object
	 * 
	 * @param parentScope
	 *            The new parent scope
	 */
	void setParentScope(IScope parentScope);

	/**
	 * Gets the enclosing function of this object, or null if global
	 * @return JSFunction
	 */
	IFunction getEnclosingFunction();
	/**
	 * Sets the enclosing function of this object, or null if global
	 * @param enclosingFunction 
	 */
	void setEnclosingFunction(IFunction enclosingFunction);

}