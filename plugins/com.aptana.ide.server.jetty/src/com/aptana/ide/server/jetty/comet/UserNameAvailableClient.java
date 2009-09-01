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
package com.aptana.ide.server.jetty.comet;

import java.util.HashMap;
import java.util.Map;

import com.aptana.ide.core.model.ServiceErrors;
import com.aptana.ide.core.model.user.User;
import com.aptana.ide.core.model.user.UsernameValidAndAvailable;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class UserNameAvailableClient extends CometResponderClient
{

	/**
	 * AVAILABLE_CHANNEL
	 */
	public static final String AVAILABLE_CHANNEL = "/portal/user/available"; //$NON-NLS-1$

	/**
	 * CHECK_USER_NAME
	 */
	public static final String CHECK_USER_NAME = "checkUserName"; //$NON-NLS-1$

	/**
	 * USER_NAME
	 */
	public static final String USER_NAME = "userName"; //$NON-NLS-1$

	/**
	 * NAME_USED
	 */
	public static final String NAME_USED = "nameUsed"; //$NON-NLS-1$

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (AVAILABLE_CHANNEL.equals(toChannel) && request instanceof Map)
		{
			Map requestData = (Map) request;
			if (CHECK_USER_NAME.equals(requestData.get(CometConstants.REQUEST)) && requestData.containsKey(USER_NAME))
			{
				Object name = requestData.get(USER_NAME);
				if (name instanceof String)
				{
					User user = UsernameValidAndAvailable.doesUsernameResolve((String) name);
					Map<Object, Object> response = new HashMap<Object, Object>();
					response.put(CometConstants.RESPONSE, CHECK_USER_NAME);
					if (user != null)
					{
						response.put(User.USERNAME, user.getUsername());
						ServiceErrors errors = user.getLastServiceErrors();
						if (errors != null)
						{
							response.put(ServiceErrors.ERRORS_ELEMENT, errors.getErrorStrings());
						}
					}
					else
					{
						response.put(User.USERNAME, null);
					}
					return response;
				}
			}
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getSubscriptionIDs()
	 */
	protected String[] getSubscriptionIDs()
	{
		return new String[] { AVAILABLE_CHANNEL };
	}

}
