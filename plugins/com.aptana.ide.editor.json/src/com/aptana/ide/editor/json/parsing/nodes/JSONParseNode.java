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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.editor.json.parsing.nodes;

import com.aptana.ide.editor.json.parsing.JSONMimeType;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.nodes.ParseNodeBase;

/**
 * @author Kevin Lindsey
 */
public class JSONParseNode extends ParseNodeBase
{
	/**
	 * JSONParseNode
	 * 
	 * @param typeIndex
	 * @param startLexeme
	 */
	public JSONParseNode(int typeIndex, Lexeme startLexeme)
	{
		super(typeIndex, JSONMimeType.MimeType, startLexeme);
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.ParseNodeBase#getName()
	 */
	public String getName()
	{
		String result = null;
		
		switch (this.getTypeIndex())
		{
			case JSONParseNodeTypes.ARRAY:
				result = "ARRAY"; //$NON-NLS-1$
				break;
				
			case JSONParseNodeTypes.OBJECT:
				result = "OBJECT"; //$NON-NLS-1$
				break;
				
			case JSONParseNodeTypes.SCALAR:
				result = "SCALAR"; //$NON-NLS-1$
				break;
				
			case JSONParseNodeTypes.NAME_VALUE_PAIR:
				result = "NAME_VALUE_PAIR"; //$NON-NLS-1$
				break;
		}
		
		if (result == null)
		{
			result = super.getName();
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.ParseNodeBase#getSource(com.aptana.ide.io.SourceWriter)
	 */
	public void getSource(SourceWriter writer)
	{
		writer.printIndent();
		
		switch (this.getTypeIndex())
		{
			case JSONParseNodeTypes.ARRAY:
				writer.println("[").increaseIndent(); //$NON-NLS-1$
				
				if (this.getChildCount() > 0)
				{
					this.getChild(0).getSource(writer);
					
					for (int i = 1; i < this.getChildCount(); i++)
					{
						writer.println(","); //$NON-NLS-1$
						this.getChild(i).getSource(writer);
					}
				}
				
				writer.decreaseIndent().println();
				writer.printWithIndent("]"); //$NON-NLS-1$
				break;
				
			case JSONParseNodeTypes.OBJECT:
				writer.println("{").increaseIndent(); //$NON-NLS-1$
				
				if (this.getChildCount() > 0)
				{
					this.getChild(0).getSource(writer);
					
					for (int i = 1; i < this.getChildCount(); i++)
					{
						writer.println(","); //$NON-NLS-1$
						this.getChild(i).getSource(writer);
					}
				}
				
				writer.decreaseIndent().println();
				writer.printWithIndent("}"); //$NON-NLS-1$
				break;
				
			case JSONParseNodeTypes.SCALAR:
				writer.print(this.getText());
				break;
				
			case JSONParseNodeTypes.NAME_VALUE_PAIR:
				writer.print(this.getChild(0).getText());
				writer.print(" : "); //$NON-NLS-1$
				writer.print(this.getChild(1).getText());
				break;
		}
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.ParseNodeBase#getText()
	 */
	public String getText()
	{
		String result = null;
		
		switch (this.getTypeIndex())
		{
			case JSONParseNodeTypes.SCALAR:
				result = this.getStartingLexeme().getText();
				break;
				
			default:
				result = super.getText();
		}
		
		return result;
	}
}
