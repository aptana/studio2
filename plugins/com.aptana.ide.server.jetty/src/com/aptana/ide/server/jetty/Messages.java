package com.aptana.ide.server.jetty;

import org.eclipse.osgi.util.NLS;

/**
 * @author Pavel Petrochenko
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.server.jetty.messages"; //$NON-NLS-1$
	/**
	 * JettyServer_STOP_EXCEPTION
	 */
	public static String JettyServer_STOP_EXCEPTION;
	/**
     * JettyServer_START_EXCEPTION
     */
    public static String JettyServer_START_EXCEPTION;
    public static String JettyServer_Status_Exception;
	/**
	 * JettyServer_DESCRIPTION
	 */
	public static String JettyServer_DESCRIPTION;
    public static String PreferenceClient_Job_RunJaxer;
	/**
	 * ShowPerspectiveClient_1
	 */
	public static String ShowPerspectiveClient_1;
	/**
	 * ShowPerspectiveClient_2
	 */
	public static String ShowPerspectiveClient_2;
    public static String ShowProjectClient_Job_DisplayProject;
    public static String ShowViewClient_Job_OpenView;

	private Messages()
	{
	}

	static{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
