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
package com.aptana.ide.editor.js.parsing;

import java.text.ParseException;
import java.util.Arrays;

import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.nodes.JSNaryNode;
import com.aptana.ide.editor.js.parsing.nodes.JSParseNode;
import com.aptana.ide.editor.js.parsing.nodes.JSParseNodeTypes;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeFactory;

/**
 * @author Kevin Lindsey
 */
public class JSParser extends JSParserBase
{
	private String _expressionErrorKey;

	// sets
	private static final int[] additiveExpressionSet = new int[] {
		JSTokenTypes.PLUS,
		JSTokenTypes.MINUS
	};

	private static final int[] assignmentOperatorSet = new int[] {
		JSTokenTypes.AMPERSAND_EQUAL,
		JSTokenTypes.CARET_EQUAL,
		JSTokenTypes.EQUAL,
		JSTokenTypes.FORWARD_SLASH_EQUAL,
		JSTokenTypes.GREATER_GREATER_EQUAL,
		JSTokenTypes.GREATER_GREATER_GREATER_EQUAL,
		JSTokenTypes.LESS_LESS_EQUAL,
		JSTokenTypes.MINUS_EQUAL,
		JSTokenTypes.PERCENT_EQUAL,
		JSTokenTypes.PIPE_EQUAL,
		JSTokenTypes.PLUS_EQUAL,
		JSTokenTypes.STAR_EQUAL
	};

	private static final int[] commentSet = new int[] {
		JSTokenTypes.COMMENT,
		JSTokenTypes.DOCUMENTATION
	};

	private static final int[] caseBlockSet = new int[] {
		JSTokenTypes.RCURLY,
		JSTokenTypes.CASE,
		JSTokenTypes.DEFAULT
	};

	private static final int[] equalityExpressionSet = new int[] {
		JSTokenTypes.EQUAL_EQUAL,
		JSTokenTypes.EXCLAMATION_EQUAL,
		JSTokenTypes.EQUAL_EQUAL_EQUAL,
		JSTokenTypes.EXCLAMATION_EQUAL_EQUAL
	};

	private static final int[] expressionStatementSet = new int[] {
		JSTokenTypes.COMMA,
		JSTokenTypes.FUNCTION
	};

	private static final int[] multiplicativeExpressionSet = new int[] {
		JSTokenTypes.STAR,
		JSTokenTypes.FORWARD_SLASH,
		JSTokenTypes.PERCENT
	};

	private static final int[] postfixExpressionSet = new int[] {
		JSTokenTypes.PLUS_PLUS,
		JSTokenTypes.MINUS_MINUS
	};

	private static final int[] postfixMemberExpressionSet = new int[] {
		JSTokenTypes.DOT,
		JSTokenTypes.LBRACKET,
		JSTokenTypes.LPAREN
	};

	private static final int[] propertyNameSet = new int[] {
		JSTokenTypes.IDENTIFIER,
		JSTokenTypes.NUMBER,
		JSTokenTypes.STRING
	};

	private static final int[] relationalExpressionSet = new int[] {
		JSTokenTypes.LESS,
		JSTokenTypes.GREATER,
		JSTokenTypes.LESS_EQUAL,
		JSTokenTypes.GREATER_EQUAL,
		JSTokenTypes.INSTANCEOF,
		JSTokenTypes.IN
	};

	private static final int[] returnSet = new int[] {
		JSTokenTypes.SEMICOLON,
		JSTokenTypes.RCURLY,
		JSTokenTypes.ERROR
	};

	private static final int[] shiftExpressionSet = new int[] {
		JSTokenTypes.LESS_LESS,
		JSTokenTypes.GREATER_GREATER,
		JSTokenTypes.GREATER_GREATER_GREATER
	};

	private static final int[] stopSet = new int[] {
		JSTokenTypes.SEMICOLON,
		JSTokenTypes.RCURLY
	};

	private static final int[] unaryExpressionSet = new int[] {
		JSTokenTypes.DELETE,
		JSTokenTypes.EXCLAMATION,
		JSTokenTypes.MINUS,
		JSTokenTypes.MINUS_MINUS,
		JSTokenTypes.PLUS,
		JSTokenTypes.PLUS_PLUS,
		JSTokenTypes.TILDE,
		JSTokenTypes.TYPEOF,
		JSTokenTypes.VOID
	};

	private static final int[] unclosedCommentSet = new int[] {
		JSTokenTypes.START_MULTILINE_COMMENT,
		JSTokenTypes.START_DOCUMENTATION
	};

	private static final int[] referenceableSet = new int[] {
		JSParseNodeTypes.GET_ELEMENT,
		JSParseNodeTypes.GET_PROPERTY,
		JSParseNodeTypes.IDENTIFIER
	};

	private Lexeme prevLexeme;

	private static final String DEFAULT_GROUP = "default"; //$NON-NLS-1$
	private static final String REGEX_GROUP = "regex"; //$NON-NLS-1$
	private static final String ADDITION_GROUP = "addition"; //$NON-NLS-1$
//	private static final String ERROR_GROUP = "error"; //$NON-NLS-1$
	
	public static final String DOCUMENTATION_DELIMITER_GROUP = "documentation-delimiter"; //$NON-NLS-1$
	public static final String LINE_DELIMITER_GROUP = "line-delimiter"; //$NON-NLS-1$
	

	/**
	 * static constructor
	 */
	static
	{
		// make sure all of our sets are sorted so that inSet will work properly
		// (that method uses a binary search to test existence of members in the
		// set)
		Arrays.sort(additiveExpressionSet);
		Arrays.sort(assignmentOperatorSet);
		Arrays.sort(caseBlockSet);
		Arrays.sort(commentSet);
		Arrays.sort(equalityExpressionSet);
		Arrays.sort(expressionStatementSet);
		Arrays.sort(multiplicativeExpressionSet);
		Arrays.sort(postfixExpressionSet);
		Arrays.sort(postfixMemberExpressionSet);
		Arrays.sort(propertyNameSet);
		Arrays.sort(relationalExpressionSet);
		Arrays.sort(returnSet);
		Arrays.sort(shiftExpressionSet);
		Arrays.sort(stopSet);
		Arrays.sort(unaryExpressionSet);
		Arrays.sort(unclosedCommentSet);
		Arrays.sort(referenceableSet);
	}

	/**
	 * Create a new instance of JSParser
	 * 
	 * @throws ParserInitializationException
	 */
	public JSParser() throws ParserInitializationException
	{
		this(JSMimeType.MimeType);
	}

	/**
	 * Create a new instance of JSParser
	 * 
	 * @param mimeType
	 * @throws ParserInitializationException
	 */
	public JSParser(String mimeType) throws ParserInitializationException
	{
		super(mimeType);
	}

	/**
	 * createNode
	 * 
	 * @param type
	 * @param startingLexeme
	 * @return JSParseNode
	 */
	private JSParseNode createNode(int type, Lexeme startingLexeme)
	{
		IParseNodeFactory factory = this.getParseNodeFactory();
		JSParseNode result = null;

		if (factory != null)
		{
			result = (JSParseNode) factory.createParseNode(type, startingLexeme);
		}
		else
		{
			// we need to return something to prevent NPE's
			result = new JSParseNode(type, startingLexeme);
		}

		return result;
	}

	/**
	 * Handle cases where semicolons are optional
	 * 
	 * @param node
	 *            The command node that is expecting either an explicit or implicit trailing semicolon
	 * @throws LexerException
	 * @throws ParseException
	 */
	private void handleOptionalSemicolon(JSParseNode node) throws LexerException, ParseException
	{
		// NOTE: Cached command nodes will include the trailing semicolon, so we need to check the node's last lexeme to
		// see if we've already handled a pre-existing semicolon from a previous parse

		// if (node.getEndingLexeme().typeIndex != JSTokenTypes.SEMICOLON)
		// {
		switch (this.currentLexeme.typeIndex)
		{
			case JSTokenTypes.SEMICOLON:
				// associate this lexeme with the specified command node
				// this._currentLexeme.setCommandNode(node);

				// tag node
				node.setIncludesSemicolon(true);

				// advance over ';'
				this.advance();
				break;

			case JSTokenTypes.ERROR:
			case JSTokenTypes.RCURLY:
				// implied ';'
				break;

			default:
				// handle auto semicolon insertion here
				if (this.currentLexeme != EOS && this.currentLexeme.isAfterEOL() == false)
				{
					this.throwParseError(JSMessages.getString("error.semicolon")); //$NON-NLS-1$
				}
				break;
		}
		// }
	}

	/**
	 * @see com.aptana.ide.parsing.AbstractParser#parseAll(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public synchronized void parseAll(IParseNode parentNode) throws LexerException, ParseException
	{
		// set node to be used as the root node for the results of this parse
		IParseNode rootNode = this.getParseRootNode(parentNode, JSNaryNode.class);

		// make sure our lexer is using our lexeme cache and switch over to our language and default group
		ILexer lexer = this.getLexer();
		lexer.setLanguageAndGroup(this.getLanguage(), DEFAULT_GROUP);

		// prime the lexeme pump
		this.advance();

		while (this.isEOS() == false)
		{
			IParseNode result = this.parseSourceElement();

			if (rootNode != null && result != null)
			{
				rootNode.appendChild(result);
			}
		}
	}

	/**
	 * Parse AdditiveExpression
	 * <p>
	 * 
	 * <pre>
	 *      AdditiveExpression
	 *      	:    MultiplicativeExpression
	 *      	|    AdditiveExpression PLUS MultiplicativeExpression
	 *      	|    AdditiveExpression MINUS MultiplicativeExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseAdditiveExpression() throws ParseException, LexerException
	{
		// get lhs
		JSParseNode result = this.parseMultiplicativeExpression();
		ILexer lexer = this.getLexer();

		if (this.isType(JSTokenTypes.NUMBER))
		{
			// a '+' or '-' may have been consumed as part of the number
			// We want to rescan as PLUS and MINUS followed by a NUMBER
			// here.
			char startChar = lexer.getCharacterAt(this.currentLexeme.offset);

			if (startChar == '-' || startChar == '+')
			{
				this.rescan(ADDITION_GROUP);
			}
		}

		while (this.inSet(additiveExpressionSet))
		{
			JSParseNode lhs = result;
			JSParseNode rhs;

			// save operator type
			int operatorType = this.currentLexeme.typeIndex;

			Lexeme operator = this.currentLexeme;

			// advance over '-' or '+'
			this.advance();

			// get right-hand side
			rhs = this.parseMultiplicativeExpression();

			if (this.isType(JSTokenTypes.NUMBER))
			{
				// a '+' or '-' may have been consumed as part of the number
				// We want to rescan as PLUS and MINUS followed by a NUMBER
				// here.
				char startChar = lexer.getCharacterAt(this.currentLexeme.offset);

				if (startChar == '-' || startChar == '+')
				{
					this.rescan(ADDITION_GROUP);
				}
			}

			switch (operatorType)
			{
				case JSTokenTypes.MINUS:
					result = this.createNode(JSParseNodeTypes.SUBTRACT, operator);
					result.appendChild(lhs);
					result.appendChild(rhs);
					break;

				case JSTokenTypes.PLUS:
					result = this.createNode(JSParseNodeTypes.ADD, operator);
					result.appendChild(lhs);
					result.appendChild(rhs);
					break;

				default:
					String typeName = JSTokenTypes.getName(operatorType);
					this.throwParseError(JSMessages.getString("error.internal.additive.expression") + typeName); //$NON-NLS-1$
			}
		}

		return result;
	}

	/**
	 * Parse ArgumentList
	 * <p>
	 * 
	 * <pre>
	 *      ArgumentList
	 *      	:    AssignmentExpression
	 *      	|    ArgumentList COMMA AssignmentExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseArgumentList() throws ParseException, LexerException
	{
		JSParseNode arguments = this.createNode(JSParseNodeTypes.ARGUMENTS, this.currentLexeme);

		// add first argument to our list
		arguments.appendChild(this.parseAssignmentExpression(false));

		while (this.isType(JSTokenTypes.COMMA))
		{
			// advance over ','
			this.advance();

			// add next argument
			arguments.appendChild(this.parseAssignmentExpression(false));
		}

		return arguments;
	}

	/**
	 * <pre>
	 *      Arguments
	 *      	:    LPAREN RPAREN
	 *      	|    LPAREN ArgumentList RPAREN
	 *      	;
	 * </pre>
	 */

