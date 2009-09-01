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
package com.aptana.ide.scripting.parsing;

import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.html.parsing.nodes.HTMLSpecialNode;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.xpath.ParseNodeXPath;
import com.aptana.ide.scripting.ScriptingPlugin;

/**
 * @author Paul Colton
 */
public class ParseNode extends ScriptableObject
{
	private static final long serialVersionUID = 5067105792229240288L;

	private com.aptana.ide.parsing.nodes.IParseNode _node;

	/**
	 * ParseNode
	 * 
	 * @param scope
	 * @param node
	 */
	public ParseNode(Scriptable scope, IParseNode node)
	{
		if (node == null)
		{
			throw new IllegalArgumentException(Messages.ParseNode_Node_Undefined);
		}

		// assign Object.prototype as our internal prototype
		Scriptable object = (Scriptable) scope.get("Object", scope); //$NON-NLS-1$
		this.setPrototype(object.getPrototype());

		// place this node into a scope
		this.setParentScope(scope);

		// save a reference to the live node
		this._node = node;

		// setup JS-accessible properties
		String[] names = new String[] { "evaluate", "getAttribute", "hasAttribute", "getChild" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		this.defineFunctionProperties(names, ParseNode.class, READONLY | PERMANENT);

		this.defineProperty("endOffset", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("childCount", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("children", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("iconBaseName", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("language", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("name", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("parent", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("source", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("startOffset", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("typeIndex", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("text", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("xml", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$

		// TODO: It's best to avoid exposing the underlying java objects here, so we should remove this when it is no
		// longer needed
		this.defineProperty("node", ParseNode.class, READONLY | PERMANENT); //$NON-NLS-1$
	}

	/**
	 * evaluate
	 * 
	 * @param xpathExpr
	 * @return Object
	 */
	public Object evaluate(String xpathExpr)
	{
		Scriptable scope = this.getParentScope();
		List xpathResult = null;

		try
		{
			XPath xpath = new ParseNodeXPath(xpathExpr);

			xpathResult = (List) xpath.evaluate(this._node);
		}
		catch (JaxenException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ParseNode_Error, e);
		}

		if (xpathResult != null)
		{

			Object[] elements = new Object[xpathResult.size()];

			for (int i = 0; i < xpathResult.size(); i++)
			{
				elements[i] = new ParseNode(scope, (IParseNode) xpathResult.get(i));
			}

			return Context.getCurrentContext().newArray(scope, elements);
		}
		else
		{
			return Context.getCurrentContext().newArray(scope, new Object[0]);
		}
	}

	/**
	 * getAttribute
	 * 
	 * @param name
	 * @return String
	 */
	public String getAttribute(String name)
	{
		return this._node.getAttribute(name);
	}

	/**
	 * getChild
	 * 
	 * @param index
	 * @return Scriptable
	 */
	public Scriptable getChild(int index)
	{
		Scriptable scope = this.getParentScope();

		return new ParseNode(scope, this._node.getChild(index));
	}

	/**
	 * getChildCount
	 * 
	 * @return int
	 */
	public int getChildCount()
	{
		return this._node.getChildCount();
	}

	/**
	 * getChildren
	 * 
	 * @return Scriptable
	 */
	public Scriptable getChildren()
	{
		Scriptable scope = this.getParentScope();

		com.aptana.ide.parsing.nodes.IParseNode[] nodes = this._node.getChildren();

		if (nodes == null)
		{
			return Context.getCurrentContext().newArray(scope, new Object[0]);
		}
		else
		{
			Object[] elements = new Object[nodes.length];

			for (int i = 0; i < nodes.length; i++)
			{
				elements[i] = new ParseNode(scope, nodes[i]);
			}

			return Context.getCurrentContext().newArray(scope, elements);
		}
	}

	/**
	 * @see org.mozilla.javascript.Scriptable#getClassName()
	 */
	public String getClassName()
	{
		return "ParseNode"; //$NON-NLS-1$
	}

	/**
	 * getEndOffset
	 * 
	 * @return int
	 */
	public int getEndOffset()
	{
		return this._node.getEndingOffset();
	}

	/**
	 * getIconBaseName
	 *
	 * @return String
	 */
	public String getIconBaseName()
	{
		String result = ""; //$NON-NLS-1$
		
		if (this._node instanceof HTMLSpecialNode)
		{
			HTMLSpecialNode node = (HTMLSpecialNode) this._node;
			
			result = node.getIconBaseName();
		}
		
		return result;
	}

	/**
	 * getLanguage
	 * 
	 * @return String
	 */
	public String getLanguage()
	{
		return this._node.getLanguage();
	}

	/**
	 * getName
	 * 
	 * @return String
	 */
	public String getName()
	{
		return this._node.getName();
	}

	/**
	 * getNode
	 * 
	 * @return com.aptana.ide.parsing.ParseNode
	 */
	public IParseNode getNode()
	{
		return this._node;
	}

	/**
	 * getParent
	 * 
	 * @return Object
	 */
	public Object getParent()
	{
		Scriptable scope = this.getParentScope();
		com.aptana.ide.parsing.nodes.IParseNode parent = this._node.getParent();

		if (parent == null)
		{
			return Context.getUndefinedValue();
		}
		else
		{
			return new ParseNode(scope, parent);
		}
	}

	/**
	 * getSource
	 * 
	 * @return String
	 */
	public String getSource()
	{
		return this._node.getSource();
	}

	/**
	 * getStartOffset
	 * 
	 * @return int
	 */
	public int getStartOffset()
	{
		return this._node.getStartingOffset();
	}

	/**
	 * getText
	 * 
	 * @return String
	 */
	public String getText()
	{
		return this._node.getText();
	}

	/**
	 * getTypeIndex
	 * 
	 * @return int
	 */
	public int getTypeIndex()
	{
		return this._node.getTypeIndex();
	}

	/**
	 * getXml
	 * 
	 * @return String
	 */
	public String getXml()
	{
		return this._node.getXML();
	}

	/**
	 * hasAttribute
	 * 
	 * @param name
	 * @return boolean
	 */
	public boolean hasAttribute(String name)
	{
		return this._node.hasAttribute(name);
	}
}
