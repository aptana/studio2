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

/**
 * Contains all preferences for the com.aptana.ide.editor.html plugin To add a preference, create a static string with
 * an all-uppercase preference key. Then assign a identically-named string to it, prefixing it with the plugin name"
 * i.e. SHOW_WHITESPACE = "com.aptana.ide.editor.html.SHOW_WHITESPACE"
 * 
 * @author Ingo Muschenetz
 */
public interface IPreferenceConstants
{

	/**
	 * AUTO_SAVE_PROMPTED
	 */
	String AUTO_SAVE_PROMPTED = "com.aptana.ide.editor.html.AUTO_SAVE_PROMPTED"; //$NON-NLS-1$

	/**
	 * AUTO_SAVE_BEFORE_PREVIEWING
	 */
	String AUTO_SAVE_BEFORE_PREVIEWING = "com.aptana.ide.editor.html.AUTO_SAVE_BEFORE_PREVIEWING"; //$NON-NLS-1$

	/**
	 * P_CHOICE
	 */
	String HTML_EDITOR_VIEW_CHOICE = "com.aptana.ide.editor.html.HTML_EDITOR_VIEW_CHOICE"; //$NON-NLS-1$

	/**
	 * Do we auto-insert the closing tags?
	 */
	String AUTO_INSERT_CLOSE_TAGS = "com.aptana.ide.editor.html.AUTO_INSERT_CLOSE_TAGS"; //$NON-NLS-1$

	/**
	 * Do we auto-complete the closing tags when typing?
	 */
	String AUTO_COMPLETE_CLOSE_TAGS = "com.aptana.ide.editor.html.AUTO_COMPLETE_CLOSE_TAGS"; //$NON-NLS-1$

	/**
	 * Do we auto-indent on carriage return?
	 */
	String AUTO_INDENT_ON_CARRIAGE_RETURN = "com.aptana.ide.editor.html.AUTO_INDENT_ON_CARRIAGE_RETURN"; //$NON-NLS-1$

	/**
	 * The character we use in "quoting" attributes. Either "" (no quotes), ", or '
	 */
	String ATTRIBUTE_QUOTE_CHARACTER = "com.aptana.ide.editor.html.ATTRIBUTE_QUOTE_CHARACTER"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_OUTLINER_ATTRIBUTE_LIST
	 */
	String HTMLEDITOR_OUTLINER_ATTRIBUTE_LIST = "com.aptana.ide.editor.html.HTMLEDITOR_OUTLINER_ATTRIBUTE_LIST"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_INSERT_EQUALS
	 */
	String HTMLEDITOR_INSERT_EQUALS = "com.aptana.ide.editor.html.HTMLEDITOR_INSERT_EQUALS"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_ERROR_COLOR
	 */

	String HTMLEDITOR_ERROR_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_ERROR_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_LITERAL_COLOR
	 */
	String HTMLEDITOR_LITERAL_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_LITERAL_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_KEYWORD_COLOR
	 */
	String HTMLEDITOR_KEYWORD_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_KEYWORD_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_PUNCTUATOR_COLOR
	 */
	String HTMLEDITOR_PUNCTUATOR_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_PUNCTUATOR_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_PI_OPEN_CLOSE_COLOR
	 */
	String HTMLEDITOR_PI_OPEN_CLOSE_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_PI_OPEN_CLOSE_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_PI_TEXT_COLOR
	 */
	String HTMLEDITOR_PI_TEXT_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_PI_TEXT_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_TAG_OPEN_CLOSE_COLOR
	 */
	String HTMLEDITOR_TAG_OPEN_CLOSE_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_TAG_OPEN_CLOSE_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_COMMENT_OPEN_CLOSE_COLOR
	 */
	String HTMLEDITOR_COMMENT_OPEN_CLOSE_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_COMMENT_OPEN_CLOSE_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_NAME_COLOR
	 */
	String HTMLEDITOR_NAME_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_NAME_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_ATTRIBUTE_COLOR
	 */
	String HTMLEDITOR_ATTRIBUTE_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_ATTRIBUTE_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_ATTRIBUTE_VALUE_COLOR
	 */
	String HTMLEDITOR_ATTRIBUTE_VALUE_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_ATTRIBUTE_VALUE_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_EQUAL_COLOR
	 */
	String HTMLEDITOR_EQUAL_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_EQUAL_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_TEXT_COLOR
	 */
	String HTMLEDITOR_TEXT_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_TEXT_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_ENTITY_REF_COLOR
	 */
	String HTMLEDITOR_ENTITY_REF_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_ENTITY_REF_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_CHAR_REF_COLOR
	 */
	String HTMLEDITOR_CHAR_REF_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_CHAR_REF_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_PE_REF_COLOR
	 */
	String HTMLEDITOR_PE_REF_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_PE_REF_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_CDATA_START_END_COLOR
	 */
	String HTMLEDITOR_CDATA_START_END_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_CDATA_START_END_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_CDATA_TEXT_COLOR
	 */
	String HTMLEDITOR_CDATA_TEXT_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_CDATA_TEXT_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_DECL_START_END_COLOR
	 */
	String HTMLEDITOR_DECL_START_END_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_DECL_START_END_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_COMMENT_COLOR
	 */
	String HTMLEDITOR_COMMENT_COLOR = "com.aptana.ide.editor.html.HTMLEDITOR_COMMENT_COLOR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_DEFAULT_EXTENSION
	 */
	String HTMLEDITOR_DEFAULT_EXTENSION = "com.aptana.ide.editor.html.HTMLEDITOR_DEFAULT_EXTENSION"; //$NON-NLS-1$

