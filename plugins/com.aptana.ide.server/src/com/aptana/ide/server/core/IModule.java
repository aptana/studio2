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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

/**
 * @author Pavel Petrochenko 
 * 
 * A module is a unit of "content" that can be published to a server.
 * All modules have a module type, which is fixed for the lifetime of the module. The set of module types (or
 * "kinds") is open-ended.
 *  
 * All modules are created by module factories using the moduleFactories extension point.
 */
public interface IModule extends IAdaptable
{
	
	
	/**
	 * KEY_PATH
	 */
	String KEY_PATH="path"; //$NON-NLS-1$
	
	/**
	 * KEY_PROJECT
	 */
	String KEY_PROJECT="project"; //$NON-NLS-1$

	/**
	 * Returns the name for this module.
	 * <p>
	 * 
	 * @return a name for this module
	 */
	String getName();

	/**
	 * @return type of this module
	 */
	IModuleType getType();
	
	/**
	 * @return set of resources that are part of this module
	 */
	IModuleResource[] getRootResources();

	/**
	 * Returns the workspace project that this module is contained in, or null if the module is outside of the
	 * workspace.
	 * 
	 * @return a project
	 */
	IProject getProject();
	
	/**
	 * @return path in the workspace or in the filesystem to the root of module
	 */
	IPath getPath();

	/**
	 * Returns <code>true</code> if the module is an external (non-workspace) module, and <code>false</code>
	 * otherwise
	 * 
	 * @return <code>true</code> if the module is an external module, and <code>false</code> otherwise
	 */
	boolean isExternal();
	
	
	/**
	 * @return id's of publish operations that will be performed on this module during deploy
	 */
	String[] getPublishOperationIds();
	
	
	/**
	 * set's  publish operation ids that should be performed on a given module during publish process 
	 * @param ids - ids of publish operations
	 */
	void setPublishOperationIds(String[] ids);
	
	
	
	/**
	 * @param configuration
	 */
	void setConfiguration(IAbstractConfiguration configuration);
	
	
	/**
	 * @return desired mode of publishing for this module one of PUBLISH constants declared in IServer
	 */
	int getDesiredPublishKind();
	
	/**
	 * @param operationId
	 * @param configuration 
	 * @throws CoreException if something goes wrong or operation with a given id is allready configured for this module
	 */
	void configurePublishOperation(String operationId,IAbstractConfiguration configuration) throws CoreException;
	
	
	/**
	 * configures publish operation for a given module
	 * @param operationId
	 * @return
	 * @throws CoreException if something goes wrong or publish operation with a given id is not configured on this server
	 */
	IAbstractConfiguration getPublishOperationConfiguration(String operationId)throws CoreException;

	/**
	 * unconfigures publish operation for a given module
	 * @param operationId
	 * @param configuration 
	 * @throws CoreException if something goes wrong
	 */
	void unconfigurePublishOperation(String operationId) throws CoreException;

	/**
	 * @param subConfiguration
	 * 
	 */
	void storeConfig(IAbstractConfiguration subConfiguration);
	
	
}