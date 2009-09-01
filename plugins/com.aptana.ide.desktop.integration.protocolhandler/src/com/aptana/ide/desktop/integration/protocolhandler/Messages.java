package com.aptana.ide.desktop.integration.protocolhandler;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.desktop.integration.protocolhandler.messages"; //$NON-NLS-1$

    public static String ProtocolHandlerStartup_ERR_CannotLocateLauncher;
    public static String ProtocolHandlerStartup_ERR_CannotLocateProtocolhandler;
    public static String ProtocolHandlerStartup_INF_FinishedUnJar;
    public static String ProtocolHandlerStartup_INF_UnjarringApp;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
