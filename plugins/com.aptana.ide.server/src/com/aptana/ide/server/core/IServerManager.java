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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Pavel Petrochenko
 */
public interface IServerManager
{

	/**
	 * Adds the given server manager listener to manager. Once registered, a listener starts receiving notification of
	 * state changes to this server. The listener continues to receive notifications until it is removed. Has no effect
	 * if an identical listener is already registered.
	 * 
	 * @param listener
	 */
	void addServerManagerListener(IServerManagerListener listener);

	/**
	 * Removes the given server manager listener from server manager. Has no effect if the listener is not registered.
	 * 
	 * @param listener
	 */
	void removeServerManagerListener(IServerManagerListener listener);

	/**
	 * @return all known servers
	 */
	IServer[] getServers();

	/**
	 * @return all known server types
	 */
	IServerType[] getServerTypes();

	/**
	 * @return all known server types
	 */
	IModuleType[] getModuleTypes();

	/**
	 * @return all known server types
	 */
	IServerLocator[] getServerLocators();
	
	/**
	 * @return all known publish operations
	 */
	IPublishOperation[] getPublishOperations();
	
	
	/**
	 * @param server 
	 * @param type 
	 * @param id 
	 * @return all known publish operations that may be used for a given server type and give operation id
	 */
	IPublishOperation getPublishOperation(IServerType server,IModuleType type,String id);
	

	/**
	 * @param project
	 * @return set of servers related to a given project
	 */
	IServer[] getServers(IProject project);

	/**
	 * @param server
	 * @return set of projects related to a given server
	 */
	IProject[] getProjects(IServer server);

	/**
	 * @param project
	 * @return
	 */
	IModule[] getModules(IProject project);

	/**
	 * @param server
	 * @throws CoreException
	 */
	void addServer(IServer server) throws CoreException;
	
	/**
	 * Finds the server with that id
	 *
	 * @param id The id of the server to find
	 * @return the server matching that id, or null if not found
	 */
	IServer findServer(String id);

	/**
	 * 
	 * @param configuration 
	 * @return created server 
	 * @throws CoreException if something goes wrong
	 */
	IServer addServer(IAbstractConfiguration configuration) throws CoreException;
	

	/**
	 * @param server
	 * @throws CoreException
	 */
	void removeServer(IServer server) throws CoreException;

	/**
	 * @param server
	 * @return true if server manager knows about this server
	 */
	boolean exists(IServer server);
	
	/**
	 * @param serverTypeId
	 * @return default configuration for a server with a given type id
	 */
	IAbstractConfiguration getInitialServerConfiguration(String serverTypeId);

	/**
	 * @param id
	 * @return server type with a given id
	 */
	IServerType getServerType(String id);
	
	
}