	/**
	 * Editor initial file name
	 */
	String HTMLEDITOR_INITIAL_FILE_NAME = "com.aptana.ide.editor.html.HTMLEDITOR_INITIAL_FILE_NAME"; //$NON-NLS-1$

	/**
	 * Editor initial file contents
	 */
	String HTMLEDITOR_INITIAL_CONTENTS = "com.aptana.ide.editor.html.HTMLEDITOR_INITIAL_CONTENTS"; //$NON-NLS-1$

	/**
	 * The types of browsers offering preview for the HTML editor
	 */
	String HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE = "com.aptana.ide.editor.html.BROWSER_PREVIEW_PREFERENCE"; //$NON-NLS-1$

	/**
	 * The workspace run config setting for html preview
	 */
	String HTMLEDITOR_RUNCONFIG_PREVIEW_PREFERENCE = "com.aptana.ide.editor.html.HTMLEDITOR_RUNCONFIG_PREVIEW_PREFERENCE"; //$NON-NLS-1$

	/**
	 * The weight between two browsers
	 */
	String HTMLEDITOR_TWO_BROWSER_WEIGHT_VERTICAL = "com.aptana.ide.editor.html.TWO_BROWSER_WEIGHT_VERTICAL"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_TWO_BROWSER_WEIGHT_HORIZONTAL
	 */
	String HTMLEDITOR_TWO_BROWSER_WEIGHT_HORIZONTAL = "com.aptana.ide.editor.html.TWO_BROWSER_WEIGHT_HORIZONTAL"; //$NON-NLS-1$

	/**
	 * The weight between the editor and the browser area
	 */
	String HTMLEDITOR_EDITOR_BROWSER_WEIGHT_VERTICAL = "com.aptana.ide.editor.html.EDITOR_BROWSER_WEIGHT_VERTICAL"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_EDITOR_BROWSER_WEIGHT_HORIZONTAL
	 */
	String HTMLEDITOR_EDITOR_BROWSER_WEIGHT_HORIZONTAL = "com.aptana.ide.editor.html.EDITOR_BROWSER_WEIGHT_HORIZONTAL"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_HIGHLIGHT_START_END_TAGS
	 */
	String HTMLEDITOR_HIGHLIGHT_START_END_TAGS = "com.aptana.ide.editor.html.EDITOR_HIGHLIGHT_START_END_TAGS"; //$NON-NLS-1$

	/**
	 * FORMATTING_ENCLOSE_BLOCK_TEXT
	 */
	String FORMATTING_ENCLOSE_BLOCK_TEXT = "com.aptana.ide.editors.FORMATTING_ENCLOSE_BLOCK_TEXT"; //$NON-NLS-1$

	/**
	 * FORMATTING_FIX_COMMENTS
	 */
	String FORMATTING_FIX_COMMENTS = "com.aptana.ide.editors.FORMATTING_FIX_COMMENTS"; //$NON-NLS-1$

	/**
	 * FORMATTING_FIX_BACKSLASH
	 */
	String FORMATTING_FIX_BACKSLASH = "com.aptana.ide.editors.FORMATTING_FIX_BACKSLASH"; //$NON-NLS-1$

	/**
	 * FORMATTING_HIDE_END_TAGS
	 */
	String FORMATTING_HIDE_END_TAGS = "com.aptana.ide.editors.FORMATTING_HIDE_END_TAGS"; //$NON-NLS-1$

	/**
	 * FORMATTING_INDENT_CONTENT
	 */
	String FORMATTING_INDENT_CONTENT = "com.aptana.ide.editors.FORMATTING_INDENT_CONTENT"; //$NON-NLS-1$

