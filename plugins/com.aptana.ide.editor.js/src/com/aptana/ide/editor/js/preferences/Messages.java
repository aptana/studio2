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
package com.aptana.ide.editor.js.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Robin
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.js.preferences.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String CodeAssistPreferencePage_Javascript_environments;

	public static String CodeAssistPreferencePage_LBL_Environments_present_in_code_assist;

	/**
	 * ColorPreferencePage_ColorPrefsMsg
	 */
	public static String ColorPreferencePage_ColorPrefsMsg;

	/**
	 * FoldingPreferencePage_FoldBlockComments
	 */
	public static String FoldingPreferencePage_FoldBlockComments;

	/**
	 * FoldingPreferencePage_FoldFunctions
	 */
	public static String FoldingPreferencePage_FoldFunctions;

	/**
	 * FoldingPreferencePage_FoldScriptDocComments
	 */
	public static String FoldingPreferencePage_FoldScriptDocComments;

	/**
	 * GeneralPreferencePage_DefaultContent
	 */
	public static String GeneralPreferencePage_DefaultContent;

	/**
	 * GeneralPreferencePage_DisplayJSToolbar
	 */
	public static String GeneralPreferencePage_DisplayJSToolbar;

	/**
	 * GeneralPreferencePage_JavaScriptDescription
	 */
	public static String GeneralPreferencePage_JavaScriptDescription;

	/**
	 * GeneralPreferencePage_JavaScriptEditorGeneralPreferences
	 */
	public static String GeneralPreferencePage_JavaScriptEditorGeneralPreferences;

	/**
	 * GeneralPreferencePage_IndentWithLeadingStar
	 */
	public static String GeneralPreferencePage_IndentWithLeadingStar;

	/**
	 * GeneralPreferencePage_AutoInsertMatchingCharacters
	 */
	public static String GeneralPreferencePage_AutoInsertMatchingCharacters;

	/**
	 * GeneralPreferencePage_DontAutoInsert
	 */
	public static String GeneralPreferencePage_DontAutoInsert;

	/**
	 * GeneralPreferencePage_AutoInsert
	 */
	public static String GeneralPreferencePage_AutoInsert;

	/**
	 * GeneralPreferencePage_AutoInsertAndDelete
	 */
	public static String GeneralPreferencePage_AutoInsertAndDelete;

	/**
	 * GeneralPreferencePage_Outline
	 */
	public static String GeneralPreferencePage_Outline;

	/**
	 * GeneralPreferencePage_PairMatchingColor
	 */
	public static String GeneralPreferencePage_PairMatchingColor;

	/**
	 * GeneralPreferencePage_PrivateFieldIndicator
	 */
	public static String GeneralPreferencePage_PrivateFieldIndicator;

	/**
	 * GeneralPreferencePage_DefaultFileName
	 */
	public static String GeneralPreferencePage_DefaultFileName;

	/**
	 * GeneralPreferencePage_InitialFileContents
	 */
	public static String GeneralPreferencePage_InitialFileContents;

	public static String PreferenceInitializer_Default_sdoc_filename;

	/**
	 * PreferenceInitializer_InitialFileContents
	 */
	public static String PreferenceInitializer_InitialFileContents;

	public static String PreferenceInitializer_Mozilla_javascript_validator;

	/**
	 * PreferenceInitializer_NewFileName
	 */
	public static String PreferenceInitializer_NewFileName;

	public static String ProblemsPreferencePage_LBL_Enable_error_filtering_inside_novalidate;

	/**
	 * GeneralPreferencePage_AutoFormatOnCloseCurly
	 */
	public static String GeneralPreferencePage_AutoFormatOnCloseCurly;

	public static String TypingPreferencePage_LBL_Formatting;

	public static String TypingPreferencePage_AutoIndentCarriageReturn;

	public static String TypingPreferencePage_Indent;

	public static String TypingPreferencePage_DontIndent;
}
