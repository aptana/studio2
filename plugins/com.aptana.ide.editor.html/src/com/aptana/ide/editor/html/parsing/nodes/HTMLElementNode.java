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
package com.aptana.ide.editor.html.parsing.nodes;

import org.eclipse.ui.IEditorInput;

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.nodes.IParseNodeAttribute;
import com.aptana.ide.parsing.nodes.QuoteType;
import com.aptana.ide.views.outline.IResolvableItem;

/**
 * @author Kevin Lindsey
 */
public class HTMLElementNode extends HTMLParseNode implements IResolvableItem
{
	// moving this to top level for ease of use
	private String _id = ""; //$NON-NLS-1$
	private String _cssClass = ""; //$NON-NLS-1$
	private String _style = ""; //$NON-NLS-1$
	private boolean _isClosed;
	private IEditorInput input;

	
	/**
	 * @param startLexeme
	 */
	public HTMLElementNode(Lexeme startLexeme)
	{
		super(HTMLParseNodeTypes.ELEMENT, startLexeme);

		this.setName(startLexeme.getText().substring(1));
		this.setIsClosed(false);
	}
	
	/**
	 * getCssClass
	 * 
	 * @return String
	 */
	public String getCSSClass()
	{
		return this._cssClass;
	}

	/**
	 * getID
	 * 
	 * @return String
	 */
	public String getID()
	{
		return this._id;
	}

	/**
	 * getStyle
	 * 
	 * @return String
	 */
	public String getStyle()
	{
		return this._style;
	}

	/**
	 * getText
	 * 
	 * @return String
	 */
	public String getText()
	{
		return this.getName();
	}
	
	/**
	 * isClosed
	 *
	 * @return boolean
	 */
	public boolean isClosed()
	{
		return this._isClosed;
	}
	
	/**
	 * setIsClosed
	 *
	 * @param value
	 */
	public void setIsClosed(boolean value)
	{
		this._isClosed = value;
	}

	/**
	 * setAttribute
	 * 
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, String value)
	{
		name = name.toLowerCase();

		super.setAttribute(name, value);

		if (name.equals("id")) //$NON-NLS-1$
		{
			this._id = value;
		}
		else if (name.equals("class")) //$NON-NLS-1$
		{
			this._cssClass = value;
		}
		else if (name.equals("style")) //$NON-NLS-1$
		{
			this._style = value;
		}
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.ParseNodeBase#getAttributeNode(java.lang.String)
	 */
	public IParseNodeAttribute getAttributeNode(String attributeName)
	{
		return super.getAttributeNode(attributeName.toLowerCase());
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.ParseNodeBase#getSource(com.aptana.ide.io.SourceWriter)
	 */
	public void getSource(SourceWriter writer)
	{
		// begin element
		writer.printWithIndent("<").print(this.getName()); //$NON-NLS-1$

		// output attributes
		IParseNodeAttribute[] attrs = this.getAttributes();
		
		for (int i = 0; i < attrs.length; i++)
		{
			writer.print(" "); //$NON-NLS-1$
			attrs[i].getSource(writer);
		}

		// handle possible child elements
		if (this.hasChildren())
		{
			writer.println(">"); //$NON-NLS-1$

			writer.increaseIndent();

			for (int i = 0; i < this.getChildCount(); i++)
			{
				this.getChild(i).getSource(writer);
			}

			writer.decreaseIndent();

			writer.printWithIndent("</").print(this.getName()).println(">"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			if (this.hasAttributes())
			{
				IParseNodeAttribute lastAttr = attrs[attrs.length - 1];

				if (lastAttr.getQuoteType() == QuoteType.NONE)
				{
					writer.print(" "); //$NON-NLS-1$
				}
			}

			writer.println("/>"); //$NON-NLS-1$
		}
	}

	/**
	 * @see com.aptana.ide.views.outline.IResolvableItem#getEditorInput()
	 */
	public IEditorInput getEditorInput()
	{
		return input;
	}

	/**
	 * @see com.aptana.ide.views.outline.IResolvableItem#isResolvable()
	 */
	public boolean isResolvable()
	{
		return input!=null;
	}
	
	/**
	 * 
	 * @param input 
	 */
	public void setResolveInformation(IEditorInput input)
	{
		this.input=input;
	}

	/**
	 * @see com.aptana.ide.views.outline.IResolvableItem#stillHighlight()
	 */
	public boolean stillHighlight()
	{
		return true;
	}

	/**
	 * @see com.aptana.ide.views.outline.IResolvableItem#getParentItem()
	 */
	public IResolvableItem getParentItem()
	{
		return null;
	}
	
	
}

