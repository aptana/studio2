package com.aptana.ide.core.ui.views.browser;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.ide.core.ui.views.browser.messages"; //$NON-NLS-1$
	public static String DefaultBrowserView_ERR_UnableToResolveURL;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
