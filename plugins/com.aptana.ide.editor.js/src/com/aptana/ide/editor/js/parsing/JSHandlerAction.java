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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin Lindsey
 */
public enum JSHandlerAction
{
	ADD_ARGUMENT("AddArgument"), //$NON-NLS-1$
	ADD_CASE_CLAUSE("AddCaseClause"), //$NON-NLS-1$
	ADD_ELEMENT("AddElement"), //$NON-NLS-1$
	ADD_ELIDED_ELEMENT("AddElidedElement"), //$NON-NLS-1$
	ADD_ELISION("AddElision"), //$NON-NLS-1$
	ADD_PARAMETER("AddParameter"), //$NON-NLS-1$
	ADD_PROPERTY("AddProperty"), //$NON-NLS-1$
	ADD_SOURCE_ELEMENT("AddSourceElement"), //$NON-NLS-1$
	ADD_STATEMENT("AddStatement"), //$NON-NLS-1$
	ADD_VAR_DECLARATION("AddVarDeclaration"), //$NON-NLS-1$
	ARGUMENTS("Arguments"), //$NON-NLS-1$
	ARRAY_LITERAL("ArrayLiteral"), //$NON-NLS-1$
	ARRAY_LITERAL_TRAILING_COMMA("ArrayLiteralTrailingComma"), //$NON-NLS-1$
	ARRAY_LITERAL_TRAILING_ELISION("ArrayLiteralTrailingElision"), //$NON-NLS-1$
	ASSIGNMENT_EXPRESSION("AssignmentExpression"), //$NON-NLS-1$
	BINARY_EXPRESSION("BinaryExpression"), //$NON-NLS-1$
	BLOCK("Block"), //$NON-NLS-1$
	BREAK("Break"), //$NON-NLS-1$
	BREAK_LABEL("BreakLabel"), //$NON-NLS-1$
	CALL_EXPRESSION("CallExpression"), //$NON-NLS-1$
	CASE_CLAUSE("CaseClause"), //$NON-NLS-1$
	CASES_BLOCK("CasesBlock"), //$NON-NLS-1$
	CASES_AND_DEFAULT_BLOCK("CasesAndDefaultBlock"), //$NON-NLS-1$
	CASES_DEFAULT_CASES_BLOCK("CasesDefaultCasesBlock"), //$NON-NLS-1$
	CATCH("Catch"), //$NON-NLS-1$
	COMMA_EXPRESSION("CommaExpression"), //$NON-NLS-1$
	CONDITIONAL_EXPRESSION("ConditionalExpression"), //$NON-NLS-1$
	CONTINUE("Continue"), //$NON-NLS-1$
	CONTINUE_LABEL("ContinueLabel"), //$NON-NLS-1$
	DEFAULT_BLOCK("DefaultBlock"), //$NON-NLS-1$
	DEFAULT_AND_CASES_BLOCK("DefaultAndCasesBlock"), //$NON-NLS-1$
	DEFAULT_CLAUSE("DefaultClause"), //$NON-NLS-1$
	DO_STATEMENT("DoStatement"), //$NON-NLS-1$
	ELIDED_ARRAY("ElidedArray"), //$NON-NLS-1$
	EMPTY_ARGUMENTS("EmptyArguments"), //$NON-NLS-1$
	EMPTY_ARRAY("EmptyArray"), //$NON-NLS-1$
	EMPTY_BLOCK("EmptyBlock"), //$NON-NLS-1$
	EMPTY_CASE_BLOCK("EmptyCaseBlock"), //$NON-NLS-1$
	EMPTY_CASE_CLAUSE("EmptyCaseClause"), //$NON-NLS-1$
	EMPTY_DEFAULT_CLAUSE("EmptyDefaultClause"), //$NON-NLS-1$
	EMPTY_FUNCTION_BODY("EmptyFunctionBody"), //$NON-NLS-1$
	EMPTY_OBJECT("EmptyObject"), //$NON-NLS-1$
	EMPTY_PARAMTER_LIST("EmptyParameterList"), //$NON-NLS-1$
	EMPTY_STATEMENT("EmptyStatement"), //$NON-NLS-1$
	EXPRESSION_STATEMENT("ExpressionStatement"), //$NON-NLS-1$
	FALSE("False"), //$NON-NLS-1$
	FINALLY("Finally"), //$NON-NLS-1$
	FIRST_ARGUMENT("FirstArgument"), //$NON-NLS-1$
	FIRST_CASE_CLAUSE("FirstCaseClause"), //$NON-NLS-1$
	FIRST_ELEMENT("FirstElement"), //$NON-NLS-1$
	FIRST_ELIDED_ELEMENT("FirstElidedElement"), //$NON-NLS-1$
	FIRST_ELISION("FirstElision"), //$NON-NLS-1$
	FIRST_PARAMETER("FirstParameter"), //$NON-NLS-1$
	FIRST_PROPERTY("FirstProperty"), //$NON-NLS-1$
	FIRST_SOURCE_ELEMENT("FirstSourceElement"), //$NON-NLS-1$
	FIRST_STATEMENT("FirstStatement"), //$NON-NLS-1$
	FIRST_VAR_DECLARATION("FirstVarDeclaration"), //$NON-NLS-1$
	FOR_ADVANCE_ONLY_STATEMENT("ForAdvanceOnlyStatement"), //$NON-NLS-1$
	FOR_BODY_ONLY_STATEMENT("ForBodyOnlyStatement"), //$NON-NLS-1$
	FOR_CONDITION_ONLY_STATEMENT("ForConditionOnlyStatement"), //$NON-NLS-1$
	FOR_IN_STATEMENT("ForInStatement"), //$NON-NLS-1$
	FOR_INITIALIZE_ONLY_STATEMENT("ForInitializeOnlyStatement"), //$NON-NLS-1$
	FOR_NO_ADVANCE_STATEMENT("ForNoAdvanceStatement"), //$NON-NLS-1$
	FOR_NO_CONDITION_STATEMENT("ForNoConditionStatement"), //$NON-NLS-1$
	FOR_NO_INITIALIZE_STATEMENT("ForNoInitializeStatement"), //$NON-NLS-1$
	FOR_STATEMENT("ForStatement"), //$NON-NLS-1$
	FOR_VAR_IN_STATEMENT("ForVarInStatement"), //$NON-NLS-1$
	FOR_VAR_INITIALIZE_ONLY_STATEMENT("ForVarInitializeOnlyStatement"), //$NON-NLS-1$
	FOR_VAR_NO_ADVANCE_STATEMENT("ForVarNoAdvanceStatement"), //$NON-NLS-1$
	FOR_VAR_NO_CONDITION_STATEMENT("ForVarNoConditionStatement"), //$NON-NLS-1$
	FOR_VAR_STATEMENT("ForVarStatement"), //$NON-NLS-1$
	FUNCTION_BODY("FunctionBody"), //$NON-NLS-1$
	FUNCTION_DECLARATION("FunctionDeclaration"), //$NON-NLS-1$
	FUNCTION_EXPRESSION("FunctionExpression"), //$NON-NLS-1$
	GET_ELEMENT("GetElement"), //$NON-NLS-1$
	GET_PROPERTY("GetProperty"), //$NON-NLS-1$
	GROUP_EXPRESSION("GroupExpression"), //$NON-NLS-1$
	IDENTIFIER("Identifier"), //$NON-NLS-1$
	IF_ELSE_STATEMENT("IfElseStatement"), //$NON-NLS-1$
	IF_STATEMENT("IfStatement"), //$NON-NLS-1$
	LABELLED_STATEMENT("LabelledStatement"), //$NON-NLS-1$
	NEW_EXPRESSION("NewExpression"), //$NON-NLS-1$
	NEW_EXPRESSION_WITHOUT_ARGUMENTS("NewExpressionWithoutArguments"), //$NON-NLS-1$
	NULL("Null"), //$NON-NLS-1$
	NUMBER("Number"), //$NON-NLS-1$
	OBJECT_LITERAL("ObjectLiteral"), //$NON-NLS-1$
	PARAMETER_LIST("ParameterList"), //$NON-NLS-1$
	POSTFIX_EXPRESSION("PostfixExpression"), //$NON-NLS-1$
	REGEX("Regex"), //$NON-NLS-1$
	RETURN("Return"), //$NON-NLS-1$
	RETURN_VALUE("ReturnValue"), //$NON-NLS-1$
	STRING("String"), //$NON-NLS-1$
	SWITCH_STATEMENT("SwitchStatement"), //$NON-NLS-1$
	THIS("This"), //$NON-NLS-1$
	THROW_STATEMENT("ThrowStatement"), //$NON-NLS-1$
	TRUE("True"), //$NON-NLS-1$
	TRY_CATCH_STATEMENT("TryCatchStatement"), //$NON-NLS-1$
	TRY_FINALLY_STATEMENT("TryFinallyStatement"), //$NON-NLS-1$
	TRY_CATCH_FINALLY_STATEMENT("TryCatchFinallyStatement"), //$NON-NLS-1$
	UNARY_EXPRESSION("UnaryExpression"), //$NON-NLS-1$
	VAR_DECLARATION("VarDeclaration"), //$NON-NLS-1$
	VAR_DECLARATION_ASSIGNMENT("VarDeclarationAssignment"), //$NON-NLS-1$
	VAR_STATEMENT("VarStatement"), //$NON-NLS-1$
	WHILE_STATEMENT("WhileStatement"), //$NON-NLS-1$
	WITH_STATEMENT("WithStatement"); //$NON-NLS-1$
	
	private static final Map<String,JSHandlerAction> NAME_MAP;
	private String _name;
	
	/**
	 * static constructor
	 */
	static
	{
		NAME_MAP = new HashMap<String,JSHandlerAction>();
		
		for (JSHandlerAction action : EnumSet.allOf(JSHandlerAction.class))
		{
			NAME_MAP.put(action.getName(), action);
		}
	}
	
	/**
	 * JSHandleActions
	 * 
	 * @param name
	 */
	private JSHandlerAction(String name)
	{
		this._name = name;
	}
	
	/**
	 * get
	 * 
	 * @param name
	 * @return
	 */
	public static final JSHandlerAction get(String name)
	{
		return NAME_MAP.get(name);
	}
	
	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}
}
