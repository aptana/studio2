/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript.doms.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.mozilla.javascript.ScriptableObject;

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
			System.err.println("Error: " + e); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
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

			return sb.toString();

		}
		catch (Exception e)
		{
			System.err.println("Error: " + e); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}
}

