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
package com.aptana.ide.editor.xml.tests;

import junit.framework.TestCase;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.xml.parsing.XMLParseState;
import com.aptana.ide.editor.xml.parsing.XMLParser;
import com.aptana.ide.editor.xml.parsing.nodes.XMLParseNode;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class TestStatements extends TestCase
{
	/*
	 * Fields
	 */
	private static String EOL = System.getProperty("line.separator"); //$NON-NLS-1$

	private XMLParser _parser;
	private XMLParseState _parseState;

	/*
	 * Methods
	 */

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this._parser = new XMLParser();
		this._parseState = new XMLParseState();
	}

	/**
	 * typingTests
	 * 
	 * @param source
	 */
	protected void parseTest(String source)
	{
		this._parseState.setEditState(source, source, 0, 0);

		try
		{
			this._parser.parse(this._parseState);
			IParseNode parseResult = this._parseState.getParseResults();
			XMLParseNode root = (XMLParseNode) parseResult.getChild(0);
			String result = root.getSource();

			assertEquals(source, result);
		}
		catch (LexerException e)
		{
			IdeLog.logInfo(TestingPlugin.getDefault(), "parseTest failed", e); //$NON-NLS-1$
		}
	}

	/*
	 * Tests
	 */

	/**
	 * Test simple assignment
	 * 
	 * @throws Exception
	 */
	public void testEmptyElement() throws Exception
	{
		this.parseTest("<svg/>" + EOL); //$NON-NLS-1$
	}
}
