package com.aptana.ide.scripting.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.ide.scripting.preferences.messages"; //$NON-NLS-1$
	public static String GeneralPreferencePage_LBL_StartScriptingServerOnStudioStartup;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
