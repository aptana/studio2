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

package com.aptana.ide.editor.scriptdoc.lexing;

import java.lang.reflect.Field;

/**
 * @author Kevin Lindsey
 */
public class ScriptDocTokenTypes
{
	/**
	 * ScriptDocTokenTypes
	 */
	protected ScriptDocTokenTypes()
	{
	}

	/**
	 * ERROR
	 */
	public static final int ERROR = 0;

	/**
	 * START_DOCUMENTATION
	 */
	public static final int START_DOCUMENTATION = 1;

	/**
	 * WHITESPACE
	 */
	public static final int WHITESPACE = 2;

	/**
	 * LINE_TERMINATOR
	 */
	public static final int LINE_TERMINATOR = 3;

	/**
	 * TEXT
	 */
	public static final int TEXT = 4;

	/**
	 * LINK
	 */
	public static final int LINK = 5;

	/**
	 * ALIAS
	 */
	public static final int ALIAS = 6;

	/**
	 * AUTHOR
	 */
	public static final int AUTHOR = 7;

	/**
	 * VERSION
	 */
	public static final int VERSION = 8;

	/**
	 * COPYRIGHT
	 */
	public static final int COPYRIGHT = 9;

	/**
	 * LICENSE
	 */
	public static final int LICENSE = 10;

	/**
	 * SINCE
	 */
	public static final int SINCE = 11;

	/**
	 * SEE
	 */
	public static final int SEE = 12;

	/**
	 * SDOC
	 */
	public static final int SDOC = 13;

	/**
	 * PROJECT_DESCRIPTION
	 */
	public static final int PROJECT_DESCRIPTION = 14;

	/**
	 * ID
	 */
	public static final int ID = 15;

	/**
	 * IGNORE
	 */
	public static final int IGNORE = 16;

	/**
	 * DEPRECATED
	 */
	public static final int DEPRECATED = 17;

	/**
	 * PRIVATE
	 */
	public static final int PRIVATE = 18;

	/**
	 * PROTECTED
	 */
	public static final int PROTECTED = 19;

	/**
	 * INTERNAL
	 */
	public static final int INTERNAL = 20;

	/**
	 * NATIVE
	 */
	public static final int NATIVE = 21;

	/**
	 * NAMESPACE
	 */
	public static final int NAMESPACE = 22;

	/**
	 * TYPE
	 */
	public static final int TYPE = 23;

	/**
	 * CONSTRUCTOR
	 */
	public static final int CONSTRUCTOR = 24;

	/**
	 * METHOD
	 */
	public static final int METHOD = 25;

	/**
	 * CLASS_DESCRIPTION
	 */
	public static final int CLASS_DESCRIPTION = 26;

	/**
	 * MEMBER_OF
	 */
	public static final int MEMBER_OF = 27;

	/**
	 * PARAM
	 */
	public static final int PARAM = 28;

	/**
	 * EXCEPTION
	 */
	public static final int EXCEPTION = 29;

	/**
	 * RETURN
	 */
	public static final int RETURN = 30;

	/**
	 * EXTENDS
	 */
	public static final int EXTENDS = 31;

	/**
	 * END_DOCUMENTATION
	 */
	public static final int END_DOCUMENTATION = 32;

	/**
	 * LCURLY
	 */
	public static final int LCURLY = 33;

	/**
	 * DOLLAR_LCURLY
	 */
	public static final int DOLLAR_LCURLY = 34;

	/**
	 * POUND
	 */
	public static final int POUND = 35;

	/**
	 * LBRACKET
	 */
	public static final int LBRACKET = 36;

	/**
	 * RBRACKET
	 */
	public static final int RBRACKET = 37;

	/**
	 * STAR
	 */
	public static final int STAR = 38;

	/**
	 * IDENTIFIER
	 */
	public static final int IDENTIFIER = 39;

	/**
	 * RCURLY
	 */
	public static final int RCURLY = 40;

	/**
	 * ELLIPSIS
	 */
	public static final int ELLIPSIS = 41;

	/**
	 * COMMA
	 */
	public static final int COMMA = 42;

	/**
	 * PIPE
	 */
	public static final int PIPE = 43;

	/**
	 * FORWARD_SLASH
	 */
	public static final int FORWARD_SLASH = 44;

	/**
	 * EXAMPLE
	 */
	public static final int EXAMPLE = 45;

