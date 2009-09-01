package com.aptana.ide.desktop.integration.protocolhandler.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.desktop.integration.protocolhandler.actions.messages"; //$NON-NLS-1$

    public static String ProtocolHandlerAction_MacDialogMessage;
    public static String ProtocolHandlerAction_MacDialogTitle;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
