package com.aptana.ide.syncing.doms;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "com.aptana.ide.syncing.doms.messages"; //$NON-NLS-1$

	public static String Sync_ERR_YouMustHaveACurrentlyOpenEditorToDownload;
	public static String Sync_ERR_YouMustHaveACurrentlyOpenEditorToUpload;
	public static String Sync_TTL_UnableToDownload;
	public static String Sync_TTL_UnableToUpload;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
