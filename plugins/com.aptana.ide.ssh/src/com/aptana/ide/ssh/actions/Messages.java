package com.aptana.ide.ssh.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.ide.ssh.actions.messages"; //$NON-NLS-1$
	public static String SSHToHostAction_LBL_SSHToHostAction;
	public static String SSHToHostAction_MSG_UserAtHost;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
