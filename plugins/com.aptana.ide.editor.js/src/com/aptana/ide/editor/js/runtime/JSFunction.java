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

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.scriptdoc.parsing.FunctionDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.PropertyDocumentation;
import com.aptana.ide.lexer.IRange;
import com.aptana.ide.metadata.IDocumentation;

/**
 * @author Robin Debreuil
 * @author Kevin Lindsey
 */
public class JSFunction extends FunctionBase
{
	/*
	 * Fields
	 */
	private String[] _parameters;
	private IScope _bodyScope;
	private JSObject memberObject;

	/*
	 * Properties
	 */

	/**
	 * Get the scope used for the body of this function
	 * 
	 * @return This function body's scope object
	 */
	public IScope getBodyScope()
	{
		return this._bodyScope;
	}

	/**
	 * Set the scope used for the body of this function
	 * 
	 * @param scope
	 *            The new function body scope
	 */
	public void setBodyScope(IScope scope)
	{
		this._bodyScope = scope;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.FunctionBase#getClassName()
	 */
	public String getClassName()
	{
		return "Function"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IFunction#setParameterNames(java.lang.String[])
	 */
	public void setParameterNames(String[] parameters)
	{
		this._parameters = parameters;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of JSFunction
	 */
	public JSFunction()
	{
		this(null);
	}

	/**
	 * Create a new instance of JSFunction
	 * 
	 * @param sourceRegion
	 *            The region in the source file that defines this object instance
	 */
	public JSFunction(IRange sourceRegion)
	{
		super(sourceRegion);

		this._bodyScope = new JSScope();
		this._bodyScope.setEnclosingFunction(this);
	}

	/*
	 * IFunction implementation
	 */

	/**
	 * @see com.aptana.ide.editor.js.runtime.FunctionBase#getParameterNames()
	 */
	public String[] getParameterNames()
	{
		return this._parameters;
	}

	/**
	 * Creates an instance of this native type from a function instance, and sets the appropriate prototype for it.
	 * 
	 * @param environment
	 *            The environment
	 * @param arguments
	 *            The arguments to use in constructing the new object.
	 * @param fileIndex
	 *            The file index
	 * @param sourceRegion
	 *            The region of source code that is constructing this new instance
	 * @return Returns a JSObject instance of this type.
	 */
	public IObject construct(Environment environment, IObject[] arguments, int fileIndex, IRange sourceRegion)
	{
		int offset = sourceRegion.getStartingOffset();

		// Create new NativeObject
		IObject instance = environment.createObject(fileIndex, sourceRegion);

		// get prototype
		IObject prototype = this.getPropertyValue("prototype", fileIndex, offset); //$NON-NLS-1$

		// Point its [[proto]] to Object's prototypeInstance
		instance.setPrototype(prototype);

		// add properties in "this" to instance
		IObject thisInstance = this._bodyScope.getPropertyValue("this", fileIndex, offset); //$NON-NLS-1$
		String[] thisNames = thisInstance.getLocalPropertyNames();

		for (int i = 0; i < thisNames.length; i++)
		{
			String name = thisNames[i];

			instance.putPropertyValue(name, thisInstance.getPropertyValue(name, fileIndex, offset), fileIndex);
		}

		return instance;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.FunctionBase#invoke(com.aptana.ide.editor.js.runtime.Environment, com.aptana.ide.editor.js.runtime.IObject[], int,
	 *      IRange)
	 */
	public IObject invoke(Environment environment, IObject[] arguments, int fileIndex, IRange sourceRegion)
	{
		// TODO: We may want to change the logic in JSFunction.invoke to fall back to type inferencing for the return
		// type if the function has documents but no return type. Currently, we will return JSUndefined in that case.

		IObject result = ObjectBase.UNDEFINED;
		int offset = sourceRegion.getStartingOffset();

		if (this.hasDocumentation())
		{
			FunctionDocumentation functionDocs = (FunctionDocumentation) this.getDocumentation();
			String[] types = functionDocs.getReturn().getTypes();
			IScope scope = environment.getGlobal();

			if (types.length > 0)
			{
				String typeName = types[0];

				if (scope.hasVariable(typeName))
				{
					IObject instance = scope.getVariableValue(typeName, fileIndex, offset);

					if (instance instanceof IFunction)
					{
						IFunction constructor = (IFunction) instance;

						result = constructor.construct(environment, new IObject[0], fileIndex, sourceRegion);
					}
					else
					{
						IdeLog.logInfo(JSPlugin.getDefault(), Messages.JSFunction_TryingToConstructWithNonFunction + typeName);
					}
				}
			}
		}

		if (result == ObjectBase.UNDEFINED)
		{
			IInvokeTypeProvider typeProvider = this.getInvokeTypeProvider();
			
			if (typeProvider != null)
			{
				result = typeProvider.getInvokeReturnType(environment, arguments, fileIndex, sourceRegion);
			}
		}

		return result;
	}

	/**
	 * Set the list of parameters for this function
	 * 
	 * @param parameters
	 */
	public void setParameters(String[] parameters)
	{
		this._parameters = parameters;
	}

	/**
	 * @return Returns the dotted name of the class that this function belongs to (based on docs), or null.
	 */
	public String getMemberOf()
	{
		String result = null; // null by default unless doc'ed
		
		IDocumentation doc = this.getDocumentation();
		if(doc != null && doc instanceof PropertyDocumentation)
		{
			PropertyDocumentation pdoc = (PropertyDocumentation) doc;
			String[] rettypes = pdoc.getMemberOf().getTypes();
			if (rettypes.length > 0)
			{
				result = rettypes[0];
			}
		}
		return result;
	}

	/**
	 * Gets the guessed object that is the prototype for this member. 
	 * This can be assigned to when seeing patterns such as xx.prototype.fn(){}. 
	 * Docs (@memberOf) will always override this guess.
	 * @return Gets the guessed object that is the prototype for this member. 
	 */
	public JSObject getGuessedMemberObject()
	{
		return this.memberObject;
	}
	/**	 
	 * Sets the guessed object that is the prototype for this member. 
	 * This can be assigned to when seeing patterns such as xx.prototype.fn(){}. 
	 * Docs (@memberOf) will always override this guess.
	 * @param classObject
	 */
	public void setGuessedMemberObject(JSObject classObject)
	{
		this.memberObject = classObject;
	}
}
