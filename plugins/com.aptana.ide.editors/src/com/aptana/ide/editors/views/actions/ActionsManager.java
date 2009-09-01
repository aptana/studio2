/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.editors.views.actions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Paul Colton
 */
public class ActionsManager
{
	/*
	 * Fields
	 */
	private ArrayList _listeners = new ArrayList();
	private ArrayList _actions = new ArrayList();
	private HashMap _actionSets = new HashMap();

	/*
	 * Properties
	 */

	/**
	 * getActionSet
	 * 
	 * @param name
	 * @return ActionSet
	 */
	public ActionSet getActionSet(String name)
	{
		return (ActionSet) this._actionSets.get(name);
	}

	/**
	 * getAll
	 * 
	 * @return IAction[]
	 */
	public IAction[] getAll()
	{
		Action[] actions = (Action[]) this._actions.toArray(new Action[0]);
		ActionSet[] sets = getActionSets();
		IAction[] results = new IAction[actions.length + sets.length];

		int index = 0;

		for (int i = 0; i < actions.length; i++)
		{
			results[index++] = actions[i];
		}

		for (int i = 0; i < sets.length; i++)
		{
			results[index++] = sets[i];
		}

		return results;
	}

	/*
	 * Methods
	 */

	/**
	 * fireActionsChangeEvent
	 * 
	 * @param a
	 */
	public void fireActionsChangeEvent(IAction a)
	{
		for (int i = 0; i < this._listeners.size(); i++)
		{
			IActionChangeListener listener = (IActionChangeListener) this._listeners.get(i);
			listener.onActionChanged(a);
		}
	}

	/**
	 * addActionsChangeListener
	 * 
	 * @param l
	 */
	public void addActionsChangeListener(IActionChangeListener l)
	{
		this._listeners.add(l);
	}

	/**
	 * removeActionsChangeListener
	 * 
	 * @param l
	 */
	public void removeActionsChangeListener(IActionChangeListener l)
	{
		this._listeners.remove(l);
	}

	/**
	 * addAction
	 * 
	 * @param name
	 * @param filename 
	 * @return Action
	 */
	public Action addAction(String name, String filename)
	{
		Action a = new Action(name, filename);
		
		this._actions.add(a);
		fireActionsChangeEvent(a);
		
		return a;
	}

	/**
	 * createActionSet
	 * 
	 * @param name
	 * @return ActionSet
	 */
	public ActionSet createActionSet(String name)
	{
		ActionSet a;
		
		if (this._actionSets.containsKey(name))
		{
			a = (ActionSet) this._actionSets.get(name);
		}
		else
		{
			a = new ActionSet(name);
			this._actionSets.put(name, a);
			fireActionsChangeEvent(a);
		}

		return a;
	}

	/**
	 * removeActionSet
	 * 
	 * @param name
	 */
	public void removeActionSet(String name)
	{
		ActionSet a;
		
		if (this._actionSets.containsKey(name))
		{
			a = (ActionSet) this._actionSets.remove(name);
			fireActionsChangeEvent(a);
		}
	}

	/**
	 * getActionSets
	 * 
	 * @return ActionSet[]
	 */
	public ActionSet[] getActionSets()
	{
		return (ActionSet[]) this._actionSets.values().toArray(new ActionSet[0]);
	}
}
