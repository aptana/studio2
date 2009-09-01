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
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.ParseNodeFactory;

/**
 * @author Kevin Lindsey
 */
public class JSParseNodeFactory extends ParseNodeFactory<JSParseNode>
{
	/**
	 * Create a new instance of JSParseNodeFactory
	 * 
	 * @param owningParseState
	 */
	public JSParseNodeFactory(IParseState owningParseState)
	{
		super(owningParseState);
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNodeFactory#createParseNode(int, Lexeme)
	 */
	public JSParseNode createParseNode(int typeIndex, Lexeme startingLexeme)
	{
		JSParseNode result = null;

		switch (typeIndex)
		{
			// JSParseNodes
			case JSParseNodeTypes.ASSIGN:
			case JSParseNodeTypes.CATCH:
			case JSParseNodeTypes.CONDITIONAL:
			case JSParseNodeTypes.CONSTRUCT:
			case JSParseNodeTypes.DECLARATION:
			case JSParseNodeTypes.DO:
			case JSParseNodeTypes.EMPTY:
			case JSParseNodeTypes.FINALLY:
			case JSParseNodeTypes.FOR_IN:
			case JSParseNodeTypes.FOR:
			case JSParseNodeTypes.IF:
			case JSParseNodeTypes.INVOKE:
			case JSParseNodeTypes.LABELLED:
			case JSParseNodeTypes.NAME_VALUE_PAIR:
			case JSParseNodeTypes.TRY:
			case JSParseNodeTypes.WHILE:
			case JSParseNodeTypes.WITH:
			case JSParseNodeTypes.ERROR:
				result = new JSParseNode(typeIndex, startingLexeme);
				break;

			case JSParseNodeTypes.FUNCTION:
				result = new JSFunctionNode(startingLexeme);
				break;

			// BinaryOperatorAssignNodes
			case JSParseNodeTypes.ADD_AND_ASSIGN:
			case JSParseNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN:
			case JSParseNodeTypes.BITWISE_AND_AND_ASSIGN:
			case JSParseNodeTypes.BITWISE_OR_AND_ASSIGN:
			case JSParseNodeTypes.BITWISE_XOR_AND_ASSIGN:
			case JSParseNodeTypes.DIVIDE_AND_ASSIGN:
			case JSParseNodeTypes.MOD_AND_ASSIGN:
			case JSParseNodeTypes.MULTIPLY_AND_ASSIGN:
			case JSParseNodeTypes.SHIFT_LEFT_AND_ASSIGN:
			case JSParseNodeTypes.SHIFT_RIGHT_AND_ASSIGN:
			case JSParseNodeTypes.SUBTRACT_AND_ASSIGN:
				result = new JSBinaryOperatorAssignNode(typeIndex, startingLexeme);
				break;

			// BinaryOperatorNodes
			case JSParseNodeTypes.GET_ELEMENT:
			case JSParseNodeTypes.GET_PROPERTY:
				result = new JSBinaryOperatorNode(typeIndex, startingLexeme);
				break;

			// LogicalBinaryOperatorNodes
			case JSParseNodeTypes.EQUAL:
			case JSParseNodeTypes.GREATER_THAN:
			case JSParseNodeTypes.GREATER_THAN_OR_EQUAL:
			case JSParseNodeTypes.IDENTITY:
			case JSParseNodeTypes.IN:
			case JSParseNodeTypes.INSTANCE_OF:
			case JSParseNodeTypes.LESS_THAN:
			case JSParseNodeTypes.LESS_THAN_OR_EQUAL:
			case JSParseNodeTypes.LOGICAL_AND:
			case JSParseNodeTypes.LOGICAL_OR:
			case JSParseNodeTypes.NOT_EQUAL:
			case JSParseNodeTypes.NOT_IDENTITY:
				result = new JSLogicalBinaryOperatorNode(typeIndex, startingLexeme);
				break;

			// NumericalBinaryOperatorNode
			case JSParseNodeTypes.ADD:
			case JSParseNodeTypes.ARITHMETIC_SHIFT_RIGHT:
			case JSParseNodeTypes.BITWISE_AND:
			case JSParseNodeTypes.BITWISE_OR:
			case JSParseNodeTypes.BITWISE_XOR:
			case JSParseNodeTypes.DIVIDE:
			case JSParseNodeTypes.MOD:
			case JSParseNodeTypes.MULTIPLY:
			case JSParseNodeTypes.SHIFT_LEFT:
			case JSParseNodeTypes.SHIFT_RIGHT:
			case JSParseNodeTypes.SUBTRACT:
				result = new JSNumericalBinaryOperatorNode(typeIndex, startingLexeme);
				break;

			// LabelNode
			case JSParseNodeTypes.BREAK:
			case JSParseNodeTypes.CONTINUE:
				result = new JSLabelNode(typeIndex, startingLexeme);
				break;

			// NaryNode
			case JSParseNodeTypes.ARGUMENTS:
			case JSParseNodeTypes.ARRAY_LITERAL:
			case JSParseNodeTypes.COMMA:
			case JSParseNodeTypes.DEFAULT:
			case JSParseNodeTypes.OBJECT_LITERAL:
			case JSParseNodeTypes.PARAMETERS:
			case JSParseNodeTypes.STATEMENTS:
			case JSParseNodeTypes.VAR:
				result = new JSNaryNode(typeIndex, startingLexeme);
				break;

			// NaryAndExpressionNode
			case JSParseNodeTypes.CASE:
			case JSParseNodeTypes.SWITCH:
				result = new JSNaryAndExpressionNode(typeIndex, startingLexeme);
				break;

			// PrimitiveNode
			case JSParseNodeTypes.FALSE:
			case JSParseNodeTypes.IDENTIFIER:
			case JSParseNodeTypes.NULL:
			case JSParseNodeTypes.NUMBER:
			case JSParseNodeTypes.REGULAR_EXPRESSION:
			case JSParseNodeTypes.THIS:
			case JSParseNodeTypes.TRUE:
				result = new JSPrimitiveNode(typeIndex, startingLexeme);
				break;
			
			// String
			case JSParseNodeTypes.STRING:
				result = new JSStringNode(startingLexeme);
				break;

			// UnaryOperatorNode
			case JSParseNodeTypes.DELETE:
			case JSParseNodeTypes.GROUP:
			case JSParseNodeTypes.LOGICAL_NOT:
			case JSParseNodeTypes.RETURN:
			case JSParseNodeTypes.THROW:
			case JSParseNodeTypes.TYPEOF:
			case JSParseNodeTypes.VOID:
				result = new JSUnaryOperatorNode(typeIndex, startingLexeme);
				break;

			// NumericalUnaryOperatorNode
			case JSParseNodeTypes.BITWISE_NOT:
			case JSParseNodeTypes.NEGATE:
			case JSParseNodeTypes.POSITIVE:
			case JSParseNodeTypes.POST_DECREMENT:
			case JSParseNodeTypes.POST_INCREMENT:
			case JSParseNodeTypes.PRE_DECREMENT:
			case JSParseNodeTypes.PRE_INCREMENT:
				result = new JSNumericalUnaryOperatorNode(typeIndex, startingLexeme);
				break;

			default:
				throw new IllegalArgumentException(Messages.JSParseNodeFactory_UnknownParseNodeType + typeIndex);
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.ParseNodeFactory#createRootNode()
	 */
	public IParseNode createRootNode()
	{
		IParseNode result = this.createParseNode(JSParseNodeTypes.STATEMENTS, null);
		
		result.setAttribute("xmlns", JSMimeType.MimeType); //$NON-NLS-1$
		
		return result;
	}
}
