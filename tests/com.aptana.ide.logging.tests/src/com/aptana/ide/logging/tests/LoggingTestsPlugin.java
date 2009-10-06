package com.aptana.ide.logging.tests;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LoggingTestsPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.ide.logging.tests";

	// The shared instance
	private static LoggingTestsPlugin plugin;
	
	/**
	 * The constructor
	 */
	public LoggingTestsPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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
	public static LoggingTestsPlugin getDefault() {
		return plugin;
	}

}
