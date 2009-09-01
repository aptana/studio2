package org.eclipse.eclipsemonkey.doms.resources;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.eclipsemonkey.doms.resources.messages"; //$NON-NLS-1$
	public static String Resources_Standard_marker_name;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
