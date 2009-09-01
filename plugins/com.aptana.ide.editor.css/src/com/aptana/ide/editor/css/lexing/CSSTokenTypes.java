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
package com.aptana.ide.editor.css.lexing;

import java.lang.reflect.Field;

/**
 * @author Kevin Lindsey
 */
public class CSSTokenTypes
{
    /**
     * CSSTokenTypes
     */
    protected CSSTokenTypes()
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
     * START_MULTILINE_COMMENT
     */
    public static final int START_MULTILINE_COMMENT = 2;
    
    /**
     * COMMENT
     */
    public static final int COMMENT = 3;
    
    /**
     * IDENTIFIER
     */
    public static final int IDENTIFIER = 4;
    
    /**
     * STRING
     */
    public static final int STRING = 5;
    
    /**
     * NUMBER
     */
    public static final int NUMBER = 6;
    
    /**
     * AT_KEYWORD
     */
    public static final int AT_KEYWORD = 7;
    
    /**
     * CLASS
     */
    public static final int CLASS = 8;
    
    /**
     * HASH
     */
    public static final int HASH = 9;
    
    /**
     * FUNCTION
     */
    public static final int FUNCTION = 10;
    
    /**
     * DIMENSION
     */
    public static final int DIMENSION = 11;
    
    /**
     * PERCENTAGE
     */
    public static final int PERCENTAGE = 12;
    
    /**
     * EMS
     */
    public static final int EMS = 13;
    
    /**
     * EXS
     */
    public static final int EXS = 14;
    
    /**
     * LENGTH
     */
    public static final int LENGTH = 15;
    
    /**
     * ANGLE
     */
    public static final int ANGLE = 16;
    
    /**
     * TIME
     */
    public static final int TIME = 17;
    
    /**
     * FREQUENCY
     */
    public static final int FREQUENCY = 18;
    
    /**
     * UNICODE_RANGE
     */
    public static final int UNICODE_RANGE = 19;
    
    /**
     * COLOR
     */
    public static final int COLOR = 20;
    
    /**
     * IMPORT
     */
    public static final int IMPORT = 21;
    
    /**
     * PAGE
     */
    public static final int PAGE = 22;
    
    /**
     * MEDIA
     */
    public static final int MEDIA = 23;
    
    /**
     * CHARSET
     */
    public static final int CHARSET = 24;
    
    /**
     * URL
     */
    public static final int URL = 25;
    
    /**
     * IMPORTANT
     */
    public static final int IMPORTANT = 26;
    
    /**
     * CDO
     */
    public static final int CDO = 27;
    
    /**
     * CDC
     */
    public static final int CDC = 28;
    
    /**
     * COLON
     */
    public static final int COLON = 29;
    
    /**
     * SEMICOLON
     */
    public static final int SEMICOLON = 30;
    
    /**
     * LCURLY
     */
    public static final int LCURLY = 31;
    
    /**
     * RCURLY
     */
    public static final int RCURLY = 32;
    
    /**
     * RPAREN
     */
    public static final int RPAREN = 33;
    
    /**
     * LBRACKET
     */
    public static final int LBRACKET = 34;
    
    /**
     * RBRACKET
     */
    public static final int RBRACKET = 35;
    
    /**
     * INCLUDES
     */
    public static final int INCLUDES = 36;
    
    /**
     * DASHMATCH
     */
    public static final int DASHMATCH = 37;
    
    /**
     * COMMA
     */
    public static final int COMMA = 38;
    
    /**
     * PLUS
     */
    public static final int PLUS = 39;
    
    /**
     * MINUS
     */
    public static final int MINUS = 40;
    
    /**
     * STAR
     */
    public static final int STAR = 41;
    
    /**
     * CARET_EQUAL
     */
    public static final int CARET_EQUAL = 42;
    
    /**
     * GREATER
     */
    public static final int GREATER = 43;
    
    /**
     * FORWARD_SLASH
     */
    public static final int FORWARD_SLASH = 44;
    
    /**
     * EQUAL
     */
    public static final int EQUAL = 45;
    
    /**
     * MULTILINE_COMMENT
     */
    public static final int MULTILINE_COMMENT = 46;
    
    /**
     * PROPERTY
     */
    public static final int PROPERTY = 47;
    
