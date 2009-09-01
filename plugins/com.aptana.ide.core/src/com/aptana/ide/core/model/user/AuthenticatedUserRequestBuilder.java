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

import com.aptana.ide.core.model.AuthUtils;
import com.aptana.ide.core.model.ILocationObject;
import com.aptana.ide.core.model.IServiceRequest;
import com.aptana.ide.core.model.IServiceRequestBuilder;
import com.aptana.ide.core.model.ITransformObject;
import com.aptana.ide.core.model.XMLServiceRequest;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class AuthenticatedUserRequestBuilder implements IServiceRequestBuilder
{

	private User user;
	private UserRequestBuilder builder;

	/**
	 * Creates a new cloud request builder
	 */
	public AuthenticatedUserRequestBuilder()
	{
		builder = new UserRequestBuilder();
	}

	/**
	 * @see com.aptana.ide.core.model.IServiceRequestBuilder#generateRequest(com.aptana.ide.core.model.ILocationObject,
	 *      java.lang.String)
	 */
	public IServiceRequest generateRequest(ILocationObject object, String type)
	{
		XMLServiceRequest request = null;
		if (object == null || object.getLocation() == null)
		{
			return request;
		}
		if (object instanceof ITransformObject)
		{
			if (IServiceRequest.COMMIT.equals(type) || IServiceRequest.DELETE.equals(type))
			{
				request = new XMLServiceRequest(((ITransformObject) object).toXML());
			}
			else
			{
				request = new XMLServiceRequest(""); //$NON-NLS-1$
			}
			request.setRequestType(type);
			if (object instanceof User)
			{
				return builder.generateRequest(object, type);
			}
			else if (user != null)
			{
				request.setAuthentication(AuthUtils.getAuthorizationHeader(user.getUsername(), user.getPassword()));
			}
		}
		return request;
	}

	/**
	 * @return the user
	 */
	public User getUser()
	{
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user)
	{
		this.user = user;
	}

}
