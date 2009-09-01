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

package com.aptana.ide.editor.js.lexing;

import java.lang.reflect.Field;

/**
 * @author Kevin Lindsey
 */
public class JSTokenTypes
{
	/**
	 * JSTokenTypes
	 */
	protected JSTokenTypes()
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
	 * CDO
	 */
	public static final int CDO = 3;

	/**
	 * CDC
	 */
	public static final int CDC = 4;

	/**
	 * LINE_TERMINATOR
	 */
	public static final int LINE_TERMINATOR = 5;

	/**
	 * START_MULTILINE_COMMENT
	 */
	public static final int START_MULTILINE_COMMENT = 6;

	/**
	 * START_DOCUMENTATION
	 */
	public static final int START_DOCUMENTATION = 7;

	/**
	 * IDENTIFIER
	 */
	public static final int IDENTIFIER = 8;

	/**
	 * BREAK
	 */
	public static final int BREAK = 9;

	/**
	 * CASE
	 */
	public static final int CASE = 10;

	/**
	 * CATCH
	 */
	public static final int CATCH = 11;

	/**
	 * CONTINUE
	 */
	public static final int CONTINUE = 12;

	/**
	 * DEFAULT
	 */
	public static final int DEFAULT = 13;

	/**
	 * DELETE
	 */
	public static final int DELETE = 14;

	/**
	 * DO
	 */
	public static final int DO = 15;

	/**
	 * ELSE
	 */
	public static final int ELSE = 16;

	/**
	 * IF
	 */
	public static final int IF = 17;

	/**
	 * IN
	 */
	public static final int IN = 18;

	/**
	 * INSTANCEOF
	 */
	public static final int INSTANCEOF = 19;

	/**
	 * FINALLY
	 */
	public static final int FINALLY = 20;

	/**
	 * FOR
	 */
	public static final int FOR = 21;

	/**
	 * FUNCTION
	 */
	public static final int FUNCTION = 22;

	/**
	 * NEW
	 */
	public static final int NEW = 23;

	/**
	 * RETURN
	 */
	public static final int RETURN = 24;

	/**
	 * SWITCH
	 */
	public static final int SWITCH = 25;

	/**
	 * THIS
	 */
	public static final int THIS = 26;

	/**
	 * THROW
	 */
	public static final int THROW = 27;

	/**
	 * TRY
	 */
	public static final int TRY = 28;

	/**
	 * TYPEOF
	 */
	public static final int TYPEOF = 29;

	/**
	 * VAR
	 */
	public static final int VAR = 30;

	/**
	 * VOID
	 */
	public static final int VOID = 31;

	/**
	 * WHILE
	 */
	public static final int WHILE = 32;

	/**
	 * WITH
	 */
	public static final int WITH = 33;

	/**
	 * LCURLY
	 */
	public static final int LCURLY = 34;

	/**
	 * DOT
	 */
	public static final int DOT = 35;

	/**
	 * GREATER_EQUAL
	 */
	public static final int GREATER_EQUAL = 36;

	/**
	 * PLUS
	 */
	public static final int PLUS = 37;

	/**
	 * LESS_LESS
	 */
	public static final int LESS_LESS = 38;

	/**
	 * EXCLAMATION
	 */
	public static final int EXCLAMATION = 39;

	/**
	 * EQUAL
	 */
	public static final int EQUAL = 40;

	/**
	 * GREATER_GREATER_EQUAL
	 */
	public static final int GREATER_GREATER_EQUAL = 41;

	/**
	 * RCURLY
	 */
	public static final int RCURLY = 42;

	/**
	 * SEMICOLON
	 */
	public static final int SEMICOLON = 43;

	/**
	 * EQUAL_EQUAL
	 */
	public static final int EQUAL_EQUAL = 44;

	/**
	 * MINUS
	 */
	public static final int MINUS = 45;

	/**
	 * GREATER_GREATER
	 */
	public static final int GREATER_GREATER = 46;

	/**
	 * TILDE
	 */
	public static final int TILDE = 47;

	/**
	 * PLUS_EQUAL
	 */
	public static final int PLUS_EQUAL = 48;

	/**
	 * GREATER_GREATER_GREATER_EQUAL
	 */
	public static final int GREATER_GREATER_GREATER_EQUAL = 49;