    /**
     * SELECTOR
     */
    public static final int SELECTOR = 48;
    
    /**
     * MAX_VALUE
     */
    public static final int MAX_VALUE = 48;

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
     * @param type The token type
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
                
            case START_MULTILINE_COMMENT:
                return "START_MULTILINE_COMMENT"; //$NON-NLS-1$
                
            case COMMENT:
                return "COMMENT"; //$NON-NLS-1$
                
            case IDENTIFIER:
                return "IDENTIFIER"; //$NON-NLS-1$
                
            case STRING:
                return "STRING"; //$NON-NLS-1$
                
            case NUMBER:
                return "NUMBER"; //$NON-NLS-1$
                
            case AT_KEYWORD:
                return "AT_KEYWORD"; //$NON-NLS-1$
                
            case CLASS:
                return "CLASS"; //$NON-NLS-1$
                
            case HASH:
                return "HASH"; //$NON-NLS-1$
                
            case FUNCTION:
                return "FUNCTION"; //$NON-NLS-1$
                
            case DIMENSION:
                return "DIMENSION"; //$NON-NLS-1$
                
            case PERCENTAGE:
                return "PERCENTAGE"; //$NON-NLS-1$
                
            case EMS:
                return "EMS"; //$NON-NLS-1$
                
            case EXS:
                return "EXS"; //$NON-NLS-1$
                
            case LENGTH:
                return "LENGTH"; //$NON-NLS-1$
                
            case ANGLE:
                return "ANGLE"; //$NON-NLS-1$
                
            case TIME:
                return "TIME"; //$NON-NLS-1$
                
            case FREQUENCY:
                return "FREQUENCY"; //$NON-NLS-1$
                
            case UNICODE_RANGE:
                return "UNICODE_RANGE"; //$NON-NLS-1$
                
            case COLOR:
                return "COLOR"; //$NON-NLS-1$
                
            case IMPORT:
                return "IMPORT"; //$NON-NLS-1$
                
            case PAGE:
                return "PAGE"; //$NON-NLS-1$
                
            case MEDIA:
                return "MEDIA"; //$NON-NLS-1$
                
            case CHARSET:
                return "CHARSET"; //$NON-NLS-1$
                
            case URL:
                return "URL"; //$NON-NLS-1$
                
            case IMPORTANT:
                return "IMPORTANT"; //$NON-NLS-1$
                
            case CDO:
                return "CDO"; //$NON-NLS-1$
                
            case CDC:
                return "CDC"; //$NON-NLS-1$
                
            case COLON:
                return "COLON"; //$NON-NLS-1$
                
            case SEMICOLON:
                return "SEMICOLON"; //$NON-NLS-1$
                
            case LCURLY:
                return "LCURLY"; //$NON-NLS-1$
                
            case RCURLY:
                return "RCURLY"; //$NON-NLS-1$
                
            case RPAREN:
                return "RPAREN"; //$NON-NLS-1$
                
            case LBRACKET:
                return "LBRACKET"; //$NON-NLS-1$
                
            case RBRACKET:
                return "RBRACKET"; //$NON-NLS-1$
                
            case INCLUDES:
                return "INCLUDES"; //$NON-NLS-1$
                
            case DASHMATCH:
                return "DASHMATCH"; //$NON-NLS-1$
                
            case COMMA:
                return "COMMA"; //$NON-NLS-1$
                
            case PLUS:
                return "PLUS"; //$NON-NLS-1$
                
            case MINUS:
                return "MINUS"; //$NON-NLS-1$
                
            case STAR:
                return "STAR"; //$NON-NLS-1$
                
            case CARET_EQUAL:
                return "CARET_EQUAL"; //$NON-NLS-1$
                
            case GREATER:
                return "GREATER"; //$NON-NLS-1$
                
            case FORWARD_SLASH:
                return "FORWARD_SLASH"; //$NON-NLS-1$
                
            case EQUAL:
                return "EQUAL"; //$NON-NLS-1$
                
            case MULTILINE_COMMENT:
                return "MULTILINE_COMMENT"; //$NON-NLS-1$
                
            case PROPERTY:
            	return "PROPERTY"; //$NON-NLS-1$
            	
            case SELECTOR:
            	return "SELECTOR"; //$NON-NLS-1$
                
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
		Class c = CSSTokenTypes.class;
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
