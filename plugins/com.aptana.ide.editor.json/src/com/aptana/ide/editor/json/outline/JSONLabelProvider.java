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
package com.aptana.ide.editor.json.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.ide.editor.json.lexing.JSONTokenTypes;
import com.aptana.ide.editor.json.parsing.nodes.JSONParseNode;
import com.aptana.ide.editor.json.parsing.nodes.JSONParseNodeTypes;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.editor.json.Activator;

/**
 * @author Kevin Lindsey
 */
public class JSONLabelProvider extends LabelProvider
{
	private static final Image ARRAY_ICON = Activator.getImage("json/icons/array-literal.png"); //$NON-NLS-1$
	private static final Image BOOLEAN_ICON = Activator.getImage("json/icons/boolean.png"); //$NON-NLS-1$
	private static final Image NULL_ICON = Activator.getImage("json/icons/null.png"); //$NON-NLS-1$
	private static final Image NUMBER_ICON = Activator.getImage("json/icons/number.png"); //$NON-NLS-1$
	private static final Image OBJECT_ICON = Activator.getImage("json/icons/object-literal.png"); //$NON-NLS-1$
//	private static final Image REFERENCE_ICON = Activator.getImage("json/icons/reference.png"); //$NON-NLS-1$
	private static final Image STRING_ICON = Activator.getImage("json/icons/string.png"); //$NON-NLS-1$
	
	/**
	 * getImage
	 * 
	 * @return Image
	 */
	public Image getImage(Object element)
	{
		Image result = null;
		
		if (element instanceof JSONParseNode)
		{
			IParseNode node = (IParseNode) element;
			
			switch (node.getTypeIndex())
			{
				case JSONParseNodeTypes.ARRAY:
					result = ARRAY_ICON;
					break;
					
				case JSONParseNodeTypes.OBJECT:
					result = OBJECT_ICON;
					break;
					
				case JSONParseNodeTypes.NAME_VALUE_PAIR:
					IParseNode child = node.getChild(1);
					
					result = this.getImage(child);
					break;
					
				case JSONParseNodeTypes.SCALAR:
					Lexeme lexeme = node.getStartingLexeme();
					
					switch (lexeme.typeIndex)
					{
						case JSONTokenTypes.FALSE:
						case JSONTokenTypes.TRUE:
							result = BOOLEAN_ICON;
							break;
							
						case JSONTokenTypes.NULL:
							result = NULL_ICON;
							break;
							
						case JSONTokenTypes.NUMBER:
							result = NUMBER_ICON;
							break;
							
						case JSONTokenTypes.REFERENCE:
						case JSONTokenTypes.PROPERTY:
						case JSONTokenTypes.STRING:
							result = STRING_ICON;
							break;
					}
					break;
			}
		}
		
		if (result == null)
		{
			result = super.getImage(element);
		}
		
		return result;
	}
	
	/**
	 * getText
	 * 
	 * @return String
	 */
	public String getText(Object element)
	{
		String result = null;
		
		if (element instanceof JSONParseNode)
		{
			IParseNode node = (IParseNode) element;
			
			switch (node.getTypeIndex())
			{
				case JSONParseNodeTypes.ARRAY:
					result = "Array"; //$NON-NLS-1$
					break;
					
				case JSONParseNodeTypes.OBJECT:
					result = "Object"; //$NON-NLS-1$
					break;
					
				case JSONParseNodeTypes.NAME_VALUE_PAIR:
					String label = this.trimQuotes(node.getChild(0).getStartingLexeme().getText());
					
					IParseNode child = node.getChild(1);
					int type = child.getTypeIndex();
					
					if (type == JSONParseNodeTypes.ARRAY || type == JSONParseNodeTypes.OBJECT)
					{
						result = label;
					}
					else
					{
						result = label + " : " + this.getText(child); //$NON-NLS-1$
					}
					break;
					
				case JSONParseNodeTypes.SCALAR:
					Lexeme lexeme = node.getStartingLexeme();
					String text = lexeme.getText();
					
					switch (lexeme.typeIndex)
					{
						case JSONTokenTypes.FALSE:
						case JSONTokenTypes.TRUE:
						case JSONTokenTypes.NULL:
						case JSONTokenTypes.NUMBER:
							result = text;
							break;
							
						case JSONTokenTypes.REFERENCE:
						case JSONTokenTypes.PROPERTY:
						case JSONTokenTypes.STRING:
							result = this.trimQuotes(text);
							break;
					}
					break;
			}
		}
		
		if (result == null)
		{
			result = super.getText(element);
		}
		
		return result;
	}
	
	private String trimQuotes(String text)
	{
		if (text.startsWith("\"") || text.startsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			text = text.substring(1);
		}
		if (text.endsWith("\"") || text.endsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			text = text.substring(0, text.length() - 1);
		}
		
		return text;
	}
}
