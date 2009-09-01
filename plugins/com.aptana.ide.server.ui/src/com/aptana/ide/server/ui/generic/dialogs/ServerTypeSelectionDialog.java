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
package com.aptana.ide.server.ui.generic.dialogs;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.ui.ServerDialogPageRegistry;
import com.aptana.ide.server.ui.views.actions.ICanAdd;

/**
 * @author Pavel Petrochenko
 */
public class ServerTypeSelectionDialog extends TitleAreaDialog
{

	private TableViewer viewer;
	private IServerType result;
	private String title = Messages.ServerTypeSelectionDialog_TITLE;
	private String description = Messages.ServerTypeSelectionDialog_DESCRIPTION;
	private boolean showOnlyExternalServers;
	private String category;

	/**
	 * Constructs a new ServerTypeSelectionDialog.
	 * 
	 * @param parentShell
	 */
	public ServerTypeSelectionDialog(Shell parentShell)
	{
		this(parentShell, false, null);
	}
	
	/**
	 * Constructs a new ServerTypeSelectionDialog.
	 * 
	 * @param parentShell
	 * @param showOnlyExternalServers If set to true, only server types that are defined as external add displayed in the dialog.
	 * @param category Display only the servers that has the given category definition (can be null to not filter)
	 */
	public ServerTypeSelectionDialog(Shell parentShell, boolean showOnlyExternalServers, String category)
	{
		super(parentShell);
		this.showOnlyExternalServers = showOnlyExternalServers;
		this.category = category;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), "com.aptana.ide.server.ui.servers_add_server"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{

		this.setTitle(title);
		this.setMessage(description);
		Label titleBarSeparator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		viewer = new TableViewer(parent, SWT.SINGLE);
		IServerType[] knownServerTypes = ServerCore.getServerManager().getServerTypes();
		ArrayList<IServerType> visibleTypes = new ArrayList<IServerType>();
		viewer.setContentProvider(new ArrayContentProvider());
		for (int a = 0; a < knownServerTypes.length; a++)
		{
			final IServerType type = knownServerTypes[a];
			// Filter out servers if needed
			if ((showOnlyExternalServers && !type.isExternal()) || (category != null && !category.equalsIgnoreCase(type.getCategory())))
				continue;
			boolean hasDialog = ServerDialogPageRegistry.getInstance().hasDialog(type.getId());
			boolean b = type.getAdapter(ICanAdd.class) != null;
			if (hasDialog || b)
			{
				visibleTypes.add(type);
			}
		}
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{

			public void doubleClick(DoubleClickEvent event)
			{
				okPressed();
			}

		});
		viewer.setInput(visibleTypes.toArray());
		viewer.setLabelProvider(new ServerTypeLabelProvider());
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setSorter(new ViewerSorter());
		getShell().setText(Messages.ServerTypeSelectionDialog_CHOOSE_SERVER_TITLE);
		return super.createDialogArea(parent);
	}

	/**
	 * @see org.eclipse.jface.dialogs.TrayDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createButtonBar(Composite parent)
	{
		Control createButtonBar = super.createButtonBar(parent);
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				validate();
			}
		});
		validate();
		return createButtonBar;
	}

	/**
	 * @return server type
	 */
	public IServerType getResult()
	{
		return result;
	}

	private IServerType calcResult()
	{
		StructuredSelection ss = (StructuredSelection) viewer.getSelection();
		if (!ss.isEmpty())
		{
			return (IServerType) ss.getFirstElement();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed()
	{
		result = calcResult();
		super.okPressed();
	}

	private void validate()
	{
		boolean empty = viewer.getSelection().isEmpty();
		getButton(Dialog.OK).setEnabled(!empty);
		if (empty)
		{

			setErrorMessage(Messages.ServerTypeSelectionDialog_PLEASE_SELECT_SERVER_TYPE);
		}
		else
		{
			setErrorMessage(null);
		}
	}
}
