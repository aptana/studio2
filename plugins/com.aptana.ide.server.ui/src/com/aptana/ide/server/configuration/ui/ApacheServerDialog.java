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
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.internal.ApacheServerTypeDelegate;
import com.aptana.ide.server.internal.Messages;

/**
 * @author Pavel Petrochenko
 */
public class ApacheServerDialog extends ServerDialog
{

	private ApacheServerComposite apacheServerComposite;
	
	

	/**
	 * @param parentShell
	 * @param title
	 * @param description
	 */
	public ApacheServerDialog()
	{
	}

	/**
	 * 
	 */
	protected void updateServer()
	{
		super.updateServer();
		setStopApache(apacheServerComposite.getApacheStop());
		setStartApache(apacheServerComposite.getApacheStart());
		setRestartApache(apacheServerComposite.getApacheRestart());
		setDocumentRoot(apacheServerComposite.getDocumentRoot());
		setEtcHosts(apacheServerComposite.getEtcHostsPath());
		getConfiguration().setStringAttribute(ApacheServerTypeDelegate.HOSTNAME, apacheServerComposite.getApacheHost());
	}

	private void setEtcHosts(String etcHostsPath)
	{
		getConfiguration().setStringAttribute(ApacheServerTypeDelegate.ETCHOSTS, etcHostsPath);
	}

	private void setDocumentRoot(String root)
	{
		getConfiguration().setStringAttribute(IServer.KEY_DOCUMENT_ROOT, root);
	}

	private void setRestartApache(String apacheRestart)
	{
		getConfiguration().setStringAttribute(ApacheServerTypeDelegate.RESTARTAPACHE, apacheRestart);
	}

	private void setStartApache(String apacheStart)
	{
		getConfiguration().setStringAttribute(ApacheServerTypeDelegate.STARTAPACHE, apacheStart);
	}

	private void setStopApache(String apacheStop)
	{
		getConfiguration().setStringAttribute(ApacheServerTypeDelegate.STOPAPACHE, apacheStop);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), "com.aptana.ide.server.ui.servers_add_apache"); //$NON-NLS-1$
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
			setTitle(Messages.ApacheServerTypeDelegate_EDIT);
			setDescription(Messages.ApacheServerTypeDelegate_DESC); 
		}
		else{
			setTitle(Messages.ApacheServerTypeDelegate_ADD); 
			setDescription(Messages.ApacheServerTypeDelegate_ADD_DESC); 
		}
		apacheServerComposite = new ApacheServerComposite(composite, SWT.NONE, statusUpdater,false);
		return apacheServerComposite;
	}

	/**
	 * @see com.aptana.ide.server.configuration.ui.ServerDialog#updateData()
	 */
	protected void updateData()
	{
		
		super.updateData();
		apacheServerComposite.setApacheStart(getStartApache());		
		apacheServerComposite.setApacheRestart(getRestartApache());
		apacheServerComposite.setApacheStop((getStopApache()));
		apacheServerComposite.setDocumentRoot(getDocumentRoot());
		apacheServerComposite.setEtcHostsPath(getEtcHosts());
		apacheServerComposite.setApacheHost(getConfiguration().getStringAttribute(ApacheServerTypeDelegate.HOSTNAME));
	}

	private String getEtcHosts()
	{
		return getConfiguration().getStringAttribute(ApacheServerTypeDelegate.ETCHOSTS);
	}

	private String getDocumentRoot()
	{
		return getConfiguration().getStringAttribute(IServer.KEY_DOCUMENT_ROOT);
	}

	private String getStopApache()
	{
		return getConfiguration().getStringAttribute(ApacheServerTypeDelegate.STOPAPACHE);
	}

	private String getRestartApache()
	{
		return getConfiguration().getStringAttribute(ApacheServerTypeDelegate.RESTARTAPACHE);
	}

	private String getStartApache()
	{
		return getConfiguration().getStringAttribute(ApacheServerTypeDelegate.STARTAPACHE);
	}

	
}