	/**
	 * Parse ArrayLiteral
	 * <p>
	 * 
	 * <pre>
	 *      ArrayLiteral
	 *      	:    LBRACKET RBRACE
	 *      	|    LBRACKET Elision RBRACE
	 *      	|    LBRACKET ElementList RBRACE
	 *      	|    LBRACKET ElementList COMMA RBRACE
	 *      	|    LBRACKET ElementList COMMA Elision RBRACE
	 *      	;
	 *      Elision
	 *      	:    COMMA
	 *      	|    Elision COMMA
	 *      	;
	 *      ElementList
	 *      	:    AssignmentExpression
	 *      	|    Elision AssignmentExpression
	 *      	|    ElementList COMMA AssignmentExpression
	 *      	|    ElementList COMMA Elision AssignmentExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseArrayLiteral() throws ParseException, LexerException
	{
		// create new command node
		JSParseNode result = this.createNode(JSParseNodeTypes.ARRAY_LITERAL, this.currentLexeme);

		// make sure we have '['
		this.assertType(JSTokenTypes.LBRACKET, "error.internal.punctuator"); //$NON-NLS-1$

		// advance over '['
		this.advance();

		// pretend like we've just seen a comma
		boolean comma = true;

		while (this.isType(JSTokenTypes.RBRACKET) == false && this.isEOS() == false)
		{
			if (this.isType(JSTokenTypes.COMMA))
			{
				// advance over ','
				this.advance();

				// tag that we've seen a comma
				if (comma == false)
				{
					comma = true;
				}
				else
				{
					// add null element
					result.appendChild(this.createNode(JSParseNodeTypes.NULL, null));
				}
			}
			else
			{
				// can't have two assignment expressions in a row
				if (comma == false)
				{
					throwParseError(JSMessages.getString("error.array.missing.comma")); //$NON-NLS-1$
				}

				// add assignment expression
				result.appendChild(this.parseAssignmentExpression(false));

				// reset comma flag
				comma = false;
			}
		}

		// add null element after last comma
		if (comma && result.getChildCount() > 0)
		{
			result.appendChild(this.createNode(JSParseNodeTypes.NULL, null));
		}

		// make sure we have ']'
		this.assertType(JSTokenTypes.RBRACKET, "error.array.literal.rbracket"); //$NON-NLS-1$

		// advance over ']'
		this.advance();
		
		result.includeLexemeInRange(currentLexeme);
		
		return result;
	}

	/**
	 * Parse AssignmentExpression
	 * <p>
	 * 
	 * <pre>
	 *      AssignmentExpression
	 *      	:    ConditionalExpression
	 *     	|    LeftHandSideExpression AssignmentOperator AssignmentExpression
	 *     	;
	 *      AssignmentOperator
	 *      	:    EQUAL
	 *      	|    STAR_EQUAL
	 *      	|    FORWARD_SLASH_EQUAL
	 *      	|    PERCENT_EQUAL
	 *      	|    PLUS_EQUAL
	 *      	|    MINUS_EQUAL
	 *      	|    LESS_LESS_EQUAL
	 *      	|    GREATER_GREATER_EQUAL
	 *      	|    GREATER_GREATER_GREATER_EQUAL
	 *      	|    AMPERSAND_EQUAL
	 *      	|    CARET_EQUAL
	 *      	|    PIPE_EQUAL
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseAssignmentExpression(boolean noIn) throws ParseException, LexerException
	{
		JSParseNode result = this.parseConditionalExpression(noIn);

		if (this.inSet(assignmentOperatorSet))
		{
			boolean referenceable = true;

			if (result.getTypeIndex() == JSParseNodeTypes.GROUP)
			{
				JSParseNode child = (JSParseNode) result.getChild(0);

				while (child != null && child.getTypeIndex() == JSParseNodeTypes.GROUP)
				{
					child = (JSParseNode) child.getChild(0);
				}

				if (child == null || Arrays.binarySearch(referenceableSet, child.getTypeIndex()) < 0)
				{
					referenceable = false;
				}
			}

			if (referenceable == false || Arrays.binarySearch(referenceableSet, result.getTypeIndex()) < 0)
			{
				this.throwParseError(JSMessages.getString("error.assignment.invalid.lhs")); //$NON-NLS-1$
			}

			// each side of the operator
			JSParseNode lhs = result;
			JSParseNode rhs;

			// save operator type
			int operatorType = this.currentLexeme.typeIndex;

			Lexeme operator = this.currentLexeme;

			// advance over operator
			this.advance();

			// get right-hand side
			rhs = this.parseAssignmentExpression(noIn);

			// create command node
			switch (operatorType)
			{
				case JSTokenTypes.AMPERSAND_EQUAL:
					result = this.createNode(JSParseNodeTypes.BITWISE_AND_AND_ASSIGN, operator);
					break;

				case JSTokenTypes.CARET_EQUAL:
					result = this.createNode(JSParseNodeTypes.BITWISE_XOR_AND_ASSIGN, operator);
					break;

				case JSTokenTypes.EQUAL:
					result = this.createNode(JSParseNodeTypes.ASSIGN, operator);
					break;

				case JSTokenTypes.FORWARD_SLASH_EQUAL:
					result = this.createNode(JSParseNodeTypes.DIVIDE_AND_ASSIGN, operator);
					break;

				case JSTokenTypes.GREATER_GREATER_EQUAL:
					result = this.createNode(JSParseNodeTypes.SHIFT_RIGHT_AND_ASSIGN, operator);
					break;

				case JSTokenTypes.GREATER_GREATER_GREATER_EQUAL:
					result = this.createNode(JSParseNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN, operator);
					break;

				case JSTokenTypes.LESS_LESS_EQUAL:
					result = this.createNode(JSParseNodeTypes.SHIFT_LEFT_AND_ASSIGN, operator);
					break;

				case JSTokenTypes.MINUS_EQUAL:
					result = this.createNode(JSParseNodeTypes.SUBTRACT_AND_ASSIGN, operator);
					break;

				case JSTokenTypes.PERCENT_EQUAL:
					result = this.createNode(JSParseNodeTypes.MOD_AND_ASSIGN, operator);
					break;

				case JSTokenTypes.PIPE_EQUAL:
					result = this.createNode(JSParseNodeTypes.BITWISE_OR_AND_ASSIGN, operator);
					break;

				case JSTokenTypes.PLUS_EQUAL:
					result = this.createNode(JSParseNodeTypes.ADD_AND_ASSIGN, operator);
					break;

				case JSTokenTypes.STAR_EQUAL:
					result = this.createNode(JSParseNodeTypes.MULTIPLY_AND_ASSIGN, operator);
					break;

				default:
					String typeName = JSTokenTypes.getName(operatorType);
					this.throwParseError(JSMessages.getString("error.internal.assignment.expression") + typeName); //$NON-NLS-1$
			}

			result.appendChild(lhs);
			result.appendChild(rhs);
		}

		return result;
	}

	/**
	 * Parse BitwiseANDExpression
	 * <p>
	 * 
	 * <pre>
	 *      BitwiseANDExpression
	 *      	:    EqualityExpression
	 *      	|    BitwiseANDExpression AMPERSAND EqualityExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseBitwiseANDExpression(boolean noIn) throws ParseException, LexerException
	{
		JSParseNode result = this.parseEqualityExpression(noIn);

		while (this.isType(JSTokenTypes.AMPERSAND))
		{
			JSParseNode lhs = result;
			JSParseNode rhs;

			// get lexeme index
			Lexeme operator = this.currentLexeme;

			// advance over '&'
			this.advance();

			// get right-hand side
			rhs = this.parseEqualityExpression(noIn);

			// create resulting command node
			result = this.createNode(JSParseNodeTypes.BITWISE_AND, operator);
			result.appendChild(lhs);
			result.appendChild(rhs);
		}

		return result;
	}

	/**
	 * Parse BitwiseORExpression
	 * <p>
	 * 
	 * <pre>
	 *      BitwiseORExpression
	 *      	:    BitwiseXORExpression
	 *      	|    BitwiseORExpression PIPE BitwiseXORExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseBitwiseORExpression(boolean noIn) throws ParseException, LexerException
	{
		JSParseNode result = this.parseBitwiseXORExpression(noIn);

		while (this.isType(JSTokenTypes.PIPE))
		{
			JSParseNode lhs = result;
			JSParseNode rhs;

			// get lexeme index
			Lexeme operator = this.currentLexeme;

			// advance over '|'
			this.advance();

			// get right-hand side
			rhs = this.parseBitwiseXORExpression(noIn);

			result = this.createNode(JSParseNodeTypes.BITWISE_OR, operator);
			result.appendChild(lhs);
			result.appendChild(rhs);
		}

		return result;
	}

	/**
	 * Parse BitwiseXORExpression
	 * <p>
	 * 
	 * <pre>
	 *      BitwiseXORExpression
	 *      	:    BitwiseANDExpression
	 *      	|    BitwiseXORExpression CARET BitwiseANDExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseBitwiseXORExpression(boolean noIn) throws ParseException, LexerException
	{
		JSParseNode result = this.parseBitwiseANDExpression(noIn);

		while (this.isType(JSTokenTypes.CARET))
		{
			JSParseNode lhs = result;
			JSParseNode rhs;

			// get lexeme index
			Lexeme operator = this.currentLexeme;

			// advance '^'
			this.advance();

			// get right-hand side
			rhs = this.parseBitwiseANDExpression(noIn);

			// create command node
			result = this.createNode(JSParseNodeTypes.BITWISE_XOR, operator);
			result.appendChild(lhs);
			result.appendChild(rhs);
		}

		return result;
	}

	/**
	 * Parse Block
	 * <p>
	 * 
	 * <pre>
	 *      Block
	 *      	:    LCURLY StatementList? RCURLY
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseBlock() throws ParseException, LexerException
	{
		// turn on inBlock flag to indicate to recover not to consume closing braces
		// this._inBlock = true;

		JSParseNode result = this.createNode(JSParseNodeTypes.STATEMENTS, this.currentLexeme);

		// make sure we have '{'
		this.assertType(JSTokenTypes.LCURLY, "error.block.lcurly"); //$NON-NLS-1$

		// advance over '{'
		this.advance();

		while (this.isType(JSTokenTypes.RCURLY) == false && this.isEOS() == false)
		{
			// parse statement
			JSParseNode lastStatement = this.parseStatement();

			// add statement
			result.appendChild(lastStatement);
		}

		result.includeLexemeInRange(this.currentLexeme);
		// check for '}'
		this.assertType(JSTokenTypes.RCURLY, "error.block.rcurly"); //$NON-NLS-1$

		// advance over '}'
		this.advance();

		// clear block flag
		// this._inBlock = false;

		return result;
	}

	/**
	 * Parse BreakStatement
	 * <p>
	 * 
	 * <pre>
	 *      BreakStatement
	 *      	:   BREAK [no LINE_TERMINATOR here] Identifier? SEMICOLON
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseBreakStatement() throws ParseException, LexerException
	{
		JSParseNode result;
		Lexeme keyword = this.currentLexeme;
		JSParseNode label = this.createNode(JSParseNodeTypes.EMPTY, null); // JSParseNode.Empty;

		// advance over 'break'
		this.assertAndAdvance(JSTokenTypes.BREAK, "error.internal.keyword"); //$NON-NLS-1$

		// handle possible identifier
		if (this.isType(JSTokenTypes.IDENTIFIER))
		{
			// get label index
			label = this.createNode(JSParseNodeTypes.IDENTIFIER, this.currentLexeme);

			// advance over identifier
			this.advance();
		}

		// create command node
		result = this.createNode(JSParseNodeTypes.BREAK, keyword);
		result.appendChild(label);

		// handle ';'
		this.handleOptionalSemicolon(result);

		// return result
		return result;
	}

	/**
	 * <pre>
	 *      CallExpression
	 *      	:    MemberExpression Arguments
	 *      	|    CallExpression Arguments
	 *      	|    CallExpression LBRACKET Expression RBRACE
	 *      	|    CallExpression DOT IDENTIFIER
	 *      	;
	 * </pre>
	 */

