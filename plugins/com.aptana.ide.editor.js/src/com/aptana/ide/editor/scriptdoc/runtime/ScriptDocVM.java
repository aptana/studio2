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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IFunction;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.lexer.IRange;
import com.aptana.ide.lexer.Range;

/**
 * @author Kevin Lindsey
 */
public class ScriptDocVM
{
	private List<Opcode> _opcodes;
	private Stack<Object> _stack;

	/**
	 * ScriptDocVM
	 * 
	 * @param environment
	 */
	public ScriptDocVM()
	{
		this._opcodes = new ArrayList<Opcode>();
		this._stack = new Stack<Object>();
	}
	
	/**
	 * addDuplicate
	 */
	public void addDuplicate()
	{
		this._opcodes.add(this.createDuplicate());
	}

	/**
	 * addGet
	 * 
	 * @param fileIndex
	 * @param fileOffset
	 */
	public void addGet(int fileIndex, int fileOffset)
	{
		this._opcodes.add(this.createGet(fileIndex, fileOffset));
	}

	/**
	 * addGetGlobal
	 */
	public void addGetGlobal()
	{
		this._opcodes.add(this.createGetGlobal());
	}

	/**
	 * addInstantiate
	 * 
	 * @param fileIndex
	 * @param range
	 */
	public void addInstantiate(int fileIndex, Range range)
	{
		this._opcodes.add(this.createInstantiate(fileIndex, range));
	}

	/**
	 * addInvoke
	 * 
	 * @param fileIndex
	 * @param range
	 */
	public void addInvoke(int fileIndex, Range range)
	{
		this._opcodes.add(this.createInvoke(fileIndex, range));
	}

	/**
	 * addNop
	 */
	public void addNoOperation()
	{
		this._opcodes.add(this.createNoOperation());
	}

	/**
	 * addOpcode
	 * 
	 * @param opcode
	 */
	public void addOpcode(Opcode opcode)
	{
		this._opcodes.add(opcode);
	}

	/**
	 * addOpcodes
	 * 
	 * @param opcodes
	 */
	public void addOpcodes(List<Opcode> opcodes)
	{
		this._opcodes.addAll(opcodes);
	}
	
	/**
	 * addPop
	 */
	public void addPop()
	{
		this._opcodes.add(this.createPop());
	}

	/**
	 * addPush
	 * 
	 * @param value
	 */
	public void addPush(Object value)
	{
		this._opcodes.add(this.createPush(value));
	}
	
	/**
	 * addPushArray
	 */
	public void addPushArray(int index, IRange range)
	{
		this._opcodes.add(this.createPushArray(index, range));
	}
	
	/**
	 * addPushBoolean
	 */
	public void addPushBoolean(int index, IRange range)
	{
		this._opcodes.add(this.createPushBoolean(index, range));
	}
	
	/**
	 * addPushNull
	 */
	public void addPushNull(int index, IRange range)
	{
		this._opcodes.add(this.createPushNull(index, range));
	}
	
	/**
	 * addPushNumber
	 */
	public void addPushNumber(int index, IRange range)
	{
		this._opcodes.add(this.createPushNumber(index, range));
	}
	
	/**
	 * addPushObject
	 */
	public void addPushObject(int index, IRange range)
	{
		this._opcodes.add(this.createPushObject(index, range));
	}
	
	/**
	 * addPushRegExp
	 */
	public void addPushRegExp(int index, IRange range)
	{
		this._opcodes.add(this.createPushRegExp(index, range));
	}
	
	/**
	 * addPushString
	 */
	public void addPushString(int index, IRange range)
	{
		this._opcodes.add(this.createPushString(index, range));
	}
	
	/**
	 * addPut
	 * 
	 * @param fileIndex
	 */
	public void addPut(int fileIndex)
	{
		this._opcodes.add(this.createPut(fileIndex));
	}

	/**
	 * addSwap
	 */
	public void addSwap()
	{
		this._opcodes.add(this.createSwap());
	}
	
	/**
	 * clearOpcodes
	 */
	public void clearOpcodes()
	{
		this._opcodes.clear();
	}
	
	/**
	 * clearStack
	 */
	public void clearStack()
	{
		this._stack.clear();
	}
	
	/**
	 * createDuplicate
	 * 
	 * @return
	 */
	public Opcode createDuplicate()
	{
		return new Opcode(OpcodeType.DUPLICATE);
	}

	/**
	 * createGet
	 * 
	 * @param fileIndex
	 * @param fileOffset
	 * @return
	 */
	public Opcode createGet(int fileIndex, int fileOffset)
	{
		return new Opcode(OpcodeType.GET, fileIndex, fileOffset);
	}

	/**
	 * createGetGlobal
	 *
	 * @return
	 */
	public Opcode createGetGlobal()
	{
		return new Opcode(OpcodeType.GET_GLOBAL);
	}

	/**
	 * createInstantiate
	 *
	 * @param fileIndex
	 * @param range
	 * @return
	 */
	public Opcode createInstantiate(int fileIndex, Range range)
	{
		return new Opcode(OpcodeType.INSTANTIATE, fileIndex, range);
	}
	
	/**
	 * createInvoke
	 *
	 * @param fileIndex
	 * @param range
	 * @return
	 */
	public Opcode createInvoke(int fileIndex, Range range)
	{
		return new Opcode(OpcodeType.INVOKE, fileIndex, range);
	}

