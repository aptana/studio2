/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.server.internal;

import org.eclipse.osgi.util.NLS;


/**
 * @author Pavel Petrochenko
 *
 */
public final class Messages  extends NLS{
	private static final String BUNDLE_NAME = "com.aptana.ide.server.internal.messages"; //$NON-NLS-1$
	/**
	 * ApacheServerTypeDelegate_EDIT
	 */
	public static String ApacheServerTypeDelegate_EDIT;
	/**
	 * ApacheServerTypeDelegate_DESC
	 */
	public static String ApacheServerTypeDelegate_DESC;
	/**
	 * ApacheServerTypeDelegate_ADD
	 */
	public static String ApacheServerTypeDelegate_ADD;
	/**
	 * ApacheServerTypeDelegate_ADD_DESC
	 */
	public static String ApacheServerTypeDelegate_ADD_DESC;
	/**
	 * LaunchUtils_ERROR_MESSAGE
	 */
	public static String LaunchUtils_ERROR_MESSAGE;
	/**
	 * MySqlServerTypeDelegate_EDIT
	 */
	public static String MySqlServerTypeDelegate_EDIT;
	/**
	 * MySqlServerTypeDelegate_EDIT_TITLE
	 */
	public static String MySqlServerTypeDelegate_EDIT_TITLE;
	/**
	 * MySqlServerTypeDelegate_ADD
	 */
	public static String MySqlServerTypeDelegate_ADD;
	/**
	 * MySqlServerTypeDelegate_ADD_TITLE
	 */
	public static String MySqlServerTypeDelegate_ADD_TITLE;
	public static String XAMPPServer_Error_Start;
    public static String XAMPPServer_Error_Stop;
    public static String XAMPPServer_Error_Title;
    /**
	 * XAMPPServerTypeDelegate_EDIT
	 */
	public static String XAMPPServerTypeDelegate_EDIT;
	/**
	 * XAMPPServerTypeDelegate_EDIT_TITLE
	 */
	public static String XAMPPServerTypeDelegate_EDIT_TITLE;
	/**
	 * XAMPPServerTypeDelegate_ADD
	 */
	public static String XAMPPServerTypeDelegate_ADD;
	/**
	 * XAMPPServerTypeDelegate_ADD_TITLE
	 */
	public static String XAMPPServerTypeDelegate_ADD_TITLE;
	/**
	 * ApacheServer_ONLY_RUNNING_MAY_BE_PAUSED
	 */
	public static String ApacheServer_ONLY_RUNNING_MAY_BE_PAUSED;
	/**
	 * ApacheServer_ALREADY_PAUSED
	 */
	public static String ApacheServer_ALREADY_PAUSED;
	/**
	 * ApacheServer_ONLY_PAUSED_MAY_BE_RESUMED
	 */
	public static String ApacheServer_ONLY_PAUSED_MAY_BE_RESUMED;


	private Messages() {
	}

	static{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