	/**
	 * FORMATTING_DROP_FONT_TAGS
	 */
	String FORMATTING_DROP_FONT_TAGS = "com.aptana.ide.editors.FORMATTING_DROP_FONT_TAGS"; //$NON-NLS-1$

	/**
	 * FORMATTING_DROP_EMPTY_PARAS
	 */
	String FORMATTING_DROP_EMPTY_PARAS = "com.aptana.ide.editors.FORMATTING_DROP_EMPTY_PARAS"; //$NON-NLS-1$

	/**
	 * FORMATTING_LITERAL_ATTRIBS
	 */
	String FORMATTING_LITERAL_ATTRIBS = "com.aptana.ide.editors.FORMATTING_LITERAL_ATTRIBS"; //$NON-NLS-1$

	/**
	 * FORMATTING_MAKE_CLEAN
	 */
	String FORMATTING_MAKE_CLEAN = "com.aptana.ide.editors.FORMATTING_MAKE_CLEAN"; //$NON-NLS-1$

	/**
	 * FORMATTING_LOGICAL_EMPHASIS
	 */
	String FORMATTING_LOGICAL_EMPHASIS = "com.aptana.ide.editors.FORMATTING_LOGICAL_EMPHASIS"; //$NON-NLS-1$

	/**
	 * FORMATTING_ENCLOSE_TEXT
	 */
	String FORMATTING_ENCLOSE_TEXT = "com.aptana.ide.editors.FORMATTING_ENCLOSE_TEXT"; //$NON-NLS-1$

	/**
	 * FORMATTING_NUM_ENTITIES
	 */
	String FORMATTING_NUM_ENTITIES = "com.aptana.ide.editors.FORMATTING_NUM_ENTITIES"; //$NON-NLS-1$

	/**
	 * FORMATTING_BREAK_BEFORE_BR
	 */
	String FORMATTING_BREAK_BEFORE_BR = "com.aptana.ide.editors.FORMATTING_BREAK_BEFORE_BR"; //$NON-NLS-1$

	/**
	 * FORMATTING_QUOTE_AMPERSAND
	 */
	String FORMATTING_QUOTE_AMPERSAND = "com.aptana.ide.editors.FORMATTING_QUOTE_AMPERSAND"; //$NON-NLS-1$

	/**
	 * FORMATTING_INDENT_ATTRIBUTES
	 */
	String FORMATTING_INDENT_ATTRIBUTES = "com.aptana.ide.editors.FORMATTING_INDENT_ATTRIBUTES"; //$NON-NLS-1$

	/**
	 * FORMATTING_QUOTE_NBSP
	 */
	String FORMATTING_QUOTE_NBSP = "com.aptana.ide.editors.FORMATTING_QUOTE_NBSP"; //$NON-NLS-1$

	/**
	 * FORMATTING_SMART_INDENT
	 */
	String FORMATTING_SMART_INDENT = "com.aptana.ide.editors.FORMATTING_SMART_INDENT"; //$NON-NLS-1$

	/**
	 * FORMATTING_UPPER_CASE_ATTRS
	 */
	String FORMATTING_UPPER_CASE_ATTRS = "com.aptana.ide.editors.FORMATTING_UPPER_CASE_ATTRS"; //$NON-NLS-1$

	/**
	 * FORMATTING_WORD_2000
	 */
	String FORMATTING_WORD_2000 = "com.aptana.ide.editors.FORMATTING_WORD_2000"; //$NON-NLS-1$

	/**
	 * FORMATTING_UPPER_CASE_TAGS
	 */
	String FORMATTING_UPPER_CASE_TAGS = "com.aptana.ide.editors.FORMATTING_UPPER_CASE_TAGS"; //$NON-NLS-1$

	/**
	 * FORMATTING_WRAP_ASP
	 */
	String FORMATTING_WRAP_ASP = "com.aptana.ide.editors.FORMATTING_WRAP_ASP"; //$NON-NLS-1$

	/**
	 * FORMATTING_WRAP_ATTR_VALUES
	 */
	String FORMATTING_WRAP_ATTR_VALUES = "com.aptana.ide.editors.FORMATTING_WRAP_ATTR_VALUES"; //$NON-NLS-1$

	/**
	 * FORMATTING_WRAP_JSTE
	 */
	String FORMATTING_WRAP_JSTE = "com.aptana.ide.editors.FORMATTING_WRAP_JSTE"; //$NON-NLS-1$

