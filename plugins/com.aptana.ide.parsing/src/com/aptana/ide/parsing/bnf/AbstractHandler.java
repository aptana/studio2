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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.parsing.bnf;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.ParsingPlugin;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public abstract class AbstractHandler implements IReductionHandler
{
	private Stack<Object> _values = new Stack<Object>();
	private Map<String, Method> _methods = new HashMap<String, Method>();
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#afterParse(com.aptana.ide.parsing.IParseState, com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void afterParse(IParseState parseState, IParseNode parentNode)
	{
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#beforeParse(com.aptana.ide.parsing.IParseState, com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void beforeParse(IParseState parseState, IParseNode parentNode)
	{
		this._values.clear();
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#getValues()
	 */
	public Object[] getValues()
	{
		return this._values.toArray();
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#push(Object)
	 */
	public void push(Object value)
	{
		this._values.push(value);
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#reduce(ReductionContext)
	 */
	public void reduce(IReductionContext context)
	{
		Method method = null;
		Object result = null;
		
		// grab the action name for the matching rule
		String actionName = context.getAction();

		// look up a method for that action either from the cache or from this class
		if (actionName != null && actionName.length() > 0)
		{
			// calculate method name
			String methodName = "on" + actionName; //$NON-NLS-1$
			
			// Try to grab a cached method or add one to the cache
			if (this._methods.containsKey(methodName) == false)
			{
				try
				{
					method = this.getClass().getMethod(methodName, new Class[] { IReductionContext.class });
				}
				catch (SecurityException e)
				{
					ParsingPlugin.logError("Accessing method " + methodName + " failed with a security violation", e); //$NON-NLS-1$ //$NON-NLS-2$
				}
				catch (NoSuchMethodException e)
				{
					ParsingPlugin.logError("Method " + methodName + " does not exist", e); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				// NOTE: method may still be null. Storing that null will prevent endless
				// lookups for methods that do not exist every time this method is called
				this._methods.put(methodName, method);
			}
			else
			{
				// NOTE: may still be null
				method = this._methods.get(methodName);
			}
		}

		// collect the matching rule's items from the stack
		int count = context.getNodeCount();
		Object[] nodes = new Object[count];
		
		for (int i = 0; i < count; i++)
		{
			nodes[count - i - 1] = this._values.pop();
		}
		
		// store the matching items within the reduction context for the handler method's use
		context.setNodes(nodes);
		
		// if the method exists, then invoke it; otherwise, assume the top-most object
		// is the return value. Using the top-most object is effectively a convenience
		// for passing through results without having to define a separate action on
		// all rules needing that functionality
		if (method != null)
		{
			try
			{
				result = method.invoke(this, new Object[] { context });
			}
			catch (Exception e)
			{
				ParsingPlugin.logError("Method for action '" + actionName + "' encountered an error during invocation", e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		else
		{
			// We should always have nodes, but let's be safe just in case
			if (nodes.length > 0)
			{
				result = nodes[0];
			}
		}

		// push the results onto our stack
		this._values.push(result);
	}
}
