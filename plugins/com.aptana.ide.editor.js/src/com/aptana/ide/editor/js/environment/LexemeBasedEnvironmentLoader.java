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
package com.aptana.ide.editor.js.environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.FunctionBase;
import com.aptana.ide.editor.js.runtime.IFunction;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.IScope;
import com.aptana.ide.editor.js.runtime.JSFunction;
import com.aptana.ide.editor.js.runtime.JSObject;
import com.aptana.ide.editor.js.runtime.ObjectBase;
import com.aptana.ide.editor.js.runtime.OrderedObject;
import com.aptana.ide.editor.js.runtime.OrderedObjectCollection;
import com.aptana.ide.editor.js.runtime.Property;
import com.aptana.ide.editor.js.runtime.Reference;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.parsing.IParseState;

/**
 * Populates the JavaScript environment based on the current set of lexemes. It reloads the environment when files are
 * opened or closed. This is the lexeme-based version of the parser in the pro product.
 * 
 * @author Spike Washburn
 * @author Kevin Lindsey
 */
public class LexemeBasedEnvironmentLoader
{
	private Environment _environment;
	private List<ScopeRange> _scopeList = new ArrayList<ScopeRange>();
	private IParseState _parseState;

	/**
	 * LexemeBasedEnvironmentLoader
	 * 
	 * @param env
	 */
	public LexemeBasedEnvironmentLoader(Environment env)
	{
		this._environment = env;
		
		ScopeRange sr = new ScopeRange(env.getGlobal(), 0, Integer.MAX_VALUE);
		
		this._scopeList.add(sr);
	}

	/**
	 * getFileIndex
	 * 
	 * @return int
	 */
	public int getFileIndex()
	{
		return this._parseState.getFileIndex();
	}
	
	/**
	 * addProperty
	 * 
	 * @param parentScope
	 * @param parent
	 * @param name
	 * @param offset
	 * @return IObject
	 */
	IObject addProperty(IScope parentScope, IObject parent, String name, int offset)
	{
		IObject propValue = getPropertyValue(parent, name, offset);
		
		if (propValue == ObjectBase.UNDEFINED)
		{
			propValue = createGuessedObject(new Range(offset, offset + 1));
			putPropertyValue(parent, name, propValue);
		}
	
		return propValue;
	}

	/**
	 * addVariable
	 * 
	 * @param parentScope
	 * @param name
	 * @param offset
	 * @param isVar
	 * @return IObject
	 */
	IObject addVariable(IScope parentScope, String name, int offset, boolean isVar)
	{
		IObject propValue;
		
		if (isVar)
		{
			if (name.equals("this") && parentScope.hasLocalProperty("this")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				// if this is already defined, reuse it, don't let isVar overwrite it.
				propValue = parentScope.getLocalProperty("this").getValue(getFileIndex(), offset); //$NON-NLS-1$
			}
			else
			{
				propValue = createGuessedObject(new Range(offset, offset + 1));
				putVariableValue(parentScope, name, propValue, isVar);
			}
		}
		else
		{
			propValue = getVariableValue(parentScope, name, offset);
			
			if (propValue == ObjectBase.UNDEFINED)
			{
				propValue = createGuessedObject(new Range(offset, offset + 1));
				putVariableValue(parentScope, name, propValue, isVar);
			}
		}
		
		return propValue;
	}

	/**
	 * createFunctionInstance
	 * 
	 * @param offset
	 * @param isInvoking 
	 * @return JSFunction
	 */
	JSFunction createFunctionInstance(int offset, boolean isInvoking)
	{
		JSFunction function = (JSFunction) createNewInstance("Function", offset, isInvoking); //$NON-NLS-1$
		
		return function;
	}

	/**
	 * createGuessedObject
	 * 
	 * @param range
	 * @return IObject
	 */
	private IObject createGuessedObject(Range range)
	{
		return new JSGuessedObject(range);
	}


