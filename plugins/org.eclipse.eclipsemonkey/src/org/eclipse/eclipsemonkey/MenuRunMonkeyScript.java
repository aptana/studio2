/*******************************************************************************
 * Copyright (c) 2005, 2006 Eclipse Foundation
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

import org.eclipse.core.runtime.IPath;
import org.eclipse.eclipsemonkey.language.IMonkeyLanguageFactory;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author Ingo Muschenetz
 *
 */
public class MenuRunMonkeyScript
{
	/**
	 * last_run
	 */
	public static StoredScript last_run = null;
	
	private IMonkeyScriptRunner monkeyScript = null;
	
	/**
	 * MenuRunMonkeyScript
	 * @param path
	 * @param window
	 */
	public MenuRunMonkeyScript(IPath path, IWorkbenchWindow window)
	{
		IMonkeyLanguageFactory langFactory = getLanguageFactory(path);
		
		if(langFactory != null)
		{
			monkeyScript = langFactory.getRunMonkeyScript(path, window);
		}
	}

	/**
	 * @param path
	 */
	public MenuRunMonkeyScript(IPath path)
	{
		this(path, null);
	}

	/**
	 * @param entryName
	 * @param functionArgs
	 * @return Object
	 * @throws RunMonkeyException
	 */
	public Object run(String entryName, Object[] functionArgs) throws RunMonkeyException 
	{
		return run(entryName, functionArgs, true);
	}
	
	/**
	 * @param entryName
	 * @param functionArgs
	 * @param rememberLast
	 * @return Object
	 * @throws RunMonkeyException
	 */
	public Object run(String entryName, Object[] functionArgs, boolean rememberLast) throws RunMonkeyException 
	{
		try 
		{
			if (monkeyScript != null)
			{
				return monkeyScript.run(entryName, functionArgs);
			}
			else
			{
				return null;
			}
			
		} finally {
			if(monkeyScript != null && rememberLast == true)
			{
				last_run = monkeyScript.getStoredScript();
				UpdateMonkeyActionsResourceChangeListener.createTheMonkeyMenu();
			}
		}
	}
		
	private IMonkeyLanguageFactory getLanguageFactory(IPath path)
	{
		String scriptExtension = path.getFileExtension();
		IMonkeyLanguageFactory factory = (IMonkeyLanguageFactory) EclipseMonkeyPlugin.getDefault().getLanguageStore().get(scriptExtension);
		return factory;
	}
}
