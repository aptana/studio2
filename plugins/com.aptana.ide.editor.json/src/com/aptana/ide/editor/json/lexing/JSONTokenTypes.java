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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.editor.json.lexing;

import java.lang.reflect.Field;

/**
 * @author Kevin Lindsey
 */
public class JSONTokenTypes
{
	protected JSONTokenTypes()
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
	 * FALSE
	 */
	public static final int FALSE = 3;
	
	/**
	 * TRUE
	 */
	public static final int TRUE = 4;
	
	/**
	 * NUMBER
	 */
	public static final int NUMBER = 5;
	
	/**
	 * REFERENCE
	 */
	public static final int REFERENCE = 6;
	
	/**
	 * STRING
	 */
	public static final int STRING = 7;
	
	/**
	 * PROPERTY
	 */
	public static final int PROPERTY = 8;
	
	/**
	 * LBRACKET
	 */
	public static final int LBRACKET = 9;
	
	/**
	 * RBRACKET
	 */
	public static final int RBRACKET = 10;

	/**
	 * LCURLY
	 */
	public static final int LCURLY = 11;
	
	/**
	 * RCURLY
	 */
	public static final int RCURLY = 12;
	
	/**
	 * COLON
	 */
	public static final int COLON = 13;
	
	/**
	 * COMMA
	 */
	public static final int COMMA = 14;
	
	/**
	 * NULL
	 */
	public static final int NULL = 15;
	
	/**
	 * MAX_VALUE
	 */
	public static final int MAX_VALUE = 15;
	
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
			
			case FALSE:
				return "FALSE"; //$NON-NLS-1$
			
			case TRUE:
				return "TRUE"; //$NON-NLS-1$
			
			case NUMBER:
				return "NUMBER"; //$NON-NLS-1$
			
			case REFERENCE:
				return "REFERENCE"; //$NON-NLS-1$
			
			case STRING:
				return "STRING"; //$NON-NLS-1$
			
			case PROPERTY:
				return "PROPERTY"; //$NON-NLS-1$
			
			case LBRACKET:
				return "LBRACKET"; //$NON-NLS-1$
			
			case RBRACKET:
				return "RBRACKET"; //$NON-NLS-1$
			
			case LCURLY:
				return "LCURLY"; //$NON-NLS-1$
			
			case RCURLY:
				return "RCURLY"; //$NON-NLS-1$
			
			case COLON:
				return "COLON"; //$NON-NLS-1$
			
			case COMMA:
				return "COMMA"; //$NON-NLS-1$
			
			case NULL:
				return "NULL"; //$NON-NLS-1$
				
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
		Class c = JSONTokenTypes.class;
		int result = -1;

		try
		{
			Field f = c.getField(name);

			result = f.getInt(c);
		}
		catch (Exception e)
		{
			// fail silently
		}

		return result;
	}
}
