package com.aptana.ide.core.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class EPLMessages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.core.ui.preferences.eplmessages"; //$NON-NLS-1$

    public static String ProjectNaturesPage_ERR_CloseProject;
    public static String ProjectNaturesPage_ERR_NaturePage;
    public static String ProjectNaturesPage_Job_CloseProject;
    public static String ProjectNaturesPage_LBL_MakePrimary;
    public static String ProjectNaturesPage_LBL_Restore;
    public static String ProjectNaturesPage_LBL_SetPrimary;
    public static String ProjectNaturesPage_MissingDescription;
    public static String ProjectNaturesPage_ResetMessage;
    public static String ProjectNaturesPage_ResetTitle;
    public static String ProjectNaturesPage_TXT_AdditionalNatures;
    public static String ProjectNaturesPage_TXT_Primary;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, EPLMessages.class);
    }

    private EPLMessages() {
    }
}
