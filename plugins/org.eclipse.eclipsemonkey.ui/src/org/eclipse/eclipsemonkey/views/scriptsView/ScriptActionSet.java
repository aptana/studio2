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

import java.util.ArrayList;

import org.eclipse.eclipsemonkey.StoredScript;

/**
 * @author Paul Colton
 */
public class ScriptActionSet implements IScriptAction
{
	/*
	 * Fields
	 */
	private String _name;
	private ArrayList _scriptActions = new ArrayList();
	private boolean _executable = false;

	/*
	 * Constructors
	 */

	/**
	 * ActionSet
	 * 
	 * @param name
	 */
	public ScriptActionSet(String name)
	{
		this._name = name;
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
	 * @return String
	 */
	public String getPath()
	{
		return "/" + this.getName() + "/"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * getActionCount
	 * 
	 * @return int
	 */
	public int getActionCount()
	{
		return this._scriptActions.size();
	}

	/**
	 * getActions
	 * 
	 * @return Action[]
	 */
	public ScriptAction[] getScriptActions()
	{
		return (ScriptAction[]) this._scriptActions.toArray(new ScriptAction[0]);
	}

	/**
	 * addScriptAction
	 * 
	 * @param name
	 * @param script 
	 * @return Action
	 */
	public ScriptAction addScriptAction(String name, StoredScript script)
	{
		ScriptAction a = findScriptAction(name);
		
		if (a != null)
		{
			return a;
		}
		
		a = new ScriptAction(this, name, script);

		this._scriptActions.add(a);
		ScriptActionsManager.getInstance().fireScriptActionsChangeEvent(a);

		return a;
	}
	
	/**
	 * removeScriptAction
	 * 
	 * @param name
	 */
	public void removeScriptAction(String name)
	{
		for (int i = 0; i < this._scriptActions.size(); i++)
		{
			ScriptAction a = (ScriptAction) this._scriptActions.get(i);
			
			if (a.getName().equals(name))
			{
				this._scriptActions.remove(i);
				ScriptActionsManager.getInstance().fireScriptActionsChangeEvent(a);
			}
		}
	}

	/**
	 * findScriptAction
	 * 
	 * @param name
	 * @return ScriptAction
	 */
	public ScriptAction findScriptAction(String name)
	{
		for (int i = 0; i < this._scriptActions.size(); i++)
		{
			ScriptAction a = (ScriptAction) this._scriptActions.get(i);
			
			if (a.getName().equals(name))
			{
				return a;
			}
		}
		
		return null;
	}
	
	/**
	 * isExecutable
	 * 
	 * @return boolean
	 */
	public boolean isExecutable()
	{
		return _executable;
	}

	/**
	 * setExecutable
	 * 
	 * @param b
	 */
	public void setExecutable(boolean b)
	{
		this._executable = b;
	}

	/**
	 * getStoredScript
	 * 
	 * @return StoredScript
	 */
	public StoredScript getStoredScript()
	{
		return null;
	}

	/**
	 * setStoredScript
	 * 
	 * @param s
	 */
	public void setStoredScript(StoredScript s)
	{
	}
}
