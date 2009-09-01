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

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;



/**
 * @author Kevin Lindsey
 * @author Robin Debreuil
 */
public class JSScope extends ObjectBase implements IScope
{
	private IFunction enclosingFunction;
	/*
	 * Fields
	 */

	/**
	 * This scope's parent scope
	 */
	protected IScope _parentScope;

	/*
	 * Properties
	 */

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getClassName()
	 */
	public String getClassName()
	{
		return "Scope"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#getLocalVariableNames()
	 */
	public String[] getLocalVariableNames()
	{
		return this.getLocalPropertyNames();
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#getParentScope()
	 */
	public IScope getParentScope()
	{
		return this._parentScope;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#setParentScope(com.aptana.ide.editor.js.runtime.IScope)
	 */
	public void setParentScope(IScope parentScope)
	{
		this._parentScope = parentScope;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#getOwningScope(java.lang.String)
	 */
	public IScope getOwningScope(String variableName)
	{
		IScope current = this;
		IScope result = null;

		while (current != null)
		{
			if (current.hasLocalProperty(variableName))
			{
				// get the property for this variable
				result = current;

				break;
			}

			current = current.getParentScope();
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#hasVariable(java.lang.String)
	 */
	public boolean hasVariable(String variableName)
	{
		Property property = this.getVariable(variableName);

		return (property != null);
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#getVariableValue(java.lang.String, int, int)
	 */
	public IObject getVariableValue(String variableName, int fileIndex, int offset)
	{
		Property property = this.getVariable(variableName);

		return (property != null) ? property.getValue(fileIndex, offset) : ObjectBase.UNDEFINED;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#getVariable(java.lang.String)
	 */
	public Property getVariable(String variableName)
	{
		IScope current = this;
		Property result = null;

		while (current != null)
		{
			if (current.hasLocalProperty(variableName))
			{
				// get the property for this variable
				result = current.getLocalProperty(variableName);

				break;
			}

			current = current.getParentScope();
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#getVariableReference(java.lang.String)
	 */
	public Reference getVariableReference(String variableName)
	{
		return new Reference(this.getOwningScope(variableName), variableName);
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#getVariableNames()
	 */
	public String[] getVariableNames()
	{
		IScope current = this;
		Map<String, String> result = new Hashtable<String, String>();

		while (current != null)
		{
			String[] localNames = current.getLocalPropertyNames();

			for (int i = 0; i < localNames.length; i++)
			{
				result.put(localNames[i], ""); //$NON-NLS-1$
			}

			current = current.getParentScope();
		}

		Set<String> s = result.keySet();

		return s.toArray(new String[s.size()]);
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#putVariableValue(java.lang.String, com.aptana.ide.editor.js.runtime.IObject, int)
	 */
	public void putVariableValue(String variableName, IObject value, int fileIndex)
	{
		Property property = this.getVariable(variableName);

		if (property != null)
		{
			if (property.isReadOnly() == false)
			{
				property.setValue(value, fileIndex);
			}
		}
		else
		{
			// put on this scope
			this.putPropertyValue(variableName, value, fileIndex);
		}
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#unputVariableName(java.lang.String)
	 */
	public void unputVariableName(String variableName)
	{
		IScope current = this;

		while (current != null)
		{
			if (current.hasLocalProperty(variableName))
			{
				// get the property
				Property p = current.getLocalProperty(variableName);

				// decrease reference count
				if (p.removeReference() == 0)
				{
					current.deletePropertyName(variableName);
				}

				break;
			}

			current = current.getParentScope();
		}
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#getEnclosingFunction()
	 */
	public IFunction getEnclosingFunction()
	{
		return enclosingFunction;
	}
	/**
	 * @see com.aptana.ide.editor.js.runtime.IScope#setEnclosingFunction(com.aptana.ide.editor.js.runtime.IFunction)
	 */
	public void setEnclosingFunction(IFunction enclosingFunction)
	{
		this.enclosingFunction = enclosingFunction;
	}
}





