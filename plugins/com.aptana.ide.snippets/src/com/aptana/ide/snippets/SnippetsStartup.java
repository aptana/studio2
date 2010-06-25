package com.aptana.ide.snippets;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.IdeLog;

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


/**
 * @author Kevin Lindsey
 */
public class SnippetsStartup
{
	private static SnippetsStartup instance;

	/**
	 * SnippetsStartup
	 */
	private SnippetsStartup()
	{
	}

	/**
	 * getInstance
	 * 
	 * @return SnippetsList instance
	 */
	public static SnippetsStartup getInstance()
	{
		if (instance == null)
		{
			instance = new SnippetsStartup();
			instance.loadBuiltins();
			instance.loadFromWorkspace();
			instance.loadFromExtensionPoints();
		}

		return instance;
	}

	/**
	 * Just init
	 */
	public static void init()
	{
		getInstance();
	}

	private void loadBuiltins()
	{
		// get snippets
		SnippetsManager snippets = SnippetsManager.getInstance();

		// get snippets directory
		Bundle snippetsBundle = Platform.getBundle(SnippetsPlugin.PLUGIN_ID);
		if (snippetsBundle != null)
		{
			URL unresolved = FileLocator.find(snippetsBundle, new Path("/snippets"), null); //$NON-NLS-1$
			if (unresolved != null)
			{
				URL resolved;
				try
				{
					resolved = FileLocator.toFileURL(unresolved);
					File snippetsDir = new File(resolved.getFile());
					// load snippets in the snippets directory
					snippets.loadSnippetDirectory(snippetsDir);
				}
				catch (IOException e)
				{
					IdeLog.logError(SnippetsPlugin.getDefault(),
							Messages.SnippetsStartup_ErrorExtractingBundledSnippets, e);
				}
			}

		}

		// get snippets directory
		String tempDir = snippets.getSnippetTempDirectory();
		File snippetsTempDir = new File(tempDir); //$NON-NLS-1$

		// load snippets in the snippets directory
		snippets.loadSnippetDirectory(snippetsTempDir);

	}

	private void loadFromWorkspace()
	{
		// get snippets
		SnippetsManager snippets = SnippetsManager.getInstance();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		// setup resource listener
		ResourceChangeListener listener = new ResourceChangeListener();
		workspace.addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);

		// collect snippets from all open projects
		for (int i = 0; i < workspace.getRoot().getProjects().length; i++)
		{
			IProject project = workspace.getRoot().getProjects()[i];
			IFolder folder = project.getFolder("snippets"); //$NON-NLS-1$

			if (folder != null)
			{
				try
				{
					for (int j = 0; j < folder.members().length; j++)
					{
						IResource resource = folder.members()[j];

						if (resource instanceof IFile)
						{
							File file = resource.getLocation().toFile();
							Snippet snippet = Snippet.fromFile(file);

							if (snippet != null)
							{
								snippets.addSnippet(snippet);
							}
						}
					}
				}
				catch (CoreException x)
				{
					// ignore folders we cannot access
				}
			}
		}
	}

	/**
	 * 
	 *
	 */
	private void loadFromExtensionPoints()
	{
		// get snippets
		SnippetsManager snippets = SnippetsManager.getInstance();

		SnippetsInfo[] snippetsInfo = findSnippetsInfoExtensions();

		for (int i = 0; i < snippetsInfo.length; i++)
		{
			String snippetsDir = snippetsInfo[i].directory;
			try
			{
				// load snippets in the snippets directory
				snippets.loadSnippetDirectory(new File(snippetsDir) /* , iconFile */);
			}
			catch (Exception e)
			{
				IdeLog.logError(SnippetsPlugin.getDefault(), "loadSnippetDirectory:" + snippetsDir); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @return SnippetsInfo
	 */
	public static SnippetsInfo[] findSnippetsInfoExtensions()
	{
		List<SnippetsInfo> list = new ArrayList<SnippetsInfo>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint("com.aptana.ide.snippets.snippetspath"); //$NON-NLS-1$

		if (point != null)
		{
			IExtension[] extensions = point.getExtensions();

			for (int i = 0; i < extensions.length; i++)
			{
				IExtension extension = extensions[i];
				IConfigurationElement[] configurations = extension.getConfigurationElements();

				for (int j = 0; j < configurations.length; j++)
				{
					IConfigurationElement element = configurations[j];
					try
					{
						IExtension declaring = element.getDeclaringExtension();

						// String declaringPluginID = declaring
						// .getDeclaringPluginDescriptor()
						// .getUniqueIdentifier();

						String declaringPluginID = declaring.getNamespaceIdentifier();
						Bundle bunble = Platform.getBundle(declaringPluginID);

						SnippetsInfo snippetsInfo = new SnippetsInfo();

						// Get 'directory'
						String directory = element.getAttribute("directory"); //$NON-NLS-1$
						String resolvedPath = getResolvedFilename(bunble, directory);
						if (resolvedPath != null)
						{
							snippetsInfo.directory = resolvedPath;
						}
						else
						{
							continue;
						}

						// TODO: not used
						// Get optional 'iconFile'
						String iconFile = element.getAttribute("iconFile"); //$NON-NLS-1$
						if (iconFile != null && iconFile.length() > 0)
						{
							resolvedPath = getResolvedFilename(bunble, iconFile);
							snippetsInfo.iconFile = resolvedPath;
						}

						list.add(snippetsInfo);
					}
					catch (InvalidRegistryObjectException x)
					{
						// ignore bad extensions
					}
				}
			}
		}

		return list.toArray(new SnippetsInfo[list.size()]);
	}

	/**
	 * @param b
	 * @param fullPath
	 * @return - resolved file name
	 */
	private static String getResolvedFilename(Bundle b, String fullPath)
	{
		URL url = getResolvedURL(b, fullPath);
		if (url != null)
		{
			return url.getFile();
		}

		return null;
	}

	/**
	 * @param b
	 * @param fullPath
	 * @return - resolved url
	 */
	private static URL getResolvedURL(Bundle b, String fullPath)
	{
		URL url = FileLocator.find(b, new Path(fullPath), null);

		if (url != null)
		{
			try
			{

				URL localUrl = FileLocator.toFileURL(url);
				if (localUrl != null)
				{
					return localUrl;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
}
