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
package com.aptana.ide.core.model;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public interface IServiceResponse
{

	/**
	 * STATUS_UNKNOWN_HOST
	 */
	public static final int STATUS_UNKNOWN_HOST = -200;

	/**
	 * STATUS_UNSET
	 */
	public static final int STATUS_UNSET = -1;

	/**
	 * STATUS_OK
	 */
	public static final int STATUS_OK = 200;

	/**
	 * STATUS_CREATED
	 */
	public static final int STATUS_CREATED = 201;

	/**
	 * STATUS_DELETED
	 */
	public static final int STATUS_DELETED = 204;

	/**
	 * STATUS_UNAUTHORIZED
	 */
	public static final int STATUS_UNAUTHORIZED = 401;

	/**
	 * Gets the data object generated from the string response
	 * 
	 * @return - data object
	 */
	Object getData();

	/**
	 * Gets the contents of the cloud response
	 * 
	 * @return - response text
	 */
	String getContents();

	/**
	 * Gets the content type of the cloud response
	 * 
	 * @return - content type of the response
	 */
	String getContentType();

	/**
	 * Gets the status of the cloud response
	 * 
	 * @return - response code
	 */
	int getStatus();

}
