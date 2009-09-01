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

/**
 * Contains all preferences for the com.aptana.ide.editor.xml plugin To add a preference, create a static string with an
 * all-uppercase preference key. Then assign a identically-named string to it, prefixing it with the plugin name" i.e.
 * SHOW_WHITESPACE = "com.aptana.ide.editor.xml.SHOW_WHITESPACE"
 * 
 * @author Ingo Muschenetz
 */
public interface IPreferenceConstants
{

	/**
	 * The character we use in "quoting" attributes. Either "" (no quotes), ", or '
	 */
	String ATTRIBUTE_QUOTE_CHARACTER = "com.aptana.ide.editor.xml.ATTRIBUTE_QUOTE_CHARACTER"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_OUTLINER_ATTRIBUTE_LIST
	 */
	String XMLEDITOR_OUTLINER_ATTRIBUTE_LIST = "com.aptana.ide.editor.xml.XMLEDITOR_OUTLINER_ATTRIBUTE_LIST"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_ERROR_COLOR
	 */
	String XMLEDITOR_ERROR_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_ERROR_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_LITERAL_COLOR
	 */
	String XMLEDITOR_LITERAL_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_LITERAL_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_KEYWORD_COLOR
	 */
	String XMLEDITOR_KEYWORD_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_KEYWORD_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_PUNCTUATOR_COLOR
	 */
	String XMLEDITOR_PUNCTUATOR_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_PUNCTUATOR_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_PI_OPEN_CLOSE_COLOR
	 */
	String XMLEDITOR_PI_OPEN_CLOSE_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_PI_OPEN_CLOSE_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_PI_TEXT_COLOR
	 */
	String XMLEDITOR_PI_TEXT_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_PI_TEXT_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_TAG_OPEN_CLOSE_COLOR
	 */
	String XMLEDITOR_TAG_OPEN_CLOSE_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_TAG_OPEN_CLOSE_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_NAME_COLOR
	 */
	String XMLEDITOR_NAME_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_NAME_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_ATTRIBUTE_COLOR
	 */
	String XMLEDITOR_ATTRIBUTE_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_ATTRIBUTE_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_ATTRIBUTE_VALUE_COLOR
	 */
	String XMLEDITOR_ATTRIBUTE_VALUE_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_ATTRIBUTE_VALUE_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_EQUAL_COLOR
	 */
	String XMLEDITOR_EQUAL_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_EQUAL_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_TEXT_COLOR
	 */
	String XMLEDITOR_TEXT_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_TEXT_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_ENTITY_REF_COLOR
	 */
	String XMLEDITOR_ENTITY_REF_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_ENTITY_REF_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_CHAR_REF_COLOR
	 */
	String XMLEDITOR_CHAR_REF_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_CHAR_REF_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_PE_REF_COLOR
	 */
	String XMLEDITOR_PE_REF_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_PE_REF_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_CDATA_START_END_COLOR
	 */
	String XMLEDITOR_CDATA_START_END_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_CDATA_START_END_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_CDATA_TEXT_COLOR
	 */
	String XMLEDITOR_CDATA_TEXT_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_CDATA_TEXT_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_DECL_START_END_COLOR
	 */
	String XMLEDITOR_DECL_START_END_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_DECL_START_END_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_COMMENT_COLOR
	 */
	String XMLEDITOR_COMMENT_COLOR = "com.aptana.ide.editor.xml.XMLEDITOR_COMMENT_COLOR"; //$NON-NLS-1$

	/**
	 * XMLEDITOR_HIGHLIGHT_START_END_TAGS
	 */
	String XMLEDITOR_HIGHLIGHT_START_END_TAGS = "com.aptana.ide.editor.xml.XMLEDITOR_HIGHLIGHT_START_END_TAGS"; //$NON-NLS-1$

	/**
	 * Editor initial file name
	 */
	String XMLEDITOR_INITIAL_FILE_NAME = "com.aptana.ide.editor.xml.XMLEDITOR_INITIAL_FILE_NAME"; //$NON-NLS-1$

	/**
	 * Editor initial file contents
	 */
	String XMLEDITOR_INITIAL_CONTENTS = "com.aptana.ide.editor.html.XMLEDITOR_INITIAL_CONTENTS"; //$NON-NLS-1$

	/**
	 * Do we auto-indent on carriage return?
	 */
	String AUTO_INDENT_ON_CARRIAGE_RETURN = "com.aptana.ide.editor.html.AUTO_INDENT_ON_CARRIAGE_RETURN"; //$NON-NLS-1$

	/**
	 * Do we auto-modify pair tag?
	 */
	String AUTO_MODIFY_PAIR_TAG = "com.aptana.ide.editor.html.AUTO_MODIFY_PAIR_TAG"; //$NON-NLS-1$

	/**
	 * do we auto insert close tags
	 */
	String AUTO_INSERT_CLOSE_TAGS = "com.aptana.ide.editor.xml.AUTO_INSERT_CLOSE_TAGS"; //$NON-NLS-1$

	/**
	 * SHOW_XML_TOOLBAR
	 */
	String SHOW_XML_TOOLBAR = "com.aptana.ide.editor.xml.SHOW_HTML_TOOLBAR"; //$NON-NLS-1$

	/**
	 * HTMLEDITOR_INSERT_EQUALS
	 */
	String HTMLEDITOR_INSERT_EQUALS = "com.aptana.ide.editor.html.HTMLEDITOR_INSERT_EQUALS"; //$NON-NLS-1$

}
