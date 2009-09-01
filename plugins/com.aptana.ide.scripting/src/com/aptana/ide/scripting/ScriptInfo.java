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
package com.aptana.ide.scripting;

import java.io.File;
import java.util.ArrayList;

import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

/**
 * @author Kevin Lindsey
 */
public class ScriptInfo
{
	/*
	 * Fields
	 */
	private ArrayList _scripts;
	private String _id;
	private File _file;
	private long _lastModification;
	private Scriptable _scope;
	private ScriptClassLoader _classLoader;

	/*
	 * Properties
	 */

	/**
	 * getClassLoader
	 * 
	 * @return ScriptClassLoader
	 */
	public ScriptClassLoader getClassLoader()
	{
		return this._classLoader;
	}

	/**
	 * get script
	 * 
	 * @return Script
	 */
	public Script[] getScripts()
	{
		return (Script[]) this._scripts.toArray(new Script[0]);
	}

	/**
	 * get id
	 * 
	 * @return String
	 */
	public String getId()
	{
		return this._id;
	}

	/**
	 * get file
	 * 
	 * @return File
	 */
	public File getFile()
	{
		return this._file;
	}

	/**
	 * get scope
	 * 
	 * @return Scriptable
	 */
	public Scriptable getScope()
	{
		return this._scope;
	}

	/**
	 * Determine if this script info needs to be refreshed
	 * 
	 * @return Returns true if the file in this script info has a different modification value than the one it had when
	 *         this info was created
	 */
	public boolean needsRefresh()
	{
		return (this._file.lastModified() != this._lastModification);
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of ScriptInfo
	 * 
	 * @param script
	 * @param id
	 * @param file
	 * @param scope
	 */
	public ScriptInfo(Script script, String id, File file, Scriptable scope)
	{
		this._scripts = new ArrayList();
		this._id = id;
		this._file = file;
		this._scope = scope;
		this._classLoader = new ScriptClassLoader();

		this._lastModification = file.lastModified();

		this.addScript(script);
	}

	/*
	 * Methods
	 */

	/**
	 * addScript
	 * 
	 * @param script
	 */
	public void addScript(Script script)
	{
		this._scripts.add(script);
	}
}
