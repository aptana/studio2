/**
 * 
 */
package com.aptana.ide.core.ui.preferences;

import com.aptana.ide.core.ui.PerspectiveManager;
import com.aptana.ide.core.ui.WebPerspectiveFactory;

/**
 * Extends the basic {@link IPreferenceConstants} with constants that requires other class references. This is needed
 * especially for pydev, which imported the {@link IPreferenceConstants} and we don't want to force it to import the
 * dependent classes.
 * 
 * @author Shalom Gibly
 */
public interface IPreferencesConstants2 extends IPreferenceConstants
{
	/**
	 * Preference for the preferences switch action that should be taken when the user hit an Aptana action outside the
	 * Aptana perspective, and we want to notify that a perspective switch is available.
	 */
	String SWITCH_TO_APTANA_PRESPECTIVE = WebPerspectiveFactory.PERSPECTIVE_ID + PerspectiveManager.SWITCH_KEY_SUFFIX;
}
