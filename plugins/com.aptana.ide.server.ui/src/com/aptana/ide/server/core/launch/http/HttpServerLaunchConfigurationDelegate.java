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
package com.aptana.ide.server.core.launch.http;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.HttpServerLaunchConfiguration;
import com.aptana.ide.server.core.ServerCorePlugin;
import com.aptana.ide.server.core.ServerManager;
import com.aptana.ide.server.http.HttpServerRunnable;

/**
 * @author Spike Washburn
 */
public class HttpServerLaunchConfigurationDelegate extends AbstractHttpServerLaunchConfigurationDelegate
{
	/**
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration,
	 *      java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode, final ILaunch launch, IProgressMonitor monitor)
			throws CoreException
	{

		HttpServerLaunchConfiguration config = new HttpServerLaunchConfiguration(configuration);

		String baseUrl;
		if (config.getServerType() == HttpServerLaunchConfiguration.SERVER_INTERNAL)
		{
			HttpServerRunnable serverRunnable = new HttpServerRunnable();
			ServerManager.getInstance().launchServer(serverRunnable, configuration, mode, launch, monitor);
			baseUrl = serverRunnable.getBaseURL();
		}
		else
		{
			baseUrl = config.getExternalBaseUrl();
		}
		if (!baseUrl.equals(StringUtils.EMPTY))
		{
			try
			{
				showStartPage(baseUrl, configuration);
			}
			catch (IOException e)
			{
				ServerCorePlugin.logError("Error launching browser", e); //$NON-NLS-1$
			}
		}

		monitor.done();
	}}
