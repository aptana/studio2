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
package com.aptana.ide.parsing.bnf;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.ast.nodes.ASTParseNode;
import com.aptana.ide.parsing.ast.nodes.ASTRootNode;
import com.aptana.ide.parsing.ast.nodes.AppendNode;
import com.aptana.ide.parsing.ast.nodes.AssignNode;
import com.aptana.ide.parsing.ast.nodes.CaseNode;
import com.aptana.ide.parsing.ast.nodes.DotNode;
import com.aptana.ide.parsing.ast.nodes.FalseNode;
import com.aptana.ide.parsing.ast.nodes.HandlerNode;
import com.aptana.ide.parsing.ast.nodes.IdentifierNode;
import com.aptana.ide.parsing.ast.nodes.ImportNode;
import com.aptana.ide.parsing.ast.nodes.InstantiationNode;
import com.aptana.ide.parsing.ast.nodes.InvocationNode;
import com.aptana.ide.parsing.ast.nodes.ListNode;
import com.aptana.ide.parsing.ast.nodes.NullNode;
import com.aptana.ide.parsing.ast.nodes.ParameterNode;
import com.aptana.ide.parsing.ast.nodes.StringNode;
import com.aptana.ide.parsing.ast.nodes.SwitchNode;
import com.aptana.ide.parsing.ast.nodes.TrueNode;

/**
 * @author Kevin Lindsey
 */
public class ASTHandler extends AbstractHandler
{
	private ASTRootNode _root = new ASTRootNode();
	
	/**
	 * getRootNode
	 * 
	 * @return
	 */
	public ASTRootNode getRootNode()
	{
		return this._root;
	}
	
	/**
	 * onAddArgument
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddArgument(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ListNode arguments = (ListNode) nodes[0];
		ASTParseNode argument = (ASTParseNode) nodes[2];
		
		arguments.appendChild(argument);
		
		return arguments;
	}
	
	/**
	 * onAddBodyStatement
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddBodyStatement(IReductionContext context)
	{
		ListNode statements = (ListNode) context.getNode(0);
		ASTParseNode statement = (ASTParseNode) context.getNode(1);
		
		statements.appendChild(statement);
		
		return statements;
	}
	
	/**
	 * onAddCaseStatement
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddCaseStatement(IReductionContext context)
	{
		ListNode statements = (ListNode) context.getNode(0);
		ASTParseNode statement = (ASTParseNode) context.getNode(1);
		
		statements.appendChild(statement);
		
		return statements;
	}
	
	/**
	 * onAddName
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddName(IReductionContext context)
	{
		ASTParseNode lhs = (ASTParseNode) context.getNode(0);
		Lexeme identifier = (Lexeme) context.getNode(2);
		
		DotNode result = new DotNode();
		result.appendChild(lhs);
		result.appendChild(new IdentifierNode(identifier));
		
		return result;
	}
	
	/**
	 * onAddParameter
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddParameter(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ASTParseNode parameters = (ASTParseNode) nodes[0];
		ASTParseNode parameter = (ASTParseNode) nodes[2];
		
		parameters.appendChild(parameter);
		
		return parameters;
	}
	
	/**
	 * onAddStatement
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddStatement(IReductionContext context)
	{
		ASTParseNode statements = (ASTParseNode) context.getNode(0);
		ASTParseNode statement = (ASTParseNode) context.getNode(1);
		
		statements.appendChild(statement);
		
		return statements;
	}
	
	/**
	 * onAppendNode
	 * 
	 * @param context
	 * @return
	 */
	public Object onAppendNode(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme lhs = (Lexeme) nodes[0];
		ASTParseNode rhs = (ASTParseNode) nodes[2];
		Lexeme semicolon = (Lexeme) nodes[3];
		
		AppendNode result = new AppendNode(lhs, semicolon);
		result.appendChild(new IdentifierNode(lhs));
		result.appendChild(rhs);
		
		return result;
	}
	
