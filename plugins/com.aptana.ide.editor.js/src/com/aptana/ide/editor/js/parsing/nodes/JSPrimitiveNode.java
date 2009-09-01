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
package com.aptana.ide.editor.js.parsing.nodes;

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;

/**
 * @author Kevin Lindsey
 */
public class JSPrimitiveNode extends JSParseNode
{
	private char _quote;
	
	/**
	 * JSPrimitiveNode
	 * 
	 * @param typeIndex
	 * @param startLexeme
	 */
	public JSPrimitiveNode(int typeIndex, Lexeme startLexeme)
	{
		super(typeIndex, startLexeme);
		
		String text = null;
		
		if (startLexeme != null)
		{
			text = startLexeme.getText();
		}
		else
		{
			// We allow this one special case since array literals like [,] have
			// implied null's with no lexemes to associate them with
			if (typeIndex == JSParseNodeTypes.NULL)
			{
				text = "null"; //$NON-NLS-1$
			}
			else
			{
				throw new IllegalArgumentException(Messages.JSPrimitiveNode_StartLexemeMustNotBeNull);
			}
		}
		
		if (typeIndex == JSParseNodeTypes.IDENTIFIER)
		{
			if (text.length() >= 2)
			{
				char first = text.charAt(0);
				char last = text.charAt(text.length() - 1);
				
				if (first == last && (first == '"' || first == '\"'))
				{
					text = text.substring(1, text.length() - 1);
					this._quote = first;
				}
			}
		}
		
		this.setAttribute("text", text); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getText()
	 */
	public String getText()
	{
		return this.getAttribute("text"); //$NON-NLS-1$
	}
	
	/**
	 * @see com.aptana.ide.editor.js.parsing.nodes.JSParseNode#getSource(com.aptana.ide.io.SourceWriter)
	 */
	public void getSource(SourceWriter writer)
	{
		switch (this.getTypeIndex())
		{
			case JSParseNodeTypes.FALSE:
			case JSParseNodeTypes.NULL:
			case JSParseNodeTypes.TRUE:
			case JSParseNodeTypes.NUMBER:
			case JSParseNodeTypes.REGULAR_EXPRESSION:
			case JSParseNodeTypes.THIS:
				writer.print(this.getAttribute("text")); //$NON-NLS-1$
				break;
				
			case JSParseNodeTypes.IDENTIFIER:
				if (this._quote != '\0')
				{
					writer.print(this._quote).print(this.getAttribute("text")).print(this._quote); //$NON-NLS-1$
				}
				else
				{
					writer.print(this.getAttribute("text")); //$NON-NLS-1$
				}
				break;
				
			default:
				break;				
		}
	}
}