	/**
	 * createNewInstance
	 * 
	 * @param type
	 * @param offset
	 * @param isInvoking 
	 * @return IObject
	 */
	IObject createNewInstance(String type, int offset, boolean isInvoking)
	{
		boolean needsInstance = false;
		
		if (type.charAt(0) == '+')
		{
			needsInstance = true;
			type = type.substring(1);
		}
		
		IObject currentRoot = this._environment.getGlobal();
		String[] propertyNames = type.split("\\."); //$NON-NLS-1$
		
		for (int i = 0; currentRoot != ObjectBase.UNDEFINED && i < propertyNames.length; i++)
		{
			currentRoot = currentRoot.getPropertyValue(propertyNames[i], getFileIndex(), offset);
		}
	
		if (currentRoot instanceof JSFunction && !isInvoking)
		{
			if (needsInstance)	
			{
				return createNewInstance(offset, (IFunction)currentRoot);
			}
			else
			{
				// poor mans clone
				JSFunction fn = (JSFunction) currentRoot;
				JSFunction nw = new JSFunction(new Range(offset, offset + 1));
				
				nw.setBodyScope(fn.getBodyScope());
				nw.setDocumentation(fn.getDocumentation());
				nw.setGuessedMemberObject(fn.getGuessedMemberObject());
				
				String[] names = fn.getLocalPropertyNames();
				
				for (int i = 0; i < names.length; i++)
				{
					String name = names[i];
					
					nw.putLocalProperty(name, fn.getLocalProperty(name));
				}
		
				nw.setParameterNames(fn.getParameterNames());
		
				return nw;
			}
			//return createNewInstance(offset, (IFunction)currentRoot);
		}
		else if (currentRoot instanceof IFunction)
		{
			return createNewInstance(offset, (IFunction) currentRoot);
		}
		else
		{
			// there is no function matching the specified type, so just return an empty object instance
			IObject ob = createGuessedObject(new Range(offset, offset + 1));
			
			return ob;
		}
	}

	/**
	 * createNewInstance note: this one creates instances from functions (x = new foo())
	 * 
	 * @param offset
	 * @param function
	 * @return IObject
	 */
	IObject createNewInstance(int offset, IFunction function)
	{
		IObject fnObj = function.construct(this._environment, FunctionBase.EmptyArgs, getFileIndex(), new Range(offset, offset + 1));
		return fnObj;
	}


	/**
	 * getPropertyValue
	 * 
	 * @param reference
	 * @param offset
	 * @return IObject
	 */
	IObject getPropertyValue(Reference reference, int offset)
	{
		IObject result;
		
		if (reference.getObjectBase() instanceof IScope)
		{
			result = getVariableValue((IScope) reference.getObjectBase(), reference.getPropertyName(), offset);
		}
		else
		{
			result = getPropertyValue(reference.getObjectBase(), reference.getPropertyName(), offset);
		}
		
		return result;
	}

	/**
	 * getPropertyValue
	 * 
	 * @param parent
	 * @param name
	 * @param offset
	 * @return IObject
	 */
	IObject getPropertyValue(IObject parent, String name, int offset)
	{
		IObject propValue;
		
		if (parent instanceof IScope)
		{
			throw new IllegalArgumentException(Messages.LexemeBasedEnvironmentLoader_ContCallGetPropertyValue);
		}
		else
		{
			propValue = parent.getPropertyValue(name, getFileIndex(), offset);
		}
		
		return propValue;
	}

