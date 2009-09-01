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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * @author Kevin Lindsey
 */
public class JavaScriptPrintStream extends ScriptableObject
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = -4338424230536797200L;
	private java.io.PrintStream _stream;

	/*
	 * Properties
	 */

	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return "PrintStream"; //$NON-NLS-1$
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of PrintStream
	 * 
	 * @param scope
	 * @param stream
	 *            The underlying stream to wrap
	 */
	public JavaScriptPrintStream(Scriptable scope, java.io.PrintStream stream)
	{
		super();

		this.setParentScope(scope);
		this._stream = stream;

		this
				.defineFunctionProperties(new String[] { "print", "println" }, JavaScriptPrintStream.class, //$NON-NLS-1$ //$NON-NLS-2$
						ScriptableObject.READONLY);
	}

	/*
	 * Methods
	 */

	/**
	 * print
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 */
	public static void print(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		JavaScriptPrintStream instance = (JavaScriptPrintStream) thisObj;

		for (int i = 0; i < args.length; i++)
		{
			String arg = Context.toString(args[0]);

			instance._stream.print(arg);
		}
	}

	/**
	 * println
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 */
	public static void println(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		JavaScriptPrintStream instance = (JavaScriptPrintStream) thisObj;

		for (int i = 0; i < args.length; i++)
		{
			String arg = Context.toString(args[0]);

			instance._stream.println(arg);
		}
	}
}

