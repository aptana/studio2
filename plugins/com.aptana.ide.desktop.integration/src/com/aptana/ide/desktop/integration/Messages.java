package com.aptana.ide.desktop.integration;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.desktop.integration.messages"; //$NON-NLS-1$

    public static String Application_ERR_UnableToGetRunningInstance;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
