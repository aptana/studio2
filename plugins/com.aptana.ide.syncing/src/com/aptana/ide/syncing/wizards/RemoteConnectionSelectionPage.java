/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Aptana Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * Redistribution, except as permitted by the above license, is prohibited.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.wizards;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.IVirtualFileManagerDialog;
import com.aptana.ide.core.io.ProtocolManager;
import com.aptana.ide.core.io.sync.SyncManager;
import com.aptana.ide.core.ui.views.fileexplorer.Messages;
import com.aptana.ide.io.ftp.IFtpVirtualFileManager;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class RemoteConnectionSelectionPage extends WizardPage
{

	private Composite displayArea;
	private Label connectionLabel;
	private Table connectionTable;
	private Button syncOnFinish;
	private boolean synchronize = true;
	private IVirtualFileManager site;

	/**
	 * RemoteConnectionSelectionPage
	 * 
	 * @param pageName
	 * @param selection
	 */
	public RemoteConnectionSelectionPage(String pageName, IStructuredSelection selection)
	{
		super(pageName);
		site = null;
		if (selection != null)
		{
			Object possibleSite = selection.getFirstElement();
			if (possibleSite != null && possibleSite instanceof IVirtualFileManager)
			{
				site = (IVirtualFileManager) possibleSite;
			}
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout daLayout = new GridLayout(1, true);
		displayArea.setLayout(daLayout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Group connGroup = new Group(displayArea, SWT.NONE);
		connGroup.setText(Messages.RemoteConnectionSelectionPage_Connections);
		connGroup.setLayout(new GridLayout(1, true));
		connGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		connectionLabel = new Label(connGroup, SWT.LEFT | SWT.WRAP);
		connectionLabel.setText(Messages.RemoteConnectionSelectionPage_LBL_SelectRemoteLocationOfThisProject);
		connectionLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		connectionTable = new Table(connGroup, SWT.CHECK | SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		connectionTable.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (e.detail == SWT.CHECK)
				{
					TableItem[] items = connectionTable.getItems();
					for (int i = 0; i < items.length; i++)
					{
						TableItem item = items[i];
						if (item != e.item)
						{
							item.setChecked(false);
						}
					}
					if (e.item instanceof TableItem)
					{
						if (((TableItem) e.item).getChecked())
						{
							setErrorMessage(null);
							setPageComplete(true);
							site = (IVirtualFileManager) e.item.getData();
						}
						else
						{
							setErrorMessage(Messages.RemoteConnectionSelectionPage_ERR_SelectSiteOrCreateNewOne);
							setPageComplete(false);
						}
					}
				}
			}

		});
		connectionTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		connectionTable.setLinesVisible(true);
		new TableColumn(connectionTable, SWT.LEFT);
		connectionTable.addControlListener(new ControlAdapter()
		{

			public void controlResized(ControlEvent e)
			{
				TableColumn c = connectionTable.getColumn(0);
				Point size = connectionTable.getSize();

				// Mac fix for always having a vertical scrollbar and not calculating it affects the horizontal scroll
				// bar
				if (Platform.getOS().equals(Platform.OS_MACOSX))
				{
					ScrollBar vScrolls = connectionTable.getVerticalBar();
					if (vScrolls != null)
					{
						size.x = size.x - vScrolls.getSize().x - 10;
					}
				}
				c.setWidth(size.x - 6);
			}

		});
		populateTable();
		if (site != null)
		{
			checkItem(site);
		}
		else
		{
			setErrorMessage(Messages.RemoteConnectionSelectionPage_ERR_SelectSiteOrCreateNewOne);
			setPageComplete(false);
		}
		createAddNewButtons(connGroup);
		Group options = new Group(displayArea, SWT.NONE);
		options.setText(Messages.RemoteConnectionSelectionPage_LBL_Options);
		options.setLayout(new GridLayout(1, true));
		options.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		syncOnFinish = new Button(options, SWT.CHECK);
		syncOnFinish.setText(Messages.RemoteConnectionSelectionPage_LBL_DownloadOnFinish);
		syncOnFinish.setSelection(isSynchronize());
		syncOnFinish.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				setSynchronize(syncOnFinish.getSelection());
			}

		});
		setControl(displayArea);
	}

	private void checkItem(IVirtualFileManager item)
	{
		TableItem[] items = connectionTable.getItems();
		for (int i = 0; i < items.length; i++)
		{
			TableItem ti = items[i];
			if (ti.getData() == item)
			{
				ti.setChecked(true);
				site = item;
				setErrorMessage(null);
				setPageComplete(true);
				break;
			}
		}
	}

	private void populateTable()
	{
		ProtocolManager[] pm = ProtocolManager.getPrototcolManagers();
		for (int i = 0; i < pm.length; i++)
		{
			String type = pm[i].getDisplayName();
			Image image = pm[i].getImage();
			if (pm[i].isRemote())
			{
				IVirtualFileManager[] sites = pm[i].getFileManagers();
				for (int m = 0; m < sites.length; m++)
				{
					// Populate only FTP locations
					if (sites[m] instanceof IFtpVirtualFileManager)
					{
						TableItem item = new TableItem(connectionTable, SWT.NONE);
						item.setImage(image);
						item.setText(type + ": " + sites[m].getNickName() + "   " + sites[m].getBasePath());
						item.setData(sites[m]);
					}
				}
			}
		}
	}

	private void createAddNewButtons(Composite parent)
	{
		Composite buttons = new Composite(parent, SWT.NONE);
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		ProtocolManager[] pm = ProtocolManager.getPrototcolManagers();
		int columns = Math.min(4, pm.length);
		GridLayout bLayout = new GridLayout(columns, false);
		bLayout.marginHeight = 0;
		bLayout.marginWidth = 0;
		buttons.setLayout(bLayout);
		// Special case for FTP connection dialog
		ProtocolManager ftpManager = ProtocolManager.getProtocolManagerByName("FTP Site"); // $NON-NLS-1$
		if (ftpManager != null)
		{
			createButton(ftpManager, buttons, Messages.RemoteConnectionSelectionPage_LBL_NewConnection);
		}
		for (int i = 0; i < pm.length; i++)
		{
			// Add a button for any non-ftp manager.
			// In case we did not locate any FTP manager (should never happen, but here just in case), we
			// display buttons for the other defined FTP managers.
			if (pm[i].isRemote() && pm[i].isAllowNew()
					&& (ftpManager == null || !pm[i].getDisplayName().contains("FTP")))// $NON-NLS-1$
			{
				createButton(pm[i], buttons, null);
			}
		}
	}

	/*
	 * Create a button for the given protocol manager.
	 * @param protocolManager
	 * @param parent
	 * @param prefferedName - The preferred text to put on the button. If null, the getDisplayName is used. In any case,
	 * the given name will be ellipsify.
	 */
	private void createButton(ProtocolManager protocolManager, Composite parent, String prefferedName)
	{
		final Button newButton = new Button(parent, SWT.PUSH);
		if (prefferedName != null)
		{
			newButton.setText(StringUtils.ellipsify(prefferedName));
		}
		else
		{
			newButton.setText(StringUtils.ellipsify(protocolManager.getDisplayName()));
		}

		newButton.setData(protocolManager);
		newButton.setImage(protocolManager.getImage());
		newButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ProtocolManager manager = (ProtocolManager) newButton.getData();
				IVirtualFileManagerDialog nld = manager.createPropertyDialog(newButton.getShell(), SWT.DIALOG_TRIM
						| SWT.RESIZE | SWT.APPLICATION_MODAL);
				if (nld != null)
				{
					// create virtual file manager
					IVirtualFileManager vfm = manager.createFileManager(false);

					// build a list of names already in use
					IVirtualFileManager[] managers = (IVirtualFileManager[]) SyncManager.getSyncManager().getItems(
							vfm.getClass());
					Set names = new HashSet();

					for (int i = 0; i < managers.length; i++)
					{
						names.add(managers[i].getNickName());
					}

					// build default name
					String nickName = StringUtils.format(Messages.FileExplorerView_AddNewFileManager, manager
							.getFileManagerName());

					// make sure it is unique
					if (names.contains(nickName))
					{
						String base = nickName;
						int index = 1;

						nickName = base + "-" + Integer.toString(index); //$NON-NLS-1$

						while (names.contains(nickName))
						{
							index++;
							nickName = base + "-" + Integer.toString(index); //$NON-NLS-1$
						}
					}

					// set nickname
					vfm.setNickName(nickName);
					nld.setItem(vfm, true);
					IVirtualFileManager item = nld.open();

					// Item will be null if we cancelled out of previous dialog
					if (item != null)
					{
						manager.addFileManager(item);
						connectionTable.removeAll();
						populateTable();
						checkItem(item);
					}
				}
			}
		});
	}

	/**
	 * @return the synchronize
	 */
	public boolean isSynchronize()
	{
		return synchronize;
	}

	/**
	 * @param synchronize
	 *            the synchronize to set
	 */
	public void setSynchronize(boolean synchronize)
	{
		this.synchronize = synchronize;
	}

	/**
	 * @return the site
	 */
	public IVirtualFileManager getSite()
	{
		return site;
	}

	/**
	 * @param site
	 *            the site to set
	 */
	public void setSite(IVirtualFileManager site)
	{
		this.site = site;
	}

}
