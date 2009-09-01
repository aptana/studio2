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
package com.aptana.ide.core.ui.preferences;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.install.PlatformValidatorPatcher;

/**
 * The form for configuring the general top-level preferences for this plugin
 */

public class ExportPreferencesPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	private Button checkBox;
	private StringButtonFieldEditor stringButtonFieldEditor;
	private Text lastRestore;

	/**
	 * GeneralPreferencePage
	 */
	public ExportPreferencesPreferencePage()
	{
		super(GRID);
		setPreferenceStore(CoreUIPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.ExportPreferencesPreferencePage_BACKUP_DESCRIPTION);		
	}

	private void updatePreferences()
	{
		String name=CoreUIPlugin.getDefault().getPreferenceStore().getString(IPreferenceConstants.PREF_AUTO_BACKUP_LASTRESTORE_NAME);
		long time=CoreUIPlugin.getDefault().getPreferenceStore().getLong(IPreferenceConstants.PREF_AUTO_BACKUP_LASTRESTORE_TIME);
		if (name.length()>0){
			Date date = new Date(time);
			String format = StringUtils.format(Messages.ExportPreferencesPreferencePage_LAST_RESTORE,new Object[]{name,DateFormat.getDateTimeInstance().format(date)});
			lastRestore.setText(format);
		}
		else{
			lastRestore.setText(Messages.ExportPreferencesPreferencePage_NO_RESTORE);
		}		
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		final Composite c = getFieldEditorParent();
		
		final BooleanFieldEditor booleanFieldEditor = new BooleanFieldEditor(
				IPreferenceConstants.PREF_AUTO_BACKUP_ENABLED, Messages.ExportPreferencesPreferencePage_BACKUP_TITLE, c);
		checkBox = (Button) c.getChildren()[0];
		addField(booleanFieldEditor);
		stringButtonFieldEditor = new StringButtonFieldEditor(IPreferenceConstants.PREF_AUTO_BACKUP_PATH,
				Messages.ExportPreferencesPreferencePage_PATH, c)
		{

			protected String changePressed()
			{
				DirectoryDialog dlg = new DirectoryDialog(getShell(), SWT.SAVE);
				dlg.setFilterPath(stringButtonFieldEditor.getStringValue());
				dlg.setText(Messages.ExportPreferencesPreferencePage_CHOOSE_CONTAINER);
				String open = dlg.open();
				return open;
			}
			
		};
		stringButtonFieldEditor.getTextControl(c).setEditable(false);
		addField(stringButtonFieldEditor);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.widthHint = 150;
		stringButtonFieldEditor.getTextControl(c).setLayoutData(data);

		String string = CoreUIPlugin.getDefault().getPreferenceStore().getString(
				IPreferenceConstants.PREF_AUTO_BACKUP_LASTNAME);
		String lastBackup = StringUtils.format(Messages.ExportPreferencesPreferencePage_LAST_BACKUP,string);
		if (string.length() == 0)
		{
			lastBackup = Messages.ExportPreferencesPreferencePage_NONE;
		}

		Label lastBacked = new Label(c, SWT.NONE);		
		lastBacked.setText(Messages.ExportPreferencesPreferencePage_LASTBACKED);
		Text lastBackedLabel = new Text(c, SWT.READ_ONLY | SWT.BORDER | SWT.SINGLE);
		lastBackedLabel.setEditable(false);
		lastBackedLabel.setText(lastBackup);
		
		data = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		data.widthHint = 150;
		lastBackedLabel.setLayoutData(data);

		Label cm = new Label(c, SWT.NONE);		
		cm.setText(Messages.ExportPreferencesPreferencePage_LAST);
		lastRestore = new Text(c, SWT.READ_ONLY | SWT.BORDER | SWT.SINGLE);
		lastRestore.setEditable(false);
		
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.widthHint = 150;
		lastRestore.setLayoutData(data);

		checkBox.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				stringButtonFieldEditor.setEnabled(checkBox.getSelection(), c);
			}

		});
		
		Button restore = new Button(c, SWT.PUSH);
		restore.setText(Messages.ExportPreferencesPreferencePage_RESTORE_TITLE);
		restore.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
				dlg.setFilterPath(stringButtonFieldEditor.getStringValue());
				dlg.setFilterExtensions(new String[] { "*.epr" }); //$NON-NLS-1$
				dlg.setText(Messages.ExportPreferencesPreferencePage_RESTORE_TEXT);
				String open = dlg.open();
				if (open != null)
				{
					long l = System.currentTimeMillis();				
					File file = new File(open);		
					
					boolean importPreferences = PlatformValidatorPatcher.importPreferences(file);
					
					if (importPreferences)
					{
						CoreUIPlugin.getDefault().getPreferenceStore().setValue(IPreferenceConstants.PREF_AUTO_BACKUP_LASTRESTORE_NAME, file.getName());
						CoreUIPlugin.getDefault().getPreferenceStore().setValue(IPreferenceConstants.PREF_AUTO_BACKUP_LASTRESTORE_TIME, l);
						updatePreferences();					
					}
				}
			}
		});

	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#initialize()
	 */
	protected void initialize()
	{
		super.initialize();
		stringButtonFieldEditor.setEnabled(checkBox.getSelection(), getFieldEditorParent());
		updatePreferences();
	}

}
