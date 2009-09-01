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
package com.aptana.ide.editor.css.parsing;

import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.editor.css.parsing.nodes.CSSCharSetNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSDeclarationNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSExprNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSImportNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSListNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSMediaNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSMediumNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSPageNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSParseNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSParseNodeTypes;
import com.aptana.ide.editor.css.parsing.nodes.CSSRuleSetNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSSelectorNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSSimpleSelectorNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSTermNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSTextNode;
import com.aptana.ide.editors.unified.parsing.UnifiedReductionHandler;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.bnf.IReductionContext;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.ParseFragment;

/**
 * @author Kevin Lindsey
 */
public class CSSASTHandler extends UnifiedReductionHandler<CSSParseNode>
{
	private List<CSSParseNode> _statements;
	
	/**
	 * CSSASTHandler
	 * 
	 * @param parseState
	 */
	public CSSASTHandler()
	{
		super();
		
		this._statements = new ArrayList<CSSParseNode>();
	}
	
	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#afterParse(com.aptana.ide.parsing.IParseState, com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void afterParse(IParseState parseState, IParseNode parentNode)
	{
		if (parentNode != null)
		{
			for (CSSParseNode node : this._statements)
			{
				parentNode.appendChild(node);
			}
		}

		this._statements.clear();
		
		super.afterParse(parseState, parentNode);
	}

	/**
	 * @param context
	 * @param primitive
	 * @return
	 */
	private CSSTermNode createTermNode(IReductionContext context, Lexeme primitive)
	{
		CSSTermNode result = (CSSTermNode) this.createNode(CSSParseNodeTypes.TERM, primitive);
		
		result.setAttribute("value", primitive.getText()); //$NON-NLS-1$
		
		return result;
	}
	
