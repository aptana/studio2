/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript.doms.resources;

import org.eclipse.eclipsemonkey.lang.javascript.doms.IJavaScriptDOMFactory;
import org.mozilla.javascript.Scriptable;

/**
 * @author Paul Colton
 *
 */
public class ResourcesDOMFactory implements IJavaScriptDOMFactory {

	/**
	 * @see org.eclipse.eclipsemonkey.lang.javascript.doms.IJavaScriptDOMFactory#getDOMroot(org.mozilla.javascript.Scriptable)
	 */
	public Object getDOMroot(Scriptable scope) 
	{
		return new Resources(scope);
	}

}
