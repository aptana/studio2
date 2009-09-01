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
package com.aptana.ide.parsing.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;

/**
 * @author Kevin Lindsey
 */
public class ParseNodeBase implements IParseNode
{
	private static final String SEPARATOR = "/"; //$NON-NLS-1$
	
	private String _name;
	private String _type;
	private int _typeIndex;
	private IParseNode _parent;
	private IParseNode[] _children;
	private int _size;
	private List<IParseNodeAttribute> _attributes;

	private String _language;
	private Lexeme _startingLexeme;
	private Lexeme _endingLexeme;

	/**
	 * Create a generic parse node
	 * 
	 * @param typeIndex
	 * @param language
	 */
	public ParseNodeBase(int typeIndex, String language)
	{
		this(Integer.toString(typeIndex), typeIndex, language, null, null);
	}
	
	/**
	 * Creates a new generic parse node
	 * 
	 * @param typeIndex
	 * @param language
	 * @param startingLexeme
	 */
	public ParseNodeBase(int typeIndex, String language, Lexeme startingLexeme)
	{
		this(Integer.toString(typeIndex), typeIndex, language, startingLexeme, startingLexeme);
	}
	
	/**
	 * Creates a new generic parse node
	 * 
	 * @param typeIndex
	 * @param language
	 * @param startingLexeme
	 */
	public ParseNodeBase(int typeIndex, String language, Lexeme startingLexeme, Lexeme endingLexeme)
	{
		this(Integer.toString(typeIndex), typeIndex, language, startingLexeme, endingLexeme);
	}
	
	/**
	 * ParseNodeBase
	 * 
	 * @param type
	 * @param typeIndex
	 * @param language
	 * @param startingLexeme
	 */
	public ParseNodeBase(String type, int typeIndex, String language, Lexeme startingLexeme)
	{
		this(type, typeIndex, language, startingLexeme, startingLexeme);
	}
	
	/**
	 * ParseNodeBase
	 * 
	 * @param type
	 * @param typeIndex
	 * @param language
	 * @param startingLexeme
	 */
	public ParseNodeBase(String type, int typeIndex, String language, Lexeme startingLexeme, Lexeme endingLexeme)
	{
		this._type = type;
		this._typeIndex = typeIndex;
		this._language = language;
		this._startingLexeme = startingLexeme;
		this._endingLexeme = endingLexeme;
		
		this._children = new IParseNode[0];
	}

	/**
	 * add
	 * 
	 * @param lexeme
	 */
	private void add(IParseNode node)
	{
		// make sure our private buffer is large enough
		int currentLength = this._children.length;
		int size = this._size + 1;
		
		// see if the index we want is within our buffer
		if (size > currentLength)
		{
			// it's not, add about 50% to our current buffer size
			int newLength = (currentLength * 3) / 2 + 1;

			// create a new empty list
			IParseNode[] newList = new IParseNode[newLength];
			
			// move the current contents to our new list
			System.arraycopy(this._children, 0, newList, 0, this._size);
			
			// set out current list to the new list
			this._children = newList;
		}
		
		// place the lexeme into the hole
		this._children[this._size] = node;
		
		// update the current size
		this._size++;
	}
	
	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#appendChild(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void appendChild(IParseNode child)
	{
		if (child == null)
		{
			throw new NullPointerException(Messages.ParseNodeBase_Undefined_Child);
		}

		if (child instanceof ParseFragment)
		{
			ParseFragment fragment = (ParseFragment) child;
			
			for (int i = 0; i < fragment.getChildCount(); i++)
			{
				this.appendChildHelper(fragment.getChild(i));
			}
			
			IParseNodeAttribute[] attributes = fragment.getAttributes();
			
			for (int i = 0; i < attributes.length; i++)
			{
				IParseNodeAttribute attr = attributes[i];
				
				this.setAttribute(attr.getName(), attr.getValue());
			}
		}
		else
		{
			this.appendChildHelper(child);
		}
	}

