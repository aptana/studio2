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

import com.aptana.ide.editor.js.parsing.nodes.JSParseNode;
import com.aptana.ide.editors.unified.parsing.UnifiedReductionHandler;
import com.aptana.ide.parsing.bnf.IReductionContext;

/**
 * @author Kevin Lindsey
 */
public abstract class JSAbstractHandler extends UnifiedReductionHandler<JSParseNode>
{
	/**
	 * JSASTHandler
	 */
	public JSAbstractHandler()
	{
		super();
	}
	
	/**
	 * onAddArgument
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onAddArgument(Object[] nodes);

	/**
	 * onAddCaseClause
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onAddCaseClause(Object[] nodes);

	/**
	 * onAddElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onAddElement(Object[] nodes);

	/**
	 * onAddElidedElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onAddElidedElement(Object[] nodes);

	/**
	 * onAddElision
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onAddElision(Object[] nodes);

	/**
	 * onAddParameter
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onAddParameter(Object[] nodes);

	/**
	 * onAddProperty
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onAddProperty(Object[] nodes);

	/**
	 * onAddSourceElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onAddSourceElement(Object[] nodes);

	/**
	 * onAddStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onAddStatement(Object[] nodes);

	/**
	 * onAddVarDeclaration
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onAddVarDeclaration(Object[] nodes);

	/**
	 * onArguments
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onArguments(Object[] nodes);

	/**
	 * onArrayLiteral
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onArrayLiteral(Object[] nodes);

	/**
	 * onArrayLiteralTrailingComma
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onArrayLiteralTrailingComma(Object[] nodes);

	/**
	 * onArrayLiteralTrailingElision
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onArrayLiteralTrailingElision(Object[] nodes);

	/**
	 * onAssignmentExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onAssignmentExpression(Object[] nodes);

	/**
	 * onBinaryExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onBinaryExpression(Object[] nodes);

	/**
	 * onBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onBlock(Object[] nodes);

	/**
	 * onBreak
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onBreak(Object[] nodes);

	/**
	 * onBreakLabel
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onBreakLabel(Object[] nodes);

	/**
	 * onCallExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onCallExpression(Object[] nodes);

	/**
	 * onCaseClause
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onCaseClause(Object[] nodes);

	/**
	 * onCasesAndDefaultBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onCasesAndDefaultBlock(Object[] nodes);

	/**
	 * onCasesBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onCasesBlock(Object[] nodes);

	/**
	 * onCasesDefaultCasesBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onCasesDefaultCasesBlock(Object[] nodes);

	/**
	 * onCatch
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onCatch(Object[] nodes);

	/**
	 * onCommaExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onCommaExpression(Object[] nodes);

	/**
	 * onConditionalExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onConditionalExpression(Object[] nodes);

	/**
	 * onContinue
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onContinue(Object[] nodes);

	/**
	 * onContinueLabel
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onContinueLabel(Object[] nodes);

	/**
	 * onDefaultAndCasesBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onDefaultAndCasesBlock(Object[] nodes);

	/**
	 * onDefaultBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onDefaultBlock(Object[] nodes);

	/**
	 * onDefaultClause
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onDefaultClause(Object[] nodes);

	/**
	 * onDoStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onDoStatement(Object[] nodes);

	/**
	 * onElidedArray
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onElidedArray(Object[] nodes);

	/**
	 * onEmptyArguments
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onEmptyArguments(Object[] nodes);

	/**
	 * onEmptyArray
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onEmptyArray(Object[] nodes);

	/**
	 * onEmptyBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onEmptyBlock(Object[] nodes);

	/**
	 * onEmptyCaseBlock
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onEmptyCaseBlock(Object[] nodes);

	/**
	 * onEmptyCaseClause
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onEmptyCaseClause(Object[] nodes);

	/**
	 * onEmptyDefaultClause
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onEmptyDefaultClause(Object[] nodes);

	/**
	 * onEmptyFunctionBody
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onEmptyFunctionBody(Object[] nodes);

	/**
	 * onEmptyObject
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onEmptyObject(Object[] nodes);
	
	/**
	 * onEmptyParameterList
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onEmptyParameterList(Object[] nodes);

	/**
	 * onEmptyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onEmptyStatement(Object[] nodes);

	/**
	 * onExpressionStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onExpressionStatement(Object[] nodes);

	/**
	 * onFalse
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFalse(Object[] nodes);

	/**
	 * onFinally
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFinally(Object[] nodes);

	/**
	 * onFirstArgument
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFirstArgument(Object[] nodes);

	/**
	 * onFirstCaseClause
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFirstCaseClause(Object[] nodes);

	/**
	 * onFirstElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFirstElement(Object[] nodes);

	/**
	 * onFirstElidedElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFirstElidedElement(Object[] nodes);

	/**
	 * onFirstElision
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFirstElision(Object[] nodes);

	/**
	 * onFirstParameter
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFirstParameter(Object[] nodes);

	/**
	 * onFirstProperty
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFirstProperty(Object[] nodes);

	/**
	 * onFirstSourceElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFirstSourceElement(Object[] nodes);

	/**
	 * onFirstStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFirstStatement(Object[] nodes);

	/**
	 * onFirstVarDeclaration
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFirstVarDeclaration(Object[] nodes);

	/**
	 * onForAdvanceOnlyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForAdvanceOnlyStatement(Object[] nodes);

	/**
	 * onForBodyOnlyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForBodyOnlyStatement(Object[] nodes);

	/**
	 * onForConditionOnlyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForConditionOnlyStatement(Object[] nodes);

	/**
	 * onForInitializeOnlyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForInitializeOnlyStatement(Object[] nodes);

	/**
	 * onForInStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForInStatement(Object[] nodes);

	/**
	 * onForNoAdvanceStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForNoAdvanceStatement(Object[] nodes);

	/**
	 * onForNoConditionStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForNoConditionStatement(Object[] nodes);

	/**
	 * onForNoInitializeStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForNoInitializeStatement(Object[] nodes);

	/**
	 * onForStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForStatement(Object[] nodes);

	/**
	 * onForVarInitializeOnlyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForVarInitializeOnlyStatement(Object[] nodes);

	/**
	 * onForVarInStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForVarInStatement(Object[] nodes);

	/**
	 * onForVarNoAdvanceStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForVarNoAdvanceStatement(Object[] nodes);

	/**
	 * onForVarNoConditionStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForVarNoConditionStatement(Object[] nodes);

	/**
	 * onForVarStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onForVarStatement(Object[] nodes);

	/**
	 * onFunctionBody
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFunctionBody(Object[] nodes);

	/**
	 * onFunctionDeclaration
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFunctionDeclaration(Object[] nodes);

	/**
	 * onFunctionExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onFunctionExpression(Object[] nodes);

	/**
	 * onGetElement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onGetElement(Object[] nodes);

	/**
	 * onGetProperty
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onGetProperty(Object[] nodes);

	/**
	 * onGroupExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onGroupExpression(Object[] nodes);

	/**
	 * onIdentifier
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onIdentifier(Object[] nodes);

	/**
	 * onIfElseStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onIfElseStatement(Object[] nodes);

	/**
	 * onIfStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onIfStatement(Object[] nodes);

	/**
	 * onLabelledStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onLabelledStatement(Object[] nodes);

	/**
	 * onNewExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onNewExpression(Object[] nodes);

	/**
	 * onNewExpressionWithoutArguments
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onNewExpressionWithoutArguments(Object[] nodes);

	/**
	 * onNull
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onNull(Object[] nodes);

	/**
	 * onNumber
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onNumber(Object[] nodes);

	/**
	 * onObjectLiteral
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onObjectLiteral(Object[] nodes);

	/**
	 * onParameterList
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onParameterList(Object[] nodes);

	/**
	 * onPostfixExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onPostfixExpression(Object[] nodes);

	/**
	 * onRegex
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onRegex(Object[] nodes);

	/**
	 * onReturn
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onReturn(Object[] nodes);

	/**
	 * onReturnValue
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onReturnValue(Object[] nodes);

	/**
	 * onString
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onString(Object[] nodes);

	/**
	 * onSwitchStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onSwitchStatement(Object[] nodes);

	/**
	 * onThis
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onThis(Object[] nodes);

	/**
	 * onThrowStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onThrowStatement(Object[] nodes);

	/**
	 * onTrue
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onTrue(Object[] nodes);

	/**
	 * onTryCatchFinallyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onTryCatchFinallyStatement(Object[] nodes);

	/**
	 * onTryCatchStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onTryCatchStatement(Object[] nodes);

	/**
	 * onTryFinallyStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onTryFinallyStatement(Object[] nodes);

	/**
	 * onUnaryExpression
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onUnaryExpression(Object[] nodes);

	/**
	 * onVarDeclaration
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onVarDeclaration(Object[] nodes);

	/**
	 * onVarDeclarationAssignment
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onVarDeclarationAssignment(Object[] nodes);

	/**
	 * onVarStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onVarStatement(Object[] nodes);

	/**
	 * onWhileStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onWhileStatement(Object[] nodes);

	/**
	 * onWithStatement
	 * 
	 * @param nodes
	 * @return
	 */
	protected abstract Object onWithStatement(Object[] nodes);

