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
import org.jaxen.XPath;

import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.parsing.JSParser;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class XPathTests
{
	private JSParser _parser;
	private JSParseState _parseState;

	/**
	 * XPathTests
	 * 
	 * @throws ParserInitializationException
	 */
	public XPathTests() throws ParserInitializationException
	{
		this._parser = new JSParser();
		this._parseState = new JSParseState();
	}

	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			XPathTests tests = new XPathTests();

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

		writer.printlnWithIndent("var aaa = 10;").println(); //$NON-NLS-1$

		writer.printlnWithIndent("function abc() {").increaseIndent(); //$NON-NLS-1$
		writer.printlnWithIndent("var xxx = true;"); //$NON-NLS-1$
		writer.printlnWithIndent("var xxy = 10;"); //$NON-NLS-1$
		writer.printlnWithIndent("var xxz = 'hello';").println(); //$NON-NLS-1$

		writer.printlnWithIndent("if (xxx = false) {").increaseIndent(); //$NON-NLS-1$
		writer.printlnWithIndent("return true;"); //$NON-NLS-1$
		writer.decreaseIndent().printlnWithIndent("}").println(); //$NON-NLS-1$

		writer.printlnWithIndent("if (xxy == false) xxy = true;").println(); //$NON-NLS-1$

		writer.printlnWithIndent("if (xxy == false) xxy == true else xxy = true").println(); //$NON-NLS-1$

		writer.decreaseIndent().printlnWithIndent("}").println(); //$NON-NLS-1$

		writer.printlnWithIndent("function def() {").increaseIndent(); //$NON-NLS-1$
		writer.printlnWithIndent("var y = /abc/ig;"); //$NON-NLS-1$
		writer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$

		String source = writer.toString();
		System.out.println(source);

		// parse source
		parse(source);

		// grab result
		IParseNode root = this._parseState.getParseResults().getChild(0);

		// try an xpath on the result
		runXPath(root, "/function"); //$NON-NLS-1$
		runXPath(root, "//var"); //$NON-NLS-1$
		runXPath(root, "/function/statements/var"); //$NON-NLS-1$
		runXPath(root, "//var[count(declaration/number) > 0]"); //$NON-NLS-1$
		runXPath(root, "//if[child::*[1][self::assignment]]"); //$NON-NLS-1$
		runXPath(root, "//function[@name='def']"); //$NON-NLS-1$
	}

	private void runXPath(IParseNode root, String xpathExpr) throws JaxenException
	{
		XPath xpath = new ParseNodeXPath(xpathExpr);
		List result = (List) xpath.evaluate(root);

		if (result != null)
		{
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
				}
			}
			else
			{
				System.out.println("<empty result>"); //$NON-NLS-1$
			}
		}
	}
}
