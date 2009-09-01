package com.aptana.ide.update.preferences;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;

import com.aptana.ide.update.ui.UpdateUIActivator;
import com.aptana.ide.update.ui.Messages;

public class MessagingPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage{

	private Button bTurnOffThisAnnouncement;
	private Button bTurnOffAllAnnouncements;

	/**
	 * Create the preference page.
	 */
	public MessagingPreferencesPage() {
	}

	/**
	 * Create contents of the preference page.
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);
		{
			Label Announcements = new Label(container, SWT.NONE);
			Announcements.setText(Messages.BrowserDialog_AnnouncementTitle);
		}

       IPreferenceStore prefs = UpdateUIActivator.getDefault().getPreferenceStore();
		{
			bTurnOffThisAnnouncement = new Button(container, SWT.CHECK);
			bTurnOffThisAnnouncement.setText(Messages.BrowserDialog_Label_DoNotShowThisAnnouncementAgain);
			boolean neverShowThisAgain = prefs.getBoolean(IPreferenceConstants.NEVER_SHOW_THIS_ANNOUNCEMENT);
			bTurnOffThisAnnouncement.setSelection(neverShowThisAgain);
		}
		{
			bTurnOffAllAnnouncements = new Button(container, SWT.CHECK);
			bTurnOffAllAnnouncements.setText(Messages.BrowserDialog_Label_DoNotShowAllAnnouncements);
			boolean neverShowThisAgain = prefs.getBoolean(IPreferenceConstants.NEVER_SHOW_ANNOUNCEMENTS);
			bTurnOffAllAnnouncements.setSelection(neverShowThisAgain);
		}

		return container;
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(UpdateUIActivator.getDefault().getPreferenceStore());
	}
	
	
	
	@Override
	protected void performApply() {
		store();
		super.performApply();
	}

	@Override
	public boolean performOk() {
		store();
		return super.performOk();
	}
	
	private void store() {
		IPreferenceStore prefs = getPreferenceStore();
        prefs.setValue(IPreferenceConstants.NEVER_SHOW_THIS_ANNOUNCEMENT, bTurnOffThisAnnouncement.getSelection());
        prefs.setValue(IPreferenceConstants.NEVER_SHOW_ANNOUNCEMENTS, bTurnOffAllAnnouncements.getSelection());
		
	}
}
