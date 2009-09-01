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
package com.aptana.ide.server.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.server.ServerCore;

/**
 * Server launchers registry.
 * @author Denis Denisenko
 */
public final class ServerLaunchers
{
	/**
	 * Extension point name.
	 */
	private static final String EXTENSION_POINT_NAME = "serverLaunchers"; //$NON-NLS-1$
	
	/**
	 * Info element name.
	 */
	private static final String INFO_ELEMENT_NAME = "launcher"; //$NON-NLS-1$
	
	/**
	 * Class attribute.
	 */
	private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$
	
	/**
	 * OS attribute.
	 */
	private static final String OS_ATTRIBUTE = "OpSystem"; //$NON-NLS-1$
	
	/**
	 * Type ID attribute.
	 */
	private static final String TYPE_ATTRIBUTE = "serverTypeID"; //$NON-NLS-1$
	
	/**
	 * Gets server launchers for the current OS and specified server type.
	 * @param serverTypeID - server type ID.
	 * @return launchers.
	 */
	public static List<IServerLauncher> getLaunchers(String serverTypeID)
	{
		List<IServerLauncher> result = new ArrayList<IServerLauncher>();
		
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		final IConfigurationElement[] elements = registry
				.getConfigurationElementsFor(ServerCore.PLUGIN_ID,
						EXTENSION_POINT_NAME);

		if (elements == null || elements.length == 0)
		{
			return null;
		}
		
		String osName = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$

		for (final IConfigurationElement element : elements)
		{
			if (INFO_ELEMENT_NAME.equals(element.getName()))
			{
				String os = element.getAttribute(OS_ATTRIBUTE);
				String serverType = element.getAttribute(TYPE_ATTRIBUTE);
				if (osName.startsWith(os) && serverTypeID.equals(serverType))
				{
					try
					{
						IServerLauncher launcher = 
							(IServerLauncher) element.createExecutableExtension(CLASS_ATTRIBUTE);
						result.add(launcher);
					} 
					catch (CoreException e)
					{
						IdeLog.logError(ServerCore.getDefault(), ServerMessages.ServerLaunchers_ERR_Create, e);
					}
				}
			}
		}
		
		return result;
	}

	/**
	 * ServerLaunchers private constructor.
	 */
	private ServerLaunchers()
	{
	}
}
