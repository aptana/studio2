/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript.doms.io;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.eclipsemonkey.lang.javascript.doms.IJavaScriptDOMFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * @author Paul Colton (Aptana, Inc.)
 *
 */
public class IODOMFactory implements IJavaScriptDOMFactory
{
	/**
	 * @see org.eclipse.eclipsemonkey.lang.javascript.doms.IJavaScriptDOMFactory#getDOMroot(org.mozilla.javascript.Scriptable)
	 */
	public Object getDOMroot(Scriptable scope) 
	{
		initIO(scope);
		
		return IO.getIO();
	}

	private void initIO(Scriptable scope) 
	{
		try {
			
			ScriptableObject.defineClass(scope, File.class);
			ScriptableObject.defineClass(scope, WebRequest.class);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
