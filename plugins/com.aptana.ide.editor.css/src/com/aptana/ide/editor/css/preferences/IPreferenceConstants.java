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
package com.aptana.ide.editor.css.preferences;

/**
 * Contains all preferences for the com.aptana.ide.editor.css plugin To add a preference, create a static string with an
 * all-uppercase preference key. Then assign a identically-named string to it, prefixing it with the package name" i.e.
 * SHOW_WHITESPACE = "com.aptana.ide.editor.css.preferences.SHOW_WHITESPACE"
 * 
 * @author Ingo Muschenetz
 */
public interface IPreferenceConstants
{

	/**
	 * Editor Color Preferences
	 */
	String CSSEDITOR_ERROR_COLOR = "com.aptana.ide.editor.css.CSSEDITOR_ERROR_COLOR"; //$NON-NLS-1$

	/**
	 * CSSEDITOR_IDENTIFIER_COLOR
	 */
	String CSSEDITOR_IDENTIFIER_COLOR = "com.aptana.ide.editor.css.CSSEDITOR_IDENTIFIER_COLOR"; //$NON-NLS-1$

	/**
	 * CSSEDITOR_LITERAL_COLOR
	 */
	String CSSEDITOR_LITERAL_COLOR = "com.aptana.ide.editor.css.CSSEDITOR_LITERAL_COLOR"; //$NON-NLS-1$

	/**
	 * CSSEDITOR_KEYWORD_COLOR
	 */
	String CSSEDITOR_KEYWORD_COLOR = "com.aptana.ide.editor.css.CSSEDITOR_KEYWORD_COLOR"; //$NON-NLS-1$

	/**
	 * CSSEDITOR_PUNCTUATOR_COLOR
	 */
	String CSSEDITOR_PUNCTUATOR_COLOR = "com.aptana.ide.editor.css.CSSEDITOR_PUNCTUATOR_COLOR"; //$NON-NLS-1$

	/**
	 * CSSEDITOR_COMMENT_COLOR
	 */
	String CSSEDITOR_COMMENT_COLOR = "com.aptana.ide.editor.css.CSSEDITOR_COMMENT_COLOR"; //$NON-NLS-1$

	/**
	 * CSSEDITOR_HASH_COLOR
	 */
	String CSSEDITOR_HASH_COLOR = "com.aptana.ide.editor.css.CSSEDITOR_HASH_COLOR"; //$NON-NLS-1$

	/**
	 * CSSEDITOR_STRING_COLOR
	 */
	String CSSEDITOR_STRING_COLOR = "com.aptana.ide.editor.css.CSSEDITOR_STRING_COLOR"; //$NON-NLS-1$

	/**
	 * Editor insert colon
	 */
	String CSSEDITOR_INSERT_COLON = "com.aptana.ide.editor.css.CSSEDITOR_INSERT_COLON"; //$NON-NLS-1$

	/**
	 * Editor initial file name
	 */
	String CSSEDITOR_INITIAL_FILE_NAME = "com.aptana.ide.editor.css.CSSEDITOR_INITIAL_FILE_NAME"; //$NON-NLS-1$

	/**
	 * Editor initial file contents
	 */
	String CSSEDITOR_INITIAL_CONTENTS = "com.aptana.ide.editor.css.CSSEDITOR_INITIAL_CONTENTS"; //$NON-NLS-1$

	/**
	 * CSSEDITOR_INSERT_SEMICOLON
	 */
	String CSSEDITOR_INSERT_SEMICOLON = "com.aptana.ide.editor.css.CSSEDITOR_INSERT_SEMICOLON"; //$NON-NLS-1$

	/**
	 * The types of browsers offering preview for the CSS editor
	 */
	String CSSEDITOR_BROWSER_PREVIEW_PREFERENCE = "com.aptana.ide.editor.css.BROWSER_PREVIEW_PREFERENCE"; //$NON-NLS-1$

	/**
	 * The preview url for the CSS editor
	 */
	String CSSEDITOR_BROWSER_URL_PREFERENCE = "com.aptana.ide.editor.css.CSSEDITOR_BROWSER_URL_PREFERENCE"; //$NON-NLS-1$

	/**
	 * The preview template for the CSS editor
	 */
	String CSSEDITOR_BROWSER_TEMPLATE_PREFERENCE = "com.aptana.ide.editor.css.CSSEDITOR_BROWSER_TEMPLATE_PREFERENCE"; //$NON-NLS-1$

	/**
	 * CSSEDITOR_BROWSER_USE_TEMPLATE_PREFERENCE
	 */
	String CSSEDITOR_BROWSER_USE_TEMPLATE_PREFERENCE = "com.aptana.ide.editor.css.CSSEDITOR_BROWSER_USE_TEMPLATE_PREFERENCE"; //$NON-NLS-1$

	/**
	 * LINK_CURSOR_WITH_CSS_TOOLBAR_TAB
	 */
	String LINK_CURSOR_WITH_CSS_TOOLBAR_TAB = "com.aptana.ide.editor.css.LINK_CURSOR_WITH_CSS_TOOLBAR_TAB"; //$NON-NLS-1$

	/**
	 * SHOW_HTML_TOOLBAR
	 */
	String SHOW_CSS_TOOLBAR = "com.aptana.ide.editor.css.SHOW_CSS_TOOLBAR"; //$NON-NLS-1$

	/**
	 * USE_TEMP_FILES_FOR_PREVIEW
	 */
	String USE_TEMP_FILES_FOR_PREVIEW = "com.aptana.ide.editor.css.USE_TEMP_FILES_FOR_PREVIEW"; //$NON-NLS-1$

}