	/**
	 * onAppendReference
	 * 
	 * @param context
	 * @return
	 */
	public Object onAppendReference(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme lhs = (Lexeme) nodes[0];
		Lexeme rhs = (Lexeme) nodes[2];
		Lexeme semicolon = (Lexeme) nodes[3];
		
		AppendNode result = new AppendNode(lhs, semicolon);
		result.appendChild(new IdentifierNode(lhs));
		result.appendChild(new IdentifierNode(rhs));
		
		return result;
	}
	
	/**
	 * onAppendSwitch
	 * 
	 * @param context
	 * @return
	 */
	public Object onAppendSwitch(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme lhs = (Lexeme) nodes[0];
		ASTParseNode rhs = (ASTParseNode) nodes[2];
		
		AppendNode result = new AppendNode(lhs);
		result.appendChild(new IdentifierNode(lhs));
		result.appendChild(rhs);
		
		return result;
	}
	
	/**
	 * onArguments
	 * 
	 * @param context
	 * @return
	 */
	public Object onArguments(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme lparen = (Lexeme) nodes[0];
		ListNode arguments = (ListNode) nodes[1];
		Lexeme rparen = (Lexeme) nodes[2];
		
		arguments.includeLexemesInRange(lparen, rparen);
		arguments.setListName("arguments"); //$NON-NLS-1$
		
		return arguments;
	}
	
	/**
	 * onAssignCreateExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onAssignCreateExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ASTParseNode lhs = (ASTParseNode) nodes[0];
		ASTParseNode rhs = (ASTParseNode) nodes[2];
		Lexeme semicolon = (Lexeme) nodes[3];
		
		AssignNode result = new AssignNode(semicolon);
		result.appendChild(lhs);
		result.appendChild(rhs);
		
		return result;
	}
	
	/**
	 * onAssignInvokeExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onAssignInvokeExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ASTParseNode lhs = (ASTParseNode) nodes[0];
		ASTParseNode rhs = (ASTParseNode) nodes[2];
		Lexeme semicolon = (Lexeme) nodes[3];
		
		AssignNode result = new AssignNode(semicolon);
		result.appendChild(lhs);
		result.appendChild(rhs);
		
		return result;
	}
	
	/**
	 * onAssignReference
	 * 
	 * @param context
	 * @return
	 */
	public Object onAssignReference(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ASTParseNode lhs = (ASTParseNode) nodes[0];
		Lexeme rhs = (Lexeme) nodes[2];
		Lexeme semicolon = (Lexeme) nodes[3];
		
		AssignNode result = new AssignNode(semicolon);
		result.appendChild(lhs);
		result.appendChild(new IdentifierNode(rhs));
		
		return result;
	}
	
	/**
	 * onAssignSwitchExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onAssignSwitchExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ASTParseNode lhs = (ASTParseNode) nodes[0];
		ASTParseNode rhs = (ASTParseNode) nodes[2];
		
		AssignNode result = new AssignNode();
		result.appendChild(lhs);
		result.appendChild(rhs);
		
		return result;
	}
	
	/**
	 * onBody
	 * 
	 * @param context
	 * @return
	 */
	public Object onBody(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme lcurly = (Lexeme) nodes[0];
		ListNode statements = (ListNode) nodes[1];
		Lexeme rcurly = (Lexeme) nodes[2];
		
		statements.includeLexemesInRange(lcurly, rcurly);
		statements.setListName("statements"); //$NON-NLS-1$
		
		return statements;
	}
	
	/**
	 * onCaseStatement
	 * 
	 * @param context
	 * @return
	 */
	public Object onCaseStatement(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme identifier = (Lexeme) nodes[1];
		ASTParseNode body = (ASTParseNode) nodes[3];
		Lexeme semicolon = (Lexeme) nodes[4];
		
		CaseNode result = new CaseNode(keyword, semicolon);
		result.appendChild(new IdentifierNode(identifier));
		result.appendChild(body);
		
		return result;
	}
	
