package com.aptana.ide.update;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class P2Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.ide.update.eclipse34"; //$NON-NLS-1$

	// The shared instance
	private static P2Activator plugin;

    private static BundleContext context;

	/**
	 * The constructor
	 */
	public P2Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		P2Activator.context = context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		P2Activator.context = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static P2Activator getDefault() {
		return plugin;
	}

	public static BundleContext getContext() {
	    return context;
	}
}
