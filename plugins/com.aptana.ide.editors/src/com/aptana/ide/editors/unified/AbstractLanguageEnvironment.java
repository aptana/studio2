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
package com.aptana.ide.editors.unified;

import java.net.URL;
import java.util.Hashtable;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.parsing.IRuntimeEnvironment;

/**
 * Base class defining what consititutes a language environment
 * @author ingo
 *
 */
public abstract class AbstractLanguageEnvironment implements ILanguageEnvironment
{
	/**
	 * METADATA_ID
	 */
	public static final String METADATA_ID = "metadata"; //$NON-NLS-1$

	/**
	 * TAG_BINARY_FILE
	 */
	public static final String TAG_BINARY_FILE = "binary-file"; //$NON-NLS-1$

	/**
	 * TAG_XML_FILE
	 */
	public static final String TAG_XML_FILE = "xml-file"; //$NON-NLS-1$

	/**
	 * ATTR_FILE_PATH
	 */
	public static final String ATTR_FILE_PATH = "path"; //$NON-NLS-1$

	/**
	 * ATTR_USER_AGENT
	 */
	public static final String ATTR_USER_AGENT = "user-agent"; //$NON-NLS-1$

	/**
	 * ATTR_ICON
	 */
	public static final String ATTR_ICON = "icon"; //$NON-NLS-1$

	/**
	 * ATTR_MIME_TYPE
	 */
	public static final String ATTR_MIME_TYPE = "mime-type"; //$NON-NLS-1$

	/**
	 * Return the current object environment
	 * 
	 * @return RuntimeEnvironment
	 */
	public abstract IRuntimeEnvironment getRuntimeEnvironment();

	/**
	 * cleanEnvironment
	 */
	public abstract void cleanEnvironment();

	/**
	 * loadEnvironment
	 */
	public abstract void loadEnvironment();
	
	/**
	 * addFromExtension
	 * 
	 * @param ids
	 * @param elementName
	 */
	protected void addFromExtension(Hashtable<URL, String> ids, String elementName, String mimeType)
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		// String envsToLoad = HTMLPlugin.getDefault().getPreferenceStore().getString(
		// IPreferenceConstants.LOADED_ENVIRONMENTS);
		// String[] envs = envsToLoad.split(",");
		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(UnifiedEditorsPlugin.ID, METADATA_ID);

			if (extensionPoint != null)
			{
				IExtension[] extensions = extensionPoint.getExtensions();

				for (int i = 0; i < extensions.length; i++)
				{
					IExtension extension = extensions[i];
					IConfigurationElement[] elements = extension.getConfigurationElements();

					for (int j = 0; j < elements.length; j++)
					{
						IConfigurationElement element = elements[j];
						if (element.getName().equals(elementName))
						{
							String resourceName = element.getAttribute(ATTR_FILE_PATH);
							String userAgent = element.getAttribute(ATTR_USER_AGENT);
							String compareMimeType = element.getAttribute(ATTR_MIME_TYPE);
							if (userAgent != null && compareMimeType.equals(mimeType))
							{
								// boolean load = false;
								// for (int k = 0; k < envs.length; k++)
								// {
								// if (userAgent.equals(envs[k]))
								// {
								// load = true;
								// break;
								// }
								// }
								// if (load)
								// {
								IExtension ext = element.getDeclaringExtension();
								String pluginId = ext.getNamespaceIdentifier();
								Bundle bundle = Platform.getBundle(pluginId);
								URL resource = bundle.getResource(resourceName);

								ids.put(resource, userAgent);
								// }
							}
						}
					}
				}
			}
		}
	}

}
