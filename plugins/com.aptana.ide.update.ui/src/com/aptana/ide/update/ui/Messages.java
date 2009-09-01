package com.aptana.ide.update.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.ide.update.ui.messages"; //$NON-NLS-1$
	public static String BrowserDialog_AnnouncementTitle;
	public static String BrowserDialog_AptanaNewsTitle;
	public static String BrowserDialog_Label_DoNotShowAllAnnouncements;
	public static String BrowserDialog_Label_DoNotShowThisAnnouncementAgain;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