	/**
	 * onCreateExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onCreateExpression(IReductionContext context)
	{
		Lexeme constant = (Lexeme) context.getNode(0);
		ListNode arguments = (ListNode) context.getNode(1);
		
		InstantiationNode result = new InstantiationNode(constant);
		result.appendChild(arguments);
		result.setAttribute("name", constant.getText()); //$NON-NLS-1$
		
		return result;
	}
	
	/**
	 * onEmptyArguments
	 * 
	 * @param context
	 * @return
	 */
	public Object onEmptyArguments(IReductionContext context)
	{
		Lexeme lparen = (Lexeme) context.getNode(0);
		Lexeme rparen = (Lexeme) context.getNode(1);
		
		ListNode result = new ListNode(lparen, rparen);
		result.setListName("arguments"); //$NON-NLS-1$
		
		return result;
	}
	
	/**
	 * onEmptyBody
	 * 
	 * @param context
	 * @return
	 */
	public Object onEmptyBody(IReductionContext context)
	{
		Lexeme lcurly = (Lexeme) context.getNode(0);
		Lexeme rcurly = (Lexeme) context.getNode(1);
		
		ListNode result = new ListNode(lcurly, rcurly);
		result.setListName("statements"); //$NON-NLS-1$
		
		return result;
	}
	
	/**
	 * onEmptyParameters
	 * 
	 * @param context
	 * @return
	 */
	public Object onEmptyParameters(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme lcurly = (Lexeme) nodes[0];
		Lexeme rcurly = (Lexeme) nodes[1];
		
		ListNode result = new ListNode(lcurly, rcurly);
		result.setListName("parameters"); //$NON-NLS-1$
		
		return result;
	}
	
	/**
	 * onFalse
	 * 
	 * @param context
	 * @return
	 */
	public Object onFalse(IReductionContext context)
	{
		Lexeme identifier = (Lexeme) context.getNode(0);
		
		FalseNode result = new FalseNode(identifier);
		
		return result;
	}
	
	/**
	 * onFirstArgument
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstArgument(IReductionContext context)
	{
		ASTParseNode argument = (ASTParseNode) context.getNode(0);
		
		ListNode result = new ListNode();
		result.appendChild(argument);
		
		return result;
	}
	
	/**
	 * onFirstBodyStatement
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstBodyStatement(IReductionContext context)
	{
		ASTParseNode statement = (ASTParseNode) context.getNode(0);
		
		ListNode result = new ListNode();
		result.appendChild(statement);
		
		return result;
	}
	
	/**
	 * onFirstCaseStatement
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstCaseStatement(IReductionContext context)
	{
		ASTParseNode statement = (ASTParseNode) context.getNode(0);
		
		ListNode result = new ListNode();
		result.appendChild(statement);
		
		return result;
	}
	
	/**
	 * onFirstName
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstName(IReductionContext context)
	{
		Lexeme identifier = (Lexeme) context.getNode(0);
		
		IdentifierNode result = new IdentifierNode(identifier);
		
		return result;
	}
	
	/**
	 * onFirstParameter
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstParameter(IReductionContext context)
	{
		ASTParseNode parameter = (ASTParseNode) context.getNode(0);
		
		ListNode result = new ListNode();
		result.appendChild(parameter);
		
		return result;
	}
	
	/**
	 * onFirstStatement
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstStatement(IReductionContext context)
	{
		ASTParseNode statement = (ASTParseNode) context.getNode(0);
		
		this._root.appendChild(statement);
		
		return this._root;
	}
	
	/**
	 * onHandlerDefinition
	 * 
	 * @param context
	 * @return
	 */
	public Object onHandlerDefinition(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme nameIdentifier = (Lexeme) nodes[0];
		ListNode parameters = (ListNode) nodes[1];
		Lexeme returnTypeIdentifier = (Lexeme) nodes[3];
		ListNode body = (ListNode) nodes[4];
		
		HandlerNode result = new HandlerNode(nameIdentifier);
		result.appendChild(parameters);
		result.appendChild(body);
		result.setAttribute("name", nameIdentifier.getText()); //$NON-NLS-1$
		result.setAttribute("return-type", returnTypeIdentifier.getText()); //$NON-NLS-1$
		
		return result;
	}
	
