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
package com.aptana.ide.editor.css.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.editor.css.parsing.nodes.CSSDeclarationNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSParseNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSParseNodeTypes;
import com.aptana.ide.editor.css.parsing.nodes.CSSSelectorNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSSimpleSelectorNode;

/**
 * @author Kevin Lindsey
 */
public class CSSLabelProvider extends LabelProvider
{
	private static final Image PROPERTY_ICON = CSSPlugin.getImage("icons/property_icon.gif"); //$NON-NLS-1$
	private static final Image SELECTOR_ICON = CSSPlugin.getImage("icons/rule_icon.gif"); //$NON-NLS-1$

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		Image result;
		
		if (element instanceof CSSSelectorNode)
		{
			result = SELECTOR_ICON;
		}
		else if (element instanceof CSSDeclarationNode)
		{
			result = PROPERTY_ICON;
		}
		else
		{
			result = super.getImage(element);
		}
		
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		StringBuffer result = new StringBuffer();
		
		if (element instanceof CSSSelectorNode)
		{
			CSSSelectorNode selector = (CSSSelectorNode) element;
			
			for (int i = 0; i < selector.getChildCount(); i++)
			{
				CSSSimpleSelectorNode simpleSelector = (CSSSimpleSelectorNode) selector.getChild(i);
				CSSParseNode partsChildren = (CSSParseNode) simpleSelector.getChild(0);
				CSSParseNode combinator = (CSSParseNode) simpleSelector.getChild(1);
				
				if (combinator.getTypeIndex() != CSSParseNodeTypes.EMPTY)
				{
					result.append(StringUtils.SPACE).append(combinator.getText()).append(StringUtils.SPACE);
				}
				
				for (int j = 0; j < partsChildren.getChildCount(); j++)
				{
					CSSParseNode part = (CSSParseNode) partsChildren.getChild(j);
					String text = part.getText();
					char firstCharacter = text.charAt(0);
					
					if (firstCharacter != '.' && firstCharacter != '#')
					{
						result.append(StringUtils.SPACE);
					}

					result.append(text);
				}
			}
		}
		else if (element instanceof CSSDeclarationNode)
		{
			CSSDeclarationNode declaration = (CSSDeclarationNode) element;
			String name = declaration.getAttribute("name"); //$NON-NLS-1$
			
			result.append(name).append(" : "); //$NON-NLS-1$
			
			// process expression
			CSSParseNode expression = (CSSParseNode) declaration.getChild(0);
			
			this.addExpression(result, expression);
		}
		else
		{
			result.append(super.getText(element));
		}
		
		return result.toString();
	}
	
	/**
	 * addExpression
	 * 
	 * @param builder
	 * @param expression
	 */
	private void addExpression(StringBuffer builder, CSSParseNode expression)
	{
		for (int i = 0; i < expression.getChildCount(); i++)
		{
			CSSParseNode term = (CSSParseNode) expression.getChild(i);
			
			// add joining operator 
			String joiningOperator = term.getAttribute("joining-operator"); //$NON-NLS-1$
			
			builder.append(joiningOperator);
			
			// add space after commas
			if (joiningOperator.equals(",")) //$NON-NLS-1$
			{
				builder.append(StringUtils.SPACE);
			}
			
			// add operator
			String operator = term.getAttribute("operator"); //$NON-NLS-1$
			builder.append(operator);
			
			// add value
			String value = term.getAttribute("value"); //$NON-NLS-1$
			builder.append(value);
			
			// NOTE: This case is here to handle the arguments of a FUNCTION node
			if (term.hasChildren())
			{
				CSSParseNode child = (CSSParseNode) term.getChild(0);
				
				this.addExpression(builder, child);
				
				builder.append(term.getEndingLexeme().getText());
			}
		}
	}
}
