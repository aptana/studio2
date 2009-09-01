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
package com.aptana.ide.editor.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Listener;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.ContributedBrowser;
import com.aptana.ide.editors.unified.IBrowserDecorator;

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
			if (IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE.equals(event.getProperty()))
			{
				computeShowList();
			}
		}
	};

	private static Map<String, IBrowserDecorator> decorators = null;

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
	 * Gets a decorator for a browser
	 * 
	 * @param browser
	 * @param listener
	 */
	public static void getDecorator(ContributedBrowser browser, Listener listener)
	{
		if (decorators == null)
		{
			decorators = new HashMap<String, IBrowserDecorator>();
			IExtensionRegistry reg = Platform.getExtensionRegistry();
			IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.BROWSER_EXTENSION_POINT);
			IExtension[] extensions = ep.getExtensions();
			for (int i = 0; i < extensions.length; i++)
			{
				IConfigurationElement[] ce = extensions[i].getConfigurationElements();
				for (int j = 0; j < ce.length; j++)
				{
					if (UnifiedEditorsPlugin.DECORATOR_ELEMENT.equals(ce[j].getName()))
					{
						String decoratorClass = ce[j].getAttribute(UnifiedEditorsPlugin.CLASS_ATTR);
						String browserClassName = ce[j].getAttribute(UnifiedEditorsPlugin.BROWSER_ELEMENT);
						if (decoratorClass != null && browserClassName != null)
						{
							try
							{
								Object obj = ce[j].createExecutableExtension(UnifiedEditorsPlugin.CLASS_ATTR);
								if (obj instanceof IBrowserDecorator)
								{
									decorators.put(browserClassName, (IBrowserDecorator) obj);
								}
							}
							catch (CoreException e)
							{
							}
						}

					}
				}
			}
		}
		if (browser != null && listener != null)
		{
			String name = browser.getBrowserType();
			if (name != null && decorators.containsKey(name))
			{
				decorators.get(name).getBrowserDecorator(browser, listener);
			}
		}
	}

	/**
	 * Computes an alpabetized list of browsers to show previews for
	 */
	private static void computeShowList()
	{
		showList.clear();
		String browserString = HTMLPlugin.getDefault().getPreferenceStore().getString(
				IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE);
		String[] browsers_names = browserString.split(","); //$NON-NLS-1$

		// See comment below about mac fix
		IConfigurationElement safari = null;

		for (int i = 0; i < browserList.size(); i++)
		{
			IConfigurationElement element = (IConfigurationElement) browserList.get(i);

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
				IConfigurationElement element = (IConfigurationElement) showList.get(0);
				IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
				store.removePropertyChangeListener(propertyChangeListener);
				if (getBrowserLabel(element).equals("Firefox")) //$NON-NLS-1$
				{
					if (safari != null)
					{
						showList.add(0, safari);
						// Add safari back to prefs
						store.setValue(IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE, browserString
								+ ",Safari"); //$NON-NLS-1$
					}
					else
					{
						showList.clear();
						// Clear prefs completely
						store.setValue(IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE, ""); //$NON-NLS-1$
					}
				}
				store.addPropertyChangeListener(propertyChangeListener);
			}
		}
	}

	/**
	 * Gets a list of browser with given names
	 * 
	 * @param names
	 * @return - browser configuration elements
	 */
	public static List<IConfigurationElement> getBrowsers(String[] names)
	{
		List<IConfigurationElement> browsers = new ArrayList<IConfigurationElement>();
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.BROWSER_EXTENSION_POINT);
		IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++)
		{
			IConfigurationElement[] ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length; j++)
			{
				if (UnifiedEditorsPlugin.BROWSER_ELEMENT.equals(ce[j].getName()))
				{
					String browserClass = ce[j].getAttribute(UnifiedEditorsPlugin.CLASS_ATTR);
					String name = getBrowserLabel(ce[j]);
					if (browserClass != null && name != null)
					{
						for (int k = 0; k < names.length; k++)
						{
							if (name.equals(names[k]))
							{
								browsers.add(ce[j]);
							}
						}
					}
				}
			}
		}
		return browsers;
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
			HTMLPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(propertyChangeListener);
			for (int i = 0; i < extensions.length; i++)
			{
				IConfigurationElement[] ce = extensions[i].getConfigurationElements();
				for (int j = 0; j < ce.length; j++)
				{
					String browserClass = ce[j].getAttribute(UnifiedEditorsPlugin.CLASS_ATTR);
					String name = getBrowserLabel(ce[j]);
					if (browserClass != null && name != null)
					{
						browserList.add(ce[j]);
					}
				}
			}
			Collections.sort(browserList, new Comparator<IConfigurationElement>()
			{

				public int compare(IConfigurationElement o1, IConfigurationElement o2)
				{
					String name1 = getBrowserLabel((IConfigurationElement) o1);
					String name2 = getBrowserLabel((IConfigurationElement) o2);
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

		if(browserList != null)
		{
			for (int j = 0; j < browserList.size(); j++)
			{
				IConfigurationElement element = (IConfigurationElement) browserList.get(j);
				String name = BrowserExtensionLoader.getBrowserLabel(element);
				IdeLog.logInfo(HTMLPlugin.getDefault(), StringUtils.format(Messages.BrowserExtensionLoader_INF_Preview, new String[] {String.valueOf(j), name}));
			}
		}
		
		return showList;
	}

	/**
	 * Gets the browser image
	 * 
	 * @param label
	 * @return - image or null
	 */
	public static Image getBrowserImage(String label)
	{
		Image image = null;
		if (Messages.BrowserExtensionLoader_IPhone.equalsIgnoreCase(label))
		{
			image = HTMLPlugin.getImage("icons/iphone-nature.png"); //$NON-NLS-1$
		}
		else if (Messages.BrowserExtensionLoader_Firefox.equalsIgnoreCase(label))
		{
			image = HTMLPlugin.getImage("icons/firefox_icon.png"); //$NON-NLS-1$
		}
		else if (Messages.BrowserExtensionLoader_IE.equalsIgnoreCase(label))
		{
			image = HTMLPlugin.getImage("icons/ie_icon.png"); //$NON-NLS-1$
		}
		else if (Messages.BrowserExtensionLoader_Default.equalsIgnoreCase(label))
		{
			image = HTMLPlugin.getImage("icons/firefox_icon.png"); //$NON-NLS-1$
		}
		else if (Messages.BrowserExtensionLoader_Safari.equalsIgnoreCase(label))
		{
			image = HTMLPlugin.getImage("icons/safari_icon.png"); //$NON-NLS-1$
		}
		return image;
	}

	/**
	 * Gets all the browsers that exist in the extension point
	 * 
	 * @return - list of browser labels
	 */
	public static List getAllBrowserLabels()
	{
		if (allBrowsers == null)
		{
			allBrowsers = new ArrayList<String>();
			IExtensionRegistry reg = Platform.getExtensionRegistry();
			IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.BROWSER_EXTENSION_POINT);
			IExtension[] extensions = ep.getExtensions();
			for (int i = 0; i < extensions.length; i++)
			{
				IConfigurationElement[] ce = extensions[i].getConfigurationElements();
				for (int j = 0; j < ce.length; j++)
				{
					String browserClass = ce[j].getAttribute(UnifiedEditorsPlugin.CLASS_ATTR);
					String name = BrowserExtensionLoader.getBrowserLabel(ce[j]);
					if (browserClass != null && name != null)
					{
						allBrowsers.add(name);
					}
				}
			}
			Collections.sort(allBrowsers, new Comparator<String>()
			{

				public int compare(String o1, String o2)
				{
					String name1 = (String) o1;
					String name2 = (String) o2;
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
