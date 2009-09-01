/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain Eclipse Public Licensed code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.server.ui.generic.dialogs;

import org.eclipse.osgi.util.NLS;

/**
 * @author Pavel Petrochenko
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.server.ui.generic.dialogs.messages"; //$NON-NLS-1$
	/**
	 * GenericConfigurationDialog_5
	 */
	public static String GenericConfigurationDialog_5;
	/**
	 * GenericConfigurationDialog_BROWSE
	 */
	public static String GenericConfigurationDialog_BROWSE;
	/**
	 * GenericConfigurationDialog_GENERIC_
	 */
	public static String GenericConfigurationDialog_GENERIC_;
	/**
	 * GenericConfigurationDialog_HOST_NAME_MUST_NOT_BE_BLANK
	 */
	public static String GenericConfigurationDialog_HOST_NAME_MUST_NOT_BE_BLANK;
	/**
	 * GenericConfigurationDialog_HOSTNAME
	 */
	public static String GenericConfigurationDialog_HOSTNAME;
	public static String GenericConfigurationDialog_INF_IntervalError;
    /**
	 * GenericConfigurationDialog_IS_SERVER_LOCAL
	 */
	public static String GenericConfigurationDialog_IS_SERVER_LOCAL;
	/**
	 * GenericConfigurationDialog_NO_FILE_UNDER_A_GIVEN_PATH
	 */
	public static String GenericConfigurationDialog_NO_FILE_UNDER_A_GIVEN_PATH;
	/**
	 * GenericConfigurationDialog_PATH
	 */
	public static String GenericConfigurationDialog_PATH;
	/**
	 * GenericConfigurationDialog_PATH_SHOULD_NOT_POINT_TO_DIRECTORY
	 */
	public static String GenericConfigurationDialog_PATH_SHOULD_NOT_POINT_TO_DIRECTORY;
	/**
	 * GenericConfigurationDialog_PATH_TO_LOG_FILE
	 */
	public static String GenericConfigurationDialog_PATH_TO_LOG_FILE;
	/**
	 * GenericConfigurationDialog_PATH_TO_LOG_FILE_SHOULD_BE_EMPTY_OR_POINT_TO_FILE
	 */
	public static String GenericConfigurationDialog_PATH_TO_LOG_FILE_SHOULD_BE_EMPTY_OR_POINT_TO_FILE;
	/**
	 * GenericConfigurationDialog_PAUSE_SERVER
	 */
	public static String GenericConfigurationDialog_PAUSE_SERVER;
	/**
	 * GenericConfigurationDialog_PORT_MUST_BE_IN_RANGE
	 */
	public static String GenericConfigurationDialog_PORT_MUST_BE_IN_RANGE;
	/**
	 * GenericConfigurationDialog_PORT_MUST_BE_INTEGER
	 */
	public static String GenericConfigurationDialog_PORT_MUST_BE_INTEGER;
	/**
	 * GenericConfigurationDialog_RESUME_SERVER
	 */
	public static String GenericConfigurationDialog_RESUME_SERVER;
	/**
	 *  GenericConfigurationDialog_SERVER_DESCRIPTION
	 */
	public static String GenericConfigurationDialog_SERVER_DESCRIPTION;
	/**
	 * GenericConfigurationDialog_SERVER_NAME
	 */
	public static String GenericConfigurationDialog_SERVER_NAME;
	/**
	 * GenericConfigurationDialog_SERVER_NAME_EXISTS
	 */
	public static String GenericConfigurationDialog_SERVER_NAME_EXISTS;
	/**
	 * GenericConfigurationDialog_SERVER_NAME_MUST_NOT_BE_BLANK
	 */
	public static String GenericConfigurationDialog_SERVER_NAME_MUST_NOT_BE_BLANK;
	/**
	 * GenericConfigurationDialog_SHELL_TITLE
	 */
	public static String GenericConfigurationDialog_SHELL_TITLE;
	/**
	 * GenericConfigurationDialog_START_IS_EMPTY
	 */
	public static String GenericConfigurationDialog_START_IS_EMPTY;
	/**
	 * GenericConfigurationDialog_START_SERVER
	 */
	public static String GenericConfigurationDialog_START_SERVER;
	/**
	 * GenericConfigurationDialog_STOP_IS_EMPTY
	 */
	public static String GenericConfigurationDialog_STOP_IS_EMPTY;
	/**
	 * GenericConfigurationDialog_STOP_SERVER
	 */
	public static String GenericConfigurationDialog_STOP_SERVER;
	/**
	 * GenericConfigurationDialog_TITLE
	 */
	public static String GenericConfigurationDialog_TITLE;
	/**
	 * GenericConfigurationDialog_DOCUMENT_ROOT
	 */
	public static String GenericConfigurationDialog_DOCUMENT_ROOT;
	/**
	 * GenericConfigurationDialog_DOCUMENT_ROOT_ERROR
	 */
	public static String GenericConfigurationDialog_DOCUMENT_ROOT_ERROR;
	public static String GenericConfigurationDialog_LBL_Banner;
    public static String GenericConfigurationDialog_LBL_Heartbeat;
    public static String GenericConfigurationDialog_LBL_Polling;
    public static String GenericConfigurationDialog_MSG_InvalidHeartbeat;
    public static String GenericConfigurationDialog_MSG_PollingBound;
    public static String GenericConfigurationDialog_MSG_PollingInteger;
    /**
	 * ServerTypeSelectionDialog_CHOOSE_SERVER_TITLE
	 */
	public static String ServerTypeSelectionDialog_CHOOSE_SERVER_TITLE;
	/**
	 * ServerTypeSelectionDialog_DESCRIPTION
	 */
	public static String ServerTypeSelectionDialog_DESCRIPTION;
	/**
	 * ServerTypeSelectionDialog_PLEASE_SELECT_SERVER_TYPE
	 */
	public static String ServerTypeSelectionDialog_PLEASE_SELECT_SERVER_TYPE;
	/**
	 * ServerTypeSelectionDialog_TITLE
	 */
	public static String ServerTypeSelectionDialog_TITLE;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
