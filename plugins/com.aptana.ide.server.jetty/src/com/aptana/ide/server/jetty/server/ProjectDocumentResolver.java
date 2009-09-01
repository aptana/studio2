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
package com.aptana.ide.server.jetty.server;

import java.net.URL;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

import com.aptana.jaxer.connectors.servlet.interfaces.IDocumentRootResolver;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ProjectDocumentResolver implements IDocumentRootResolver
{

	private IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

	/**
	 * @see com.aptana.jaxer.connectors.servlet.interfaces.IDocumentRootResolver#getDocumentRoot(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse)
	 */
	public String getDocumentRoot(ServletRequest request, ServletResponse res)
	{
		String path = null;
		try
		{
			if (request instanceof HttpServletRequest)
			{
				path = ((HttpServletRequest) request).getServletPath();
				String ref = ((HttpServletRequest) request).getHeader("Referer"); //$NON-NLS-1$
				IProject project = null;
				if (ref != null)
				{
					URL url = new URL(ref);
					String refPath = url.getPath();
					IResource resource = workspaceRoot.findMember(new Path(refPath));
					if (resource != null)
					{
						project = resource.getProject();

					}
					else
					{
						resource = workspaceRoot.findMember(new Path(path));
						if (resource != null && resource instanceof IFile)
						{
							project = resource.getProject();
						}
					}
				}
				else
				{
					IResource resource = workspaceRoot.findMember(new Path(path));
					if (resource != null && resource instanceof IFile)
					{
						project = resource.getProject();
					}
				}
				if (project != null)
				{
					String override = project.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
							HTMLPreviewConstants.HTML_PREVIEW_OVERRIDE));
					if (HTMLPreviewConstants.TRUE.equals(override))
					{
						String contextRoot = project.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
								HTMLPreviewConstants.CONTEXT_ROOT));
						IResource root = project.findMember(new Path(contextRoot));
						path = root.getLocation().makeAbsolute().toString();
					}
					else
					{
						path = project.getLocation().makeAbsolute().toString();
					}
				}
			}
		}
		catch (Exception e)
		{
			path = null;
		}
		return path;
	}

	/**
	 * @see com.aptana.jaxer.connectors.servlet.interfaces.IDocumentRootResolver#getPageFile(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse)
	 */
	public String getPageFile(ServletRequest request, ServletResponse response)
	{
		String path = null;
		try
		{
			if (request instanceof HttpServletRequest)
			{
				path = ((HttpServletRequest) request).getServletPath();
				String ref = ((HttpServletRequest) request).getHeader("Referer"); //$NON-NLS-1$
				if (ref != null)
				{
					URL url = new URL(ref);
					String refPath = url.getPath();
					IResource resource = workspaceRoot.findMember(new Path(refPath));
					if (resource != null)
					{
						IProject project = resource.getProject();
						path = HTMLContextRootUtils.resolveURL(project, path);
						IResource candidate = workspaceRoot.findMember(new Path(path));
						if (candidate != null && candidate.getProject().equals(project) && candidate instanceof IFile)
						{
							path = candidate.getLocation().makeAbsolute().toString();
						}
						else
						{
							candidate = project.findMember(new Path(path));
							if (candidate != null && candidate.getProject().equals(project)
									&& candidate instanceof IFile)
							{
								path = candidate.getLocation().makeAbsolute().toString();
							}
						}
					}
				}
				else
				{
					IResource resource = workspaceRoot.findMember(new Path(path));
					if (resource != null && resource instanceof IFile)
					{
						path = resource.getLocation().makeAbsolute().toString();
					}
				}
			}
		}
		catch (Exception e)
		{
			path = null;
		}
		return path;
	}
}
