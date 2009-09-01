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

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * @author Paul Colton
 */
public class ActionSet implements IAction
{
	/*
	 * Fields
	 */
	private String _name;
	private ArrayList _actions = new ArrayList();
	private boolean _executable = false;
	private String _toolTipText;

	/*
	 * Constructors
	 */

	/**
	 * ActionSet
	 * 
	 * @param name
	 */
	public ActionSet(String name)
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
	 * @see com.aptana.ide.editors.views.actions.IAction#getPath()
	 */
	public String getPath()
	{
		return "/" + this.getName() + "/"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * @see com.aptana.ide.editors.views.actions.IAction#getToolTipText()
	 */
	public String getToolTipText()
	{
		String result = this._toolTipText;
		
		if (result == null)
		{
			result = StringUtils.format(Messages.ActionSet_ActionSetActions, new Object[]{this.getName(), Integer.toString(this.getActions().length)}); 
		}
		
		return result;
	}
	
	/**
	 * setToolTipText
	 *
	 * @param text
	 */
	public void setToolTipText(String text)
	{
		this._toolTipText = text;
	}

	/**
	 * getActionCount
	 * 
	 * @return int
	 */
	public int getActionCount()
	{
		return this._actions.size();
	}

	/**
	 * getActions
	 * 
	 * @return Action[]
	 */
	public Action[] getActions()
	{
		return (Action[]) this._actions.toArray(new Action[0]);
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
		Action a = new Action(this, name, filename);

		this._actions.add(a);
		UnifiedEditorsPlugin.getDefault().getActionsManager().fireActionsChangeEvent(a);

		return a;
	}
	
	/**
	 * addAction
	 * 
	 * @param name
	 * @param filename 
	 * @param scriptName 
	 * @return Action
	 */
	public Action addAction(String name, String filename, String scriptName)
	{
		Action a = new Action(this, name, filename, scriptName);

		this._actions.add(a);
		UnifiedEditorsPlugin.getDefault().getActionsManager().fireActionsChangeEvent(a);

		return a;
	}

	/**
	 * removeAction
	 * 
	 * @param name
	 */
	public void removeAction(String name)
	{
		for (int i = 0; i < this._actions.size(); i++)
		{
			Action a = (Action) this._actions.get(i);
			
			if (a.getName().equals(name))
			{
				this._actions.remove(i);
				UnifiedEditorsPlugin.getDefault().getActionsManager().fireActionsChangeEvent(a);
			}
		}
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
	 * @see com.aptana.ide.editors.views.actions.IAction#getFilePath()
	 */
	public String getFilePath()
	{
		return null;
	}
	
	/**
	 * @see com.aptana.ide.editors.views.actions.IAction#getScriptPath()
	 */
	public String getScriptPath()
	{
		return null;
	}
}
