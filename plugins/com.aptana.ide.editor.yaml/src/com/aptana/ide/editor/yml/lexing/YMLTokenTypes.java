/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL youelect, is prohibited.
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
package com.aptana.ide.editor.yml.lexing;

import java.lang.reflect.Field;

/**
 * YML Token Types
 * 
 * @author Kevin Sawicki
 */
public class YMLTokenTypes
{

	/**
	 * YMLTokenTypes
	 */
	protected YMLTokenTypes()
	{
	}

	/**
	 * ERROR
	 */
	public static final int ERROR = 0;

	/**
	 * WHITESPACE
	 */
	public static final int WHITESPACE = 1;

	/**
	 * COMMENT
	 */
	public static final int COMMENT = 2;

	/**
	 * IDENTIFIER
	 */
	public static final int IDENTIFIER = 3;

	/**
	 * STRING
	 */
	public static final int STRING = 4;

	/**
	 * COLON
	 */
	public static final int COLON = 5;

	/**
	 * LCURLY
	 */
	public static final int LCURLY = 6;

	/**
	 * RCURLY
	 */
	public static final int RCURLY = 7;

	/**
	 * LBRACKET
	 */
	public static final int LBRACKET = 8;

	/**
	 * RBRACKET
	 */
	public static final int RBRACKET = 9;

	/**
	 * COMMA
	 */
	public static final int COMMA = 10;

	/**
	 * PLUS
	 */
	public static final int PLUS = 11;

	/**
	 * MINUS
	 */
	public static final int MINUS = 12;

	/**
	 * QUESTION_MARK
	 */
	public static final int QUESTION_MARK = 13;

	/**
	 * START_DOCUMENT
	 */
	public static final int START_DOCUMENT = 14;

	/**
	 * SEQUENCE
	 */
	public static final int SEQUENCE = 15;

	/**
	 * MAPPING
	 */
	public static final int MAPPING = 16;

	/**
	 * ROOT
	 */
	public static final int ROOT = 17;

	/**
	 * INDENT
	 */
	public static final int INDENT = 18;

	/**
	 * MAX_VALUE
	 */
	public static final int MAX_VALUE = 18;

	/**
	 * getNames
	 * 
	 * @return String[]
	 */
	public static String[] getNames()
	{
		String[] result = new String[MAX_VALUE + 1];

		for (int i = 0; i <= MAX_VALUE; i++)
		{
			result[i] = getName(i);
		}

		return result;
	}

	/**
	 * Get the name associated with the specified token type
	 * 
	 * @param type
	 *            The token type
	 * @return The name associated with this token type
	 */
	public static String getName(int type)
	{
		switch (type)
		{
			case ERROR:
				return "ERROR"; //$NON-NLS-1$

			case WHITESPACE:
				return "WHITESPACE"; //$NON-NLS-1$

			case COMMENT:
				return "COMMENT"; //$NON-NLS-1$

			case IDENTIFIER:
				return "IDENTIFIER"; //$NON-NLS-1$

			case STRING:
				return "STRING"; //$NON-NLS-1$

			case COLON:
				return "COLON"; //$NON-NLS-1$

			case LCURLY:
				return "LCURLY"; //$NON-NLS-1$

			case RCURLY:
				return "RCURLY"; //$NON-NLS-1$

			case LBRACKET:
				return "LBRACKET"; //$NON-NLS-1$

			case RBRACKET:
				return "RBRACKET"; //$NON-NLS-1$

			case COMMA:
				return "COMMA"; //$NON-NLS-1$

			case PLUS:
				return "PLUS"; //$NON-NLS-1$

			case MINUS:
				return "MINUS"; //$NON-NLS-1$

			default:
				return "<unknown>"; //$NON-NLS-1$
		}
	}

	/**
	 * getIntValue
	 * 
	 * @param name
	 * @return int
	 */
	public static int getIntValue(String name)
	{
		Class c = YMLTokenTypes.class;
		int result = -1;

		try
		{
			Field f = c.getField(name);

			result = f.getInt(c);
		}
		// fail silently
		catch (SecurityException e)
		{
		}
		catch (NoSuchFieldException e)
		{
		}
		catch (IllegalArgumentException e)
		{
		}
		catch (IllegalAccessException e)
		{
		}

		return result;
	}

}
