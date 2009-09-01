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

package com.aptana.ide.syncing.wizards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.sync.SyncManager;
import com.aptana.ide.syncing.SyncingPlugin;
import com.aptana.ide.syncing.preferences.IPreferenceConstants;

/**
 * @author Pavel Petrochenko
 * 
 */
public class SyncImportPage extends WizardPage implements IWizardPage
{

	private Text path;

	/**
	 * @param pageName
	 */
	protected SyncImportPage(String pageName)
	{
		super(pageName);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(final Composite parent)
	{
		Composite cm = new Composite(parent, SWT.NONE);
		cm.setLayout(new GridLayout(3, false));
		Label ls = new Label(cm, SWT.NONE);
		ls.setText(Messages.SyncImportPage_FROM_FILE);
		path = new Text(cm, SWT.BORDER);
		path.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button bs = new Button(cm, SWT.PUSH);
		bs.setText(Messages.SyncExportPage_BROWSE);
		GridData gridData = new GridData();
		gridData.widthHint = 100;
		bs.setLayoutData(gridData);
		bs.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog ds = new FileDialog(parent.getShell(), SWT.OPEN);
				ds.setFilterExtensions(new String[] { "*.snc" }); //$NON-NLS-1$
				ds
						.setFilterNames(new String[] { Messages.SyncImportPage_SYNC_SETTINGS });
				String result = ds.open();
				if (result != null)
				{
					if (result.indexOf('.') == -1)
					{
						StringBuilder bld = new StringBuilder();
						bld.append(result);
						bld.append(".snc"); //$NON-NLS-1$
						result = bld.toString();
					}
					path.setText(result);
				}
				super.widgetSelected(e);
			}

		});
		path.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				setPageComplete(path.getText().trim().length() > 0);
			}
		});
		this.setTitle(Messages.SyncImportPage_TITLE);
		this.setDescription(Messages.SyncImportPage_DESCRIPTION);
		path.setText(SyncingPlugin.getDefault().getPreferenceStore().getString(
				IPreferenceConstants.EXPORT_INITIAL_PATH));
		setControl(cm);
	}

	/**
	 * @return success
	 */
	public boolean performFinish()
	{
		String ps = path.getText();
		File fs = new File(ps);
		if (!fs.exists())
		{
			MessageDialog.openError(path.getShell(),
					Messages.SyncImportPage_FILE_NOT_EXIST, StringUtils.format(
							Messages.SyncImportPage_FILE_NOT_EXIST_DESC, fs
									.getAbsolutePath()));
			return false;
		}
		if (!fs.canRead())
		{
			MessageDialog.openError(path.getShell(),
					Messages.SyncImportPage_FILE_NOT_READABLE,
					StringUtils.format(
							Messages.SyncImportPage_FILE_NOT_READABLE_DESC, fs
									.getAbsolutePath()));
			return false;
		}
		StringBuffer contents = new StringBuffer();
		BufferedReader input = null;
		try
		{
			input = new BufferedReader(new FileReader(fs));
			String line = null;
			while ((line = input.readLine()) != null)
			{
				contents.append(line);
				// contents.append(System.getProperty("line.separator"));
			}
		} catch (FileNotFoundException ex)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), StringUtils.format(
					"Strange file not found exception", //$NON-NLS-1$
					fs.getAbsolutePath()));
			return false;
		} catch (IOException ex)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), StringUtils.format(
					"IO Error", fs //$NON-NLS-1$
							.getAbsolutePath()));
			return false;
		} finally
		{
			try
			{
				if (input != null)
				{
					input.close();
				}
			} catch (IOException ex)
			{
				IdeLog.logError(AptanaCorePlugin.getDefault(), StringUtils
						.format("Error closing file", //$NON-NLS-1$
								fs.getAbsolutePath()));
			}
		}
		SyncManager.getSyncManager()
				.fromSerializableString(contents.toString());
		return true;
	}

}