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
package com.aptana.ide.editor.css.parsing.nodes;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.ParseNodeFactory;

/**
 * @author Kevin Lindsey
 */
public class CSSParseNodeFactory extends ParseNodeFactory<CSSParseNode>
{
	/**
	 * @param owningParseState
	 */
	public CSSParseNodeFactory(IParseState owningParseState)
	{
		super(owningParseState);
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNodeFactory#createParseNode(int, com.aptana.ide.lexer.Lexeme)
	 */
	public CSSParseNode createParseNode(int nodeType, Lexeme startingLexeme)
	{
		CSSParseNode result = null;

		switch (nodeType)
		{
			case CSSParseNodeTypes.AT_RULE:
			case CSSParseNodeTypes.BLOCK:
				result = new CSSParseNode(nodeType, startingLexeme);
				break;

			case CSSParseNodeTypes.CHAR_SET:
				result = new CSSCharSetNode(startingLexeme);
				break;

			case CSSParseNodeTypes.DECLARATION:
				result = new CSSDeclarationNode(startingLexeme);
				break;

			case CSSParseNodeTypes.EXPR:
				result = new CSSExprNode(startingLexeme);
				break;

			case CSSParseNodeTypes.IMPORT:
				result = new CSSImportNode(startingLexeme);
				break;

			case CSSParseNodeTypes.LIST:
				result = new CSSListNode(startingLexeme);
				break;

			case CSSParseNodeTypes.MEDIA:
				result = new CSSMediaNode(startingLexeme);
				break;
				
			case CSSParseNodeTypes.MEDIUM:
				result = new CSSMediumNode(startingLexeme);
				break;

			case CSSParseNodeTypes.PAGE:
				result = new CSSPageNode(startingLexeme);
				break;

			case CSSParseNodeTypes.RULE_SET:
				result = new CSSRuleSetNode(startingLexeme);
				break;

			case CSSParseNodeTypes.SELECTOR:
				result = new CSSSelectorNode(startingLexeme);
				break;

			case CSSParseNodeTypes.SIMPLE_SELECTOR:
				result = new CSSSimpleSelectorNode(startingLexeme);
				break;

			case CSSParseNodeTypes.TERM:
				result = new CSSTermNode(startingLexeme);
				break;

			case CSSParseNodeTypes.TEXT:
				result = new CSSTextNode(startingLexeme);
				break;

			default:
				throw new IllegalArgumentException(StringUtils.format(Messages.CSSParseNodeFactory_UnknownCSSParseNodeType, nodeType));
		}

		return result;
	}
}