	/**
	 * Parse CaseBlock
	 * <p>
	 * 
	 * <pre>
	 *      CaseClause
	 *      	:    CASE Expression COLON StatementList?
	 *      	;
	 *      DefaultClause
	 *      	:    DEFAULT COLON
	 *      	|    DEFAULT COLON StatementList
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseCaseOrDefaultBlock() throws ParseException, LexerException
	{
		JSParseNode clause = null;
		Lexeme keyword = this.currentLexeme;

		switch (this.currentLexeme.typeIndex)
		{
			case JSTokenTypes.CASE:
				// advance over 'case'
				this.assertAndAdvance(JSTokenTypes.CASE, "error.internal.keyword"); //$NON-NLS-1$

				// parse expression
				JSParseNode expression = this.parseExpression(false, "error.switch.case.expression"); //$NON-NLS-1$

				// grab expression
				clause = this.createNode(JSParseNodeTypes.CASE, keyword);
				clause.appendChild(expression);

				// make sure we have ':'
				this.assertType(JSTokenTypes.COLON, "error.switch.case.colon"); //$NON-NLS-1$

				// advance over ':'
				this.advance();
				break;

			case JSTokenTypes.DEFAULT:
				// advance over 'default'
				this.assertAndAdvance(JSTokenTypes.DEFAULT, "error.internal.keyword"); //$NON-NLS-1$

				// make sure we have ':'
				this.assertType(JSTokenTypes.COLON, "error.switch.default.colon"); //$NON-NLS-1$

				// create default clause
				clause = this.createNode(JSParseNodeTypes.DEFAULT, keyword);

				clause.includeLexemeInRange(this.currentLexeme);

				// advance over ':'
				this.advance();
				break;

			default:
				this.throwParseError(JSMessages.getString("error.switch.body")); //$NON-NLS-1$
		}

		// parse statement list
		while (this.inSet(caseBlockSet) == false && this.isEOS() == false)
		{
			// add statement
			clause.appendChild(this.parseStatement());
		}

		return clause;
	}

	/**
	 * Parse Catch
	 * <p>
	 * 
	 * <pre>
	 *      Catch
	 *      	:    CATCH LPAREN IDENTIFIER RPAREN Block
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseCatch() throws ParseException, LexerException
	{
		JSParseNode name;
		Lexeme keyword = this.currentLexeme;

		// advance over 'catch'
		this.assertAndAdvance(JSTokenTypes.CATCH, "error.internal.keyword"); //$NON-NLS-1$

		// advance over '('
		this.assertAndAdvance(JSTokenTypes.LPAREN, "error.try.catch.lparen"); //$NON-NLS-1$

		// make sure we have an identifier
		this.assertType(JSTokenTypes.IDENTIFIER, "error.try.catch.identifier"); //$NON-NLS-1$

		// grab identifier name
		name = this.createNode(JSParseNodeTypes.IDENTIFIER, this.currentLexeme);

		// advance over identifier
		this.advance();

		// advance over ')'
		this.assertAndAdvance(JSTokenTypes.RPAREN, "error.try.catch.rparen"); //$NON-NLS-1$

		// parse body
		JSParseNode body = this.parseBlock();

		// create result
		JSParseNode result = this.createNode(JSParseNodeTypes.CATCH, keyword);
		result.appendChild(name);
		result.appendChild(body);

		return result;
	}

	/**
	 * Parse ConditionalExpression
	 * <p>
	 * 
	 * <pre>
	 *      ConditionalExpression
	 *      	:    LogicalORExpression
	 *      	|    LogicalORExpression QUESTION AssignmentExpression COLON AssignmentExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseConditionalExpression(boolean noIn) throws ParseException, LexerException
	{
		JSParseNode result = this.parseLogicalORExpression(noIn);

		if (this.isType(JSTokenTypes.QUESTION))
		{
			JSParseNode condition = result;
			JSParseNode trueCase = this.createNode(JSParseNodeTypes.EMPTY, null); // JSParseNode.Empty;
			JSParseNode falseCase = this.createNode(JSParseNodeTypes.EMPTY, null); // JSParseNode.Empty;

			// get '?' position
			Lexeme question = this.currentLexeme;

			// advance over '?'
			this.advance();

			// parse trueCase
			trueCase = this.parseAssignmentExpression(noIn);

			// check for ':'
			this.assertType(JSTokenTypes.COLON, "error.conditional.colon"); //$NON-NLS-1$

			// advance over ':'
			this.advance();

			// parse falseCase
			falseCase = this.parseAssignmentExpression(noIn);

			// create command node
			result = this.createNode(JSParseNodeTypes.CONDITIONAL, question);
			result.appendChild(condition);
			result.appendChild(trueCase);
			result.appendChild(falseCase);
		}

		return result;
	}

	/**
	 * Parse ContinueStatement
	 * <p>
	 * 
	 * <pre>
	 *      ContinueStatement
	 *      	:   CONTINUE [no LINE_TERMINATOR here] IDENTIFIER? SEMICOLON
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseContinueStatement() throws ParseException, LexerException
	{
		JSParseNode label = this.createNode(JSParseNodeTypes.EMPTY, null); // JSParseNode.Empty;
		Lexeme keyword = this.currentLexeme;
		JSParseNode result;

		// advance over 'continue'
		this.assertAndAdvance(JSTokenTypes.CONTINUE, "error.internal.keyword"); //$NON-NLS-1$

		if (this.isType(JSTokenTypes.IDENTIFIER))
		{
			// get label
			label = this.createNode(JSParseNodeTypes.IDENTIFIER, this.currentLexeme);

			// advance over identifier
			this.advance();
		}

		// create command node
		result = this.createNode(JSParseNodeTypes.CONTINUE, keyword);
		result.appendChild(label);

		// handle ';'
		this.handleOptionalSemicolon(result);

		// return result
		return result;
	}

	/**
	 * Parse DoStatement
	 * <p>
	 * 
	 * <pre>
	 *      DoStatement
	 *      	:    DO Statement WHILE LPAREN Expression RPAREN
	 *      	;
	 * </pre>
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private JSParseNode parseDoStatement() throws ParseException, LexerException
	{
		JSParseNode body;
		JSParseNode condition;
		Lexeme keyword = this.currentLexeme;
		JSParseNode result;

		// advance over 'do'
		this.assertAndAdvance(JSTokenTypes.DO, "error.internal.keyword"); //$NON-NLS-1$

		// get body
		body = this.parseStatement();

		// advance over 'while'
		this.assertAndAdvance(JSTokenTypes.WHILE, "error.do.while"); //$NON-NLS-1$

		// advance over '('
		this.assertAndAdvance(JSTokenTypes.LPAREN, "error.do.while.lparen"); //$NON-NLS-1$

		// get condition
		condition = this.parseExpression(false, "error.do.while.expression"); //$NON-NLS-1$

		// advance over ')'
		result = this.createNode(JSParseNodeTypes.DO, keyword);
		result.includeLexemeInRange(this.currentLexeme);
		this.assertAndAdvance(JSTokenTypes.RPAREN, "error.do.while.rparen"); //$NON-NLS-1$
		
		if (this.isType(JSTokenTypes.SEMICOLON))
		{
			result.includeLexemeInRange(this.currentLexeme);
			result.setIncludesSemicolon(true);
			this.advance();
		}

		result.appendChild(body);
		result.appendChild(condition);

		return result;
	}

	/**
	 * Parse EqualityExpression
	 * <p>
	 * 
	 * <pre>
	 *      EqualityExpression
	 *      	:    RelationalExpression
	 *      	|    EqualityExpression EQUAL_EQUAL RelationalExpression
	 *      	|    EqualityExpression EXCLAMATION_EQUAL RelationalExpression
	 *      	|    EqualityExpression EQUAL_EQUAL_EQUAL RelationalExpression
	 *      	|    EqualityExpression EXCLAMATION_EQUAL_EQUAL RelationalExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseEqualityExpression(boolean noIn) throws ParseException, LexerException
	{
		JSParseNode result = this.parseRelationalExpression(noIn);

		while (this.inSet(equalityExpressionSet))
		{
			JSParseNode lhs = result;
			JSParseNode rhs;

			// save operator type
			int operatorType = this.currentLexeme.typeIndex;

			Lexeme operator = this.currentLexeme;

			// advance over '==', '!=', '===', or '!=='
			this.advance();

			// get right-hand side
			rhs = this.parseRelationalExpression(noIn);

			switch (operatorType)
			{
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

				default:
					String typeName = JSTokenTypes.getName(operatorType);
					this.throwParseError(JSMessages.getString("error.internal.equality.expression") + typeName); //$NON-NLS-1$
			}

			result.appendChild(lhs);
			result.appendChild(rhs);
		}

		return result;
	}

	/**
	 * Parse Expression
	 * <p>
	 * 
	 * <pre>
	 *      Expression
	 *      	:    AssignmentExpression
	 *      	|    Expression COMMA AssignmentExpression
	 *      	;
	 * </pre>
	 * 
	 * @param errorKey
	 *            A key used to look up an error message from our error property list
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseExpression(boolean noIn, String errorKey) throws ParseException, LexerException
	{
		// store error key in case we encounter an error while processing this expression
		this._expressionErrorKey = errorKey;

		// parse assignment
		JSParseNode result = this.parseAssignmentExpression(noIn);

		if (this.isType(JSTokenTypes.COMMA))
		{
			JSParseNode commas = this.createNode(JSParseNodeTypes.COMMA, this.currentLexeme);

			// add first expression
			commas.appendChild(result);

			while (this.isType(JSTokenTypes.COMMA))
			{
				// advance over ','
				this.advance();

				// parse expression
				JSParseNode node = this.parseAssignmentExpression(noIn);

				// add expression to list
				commas.appendChild(node);
			}

			result = commas;
		}

		// clear error key so other non-expression parses won't pick up our cached error key
		this._expressionErrorKey = null;

		// return resulting expression
		return result;
	}

	/**
	 * <pre>
	 *      ExpressionStatement
	 *      	:    Expression SEMICOLON
	 *      	;
	 * </pre>
	 */

