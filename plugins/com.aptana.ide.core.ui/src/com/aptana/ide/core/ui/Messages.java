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
package com.aptana.ide.core.ui;

import org.eclipse.osgi.util.NLS;

/**
 * NLS class
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.core.ui.messages";//$NON-NLS-1$

	private Messages()
	{
		// Do not instantiate
	}

	/**
	 * CoreUIUtils_PluginLocationError
	 */
	public static String CoreUIUtils_PluginLocationError;

	/**
	 * CoreUIUtils_UnableToURLEncodeFilename
	 */
	public static String CoreUIUtils_UnableToURLEncodeFilename;

	/**
	 * CoreUIUtils_UnableToRetrieveActiveEditor
	 */
	public static String CoreUIUtils_UnableToRetrieveActiveEditor;

	/**
	 * CoreUIUtils_UnableToGetCurrentEditorPaths
	 */
	public static String CoreUIUtils_UnableToGetCurrentEditorPaths;

	/**
	 * CoreUIUtils_EncodingNotSupported
	 */
	public static String CoreUIUtils_EncodingNotSupported;

	public static String CoreUIUtils_ERR_InAsyncCallInGetOpenEditorPaths;

	public static String CoreUIUtils_ERR_OpeningEditorToVirtualFile;

	public static String CoreUIUtils_ERR_UnableToFindBundleName;

	public static String CoreUIUtils_ERR_UnableToResolveURL;

	public static String CoreUIUtils_ERR_UnableToSwitchOutBundleIdForURL;

	public static String CoreUIUtils_MSG_Error;

	public static String CoreUIUtils_MSG_Information;

	public static String CoreUIUtils_MSG_OpeningRemoteFile;

	public static String CoreUIUtils_MSG_RemotelySaving;

	public static String CoreUIUtils_MSG_ShowingError;

	public static String CoreUIUtils_MSG_ShowingMessage;

	/**
	 * CoreUIUtils_UnableToGetCurrentUserName
	 */
	public static String CoreUIUtils_UnableToGetCurrentUserName;

	/**
	 * WindowActionDelegate_AptanaPrompt
	 */
	public static String WindowActionDelegate_AptanaPrompt;

	/**
	 * WindowActionDelegate_ProblemDescription
	 */
	public static String WindowActionDelegate_ProblemDescription;

	/**
	 * WindowActionDelegate_LogSuccessTitle
	 */
	public static String WindowActionDelegate_LogSuccessTitle;

	/**
	 * WindowActionDelegate_LogSuccessText
	 */
	public static String WindowActionDelegate_LogSuccessText;

	/**
	 * WindowActionDelegate_LogFailedError
	 */
	public static String WindowActionDelegate_LogFailedError;

	/**
	 * WindowActionDelegate_AptanaOverviewErrorTitle
	 */
	public static String WindowActionDelegate_AptanaOverviewErrorTitle;

	/**
	 * WindowActionDelegate_AptanaOverviewErrorMessage
	 */
	public static String WindowActionDelegate_AptanaOverviewErrorMessage;

	/**
	 * WindowActionDelegate_SearchingForUpdates
	 */
	public static String WindowActionDelegate_SearchingForUpdates;

	/**
	 * WindowActionDelegate_CleanConfigurationTitle
	 */
	public static String WindowActionDelegate_CleanConfigurationTitle;

	/**
	 * WindowActionDelegate_CleanConfigurationDescription
	 */
	public static String WindowActionDelegate_CleanConfigurationDescription;

	/**
	 * ImageUtils_ErrorSelecingImageNamesFromCache
	 */
	public static String ImageUtils_ErrorSelecingImageNamesFromCache;

	/**
	 * ImageUtils_ErrorLoadingIconImageCache
	 */
	public static String ImageUtils_ErrorLoadingIconImageCache;

	/**
	 * ImageUtils_ErrorSavingIconImageCache
	 */
	public static String ImageUtils_ErrorSavingIconImageCache;

	/**
	 * ImageUtils_ConnectedToDatabaseAptanaDB
	 */
	public static String ImageUtils_ConnectedToDatabaseAptanaDB;

	/**
	 * ImageUtils_ErrorInitializingDbConnection
	 */
	public static String ImageUtils_ErrorInitializingDbConnection;

	/**
	 * ImageUtils_ErrorCreatingImageCacheTable
	 */
	public static String ImageUtils_ErrorCreatingImageCacheTable;

	/**
	 * ImageUtils_IncorrectTableDefinition
	 */
	public static String ImageUtils_IncorrectTableDefinition;

	/**
	 * ImageUtils_SQLException
	 */
	public static String ImageUtils_SQLException;

	/**
	 * ImageUtils_DatabaseDidNotShutDownNormally
	 */
	public static String ImageUtils_DatabaseDidNotShutDownNormally;

	/**
	 * ImageUtils_DatabaseShutDownNormally
	 */
	public static String ImageUtils_DatabaseShutDownNormally;

	public static String ImageUtils_NoConnection;

	/**
	 * WebPerspectiveFactory_UpdatePerspectiveConfirmation
	 */
	public static String WebPerspectiveFactory_UpdatePerspectiveConfirmation;

    public static String WebPerspectiveFactory_UpdatePerspectiveTitle;

	/**
	 * WebProjectPerspectiveFactory_PerspectiveChangedQuery
	 */
	public static String WebProjectPerspectiveFactory_PerspectiveChangedQuery;

	public static String InitialRestartStartup_ERR_UnableToCleanConfiguration;

	public static String InitialRestartStartup_ERR_VMArgumentsNullForRestartOfIDE;

	public static String InitialRestartStartup_INF_CleanPreference;

	public static String InitialRestartStartup_INF_NewCommandLine;

	public static String InitialRestartStartup_MSG_RestartingIDE;

	/**
	 * InitialStartup_UnableToSwitchNewFileWizardListing
	 */
	public static String InitialStartup_UnableToSwitchNewFileWizardListing;

	/**
	 * AbstractPerspectiveFactory_OverviewTitle
	 */
	public static String AbstractPerspectiveFactory_OverviewTitle;

	/**
	 * AbstractPerspectiveFactory_OverviewMessage
	 */
	public static String AbstractPerspectiveFactory_OverviewMessage;

	/**
	 * AbstractPerspectiveFactory_ErrorInitializingUiHelpPage
	 */
	public static String AbstractPerspectiveFactory_ErrorInitializingUiHelpPage;

	/**
	 * WorkbenchHelper_UnableToLaunchBrowser
	 */
	public static String WorkbenchHelper_UnableToLaunchBrowser;

	/**
	 * WorkbenchHelper_EditorPartCannotBeActivated
	 */
	public static String WorkbenchHelper_EditorPartCannotBeActivated;

	/**
	 * WorkbenchHelper_EditorPartCannotBeOpened
	 */
	public static String WorkbenchHelper_EditorPartCannotBeOpened;

	/**
	 * WorkbenchHelper_ErrorOpeningEditor
	 */
	public static String WorkbenchHelper_ErrorOpeningEditor;

	/**
	 * WorkbenchHelper_ErrorGettingEditorId
	 */
	public static String WorkbenchHelper_ErrorGettingEditorId;

	/**
	 * WorkbenchHelper_ErrorGettingInputStreamFromFile
	 */
	public static String WorkbenchHelper_ErrorGettingInputStreamFromFile;

	/**
	 * WorkbenchHelper_ErrorClosingInputStream
	 */
	public static String WorkbenchHelper_ErrorClosingInputStream;

	/**
	 * WorkbenchHelper_ElementListSelectionDialogTitle
	 */
	public static String WorkbenchHelper_ElementListSelectionDialogTitle;

	/**
	 * WorkbenchHelper_ElementListSelectionDialogMessage
	 */
	public static String WorkbenchHelper_ElementListSelectionDialogMessage;

	/**
	 * CoreUIUtils_UnableToCreateJavaFileEditorInput
	 */
	public static String CoreUIUtils_UnableToCreateJavaFileEditorInput;

	/**
	 * CoreUIUtils_UnableToCreateNonExistingFileEditorInput
	 */
	public static String CoreUIUtils_UnableToCreateNonExistingFileEditorInput;

	public static String DeleteConfigurationsDialog_MSG_CheckTheOnesToBeDeletedWithProject;

	public static String DeleteConfigurationsDialog_TTL_RunAndDebugConfigurationProjectAssociation;

	/**
	 * DiagnosticDialog_Close
	 */
	public static String DiagnosticDialog_Close;

	/**
	 * DiagnosticDialog_CopyToClipboard
	 */
	public static String DiagnosticDialog_CopyToClipboard;

	/**
	 * DiagnosticDialog_Title
	 */
	public static String DiagnosticDialog_Title;

	/**
	 * DialogUtils_HideThisMessageInFuture
	 */
	public static String DialogUtils_HideThisMessageInFuture;

	/**
	 * AbstractPerspectiveFactory_AptanaReleaseNotes
	 */
	public static String AbstractPerspectiveFactory_AptanaReleaseNotes;

	public static String AptanaAuthenticator_ERR_BadPadding;

	public static String AptanaAuthenticator_ERR_IllegalBlockSize;

	public static String AptanaAuthenticator_ERR_InvalidKey;

	public static String AptanaAuthenticator_ERR_NoSuchAlgorithm;

	public static String AptanaAuthenticator_ERR_NoSuchPadding;

	public static String AptanaAuthenticator_ERR_UnableToDecodeExistingKey;

	public static String AptanaAuthenticator_INF_LoggingPasswordForHost;

	public static String AutoOpenPerspectivesJob_MSG_AutomaticallyOpeningNewPerspectives;

	public static String BaseTimingStartup_INF_StartupTookMilliseconds;

	/**
	 * CoreUIUtils_UnableToParseEclipseVersion
	 */
	public static String CoreUIUtils_UnableToParseEclipseVersion;

	public static String CoreUIUtils_WRN_UnableToGetInstalledFeatureId;

	public static String CoreUIUtils_WRN_UnableToGetInstalledFeatures;

	/**
	 * WindowActionDelegate_UnableToOpenLogFile
	 */
	public static String WindowActionDelegate_UnableToOpenLogFile;

	/**
	 * FileExplorerView_ErrorOpeningEditor
	 */
	public static String FileExplorerView_ErrorOpeningEditor;

	/**
	 * FileExplorerView_ErrorSavingFile
	 */
	public static String FileExplorerView_ErrorSavingFile;

	/**
	 * FileExplorerView_UnableToSaveRemoteFile
	 */
	public static String FileExplorerView_UnableToSaveRemoteFile;

	/**
	 * FileExplorerView_ConnectionFailed
	 */
	public static String FileExplorerView_OpenPropertiesDialog;

	/**
	 * FileExplorerView_ConnectionFailed
	 */
	public static String FileExplorerView_ConnectionFailed;

	/**
	 * FileExplorerView_ConnectionTimedOut
	 */
	public static String FileExplorerView_ConnectionTimedOut;

	/**
	 * ClearLogConfirmTitle
	 */
	public static String ClearLogConfirmTitle;

	/**
	 * ClearLogConfirmDescription
	 */
	public static String ClearLogConfirmDescription;

	/**
	 * EclipseDiagnosticLog_EclipseVersion
	 */
	public static String EclipseDiagnosticLog_EclipseVersion;

	/**
	 * EclipseDiagnosticLog_HomeDir
	 */
	public static String EclipseDiagnosticLog_HomeDir;

	/**
	 * EclipseDiagnosticLog_HostOS
	 */
	public static String EclipseDiagnosticLog_HostOS;

	public static String EclipseDiagnosticLog_JREHome;

	/**
	 * EclipseDiagnosticLog_JavaVendor
	 */
	public static String EclipseDiagnosticLog_JREVendor;

	/**
	 * EclipseDiagnosticLog_JREVersion
	 */
	public static String EclipseDiagnosticLog_JREVersion;

	/**
	 * EclipseDiagnosticLog_Language
	 */
	public static String EclipseDiagnosticLog_Language;

	/**
	 * EclipseDiagnosticLog_LicenseKey
	 */
	public static String EclipseDiagnosticLog_LicenseKey;

	/**
	 * EclipseDiagnosticLog_LicenseUser
	 */
	public static String EclipseDiagnosticLog_LicenseUser;

	/**
	 * EclipseDiagnosticLog_OSArch
	 */
	public static String EclipseDiagnosticLog_OSArch;

	/**
	 * EclipseDiagnosticLog_StudioID
	 */
	public static String EclipseDiagnosticLog_StudioID;

	/**
	 * EclipseDiagnosticLog_VMArgs
	 */
	public static String EclipseDiagnosticLog_VMArgs;

	/**
	 * EclipseDiagnosticLog_WorkspaceDir
	 */
	public static String EclipseDiagnosticLog_WorkspaceDir;

	public static String LogReadonlyEditor_ERR_Exception;

    public static String LogReadonlyEditor_ERR_Update;

    public static String LogReadonlyEditor_Job_Refresh;

    public static String LogReadonlyEditor_Job_Updating;

    public static String LogReadonlyEditor_TTP_Refresh;

    /**
	 * NetworkDiagnosticLog_AddressReachable
	 */
	public static String NetworkDiagnosticLog_AddressReachable;

	/**
	 * NetworkDiagnosticLog_Host_IP
	 */
	public static String NetworkDiagnosticLog_Host_IP;

	/**
	 * NetworkDiagnosticLog_HostName
	 */
	public static String NetworkDiagnosticLog_HostName;

	/**
	 * NetworkDiagnosticLog_Unknown
	 */
	public static String NetworkDiagnosticLog_Unknown;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
