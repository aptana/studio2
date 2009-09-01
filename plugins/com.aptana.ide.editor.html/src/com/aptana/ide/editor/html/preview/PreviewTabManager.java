/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.editor.html.HTMLPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PreviewTabManager
{

	/**
	 * EXTENSION_NAME
	 */
	public static final String EXTENSION_NAME = "browserTabs"; //$NON-NLS-1$

	/**
	 * EXTENSION_POINT
	 */
	public static final String EXTENSION_POINT = HTMLPlugin.ID + "." + EXTENSION_NAME; //$NON-NLS-1$

	/**
	 * TAB_ELEMENT
	 */
	public static final String TAB_ELEMENT = "tab"; //$NON-NLS-1$

	/**
	 * STATIC_ELEMENT
	 */
	public static final String STATIC_ELEMENT = "static"; //$NON-NLS-1$

	/**
	 * URL_ELEMENT
	 */
	public static final String URL_ELEMENT = "url"; //$NON-NLS-1$

	/**
	 * NAME_ELEMENT
	 */
	public static final String NAME_ELEMENT = "name"; //$NON-NLS-1$

	/**
	 * CLASS_ATTRIBUTE
	 */
	public static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

	private static PreviewTabManager manager;

	private List<IBrowserTabAdder> adders;

	private List<ContributedPreviewPage> staticTabs;

	private PreviewTabManager()
	{
		adders = new ArrayList<IBrowserTabAdder>();
		staticTabs = new ArrayList<ContributedPreviewPage>();
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT);
		for (IConfigurationElement element : elements)
		{
			if (TAB_ELEMENT.equals(element.getName()))
			{
				String className = element.getAttribute(CLASS_ATTRIBUTE);
				if (className != null)
				{
					try
					{
						Object client = element.createExecutableExtension(CLASS_ATTRIBUTE);
						if (client instanceof IBrowserTabAdder)
						{
							adders.add((IBrowserTabAdder) client);
						}
					}
					catch (CoreException e)
					{
					}
				}
			}
			else if (STATIC_ELEMENT.equals(element.getName()))
			{
				String url = element.getAttribute(URL_ELEMENT);
				String name = element.getAttribute(NAME_ELEMENT);
				if (url != null && name != null)
				{
					ContributedPreviewPage page = new ContributedPreviewPage(null);
					page.setBrowser(new DefaultBrowser(), "Default"); //$NON-NLS-1$
					page.setTitle(name);
					page.setValue(url);
					staticTabs.add(page);
				}
			}
		}
	}

	/**
	 * Gets the tab adders registered via extension point
	 * 
	 * @return - array of tab adders
	 */
	public IBrowserTabAdder[] getTabAdders()
	{
		return this.adders.toArray(new IBrowserTabAdder[0]);
	}

	/**
	 * Get the preview page that just show static content
	 * 
	 * @return - array of contributed preview pages
	 */
	public ContributedPreviewPage[] getStaticTabs()
	{
		return this.staticTabs.toArray(new ContributedPreviewPage[0]);
	}

	/**
	 * Gets the preview tab manager
	 * 
	 * @return - manager instance
	 */
	public static PreviewTabManager getManager()
	{
		if (manager == null)
		{
			manager = new PreviewTabManager();
		}
		return manager;
	}

}
