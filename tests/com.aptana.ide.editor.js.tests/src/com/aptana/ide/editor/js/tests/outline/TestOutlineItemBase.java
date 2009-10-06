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
 */package com.aptana.ide.editor.js.tests.outline;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.outline.JSContentProvider;
import com.aptana.ide.editor.js.outline.JSOutlineItem;
import com.aptana.ide.editor.js.outline.JSOutlineItemType;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.parsing.JSParser;
import com.aptana.ide.editor.js.tests.TestingPlugin;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.sun.org.apache.xpath.internal.NodeSet;

/**
 * @author Kevin Lindsey
 */
public abstract class TestOutlineItemBase extends TestCase
{
	private JSParser _parser;
	private JSParseState _parseState;

	/**
	 * buildXML
	 * 
	 * @param nodes
	 * @param writer
	 * @param contentProvider
	 */
	private void buildXML(Object[] nodes, SourceWriter writer, JSContentProvider provider)
	{
		for (int i = 0; i < nodes.length; i++)
		{
			Object node = nodes[i];
			
			if (node instanceof JSOutlineItem)
			{
				JSOutlineItem item = (JSOutlineItem) node;
				String label = item.getLabel();
				String name = "item";
				
				switch (item.getType())
				{
					case JSOutlineItemType.ARRAY:
						name = "array-literal";
						break;
						
					case JSOutlineItemType.BOOLEAN:
						name = "boolean";
						break;
						
					case JSOutlineItemType.FUNCTION:
						name = "function";
						break;
						
					case JSOutlineItemType.NULL:
						name = "null";
						break;
						
					case JSOutlineItemType.NUMBER:
						name = "number";
						break;
						
					case JSOutlineItemType.OBJECT_LITERAL:
						name = "object-literal";
						break;
						
					case JSOutlineItemType.REGEX:
						name = "regex";
						break;
						
					case JSOutlineItemType.STRING:
						name = "string";
						break;
						
					default:
						name = "property";
						break;
				}
				
				writer.printWithIndent("<").print(name);
				writer.print(" label='").print(label).print("'");
				
				if (item.hasChildren())
				{
					writer.println(">").increaseIndent();
					this.buildXML(provider.getChildren(node), writer, provider);
					writer.decreaseIndent().printWithIndent("</").print(name).println(">");
				}
				else
				{
					writer.println("/>");
				}
			}
		}
	}

	/**
	 * getParseResults
	 * 
	 * @param source
	 * @return IParseNode
	 */
	protected IParseNode getParseResults(String source)
	{
		IParseNode result = null;
		
		this._parseState.setEditState(source, source, 0, 0);
		
		try
		{
			this._parser.parse(this._parseState);
			
			result = this._parseState.getParseResults();
		}
		catch (Exception e)
		{
			IdeLog.logInfo(TestingPlugin.getDefault(), "getParseResults failed", e); //$NON-NLS-1$
		}
		
		return result;
	}

	/**
	 * getXML
	 * 
	 * @param source
	 * @return String
	 */
	protected String getXML(String source)
	{
		IParseNode root = this.getParseResults(source);
		JSContentProvider provider = new JSContentProvider();
		SourceWriter writer = new SourceWriter();
		
		writer.printlnWithIndent("<?xml version='1.0'?>");
		writer.printlnWithIndent("<outline>").increaseIndent();
		this.buildXML(provider.getElements(root), writer, provider);
		writer.decreaseIndent().printlnWithIndent("</outline>");
		
		return writer.toString();
	}

	/**
	 * getBoolean
	 * 
	 * @param source
	 * @param expression
	 * @return
	 */
	protected boolean getBoolean(String source, String expression)
	{
		return ((Boolean) this.getType(source, expression, XPathConstants.BOOLEAN)).booleanValue();
	}

	/**
	 * getNode
	 * 
	 * @param source
	 * @param expression
	 * @return
	 */
	protected Node getNode(String source, String expression)
	{
		return (Node) this.getType(source, expression, XPathConstants.NODE);
	}

	/**
	 * getNodeSet
	 * 
	 * @param source
	 * @param expression
	 * @return
	 */
	protected NodeSet getNodeSet(String source, String expression)
	{
		return (NodeSet) this.getType(source, expression, XPathConstants.NODESET);
	}

	/**
	 * getNumber
	 * 
	 * @param source
	 * @param expression
	 * @return
	 */
	protected double getNumber(String source, String expression)
	{
		return ((Double) this.getType(source, expression, XPathConstants.NUMBER)).doubleValue();
	}

	/**
	 * getString
	 * 
	 * @param source
	 * @param expression
	 * @return
	 */
	protected String getString(String source, String expression)
	{
		return (String) this.getType(source, expression, XPathConstants.STRING);
	}

	/**
	 * getType
	 * 
	 * @param source
	 * @param expression
	 * @param type
	 * @return
	 */
	private Object getType(String source, String expression, QName type)
	{
		Object result = null;
		
		String xml = this.getXML(source);
		InputSource inputSource = new InputSource(new StringReader(xml));
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		try
		{
			result = xpath.evaluate(expression, inputSource, type);
		}
		catch (XPathExpressionException e)
		{
			//e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * setUp
	 */
	protected void setUp() throws Exception
	{
		this._parser = new JSParser();
		this._parseState = new JSParseState();
	}

	/**
	 * testItem
	 * 
	 * @param source
	 * @param xpath
	 * @param label
	 */
	protected void testItem(String source, String xpath, String label)
	{
		this.testItem(source, xpath, label, 0);
	}

	/**
	 * Make sure there is only one element for the given xpath. Check that
	 * element's label and make sure it has the specified number of children
	 * 
	 * @param source
	 * 		The JS source code to test
	 * @param xpath
	 * 		The XPath selector for the element we're interested in
	 * @param label
	 * 		The expected text on the element's item in the outline
	 * @param childCount
	 * 		The expected number of children for the element
	 */
	protected void testItem(String source, String xpath, String label, int childCount)
	{
		String countPath = "count(" + xpath + ")";
		String labelPath = xpath + "/@label";
		
		// make sure there is only one object at the specified path
		double count = this.getNumber(source, countPath);
		assertEquals(1.0, count, 0.0);
		
		// grab the element
		Element element = (Element) this.getNode(source, xpath);
		
		// count how many children are elements
		NodeList children = element.getChildNodes();
		int elementCount = 0;

		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			
			if (child.getNodeType() == Node.ELEMENT_NODE)
			{
				elementCount++;
			}
		}
		
		// assert that we have the expected number of children
		assertEquals(childCount, elementCount);
	
		// grab the element's label
		String labelText = this.getString(source, labelPath);
		
		// assert that we have the expected label
		assertEquals(label, labelText);
	}
}
