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

package com.aptana.ide.editor.html.lexing;

import java.lang.reflect.Field;

/**
 * @author Kevin Lindsey
 */
public class HTMLTokenTypes
{
	/**
	 * HTMLTokenTypes
	 */
	protected HTMLTokenTypes()
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
	 * START_COMMENT
	 */
	public static final int START_COMMENT = 3;
	
	/**
	 * ANY
	 */
	public static final int ANY = 4;
	
	/**
	 * CDATA
	 */
	public static final int CDATA = 5;
	
	/**
	 * EMPTY
	 */
	public static final int EMPTY = 6;
	
	/**
	 * ENTITY
	 */
	public static final int ENTITY = 7;
	
	/**
	 * ENTITIES
	 */
	public static final int ENTITIES = 8;
	
	/**
	 * FIXED
	 */
	public static final int FIXED = 9;
	
	/**
	 * ID
	 */
	public static final int ID = 10;
	
	/**
	 * IDREF
	 */
	public static final int IDREF = 11;
	
	/**
	 * IDREFS
	 */
	public static final int IDREFS = 12;
	
	/**
	 * IMPLIED
	 */
	public static final int IMPLIED = 13;
	
	/**
	 * NDATA
	 */
	public static final int NDATA = 14;
	
	/**
	 * NMTOKEN
	 */
	public static final int NMTOKEN = 15;
	
	/**
	 * NMTOKENS
	 */
	public static final int NMTOKENS = 16;
	
	/**
	 * NOTATION
	 */
	public static final int NOTATION = 17;
	
	/**
	 * PCDATA
	 */
	public static final int PCDATA = 18;
	
	/**
	 * PUBLIC
	 */
	public static final int PUBLIC = 19;
	
	/**
	 * REQUIRED
	 */
	public static final int REQUIRED = 20;
	
	/**
	 * SYSTEM
	 */
	public static final int SYSTEM = 21;
	
	/**
	 * ATTLIST_DECL
	 */
	public static final int ATTLIST_DECL = 22;
	
	/**
	 * CDATA_END
	 */
	public static final int CDATA_END = 23;
	
	/**
	 * CDATA_START
	 */
	public static final int CDATA_START = 24;
	
	/**
	 * DOCTYPE_DECL
	 */
	public static final int DOCTYPE_DECL = 25;
	
	/**
	 * ELEMENT_DECL
	 */
	public static final int ELEMENT_DECL = 26;
	
	/**
	 * END_TAG
	 */
	public static final int END_TAG = 27;
	
	/**
	 * ENTITY_DECL
	 */
	public static final int ENTITY_DECL = 28;
	
	/**
	 * EQUAL
	 */
	public static final int EQUAL = 29;
	
	/**
	 * GREATER_THAN
	 */
	public static final int GREATER_THAN = 30;
	
	/**
	 * LBRACKET
	 */
	public static final int LBRACKET = 31;
	
	/**
	 * NOTATION_DECL
	 */
	public static final int NOTATION_DECL = 32;
	
	/**
	 * PERCENT_OPEN
	 */
	public static final int PERCENT_OPEN = 33;
	
	/**
	 * PI_OPEN
	 */
	public static final int PI_OPEN = 34;
	
	/**
	 * PLUS
	 */
	public static final int PLUS = 35;
	
	/**
	 * QUESTION
	 */
	public static final int QUESTION = 36;
	
	/**
	 * QUESTION_GREATER_THAN
	 */
	public static final int QUESTION_GREATER_THAN = 37;
	
	/**
	 * RBRACKET
	 */
	public static final int RBRACKET = 38;
	
	/**
	 * SLASH_GREATER_THAN
	 */
	public static final int SLASH_GREATER_THAN = 39;
	
	/**
	 * STAR
	 */
	public static final int STAR = 40;
	
	/**
	 * START_TAG
	 */
	public static final int START_TAG = 41;
	
	/**
	 * XML_DECL
	 */
	public static final int XML_DECL = 42;
	
	/**
	 * NAME
	 */
	public static final int NAME = 43;
	
	/**
	 * STRING
	 */
	public static final int STRING = 44;
	
	/**
	 * ENTITY_REF
	 */
	public static final int ENTITY_REF = 45;
	
	/**
	 * CHAR_REF
	 */
	public static final int CHAR_REF = 46;
	
	/**
	 * PE_REF
	 */
	public static final int PE_REF = 47;
	
	/**
	 * CDATA_TEXT
	 */
	public static final int CDATA_TEXT = 48;
	
	/**
	 * PERCENT_TEXT
	 */
	public static final int PERCENT_TEXT = 49;
	
	/**
	 * PI_TEXT
	 */
	public static final int PI_TEXT = 50;
	
	/**
	 * TEXT
	 */
	public static final int TEXT = 51;
	
	/**
	 * ENCODING
	 */
	public static final int ENCODING = 52;
	
	/**
	 * STANDALONE
	 */
	public static final int STANDALONE = 53;
	
	/**
	 * VERSION
	 */
	public static final int VERSION = 54;
	
	/**
	 * PERCENT_GREATER
	 */
	public static final int PERCENT_GREATER = 55;
	
