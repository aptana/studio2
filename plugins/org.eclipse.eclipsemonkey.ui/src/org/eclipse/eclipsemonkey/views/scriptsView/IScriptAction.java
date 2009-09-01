/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.views.scriptsView;

import org.eclipse.eclipsemonkey.StoredScript;

/**
 * @author Paul Colton
 */
public interface IScriptAction
{
	/**
	 * getName
	 *
	 * @return String
	 */
	String getName();

	/**
	 * setName
	 * 
	 * @param name
	 */
	void setName(String name);
	
	/**
	 * getStoredScript
	 * 
	 * @return StoredScript
	 */
	StoredScript getStoredScript();
	
	/**
	 * setStoredScript
	 * 
	 * @param s
	 */
	void setStoredScript(StoredScript s);
}