	/**
	 * onAddAttributeSelector
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddAttributeSelector(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CSSTextNode component = (CSSTextNode) nodes[1];
		
		ParseFragment fragment = (ParseFragment) nodes[0];
		fragment.appendChild(component);
		
		return fragment;
	}
	
	/**
	 * onAddDeclaration
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddDeclaration(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CSSListNode declarations = (CSSListNode) nodes[0];
		CSSDeclarationNode declaration = (CSSDeclarationNode) nodes[2];
		
		declarations.appendChild(declaration);
		
		return declarations;
	}
	
	/**
	 * onAddEmptyDeclaration
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddEmptyDeclaration(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CSSListNode declarations = (CSSListNode) nodes[0];
		
		declarations.appendChild(CSSParseNode.Empty);
		
		return declarations;
	}
	
	/**
	 * onAddFunctionExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddFunctionExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		CSSTermNode function = (CSSTermNode) nodes[1];
		function.setAttribute("joining-operator", " "); //$NON-NLS-1$ //$NON-NLS-2$
		
		CSSExprNode expression = (CSSExprNode) nodes[0];
		expression.appendChild(function);
		
		return expression;
	}
	
	/**
	 * onAddList
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddList(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CSSListNode list = (CSSListNode) nodes[0];
		Lexeme identifier = (Lexeme) nodes[2];
		CSSTextNode medium = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, identifier);
		
		list.appendChild(medium);
		
		return list;
	}
	
	/**
	 * onAddPrimitiveExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddPrimitiveExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme primitive = (Lexeme) nodes[1];
		
		CSSTermNode term = this.createTermNode(context, primitive);
		term.setAttribute("joining-operator", " "); //$NON-NLS-1$ //$NON-NLS-2$
		
		CSSExprNode expression = (CSSExprNode) nodes[0];
		expression.appendChild(term);
		
		return expression;
	}
	
	/**
	 * onAddSelector
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddSelector(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CSSListNode selectors = (CSSListNode) nodes[0];
		CSSSelectorNode selector = (CSSSelectorNode) nodes[2];
		
		selectors.appendChild(selector);
		
		return selectors;
	}
	
	/**
	 * onAddSeparatorFunctionExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddSeparatorFunctionExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme separator = (Lexeme) nodes[1];
		CSSTermNode function = (CSSTermNode) nodes[2];
		
		function.setAttribute("joining-operator", separator.getText()); //$NON-NLS-1$
		
		CSSExprNode expression = (CSSExprNode) nodes[0];
		expression.appendChild(function);
		
		return expression;
	}
	
	/**
	 * onAddSeparatorPrimitiveExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddSeparatorPrimitiveExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme separator = (Lexeme) nodes[1];
		Lexeme primitive = (Lexeme) nodes[2];
		
		CSSTermNode term = this.createTermNode(context, primitive);
		term.setAttribute("joining-operator", separator.getText()); //$NON-NLS-1$
		
		CSSExprNode expression = (CSSExprNode) nodes[0];
		expression.appendChild(term);
		
		return expression;
	}
	
	/**
	 * onAddSimpleCombinedSelector
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddSimpleCombinedSelector(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme combinator = (Lexeme) nodes[1];
		
		CSSTextNode combinatorNode = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, combinator);
		
		CSSSimpleSelectorNode simpleSelector = (CSSSimpleSelectorNode) nodes[2];
		simpleSelector.appendChild(combinatorNode);
		
		CSSSelectorNode selector = (CSSSelectorNode) nodes[0];
		selector.appendChild(simpleSelector);
		
		return selector;
	}
	
	/**
	 * onAddSimpleSelector
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddSimpleSelector(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		CSSSimpleSelectorNode simpleSelector = (CSSSimpleSelectorNode) nodes[1];
		simpleSelector.appendChild(CSSParseNode.Empty);
		
		CSSSelectorNode selector = (CSSSelectorNode) nodes[0];
		selector.appendChild(simpleSelector);
		
		return selector;
	}
	
	/**
	 * onAddStatement
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddStatement(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CSSParseNode statement = (CSSParseNode) nodes[1];
		
		this._statements.add(statement);
		
		return this._statements;
	}
	
	/**
	 * onAttributeExistsSelector
	 * 
	 * @param context
	 * @return
	 */
	public Object onAttributeExistsSelector(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme lbracket = (Lexeme) nodes[0];
		Lexeme name = (Lexeme) nodes[1];
		Lexeme rbracket = (Lexeme) nodes[2];
		
		String text = "[" + name.getText() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		
		CSSTextNode result = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, lbracket);
		result.setText(text);
		result.includeLexemeInRange(rbracket);
		