	/**
	 * appendChildHelper
	 *
	 * @param child
	 */
	private void appendChildHelper(IParseNode child)
	{
		// append child
		this.add(child);

		// propagate starting and ending offsets
		this.includeLexemesInRange(child.getStartingLexeme(), child.getEndingLexeme());
		
		// assign parent to child
		if (child instanceof ParseNodeBase)
		{
			((ParseNodeBase) child)._parent = this;
		}
	}
	
	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#containsOffset(int)
	 */
	public boolean containsOffset(int offset)
	{
		boolean result = false;

		if (this._startingLexeme != null && this._endingLexeme != null)
		{
			int startingOffset = this._startingLexeme.offset;
			int endingOffset = this._endingLexeme.getEndingOffset();

			result = (startingOffset <= offset && offset < endingOffset);
		}

		return result;
	}

	/**
	 * createAttribute
	 * 
	 * @param name
	 * @param value
	 * @return IParseNodeAttribute
	 */
	protected IParseNodeAttribute createAttribute(String name, String value)
	{
		return new ParseNodeAttribute(this, name, value);
	}
	
	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getAttribute(java.lang.String)
	 */
	public String getAttribute(String attributeName)
	{
		String result = ""; //$NON-NLS-1$

		if (this._attributes != null)
		{
			for (int i = 0; i < this._attributes.size(); i++)
			{
				IParseNodeAttribute attribute = this._attributes.get(i);

				if (attribute.getName().equals(attributeName))
				{
					result = attribute.getValue();
					break;
				}
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getAttributeNode(java.lang.String)
	 */
	public IParseNodeAttribute getAttributeNode(String attributeName)
	{
		IParseNodeAttribute result = null;

		if (this._attributes != null)
		{
			for (int i = 0; i < this._attributes.size(); i++)
			{
				IParseNodeAttribute attribute = this._attributes.get(i);

				if (attribute.getName().equals(attributeName))
				{
					result = attribute;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getAttributes()
	 */
	public IParseNodeAttribute[] getAttributes()
	{
		if (this._attributes != null)
		{
			return this._attributes.toArray(new IParseNodeAttribute[this._attributes.size()]);
		}
		else
		{
			return new IParseNodeAttribute[0];
		}
	}
	
	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getChild(int)
	 */
	public IParseNode getChild(int index)
	{
		IParseNode result = null;

		if (0 <= index && index < this._size)
		{
			result = this._children[index];
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getChildCount()
	 */
	public int getChildCount()
	{
		return this._size;
	}

	/**
	 * getChildIndex
	 *
	 * @return int
	 */
	public int getChildIndex()
	{
		IParseNode parent = this.getParent();
		int result = 0;
		
		if (parent != null)
		{
			for (int i = 0; i < parent.getChildCount(); i++)
			{
				if (parent.getChild(i) == this)
				{
					result = i;
					break;
				}
			}
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getChildren()
	 */
	public IParseNode[] getChildren()
	{
		IParseNode[] result = new IParseNode[this._size];

		if (this._size > 0)
		{
			System.arraycopy(this._children, 0, result, 0, this._size);
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getEndingLexeme()
	 */
	public Lexeme getEndingLexeme()
	{
		return this._endingLexeme;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getEndingOffset()
	 */
	public int getEndingOffset()
	{
		int result = -1;

		if (this._endingLexeme != null)
		{
			result = this._endingLexeme.getEndingOffset();
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getLanguage()
	 */
	public String getLanguage()
	{
		return this._language;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getLength()
	 */
	public int getLength()
	{
		return this.getEndingOffset() - this.getStartingOffset();
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getName()
	 */
	public String getName()
	{
		if (this._name == null)
		{
			// [KEL] temp for debugging
			if (this._startingLexeme != null)
			{
				this._name = this._startingLexeme.getText();
			}
			else
			{
				String name = this.getClass().getName();
				name = name.substring(name.lastIndexOf('.') + 1);
				this._name = name;
			}
		}
		
		return this._name;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getNodeAtOffset(int)
	 */
	public IParseNode getNodeAtOffset(int offset)
	{
		IParseNode result = null;
		
		if (this.containsOffset(offset))
		{
			result = this;

			for (int i = 0; i < this._size; i++)
			{
				IParseNode child = this._children[i];

				if (child.containsOffset(offset))
				{
					result = child.getNodeAtOffset(offset);
					break;
				}
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getParent()
	 */
	public IParseNode getParent()
	{
		return this._parent;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getPath()
	 */
	public String getPath()
	{
		List<String> parts = new ArrayList<String>();
		
		// walk the ancestor chain to the root node
		IParseNode current = this;
		
		while (current != null)
		{
			parts.add(current.getName());
			parts.add(SEPARATOR);
			
			current = current.getParent();
		}
		
		// reverse the list
		Collections.reverse(parts);
		
		// convert list into a string
		StringBuffer buffer = new StringBuffer();
		
		for (String item : parts)
		{
			buffer.append(item);
		}
		
		return buffer.toString();
	}

	/**
	 * Gets the root node of this node.
	 * 
	 * @return Returns the root node of this node.
	 */
	public IParseNode getRootNode()
	{
		IParseNode root = this;
		IParseNode p = this._parent;

		while (p != null)
		{
			root = p;
			p = p.getParent();
		}

		return root;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getSource()
	 */
	public String getSource()
	{
		SourceWriter writer = new SourceWriter();

		this.getSource(writer);

		return writer.toString();
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getSource(com.aptana.ide.io.SourceWriter)
	 */
	public void getSource(SourceWriter writer)
	{
		// do nothing
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getStartingLexeme()
	 */
	public Lexeme getStartingLexeme()
	{
		return this._startingLexeme;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getStartingOffset()
	 */
	public int getStartingOffset()
	{
		int result = -1;

		if (this._startingLexeme != null)
		{
			result = this._startingLexeme.offset;
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getText()
	 */
	public String getText()
	{
		return ""; //$NON-NLS-1$
	}

	/**
	 * getType
	 *
	 * @return String
	 */
	public String getType()
	{
		return this._type;
	}
	
	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getTypeIndex()
	 */
	public int getTypeIndex()
	{
		return this._typeIndex;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getUniquePath()
	 */
	public String getUniquePath()
	{
		List<String> parts = new ArrayList<String>();
		
		// walk the ancestor chain to the root node
		IParseNode current = this;
		
		while (current != null)
		{
			int index = current.getChildIndex() + 1;
			
			parts.add(current.getName() + "[" + index + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			parts.add(SEPARATOR);
			
			current = current.getParent();
		}
		
		// reverse the list
		Collections.reverse(parts);
		
		// convert list into a string
		StringBuffer buffer = new StringBuffer();
		
		for (String item : parts)
		{
			buffer.append(item);
		}
		
		return buffer.toString();
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getXML()
	 */
	public String getXML()
	{
		SourceWriter writer = new SourceWriter();

		this.getXML(writer);

		return writer.toString();
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getXML(com.aptana.ide.io.SourceWriter)
	 */
	public void getXML(SourceWriter writer)
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
				this.getChild(i).getXML(writer);
			}

			writer.decreaseIndent();

			writer.printWithIndent("</").print(this.getName()).println(">"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			writer.println("/>"); //$NON-NLS-1$
		}
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#hasAttribute(java.lang.String)
	 */
	public boolean hasAttribute(String attributeName)
	{
		boolean result = false;

		if (this._attributes != null)
		{
			for (int i = 0; i < this._attributes.size(); i++)
			{
				IParseNodeAttribute attribute = this._attributes.get(i);

				if (attribute.getName().equals(attributeName))
				{
					result = true;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#hasAttributes()
	 */
	public boolean hasAttributes()
	{
		return (this._attributes != null && this._attributes.size() > 0);
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#hasChildren()
	 */
	public boolean hasChildren()
	{
		return this._size > 0;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#includeLexemeInRange(com.aptana.ide.lexer.Lexeme)
	 */
	public boolean includeLexemeInRange(Lexeme lexeme)
	{
		return this.includeLexemesInRange(lexeme, lexeme);
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#includeLexemesInRange(com.aptana.ide.lexer.Lexeme, Lexeme)
	 */
	public boolean includeLexemesInRange(Lexeme startingLexeme, Lexeme endingLexeme)
	{
		boolean result = false;
		
		// NOTE: We have to be careful with the test that determines if the starting lexeme comes before
		// the ending lexeme. It's possible that startingLexeme and endingLexeme could reference the same
		// lexeme. In that case startingLexeme.getEndingOffset() <= endingLexeme.offset would fail since
		// a lexeme's ending offset cannot come before it's starting offset.
		if (startingLexeme != null && endingLexeme != null && startingLexeme.offset <= endingLexeme.offset)
		{
			if (this._startingLexeme == null || (startingLexeme.offset != -1 && startingLexeme.getEndingOffset() <= this._startingLexeme.offset))
			{
				this._startingLexeme = startingLexeme;
				result = true;
			}
	
			if (this._endingLexeme == null || (endingLexeme.offset != -1 && this._endingLexeme.getEndingOffset() <= endingLexeme.offset))
			{
				this._endingLexeme = endingLexeme;
				result = true;
			}
			
			if (result)
			{
				// propagate change to ancestors, as necessary
				IParseNode parent = this._parent;
				
				// NOTE: [KEL] would be faster to do this iteratively instead of recursively
				if (parent != null)
				{
					parent.includeLexemesInRange(startingLexeme, endingLexeme);
				}
			}
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#isEmpty()
	 */
	public boolean isEmpty()
	{
		return (this.getLength() == 0);
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#setAttribute(java.lang.String, java.lang.String)
	 */
	public void setAttribute(String name, String value)
	{
		if (name != null && name.length() > 0 && value != null)
		{
			boolean found = false;
	
			if (this._attributes != null)
			{
				for (int i = 0; i < this._attributes.size(); i++)
				{
					IParseNodeAttribute attribute = this._attributes.get(i);
	
					if (attribute.getName().equals(name))
					{
						attribute.setValue(value);
						found = true;
						break;
					}
				}
			}
			else
			{
				// make sure we have an attribute list
				this._attributes = new ArrayList<IParseNodeAttribute>();
			}
	
			if (found == false)
			{
				// create a new attribute
				IParseNodeAttribute attribute = this.createAttribute(name, value);
	
				// add the new attribute to our list
				this._attributes.add(attribute);
			}
		}
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#setEndingLexeme(com.aptana.ide.lexer.Lexeme)
	 * @deprecated
	 */
	public void setEndingLexeme(Lexeme endLexeme)
	{
		this.includeLexemeInRange(endLexeme);
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/**
	 * Allows to append child node before given node
	 * @param node
	 * @param child
	 */
	public void appendChildBefore(IParseNode node, IParseNode child) {
		if (this._children!=null)
		{
		ArrayList<IParseNode>list=new ArrayList<IParseNode>(Arrays.asList(this._children));
		
		int indexOf = list.indexOf(node);
		if (indexOf==-1)
		{
			appendChild(child);
			return;
		}
		else
		{
			ParseNodeBase base=(ParseNodeBase) child;
			base._parent=this;
			list.add(indexOf, child);
		}
		this._children=list.toArray(new IParseNode[list.size()]);
		this._size=this._size+1;
		}
		else{
			appendChild(child);
		}
	}

	/**
	 * allows to set children of node from a array
	 * @param array
	 */
	public void setChildren(IParseNode[] array) {
		this._children=array;
		this._size=array.length;
		for (IParseNode n: array){
			ParseNodeBase b=(ParseNodeBase) n;
			b._parent=this;
		}
	}
}
