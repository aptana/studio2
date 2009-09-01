package com.aptana.ide.desktop.integration.server;

import org.eclipse.ui.IStartup;

public class DesktopIntegrationServerStartup implements IStartup {

	public void earlyStartup() {
		// Start the server to accept command line args
		DesktopIntegrationServerActivator.getDefault().getLaunchHelper().startServer();
	}
}
