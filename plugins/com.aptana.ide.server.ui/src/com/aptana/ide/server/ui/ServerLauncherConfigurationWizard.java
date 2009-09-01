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
package com.aptana.ide.server.ui;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.server.core.IServerEnvironmentConfigurator;
import com.aptana.ide.server.core.IServerLauncher;

/**
 * Server launcher configuration wizard.
 * @author Denis Denisenko
 */
public class ServerLauncherConfigurationWizard extends Wizard
{
	
	/**
	 * Welcome page.
	 * 
	 * @author Denis Denisenko
	 */
	private class WelcomePage extends WizardPage
	{
		/**
		 * Main composite.
		 */
		private Composite main;

		protected WelcomePage(String servername)
		{
			super(Messages.ServerLauncherConfigurationWizard_Title);
			this.setTitle(Messages.ServerLauncherConfigurationWizard_TitleMessage);
		}

		/**
		 * {@inheritDoc}
		 */
		public void createControl(Composite parent)
		{
			main = new Composite(parent, SWT.NONE);
			main.setLayout(new GridLayout());
			
			Label label1 = new Label(main, SWT.NONE);
			Label label2 = new Label(main, SWT.NONE);
			label1.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
			label2.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
			
			String buttonName = null;
			if (ServerLauncherConfigurationWizard.this.getPageCount() > 1)
			{
				buttonName = Messages.ServerLauncherConfigurationWizard_LBL_Next;
			}
			else
			{
				buttonName = Messages.ServerLauncherConfigurationWizard_LBL_Finish;
			}
				
			String message1 = Messages.ServerLauncherConfigurationWizard_Message1;
			String message2 = MessageFormat
                    .format(
                            Messages.ServerLauncherConfigurationWizard_Message2,
                            buttonName);
			label1.setText(message1);
			label2.setText(message2);
			
			setControl(main);
		}

		/**
		 * {@inheritDoc}
		 */
		public Control getControl()
		{
			return main;
		}	
	}
	
	/**
	 * Map of launchers that require configuration to the wizard page representation 
	 * of launcher configurators.
	 */
	private Map<IServerLauncher, WizardPage> launchers = new HashMap<IServerLauncher, WizardPage>();

	/**
	 * ServerPatchingWizard constructor.
	 * 
	 * @param serverName - server name.
	 * @param launchers - launchers to configure.
	 */
	public ServerLauncherConfigurationWizard(String serverName, List<IServerLauncher> launchers)
	{
		addPage(new WelcomePage(serverName));
		
		for (IServerLauncher launcher : launchers)
		{
			if (!launcher.isConfigured())
			{
				IServerEnvironmentConfigurator configurator = launcher.getConfigurator();
				if (configurator == null || !configurator.requiresAdditionalInformation())
				{
					continue;
				}
				
				WizardPage page = (WizardPage) configurator.getAdapter(WizardPage.class);
				if (page == null)
				{
					IdeLog.logError(ServerUIPlugin.getDefault(), 
							Messages.ServerLauncherConfigurationWizard_ERR_GetConfigurator);
					continue;
				}
				
				this.launchers.put(launcher, page);
				addPage(page);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean performFinish()
	{
		for (Entry<IServerLauncher, WizardPage> entry : launchers.entrySet())
		{
			IServerLauncher launcher = entry.getKey();
			WizardPage page = entry.getValue();
			launcher.getConfigurator().configure(page);
		}
		
		return true;
	}
}
