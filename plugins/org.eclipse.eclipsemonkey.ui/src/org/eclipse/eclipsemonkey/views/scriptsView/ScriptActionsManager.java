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
import java.util.HashMap;

import org.eclipse.eclipsemonkey.StoredScript;

/**
 * @author Paul Colton
 */
public class ScriptActionsManager
{
	/**
	 * instance
	 */
	public static ScriptActionsManager instance = null;

	/*
	 * Fields
	 */
	private ArrayList _listeners = new ArrayList();
	private ArrayList _scriptActions = new ArrayList();
	private HashMap _scriptActionSets = new HashMap();

	/**
	 * Constructor
	 */
	protected ScriptActionsManager() {
	}

	/*
	 * Properties
	 */

	/**
	 * getInstance
	 * 
	 * @return ScriptActionsManager
	 */
	public static ScriptActionsManager getInstance() {
		if (instance == null) {
			instance = new ScriptActionsManager();
		}
		return instance;
	}

	/**
	 * getScriptActionSet
	 * 
	 * @param name
	 * @return ScriptActionSet
	 */
	public ScriptActionSet getScriptActionSet(String name) {
		return (ScriptActionSet) this._scriptActionSets.get(name);
	}

	/**
	 * getAll
	 * 
	 * @return IScriptAction[]
	 */
	public IScriptAction[] getAll() {
		ScriptAction[] actions = getScriptActions();
		ScriptActionSet[] sets = getScriptActionSets();
		IScriptAction[] results = new IScriptAction[actions.length + sets.length];

		int index = 0;

		for (int i = 0; i < actions.length; i++) {
			results[index++] = actions[i];
		}

		for (int i = 0; i < sets.length; i++) {
			results[index++] = sets[i];
		}

		return results;
	}
	
	/*
	 * Methods
	 */

	/**
	 * fireScriptActionsChangeEvent
	 * 
	 * @param a
	 */
	public void fireScriptActionsChangeEvent(IScriptAction a) {
		for (int i = 0; i < this._listeners.size(); i++) {
			IScriptActionChangeListener listener = (IScriptActionChangeListener) this._listeners
					.get(i);
			listener.onScriptActionChanged(a);
		}
	}

	/**
	 * addScriptActionsChangeListener
	 * 
	 * @param l
	 */
	public void addScriptActionsChangeListener(IScriptActionChangeListener l) {
		this._listeners.add(l);
	}

	/**
	 * removeScriptActionsChangeListener
	 * 
	 * @param l
	 */
	public void removeScriptActionsChangeListener(IScriptActionChangeListener l) {
		this._listeners.remove(l);
	}

	/**
	 * addScriptAction
	 * 
	 * @param name
	 * @param script 
	 * @return Action
	 */
	public ScriptAction addScriptAction(String name, StoredScript script) {
		
		ScriptAction a = findScriptAction(name);
		
		if (a != null)
		{
			return a;
		}
		
		a = new ScriptAction(name, script);

		this._scriptActions.add(a);
		fireScriptActionsChangeEvent(a);

		return a;
	}

	/**
	 * removeScriptActionSet
	 * @param a ScriptAction
	 */
	public void removeScriptAction(ScriptAction a) {
		if (this._scriptActions.contains(a)) {
			this._scriptActions.remove(a);
			fireScriptActionsChangeEvent(a);
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
	 * createActionSet
	 * 
	 * @param name
	 * @return ScriptActionSet
	 */
	public ScriptActionSet createScriptActionSet(String name) {
		ScriptActionSet a;

		if (this._scriptActionSets.containsKey(name)) {
			a = (ScriptActionSet) this._scriptActionSets.get(name);
		} else {
			a = new ScriptActionSet(name);
			this._scriptActionSets.put(name, a);
			fireScriptActionsChangeEvent(a);
		}

		return a;
	}

	/**
	 * removeScriptActionSet
	 * 
	 * @param name
	 */
	public void removeScriptActionSet(String name) {
		ScriptActionSet a;

		if (this._scriptActionSets.containsKey(name)) {
			a = (ScriptActionSet) this._scriptActionSets.remove(name);
			fireScriptActionsChangeEvent(a);
		}
	}

	/**
	 * getScriptActions
	 * 
	 * @return ScriptAction[]
	 */
	public ScriptAction[] getScriptActions()
	{
		return (ScriptAction[]) this._scriptActions.toArray(new ScriptAction[0]);
	}
	
	/**
	 * getScriptActionSets
	 * 
	 * @return ScriptActionSet[]
	 */
	public ScriptActionSet[] getScriptActionSets() {
		return (ScriptActionSet[]) this._scriptActionSets.values()
				.toArray(new ScriptActionSet[0]);
	}
	
	/**
	 * clearAll
	 */
	public void clearAll()
	{
		this._scriptActions = new ArrayList();
		this._scriptActionSets = new HashMap();
	}
}
