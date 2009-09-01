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
 * with certain other free and open source software ("FOSS") code and certain additional terms
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
package com.aptana.ide.core.ui.preferences;

import com.aptana.ide.core.ui.PerspectiveManager;
import com.aptana.ide.core.ui.WebPerspectiveFactory;

/**
 * Contains all preferences for the com.aptana.ide.core.ui plugin To add a preference, create a static string with an
 * all-uppercase preference key. Then assign a identically-named string to it, prefixing it with the plugin name" i.e.
 * SHOW_WHITESPACE = "com.aptana.ide.core.ui.SHOW_WHITESPACE"
 * 
 * @author Ingo Muschenetz
 */
public interface IPreferenceConstants
{
	// TODO: Rename these to fit plugin preference conventions

	/**
	 * P_ENABLED
	 */
	String P_ENABLED = "enabled"; //$NON-NLS-1$

	/**
	 * P_SCHEDULE
	 */
	String P_SCHEDULE = "schedule"; //$NON-NLS-1$

	/**
	 * VALUE_ON_STARTUP
	 */
	String VALUE_ON_STARTUP = "on-startup"; //$NON-NLS-1$

	/**
	 * VALUE_ON_SCHEDULE
	 */
	String VALUE_ON_SCHEDULE = "on-schedule"; //$NON-NLS-1$

	/**
	 * P_DOWNLOAD
	 */
	String P_DOWNLOAD = "download"; // value is true or false, default is false //$NON-NLS-1$

	/**
	 * P_IDE_ID
	 */
	String P_IDE_ID = "ide-id"; //$NON-NLS-1$

	/**
	 * P_IDE_HAS_RUN
	 */
	String P_IDE_HAS_RUN = "ide-has-run"; //$NON-NLS-1$

	/**
	 * COMPARE_IN_BACKGROUND
	 */
	public static final String COMPARE_IN_BACKGROUND = "com.aptana.ide.syncing.COMPARE_IN_BACKGROUND"; //$NON-NLS-1$

	/**
	 * USE_CRC
	 */
	public static final String USE_CRC = "com.aptana.ide.syncing.USE_CRC"; //$NON-NLS-1$

	/**
	 * INITIAL_POOL_SIZE
	 */
	public static final String INITIAL_POOL_SIZE = "com.aptana.ide.syncing.INITIAL_POOL_SIZE"; //$NON-NLS-1$

	/**
	 * MAX_POOL_SIZE
	 */
	public static final String MAX_POOL_SIZE = "com.aptana.ide.syncing.MAX_POOL_SIZE"; //$NON-NLS-1$

	/**
	 * PREF_LAST_VERSION
	 */
	String WEB_PERSPECTIVE_LAST_VERSION = "com.aptana.ide.core.ui.WEB_PERSPECTIVE_LAST_VERSION"; //$NON-NLS-1$

	/**
	 * PREF_RESETTING_PERSPECTIVE
	 */
	String WEB_PERSPECTIVE_RESETTING_PERSPECTIVE = "com.aptana.ide.core.ui.WEB_PERSPECTIVE_RESETTING_PERSPECTIVE"; //$NON-NLS-1$

	/**
	 * PREF_RESET_PERSPECTIVE
	 */
	String WEB_PERSPECTIVE_RESET_PERSPECTIVE = "com.aptana.ide.core.ui.WEB_PERSPECTIVE_RESET_PERSPECTIVE"; //$NON-NLS-1$

	/**
	 * PREF_RESET_PERSPECTIVE
	 */
	String WEB_PROJECT_PERSPECTIVE_RESET_PERSPECTIVE = "com.aptana.ide.core.ui.WEB_PROJECT_PERSPECTIVE_RESET_PERSPECTIVE"; //$NON-NLS-1$

	/**
	 * PREF_RESETTING_PERSPECTIVE
	 */
	String WEB_PROJECT_PERSPECTIVE_RESETTING_PERSPECTIVE = "com.aptana.ide.core.ui.WEB_PROJECT_PERSPECTIVE_RESETTING_PERSPECTIVE"; //$NON-NLS-1$

	/**
	 * PREF_LAST_VERSION
	 */
	String WEB_PROJECT_PERSPECTIVE_LAST_VERSION = "com.aptana.ide.core.ui.WEB_PROJECT_PERSPECTIVE_LAST_VERSION"; //$NON-NLS-1$

	/**
	 * PREF_KEY_FIRST_STARTUP
	 */
	String PREF_KEY_FIRST_STARTUP = "com.aptana.ide.core.ui.PREF_KEY_FIRST_STARTUP"; //$NON-NLS-1$

	/**
	 * PREF_USER_NAME
	 */
	String PREF_USER_NAME = "com.aptana.ide.core.ui.PREF_USER_NAME"; //$NON-NLS-1$

	/**
	 * Activation email address
	 */
	String ACTIVATION_EMAIL_ADDRESS = "com.aptana.ide.core.ui.ACTIVATION_EMAIL_ADDRESS"; //$NON-NLS-1$

	/**
	 * Activation key
	 */
	String ACTIVATION_KEY = "com.aptana.ide.core.ui.ACTIVATION_KEY"; //$NON-NLS-1$

	/**
	 * Preference for what are the default set of web files
	 */
	String PREF_FILE_EXPLORER_WEB_FILES = "com.aptana.ide.core.ui.PREF_FILE_EXPLORER_WEB_FILES"; //$NON-NLS-1$

	/**
	 * Preference to allow expanding of compressed files in the file view
	 */
	String PREF_FILE_EXPLORER_SHOW_COMPRESSED = "com.aptana.ide.core.ui.PREF_FILE_EXPLORER_SHOW_COMPRESSED"; //$NON-NLS-1$

	/**
	 * Preference for container path;
	 */
	String PREF_AUTO_BACKUP_PATH = "com.aptana.ide.core.ui.AUTO_BACKUP_PATH"; //$NON-NLS-1$

	/**
	 * Preference controlling is auto backup enabled;
	 */
	String PREF_AUTO_BACKUP_ENABLED = "com.aptana.ide.core.ui.AUTO_BACKUP_ENABLED"; //$NON-NLS-1$

	/**
	 * Preference containing last backup filename;
	 */
	String PREF_AUTO_BACKUP_LASTNAME = "com.aptana.ide.core.ui.AUTO_BACKUP_LASTNAME"; //$NON-NLS-1$

	/**
	 * Preference containing last preference restore filename;
	 */
	String PREF_AUTO_BACKUP_LASTRESTORE_NAME = "com.aptana.ide.core.ui.AUTO_BACKUP_LASTRESTORE"; //$NON-NLS-1$

	/**
	 * Preference containing last preference restore time;
	 */
	String PREF_AUTO_BACKUP_LASTRESTORE_TIME = "com.aptana.ide.core.ui.AUTO_BACKUP_LASTRESTORETIME"; //$NON-NLS-1$

	/**
	 * Preference of current "selected" working directory. Used for untitled file save locations, etc.;
	 */
	String PREF_CURRENT_DIRECTORY = "com.aptana.ide.core.ui.PREF_CURRENT_DIRECTORY"; //$NON-NLS-1$
	
	/**
	 * Preference for the preferences switch action that should be taken when the user hit an Aptana action outside the 
	 * Aptana perspective, and we want to notify that a perspective switch is available.
	 */
	String SWITCH_TO_APTANA_PRESPECTIVE = WebPerspectiveFactory.PERSPECTIVE_ID + PerspectiveManager.SWITCH_KEY_SUFFIX;

}
