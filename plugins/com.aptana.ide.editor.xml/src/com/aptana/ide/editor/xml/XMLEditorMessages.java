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
package com.aptana.ide.editor.xml;

import org.eclipse.osgi.util.NLS;

/**
 * XMLEditorMessages
 */
public final class XMLEditorMessages
{

	private static final String BUNDLE_NAME = XMLEditorMessages.class.getName();

	private XMLEditorMessages()
	{
		// Do not instantiate
	}

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, XMLEditorMessages.class);
	}

	/**
	 * XMLEDITOR_ERROR_COLOR
	 */
	public static String XMLEDITOR_ERROR_COLOR;

	/**
	 * XMLEDITOR_LITERAL_COLOR
	 */
	public static String XMLEDITOR_LITERAL_COLOR;

	/**
	 * XMLEDITOR_KEYWORD_COLOR
	 */
	public static String XMLEDITOR_KEYWORD_COLOR;

	/**
	 * XMLEDITOR_PUNCTUATOR_COLOR
	 */
	public static String XMLEDITOR_PUNCTUATOR_COLOR;

	/**
	 * XMLEDITOR_PI_OPEN_CLOSE_COLOR
	 */
	public static String XMLEDITOR_PI_OPEN_CLOSE_COLOR;

	/**
	 * XMLEDITOR_PI_TEXT_COLOR
	 */
	public static String XMLEDITOR_PI_TEXT_COLOR;

	/**
	 * XMLEDITOR_TAG_OPEN_CLOSE_COLOR
	 */
	public static String XMLEDITOR_TAG_OPEN_CLOSE_COLOR;

	// public static String XMLEDITOR_COMMENT_OPEN_CLOSE_COLOR;

	/**
	 * XMLEDITOR_NAME_COLOR
	 */
	public static String XMLEDITOR_NAME_COLOR;

	/**
	 * XMLEDITOR_ATTRIBUTE_COLOR
	 */
	public static String XMLEDITOR_ATTRIBUTE_COLOR;

	/**
	 * XMLEDITOR_ATTRIBUTE_VALUE_COLOR
	 */
	public static String XMLEDITOR_ATTRIBUTE_VALUE_COLOR;

	/**
	 * XMLEDITOR_EQUAL_COLOR
	 */
	public static String XMLEDITOR_EQUAL_COLOR;

	/**
	 * XMLEDITOR_TEXT_COLOR
	 */
	public static String XMLEDITOR_TEXT_COLOR;

	/**
	 * XMLEDITOR_ENTITY_REF_COLOR
	 */
	public static String XMLEDITOR_ENTITY_REF_COLOR;

	/**
	 * XMLEDITOR_CHAR_REF_COLOR
	 */
	public static String XMLEDITOR_CHAR_REF_COLOR;

	/**
	 * XMLEDITOR_PE_REF_COLOR
	 */
	public static String XMLEDITOR_PE_REF_COLOR;

	/**
	 * XMLEDITOR_CDATA_START_END_COLOR
	 */
	public static String XMLEDITOR_CDATA_START_END_COLOR;

	/**
	 * XMLEDITOR_CDATA_TEXT_COLOR
	 */
	public static String XMLEDITOR_CDATA_TEXT_COLOR;

	/**
	 * XMLEDITOR_DECL_START_END_COLOR
	 */
	public static String XMLEDITOR_DECL_START_END_COLOR;

	/**
	 * XMLEDITOR_COMMENT_COLOR
	 */
	public static String XMLEDITOR_COMMENT_COLOR;
}
