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
package com.aptana.ide.editor.xml.parsing.nodes;

import java.lang.reflect.Field;

/**
 * @author Kevin Lindsey
 */
public class XMLParseNodeTypes
{
	/**
	 * XMLParseNodeTypes
	 */
	protected XMLParseNodeTypes()
	{
	}

	/**
	 * UNKNOWN
	 */
	public static final int UNKNOWN = 0;

	/**
	 * DOCUMENT
	 */
	public static final int DOCUMENT = 1;

	/**
	 * DECLARATION
	 */
	public static final int DECLARATION = 2;

	/**
	 * ELEMENT
	 */
	public static final int ELEMENT = 3;

	/**
	 * MAX_VALUE
	 */
	public static final int MAX_VALUE = 3;

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
			case DOCUMENT:
				return "DOCUMENT"; //$NON-NLS-1$
			case DECLARATION:
				return "DECLARATION"; //$NON-NLS-1$
			case ELEMENT:
				return "ELEMENT"; //$NON-NLS-1$
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
		Class c = XMLParseNodeTypes.class;
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
