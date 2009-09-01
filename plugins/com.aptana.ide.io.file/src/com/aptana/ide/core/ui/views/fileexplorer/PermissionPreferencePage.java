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
package com.aptana.ide.core.ui.views.fileexplorer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.core.ui.io.file.IPreferenceConstants;
import com.aptana.ide.core.ui.io.file.PermissionsGroup;
import com.aptana.ide.io.file.FilePlugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class PermissionPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private PermissionsGroup fFilePermissions;
	private PermissionsGroup fDirectoryPermissions;

	/**
	 * Constructor.
	 */
	public PermissionPreferencePage()
	{
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk()
	{
		doGetPreferenceStore().setValue(IPreferenceConstants.FILE_PERMISSION, fFilePermissions.getPermissions());
		doGetPreferenceStore().setValue(IPreferenceConstants.DIRECTORY_PERMISSION,
				fDirectoryPermissions.getPermissions());
		return super.performOk();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());

		Label label = new Label(main, SWT.WRAP);
		label.setText(Messages.PermissionPreferencePage_Notes);
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		fFilePermissions = new PermissionsGroup(main);
		fFilePermissions.setText(Messages.PermissionPreferencePage_FileGroupTitle);
		fFilePermissions.setPermissions(doGetPreferenceStore().getInt(IPreferenceConstants.FILE_PERMISSION));
		fFilePermissions.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		fDirectoryPermissions = new PermissionsGroup(main);
		fDirectoryPermissions.setText(Messages.PermissionPreferencePage_DirectoryGroupTitle);
		fDirectoryPermissions.setPermissions(doGetPreferenceStore().getInt(IPreferenceConstants.DIRECTORY_PERMISSION));
		fDirectoryPermissions.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		return main;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		fFilePermissions.setPermissions(doGetPreferenceStore().getDefaultInt(IPreferenceConstants.FILE_PERMISSION));
		fDirectoryPermissions.setPermissions(doGetPreferenceStore().getDefaultInt(
				IPreferenceConstants.DIRECTORY_PERMISSION));
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore()
	{
		return FilePlugin.getDefault().getPreferenceStore();
	}

}
