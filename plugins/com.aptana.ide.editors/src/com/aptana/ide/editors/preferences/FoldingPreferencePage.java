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
package com.aptana.ide.editors.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.folding.FoldingExtensionPointLoader;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public abstract class FoldingPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private Composite displayArea;
	private Group foldingGroup;
	private Button enableFolding;
	private List foldingFields = new ArrayList();

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		String pref = FoldingExtensionPointLoader.createEnablePreferenceId(getLanguage());
		IPreferenceStore storeToInitialize = storeToInitialize();
		if (storeToInitialize != null)
		{
			// Make dummy call to initialize store
			storeToInitialize.getBoolean(pref);
		}
		displayArea = new Composite(parent, SWT.NONE);
		displayArea.setLayout(new GridLayout(1, true));
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		foldingGroup = new Group(displayArea, SWT.NONE);
		foldingGroup.setLayout(new GridLayout(1, true));
		foldingGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		foldingGroup.setText(Messages.FoldingPreferencePage_FoldingOptions);

		enableFolding = new Button(foldingGroup, SWT.CHECK);
		enableFolding.setText(Messages.FoldingPreferencePage_EnableFolding);

		enableFolding.setData(pref);
		IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		enableFolding.setSelection(store.getBoolean(pref));
		enableFolding.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				for (int i = 0; i < foldingFields.size(); i++)
				{
					Object obj = foldingFields.get(i);
					if (obj instanceof Button)
					{
						Button button = (Button) obj;
						button.setEnabled(enableFolding.getSelection());
					}
				}
			}

		});

		addInitialFoldingFields();

		return displayArea;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		String enable = (String) enableFolding.getData();
		enableFolding.setSelection(store.getDefaultBoolean(enable));
		for (int i = 0; i < foldingFields.size(); i++)
		{
			Object obj = foldingFields.get(i);
			if (obj instanceof Button)
			{
				Button button = (Button) obj;
				Object obj2 = button.getData();
				if (obj2 instanceof String)
				{
					String data = (String) obj2;
					button.setEnabled(store.getDefaultBoolean(enable));
					button.setSelection(store.getDefaultBoolean(data));
				}
			}

		}
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk()
	{
		IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		String enable = (String) enableFolding.getData();
		store.setValue(enable, enableFolding.getSelection());
		for (int i = 0; i < foldingFields.size(); i++)
		{
			Object obj = foldingFields.get(i);
			if (obj instanceof Button)
			{
				Button button = (Button) obj;
				Object obj2 = button.getData();
				if (obj2 instanceof String)
				{
					String data = (String) obj2;
					store.setValue(data, button.getSelection());
				}
			}

		}
		return super.performOk();
	}

	/**
	 * Gets the language
	 * 
	 * @return - language mime type
	 */
	public abstract String getLanguage();

	/**
	 * Gets the local pref store to initialize if it is setting prefs in UnifiedEditorPlugin pref store
	 * 
	 * @return - local pref store to initialized
	 */
	public abstract IPreferenceStore storeToInitialize();

	/**
	 * Adds the initial folding fields
	 */
	public abstract void addInitialFoldingFields();

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{

	}

	/**
	 * Add initial folding field for a node name and language
	 * 
	 * @param language
	 * @param name
	 * @param label
	 */
	protected void addInitialFoldingField(String language, String name, String label)
	{
		String prefId = FoldingExtensionPointLoader.createInitialFoldingPreferenceId(language, name);
		Button foldField = new Button(foldingGroup, SWT.CHECK);
		foldField.setText(label);
		foldField.setData(prefId);
		IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		foldField.setSelection(store.getBoolean(prefId));
		foldField.setEnabled(enableFolding.getSelection());
		foldingFields.add(foldField);
	}

}
