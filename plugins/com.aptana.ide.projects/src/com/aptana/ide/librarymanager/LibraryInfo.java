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

import org.eclipse.core.runtime.FileLocator;

/**
 * Library info class
 */
public class LibraryInfo
{
	private String name;
	private String iconFile;
	private URL unresolvedURL;

	/**
	 * Gets the name of this library
	 * 
	 * @return - library name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this library
	 * 
	 * @param name -
	 *            new library name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets unresolve URL of the directory for this library
	 * 
	 * @param unresolved -
	 *            new directory url
	 */
	public void setUnresolvedURL(URL unresolved)
	{
		this.unresolvedURL = unresolved;
	}

	/**
	 * Gets unresolve URL of the directory for this library
	 * 
	 * @return unresolved directory url
	 */
	public URL getUnresolvedURL()
	{
		return unresolvedURL;
	}

	/**
	 * Gets the resolved url. This will unpack the URL if needed
	 * 
	 * @return - absolute url to library directory
	 */
	public URL getResolvedURL()
	{
		URL resolved = null;
		if (unresolvedURL != null)
		{
			try
			{
				resolved = FileLocator.toFileURL(unresolvedURL);
			}
			catch (IOException e)
			{
				resolved = null;
			}
		}
		return resolved;
	}

	/**
	 * Gets the icon file
	 * 
	 * @return - icon file path
	 */
	public String getIconFile()
	{
		return iconFile;
	}

	/**
	 * Sets the icon file path
	 * 
	 * @param iconFile -
	 *            new icon file
	 */
	public void setIconFile(String iconFile)
	{
		this.iconFile = iconFile;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		if (name == null)
		{
			return super.toString();
		}
		else
		{
			return name;
		}
	}
}
