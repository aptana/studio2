/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript;

import org.eclipse.eclipsemonkey.utils.StringUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 * @author Paul Colton
 */
public class JavaScriptThread implements Runnable
{
	/*
	 * Fields
	 */
	private Scriptable _scope;
	private Object[] _args;
	private Object _callback;
	private Object _result;
	private JavaScriptClassLoader _classLoader;

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
	public JavaScriptThread(Scriptable scope, Object callback, Object[] args)
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
	public JavaScriptThread(Scriptable scope, Object callback, Object[] args, JavaScriptClassLoader classLoader)
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
				String message = StringUtils.format("ScriptThread_Callback_Does_Not_Exist: {0}", this._callback); //$NON-NLS-1$
				
				System.err.println(message);
				
				return;
			}
		}
		finally
		{
			Context.exit();
		}
	}
}

