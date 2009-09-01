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
package com.aptana.ide.editor.html.preview;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.aptana.ide.debug.core.JSLaunchConfigurationHelper;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.server.core.IServer;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class HTMLPreviewHelper
{

	private HTMLPreviewHelper()
	{
		// Does nothing
	}

	/**
	 * Gets the url from a launch configuration
	 * 
	 * @param config
	 * @param input
	 * @return - url or null
	 */
	public static String getConfigURL(ILaunchConfiguration config, IEditorInput input)
	{
		URL url = null;
		IFile file = null;
		if (input instanceof FileEditorInput)
		{
			file = ((FileEditorInput) input).getFile();
		}
		try
		{
			url = JSLaunchConfigurationHelper.getLaunchURL(config, file);
			if (url != null)
			{
				return url.toExternalForm();
			}
		}
		catch (CoreException e)
		{
			HTMLPlugin.getDefault().getLog().log(e.getStatus());
		}
		return null;
	}

	/**
	 * Gets the url from a server configuration
	 * 
	 * @param server
	 * @param input
	 * @param appendProjectName
	 * @return - url or null
	 */
	public static String getServerURL(IServer server, IEditorInput input, boolean appendProjectName)
	{
		return getServerURL(server, input, appendProjectName, null);
	}

	/**
	 * Gets the url from a server configuration
	 * 
	 * @param server
	 * @param input
	 * @param appendProjectName
	 * @param pathHeader -
	 *            path header.
	 * @return - url or null
	 */
	public static String getServerURL(IServer server, IEditorInput input, boolean appendProjectName, String pathHeader)
	{
		String url = null;
		String externalBaseUrl = getServerHostURL(server);
		if (input instanceof FileEditorInput)
		{
			IFile file = ((FileEditorInput) input).getFile();
			url = getServerURLFromFile(server, file, appendProjectName, pathHeader);
		}
		else
		{
			url = externalBaseUrl;
		}
		return url;
	}

	/**
	 * Gets the absolute url
	 * 
	 * @param baseURL
	 * @param input
	 * @param appendProjectName
	 * @return - string absolute url
	 */
	public static String getAbsoluteURL(String baseURL, IEditorInput input, boolean appendProjectName)
	{
		String url = baseURL;
		if (url != null && appendProjectName && input instanceof FileEditorInput)
		{
			IFile file = ((FileEditorInput) input).getFile();
			if (file != null)
			{
				if (!url.endsWith("/")) //$NON-NLS-1$
				{
					url += "/"; //$NON-NLS-1$
				}
				url += file.getProjectRelativePath();
			}
		}
		return url;
	}

	/**
	 * Gets server url from a file
	 * 
	 * @param server
	 * @param file
	 * @param appendProjectName
	 * @return - string url
	 */
	public static String getServerURLFromFile(IServer server, IResource file, boolean appendProjectName)
	{
		return getServerURLFromFile(server, file, appendProjectName, null);
	}

	/**
	 * Gets server url from a file
	 * 
	 * @param server
	 * @param file
	 * @param appendProjectName
	 * @param pathHeader -
	 *            path header.
	 * @return - string url
	 */
	private static String getServerURLFromFile(IServer server, IResource file, boolean appendProjectName,
			String pathHeader)
	{
		String url = null;
		String externalBaseUrl = getServerHostURL(server);
		if (pathHeader != null)
		{
			externalBaseUrl += pathHeader + "/"; //$NON-NLS-1$
		}
		try
		{
			URL baseURL = new URL(externalBaseUrl);
			IProject project = file.getProject();
			if (appendProjectName)
			{
				baseURL = new URL(baseURL, project.getName() + '/' + file.getProjectRelativePath());
			}
			else
			{
				baseURL = new URL(baseURL, file.getProjectRelativePath().toString());
			}
			url = baseURL.toExternalForm();
		}
		catch (MalformedURLException e)
		{
			url = externalBaseUrl;
		}
		return url;
	}

	/**
	 * Gets the server host url section
	 * 
	 * @param server
	 * @return - server host
	 */
	public static String getServerHostURL(IServer server)
	{
		String host = server.getHost() == null ? "localhost" : server.getHost(); //$NON-NLS-1$
		String externalBaseUrl = "http://" + host + "/"; //$NON-NLS-1$ //$NON-NLS-2$
		return externalBaseUrl;
	}

	/**
	 * Gets sample url from a launch configuration
	 * 
	 * @param config
	 * @param projectName
	 * @return - sample url
	 */
	public static String getConfigSampleURL(ILaunchConfiguration config, String projectName)
	{
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(projectName + "/file.html")); //$NON-NLS-1$
		try
		{
			URL url = JSLaunchConfigurationHelper.getLaunchURL(config, file);
			if (url != null)
			{
				return url.toExternalForm();
			}
		}
		catch (CoreException e)
		{
			HTMLPlugin.getDefault().getLog().log(e.getStatus());
		}
		return null;
	}

}
