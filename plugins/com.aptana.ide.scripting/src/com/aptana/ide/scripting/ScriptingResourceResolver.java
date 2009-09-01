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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.mozilla.javascript.Undefined;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.http.HttpServer;
import com.aptana.ide.server.http.RequestLineParser;
import com.aptana.ide.server.resolvers.IHttpResourceResolver;
import com.aptana.ide.server.resources.IHttpResource;

/**
 * @author Kevin Lindsey
 */
public class ScriptingResourceResolver implements IHttpResourceResolver
{
	/*
	 * Fields
	 */
	private File _rootDirectory;
	private ScriptingHttpServer _server;

	/*
	 * Properties
	 */

	/**
	 * @param server
	 */
	public void setServer(ScriptingHttpServer server)
	{
		this._server = server;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of ScriptingResourceResolver
	 * 
	 * @param dir
	 */
	public ScriptingResourceResolver(File dir)
	{
		this._rootDirectory = dir;
	}

	/*
	 * Methods
	 */

	/**
	 * getResource
	 * 
	 * @param requestLine
	 * @return IHttpResource
	 */
	public IHttpResource getResource(RequestLineParser requestLine)
	{
		IHttpResource result = null;

		String uri = requestLine.getUri();

		if (uri.startsWith("/")) //$NON-NLS-1$
		{
			uri = uri.substring(1);
		}

		File file = new File(this._rootDirectory, uri.replace('/', File.separatorChar));

		if (requestLine.hasKey("action")) //$NON-NLS-1$
		{
			String path = null;

			try
			{
				path = file.getCanonicalPath();
			}
			catch (IOException e)
			{
				IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ScriptingResourceResolver_Error, e);
			}

			String id = this._server.getGlobal().getXrefId(path);
			String scriptThreadResultString = StringUtils.EMPTY;

			if (id != null)
			{
				ScriptInfo info = this._server.getGlobal().getScriptInfo(id);

				if (info != null)
				{
					String func = requestLine.getKeyValue("action"); //$NON-NLS-1$
					Object funcObj = info.getScope().get(func, info.getScope());
					String data = requestLine.getKeyValue("data"); //$NON-NLS-1$

					// Note: we are not running this thread, only using its
					// run() method synchronously
					ScriptThread scriptThread = new ScriptThread(info.getScope(), funcObj, new Object[] { data });
					scriptThread.run();
					Object scriptThreadResult = scriptThread.getResult();

					if (scriptThreadResult != null && (scriptThreadResult instanceof Undefined) == false)
					{
						scriptThreadResultString = scriptThreadResult.toString();
					}
				}
				else
				{
					scriptThreadResultString = "<<InvalidScriptIdException>>"; //$NON-NLS-1$
				}
			}
			else
			{
				scriptThreadResultString = "<<InvalidScriptIdException>>"; //$NON-NLS-1$
			}

			final String scriptResult = scriptThreadResultString;

			result = new IHttpResource()
			{
				public InputStream getContentInputStream(HttpServer server) throws IOException
				{
					return new ByteArrayInputStream(scriptResult.getBytes("UTF-8")); //$NON-NLS-1$
				}

				public long getContentLength()
				{
					try
					{
						return scriptResult.getBytes("UTF-8").length; //$NON-NLS-1$
					}
					catch (UnsupportedEncodingException e)
					{
						IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ScriptingResourceResolver_Error, e);
						return 0;
					}
				}

				public String getContentType()
				{
					return "text/plain"; //$NON-NLS-1$
				}
			};
		}
		else if (file.exists())
		{
			result = new ScriptingHttpResource(file);
		}

		return result;
	}
}
