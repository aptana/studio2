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
package com.aptana.ide.scripting.parsing;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Scriptable;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.scripting.ScriptingPlugin;

/**
 * @author Kevin Lindsey
 */
public abstract class ConstantMapper extends IdScriptableObject
{
	/*
	 * Fields
	 */
	private Class _class;
	private Hashtable _fieldMap;
	private Method _getName;

	/*
	 * Properties
	 */

	/**
	 * @see org.mozilla.javascript.IdScriptableObject#getInstanceIdName(int)
	 */
	protected String getInstanceIdName(int id)
	{
		Object[] args = new Object[] { new Integer(id - 1) };
		String result = "<error>"; //$NON-NLS-1$

		try
		{
			result = (String) this._getName.invoke(this._class, args);
		}
		catch (IllegalArgumentException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ConstantMapper_Error, e);
		}
		catch (IllegalAccessException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ConstantMapper_Error, e);
		}
		catch (InvocationTargetException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ConstantMapper_Error, e);
		}

		return result;
	}

	/**
	 * @see org.mozilla.javascript.IdScriptableObject#getMaxInstanceId()
	 */
	protected abstract int getMaxInstanceId();

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of TokenTypes
	 * 
	 * @param scope
	 * @param klass
	 */
	public ConstantMapper(Scriptable scope, Class klass)
	{
		if (scope != null)
		{
			this.setParentScope(scope);
		}

		this._class = klass;
		this._fieldMap = new Hashtable();

		try
		{
			this._getName = klass.getMethod("getName", new Class[] { int.class }); //$NON-NLS-1$
		}
		catch (SecurityException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ConstantMapper_Error, e);
		}
		catch (NoSuchMethodException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ConstantMapper_Error, e);
		}
	}

	/*
	 * Methods
	 */

	/**
	 * @see org.mozilla.javascript.IdScriptableObject#getInstanceIdValue(int)
	 */
	protected Object getInstanceIdValue(int id)
	{
		return new Integer(id - 1);
	}

	/**
	 * @see org.mozilla.javascript.IdScriptableObject#findInstanceIdInfo(java.lang.String)
	 */
	protected int findInstanceIdInfo(String name)
	{
		int result = -1;

		if (this._fieldMap.containsKey(name) == false)
		{
			Field field = null;

			try
			{
				field = this._class.getField(name);
			}
			catch (SecurityException e)
			{
				IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ConstantMapper_Error, e);
			}
			catch (NoSuchFieldException e)
			{
				IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ConstantMapper_Error, e);
			}

			if (field != null)
			{
				try
				{
					result = field.getInt(this._class);
				}
				catch (IllegalArgumentException e)
				{
					IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ConstantMapper_Error, e);
				}
				catch (IllegalAccessException e)
				{
					IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ConstantMapper_Error, e);
				}
			}

			this._fieldMap.put(name, new Integer(result));
		}
		else
		{
			result = ((Integer) this._fieldMap.get(name)).intValue();
		}

		return result + 1;
	}
}
