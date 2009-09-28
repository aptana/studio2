package com.aptana.ide.update.preferences;

import com.aptana.ide.update.ui.UpdateUIActivator;

public interface IPreferenceConstants {

    /**
	 * 
	 */
    public static final String RELEASE_MESSAGE_URL_PREFIX = UpdateUIActivator.PLUGIN_ID
            + ".RELEASE_MESSAGE_URL_PREFIX"; //$NON-NLS-1$
    /**
	 * 
	 */
    public static final String NEWS_MESSAGE_URL_PREFIX = UpdateUIActivator.PLUGIN_ID
            + ".NEWS_MESSAGE_URL_PREFIX"; //$NON-NLS-1$
    /**
	 * 
	 */
    public static final String ANNOUNCEMENT_URL_PREFIX = UpdateUIActivator.PLUGIN_ID
            + ".ANNOUNCEMENT_URL_PREFIX"; //$NON-NLS-1$
    /*
     * Used to determine if Announcement should never be shown.
     */
    public static final String NEVER_SHOW_ANNOUNCEMENTS = UpdateUIActivator.PLUGIN_ID
            + ".NEVER_SHOW_ANNOUNCEMENTS"; //$NON-NLS-1$
    /*
     * Never Show a Specific Announcement Again
     */
    public static final String NEVER_SHOW_THIS_ANNOUNCEMENT = UpdateUIActivator.PLUGIN_ID
            + ".NEVER_SHOW_THIS_ANNOUNCEMENT"; //$NON-NLS-1$
    /*
     * Never Show a Specific Announcement Again
     */
    public static final String NEVER_SHOW_THIS_ANNOUNCEMENT_LAST_LAST_MODIFIED = UpdateUIActivator.PLUGIN_ID
            + ".NEVER_SHOW_AGAIN_LAST_LAST_MODIFIED"; //$NON-NLS-1$
}
