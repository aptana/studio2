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
package com.aptana.ide.core.ui;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class DeleteConfigurationsDialog extends TitleAreaDialog
{

	private Composite displayArea;
	private Table deleteTable;
	private CheckboxTableViewer deleteTableViewer;
	private List<ILaunchConfiguration> configurations;

	/**
	 * @param parentShell
	 * @param configurations
	 */
	public DeleteConfigurationsDialog(Shell parentShell, List<ILaunchConfiguration> configurations)
	{
		super(parentShell);
		this.configurations = configurations;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
	{
		Object[] checkedConfigs = deleteTableViewer.getCheckedElements();
		for (int i = 0; i < checkedConfigs.length; i++)
		{
			try
			{
				((ILaunchConfiguration) checkedConfigs[i]).delete();
			}
			catch (CoreException e)
			{
			}
		}
		super.okPressed();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		setTitle(Messages.DeleteConfigurationsDialog_TTL_RunAndDebugConfigurationProjectAssociation);
		setMessage(Messages.DeleteConfigurationsDialog_MSG_CheckTheOnesToBeDeletedWithProject);
		displayArea = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(1,true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		displayArea.setLayout(layout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		deleteTable = new Table(displayArea, SWT.FULL_SELECTION | SWT.CHECK);
		deleteTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		deleteTableViewer = new CheckboxTableViewer(deleteTable);
		ILabelProvider provider = DebugUIPlugin.getDefaultLabelProvider();
		for (int i = 0; i < configurations.size(); i++)
		{
			TableItem item = new TableItem(deleteTable, SWT.NONE);
			ILaunchConfiguration config = configurations.get(i);
			item.setData(config);
			item.setText(config.getName());
			item.setImage(provider.getImage(config));
		}
		return composite;
	}
}
