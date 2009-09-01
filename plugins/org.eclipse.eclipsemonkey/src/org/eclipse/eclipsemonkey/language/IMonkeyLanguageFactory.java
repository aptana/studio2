/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.language;

import org.eclipse.core.runtime.IPath;
import org.eclipse.eclipsemonkey.IMonkeyScriptRunner;
import org.eclipse.eclipsemonkey.ScriptMetadata;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author Paul Colton (Aptana, Inc.)
 *
 */
public interface IMonkeyLanguageFactory 
{
	/**
	 * @param pluginID
	 * @param languageName
	 */
	void init(String pluginID, String languageName);

	/**
	 * @param path
	 * @param window
	 * @return IMonkeyScriptRunner
	 */
	IMonkeyScriptRunner getRunMonkeyScript(IPath path, IWorkbenchWindow window);
	
	/**
	 * 
	 * @param contents
	 * @return ScriptMetadata
	 */
	ScriptMetadata getScriptMetadata(String contents);
}