	/**
	 * QUOTE
	 */
	public static final int QUOTE = 56;
	
	/**
	 * MAX_VALUE
	 */
	public static final int MAX_VALUE = 56;

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
				
			case START_COMMENT:
				return "START_COMMENT"; //$NON-NLS-1$
				
			case ANY:
				return "ANY"; //$NON-NLS-1$
				
			case CDATA:
				return "CDATA"; //$NON-NLS-1$
				
			case EMPTY:
				return "EMPTY"; //$NON-NLS-1$
				
			case ENTITY:
				return "ENTITY"; //$NON-NLS-1$
				
			case ENTITIES:
				return "ENTITIES"; //$NON-NLS-1$
				
			case FIXED:
				return "FIXED"; //$NON-NLS-1$
				
			case ID:
				return "ID"; //$NON-NLS-1$
				
			case IDREF:
				return "IDREF"; //$NON-NLS-1$
				
			case IDREFS:
				return "IDREFS"; //$NON-NLS-1$
				
			case IMPLIED:
				return "IMPLIED"; //$NON-NLS-1$
				
			case NDATA:
				return "NDATA"; //$NON-NLS-1$
				
			case NMTOKEN:
				return "NMTOKEN"; //$NON-NLS-1$
				
			case NMTOKENS:
				return "NMTOKENS"; //$NON-NLS-1$
				
			case NOTATION:
				return "NOTATION"; //$NON-NLS-1$
				
			case PCDATA:
				return "PCDATA"; //$NON-NLS-1$
				
			case PUBLIC:
				return "PUBLIC"; //$NON-NLS-1$
				
			case REQUIRED:
				return "REQUIRED"; //$NON-NLS-1$
				
			case SYSTEM:
				return "SYSTEM"; //$NON-NLS-1$
				
			case ATTLIST_DECL:
				return "ATTLIST_DECL"; //$NON-NLS-1$
				
			case CDATA_END:
				return "CDATA_END"; //$NON-NLS-1$
				
			case CDATA_START:
				return "CDATA_START"; //$NON-NLS-1$
				
			case DOCTYPE_DECL:
				return "DOCTYPE_DECL"; //$NON-NLS-1$
				
			case ELEMENT_DECL:
				return "ELEMENT_DECL"; //$NON-NLS-1$
				
			case END_TAG:
				return "END_TAG"; //$NON-NLS-1$
				
			case ENTITY_DECL:
				return "ENTITY_DECL"; //$NON-NLS-1$
				
			case EQUAL:
				return "EQUAL"; //$NON-NLS-1$
				
			case GREATER_THAN:
				return "GREATER_THAN"; //$NON-NLS-1$
				
			case LBRACKET:
				return "LBRACKET"; //$NON-NLS-1$
				
			case NOTATION_DECL:
				return "NOTATION_DECL"; //$NON-NLS-1$
				
			case PERCENT_OPEN:
				return "PERCENT_OPEN"; //$NON-NLS-1$
				
			case PI_OPEN:
				return "PI_OPEN"; //$NON-NLS-1$
				
			case PLUS:
				return "PLUS"; //$NON-NLS-1$
				
			case QUESTION:
				return "QUESTION"; //$NON-NLS-1$
				
			case QUESTION_GREATER_THAN:
				return "QUESTION_GREATER_THAN"; //$NON-NLS-1$
				
			case RBRACKET:
				return "RBRACKET"; //$NON-NLS-1$
				
			case SLASH_GREATER_THAN:
				return "SLASH_GREATER_THAN"; //$NON-NLS-1$
				
			case STAR:
				return "STAR"; //$NON-NLS-1$
				
			case START_TAG:
				return "START_TAG"; //$NON-NLS-1$
				
			case XML_DECL:
				return "XML_DECL"; //$NON-NLS-1$
				
			case NAME:
				return "NAME"; //$NON-NLS-1$
				
			case STRING:
				return "STRING"; //$NON-NLS-1$
				
			case ENTITY_REF:
				return "ENTITY_REF"; //$NON-NLS-1$
				
			case CHAR_REF:
				return "CHAR_REF"; //$NON-NLS-1$
				
			case PE_REF:
				return "PE_REF"; //$NON-NLS-1$
				
			case CDATA_TEXT:
				return "CDATA_TEXT"; //$NON-NLS-1$
				
			case PERCENT_TEXT:
				return "PERCENT_TEXT"; //$NON-NLS-1$
				
			case PI_TEXT:
				return "PI_TEXT"; //$NON-NLS-1$
				
			case TEXT:
				return "TEXT"; //$NON-NLS-1$
				
			case ENCODING:
				return "ENCODING"; //$NON-NLS-1$
				
			case STANDALONE:
				return "STANDALONE"; //$NON-NLS-1$
				
			case VERSION:
				return "VERSION"; //$NON-NLS-1$
				
			case PERCENT_GREATER:
				return "PERCENT_GREATER"; //$NON-NLS-1$
				
			case QUOTE:
				return "QUOTE";	//$NON-NLS-1$
				
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
		Class c = HTMLTokenTypes.class;
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
