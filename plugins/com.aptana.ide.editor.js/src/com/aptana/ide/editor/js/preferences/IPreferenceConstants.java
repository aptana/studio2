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

/**
 * Contains all preferences for the com.aptana.ide.editor.js plugin To add a preference, create a static string with an
 * all-uppercase preference key. Then assign a identically-named string to it, prefixing it with the plugin name" i.e.
 * SHOW_WHITESPACE = "com.aptana.ide.editor.js.SHOW_WHITESPACE"
 * 
 * @author Ingo Muschenetz
 */
public interface IPreferenceConstants
{
	/**
	 * PREFERENCE_COMMENT_INDENT_USE_STAR
	 */
	String PREFERENCE_COMMENT_INDENT_USE_STAR = "UnifiedEditor.CommentIndentUseStar"; //$NON-NLS-1$

	/**
	 * SCRIPTDOCEDITOR_DELIMITER_COLOR
	 */
	String JSEDITOR_FONT = "com.aptana.ide.editor.js.JSEDITOR_FONT"; //$NON-NLS-1$

	/**
	 * Editor Color Preferences
	 */

	/**
	 * JSEDITOR_PROPOSALS_FOREGROUND_COLOR
	 */
	String JSEDITOR_PROPOSALS_FOREGROUND_COLOR = "com.aptana.ide.editor.js.JSEDITOR_PROPOSALS_FOREGROUND_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_PROPOSALS_BACKGROUND_COLOR
	 */
	String JSEDITOR_PROPOSALS_BACKGROUND_COLOR = "com.aptana.ide.editor.js.JSEDITOR_BACKGROUND_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_DEFAULT_COLOR
	 */
	String JSEDITOR_DEFAULT_COLOR = "com.aptana.ide.editor.js.JSEDITOR_DEFAULT_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_KEYWORD_COLOR
	 */
	String JSEDITOR_KEYWORD_COLOR = "com.aptana.ide.editor.js.JSEDITOR_KEYWORD_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_NATIVETYPE_COLOR
	 */
	String JSEDITOR_NATIVETYPE_COLOR = "com.aptana.ide.editor.js.JSEDITOR_NATIVETYPE_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_PUNCTUATOR_COLOR
	 */
	String JSEDITOR_PUNCTUATOR_COLOR = "com.aptana.ide.editor.js.JSEDITOR_PUNCTUATOR_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_RESERVED_COLOR
	 */
	String JSEDITOR_RESERVED_COLOR = "com.aptana.ide.editor.js.JSEDITOR_RESERVED_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_FUTURE_COLOR
	 */
	String JSEDITOR_FUTURE_COLOR = "com.aptana.ide.editor.js.JSEDITOR_FUTURE_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_STRING_COLOR
	 */
	String JSEDITOR_STRING_COLOR = "com.aptana.ide.editor.js.JSEDITOR_STRING_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_NUMBER_COLOR
	 */
	String JSEDITOR_NUMBER_COLOR = "com.aptana.ide.editor.js.JSEDITOR_NUMBER_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_LITERAL_COLOR
	 */
	String JSEDITOR_LITERAL_COLOR = "com.aptana.ide.editor.js.JSEDITOR_LITERAL_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_COMMENT_COLOR
	 */
	String JSEDITOR_COMMENT_COLOR = "com.aptana.ide.editor.js.JSEDITOR_COMMENT_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_DOCUMENTATION_COLOR
	 */
	String JSEDITOR_DOCUMENTATION_COLOR = "com.aptana.ide.editor.js.JSEDITOR_DOCUMENTATION_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_ERROR_COLOR
	 */
	String JSEDITOR_ERROR_COLOR = "com.aptana.ide.editor.js.JSEDITOR_ERROR_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_HTMLDOM_COLOR
	 */
	String JSEDITOR_HTMLDOM_COLOR = "com.aptana.ide.editor.js.JSEDITOR_HTMLDOM_COLOR"; //$NON-NLS-1$

	/**
	 * JSEDITOR_JSCORE_COLOR
	 */
	String JSEDITOR_JSCORE_COLOR = "com.aptana.ide.editor.js.JSEDITOR_JSCORE_COLOR"; //$NON-NLS-1$

	/**
	 * JSCOMMENTEDITOR_TEXT_COLOR
	 */
	String JSCOMMENTEDITOR_TEXT_COLOR = "com.aptana.ide.editor.js.JSCOMMENTEDITOR_TEXT_COLOR"; //$NON-NLS-1$

	/**
	 * JSCOMMENTEDITOR_DELIMITER_COLOR
	 */
	String JSCOMMENTEDITOR_DELIMITER_COLOR = "com.aptana.ide.editor.js.JSCOMMENTEDITOR_DELIMITER_COLOR"; //$NON-NLS-1$