	/**
	 * Parse Finally
	 * <p>
	 * 
	 * <pre>
	 *      Finally
	 *      	:    FINALLY Block
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseFinally() throws ParseException, LexerException
	{
		// test for 'finally'
		this.assertType(JSTokenTypes.FINALLY, "error.internal.keyword"); //$NON-NLS-1$

		// get 'finally' lexeme
		Lexeme finallyLexeme = this.currentLexeme;

		// advance over 'finally'
		this.advance();

		// parse block
		JSParseNode block = this.parseBlock();

		// create command node
		JSParseNode result = this.createNode(JSParseNodeTypes.FINALLY, finallyLexeme);
		result.appendChild(block);

		// return result
		return result;
	}

	/**
	 * Parse ForStatement
	 * <p>
	 * 
	 * <pre>
	 *      ForStatement
	 *      	:    FOR LPAREN ExpressionNoIn* SEMICOLON Expression* SEMICOLON Expression* RPAREN Statement
	 *      	|    FOR LPAREN VAR VariableDeclarationListNoIn SEMICOLON Expression* SEMICOLON Expression* RPAREN Statement
	 *      	|    FOR LPAREN LeftHandSideExpression IN Expression RPAREN Statement
	 *      	|    FOR LPAREN VAR VariableDeclarationNoIn IN Expression RPAREN Statement
	 *      	;
	 * </pre>
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private JSParseNode parseForStatement() throws ParseException, LexerException
	{
		JSParseNode result;

		JSParseNode condition = null; // JSParseNode.Empty;
		JSParseNode object = this.createNode(JSParseNodeTypes.EMPTY, null); // JSParseNode.Empty;
		JSParseNode advance = null; // JSParseNode.Empty;
		JSParseNode body = this.createNode(JSParseNodeTypes.EMPTY, null); // JSParseNode.Empty;
		boolean isForIn = false;
		Lexeme keyword = this.currentLexeme;

		// advance over 'for'
		this.assertAndAdvance(JSTokenTypes.FOR, "error.internal.keyword"); //$NON-NLS-1$

		JSParseNode initialize = this.createNode(JSParseNodeTypes.EMPTY, currentLexeme); // JSParseNode.Empty;

		// advance over '('
		this.assertAndAdvance(JSTokenTypes.LPAREN, "error.for.lparen"); //$NON-NLS-1$

		// initial assignment
		if (this.isType(JSTokenTypes.SEMICOLON) == false)
		{
			if (this.isType(JSTokenTypes.VAR))
			{
				initialize = this.parseVarExpression(true);
			}
			else
			{
				initialize = this.parseExpression(true, "error.for.initialization.expression"); //$NON-NLS-1$
			}
		}
		else
		{
			initialize.includeLexemeInRange(currentLexeme);
		}

		// determine if this is a for-loop or for-in-loop
		if (this.isType(JSTokenTypes.IN))
		{
			// advance over 'in'
			this.advance();

			// set flag telling us which for-node to create
			isForIn = true;

			// parse expression that returns an object
			object = this.parseExpression(false, "error.for.in.expression"); //$NON-NLS-1$
		}
		else
		{
			condition = this.createNode(JSParseNodeTypes.EMPTY, currentLexeme); // JSParseNode.Empty;

			// advance over ';'
			this.assertAndAdvance(JSTokenTypes.SEMICOLON, "error.for.initialization.semicolon"); //$NON-NLS-1$

			if (this.isType(JSTokenTypes.SEMICOLON) == false)
			{
				// parse condition
				condition = this.parseExpression(false, "error.for.condition.expression"); //$NON-NLS-1$
			}
			else
			{
				condition.includeLexemeInRange(currentLexeme);
			}

			// advance over ';'
			advance = this.createNode(JSParseNodeTypes.EMPTY, currentLexeme); // JSParseNode.Empty;
			this.assertAndAdvance(JSTokenTypes.SEMICOLON, "error.for.condition.semicolon"); //$NON-NLS-1$

			// iterator
			if (this.isType(JSTokenTypes.RPAREN) == false)
			{
				// parse iterator
				advance = this.parseExpression(false, "error.for.iteration.expression"); //$NON-NLS-1$
			}
			else
			{
				advance.includeLexemeInRange(currentLexeme);
			}
		}

		// advance over ')'
		this.assertAndAdvance(JSTokenTypes.RPAREN, "error.for.rparen"); //$NON-NLS-1$

		// parse body
		body = this.parseStatement();

		if (isForIn)
		{
			// create command node
			result = this.createNode(JSParseNodeTypes.FOR_IN, keyword);
			result.appendChild(initialize);
			result.appendChild(object);
			result.appendChild(body);
		}
		else
		{
			// create command node
			result = this.createNode(JSParseNodeTypes.FOR, keyword);
			result.appendChild(initialize);
			result.appendChild(condition);
			result.appendChild(advance);
			result.appendChild(body);
		}

		return result;
	}

	/**
	 * Parse FormalParameterList
	 * <p>
	 * 
	 * <pre>
	 *      FormalParameterList
	 *      	:    IDENTIFIER
	 *      	|    FormalParameterList COMMA IDENTIFIER
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseFormalParameterList() throws ParseException, LexerException
	{
		JSParseNode result = this.createNode(JSParseNodeTypes.PARAMETERS, this.prevLexeme);

		// make sure we have at least one identifier
		this.assertType(JSTokenTypes.IDENTIFIER, "error.parameters.name"); //$NON-NLS-1$

		// create identifier
		JSParseNode name = this.createNode(JSParseNodeTypes.IDENTIFIER, this.currentLexeme);

		// advance over identifier
		this.advance();

		// save parameter
		result.appendChild(name);

		while (this.isType(JSTokenTypes.COMMA))
		{
			// advance over ','
			this.advance();

			// make sure we have an identifier
			this.assertType(JSTokenTypes.IDENTIFIER, "error.parameters.name"); //$NON-NLS-1$

			// save, associate, advance, and add
			name = this.createNode(JSParseNodeTypes.IDENTIFIER, this.currentLexeme);

			// this._currentLexeme.setCommandNode(name);
			this.advance();
			result.appendChild(name);
		}

		return result;
	}

	/**
	 * Parse FunctionBody
	 * <p>
	 * 
	 * <pre>
	 *      FunctionBody
	 *      	:    SourceElements
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseFunctionBody() throws ParseException, LexerException
	{
		JSParseNode statements = this.createNode(JSParseNodeTypes.STATEMENTS, prevLexeme);

		// turn on inFunction flag to indicate it's OK to encounter 'return' now
		// this._inFunction = true;

		while (this.isType(JSTokenTypes.RCURLY) == false && this.isEOS() == false)
		{
			JSParseNode statement;

			if (this.isType(JSTokenTypes.FUNCTION))
			{
				// add function declaration to body collection
				statement = this.parseFunctionDeclaration(false);
			}
			else
			{
				// add statement to body collection
				statement = this.parseStatement();
			}

			statements.appendChild(statement);
		}
		statements.includeLexemeInRange(currentLexeme);

		// turn off inFunction flag
		// this._inFunction = false;

		return statements;
	}

	/**
	 * Main entry point for parsing function definitions
	 * 
	 * @param identifierRequired
	 * @return
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseFunctionDeclaration(boolean identifierRequired) throws ParseException, LexerException
	{
		Lexeme keyword = this.currentLexeme;

		// advance over 'function'
		this.assertAndAdvance(JSTokenTypes.FUNCTION, "error.internal.keyword"); //$NON-NLS-1$

		return parseFunctionDeclaration2(identifierRequired, keyword);
	}

	/**
	 * Parse a function declaration. This is a shared method used both by actual function declarations and getter/setter
	 * declarations in object literals
	 * <p>
	 * 
	 * <pre>
	 *      FunctionDeclaration
	 *      	:   FUNCTION IDENTIFIER LPAREN FormalParameterList? RPAREN LCURLY FunctionBody RCURLY
	 *      	;
	 *      FunctionExpression
	 *      	:    FUNCTION IDENTIFIER? LPAREN FormalParameterList? RPAREN LCURLY FunctionBody RCURLY
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseFunctionDeclaration2(boolean identifierRequired, Lexeme keyword) throws LexerException,
			ParseException
	{
		String name = null;
		JSParseNode result;
		JSParseNode parameters;
		JSParseNode body;
		Lexeme rcurly;

		// NOTE: function declaration requires an identifier, but a function
		// expression does not
		if (this.isType(JSTokenTypes.IDENTIFIER))
		{
			// get function name
			name = this.currentLexeme.getText();

			// advance over identifier
			this.advance();
		}
		else if (identifierRequired)
		{
			this.throwParseError(JSMessages.getString("error.function.name")); //$NON-NLS-1$
		}

		// advance over '('
		parameters = this.createNode(JSParseNodeTypes.EMPTY, currentLexeme);
		prevLexeme = currentLexeme;
		this.assertAndAdvance(JSTokenTypes.LPAREN, "error.function.lparen"); //$NON-NLS-1$

		// handle formal parameter list
		if (this.isType(JSTokenTypes.IDENTIFIER))
		{
			parameters = this.parseFormalParameterList();
		}

		// advance over ')'
		parameters.includeLexemeInRange(currentLexeme);
		this.assertAndAdvance(JSTokenTypes.RPAREN, "error.function.rparen"); //$NON-NLS-1$

		// advance over '{'
		this.prevLexeme = this.currentLexeme;
		this.assertAndAdvance(JSTokenTypes.LCURLY, "error.function.lcurly"); //$NON-NLS-1$

		// parse body and retrieve
		body = this.parseFunctionBody();

		// get '}' index
		rcurly = this.currentLexeme;

		// advance over '}'
		this.assertAndAdvance(JSTokenTypes.RCURLY, "error.function.rcurly"); //$NON-NLS-1$

		// create function node
		result = this.createNode(JSParseNodeTypes.FUNCTION, keyword);
		if (name != null)
		{
			result.setAttribute("name", name); //$NON-NLS-1$
		}
		result.appendChild(parameters);
		result.appendChild(body);

		result.includeLexemeInRange(rcurly);

		return result;
	}

	/**
	 * <pre>
	 *      LeftHandSideExpression
	 *      	:    NewExpression
	 *      	|    CallExpression
	 *      	;
	 * </pre>
	 */

