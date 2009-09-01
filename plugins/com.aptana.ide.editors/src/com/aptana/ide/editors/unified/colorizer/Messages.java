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
package com.aptana.ide.editors.unified.colorizer;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editors.unified.colorizer.messages"; //$NON-NLS-1$

	public static String ColorizerReader_ERR_ErrorCreatingColorizerHandler;

	public static String ColorizerReader_ERR_ErrorParsingColorizationFile;

	public static String ColorizerReader_ERR_InvalidBlueValueForRGBColor;

	public static String ColorizerReader_ERR_InvalidGreenValueForRGBColor;

	public static String ColorizerReader_ERR_InvalidRedValueForRGBColor;

	public static String ColorizerReader_ERR_InvalidRGBValue;

	public static String ColorizerReader_ERR_InvalidRGBValueSuffix;

	public static String ColorizerReader_ERR_NFEInLengthRegion;

	public static String ColorizerReader_ERR_NFEInOffsetRegion;

	/**
	 * ERROR_EXTENSION_POINT
	 */
	public static String ColorizerReader_ERROR_EXTENSION_POINT;

	/**
	 * ERROR_IMPORTING
	 */
	public static String ColorizerReader_ERROR_IMPORTING;

	/**
	 * ERROR_LOADING
	 */
	public static String ColorizerReader_ERROR_LOADING;

	/**
	 * ColorizerReader_ERROR_LOADING_SCHEMA
	 */
	public static String ColorizerReader_ERROR_LOADING_SCHEMA;

	/**
	 * ERROR_PARSING_COLOR
	 */
	public static String ColorizerReader_ERROR_PARSING_COLOR;

	public static String ColorizerReader_WRN_IgnoringDeclarationOfDuplicateStyle;

	/**
	 * ERROR_SAVING
	 */
	public static String ColorizerWriter_ERROR_SAVING;

	public static String LanguageColorizationWidget_ERR_ErrorExportingColorization;

	public static String LanguageColorizationWidget_ERR_MustHaveValidLengthSuffix;

	public static String LanguageColorizationWidget_ERR_MustHaveValidOffsetSuffix;

	public static String LanguageColorizationWidget_ERR_Region;

	public static String LanguageColorizationWidget_ERR_RegionNameAlreadyExists;

	public static String LanguageColorizationWidget_LBL_AddNewRegion;

	public static String LanguageColorizationWidget_LBL_Background;

	public static String LanguageColorizationWidget_LBL_CaretColor;

	public static String LanguageColorizationWidget_LBL_Category;

	public static String LanguageColorizationWidget_LBL_ColorizeTheLanguage;

	public static String LanguageColorizationWidget_LBL_EditorOptions;

	public static String LanguageColorizationWidget_LBL_Export;

	public static String LanguageColorizationWidget_LBL_FoldingBackground;

	public static String LanguageColorizationWidget_LBL_FoldingForeground;

	public static String LanguageColorizationWidget_LBL_Import;

	public static String LanguageColorizationWidget_LBL_Length;

	public static String LanguageColorizationWidget_LBL_LineHighlight;

	public static String LanguageColorizationWidget_LBL_LoadColorization;

	public static String LanguageColorizationWidget_LBL_ManageColorization;

	public static String LanguageColorizationWidget_LBL_Name;

	public static String LanguageColorizationWidget_LBL_NoteOffsetAndLength;

	public static String LanguageColorizationWidget_LBL_Offset;

	public static String LanguageColorizationWidget_LBL_OverrideEclipseEditorSettings;

	public static String LanguageColorizationWidget_LBL_RegionColorizations;

	public static String LanguageColorizationWidget_LBL_Remove;

	public static String LanguageColorizationWidget_LBL_SaveColorization;

	public static String LanguageColorizationWidget_LBL_SelectionBackground;

	public static String LanguageColorizationWidget_LBL_SelectionForeground;

	public static String LanguageColorizationWidget_LBL_Token;

	public static String LanguageColorizationWidget_LBL_Tokens;

	public static String LanguageColorizationWidget_MSG_RegionName;

	public static String LanguageColorizationWidget_MSG_RegionNameAlreadyExists;

	public static String LanguageColorizationWidget_TTL_EnterRegionName;

	/**
	 * LanguageRegistry_Cannot_Create_Token_List
	 */
	public static String LanguageRegistry_Cannot_Create_Token_List;

	/**
	 * LanguageRegistry_Cannot_Load_Token_List
	 */
	public static String LanguageRegistry_Cannot_Load_Token_List;

	/**
	 * LanguageRegistry_No_Associate_Token_List
	 */
	public static String LanguageRegistry_No_Associate_Token_List;

	/**
	 * LanguageRegistry_No_Associated_Parser
	 */
	public static String LanguageRegistry_No_Associated_Parser;
	
	/**
	 * LanguageRegistry_No_Associated_Finder
	 */
	public static String LanguageRegistry_No_Associated_Finder;

	/**
	 * NO_BUILDER
	 */
	public static String LanguageRegistry_NO_BUILDER;

	/**
	 * NO_COLORIZER
	 */
	public static String LanguageRegistry_NO_COLORIZER;

	/**
	 * ERROR_LOADING_GRAMMAR
	 */
	public static String LexerExtensionProcessor_Error_Loading_Grammar;

	/**
	 * ERROR_EXTENSION_POINT
	 */
	public static String LexerReader_ERROR_EXTENSION_POINT;

	/**
	 * ERROR_LOADING
	 */
	public static String LexerReader_ERROR_LOADING;

	public static String LanguageRegistry_No_Associated_Formatter;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