	/**
	 * createNoOperation
	 *
	 * @return
	 */
	public Opcode createNoOperation()
	{
		return new Opcode(OpcodeType.NO_OPERATION);
	}

	/**
	 * createPop
	 *
	 * @return
	 */
	public Opcode createPop()
	{
		return new Opcode(OpcodeType.POP);
	}

	/**
	 * createPush
	 *
	 * @param value
	 * @return
	 */
	public Opcode createPush(Object value)
	{
		return new Opcode(OpcodeType.PUSH, value);
	}
	
	/**
	 * createPushArray
	 *
	 * @return
	 */
	public Opcode createPushArray(int fileIndex, IRange range)
	{
		return new Opcode(OpcodeType.PUSH_ARRAY, fileIndex, range);
	}
	
	/**
	 * createPushBoolean
	 *
	 * @return
	 */
	public Opcode createPushBoolean(int fileIndex, IRange range)
	{
		return new Opcode(OpcodeType.PUSH_BOOLEAN, fileIndex, range);
	}
	
	/**
	 * createPushNull
	 *
	 * @return
	 */
	public Opcode createPushNull(int fileIndex, IRange range)
	{
		return new Opcode(OpcodeType.PUSH_NULL, fileIndex, range);
	}
	
	/**
	 * createPushNumber
	 *
	 * @return
	 */
	public Opcode createPushNumber(int fileIndex, IRange range)
	{
		return new Opcode(OpcodeType.PUSH_NUMBER, fileIndex, range);
	}
	
	/**
	 * createPushObject
	 *
	 * @return
	 */
	public Opcode createPushObject(int fileIndex, IRange range)
	{
		return new Opcode(OpcodeType.PUSH_OBJECT, fileIndex, range);
	}
	
	/**
	 * createPushRegExp
	 *
	 * @return
	 */
	public Opcode createPushRegExp(int fileIndex, IRange range)
	{
		return new Opcode(OpcodeType.PUSH_REGEXP, fileIndex, range);
	}
	
	/**
	 * createPushString
	 *
	 * @return
	 */
	public Opcode createPushString(int fileIndex, IRange range)
	{
		return new Opcode(OpcodeType.PUSH_STRING, fileIndex, range);
	}
	
	/**
	 * createPut
	 *
	 * @param fileIndex
	 * @return
	 */
	public Opcode createPut(int fileIndex)
	{
		return new Opcode(OpcodeType.PUT, fileIndex);
	}

	/**
	 * createSwap
	 *
	 * @return
	 */
	public Opcode createSwap()
	{
		return new Opcode(OpcodeType.SWAP);
	}

	/**
	 * execute
	 * 
	 * @param environment
	 */
	public void execute(Environment environment)
	{
		Stack<Object> stack = this._stack;

		// locals used during execution
		IObject[] args = new IObject[0];
		IObject object;
		IFunction function;
		String propertyName;

		for (Opcode opcode : this._opcodes)
		{
			switch (opcode.type)
			{
				case DUPLICATE:
					stack.push(stack.peek());
					break;
					
				case GET:
					propertyName = (String) stack.pop();
					object = (IObject) stack.pop();
					stack.push(object.getPropertyValue(propertyName, opcode.fileIndex, opcode.fileOffset));
					break;
					
				case GET_GLOBAL:
					stack.push(environment.getGlobal());
					break;

				case INSTANTIATE:
					function = (IFunction) stack.pop();
					stack.push(function.construct(environment, args, opcode.fileIndex, opcode.range));
					break;

				case INVOKE:
					function = (IFunction) stack.pop();
					stack.push(function.invoke(environment, args, opcode.fileIndex, opcode.range));
					break;

				case NO_OPERATION:
					// do nothing
					break;
					
				case POP:
					stack.pop();
					break;
					
				case PUT:
					IObject value = (IObject) stack.pop();
					propertyName = (String) stack.pop();
					object = (IObject) stack.pop();
					object.putPropertyValue(propertyName, value, opcode.fileIndex);
					break;

				case PUSH:
					stack.push(opcode.value);
					break;
					
				case PUSH_ARRAY:
					stack.push(environment.createArray(opcode.fileIndex, opcode.range));
					break;
					
				case PUSH_BOOLEAN:
					stack.push(environment.createBoolean(opcode.fileIndex, opcode.range));
					break;
					
				case PUSH_NULL:
					// NOTE: seems like we should use fileIndex/IRange for null constants
					stack.push(environment.createNull());
					break;
					
				case PUSH_NUMBER:
					stack.push(environment.createNumber(opcode.fileIndex, opcode.range));
					break;
					
				case PUSH_OBJECT:
					stack.push(environment.createObject(opcode.fileIndex, opcode.range));
					break;
					
				case PUSH_REGEXP:
					stack.push(environment.createRegExp(opcode.fileIndex, opcode.range));
					break;
					
				case PUSH_STRING:
					stack.push(environment.createString(opcode.fileIndex, opcode.range));
					break;

				case SWAP:
					int lastIndex = stack.size() - 1;
					stack.set(lastIndex, stack.set(lastIndex - 1, stack.peek()));
					break;
					
				default:
					break;
			}
		}
	}

	/**
	 * getOpcodes
	 * 
	 * @return
	 */
	public Opcode[] getOpcodes()
	{
		return this._opcodes.toArray(new Opcode[this._opcodes.size()]);
	}

	/**
	 * getStackValues
	 * 
	 * @return
	 */
	public Object[] getStackValues()
	{
		return this._stack.toArray(new Object[this._stack.size()]);
	}
}
