package com.aptana.ide.pathtools;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.pathtools.messages"; //$NON-NLS-1$

    public static String CommandLauncher_ERR_Exception;
    public static String CommandLauncher_Message_ProcessExited;

    public static String ExplorePlacesActions_TXT_ConfigFolder;
    public static String ExplorePlacesActions_TXT_InstallFolder;
    public static String ExplorePlacesActions_TXT_UserDataFolder;
    public static String ExplorePlacesActions_TXT_WorkspaceFolder;
    public static String ExplorePlacesActions_TXT_WorkspaceMetadata;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
