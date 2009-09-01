/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.librarymanager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.ide.projects.ProjectsPlugin;

/**
 * Library manager class
 */
public final class LibraryManager
{

	private static LibraryManager instance;

	private LibraryInfo[] cachedList = null;

	/**
	 * Gets the library manager instance
	 * 
	 * @return - the singleton instance
	 */
	public static LibraryManager getInstance()
	{
		if (instance == null)
		{
			instance = new LibraryManager();
		}

		return instance;
	}

	private LibraryManager()
	{
	}

	/**
	 * getLibraryInfoExtensions
	 * 
	 * @return - library info extensions
	 */
	public LibraryInfo[] getLibraryInfoExtensions()
	{
		if (cachedList == null)
		{
			cachedList = findLibraryInfoExtensions();
		}

		return cachedList;
	}

	private LibraryInfo[] findLibraryInfoExtensions()
	{
		ArrayList<LibraryInfo> list = new ArrayList<LibraryInfo>();

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(ProjectsPlugin.PLUGIN_ID + ".library"); //$NON-NLS-1$

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

						LibraryInfo libraryInfo = new LibraryInfo();

						// Get 'name'
						String name = element.getAttribute("name"); //$NON-NLS-1$
						libraryInfo.setName(name);

						// Get 'directory'
						String directory = element.getAttribute("directory"); //$NON-NLS-1$
						if (directory != null)
						{
							URL unresolved = FileLocator.find(bunble, new Path(directory), null);
							if (unresolved != null)
							{
								libraryInfo.setUnresolvedURL(unresolved);
							}
						}

						// TODO: not used
						// Get optional 'iconFile'
						String iconFile = element.getAttribute("iconFile"); //$NON-NLS-1$
						if (iconFile != null && iconFile.length() > 0)
						{
							String resolvedPath = getResolvedFilename(bunble, iconFile);
							libraryInfo.setIconFile(resolvedPath);
						}
						if (directory != null && name != null)
						{
							list.add(libraryInfo);
						}
					}
					catch (InvalidRegistryObjectException x)
					{
						// ignore bad extensions
					}
				}
			}
		}
		Collections.sort(list, new Comparator<LibraryInfo>()
		{

			public int compare(LibraryInfo o1, LibraryInfo o2)
			{
				if (o1 != null && o1.getName() != null && o2 != null && o2.getName() != null)
				{
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
				return 0;
			}

		});
		return (LibraryInfo[]) list.toArray(new LibraryInfo[0]);
	}

	private String getResolvedFilename(Bundle b, String fullPath)
	{
		URL url = getResolvedURL(b, fullPath);
		if (url != null)
		{
			return url.getFile();
		}

		return null;
	}

	private URL getResolvedURL(Bundle b, String fullPath)
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
				return null;
			}
		}
		return null;
	}

}
