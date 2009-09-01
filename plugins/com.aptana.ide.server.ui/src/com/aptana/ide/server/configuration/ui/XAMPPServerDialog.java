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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.server.configuration.ui.BasicServerComposite.StatusUpdater;
import com.aptana.ide.server.internal.Messages;
import com.aptana.ide.server.internal.XAMPPServerTypeDelegate;
import com.aptana.ide.server.ui.ServerUIPlugin;

/**
 * @author Pavel Petrochenko
 * 
 */
public abstract class XAMPPServerDialog extends ServerDialog {

	private XAMPPServerComposite mySqlServerComposite;
	
	/**
	 * Whether dialog requires path.
	 * @return true if requires, false otherwise.
	 */
	protected abstract boolean requiresPath();
	
	/**
	 * Gets server start path by root path.
	 * @param rootPath - server root path or null if root path is not required.
	 * @return server start path
	 */
	protected abstract String getServerStartPath(String rootPath);
	
	/**
	 * Gets server stop path by root path.
	 * @param rootPath - server root path or null if root path is not required.
	 * @return server stop path
	 */
	protected abstract String getServerStopPath(String rootPath);

	/**
	 * @param parentShell
	 * @param title
	 * @param description
	 */
	public XAMPPServerDialog() {
		super();		
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), "com.aptana.ide.server.ui.servers_add_xampp"); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void updateServer() {
		super.updateServer();
		setServerPath(getServerStartPath(mySqlServerComposite.getServerPath()));
		setStopPath(getServerStopPath(mySqlServerComposite.getServerPath()));		
	}	

	private void setStopPath(String serverStopPath)
	{
		getConfiguration().setStringAttribute(XAMPPServerTypeDelegate.STOPPATH,serverStopPath);
	}

	/**
	 * @param composite
	 * @param statusUpdater
	 * @return composite
	 */
	protected BasicServerComposite createServerComposite(Composite composite,
			StatusUpdater statusUpdater) {
		if (isEdit())
		{
			setTitle(Messages.XAMPPServerTypeDelegate_EDIT);
			setDescription(Messages.XAMPPServerTypeDelegate_EDIT_TITLE);
		}
		else{
			setTitle(Messages.XAMPPServerTypeDelegate_ADD);
			setDescription(Messages.XAMPPServerTypeDelegate_ADD_TITLE);
		}
		setTitleImage(ServerUIPlugin.getImage("icons/server/wizban/xampp_wiz.png")); //$NON-NLS-1$
		mySqlServerComposite = new XAMPPServerComposite(
				composite, SWT.NONE, statusUpdater, requiresPath());
		return mySqlServerComposite;
	}


	/**
	 * @see com.aptana.ide.server.configuration.ui.ServerDialog#updateData()
	 */
	protected void updateData() {
		
		super.updateData();
		mySqlServerComposite.setServerStartPath(getStopPath());
	}

	private String getStopPath()
	{
		return getConfiguration().getStringAttribute(XAMPPServerTypeDelegate.STOPPATH);
	}
}
