/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.server.jetty.ui.dialogs;

import java.util.HashSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.jetty.JettyServerTypeDelegate;
import com.aptana.ide.server.ui.IConfigurationDialog;
import com.aptana.ide.server.ui.views.ServerLabelProvider;

/**
 * @author Pavel Petrochenko
 */
public class JettyConfigurationDialog extends TitleAreaDialog implements IConfigurationDialog
{
	private Text nameText;
	private boolean isEdit;
	private IAbstractConfiguration configuration;
	private HashSet<String> serverNames = new HashSet<String>();
	private Text portText;
	private Text idText;
	private boolean wasError;
	private Text descText;
	private IServer server;

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), "com.aptana.ide.server.ui.servers_add_jetty"); //$NON-NLS-1$
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData dta2 = new GridData(GridData.FILL_HORIZONTAL);
		dta2.horizontalSpan = 2;
		titleBarSeparator.setLayoutData(dta2);
		Composite composite1 = new Composite(composite, SWT.NONE);
		composite1.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite1.setLayout(new GridLayout(2, false));
		this.setTitle(Messages.JettyConfigurationDialog_SERVER);
		this.getShell().setText(Messages.JettyConfigurationDialog_DIALOG_SHELLTITLE);
		this.setMessage(Messages.JettyConfigurationDialog_DESCRIPTION);
		composite.setLayout(layout);
		GridData cData = new GridData(GridData.FILL_BOTH);
		cData.widthHint = 500;
		composite.setLayoutData(cData);
		composite.setFont(parent.getFont());
		Label name = new Label(composite1, SWT.NONE);
		name.setText(Messages.JettyConfigurationDialog_SERVER_NAME);
		nameText = new Text(composite1, SWT.BORDER);
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameText.setText(configuration.getStringAttribute(IServer.KEY_NAME));
		ModifyListener modifyListener = new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				validate();

			}

		};
		Label descr = new Label(composite1, SWT.NONE);
		descr.setText(Messages.JettyConfigurationDialog_Description);

		descText = new Text(composite1, SWT.BORDER);
		descText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		descText.setText(configuration.getStringAttribute(IServer.KEY_DESCRIPTION));
		descText.addModifyListener(modifyListener);
		Label port = new Label(composite1, SWT.NONE);
		port.setText(Messages.JettyConfigurationDialog_PORT);
		portText = new Text(composite1, SWT.BORDER);
		portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label id = new Label(composite1, SWT.NONE);
		id.setText(Messages.JettyConfigurationDialog_APP_ID);
		portText.setText(configuration.getStringAttribute(IServer.KEY_PORT));
		Composite cm2 = new Composite(composite1, SWT.NONE);
		GridLayout ll2 = new GridLayout(2, false);
		ll2.marginWidth = 0;
		ll2.marginHeight = 0;
		cm2.setLayout(ll2);
		idText = new Text(cm2, SWT.BORDER | SWT.READ_ONLY);
		idText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		String root = configuration.getStringAttribute(JettyServerTypeDelegate.KEY_SERVERID);
		IResource findMember = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(new Path(root));

		if (findMember != null)
		{
			idText.setText(findMember.getLocation().toPortableString());
		}

		final Button choose = new Button(cm2, SWT.PUSH);
		choose.setText(Messages.JettyConfigurationDialog_CHOOSE_BUTTON_TITLE);
		choose.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ContainerSelectionDialog dlg = new ContainerSelectionDialog(choose.getShell(), ResourcesPlugin
						.getWorkspace().getRoot(), false, Messages.JettyConfigurationDialog_CONTAINER_DIALOG_MESSAGE);
				int open = dlg.open();
				if (open == Dialog.OK)
				{
					Object[] result = dlg.getResult();

					IPath cnt = (IPath) result[0];
					IResource c1 = ResourcesPlugin.getWorkspace().getRoot().findMember(cnt);
					idText.setText(c1.getLocation().toPortableString());
					validate();
				}
			}

		});
		cm2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		initDublicateNames();
		nameText.addModifyListener(modifyListener);
		portText.addModifyListener(modifyListener);
		validate();
		// Build the separator line
		return composite;
	}

	private void initDublicateNames()
	{
		IServer[] servers = ServerCore.getServerManager().getServers();

		for (IServer s : servers)
		{
			if (!s.getId().equals(configuration.getStringAttribute(IServer.KEY_ID)))
			{
				serverNames.add(s.getName());
			}
		}
	}

	/**
	 * @return is configuration valid
	 */
	public boolean validate()
	{
		String name = nameText.getText();
		if (name.length() == 0)
		{
			updateStatus(false, Messages.JettyConfigurationDialog_NAME_SHOULD_NOT_BE_EMPTY);
			return false;
		}
		if (serverNames.contains(name))
		{
			updateStatus(false, Messages.JettyConfigurationDialog_DUBLICATES_ARE_NOT_ALLOWED);
			return false;
		}
		String port = portText.getText();
		try
		{
			Integer i = Integer.parseInt(port);
			if (i < 0 || i > 65535)
			{
				updateStatus(false, Messages.JettyConfigurationDialog_PORT_RANGE);
				return false;
			}
		}
		catch (NumberFormatException e)
		{
			updateStatus(false, Messages.JettyConfigurationDialog_PORT_SHOULD_BE_NUMBER);
			return false;
		}
		String root = this.idText.getText();
		if (root.length() == 0)
		{
			updateStatus(false, Messages.JettyConfigurationDialog_CONTEXT_ROOT_VALIDATION_MESSAGE);
			return false;
		}
		updateStatus(true, null);
		return true;
	}

	/**
	 * @see org.eclipse.jface.dialogs.TrayDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createButtonBar(Composite parent)
	{
		Control createButtonBar = super.createButtonBar(parent);
		validate();
		return createButtonBar;
	}

	/**
	 * JettyConfigurationDialog constructor
	 */
	public JettyConfigurationDialog()
	{
		super(Display.getCurrent().getActiveShell());
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#getConfiguration()
	 */
	public IAbstractConfiguration getConfiguration()
	{
		return configuration;
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#getDialog()
	 */
	public Dialog getDialog()
	{
		return this;
	}

	/**
	 * @param isOk
	 * @param message
	 */
	public void updateStatus(boolean isOk, String message)
	{
		Button button = getButton(IDialogConstants.OK_ID);
		if (!isOk)
		{
			wasError = true;
			setErrorMessage(message);
			if (button != null)
			{
				button.setEnabled(false);
			}
		}
		else
		{
			if (!wasError)
			{
				if (server!=null&&!(server.getServerState()==IServer.STATE_STOPPED||server.getServerState()==IServer.STATE_UNKNOWN)){
					setErrorMessage(ServerLabelProvider.SERVER_IS_RUNNING_NO_EDIT);
					if (button!=null){
						button.setEnabled(false);
					}
					return;
				}
				return;
			}
			wasError = false;
			setErrorMessage(null);
			if (button != null)
			{
				button.setEnabled(true);
			}
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed()
	{
		configuration.setStringAttribute(IServer.KEY_NAME, this.nameText.getText());
		configuration.setStringAttribute(IServer.KEY_PORT, this.portText.getText());
		configuration.setStringAttribute(IServer.KEY_DESCRIPTION, this.descText.getText());
		configuration.setStringAttribute(JettyServerTypeDelegate.KEY_SERVERID, (String) this.idText.getText());
		super.okPressed();
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#isEdit()
	 */
	public boolean isEdit()
	{
		return isEdit;
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#setConfiguration(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void setConfiguration(IAbstractConfiguration configuration)
	{
		this.configuration = configuration;
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#setEdit(boolean)
	 */
	public void setEdit(boolean isEdit)
	{
		this.isEdit = isEdit;
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#setServer(com.aptana.ide.server.core.IServer)
	 */
	public void setServer(IServer server)
	{
		this.server=server;
	}

}