		return result;
	}
	
	/**
	 * onAttributeSelector
	 * 
	 * @param context
	 * @return
	 */
	public Object onAttributeSelector(IReductionContext context)
	{
		Lexeme selector = (Lexeme) context.getNode(0);
		
		CSSTextNode result = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, selector);
		result.setText(selector.getText());
		
		return result;
	}
	
	/**
	 * onTAttributesSelectors
	 * 
	 * @param context
	 * @return
	 */
	public Object onAttributeSelectors(IReductionContext context)
	{
		ParseFragment attributeSelectors = (ParseFragment) context.getNode(0);
		
		CSSListNode components = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);
		components.setListName("components"); //$NON-NLS-1$
		components.appendChild(attributeSelectors);
		
		CSSSimpleSelectorNode result = (CSSSimpleSelectorNode) this.createNode(
			CSSParseNodeTypes.SIMPLE_SELECTOR,
			attributeSelectors.getStartingLexeme()
		);
		result.appendChild(components);
		
		return result;
	}
	
	/**
	 * onAttributeValueExistsSelector
	 * 
	 * @param context
	 * @return
	 */
	public Object onAttributeValueExistsSelector(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme lbracket = (Lexeme) nodes[0];
		Lexeme name = (Lexeme) nodes[1];
		Lexeme operator = (Lexeme) nodes[2];
		Lexeme test = (Lexeme) nodes[3];
		Lexeme rbracket = (Lexeme) nodes[4];
		
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("["); //$NON-NLS-1$
		buffer.append(name.getText());
		buffer.append(operator.getText());
		buffer.append(test.getText());
		buffer.append("]"); //$NON-NLS-1$
		
		CSSTextNode result = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, lbracket);
		result.setText(buffer.toString());
		result.includeLexemeInRange(rbracket);
		
		return result;
	}
	
	/**
	 * onStart
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onAtWord(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme atLexeme = (Lexeme) nodes[0];
		CSSParseNode result = this.createNode(CSSParseNodeTypes.AT_RULE, atLexeme);
		
		return result;
	}
	
	/**
	 * onCharSet
	 * 
	 * @param context
	 * @return
	 */
	public Object onCharSet(IReductionContext context)
	{
		Object[] objects = context.getNodes();
		
		Lexeme keyword = (Lexeme) objects[0];
		Lexeme name = (Lexeme) objects[1];
		Lexeme semicolon = (Lexeme) objects[2];
		
		CSSTextNode nameNode = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, name);
		
		CSSCharSetNode result = (CSSCharSetNode) this.createNode(CSSParseNodeTypes.CHAR_SET, keyword);
		result.appendChild(nameNode);
		result.includeLexemeInRange(semicolon);
		
		return result;
	}
	
	/**
	 * onDeclaration
	 * 
	 * @param context
	 * @return
	 */
	public Object onDeclaration(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme identifier = (Lexeme) nodes[0];
		CSSExprNode expression = (CSSExprNode) nodes[2];
		
//		if (identifier.typeIndex == CSSTokenTypes.IDENTIFIER)
//		{
//			context.getParser().changeTokenType(CSSTokenTypes.PROPERTY);
//		}
		
		CSSDeclarationNode result = (CSSDeclarationNode) this.createNode(CSSParseNodeTypes.DECLARATION, identifier);
		result.setAttribute("name", identifier.getText()); //$NON-NLS-1$
		result.appendChild(expression);
		
		return result;
	}
	
	/**
	 * onEmptyDeclaration
	 * 
	 * @param context
	 * @return
	 */
	public Object onEmptyDeclaration(IReductionContext context)
	{
		CSSListNode result = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);
		result.appendChild(CSSParseNode.Empty);
		result.setListName("properties"); //$NON-NLS-1$
		result.setDelimiter("\n"); //$NON-NLS-1$
		
		return result;
	}
	
	/**
	 * onFirstAttributeSelector
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstAttributeSelector(IReductionContext context)
	{
		CSSTextNode component = (CSSTextNode) context.getNode(0);
		
		ParseFragment result = new ParseFragment();
		result.appendChild(component);
		
		return result;
	}
	
	/**
	 * onFirstDeclaration
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstDeclaration(IReductionContext context)
	{
		CSSDeclarationNode declaration = (CSSDeclarationNode) context.getNode(0);
		
		CSSListNode result = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);
		result.appendChild(declaration);
		result.setListName("properties"); //$NON-NLS-1$
		result.setDelimiter("\n"); //$NON-NLS-1$
		
		return result;
	}
	
	/**
	 * onFirstExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstExpression(IReductionContext context)
	{
		CSSTermNode term = (CSSTermNode) context.getNode(0);
		
		CSSExprNode result = (CSSExprNode) this.createNode(CSSParseNodeTypes.EXPR, term.getStartingLexeme());
		result.appendChild(term);
		
		return result;
	}
	
	/**
	 * onFirstList
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstList(IReductionContext context)
	{
		Lexeme identifier = (Lexeme) context.getNode(0);
		CSSListNode list = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, identifier);
		CSSTextNode medium = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, identifier);
		
		list.setDelimiter(", "); //$NON-NLS-1$
		list.appendChild(medium);
		
		return list;
	}
	
	/**
	 * onFirstSelector
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstSelector(IReductionContext context)
	{
		CSSSelectorNode selector = (CSSSelectorNode) context.getNode(0);
		
		CSSListNode result = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, selector.getStartingLexeme());
		result.appendChild(selector);
		result.setListName("selectors"); //$NON-NLS-1$
		
		return result;
	}
	
	/**
	 * onFirstSelector
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstSimpleSelector(IReductionContext context)
	{
		CSSSimpleSelectorNode selector = (CSSSimpleSelectorNode) context.getNode(0);
		selector.appendChild(CSSParseNode.Empty);
		
		CSSSelectorNode result = (CSSSelectorNode) this.createNode(CSSParseNodeTypes.SELECTOR, selector.getStartingLexeme());
		result.appendChild(selector);
		
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
		CSSParseNode statement = (CSSParseNode) context.getNode(0);
		
		this._statements.add(statement);
		
		return this._statements;
	}
	
	/**
	 * onFunction
	 * 
	 * @param context
	 * @return
	 */
	public Object onFunction(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme keyword = (Lexeme) nodes[0];
		CSSExprNode args = (CSSExprNode) nodes[1];
		Lexeme rcurly = (Lexeme) nodes[2];
		
		CSSTermNode result = createTermNode(context, keyword);
		result.appendChild(args);
		result.includeLexemeInRange(rcurly);
		
		return result;
	}
	
	/**
	 * onImport
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onImport(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme importLexeme = (Lexeme) nodes[0];
		Lexeme nameLexeme = (Lexeme) nodes[1];
		Lexeme semicolon = (Lexeme) nodes[2];
		CSSImportNode result = (CSSImportNode) this.createNode(CSSParseNodeTypes.IMPORT, importLexeme);
		
		result.setAttribute("name", nameLexeme.getText()); //$NON-NLS-1$
		
		result.includeLexemeInRange(semicolon);

		return result;
	}
	
	/**
	 * onImportantDeclaration
	 * 
	 * @param context
	 * @return
	 */
	public Object onImportantDeclaration(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme identifier = (Lexeme) nodes[0];
		CSSExprNode expression = (CSSExprNode) nodes[2];
		Lexeme important = (Lexeme) nodes[3];
		
//		if (identifier.typeIndex == CSSTokenTypes.IDENTIFIER)
//		{
//			context.getParser().changeTokenType(CSSTokenTypes.PROPERTY);
//		}
		
		CSSDeclarationNode result = (CSSDeclarationNode) this.createNode(CSSParseNodeTypes.DECLARATION, identifier);
		result.setAttribute("name", identifier.getText()); //$NON-NLS-1$
		result.setAttribute("status", important.getText()); //$NON-NLS-1$
		result.appendChild(expression);
		result.includeLexemeInRange(important);
		
		return result;
	}
	
	/**
	 * onImportList
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onImportList(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme importLexeme = (Lexeme) nodes[0];
		Lexeme nameLexeme = (Lexeme) nodes[1];
		CSSListNode list = (CSSListNode) nodes[2];
		Lexeme semicolon = (Lexeme) nodes[3];
		CSSImportNode result = (CSSImportNode) this.createNode(CSSParseNodeTypes.IMPORT, importLexeme);
		
		result.setAttribute("name", nameLexeme.getText()); //$NON-NLS-1$
		result.appendChild(list);
		result.includeLexemeInRange(semicolon);
		
		return result;
	}
	
	/**
	 * onMedia
	 * 
	 * @param context
	 * @return
	 */
	public Object onMedia(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme identifier = (Lexeme) nodes[1];
		Lexeme rcurly = (Lexeme) nodes[3];
		
		// create media container and set its delimiter type
		CSSListNode media = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);
		media.setDelimiter(", "); //$NON-NLS-1$
		
		// create first medium and add to media list
		CSSMediumNode medium = (CSSMediumNode) this.createNode(CSSParseNodeTypes.MEDIUM, identifier);
		media.appendChild(medium);
		
		// create resulting media parse node, add children, and update its range
		CSSMediaNode result = (CSSMediaNode) this.createNode(CSSParseNodeTypes.MEDIA, keyword);
		result.includeLexemeInRange(rcurly);
		result.appendChild(media);
		result.appendChild(CSSParseNode.Empty);
		
		return result;
	}
	
	/**
	 * onPage
	 * 
	 * @param context
	 * @return
	 */
	public Object onPage(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme rcurly = (Lexeme) nodes[2];
		
		CSSListNode declarations = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);
		declarations.setDelimiter("\n"); //$NON-NLS-1$
		
		CSSPageNode result = (CSSPageNode) this.createNode(CSSParseNodeTypes.PAGE, keyword);
		result.appendChild(declarations);
		result.includeLexemeInRange(rcurly);
		
		return result;
	}
	
	/**
	 * onPageDeclaration
	 * 
	 * @param context
	 * @return
	 */
	public Object onPageDeclaration(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme keyword = (Lexeme) nodes[0];
		CSSListNode declarations = (CSSListNode) nodes[2];
		Lexeme rcurly = (Lexeme) nodes[3];
		
		CSSPageNode result = (CSSPageNode) this.createNode(CSSParseNodeTypes.PAGE, keyword);
		result.appendChild(declarations);
		result.includeLexemeInRange(rcurly);
		
		return result;
	}
	
	/**
	 * onPlusMinusTerm
	 * 
	 * @param context
	 * @return
	 */
	public Object onPlusMinusTerm(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme plusMinus = (Lexeme) nodes[0];
		Lexeme primitive = (Lexeme) nodes[1];
		
		CSSTermNode result = (CSSTermNode) this.createNode(CSSParseNodeTypes.TERM, plusMinus);
		result.setAttribute("operator", plusMinus.getText()); //$NON-NLS-1$
		result.setAttribute("value", primitive.getText()); //$NON-NLS-1$
		
		return result;
	}
	
	/**
	 * onPseudoPage
	 * 
	 * @param context
	 * @return
	 */
	public Object onPseudoPage(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme keyword = (Lexeme) nodes[0];
		Lexeme identifier = (Lexeme) nodes[2];
		Lexeme rcurly = (Lexeme) nodes[4];
		
		CSSListNode declarations = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);
		declarations.setDelimiter("\n"); //$NON-NLS-1$
		
		CSSPageNode result = (CSSPageNode) this.createNode(CSSParseNodeTypes.PAGE, keyword);
		result.setAttribute("name", identifier.getText()); //$NON-NLS-1$
		result.appendChild(declarations);
		result.includeLexemeInRange(rcurly);
		
		return result;
	}
	
	/**
	 * onPseudoSelector
	 * 
	 * @param context
	 * @return
	 */
	public Object onPseudoSelector(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme colon = (Lexeme) nodes[0];
		Lexeme name = (Lexeme) nodes[1];
		
		CSSTextNode result = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, colon);
		result.setText(Messages.CSSASTHandler_29 + name.getText());
		result.includeLexemeInRange(name);
		
		return result;
	}
	
	/**
	 * onPseudoSelectorFunction
	 * 
	 * @param context
	 * @return
	 */
	public Object onPseudoSelectorFunction(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme colon = (Lexeme) nodes[0];
		CSSTermNode function = (CSSTermNode) nodes[1];
		
		int startingIndex = this.lexemes.getLexemeIndex(function.getStartingLexeme());
		int endingIndex = this.lexemes.getLexemeIndex(function.getEndingLexeme());
		StringBuilder buffer = new StringBuilder();
		
		buffer.append(Messages.CSSASTHandler_30);
		
		for (int i = startingIndex; i <= endingIndex; i++)
		{
			buffer.append(this.lexemes.get(i).getText());
		}
		
		CSSTextNode result = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, colon);
		result.setText(buffer.toString());
		result.includeLexemeInRange(function.getEndingLexeme());
		
		return result;
	}
	
	/**
	 * onRule
	 * 
	 * @param context
	 * @return
	 */
	public Object onRule(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CSSListNode selectors = (CSSListNode) nodes[0];
		Lexeme rcurly = (Lexeme) nodes[2];
		
		CSSListNode declarations = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);
		declarations.setListName(Messages.CSSASTHandler_31);
		
		CSSRuleSetNode result = (CSSRuleSetNode) this.createNode(CSSParseNodeTypes.RULE_SET, selectors.getStartingLexeme());
		result.appendChild(selectors);
		result.appendChild(declarations);
		result.includeLexemeInRange(rcurly);
		
		return result;
	}
	
	/**
	 * onRuleDeclarations
	 * 
	 * @param context
	 * @return
	 */
	public Object onRuleDeclarations(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CSSListNode selectors = (CSSListNode) nodes[0];
		CSSListNode declarations = (CSSListNode) nodes[2];
		Lexeme rcurly = (Lexeme) nodes[3];
		
		CSSRuleSetNode result = (CSSRuleSetNode) this.createNode(CSSParseNodeTypes.RULE_SET, selectors.getStartingLexeme());
		result.appendChild(selectors);
		result.appendChild(declarations);
		result.includeLexemeInRange(rcurly);
		
		return result;
	}

	/**
	 * onTerm
	 * 
	 * @param context
	 * @return
	 */
	public Object onTerm(IReductionContext context)
	{
		Lexeme primitive = (Lexeme) context.getNode(0);
		
		CSSTermNode result = createTermNode(context, primitive);
		
		return result;
	}
	
	/**
	 * onTypeAndAttributesSelectors
	 * 
	 * @param context
	 * @return
	 */
	public Object onTypeAndAttributeSelectors(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme selector = (Lexeme) nodes[0];
		ParseFragment attributeSelectors = (ParseFragment) nodes[1];
		
		CSSTextNode component = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, selector);
		
		CSSListNode components = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);
		components.setListName(Messages.CSSASTHandler_32);
		components.appendChild(component);
		components.appendChild(attributeSelectors);
		
		CSSSimpleSelectorNode result = (CSSSimpleSelectorNode) this.createNode(
			CSSParseNodeTypes.SIMPLE_SELECTOR,
			selector
		);
		result.appendChild(components);
		
		return result;
	}
	
	/**
	 * onTypeOrUniversalSelector
	 * 
	 * @param context
	 * @return
	 */
	public Object onTypeOrUniversalSelector(IReductionContext context)
	{
		Lexeme selector = (Lexeme) context.getNode(0);
		
		CSSTextNode component = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, selector);
		
		CSSListNode components = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);
		components.setListName(Messages.CSSASTHandler_33);
		components.appendChild(component);
		
		CSSSimpleSelectorNode result = (CSSSimpleSelectorNode) this.createNode(
			CSSParseNodeTypes.SIMPLE_SELECTOR,
			selector
		);
		result.appendChild(components);
		
		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.bnf.IReductionHandler#reduce(com.aptana.ide.parsing.bnf.IReductionContext)
	 */
	public void reduce(IReductionContext context)
	{
		String actionName = context.getAction();
		Object result = null;
		
		// collect the matching rule's items from the stack
		Object[] nodes = this.pop(context.getNodeCount());
		
		// store the matching items within the reduction context for the handler method's use
		context.setNodes(nodes);
		
		if (actionName != null && actionName.length() > 0)
		{
			CSSHandlerAction action = CSSHandlerAction.get(actionName);
			
			if (action == null)
			{
				throw new IllegalArgumentException(Messages.CSSASTHandler_34 + actionName);
			}
			
			switch (action)
			{
				case ADD_ATTRIBUTE_SELECTOR:
					result = this.onAddAttributeSelector(context);
					break;
					
				case ADD_DECLARATION:
					result = this.onAddDeclaration(context);
					break;
					
				case ADD_EMPTY_DECLARATION:
					result = this.onAddEmptyDeclaration(context);
					break;
					
				case ADD_FUNCTION_EXPRESSION:
					result = this.onAddFunctionExpression(context);
					break;
					
				case ADD_LIST:
					result = this.onAddList(context);
					break;
					
				case ADD_PRIMITIVE_EXPRESSION:
					result = this.onAddPrimitiveExpression(context);
					break;
					
				case ADD_SELECTOR:
					result = this.onAddSelector(context);
					break;
					
				case ADD_SEPARATOR_PRIMITIVE_EXPRESSION:
					result = this.onAddSeparatorPrimitiveExpression(context);
					break;
					
				case ADD_SEPARATOR_FUNCTION_EXPRESSION:
					result = this.onAddSeparatorFunctionExpression(context);
					break;
					
				case ADD_SIMPLE_SELECTOR:
					result = this.onAddSimpleSelector(context);
					break;
					
				case ADD_SIMPLE_COMBINED_SELECTOR:
					result = this.onAddSimpleCombinedSelector(context);
					break;
					
				case ADD_STATEMENT:
					result = this.onAddStatement(context);
					break;
					
				case ATTRIBUTE_EXISTS_SELECTOR:
					result = this.onAttributeExistsSelector(context);
					break;
					
				case ATTRIBUTE_SELECTOR:
					result = this.onAttributeSelector(context);
					break;
					
				case ATTRIBUTE_SELECTORS:
					result = this.onAttributeSelectors(context);
					break;
					
				case ATTRIBUTE_VALUE_EXISTS_SELECTOR:
					result = this.onAttributeValueExistsSelector(context);
					break;
					
				case AT_WORD:
					result = this.onAtWord(context);
					break;
					
				case CHARSET:
					result = this.onCharSet(context);
					break;
					
				case DECLARATION:
					result = this.onDeclaration(context);
					break;
					
				case EMPTY_DECLARATION:
					result = this.onEmptyDeclaration(context);
					break;
					
				case FIRST_ATTRIBUTE_SELECTOR:
					result = this.onFirstAttributeSelector(context);
					break;
					
				case FIRST_LIST:
					result = this.onFirstList(context);
					break;
					
				case FIRST_DECLARATION:
					result = this.onFirstDeclaration(context);
					break;
					
				case FIRST_EXPRESSION:
					result = this.onFirstExpression(context);
					break;
					
				case FIRST_SELECTOR:
					result = this.onFirstSelector(context);
					break;
					
				case FIRST_SIMPLE_SELECTOR:
					result = this.onFirstSimpleSelector(context);
					break;
					
				case FIRST_STATEMENT:
					result = this.onFirstStatement(context);
					break;
					
				case FUNCTION:
					result = this.onFunction(context);
					break;
					
				case IMPORT:
					result = this.onImport(context);
					break;
					
				case IMPORTANT_DECLARATION:
					result = this.onImportantDeclaration(context);
					break;
					
				case IMPORT_LIST:
					result = this.onImportList(context);
					break;
					
				case MEDIA:
					result = this.onMedia(context);
					break;
					
				case PAGE:
					result = this.onPage(context);
					break;
					
				case PAGE_DECLARATION:
					result = this.onPageDeclaration(context);
					break;
					
				case PLUS_MINUS_TERM:
					result = this.onPlusMinusTerm(context);
					break;
					
				case PSEUDO_PAGE:
					result = this.onPseudoPage(context);
					break;
					
				case PSEUDO_SELECTOR:
					result = this.onPseudoSelector(context);
					break;
					
				case PSEUDO_SELECTOR_FUNCTION:
					result = this.onPseudoSelectorFunction(context);
					break;
					
				case RULE:
					result = this.onRule(context);
					break;
					
				case RULE_DECLARATIONS:
					result = this.onRuleDeclarations(context);
					break;
					
				case TERM:
					result = this.onTerm(context);
					break;
					
				case TYPE_OR_UNIVERSAL_SELECTOR:
					result = this.onTypeOrUniversalSelector(context);
					break;
					
				case TYPE_AND_ATTRIBUTE_SELECTORS:
					result = this.onTypeAndAttributeSelectors(context);
					break;
					
				default:
					throw new IllegalArgumentException(Messages.CSSASTHandler_35 + actionName);
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
