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
package com.aptana.ide.core.ui.views.fileexplorer;

import org.eclipse.osgi.util.NLS;

/**
 * NLS
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.core.ui.views.fileexplorer.messages";//$NON-NLS-1$

	private Messages()
	{
		// Do not instantiate
	}

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * DrillDownAdapter_BrowseFromHere
	 */
	public static String DrillDownAdapter_BrowseFromHere;

	/**
	 * DrillDownAdapter_BrowseFromPrevious
	 */
	public static String DrillDownAdapter_BrowseFromPrevious;

	/**
	 * DrillDownAdapter_BrowseFromRoot
	 */
	public static String DrillDownAdapter_BrowseFromRoot;

	public static String FileExplorerView_DestinationDoesNotExist;

	public static String FileTreeContentProvider_Collapsing_item_job_title;

	public static String FileTreeContentProvider_Fetching_job_title;

	/**
	 * FileTreeContentProvider_GettingFileList
	 */
	public static String FileTreeContentProvider_GettingFileList;

	/**
	 * FileTreeContentProvider_getChildren
	 */
	public static String FileTreeContentProvider_getChildren;

	public static String FileTreeContentProvider_Refreshing_tree_job_title;

	/**
	 * FileTreeContentProvider_UnableToRetrieveFilesFolderDoesntExist
	 */
	public static String FileTreeContentProvider_UnableToRetrieveFilesFolderDoesntExist;

	/**
	 * FileExplorerPreferencePage_AddExtensions
	 */
	public static String FileExplorerPreferencePage_AddExtensions;

	public static String FileExplorerPreferencePage_Display_compressed_files_as_expandable;

	/**
	 * FileExplorerView_ErrorCreatingFileView
	 */
	public static String FileExplorerView_ErrorCreatingFileView;

	/**
	 * FileExplorerView_UnableToDropSourceOntoDestination
	 */
	public static String FileExplorerView_UnableToDropSourceOntoDestination;

	/**
	 * FileExplorerView_AreYouSureYouWishToCopyToDirectory
	 */
	public static String FileExplorerView_AreYouSureYouWishToCopyToDirectory;

	/**
	 * FileExplorerView_UnableToDropOnto
	 */
	public static String FileExplorerView_UnableToDropOnto;

	/**
	 * FileExplorerView_UnableToConnectTo
	 */
	public static String FileExplorerView_UnableToConnectTo;

	/**
	 * FileExplorerView_ErrorCopyingFileToDestination
	 */
	public static String FileExplorerView_ErrorCopyingFileToDestination;

	/**
	 * FileExplorerView_ErrorCreatingDuplicateFileView
	 */
	public static String FileExplorerView_ErrorCreatingDuplicateFileView;

	/**
	 * FileExplorerView_CreateANewFileExplorer
	 */
	public static String FileExplorerView_CreateANewFileExplorer;

	public static String FileExplorerView_CreateProject;

	/**
	 * FileExplorerView_ShowWebFilesOnly
	 */
	public static String FileExplorerView_ShowWebFilesOnly;

	/**
	 * FileExplorerView_ShowWebFilesOnlyTT
	 */
	public static String FileExplorerView_ShowWebFilesOnlyTT;

	/**
	 * FileExplorerView_PropertiesForConnection
	 */
	public static String FileExplorerView_PropertiesForConnection;

	/**
	 * FileExplorerView_EnterFolderName
	 */
	public static String FileExplorerView_EnterFolderName;

	/**
	 * FileExplorerView_CouldNotCreateFolder
	 */
	public static String FileExplorerView_CouldNotCreateFolder;

	/**
	 * FileExplorerView_AreYouSureYouWantToDeleteThis
	 */
	public static String FileExplorerView_AreYouSureYouWantToDeleteThis;

	/**
	 * FileExplorerView_AreYouSureYouWantToDeleteThisFileManager
	 */
	public static String FileExplorerView_AreYouSureYouWantToDeleteThisFileManager;

	/**
	 * FileExplorerView_UnableToDeleteItemsOfThisType
	 */
	public static String FileExplorerView_UnableToDeleteItemsOfThisType;

	/**
	 * FileExplorerView_CannotRenameThisItem
	 */
	public static String FileExplorerView_CannotRenameThisItem;

	/**
	 * FileExplorerView_UnableToRenameFile
	 */
	public static String FileExplorerView_UnableToRenameFile;

	/**
	 * FileExplorerView_ErrorInGetEditorDescriptor
	 */
	public static String FileExplorerView_ErrorInGetEditorDescriptor;

	/**
	 * FileExplorerView_ErrorInitializingFileView
	 */
	public static String FileExplorerView_ErrorInitializingFileView;

	/**
	 * FileExplorerView_UnableToSaveFileViewState
	 */
	public static String FileExplorerView_UnableToSaveFileViewState;

	/**
	 * FileTreeContentProvider_UnableToRetrieveFilesRemoteDirError
	 */
	public static String FileTreeContentProvider_UnableToRetrieveFilesRemoteDirError;

	/**
	 * FileTreeContentProvider_UnableToRetrieveFilesError
	 */
	public static String FileTreeContentProvider_UnableToRetrieveFilesError;

	/**
	 * FileReplaceDialog_LBL_No
	 */
	public static String FileReplaceDialog_LBL_No;

	/**
	 * FileReplaceDialog_LBL_Yes
	 */
    public static String FileReplaceDialog_LBL_Yes;

    /**
     * FileReplaceDialog_LBL_YesToAll
     */
    public static String FileReplaceDialog_LBL_YesToAll;

    /**
     * FileReplaceDialog_ReplaceItem
     */
    public static String FileReplaceDialog_ReplaceItem;

    /**
     * FileReplaceDialog_OverwriteExistingItem
     */
    public static String FileReplaceDialog_OverwriteExistingItem;

	/**
	 * FileExplorerView_UnableToDrop
	 */
	public static String FileExplorerView_UnableToDrop;

	/**
	 * FileExplorerView_CopyingFiles
	 */
	public static String FileExplorerView_CopyingFiles;

	/**
	 * FileExplorerView_AddANewItem2
	 */
	public static String FileExplorerView_AddANewItem2;

	/**
	 * FileExplorerView_DeleteFailed
	 */
	public static String FileExplorerView_DeleteFailed;

	/**
	 * FileExplorerView_FileExplorer
	 */
	public static String FileExplorerView_FileExplorer;

	/**
	 * FileExplorerView_CopyStopped
	 */
	public static String FileExplorerView_CopyStopped;

	/**
	 * FileExplorerView_CollapseAll
	 */
	public static String FileExplorerView_CollapseAll;

	/**
	 * FileExplorerView_SortFilesTT
	 */
	public static String FileExplorerView_SortFilesTT;

	/**
	 * FileExplorerView_NewFile
	 */
	public static String FileExplorerView_NewFile;

	/**
	 * FileExplorerView_NewFolderTT
	 */
	public static String FileExplorerView_NewFolderTT;

	/**
	 * FileExplorerView_UnknownElement
	 */
	public static String FileExplorerView_UnknownElement;

	/**
	 * FileExplorerView_NothingToCopy
	 */
	public static String FileExplorerView_NothingToCopy;

	/**
	 * FileExplorerView_CollapseAllTT
	 */
	public static String FileExplorerView_CollapseAllTT;

	/**
	 * FileExplorerView_PropertiesTT2
	 */
	public static String FileExplorerView_PropertiesTT2;

	/**
	 * FileExplorerView_PropertiesTT3
	 */
	public static String FileExplorerView_PropertiesTT3;

	/**
	 * FileExplorerView_NewFolderName
	 */
	public static String FileExplorerView_NewFolderName;

	/**
	 * FileExplorerView_ConfirmDelete
	 */
	public static String FileExplorerView_ConfirmDelete;

	/**
	 * FileExplorerView_DeletingFiles
	 */
	public static String FileExplorerView_DeletingFiles;

	/**
	 * FileExplorerView_UnableToDelete
	 */
	public static String FileExplorerView_UnableToDelete;

	/**
	 * FileExplorerView_FileExplorer2
	 */
	public static String FileExplorerView_FileExplorer2;

	/**
	 * FileExplorerView_FileNotExistError
	 */
	public static String FileExplorerView_FileNotExistError;

	/**
	 * FileExplorerView_AddNew
	 */
	public static String FileExplorerView_AddNew;

	/**
	 * FileExplorerView_Open
	 */
	public static String FileExplorerView_Open;

	public static String FileExplorerView_OpenRemoteError;

	/**
	 * FileExplorerView_NewTT
	 */
	public static String FileExplorerView_NewTT;

	/**
	 * FileExplorerView_Delete
	 */
	public static String FileExplorerView_Delete;

	/**
	 * FileExplorerView_ReadOnlyMessage
	 */
	public static String FileExplorerView_ReadOnlyMessage;

	/**
	 * FileExplorerView_ReadOnlyText
	 */
	public static String FileExplorerView_ReadOnlyText;

	/**
	 * FileExplorerView_Rename
	 */
	public static String FileExplorerView_Rename;

	/**
	 * FileExplorerView_CopyFailed
	 */
	public static String FileExplorerView_CopyFailed;

	/**
	 * FileExplorerView_AptanaIDE
	 */
	public static String FileExplorerView_AptanaIDE;

	/**
	 * FileExplorerView_OkToMoveFilesToDirectory
	 */
	public static String FileExplorerView_OkToMoveFilesToDirectory;

	/**
	 * FileExplorerView_Copying
	 */
	public static String FileExplorerView_Copying;

	/**
	 * FileExplorerView_Deleting
	 */
	public static String FileExplorerView_Deleting;

	/**
	 * FileExplorerView_SortFiles
	 */
	public static String FileExplorerView_SortFiles;

	/**
	 * FileExplorerView_Refresh
	 */
	public static String FileExplorerView_Refresh;

	/**
	 * FileExplorerView_RefreshTT
	 */
	public static String FileExplorerView_RefreshTT;

	/**
	 * FileExplorerView_Properties
	 */
	public static String FileExplorerView_Properties;

	/**
	 * FileExplorerView_NewFolder
	 */
	public static String FileExplorerView_NewFolder;

	public static String FileExplorerView_NewName;

	/**
	 * FileExplorerView_DeleteTT
	 */
	public static String FileExplorerView_DeleteTT;

	/**
	 * FileExplorerView_RenameTitle
	 */
	public static String FileExplorerView_RenameTitle;

	/**
	 * FileExplorerView_RenameTT
	 */
	public static String FileExplorerView_RenameTT;

	/**
	 * FileExplorerView_ShowHideLocalConnections
	 */
	public static String FileExplorerView_ShowHideLocalConnections;

	/**
	 * NewVirualFileDialog_CreateRemoteFile
	 */
	public static String NewVirualFileDialog_CreateRemoteFile;
	/**
	 * NewVirualFileDialog_CreateRemoteFolder
	 */
	public static String NewVirualFileDialog_CreateRemoteFolder;
	/**
	 * NewVirualFileDialog_Creating
	 */
	public static String NewVirualFileDialog_Creating;
	/**
	 * NewVirualFileDialog_CreatingRemoteFileJob
	 */
	public static String NewVirualFileDialog_CreatingRemoteFileJob;
	/**
	 * NewVirualFileDialog_EnterFileName
	 */
	public static String NewVirualFileDialog_EnterFileName;
	/**
	 * NewVirualFileDialog_EnterFolderName
	 */
	public static String NewVirualFileDialog_EnterFolderName;
	/**
	 * NewVirualFileDialog_ErrorCreatingRemoteFile
	 */
	public static String NewVirualFileDialog_ErrorCreatingRemoteFile;
	/**
	 * NewVirualFileDialog_ErrorCreatingRemoteFolder
	 */
	public static String NewVirualFileDialog_ErrorCreatingRemoteFolder;
	/**
	 * NewVirualFileDialog_FileAlreadyExists
	 */
	public static String NewVirualFileDialog_FileAlreadyExists;
	/**
	 * NewVirualFileDialog_FolderAlreadyExists
	 */
	public static String NewVirualFileDialog_FolderAlreadyExists;
	/**
	 * NewVirualFileDialog_UpdatingStatus
	 */
	public static String NewVirualFileDialog_UpdatingStatus;

	/**
	 * FileExplorerView_CopyingBetweenFileViewsIsNotCurrentlySupported
	 */
	public static String FileExplorerView_CopyingBetweenFileViewsIsNotCurrentlySupported;

	/**
	 * FileExplorerView_CannotCreateFolderInThisSystemDirectory
	 */
	public static String FileExplorerView_CannotCreateFolderInThisSystemDirectory;

	/**
	 * FileExplorerView_CannotCreateAFolderInThisTypeOfItem
	 */
	public static String FileExplorerView_CannotCreateAFolderInThisTypeOfItem;

	/**
	 * FileExplorerView_UnableToReserializeLocalFileFromStream
	 */
	public static String FileExplorerView_UnableToReserializeLocalFileFromStream;

	/**
	 * FileExplorerView_Desktop
	 */
	public static String FileExplorerView_Desktop;

	public static String FileExplorerView_Disconnecting_file_managers_job_title;

	/**
	 * FileExplorerView_UnableToDeleteFile
	 */
	public static String FileExplorerView_UnableToDeleteFile;

	/**
	 * FileExplorerView_AddNewFileManager
	 */
	public static String FileExplorerView_AddNewFileManager;

	/**
	 * FileExplorerView_SourceCannotBeNull
	 */
	public static String FileExplorerView_SourceCannotBeNull;

	/**
	 * FileExplorerView_SourceNoFileManagerAttached
	 */
	public static String FileExplorerView_SourceNoFileManagerAttached;

	/**
	 * FileExplorerView_UnableToLoadIconCache
	 */
	public static String FileExplorerView_UnableToLoadIconCache;

	public static String FileExplorerView_Updating_view_job_title;

	/**
	 * FileExplorerView_EncounteredNullWhenRetrievingFiles
	 */
	public static String FileExplorerView_EncounteredNullWhenRetrievingFiles;

	/**
	 * FileExplorerView_LinkNotExistError
	 */
	public static String FileExplorerView_LinkNotExistError;

	/**
	 * FileExplorerView_Loading
	 */
	public static String FileExplorerView_Loading;

	/**
	 * FileExplorerView_LocalizedError
	 */
	public static String FileExplorerView_LocalizedError;

	public static String FileLabelProvider_Loading_msg;

	public static String FtpBrowserContentProvider_JOB_Fetching;

	public static String FtpBrowserContentProvider_TTL_ConnectionFailed;

	public static String FtpBrowserContentProvider_UIJob_CollapsingItem;

	public static String FtpBrowserContentProvider_UIJOB_RefreshingTree;

	public static String FtpBrowserContentProvider_WRN_UnableToConnect;
}