	/**
	 * Parse IfStatement
	 * <p>
	 * 
	 * <pre>
	 *      IfStatement
	 *      	:    IF LPAREN Expression RPAREN Statement ELSE Statement
	 *      	|    IF LPAREN Expression RPAREN Statement
	 *      	;
	 * </pre>
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private JSParseNode parseIfStatement() throws ParseException, LexerException
	{
		JSParseNode result;

		JSParseNode condition;
		JSParseNode trueCase;
		JSParseNode falseCase = this.createNode(JSParseNodeTypes.EMPTY, null); // JSParseNode.Empty;
		Lexeme keyword = this.currentLexeme;

		// advance over 'if'
		this.assertAndAdvance(JSTokenTypes.IF, "error.internal.keyword"); //$NON-NLS-1$

		// advance over '('
		this.assertAndAdvance(JSTokenTypes.LPAREN, "error.if.lparen"); //$NON-NLS-1$

		// parse condition
		condition = this.parseExpression(false, "error.if.condition"); //$NON-NLS-1$

		// advance over ')'
		this.assertAndAdvance(JSTokenTypes.RPAREN, "error.if.rparen"); //$NON-NLS-1$

		// parse true case
		trueCase = this.parseStatement();

		if (this.isType(JSTokenTypes.ELSE))
		{
			// advance over 'else'
			this.advance();

			// parse false case
			falseCase = this.parseStatement();
		}

		// create command node
		result = this.createNode(JSParseNodeTypes.IF, keyword);
		result.appendChild(condition);
		result.appendChild(trueCase);
		result.appendChild(falseCase);

		return result;
	}

	/**
	 * Parse LabelledStatement
	 * <p>
	 * 
	 * <pre>
	 *      LabelledStatement
	 *      	:   Identifier COLON Statement
	 *      	;
	 * </pre>
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private JSParseNode parseLabelledStatement() throws ParseException, LexerException
	{
		JSParseNode result;
		Lexeme identifier = this.currentLexeme;

		result = this.parseExpression(false, null); // TODO: add error message for this case

		if (this.isType(JSTokenTypes.COLON))
		{
			// recover label name
			JSParseNode label = this.createNode(JSParseNodeTypes.IDENTIFIER, identifier);

			// is label, advance over ':'
			this.advance();

			// parse statement following label
			JSParseNode statement = this.parseStatement();

			// create command node
			result = this.createNode(JSParseNodeTypes.LABELLED, null);
			result.appendChild(label);
			result.appendChild(statement);
		}

		// handle ';'
		this.handleOptionalSemicolon(result);

		// return result
		return result;
	}

	/**
	 * Parse LogicalANDExpression
	 * <p>
	 * 
	 * <pre>
	 *      LogicalANDExpression
	 *      	:    BitwiseORExpression
	 *      	|    LogicalANDExpression AMPERSAND_AMPERSAND BitwiseORExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseLogicalANDExpression(boolean noIn) throws ParseException, LexerException
	{
		JSParseNode result = this.parseBitwiseORExpression(noIn);

		while (this.isType(JSTokenTypes.AMPERSAND_AMPERSAND))
		{
			JSParseNode lhs = result;
			JSParseNode rhs;

			// get operator index
			Lexeme operator = this.currentLexeme;

			// advance over '&&'
			this.advance();

			// get right-hand side
			rhs = this.parseBitwiseORExpression(noIn);

			// create command node
			result = this.createNode(JSParseNodeTypes.LOGICAL_AND, operator);
			result.appendChild(lhs);
			result.appendChild(rhs);
		}

		return result;
	}

	/**
	 * Parse LogicalORExpression
	 * <p>
	 * 
	 * <pre>
	 *      LogicalORExpression
	 *      	:    LogicalANDExpression
	 *      	|    LogicalORExpression PIPE_PIPE LogicalANDExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseLogicalORExpression(boolean noIn) throws ParseException, LexerException
	{
		JSParseNode result = this.parseLogicalANDExpression(noIn);

		while (this.isType(JSTokenTypes.PIPE_PIPE))
		{
			JSParseNode lhs = result;
			JSParseNode rhs;

			// get operator index
			Lexeme operator = this.currentLexeme;

			// advance over '||'
			this.advance();

			// get right-hand size
			rhs = this.parseLogicalANDExpression(noIn);

			// create command node
			result = this.createNode(JSParseNodeTypes.LOGICAL_OR, operator);
			result.appendChild(lhs);
			result.appendChild(rhs);
		}

		return result;
	}

	/**
	 * Parse MemberExpression
	 * <p>
	 * 
	 * <pre>
	 *      MemberExpression
	 *      	:    PrimaryExpression
	 *      	|    FunctionExpression
	 *      	|    MemberExpression LBRACKET Expression RBRACE
	 *      	|    MemberExpression DOT IDENTIFIER
	 *      	|    NEW MemberExpression Arguments
	 *      	;
	 * </pre>
	 * 
	 * @param isInvocable
	 *            A boolean indicating whether invocation is allowed after this member expression
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseMemberExpression(boolean isInvocable) throws ParseException, LexerException
	{
		JSParseNode result = null;

		switch (this.currentLexeme.typeIndex)
		{
			case JSTokenTypes.FUNCTION:
				// parse function
				result = this.parseFunctionDeclaration(false);
				break;

			case JSTokenTypes.NEW:
				Lexeme newKeyword = this.currentLexeme;

				// advance over 'new'
				this.advance();

				// parse member expression following 'new'
				JSParseNode memberExpression = this.parseMemberExpression(false);

				// parse arguments
				JSParseNode arguments = this.createNode(JSParseNodeTypes.EMPTY, currentLexeme); // JSParseNode.Empty;

				if (this.isType(JSTokenTypes.LPAREN))
				{
					// advance over '('
					this.advance();

					if (this.isType(JSTokenTypes.RPAREN) == false)
					{
						// parse parameter list
						arguments = this.parseArgumentList();
					}
					else
					{
						arguments.includeLexemeInRange(currentLexeme);
					}

					// advance over ');
					this.assertAndAdvance(JSTokenTypes.RPAREN, "error.new.rparen"); //$NON-NLS-1$
				}

				// create command node
				result = this.createNode(JSParseNodeTypes.CONSTRUCT, newKeyword);
				result.appendChild(memberExpression);
				result.appendChild(arguments);

				break;

			default:
				result = this.parsePrimaryExpression();
				break;
		}

		memberTail: while (this.inSet(postfixMemberExpressionSet))
		{
			switch (this.currentLexeme.typeIndex)
			{
				case JSTokenTypes.DOT:
					Lexeme dot = this.currentLexeme;
					JSParseNode identifier;

					// advance over '.'
					this.advance();

					// make sure we have an identifier
					this.assertType(JSTokenTypes.IDENTIFIER, "error.get.property.name"); //$NON-NLS-1$

					// get id index
					identifier = this.createNode(JSParseNodeTypes.IDENTIFIER, this.currentLexeme);

					// advance over identifier
					this.advance();

					// add getName node
					JSParseNode temp = this.createNode(JSParseNodeTypes.GET_PROPERTY, dot);
					temp.appendChild(result);
					temp.appendChild(identifier);
					result = temp;

					break;

				case JSTokenTypes.LBRACKET:
					Lexeme lbrace = this.currentLexeme;
					// advance over '['
					this.advance();

					// get index expression
					JSParseNode indexExpression = this.parseExpression(false, "error.get.element.expression"); //$NON-NLS-1$

					// advance over ']'
					this.assertAndAdvance(JSTokenTypes.RBRACKET, "error.get.element.rbracket"); //$NON-NLS-1$

					// add index node
					temp = this.createNode(JSParseNodeTypes.GET_ELEMENT, lbrace);
					temp.appendChild(result);
					temp.appendChild(indexExpression);
					result = temp;

					break;

				case JSTokenTypes.LPAREN:
					if (isInvocable == false)
					{
						break memberTail;
					}

					JSParseNode arguments = this.createNode(JSParseNodeTypes.EMPTY, this.currentLexeme); // JSParseNode.Empty;
					Lexeme lparen = this.currentLexeme;

					// advance over '('
					this.advance();

					if (this.currentLexeme.typeIndex != JSTokenTypes.RPAREN)
					{
						// parse parameter list
						arguments = this.parseArgumentList();
						arguments.includeLexemeInRange(this.currentLexeme);
					}
					else
					{
						arguments.includeLexemeInRange(this.currentLexeme);
					}
					Lexeme end = this.currentLexeme;
					// advance over ');
					this.assertAndAdvance(JSTokenTypes.RPAREN, "error.invocation.rparen"); //$NON-NLS-1$

					// create invoke node
					temp = this.createNode(JSParseNodeTypes.INVOKE, lparen);
					temp.appendChild(result);
					temp.appendChild(arguments);
					temp.includeLexemeInRange(end);
					result = temp;

					break;

				default:
					break;
			}
		}

		return result;
	}

	/**
	 * Parse MultiplicativeExpression
	 * <p>
	 * 
	 * <pre>
	 *      MultiplicativeExpression
	 *      	:    UnaryExpression
	 *      	|    MultiplicativeExpression STAR UnaryExpression
	 *      	|    MultiplicativeExpression FORWARD_SLASH UnaryExpression
	 *      	|    MultiplicativeExpression PERCENT UnaryExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseMultiplicativeExpression() throws ParseException, LexerException
	{
		JSParseNode result = this.parseUnaryExpression();

		while (this.inSet(multiplicativeExpressionSet))
		{
			JSParseNode lhs = result;
			JSParseNode rhs;

			// save operator type
			int operatorType = this.currentLexeme.typeIndex;
			Lexeme operator = this.currentLexeme;

			// advance over '*', '/', or '%'
			this.advance();

			// get right-hand side
			rhs = this.parseUnaryExpression();

			switch (operatorType)
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

				default:
					String typeName = JSTokenTypes.getName(operatorType);
					this.throwParseError(JSMessages.getString("error.internal.multiplicative.expression") + typeName); //$NON-NLS-1$
			}

			result.appendChild(lhs);
			result.appendChild(rhs);
		}

		return result;
	}

	/**
	 * <pre>
	 *      NewExpression
	 *      	:    MemberExpression
	 *      	|    NEW NewExpression
	 *      	;
	 * </pre>
	 */

