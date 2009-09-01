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
package com.aptana.ide.editor.css;

import org.eclipse.osgi.util.NLS;

/**
 * Messages
 * 
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.css.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * CSSEditor_cuInfo_Null_At_DoSetInput
	 */
	public static String CSSEditor_cuInfo_Null_At_DoSetInput;

	/**
	 * CSSEditor_Document_Provier_Null
	 */
	public static String CSSEditor_Document_Provier_Null;

	/**
	 * CSSEditor_Provider_Null
	 */
	public static String CSSEditor_Provider_Null;

	/**
	 * CSSErrorManager_InvalidUL
	 */
	public static String CSSErrorManager_InvalidUL;

	/**
	 * CSSErrorManager_ErrorParseMessage
	 */
	public static String CSSErrorManager_ErrorParseMessage;

	/**
	 * CSSErrorManager_LevelWarningMessage
	 */
	public static String CSSErrorManager_LevelWarningMessage;

	/**
	 * CSSFileLanguageService_NoInformationAvailable
	 */
	public static String CSSFileLanguageService_NoInformationAvailable;

	/**
	 * CSSFileLanguageService_FieldDescription
	 */
	public static String CSSFileLanguageService_FieldDescription;

	/**
	 * CSSFileLanguageService_StringLiteralDescription
	 */
	public static String CSSFileLanguageService_StringLiteralDescription;

	/**
	 * CSSFileLanguageService_NoLanguageServiceAvailable
	 */
	public static String CSSFileLanguageService_NoLanguageServiceAvailable;

	public static String CSSFileServiceFactory_ERR_UnableToCreateParser;

	/**
	 * CSSFileServiceFactory_FileServiceInitializationFailed
	 */
	public static String CSSFileServiceFactory_FileServiceInitializationFailed;

	/**
	 * CSSLanguageEnvironment_InitEnvironmentAborted
	 */
	public static String CSSLanguageEnvironment_InitEnvironmentAborted;

	/**
	 * CSSLanguageEnvironment_EnvironmentLoaderThreadName
	 */
	public static String CSSLanguageEnvironment_EnvironmentLoaderThreadName;
	
	/**
	 * CSSDocumentProvider_CantCreateFileInfo
	 */
	public static String CSSDocumentProvider_CantCreateFileInfo;
	
	/**
	 * CSSDocumentProvider_ErrorDisconnectingDocumentProvider
	 */
	public static String CSSDocumentProvider_ErrorDisconnectingDocumentProvider;

	/**
	 * 
	 */
    public static String StylesheetValidator_ILLEGAL_PROPERTY_MESSAGE;
    
    /**
     * 
     */
	public static String CSSLanguageEnvironment_ErrorLoadingEnvironment;

	public static String MultiPageCSSEditor_ERR_UnableToCreateBrowserControl;

	public static String MultiPageCSSEditor_ERR_UnableToUpdatePreview;

	public static String MultiPageCSSEditor_LB_FilePreviewSettings;

	public static String MultiPageCSSEditor_LBL_EditDefaultPreviewTemplate;

	public static String MultiPageCSSEditor_LBL_ProjectPreviewSettings;

	public static String MultiPageCSSEditor_LBL_WorkspacePreviewSettings;

	public static String MultiPageCSSEditor_TTL_Preview;

	public static String MultiPageCSSEditor_TTP_ConfigureCSSPreview;
}
