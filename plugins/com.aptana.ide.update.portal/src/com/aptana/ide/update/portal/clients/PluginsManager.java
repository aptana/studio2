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
package com.aptana.ide.update.portal.clients;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.update.Activator;
import com.aptana.ide.update.FeatureUtil;
import com.aptana.ide.update.manager.IPluginManager;
import com.aptana.ide.update.manager.Plugin;
import com.aptana.ide.update.manager.PluginManagerException;

/**
 * @author cwilliams
 */
class PluginsManager
{

	public static void install(String pluginId)
	{
		Plugin plugin = FeatureUtil.findRemotePlugin(pluginId);
		if (plugin == null)
		{
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					Messages.PluginsManager_ERR_TTL_Unable_find_plugin,
					Messages.PluginsManager_ERR_MSG_Unable_find_plugin + pluginId);
			return;
		}
		if (FeatureUtil.isInstalled(pluginId))
		{
			MessageDialog.openInformation(Display.getDefault().getActiveShell(),
					Messages.PluginsManager_TTL_Plugin_already_installed,
					Messages.PluginsManager_INF_MSG_Feature_selected_already_installed);
			return;
		}
		try
		{
			getPluginManager().install(new Plugin[] { plugin }, new NullProgressMonitor());
		}
		catch (PluginManagerException e)
		{
			IdeLog.logError(Activator.getDefault(), e.getMessage(), e);
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					Messages.PluginsManager_ERR_TTL_Unable_install_plugin,
					Messages.PluginsManager_ERR_MSG_Unable_install_plugin + pluginId);
		}
	}

	private static IPluginManager getPluginManager()
	{
		return Activator.getDefault().getPluginManager();
	}

}