	/**
	 * Parse ObjectLiteral
	 * <p>
	 * 
	 * <pre>
	 *      ObjectLiteral
	 *      	:    LCURLY RCURLY
	 *      	|    LCURLY PropertyNameAndValueList RCURLY
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseObjectLiteral() throws ParseException, LexerException
	{
		JSParseNode result = this.createNode(JSParseNodeTypes.OBJECT_LITERAL, this.currentLexeme);

		// advance over '{'
		this.assertAndAdvance(JSTokenTypes.LCURLY, "error.internal.punctuator"); //$NON-NLS-1$

		if (this.inSet(propertyNameSet))
		{
			// parse and add name/value pair
			result.appendChild(this.parseNameValuePair());

			while (this.isType(JSTokenTypes.COMMA))
			{
				// advance over ','
				this.advance();

				// allow an object literal body to end with a comma
				if (this.isType(JSTokenTypes.RCURLY) == false)
				{
					// parse and add name/value pair
					result.appendChild(this.parseNameValuePair());
				}
			}
		}

		// advance over '}'
		result.includeLexemeInRange(this.currentLexeme);
		this.assertAndAdvance(JSTokenTypes.RCURLY, "error.object.literal.rcurly"); //$NON-NLS-1$

		// return result
		return result;
	}

	/**
	 * Parse PrimaryExpression
	 * <p>
	 * 
	 * <pre>
	 *      PrimaryExpression
	 *      	:    THIS
	 *      	|    IDENTIFIER
	 *      	|    Literal
	 *      	|    ArrayLiteral
	 *      	|    ObjectLiteral
	 *      	|    LPAREN Expression RPAREN
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parsePrimaryExpression() throws ParseException, LexerException
	{
		JSParseNode result = null;

		switch (this.currentLexeme.typeIndex)
		{
			case JSTokenTypes.FORWARD_SLASH:
			case JSTokenTypes.FORWARD_SLASH_EQUAL:
				Lexeme save = this.currentLexeme;

				// We're supposed to have a regex here but the lexer is reporting the incorrect type since it doesn't
				// have the proper context, so we have to re-scan this section of source
				this.rescan(REGEX_GROUP);

				// make sure we ended up with a REGEX
				if (this.currentLexeme.typeIndex == JSTokenTypes.REGEX)
				{
					// save regex node
					result = this.createNode(JSParseNodeTypes.REGULAR_EXPRESSION, this.currentLexeme);

					// advance over regex
					this.advance();
				}
				else
				{
					ILexer lexer = this.getLexer();

					// reset lexeme to get a better error
					this.currentLexeme = save;

					// put lexeme back into list
					this.addLexeme(this.currentLexeme);

					// reset the lexer's offset to the beginning of the invalid lexeme's position
					lexer.setCurrentOffset(save.getEndingOffset());

					// switch lexer over to a group that will scan for the correct token type
					lexer.setGroup(DEFAULT_GROUP);

					// throw error
					this.assertType(JSTokenTypes.REGEX, "error.regex"); //$NON-NLS-1$
				}
				break;

			case JSTokenTypes.REGEX:
				// save regex node
				result = this.createNode(JSParseNodeTypes.REGULAR_EXPRESSION, this.currentLexeme);

				// advance over regex
				this.advance();
				break;

			case JSTokenTypes.FALSE:
				// save false node
				result = this.createNode(JSParseNodeTypes.FALSE, this.currentLexeme);

				// advance over 'false'
				this.advance();
				break;

			case JSTokenTypes.IDENTIFIER:
				// save identifier
				result = this.createNode(JSParseNodeTypes.IDENTIFIER, this.currentLexeme);

				// advance over identifier
				this.advance();

				break;

			case JSTokenTypes.LBRACKET:
				result = this.parseArrayLiteral();
				break;

			case JSTokenTypes.LCURLY:
				result = this.parseObjectLiteral();
				break;

			case JSTokenTypes.LPAREN:
				Lexeme lparen = this.currentLexeme;
				// advance over '('
				this.advance();

				// get interior expression
				JSParseNode expression = this.parseExpression(false, "error.group.expression"); //$NON-NLS-1$

				// make sure we have ')'
				this.assertType(JSTokenTypes.RPAREN, "error.group.rparen"); //$NON-NLS-1$

				// advance over ')'
				this.advance();

				// create command node
				result = this.createNode(JSParseNodeTypes.GROUP, lparen);
				result.appendChild(expression);

				break;

			case JSTokenTypes.NULL:
				// save null node
				result = this.createNode(JSParseNodeTypes.NULL, this.currentLexeme);

				// advance over 'null'
				this.advance();
				break;

			case JSTokenTypes.NUMBER:
				// save number node
				result = this.createNode(JSParseNodeTypes.NUMBER, this.currentLexeme);

				// advance over number
				this.advance();
				break;

			case JSTokenTypes.STRING:
				// save string node
				result = this.createNode(JSParseNodeTypes.STRING, this.currentLexeme);

				// advance over string
				this.advance();
				break;

			case JSTokenTypes.THIS:
				// save 'this' node
				result = this.createNode(JSParseNodeTypes.THIS, this.currentLexeme);

				// advance over 'this'
				this.advance();
				break;

			case JSTokenTypes.TRUE:
				// save true node
				result = this.createNode(JSParseNodeTypes.TRUE, this.currentLexeme);

				// advance over 'true'
				this.advance();
				break;

			default:
				// NOTE: If we want to throw errors on future reserved keywords, that would need to be done here
				String message = JSMessages.getString("error.primitive"); //$NON-NLS-1$

				if (this.isEOS())
				{
					message += JSMessages.getString("error.found.eof"); //$NON-NLS-1$
				}

				this.throwParseError(message);
		}

		return result;
	}

	/**
	 * Parse PropertyNameAndValueList
	 * <p>
	 * 
	 * <pre>
	 *      PropertyNameAndValueList
	 *      	:    PropertyName COLON AssignmentExpression
	 *      	|    PropertyNameAndValueList COMMA PropertyName COLON AssignmentExpression
	 *      	;
	 *      PropertyName
	 *      	:    IDENTIFIER
	 *      	|    STRING
	 *      	|    NUMBER
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseNameValuePair() throws ParseException, LexerException
	{
		JSParseNode result;

		JSParseNode name;
		JSParseNode value;

		// make sure we have a valid property name
		this.assertInSet(propertyNameSet, "error.object.literal.name"); //$NON-NLS-1$

		String identifier = this.currentLexeme.getText();

		if ("get".equals(identifier) || "set".equals(identifier)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			Lexeme keyword = this.currentLexeme;

			// advance over keyword
			this.advance();
			
			switch (this.currentLexeme.typeIndex)
			{
				case JSTokenTypes.COLON:
					// get name
					name = this.createNode(JSParseNodeTypes.IDENTIFIER, keyword);
					
					// advance over ':'
					this.assertAndAdvance(JSTokenTypes.COLON, "error.object.literal.colon"); //$NON-NLS-1$
		
					// parse expression
					value = this.parseAssignmentExpression(false);
					break;
			
				default:
					// get name
					name = this.createNode(JSParseNodeTypes.IDENTIFIER, this.currentLexeme);
					
					// parse function declaration
					value = this.parseFunctionDeclaration2(true, keyword);
					break;
			}
		}
		else
		{
			// get name
			name = this.createNode(JSParseNodeTypes.IDENTIFIER, this.currentLexeme);

			// advance over property name
			this.advance();

			// advance over ':'
			this.assertAndAdvance(JSTokenTypes.COLON, "error.object.literal.colon"); //$NON-NLS-1$

			// parse expression
			value = this.parseAssignmentExpression(false);
		}

		// create new command node
		result = this.createNode(JSParseNodeTypes.NAME_VALUE_PAIR, null);
		result.appendChild(name);
		result.appendChild(value);

		return result;
	}

	/**
	 * Parse RelationalExpression
	 * <p>
	 * 
	 * <pre>
	 *      RelationalExpression
	 *      	:    ShiftExpression
	 *      	|    RelationalExpression LESS ShiftExpression
	 *      	|    RelationalExpression GREATER ShiftExpression
	 *      	|    RelationalExpression LESS_EQUAL ShiftExpression
	 *      	|    RelationalExpression GREATER_EQUAL ShiftExpression
	 *      	|    RelationalExpression INSTANCEOF ShiftExpression
	 *      	|    RelationalExpression IN ShiftExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseRelationalExpression(boolean noIn) throws ParseException, LexerException
	{
		JSParseNode result = this.parseShiftExpression();

		while (this.inSet(relationalExpressionSet))
		{
			// check if we're supposed to include 'in'
			if (noIn)
			{
				break;
			}

			JSParseNode lhs = result;
			JSParseNode rhs;

			// save operator type
			int operatorType = this.currentLexeme.typeIndex;
			Lexeme operator = this.currentLexeme;

			// advance over '<', '>', '<=', '>=', 'instanceof', or 'in'
			this.advance();

			// get right-hand side
			rhs = this.parseShiftExpression();

			switch (operatorType)
			{
				case JSTokenTypes.LESS:
					result = this.createNode(JSParseNodeTypes.LESS_THAN, operator);
					result.appendChild(lhs);
					result.appendChild(rhs);
					break;

				case JSTokenTypes.GREATER:
					result = this.createNode(JSParseNodeTypes.GREATER_THAN, operator);
					result.appendChild(lhs);
					result.appendChild(rhs);
					break;

				case JSTokenTypes.LESS_EQUAL:
					result = this.createNode(JSParseNodeTypes.LESS_THAN_OR_EQUAL, operator);
					result.appendChild(lhs);
					result.appendChild(rhs);
					break;

				case JSTokenTypes.GREATER_EQUAL:
					result = this.createNode(JSParseNodeTypes.GREATER_THAN_OR_EQUAL, operator);
					result.appendChild(lhs);
					result.appendChild(rhs);
					break;

				case JSTokenTypes.INSTANCEOF:
					result = this.createNode(JSParseNodeTypes.INSTANCE_OF, operator);
					result.appendChild(lhs);
					result.appendChild(rhs);
					break;

				case JSTokenTypes.IN:
					result = this.createNode(JSParseNodeTypes.IN, operator);
					result.appendChild(lhs);
					result.appendChild(rhs);
					break;

				default:
					String typeName = JSTokenTypes.getName(operatorType);
					this.throwParseError(JSMessages.getString("error.internal.relational.expression") + typeName); //$NON-NLS-1$
			}
		}

		return result;
	}

	/**
	 * Parse ReturnStatement
	 * <p>
	 * 
	 * <pre>
	 *      ReturnStatement
	 *      	:   RETURN [no LINE_TERMINATOR here] Expression? SEMICOLON
	 *      	;
	 * </pre>
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private JSParseNode parseReturnStatement() throws ParseException, LexerException
	{
		JSParseNode result;

		JSParseNode expression = this.createNode(JSParseNodeTypes.EMPTY, null); // JSParseNode.Empty;
		Lexeme keyword = this.currentLexeme;

		// advance over 'return'
		this.assertAndAdvance(JSTokenTypes.RETURN, "error.internal.keyword"); //$NON-NLS-1$

		if (this.inSet(returnSet) == false)
		{
			// parse expression
			expression = this.parseExpression(false, "error.return.expression"); //$NON-NLS-1$
		}

		// create command node
		result = this.createNode(JSParseNodeTypes.RETURN, keyword);
		result.appendChild(expression);

		// handle ';'
		this.handleOptionalSemicolon(result);

		// if (this._inFunction == false)
		// {
		// // TODO: throw error since 'return' is only allowed inside of a function
		// }

		return result;
	}

	/**
	 * Parse ShiftExpression
	 * <p>
	 * 
	 * <pre>
	 *      ShiftExpression
	 *      	:    AdditiveExpression
	 *      	|    ShiftExpression LESS_LESS AdditiveExpression
	 *      	|    ShiftExpression GREATER_GREATER AdditiveExpression
	 *      	|    ShiftExpression GREATER_GREATER_GREATER AdditiveExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseShiftExpression() throws ParseException, LexerException
	{
		JSParseNode result = this.parseAdditiveExpression();

		while (this.inSet(shiftExpressionSet))
		{
			JSParseNode lhs = result;
			JSParseNode rhs;

			// save operator type
			int operatorType = this.currentLexeme.typeIndex;
			Lexeme operator = this.currentLexeme;

			// advance over '<<', '>>', or '>>>'
			this.advance();

			// get right-hand side
			rhs = this.parseAdditiveExpression();

			// create command node
			switch (operatorType)
			{
				case JSTokenTypes.LESS_LESS:
					result = this.createNode(JSParseNodeTypes.SHIFT_LEFT, operator);
					result.appendChild(lhs);
					result.appendChild(rhs);
					break;

				case JSTokenTypes.GREATER_GREATER:
					result = this.createNode(JSParseNodeTypes.SHIFT_RIGHT, operator);
					result.appendChild(lhs);
					result.appendChild(rhs);
					break;

				case JSTokenTypes.GREATER_GREATER_GREATER:
					result = this.createNode(JSParseNodeTypes.ARITHMETIC_SHIFT_RIGHT, operator);
					result.appendChild(lhs);
					result.appendChild(rhs);
					break;

				default:
					String typeName = JSTokenTypes.getName(operatorType);
					this.throwParseError(JSMessages.getString("error.internal.shift.expression") + typeName); //$NON-NLS-1$
			}
		}

		return result;
	}

	/**
	 * Parse SourceElement
	 * <p>
	 * 
	 * <pre>
	 *      SourceElement
	 *     	:    FunctionDeclaration
	 *     	|    Statement
	 *     	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseSourceElement() throws ParseException, LexerException
	{
		JSParseNode result = null;
		Lexeme startingLexeme = this.currentLexeme;

		if (startingLexeme != EOS)
		{
			switch (startingLexeme.typeIndex)
			{
				case JSTokenTypes.FUNCTION:
					try
					{
						result = this.parseFunctionDeclaration(true);
					}
					catch (ParseException pe)
					{
						result = this.recover(startingLexeme, pe);
					}
					break;

				default:
					result = this.parseStatement();
					break;
			}
		}

		return result;
	}

	/**
	 * Parse a statement
	 * <p>
	 * 
	 * <pre>
	 *      Statement
	 *      	:   Block
	 *      	|   BreakStatement
	 *      	|   ContinueStatement
	 *      	|   EmptyStatement
	 *      	|   ExpressionStatement
	 *      	|   IfStatement
	 *      	|   IterationStatement
	 *      	|   LabelledStatement
	 *      	|   ReturnStatement
	 *      	|   SwitchStatement
	 *      	|   ThrowStatement
	 *      	|   TryStatement
	 *      	|   VariableStatement
	 *      	|   WithStatement
	 *      	;
	 *      EmptyStatement
	 *      	:	SEMICOLON
	 *      	;
	 *      IterationStatement
	 *      	:    DoStatement
	 *      	|    WhileStatement
	 *      	|    ForStatement
	 *      	;
	 * </pre>
	 * 
	 * @throws LexerException
	 */
	private JSParseNode parseStatement() throws LexerException
	{
		Lexeme startingLexeme = this.currentLexeme;
		JSParseNode result = null;

		try
		{
			switch (this.currentLexeme.typeIndex)
			{
				// Block
				case JSTokenTypes.LCURLY:
					result = this.parseBlock();
					break;

				// BreakStatement
				case JSTokenTypes.BREAK:
					result = this.parseBreakStatement();
					break;

				// ContinueStatement
				case JSTokenTypes.CONTINUE:
					result = this.parseContinueStatement();
					break;

				// EmptyStatement
				case JSTokenTypes.SEMICOLON:
					result = this.createNode(JSParseNodeTypes.EMPTY, this.currentLexeme);
					result.setIncludesSemicolon(true);
					
					// advance over ';'
					this.advance();
					break;

				// ForStatement
				case JSTokenTypes.FOR:
					result = this.parseForStatement();
					break;

				// LabelledStatement
				case JSTokenTypes.IDENTIFIER:
					result = this.parseLabelledStatement();
					break;

				// IfStatement
				case JSTokenTypes.IF:
					result = this.parseIfStatement();
					break;

				// DoStatement
				case JSTokenTypes.DO:
					result = this.parseDoStatement();
					break;

				// ReturnStatement
				case JSTokenTypes.RETURN:
					result = this.parseReturnStatement();
					break;

				// SwitchStatement
				case JSTokenTypes.SWITCH:
					result = this.parseSwitchStatement();
					break;

				// ThrowStatement
				case JSTokenTypes.THROW:
					result = this.parseThrowStatement();
					break;

				// TryStatement
				case JSTokenTypes.TRY:
					result = this.parseTryStatement();
					break;

				// VarStatement
				// TryStatement
				case JSTokenTypes.VAR:
					result = this.parseVarExpression(false);

					// NOTE: we're putting the EOL handling here so we can
					// re-use parseVarExpression in ForStatement
					this.handleOptionalSemicolon(result);
					break;

				// WhileStatement
				case JSTokenTypes.WHILE:
					result = this.parseWhileStatement();
					break;

				// WithStatement
				case JSTokenTypes.WITH:
					result = this.parseWithStatement();
					break;

				// Expression
				default:
					result = this.parseExpression(false, null); // TODO: add error message
					this.handleOptionalSemicolon(result);
			}
		}
		catch (ParseException pe)
		{
			result = this.recover(startingLexeme, pe);
		}

		return result;
	}