	/**
	 * LPAREN
	 */
	public static final int LPAREN = 50;

	/**
	 * COMMA
	 */
	public static final int COMMA = 51;

	/**
	 * EXCLAMATION_EQUAL
	 */
	public static final int EXCLAMATION_EQUAL = 52;

	/**
	 * STAR
	 */
	public static final int STAR = 53;

	/**
	 * GREATER_GREATER_GREATER
	 */
	public static final int GREATER_GREATER_GREATER = 54;

	/**
	 * AMPERSAND_AMPERSAND
	 */
	public static final int AMPERSAND_AMPERSAND = 55;

	/**
	 * MINUS_EQUAL
	 */
	public static final int MINUS_EQUAL = 56;

	/**
	 * AMPERSAND_EQUAL
	 */
	public static final int AMPERSAND_EQUAL = 57;

	/**
	 * RPAREN
	 */
	public static final int RPAREN = 58;

	/**
	 * LESS
	 */
	public static final int LESS = 59;

	/**
	 * EQUAL_EQUAL_EQUAL
	 */
	public static final int EQUAL_EQUAL_EQUAL = 60;

	/**
	 * PERCENT
	 */
	public static final int PERCENT = 61;

	/**
	 * AMPERSAND
	 */
	public static final int AMPERSAND = 62;

	/**
	 * PIPE_PIPE
	 */
	public static final int PIPE_PIPE = 63;

	/**
	 * STAR_EQUAL
	 */
	public static final int STAR_EQUAL = 64;

	/**
	 * PIPE_EQUAL
	 */
	public static final int PIPE_EQUAL = 65;

	/**
	 * LBRACKET
	 */
	public static final int LBRACKET = 66;

	/**
	 * GREATER
	 */
	public static final int GREATER = 67;

	/**
	 * EXCLAMATION_EQUAL_EQUAL
	 */
	public static final int EXCLAMATION_EQUAL_EQUAL = 68;

	/**
	 * PLUS_PLUS
	 */
	public static final int PLUS_PLUS = 69;

	/**
	 * PIPE
	 */
	public static final int PIPE = 70;

	/**
	 * QUESTION
	 */
	public static final int QUESTION = 71;

	/**
	 * PERCENT_EQUAL
	 */
	public static final int PERCENT_EQUAL = 72;

	/**
	 * CARET_EQUAL
	 */
	public static final int CARET_EQUAL = 73;

	/**
	 * RBRACKET
	 */
	public static final int RBRACKET = 74;

	/**
	 * LESS_EQUAL
	 */
	public static final int LESS_EQUAL = 75;

	/**
	 * MINUS_MINUS
	 */
	public static final int MINUS_MINUS = 76;

	/**
	 * CARET
	 */
	public static final int CARET = 77;

	/**
	 * COLON
	 */
	public static final int COLON = 78;

	/**
	 * LESS_LESS_EQUAL
	 */
	public static final int LESS_LESS_EQUAL = 79;

	/**
	 * FORWARD_SLASH
	 */
	public static final int FORWARD_SLASH = 80;

	/**
	 * FORWARD_SLASH_EQUAL
	 */
	public static final int FORWARD_SLASH_EQUAL = 81;

	/**
	 * PI_OPEN
	 */
	public static final int PI_OPEN = 82;

	/**
	 * PI_TEXT
	 */
	public static final int PI_TEXT = 83;
	
	/**
	 * PI_CLOSE
	 */
	public static final int PI_CLOSE = 92;

	/**
	 * NULL
	 */
	public static final int NULL = 84;

	/**
	 * TRUE
	 */
	public static final int TRUE = 85;

	/**
	 * FALSE
	 */
	public static final int FALSE = 86;

	/**
	 * NUMBER
	 */
	public static final int NUMBER = 87;

	/**
	 * STRING
	 */
	public static final int STRING = 88;

	/**
	 * REGEX
	 */
	public static final int REGEX = 89;

	/**
	 * MULTILINE_COMMENT
	 */
	public static final int MULTILINE_COMMENT = 90;

	/**
	 * DOCUMENTATION
	 */
	public static final int DOCUMENTATION = 91;

