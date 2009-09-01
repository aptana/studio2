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

package com.aptana.ide.logging.coloring;

import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.ITokenList;
import com.aptana.ide.logging.LoggingPlugin;
import com.aptana.ide.logging.LoggingPreferences;


/**
 * Log token types.
 * @author Denis Denisenko
 */
public class TokenTypes
{
    /**
     * Language MIME.
     */
    public static final String LANGUAGE = "text/log"; //$NON-NLS-1$
    
    /**
     * Starting group name.
     */
    public static final String START_GROUP_NAME = "default"; //$NON-NLS-1$
    
    /**
     * Error token type name.
     */
    public static final String ERROR = "SYSTEM_ERROR_LEXEME"; //$NON-NLS-1$

    /**
     * Whitespace token type.
     */
    public static String WHITESPACE = "SYSTE_WHITESPACE_LEXEME"; //$NON-NLS-1$
    
    /**
     * Default category.
     */
    public static String DEFAULT_CATEGORY = "DEFAULT"; //$NON-NLS-1$
    
    /**
     * Number of system tokens. 
     */
//    private static int SYS_TOKENS_NUM = 2;
    private static int SYS_TOKENS_NUM = 1;
    
    /**
     * Unknown token type.
     */
    static String UNKNOWN = "<unknown>"; //$NON-NLS-1$
    
    /**
     * System category.
     */
    static String SYSTEM = "SYSTEM"; //$NON-NLS-1$

    /**
     * Lexer factory.
     */
    private static LoggingLexerFactory lexerFactory;
    
	/**
	 * HTMLTokenTypes
	 */
	protected TokenTypes()
	{
	}

	/**
	 * getNames
	 *
	 * @return String[]
	 */
	public static String[] getNames()
	{
	    List<LoggingPreferences.Rule> rules = LoggingPlugin.getDefault().getLoggingPreferences().getRules();
	    
	    String[] result = new String[rules.size() + SYS_TOKENS_NUM];
	    
	    result[0] = ERROR;
	    result[1] = WHITESPACE;
	    
		for (int i = 0; i < rules.size(); i++)
		{
		    result[i + SYS_TOKENS_NUM] = rules.get(i).getName();
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
	    if (type < SYS_TOKENS_NUM)
	    {
    		switch (type)
    		{
    			case 0:
    				return ERROR;
    				
    			case 1:
                    return WHITESPACE;
    			    				
    			default:
    				return UNKNOWN;
    		}
	    }
	    
	    List<LoggingPreferences.Rule> rules = LoggingPlugin.getDefault().getLoggingPreferences().getRules();
	    if (type - SYS_TOKENS_NUM >= rules.size())
	    {
	        return UNKNOWN;
	    }
	    return rules.get(type - SYS_TOKENS_NUM).getName();
	}
	
	/**
	 * Gets int value of the token type name.
	 * 
	 * @param name - type name.
	 * @return int value
	 */
	public static int getIntValue(String name)
	{
	    if (ERROR.equals(name))
	    {
	        return 0;
	    }
	    else if (WHITESPACE.equals(name))
        {
            return 1;
        }
	    else
	    {
	        List<LoggingPreferences.Rule> rules = LoggingPlugin.getDefault().getLoggingPreferences().getRules();
	        for (int i = 0; i < rules.size(); i++)
	        {
	            LoggingPreferences.Rule rule = rules.get(i);
	            if (rule.getName().equals(name))
	            {
	                return i + SYS_TOKENS_NUM;
	            }
	        }
	    }
	    
	    return -1;
	}

    /**
     * Checks whether type is regexp type.
     * @param type - type to check.
     * @return true pif regexp type, false otherwise.
     */
    public static boolean isRegexpType(String type)
    {
        int typeIndex = getIntValue(type);
        return typeIndex >= SYS_TOKENS_NUM;
    }
	
    /**
     * Gets lexer factory.
     * @return lexer factory.
     */
    public static LoggingLexerFactory getLexerFactory()
    {
        if (lexerFactory == null)
        {
            lexerFactory = new LoggingLexerFactory();
        }
        
        return lexerFactory;
    }
    
    /**
     * Gets token list.
     * @return token list.
     */
    public static ITokenList getTokenList()
    {
        return getLexerFactory().getLexer().getTokenList(LANGUAGE);
    }
    
    /**
     * Gets the lsit of custom (regexp or string) tokens.
     * @return token list.
     */
    public static List<IToken> getRegexpTokenList()
    {
        ITokenList lst = getLexerFactory().getLexer().getTokenList(LANGUAGE);
        List<IToken> result = new ArrayList<IToken>();
        for (int i = 0; i < lst.size(); i++)
        {
            IToken token = lst.get(i);
            if (!token.getCategory().equals(SYSTEM))
            {
                result.add(token);
            }
        }
        
        return result;
    }
}
