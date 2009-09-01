/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.internal.update.manager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.aptana.ide.update.Activator;
import com.aptana.ide.update.manager.InstallerCategory;
import com.aptana.ide.update.manager.Plugin;

/**
 * The XML handler to parse the plug-in content file.
 */
public class PluginsContentHandler implements ContentHandler
{

	private static final String YEAR = "year"; //$NON-NLS-1$
	private static final String DAY = "day"; //$NON-NLS-1$
	private static final String MONTH = "month"; //$NON-NLS-1$
	private static final String URL = "url"; //$NON-NLS-1$
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String VERSION = "version"; //$NON-NLS-1$
	private static final String PLUGIN = "plugin"; //$NON-NLS-1$
	private static final String MORE = "more"; //$NON-NLS-1$
	private static final String CATEGORY = "category"; //$NON-NLS-1$
	private static final String SORTWEIGHT = "sortweight"; //$NON-NLS-1$
	private static final String ICON = "icon"; //$NON-NLS-1$
	private static final String REQUIRES = "requires"; //$NON-NLS-1$
	private static final String PARENT = "parent"; //$NON-NLS-1$
	private static final String COLLAPSE = "collapse"; //$NON-NLS-1$
	private static final String INSTALLER = "installer"; //$NON-NLS-1$
	private static final String INSTALLER_ID = "installer_id"; //$NON-NLS-1$

	private StringBuilder data;
	private Map<String, InstallerCategory> installerCategories;
	private List<Plugin> plugins;

	private URL url;
	private String version;
	private String name;
	private String description;
	private String id;
	private String category;
	private int sortweight;
	private int year;
	private int month;
	private int day;
	private String more;
	private String icon;
	private String requires;
	private String installerID;
	private String parentID;
	private boolean collapse;

	public PluginsContentHandler()
	{
		plugins = new ArrayList<Plugin>();
		installerCategories = new HashMap<String, InstallerCategory>();
		sortweight = Plugin.UNKNOWN_WEIGHT;
	}

	public List<Plugin> getPlugins()
	{
		return plugins;
	}

	public void characters(char[] ch, int start, int length) throws SAXException
	{
		for (int i = start; i < start + length; i++)
		{
			data.append(ch[i]);
		}
	}

	public void endDocument() throws SAXException
	{
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equals(URL))
		{
			try
			{
				URL proj = new URL(data.toString().trim());
				url = proj;
			}
			catch (MalformedURLException e)
			{
				Activator.log(IStatus.ERROR, e.getMessage(), null);
			}
		}
		else if (qName.equals(NAME))
		{
			name = data.toString().trim();
		}
		else if (qName.equals(ID))
		{
			id = data.toString().trim();
		}
		else if (qName.equals(DESCRIPTION))
		{
			description = data.toString().trim();
		}
		else if (qName.equals(CATEGORY))
		{
			category = data.toString().trim();
		}
		else if (qName.equals(SORTWEIGHT))
		{
			sortweight = Integer.parseInt(data.toString().trim());
		}
		else if (qName.equals(VERSION))
		{
			version = data.toString().trim();
		}
		else if (qName.equals(YEAR))
		{
			year = Integer.parseInt(data.toString().trim());
		}
		else if (qName.equals(MONTH))
		{
			month = Integer.parseInt(data.toString().trim());
		}
		else if (qName.equals(DAY))
		{
			day = Integer.parseInt(data.toString().trim());
		}
		else if (qName.equals(MORE))
		{
			more = data.toString().trim();
		}
		else if (qName.equals(ICON))
		{
			icon = data.toString().trim();
		}
		else if (qName.equals(REQUIRES))
		{
			requires = data.toString().trim();
		}
		else if (qName.equals(INSTALLER_ID))
		{
			installerID = data.toString().trim();
		}
		else if (qName.equals(PARENT))
		{
			parentID = data.toString().trim();
		}
		else if (qName.equals(COLLAPSE))
		{
			collapse = Boolean.parseBoolean(data.toString().trim());
		}
		else if (qName.equals(INSTALLER))
		{
			// the end of an installer block
			InstallerCategory parent = null;
			if (parentID != null)
			{
				parent = installerCategories.get(parentID);
			}
			InstallerCategory installerCategory = new InstallerCategory(id, category, sortweight, parent, collapse);
			installerCategories.put(id, installerCategory);
		}
		else if (qName.equals(PLUGIN))
		{
			// the end of a plugin block
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);
			cal.set(Calendar.DAY_OF_MONTH, day);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			plugins.add(new Plugin(id, name, version, cal, description, url, more, category, sortweight, icon,
					requires, installerCategories.get(installerID)));
		}
	}

	public void endPrefixMapping(String prefix) throws SAXException
	{
	}

	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
	{
	}

	public void processingInstruction(String target, String data) throws SAXException
	{
	}

	public void setDocumentLocator(Locator locator)
	{
	}

	public void skippedEntity(String name) throws SAXException
	{
	}

	public void startDocument() throws SAXException
	{
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		data = new StringBuilder();
		if (qName.equals(INSTALLER))
		{
			clearInstallerData();
		}
		else if (qName.equals(PLUGIN))
		{
			clearPluginData();
		}
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException
	{
	}

	private void clearInstallerData()
	{
		id = null;
		category = null;
		sortweight = InstallerCategory.UNKNOWN_WEIGHT;
		parentID = null;
		collapse = false;
	}

	private void clearPluginData()
	{
		url = null;
		version = null;
		name = null;
		description = null;
		id = null;
		category = null;
		sortweight = Plugin.UNKNOWN_WEIGHT;
		year = 2009;
		month = 1;
		day = 1;
		more = null;
		icon = null;
		requires = null;
		installerID = null;
	}

}
