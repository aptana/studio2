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
package com.aptana.ide.scripting.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.mozilla.javascript.ScriptableObject;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.scripting.ScriptingPlugin;

/**
 * @author Paul Colton
 */
public class WebRequest extends ScriptableObject
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = -2151860369251448749L;

	private String _method;
	private String _uri;

	/*
	 * Constructors
	 */

	/**
	 * XMLHttpRequest
	 */
	public WebRequest()
	{
		this._method = null;
		this._uri = null;
	}

	/*
	 * Methods
	 */

	/**
	 * @see org.mozilla.javascript.Scriptable#getClassName()
	 */
	public String getClassName()
	{
		return "WebRequest"; //$NON-NLS-1$
	}

	/**
	 * jsFunction_open
	 * 
	 * @param method
	 * @param uri
	 */
	public void jsFunction_open(String method, String uri)
	{
		this._method = method.toLowerCase();
		this._uri = uri;
	}

	/**
	 * jsFunction_send
	 * 
	 * @param postData
	 * @return String
	 */
	public String jsFunction_send(String postData)
	{
		URL url = null;

		try
		{
			if (_uri.startsWith("http")) //$NON-NLS-1$
			{
				url = new URL(_uri);
			}
			else
			{
				url = new URL("file://./" + _uri); //$NON-NLS-1$
			}
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.WebRequest_Error, e);
			return StringUtils.EMPTY;
		}

		try
		{
			URLConnection conn = url.openConnection();
			OutputStreamWriter wr = null;

			if (this._method.equals("post")) //$NON-NLS-1$
			{
				conn.setDoOutput(true);
				wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(postData);
				wr.flush();
			}

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null)
			{
				sb.append(line + "\r\n"); //$NON-NLS-1$
			}

			if (wr != null)
			{
				wr.close();
			}

			rd.close();

			String result = sb.toString();
			
			return result;

		}
		catch (Exception e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.WebRequest_Error, e);
			return StringUtils.EMPTY;
		}
	}
}
