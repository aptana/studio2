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
package com.aptana.ide.core.ui.browser;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.CoreUIPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class BrowserRegistry
{

	/**
	 * EXTENSION_NAME
	 */
	public static final String EXTENSION_NAME = "browser"; //$NON-NLS-1$

	/**
	 * EXTENSION_POINT
	 */
	public static final String EXTENSION_POINT = CoreUIPlugin.ID + "." + EXTENSION_NAME; //$NON-NLS-1$

	/**
	 * CLASS_ATTRIBUTE
	 */
	public static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

	/**
	 * ID_ATTRIBUTE
	 */
	public static final String ID_ATTRIBUTE = "id"; //$NON-NLS-1$

	private static BrowserRegistry registry = null;

	private Map<String, IConfigurationElement> browsers;

	private BrowserRegistry()
	{
		browsers = new HashMap<String, IConfigurationElement>();
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT);
		for (IConfigurationElement element : elements)
		{
			if (EXTENSION_NAME.equals(element.getName()))
			{
				String className = element.getAttribute(CLASS_ATTRIBUTE);
				String id = element.getAttribute(ID_ATTRIBUTE);
				if (className != null && id != null)
				{
					browsers.put(id, element);
				}
			}
		}
	}

	/**
	 * Gets the browser for the given id
	 * 
	 * @param id
	 * @return - browser or null if none found or couldn't be instantiated
	 */
	public IBrowser getBrowser(String id)
	{
		IBrowser browser = null;
		if (browsers.containsKey(id))
		{
			IConfigurationElement element = browsers.get(id);
			Object obj;
			try
			{
				obj = element.createExecutableExtension(CLASS_ATTRIBUTE);
				if (obj instanceof IBrowser)
				{
					browser = (IBrowser) obj;
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), Messages.BrowserRegistry_ERR_ErrorCreatingBrowser, e);
			}
		}
		return browser;
	}

	/**
	 * Gets the registry
	 * 
	 * @return - registry
	 */
	public synchronized static BrowserRegistry getRegistry()
	{
		if (registry == null)
		{
			registry = new BrowserRegistry();
		}
		return registry;
	}
}
