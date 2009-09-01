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
package com.aptana.ide.core;

import org.eclipse.osgi.util.NLS;

/**
 * Messages class for internationalization
 * @author Ingo Muschenetz
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.core.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	/**
	 * DateUtils_seconds
	 */
	public static String DateUtils_seconds;
	
	/**
	 * DateUtils_minutes
	 */
	public static String DateUtils_minutes;
	
	/**
	 * DateUtils_hours
	 */
	public static String DateUtils_hours;

	/**
	 * AptanaCorePlugin_Serialization_CreateFailed
	 */
	public static String AptanaCorePlugin_Serialization_CreateFailed;
	
	/**
	 * AptanaCorePlugin_Serialization_CloseFailed
	 */
	public static String AptanaCorePlugin_Serialization_CloseFailed;
	
	/**
	 * FileTricks_AWT_UnableToInstantiate
	 */
	public static String FileTricks_AWT_UnableToInstantiate;
	
	/**
	 * FileTricks_AWT_UnableToReplaceFolder
	 */
	public static String FileTricks_AWT_UnableToReplaceFolder;
	
	public static String IdeLog_ERROR;

	public static String IdeLog_File_Written_To;

	public static String IdeLog_IMPORTANT;

	public static String IdeLog_INFO;

	/**
	 * IdeLog_LogMessage
	 */
	public static String IdeLog_LogMessage;

	public static String IdeLog_Unable_To_Write_Temporary_File;

	public static String IdeLog_UNKNOWN;
	
	/**
	 * InitialStartup_ErrorReadingData
	 */
	public static String InitialStartup_ErrorReadingData;
	
	/**
	 * InitialStartup_CannotFindFile
	 */
	public static String InitialStartup_CannotFindFile;
	
	/**
	 * InitialStartup_IOError
	 */
	public static String InitialStartup_IOError;
	
	/**
	 * InitialStartup_ErrorClosing
	 */
	public static String InitialStartup_ErrorClosing;
	
	public static String SetExecutableBits_File_Not_In_Bundle;

	public static String SetExecutableBits_Set_Binary_As_Executable;

	/**
	 * SubmitTracReport_Done
	 */
	public static String SubmitTracReport_Done;
	
	/**
	 * SubmitTracReport_Error
	 */
	public static String SubmitTracReport_Error;
	
	/**
	 * SubmitTracReport_HasListenerError
	 */
	public static String SubmitTracReport_HasListenerError;
	
	/**
	 * RuntimeUtils_AncestorOutOfRangeError
	 */
	public static String RuntimeUtils_AncestorOutOfRangeError;
	
	/**
	 * CoreStrings_OK
	 */
	public static String CoreStrings_OK;
	
	/**
	 * CoreStrings_CONTINUE
	 */
	public static String CoreStrings_CONTINUE;
	
	/**
	 * CoreStrings_CANCEL
	 */
	public static String CoreStrings_CANCEL;
	
	/**
	 * CoreStrings_BROWSE
	 */
	public static String CoreStrings_BROWSE;
	
	/**
	 * CoreStrings_YES
	 */
	public static String CoreStrings_YES;
	
	/**
	 * CoreStrings_NO
	 */
	public static String CoreStrings_NO;
	
	/**
	 * CoreStrings_OPEN
	 */
	public static String CoreStrings_OPEN;
	
	/**
	 * CoreStrings_NEW
	 */
	public static String CoreStrings_NEW;
	
	/**
	 * CoreStrings_RENAME
	 */
	public static String CoreStrings_RENAME;
	
	/**
	 * CoreStrings_DELETE
	 */
	public static String CoreStrings_DELETE;
	
	/**
	 * CoreStrings_ELLIPSIS
	 */
	public static String CoreStrings_ELLIPSIS;
	
	/**
	 * CoreStrings_YES_TO_ALL
	 */
	public static String CoreStrings_YES_TO_ALL;
	
	/**
	 * CoreStrings_ADD
	 */
	public static String CoreStrings_ADD;
	
	/**
	 * CoreStrings_REMOVE
	 */
	public static String CoreStrings_REMOVE;
	
	/**
	 * CoreStrings_REFRESH
	 */
	public static String CoreStrings_REFRESH;
	
	/**
	 * CoreStrings_PROPERTIES
	 */
	public static String CoreStrings_PROPERTIES;

	/**
	 * CoreStrings_ERROR
	 */
	public static String CoreStrings_ERROR;
	
	/**
	 * CoreStrings_EDIT
	 */
	public static String CoreStrings_EDIT;
	
	/**
	 * CoreStrings_INSTALL
	 */
	public static String CoreStrings_INSTALL;

	/**
	 * CoreStrings_HELP
	 */
	public static String CoreStrings_HELP;

	/**
	 * BundleClassLoader_BundleMustNotBeNull
	 */
	public static String BundleClassLoader_BundleMustNotBeNull;

	/**
	 * BundleClassLoader_UnableToFindClass
	 */
	public static String BundleClassLoader_UnableToFindClass;

	/**
	 * BundleClassLoader_UnableToFindResources
	 */
	public static String BundleClassLoader_UnableToFindResources;

	/**
	 * BundleClassLoader_UnableToLoadClass
	 */
	public static String BundleClassLoader_UnableToLoadClass;

	/**
	 * FileUtils_CoreLibraryNotFound
	 */
	public static String FileUtils_CoreLibraryNotFound;

	public static String FileUtils_Invalid_URI_Syntax;

	public static String FileUtils_Malformed_URI;

	public static String FileUtils_Unable_To_Open_URL;

	/**
	 * PlatformUtils_CoreLibraryNotFound
	 */
	public static String PlatformUtils_CoreLibraryNotFound;

	/**
	 * VersionCheck_BAD_JRE_DESCRIPTION
	 */
	public static String VersionCheck_BAD_JRE_DESCRIPTION;

	/**
	 * VersionCheck_BAD_JRE_TITLE
	 */
	public static String VersionCheck_BAD_JRE_TITLE;
	
	/**
	 * Base64_UnableToWriteOutputStream
	 */
	public static String Base64_UnableToWriteOutputStream;

	/**
	 * Base64_BadBase64InputCharacterAt
	 */
	public static String Base64_BadBase64InputCharacterAt;

	/**
	 * Base64_Decimal
	 */
	public static String Base64_Decimal;

	/**
	 * Base64_UnableToReadFromStream
	 */
	public static String Base64_UnableToReadFromStream;

	/**
	 * Base64_CanNotFindClass
	 */
	public static String Base64_CanNotFindClass;

	/**
	 * Base64_FileIsTooBigForThisConvenienceMethod
	 */
	public static String Base64_FileIsTooBigForThisConvenienceMethod;

	/**
	 * Base64_ErrorDecodingFromFile
	 */
	public static String Base64_ErrorDecodingFromFile;

	/**
	 * Base64_ErrorEncodingFromFile
	 */
	public static String Base64_ErrorEncodingFromFile;

	/**
	 * Base64_ImproperlyPaddedBase64Input
	 */
	public static String Base64_ImproperlyPaddedBase64Input;

	/**
	 * Base64_ErrorInBase64CodeReadingStream
	 */
	public static String Base64_ErrorInBase64CodeReadingStream;

	/**
	 * Base64_InvalidCharacterInBase64Data
	 */
	public static String Base64_InvalidCharacterInBase64Data;

	/**
	 * Base64_Base64InputNotProperlyPadded
	 */
	public static String Base64_Base64InputNotProperlyPadded;

	public static String URLEncoder_Cannot_Encode_URL;

    public static String WorkspaceSaveParticipant_ERR_FailedToSave;
}
