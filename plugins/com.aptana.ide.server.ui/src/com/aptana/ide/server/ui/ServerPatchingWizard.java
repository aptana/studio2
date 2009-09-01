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
import com.aptana.ide.server.core.IServerPatcher;

/**
 * Server patching wizard.
 * The wizard does not perform the patching itself, instead it asks user whether patching is acceptable
 * and configures patchers in this case.
 * 
 * @author Denis Denisenko
 */
public class ServerPatchingWizard extends Wizard
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
			super(Messages.ServerPatchingWizard_0);
			setTitle(Messages.ServerPatchingWizard_1);
			setHelpAvailable(false);
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
			Label label3 = new Label(main, SWT.NONE);
			label1.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
			label2.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
			label3.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
			
			String buttonName = null;
			if (ServerPatchingWizard.this.getPageCount() > 1)
			{
				buttonName = Messages.ServerPatchingWizard_2;
			}
			else
			{
				buttonName = Messages.ServerPatchingWizard_3;
			}
				
			String message1 = Messages.ServerPatchingWizard_4;
			String message2 = Messages.ServerPatchingWizard_5 + buttonName + Messages.ServerPatchingWizard_6 + Messages.ServerPatchingWizard_7 + 
				Messages.ServerPatchingWizard_8;
			String message3 = Messages.ServerPatchingWizard_9;
			label1.setText(message1);
			label2.setText(message2);
			label3.setText(message3);
			
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
	 * Map of patchers that require configuration to the wizard page representation 
	 * of patcher configurators.
	 */
	private Map<IServerPatcher, WizardPage> patchers = new HashMap<IServerPatcher, WizardPage>();

	/**
	 * ServerPatchingWizard constructor.
	 * 
	 * @param serverName - server name.
	 * @param patchers - patchers to configure.
	 */
	public ServerPatchingWizard(String serverName, List<IServerPatcher> patchers)
	{
		addPage(new WelcomePage(serverName));
		
		for (IServerPatcher patcher : patchers)
		{
			if (!patcher.isConfigured())
			{
				IServerEnvironmentConfigurator configurator = patcher.getConfigurator();
				if (configurator == null || !configurator.requiresAdditionalInformation())
				{
					continue;
				}
				
				WizardPage page = (WizardPage) configurator.getAdapter(WizardPage.class);
				if (page == null)
				{
					IdeLog.logError(ServerUIPlugin.getDefault(), 
							Messages.ServerPatchingWizard_10);
					continue;
				}
				
				this.patchers.put(patcher, page);
				addPage(page);
			}
		}
		setDefaultPageImageDescriptor(ServerUIPlugin.getImageDescriptor("icons/server/wizban/xampp_wiz.png")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean performFinish()
	{
		for (Entry<IServerPatcher, WizardPage> entry : patchers.entrySet())
		{
			IServerPatcher patcher = entry.getKey();
			WizardPage page = entry.getValue();
			patcher.getConfigurator().configure(page);
		}
		
		return true;
	}
}