	/**
	 * getScope
	 * 
	 * @param offset
	 * @param defaultScope
	 * @return IScope
	 */
	public IScope getScope(int offset, IScope defaultScope)
	{
		int size = this._scopeList.size();
		
		if (size == 0)
		{
			return defaultScope; // need to guard on (& figure out why) no scope before first edit
		}
	
		try
		{
			int rangeIndex = 0;
			
			for (int i = 0; i < size; i++)
			{
				ScopeRange currRange = this._scopeList.get(i);
				
				if (offset > currRange.startOffset)
				{
					rangeIndex = i;
				}
				else
				{
					break;
				}
			}
	
			for (int j = rangeIndex; j >= 0; j--)
			{
				ScopeRange range = this._scopeList.get(j);
				
				// invalid functoin end offset
				if (range.endOffset != -1 && range.endOffset > offset)
				{
					return range.scope;
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logInfo(JSPlugin.getDefault(), Messages.LexemeBasedEnvironmentLoader_GetScopeFailed, e);
		}
	
		return defaultScope;
	}

	/**
	 * getVariableValue
	 * 
	 * @param parentScope
	 * @param name
	 * @param offset
	 * @return IObject
	 */
	IObject getVariableValue(IScope parentScope, String name, int offset)
	{
		IObject propValue = parentScope.getVariableValue(name, getFileIndex(), offset);
		
		if (propValue == ObjectBase.UNDEFINED)
		{
			// The global scope is a special case since its an instance of Window too, so
			// look to see if this property is defined up the prototype chain
			propValue = this._environment.getGlobal().getPropertyValue(name, getFileIndex(), offset);
		}
	
		return propValue;
	}

	/**
	 * loadEnvironment
	 * 
	 * @param parseState
	 */
	private void loadEnvironment(IParseState parseState)
	{
		try
		{
			this._parseState = parseState;
	
			// set global as the first scope
			ScopeRange sr = new ScopeRange(this._environment.getGlobal(), 0, Integer.MAX_VALUE);
			this._scopeList.add(sr);
	
			JSLexemeListWalker walker = new JSLexemeListWalker(this._environment, this._environment.getGlobal(), this);
			walker.walkList(parseState, 0);
		}
		catch (Exception e)
		{
			IdeLog.logInfo(JSPlugin.getDefault(), Messages.LexemeBasedEnvironmentLoader_LoadEnvironmentFailed, e);
		}
	
		// sort the scope list
		Collections.sort(this._scopeList);
	}

	/**
	 * putPropertyValue
	 * 
	 * @param ref
	 * @param value
	 */
	void putPropertyValue(JSReference ref, IObject value)
	{
		IObject base = ref.getObjectBase();
		String name = ref.getPropertyName();
		if (base instanceof IScope)
		{
			// make sure the prop doesn't exist (can happen with @alias)
			IScope scope = (IScope)base;
			IObject existing = scope.getVariableValue(name, this.getFileIndex(), value.getStartingOffset());
			if(existing != null && existing.getStartingOffset() == value.getStartingOffset())
			{
				return; // this is a dup variable
			}
						
			putVariableValue(scope, name, value, ref.isVar());
		}
		else if (ref.isVar())
		{
			throw new IllegalStateException(Messages.LexemeBasedEnvironmentLoader_CantPutVarOnNonScope);
		}
		else
		{
			// make sure the prop doesn't exist (can happen with @alias)
			IObject existing = base.getPropertyValue(name, getFileIndex(), value.getStartingOffset());
			if(existing != null && existing.getStartingOffset() == value.getStartingOffset())
			{
				return; // this is a dup property
			}

			putPropertyValue(base, name, value);
		}
	}

	/**
	 * putPropertyValue
	 * 
	 * @param parentObject
	 * @param name
	 * @param value
	 */
	void putPropertyValue(IObject parentObject, String name, IObject value)
	{
		if (parentObject instanceof IScope)
		{
			throw new IllegalArgumentException(Messages.LexemeBasedEnvironmentLoader_ContCallPutPropertyValue);
		}
	
		parentObject.putPropertyValue(name, value, getFileIndex(), Property.NONE);
		Reference reference = new Reference(parentObject, name);
		Map<Object,Object> updatedProperties = this._parseState.getUpdatedProperties();
		updatedProperties.put(reference.getProperty(), reference);
	}

	/**
	 * putVariableValue
	 * 
	 * @param scope
	 * @param name
	 * @param value
	 * @param isVar
	 */
	void putVariableValue(IScope scope, String name, IObject value, boolean isVar)
	{
		boolean skipAssignment = false;
		if (isVar)
		{
			if (!scope.hasLocalProperty(name))
			{
				Property prop = new Property(value, getFileIndex(), Property.NONE);
				scope.putLocalProperty(name, prop);
			}
			else
			{
				// note: mochikit uses "var MochiKit={};"
				if (value instanceof JSObject && value.getLocalPropertyCount() == 0 && value.getPrototype() == null	&& scope.getVariable(name).hasAssignments())
				{
					// this is to skip the assignment in cases where code
					// insures a namespace by possible reassignment eg:
					// if(mochiKit == null){mochiKit = {});
					skipAssignment = true;
				}
				else
				{
					Property prop = scope.getLocalProperty(name);
					prop.setValue(value, getFileIndex());
				}
			}
		}
		else if (scope.hasVariable(name))
		{
			Property existing = scope.getVariable(name);
			if (	value instanceof JSObject && 
					value.getLocalPropertyCount() == 0 && 
					value.getPrototype() == null && 
					existing.hasAssignments())
			{
				// this is to skip the assignment in cases where code
				// insures a namespace by possible reassignment eg:
				// if(mochiKit == null){mochiKit = {});
				skipAssignment = true;
			}
			else
			{
				scope.putVariableValue(name, value, getFileIndex());
			}
		}
		else
		{
			this._environment.getGlobal().putVariableValue(name, value, getFileIndex());
		}
	
		if (!skipAssignment)
		{
			IScope owningScope = scope.getOwningScope(name);
			Reference reference = new Reference(owningScope, name);
			Map<Object,Object> updatedProperties = this._parseState.getUpdatedProperties();
			
			updatedProperties.put(reference.getProperty(), reference);
		}
	}

	/**
	 * registerScope
	 * 
	 * @param scope
	 * @param startOffset
	 * @param endOffset
	 */
	void registerScope(IScope scope, int startOffset, int endOffset)
	{
		this._scopeList.add(new ScopeRange(scope, startOffset, endOffset));
	}

	/**
	 * reloadEnvironment
	 * 
	 * @param parseState
	 */
	public void reloadEnvironment(IParseState parseState)
	{
		this._parseState = parseState;

		synchronized (this._environment)
		{
			LexemeList lexemeList = this._parseState.getLexemeList();
			
			synchronized (lexemeList)
			{
				try
				{
					unloadEnvironment();		
	
					if (this._parseState.getFileIndex() > -1)
					{
						loadEnvironment(parseState);
					}
				}
				catch (Exception e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.LexemeBasedEnvironmentLoader_ErrorReloading, e);
				}
			}
		}
	}

	/**
	 * Replaces an existing definition of a variable in a scope with a function. If the previously assigned value had
	 * been assigned sub-properties, those properties are transfered to the replacement function.
	 * 
	 * @param offset
	 * @param parentScope
	 * @param functionName
	 * @param func
	 */
	public void replaceFunctionDeclaration(int offset, IScope parentScope, String functionName, JSFunction func)
	{
		// if there is an old definition for this function, transfer any properties that were
		// previously added to the object
		if (parentScope.hasLocalProperty(functionName))
		{
			// IObject oldObject = parentScope.getPropertyValue(functionName, getFileIndex(), offset);
			Property oldProperty = parentScope.getLocalProperty(functionName);
	
			Map<Object,Object> updatedProperties = this._parseState.getUpdatedProperties();
			
			// remove all assignments that were JSObjects since they were intelliguessed.
			OrderedObjectCollection c = oldProperty.getAssignments();
			
			for (int j = 0; j < c.size(); j++)
			{
				OrderedObject obj = c.get(j);
				
				if (obj.object instanceof JSGuessedObject)
				{
					IObject oldObject = obj.object;
					String[] localProps = oldObject.getLocalPropertyNames();
					
					for (int i = 0; i < localProps.length; i++)
					{
						Property oldProp = oldObject.getLocalProperty(localProps[i]);
						
						if (!func.hasLocalProperty(localProps[i]))
						{
							func.putLocalProperty(localProps[i], oldProp);
							updatedProperties.put(oldProp, new Reference(func, localProps[i]));
						}
						else if (updatedProperties.containsKey(oldProp))
						{
							updatedProperties.remove(oldProp);
						}
					}
	
					c.remove(obj.fileIndex, obj.object.getStartingOffset());
					j--;
				}
			}
	
			// Minor optimization: now that this property no longer has assignments from this file, we can remove it
			// from it from the list of properties that were updated by this file.
			updatedProperties.remove(oldProperty);
		}
		// set the replacement function into the parent scope
		this.putVariableValue(parentScope, functionName, func, true);
	}

	/**
	 * unloadEnvironment
	 */
	public void unloadEnvironment()
	{
		// clear the current environment
		synchronized (this._environment)
		{
			if (this._parseState != null)
			{
				this._parseState.getParent().unloadFromEnvironment();

				// TODO: Remove once scope list is moved to parse state
				this._scopeList.clear();
			}
		}
	}
}
