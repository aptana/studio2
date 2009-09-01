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
package com.aptana.ide.scripting;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;

/**
 * @author Paul Colton
 */
public class ScriptThread implements Runnable
{
	/*
	 * Fields
	 */
	private Scriptable _scope;
	private Object[] _args;
	private Object _callback;
	private Object _result;
	private ScriptClassLoader _classLoader;

	/**
	 * getResult
	 * 
	 * @return Object
	 */
	public Object getResult()
	{
		return _result;
	}

	/**
	 * ScriptThread
	 * 
	 * @param scope
	 * @param callback
	 * @param args
	 */
	public ScriptThread(Scriptable scope, Object callback, Object[] args)
	{
		this(scope, callback, args, null);
	}
	
	/**
	 * ScriptThread
	 * 
	 * @param scope
	 * @param callback
	 * @param args
	 * @param classLoader
	 */
	public ScriptThread(Scriptable scope, Object callback, Object[] args, ScriptClassLoader classLoader)
	{
		this._scope = scope;
		this._callback = callback;
		this._args = args;
		this._classLoader = classLoader;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		Context cx = Context.enter();

		try
		{
			if (this._callback instanceof Function)
			{
				ClassLoader oldClassLoader = null;
				
				if (this._classLoader != null)
				{
					oldClassLoader = cx.getApplicationClassLoader();
					
					cx.setApplicationClassLoader(this._classLoader);
				}
				
				Function f = (Function) this._callback;
				
				try
				{
					this._result = f.call(cx, this._scope, this._scope, this._args);
				}
				finally
				{
					if (this._classLoader != null)
					{
						cx.setApplicationClassLoader(oldClassLoader);
					}
				}
			}
			else
			{
				String message = StringUtils.format(Messages.ScriptThread_Callback_Does_Not_Exist, this._callback);
				
				IdeLog.logError(ScriptingPlugin.getDefault(), message);
				
				return;
			}
		}
		finally
		{
			Context.exit();
		}
	}
}
