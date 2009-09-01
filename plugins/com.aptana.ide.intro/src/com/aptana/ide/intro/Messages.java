/**
 * Copyright (c) 2005-2009 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.intro;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.intro.messages"; //$NON-NLS-1$

    public static String FeatureChangeManager_INF_Feature;

    public static String FeatureChangeManager_INF_PersistList;

    public static String FeatureInstallJob_ERR_SearchSite;

    public static String FeatureInstallJob_INF_AddSite;

    public static String FeatureInstallJob_Job_SearchFeatures;

    public static String FeatureInstallJob_Title;

    public static String IntroStartup_INF_AddSite;

    public static String IntroStartup_InstallMessage;

    public static String IntroStartup_InstallTitle;

    public static String IntroStartup_Job_InstallFeatures;

    public static String IntroStartup_Job_SearchUpdate;

    public static String IntroStartup_Job_UpdateFeatures;

    public static String IntroStartup_Name;

    public static String IntroStartup_UpdateMessage;

    public static String IntroStartup_UpdateTitle;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
