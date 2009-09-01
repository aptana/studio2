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
package com.aptana.ide.server.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kevin Lindsey
 */
public class RequestLineParser
{
	/*
	 * Fields
	 */
	private static Pattern HTTP_PATTERN = Pattern.compile("(GET|HEAD|POST)\\s(/[^\\?]*)(.*)\\sHTTP/1\\.[0-9]"); //$NON-NLS-1$

	private String _method;
	private String _uri;
	private String _queryString;
	private Hashtable _keyValuePairs;

	/*
	 * Properties
	 */

	/**
	 * The method in this request
	 * 
	 * @return String
	 */
	public String getMethod()
	{
		return this._method;
	}

	/**
	 * The current Uri
	 * 
	 * @return String
	 */
	public String getUri()
	{
		return this._uri;
	}

	/**
	 * The current query string
	 * 
	 * @return String
	 */
	public String getQueryString()
	{
		return this._queryString;
	}

	/**
	 * getKeyValue
	 * 
	 * @param name
	 * @return String
	 */
	public String getKeyValue(String name)
	{
		return (String) _keyValuePairs.get(name);
	}

	/**
	 * hasKey
	 * 
	 * @param name
	 * @return boolean
	 */
	public boolean hasKey(String name)
	{
		return _keyValuePairs.containsKey(name);
	}

	/**
	 * getKeys
	 * 
	 * @return String[]
	 */
	public String[] getKeys()
	{
		return (String[]) _keyValuePairs.keySet().toArray(new String[0]);
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of RequestLineParser
	 * 
	 * @param reqLine
	 */
	RequestLineParser(String header)
	{
		if (header != null && header.length() > 0)
		{
			_keyValuePairs = new Hashtable();

			String reqLine = header.substring(0, header.indexOf("\r\n")); //$NON-NLS-1$
			
			Matcher matcher = HTTP_PATTERN.matcher(reqLine);

			if (matcher.matches())
			{
				int groupCount = matcher.groupCount();

				if (groupCount >= 1)
				{
					this._method = matcher.group(1).toUpperCase();
				}
				if (groupCount >= 2)
				{
					this._uri = matcher.group(2);
					this._uri = getUrlDecodedValue(_uri);
				}
				if (groupCount == 3)
				{
					this._queryString = matcher.group(3);
					// trim off the preceding ? char
					if (this._queryString.length() > 0)
					{
						this._queryString = this._queryString.substring(1);
						this._queryString = getUrlDecodedValue(this._queryString);
					}

					parseQueryString(this._queryString);
				}
			}
		}
	}

	/*
	 * Methods
	 */

	private void parseQueryString(String queryString)
	{
		if (queryString == null || this._queryString.length() == 0)
		{
			return;
		}

		String[] pairs = queryString.split("&"); //$NON-NLS-1$

		for (int i = 0; i < pairs.length; i++)
		{
			String[] nameValue = pairs[i].split("="); //$NON-NLS-1$
			String name = nameValue[0];
			
			if (name != null && name.length() > 0 )
			{
				_keyValuePairs.put(name, nameValue.length > 1 ? nameValue[1] : ""); //$NON-NLS-1$
			}
		}
	}

	/**
	 * getUrlDecodedValue
	 * 
	 * @param s
	 */
	private String getUrlDecodedValue(String s)
	{
		try
		{
			return URLDecoder.decode(s, "UTF-8"); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			try
			{
				return URLDecoder.decode(s, "ASCII"); //$NON-NLS-1$
			}
			catch (UnsupportedEncodingException e1)
			{
				return s;
			}
		}
	}
}
