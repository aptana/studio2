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
package com.aptana.ide.server.ui.views.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerPatcher;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.ServerPatchers;
import com.aptana.ide.server.core.impl.servers.ServerManager;
import com.aptana.ide.server.ui.IConfigurationDialog;
import com.aptana.ide.server.ui.ServerDialogPageRegistry;
import com.aptana.ide.server.ui.ServerImagesRegistry;
import com.aptana.ide.server.ui.ServerPatchingWizard;
import com.aptana.ide.server.ui.ServerUIPlugin;
import com.aptana.ide.server.ui.views.Messages;

/**
 * @author Pavel Petrochenko
 */
public final class NewServerAction extends Action
{
	private final IServerType type;
	private IServer createdServer;

	/**
	 * @param type
	 */
	public NewServerAction(IServerType type)
	{
		this.createdServer = null;
		this.type = type;
		this.setImageDescriptor(ServerImagesRegistry.getInstance().getDescriptor(type));
		String format = StringUtils.format(Messages.ServersView_CONNECT, type.getName());
		this.setToolTipText(format);
		this.setText(format);
	}

	/**
	 * Gets the id of the created server
	 * 
	 * @return - server id
	 */
	public String getCreatedServerID()
	{
		if (createdServer != null)
		{
			return createdServer.getId();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{
		try
		{
			ICanAdd adapter = (ICanAdd) type.getAdapter(ICanAdd.class);
			if (adapter != null)
			{
				adapter.doAdd();
				return;
			}
			IConfigurationDialog dialog = ServerDialogPageRegistry.getInstance().getDialog(type.getId());
			dialog.setConfiguration(ServerManager.getInstance().getInitialServerConfiguration(type.getId()));
			dialog.setEdit(false);

			Dialog dialog3 = dialog.getDialog();
			int open = dialog3.open();
			if (open == Dialog.OK)
			{
				IAbstractConfiguration configuration = dialog.getConfiguration();
				createdServer = ServerManager.getInstance().addServer(configuration);
				//checking if server requires patching
				List<IServerPatcher> patchers = ServerPatchers.getPatchers(createdServer.getServerType().getId());
				
				if (patchers != null && patchers.size() != 0)
				{
					List<IServerPatcher> requiredPatchers = new ArrayList<IServerPatcher>();
					
					for (IServerPatcher patcher : patchers)
					{
						if (patcher.checkPatchRequired(createdServer))
						{
							requiredPatchers.add(patcher);
						}
					}
					
					//if patching is required
					if (requiredPatchers.size() != 0)
					{
						
						ServerPatchingWizard wizard = new ServerPatchingWizard(createdServer.getName(),
								requiredPatchers);
						WizardDialog wizardDialog = new WizardDialog(Display.getDefault().getActiveShell(),
								(IWizard) wizard);
						wizardDialog.create();
						
						int result = wizardDialog.open();
						if (result == Window.OK)
						{
							for (IServerPatcher patcher : requiredPatchers)
							{
								if (patcher.isConfigured())
								{
									patcher.applyPatch(createdServer);
								}
							}
						}
					}
				}
			}
		}
		
		catch (Exception e)
		{
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error while adding server", e.getMessage()); //$NON-NLS-1$
			IdeLog.log(ServerUIPlugin.getDefault(), IStatus.ERROR, "exception while opening new server dialog", e); //$NON-NLS-1$
		}
	}
}