	/**
	 * MAX_VALUE
	 */
	public static final int MAX_VALUE = 92;

//	/**
//	 * Get the name associated with the specified token type
//	 * 
//	 * @param type
//	 *            The token type
//	 * @return The name associated with this token type
//	 */
//	public static String getName(int type)
//	{
//		Class c = JSTokenTypes.class;
//		String result = "<unknown>";
//		
//		Field[] fields = c.getFields();
//		
//		if (0 <= type && type < fields.length)
//		{
//			result = fields[type].getName();
//		}
//		
//		return result;
//	}
	
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

			case CDO:
				return "CDO"; //$NON-NLS-1$

			case CDC:
				return "CDC"; //$NON-NLS-1$

			case LINE_TERMINATOR:
				return "LINE_TERMINATOR"; //$NON-NLS-1$

			case START_MULTILINE_COMMENT:
				return "START_MULTILINE_COMMENT"; //$NON-NLS-1$

			case START_DOCUMENTATION:
				return "START_DOCUMENTATION"; //$NON-NLS-1$

			case IDENTIFIER:
				return "IDENTIFIER"; //$NON-NLS-1$

			case BREAK:
				return "BREAK"; //$NON-NLS-1$

			case CASE:
				return "CASE"; //$NON-NLS-1$

			case CATCH:
				return "CATCH"; //$NON-NLS-1$

			case CONTINUE:
				return "CONTINUE"; //$NON-NLS-1$

			case DEFAULT:
				return "DEFAULT"; //$NON-NLS-1$

			case DELETE:
				return "DELETE"; //$NON-NLS-1$

			case DO:
				return "DO"; //$NON-NLS-1$

			case ELSE:
				return "ELSE"; //$NON-NLS-1$

			case IF:
				return "IF"; //$NON-NLS-1$

			case IN:
				return "IN"; //$NON-NLS-1$

			case INSTANCEOF:
				return "INSTANCEOF"; //$NON-NLS-1$

			case FINALLY:
				return "FINALLY"; //$NON-NLS-1$

			case FOR:
				return "FOR"; //$NON-NLS-1$

			case FUNCTION:
				return "FUNCTION"; //$NON-NLS-1$

			case NEW:
				return "NEW"; //$NON-NLS-1$

			case RETURN:
				return "RETURN"; //$NON-NLS-1$

			case SWITCH:
				return "SWITCH"; //$NON-NLS-1$

			case THIS:
				return "THIS"; //$NON-NLS-1$

			case THROW:
				return "THROW"; //$NON-NLS-1$

			case TRY:
				return "TRY"; //$NON-NLS-1$

			case TYPEOF:
				return "TYPEOF"; //$NON-NLS-1$

			case VAR:
				return "VAR"; //$NON-NLS-1$

			case VOID:
				return "VOID"; //$NON-NLS-1$

			case WHILE:
				return "WHILE"; //$NON-NLS-1$

			case WITH:
				return "WITH"; //$NON-NLS-1$

			case LCURLY:
				return "LCURLY"; //$NON-NLS-1$

			case DOT:
				return "DOT"; //$NON-NLS-1$

			case GREATER_EQUAL:
				return "GREATER_EQUAL"; //$NON-NLS-1$

			case PLUS:
				return "PLUS"; //$NON-NLS-1$

			case LESS_LESS:
				return "LESS_LESS"; //$NON-NLS-1$

			case EXCLAMATION:
				return "EXCLAMATION"; //$NON-NLS-1$

			case EQUAL:
				return "EQUAL"; //$NON-NLS-1$

			case GREATER_GREATER_EQUAL:
				return "GREATER_GREATER_EQUAL"; //$NON-NLS-1$

			case RCURLY:
				return "RCURLY"; //$NON-NLS-1$

			case SEMICOLON:
				return "SEMICOLON"; //$NON-NLS-1$

			case EQUAL_EQUAL:
				return "EQUAL_EQUAL"; //$NON-NLS-1$

			case MINUS:
				return "MINUS"; //$NON-NLS-1$

			case GREATER_GREATER:
				return "GREATER_GREATER"; //$NON-NLS-1$

			case TILDE:
				return "TILDE"; //$NON-NLS-1$

			case PLUS_EQUAL:
				return "PLUS_EQUAL"; //$NON-NLS-1$

