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
public class ScriptAction implements IScriptAction
{
	/*
	 * Fields
	 */
	private String _name;
	private StoredScript _storedScript;
	private ScriptActionSet _parent;

	/*
	 * Constructors
	 */

	/**
	 * ScriptAction
	 * 
	 * @param name
	 * @param script 
	 */
	public ScriptAction(String name, StoredScript script)
	{
		this(null, name, script);
	}

	/**
	 * ScriptAction
	 *
	 * @param parent
	 * @param name
	 * @param script 
	 */
	public ScriptAction(ScriptActionSet parent, String name, StoredScript script)
	{
		this._parent = parent;
		this._name = name;
		this._storedScript = script;
	}

	/*
	 * Properties
	 */

	/**
	 * setName
	 * 
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/**
	 * getName
	 * 
	 * @return String
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * getParent
	 * 
	 * @return ActionSet
	 */
	public ScriptActionSet getParent()
	{
		return this._parent;
	}

	/**
	 * setStoredScript
	 * @param s StoredScript
	 */
	public void setStoredScript(StoredScript s)
	{
		_storedScript = s;
	}
	
	/**
	 * getStoredScript
	 * 
	 * @return StoredScript
	 */
	public StoredScript getStoredScript()
	{
		return _storedScript;
	}
}