	/**
	 * @see com.aptana.ide.editors.unified.parsing.UnifiedReductionHandler#reduce(com.aptana.ide.parsing.bnf.IReductionContext)
	 */
	public void reduce(IReductionContext context)
	{
		String actionName = context.getAction();
		Object result = null;
		
		// collect the matching rule's items from the stack
		Object[] nodes = this.pop(context.getNodeCount());
		
		if (actionName != null && actionName.length() > 0)
		{
			JSHandlerAction action = JSHandlerAction.get(actionName);
			
			if (action == null)
			{
				throw new IllegalArgumentException("Unknown action: " + actionName); //$NON-NLS-1$
			}
			
			switch (action)
			{
				case ADD_ARGUMENT:
					result = this.onAddArgument(nodes);
					break;
					
				case ADD_CASE_CLAUSE:
					result = this.onAddCaseClause(nodes);
					break;
					
				case ADD_ELEMENT:
					result = this.onAddElement(nodes);
					break;
					
				case ADD_ELIDED_ELEMENT:
					result = this.onAddElidedElement(nodes);
					break;
					
				case ADD_ELISION:
					result = this.onAddElision(nodes);
					break;
					
				case ADD_PARAMETER:
					result = this.onAddParameter(nodes);
					break;
					
				case ADD_PROPERTY:
					result = this.onAddProperty(nodes);
					break;
					
				case ADD_SOURCE_ELEMENT:
					result = this.onAddSourceElement(nodes);
					break;
					
				case ADD_STATEMENT:
					result = this.onAddStatement(nodes);
					break;
					
				case ADD_VAR_DECLARATION:
					result = this.onAddVarDeclaration(nodes);
					break;
					
				case ARGUMENTS:
					result = this.onArguments(nodes);
					break;
					
				case ARRAY_LITERAL:
					result = this.onArrayLiteral(nodes);
					break;
					
				case ARRAY_LITERAL_TRAILING_COMMA:
					result = this.onArrayLiteralTrailingComma(nodes);
					break;
					
				case ARRAY_LITERAL_TRAILING_ELISION:
					result = this.onArrayLiteralTrailingElision(nodes);
					break;
					
				case ASSIGNMENT_EXPRESSION:
					result = this.onAssignmentExpression(nodes);
					break;
					
				case BINARY_EXPRESSION:
					result = this.onBinaryExpression(nodes);
					break;
					
				case BLOCK:
					result = this.onBlock(nodes);
					break;
					
				case BREAK:
					result = this.onBreak(nodes);
					break;
					
				case BREAK_LABEL:
					result = this.onBreakLabel(nodes);
					break;
				
				case CALL_EXPRESSION:
					result = this.onCallExpression(nodes);
					break;
					
				case CASE_CLAUSE:
					result = this.onCaseClause(nodes);
					break;
					
				case CASES_AND_DEFAULT_BLOCK:
					result = this.onCasesAndDefaultBlock(nodes);
					break;
					
				case CASES_BLOCK:
					result = this.onCasesBlock(nodes);
					break;
					
				case CASES_DEFAULT_CASES_BLOCK:
					result = this.onCasesDefaultCasesBlock(nodes);
					break;
					
				case CATCH:
					result = this.onCatch(nodes);
					break;
					
				case COMMA_EXPRESSION:
					result = this.onCommaExpression(nodes);
					break;
					
				case CONDITIONAL_EXPRESSION:
					result = this.onConditionalExpression(nodes);
					break;
					
				case CONTINUE:
					result = this.onContinue(nodes);
					break;
					
				case CONTINUE_LABEL:
					result = this.onContinueLabel(nodes);
					break;
					
				case DEFAULT_AND_CASES_BLOCK:
					result = this.onDefaultAndCasesBlock(nodes);
					break;
					
				case DEFAULT_BLOCK:
					result = this.onDefaultBlock(nodes);
					break;
					
				case DEFAULT_CLAUSE:
					result = this.onDefaultClause(nodes);
					break;
					
				case DO_STATEMENT:
					result = this.onDoStatement(nodes);
					break;
					
				case ELIDED_ARRAY:
					result = this.onElidedArray(nodes);
					break;
					
				case EMPTY_ARGUMENTS:
					result = this.onEmptyArguments(nodes);
					break;
					
				case EMPTY_ARRAY:
					result = this.onEmptyArray(nodes);
					break;
					
				case EMPTY_BLOCK:
					result = this.onEmptyBlock(nodes);
					break;
				
				case EMPTY_CASE_BLOCK:
					result = this.onEmptyCaseBlock(nodes);
					break;
					
				case EMPTY_CASE_CLAUSE:
					result = this.onEmptyCaseClause(nodes);
					break;
					
				case EMPTY_DEFAULT_CLAUSE:
					result = this.onEmptyDefaultClause(nodes);
					break;
					
				case EMPTY_FUNCTION_BODY:
					result = this.onEmptyFunctionBody(nodes);
					break;
					
				case EMPTY_OBJECT:
					result = this.onEmptyObject(nodes);
					break;
					
				case EMPTY_PARAMTER_LIST:
					result = this.onEmptyParameterList(nodes);
					break;
					
				case EMPTY_STATEMENT:
					result = this.onEmptyStatement(nodes);
					break;
					
				case EXPRESSION_STATEMENT:
					result = this.onExpressionStatement(nodes);
					break;
					
				case FALSE:
					result = this.onFalse(nodes);
					break;
				
				case FINALLY:
					result = this.onFinally(nodes);
					break;
					
				case FIRST_ARGUMENT:
					result = this.onFirstArgument(nodes);
					break;
					
				case FIRST_CASE_CLAUSE:
					result = this.onFirstCaseClause(nodes);
					break;
					
				case FIRST_ELEMENT:
					result = this.onFirstElement(nodes);
					break;
					
				case FIRST_ELIDED_ELEMENT:
					result = this.onFirstElidedElement(nodes);
					break;
					
				case FIRST_ELISION:
					result = this.onFirstElision(nodes);
					break;
					
				case FIRST_PARAMETER:
					result = this.onFirstParameter(nodes);
					break;
					
				case FIRST_PROPERTY:
					result = this.onFirstProperty(nodes);
					break;
					
				case FIRST_SOURCE_ELEMENT:
					result = this.onFirstSourceElement(nodes);
					break;
					
				case FIRST_STATEMENT:
					result = this.onFirstStatement(nodes);
					break;
					
				case FIRST_VAR_DECLARATION:
					result = this.onFirstVarDeclaration(nodes);
					break;
					
				case FOR_ADVANCE_ONLY_STATEMENT:
					result = this.onForAdvanceOnlyStatement(nodes);
					break;
					
				case FOR_BODY_ONLY_STATEMENT:
					result = this.onForBodyOnlyStatement(nodes);
					break;
					
				case FOR_CONDITION_ONLY_STATEMENT:
					result = this.onForConditionOnlyStatement(nodes);
					break;
					
				case FOR_IN_STATEMENT:
					result = this.onForInStatement(nodes);
					break;
					
				case FOR_INITIALIZE_ONLY_STATEMENT:
					result = this.onForInitializeOnlyStatement(nodes);
					break;
					
				case FOR_NO_ADVANCE_STATEMENT:
					result = this.onForNoAdvanceStatement(nodes);
					break;
					
				case FOR_NO_CONDITION_STATEMENT:
					result = this.onForNoConditionStatement(nodes);
					break;
					
				case FOR_NO_INITIALIZE_STATEMENT:
					result = this.onForNoInitializeStatement(nodes);
					break;
					
				case FOR_STATEMENT:
					result = this.onForStatement(nodes);
					break;
					
				case FOR_VAR_IN_STATEMENT:
					result = this.onForVarInStatement(nodes);
					break;
					
				case FOR_VAR_INITIALIZE_ONLY_STATEMENT:
					result = this.onForVarInitializeOnlyStatement(nodes);
					break;
					
				case FOR_VAR_NO_ADVANCE_STATEMENT:
					result = this.onForVarNoAdvanceStatement(nodes);
					break;
					
				case FOR_VAR_NO_CONDITION_STATEMENT:
					result = this.onForVarNoConditionStatement(nodes);
					break;
					
				case FOR_VAR_STATEMENT:
					result = this.onForVarStatement(nodes);
					break;
					
				case FUNCTION_BODY:
					result = this.onFunctionBody(nodes);
					break;
					
				case FUNCTION_DECLARATION:
					result = this.onFunctionDeclaration(nodes);
					break;
					
				case FUNCTION_EXPRESSION:
					result = this.onFunctionExpression(nodes);
					break;
					
				case GET_ELEMENT:
					result = this.onGetElement(nodes);
					break;
					
				case GET_PROPERTY:
					result = this.onGetProperty(nodes);
					break;
					
				case GROUP_EXPRESSION:
					result = this.onGroupExpression(nodes);
					break;
					
				case IDENTIFIER:
					result = this.onIdentifier(nodes);
					break;
					
				case IF_ELSE_STATEMENT:
					result = this.onIfElseStatement(nodes);
					break;
				
				case IF_STATEMENT:
					result = this.onIfStatement(nodes);
					break;
					
				case LABELLED_STATEMENT:
					result = this.onLabelledStatement(nodes);
					break;
					
				case NEW_EXPRESSION:
					result = this.onNewExpression(nodes);
					break;
					
				case NEW_EXPRESSION_WITHOUT_ARGUMENTS:
					result = this.onNewExpressionWithoutArguments(nodes);
					break;
					
				case NULL:
					result = this.onNull(nodes);
					break;
					
				case NUMBER:
					result = this.onNumber(nodes);
					break;
					
				case OBJECT_LITERAL:
					result = this.onObjectLiteral(nodes);
					break;
					
				case PARAMETER_LIST:
					result = this.onParameterList(nodes);
					break;
					
				case POSTFIX_EXPRESSION:
					result = this.onPostfixExpression(nodes);
					break;
					
				case REGEX:
					result = this.onRegex(nodes);
					break;
					
				case RETURN:
					result = this.onReturn(nodes);
					break;
					
				case RETURN_VALUE:
					result = this.onReturnValue(nodes);
					break;
					
				case STRING:
					result = this.onString(nodes);
					break;
					
				case SWITCH_STATEMENT:
					result = this.onSwitchStatement(nodes);
					break;
					
				case THIS:
					result = this.onThis(nodes);
					break;
					
				case THROW_STATEMENT:
					result = this.onThrowStatement(nodes);
					break;
					
				case TRUE:
					result = this.onTrue(nodes);
					break;
					
				case TRY_CATCH_STATEMENT:
					result = this.onTryCatchStatement(nodes);
					break;
					
				case TRY_FINALLY_STATEMENT:
					result = this.onTryFinallyStatement(nodes);
					break;
					
				case TRY_CATCH_FINALLY_STATEMENT:
					result = this.onTryCatchFinallyStatement(nodes);
					break;
					
				case UNARY_EXPRESSION:
					result = this.onUnaryExpression(nodes);
					break;
					
				case VAR_DECLARATION:
					result = this.onVarDeclaration(nodes);
					break;
					
				case VAR_DECLARATION_ASSIGNMENT:
					result = this.onVarDeclarationAssignment(nodes);
					break;
					
				case VAR_STATEMENT:
					result = this.onVarStatement(nodes);
					break;
					
				case WHILE_STATEMENT:
					result = this.onWhileStatement(nodes);
					break;
					
				case WITH_STATEMENT:
					result = this.onWithStatement(nodes);
					break;
				
				default:
					throw new IllegalArgumentException("Missing handler for action: " + actionName); //$NON-NLS-1$
			}
		}
		else
		{
			// We should always have nodes, but let's be safe just in case
			if (nodes.length > 0)
			{
				result = nodes[0];
			}
		}
		
		// push the results onto our stack
		this.push(result);
	}
}
