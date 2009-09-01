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
package com.aptana.ide.editor.js.parsing.nodes;

import java.lang.reflect.Field;

/**
 * @author Kevin Lindsey
 */
public class JSParseNodeTypes
{
	/**
	 * JSParseNodeTypes
	 */
	protected JSParseNodeTypes()
	{
	}
	
	/**
	 * ERROR
	 */
	public static final int ERROR = -1;

	/**
	 * UNKNOWN
	 */
	public static final int UNKNOWN = 0;

	/**
	 * ASSIGN
	 */
	public static final int ASSIGN = 1;

	/**
	 * ADD_AND_ASSIGN
	 */
	public static final int ADD_AND_ASSIGN = 2;

	/**
	 * ARITHMETIC_SHIFT_LEFT_AND_ASSIGN
	 */
	public static final int ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN = 3;

	/**
	 * BITWISE_AND_AND_ASSIGN
	 */
	public static final int BITWISE_AND_AND_ASSIGN = 4;

	/**
	 * BITWISE_OR_AND_ASSIGN
	 */
	public static final int BITWISE_OR_AND_ASSIGN = 5;

	/**
	 * BITWISE_XOR_AND_ASSIGN
	 */
	public static final int BITWISE_XOR_AND_ASSIGN = 6;

	/**
	 * DIVIDE_AND_ASSIGN
	 */
	public static final int DIVIDE_AND_ASSIGN = 7;

	/**
	 * MOD_AND_ASSIGN
	 */
	public static final int MOD_AND_ASSIGN = 8;

	/**
	 * MULTIPLY_AND_ASSIGN
	 */
	public static final int MULTIPLY_AND_ASSIGN = 9;

	/**
	 * SHIFT_LEFT_AND_ASSIGN
	 */
	public static final int SHIFT_LEFT_AND_ASSIGN = 10;

	/**
	 * SHIFT_RIGHT_AND_ASSIGN
	 */
	public static final int SHIFT_RIGHT_AND_ASSIGN = 11;

	/**
	 * SUBTRACT_AND_ASSIGN
	 */
	public static final int SUBTRACT_AND_ASSIGN = 12;

	/**
	 * GET_ELEMENT - square bracket notation
	 */
	public static final int GET_ELEMENT = 13;

	/**
	 * GET_PROPERTY - dotted notation
	 */
	public static final int GET_PROPERTY = 14;

	/**
	 * EQUAL
	 */
	public static final int EQUAL = 15;

	/**
	 * GREATER_THAN
	 */
	public static final int GREATER_THAN = 16;

	/**
	 * GREATER_THAN_OR_EQUAL
	 */
	public static final int GREATER_THAN_OR_EQUAL = 17;

	/**
	 * IDENTITY
	 */
	public static final int IDENTITY = 18;

	/**
	 * IN
	 */
	public static final int IN = 19;

	/**
	 * INSTANCE_OF
	 */
	public static final int INSTANCE_OF = 20;

	/**
	 * LESS_THAN
	 */
	public static final int LESS_THAN = 21;

	/**
	 * LESS_THAN_OR_EQUAL
	 */
	public static final int LESS_THAN_OR_EQUAL = 22;

	/**
	 * LOGICAL_AND
	 */
	public static final int LOGICAL_AND = 23;

	/**
	 * LOGICAL_OR
	 */
	public static final int LOGICAL_OR = 24;

	/**
	 * NOT_EQUAL
	 */
	public static final int NOT_EQUAL = 25;

	/**
	 * NOT_IDENTITY
	 */
	public static final int NOT_IDENTITY = 26;

	/**
	 * ADD
	 */
	public static final int ADD = 27;

	/**
	 * ARITHMETIC_SHIFT_RIGHT
	 */
	public static final int ARITHMETIC_SHIFT_RIGHT = 28;

	/**
	 * BITWISE_AND
	 */
	public static final int BITWISE_AND = 29;

	/**
	 * BITWISE_OR
	 */
	public static final int BITWISE_OR = 30;

	/**
	 * BITWISE_XOR
	 */
	public static final int BITWISE_XOR = 31;

	/**
	 * DIVIDE
	 */
	public static final int DIVIDE = 32;

	/**
	 * MOD
	 */
	public static final int MOD = 33;

	/**
	 * MULTIPLY
	 */
	public static final int MULTIPLY = 34;

