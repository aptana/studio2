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
package com.aptana.ide.editor.js.parsing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.nodes.JSFunctionNode;
import com.aptana.ide.editor.js.parsing.nodes.JSParseNode;
import com.aptana.ide.editor.js.parsing.nodes.JSParseNodeTypes;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.ParseFragment;

/**
 * @author Kevin Lindsey
 */
public class JSASTHandler extends JSAbstractHandler
{
	private List<JSParseNode> _statements;
	
	/**
	 * JSASTHandler
	 */
	public JSASTHandler()
	{
		super();
		
		this._statements = new ArrayList<JSParseNode>();
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#afterParse(com.aptana.ide.parsing.IParseState, com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void afterParse(IParseState parseState, IParseNode parentNode)
	{
		if (parentNode != null)
		{
			Object[] results = this.getValues();
			
			if (results != null && results.length > 0)
			{
				Object result = results[0];
				
				if (result instanceof ParseFragment)
				{
					parentNode.appendChild((ParseFragment) result);
				}
			}
		}

		this._statements.clear();
		
		super.afterParse(parseState, parentNode);
	}
	
	/**
	 * onAddArgument
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onAddArgument(Object[] nodes)
	{
		ParseFragment arguments = (ParseFragment) nodes[0];
		JSParseNode argument = (JSParseNode) nodes[2];
		
		arguments.appendChild(argument);
		
		return arguments;
	}

	/**
	 * onAddCaseClause
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onAddCaseClause(Object[] nodes)
	{
		ParseFragment clauses = (ParseFragment) nodes[0];
		JSParseNode clause = (JSParseNode) nodes[1];
		
		clauses.appendChild(clause);
		
		return clauses;
	}

	/**
	 * onAddElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onAddElement(Object[] nodes)
	{
		ParseFragment elements = (ParseFragment) nodes[0];
		JSParseNode element = (JSParseNode) nodes[2];
		
		elements.appendChild(element);
		
		return elements;
	}

	/**
	 * onAddElidedElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onAddElidedElement(Object[] nodes)
	{
		ParseFragment elements = (ParseFragment) nodes[0];
		ParseFragment elisions = (ParseFragment) nodes[2];
		JSParseNode element = (JSParseNode) nodes[3];
		
		elements.appendChild(elisions);
		elements.appendChild(element);
		
		return elements;
	}

	/**
	 * onAddElision
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onAddElision(Object[] nodes)
	{
		JSParseNode nullNode = this.createNode(JSParseNodeTypes.NULL, null);
		ParseFragment elisions = (ParseFragment) nodes[0];
		
		elisions.appendChild(nullNode);
		
		return elisions;
	}

	/**
	 * onAddParameter
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onAddParameter(Object[] nodes)
	{
		JSParseNode parameters = (JSParseNode) nodes[0];
		Lexeme name = (Lexeme) nodes[2];
		
		JSParseNode identifier = this.createNode(JSParseNodeTypes.IDENTIFIER, name);
		
		parameters.appendChild(identifier);
		
		return parameters;
	}

	/**
	 * onAddProperty
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onAddProperty(Object[] nodes)
	{
		ParseFragment properties = (ParseFragment) nodes[0];
		Lexeme identifier = (Lexeme) nodes[2];
		JSParseNode name = this.createNode(JSParseNodeTypes.IDENTIFIER, identifier);
		JSParseNode value = (JSParseNode) nodes[4];
		
		JSParseNode property = this.createNode(JSParseNodeTypes.NAME_VALUE_PAIR, null);
		property.appendChild(name);
		property.appendChild(value);
		
		properties.appendChild(property);
		
		return properties;
	}

	/**
	 * onAddSourceElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onAddSourceElement(Object[] nodes)
	{
		ParseFragment fragment = (ParseFragment) nodes[0];
		JSParseNode element = (JSParseNode) nodes[1];
		
		fragment.appendChild(element);
		
		return fragment;
	}

	/**
	 * onAddStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onAddStatement(Object[] nodes)
	{
		JSParseNode statements = (JSParseNode) nodes[0];
		JSParseNode statement = (JSParseNode) nodes[1];
		
		statements.appendChild(statement);
		
		return statements;
	}

	/**
	 * onAddVarDeclaration
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onAddVarDeclaration(Object[] nodes)
	{
		ParseFragment declarations = (ParseFragment) nodes[0];
		JSParseNode declaration = (JSParseNode) nodes[2];
		
		declarations.appendChild(declaration);
		
		return declarations;
	}

	/**
	 * onArguments
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onArguments(Object[] nodes)
	{
		Lexeme lparen = (Lexeme) nodes[0];
		ParseFragment arguments = (ParseFragment) nodes[1];
		Lexeme rparen = (Lexeme) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.ARGUMENTS, lparen);
		result.appendChild(arguments);
		result.includeLexemeInRange(rparen);
		
		return result;
	}

	/**
	 * onArrayLiteral
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onArrayLiteral(Object[] nodes)
	{
		Lexeme lbracket = (Lexeme) nodes[0];
		ParseFragment elements = (ParseFragment) nodes[1];
		Lexeme rbracket = (Lexeme) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.ARRAY_LITERAL, lbracket);
		result.appendChild(elements);
		result.includeLexemeInRange(rbracket);
		
		return result;
	}

	/**
	 * onArrayLiteralTrailingComma
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onArrayLiteralTrailingComma(Object[] nodes)
	{
		Lexeme lbracket = (Lexeme) nodes[0];
		ParseFragment elements = (ParseFragment) nodes[1];
		JSParseNode nullNode = this.createNode(JSParseNodeTypes.NULL, null);
		Lexeme rbracket = (Lexeme) nodes[3];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.ARRAY_LITERAL, lbracket);
		result.appendChild(elements);
		result.appendChild(nullNode);
		result.includeLexemeInRange(rbracket);
		
		return result;
	}

	/**
	 * onArrayLiteralTrailingElision
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onArrayLiteralTrailingElision(Object[] nodes)
	{
		Lexeme lbracket = (Lexeme) nodes[0];
		ParseFragment elements = (ParseFragment) nodes[1];
		ParseFragment elisions = (ParseFragment) nodes[3];
		Lexeme rbracket = (Lexeme) nodes[4];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.ARRAY_LITERAL, lbracket);
		result.appendChild(elements);
		result.appendChild(elisions);
		result.includeLexemeInRange(rbracket);
		
		return result;
	}

	/**
	 * onAssignmentExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onAssignmentExpression(Object[] nodes)
	{
		JSParseNode lhs = (JSParseNode) nodes[0];
		Lexeme operator = (Lexeme) nodes[1];
		JSParseNode rhs = (JSParseNode) nodes[2];
		
		JSParseNode result;
		
		switch (operator.typeIndex)
		{
			case JSTokenTypes.EQUAL:
				result = this.createNode(JSParseNodeTypes.ASSIGN, operator);
				break;
				
			case JSTokenTypes.STAR_EQUAL:
				result = this.createNode(JSParseNodeTypes.MULTIPLY_AND_ASSIGN, operator);
				break;
				
			case JSTokenTypes.FORWARD_SLASH_EQUAL:
				result = this.createNode(JSParseNodeTypes.DIVIDE_AND_ASSIGN, operator);
				break;
				
			case JSTokenTypes.PERCENT_EQUAL:
				result = this.createNode(JSParseNodeTypes.MOD_AND_ASSIGN, operator);
				break;
				
			case JSTokenTypes.PLUS_EQUAL:
				result = this.createNode(JSParseNodeTypes.ADD_AND_ASSIGN, operator);
				break;
				
			case JSTokenTypes.MINUS_EQUAL:
				result = this.createNode(JSParseNodeTypes.SUBTRACT_AND_ASSIGN, operator);
				break;
				
			case JSTokenTypes.LESS_LESS_EQUAL:
				result = this.createNode(JSParseNodeTypes.SHIFT_LEFT_AND_ASSIGN, operator);
				break;
				
			case JSTokenTypes.GREATER_GREATER_EQUAL:
				result = this.createNode(JSParseNodeTypes.SHIFT_RIGHT_AND_ASSIGN, operator);
				break;
				
			case JSTokenTypes.GREATER_GREATER_GREATER_EQUAL:
				result = this.createNode(JSParseNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN, operator);
				break;
				
			case JSTokenTypes.AMPERSAND_EQUAL:
				result = this.createNode(JSParseNodeTypes.BITWISE_AND_AND_ASSIGN, operator);
				break;

			case JSTokenTypes.CARET_EQUAL:
				result = this.createNode(JSParseNodeTypes.BITWISE_XOR_AND_ASSIGN, operator);
				break;

			case JSTokenTypes.PIPE_EQUAL:
				result = this.createNode(JSParseNodeTypes.BITWISE_OR_AND_ASSIGN, operator);
				break;

			default:
				throw new IllegalArgumentException(MessageFormat.format(Messages.JSASTHandler_Unknown_operator_0, operator));
		}
		
		result.appendChild(lhs);
		result.appendChild(rhs);
		
		return result;
	}

	/**
	 * onBinaryExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onBinaryExpression(Object[] nodes)
	{
		JSParseNode lhs = (JSParseNode) nodes[0];
		Lexeme operator = (Lexeme) nodes[1];
		JSParseNode rhs = (JSParseNode) nodes[2];
		
		JSParseNode result;
		
		switch (operator.typeIndex)
		{
			case JSTokenTypes.STAR:
				result = this.createNode(JSParseNodeTypes.MULTIPLY, operator);
				break;

			case JSTokenTypes.FORWARD_SLASH:
				result = this.createNode(JSParseNodeTypes.DIVIDE, operator);
				break;

			case JSTokenTypes.PERCENT:
				result = this.createNode(JSParseNodeTypes.MOD, operator);
				break;
				
			case JSTokenTypes.MINUS:
				result = this.createNode(JSParseNodeTypes.SUBTRACT, operator);
				break;

			case JSTokenTypes.PLUS:
				result = this.createNode(JSParseNodeTypes.ADD, operator);
				break;
				
			case JSTokenTypes.LESS_LESS:
				result = this.createNode(JSParseNodeTypes.SHIFT_LEFT, operator);
				break;

			case JSTokenTypes.GREATER_GREATER:
				result = this.createNode(JSParseNodeTypes.SHIFT_RIGHT, operator);
				break;

			case JSTokenTypes.GREATER_GREATER_GREATER:
				result = this.createNode(JSParseNodeTypes.ARITHMETIC_SHIFT_RIGHT, operator);
				break;
				
			case JSTokenTypes.LESS:
				result = this.createNode(JSParseNodeTypes.LESS_THAN, operator);
				break;
	
			case JSTokenTypes.GREATER:
				result = this.createNode(JSParseNodeTypes.GREATER_THAN, operator);
				break;
	
			case JSTokenTypes.LESS_EQUAL:
				result = this.createNode(JSParseNodeTypes.LESS_THAN_OR_EQUAL, operator);
				break;
	
			case JSTokenTypes.GREATER_EQUAL:
				result = this.createNode(JSParseNodeTypes.GREATER_THAN_OR_EQUAL, operator);
				break;
	
			case JSTokenTypes.INSTANCEOF:
				result = this.createNode(JSParseNodeTypes.INSTANCE_OF, operator);
				break;
	
			case JSTokenTypes.IN:
				result = this.createNode(JSParseNodeTypes.IN, operator);
				break;
				
			case JSTokenTypes.EQUAL_EQUAL:
				result = this.createNode(JSParseNodeTypes.EQUAL, operator);
				break;

			case JSTokenTypes.EXCLAMATION_EQUAL:
				result = this.createNode(JSParseNodeTypes.NOT_EQUAL, operator);
				break;

			case JSTokenTypes.EQUAL_EQUAL_EQUAL:
				result = this.createNode(JSParseNodeTypes.IDENTITY, operator);
				break;

			case JSTokenTypes.EXCLAMATION_EQUAL_EQUAL:
				result = this.createNode(JSParseNodeTypes.NOT_IDENTITY, operator);
				break;
				
			case JSTokenTypes.AMPERSAND:
				result = this.createNode(JSParseNodeTypes.BITWISE_AND, operator);
				break;
				
			case JSTokenTypes.CARET:
				result = this.createNode(JSParseNodeTypes.BITWISE_XOR, operator);
				break;
				
			case JSTokenTypes.PIPE:
				result = this.createNode(JSParseNodeTypes.BITWISE_OR, operator);
				break;
				
			case JSTokenTypes.AMPERSAND_AMPERSAND:
				result = this.createNode(JSParseNodeTypes.LOGICAL_AND, operator);
				break;
				
			case JSTokenTypes.PIPE_PIPE:
				result = this.createNode(JSParseNodeTypes.LOGICAL_OR, operator);
				break;
				
			default:
				throw new IllegalArgumentException(MessageFormat.format(Messages.JSASTHandler_Unknown_operator_0, operator));
		}
		
		result.appendChild(lhs);
		result.appendChild(rhs);
		
		return result;
	}

	/**
	 * onBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onBlock(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		Lexeme rcurly = (Lexeme) nodes[2];
		JSParseNode statements = (JSParseNode) nodes[1];
		
		statements.includeLexemesInRange(lcurly, rcurly);
		
		return statements;
	}

	/**
	 * onBreak
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onBreak(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme semicolon = (Lexeme) nodes[1];
		
		JSParseNode label = this.createNode(JSParseNodeTypes.EMPTY, null);
		
		JSParseNode result = this.createNode(JSParseNodeTypes.BREAK, keyword);
		result.appendChild(label);
		result.includeLexemeInRange(semicolon);
		result.setIncludesSemicolon(true);
		
		return result;
	}

	/**
	 * onBreakLabel
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onBreakLabel(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme identifier = (Lexeme )nodes[1];
		Lexeme semicolon = (Lexeme) nodes[2];
		
		JSParseNode label = this.createNode(JSParseNodeTypes.IDENTIFIER, identifier);
		
		JSParseNode result = this.createNode(JSParseNodeTypes.BREAK, keyword);
		result.appendChild(label);
		result.includeLexemeInRange(semicolon);
		result.setIncludesSemicolon(true);
		
		return result;
	}

	/**
	 * onCallExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onCallExpression(Object[] nodes)
	{
		JSParseNode expression = (JSParseNode) nodes[0];
		JSParseNode arguments = (JSParseNode) nodes[1];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.INVOKE, null);
		result.appendChild(expression);
		result.appendChild(arguments);
		
		return result;
	}

	/**
	 * onCaseClause
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onCaseClause(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode expression = (JSParseNode) nodes[1];
		JSParseNode statements = (JSParseNode) nodes[3];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.CASE, keyword);
		result.appendChild(expression);
		
		for (int i = 0; i < statements.getChildCount(); i++)
		{
			result.appendChild(statements.getChild(i));
		}
		
		return result;
	}

	/**
	 * onCasesAndDefaultBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onCasesAndDefaultBlock(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		ParseFragment cases = (ParseFragment) nodes[1];
		JSParseNode defaultClause = (JSParseNode) nodes[2];
		Lexeme rcurly = (Lexeme) nodes[3];
		
		ParseFragment result = new ParseFragment();
		result.appendChild(cases);
		result.appendChild(defaultClause);
		result.includeLexemesInRange(lcurly, rcurly);
		
		return result;
	}

	/**
	 * onCasesBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onCasesBlock(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		ParseFragment cases = (ParseFragment) nodes[1];
		Lexeme rcurly = (Lexeme) nodes[2];
		
		ParseFragment result = new ParseFragment();
		result.appendChild(cases);
		result.includeLexemesInRange(lcurly, rcurly);
		
		return result;
	}

	/**
	 * onCasesDefaultCasesBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onCasesDefaultCasesBlock(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		ParseFragment cases1 = (ParseFragment) nodes[1];
		JSParseNode defaultClause = (JSParseNode) nodes[2];
		ParseFragment cases2 = (ParseFragment) nodes[3];
		Lexeme rcurly = (Lexeme) nodes[4];
		
		ParseFragment result = new ParseFragment();
		result.appendChild(cases1);
		result.appendChild(defaultClause);
		result.appendChild(cases2);
		result.includeLexemesInRange(lcurly, rcurly);
		
		return result;
	}

	/**
	 * onCatch
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onCatch(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme identifier = (Lexeme) nodes[2];
		JSParseNode name = this.createNode(JSParseNodeTypes.IDENTIFIER, identifier);
		JSParseNode body = (JSParseNode) nodes[4];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.CATCH, keyword);
		result.appendChild(name);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onCommaExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onCommaExpression(Object[] nodes)
	{
		JSParseNode lhs = (JSParseNode) nodes[0];
		JSParseNode rhs = (JSParseNode) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.COMMA, null);
		result.appendChild(lhs);
		result.appendChild(rhs);
		
		return result;
	}

	/**
	 * onConditionalExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onConditionalExpression(Object[] nodes)
	{
		JSParseNode condition = (JSParseNode) nodes[0];
		JSParseNode trueCase = (JSParseNode) nodes[2];
		JSParseNode falseCase = (JSParseNode) nodes[4];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.CONDITIONAL, null);
		result.appendChild(condition);
		result.appendChild(trueCase);
		result.appendChild(falseCase);
		
		return result;
	}

	/**
	 * onContinue
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onContinue(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme semicolon = (Lexeme) nodes[1];
		
		JSParseNode label = this.createNode(JSParseNodeTypes.EMPTY, null);
		
		JSParseNode result = this.createNode(JSParseNodeTypes.CONTINUE, keyword);
		result.appendChild(label);
		result.includeLexemeInRange(semicolon);
		result.setIncludesSemicolon(true);
		
		return result;
	}

	/**
	 * onContinueLabel
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onContinueLabel(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme identifier = (Lexeme )nodes[1];
		Lexeme semicolon = (Lexeme) nodes[2];
		
		JSParseNode label = this.createNode(JSParseNodeTypes.IDENTIFIER, identifier);
		
		JSParseNode result = this.createNode(JSParseNodeTypes.CONTINUE, keyword);
		result.appendChild(label);
		result.includeLexemeInRange(semicolon);
		result.setIncludesSemicolon(true);
		
		return result;
	}

	/**
	 * onDefaultAndCasesBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onDefaultAndCasesBlock(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		JSParseNode defaultClause = (JSParseNode) nodes[1];
		ParseFragment cases = (ParseFragment) nodes[2];
		Lexeme rcurly = (Lexeme) nodes[3];
		
		ParseFragment result = new ParseFragment();
		result.appendChild(defaultClause);
		result.appendChild(cases);
		result.includeLexemesInRange(lcurly, rcurly);
		
		return result;
	}

	/**
	 * onDefaultBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onDefaultBlock(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		JSParseNode defaultClause = (JSParseNode) nodes[1];
		Lexeme rcurly = (Lexeme) nodes[2];
		
		ParseFragment result = new ParseFragment();
		result.appendChild(defaultClause);
		result.includeLexemesInRange(lcurly, rcurly);
		
		return result;
	}

	/**
	 * onDefaultClause
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onDefaultClause(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode statements = (JSParseNode) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.DEFAULT, keyword);
		
		for (int i = 0; i < statements.getChildCount(); i++)
		{
			result.appendChild(statements.getChild(i));
		}
		
		return result;
	}

	/**
	 * onDoStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onDoStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode body = (JSParseNode) nodes[1];
		JSParseNode condition = (JSParseNode) nodes[4];
		Lexeme semicolon = (Lexeme) nodes[5];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.DO, keyword);
		result.appendChild(body);
		result.appendChild(condition);
		result.includeLexemeInRange(semicolon);
		result.setIncludesSemicolon(true);
		
		return result;
	}

	/**
	 * onElidedArray
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onElidedArray(Object[] nodes)
	{
		Lexeme lbracket = (Lexeme) nodes[0];
		ParseFragment elisions = (ParseFragment) nodes[1];
		Lexeme rbracket = (Lexeme) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.ARRAY_LITERAL, lbracket);
		result.appendChild(elisions);
		result.includeLexemeInRange(rbracket);
		
		return result;
	}

	/**
	 * onEmptyArguments
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onEmptyArguments(Object[] nodes)
	{
		Lexeme lparen = (Lexeme) nodes[0];
		Lexeme rparen = (Lexeme) nodes[1];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.ARGUMENTS, lparen);
		result.includeLexemeInRange(rparen);
		
		return result;
	}

	/**
	 * onEmptyArray
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onEmptyArray(Object[] nodes)
	{
		Lexeme lbracket = (Lexeme) nodes[0];
		Lexeme rbracket = (Lexeme) nodes[1];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.ARRAY_LITERAL, lbracket);
		result.includeLexemeInRange(rbracket);
		
		return result;
	}

	/**
	 * onEmptyBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onEmptyBlock(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		Lexeme rcurly = (Lexeme) nodes[1];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.STATEMENTS, lcurly);
		result.includeLexemeInRange(rcurly);
		
		return result;
	}

	/**
	 * onEmptyCaseBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onEmptyCaseBlock(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		Lexeme rcurly = (Lexeme) nodes[1];
		
		ParseFragment result = new ParseFragment();
		result.includeLexemesInRange(lcurly, rcurly);
		
		return result;
	}

	/**
	 * onEmptyCaseClause
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onEmptyCaseClause(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode expression = (JSParseNode) nodes[1];
		Lexeme colon = (Lexeme) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.CASE, keyword);
		result.appendChild(expression);
		result.includeLexemeInRange(colon);
		
		return result;
	}

	/**
	 * onEmptyDefaultClause
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onEmptyDefaultClause(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme colon = (Lexeme) nodes[1];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.DEFAULT, keyword);
		result.includeLexemeInRange(colon);
		
		return result;
	}

	/**
	 * onEmptyFunctionBody
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onEmptyFunctionBody(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		Lexeme rcurly = (Lexeme) nodes[1];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.STATEMENTS, lcurly);
		result.includeLexemeInRange(rcurly);
		
		return result;
	}

	/**
	 * onEmptyObject
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onEmptyObject(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		Lexeme rcurly = (Lexeme) nodes[1];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.OBJECT_LITERAL, lcurly);
		result.includeLexemeInRange(rcurly);
		
		return result;
	}
	
	/**
	 * onEmptyParameterList
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onEmptyParameterList(Object[] nodes)
	{
		Lexeme lparen = (Lexeme) nodes[0];
		Lexeme rparen = (Lexeme) nodes[1];
		
		JSParseNode params = this.createNode(JSParseNodeTypes.EMPTY, lparen);
		params.includeLexemeInRange(rparen);
		
		return params;
	}

	/**
	 * onEmptyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onEmptyStatement(Object[] nodes)
	{
		Lexeme semicolon = (Lexeme) nodes[0];
		JSParseNode result = this.createNode(JSParseNodeTypes.EMPTY, semicolon);
		
		result.setIncludesSemicolon(true);
		
		return result;
	}

	/**
	 * onExpressionStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onExpressionStatement(Object[] nodes)
	{
		JSParseNode expression = (JSParseNode) nodes[0];
		Lexeme semicolon = (Lexeme) nodes[1];
		
		expression.includeLexemeInRange(semicolon);
		expression.setIncludesSemicolon(true);
		
		return expression;
	}

	/**
	 * onFalse
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFalse(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		return this.createNode(JSParseNodeTypes.FALSE, keyword);
	}

	/**
	 * onFinally
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFinally(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode body = (JSParseNode) nodes[1];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FINALLY, keyword);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onFirstArgument
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFirstArgument(Object[] nodes)
	{
		JSParseNode argument = (JSParseNode) nodes[0];
		
		ParseFragment result = new ParseFragment();
		result.appendChild(argument);
		
		return result;
	}

	/**
	 * onFirstCaseClause
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFirstCaseClause(Object[] nodes)
	{
		JSParseNode clause = (JSParseNode) nodes[0];
		
		ParseFragment result = new ParseFragment();
		result.appendChild(clause);
		
		return result;
	}

	/**
	 * onFirstElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFirstElement(Object[] nodes)
	{
		JSParseNode element = (JSParseNode) nodes[0];
		
		ParseFragment result = new ParseFragment();
		result.appendChild(element);
		
		return result;
	}

	/**
	 * onFirstElidedElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFirstElidedElement(Object[] nodes)
	{
		ParseFragment elisions = (ParseFragment) nodes[0];
		JSParseNode element = (JSParseNode) nodes[1];
		
		ParseFragment result = new ParseFragment();
		result.appendChild(elisions);
		result.appendChild(element);
		
		return result;
	}

	/**
	 * onFirstElision
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFirstElision(Object[] nodes)
	{
		JSParseNode nullNode = this.createNode(JSParseNodeTypes.NULL, null);
		
		ParseFragment result = new ParseFragment();
		result.appendChild(nullNode);
		
		return result;
	}

	/**
	 * onFirstParameter
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFirstParameter(Object[] nodes)
	{
		Lexeme name = (Lexeme) nodes[0];
		JSParseNode identifier = this.createNode(JSParseNodeTypes.IDENTIFIER, name);
		
		JSParseNode parameters = this.createNode(JSParseNodeTypes.PARAMETERS, name);
		parameters.appendChild(identifier);
		
		return parameters;
	}

	/**
	 * onFirstProperty
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFirstProperty(Object[] nodes)
	{
		Lexeme identifier = (Lexeme) nodes[0];
		JSParseNode name = this.createNode(JSParseNodeTypes.IDENTIFIER, identifier);
		JSParseNode value = (JSParseNode) nodes[2];
		
		JSParseNode property = this.createNode(JSParseNodeTypes.NAME_VALUE_PAIR, null);
		property.appendChild(name);
		property.appendChild(value);
		
		ParseFragment result = new ParseFragment();
		result.appendChild(property);
		
		return result;
	}

	/**
	 * onFirstSourceElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFirstSourceElement(Object[] nodes)
	{
		ParseFragment fragment = new ParseFragment();
		JSParseNode element = (JSParseNode) nodes[0];
		
		fragment.appendChild(element);
		
		return fragment;
	}

	/**
	 * onFirstStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFirstStatement(Object[] nodes)
	{
		JSParseNode statement = (JSParseNode) nodes[0];
		JSParseNode result = this.createNode(JSParseNodeTypes.STATEMENTS, null);
		
		result.appendChild(statement);
		
		return result;
	}

	/**
	 * onFirstVarDeclaration
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFirstVarDeclaration(Object[] nodes)
	{
		JSParseNode declaration = (JSParseNode) nodes[0];
		
		ParseFragment result = new ParseFragment();
		result.appendChild(declaration);
		
		return result;
	}

	/**
	 * onForAdvanceOnlyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForAdvanceOnlyStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode initialize = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode condition = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode advance = (JSParseNode) nodes[4];
		JSParseNode body = (JSParseNode) nodes[6];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR, keyword);
		result.appendChild(initialize);
		result.appendChild(condition);
		result.appendChild(advance);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForBodyOnlyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForBodyOnlyStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode initialize = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode condition = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode advance = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode body = (JSParseNode) nodes[5];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR, keyword);
		result.appendChild(initialize);
		result.appendChild(condition);
		result.appendChild(advance);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForConditionOnlyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForConditionOnlyStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode initialize = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode condition = (JSParseNode) nodes[3];
		JSParseNode advance = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode body = (JSParseNode) nodes[6];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR, keyword);
		result.appendChild(initialize);
		result.appendChild(condition);
		result.appendChild(advance);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForInitializeOnlyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForInitializeOnlyStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode initialize = (JSParseNode) nodes[2];
		JSParseNode condition = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode advance = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode body = (JSParseNode) nodes[6];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR, keyword);
		result.appendChild(initialize);
		result.appendChild(condition);
		result.appendChild(advance);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForInStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForInStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode initialize = (JSParseNode) nodes[2];
		JSParseNode object = (JSParseNode) nodes[4];
		JSParseNode body = (JSParseNode) nodes[6];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR_IN, keyword);
		result.appendChild(initialize);
		result.appendChild(object);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForNoAdvanceStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForNoAdvanceStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode initialize = (JSParseNode) nodes[2];
		JSParseNode condition = (JSParseNode) nodes[4];
		JSParseNode advance = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode body = (JSParseNode) nodes[7];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR, keyword);
		result.appendChild(initialize);
		result.appendChild(condition);
		result.appendChild(advance);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForNoConditionStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForNoConditionStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode initialize = (JSParseNode) nodes[2];
		JSParseNode condition = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode advance = (JSParseNode) nodes[5];
		JSParseNode body = (JSParseNode) nodes[7];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR, keyword);
		result.appendChild(initialize);
		result.appendChild(condition);
		result.appendChild(advance);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForNoInitializeStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForNoInitializeStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode initialize = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode condition = (JSParseNode) nodes[3];
		JSParseNode advance = (JSParseNode) nodes[5];
		JSParseNode body = (JSParseNode) nodes[7];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR, keyword);
		result.appendChild(initialize);
		result.appendChild(condition);
		result.appendChild(advance);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode initialize = (JSParseNode) nodes[2];
		JSParseNode condition = (JSParseNode) nodes[4];
		JSParseNode advance = (JSParseNode) nodes[6];
		JSParseNode body = (JSParseNode) nodes[8];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR, keyword);
		result.appendChild(initialize);
		result.appendChild(condition);
		result.appendChild(advance);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForVarInitializeOnlyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForVarInitializeOnlyStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		ParseFragment declarations = (ParseFragment) nodes[3];
		JSParseNode initialize = this.createNode(JSParseNodeTypes.VAR, null);
		initialize.appendChild(declarations);
		
		JSParseNode condition = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode advance = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode body = (JSParseNode) nodes[7];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR, keyword);
		result.appendChild(initialize);
		result.appendChild(condition);
		result.appendChild(advance);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForVarInStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForVarInStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		ParseFragment declarations = (ParseFragment) nodes[3];
		JSParseNode initialize = this.createNode(JSParseNodeTypes.VAR, null);
		initialize.appendChild(declarations);
		
		JSParseNode object = (JSParseNode) nodes[5];
		JSParseNode body = (JSParseNode) nodes[7];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR_IN, keyword);
		result.appendChild(initialize);
		result.appendChild(object);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForVarNoAdvanceStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForVarNoAdvanceStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		ParseFragment declarations = (ParseFragment) nodes[3];
		JSParseNode initialize = this.createNode(JSParseNodeTypes.VAR, null);
		initialize.appendChild(declarations);
		
		JSParseNode condition = (JSParseNode) nodes[5];
		JSParseNode advance = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode body = (JSParseNode) nodes[8];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR, keyword);
		result.appendChild(initialize);
		result.appendChild(condition);
		result.appendChild(advance);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForVarNoConditionStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForVarNoConditionStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		ParseFragment declarations = (ParseFragment) nodes[3];
		JSParseNode initialize = this.createNode(JSParseNodeTypes.VAR, null);
		initialize.appendChild(declarations);
		
		JSParseNode condition = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode advance = (JSParseNode) nodes[6];
		JSParseNode body = (JSParseNode) nodes[8];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR, keyword);
		result.appendChild(initialize);
		result.appendChild(condition);
		result.appendChild(advance);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onForVarStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onForVarStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		ParseFragment declarations = (ParseFragment) nodes[3];
		JSParseNode initialize = this.createNode(JSParseNodeTypes.VAR, null);
		initialize.appendChild(declarations);
		
		JSParseNode condition = (JSParseNode) nodes[5];
		JSParseNode advance = (JSParseNode) nodes[7];
		JSParseNode body = (JSParseNode) nodes[9];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.FOR, keyword);
		result.appendChild(initialize);
		result.appendChild(condition);
		result.appendChild(advance);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onFunctionBody
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFunctionBody(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		ParseFragment statements = (ParseFragment) nodes[1];
		Lexeme rcurly = (Lexeme) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.STATEMENTS, lcurly);
		result.appendChild(statements);
		result.includeLexemeInRange(rcurly);
		
		return result;
	}

	/**
	 * onFunctionDeclaration
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFunctionDeclaration(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme name = (Lexeme) nodes[1];
		JSParseNode params = (JSParseNode) nodes[2];
		JSParseNode body = (JSParseNode) nodes[3];
		
		JSFunctionNode result = (JSFunctionNode) this.createNode(JSParseNodeTypes.FUNCTION, keyword);
		result.appendChild(params);
		result.appendChild(body);
		result.setAttribute("name", name.getText()); //$NON-NLS-1$
		
		return result;
	}

	/**
	 * onFunctionExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onFunctionExpression(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode params = (JSParseNode) nodes[1];
		JSParseNode body = (JSParseNode) nodes[2];
		
		JSFunctionNode result = (JSFunctionNode) this.createNode(JSParseNodeTypes.FUNCTION, keyword);
		result.appendChild(params);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onGetElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onGetElement(Object[] nodes)
	{
		JSParseNode expression = (JSParseNode) nodes[0];
		Lexeme lbrace = (Lexeme) nodes[1];
		JSParseNode indexExpression = (JSParseNode) nodes[2];
		Lexeme rbrace = (Lexeme) nodes[3];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.GET_ELEMENT, lbrace);
		result.appendChild(expression);
		result.appendChild(indexExpression);
		result.includeLexemeInRange(rbrace);
		
		return result;
	}

	/**
	 * onGetProperty
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onGetProperty(Object[] nodes)
	{
		JSParseNode expression = (JSParseNode) nodes[0];
		Lexeme dot = (Lexeme) nodes[1];
		Lexeme identifier = (Lexeme) nodes[2];
		JSParseNode name = this.createNode(JSParseNodeTypes.IDENTIFIER, identifier);
		
		JSParseNode result = this.createNode(JSParseNodeTypes.GET_PROPERTY, dot);
		result.appendChild(expression);
		result.appendChild(name);
		
		return result;
	}

	/**
	 * onGroupExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onGroupExpression(Object[] nodes)
	{
		Lexeme lparen = (Lexeme) nodes[0];
		JSParseNode expression = (JSParseNode) nodes[1];
		Lexeme rparen = (Lexeme) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.GROUP, lparen);
		result.appendChild(expression);
		result.includeLexemeInRange(rparen);
		
		return result;
	}

	/**
	 * onIdentifier
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onIdentifier(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		return this.createNode(JSParseNodeTypes.IDENTIFIER, keyword);
	}

	/**
	 * onIfElseStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onIfElseStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode condition = (JSParseNode) nodes[2];
		JSParseNode trueCase = (JSParseNode) nodes[4];
		JSParseNode falseCase = (JSParseNode) nodes[6];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.IF, keyword);
		result.appendChild(condition);
		result.appendChild(trueCase);
		result.appendChild(falseCase);
		
		return result;
	}

	/**
	 * onIfStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onIfStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode condition = (JSParseNode) nodes[2];
		JSParseNode trueCase = (JSParseNode) nodes[4];
		JSParseNode falseCase = this.createNode(JSParseNodeTypes.EMPTY, null);
		
		JSParseNode result = this.createNode(JSParseNodeTypes.IF, keyword);
		result.appendChild(condition);
		result.appendChild(trueCase);
		result.appendChild(falseCase);
		
		return result;
	}

	/**
	 * onLabelledStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onLabelledStatement(Object[] nodes)
	{
		Lexeme identifier = (Lexeme) nodes[0];
		JSParseNode label = this.createNode(JSParseNodeTypes.IDENTIFIER, identifier);
		JSParseNode statement = (JSParseNode) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.LABELLED, null);
		result.appendChild(label);
		result.appendChild(statement);
		
		return result;
	}

	/**
	 * onNewExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onNewExpression(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode expression = (JSParseNode) nodes[1];
		JSParseNode arguments = (JSParseNode) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.CONSTRUCT, keyword);
		result.appendChild(expression);
		result.appendChild(arguments);
		
		return result;
	}

	/**
	 * onNewExpressionWithoutArguments
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onNewExpressionWithoutArguments(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode expression = (JSParseNode) nodes[1];
		JSParseNode arguments = this.createNode(JSParseNodeTypes.EMPTY, null);
		
		JSParseNode result = this.createNode(JSParseNodeTypes.CONSTRUCT, keyword);
		result.appendChild(expression);
		result.appendChild(arguments);
		
		return result;
	}

	/**
	 * onNull
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onNull(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		return this.createNode(JSParseNodeTypes.NULL, keyword);
	}

	/**
	 * onNumber
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onNumber(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		return this.createNode(JSParseNodeTypes.NUMBER, keyword);
	}

	/**
	 * onObjectLiteral
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onObjectLiteral(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		ParseFragment properties = (ParseFragment) nodes[1];
		Lexeme rcurly = (Lexeme) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.OBJECT_LITERAL, lcurly);
		result.appendChild(properties);
		result.includeLexemeInRange(rcurly);
		
		return result;
	}

	/**
	 * onParameterList
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onParameterList(Object[] nodes)
	{
		Lexeme lparen = (Lexeme) nodes[0];
		JSParseNode params = (JSParseNode) nodes[1];
		Lexeme rparen = (Lexeme) nodes[2];
		
		params.includeLexemeInRange(lparen);
		params.includeLexemeInRange(rparen);
		
		return params;
	}

	/**
	 * onPostfixExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onPostfixExpression(Object[] nodes)
	{
		JSParseNode expression = (JSParseNode) nodes[0];
		Lexeme operator = (Lexeme) nodes[1];
		
		JSParseNode result;
		
		switch (operator.typeIndex)
		{
			case JSTokenTypes.PLUS_PLUS:
				result = this.createNode(JSParseNodeTypes.POST_INCREMENT, operator);
				break;
			
			case JSTokenTypes.MINUS_MINUS:
				result = this.createNode(JSParseNodeTypes.POST_DECREMENT, operator);
				break;
				
			default:
				throw new IllegalArgumentException(MessageFormat.format(Messages.JSASTHandler_Unknown_operator_0, operator));
		}
		
		result.appendChild(expression);
		
		return result;
	}

	/**
	 * onRegex
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onRegex(Object[] nodes)
	{
		Lexeme regex = (Lexeme) nodes[0];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.REGULAR_EXPRESSION, regex);
		
		return result;
	}

	/**
	 * onReturn
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onReturn(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme semicolon = (Lexeme) nodes[1];
		JSParseNode expression = this.createNode(JSParseNodeTypes.EMPTY, null);
		
		JSParseNode result = this.createNode(JSParseNodeTypes.RETURN, keyword);
		result.appendChild(expression);
		result.includeLexemeInRange(semicolon);
		result.setIncludesSemicolon(true);
		
		return result;
	}

	/**
	 * onReturnValue
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onReturnValue(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode expression = (JSParseNode) nodes[1];
		Lexeme semicolon = (Lexeme) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.RETURN, keyword);
		result.appendChild(expression);
		result.includeLexemeInRange(semicolon);
		result.setIncludesSemicolon(true);
		
		return result;
	}

	/**
	 * onString
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onString(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		return this.createNode(JSParseNodeTypes.STRING, keyword);
	}

	/**
	 * onSwitchStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onSwitchStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode expression = (JSParseNode) nodes[2];
		ParseFragment clauses = (ParseFragment) nodes[4];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.SWITCH, keyword);
		result.appendChild(expression);
		result.appendChild(clauses);
		
		return result;
	}

	/**
	 * onThis
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onThis(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		return this.createNode(JSParseNodeTypes.THIS, keyword);
	}

	/**
	 * onThrowStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onThrowStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode expression = (JSParseNode) nodes[1];
		Lexeme semicolon = (Lexeme) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.THROW, keyword);
		result.appendChild(expression);
		result.includeLexemeInRange(semicolon);
		result.setIncludesSemicolon(true);
		
		return result;
	}

	/**
	 * onTrue
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onTrue(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		return this.createNode(JSParseNodeTypes.TRUE, keyword);
	}

	/**
	 * onTryCatchFinallyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onTryCatchFinallyStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode body = (JSParseNode) nodes[1];
		JSParseNode catchNode = (JSParseNode) nodes[2];
		JSParseNode finallyNode = (JSParseNode) nodes[3];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.TRY, keyword);
		result.appendChild(body);
		result.appendChild(catchNode);
		result.appendChild(finallyNode);
		
		return result;
	}

	/**
	 * onTryCatchStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onTryCatchStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode body = (JSParseNode) nodes[1];
		JSParseNode catchNode = (JSParseNode) nodes[2];
		JSParseNode finallyNode = this.createNode(JSParseNodeTypes.EMPTY, null);
		
		JSParseNode result = this.createNode(JSParseNodeTypes.TRY, keyword);
		result.appendChild(body);
		result.appendChild(catchNode);
		result.appendChild(finallyNode);
		
		return result;
	}

	/**
	 * onTryFinallyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onTryFinallyStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode body = (JSParseNode) nodes[1];
		JSParseNode catchNode = this.createNode(JSParseNodeTypes.EMPTY, null);
		JSParseNode finallyNode = (JSParseNode) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.TRY, keyword);
		result.appendChild(body);
		result.appendChild(catchNode);
		result.appendChild(finallyNode);
		
		return result;
	}

	/**
	 * onUnaryExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onUnaryExpression(Object[] nodes)
	{
		Lexeme operator = (Lexeme) nodes[0];
		JSParseNode expression = (JSParseNode) nodes[1];
		
		JSParseNode result;
		
		switch (operator.typeIndex)
		{
			case JSTokenTypes.DELETE:
				result = this.createNode(JSParseNodeTypes.DELETE, operator);
				break;

			case JSTokenTypes.EXCLAMATION:
				result = this.createNode(JSParseNodeTypes.LOGICAL_NOT, operator);
				break;

			case JSTokenTypes.MINUS:
				result = this.createNode(JSParseNodeTypes.NEGATE, operator);
				break;

			case JSTokenTypes.MINUS_MINUS:
				result = this.createNode(JSParseNodeTypes.PRE_DECREMENT, operator);
				break;

			case JSTokenTypes.PLUS:
				result = this.createNode(JSParseNodeTypes.POSITIVE, operator);
				break;

			case JSTokenTypes.PLUS_PLUS:
				result = this.createNode(JSParseNodeTypes.PRE_INCREMENT, operator);
				break;

			case JSTokenTypes.TILDE:
				result = this.createNode(JSParseNodeTypes.BITWISE_NOT, operator);
				break;

			case JSTokenTypes.TYPEOF:
				result = this.createNode(JSParseNodeTypes.TYPEOF, operator);
				break;

			case JSTokenTypes.VOID:
				result = this.createNode(JSParseNodeTypes.VOID, operator);
				break;
				
			default:
				throw new IllegalArgumentException(MessageFormat.format(Messages.JSASTHandler_Unknown_operator_0, operator));
		}
		
		result.appendChild(expression);
		
		return result;
	}

	/**
	 * onVarDeclaration
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onVarDeclaration(Object[] nodes)
	{
		Lexeme identifier = (Lexeme) nodes[0];
		JSParseNode id = this.createNode(JSParseNodeTypes.IDENTIFIER, identifier);
		JSParseNode assignment = this.createNode(JSParseNodeTypes.EMPTY, null);
		
		JSParseNode result = this.createNode(JSParseNodeTypes.DECLARATION, null);
		result.appendChild(id);
		result.appendChild(assignment);
		
		return result;
	}

	/**
	 * onVarDeclarationAssignment
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onVarDeclarationAssignment(Object[] nodes)
	{
		Lexeme identifier = (Lexeme) nodes[0];
		JSParseNode id = this.createNode(JSParseNodeTypes.IDENTIFIER, identifier);
		JSParseNode assignment = (JSParseNode) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.DECLARATION, null);
		result.appendChild(id);
		result.appendChild(assignment);
		
		return result;
	}

	/**
	 * onVarStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onVarStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		ParseFragment declarations = (ParseFragment) nodes[1];
		Lexeme semicolon = (Lexeme) nodes[2];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.VAR, keyword);
		result.appendChild(declarations);
		result.includeLexemeInRange(semicolon);
		result.setIncludesSemicolon(true);
		
		return result;
	}

	/**
	 * onWhileStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onWhileStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode condition = (JSParseNode) nodes[2];
		JSParseNode body = (JSParseNode) nodes[4];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.WHILE, keyword);
		result.appendChild(condition);
		result.appendChild(body);
		
		return result;
	}

	/**
	 * onWithStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected Object onWithStatement(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		JSParseNode expression = (JSParseNode) nodes[2];
		JSParseNode body = (JSParseNode) nodes[4];
		
		JSParseNode result = this.createNode(JSParseNodeTypes.WITH, keyword);
		result.appendChild(expression);
		result.appendChild(body);
		
		return result;
	} 
}
