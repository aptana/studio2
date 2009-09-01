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
package com.aptana.ide.core.ui.syncing;

import org.eclipse.osgi.util.NLS;

/**
 * External messages class for internationalized strings
 * 
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.core.ui.syncing.messages";//$NON-NLS-1$

	private Messages()
	{
		// Do not instantiate
	}

	/**
	 * SyncingConsole_AptanaSyncingConsole
	 */
	public static String SyncingConsole_AptanaSyncingConsole;

	/**
	 * SyncingConsole_UnableToWriteToConsole
	 */
	public static String SyncingConsole_UnableToWriteToConsole;

	/**
	 * SyncingConsole_ErrorClosingStream
	 */
	public static String SyncingConsole_ErrorClosingStream;

	/**
	 * SyncGlobalCloakingPreferencePage_AddFileExtensionsToCloak
	 */
	public static String SyncGlobalCloakingPreferencePage_AddFileExtensionsToCloak;

	/**
	 * SyncGlobalCloakingPreferencePage_CompareInBackground
	 */
	public static String SyncGlobalCloakingPreferencePage_CompareInBackground;

	/**
	 * SyncGlobalCloakingPreferencePage_IgnoreWarningTitle
	 */
	public static String SyncGlobalCloakingPreferencePage_IgnoreWarningTitle;

	/**
	 * SyncGlobalCloakingPreferencePage_IgnoreFileFolder
	 */
	public static String SyncGlobalCloakingPreferencePage_IgnoreFileFolder;

	/**
	 * SyncGlobalCloakingPreferencePage_IgnoteWarningMessage
	 */
	public static String SyncGlobalCloakingPreferencePage_IgnoteWarningMessage;

	/**
	 * SyncGlobalCloakingPreferencePage_InitialPoolSize
	 */
	public static String SyncGlobalCloakingPreferencePage_InitialPoolSize;

	/**
	 * SyncGlobalCloakingPreferencePage_FileExtensionLabel
	 */
	public static String SyncGlobalCloakingPreferencePage_FileExtensionLabel;

	/**
	 * SyncGlobalCloakingPreferencePage_MaxPoolSize
	 */
	public static String SyncGlobalCloakingPreferencePage_MaxPoolSize;

	/**
	 * SyncGlobalCloakingPreferencePage_PoolSizeError
	 */
	public static String SyncGlobalCloakingPreferencePage_PoolSizeError;

	/**
	 * SyncGlobalCloakingPreferencePage_SmartSyncOptions
	 */
	public static String SyncGlobalCloakingPreferencePage_SmartSyncOptions;

	/**
	 * SyncGlobalCloakingPreferencePage_UseCRC
	 */
	public static String SyncGlobalCloakingPreferencePage_UseCRC;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
