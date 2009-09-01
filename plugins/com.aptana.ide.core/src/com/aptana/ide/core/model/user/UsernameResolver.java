/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.core.model.user;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.aptana.ide.core.StreamUtils;
import com.aptana.ide.core.model.ServiceErrors;
import com.aptana.ide.core.xpath.XPathUtils;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class UsernameResolver
{

	/**
	 * XPATH
	 */
	protected static final XPath XPATH = XPathFactory.newInstance().newXPath();

	private static final String USERNAMES = AptanaUser.BASE_URL + "/usernames?email="; //$NON-NLS-1$

	// public static void main(String args[])
	// {
	// User user = doesUsernameResolve("ksawicki@aptana.com");
	// System.out.println("User name should not be here:" + user.getUsername());
	// if (user.getLastServiceErrors() != null)
	// {
	// for (ServiceError error : user.getLastServiceErrors().getItems())
	// {
	// System.out.println("Error:" + error.getMessage());
	// }
	// }
	// user = doesUsernameResolve("davey@aptana.com");
	// System.out.println("User name should be here:" + user.getUsername());
	// if (user.getLastServiceErrors() != null)
	// {
	// for (ServiceError error : user.getLastServiceErrors().getItems())
	// {
	// System.out.println("Shouldn't be Error:" + error.toString());
	// }
	// }
	// user = doesUsernameResolve("ksawick234234234234234i@aptana.com");
	// System.out.println("User name should be here:" + user.getUsername());
	// if (user.getLastServiceErrors() != null)
	// {
	// for (ServiceError error : user.getLastServiceErrors().getItems())
	// {
	// System.out.println("Error:" + error.getMessage());
	// }
	// }
	// }

	/**
	 * Is the username available
	 * 
	 * @param email
	 * @return - true if available
	 */
	public static User doesUsernameResolve(String email)
	{
		User user = new User();
		try
		{
			URL available = new URL(USERNAMES + email);
			URLConnection connection = available.openConnection();
			if (connection instanceof HttpURLConnection)
			{
				HttpURLConnection httpConn = (HttpURLConnection) connection;
				httpConn.setRequestMethod("GET"); //$NON-NLS-1$
				String response = null;
				try
				{
					response = StreamUtils.readContent(httpConn.getInputStream(), null);
				}
				catch (IOException e)
				{
					response = StreamUtils.readContent(httpConn.getErrorStream(), null);
				}
				if (response != null)
				{
					try
					{
						if (httpConn.getResponseCode() == 200)
						{
							String username = XPATH.evaluate("/" + User.USERNAME + "/text()", XPathUtils //$NON-NLS-1$ //$NON-NLS-2$
									.createSource(response));
							if (username != null && username.length() > 0)
							{
								user.setUsername(username);
							}
						}
						else
						{
							user.setLastServiceErrors(new ServiceErrors());
							user.getLastServiceErrors().fromXML(response);
						}
					}
					catch (XPathExpressionException e)
					{
					}
				}
			}
		}
		catch (Exception e)
		{
			return user;
		}
		return user;
	}
}
