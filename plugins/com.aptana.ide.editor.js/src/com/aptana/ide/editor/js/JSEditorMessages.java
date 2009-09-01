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
package com.aptana.ide.editor.js;

import org.eclipse.osgi.util.NLS;

/**
 * JSEditorMessages
 */
public final class JSEditorMessages
{
	private static final String BUNDLE_NAME = JSEditorMessages.class.getName();

	private JSEditorMessages()
	{
		// Do not instantiate
	}

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, JSEditorMessages.class);
	}

	/**
	 * JSEDITOR_PROPOSALS_FOREGROUND_COLOR
	 */
	public static String JSEDITOR_PROPOSALS_FOREGROUND_COLOR;

	/**
	 * JSEDITOR_PROPOSALS_BACKGROUND_COLOR
	 */
	public static String JSEDITOR_PROPOSALS_BACKGROUND_COLOR;

	/**
	 * JSEDITOR_DEFAULT_COLOR
	 */
	public static String JSEDITOR_DEFAULT_COLOR;

	/**
	 * JSEDITOR_KEYWORD_COLOR
	 */
	public static String JSEDITOR_KEYWORD_COLOR;

	/**
	 * JSEDITOR_NATIVETYPE_COLOR
	 */
	public static String JSEDITOR_NATIVETYPE_COLOR;

	/**
	 * JSEDITOR_PUNCTUATOR_COLOR
	 */
	public static String JSEDITOR_PUNCTUATOR_COLOR;

	/**
	 * JSEDITOR_RESERVED_COLOR
	 */
	public static String JSEDITOR_RESERVED_COLOR;

	/**
	 * JSEDITOR_FUTURE_COLOR
	 */
	public static String JSEDITOR_FUTURE_COLOR;

	/**
	 * JSEDITOR_STRING_COLOR
	 */
	public static String JSEDITOR_STRING_COLOR;

	/**
	 * JSEDITOR_NUMBER_COLOR
	 */
	public static String JSEDITOR_NUMBER_COLOR;

	/**
	 * JSEDITOR_LITERAL_COLOR
	 */
	public static String JSEDITOR_LITERAL_COLOR;

	/**
	 * JSEDITOR_COMMENT_COLOR
	 */
	public static String JSEDITOR_COMMENT_COLOR;

	/**
	 * JSEDITOR_DOCUMENTATION_COLOR
	 */
	public static String JSEDITOR_DOCUMENTATION_COLOR;

	/**
	 * JSEDITOR_ERROR_COLOR
	 */
	public static String JSEDITOR_ERROR_COLOR;

	/**
	 * JSEDITOR_HTMLDOM_COLOR
	 */
	public static String JSEDITOR_HTMLDOM_COLOR;

	/**
	 * JSEDITOR_JSCORE_COLOR
	 */
	public static String JSEDITOR_JSCORE_COLOR;

	/**
	 * JSCOMMENTEDITOR_TEXT_COLOR
	 */
	public static String JSCOMMENTEDITOR_TEXT_COLOR;

	/**
	 * JSCOMMENTEDITOR_DELIMITER_COLOR
	 */
	public static String JSCOMMENTEDITOR_DELIMITER_COLOR;

	/**
	 * SCRIPTDOCEDITOR_TEXT_COLOR
	 */
	public static String SCRIPTDOCEDITOR_TEXT_COLOR;

	/**
	 * SCRIPTDOCEDITOR_KEYWORDCOLOR
	 */
	public static String SCRIPTDOCEDITOR_KEYWORDCOLOR;

	/**
	 * SCRIPTDOCEDITOR_USER_KEYWORD_COLOR
	 */
	public static String SCRIPTDOCEDITOR_USER_KEYWORD_COLOR;

	/**
	 * SCRIPTDOCEDITOR_IDENTIFIER_COLOR
	 */
	public static String SCRIPTDOCEDITOR_IDENTIFIER_COLOR;

	/**
	 * SCRIPTDOCEDITOR_PUNCTUATOR_COLOR
	 */
	public static String SCRIPTDOCEDITOR_PUNCTUATOR_COLOR;

	/**
	 * SCRIPTDOCEDITOR_DELIMITER_COLOR
	 */
	public static String SCRIPTDOCEDITOR_DELIMITER_COLOR;
}
