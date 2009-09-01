/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.editor.scriptdoc.runtime;

import com.aptana.ide.lexer.IRange;

/**
 * This is a general purpose container for ScriptDocVM instructions. In order to
 * reduce the number of runtime type tests and runtime casts, this is the union
 * of all opcodes as far as what supporting data is needed for each opcode.
 * 
 * @author Kevin Lindsey
 */
public class Opcode
{
	public final OpcodeType type;
	public final int fileIndex;
	public final int fileOffset;
	public final IRange range;
	public final Object value;
	
	/**
	 * Opcode
	 * 
	 * @param type
	 */
	public Opcode(OpcodeType type)
	{
		this(type, 0, 0, null, null);
	}
	
	/**
	 * Opcode
	 * 
	 * @param type
	 * @param fileIndex
	 */
	public Opcode(OpcodeType type, int fileIndex)
	{
		this(type, fileIndex, 0, null, null);
	}
	
	/**
	 * Opcode
	 * 
	 * @param type
	 * @param fileIndex
	 * @param fileOffset
	 */
	public Opcode(OpcodeType type, int fileIndex, int fileOffset)
	{
		this(type, fileIndex, fileOffset, null, null);
	}
	
	/**
	 * Opcode
	 * 
	 * @param type
	 * @param range
	 */
	public Opcode(OpcodeType type, int fileIndex, IRange range)
	{
		this(type, fileIndex, 0, range, null);
	}
	
	/**
	 * Opcode
	 * 
	 * @param type
	 * @param range
	 */
	public Opcode(OpcodeType type, Object value)
	{
		this(type, 0, 0, null, value);
	}
	
	/**
	 * Opcode
	 * 
	 * @param type
	 * @param fileIndex
	 * @param fileOffset
	 * @param range
	 */
	private Opcode(OpcodeType type, int fileIndex, int fileOffset, IRange range, Object value)
	{
		this.type = type;
		this.fileIndex = fileIndex;
		this.fileOffset = fileOffset;
		this.range = range;
		this.value = value;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String result;
		
		switch (this.type)
		{
			case DUPLICATE:
				result = "duplicate"; //$NON-NLS-1$
				break;
				
			case GET:
				result = "get"; //$NON-NLS-1$
				break;
				
			case GET_GLOBAL:
				result = "get_global"; //$NON-NLS-1$
				break;
				
			case INSTANTIATE:
				result = "instantiate"; //$NON-NLS-1$
				break;
				
			case INVOKE:
				result = "invoke"; //$NON-NLS-1$
				break;
				
			case NO_OPERATION:
				result = "no_operation"; //$NON-NLS-1$
				break;
				
			case POP:
				result = "pop"; //$NON-NLS-1$
				break;
				
			case PUSH:
				if (this.value instanceof String)
				{
					result = "push '" + this.value.toString() + "'"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				else
				{
					result = "push " + this.value.toString(); //$NON-NLS-1$
				}
				break;
				
			case PUSH_ARRAY:
				result = "push_array"; //$NON-NLS-1$
				break;
				
			case PUSH_BOOLEAN:
				result = "push_boolean"; //$NON-NLS-1$
				break;
				
			case PUSH_NULL:
				result = "push_null"; //$NON-NLS-1$
				break;
				
			case PUSH_NUMBER:
				result = "push_number"; //$NON-NLS-1$
				break;
				
			case PUSH_OBJECT:
				result = "push_object"; //$NON-NLS-1$
				break;
				
			case PUSH_REGEXP:
				result = "push_regexp"; //$NON-NLS-1$
				break;
				
			case PUSH_STRING:
				result = "push_string"; //$NON-NLS-1$
				break;
				
			case PUT:
				result = "put"; //$NON-NLS-1$
				break;
			
			case SWAP:
				result = "swap"; //$NON-NLS-1$
				break;
				
			default:
				result = "unknown"; //$NON-NLS-1$
		}
		
		return result;
	}
}
