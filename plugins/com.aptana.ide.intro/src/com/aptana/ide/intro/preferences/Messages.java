/**
 * Copyright (c) 2005-2009 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.intro.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.intro.preferences.messages"; //$NON-NLS-1$

    /**
     * IntroPreferencePage_AlwaysDisplayAfterAnyUpdates
     */
    public static String IntroPreferencePage_AlwaysDisplayAfterAnyUpdates;

    /**
     * IntroPreferencePage_AlwaysDisplayAtStart
     */
    public static String IntroPreferencePage_AlwaysDisplayAtStart;

    public static String IntroPreferencePage_LBL_UseFirefox;

    /**
     * IntroPreferencePage_NeverDisplayAfterStartup
     */
    public static String IntroPreferencePage_NeverDisplayAfterStartup;

    /**
     * IntroPreferencePage_StartPageOptions
     */
    public static String IntroPreferencePage_StartPageOptions;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
