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

import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.nodes.JSParseNode;
import com.aptana.ide.editor.js.parsing.nodes.JSParseNodeTypes;
import com.aptana.ide.editor.scriptdoc.runtime.ScriptDocVM;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.parsing.nodes.ParseFragment;

/**
 * @author Kevin Lindsey
 */
public class JSEnvironmentHandler extends JSAbstractHandler
{
	private ScriptDocVM _vm;
	private int _fileIndex = 0;
	
	/**
	 * JSEnvironmentHandler
	 */
	public JSEnvironmentHandler()
	{
		this._vm = new ScriptDocVM();
	}
	
	/**
	 * getVM
	 * 
	 * @return
	 */
	public ScriptDocVM getVM()
	{
		return this._vm;
	}
	
	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onAddArgument(java.lang.Object[])
	 */
	protected Object onAddArgument(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onAddCaseClause(java.lang.Object[])
	 */
	protected Object onAddCaseClause(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onAddElement(java.lang.Object[])
	 */
	protected Object onAddElement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onAddElidedElement(java.lang.Object[])
	 */
	protected Object onAddElidedElement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onAddElision(java.lang.Object[])
	 */
	protected Object onAddElision(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onAddParameter(java.lang.Object[])
	 */
	protected Object onAddParameter(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onAddProperty(java.lang.Object[])
	 */
	protected Object onAddProperty(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onAddSourceElement(java.lang.Object[])
	 */
	protected Object onAddSourceElement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onAddStatement(java.lang.Object[])
	 */
	protected Object onAddStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onAddVarDeclaration(java.lang.Object[])
	 */
	protected Object onAddVarDeclaration(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onArguments(java.lang.Object[])
	 */
	protected Object onArguments(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onArrayLiteral(java.lang.Object[])
	 */
	protected Object onArrayLiteral(Object[] nodes)
	{
		Lexeme lbracket = (Lexeme) nodes[0];
		Lexeme rbracket = (Lexeme) nodes[2];
		Range range = new Range(lbracket.offset, rbracket.getEndingOffset());
		
		this._vm.addPushArray(this._fileIndex, range);
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onArrayLiteralTrailingComma(java.lang.Object[])
	 */
	protected Object onArrayLiteralTrailingComma(Object[] nodes)
	{
		Lexeme lbracket = (Lexeme) nodes[0];
		Lexeme rbracket = (Lexeme) nodes[3];
		Range range = new Range(lbracket.offset, rbracket.getEndingOffset());
		
		this._vm.addPushArray(this._fileIndex, range);
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onArrayLiteralTrailingElision(java.lang.Object[])
	 */
	protected Object onArrayLiteralTrailingElision(Object[] nodes)
	{
		Lexeme lbracket = (Lexeme) nodes[0];
		Lexeme rbracket = (Lexeme) nodes[4];
		Range range = new Range(lbracket.offset, rbracket.getEndingOffset());
		
		this._vm.addPushArray(this._fileIndex, range);
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onAssignmentExpression(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	protected Object onAssignmentExpression(Object[] nodes)
	{
		Lexeme operator = (Lexeme) nodes[1];
		
		switch (operator.typeIndex)
		{
			case JSTokenTypes.EQUAL:
				this._vm.addPut(this._fileIndex);
				break;
				
			case JSTokenTypes.STAR_EQUAL:
				break;
				
			case JSTokenTypes.FORWARD_SLASH_EQUAL:
				break;
				
			case JSTokenTypes.PERCENT_EQUAL:
				break;
				
			case JSTokenTypes.PLUS_EQUAL:
				break;
				
			case JSTokenTypes.MINUS_EQUAL:
				break;
				
			case JSTokenTypes.LESS_LESS_EQUAL:
				break;
				
			case JSTokenTypes.GREATER_GREATER_EQUAL:
				break;
				
			case JSTokenTypes.GREATER_GREATER_GREATER_EQUAL:
				break;
				
			case JSTokenTypes.AMPERSAND_EQUAL:
				break;

			case JSTokenTypes.CARET_EQUAL:
				break;

			case JSTokenTypes.PIPE_EQUAL:
				break;

			default:
				throw new IllegalArgumentException("Unknown operator: " + operator); //$NON-NLS-1$
		}
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onBinaryExpression(java.lang.Object[])
	 */
	protected Object onBinaryExpression(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onBlock(java.lang.Object[])
	 */
	protected Object onBlock(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onBreak(java.lang.Object[])
	 */
	protected Object onBreak(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onBreakLabel(java.lang.Object[])
	 */
	protected Object onBreakLabel(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onCallExpression(java.lang.Object[])
	 */
	protected Object onCallExpression(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onCaseClause(java.lang.Object[])
	 */
	protected Object onCaseClause(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onCasesAndDefaultBlock(java.lang.Object[])
	 */
	protected Object onCasesAndDefaultBlock(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onCasesBlock(java.lang.Object[])
	 */
	protected Object onCasesBlock(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onCasesDefaultCasesBlock(java.lang.Object[])
	 */
	protected Object onCasesDefaultCasesBlock(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onCatch(java.lang.Object[])
	 */
	protected Object onCatch(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onCommaExpression(java.lang.Object[])
	 */
	protected Object onCommaExpression(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onConditionalExpression(java.lang.Object[])
	 */
	protected Object onConditionalExpression(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onContinue(java.lang.Object[])
	 */
	protected Object onContinue(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onContinueLabel(java.lang.Object[])
	 */
	protected Object onContinueLabel(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onDefaultAndCasesBlock(java.lang.Object[])
	 */
	protected Object onDefaultAndCasesBlock(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onDefaultBlock(java.lang.Object[])
	 */
	protected Object onDefaultBlock(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onDefaultClause(java.lang.Object[])
	 */
	protected Object onDefaultClause(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onDoStatement(java.lang.Object[])
	 */
	protected Object onDoStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onElidedArray(java.lang.Object[])
	 */
	protected Object onElidedArray(Object[] nodes)
	{
		Lexeme lbracket = (Lexeme) nodes[0];
		Lexeme rbracket = (Lexeme) nodes[2];
		Range range = new Range(lbracket.offset, rbracket.getEndingOffset());
		
		this._vm.addPushArray(this._fileIndex, range);
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onEmptyArguments(java.lang.Object[])
	 */
	protected Object onEmptyArguments(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onEmptyArray(java.lang.Object[])
	 */
	protected Object onEmptyArray(Object[] nodes)
	{
		Lexeme lbracket = (Lexeme) nodes[0];
		Lexeme rbracket = (Lexeme) nodes[1];
		Range range = new Range(lbracket.offset, rbracket.getEndingOffset());
		
		this._vm.addPushArray(this._fileIndex, range);
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onEmptyBlock(java.lang.Object[])
	 */
	protected Object onEmptyBlock(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onEmptyCaseBlock(java.lang.Object[])
	 */
	protected Object onEmptyCaseBlock(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onEmptyCaseClause(java.lang.Object[])
	 */
	protected Object onEmptyCaseClause(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onEmptyDefaultClause(java.lang.Object[])
	 */
	protected Object onEmptyDefaultClause(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onEmptyFunctionBody(java.lang.Object[])
	 */
	protected Object onEmptyFunctionBody(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onEmptyObject(java.lang.Object[])
	 */
	protected Object onEmptyObject(Object[] nodes)
	{
		Lexeme lcurly = (Lexeme) nodes[0];
		Lexeme rcurly = (Lexeme) nodes[1];
		
		Range range = new Range(lcurly.offset, rcurly.getEndingOffset());
		
		this._vm.addPushObject(this._fileIndex, range);
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onEmptyParameterList(java.lang.Object[])
	 */
	protected Object onEmptyParameterList(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onEmptyStatement(java.lang.Object[])
	 */
	protected Object onEmptyStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onExpressionStatement(java.lang.Object[])
	 */
	protected Object onExpressionStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFalse(java.lang.Object[])
	 */
	protected Object onFalse(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		this._vm.addPushBoolean(this._fileIndex, keyword);
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFinally(java.lang.Object[])
	 */
	protected Object onFinally(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFirstArgument(java.lang.Object[])
	 */
	protected Object onFirstArgument(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFirstCaseClause(java.lang.Object[])
	 */
	protected Object onFirstCaseClause(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFirstElement(java.lang.Object[])
	 */
	protected Object onFirstElement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFirstElidedElement(java.lang.Object[])
	 */
	protected Object onFirstElidedElement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFirstElision(java.lang.Object[])
	 */
	protected Object onFirstElision(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFirstParameter(java.lang.Object[])
	 */
	protected Object onFirstParameter(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFirstProperty(java.lang.Object[])
	 */
	protected Object onFirstProperty(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFirstSourceElement(java.lang.Object[])
	 */
	protected Object onFirstSourceElement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFirstStatement(java.lang.Object[])
	 */
	protected Object onFirstStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFirstVarDeclaration(java.lang.Object[])
	 */
	protected Object onFirstVarDeclaration(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForAdvanceOnlyStatement(java.lang.Object[])
	 */
	protected Object onForAdvanceOnlyStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForBodyOnlyStatement(java.lang.Object[])
	 */
	protected Object onForBodyOnlyStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForConditionOnlyStatement(java.lang.Object[])
	 */
	protected Object onForConditionOnlyStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForInStatement(java.lang.Object[])
	 */
	protected Object onForInStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForInitializeOnlyStatement(java.lang.Object[])
	 */
	protected Object onForInitializeOnlyStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForNoAdvanceStatement(java.lang.Object[])
	 */
	protected Object onForNoAdvanceStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForNoConditionStatement(java.lang.Object[])
	 */
	protected Object onForNoConditionStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForNoInitializeStatement(java.lang.Object[])
	 */
	protected Object onForNoInitializeStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForStatement(java.lang.Object[])
	 */
	protected Object onForStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForVarInStatement(java.lang.Object[])
	 */
	protected Object onForVarInStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForVarInitializeOnlyStatement(java.lang.Object[])
	 */
	protected Object onForVarInitializeOnlyStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForVarNoAdvanceStatement(java.lang.Object[])
	 */
	protected Object onForVarNoAdvanceStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForVarNoConditionStatement(java.lang.Object[])
	 */
	protected Object onForVarNoConditionStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onForVarStatement(java.lang.Object[])
	 */
	protected Object onForVarStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFunctionBody(java.lang.Object[])
	 */
	protected Object onFunctionBody(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFunctionDeclaration(java.lang.Object[])
	 */
	protected Object onFunctionDeclaration(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onFunctionExpression(java.lang.Object[])
	 */
	protected Object onFunctionExpression(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onGetElement(java.lang.Object[])
	 */
	protected Object onGetElement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onGetProperty(java.lang.Object[])
	 */
	protected Object onGetProperty(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onGroupExpression(java.lang.Object[])
	 */
	protected Object onGroupExpression(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onIdentifier(java.lang.Object[])
	 */
	protected Object onIdentifier(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		this._vm.addGetGlobal();
		this._vm.addPush(keyword.getText());
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onIfElseStatement(java.lang.Object[])
	 */
	protected Object onIfElseStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onIfStatement(java.lang.Object[])
	 */
	protected Object onIfStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onLabelledStatement(java.lang.Object[])
	 */
	protected Object onLabelledStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onNewExpression(java.lang.Object[])
	 */
	protected Object onNewExpression(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onNewExpressionWithoutArguments(java.lang.Object[])
	 */
	protected Object onNewExpressionWithoutArguments(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onNull(java.lang.Object[])
	 */
	protected Object onNull(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		this._vm.addPushNull(this._fileIndex, keyword);
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onNumber(java.lang.Object[])
	 */
	protected Object onNumber(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		this._vm.addPushNumber(this._fileIndex, keyword);
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onObjectLiteral(java.lang.Object[])
	 */
	protected Object onObjectLiteral(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onParameterList(java.lang.Object[])
	 */
	protected Object onParameterList(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onPostfixExpression(java.lang.Object[])
	 */
	protected Object onPostfixExpression(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onRegex(java.lang.Object[])
	 */
	protected Object onRegex(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		this._vm.addPushRegExp(this._fileIndex, keyword);
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onReturn(java.lang.Object[])
	 */
	protected Object onReturn(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onReturnValue(java.lang.Object[])
	 */
	protected Object onReturnValue(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onString(java.lang.Object[])
	 */
	protected Object onString(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		this._vm.addPushString(this._fileIndex, keyword);
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onSwitchStatement(java.lang.Object[])
	 */
	protected Object onSwitchStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onThis(java.lang.Object[])
	 */
	protected Object onThis(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onThrowStatement(java.lang.Object[])
	 */
	protected Object onThrowStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onTrue(java.lang.Object[])
	 */
	protected Object onTrue(Object[] nodes)
	{
		Lexeme keyword = (Lexeme) nodes[0];
		
		this._vm.addPushBoolean(this._fileIndex, keyword);
		
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onTryCatchFinallyStatement(java.lang.Object[])
	 */
	protected Object onTryCatchFinallyStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onTryCatchStatement(java.lang.Object[])
	 */
	protected Object onTryCatchStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onTryFinallyStatement(java.lang.Object[])
	 */
	protected Object onTryFinallyStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onUnaryExpression(java.lang.Object[])
	 */
	protected Object onUnaryExpression(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onVarDeclaration(java.lang.Object[])
	 */
	protected Object onVarDeclaration(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onVarDeclarationAssignment(java.lang.Object[])
	 */
	protected Object onVarDeclarationAssignment(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onVarStatement(java.lang.Object[])
	 */
	protected Object onVarStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onWhileStatement(java.lang.Object[])
	 */
	protected Object onWhileStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.JSAbstractHandler#onWithStatement(java.lang.Object[])
	 */
	protected Object onWithStatement(Object[] nodes)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
