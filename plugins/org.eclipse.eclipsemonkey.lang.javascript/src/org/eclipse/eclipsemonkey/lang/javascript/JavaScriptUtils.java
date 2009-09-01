/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript;

import java.lang.reflect.Method;

import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.ScriptableObject;

/**
 * @author Kevin Lindsey
 */
public final class JavaScriptUtils
{
	/**
	 * JavaScriptUtils
	 */
	private JavaScriptUtils()
	{
		// make this class publicly un-instantiable
	}

	/**
	 * Apply a collection of function property names to the specified target object. This is primarily used to add new
	 * function properties to the JavaScript global
	 * 
	 * @param functionNames
	 *            The names of the functions to apply to the target object. This method will throw an
	 *            IllegalArgumentException if this parameter is null
	 * @param functionProvider
	 *            The scriptable object that contains the function implementations. This method will throw an
	 *            IllegalArgumentException if this parameter is null
	 * @param attributes
	 *            The property attributes to apply for each function property
	 * @param target
	 *            The scriptable object to which the function properties will be added. This method will throw an
	 *            IllegalArgumentException if this parameter is null
	 * @throws IllegalArgumentException
	 */
	public static void defineFunctionProperties(String[] functionNames, ScriptableObject functionProvider, int attributes, ScriptableObject target)
	{
		if (functionNames == null)
		{
			throw new IllegalArgumentException("functionNames must be defined"); //$NON-NLS-1$
		}
		if (functionProvider == null)
		{
			throw new IllegalArgumentException("functionProvider must be defined"); //$NON-NLS-1$
		}
		if (target == null)
		{
			throw new IllegalArgumentException("target must be defined"); //$NON-NLS-1$
		}

		Method methods[] = functionProvider.getClass().getDeclaredMethods();

		// add each function name
		for (int i = 0; i < functionNames.length; i++)
		{
			String functionName = functionNames[i];

			// silently ignore nulls and empty strings
			if (functionName != null && functionName.length() > 0)
			{
				// find the method for the current function name
				for (int j = 0; j < methods.length; j++)
				{
					Method currentMethod = methods[j];

					if (currentMethod.getName().equals(functionName))
					{
						// create function object for this method
						FunctionObject function = new FunctionObject(functionName, currentMethod, functionProvider);

						// add function property to target object
						target.defineProperty(functionName, function, attributes);
					}
				}
			}
		}
	}
}
