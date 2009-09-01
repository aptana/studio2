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
import org.eclipse.core.runtime.IProgressMonitor;


/**
 * @author Pavel Petrochenko
 *
 */
public interface IServerLocator
{
	
	/**
	 * A callback listener interface.
	 */
	public interface IServerSearchListener {
		/**
		 * Called when a new server is found by the locator.
		 * The server must never be null.
		 * 
		 * @param server the runtime that was found.
		 */
		void serverFound(IServer server);
	}

	/**
	 * Returns the id of this server locator.
	 * Each known server locator has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the server locator id
	 */
	String getId();

	/**
	 * Returns true if the server locator can find servers of the given type.
	 * The id should never be null.
	 * 
	 * @param serverTypeId the id of a server type
	 * @return boolean
	 */
	boolean supportsType(String serverTypeId);
	
	/**
	 * Returns <code>true</code> if this type of server can run on a remote host.
	 * Returns <code>false</code> if the server type can only be run on "localhost"
	 * (the local machine).
	 * 
	 * @return <code>true</code> if this type of server can run on
	 *    a remote host, and <code>false</code> if it cannot
	 */
	boolean supportsRemoteHosts();

	/**
	 * Searches for servers. 
	 * It uses the callback listener to report servers that are found.
	 * 
	 * @param host a host string conforming to RFC 2732
	 * @param listener a listener to report status to
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException
	 */
	void searchForServers(String host, IServerSearchListener listener, IProgressMonitor monitor) throws CoreException;
}
