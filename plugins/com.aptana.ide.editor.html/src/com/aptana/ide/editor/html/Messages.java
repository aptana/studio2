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
package com.aptana.ide.editor.html;

import org.eclipse.osgi.util.NLS;

/**
 * @author Robin
 */
public final class Messages extends NLS
{
	/*
	 * Fields
	 */
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.html.messages"; //$NON-NLS-1$

	public static String BrowserExtensionLoader_Default;

    public static String BrowserExtensionLoader_Firefox;

    public static String BrowserExtensionLoader_IE;

    public static String BrowserExtensionLoader_INF_Preview;

    public static String BrowserExtensionLoader_IPhone;

    public static String BrowserExtensionLoader_Safari;

    /**
	 * HTMLErrorManager_HTMLTidyParseError
	 */
	public static String HTMLErrorManager_HTMLTidyParseError;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * Messages
	 */
	private Messages()
	{
	}

	/**
	 * HTMLEditor_UnableToUpdatePreview
	 */
	public static String HTMLEditor_UnableToUpdatePreview;

	/**
	 * HTMLEnvironmentLoader_ErrorReloadingFile
	 */
	public static String HTMLEnvironmentLoader_ErrorReloadingFile;

	/**
	 * HTMLEnvironmentLoader_ErrorLoadingFile
	 */
	public static String HTMLEnvironmentLoader_ErrorLoadingFile;

	/**
	 * HTMLErrorManager_ErrorParsingForErrors
	 */
	public static String HTMLErrorManager_ErrorParsingForErrors;

	/**
	 * HTMLFileLanguageService_IFileContextShouldNotBeNull
	 */
	public static String HTMLFileLanguageService_IFileContextShouldNotBeNull;

	/**
	 * HTMLFileLanguageService_NoInformationAvailable
	 */
	public static String HTMLFileLanguageService_NoInformationAvailable;

	/**
	 * HTMLFileLanguageService_NoInformationAvailableHTML
	 */
	public static String HTMLFileLanguageService_NoInformationAvailableHTML;

	/**
	 * HTMLFileLanguageService_InformationAvailableEndTagHTML
	 */
	public static String HTMLFileLanguageService_InformationAvailableEndTagHTML;

	/**
	 * HTMLFileLanguageService_InformationAvailableHTML
	 */
	public static String HTMLFileLanguageService_InformationAvailableHTML;

	/**
	 * HTMLFileLanguageService_StringLiteralHTML
	 */
	public static String HTMLFileLanguageService_StringLiteralHTML;

	/**
	 * HTMLFileLanguageService_NoHTMLLanguageServiceAvailable
	 */
	public static String HTMLFileLanguageService_NoHTMLLanguageServiceAvailable;

	/**
	 * HTMLLanguageEnvironment_EnvironmentInitializationAborted
	 */
	public static String HTMLLanguageEnvironment_EnvironmentInitializationAborted;

	/**
	 * HTMLLanguageEnvironment_HTMLLanguageThreadName
	 */
	public static String HTMLLanguageEnvironment_HTMLLanguageThreadName;

	/**
	 * HTMLSourceEditor_cuInfo_Null_At_DoSetInput
	 */
	public static String HTMLSourceEditor_cuInfo_Null_At_DoSetInput;

	/**
	 * HTMLSourceEditor_DescriptionAll
	 */
	public static String HTMLSourceEditor_DescriptionAll;

	/**
	 * HTMLSourceEditor_DescriptionHTM
	 */
	public static String HTMLSourceEditor_DescriptionHTM;

	/**
	 * HTMLSourceEditor_DescriptionHTML
	 */
	public static String HTMLSourceEditor_DescriptionHTML;

	/**
	 * HTMLSourceEditor_Document_Provider_Null
	 */
	public static String HTMLSourceEditor_Document_Provider_Null;

	/**
	 * HTMLSourceEditor_ExtensionAll
	 */
	public static String HTMLSourceEditor_ExtensionAll;

	/**
	 * HTMLSourceEditor_ExtensionAll
	 */
	public static String HTMLSourceEditor_ExtensionHTM;

	/**
	 * HTMLSourceEditor_ExtensionAll
	 */
	public static String HTMLSourceEditor_ExtensionHTML;

	/**
	 * HTMLSourceEditor_Provider_Null
	 */
	public static String HTMLSourceEditor_Provider_Null;

	/**
	 * HTMLSourceEditor_RunHTMLTidy
	 */
	public static String HTMLSourceEditor_RunHTMLTidy;

	/**
	 * MultiPageHTMLEditor_AddNewPreview
	 */
	public static String MultiPageHTMLEditor_AddNewPreview;

	/**
	 * MultiPageHTMLEditor_AutoSaveMessage
	 */
	public static String MultiPageHTMLEditor_AutoSaveMessage;

	/**
	 * MultiPageHTMLEditor_AutoSaveTitlte
	 */
	public static String MultiPageHTMLEditor_AutoSaveTitlte;

	/**
	 * MultiPageHTMLEditor_ConfigureHTMLPreview
	 */
	public static String MultiPageHTMLEditor_ConfigureHTMLPreview;

	/**
	 * MultiPageHTMLEditor_CopyPreviewURL
	 */
	public static String MultiPageHTMLEditor_CopyPreviewURL;

	/**
	 * MultiPageHTMLEditor_EditActivePreview
	 */
	public static String MultiPageHTMLEditor_EditActivePreview;