			case GREATER_GREATER_GREATER_EQUAL:
				return "GREATER_GREATER_GREATER_EQUAL"; //$NON-NLS-1$

			case LPAREN:
				return "LPAREN"; //$NON-NLS-1$

			case COMMA:
				return "COMMA"; //$NON-NLS-1$

			case EXCLAMATION_EQUAL:
				return "EXCLAMATION_EQUAL"; //$NON-NLS-1$

			case STAR:
				return "STAR"; //$NON-NLS-1$

			case GREATER_GREATER_GREATER:
				return "GREATER_GREATER_GREATER"; //$NON-NLS-1$

			case AMPERSAND_AMPERSAND:
				return "AMPERSAND_AMPERSAND"; //$NON-NLS-1$

			case MINUS_EQUAL:
				return "MINUS_EQUAL"; //$NON-NLS-1$

			case AMPERSAND_EQUAL:
				return "AMPERSAND_EQUAL"; //$NON-NLS-1$

			case RPAREN:
				return "RPAREN"; //$NON-NLS-1$

			case LESS:
				return "LESS"; //$NON-NLS-1$

			case EQUAL_EQUAL_EQUAL:
				return "EQUAL_EQUAL_EQUAL"; //$NON-NLS-1$

			case PERCENT:
				return "PERCENT"; //$NON-NLS-1$

			case AMPERSAND:
				return "AMPERSAND"; //$NON-NLS-1$

			case PIPE_PIPE:
				return "PIPE_PIPE"; //$NON-NLS-1$

			case STAR_EQUAL:
				return "STAR_EQUAL"; //$NON-NLS-1$

			case PIPE_EQUAL:
				return "PIPE_EQUAL"; //$NON-NLS-1$

			case LBRACKET:
				return "LBRACKET"; //$NON-NLS-1$

			case GREATER:
				return "GREATER"; //$NON-NLS-1$

			case EXCLAMATION_EQUAL_EQUAL:
				return "EXCLAMATION_EQUAL_EQUAL"; //$NON-NLS-1$

			case PLUS_PLUS:
				return "PLUS_PLUS"; //$NON-NLS-1$

			case PIPE:
				return "PIPE"; //$NON-NLS-1$

			case QUESTION:
				return "QUESTION"; //$NON-NLS-1$

			case PERCENT_EQUAL:
				return "PERCENT_EQUAL"; //$NON-NLS-1$

			case CARET_EQUAL:
				return "CARET_EQUAL"; //$NON-NLS-1$

			case RBRACKET:
				return "RBRACKET"; //$NON-NLS-1$

			case LESS_EQUAL:
				return "LESS_EQUAL"; //$NON-NLS-1$

			case MINUS_MINUS:
				return "MINUS_MINUS"; //$NON-NLS-1$

			case CARET:
				return "CARET"; //$NON-NLS-1$

			case COLON:
				return "COLON"; //$NON-NLS-1$

			case LESS_LESS_EQUAL:
				return "LESS_LESS_EQUAL"; //$NON-NLS-1$

			case FORWARD_SLASH:
				return "FORWARD_SLASH"; //$NON-NLS-1$

			case FORWARD_SLASH_EQUAL:
				return "FORWARD_SLASH_EQUAL"; //$NON-NLS-1$

			case PI_OPEN:
				return "PI_OPEN"; //$NON-NLS-1$
			case PI_CLOSE:
				return "PI_CLOSE"; //$NON-NLS-1$
				
			case PI_TEXT:
				return "PI_TEXT"; //$NON-NLS-1$

			case NULL:
				return "NULL"; //$NON-NLS-1$

			case TRUE:
				return "TRUE"; //$NON-NLS-1$

			case FALSE:
				return "FALSE"; //$NON-NLS-1$

			case NUMBER:
				return "NUMBER"; //$NON-NLS-1$

			case STRING:
				return "STRING"; //$NON-NLS-1$

			case REGEX:
				return "REGEX"; //$NON-NLS-1$

			case MULTILINE_COMMENT:
				return "MULTILINE_COMMENT"; //$NON-NLS-1$

			case DOCUMENTATION:
				return "DOCUMENTATION"; //$NON-NLS-1$

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
		Class<?> c = JSTokenTypes.class;
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
