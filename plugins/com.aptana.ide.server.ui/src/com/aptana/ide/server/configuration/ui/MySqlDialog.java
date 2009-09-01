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
import com.aptana.ide.server.internal.MySqlServerTypeDelegate;

/**
 * @author Pavel Petrochenko
 */
public class MySqlDialog extends ServerDialog
{

	private MySqlServerComposite mySqlServerComposite;

	/**
	 * @param parentShell
	 * @param title
	 * @param description
	 */
	public MySqlDialog()
	{
		super();
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), "com.aptana.ide.server.ui.servers_add_mysql"); //$NON-NLS-1$
	}

	/**
	 * 
	 */
	protected void updateServer()
	{
		super.updateServer();
		setLaunchArgs(mySqlServerComposite.getLaunchArgs());
	}

	private void setLaunchArgs(String launchArgs)
	{
		getConfiguration().setStringAttribute(MySqlServerTypeDelegate.LAUNCHARRGS, launchArgs);
	}

	/**
	 * @param composite
	 * @param statusUpdater
	 * @return composite
	 */
	protected BasicServerComposite createServerComposite(Composite composite, StatusUpdater statusUpdater)
	{
		if (isEdit())
		{
			setTitle(Messages.MySqlServerTypeDelegate_EDIT);
			setDescription(Messages.MySqlServerTypeDelegate_EDIT_TITLE);
		}
		else
		{
			setTitle(Messages.MySqlServerTypeDelegate_ADD);
			setDescription(Messages.MySqlServerTypeDelegate_ADD_TITLE);
		}
		mySqlServerComposite = new MySqlServerComposite(composite, SWT.NONE, statusUpdater,false);
		return mySqlServerComposite;
	}

	/**
	 * @see com.aptana.ide.server.configuration.ui.ServerDialog#updateData()
	 */
	protected void updateData()
	{
		
		super.updateData();
		mySqlServerComposite.setLaunchArgs(getLaunchArgs());
	}

	private String getLaunchArgs()
	{
		return getConfiguration().getStringAttribute(MySqlServerTypeDelegate.LAUNCHARRGS);
	}
}
