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
package com.aptana.ide.editor.html.preview;

import java.text.MessageFormat;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.html.HTMLPlugin;

/**
 * PreviewConfigurations
 * 
 * @author Denis Denisenko
 */
public final class PreviewConfigurations
{
	/**
	 * Extension point name.
	 */
	private static final String EXTENSION_POINT_NAME = "previewConfiguration"; //$NON-NLS-1$

	/**
	 * Configuration element name.
	 */
	private static final String CONFIGURATION_ELEMENT_NAME = "configuration"; //$NON-NLS-1$

	/**
	 * Class attribute name.
	 */
	private static final String CLASS_ATTRIBUTE_NAME = "class"; //$NON-NLS-1$

	/**
	 * File extension attribute name.
	 */
	private static final String FILE_EXTENSION_ATTRIBUTE_NAME = "fileExtension"; //$NON-NLS-1$

	/**
	 * Priority attribute name.
	 */
	private static final String PRIORITY_ATTRIBUTE_NAME = "priority"; //$NON-NLS-1$

	/**
	 * Gets preview configuration by file extension.
	 * 
	 * @param fileExtension
	 * @return patchers.
	 */
	public static IPreviewConfiguration getConfiguration(String fileExtension)
	{

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep = registry.getExtensionPoint(HTMLPlugin.ID, EXTENSION_POINT_NAME);

		int maxPriority = -1;
		IPreviewConfiguration toReturn = null;

		if (ep != null)
		{
			IExtension[] extensions = ep.getExtensions();

			for (int i = 0; i < extensions.length; i++)
			{
				IExtension extension = extensions[i];
				IConfigurationElement[] elements = extension.getConfigurationElements();

				for (int j = 0; j < elements.length; j++)
				{
					IConfigurationElement element = elements[j];
					String elementName = element.getName();

					if (elementName.equals(CONFIGURATION_ELEMENT_NAME))
					{
						String currentExtension = element.getAttribute(FILE_EXTENSION_ATTRIBUTE_NAME);
						if (!currentExtension.equals(fileExtension))
						{
							continue;
						}

						int priority = 0;
						String priorityString = element.getAttribute(PRIORITY_ATTRIBUTE_NAME);
						if (priorityString != null)
						{
							try
							{
								priority = Integer.parseInt(priorityString);
							}
							catch (NumberFormatException ex)
							{
								IdeLog.logError(HTMLPlugin.getDefault(), MessageFormat
                                        .format(
                                                Messages.PreviewConfigurations_ERR_WrongFormat,
                                                elementName));
							}
						}

						if (priority <= maxPriority)
						{
							continue;
						}

						try
						{
							toReturn = (IPreviewConfiguration) element.createExecutableExtension(CLASS_ATTRIBUTE_NAME);

						}
						catch (CoreException e)
						{
							IdeLog.logError(HTMLPlugin.getDefault(), Messages.PreviewConfigurations_ERR_CreateConfig, e);
						}
					}
				}
			}
		}

		return toReturn;
	}

	/**
	 * PreviewConfigurations private constructor.
	 */
	private PreviewConfigurations()
	{
	}
}