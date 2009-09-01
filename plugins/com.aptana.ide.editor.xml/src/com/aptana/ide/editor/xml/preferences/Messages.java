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
package com.aptana.ide.editor.xml.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Robin
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.xml.preferences.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * BlankLinesTabPage_PRESERVE_CR_TITLE
	 */
	public static String BlankLinesTabPage_PRESERVE_CR_TITLE;

	/**
	 * BlankLinesTabPage_PRESERVE_LINES_TITLE
	 */
	public static String BlankLinesTabPage_PRESERVE_LINES_TITLE;

	/**
	 * BlankLinesTabPage_WHITESPACES_IN_CDATA_TITLE
	 */
	public static String BlankLinesTabPage_WHITESPACES_IN_CDATA_TITLE;

	/**
	 * BlankLinesTabPage_WHITESPACES_TITLE
	 */
	public static String BlankLinesTabPage_WHITESPACES_TITLE;

	/**
	 * GeneralPreferencePage_DafaultContent
	 */
	public static String GeneralPreferencePage_DafaultContent;

	/**
	 * GeneralPreferencePage_Description
	 */
	public static String GeneralPreferencePage_Description;

	/**
	 * GeneralPreferencePage_DisplayXMLToolbar
	 */
	public static String GeneralPreferencePage_DisplayXMLToolbar;

	/**
	 * GeneralPreferencePage_Outline
	 */
	public static String GeneralPreferencePage_Outline;

	/**
	 * GeneralPreferencePage_PairMatchingColor
	 */
	public static String GeneralPreferencePage_PairMatchingColor;

	/**
	 * GeneralPreferencePage_Prefs
	 */
	public static String GeneralPreferencePage_Prefs;

	/**
	 * GeneralPreferencePage_QuoteChar
	 */
	public static String GeneralPreferencePage_QuoteChar;

	/**
	 * GeneralPreferencePage_None
	 */
	public static String GeneralPreferencePage_None;

	/**
	 * GeneralPreferencePage_Double
	 */
	public static String GeneralPreferencePage_Double;

	/**
	 * GeneralPreferencePage_MarkOccurrenHighlightBothTags
	 */
	public static String GeneralPreferencePage_MarkOccurrenHighlightBothTags;	
	
	/**
	 * GeneralPreferencePage_Single
	 */
	public static String GeneralPreferencePage_Single;

	/**
	 * GeneralPreferencePage_TagAttributes
	 */
	public static String GeneralPreferencePage_TagAttributes;

	/**
	 * CodeAssistPreferencePage_AutoInsertion
	 */
	public static String CodeAssistPreferencePage_AutoInsertion;

	/**
	 * CodeAssistPreferencePage_AutomaticallyInsertTheClosingTagInCodeAssist
	 */
	public static String CodeAssistPreferencePage_AutomaticallyInsertTheClosingTagInCodeAssist;

	/**
	 * CodeAssistPreferencePage_AutomaticallyInsertTheEqualsSignInCodeAssist
	 */
	public static String CodeAssistPreferencePage_AutomaticallyInsertTheEqualsSignInCodeAssist;

	/**
	 * ColorPreferencePage_ColorPrefs
	 */
	public static String ColorPreferencePage_ColorPrefs;

	/**
	 * GeneralPreferencePage_DefaultXMLFileName
	 */
	public static String GeneralPreferencePage_DefaultXMLFileName;

	/**
	 * GeneralPreferencePage_InitialXMLFileContents
	 */
	public static String GeneralPreferencePage_InitialXMLFileContents;
	
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
	 * NewLinesTabPage_DO_NOT_WRAP
	 */
	public static String NewLinesTabPage_DO_NOT_WRAP;

	/**
	 * NewLinesTabPage_GROUP_TITLE
	 */
	public static String NewLinesTabPage_GROUP_TITLE;

	/**
	 * NewLinesTabPage_REMOVE_TOOLTIP
	 */
	public static String NewLinesTabPage_REMOVE_TOOLTIP;

	/**
	 * NewLinesTabPage_WRAPGROUP_TITLE
	 */
	public static String NewLinesTabPage_WRAPGROUP_TITLE;
	
	/**
	 * GeneralPreferencePage_AutoIndentCarriageReturn
	 */
	public static String GeneralPreferencePage_AutoIndentCarriageReturn;
	
	/**
	 * GeneralPreferencePage_DontIndent
	 */
	public static String GeneralPreferencePage_DontIndent;
	
	/**
	 * GeneralPreferencePage_Indent
	 */
	public static String GeneralPreferencePage_Indent;
	
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
