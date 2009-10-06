/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.editor.js.tests;

import junit.framework.TestCase;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.parsing.JSParser;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class TestComments extends TestCase
{
	private static final String EOL = System.getProperty("line.separator"); //$NON-NLS-1$

	private JSParser _parser;
	private JSParseState _parseState;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this._parser = new JSParser();
		this._parseState = (JSParseState) this._parser.createParseState(null);
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
			IParseState rootParseState = this._parseState.getRoot();
			IParseNode[] comments = rootParseState.getCommentRegions();
			
			assertEquals(1, comments.length);
			assertEquals(source.length(), comments[0].getLength());
		}
		catch (LexerException e)
		{
			IdeLog.logInfo(TestingPlugin.getDefault(), "parseTest failed", e); //$NON-NLS-1$
		}
	}
	
	/**
	 * parseTwiceTest
	 * 
	 * @param source
	 * @param insertedSource
	 */
	protected void parseTwiceTest(String source, String insertedSource)
	{
		this._parseState.setEditState(source, source, 0, 0);

		try
		{
			this._parser.parse(this._parseState);
			
			int index = source.indexOf(insertedSource);
			this._parseState.setEditState(source, insertedSource, index, insertedSource.length());
			this._parser.parse(this._parseState);
			
			IParseState rootParseState = this._parseState.getRoot();
			IParseNode[] comments = rootParseState.getCommentRegions();
			
			assertEquals(1, comments.length);
			assertEquals(source.length(), comments[0].getLength());
		}
		catch (LexerException e)
		{
			IdeLog.logInfo(TestingPlugin.getDefault(), "parseTest failed", e); //$NON-NLS-1$
		}
	}
	
	/**
	 * testSingleLineComment
	 */
	public void testSingleLineComment()
	{
		this.parseTest("// single-line comment"); //$NON-NLS-1$
	}

	/**
	 * testMultiLineComment
	 */
	public void testMultiLineComment()
	{
		this.parseTest("/*" + EOL + " * multi-line comment" + EOL + " */"); //$NON-NLS-1$
	}
	
	/**
	 * testSDocComment
	 */
	public void testSDocComment()
	{
		this.parseTest("/**" + EOL + " * @return {Object}" + EOL + " */"); //$NON-NLS-1$
	}
	
	/**
	 * testSingleLineComment2
	 */
	public void testSingleLineComment2()
	{
		this.parseTwiceTest("// single-line comment", "single"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * testMultiLineComment2
	 */
	public void testMultiLineComment2()
	{
		this.parseTwiceTest("/*" + EOL + " * multi-line comment" + EOL + " */", "multi"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * testSDocComment2
	 */
	public void testSDocComment2()
	{
		this.parseTwiceTest("/**" + EOL + " * @return {Object}" + EOL + " */", "return"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