	/**
	 * <pre>
	 *      StatementList
	 *      	:    Statement
	 *      	|    StatementList Statement
	 *      	;
	 * </pre>
	 */

	/**
	 * Parse SwitchStatement
	 * <p>
	 * 
	 * <pre>
	 *      SwitchStatement
	 *      	:    SWITCH LPAREN Expression RPAREN CaseBlock
	 *      	;
	 *      CaseBlock
	 *      	:    LCURLY RCURLY
	 *      	|    LCURLY CaseClauses RCURLY
	 *      	|    LCURLY CaseClauses DefaultClause RCURLY
	 *      	|    LCURLY CaseClauses DefaultClause CaseClauses RCURLY
	 *      	|    LCURLY DefaultClause RCURLY
	 *      	|    LCURLY DefaultClause CaseClauses RCURLY
	 *      	;
	 *      CaseClauses
	 *      	:    CaseClause
	 *      	|    CaseClauses CaseClause
	 *      	;
	 * </pre>
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private JSParseNode parseSwitchStatement() throws ParseException, LexerException
	{
		JSParseNode result;

		boolean hasDefault = false;
		Lexeme keyword = this.currentLexeme;

		// advance over 'switch'
		this.assertAndAdvance(JSTokenTypes.SWITCH, "error.internal.keyword"); //$NON-NLS-1$

		// advance over '('
		this.assertAndAdvance(JSTokenTypes.LPAREN, "error.switch.lparen"); //$NON-NLS-1$

		// parse expression
		JSParseNode expression = this.parseExpression(false, "error.switch.expression"); //$NON-NLS-1$

		// create command node
		result = this.createNode(JSParseNodeTypes.SWITCH, keyword);
		result.appendChild(expression);

		// advance over ')'
		this.assertAndAdvance(JSTokenTypes.RPAREN, "error.switch.rparen"); //$NON-NLS-1$

		// advance over '{'
		this.assertAndAdvance(JSTokenTypes.LCURLY, "error.switch.lcurly"); //$NON-NLS-1$

		// process all case and default blocks
		while (this.isType(JSTokenTypes.RCURLY) == false && this.isEOS() == false)
		{
			// parse 'case' or 'default'
			JSParseNode block = this.parseCaseOrDefaultBlock();

			// make sure we have only one default case
			if (block.getTypeIndex() == JSParseNodeTypes.DEFAULT)
			{
				if (hasDefault)
				{
					this.throwParseError(JSMessages.getString("error.switch.default.duplicate")); //$NON-NLS-1$
				}
				else
				{
					hasDefault = true;
				}
			}

			// add to switch
			result.appendChild(block);
		}

		// advance over '}'
		result.includeLexemeInRange(currentLexeme);
		this.assertAndAdvance(JSTokenTypes.RCURLY, "error.switch.rcurly"); //$NON-NLS-1$

		return result;
	}

	/**
	 * Parse ThrowStatement
	 * <p>
	 * 
	 * <pre>
	 *      ThrowStatement
	 *      	:    THROW Expression SEMICOLON
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseThrowStatement() throws ParseException, LexerException
	{
		JSParseNode result;

		Lexeme keyword = this.currentLexeme;

		// advance over 'throw;
		this.assertAndAdvance(JSTokenTypes.THROW, "error.internal.keyword"); //$NON-NLS-1$

		// parse expression
		JSParseNode expression = this.parseExpression(false, "error.throw.expression"); //$NON-NLS-1$

		// create command node
		result = this.createNode(JSParseNodeTypes.THROW, keyword);
		result.appendChild(expression);

		// handle ';'
		this.handleOptionalSemicolon(result);

		// return result
		return result;
	}

	/**
	 * Parse TryStatement
	 * <p>
	 * 
	 * <pre>
	 *      TryStatement
	 *      	:    TRY Block Catch
	 *      	|    TRY Block Finally
	 *      	|    TRY Block Catch Finally
	 *      	;
	 * </pre>
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private JSParseNode parseTryStatement() throws ParseException, LexerException
	{
		JSParseNode result;

		JSParseNode body;
		JSParseNode catchNode = this.createNode(JSParseNodeTypes.EMPTY, null); // JSParseNode.Empty;
		JSParseNode finallyNode = this.createNode(JSParseNodeTypes.EMPTY, null); // JSParseNode.Empty;

		// advance over 'try'
		this.assertType(JSTokenTypes.TRY, "error.internal.keyword"); //$NON-NLS-1$

		// get 'try' index
		Lexeme keyword = this.currentLexeme;

		// advance over 'try'
		this.advance();

		// parse body
		body = this.parseBlock();

		if (this.isType(JSTokenTypes.CATCH))
		{
			// parse catch
			catchNode = this.parseCatch();
		}

		if (this.isType(JSTokenTypes.FINALLY))
		{
			// parse finally
			finallyNode = this.parseFinally();
		}

		// make sure either catch or finally is defined
		if (catchNode == null && finallyNode == null)
		{
			this.throwParseError(JSMessages.getString("error.try.empty")); //$NON-NLS-1$
		}

		// create command node
		result = this.createNode(JSParseNodeTypes.TRY, keyword);
		result.appendChild(body);
		if (catchNode.isEmpty())
		{
			result.includeLexemeInRange(catchNode.getEndingLexeme());
		}
		result.appendChild(catchNode);
		if (!finallyNode.isEmpty())
		{
			result.includeLexemeInRange(finallyNode.getEndingLexeme());
		}
		result.appendChild(finallyNode);

		return result;
	}

	/**
	 * Parse UnaryExpression
	 * <p>
	 * 
	 * <pre>
	 *      UnaryExpression
	 *      	:    PostfixExpression
	 *      	|    DELETE UnaryExpression
	 *      	|    VOID UnaryExpression
	 *      	|    TYPEOF UnaryExpression
	 *      	|    PLUS_PLUS UnaryExpression
	 *      	|    MINUS_MINUS UnaryExpression
	 *      	|    PLUS UnaryExpression
	 *      	|    MINUS UnaryExpression
	 *      	|    TILDE UnaryExpression
	 *      	|    EXCLAMATION UnaryExpression
	 *      	;
	 *      PostfixExpression
	 *      	:    LeftHandSideExpression
	 *      	|    LeftHandSideExpression PLUS_PLUS
	 *      	|    LeftHandSideExpression MINUS_MINUS
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseUnaryExpression() throws ParseException, LexerException
	{
		JSParseNode result = null;

		if (this.inSet(unaryExpressionSet))
		{
			// save type
			int type = this.currentLexeme.typeIndex;

			// determine which expression type to call
			boolean isMemberExpression = this.inSet(postfixExpressionSet);

			// get operator index
			Lexeme operator = this.currentLexeme;

			// advance over operator
			this.advance();

			if (isMemberExpression)
			{
				// parse member expression
				result = this.parseMemberExpression(true);
			}
			else
			{
				// parse unary expression
				result = this.parseUnaryExpression();
			}

			switch (type)
			{
				case JSTokenTypes.DELETE:
					JSParseNode temp = this.createNode(JSParseNodeTypes.DELETE, operator);
					temp.appendChild(result);
					result = temp;
					break;

				case JSTokenTypes.EXCLAMATION:
					temp = this.createNode(JSParseNodeTypes.LOGICAL_NOT, operator);
					temp.appendChild(result);
					result = temp;
					break;

				case JSTokenTypes.MINUS:
					temp = this.createNode(JSParseNodeTypes.NEGATE, operator);
					temp.appendChild(result);
					result = temp;
					break;

				case JSTokenTypes.MINUS_MINUS:
					temp = this.createNode(JSParseNodeTypes.PRE_DECREMENT, operator);
					temp.appendChild(result);
					result = temp;
					break;

				case JSTokenTypes.PLUS:
					temp = this.createNode(JSParseNodeTypes.POSITIVE, operator);
					temp.appendChild(result);
					result = temp;
					break;

				case JSTokenTypes.PLUS_PLUS:
					temp = this.createNode(JSParseNodeTypes.PRE_INCREMENT, operator);
					temp.appendChild(result);
					result = temp;
					break;

				case JSTokenTypes.TILDE:
					temp = this.createNode(JSParseNodeTypes.BITWISE_NOT, operator);
					temp.appendChild(result);
					result = temp;
					break;

				case JSTokenTypes.TYPEOF:
					temp = this.createNode(JSParseNodeTypes.TYPEOF, operator);
					temp.appendChild(result);
					result = temp;
					break;

				case JSTokenTypes.VOID:
					temp = this.createNode(JSParseNodeTypes.VOID, operator);
					temp.appendChild(result);
					result = temp;
					break;

				default:
					break;
			}
		}
		else
		{
			result = this.parseMemberExpression(true);

			if (this.currentLexeme.isAfterEOL() == false && this.inSet(postfixExpressionSet))
			{
				int type = this.currentLexeme.typeIndex;

				// get operator index
				Lexeme operator = this.currentLexeme;

				// advance over '--' or '++'
				this.advance();

				switch (type)
				{
					case JSTokenTypes.PLUS_PLUS:
						JSParseNode temp = this.createNode(JSParseNodeTypes.POST_INCREMENT, operator);
						temp.appendChild(result);
						result = temp;
						break;

					case JSTokenTypes.MINUS_MINUS:
						temp = this.createNode(JSParseNodeTypes.POST_DECREMENT, operator);
						temp.appendChild(result);
						result = temp;
						break;

					default:
						break;
				}
			}
		}

		return result;
	}

	/**
	 * Parse VarExpression
	 * <p>
	 * 
	 * <pre>
	 *      VarExpression
	 *      	:    VAR VariableDeclarationList SEMICOLON
	 *      	;
	 *      VariableDeclarationList
	 *      	:    VariableDeclaration
	 *      	|    VariableDeclarationList COMMA VariableDeclaration
	 *      	;
	 * </pre>
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private JSParseNode parseVarExpression(boolean noIn) throws ParseException, LexerException
	{
		JSParseNode result = this.createNode(JSParseNodeTypes.VAR, this.currentLexeme);

		// advance over 'var'
		this.assertAndAdvance(JSTokenTypes.VAR, "error.internal.keyword"); //$NON-NLS-1$

		// add variable declaration to our collection
		result.appendChild(this.parseVariableDeclaration(noIn));

		while (this.isType(JSTokenTypes.COMMA))
		{
			// advance over ','
			this.advance();

			// add variable declaration to our collection
			result.appendChild(this.parseVariableDeclaration(noIn));
		}

		return result;
	}

	/**
	 * Parse VariableDeclaration
	 * <p>
	 * 
	 * <pre>
	 *      VariableDeclaration
	 *      	:    IDENTIFIER
	 *      	|    IDENTIFIER EQUAL AssignmentExpression
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseVariableDeclaration(boolean noIn) throws ParseException, LexerException
	{
		JSParseNode id;
		JSParseNode assignment = this.createNode(JSParseNodeTypes.EMPTY, null); // JSParseNode.Empty;
		JSParseNode result;

		// make sure we have at least one identifier
		this.assertType(JSTokenTypes.IDENTIFIER, "error.var.name"); //$NON-NLS-1$

		// grab name
		id = this.createNode(JSParseNodeTypes.IDENTIFIER, this.currentLexeme);

		// advance over identifier
		this.advance();

		if (this.isType(JSTokenTypes.EQUAL))
		{
			// advance over '='
			this.advance();

			// get right-hand side
			assignment = this.parseAssignmentExpression(noIn);
		}

		// create result
		result = this.createNode(JSParseNodeTypes.DECLARATION, null);
		result.appendChild(id);
		result.appendChild(assignment);

		return result;
	}

	/**
	 * Parse WhileStatement
	 * <p>
	 * 
	 * <pre>
	 *      WhileStatement
	 *      	:    WHILE LPAREN Expression RPAREN Statement
	 *      	;
	 * </pre>
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private JSParseNode parseWhileStatement() throws ParseException, LexerException
	{
		JSParseNode result;

		JSParseNode condition;
		JSParseNode body;
		Lexeme keyword = this.currentLexeme;

		// advance over 'while'
		this.assertAndAdvance(JSTokenTypes.WHILE, "error.internal.keyword"); //$NON-NLS-1$

		// advance over '('
		this.assertAndAdvance(JSTokenTypes.LPAREN, "error.while.lparen"); //$NON-NLS-1$

		// parse condition
		condition = this.parseExpression(false, "error.while.expression"); //$NON-NLS-1$

		// advance over ')'
		this.assertAndAdvance(JSTokenTypes.RPAREN, "error.while.rparen"); //$NON-NLS-1$

		// parse body
		body = this.parseStatement();

		result = this.createNode(JSParseNodeTypes.WHILE, keyword);
		result.appendChild(condition);
		result.appendChild(body);

		return result;
	}

	/**
	 * Parse WithStatement
	 * <p>
	 * 
	 * <pre>
	 *      WithStatement
	 *      	:    WITH LPAREN Expression RPAREN Statement
	 *      	;
	 * </pre>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private JSParseNode parseWithStatement() throws ParseException, LexerException
	{
		JSParseNode result;

		JSParseNode expression;
		JSParseNode body;
		Lexeme keyword = this.currentLexeme;

		// advance over 'with'
		this.assertAndAdvance(JSTokenTypes.WITH, "error.internal.keyword"); //$NON-NLS-1$

		// advance over '('
		this.assertAndAdvance(JSTokenTypes.LPAREN, "error.with.lparen"); //$NON-NLS-1$

		// parse expression
		expression = this.parseExpression(false, "error.with.expression"); //$NON-NLS-1$

		// advance over '('
		this.assertAndAdvance(JSTokenTypes.RPAREN, "error.with.rparen"); //$NON-NLS-1$

		// parse body
		body = this.parseStatement();

		result = this.createNode(JSParseNodeTypes.WITH, keyword);
		result.appendChild(expression);
		result.appendChild(body);

		return result;
	}

	/**
	 * Try to recover from a parse error
	 * 
	 * @param exception
	 * @return Returns an error node including all lexemes that are considered in error
	 * @throws LexerException
	 */
	private JSParseNode recover(Lexeme startingLexeme, ParseException exception) throws LexerException
	{
		// try to recover to a known good starting point
		JSParseNode error;

		// create a new error node
		error = this.createNode(JSParseNodeTypes.ERROR, startingLexeme);

		// if (this._currentLexeme != EOS)
		// {
		// // associate error token with error node
		// error.setErrorRange(this._currentLexeme);
		// }
		// else if (startingLexeme != EOS)
		// {
		// // associate error token with error node
		// error.setErrorRange(startingLexeme);
		// }

		if (startingLexeme != EOS)
		{
			LexemeList lexemes = this.getLexemeList();

			int lexemeCount = lexemes.size();
			boolean inStopSet = false;

			// get starting and ending lexeme indexes
			int start = lexemes.getLexemeIndex(startingLexeme);
			int stop = (this.currentLexeme != EOS) ? lexemes.getLexemeIndex(this.currentLexeme) : lexemeCount;

			// boolean inBlock = this._inFunction == true || this._inBlock == true;
			// boolean isRCurly = this._currentLexeme.typeIndex == JSTokenTypes.RCURLY;
			// boolean consumeCurly = inBlock && isRCurly;
			//				
			// if (start == stop && consumeCurly == false)
			if (start == stop)
			{
				stop = Math.min(stop + 1, lexemeCount);
				this.advance();
			}

			// associate all lexemes from the starting lexeme to the current
			// lexeme with this error node
			// for (int i = start; i < stop; i++)
			// {
			// Lexeme currentLexeme = this._lexemes.get(i);
			//
			// currentLexeme.setCommandNode(error);
			// }

			if (this.isEOS() == false && this.currentLexeme.isAfterEOL())
			{
				inStopSet = this.inSet(stopSet);

				if (inStopSet == false)
				{
					// add current lexeme to the error node
					// this._currentLexeme.setCommandNode(error);

					// advance to the next lexeme
					this.advance();
				}
			}

			// stop until we reach the end of a line, a semicolon, or the end of
			// the file
			while (this.isEOS() == false && this.currentLexeme.isAfterEOL() == false && inStopSet == false)
			{
				inStopSet = this.inSet(stopSet);

				// add current lexeme to the error node
				// this._currentLexeme.setCommandNode(error);

				// advance to the next lexeme
				this.advance();
			}
		}

		// mark error node as root command node
		// error.setCommandRoot();

		// add to error list.
		// NOTE: We can't add error nodes to an error list if it does not refer to a range of characters in the
		// source code. EOF errors are empty, so parse methods receiving this error must add lexemes to the error
		// and add the error to the error list
		// if (error.isAtEOF() == false)
		// {
		// this.removeInvalidErrors(this._state);
		// this._errors.add(error);
		// }

		return error;
	}

