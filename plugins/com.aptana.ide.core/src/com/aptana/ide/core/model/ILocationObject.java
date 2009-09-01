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

import java.net.URL;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public interface ILocationObject
{

	/**
	 * Gets the location of this object
	 * 
	 * @return - location url
	 */
	URL getLocation();

	/**
	 * Gets the default location of this object
	 * 
	 * @return - default url
	 */
	URL getDefaultLocation();

	/**
	 * Sets the default location of this object
	 * 
	 * @param defaultLocation
	 */
	void setDefaultLocation(URL defaultLocation);

	/**
	 * Sets the location of this object
	 * 
	 * @param location
	 */
	void setLocation(URL location);

	/**
	 * True if the object is using its default location
	 * 
	 * @return - true if using default location
	 */
	boolean hasLocation();

	/**
	 * Sets the service provider
	 * 
	 * @param provider
	 */
	void setServiceProvider(IServiceProvider provider);

	/**
	 * Gets the service provider
	 * 
	 * @return - service provider
	 */
	IServiceProvider getServiceProvider();

	/**
	 * Sets the request builder this object should to generate service requets
	 * 
	 * @param builder
	 */
	void setRequestBuilder(IServiceRequestBuilder builder);

	/**
	 * Gets the request builder this location object is using for generating requests
	 * 
	 * @return - request builder
	 */
	IServiceRequestBuilder getRequestBuilder();

	/**
	 * Gets the last error object encountered, should be cleared after each service call or set to the new errors
	 * 
	 * @return - service error chain
	 */
	ServiceErrors getLastServiceErrors();

}
