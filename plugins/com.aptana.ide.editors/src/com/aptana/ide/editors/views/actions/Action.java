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

/**
 * @author Paul Colton
 */
public class Action implements IAction
{
	/*
	 * Fields
	 */
	private String _name;
	private String _filePath;
	private String _scriptPath;
	private ActionSet _parent;
	private String _toolTipText;

	/*
	 * Constructors
	 */

	/**
	 * Action
	 * 
	 * @param name
	 * @param filename 
	 */
	public Action(String name, String filename)
	{
		this(null, name, filename);
	}

	/**
	 * Action
	 * 
	 * @param parent
	 * @param name
	 * @param filename 
	 */
	public Action(ActionSet parent, String name, String filename)
	{
		this(parent, name, filename, filename);
	}
	
	/**
	 * Action
	 *
	 * @param parent
	 * @param name
	 * @param filename
	 * @param scriptName
	 */
	public Action(ActionSet parent, String name, String filename, String scriptName)
	{
		this._parent = parent;
		this._name = name;
		this._filePath = filename;
		this._scriptPath = scriptName;
	}

	/*
	 * Properties
	 */

	/**
	 * getFilePath
	 * 
	 * @return String
	 */
	public String getFilePath()
	{
		return this._filePath;
	}
	
	/**
	 * getScriptPath
	 * 
	 * @return String
	 */
	public String getScriptPath()
	{
		return this._scriptPath;
	}
	
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
	public ActionSet getParent()
	{
		return this._parent;
	}
	
	/**
	 * @see com.aptana.ide.editors.views.actions.IAction#getPath()
	 */
	public String getPath()
	{
		String result;
		ActionSet parent = this.getParent();
		
		if (parent != null)
		{
			result = parent.getPath() + this.getName();
		}
		else
		{
			result = this.getName();
		}
		
		return result;
	}
	
	/**
	 * @see com.aptana.ide.editors.views.actions.IAction#getToolTipText()
	 */
	public String getToolTipText()
	{
		return this._toolTipText;
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
}
