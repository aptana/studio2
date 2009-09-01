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
package com.aptana.ide.editor.html.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Robin
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.html.preferences.messages"; //$NON-NLS-1$

	/**
	 * CodeAssistPreferencePage_AutoInsertion
	 */
	public static String CodeAssistPreferencePage_AutoInsertion;

	/**
	 * FoldingPreferencePage_Add_One_Or_Mode_Tooltip
	 */
	public static String FoldingPreferencePage_Add_One_Or_Mode_Tooltip;

	/**
	 * FoldingPreferencePage_AddFoldableNodes
	 */
	public static String FoldingPreferencePage_AddFoldableNodes;

	/**
	 * FoldingPreferencePage_AddFoldableNodesDesc
	 */
	public static String FoldingPreferencePage_AddFoldableNodesDesc;

	/**
	 * FoldingPreferencePage_FoldableHTMLNodes
	 */
	public static String FoldingPreferencePage_FoldableHTMLNodes;

	/**
	 * FoldingPreferencePage_RemoveSelectedNode
	 */
	public static String FoldingPreferencePage_RemoveSelectedNode;

	/**
	 * GeneralPreferencePage_AptanaHTMLEditorEditsHTMLFiles
	 */
	public static String GeneralPreferencePage_AptanaHTMLEditorEditsHTMLFiles;

	/**
	 * GeneralPreferencePage_AutoIndentCarriageReturn
	 */
	public static String GeneralPreferencePage_AutoIndentCarriageReturn;

	/**
	 * GeneralPreferencePage_DefaultContent
	 */
	public static String GeneralPreferencePage_DefaultContent;

	/**
	 * GeneralPreferencePage_DisplayHTMLToolbar
	 */
	public static String GeneralPreferencePage_DisplayHTMLToolbar;

	/**
	 * GeneralPreferencePage_DontIndent
	 */
	public static String GeneralPreferencePage_DontIndent;

	/**
	 * GeneralPreferencePage_HTMLEditorGeneralPreferences
	 */
	public static String GeneralPreferencePage_HTMLEditorGeneralPreferences;

	/**
	 * GeneralPreferencePage_HTMLEditorMode
	 */
	public static String GeneralPreferencePage_HTMLEditorMode;

	/**
	 * GeneralPreferencePage_Indent
	 */
	public static String GeneralPreferencePage_Indent;

	/**
	 * GeneralPreferencePage_MarkOccurrenHighlightBothTags
	 */
	public static String GeneralPreferencePage_MarkOccurrenHighlightBothTags;

	/**
	 * GeneralPreferencePage_Outline
	 */
	public static String GeneralPreferencePage_Outline;

	/**
	 * GeneralPreferencePage_PairMatchingColor
	 */
	public static String GeneralPreferencePage_PairMatchingColor;

	/**
	 * GeneralPreferencePage_PHPEditorMode
	 */
	public static String GeneralPreferencePage_PHPEditorMode;

	/**
	 * GeneralPreferencePage_SourceOnlyView
	 */
	public static String GeneralPreferencePage_SourceOnlyView;

	/**
	 * GeneralPreferencePage_TabbedView
	 */
	public static String GeneralPreferencePage_TabbedView;

	/**
	 * GeneralPreferencePage_HorizontalSplitView
	 */
	public static String GeneralPreferencePage_HorizontalSplitView;

	/**
	 * GeneralPreferencePage_VerticalSplitView
	 */
	public static String GeneralPreferencePage_VerticalSplitView;

	/**
	 * GeneralPreferencePage_CharacterToUseWhenQuotingAttributes
	 */
	public static String GeneralPreferencePage_CharacterToUseWhenQuotingAttributes;

	/**
	 * GeneralPreferencePage_None
	 */
	public static String GeneralPreferencePage_None;

	/**
	 * GeneralPreferencePage_DoubleQuote
	 */
	public static String GeneralPreferencePage_DoubleQuote;

	/**
	 * GeneralPreferencePage_SingleQuote
	 */
	public static String GeneralPreferencePage_SingleQuote;

	/**
	 * GeneralPreferencePage_AutomaticallyInsertTheClosingTagInCodeAssist
	 */
	public static String GeneralPreferencePage_AutomaticallyInsertTheClosingTagInCodeAssist;

	/**
	 * GeneralPreferencePage_AutomaticallyInsertTheEqualsSignInCodeAssist
	 */
	public static String GeneralPreferencePage_AutomaticallyInsertTheEqualsSignInCodeAssist;

	/**
	 * GeneralPreferencePage_TagAttributesToShowInOutlineView
	 */
	public static String GeneralPreferencePage_TagAttributesToShowInOutlineView;

	/**
	 * GeneralPreferencePage_DefaultExtensionForNewHtMLFiles
	 */
	public static String GeneralPreferencePage_DefaultExtensionForNewHtMLFiles;

	/**
	 * ColorPreferencePage_HTMLColorPrefs
	 */
	public static String ColorPreferencePage_HTMLColorPrefs;

	/**
	 * static ctor
	 */
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
	 * GeneralPreferencePage_DefaultFileName
	 */
	public static String GeneralPreferencePage_DefaultFileName;

	/**
	 * GeneralPreferencePage_InitialFileContents
	 */
	public static String GeneralPreferencePage_InitialFileContents;

	/**
	 * IndentationTabPage_DO_NOT_INDENT_ON
	 */
	public static String IndentationTabPage_DO_NOT_INDENT_ON;

	/**
	 * NewLinesTabPage_9
	 */
	public static String NewLinesTabPage_9;

	/**
	 * NewLinesTabPage_ADD_DESCRIPTION
	 */
	public static String NewLinesTabPage_ADD_DESCRIPTION;

	/**
	 * NewLinesTabPage_ADD_TITLE
	 */
	public static String NewLinesTabPage_ADD_TITLE;

	/**
	 * NewLinesTabPage_ADD_TOOLTIP
	 */
	public static String NewLinesTabPage_ADD_TOOLTIP;

	/**
	 * NewLinesTabPage_GROUP_TITLE
	 */
	public static String NewLinesTabPage_GROUP_TITLE;

	/**
	 * NewLinesTabPage_REMOVE_TOOLTIP
	 */
	public static String NewLinesTabPage_REMOVE_TOOLTIP;

	/**
	 * PreferenceInitializer_IntitialFileContents
	 */
	public static String PreferenceInitializer_IntitialFileContents;

	/**
	 * PreferenceInitializer_NewFileName
	 */
	public static String PreferenceInitializer_NewFileName;

	/**
	 * PreviewPreferencePage_FirefoxPreviewIssueMessage
	 */
	public static String PreviewPreferencePage_FirefoxPreviewIssueMessage;

	/**
	 * PreviewPreferencePage_FirefoxPreviewIssueTitle
	 */
	public static String PreviewPreferencePage_FirefoxPreviewIssueTitle;

	public static String PreviewPreferencePage_LBL_AutoSave;

    public static String PreviewPreferencePage_LBL_DefaultSettings;

    public static String PreviewPreferencePage_LBL_GenerateTemp;

    public static String PreviewPreferencePage_LBL_PreventCache;

    public static String PreviewPreferencePage_LBL_SampleURL;

    /**
	 * PreviewPreferencePage_PreviewBrowserDescription
	 */
	public static String PreviewPreferencePage_PreviewBrowserDescription;

	/**
	 * PreviewPreferencePage_PreviewBrowsers
	 */
	public static String PreviewPreferencePage_PreviewBrowsers;

	/**
	 * FormattingPreferencePage_GeneralSettings
	 */
	public static String FormattingPreferencePage_GeneralSettings;

	/**
	 * FormattingPreferencePage_TabSize
	 */
	public static String FormattingPreferencePage_TabSize;

	/**
	 * FormattingPreferencePage_Indent
	 */
	public static String FormattingPreferencePage_Indent;

	/**
	 * FormattingPreferencePage_IndentTags
	 */
	public static String FormattingPreferencePage_IndentTags;

	/**
	 * FormattingPreferencePage_IndentTagContent
	 */
	public static String FormattingPreferencePage_IndentTagContent;

	/**
	 * FormattingPreferencePage_NumberOfSpacesToIndentContent
	 */
	public static String FormattingPreferencePage_NumberOfSpacesToIndentContent;

	/**
	 * FormattingPreferencePage_LineWrapping
	 */
	public static String FormattingPreferencePage_LineWrapping;

	/**
	 * FormattingPreferencePage_DefaultWrapMargin
	 */
	public static String FormattingPreferencePage_DefaultWrapMargin;

	/**
	 * FormattingPreferencePage_InsertNewlineBeforeBR
	 */
	public static String FormattingPreferencePage_InsertNewlineBeforeBR;

	/**
	 * FormattingPreferencePage_WrapTextInBlocks
	 */
	public static String FormattingPreferencePage_WrapTextInBlocks;

	/**
	 * FormattingPreferencePage_WrapTextInBody
	 */
	public static String FormattingPreferencePage_WrapTextInBody;

	/**
	 * FormattingPreferencePage_InsertNewlineBeforeAttribute
	 */
	public static String FormattingPreferencePage_InsertNewlineBeforeAttribute;

	/**
	 * FormattingPreferencePage_AllowAttributesOnNewlines
	 */
	public static String FormattingPreferencePage_AllowAttributesOnNewlines;

	/**
	 * FormattingPreferencePage_Cleanup
	 */
	public static String FormattingPreferencePage_Cleanup;

	/**
	 * FormattingPreferencePage_OutputFormat
	 */
	public static String FormattingPreferencePage_OutputFormat;

	/**
	 * FormattingPreferencePage_Original
	 */
	public static String FormattingPreferencePage_Original;

	/**
	 * FormattingPreferencePage_XHTML
	 */
	public static String FormattingPreferencePage_XHTML;

	/**
	 * FormattingPreferencePage_XML
	 */
	public static String FormattingPreferencePage_XML;

	/**
	 * FormattingPreferencePage_DefaultTextForAlt
	 */
	public static String FormattingPreferencePage_DefaultTextForAlt;

	/**
	 * FormattingPreferencePage_DiscardEmptyPElements
	 */
	public static String FormattingPreferencePage_DiscardEmptyPElements;

	/**
	 * FormattingPreferencePage_DiscardPresentationTags
	 */
	public static String FormattingPreferencePage_DiscardPresentationTags;

	/**
	 * FormattingPreferencePage_FirURLs
	 */
	public static String FormattingPreferencePage_FirURLs;

	/**
	 * FormattingPreferencePage_FixAdjacentComments
	 */
	public static String FormattingPreferencePage_FixAdjacentComments;

	/**
	 * FormattingPreferencePage_SuppressOptionalEndTags
	 */
	public static String FormattingPreferencePage_SuppressOptionalEndTags;

	/**
	 * FormattingPreferencePage_ReplaceIAndBTags
	 */
	public static String FormattingPreferencePage_ReplaceIAndBTags;

	/**
	 * FormattingPreferencePage_RemovePresentationalClutter
	 */
	public static String FormattingPreferencePage_RemovePresentationalClutter;

	/**
	 * FormattingPreferencePage_UseNumericEntities
	 */
	public static String FormattingPreferencePage_UseNumericEntities;

	/**
	 * FormattingPreferencePage_OutputNakedAmperand
	 */
	public static String FormattingPreferencePage_OutputNakedAmperand;

	/**
	 * FormattingPreferencePage_OutputQuoteMarks
	 */
	public static String FormattingPreferencePage_OutputQuoteMarks;

	/**
	 * FormattingPreferencePage_OutputNonBreakingSpace
	 */
	public static String FormattingPreferencePage_OutputNonBreakingSpace;

	/**
	 * FormattingPreferencePage_AvoidMappingValues
	 */
	public static String FormattingPreferencePage_AvoidMappingValues;

	/**
	 * FormattingPreferencePage_CleanWordTags
	 */
	public static String FormattingPreferencePage_CleanWordTags;

    public static String HTMLPreviewPropertyPage_Description;

    public static String HTMLPreviewPropertyPage_INF_ErrorLoading;

	public static String HTMLPreviewPropertyPage_LBL_Browse;

    public static String HTMLPreviewPropertyPage_LBL_DocRoot;

    public static String HTMLPreviewPropertyPage_LBL_EditLink;

    public static String HTMLPreviewPropertyPage_LBL_Override;

    public static String HTMLPreviewPropertyPage_LBL_SampleURL;

    public static String HTMLPreviewPropertyPage_MSG_SelectRoot;

    /**
	 * TypingPreferencePage_AutoCloseTags
	 */
	public static String TypingPreferencePage_AutoCloseTags;

	/**
	 * TypingPreferencePage_Never
	 */
	public static String TypingPreferencePage_Never;

	/**
	 * TypingPreferencePage_WhenTypingOpeningTag
	 */
	public static String TypingPreferencePage_WhenTypingOpeningTag;

	/**
	 * TypingPreferencePage_WhyenTypingClosingTag
	 */
	public static String TypingPreferencePage_WhyenTypingClosingTag;

	/**
	 * NewLinesTabPage_WRAPGROUP_TITLE
	 */
	public static String NewLinesTabPage_WRAPGROUP_TITLE;

	/**
	 * NewLinesTabPage_DO_NOT_WRAP
	 */
	public static String NewLinesTabPage_DO_NOT_WRAP;

	/**
	 * TypingPreferencePage_AutoModifyPairTag
	 */
	public static String TypingPreferencePage_AutoModifyPairTag;

	/**
	 * TypingPreferencePage_AutoModifyPairTag
	 */
	public static String TypingPreferencePage_ModifyPairTag;

	/**
	 * TypingPreferencePage_AutoModifyPairTag
	 */
	public static String TypingPreferencePage_NOModifyPairTag;
}
