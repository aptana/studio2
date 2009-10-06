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
package com.aptana.ide.parsing.experimental;

import java.io.InputStream;

import junit.framework.TestCase;

import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.matcher.ITextMatcher;
import com.aptana.ide.lexer.matcher.RegexMatcherHandler;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.bnf.LRParser;
import com.aptana.ide.parsing.bnf.LRParserBuilder;

/**
 * @author Kevin Lindsey
 */
public class TestLRParserBuilder extends TestCase
{
	private LRParser _parser;
	private RegexMatcherHandler _handler;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		InputStream regexBNF = this.getClass().getResourceAsStream("Regex.bnf");
		InputStream regexLexer = this.getClass().getResourceAsStream("Regex.lxr");
		LRParserBuilder builder = new LRParserBuilder();
		
		this._parser = (LRParser) builder.buildParser(regexBNF, regexLexer);
		this._handler = new RegexMatcherHandler();
		this._parser.addHandler(this._handler);
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		this._parser = null;
		this._handler = null;
		super.tearDown();
	}

	/**
	 * testBuildParser
	 */
	public void testBuildParser()
	{
		assertNotNull(this._parser);
	}
	
	/**
	 * testSimplePattern
	 * @throws LexerException 
	 */
	public void testSimplePattern() throws LexerException
	{
		String pattern = "abc";
		
		IParseState parseState = this._parser.createParseState(null);
		parseState.setEditState(pattern, pattern, 0, 0);
		this._parser.parse(parseState);
		
		Object[] values = this._handler.getValues();
		assertEquals(1, values.length);
		
		Object value = values[0];
		assertTrue(value instanceof ITextMatcher);
		
		ITextMatcher matcher = (ITextMatcher) value;
		int result = matcher.match(pattern.toCharArray(), 0, pattern.length());
		assertEquals(3, result);
	}
}