	/**
	 * SHIFT_LEFT
	 */
	public static final int SHIFT_LEFT = 35;

	/**
	 * SHIFT_RIGHT
	 */
	public static final int SHIFT_RIGHT = 36;

	/**
	 * SUBTRACT
	 */
	public static final int SUBTRACT = 37;

	/**
	 * CATCH
	 */
	public static final int CATCH = 38;

	/**
	 * CONDITIONAL
	 */
	public static final int CONDITIONAL = 39;

	/**
	 * CONSTRUCT
	 */
	public static final int CONSTRUCT = 40;

	/**
	 * DECLARATION
	 */
	public static final int DECLARATION = 41;

	/**
	 * DO
	 */
	public static final int DO = 42;

	/**
	 * EMPTY
	 */
	public static final int EMPTY = 43;

	/**
	 * FINALLY
	 */
	public static final int FINALLY = 44;

	/**
	 * FOR_IN
	 */
	public static final int FOR_IN = 45;

	/**
	 * FOR
	 */
	public static final int FOR = 46;

	/**
	 * FUNCTION
	 */
	public static final int FUNCTION = 47;

	/**
	 * IF
	 */
	public static final int IF = 48;

	/**
	 * INVOKE
	 */
	public static final int INVOKE = 49;

	/**
	 * LABELLED
	 */
	public static final int LABELLED = 50;

	/**
	 * BREAK
	 */
	public static final int BREAK = 51;

	/**
	 * CONTINUE
	 */
	public static final int CONTINUE = 52;

	/**
	 * ARGUMENTS
	 */
	public static final int ARGUMENTS = 53;

	/**
	 * ARRAY_LITERAL
	 */
	public static final int ARRAY_LITERAL = 54;

	/**
	 * COMMA
	 */
	public static final int COMMA = 55;

	/**
	 * DEFAULT
	 */
	public static final int DEFAULT = 56;

	/**
	 * CASE
	 */
	public static final int CASE = 57;

	/**
	 * SWITCH
	 */
	public static final int SWITCH = 58;

	/**
	 * OBJECT_LITERAL
	 */
	public static final int OBJECT_LITERAL = 59;

	/**
	 * PARAMETERS
	 */
	public static final int PARAMETERS = 60;

	/**
	 * STATEMENTS
	 */
	public static final int STATEMENTS = 61;

	/**
	 * VAR
	 */
	public static final int VAR = 62;

	/**
	 * FALSE
	 */
	public static final int FALSE = 63;

	/**
	 * IDENTIFIER
	 */
	public static final int IDENTIFIER = 64;

	/**
	 * NULL
	 */
	public static final int NULL = 65;

	/**
	 * NUMBER
	 */
	public static final int NUMBER = 66;

	/**
	 * REGULAR_EXPRESSION
	 */
	public static final int REGULAR_EXPRESSION = 67;

	/**
	 * STRING
	 */
	public static final int STRING = 68;

	/**
	 * TRUE
	 */
	public static final int TRUE = 69;

	/**
	 * NAME_VALUE_PAIR
	 */
	public static final int NAME_VALUE_PAIR = 70;

	/**
	 * THIS
	 */
	public static final int THIS = 71;

	/**
	 * TRY
	 */
	public static final int TRY = 72;

	/**
	 * DELETE
	 */
	public static final int DELETE = 73;

	/**
	 * GROUP
	 */
	public static final int GROUP = 74;

	/**
	 * LOGICAL_NOT
	 */
	public static final int LOGICAL_NOT = 75;

	/**
	 * BITWISE_NOT
	 */
	public static final int BITWISE_NOT = 76;

	/**
	 * NEGATE
	 */
	public static final int NEGATE = 77;

	/**
	 * POSITIVE
	 */
	public static final int POSITIVE = 78;

	/**
	 * POST_DECREMENT
	 */
	public static final int POST_DECREMENT = 79;

	/**
	 * POST_INCREMENT
	 */
	public static final int POST_INCREMENT = 80;

	/**
	 * PRE_DECREMENT
	 */
	public static final int PRE_DECREMENT = 81;

	/**
	 * PRE_INCREMENT
	 */
	public static final int PRE_INCREMENT = 82;

	/**
	 * RETURN
	 */
	public static final int RETURN = 83;

	/**
	 * THROW
	 */
	public static final int THROW = 84;