	/**
	 * onIdentifier
	 * 
	 * @param context
	 * @return
	 */
	public Object onIdentifier(IReductionContext context)
	{
		Lexeme identifier = (Lexeme) context.getNode(0);
		
		IdentifierNode result = new IdentifierNode(identifier);
		
		return result;
	}
	
	/**
	 * onInvokeExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onInvokeExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ASTParseNode target = (ASTParseNode) nodes[0];
		ListNode arguments = (ListNode) nodes[1];
		
		InvocationNode result = new InvocationNode();
		result.appendChild(target);
		result.appendChild(arguments);
		
		return result;
	}
	
	/**
	 * onImportStatement
	 * 
	 * @param context
	 * @return
	 */
	public Object onImportStatement(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme keyword = (Lexeme) nodes[0];
		ASTParseNode name = (ASTParseNode) nodes[1];
		Lexeme semicolon = (Lexeme) nodes[2];
		
		ImportNode result = new ImportNode(keyword, semicolon);
		result.appendChild(name);
		
		return result;
	}
	
	/**
	 * onInvocationStatement
	 * 
	 * @param context
	 * @return
	 */
	public Object onInvocationStatement(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ASTParseNode target = (ASTParseNode) nodes[0];
		ListNode arguments = (ListNode) nodes[1];
		Lexeme semicolon = (Lexeme) nodes[2];
		
		InvocationNode result = new InvocationNode(semicolon);
		result.appendChild(target);
		result.appendChild(arguments);
		
		return result;
	}
	
	/**
	 * onNull
	 * 
	 * @param context
	 * @return
	 */
	public Object onNull(IReductionContext context)
	{
		Lexeme identifier = (Lexeme) context.getNode(0);
		
		NullNode result = new NullNode(identifier);
		
		return result;
	}
	
	/**
	 * onParameter
	 * 
	 * @param context
	 * @return
	 */
	public Object onParameter(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme nameIdentifier = (Lexeme) nodes[0];
		Lexeme typeIdentifier = (Lexeme) nodes[2];
		
		ParameterNode result = new ParameterNode();
		result.setAttribute("name", nameIdentifier.getText()); //$NON-NLS-1$
		result.setAttribute("type", typeIdentifier.getText()); //$NON-NLS-1$
		
		return result;
	}
	
	/**
	 * onParameters
	 * 
	 * @param context
	 * @return
	 */
	public Object onParameters(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme lcurly = (Lexeme) nodes[0];
		ListNode parameters = (ListNode) nodes[1];
		Lexeme rcurly = (Lexeme) nodes[2];
		
		parameters.includeLexemesInRange(lcurly, rcurly);
		parameters.setListName("parameters"); //$NON-NLS-1$
		
		return parameters;
	}
	
	/**
	 * onString
	 * 
	 * @param context
	 * @return
	 */
	public Object onString(IReductionContext context)
	{
		Lexeme string = (Lexeme) context.getNode(0);
		
		StringNode result = new StringNode(string);
		
		return result;
	}
	
	/**
	 * onSwitchExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onSwitchExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme identifier = (Lexeme) nodes[2];
		ListNode statements = (ListNode) nodes[5];
		Lexeme rcurly = (Lexeme) nodes[6];
		
		statements.setListName("statements"); //$NON-NLS-1$
		
		SwitchNode result = new SwitchNode(keyword, rcurly);
		result.appendChild(new IdentifierNode(identifier));
		result.appendChild(statements);
		
		return result;
	}
	
	/**
	 * onTrue
	 * 
	 * @param context
	 * @return
	 */
	public Object onTrue(IReductionContext context)
	{
		Lexeme identifier = (Lexeme) context.getNode(0);
		
		TrueNode result = new TrueNode(identifier);
		
		return result;
	}
	
	/**
	 * onTypeOnlyParameter
	 * 
	 * @param context
	 * @return
	 */
	public Object onTypeOnlyParameter(IReductionContext context)
	{
		Lexeme typeIdentifier = (Lexeme) context.getNode(1);
		
		ParameterNode result = new ParameterNode();
		result.setAttribute("type", typeIdentifier.getText()); //$NON-NLS-1$
		
		return result;
	}
}
