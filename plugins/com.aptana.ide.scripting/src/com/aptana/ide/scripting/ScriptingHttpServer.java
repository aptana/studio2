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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.http.HttpServer;

/**
 * @author Kevin Lindsey
 */
public class ScriptingHttpServer extends HttpServer
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = 3197904170876835706L;
	private ScriptingEngine _scriptingEngine;
	private String _rootPath;

	/*
	 * Properties
	 */

	/**
	 * getGlobal
	 * 
	 * @return ServerGlobal
	 */
	public Global getGlobal()
	{
		return this._scriptingEngine.getGlobal();
	}

	/**
	 * getRootPath
	 * 
	 * @return String
	 */
	public String getRootPath()
	{
		return this._rootPath;
	}

	/*
	 * Constructors
	 */

	/**
	 * ScriptingHttpServer
	 * 
	 * @param scriptingEngine
	 * @param path
	 * @param port
	 * @param endPort
	 */
	public ScriptingHttpServer(ScriptingEngine scriptingEngine, String path, int port, int endPort)
	{
		super(new ScriptingResourceResolver(new File(path)), port, endPort, 750);

		this._rootPath = path;

		// associate this server with the resolver
		((ScriptingResourceResolver) getResourceResolver()).setServer(this);

		// make sure we have a running scripting engine
		this._scriptingEngine = scriptingEngine;
	}

	/*
	 * Methods
	 */

	/**
	 * Create a new scripting environment for a web application
	 * 
	 * @param uri
	 * @return id
	 */
	public String createScriptEnvironment(String uri)
	{
		Context cx = Context.enter();
		Global global = this.getGlobal();

		// build script's local global scope
		Scriptable scope = cx.newObject(global);
		scope.setPrototype(global);
		scope.setParentScope(null);

		// compile
		Script script = cx.compileString(StringUtils.EMPTY, uri, 1, null);
		// script.exec(cx, scope);

		Context.exit();

		return global.storeScriptState(uri, script, scope);
	}

	/**
	 * runScript
	 * 
	 * @param uri
	 * @param source
	 */
	public void include(String uri, String source)
	{
		Context cx = Context.enter();
		Global global = this.getGlobal();
		String id = global.getXrefId(uri);

		try
		{
			if (id != null && global.hasScriptInfo(id))
			{
				ScriptInfo info = global.getScriptInfo(id);
				Scriptable scope = info.getScope();

				// compile the script
				Script script = cx.compileString(source, uri, 1, null);

				// add script to info
				info.addScript(script);

				// exec
				script.exec(cx, scope);
			}
		}
		finally
		{
			Context.exit();
		}
	}

	/**
	 * Remove the scripting environment for the specified uri
	 * 
	 * @param uri
	 */
	public void removeScriptEnvironment(String uri)
	{
		Global global = this.getGlobal();
		String id = global.getXrefId(uri);

		if (id != null && global.hasScriptInfo(id))
		{
			ScriptInfo info = global.getScriptInfo(id);
			Scriptable scope = info.getScope();
			Script[] scripts = info.getScripts();

			for (int i = 0; i < scripts.length; i++)
			{
				Object onunload = scope.get("onunload", scope); //$NON-NLS-1$

				if (onunload instanceof Function)
				{
					Function unloadFunction = (Function) onunload;

					Context cx = Context.enter();
					unloadFunction.call(cx, scope, scope, new Object[0]);
					Context.exit();
				}
			}

			global.removeScriptInfo(id);
		}
	}
}
