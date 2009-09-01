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

import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editors.actions.ICanBeNotNavigatable;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.nodes.ParseNodeBase;

/**
 * @author Kevin Lindsey
 */
public class JSParseNode extends ParseNodeBase implements ICanBeNotNavigatable
{
	private boolean _includesSemicolon;

	/**
	 * JSNodeBase
	 * 
	 * @param typeIndex
	 * @param startLexeme
	 */
	public JSParseNode(int typeIndex, Lexeme startLexeme)
	{
		super(typeIndex, JSMimeType.MimeType, startLexeme);
	}

	/**
	 * getIncludesSemicolon
	 *
	 * @return boolean
	 */
	public boolean getIncludesSemicolon()
	{
		return this._includesSemicolon;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getName()
	 */
	public String getName()
	{
		switch (this.getTypeIndex())
		{
			case JSParseNodeTypes.ASSIGN:
				return "assignment"; //$NON-NLS-1$
			case JSParseNodeTypes.ADD_AND_ASSIGN:
				return "add-and-assign"; //$NON-NLS-1$
			case JSParseNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN:
				return "arithmetic-shift-right-and-assign"; //$NON-NLS-1$
			case JSParseNodeTypes.BITWISE_AND_AND_ASSIGN:
				return "bitwise-and-and-assign"; //$NON-NLS-1$
			case JSParseNodeTypes.BITWISE_OR_AND_ASSIGN:
				return "bitwise-or-and-assign"; //$NON-NLS-1$
			case JSParseNodeTypes.BITWISE_XOR_AND_ASSIGN:
				return "bitwise-xor-and-assign"; //$NON-NLS-1$
			case JSParseNodeTypes.DIVIDE_AND_ASSIGN:
				return "divide-and-assign"; //$NON-NLS-1$
			case JSParseNodeTypes.MOD_AND_ASSIGN:
				return "mod-and-assign"; //$NON-NLS-1$
			case JSParseNodeTypes.MULTIPLY_AND_ASSIGN:
				return "multiply-and-assign"; //$NON-NLS-1$
			case JSParseNodeTypes.SHIFT_LEFT_AND_ASSIGN:
				return "shift-left-and-assign"; //$NON-NLS-1$
			case JSParseNodeTypes.SHIFT_RIGHT_AND_ASSIGN:
				return "shift-right-and-assign"; //$NON-NLS-1$
			case JSParseNodeTypes.SUBTRACT_AND_ASSIGN:
				return "subtract-and-assign"; //$NON-NLS-1$
			case JSParseNodeTypes.GET_ELEMENT:
				return "get-element"; //$NON-NLS-1$
			case JSParseNodeTypes.GET_PROPERTY:
				return "get-property"; //$NON-NLS-1$
			case JSParseNodeTypes.EQUAL:
				return "equals"; //$NON-NLS-1$
			case JSParseNodeTypes.GREATER_THAN:
				return "greater-than"; //$NON-NLS-1$
			case JSParseNodeTypes.GREATER_THAN_OR_EQUAL:
				return "greater-than-or-equal"; //$NON-NLS-1$
			case JSParseNodeTypes.IDENTITY:
				return "identity"; //$NON-NLS-1$
			case JSParseNodeTypes.IN:
				return "in"; //$NON-NLS-1$
			case JSParseNodeTypes.INSTANCE_OF:
				return "instanceof"; //$NON-NLS-1$
			case JSParseNodeTypes.LESS_THAN:
				return "less-than"; //$NON-NLS-1$
			case JSParseNodeTypes.LESS_THAN_OR_EQUAL:
				return "less-than-or-equal"; //$NON-NLS-1$
			case JSParseNodeTypes.LOGICAL_AND:
				return "logical-and"; //$NON-NLS-1$
			case JSParseNodeTypes.LOGICAL_OR:
				return "logical-or"; //$NON-NLS-1$
			case JSParseNodeTypes.NOT_EQUAL:
				return "not-equal"; //$NON-NLS-1$
			case JSParseNodeTypes.NOT_IDENTITY:
				return "not-identity"; //$NON-NLS-1$
			case JSParseNodeTypes.ADD:
				return "add"; //$NON-NLS-1$
			case JSParseNodeTypes.ARITHMETIC_SHIFT_RIGHT:
				return "arithmetic-shift-right"; //$NON-NLS-1$
			case JSParseNodeTypes.BITWISE_AND:
				return "bitwise-and"; //$NON-NLS-1$
			case JSParseNodeTypes.BITWISE_OR:
				return "bitwise-or"; //$NON-NLS-1$
			case JSParseNodeTypes.BITWISE_XOR:
				return "bitwise-xor"; //$NON-NLS-1$
			case JSParseNodeTypes.DIVIDE:
				return "divide"; //$NON-NLS-1$
			case JSParseNodeTypes.MOD:
				return "mod"; //$NON-NLS-1$
			case JSParseNodeTypes.MULTIPLY:
				return "multiply"; //$NON-NLS-1$
			case JSParseNodeTypes.SHIFT_LEFT:
				return "shift-left"; //$NON-NLS-1$
			case JSParseNodeTypes.SHIFT_RIGHT:
				return "shift-right"; //$NON-NLS-1$
			case JSParseNodeTypes.SUBTRACT:
				return "subtract"; //$NON-NLS-1$
			case JSParseNodeTypes.CATCH:
				return "catch"; //$NON-NLS-1$
			case JSParseNodeTypes.CONDITIONAL:
				return "conditional"; //$NON-NLS-1$
			case JSParseNodeTypes.CONSTRUCT:
				return "new"; //$NON-NLS-1$
			case JSParseNodeTypes.DECLARATION:
				return "declaration"; //$NON-NLS-1$
			case JSParseNodeTypes.DO:
				return "do"; //$NON-NLS-1$
			case JSParseNodeTypes.EMPTY:
				return "empty"; //$NON-NLS-1$
			case JSParseNodeTypes.FINALLY:
				return "finally"; //$NON-NLS-1$
			case JSParseNodeTypes.FOR_IN:
				return "for-in"; //$NON-NLS-1$
			case JSParseNodeTypes.FOR:
				return "for"; //$NON-NLS-1$
			case JSParseNodeTypes.FUNCTION:
				return "function"; //$NON-NLS-1$
			case JSParseNodeTypes.IF:
				return "if"; //$NON-NLS-1$
			case JSParseNodeTypes.INVOKE:
				return "invoke"; //$NON-NLS-1$
			case JSParseNodeTypes.LABELLED:
				return "labelled"; //$NON-NLS-1$
			case JSParseNodeTypes.BREAK:
				return "break"; //$NON-NLS-1$
			case JSParseNodeTypes.CONTINUE:
				return "continue"; //$NON-NLS-1$
			case JSParseNodeTypes.ARGUMENTS:
				return "arguments"; //$NON-NLS-1$
			case JSParseNodeTypes.ARRAY_LITERAL:
				return "array-literal"; //$NON-NLS-1$
			case JSParseNodeTypes.COMMA:
				return "comma"; //$NON-NLS-1$
			case JSParseNodeTypes.DEFAULT:
				return "default"; //$NON-NLS-1$
			case JSParseNodeTypes.CASE:
				return "case"; //$NON-NLS-1$
			case JSParseNodeTypes.SWITCH:
				return "switch"; //$NON-NLS-1$
			case JSParseNodeTypes.OBJECT_LITERAL:
				return "object-literal"; //$NON-NLS-1$
			case JSParseNodeTypes.PARAMETERS:
				return "parameters"; //$NON-NLS-1$
			case JSParseNodeTypes.STATEMENTS:
				return "statements"; //$NON-NLS-1$
			case JSParseNodeTypes.VAR:
				return "var"; //$NON-NLS-1$
			case JSParseNodeTypes.FALSE:
				return "false"; //$NON-NLS-1$
			case JSParseNodeTypes.IDENTIFIER:
				return "identifier"; //$NON-NLS-1$
			case JSParseNodeTypes.NULL:
				return "null"; //$NON-NLS-1$
			case JSParseNodeTypes.NUMBER:
				return "number"; //$NON-NLS-1$
			case JSParseNodeTypes.REGULAR_EXPRESSION:
				return "regexp"; //$NON-NLS-1$
			case JSParseNodeTypes.STRING:
				return "string"; //$NON-NLS-1$
			case JSParseNodeTypes.TRUE:
				return "true"; //$NON-NLS-1$
			case JSParseNodeTypes.NAME_VALUE_PAIR:
				return "name-value-pair"; //$NON-NLS-1$
			case JSParseNodeTypes.THIS:
				return "this"; //$NON-NLS-1$
			case JSParseNodeTypes.TRY:
				return "try"; //$NON-NLS-1$
			case JSParseNodeTypes.DELETE:
				return "delete"; //$NON-NLS-1$
			case JSParseNodeTypes.GROUP:
				return "group"; //$NON-NLS-1$
			case JSParseNodeTypes.LOGICAL_NOT:
				return "logical-not"; //$NON-NLS-1$
			case JSParseNodeTypes.BITWISE_NOT:
				return "bitwise-not"; //$NON-NLS-1$
			case JSParseNodeTypes.NEGATE:
				return "negate"; //$NON-NLS-1$
			case JSParseNodeTypes.POSITIVE:
				return "positive"; //$NON-NLS-1$
			case JSParseNodeTypes.POST_DECREMENT:
				return "post-decrement"; //$NON-NLS-1$
			case JSParseNodeTypes.POST_INCREMENT:
				return "post-increment"; //$NON-NLS-1$
			case JSParseNodeTypes.PRE_DECREMENT:
				return "pre-decrement"; //$NON-NLS-1$
			case JSParseNodeTypes.PRE_INCREMENT:
				return "pre-increment"; //$NON-NLS-1$
			case JSParseNodeTypes.RETURN:
				return "return"; //$NON-NLS-1$
			case JSParseNodeTypes.THROW:
				return "throw"; //$NON-NLS-1$
			case JSParseNodeTypes.TYPEOF:
				return "typeof"; //$NON-NLS-1$
			case JSParseNodeTypes.VOID:
				return "void"; //$NON-NLS-1$
			case JSParseNodeTypes.WHILE:
				return "while"; //$NON-NLS-1$
			case JSParseNodeTypes.WITH:
				return "with"; //$NON-NLS-1$
			default:
				return super.getName();
		}
	}

	/*
	 * Constructors
	 */

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getSource(com.aptana.ide.io.SourceWriter)
	 */
	public void getSource(SourceWriter writer)
	{
		switch (this.getTypeIndex())
		{
			case JSParseNodeTypes.ASSIGN:
				((JSParseNode) this.getChild(0)).getSource(writer); // left
				writer.print(" = "); //$NON-NLS-1$
				((JSParseNode) this.getChild(1)).getSource(writer); // right
				break;

			case JSParseNodeTypes.CATCH:
				writer.print("catch ("); //$NON-NLS-1$
				((JSParseNode) this.getChild(0)).getSource(writer); // name
				writer.print(") "); //$NON-NLS-1$
				((JSParseNode) this.getChild(1)).getSource(writer); // body
				break;

			case JSParseNodeTypes.CONDITIONAL:
				((JSParseNode) this.getChild(0)).getSource(writer); // condition
				writer.print(" ? "); //$NON-NLS-1$
				((JSParseNode) this.getChild(1)).getSource(writer); // true case
				writer.print(" : "); //$NON-NLS-1$
				((JSParseNode) this.getChild(2)).getSource(writer); // false case
				break;

			case JSParseNodeTypes.CONSTRUCT:
				writer.print("new "); //$NON-NLS-1$
				((JSParseNode) this.getChild(0)).getSource(writer); // function
				writer.print("("); //$NON-NLS-1$
				((JSParseNode) this.getChild(1)).getSource(writer); // args
				writer.print(")"); //$NON-NLS-1$
				break;

			case JSParseNodeTypes.DECLARATION:
				((JSParseNode) this.getChild(0)).getSource(writer); // name
				if (this.getChildCount() > 1 && this.getChild(1).getTypeIndex() != JSParseNodeTypes.EMPTY)
				{
					writer.print(" = "); //$NON-NLS-1$
					((JSParseNode) this.getChild(1)).getSource(writer); // assignment
				}
				break;

			case JSParseNodeTypes.DO:
				JSParseNode doBody = (JSParseNode) this.getChild(0);
				
				writer.print("do "); //$NON-NLS-1$
				doBody.getSource(writer);
				if (doBody.getTypeIndex() != JSParseNodeTypes.STATEMENTS)
				{
					writer.print(";"); //$NON-NLS-1$
				}
				writer.print(" while ("); //$NON-NLS-1$
				((JSParseNode) this.getChild(1)).getSource(writer); // condition
				writer.print(")"); //$NON-NLS-1$
				break;

			case JSParseNodeTypes.EMPTY:
				break;

			case JSParseNodeTypes.FINALLY:
				writer.print("finally "); //$NON-NLS-1$
				((JSParseNode) this.getChild(0)).getSource(writer); // body
				break;

			case JSParseNodeTypes.FOR_IN:
				JSParseNode initializer = ((JSParseNode) this.getChild(0));
				JSParseNode object = ((JSParseNode) this.getChild(1));
				JSParseNode body = ((JSParseNode) this.getChild(2));
				
				writer.print("for ("); //$NON-NLS-1$
				initializer.getSource(writer);
				writer.print(" in "); //$NON-NLS-1$
				object.getSource(writer);
				writer.print(") "); //$NON-NLS-1$
				body.getSource(writer); // body
				break;

			case JSParseNodeTypes.FOR:
				initializer = ((JSParseNode) this.getChild(0));
				JSParseNode condition = ((JSParseNode) this.getChild(1));
				JSParseNode advance = ((JSParseNode) this.getChild(2));
				body = ((JSParseNode) this.getChild(3));
				
				writer.print("for ("); //$NON-NLS-1$
				
				if (initializer.isEmpty() == false)
				{
					initializer.getSource(writer);
				}
				writer.print(";"); //$NON-NLS-1$
				
				if (condition.isEmpty() == false)
				{
					writer.print(" "); //$NON-NLS-1$
					condition.getSource(writer);
				}
				writer.print(";"); //$NON-NLS-1$
				
				if (advance.isEmpty() == false)
				{
					writer.print(" "); //$NON-NLS-1$
					advance.getSource(writer);
				}
				writer.print(") "); //$NON-NLS-1$
				
				body.getSource(writer);
				break;

			case JSParseNodeTypes.FUNCTION:
				JSParseNode parameters = ((JSParseNode) this.getChild(0));
				body = ((JSParseNode) this.getChild(1));

				// output keyword
				writer.print("function"); //$NON-NLS-1$

				// output name, if we have one
				if (this.hasAttribute("name")) //$NON-NLS-1$
				{
					writer.print(" "); //$NON-NLS-1$
					writer.print(this.getAttribute("name")); //$NON-NLS-1$
					writer.print(" "); //$NON-NLS-1$
				}

				// open parameter list
				writer.print("("); //$NON-NLS-1$

				// output parameters, if we have any
				if (parameters.isEmpty() == false)
				{
					parameters.getSource(writer);
				}

				// close parameter list
				writer.print(") "); //$NON-NLS-1$

				// output body
				body.getSource(writer);
				break;

			case JSParseNodeTypes.IF:
				condition = ((JSParseNode) this.getChild(0));
				JSParseNode trueCase = ((JSParseNode) this.getChild(1));
				JSParseNode falseCase = ((JSParseNode) this.getChild(2));
				
				writer.print("if ("); //$NON-NLS-1$
				condition.getSource(writer);
				writer.print(") "); //$NON-NLS-1$
				
				trueCase.getSource(writer);
				if (falseCase.isEmpty() == false)
				{
					if (trueCase.getTypeIndex() != JSParseNodeTypes.STATEMENTS)
					{
						writer.print(";"); //$NON-NLS-1$
					}
					writer.print(" else "); //$NON-NLS-1$
					falseCase.getSource(writer);
				}
				break;

			case JSParseNodeTypes.INVOKE:
				((JSParseNode) this.getChild(0)).getSource(writer); // function
				writer.print("("); //$NON-NLS-1$
				((JSParseNode) this.getChild(1)).getSource(writer); // arguments
				writer.print(")"); //$NON-NLS-1$
				break;

			case JSParseNodeTypes.LABELLED:
				((JSParseNode) this.getChild(0)).getSource(writer); // label
				writer.print(": "); //$NON-NLS-1$
				((JSParseNode) this.getChild(1)).getSource(writer); // statement
				break;

			case JSParseNodeTypes.NAME_VALUE_PAIR:
				((JSParseNode) this.getChild(0)).getSource(writer); // name
				writer.print(": "); //$NON-NLS-1$
				((JSParseNode) this.getChild(1)).getSource(writer); // value
				break;

			case JSParseNodeTypes.THIS:
				writer.print("this"); //$NON-NLS-1$
				break;

			case JSParseNodeTypes.TRY:
				body = ((JSParseNode) this.getChild(0));
				JSParseNode catchNode = ((JSParseNode) this.getChild(1));
				JSParseNode finallyNode = ((JSParseNode) this.getChild(2));
				
				writer.print("try "); //$NON-NLS-1$
				body.getSource(writer);
				
				if (catchNode.isEmpty() == false)
				{
					writer.print(" "); //$NON-NLS-1$
					catchNode.getSource(writer);
				}
				
				if (finallyNode.isEmpty() == false)
				{
					writer.print(" "); //$NON-NLS-1$
					finallyNode.getSource(writer);
				}
				break;

			case JSParseNodeTypes.WHILE:
				writer.print("while ("); //$NON-NLS-1$
				((JSParseNode) this.getChild(0)).getSource(writer); // condition
				writer.print(") "); //$NON-NLS-1$
				((JSParseNode) this.getChild(1)).getSource(writer); // body
				break;

			case JSParseNodeTypes.WITH:
				writer.print("with ("); //$NON-NLS-1$
				((JSParseNode) this.getChild(0)).getSource(writer); // expression
				writer.print(") "); //$NON-NLS-1$
				((JSParseNode) this.getChild(1)).getSource(writer); // body
				break;

			case JSParseNodeTypes.ERROR:
				writer.print("ERROR"); //$NON-NLS-1$
				break;
				
			default:
				break;				
		}
	}

	/**
	 * Determines if this is an empty node
	 * 
	 * @return Returns true if this is an empty node
	 */
	public boolean isEmpty()
	{
		return this.getTypeIndex() == JSParseNodeTypes.EMPTY;
	}

	/**
	 * setIncludesSemicolon
	 *
	 * @param value
	 */
	public void setIncludesSemicolon(boolean value)
	{
		this._includesSemicolon = value;
	}

	/**
	 * @see com.aptana.ide.editors.actions.ICanBeNotNavigatable#isNavigatable()
	 */
	public boolean isNavigatable()
	{
		return false;
	}
}
