package com.aptana.ide.core.io.syncing;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.core.io.syncing.messages"; //$NON-NLS-1$
	public static String VirtualFileSyncPair_DestFileInfoError;
	public static String VirtualFileSyncPair_SourceFileInfoError;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