	/**
	 * Reposition the lexer to the current lexeme's beginning offset, switch to a new lexer group and rescan
	 * 
	 * @param groupName
	 *            The name of the lexer group to switch to
	 * @throws LexerException
	 */
	private void rescan(String groupName) throws LexerException
	{
		ILexer lexer = this.getLexer();
		LexemeList lexemes = this.getLexemeList();

		// remove token since it is (potentially) invalid
		lexemes.remove(this.currentLexeme);

		// update the affected region
		lexemes.getAffectedRegion().includeInRange(this.currentLexeme);

		// set the lexer's group and reset lexer's offset to the beginning of
		// the invalid lexeme's position
		lexer.setLexerState(groupName, this.currentLexeme.offset);

		// force a rescan
		this.advance();
		
		// include new lexeme in refresh region
		if (this.isEOS() == false)
		{
			this.getParseState().addUpdateRegion(this.currentLexeme);
		}
	}

	/**
	 * Throw a parse exception
	 * 
	 * @param message
	 *            The exception message
	 * @throws ParseException
	 */
	protected void throwParseError(String message) throws ParseException
	{
		if (this.currentLexeme != null)
		{
			if (this._expressionErrorKey != null && this._expressionErrorKey.length() > 0)
			{
				message = JSMessages.getString(this._expressionErrorKey);
			}

			if (this.currentLexeme != EOS)
			{
				message += JSMessages.getString("error.near", this.currentLexeme.getText()); //$NON-NLS-1$
			}
		}

		throw new ParseException(message, -1);
	}

	/**
	 * @return language to process text inside of PI instructions
	 */
	public String getPiLanguage()
	{
		return this._piLanguage;
	}

	/**
	 * @param piLanguage
	 */
	public void setPiLanguage(String piLanguage)
	{
		this._piLanguage = piLanguage;
	}
}
