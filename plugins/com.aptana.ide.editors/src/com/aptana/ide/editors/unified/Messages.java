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
package com.aptana.ide.editors.unified;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editors.unified.messages"; //$NON-NLS-1$

	public static String BaseDocumentProvider_ERR_FileCouldNotBeSaved;

	/**
	 * BaseDocumentProvider_Error
	 */
	public static String BaseDocumentProvider_Error;

	public static String BaseDocumentProvider_MSG_HelpChangeFileEncoding;

	public static String BaseFormatter_ERR_FormattingFailure;

	public static String BaseFormatter_INF_ErrorParsingFormattedContent;

	public static String BaseFormatter_INF_FormattingErrorDetails;

	public static String BaseFormatter_INF_NewLexemeListSizeMismatch;

	public static String BaseFormatter_INF_NewLexemeMismatch;

	public static String BaseFormatter_TTL_ErrorFormatting;

	/**
	 * ChildOffsetMapper_NonNullParent
	 */
	public static String ChildOffsetMapper_NonNullParent;

	/**
	 * DocumentationHelper_Category
	 */
	public static String DocumentationHelper_Category;

	/**
	 * DocumentationHelper_DebugInformation
	 */
	public static String DocumentationHelper_DebugInformation;

	/**
	 * DocumentationHelper_Language
	 */
	public static String DocumentationHelper_Language;

	/**
	 * DocumentationHelper_Offset
	 */
	public static String DocumentationHelper_Offset;

	/**
	 * DocumentationHelper_SourceLength
	 */
	public static String DocumentationHelper_SourceLength;

	/**
	 * DocumentationHelper_SourcePath
	 */
	public static String DocumentationHelper_SourcePath;

	/**
	 * DocumentationHelper_Text
	 */
	public static String DocumentationHelper_Text;

	/**
	 * DocumentationHelper_Total
	 */
	public static String DocumentationHelper_Total;

	/**
	 * DocumentSourceProvider_Error
	 */
	public static String DocumentSourceProvider_Error;

	/**
	 * FileService_CantConnetSourceProvider
	 */
	public static String FileService_CantConnetSourceProvider;

	/**
	 * FileService_DoFullParseFailed
	 */
	public static String FileService_DoFullParseFailed;

	/**
	 * FileService_DoParseFailedLexer
	 */
	public static String FileService_DoParseFailedLexer;

	/**
	 * FileService_DoParseFailedParse
	 */
	public static String FileService_DoParseFailedParse;

	/**
	 * FileService_ErrorOnFireChangedEvent
	 */
	public static String FileService_ErrorOnFireChangedEvent;

	/**
	 * FileService_GetSourceFailed
	 */
	public static String FileService_GetSourceFailed;

	/**
	 * FileService_LanguageServiceRegistered
	 */
	public static String FileService_LanguageServiceRegistered;

	/**
	 * FileService_SourceProviderAlreadyConnected
	 */
	public static String FileService_SourceProviderAlreadyConnected;

	/**
	 * FileService_SourceProviderNotConnected
	 */
	public static String FileService_SourceProviderNotConnected;

	/**
	 * FileService_UpdateContentFailed
	 */
	public static String FileService_UpdateContentFailed;

	/**
	 * FileSourceProvider_MethodNotAvailable
	 */
	public static String FileSourceProvider_MethodNotAvailable;

	/**
	 * IdleFileChangedNotifier_SourceNotFileService
	 */
	public static String IdleFileChangedNotifier_SourceNotFileService;

	public static String InstanceCreator_ERR_UnableToCreateInstance;

	/**
	 * ParentOffsetMapper_IFileContentNotNull
	 */
	public static String ParentOffsetMapper_IFileContentNotNull;

	/**
	 * ParentOffsetMapper_LanguageNotSupported
	 */
	public static String ParentOffsetMapper_LanguageNotSupported;

	/**
	 * UnifiedAutoIndentStrategy_InvalidOffset
	 */
	public static String UnifiedAutoIndentStrategy_InvalidOffset;

	public static String UnifiedConfiguration_ERR_UnableToLoadContentAssistant;

	/**
	 * UnifiedDocumentProvider_CantCreateFileInfo
	 */
	public static String UnifiedDocumentProvider_CantCreateFileInfo;

	/**
	 * UnifiedDocumentProvider_ErrorDisconnectingDocumentProvider
	 */
	public static String UnifiedDocumentProvider_ErrorDisconnectingDocumentProvider;

	/**
	 * UnifiedEditor_CuInfoIsNull
	 */
	public static String UnifiedEditor_CuInfoIsNull;

	/**
	 * UnifiedEditor_DocumentMustBe
	 */
	public static String UnifiedEditor_DocumentMustBe;

	/**
	 * UnifiedEditor_DocumentProviderNull
	 */
	public static String UnifiedEditor_DocumentProviderNull;

	/**
	 * UnifiedEditor_ErrorContentProposals
	 */
	public static String UnifiedEditor_ErrorContentProposals;

	/**
	 * UnifiedEditor_ErrorHandlingPreferenceChange
	 */
	public static String UnifiedEditor_ErrorHandlingPreferenceChange;

	/**
	 * UnifiedEditor_ErrorUpdateTabWidth
	 */
	public static String UnifiedEditor_ErrorUpdateTabWidth;

	/**
	 * UnifiedEditor_LexemeListIsNull
	 */
	public static String UnifiedEditor_LexemeListIsNull;

	/**
	 * UnifiedEditor_MatchingPairError
	 */
	public static String UnifiedEditor_MatchingPairError;

	/**
	 * UnifiedEditor_MatchingPairErrorMessage
	 */
	public static String UnifiedEditor_MatchingPairErrorMessage;

	/**
	 * UnifiedEditor_PairDraw
	 */
	public static String UnifiedEditor_PairDraw;

	/**
	 * UnifiedEditor_ProviderIsNull
	 */
	public static String UnifiedEditor_ProviderIsNull;

	/**
	 * UnifiedEditor_UnableToRetrievePreferenceStore
	 */
	public static String UnifiedEditor_UnableToRetrievePreferenceStore;

	/**
	 * UnifiedPartitionScanner_DefaultLanguageMustBeDefined
	 */
	public static String UnifiedPartitionScanner_DefaultLanguageMustBeDefined;

	/**
	 * UnifiedPartitionScanner_ServiceNotNull
	 */
	public static String UnifiedPartitionScanner_ServiceNotNull;

	/**
	 * UnifiedReconcilingStrategy_EmitPositionFailed
	 */
	public static String UnifiedReconcilingStrategy_EmitPositionFailed;

	/**
	 * UniformResourceMarkerAnnotationModel_ErrorDeletingMarkers
	 */
	public static String UniformResourceMarkerAnnotationModel_ErrorDeletingMarkers;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
