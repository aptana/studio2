/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.intro.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.intro.IntroPlugin;
import com.aptana.ide.server.jetty.JettyPlugin;

/**
 * Start page preference page
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class IntroPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private BooleanFieldEditor portalBrowserEditor;
	private RadioGroupFieldEditor showStartPageEditor;

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{

		Composite entryTable = new Composite(parent, SWT.NULL);

		// Create a data that takes up the extra space in the dialog .
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		entryTable.setLayoutData(data);

		GridLayout layout = new GridLayout();
		entryTable.setLayout(layout);

		Composite colorComposite = new Composite(entryTable, SWT.NONE);

		colorComposite.setLayout(new GridLayout());

		// Create a data that takes up the extra space in the dialog.
		colorComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		showStartPageEditor = new RadioGroupFieldEditor(IPreferenceConstants.SHOW_STARTPAGE_ON_STARTUP,
				Messages.IntroPreferencePage_StartPageOptions, 1, new String[][] {
						{ Messages.IntroPreferencePage_AlwaysDisplayAtStart, IPreferenceConstants.ALWAYS_SHOW },
						{ Messages.IntroPreferencePage_AlwaysDisplayAfterAnyUpdates,
								IPreferenceConstants.SHOW_ALL_UPDATES },
						{ Messages.IntroPreferencePage_AlwaysDisplayAfterAptanaUpdates,
								IPreferenceConstants.SHOW_APTANA_UPDATES },
						{ Messages.IntroPreferencePage_NeverDisplayAfterStartup, IPreferenceConstants.NEVER_SHOW } },
				colorComposite, true);
		showStartPageEditor.setPage(this);
		showStartPageEditor.setPreferenceStore(getPreferenceStore());
		showStartPageEditor.load();

		if (CoreUIUtils.onWindows)
		{
			portalBrowserEditor = new BooleanFieldEditor(
					com.aptana.ide.server.jetty.preferences.IPreferenceConstants.USE_FIREFOX,
					Messages.IntroPreferencePage_LBL_UseFirefox, colorComposite);
			portalBrowserEditor.setPage(this);
			portalBrowserEditor.setPreferenceStore(JettyPlugin.getDefault().getPreferenceStore());
			portalBrowserEditor.load();
		}
		return entryTable;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		// Initialize the preference store we wish to use
		setPreferenceStore(IntroPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Performs special processing when this page's Restore Defaults button has been pressed. Sets the contents of the
	 * color field to the default value in the preference store.
	 */
	protected void performDefaults()
	{
		showStartPageEditor.loadDefault();
		if (CoreUIUtils.onWindows)
		{
			portalBrowserEditor.loadDefault();
		}
	}

	/**
	 * Method declared on IPreferencePage. Save the color preference to the preference store.
	 * 
	 * @return boolean
	 */
	public boolean performOk()
	{
		showStartPageEditor.store();
		if (CoreUIUtils.onWindows)
		{
			portalBrowserEditor.store();
		}
		return super.performOk();
	}

	/**
	 * Gets the preference store
	 * 
	 * @return - preference store
	 */
	protected IPreferenceStore doGetPreferenceStore()
	{
		return IntroPlugin.getDefault().getPreferenceStore();
	}

}
