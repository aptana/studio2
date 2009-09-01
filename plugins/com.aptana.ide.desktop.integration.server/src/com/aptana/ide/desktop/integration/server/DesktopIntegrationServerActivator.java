package com.aptana.ide.desktop.integration.server;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DesktopIntegrationServerActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.ide.desktop.integration.server"; //$NON-NLS-1$

	// The shared instance
	private static DesktopIntegrationServerActivator plugin;

    private LaunchHelper launchHelper;

	/**
	 * The constructor
	 */
	public DesktopIntegrationServerActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DesktopIntegrationServerActivator getDefault() {
		return plugin;
	}

	/**
	 * getLaunchHelper
	 * 
	 * @return LaunchHelper
	 */
	public LaunchHelper getLaunchHelper()
	{
		if (launchHelper == null)
		{
			launchHelper = new LaunchHelper();
		}
		return launchHelper;
	}
}
