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
package com.aptana.ide.core.ui.syncing;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage;
import com.aptana.ide.core.ui.preferences.IPreferenceConstants;

/**
 * SyncGlobalCloakingPreferencePage
 */
public class SyncGlobalCloakingPreferencePage extends FileExtensionPreferencePage implements ModifyListener
{

	private Button useCrc;
	private Button compareInBackground;
	private Text initialPoolSize;
	private Text maxPoolSize;

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e)
	{
		if (e.widget instanceof Text)
		{
			try
			{
				Integer.parseInt(((Text) e.widget).getText());
				setErrorMessage(null);
				setValid(true);
			}
			catch (NumberFormatException nfe)
			{
				setErrorMessage(Messages.SyncGlobalCloakingPreferencePage_PoolSizeError);
				setValid(false);
			}
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Control c = super.createContents(parent);

		Group smartSyncOptions = new Group(parent, SWT.NONE);
		smartSyncOptions.setLayout(new GridLayout());
		smartSyncOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		smartSyncOptions.setText(Messages.SyncGlobalCloakingPreferencePage_SmartSyncOptions);

		useCrc = new Button(smartSyncOptions, SWT.CHECK);
		useCrc.setText(Messages.SyncGlobalCloakingPreferencePage_UseCRC);
		useCrc.setSelection(getPreferenceStore().getBoolean(IPreferenceConstants.USE_CRC));

		compareInBackground = new Button(smartSyncOptions, SWT.CHECK);
		compareInBackground.setSelection(getPreferenceStore().getBoolean(IPreferenceConstants.COMPARE_IN_BACKGROUND));
		compareInBackground.setText(Messages.SyncGlobalCloakingPreferencePage_CompareInBackground);

		Composite poolSizes = new Composite(smartSyncOptions, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		poolSizes.setLayout(layout);
		poolSizes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label label = new Label(poolSizes, SWT.NONE);
		label.setText(Messages.SyncGlobalCloakingPreferencePage_InitialPoolSize);
		initialPoolSize = new Text(poolSizes, SWT.BORDER | SWT.SINGLE);
		initialPoolSize.setText(String.valueOf(getPreferenceStore().getInt(IPreferenceConstants.INITIAL_POOL_SIZE)));
		initialPoolSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		initialPoolSize.addModifyListener(this);

		label = new Label(poolSizes, SWT.NONE);
		label.setText(Messages.SyncGlobalCloakingPreferencePage_MaxPoolSize);
		maxPoolSize = new Text(poolSizes, SWT.BORDER | SWT.SINGLE);
		maxPoolSize.setText(String.valueOf(getPreferenceStore().getInt(IPreferenceConstants.MAX_POOL_SIZE)));
		maxPoolSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		maxPoolSize.addModifyListener(this);

		return c;
	}

	/**
	 * @see com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		useCrc.setSelection(getPreferenceStore().getDefaultBoolean(IPreferenceConstants.USE_CRC));
		compareInBackground.setSelection(getPreferenceStore().getDefaultBoolean(
				IPreferenceConstants.COMPARE_IN_BACKGROUND));
		initialPoolSize.setText(String.valueOf(getPreferenceStore().getDefaultInt(IPreferenceConstants.INITIAL_POOL_SIZE)));
		maxPoolSize.setText(String.valueOf(getPreferenceStore().getDefaultInt(IPreferenceConstants.MAX_POOL_SIZE)));

		super.performDefaults();
	}

	/**
	 * @see com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		getPreferenceStore().setValue(IPreferenceConstants.USE_CRC, useCrc.getSelection());
		getPreferenceStore().setValue(IPreferenceConstants.COMPARE_IN_BACKGROUND, compareInBackground.getSelection());
		getPreferenceStore().setValue(IPreferenceConstants.INITIAL_POOL_SIZE, Integer.parseInt(initialPoolSize.getText()));
		getPreferenceStore().setValue(IPreferenceConstants.MAX_POOL_SIZE, Integer.parseInt(maxPoolSize.getText()));
		return super.performOk();
	}

	/**
	 * @see com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage#getTableDescription()
	 */
	protected String getTableDescription()
	{
		return Messages.SyncGlobalCloakingPreferencePage_AddFileExtensionsToCloak;
	}

	/**
	 * @see com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage#doGetPreferenceID()
	 */
	protected String doGetPreferenceID()
	{
		return com.aptana.ide.core.preferences.IPreferenceConstants.PREF_GLOBAL_SYNC_CLOAKING_EXTENSIONS;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore()
	{
		return CoreUIPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * @see com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage#doGetPlugin()
	 */
	protected Plugin doGetPlugin()
	{
		return CoreUIPlugin.getDefault();
	}

	/**
	 * Prompt for resource type.
	 * 
	 * @return Object
	 */
	public Object addItem()
	{
		CloakingInfoDialog dialog = new CloakingInfoDialog(getControl().getShell());
		if (dialog.open() == Window.OK)
		{
			return dialog.messageText;
		}
		return null;
	}

	/**
	 * Prompt for resource type.
	 * 
	 * @param item
	 * @return Object
	 */
	public Object editItem(Object item)
	{
		CloakingInfoDialog dialog = new CloakingInfoDialog(getControl().getShell());
		dialog.setItem(item);

		if (dialog.open() == Window.OK)
		{
			return dialog.messageText;
		}
		return null;
	}

	/**
	 * This class is used to prompt the user for a file name & extension.
	 */
	public class CloakingInfoDialog extends TitleAreaDialog
	{

		private Text message;
		private String messageText;

		private Button okButton;

		/**
		 * Constructs a new file extension dialog.
		 * 
		 * @param parentShell
		 *            the parent shell
		 */
		public CloakingInfoDialog(Shell parentShell)
		{
			super(parentShell);
		}

		/**
		 * Method declared in Window.
		 * 
		 * @param shell
		 */
		protected void configureShell(Shell shell)
		{
			super.configureShell(shell);
			shell.setText(Messages.SyncGlobalCloakingPreferencePage_IgnoreWarningTitle);
			PlatformUI.getWorkbench().getHelpSystem().setHelp(shell, IWorkbenchHelpContextIds.FILE_EXTENSION_DIALOG);
		}

		/**
		 * Creates and returns the contents of the upper part of the dialog (above the button bar). Subclasses should
		 * override.
		 * 
		 * @param parent
		 *            the parent composite to contain the dialog area
		 * @return the dialog area control
		 */
		protected Control createDialogArea(Composite parent)
		{
			// top level composite
			Composite parentComposite = (Composite) super.createDialogArea(parent);

			// create a composite with standard margins and spacing
			Composite contents = new Composite(parentComposite, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			layout.numColumns = 2;
			contents.setLayout(layout);
			contents.setLayoutData(new GridData(GridData.FILL_BOTH));
			contents.setFont(parentComposite.getFont());

			setTitle(Messages.SyncGlobalCloakingPreferencePage_IgnoreFileFolder);
			setMessage(Messages.SyncGlobalCloakingPreferencePage_IgnoteWarningMessage);

			// begin the layout

			Label label = new Label(contents, SWT.LEFT);
			label.setText(Messages.SyncGlobalCloakingPreferencePage_FileExtensionLabel);

			GridData data = new GridData();
			data.horizontalAlignment = GridData.FILL;
			label.setLayoutData(data);
			label.setFont(parent.getFont());

			message = new Text(contents, SWT.SINGLE | SWT.BORDER);
			message.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent event)
				{
					if (event.widget == message)
					{
						okButton.setEnabled(validateErrorDescriptor());
					}
				}
			});
			data = new GridData();
			data.horizontalAlignment = GridData.FILL;
			data.grabExcessHorizontalSpace = true;
			message.setLayoutData(data);
			message.setFocus();

			Dialog.applyDialogFont(parentComposite);

			return contents;
		}

		/**
		 * Validate the user input for a file type
		 */
		private boolean validateErrorDescriptor()
		{
			// check for empty message
			if (StringUtils.EMPTY.equals(message.getText()))
			{
				return false;
			}

			messageText = message.getText();
			return true;
		}

		/**
		 * Method declared on Dialog.
		 * 
		 * @param parent
		 */
		protected void createButtonsForButtonBar(Composite parent)
		{
			okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
			okButton.setEnabled(false);
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		}

		/**
		 * Get the message.
		 * 
		 * @return the extension
		 */
		public String getMessage()
		{
			return messageText;
		}

		/**
		 * Sets the item
		 * 
		 * @param item
		 * @return Object
		 */
		public Object setItem(Object item)
		{
			return null;
		}
	}

}
