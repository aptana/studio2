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
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class JSNaryNode extends JSParseNode
{
	/**
	 * JSNaryNode
	 * 
	 * @param typeIndex
	 * @param startLexeme
	 */
	public JSNaryNode(int typeIndex, Lexeme startLexeme)
	{
		super(typeIndex, startLexeme);
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.nodes.JSParseNode#getSource(com.aptana.ide.io.SourceWriter)
	 */
	public void getSource(SourceWriter writer)
	{
		switch (this.getTypeIndex())
		{
			case JSParseNodeTypes.ARGUMENTS:
				this.getSourceCommon(writer);
				break;

			case JSParseNodeTypes.ARRAY_LITERAL:
				writer.print("["); //$NON-NLS-1$
				this.getSourceCommon(writer);
				writer.print("]"); //$NON-NLS-1$
				break;

			case JSParseNodeTypes.COMMA:
				this.getSourceCommon(writer);
				break;

			case JSParseNodeTypes.DEFAULT:
				writer.println("default:"); //$NON-NLS-1$

				writer.increaseIndent();
				for (int i = 0; i < this.getChildCount(); i++)
				{
					JSParseNode statement = (JSParseNode) this.getChild(i);

					writer.printIndent();
					statement.getSource(writer);

					if (statement.getIncludesSemicolon())
					{
						writer.println(";"); //$NON-NLS-1$
					}
					else
					{
						writer.println();
					}
				}
				writer.decreaseIndent();
				break;

			case JSParseNodeTypes.OBJECT_LITERAL:
				writer.print("{"); //$NON-NLS-1$

				if (this.getChildCount() > 0)
				{
					writer.println();
					writer.increaseIndent();

					JSParseNode element = (JSParseNode) this.getChild(0);

					writer.printIndent();
					element.getSource(writer);

					for (int i = 1; i < this.getChildCount(); i++)
					{
						writer.println(","); //$NON-NLS-1$
						element = (JSParseNode) this.getChild(i);
						writer.printIndent();
						element.getSource(writer);
					}

					writer.decreaseIndent();
					writer.println();
					writer.printWithIndent("}"); //$NON-NLS-1$
				}
				else
				{
					writer.print("}"); //$NON-NLS-1$
				}
				break;

			case JSParseNodeTypes.PARAMETERS:
				this.getSourceCommon(writer);
				break;

			case JSParseNodeTypes.STATEMENTS:

				if (this.isTopLevel() == false)
				{
					writer.print("{"); //$NON-NLS-1$

					if (this.getChildCount() > 0)
					{
						writer.println();
						writer.increaseIndent();
					}
				}

				for (int i = 0; i < this.getChildCount(); i++)
				{
					IParseNode node = this.getChild(i);
					
					if (node instanceof JSParseNode)
					{
						JSParseNode statement = (JSParseNode) this.getChild(i);

						writer.printIndent();
						statement.getSource(writer);

						if (statement.getIncludesSemicolon())
						{
							writer.println(";"); //$NON-NLS-1$
						}
						else
						{
							writer.println();
						}
					}
					else
					{
						node.getSource(writer);
					}
				}

				if (this.isTopLevel() == false)
				{
					if (this.getChildCount() > 0)
					{
						writer.decreaseIndent();
						writer.printWithIndent("}"); //$NON-NLS-1$
					}
					else
					{
						writer.print("}"); //$NON-NLS-1$
					}
				}
				break;

			case JSParseNodeTypes.VAR:
				writer.print("var "); //$NON-NLS-1$
				this.getSourceCommon(writer);
				break;
				
			default:
				break;				
		}
	}

	/**
	 * convenience code for writing most of the binary operator types
	 * 
	 * @param writer
	 */
	private void getSourceCommon(SourceWriter writer)
	{
		if (this.getChildCount() > 0)
		{
			JSParseNode element = (JSParseNode) this.getChild(0);

			element.getSource(writer);

			for (int i = 1; i < this.getChildCount(); i++)
			{
				element = (JSParseNode) this.getChild(i);

				writer.print(", "); //$NON-NLS-1$
				element.getSource(writer);
			}
		}
	}
	
	/**
	 * isTopLevel
	 *
	 * @return boolean
	 */
	public boolean isTopLevel()
	{
		IParseNode parent = this.getParent();

		return (parent == null || parent.getLanguage() != this.getLanguage());
	}

	/**
	 * @see com.aptana.ide.editor.js.parsing.nodes.JSParseNode#isNavigatable()
	 */
	@Override
	public boolean isNavigatable()
	{		
		return isTopLevel();
	}
}
