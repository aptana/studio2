package com.aptana.ide.core.ui.contentassist;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.core.ui.contentassist.messages"; //$NON-NLS-1$

    public static String ContentAssistHandler_TXT_CA_Available;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