	/**
	 * PROPERTY
	 */
	public static final int PROPERTY = 46;
	
	/**
	 * OVERVIEW
	 */
	public static final int OVERVIEW = 47;

	/**
	 * ADVANCED
	 */
	public static final int ADVANCED = 48;

	/**
	 * MAX_VALUE (set equal to last int)
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

			case START_DOCUMENTATION:
				return "START_DOCUMENTATION"; //$NON-NLS-1$

			case WHITESPACE:
				return "WHITESPACE"; //$NON-NLS-1$

			case LINE_TERMINATOR:
				return "LINE_TERMINATOR"; //$NON-NLS-1$

			case TEXT:
				return "TEXT"; //$NON-NLS-1$

			case LINK:
				return "LINK"; //$NON-NLS-1$

			case ALIAS:
				return "ALIAS"; //$NON-NLS-1$

			case AUTHOR:
				return "AUTHOR"; //$NON-NLS-1$

			case VERSION:
				return "VERSION"; //$NON-NLS-1$

			case COPYRIGHT:
				return "COPYRIGHT"; //$NON-NLS-1$

			case LICENSE:
				return "LICENSE"; //$NON-NLS-1$

			case SINCE:
				return "SINCE"; //$NON-NLS-1$

			case SEE:
				return "SEE"; //$NON-NLS-1$

			case SDOC:
				return "SDOC"; //$NON-NLS-1$

			case PROJECT_DESCRIPTION:
				return "PROJECT_DESCRIPTION"; //$NON-NLS-1$

			case ID:
				return "ID"; //$NON-NLS-1$

			case IGNORE:
				return "IGNORE"; //$NON-NLS-1$

			case DEPRECATED:
				return "DEPRECATED"; //$NON-NLS-1$

			case PRIVATE:
				return "PRIVATE"; //$NON-NLS-1$

			case PROTECTED:
				return "PROTECTED"; //$NON-NLS-1$

			case INTERNAL:
				return "INTERNAL"; //$NON-NLS-1$

			case NATIVE:
				return "NATIVE"; //$NON-NLS-1$

			case NAMESPACE:
				return "NAMESPACE"; //$NON-NLS-1$

			case TYPE:
				return "TYPE"; //$NON-NLS-1$

			case CONSTRUCTOR:
				return "CONSTRUCTOR"; //$NON-NLS-1$

			case METHOD:
				return "METHOD"; //$NON-NLS-1$

			case CLASS_DESCRIPTION:
				return "CLASS_DESCRIPTION"; //$NON-NLS-1$

			case MEMBER_OF:
				return "MEMBER_OF"; //$NON-NLS-1$

			case PARAM:
				return "PARAM"; //$NON-NLS-1$

			case EXCEPTION:
				return "EXCEPTION"; //$NON-NLS-1$

			case RETURN:
				return "RETURN"; //$NON-NLS-1$

			case EXTENDS:
				return "EXTENDS"; //$NON-NLS-1$

			case END_DOCUMENTATION:
				return "END_DOCUMENTATION"; //$NON-NLS-1$

			case LCURLY:
				return "LCURLY"; //$NON-NLS-1$

			case DOLLAR_LCURLY:
				return "DOLLAR_LCURLY"; //$NON-NLS-1$

			case POUND:
				return "POUND"; //$NON-NLS-1$

			case LBRACKET:
				return "LBRACKET"; //$NON-NLS-1$

			case RBRACKET:
				return "RBRACKET"; //$NON-NLS-1$

			case STAR:
				return "STAR"; //$NON-NLS-1$

			case IDENTIFIER:
				return "IDENTIFIER"; //$NON-NLS-1$

			case RCURLY:
				return "RCURLY"; //$NON-NLS-1$

			case ELLIPSIS:
				return "ELLIPSIS"; //$NON-NLS-1$

			case COMMA:
				return "COMMA"; //$NON-NLS-1$

			case PIPE:
				return "PIPE"; //$NON-NLS-1$

			case FORWARD_SLASH:
				return "FORWARD_SLASH"; //$NON-NLS-1$

			case EXAMPLE:
				return "EXAMPLE"; //$NON-NLS-1$

			case PROPERTY:
				return "PROPERTY"; //$NON-NLS-1$

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
		Class<?> c = ScriptDocTokenTypes.class;
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
