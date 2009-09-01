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
package com.aptana.ide.core.ui.io.file;

import org.eclipse.osgi.util.NLS;

/**
 * NLS
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.core.ui.io.file.messages";//$NON-NLS-1$

	private Messages()
	{
		// Do not instantiate
	}

	public static String FtpDialog_ERR_PleaseProvideAServerHost;
	public static String FtpDialog_ERR_PleaseProvideAUserName;
	public static String FtpDialog_ERR_PleaseSetAConnectionName;
	public static String FtpDialog_ERR_SiteWithSimilarNameExistsForThisTypeOfFTP;
	public static String FtpDialog_LBL_AdvancedOptions;
	public static String FtpDialog_LBL_Browse;
	public static String FtpDialog_LBL_ConnectionType;
	public static String FtpDialog_LBL_EditConnection;
	public static String FtpDialog_LBL_Example;
	public static String FtpDialog_LBL_NewConnection;
	public static String FtpDialog_LBL_Password;
	public static String FtpDialog_LBL_KeyPass;
	public static String FtpDialog_LBL_RemoteInfo;
	public static String FtpDialog_LBL_RemotePath;
	public static String FtpDialog_LBL_Save;
	public static String FtpDialog_LBL_Server;
	public static String FtpDialog_LBL_SiteName;
	public static String FtpDialog_LBL_Test;
	public static String FtpDialog_LBL_UsePublicKeyAuthentication;
	public static String FtpDialog_LBL_NoPrivateKeySelected;
	public static String FtpDialog_LBL_UserName;
	public static String FtpDialog_TTL_connection;
	public static String FtpDialog_TTL_ConnectionError;
	public static String FtpDialog_TTL_CreateANew;
	public static String FtpDialog_TTL_EditThe;
	public static String FtpDialog_TTL_PathSelection;
	public static String FtpDialog_WRN_CouldNotConnect;

	/**
	 * PermissionsGroup_All
	 */
	public static String PermissionsGroup_All;

	/**
	 * PermissionsGroup_Execute
	 */
	public static String PermissionsGroup_Execute;

	/**
	 * PermissionsGroup_Group
	 */
	public static String PermissionsGroup_Group;

	/**
	 * PermissionsGroup_Read
	 */
	public static String PermissionsGroup_Read;

	/**
	 * PermissionsGroup_Title
	 */
	public static String PermissionsGroup_Title;

	/**
	 * PermissionsGroup_User
	 */
	public static String PermissionsGroup_User;

	/**
	 * PermissionsGroup_Write
	 */
	public static String PermissionsGroup_Write;

	/**
	 * ProjectLocationDialog_ProjectSiteConfiguration
	 */
	public static String ProjectLocationDialog_ProjectSiteConfiguration;

	/**
	 * ProjectLocationDialog_ConnectionMessage
	 */
	public static String ProjectLocationDialog_ConnectionMessage;

	/**
	 * ProjectLocationDialog_ProjectPath
	 */
	public static String ProjectLocationDialog_ProjectPath;

	/**
	 * ProjectLocationDialog_SelectSynchronizeSource
	 */
	public static String ProjectLocationDialog_SelectSynchronizeSource;

	/**
	 * ProjectLocationDialog_CanOnlyAcceptProjectFileManagerItemsError
	 */
	public static String ProjectLocationDialog_CanOnlyAcceptProjectFileManagerItemsError;

	/**
	 * VirtualFileManagerLocationDialog_UnknownError
	 */
	public static String VirtualFileManagerLocationDialog_UnknownError;

	/**
	 * VirtualFileManagerLocationDialog_ConnectionErrorTitle
	 */
	public static String VirtualFileManagerLocationDialog_ConnectionErrorTitle;

	/**
	 * VirtualFileManagerLocationDialog_ConnectionErrorMessage
	 */
	public static String VirtualFileManagerLocationDialog_ConnectionErrorMessage;

	/**
	 * VirtualFileManagerLocationDialog_ConnectionSuccessful
	 */
	public static String VirtualFileManagerLocationDialog_ConnectionSuccessful;

	/**
	 * VirtualFileManagerLocationDialog_ConnectionToSucceeded
	 */
	public static String VirtualFileManagerLocationDialog_ConnectionToSucceeded;

	/**
	 * VirtualFileManagerLocationDialog_ProgressMonitorNullError
	 */
	public static String VirtualFileManagerLocationDialog_ProgressMonitorNullError;

	/**
	 * VirtualFileManagerLocationDialog_TryingToConnect
	 */
	public static String VirtualFileManagerLocationDialog_TryingToConnect;

	/**
	 * LocalFileManager_BasePathCannotBeNull
	 */
	public static String LocalFileManager_BasePathCannotBeNull;

	/**
	 * LocalFileManager_pathCannotBeEmptyOrNull
	 */
	public static String LocalFileManager_pathCannotBeEmptyOrNull;

	/**
	 * LocalFileManager_UnableToCreateFileStreamForFile
	 */
	public static String LocalFileManager_UnableToCreateFileStreamForFile;

	/**
	 * LocalFileManager_InputStreamCannotBeNull
	 */
	public static String LocalFileManager_InputStreamCannotBeNull;

	public static String LocalFileManager_Error_copying_local_file;

	/**
	 * LocalFileManager_ErrorSerializingStreamForFile
	 */
	public static String LocalFileManager_ErrorSerializingStreamForFile;

	/**
	 * LocalFileManager_UnableToRemoveExistingFile
	 */
	public static String LocalFileManager_UnableToRemoveExistingFile;

	/**
	 * InfoDialog_Info
	 */
	public static String InfoDialog_Info;

	/**
	 * InfoDialog_General
	 */
	public static String InfoDialog_General;

	/**
	 * InfoDialog_Kind
	 */
	public static String InfoDialog_Kind;

	/**
	 * InfoDialog_Size
	 */
	public static String InfoDialog_Size;

	/**
	 * InfoDialog_Where
	 */
	public static String InfoDialog_Where;

	/**
	 * InfoDialog_Modified
	 */
	public static String InfoDialog_Modified;

	/**
	 * InfoDialog_Folder
	 */
	public static String InfoDialog_Folder;

	/**
	 * InfoDialog_File
	 */
	public static String InfoDialog_File;

	/**
	 * InfoDialog_Bytes
	 */
	public static String InfoDialog_Bytes;
	
	public static String SftpVirtualFileManager_ERR_PrivateKeyFailedWithNoPassword;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}