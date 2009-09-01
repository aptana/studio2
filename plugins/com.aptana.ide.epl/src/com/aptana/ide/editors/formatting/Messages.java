package com.aptana.ide.editors.formatting;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.editors.formatting.messages"; //$NON-NLS-1$

    public static String UnifiedBracketInserterBase_ERR_BadLocation;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
