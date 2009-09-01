package org.eclipse.eclipsemonkey.utils;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.eclipsemonkey.utils.messages"; //$NON-NLS-1$
	public static String BundleClassLoader_BundleMustNotBeNull;
	public static String BundleClassLoader_UnableToFindClass;
	public static String BundleClassLoader_UnableToFindResources;
	public static String BundleClassLoader_UnableToLoadClass;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
