package com.aptana.ide.ui.io.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.ui.io.actions.messages"; //$NON-NLS-1$

    public static String CopyFilesOperation_Copy_Subtask;
    public static String CopyFilesOperation_CopyJob_Title;
    public static String CopyFilesOperation_ERR_DestinationInSource;
    public static String CopyFilesOperation_ERR_SourceInDestination;
    public static String CopyFilesOperation_OverwriteWarning;
    public static String CopyFilesOperation_QuestionTitle;
    public static String CopyFilesOperation_Status_OK;

    public static String MoveFilesOperation_Subtask_Moving;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
