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

import java.util.Map;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public interface IServiceRequest
{

	/**
	 * COMMIT
	 */
	public static final String COMMIT = "PUT"; //$NON-NLS-1$

	/**
	 * UPLOAD
	 */
	public static final String UPLOAD = "POST"; //$NON-NLS-1$

	/**
	 * UPDATE
	 */
	public static final String UPDATE = "GET"; //$NON-NLS-1$

	/**
	 * DELETE
	 */
	public static final String DELETE = "DELETE"; //$NON-NLS-1$

	/**
	 * Gets the content of the request
	 * 
	 * @return - contents of the request
	 */
	String getContents();

	/**
	 * Gets the content type of the request
	 * 
	 * @return - content type of request body
	 */
	String getContentType();

	/**
	 * Gets the desired return content type for the response. Typically this will be identical to content type of
	 * request body
	 * 
	 * @return - desired response content type
	 */
	String getAccept();

	/**
	 * Gets the request method for this cloud request
	 * 
	 * @return - method
	 */
	String getRequestType();

	/**
	 * True if this request contains a body and thus requires transmission of data to the site provider
	 * 
	 * @return - true is contains a message body
	 */
	boolean containsBody();

	/**
	 * Gets the authentication header for this request if it has one
	 * 
	 * @return - auth header or null for it to not be set
	 */
	String getAuthentication();
	
	/**
	 * Returns a map of property name/value pairs.
	 * 
	 * @return
	 */
	Map<String, String> getRequestProperties();

}
