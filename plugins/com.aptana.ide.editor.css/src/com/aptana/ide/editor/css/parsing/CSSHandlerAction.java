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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin Lindsey
 */
public enum CSSHandlerAction
{
	ADD_ATTRIBUTE_SELECTOR("AddAttributeSelector"), //$NON-NLS-1$
	ADD_DECLARATION("AddDeclaration"), //$NON-NLS-1$
	ADD_EMPTY_DECLARATION("AddEmptyDeclaration"), //$NON-NLS-1$
	ADD_FUNCTION_EXPRESSION("AddFunctionExpression"), //$NON-NLS-1$
	ADD_LIST("AddList"), //$NON-NLS-1$
	ADD_PRIMITIVE_EXPRESSION("AddPrimitiveExpression"), //$NON-NLS-1$
	ADD_SELECTOR("AddSelector"), //$NON-NLS-1$
	ADD_SEPARATOR_PRIMITIVE_EXPRESSION("AddSeparatorPrimitiveExpression"), //$NON-NLS-1$
	ADD_SEPARATOR_FUNCTION_EXPRESSION("AddSeparatorFunctionExpression"), //$NON-NLS-1$
	ADD_SIMPLE_SELECTOR("AddSimpleSelector"), //$NON-NLS-1$
	ADD_SIMPLE_COMBINED_SELECTOR("AddSimpleCombinedSelector"), //$NON-NLS-1$
	ADD_STATEMENT("AddStatement"), //$NON-NLS-1$
	ATTRIBUTE_EXISTS_SELECTOR("AttributeExistsSelector"), //$NON-NLS-1$
	ATTRIBUTE_SELECTOR("AttributeSelector"), //$NON-NLS-1$
	ATTRIBUTE_SELECTORS("AttributeSelectors"), //$NON-NLS-1$
	ATTRIBUTE_VALUE_EXISTS_SELECTOR("AttributeValueExistsSelector"), //$NON-NLS-1$
	AT_WORD("AtWord"), //$NON-NLS-1$
	CHARSET("CharSet"), //$NON-NLS-1$
	DECLARATION("Declaration"), //$NON-NLS-1$
	EMPTY_DECLARATION("EmptyDeclaration"), //$NON-NLS-1$
	FIRST_ATTRIBUTE_SELECTOR("FirstAttributeSelector"), //$NON-NLS-1$
	FIRST_LIST("FirstList"), //$NON-NLS-1$
	FIRST_DECLARATION("FirstDeclaration"), //$NON-NLS-1$
	FIRST_EXPRESSION("FirstExpression"), //$NON-NLS-1$
	FIRST_SELECTOR("FirstSelector"), //$NON-NLS-1$
	FIRST_SIMPLE_SELECTOR("FirstSimpleSelector"), //$NON-NLS-1$
	FIRST_STATEMENT("FirstStatement"), //$NON-NLS-1$
	FUNCTION("Function"), //$NON-NLS-1$
	IMPORT("Import"), //$NON-NLS-1$
	IMPORTANT_DECLARATION("ImportantDeclaration"), //$NON-NLS-1$
	IMPORT_LIST("ImportList"), //$NON-NLS-1$
	MEDIA("Media"), //$NON-NLS-1$
	PAGE("Page"), //$NON-NLS-1$
	PAGE_DECLARATION("PageDeclaration"), //$NON-NLS-1$
	PLUS_MINUS_TERM("PlusMinusTerm"), //$NON-NLS-1$
	PSEUDO_PAGE("PseudoPage"), //$NON-NLS-1$
	PSEUDO_SELECTOR("PseudoSelector"), //$NON-NLS-1$
	PSEUDO_SELECTOR_FUNCTION("PseudoSelectorFunction"), //$NON-NLS-1$
	RULE("Rule"), //$NON-NLS-1$
	RULE_DECLARATIONS("RuleDeclarations"), //$NON-NLS-1$
	TERM("Term"), //$NON-NLS-1$
	TYPE_OR_UNIVERSAL_SELECTOR("TypeOrUniversalSelector"), //$NON-NLS-1$
	TYPE_AND_ATTRIBUTE_SELECTORS("TypeAndAttributeSelectors"); //$NON-NLS-1$
	
	private static final Map<String,CSSHandlerAction> NAME_MAP;
	private String _name;
	
	/**
	 * static constructor
	 */
	static
	{
		NAME_MAP = new HashMap<String,CSSHandlerAction>();
		
		for (CSSHandlerAction action : EnumSet.allOf(CSSHandlerAction.class))
		{
			NAME_MAP.put(action.getName(), action);
		}
	}
	
	/**
	 * CSSHandleActions
	 * 
	 * @param name
	 */
	private CSSHandlerAction(String name)
	{
		this._name = name;
	}
	
	/**
	 * get
	 * 
	 * @param name
	 * @return
	 */
	public static final CSSHandlerAction get(String name)
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
