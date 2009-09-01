package com.aptana.ide.editor.html.formatting;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.aptana.ide.editor.html.formatting.messages"; //$NON-NLS-1$
    public static String HTMLAutoIndentStrategy_ERR_ClosingTag;
    public static String HTMLPairTagModifyStrategy_ERR_TagRemoval;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
