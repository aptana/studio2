package com.aptana.ide.update.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.ide.update.ui.UpdateUIActivator;

public class MessagingPreferencesInitializer extends
		AbstractPreferenceInitializer {

	private static final String DEFAULT_RELEASE_MESSAGE_URL_PREFIX = "http://content.aptana.com/aptana/studio/messaging/release/"; //$NON-NLS-1$
	private static final String DEFAULT_NEWS_MESSAGE_URL_PREFIX = "http://content.aptana.com/aptana/studio/messaging/news/"; //$NON-NLS-1$
	private static final String DEFAULT_ANNOUNCEMENT_URL_PREFIX = "http://content.aptana.com/aptana/studio/messaging/announce/"; //$NON-NLS-1$
	private static final boolean DEFAULT_NEVER_SHOW_THIS_ANNOUNCEMENT = false;
	private static final boolean DEFAULT_NEVER_SHOW_ANNOUNCEMENTS = false;
	
	public MessagingPreferencesInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		

		final IPreferenceStore prefs = UpdateUIActivator.getDefault().getPreferenceStore();
        prefs.setDefault(IPreferenceConstants.RELEASE_MESSAGE_URL_PREFIX,
                DEFAULT_RELEASE_MESSAGE_URL_PREFIX);
        prefs.setDefault(IPreferenceConstants.NEWS_MESSAGE_URL_PREFIX,
                DEFAULT_NEWS_MESSAGE_URL_PREFIX);
        prefs.setDefault(IPreferenceConstants.ANNOUNCEMENT_URL_PREFIX,
                DEFAULT_ANNOUNCEMENT_URL_PREFIX);
        prefs.setDefault(IPreferenceConstants.NEVER_SHOW_THIS_ANNOUNCEMENT, DEFAULT_NEVER_SHOW_THIS_ANNOUNCEMENT);
		prefs.setDefault(IPreferenceConstants.NEVER_SHOW_ANNOUNCEMENTS, DEFAULT_NEVER_SHOW_ANNOUNCEMENTS);

	}

}
