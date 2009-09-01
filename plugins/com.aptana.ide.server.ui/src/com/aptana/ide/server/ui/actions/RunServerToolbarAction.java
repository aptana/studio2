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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.debug.internal.ui.launchConfigurations.OrganizeFavoritesAction;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction;
import org.eclipse.debug.ui.actions.LaunchAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.server.core.HttpServerLaunchConfiguration;
import com.aptana.ide.server.ui.IServerUIConstants;
import com.aptana.ide.server.ui.ServerUIPlugin;
import com.aptana.ide.server.ui.StartPage;

/**
 * Run drop-down history/favorites action.
 */
public class RunServerToolbarAction extends AbstractLaunchToolbarAction
{
	ILaunchConfigurationListener launchListener;
	String launchMode;

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window)
	{
		super.init(window);
		updateLaunchStartPage();

		// register a listener to deal with updating the startup page if the launch configurations change
		launchListener = new ILaunchConfigurationListener()
		{
			public void launchConfigurationChanged(ILaunchConfiguration configuration)
			{
				updateLaunchStartPage();
			}

			public void launchConfigurationAdded(ILaunchConfiguration configuration)
			{
			}

			public void launchConfigurationRemoved(ILaunchConfiguration configuration)
			{
			}
		};
		DebugPlugin.getDefault().getLaunchManager().addLaunchConfigurationListener(launchListener);
	}

	/**
	 * RunServerToolbarAction
	 */
	public RunServerToolbarAction()
	{
		super(IServerUIConstants.ID_SERVER_RUN_LAUNCH_GROUP);
	}

	/**
	 * Launch the last launch, or open the launch config dialog if none.
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		launchMode = action.getActionDefinitionId();

		ILaunchConfiguration configuration = getLastLaunch();
		if (configuration == null)
		{
			try
			{
				launchDefaultServer();
			}
			catch (CoreException e)
			{
				IdeLog.logError(ServerUIPlugin.getDefault(), Messages.RunServerToolbarAction_ERR_CreateConfig, e);
				super.run(action);
			}
		}
		else
		{
			super.run(action);
		}
	}

	/**
	 * @see org.eclipse.debug.ui.actions.AbstractLaunchHistoryAction#getMode()
	 */
	protected String getMode()
	{
		if (launchMode == null)
		{
			launchMode = "run"; //$NON-NLS-1$
		}

		return launchMode;
	}

	/**
	 * Launches the default server if it already exists, otherwise it will automatically create and run it.
	 * 
	 * @throws CoreException
	 */
	private void launchDefaultServer() throws CoreException
	{
		ILaunchConfiguration defaultConfiguration = HttpServerLaunchConfigurationHelper
				.getDefaultHttpServerLaunchConfiguration();
		DebugUITools.launch(defaultConfiguration, getMode());
	}

	/**
	 * Fills the drop-down menu with favorites and launch history, launch shortcuts, and an action to open the launch
	 * configuration dialog.
	 * 
	 * @param menu
	 *            the menu to fill
	 */
	protected void fillMenu(Menu menu)
	{
		ILaunchConfiguration[] historyList = LaunchConfigurationManager.filterConfigs(getLaunchHistory().getHistory());
		ILaunchConfiguration[] favoriteList = LaunchConfigurationManager.filterConfigs(getLaunchHistory()
				.getFavorites());

		// Add favorites
		int accelerator = 1;
		for (int i = 0; i < favoriteList.length; i++)
		{
			ILaunchConfiguration launch = favoriteList[i];
			LaunchAction action = new LaunchAction(launch, getMode());
			addToMenu(menu, action, accelerator);
			accelerator++;
		}

		// Separator between favorites and history
		if (favoriteList.length > 0 && historyList.length > 0)
		{
			addSeparator(menu);
		}

		// Add history launches next
		for (int i = 0; i < historyList.length; i++)
		{
			ILaunchConfiguration launch = historyList[i];
			LaunchAction action = new LaunchAction(launch, getMode());
			addToMenu(menu, action, accelerator);
			accelerator++;
		}

		// Separator between history and common actions
		if (menu.getItemCount() > 0)
		{
			addSeparator(menu);
		}

		// addToMenu(menu, new LaunchShortcutsAction(getLaunchGroupIdentifier()), -1);
		addToMenu(menu, new OrganizeFavoritesAction(getLaunchGroupIdentifier()), -1);
		addToMenu(menu, getOpenDialogAction(), -1);
	}

	/**
	 * @see org.eclipse.debug.internal.ui.ILaunchHistoryChangedListener#launchHistoryChanged()
	 */
	public void launchHistoryChanged()
	{
		super.launchHistoryChanged();
		updateLaunchStartPage();
	}

	/**
	 * Synchronizes the startup page specified by the current launcher with the startup page decoration.
	 */
	private void updateLaunchStartPage()
	{
		ILaunchConfiguration currentLaunch = getLastLaunch();
		if (currentLaunch == null)
		{
			try
			{
				currentLaunch = HttpServerLaunchConfigurationHelper.getDefaultHttpServerLaunchConfiguration(false);
			}
			catch (CoreException e)
			{
			}
		}
		if (currentLaunch != null)
		{
			HttpServerLaunchConfiguration config = new HttpServerLaunchConfiguration(currentLaunch);

			if (config.getStartActionType() == HttpServerLaunchConfiguration.START_ACTION_SPECIFIC_PAGE)
			{
				String startPagePath = config.getStartPagePath();
				IResource startPage = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(startPagePath));
				StartPage.getInstance().setStartPageResource(startPage);
			}
			else
			{
				StartPage.getInstance().setStartPageResource(null);
			}
		}
		else
		{
			StartPage.getInstance().setStartPageResource(null);
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose()
	{
		super.dispose();
		DebugPlugin.getDefault().getLaunchManager().removeLaunchConfigurationListener(launchListener);
	}
}
