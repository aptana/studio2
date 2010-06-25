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
package com.aptana.ide.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

/**
 * @author Pavel Petrochenko
 */
public interface IServerType extends IAdaptable
{

	/**
	 * Returns the id of this server type. Each known server type has a distinct id. Ids are intended to be used
	 * internally as keys; they are not intended to be shown to end users.
	 * 
	 * @return the server type id
	 */
	String getId();

	/**
	 * Returns the displayable name for this server type.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 * 
	 * @return a displayable name for this server type
	 */
	String getName();
	
	/**
	 * Returns the displayable category name for this server type. This is more generic than the "type"
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 * 
	 * @return a displayable name for this server category
	 */
	String getCategory();

	/**
	 * Returns the displayable description for this server type.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 * 
	 * @return a displayable description for this server type
	 */
	String getDescription();

	/**
	 * @param launchMode
	 * @return true if supported
	 */
	boolean supportsLaunchMode(String launchMode);

	/**
	 * @param launchMode
	 * @return true if supported
	 */
	boolean supportsRestart(String launchMode);

	/**
	 * @param launchMode
	 * @return true if supported
	 */
	boolean supportsPublish();

	/**
	 * Returns whether this type of server requires a server configuration.
	 * 
	 * @return <code>true</code> if this type of server requires a server configuration, and <code>false</code> if
	 *         it does not
	 */
	boolean hasServerConfiguration();

	/**
	 * @param configuration
	 * @return - created server
	 * @throws CoreException
	 */
	IServer create(IAbstractConfiguration configuration) throws CoreException;
	
	/**
	 * Returns whether this type of server is an external one (like Apache or XAMPP)
	 * @return <code>true</code> if this type of server is external.
	 */
	boolean isExternal();
}