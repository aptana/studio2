package com.aptana.ide.update.eclipse36;

import org.eclipse.osgi.util.NLS;

public class P2Eclipse36Messages extends NLS {

    private static final String BUNDLE_NAME = P2Eclipse36Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$

    public static String P2PluginManager_ERR_MSG_Artifact_repo_not_found;
    public static String P2PluginManager_ERR_MSG_Metadata_repo_not_found;
    public static String P2PluginManager_ERR_MSG_No_installable_units_found;
    public static String P2PluginManager_Locating_selected_features_job_title;
    public static String ProfileModificationAction_UnexpectedError;

    static {
        NLS.initializeMessages(BUNDLE_NAME, P2Eclipse36Messages.class);
    }

}
