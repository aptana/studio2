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
package com.aptana.ide.server.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

import com.aptana.ide.server.core.HttpServerLaunchConfiguration;
import com.aptana.ide.server.core.IHttpLaunchConfigurationConstants;
import com.aptana.ide.server.ui.IServerUIConstants;

/**
 * HttpServerLaunchConfigurationHelper
 */
public final class HttpServerLaunchConfigurationHelper
{
	/**
	 * HttpServerLaunchConfigurationHelper
	 */
	private HttpServerLaunchConfigurationHelper()
	{
	}
	
	/**
	 * createInitialLaunchConfiguration
	 * 
	 * @return ILaunchConfiguration
	 * @throws CoreException
	 */
	public static ILaunchConfiguration createInitialLaunchConfiguration() throws CoreException
	{

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType(IHttpLaunchConfigurationConstants.HTTP_SERVER_LAUNCH_CONFIGURATION_TYPE_ID);

		ILaunchConfigurationWorkingCopy wc = type.newInstance(null, DebugPlugin.getDefault().getLaunchManager()
				.generateUniqueLaunchConfigurationNameFrom(IServerUIConstants.DEFAULT_SERVER_NAME));

		HttpServerLaunchConfiguration config = new HttpServerLaunchConfiguration();
		config.setDefaults(wc);

		/*
		 * //add the local server to the list of favorites List list =
		 * wc.getAttribute(IDebugUIConstants.ATTR_FAVORITE_GROUPS, (List)null); if (list == null) { list = new
		 * ArrayList(); } ILaunchGroup group = DebugUITools.getLaunchGroup(wc, "run"); list.add(group.getIdentifier());
		 * wc.setAttribute(IDebugUIConstants.ATTR_FAVORITE_GROUPS, list);
		 */

		wc.doSave();
		return wc;
	}

	/**
	 * getDefaultHttpServerLaunchConfiguration
	 * 
	 * @return ILaunchConfiguration
	 * @throws CoreException
	 */
	public static ILaunchConfiguration getDefaultHttpServerLaunchConfiguration() throws CoreException
	{
		return getDefaultHttpServerLaunchConfiguration(true);
	}

	/**
	 * getDefaultHttpServerLaunchConfiguration
	 * 
	 * @param create
	 * @return ILaunchConfiguration
	 * @throws CoreException
	 */
	public static ILaunchConfiguration getDefaultHttpServerLaunchConfiguration(boolean create) throws CoreException
	{
		ILaunchConfiguration defaultConfiguration = null;
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchType = launchManager
				.getLaunchConfigurationType(IServerUIConstants.HTTP_SERVER_LAUNCH_TYPE_ID);
		if (launchType != null)
		{
			ILaunchConfiguration[] configs = launchManager.getLaunchConfigurations(launchType);
			for (int i = 0; defaultConfiguration == null && i < configs.length; i++)
			{
				if (configs[i].getName().equals(IServerUIConstants.DEFAULT_SERVER_NAME))
				{
					defaultConfiguration = configs[i];
				}

			}
		}
		if (defaultConfiguration == null && create)
		{
			defaultConfiguration = createInitialLaunchConfiguration();
		}
		return defaultConfiguration;
	}
}