	/**
	 * MultiPageHTMLEditor_EmbeddedFirefoxLoadingIssue
	 */
	public static String MultiPageHTMLEditor_EmbeddedFirefoxLoadingIssue;
	/**
	 * MultiPageHTMLEditor_FirefoxBrowserCantBeFirst
	 */
	public static String MultiPageHTMLEditor_FirefoxBrowserCantBeFirst;
	/**
	 * MultiPageHTMLEditor_FirefoxBrowserCantBeFirstSave
	 */
	public static String MultiPageHTMLEditor_FirefoxBrowserCantBeFirstSave;

	/**
	 * MultiPageHTMLEditor_OpenInExternalBrowser
	 */
	public static String MultiPageHTMLEditor_OpenInExternalBrowser;

	/**
	 * MultiPageHTMLEditor_Preview
	 */
	public static String MultiPageHTMLEditor_Preview;

	/**
	 * MultiPageHTMLEditor_ProjectPreviewSettings
	 */
	public static String MultiPageHTMLEditor_ProjectPreviewSettings;

	/**
	 * MultiPageHTMLEditor_RefreshActivePreview
	 */
	public static String MultiPageHTMLEditor_RefreshActivePreview;

	/**
	 * MultiPageHTMLEditor_RemoveActivePreview
	 */
	public static String MultiPageHTMLEditor_RemoveActivePreview;

	/**
	 * MultiPageHTMLEditor_RestoreTabDefaults
	 */
	public static String MultiPageHTMLEditor_RestoreTabDefaults;

	/**
	 * MultiPageHTMLEditor_UnableToCreateBrowserControl
	 */
	public static String MultiPageHTMLEditor_UnableToCreateBrowserControl;

	/**
	 * MultiPageHTMLEditor_WorkspacePreviewSettings
	 */
	public static String MultiPageHTMLEditor_WorkspacePreviewSettings;

	/**
	 * MultiPageHTMLEditor_WrapperCannotBeNull
	 */
	public static String MultiPageHTMLEditor_WrapperCannotBeNull;

	/**
	 * MultiPageHTMLEditor_EditorCannotBeNull
	 */
	public static String MultiPageHTMLEditor_EditorCannotBeNull;

	/**
	 * MultiPageHTMLEditor_HTMLStartPage
	 */
	public static String MultiPageHTMLEditor_HTMLStartPage;

	/**
	 * MultiPageHTMLEditor_HTMLEditorStartPage
	 */
	public static String MultiPageHTMLEditor_HTMLEditorStartPage;

	/**
	 * MultiPageHTMLEditor_EditorIsNull
	 */
	public static String MultiPageHTMLEditor_EditorIsNull;

	public static String MultiPageHTMLEditor_ERR_CreateFileTab;

    public static String MultiPageHTMLEditor_ERR_CreateTab;

    public static String MultiPageHTMLEditor_INF_LoadedFile;

    public static String MultiPageHTMLEditor_INF_LoadedPreview;

    public static String MultiPageHTMLEditor_INF_LoadedStaticContent;

    public static String MultiPageHTMLEditor_INF_LoadingFile;

    public static String MultiPageHTMLEditor_INF_LoadingFromExt;

    public static String MultiPageHTMLEditor_INF_LoadingPreview;

    public static String MultiPageHTMLEditor_INF_LoadingStaticContent;

    public static String MultiPageHTMLEditor_INF_MovingTab;

    public static String MultiPageHTMLEditor_INF_SavingTab;

    public static String MultiPageHTMLEditor_INF_SkippingSave;

    public static String MultiPageHTMLEditor_INF_SkippingTab;

    public static String MultiPageHTMLEditor_Job_LoadingPreview;

    public static String MultiPageHTMLEditor_LBL_ViewSource;

    public static String HTMLFileServiceFactory_ERR_CreateParser;

    public static String HTMLFileServiceFactory_ERR_NullParser;

    /**
	 * HTMLFileServiceFactory_HTMLFileServiceFactoryInstallationFailed
	 */
	public static String HTMLFileServiceFactory_HTMLFileServiceFactoryInstallationFailed;

	/**
	 * SplitPageHTMLEditor_Preview
	 */
	public static String SplitPageHTMLEditor_Preview;

	/**
	 * SplitPageHTMLEditor_UnableToCreateBrowserControl
	 */
	public static String SplitPageHTMLEditor_UnableToCreateBrowserControl;

	/**
	 * SplitPageHTMLEditor_WrapperCannotBeNull
	 */
	public static String SplitPageHTMLEditor_WrapperCannotBeNull;

	/**
	 * SplitPageHTMLEditor_EditorCannotBeNull
	 */
	public static String SplitPageHTMLEditor_EditorCannotBeNull;

	/**
	 * SplitPageHTMLEditor_NotAFileEditorInput
	 */
	public static String SplitPageHTMLEditor_NotAFileEditorInput;

	/**
	 * SplitPageHTMLEditor_EditorIsNull
	 */
	public static String SplitPageHTMLEditor_EditorIsNull;

	/**
	 * HTMLDocumentProvider_CantCreateFileInfo
	 */
	public static String HTMLDocumentProvider_CantCreateFileInfo;

	/**
	 * HTMLDocumentProvider_ErrorDisconnectingDocumentProvider
	 */
	public static String HTMLDocumentProvider_ErrorDisconnectingDocumentProvider;

	/**
	 * TidyHtmlValidator_Null_Pointer
	 */
	public static String TidyHtmlValidator_Null_Pointer;

	/**
	 * HTMLLanguageEnvironment_ErrorLoadingEnvironment
	 */
	public static String HTMLLanguageEnvironment_ErrorLoadingEnvironment;

}
