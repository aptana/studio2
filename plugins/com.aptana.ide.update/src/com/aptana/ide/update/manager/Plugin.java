/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.update.manager;

import java.net.URL;
import java.util.Calendar;

import org.eclipse.core.runtime.CoreException;

public class Plugin implements IPlugin
{

	private static final String DEFAULT_MORE_URL = "http://www.aptana.com/plugins"; //$NON-NLS-1$

	public static final int UNKNOWN_WEIGHT = Integer.MAX_VALUE;

	private String name;
	private URL url;
	private String id;
	private String version;
	private String description;
	private Calendar releaseDate;
	private String more;
	private String category;
	private int sortweight;
	private String imagePath;
	private String requires;
	private InstallerCategory installerCategory;

	public Plugin(String id, String name, String version, Calendar releaseDate, String description, URL updateSite,
			String more, String category, int sortweight, String imagePath, String requires,
			InstallerCategory installerCategory)
	{
		this.id = id;
		this.name = name;
		this.version = version;
		this.releaseDate = releaseDate;
		this.description = description;
		this.url = updateSite;
		this.more = more;
		this.category = category;
		this.sortweight = sortweight;
		this.imagePath = imagePath;
		this.requires = requires;
		this.installerCategory = installerCategory;
	}

	public String getName()
	{
		return name;
	}

	public URL getURL()
	{
		return url;
	}

	public boolean isPatch()
	{
		return false;
	}

	public void setURL(URL url) throws CoreException
	{
		this.url = url;
	}

	public String getDescription()
	{
		return description;
	}

	public String getMore()
	{
		if (more == null || more.trim().equals("")) //$NON-NLS-1$
			return DEFAULT_MORE_URL;
		return more;
	}

	public Calendar getReleaseDate()
	{
		return releaseDate;
	}

	public String getCategory()
	{
		return category;
	}

	public int getSortweight()
	{
		return sortweight;
	}

	public String getImagePath()
	{
		return imagePath;
	}

	public String getRequiredPlugins()
	{
		return requires;
	}

	public InstallerCategory getInstallerCategory()
	{
		return installerCategory;
	}

	public void setImagePath(String path)
	{
		this.imagePath = path;
	}

	public String toString()
	{
		return name;
	}

	public String getId()
	{
		return id;
	}

	public String getVersion()
	{
		return version;
	}

}
