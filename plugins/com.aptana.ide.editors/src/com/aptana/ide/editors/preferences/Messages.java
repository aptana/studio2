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
package com.aptana.ide.editors.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * Messages
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editors.preferences.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * AdvancedPreferencePage_AssociateWithAptana
	 */
	public static String AdvancedPreferencePage_AssociateWithAptana;
	/**
	 * AdvancedPreferencePage_AssociateWithNotepad
	 */
	public static String AdvancedPreferencePage_AssociateWithNotepad;
	/**
	 * AdvancedPreferencePage_AssociateWithOther
	 */
	public static String AdvancedPreferencePage_AssociateWithOther;
	/**
	 * AdvancedPreferencePage_CheckPrivileges
	 */
	public static String AdvancedPreferencePage_CheckPrivileges;
	/**
	 * AdvancedPreferencePage_Debugging
	 */
	public static String AdvancedPreferencePage_Debugging;

	/**
	 * AdvancedPreferencePage_DebuggingAndAdvanced
	 */
	public static String AdvancedPreferencePage_DebuggingAndAdvanced;
	public static String AdvancedPreferencePage_ERR_ErrorGettingRegistryValue;
	/**
	 * AdvancedPreferencePage_ErrorSettingRegistry
	 */
	public static String AdvancedPreferencePage_ErrorSettingRegistry;
	/**
	 * AdvancedPreferencePage_IESettings
	 */
	public static String AdvancedPreferencePage_IESettings;

	public static String AdvancedPreferencePage_LBL_AdvancedFunctionality;
	public static String AdvancedPreferencePage_LBL_All;
	public static String AdvancedPreferencePage_LBL_AllDebuggingInformation;
	public static String AdvancedPreferencePage_LBL_ControlDebugInformationAmountHelp;
	public static String AdvancedPreferencePage_LBL_DebuggingOutputLevel;
	public static String AdvancedPreferencePage_LBL_Errors;
	public static String AdvancedPreferencePage_LBL_ErrorsAndImportant;
	public static String AdvancedPreferencePage_LBL_NoDebuggingOutput;
	public static String AdvancedPreferencePage_LBL_OnlyError;
	public static String AdvancedPreferencePage_LBL_ParserOffUI;
	public static String AdvancedPreferencePage_LBL_UnknownLoggingLevel;
	/**
	 * AdvancedPreferencePage_LogDebuggingMessages
	 */
	public static String AdvancedPreferencePage_LogDebuggingMessages;
	/**
	 * AdvancedPreferencePage_PleaseSpecifyApplication
	 */
	public static String AdvancedPreferencePage_PleaseSpecifyApplication;

	/**
	 * AdvancedPreferencePage_ShowDebugInformation
	 */
	public static String AdvancedPreferencePage_ShowDebugInformation;
	/**
	 * AdvancedPreferencePage_User
	 */
	public static String AdvancedPreferencePage_User;

	/**
	 * ColorPreferencePage_ColorizationDescription
	 */
	public static String ColorPreferencePage_ColorizationDescription;

	/**
	 * ColorPreferencePage_ForegroundColor
	 */
	public static String ColorPreferencePage_ForegroundColor;

	/**
	 * ColorPreferencePage_SyntaxColorization
	 */
	public static String ColorPreferencePage_SyntaxColorization;

	public static String CoreEditorPreferencePage_LBL_edit;
	public static String CoreEditorPreferencePage_LBL_FileAssociations;
	public static String CoreEditorPreferencePage_TTP_EditFileAssociations;
	/**
	 * EditorPreferencePage_AutoInsert
	 */
	public static String EditorPreferencePage_AutoInsert;

	/**
	 * EditorPreferencePage_AutoInsertMatching
	 */
	public static String EditorPreferencePage_AutoInsertMatching;

	/**
	 * EditorPreferencePage_DisplayMatchingElement
	 */
	public static String EditorPreferencePage_DisplayMatchingElement;

	/**
	 * EditorPreferencePage_DisplayOptions
	 */
	public static String EditorPreferencePage_DisplayOptions;

	/**
	 * EditorPreferencePage_DoNotDisplay
	 */
	public static String EditorPreferencePage_DoNotDisplay;

	/**
	 * EditorPreferencePage_DontAutoInsert
	 */
	public static String EditorPreferencePage_DontAutoInsert;

	/**
	 * EditorPreferencePage_HighlightBoth
	 */
	public static String EditorPreferencePage_HighlightBoth;

	/**
	 * EditorPreferencePage_HighlightColor
	 */
	public static String EditorPreferencePage_HighlightColor;

	/**
	 * EditorPreferencePage_HiglightMatch
	 */
	public static String EditorPreferencePage_HiglightMatch;

	/**
	 * EditorPreferencePage_INSERT
	 */
	public static String EditorPreferencePage_INSERT;

	/**
	 * EditorPreferencePage_NONE
	 */
	public static String EditorPreferencePage_NONE;

	/**
	 * EditorPreferencePage_PairMatching
	 */
	public static String EditorPreferencePage_PairMatching;

	/**
	 * FoldingPreferencePage_EnableFolding
	 */
	public static String FoldingPreferencePage_EnableFolding;

	/**
	 * FoldingPreferencePage_FoldingOptions
	 */
	public static String FoldingPreferencePage_FoldingOptions;
	
	/**
	 * GeneralPreferencePage_Advanced
	 */
	public static String GeneralPreferencePage_Advanced;

	/**
	 * GeneralPreferencePage_CodeAssist
	 */
	public static String GeneralPreferencePage_CodeAssist;

	/**
	 * GeneralPreferencePage_DelayBeforeShowing
	 */
	public static String GeneralPreferencePage_DelayBeforeShowing;
	
	/**
	 * GeneralPreferencePage_EditLink
	 */
	public static String GeneralPreferencePage_EditLink;
	public static String GeneralPreferencePage_EmailAddressForBugReports;

	/**
	 * GeneralPreferencePage_EnableCodeDragAndDrop
	 */
	public static String GeneralPreferencePage_EnableCodeDragAndDrop;

	/**
	 * GeneralPreferencePage_EnableCodePairMatching
	 */
	public static String GeneralPreferencePage_EnableCodePairMatching;

	/**
	 * GeneralPreferencePage_EnableSourceColorizing
	 */
	public static String GeneralPreferencePage_EnableSourceColorizing;

	/**
	 * GeneralPreferencePage_EnableUnicode
	 */
	public static String GeneralPreferencePage_EnableUnicode;

	/**
	 * GeneralPreferencePage_EnableWordWrap
	 */
	public static String GeneralPreferencePage_EnableWordWrap;

	/**
	 * GeneralPreferencePage_Formatting
	 */
	public static String GeneralPreferencePage_Formatting;
	
	/**
	 * GeneralPreferencePage_General
	 */
	public static String GeneralPreferencePage_General;
	public static String GeneralPreferencePage_HomeEndBehavior;

	public static String GeneralPreferencePage_INF_ErrorParsingOSGIFrameworkVersion;
	/**
	 * GeneralPreferencePage_InsertSelectedProposal
	 */
	public static String GeneralPreferencePage_InsertSelectedProposal;

	/**
	 * GeneralPreferencePage_InsertSpaces
	 */
	public static String GeneralPreferencePage_InsertSpaces;
	public static String GeneralPreferencePage_JumpsStartEnd;
	
	/**
	 * GeneralPreferencePage_MarkOccurrences
	 */
	public static String GeneralPreferencePage_MarkOccurrences;

	/**
	 * GeneralPreferencePage_MaxColorizeColumns
	 */
	public static String GeneralPreferencePage_MaxColorizeColumns;

	/**
	 * GeneralPreferencePage_OptimizeProfileReparsing
	 */
	public static String GeneralPreferencePage_OptimizeProfileReparsing;

	/**
	 * GeneralPreferencePage_PreferenceDescription
	 */
	public static String GeneralPreferencePage_PreferenceDescription;

	/**
	 * GeneralPreferencePage_ShowLocations
	 */
	public static String GeneralPreferencePage_ShowLocations;

	/**
	 * GeneralPreferencePage_ShowSpacesAs
	 */
	public static String GeneralPreferencePage_ShowSpacesAs;

	/**
	 * GeneralPreferencePage_ShowTabsAs
	 */
	public static String GeneralPreferencePage_ShowTabsAs;
	
	/**
	 * GeneralPreferencePage_TabInsertion
	 */
	public static String GeneralPreferencePage_TabInsertion;
	public static String GeneralPreferencePage_ToggleBetween;
	
	/**
	 * GeneralPreferencePage_UseSpaces
	 */
	public static String GeneralPreferencePage_UseSpaces;
	
	/**
	 * GeneralPreferencePage_UseTabs
	 */
	public static String GeneralPreferencePage_UseTabs;

	/**
	 * GeneralPreferencePage_WhitespaceMarkers
	 */
	public static String GeneralPreferencePage_WhitespaceMarkers;

	/**
	 * GeneralPreferencePage_EnableOccurrenceHighlight
	 */
	public static String GeneralPreferencePage_EnableOccurrenceHighlight;
	public static String GeneralPreferencePage_LBL_Colorization;
	public static String GeneralPreferencePage_LBL_Less;
	public static String GeneralPreferencePage_LBL_More;
	public static String GeneralPreferencePage_LBL_PianoKeyColorDifference;

	/**
	 * GeneralPreferencePage_OccurrenceBackgroundColor
	 */
	public static String GeneralPreferencePage_OccurrenceBackgroundColor;

	/**
	 * GeneralPreferencePage_SmartHomeKeyPositioning
	 */
	public static String GeneralPreferencePage_SmartHomeKeyPositioning;
	
	/**
	 * AdvancedPreferencePage_switchToAptanaPerspective
	 */
	public static String AdvancedPreferencePage_switchToAptanaPerspective;
	
	/**
	 * AdvancedPreferencePage_Always
	 */
	public static String AdvancedPreferencePage_Always;
	
	/**
	 * AdvancedPreferencePage_Never
	 */
	public static String AdvancedPreferencePage_Never;
	
	/**
	 * AdvancedPreferencePage_Prompt
	 */
	public static String AdvancedPreferencePage_Prompt;
	public static String TypingPreferencePage_LBL_TypingPreferencesPageDescription;
	
	/**
	 * GeneralPreferencePage_GeneralTextEditorPrefLink
	 */
	public static String GeneralPreferencePage_GeneralTextEditorPrefLink;
}
