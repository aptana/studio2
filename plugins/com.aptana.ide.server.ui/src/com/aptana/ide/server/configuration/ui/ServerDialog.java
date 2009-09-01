/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.server.configuration.ui;

import java.util.HashSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.configuration.ui.BasicServerComposite.StatusUpdater;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.ui.IConfigurationDialog;
import com.aptana.ide.server.ui.views.ServerLabelProvider;

/**
 * @author Pavel Petrochenko
 */
public abstract class ServerDialog extends TitleAreaDialog implements IConfigurationDialog
{

	private IServer eserver;
	
	private boolean edit;

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#getConfiguration()
	 */
	public IAbstractConfiguration getConfiguration()
	{
		return server;
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#getDialog()
	 */
	public Dialog getDialog()
	{
		return this;
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#isEdit()
	 */
	public boolean isEdit()
	{
		return edit;
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#setConfiguration(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void setConfiguration(IAbstractConfiguration configuration)
	{
		this.server = configuration;
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#setEdit(boolean)
	 */
	public void setEdit(boolean isEdit)
	{
		this.edit = isEdit;
	}

	private String title;
	private String description;
	private BasicServerComposite basicServerComposite;

	/**
	 * @return server path
	 */
	public String getServerPath()
	{
		return basicServerComposite.getServerPath();
	}

	private IAbstractConfiguration server;
	private BasicServerComposite.StatusUpdater statusUpdater = new BasicServerComposite.StatusUpdater()
	{

		boolean wasError;

		public void updateStatus(boolean isOk, String message)
		{
			Button button = getButton(IDialogConstants.OK_ID);

			if (!isOk)
			{
				wasError = true;
				setErrorMessage(message);
				button.setEnabled(false);
			}
			else
			{
				if (eserver!=null&&!(eserver.getServerState()==IServer.STATE_STOPPED||eserver.getServerState()==IServer.STATE_UNKNOWN)){
					setErrorMessage(ServerLabelProvider.SERVER_IS_RUNNING_NO_EDIT);
					if (button!=null){
						button.setEnabled(false);
					}
					return;
				}
				if (!wasError)
				{
					return;
				}
				wasError = false;
				setErrorMessage(null);
				button.setEnabled(true);
			}
		}

	};

	/**
	 * Server dialog
	 */
	public ServerDialog()
	{
		super(Display.getDefault().getActiveShell());
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		super.createDialogArea(parent);
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		GridData cData = new GridData(GridData.FILL_BOTH);
		cData.widthHint = 500;
		composite.setLayoutData(cData);
		composite.setFont(parent.getFont());
		basicServerComposite = createServerComposite(composite, statusUpdater);
		basicServerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Build the separator line
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.setTitle(title);
		this.setMessage(description);
		this.getShell().setText(title);
		return composite;
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Control createContents = super.createContents(parent);
		return createContents;
	}

	/**
	 * 
	 */
	protected void updateServer()
	{
		setServerName(basicServerComposite.getServerName());
		setServerPath(basicServerComposite.getServerPath());
		setServerDescription(basicServerComposite.getServerDescription());
		String logPath = basicServerComposite.getLogPath();
		server.setStringAttribute(IServer.KEY_LOG_PATH, logPath);
	}

	private void setServerDescription(String serverDescription)
	{
		server.setStringAttribute(IServer.KEY_DESCRIPTION, serverDescription);
	}

	protected void setServerPath(String serverPath)
	{
		server.setStringAttribute(IServer.KEY_PATH, serverPath);
	}

	private void setServerName(String serverName)
	{
		server.setStringAttribute(IServer.KEY_NAME, serverName);
	}

	/**
	 * @param composite
	 * @param statusUpdater
	 * @return composite
	 */
	protected BasicServerComposite createServerComposite(Composite composite, StatusUpdater statusUpdater)
	{
		BasicServerComposite basicServerComposite = new BasicServerComposite(composite, SWT.NONE, statusUpdater,false);
		return basicServerComposite;
	}

	/**
	 * 
	 */
	protected void updateData()
	{
		this.basicServerComposite.setServerPath((this.server).getStringAttribute(IServer.KEY_PATH));
		this.basicServerComposite.setServerName((this.server).getStringAttribute(IServer.KEY_NAME));
		this.basicServerComposite.setServerDescription((this.server).getStringAttribute(IServer.KEY_DESCRIPTION));
		this.basicServerComposite.setLogPath((this.server).getStringAttribute(IServer.KEY_LOG_PATH));
		IServer[] servers = ServerCore.getServerManager().getServers();
		HashSet<String> str = new HashSet<String>();
		for (IServer s : servers)
		{
			if (!s.getId().equals(this.server.getStringAttribute(IServer.KEY_ID)))
			{
				str.add(s.getName());
			}
		}
		basicServerComposite.setServerNames(str);
		this.basicServerComposite.validate();
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	public void create()
	{
		super.create();
		updateData();
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
	{
		updateServer();
		super.okPressed();
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#setTitle(java.lang.String)
	 */
	public void setTitle(String title)
	{
		this.title = title;
		super.setTitle(title);
	}

	/**
	 * @param description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#setServer(com.aptana.ide.server.core.IServer)
	 */
	public void setServer(IServer server)
	{
		this.eserver=server;
	}
}
