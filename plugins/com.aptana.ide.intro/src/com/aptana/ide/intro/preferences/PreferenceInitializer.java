/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.intro.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.ide.intro.IntroPlugin;
import com.aptana.ide.server.portal.ui.MyAptanaEditor;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    private static final String DEFAULT_REMOTE_IMAGE_LOCATION = "http://ide.aptana.com/content_ide/images/toolbar_intro.gif"; //$NON-NLS-1$

	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = IntroPlugin.getDefault().getPreferenceStore();
		store.setDefault(IPreferenceConstants.INSTALL_PRO_AND_REQUIRED_FEATURES, true);
		store.setDefault(IPreferenceConstants.SHOW_STARTPAGE_ON_STARTUP, IPreferenceConstants.ALWAYS_SHOW);
		// by default uses My Aptana page
		store.setDefault(IPreferenceConstants.INTRO_EDITOR_ID, MyAptanaEditor.ID);
		store.setDefault(IPreferenceConstants.INTRO_TOOLBAR_IMAGE_LOCATION, DEFAULT_REMOTE_IMAGE_LOCATION);
	}
}
