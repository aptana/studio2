/*******************************************************************************
 * Copyright (c) 2006 Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bjorn Freeman-Benson - initial implementation
 *     Ward Cunningham - initial implementation
 *******************************************************************************/

package org.eclipse.eclipsemonkey;

import java.util.HashMap;

import org.eclipse.core.runtime.IPath;

/**
 * StoredScript
 */
public class StoredScript
{
	/**
	 * 
	 */
	public IPath scriptPath;
	/**
	 * 
	 */
	public ScriptMetadata metadata;
	/**
	 * 
	 */
	public HashMap<String, Object> extra = new HashMap<String, Object>();
}
