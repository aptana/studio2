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
package com.aptana.ide.editor.text;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 *
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.text.messages"; //$NON-NLS-1$
	
	/**
	 * GenericTextEditor_COLORIZE_AS
	 */
	public static String GenericTextEditor_COLORIZE_AS;
	
	/**
	 * GenericTextEditor_CREATE_COLORIZER_FAILED
	 */
	public static String GenericTextEditor_CREATE_COLORIZER_FAILED;
	
	/**
	 * GenericTextEditor_ERROR
	 */
	public static String GenericTextEditor_ERROR;
	
	/**
	 * GenericTextEditor_ERROR_LOADING_COLORIZATION
	 */
	public static String GenericTextEditor_ERROR_LOADING_COLORIZATION;
	
	/**
	 * GenericTextEditor_ERROR_LOADING_GRAMMR
	 */
	public static String GenericTextEditor_ERROR_LOADING_GRAMMR;
	
	/**
	 * GenericTextEditor_ERROR_OCCURED_DURING_PARSE_COLORIZER
	 */
	public static String GenericTextEditor_ERROR_OCCURED_DURING_PARSE_COLORIZER;
	
	/**
	 * GenericTextEditor_ERROR_OCCURED_DURING_PARSE_LEXER
	 */
	public static String GenericTextEditor_ERROR_OCCURED_DURING_PARSE_LEXER;
	
	/**
	 * GenericTextEditor_ERROR_PARSING_COLORIZATION
	 */
	public static String GenericTextEditor_ERROR_PARSING_COLORIZATION;
	
	/**
	 * GenericTextEditor_ERROR_PARSING_LEXER
	 */
	public static String GenericTextEditor_ERROR_PARSING_LEXER;
	
	/**
	 * GenericTextEditor_ERROR_RETRIEVING_ASSOCIATION
	 */
	public static String GenericTextEditor_ERROR_RETRIEVING_ASSOCIATION;
	
	/**
	 * GenericTextEditor_MONITOR_GRAMMAR_FILE
	 */
	public static String GenericTextEditor_MONITOR_GRAMMAR_FILE;

	/**
	 * GenericTextEditor_No_Language_Defined
	 */
	public static String GenericTextEditor_No_Language_Defined;
	
	/**
	 * GenericTextEditor_REFRESHING_COLORS
	 */
	public static String GenericTextEditor_REFRESHING_COLORS;
	
	/**
	 * GenericTextEditor_REFRESHING_LEXER
	 */
	public static String GenericTextEditor_REFRESHING_LEXER;
	
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
