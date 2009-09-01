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
package com.aptana.ide.editor.css.parsing.nodes;

import java.lang.reflect.Field;

/**
 * @author Kevin Lindsey
 */
public class CSSParseNodeTypes
{
	/**
	 * CSSParseNodeTypes
	 */
	protected CSSParseNodeTypes()
	{
	}
	
	/**
	 * UNKNOWN
	 */
	public static final int UNKNOWN = 0;

	/**
	 * AT_RULE
	 */
	public static final int AT_RULE = 1;

	/**
	 * BLOCK
	 */
	public static final int BLOCK = 2;

	/**
	 * CHAR_SET
	 */
	public static final int CHAR_SET = 3;

	/**
	 * DECLARATION
	 */
	public static final int DECLARATION = 4;

	/**
	 * IMPORT
	 */
	public static final int IMPORT = 5;

	/**
	 * RULE_SET
	 */
	public static final int RULE_SET = 6;
	
	/**
	 * MEDIA
	 */
	public static final int MEDIA = 7;
	
	/**
	 * MEDIUM
	 */
	public static final int MEDIUM = 8;
	
	/**
	 * PAGE
	 */
	public static final int PAGE = 9;
	
	/**
	 * SELECTOR
	 */
	public static final int SELECTOR = 10;
	
	/**
	 * SIMPLE_SELECTOR
	 */
	public static final int SIMPLE_SELECTOR = 11;
	
	/**
	 * EXPR
	 */
	public static final int EXPR = 12;
	
	/**
	 * TERM
	 */
	public static final int TERM = 13;
	
	/**
	 * TEXT
	 */
	public static final int TEXT = 14;
	
	/**
	 * EMPTY
	 */
	public static final int EMPTY = 15;
	
	/**
	 * LIST
	 */
	public static final int LIST = 16;
	
	/**
	 * MAX_VALUE
	 */
	public static final int MAX_VALUE = 16;

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
			case UNKNOWN:
				return "UNKNOWN"; //$NON-NLS-1$
			case AT_RULE:
				return "AT_RULE"; //$NON-NLS-1$
			case BLOCK:
				return "BLOCK"; //$NON-NLS-1$
			case CHAR_SET:
				return "CHAR_SET"; //$NON-NLS-1$
			case DECLARATION:
				return "DECLARATION"; //$NON-NLS-1$
			case IMPORT:
				return "IMPORT"; //$NON-NLS-1$
			case RULE_SET:
				return "RULE_SET"; //$NON-NLS-1$
			case MEDIA:
				return "MEDIA"; //$NON-NLS-1$
			case MEDIUM:
				return "MEDIUM"; //$NON-NLS-1$
			case PAGE:
				return "PAGE"; //$NON-NLS-1$
			case SELECTOR:
				return "SELECTOR"; //$NON-NLS-1$
			case SIMPLE_SELECTOR:
				return "SIMPLE_SELECTOR"; //$NON-NLS-1$
			case EXPR:
				return "EXPR"; //$NON-NLS-1$
			case TERM:
				return "TERM"; //$NON-NLS-1$
			case TEXT:
				return "TEXT"; //$NON-NLS-1$
			case EMPTY:
				return "EMPTY"; //$NON-NLS-1$
			case LIST:
				return "LIST"; //$NON-NLS-1$
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
		Class<CSSParseNodeTypes> c = CSSParseNodeTypes.class;
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
