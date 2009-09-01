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

import org.eclipse.core.resources.IProject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * 
 * @author Paul Colton
 *
 */
public class Resources extends ScriptableObject
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = 5439070601529120100L;
	
	/**
	 * 
	 */
	
	private org.eclipse.eclipsemonkey.doms.resources.Resources _resources;
	
	/*
	 * Properties
	 */

	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return "Resources"; //$NON-NLS-1$
	}

	/**
	 * Constructors
	 */
	public Resources()
	{
		System.err.println("Shouldn't be used directly."); //$NON-NLS-1$
	}

	/**
	 * Create a new instance of Views
	 * 
	 * @param scope
	 */
	public Resources(Scriptable scope)
	{
		this.setParentScope(scope);

		_resources = new org.eclipse.eclipsemonkey.doms.resources.Resources();
		
		String[] names = new String[] { 
				"filesMatching", //$NON-NLS-1$
				"filesMatchingIgnoreCase", //$NON-NLS-1$
				"filesMatchingForProject", //$NON-NLS-1$
				"filesMatchingForProjectIgnoreCase" //$NON-NLS-1$
				};

		this.defineFunctionProperties(names, Resources.class, PERMANENT | READONLY);

//		this.defineProperty("all", Resources.class, PERMANENT | READONLY);
//		this.defineProperty("activeEditor", Resources.class, PERMANENT | READONLY);
	}
	
	/**
	 * @param patternString
	 * @return filesMatching
	 */
	public Object[] filesMatching(String patternString)
	{
		return _resources.filesMatching(patternString);
	}
	
	public Object[] filesMatchingIgnoreCase(String patternString)
	{
		return _resources.filesMatchingIgnoreCase(patternString);
	}

	public Object[] filesMatchingForProject(String project, String patternString)
	{
		return _resources.filesMatchingForProject(project, patternString);
	}
	
	public Object[] filesMatchingForProjectIgnoreCase(String project, String patternString)
	{
		return _resources.filesMatchingForProjectIgnoreCase(project, patternString);
	}
	
}
