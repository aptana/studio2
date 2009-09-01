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
package com.aptana.ide.editor.css;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.aptana.ide.editor.css.preferences.IPreferenceConstants;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * Browser extension reader that returns the list of browsers to show previews with when an editor asks. Listens to the
 * browser preview preferences so the list contains all browsers that should be shown in alphabetical order.
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class BrowserExtensionLoader
{

	// List of all browsers
	private static List<IConfigurationElement> browserList = null;

	private static List<String> allBrowsers = null;

	// List of all browsers that are current set to be shown in the preferences
	private static List<IConfigurationElement> showList = new ArrayList<IConfigurationElement>();

	// Listener for changes to preview preferences
	private static IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener()
	{

		public void propertyChange(PropertyChangeEvent event)
		{
			if (IPreferenceConstants.CSSEDITOR_BROWSER_PREVIEW_PREFERENCE.equals(event.getProperty()))
			{
				computeShowList();
			}
		}
	};

	private static String OS = Platform.getOS();

	private BrowserExtensionLoader()
	{
		// does nothing
	}

	/**
	 * Looks up the label for the browser based on the os run on.
	 * 
	 * @param browserElement -
	 *            element containing label node(s)
	 * @return - browser label if found, null otherwise
	 */
	public static String getBrowserLabel(IConfigurationElement browserElement)
	{
		String label = null;
		boolean found = false;
		IConfigurationElement[] ce = browserElement.getChildren(UnifiedEditorsPlugin.LABEL_NODE);
		for (int k = 0; k < ce.length && !found; k++)
		{
			if (ce[k].getAttribute(UnifiedEditorsPlugin.OS_ATTR) != null
					&& ce[k].getAttribute(UnifiedEditorsPlugin.VALUE_ATTR) != null)
			{
				if (OS.equals(ce[k].getAttribute(UnifiedEditorsPlugin.OS_ATTR)))
				{
					label = ce[k].getAttribute(UnifiedEditorsPlugin.VALUE_ATTR);
					found = true;
				}
			}
		}
		return label;
	}

	/**
	 * Computes an alpabetized list of browsers to show previews for.
	 */
	private static void computeShowList()
	{
		showList.clear();
		String browserString = CSSPlugin.getDefault().getPreferenceStore().getString(
				IPreferenceConstants.CSSEDITOR_BROWSER_PREVIEW_PREFERENCE);
		String[] browsers_names = browserString.split(","); //$NON-NLS-1$

		// See comment below about mac fix
		IConfigurationElement safari = null;
		IConfigurationElement element;
		int size = browserList.size();

		for (int i = 0; i < size; i++)
		{
			element = browserList.get(i);

			// See comment below about mac fix
			String name = getBrowserLabel(element);
			if (safari == null && "Safari".equals(name)) //$NON-NLS-1$
			{
				safari = element;
			}

			if (name != null)
			{
				boolean show = false;
				for (int k = 0; k < browsers_names.length && !show; k++)
				{
					if (name.equals(browsers_names[k]))
					{
						show = true;
					}
				}
				if (show)
				{
					showList.add(element);
				}
			}
		}

		// Fix for users with currently only the Firefox preview added, this will add the safari first so the start
		// page/web views won't break anymore for these users
		if (OS.equals(Platform.OS_MACOSX))
		{
			if (showList.size() == 1)
			{
				element = showList.get(0);
				IPreferenceStore store = CSSPlugin.getDefault().getPreferenceStore();
				store.removePropertyChangeListener(propertyChangeListener);
				if (getBrowserLabel(element).equals("Firefox")) //$NON-NLS-1$
				{
					if (safari != null)
					{
						showList.add(0, safari);
						// Add safari back to prefs
						store.setValue(IPreferenceConstants.CSSEDITOR_BROWSER_PREVIEW_PREFERENCE, browserString
								+ ",Safari"); //$NON-NLS-1$
					}
					else
					{
						showList.clear();
						// Clear prefs completely
						store.setValue(IPreferenceConstants.CSSEDITOR_BROWSER_PREVIEW_PREFERENCE, ""); //$NON-NLS-1$
					}
				}
				store.addPropertyChangeListener(propertyChangeListener);
			}
		}
	}

	/**
	 * Returns the list of browser extension points to show. To be called by an HTML editor.
	 * 
	 * @return - List of loadable browsers
	 */
	public static List<IConfigurationElement> loadBrowsers()
	{
		if (browserList == null)
		{
			browserList = new ArrayList<IConfigurationElement>();
			IExtensionRegistry reg = Platform.getExtensionRegistry();
			IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.BROWSER_EXTENSION_POINT);
			IExtension[] extensions = ep.getExtensions();
			CSSPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(propertyChangeListener);
			IConfigurationElement[] ce;
			String browserClass;
			String browserName;
			for (int i = 0; i < extensions.length; i++)
			{
				ce = extensions[i].getConfigurationElements();
				for (int j = 0; j < ce.length; j++)
				{
					browserClass = ce[j].getAttribute(UnifiedEditorsPlugin.CLASS_ATTR);
					browserName = getBrowserLabel(ce[j]);
					if (browserClass != null && browserName != null)
					{
						browserList.add(ce[j]);
					}
				}
			}
			Collections.sort(browserList, new Comparator<IConfigurationElement>()
			{
				public int compare(IConfigurationElement o1, IConfigurationElement o2)
				{
					String name1 = getBrowserLabel(o1);
					String name2 = getBrowserLabel(o2);
					if (name1 != null && name2 != null)
					{
						// This is to put the browsers in reverse alphabetical order so Firefox is always last since
						// that affects loading on Mac OS X. Basically 'Safari' must come before 'Firefox'
						if (Platform.getOS().equals(Platform.OS_MACOSX))
						{
							return name2.compareTo(name1);
						}

						// For all other platforms do standard alphabetical order
						return name1.compareTo(name2);
					}
					return 0;
				}
			});
			computeShowList();
		}

		return showList;
	}

	/**
	 * Gets all the browsers that exist in the extension point
	 * 
	 * @return - list of browser labels
	 */
	public static List<String> getAllBrowserLabels()
	{
		if (allBrowsers == null)
		{
			allBrowsers = new ArrayList<String>();
			IExtensionRegistry reg = Platform.getExtensionRegistry();
			IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.BROWSER_EXTENSION_POINT);
			IExtension[] extensions = ep.getExtensions();
			IConfigurationElement[] ce;
			String browserClass;
			String browserName;
			for (int i = 0; i < extensions.length; i++)
			{
				ce = extensions[i].getConfigurationElements();
				for (int j = 0; j < ce.length; j++)
				{
					browserClass = ce[j].getAttribute(UnifiedEditorsPlugin.CLASS_ATTR);
					browserName = BrowserExtensionLoader.getBrowserLabel(ce[j]);
					if (browserClass != null && browserName != null)
					{
						allBrowsers.add(browserName);
					}
				}
			}
			Collections.sort(allBrowsers, new Comparator<String>()
			{
				public int compare(String name1, String name2)
				{
					if (name1 != null && name2 != null)
					{
						return name1.compareTo(name2);
					}
					return 0;
				}
			});
		}
		return allBrowsers;
	}
}