	/**
	 * TYPEOF
	 */
	public static final int TYPEOF = 85;

	/**
	 * VOID
	 */
	public static final int VOID = 86;

	/**
	 * WHILE
	 */
	public static final int WHILE = 87;

	/**
	 * WITH
	 */
	public static final int WITH = 88;

	/**
	 * MAX_VALUE
	 */
	public static final int MAX_VALUE = 88;

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
			case UNKNOWN:
				return "UNKNOWN"; //$NON-NLS-1$
			case ASSIGN:
				return "ASSIGN"; //$NON-NLS-1$
			case ADD_AND_ASSIGN:
				return "ADD_AND_ASSIGN"; //$NON-NLS-1$
			case ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN:
				return "ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN"; //$NON-NLS-1$
			case BITWISE_AND_AND_ASSIGN:
				return "BITWISE_AND_AND_ASSIGN"; //$NON-NLS-1$
			case BITWISE_OR_AND_ASSIGN:
				return "BITWISE_OR_AND_ASSIGN"; //$NON-NLS-1$
			case BITWISE_XOR_AND_ASSIGN:
				return "BITWISE_XOR_AND_ASSIGN"; //$NON-NLS-1$
			case DIVIDE_AND_ASSIGN:
				return "DIVIDE_AND_ASSIGN"; //$NON-NLS-1$
			case MOD_AND_ASSIGN:
				return "MOD_AND_ASSIGN"; //$NON-NLS-1$
			case MULTIPLY_AND_ASSIGN:
				return "MULTIPLY_AND_ASSIGN"; //$NON-NLS-1$
			case SHIFT_LEFT_AND_ASSIGN:
				return "SHIFT_LEFT_AND_ASSIGN"; //$NON-NLS-1$
			case SHIFT_RIGHT_AND_ASSIGN:
				return "SHIFT_RIGHT_AND_ASSIGN"; //$NON-NLS-1$
			case SUBTRACT_AND_ASSIGN:
				return "SUBTRACT_AND_ASSIGN"; //$NON-NLS-1$
			case GET_ELEMENT:
				return "GET_ELEMENT"; //$NON-NLS-1$
			case GET_PROPERTY:
				return "GET_PROPERTY"; //$NON-NLS-1$
			case EQUAL:
				return "EQUAL"; //$NON-NLS-1$
			case GREATER_THAN:
				return "GREATER_THAN"; //$NON-NLS-1$
			case GREATER_THAN_OR_EQUAL:
				return "GREATER_THAN_OR_EQUAL"; //$NON-NLS-1$
			case IDENTITY:
				return "IDENTITY"; //$NON-NLS-1$
			case IN:
				return "IN"; //$NON-NLS-1$
			case INSTANCE_OF:
				return "INSTANCE_OF"; //$NON-NLS-1$
			case LESS_THAN:
				return "LESS_THAN"; //$NON-NLS-1$
			case LESS_THAN_OR_EQUAL:
				return "LESS_THAN_OR_EQUAL"; //$NON-NLS-1$
			case LOGICAL_AND:
				return "LOGICAL_AND"; //$NON-NLS-1$
			case LOGICAL_OR:
				return "LOGICAL_OR"; //$NON-NLS-1$
			case NOT_EQUAL:
				return "NOT_EQUAL"; //$NON-NLS-1$
			case NOT_IDENTITY:
				return "NOT_IDENTITY"; //$NON-NLS-1$
			case ADD:
				return "ADD"; //$NON-NLS-1$
			case ARITHMETIC_SHIFT_RIGHT:
				return "ARITHMETIC_SHIFT_RIGHT"; //$NON-NLS-1$
			case BITWISE_AND:
				return "BITWISE_AND"; //$NON-NLS-1$
			case BITWISE_OR:
				return "BITWISE_OR"; //$NON-NLS-1$
			case BITWISE_XOR:
				return "BITWISE_XOR"; //$NON-NLS-1$
			case DIVIDE:
				return "DIVIDE"; //$NON-NLS-1$
			case MOD:
				return "MOD"; //$NON-NLS-1$
			case MULTIPLY:
				return "MULTIPLY"; //$NON-NLS-1$
			case SHIFT_LEFT:
				return "SHIFT_LEFT"; //$NON-NLS-1$
			case SHIFT_RIGHT:
				return "SHIFT_RIGHT"; //$NON-NLS-1$
			case SUBTRACT:
				return "SUBTRACT"; //$NON-NLS-1$
			case CATCH:
				return "CATCH"; //$NON-NLS-1$
			case CONDITIONAL:
				return "CONDITIONAL"; //$NON-NLS-1$
			case CONSTRUCT:
				return "CONSTRUCT"; //$NON-NLS-1$
			case DECLARATION:
				return "DECLARATION"; //$NON-NLS-1$
			case DO:
				return "DO"; //$NON-NLS-1$
			case EMPTY:
				return "EMPTY"; //$NON-NLS-1$
			case FINALLY:
				return "FINALLY"; //$NON-NLS-1$
			case FOR_IN:
				return "FOR_IN"; //$NON-NLS-1$
			case FOR:
				return "FOR"; //$NON-NLS-1$
			case FUNCTION:
				return "FUNCTION"; //$NON-NLS-1$
			case IF:
				return "IF"; //$NON-NLS-1$
			case INVOKE:
				return "INVOKE"; //$NON-NLS-1$
			case LABELLED:
				return "LABELLED"; //$NON-NLS-1$
			case BREAK:
				return "BREAK"; //$NON-NLS-1$
			case CONTINUE:
				return "CONTINUE"; //$NON-NLS-1$
			case ARGUMENTS:
				return "ARGUMENTS"; //$NON-NLS-1$
			case ARRAY_LITERAL:
				return "ARRAY_LITERAL"; //$NON-NLS-1$
			case COMMA:
				return "COMMA"; //$NON-NLS-1$
			case DEFAULT:
				return "DEFAULT"; //$NON-NLS-1$
			case CASE:
				return "CASE"; //$NON-NLS-1$
			case SWITCH:
				return "SWITCH"; //$NON-NLS-1$
			case OBJECT_LITERAL:
				return "OBJECT_LITERAL"; //$NON-NLS-1$
			case PARAMETERS:
				return "PARAMETERS"; //$NON-NLS-1$
			case STATEMENTS:
				return "STATEMENTS"; //$NON-NLS-1$
			case VAR:
				return "VAR"; //$NON-NLS-1$
			case FALSE:
				return "FALSE"; //$NON-NLS-1$
			case IDENTIFIER:
				return "IDENTIFIER"; //$NON-NLS-1$
			case NULL:
				return "NULL"; //$NON-NLS-1$
			case NUMBER:
				return "NUMBER"; //$NON-NLS-1$
			case REGULAR_EXPRESSION:
				return "REGULAR_EXPRESSION"; //$NON-NLS-1$
			case STRING:
				return "STRING"; //$NON-NLS-1$
			case TRUE:
				return "TRUE"; //$NON-NLS-1$
			case NAME_VALUE_PAIR:
				return "NAME_VALUE_PAIR"; //$NON-NLS-1$
			case THIS:
				return "THIS"; //$NON-NLS-1$
			case TRY:
				return "TRY"; //$NON-NLS-1$
			case DELETE:
				return "DELETE"; //$NON-NLS-1$
			case GROUP:
				return "GROUP"; //$NON-NLS-1$
			case LOGICAL_NOT:
				return "LOGICAL_NOT"; //$NON-NLS-1$
			case BITWISE_NOT:
				return "BITWISE_NOT"; //$NON-NLS-1$
			case NEGATE:
				return "NEGATE"; //$NON-NLS-1$
			case POSITIVE:
				return "POSITIVE"; //$NON-NLS-1$
			case POST_DECREMENT:
				return "POST_DECREMENT"; //$NON-NLS-1$
			case POST_INCREMENT:
				return "POST_INCREMENT"; //$NON-NLS-1$
			case PRE_DECREMENT:
				return "PRE_DECREMENT"; //$NON-NLS-1$
			case PRE_INCREMENT:
				return "PRE_INCREMENT"; //$NON-NLS-1$
			case RETURN:
				return "RETURN"; //$NON-NLS-1$
			case THROW:
				return "THROW"; //$NON-NLS-1$
			case TYPEOF:
				return "TYPEOF"; //$NON-NLS-1$
			case VOID:
				return "VOID"; //$NON-NLS-1$
			case WHILE:
				return "WHILE"; //$NON-NLS-1$
			case WITH:
				return "WITH"; //$NON-NLS-1$
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
		Class<?> c = JSParseNodeTypes.class;
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
