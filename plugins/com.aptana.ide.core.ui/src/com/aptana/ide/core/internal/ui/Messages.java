package com.aptana.ide.core.internal.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.ide.core.internal.ui.messages"; //$NON-NLS-1$
	public static String NaturePropertyTester_ERR_ExceptionWhileTestingNature;
	public static String NaturePropertyTester_NATURE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