	/**
	 * FORMATTING_RAW_OUT
	 */
	String FORMATTING_RAW_OUT = "com.aptana.ide.editors.FORMATTING_RAW_OUT"; //$NON-NLS-1$

	/**
	 * FORMATTING_QUOTE_MARKS
	 */
	String FORMATTING_QUOTE_MARKS = "com.aptana.ide.editors.FORMATTING_QUOTE_MARKS"; //$NON-NLS-1$

	/**
	 * FORMATTING_WRAP_SCRIPTLETS
	 */
	String FORMATTING_WRAP_SCRIPTLETS = "com.aptana.ide.editors.FORMATTING_WRAP_SCRIPTLETS"; //$NON-NLS-1$

	/**
	 * FORMATTING_SPACES
	 */
	String FORMATTING_SPACES = "com.aptana.ide.editors.FORMATTING_SPACES"; //$NON-NLS-1$

	/**
	 * FORMATTING_WRAP_SECTION
	 */
	String FORMATTING_WRAP_SECTION = "com.aptana.ide.editors.FORMATTING_WRAP_SECTION"; //$NON-NLS-1$

	/**
	 * FORMATTING_WRAP_PHP
	 */
	String FORMATTING_WRAP_PHP = "com.aptana.ide.editors.FORMATTING_WRAP_PHP"; //$NON-NLS-1$

	/**
	 * FORMATTING_SET_XML
	 */
	String FORMATTING_SET_XML = "com.aptana.ide.editors.FORMATTING_SET_XML"; //$NON-NLS-1$

	/**
	 * FORMATTING_ALT_TEXT
	 */
	String FORMATTING_ALT_TEXT = "com.aptana.ide.editors.FORMATTING_ALT_TEXT"; //$NON-NLS-1$

	/**
	 * FORMATTING_SET_XHTML
	 */
	String FORMATTING_SET_XHTML = "com.aptana.ide.editors.FORMATTING_SET_XHTML"; //$NON-NLS-1$

	/**
	 * FORMATTING_TAB_SIZE
	 */
	String FORMATTING_TAB_SIZE = "com.aptana.ide.editors.FORMATTING_TAB_SIZE"; //$NON-NLS-1$

	/**
	 * FORMATTING_WRAP_MARGIN
	 */
	String FORMATTING_WRAP_MARGIN = "com.aptana.ide.editors.FORMATTING_WRAP_MARGIN"; //$NON-NLS-1$

	/**
	 * FORMATTING_DOCTYPE
	 */
	String FORMATTING_DOCTYPE = "com.aptana.ide.editors.FORMATTING_DOCTYPE"; //$NON-NLS-1$

	/**
	 * FORMATTING_SET_OUTPUT
	 */
	String FORMATTING_SET_OUTPUT = "com.aptana.ide.editors.FORMATTING_SET_OUTPUT"; //$NON-NLS-1$

	/**
	 * FORMATTING_SPACES_SIZE
	 */
	String FORMATTING_SPACES_SIZE = "com.aptana.ide.editors.FORMATTING_SPACES_SIZE"; //$NON-NLS-1$

	/**
	 * FOLDING_HTML_NODE_LIST
	 */
	String FOLDING_HTML_NODE_LIST = "com.aptana.ide.editors.FOLDING_HTML_NODE_LIST"; //$NON-NLS-1$

	/**
	 * Do we auto-modify pair tag?
	 */
	String AUTO_MODIFY_PAIR_TAG = "com.aptana.ide.editor.html.AUTO_MODIFY_PAIR_TAG"; //$NON-NLS-1$

	/**
	 * LINK_CURSOR_WITH_HTML_TOOLBAR_TAB
	 */
	String LINK_CURSOR_WITH_HTML_TOOLBAR_TAB = "com.aptana.ide.editor.html.LINK_CURSOR_WITH_HTML_TOOLBAR_TAB"; //$NON-NLS-1$

	/**
	 * SHOW_HTML_TOOLBAR
	 */
	String SHOW_HTML_TOOLBAR = "com.aptana.ide.editor.html.SHOW_HTML_TOOLBAR"; //$NON-NLS-1$

	/**
	 * USE_TEMP_FILES_FOR_PREVIEW
	 */
	String USE_TEMP_FILES_FOR_PREVIEW = "com.aptana.ide.editor.html.USE_TEMP_FILES_FOR_PREVIEW"; //$NON-NLS-1$

	/**
	 * LOADED_ENVIRONMENTS
	 */
	String LOADED_ENVIRONMENTS = "com.aptana.ide.editor.html.LOADED_ENVIRONMENTS"; //$NON-NLS-1$
}
