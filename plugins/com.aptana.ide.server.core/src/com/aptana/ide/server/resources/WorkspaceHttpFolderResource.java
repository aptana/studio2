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
package com.aptana.ide.server.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.server.core.ServerCorePlugin;
import com.aptana.ide.server.http.HttpServer;

/**
 * @author Kevin Lindsey
 */
public class WorkspaceHttpFolderResource implements IHttpFolderResource
{
	/*
	 * Fields
	 */
	IContainer _resource;
	String[] _fileNames;
	String[] _folderNames;

	/*
	 * Constructors
	 */

	/**
	 * WorkspaceHttpFolderResource
	 * 
	 * @param resource
	 */
	public WorkspaceHttpFolderResource(IContainer resource)
	{
		this._resource = resource;
	}

	/*
	 * Methods
	 */

	/**
	 * Returns the input stream for the current contents
	 * 
	 * @param server
	 * @return The InputStream representing the resource
	 * @throws IOException
	 *             If the resource cannot be returned
	 */
	public InputStream getContentInputStream(HttpServer server) throws IOException
	{
		throw new IOException("Not a file"); //$NON-NLS-1$
	}

	/**
	 * Returns the length of the content
	 * 
	 * @return The length of the content. -1 in this case.
	 */
	public long getContentLength()
	{
		return -1;
	}

	/**
	 * Returns the type of the content
	 * 
	 * @return The type of the content. Returns null in this case.
	 */
	public String getContentType()
	{
		return null;
	}

	/**
	 * Returns an array of all files in the project
	 * 
	 * @return An array of files
	 */
	public String[] getFileNames()
	{
		if (this._fileNames == null)
		{
			loadMemberNames();
		}

		return this._fileNames;
	}

	/**
	 * Returns an array of all folders in the project
	 * 
	 * @return An array of folders
	 */
	public String[] getFolderNames()
	{
		if (this._folderNames == null)
		{
			loadMemberNames();
		}

		return this._folderNames;
	}

	/**
	 * getResourceFromProject
	 * 
	 * @param path
	 * @param project
	 * @return IResource
	 */
	private static IResource getResourceFromProject(IPath path, IProject project)
	{
		if (project.isOpen())
		{
			if (path.segmentCount() == 0)
			{
				return project;
			}

			IResource resource = project.findMember(path);

			if (resource != null && resource.exists())
			{
				return resource;
			}
		}

		return null;
	}

	/**
	 * loadMemberNames
	 */
	private void loadMemberNames()
	{
		ArrayList fileList = new ArrayList();
		ArrayList folderList = new ArrayList();
		Hashtable usedNames = new Hashtable();

		addMemberNamesToList(this._resource, fileList, folderList, usedNames);

		try
		{
			IProject project = this._resource.getProject();

			if (project != null)
			{
				IProject[] referencedProjects = project.getReferencedProjects();
				IPath resourcePath = this._resource.getProjectRelativePath();

				for (int i = 0; i < referencedProjects.length; i++)
				{
					IResource referencedResource = getResourceFromProject(resourcePath, referencedProjects[i]);

					if (referencedResource instanceof IContainer)
					{
						addMemberNamesToList((IContainer) referencedResource, fileList, folderList, usedNames);
					}
				}
			}

			Collections.sort(fileList);
			Collections.sort(folderList);
			this._fileNames = (String[]) fileList.toArray(new String[0]);
			this._folderNames = (String[]) folderList.toArray(new String[0]);
		}
		catch (CoreException e)
		{
			IdeLog.logError(ServerCorePlugin.getDefault(), Messages.WorkspaceHttpFolderResource_ERR_LoadingNames, e);
		}

	}

	/**
	 * addMemberNamesToList
	 * 
	 * @param folder
	 * @param fileList
	 * @param folderList
	 * @param usedNames
	 */
	private void addMemberNamesToList(IContainer folder, ArrayList fileList, ArrayList folderList, Hashtable usedNames)
	{
		try
		{
			IResource[] resources = folder.members();

			for (int i = 0; i < resources.length; i++)
			{
				IResource resource = resources[i];

				if (!usedNames.contains(resource.getName()))
				{
					if (resource instanceof IProject)
					{
						folderList.add(resource.getName());
					}
					if (resource instanceof IFolder)
					{
						folderList.add(resource.getName());
					}
					else if (resource instanceof IFile)
					{
						fileList.add(resource.getName());
					}

					usedNames.put(resource.getName(), resource.getName());
				}
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(ServerCorePlugin.getDefault(), Messages.WorkspaceHttpFolderResource_ERR_AddingNames, e);
		}
	}
}
