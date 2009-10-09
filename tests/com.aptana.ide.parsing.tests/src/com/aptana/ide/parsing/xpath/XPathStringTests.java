/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.parsing.xpath;

import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.XPathFunctionContext;

import com.aptana.ide.editor.css.parsing.CSSParseState;
import com.aptana.ide.editor.css.parsing.CSSParser;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeAttribute;

/**
 * @author Kevin Lindsey
 */
public class XPathStringTests
{
	private static final String NAMESPACE = "http://www.aptana.com/xpath"; //$NON-NLS-1$
	private static final String PREFIX = "apt"; //$NON-NLS-1$
	
	private CSSParser _parser;
	private CSSParseState _parseState;

	/*
	 * Constructors
	 */

	/**
	 * XPathTests
	 * 
	 * @throws ParserInitializationException
	 */
	public XPathStringTests() throws ParserInitializationException
	{
		this._parser = new CSSParser();
		this._parseState = new CSSParseState();
	}

	/*
	 * Methods
	 */

	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			XPathStringTests tests = new XPathStringTests();

			tests.run();
		}
		catch (ParserInitializationException e)
		{
			e.printStackTrace();
		}
		catch (JaxenException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * parse
	 * 
	 * @param source
	 */
	protected void parse(String source)
	{
		this._parseState.setEditState(source, source, 0, 0);

		try
		{
			this._parser.parse(this._parseState);
		}
		catch (LexerException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * run
	 * 
	 * @throws JaxenException
	 */
	public void run() throws JaxenException
	{
		// create source
		SourceWriter writer = new SourceWriter();

		writer.printlnWithIndent(".abc {}"); //$NON-NLS-1$
		writer.printlnWithIndent(".def {}"); //$NON-NLS-1$
		writer.printlnWithIndent(".ghi {}"); //$NON-NLS-1$

		String source = writer.toString();
		System.out.println(source);

		// parse source
		parse(source);

		// grab result
		IParseNode root = this._parseState.getParseResults();

		// try an xpath on the result
		runXPath(root, "//CSSTextNode/@value[starts-with(., '.')]"); //$NON-NLS-1$
		runXPath(root, "substring('abc', 2)"); //$NON-NLS-1$
		runXPath(root, "apt:substring(//CSSTextNode/@value[starts-with(., '.')], 2)"); //$NON-NLS-1$
	}

	private void runXPath(IParseNode root, String xpathExpr) throws JaxenException
	{
		SimpleFunctionContext fc = new XPathFunctionContext();
		fc.registerFunction(NAMESPACE, "substring", new SubstringFunction()); //$NON-NLS-1$
		
		SimpleNamespaceContext nc = new SimpleNamespaceContext();
		nc.addNamespace(PREFIX, NAMESPACE);
		
		XPath xpath = new ParseNodeXPath(xpathExpr);
		
		xpath.setFunctionContext(fc);
		xpath.setNamespaceContext(nc);
		
		Object temp = xpath.evaluate(root);

		if (temp != null)
		{
			if (temp instanceof List)
			{
				List result = (List) temp;

				System.out.println();
				System.out.println(xpathExpr);
				System.out.println("======"); //$NON-NLS-1$

				if (result.size() > 0)
				{
					for (int i = 0; i < result.size(); i++)
					{
						Object item = result.get(i);

						if (item instanceof IParseNode)
						{
							System.out.println(((IParseNode) item).getSource());
						}
						else if (item instanceof IParseNodeAttribute)
						{
							System.out.println(((IParseNodeAttribute) item).getSource());
						}
						else
						{
							System.out.println(item);
						}
					}
				}
				else
				{
					System.out.println("<empty result>"); //$NON-NLS-1$
				}
			}
			else
			{
				System.out.println(temp);
			}
		}
	}
}
