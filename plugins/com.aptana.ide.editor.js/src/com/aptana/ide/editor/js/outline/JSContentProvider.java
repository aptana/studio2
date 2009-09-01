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
package com.aptana.ide.editor.js.outline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.parsing.nodes.JSParseNodeTypes;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class JSContentProvider implements ITreeContentProvider
{
	private static final String CONTAINER_TYPE = "/"; //$NON-NLS-1$
	private static final String PROPERTY_TYPE = "."; //$NON-NLS-1$

	private static final Set<String> CLASS_EXTENDERS;

	private Map<String,JSOutlineItem> _itemsByScope;

	/**
	 * static constructor
	 */
	static
	{
		CLASS_EXTENDERS = new HashSet<String>();
		
		CLASS_EXTENDERS.add("dojo.lang.extend"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("Ext.extend"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("jQuery.extend"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("MochiKit.Base.update"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("Object.extend"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("qx.Class.define"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("qx.Interface.define"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("qx.Theme.define"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("qx.Mixin.define"); //$NON-NLS-1$
	}

	/**
	 * JSContentProvider
	 */
	public JSContentProvider()
	{
		this._itemsByScope = new HashMap<String,JSOutlineItem>();
	}

	/**
	 * addValue
	 * 
	 * @param reference
	 * @param value
	 * @param parent
	 */
	private void addValue(List<JSOutlineItem> elements, Reference reference, IParseNode value)
	{
		this.addValue(elements, reference, value, null);
	}

	/**
	 * addValue
	 * 
	 * @param reference
	 * @param value
	 * @param parent
	 */
	private void addValue(List<JSOutlineItem> elements, Reference reference, IParseNode value, IParseNode parent)
	{
		boolean processed = false;

		switch (value.getTypeIndex())
		{
			case JSParseNodeTypes.FUNCTION:
				this.processFunction(elements, value, reference);
				processed = true;
				break;

			case JSParseNodeTypes.INVOKE:
				IParseNode child = value.getChild(0);

				if (child.getTypeIndex() == JSParseNodeTypes.FUNCTION)
				{
					this.processFunction(elements, child, reference);
					processed = true;
				}
				else
				{
					value = child;
				}
				break;

			default:
				break;
		}

		if (processed == false)
		{
			int type = this.getOutlineType(value);
			int count = 0;

			if (value.getTypeIndex() == JSParseNodeTypes.OBJECT_LITERAL)
			{
				count = value.getChildCount();
			}

			if (parent == null)
			{
				parent = value.getParent();
			}

			// keep track of this item's scope so we can add virtual children later, if needed
			String path = reference.toString();

			if (this._itemsByScope.containsKey(path) == false)
			{
				JSOutlineItem item = new JSOutlineItem(reference.getName(), type, parent, value, count);

				this._itemsByScope.put(path, item);

				elements.add(item);
			}
		}
	}

	/**
	 * addVirtualChild
	 * 
	 * @param elements
	 * @param reference
	 * @param node
	 * @param target
	 */
	private void addVirtualChild(List<JSOutlineItem> elements, Reference reference, IParseNode node, IParseNode target)
	{
		String key = reference.getScope();
		JSOutlineItem item;

		if (this._itemsByScope.containsKey(key))
		{
			item = this._itemsByScope.get(key);
		}
		else
		{
			// get outline node type
			int type = (node.getTypeIndex() == JSParseNodeTypes.FUNCTION) ? JSOutlineItemType.FUNCTION
					: JSOutlineItemType.PROPERTY;

			// create outline item
			item = new JSOutlineItem(node.getText(), type, node, node);

			// cache associated by scope
			this._itemsByScope.put(key, item);

			// add item to our result list
			elements.add(item);
		}

		item.addVirtualChild(target);
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * getChildCount
	 * 
	 * @param node
	 * @return child count
	 */
	private int getChildCount(IParseNode node)
	{
		int result = 0;

		for (int i = 0; i < node.getChildCount(); i++)
		{
			IParseNode child = node.getChild(i);

			switch (child.getTypeIndex())
			{
				case JSParseNodeTypes.ASSIGN:
					IParseNode lhs = child.getChild(0);
					IParseNode rhs = child.getChild(1);
					int lhsTypeIndex = lhs.getTypeIndex();
					int rhsTypeIndex = rhs.getTypeIndex();

					boolean identifierOrProperty = (lhsTypeIndex == JSParseNodeTypes.IDENTIFIER || lhsTypeIndex == JSParseNodeTypes.GET_PROPERTY);
					boolean ofInterest = (rhsTypeIndex == JSParseNodeTypes.FUNCTION
							|| rhsTypeIndex == JSParseNodeTypes.OBJECT_LITERAL || rhsTypeIndex == JSParseNodeTypes.INVOKE);

					// if (identifierOrProperty) // && rhs.getTypeIndex() == JSParseNodeTypes.FUNCTION)
					if (identifierOrProperty && ofInterest)
					{
						result++;
					}
					break;

				case JSParseNodeTypes.FUNCTION:
				case JSParseNodeTypes.VAR:
					result++;
					break;

				case JSParseNodeTypes.IF:
				case JSParseNodeTypes.TRY:
				case JSParseNodeTypes.CATCH:
				case JSParseNodeTypes.STATEMENTS:
					result += this.getChildCount(child);
					break;

				case JSParseNodeTypes.RETURN:
					if (child.getChildCount() > 0)
					{
						IParseNode grandchild = child.getChild(0);

						if (grandchild.getTypeIndex() == JSParseNodeTypes.OBJECT_LITERAL)
						{
							result++;
						}
					}
					break;

				case JSParseNodeTypes.INVOKE:
					if (child.getChildCount() > 0)
					{
						IParseNode grandchild = child.getChild(0);

						if (grandchild.getTypeIndex() == JSParseNodeTypes.FUNCTION)
						{
							result++;
						}
					}
					break;

				default:
					break;
			}
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		Object[] result = null;

		if (parentElement instanceof JSOutlineItem)
		{
			JSOutlineItem item = (JSOutlineItem) parentElement;
			Object[] elements = this.getElements(item.getReferenceNodes());
			
			IEditorInput input=item.getEditorInput();
			if (input != null)
			{
				for (int a = 0; a < elements.length; a++)
				{
					Object object = elements[a];
					// patching path if needed setting it from parent
					if (object instanceof JSOutlineItem)
					{
						JSOutlineItem element = (JSOutlineItem) object;
						if (element.getEditorInput() == null)
						{
							element.setResolveInformation(input);
							element.setParent(item);
						}
					}
				}
			}
			result = elements;
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		List<JSOutlineItem> elements = new ArrayList<JSOutlineItem>();

		if (inputElement instanceof IParseNode[])
		{
			IParseNode[] nodes = (IParseNode[]) inputElement;

			for (int i = 0; i < nodes.length; i++)
			{
				this.processNode(elements, nodes[i]);
			}
		}
		else if (inputElement instanceof IParseNode)
		{
			this._itemsByScope.clear();

			// process node
			this.processNode(elements, (IParseNode) inputElement);
		}

		return elements.toArray(new Object[elements.size()]);
	}

	/**
	 * getOutlineType
	 * 
	 * @param node
	 * @return
	 */
	private int getOutlineType(IParseNode node)
	{
		int result;

		switch (node.getTypeIndex())
		{
			case JSParseNodeTypes.ARRAY_LITERAL:
				result = JSOutlineItemType.ARRAY;
				break;

			case JSParseNodeTypes.TRUE:
			case JSParseNodeTypes.FALSE:
				result = JSOutlineItemType.BOOLEAN;
				break;

			case JSParseNodeTypes.FUNCTION:
				result = JSOutlineItemType.FUNCTION;
				break;

			case JSParseNodeTypes.NULL:
				result = JSOutlineItemType.NULL;
				break;

			case JSParseNodeTypes.NUMBER:
				result = JSOutlineItemType.NUMBER;
				break;

			case JSParseNodeTypes.OBJECT_LITERAL:
				result = JSOutlineItemType.OBJECT_LITERAL;
				break;

			case JSParseNodeTypes.REGULAR_EXPRESSION:
				result = JSOutlineItemType.REGEX;
				break;

			case JSParseNodeTypes.STRING:
				result = JSOutlineItemType.STRING;
				break;

			default:
				result = JSOutlineItemType.PROPERTY;
				break;
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		Object result = null;

		if (element instanceof JSOutlineItem)
		{
			JSOutlineItem item = (JSOutlineItem) element;

			result = item.getReferenceNode().getParent();
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		boolean result = false;

		if (element instanceof JSOutlineItem)
		{
			JSOutlineItem item = (JSOutlineItem) element;

			result = (item.getChildCount() > 0);
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}

	/**
	 * processAssignNode
	 * 
	 * @param elements
	 * @param child
	 */
	private void processAssignment(List<JSOutlineItem> elements, IParseNode lhs, IParseNode rhs)
	{
		int rhsTypeIndex = rhs.getTypeIndex();

		switch (lhs.getTypeIndex())
		{
			case JSParseNodeTypes.STRING:
				if (rhsTypeIndex == JSParseNodeTypes.FUNCTION || rhsTypeIndex == JSParseNodeTypes.OBJECT_LITERAL)
				{
					String text = lhs.getSource();
					Reference reference = new Reference(lhs.getParent(), text.substring(1, text.length() - 1),
							CONTAINER_TYPE);
					this.addValue(elements, reference, rhs);
				}
				break;
			case JSParseNodeTypes.IDENTIFIER:
				if (rhsTypeIndex == JSParseNodeTypes.FUNCTION || rhsTypeIndex == JSParseNodeTypes.OBJECT_LITERAL)
				{
					Reference reference = new Reference(lhs.getParent(), lhs.getText(), CONTAINER_TYPE);

					this.addValue(elements, reference, rhs);
				}
				else if (rhsTypeIndex == JSParseNodeTypes.INVOKE && rhs.getChildCount() == 2)
				{
					IParseNode child = rhs.getChild(0);
					Reference reference = new Reference(lhs.getParent(), lhs.getText(), CONTAINER_TYPE);
					this.addValue(elements, reference, child);
				}
				break;

			case JSParseNodeTypes.GET_PROPERTY:
				IParseNode target = null;

				// travel down the left-side get-property nodes
				while (lhs.getTypeIndex() == JSParseNodeTypes.GET_PROPERTY)
				{
					target = lhs.getChild(1);
					lhs = lhs.getChild(0);
				}

				// only process get-property expressions that begin with an identifier or 'this'
				if (lhs.getTypeIndex() == JSParseNodeTypes.IDENTIFIER || lhs.getTypeIndex() == JSParseNodeTypes.THIS)
				{
					String scopeString = Reference.createScopeString(lhs.getParent());
					Reference reference;

					if (this._itemsByScope.containsKey(scopeString))
					{
						reference = new Reference(scopeString, target.getText(), CONTAINER_TYPE);
						addVirtualChild(elements, reference, lhs, target);
					}
					else
					{
						IParseNode node = lhs;

						reference = new Reference(node, lhs.getText(), CONTAINER_TYPE);

						this.addValue(elements, reference, target);

						JSOutlineItem item = this._itemsByScope.get(scopeString);

						item.addVirtualChild(target);
					}
				}
				break;

			default:
				break;
		}
	}

	/**
	 * processFunctionNode
	 * 
	 * @param elements
	 * @param node
	 */
	private void processFunction(List<JSOutlineItem> elements, IParseNode node, Reference reference)
	{
		IParseNode parameters = node.getChild(0);
		IParseNode body = node.getChild(1);
		String name;

		if (node.hasAttribute("name")) //$NON-NLS-1$
		{
			name = node.getAttribute("name"); //$NON-NLS-1$
		}
		else
		{
			if (reference != null)
			{
				name = reference.getName();
			}
			else
			{
				name = "<literal>"; //$NON-NLS-1$
			}
		}

		String label = name + "(" + parameters.getSource() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		int count = this.getChildCount(body);

		// keep track of this item's scope so we can add virtual children later, if needed
		if (reference == null)
		{
			reference = new Reference(node, name, CONTAINER_TYPE);
		}

		String fullpath = reference.toString();

		if (this._itemsByScope.containsKey(fullpath) == false)
		{
			JSOutlineItem item = new JSOutlineItem(label, JSOutlineItemType.FUNCTION, node, body, count);

			this._itemsByScope.put(fullpath, item);

			elements.add(item);
		}
		else
		{
		}
	}

	/**
	 * processIdentifier
	 * 
	 * @param elements
	 * @param node
	 */
	private void processIdentifier(List<JSOutlineItem> elements, IParseNode node)
	{
		IParseNode parent = node.getParent();

		if (parent.hasChildren())
		{
			IParseNode rhs = parent.getChild(1);

			if (rhs == node)
			{
				// get grandparent
				IParseNode grandparent = parent.getParent();
				Reference reference;

				if (grandparent != null)
				{
					IParseNode target = grandparent.getChild(1);

					switch (grandparent.getTypeIndex())
					{
						case JSParseNodeTypes.ARGUMENTS:
							// support dojo.lang.extend, MochiKit.Base.update, and Object.extend
							target = grandparent.getChild(grandparent.getChildCount() - 1);
							reference = new Reference(parent, rhs.getText(), PROPERTY_TYPE);

							String parentFullPath = reference.toString();

							// process all key/value pairs
							for (int i = 0; i < target.getChildCount(); i++)
							{
								IParseNode keyValuePair = target.getChild(i);
								String keyString = keyValuePair.getChild(0).getSource();
								Reference keyValueReference = new Reference(parentFullPath, keyString, PROPERTY_TYPE);

								this.addVirtualChild(elements, keyValueReference, node, keyValuePair);
							}

							break;

						case JSParseNodeTypes.ASSIGN:
							reference = new Reference(parent, rhs.getText(), PROPERTY_TYPE);
							this.addValue(elements, reference, target);
							break;

						case JSParseNodeTypes.GET_PROPERTY:
							IParseNode property = grandparent.getChild(1);

							reference = new Reference(grandparent, property.getText(), PROPERTY_TYPE);

							this.addVirtualChild(elements, reference, node, target);
							break;

						default:
							break;
					}
				}
			}
		}
	}

	/**
	 * processInvoke
	 * 
	 * @param elements
	 * @param node
	 */
	private void processInvoke(List<JSOutlineItem> elements, IParseNode node)
	{
		IParseNode lhs = node.getChild(0);
		String source = lhs.getSource();

		if (CLASS_EXTENDERS.contains(source))
		{
			IParseNode args = node.getChild(1);

			if (args.getTypeIndex() == JSParseNodeTypes.ARGUMENTS && args.getChildCount() == 2)
			{
				IParseNode arg1 = args.getChild(0);
				IParseNode arg2 = args.getChild(1);

				if (arg2.getTypeIndex() == JSParseNodeTypes.OBJECT_LITERAL)
				{
					switch (arg1.getTypeIndex())
					{
						case JSParseNodeTypes.STRING:
						case JSParseNodeTypes.IDENTIFIER:
						case JSParseNodeTypes.GET_PROPERTY:
							this.processAssignment(elements, arg1, arg2);
							break;

						default:
							break;
					}
				}
			}
			else if (args.getTypeIndex() == JSParseNodeTypes.ARGUMENTS && args.getChildCount() == 3)
			{
				// EXT case
				IParseNode arg1 = args.getChild(0);
				IParseNode arg3 = args.getChild(2);

				if (arg3.getTypeIndex() == JSParseNodeTypes.OBJECT_LITERAL)
				{
					switch (arg1.getTypeIndex())
					{
						case JSParseNodeTypes.STRING:
						case JSParseNodeTypes.IDENTIFIER:
						case JSParseNodeTypes.GET_PROPERTY:
							this.processAssignment(elements, arg1, arg3);
							break;

						default:
							break;
					}
				}
			}
		}
		else if (lhs.getTypeIndex() == JSParseNodeTypes.GROUP)
		{
			// See if we are in a self-invoking function
			IParseNode[] nodes = lhs.getChildren();
			for (int i = 0; i < nodes.length; i++)
			{
				IParseNode node2 = nodes[i];
				if (node2.getTypeIndex() == JSParseNodeTypes.FUNCTION && !node.hasAttribute("name")) //$NON-NLS-1$
				{
					IParseNode[] grandChildren = node2.getChildren();
					for (int j = 0; j < grandChildren.length; j++)
					{
						IParseNode node3 = grandChildren[j];
						this.processNode(elements, node3);
					}
				}
				else
				{
					this.processNode(elements, node2);
				}
			}
		}
	}

	/**
	 * processNameValuePair
	 * 
	 * @param elements
	 * @param node
	 */
	private void processNameValuePair(List<JSOutlineItem> elements, IParseNode node)
	{
		IParseNode property = node.getChild(0);
		IParseNode value = node.getChild(1);
		String name = property.getText();
		int type = this.getOutlineType(value);

		if (property.getTypeIndex() == JSParseNodeTypes.STRING)
		{
			name = name.substring(1, name.length());
		}

		switch (value.getTypeIndex())
		{
			case JSParseNodeTypes.FUNCTION:
				this.processFunction(elements, value, new Reference(value, name, "")); //$NON-NLS-1$
				break;

			case JSParseNodeTypes.OBJECT_LITERAL:
				elements.add(new JSOutlineItem(name, type, node, value, value.getChildCount()));
				break;

			default:
				elements.add(new JSOutlineItem(name, type, node, value));
				break;
		}
	}

	/**
	 * processNode
	 * 
	 * @param elements
	 * @param node
	 */
	private void processNode(List<JSOutlineItem> elements, IParseNode node)
	{
		try
		{
			switch (node.getTypeIndex())
			{
				case JSParseNodeTypes.ASSIGN:
					this.processAssignment(elements, node.getChild(0), node.getChild(1));
					break;

				case JSParseNodeTypes.FUNCTION:
					this.processFunction(elements, node, null);
					break;

				case JSParseNodeTypes.GROUP:
					if (node.getChildCount() > 0)
					{
						this.processNode(elements, node.getChild(0));
					}
					break;

				case JSParseNodeTypes.IDENTIFIER:
					this.processIdentifier(elements, node);
					break;

				case JSParseNodeTypes.INVOKE:
					this.processInvoke(elements, node);
					break;

				case JSParseNodeTypes.NAME_VALUE_PAIR:
					this.processNameValuePair(elements, node);
					break;

				case JSParseNodeTypes.OBJECT_LITERAL:
					for (int i = 0; i < node.getChildCount(); i++)
					{
						this.processNode(elements, node.getChild(i));
					}
					break;

				case JSParseNodeTypes.RETURN:
					if (node.getChildCount() > 0)
					{
						IParseNode child = node.getChild(0);
						if (child.getTypeIndex() == JSParseNodeTypes.OBJECT_LITERAL)
						{
							for (int i = 0; i < child.getChildCount(); i++)
							{
								this.processNode(elements, child.getChild(i));
							}
						}
					}
					break;

				case JSParseNodeTypes.STATEMENTS:
					// process named functions first
					for (int i = 0; i < node.getChildCount(); i++)
					{
						IParseNode child = node.getChild(i);

						if (child.getTypeIndex() == JSParseNodeTypes.FUNCTION && child.hasAttribute("name")) //$NON-NLS-1$
						{
							this.processNode(elements, child);
						}
					}

					// process var declarations
					for (int i = 0; i < node.getChildCount(); i++)
					{
						IParseNode child = node.getChild(i);

						if (child.getTypeIndex() == JSParseNodeTypes.VAR)
						{
							this.processNode(elements, child);
						}
					}

					// process var assignments, identifiers, and name/value pairs
					for (int i = 0; i < node.getChildCount(); i++)
					{
						IParseNode child = node.getChild(i);
						int typeIndex = child.getTypeIndex();

						if (typeIndex == JSParseNodeTypes.ASSIGN || typeIndex == JSParseNodeTypes.IDENTIFIER
								|| typeIndex == JSParseNodeTypes.NAME_VALUE_PAIR
								|| typeIndex == JSParseNodeTypes.INVOKE || typeIndex == JSParseNodeTypes.RETURN)
						{
							this.processNode(elements, child);
						}
					}

					// process if statements
					for (int i = 0; i < node.getChildCount(); i++)
					{
						IParseNode child = node.getChild(i);
						int typeIndex = child.getTypeIndex();

						if (typeIndex == JSParseNodeTypes.IF)
						{
							this.processNode(elements, child);
						}
					}

					// process try/catch statements
					for (int i = 0; i < node.getChildCount(); i++)
					{
						IParseNode child = node.getChild(i);
						int typeIndex = child.getTypeIndex();

						if (typeIndex == JSParseNodeTypes.TRY || typeIndex == JSParseNodeTypes.CATCH)
						{
							this.processNode(elements, child);
						}
					}

					break;

				case JSParseNodeTypes.IF:
				case JSParseNodeTypes.TRY:
				case JSParseNodeTypes.CATCH:
					for (int i = 0; i < node.getChildCount(); i++)
					{
						IParseNode child = node.getChild(i);
						this.processNode(elements, child);
					}

				case JSParseNodeTypes.THIS:
					this.processIdentifier(elements, node);
					break;

				case JSParseNodeTypes.VAR:
					this.processVar(elements, node);
					break;

				default:
					break;
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(JSPlugin.getDefault(), Messages.JSContentProvider_Error_Processing_Parse_Node, e);
		}
	}

	/**
	 * processVarNode
	 * 
	 * @param elements
	 * @param node
	 */
	private void processVar(List<JSOutlineItem> elements, IParseNode node)
	{
		// process all declarations
		for (int i = 0; i < node.getChildCount(); i++)
		{
			IParseNode declaration = node.getChild(i);
			IParseNode identifier = declaration.getChild(0);
			IParseNode assignedValue = declaration.getChild(1);

			if (assignedValue.getTypeIndex() != JSParseNodeTypes.EMPTY)
			{
				while (assignedValue.getTypeIndex() == JSParseNodeTypes.ASSIGN)
				{
					assignedValue = assignedValue.getChild(1);
				}
			}

			Reference reference = new Reference(node, identifier.getText(), CONTAINER_TYPE);

			this.addValue(elements, reference, assignedValue, node);
			// elements.add(new JSOutlineItem(identifier.getText(), JSOutlineItemType.PROPERTY, identifier,
			// assignedValue));
		}
	}
}
