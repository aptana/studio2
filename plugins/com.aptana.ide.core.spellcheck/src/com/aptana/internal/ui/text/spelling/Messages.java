package com.aptana.internal.ui.text.spelling;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.aptana.internal.ui.text.spelling.messages"; //$NON-NLS-1$
    public static String JavaUIMessages_INF_Add;
    public static String JavaUIMessages_INF_Disable;
    public static String JavaUIMessages_INF_Ignore;
    public static String JavaUIMessages_LBL_Add;
    public static String JavaUIMessages_LBL_Disable;
    public static String JavaUIMessages_LBL_Ignore;
    public static String JavaUIMessages_LBL_Spellcase;
    public static String JavaUIMessages_MSG_Ignore;
    public static String JavaUIMessages_Ques_Configure;
    public static String JavaUIMessages_Title_Configure;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