	/**
	 * SCRIPTDOCEDITOR_TEXT_COLOR
	 */
	String SCRIPTDOCEDITOR_TEXT_COLOR = "com.aptana.ide.editor.js.SCRIPTDOCEDITOR_TEXT_COLOR"; //$NON-NLS-1$

	/**
	 * SCRIPTDOCEDITOR_KEYWORDCOLOR
	 */
	String SCRIPTDOCEDITOR_KEYWORDCOLOR = "com.aptana.ide.editor.js.SCRIPTDOCEDITOR_KEYWORDCOLOR"; //$NON-NLS-1$

	/**
	 * SCRIPTDOCEDITOR_USER_KEYWORD_COLOR
	 */
	String SCRIPTDOCEDITOR_USER_KEYWORD_COLOR = "com.aptana.ide.editor.js.SCRIPTDOCEDITOR_USER_KEYWORD_COLOR"; //$NON-NLS-1$

	/**
	 * SCRIPTDOCEDITOR_IDENTIFIER_COLOR
	 */
	String SCRIPTDOCEDITOR_IDENTIFIER_COLOR = "com.aptana.ide.editor.js.SCRIPTDOCEDITOR_IDENTIFIER_COLOR"; //$NON-NLS-1$

	/**
	 * SCRIPTDOCEDITOR_PUNCTUATOR_COLOR
	 */
	String SCRIPTDOCEDITOR_PUNCTUATOR_COLOR = "com.aptana.ide.editor.js.SCRIPTDOCEDITOR_PUNCTUATOR_COLOR"; //$NON-NLS-1$

	/**
	 * SCRIPTDOCEDITOR_DELIMITER_COLOR
	 */
	String SCRIPTDOCEDITOR_DELIMITER_COLOR = "com.aptana.ide.editor.js.SCRIPTDOCEDITOR_DELIMITER_COLOR"; //$NON-NLS-1$

	/**
	 * PREFERENCE_PRIVATE_FIELD_INDICATOR
	 */
	String PREFERENCE_PRIVATE_FIELD_INDICATOR = "com.aptana.ide.editor.js.PREFERENCE_PRIVATE_FIELD_INDICATOR"; //$NON-NLS-1$

	/**
	 * Editor initial file name
	 */
	String JSEDITOR_INITIAL_FILE_NAME = "com.aptana.ide.editor.js.JSEDITOR_INITIAL_FILE_NAME"; //$NON-NLS-1$

	/**
	 * SDoc Editor initial file name
	 */
	String SCRIPTDOCEDITOR_INITIAL_FILE_NAME = "com.aptana.ide.editor.scriptdoc.SCRIPTDOCEDITOR_INITIAL_FILE_NAME"; //$NON-NLS-1$

	/**
	 * Editor initial file contents
	 */
	String JSEDITOR_INITIAL_CONTENTS = "com.aptana.ide.editor.js.JSEDITOR_INITIAL_CONTENTS"; //$NON-NLS-1$

	/**
	 * AUTO_FORMAT_ON_CLOSE_CURLY
	 */
	String AUTO_FORMAT_ON_CLOSE_CURLY = "com.aptana.ide.editor.js.AUTO_FORMAT_ON_CLOSE_CURLY"; //$NON-NLS-1$
	
	/**
	 * Do we auto-indent on carriage return?
	 */
	String AUTO_INDENT_ON_CARRIAGE_RETURN = "com.aptana.ide.editor.js.AUTO_INDENT_ON_CARRIAGE_RETURN"; //$NON-NLS-1$

	/**
	 * LOADED_ENVIRONMENTS
	 */
	String LOADED_ENVIRONMENTS = "com.aptana.ide.editor.js.LOADED_ENVIRONMENTS"; //$NON-NLS-1$

	/**
	 * DISABLED_ENVIRONMENTS
	 */
	String DISABLED_ENVIRONMENTS = "com.aptana.ide.editor.js.DISABLED_ENVIRONMENTS"; //$NON-NLS-1$;

	/**
	 * ADDED_ENVIRONMENTS
	 */
	String ADDED_ENVIRONMENTS = "com.aptana.ide.editor.js.ADDED_ENVIRONMENTS"; //$NON-NLS-1$;

	/**
	 * LINK_CURSOR_WITH_JS_TOOLBAR_TAB
	 */
	String LINK_CURSOR_WITH_JS_TOOLBAR_TAB = "com.aptana.ide.editor.js.LINK_CURSOR_WITH_JS_TOOLBAR_TAB"; //$NON-NLS-1$

	/**
	 * SHOW_JS_TOOLBAR
	 */
	String SHOW_JS_TOOLBAR = "com.aptana.ide.editor.js.SHOW_JS_TOOLBAR"; //$NON-NLS-1$

	/**
	 * ENABLE_NO_VALIDATE_COMMENT
	 */
	String ENABLE_NO_VALIDATE_COMMENT = "com.aptana.ide.editor.js.ENABLE_NO_VALIDATE_COMMENT"; //$NON-NLS-1$

}
