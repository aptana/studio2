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
package com.aptana.ide.editor.js.tests;

import junit.framework.TestCase;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.parsing.JSParser;
import com.aptana.ide.editors.unified.IPairFinder;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.PairMatch;
import com.aptana.ide.lexer.LexerException;

/**
 * @author Pavel Petrochenko
 */
public class TestPairFinder extends TestCase
{

	private JSParser _parser;
	private JSParseState _parseState;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this._parser = new JSParser();
		this._parseState = (JSParseState) _parser.createParseState(null);
	}

	/**
	 * @return MimeType
	 */
	protected String getMimeType()
	{
		return JSMimeType.MimeType;
	}

	/**
	 * typingTests
	 * 
	 * @param source
	 * @param testOffset
	 * @param expectedResult
	 */
	protected void findTest(String source, int testOffset, PairMatch expectedResult)
	{
		this._parseState.setEditState(source, source, 0, 0);

		try
		{
			this._parser.parse(this._parseState);
			IPairFinder pairFinder = LanguageRegistry.getPairFinder(getMimeType());
			PairMatch findPairMatch = pairFinder.findPairMatch(testOffset, this._parseState);
			assertEquals("beginStart", expectedResult.beginStart, findPairMatch.beginStart); //$NON-NLS-1$
			assertEquals("beginEnd", expectedResult.beginEnd, findPairMatch.beginEnd); //$NON-NLS-1$
			assertEquals("endStart", expectedResult.endStart, findPairMatch.endStart); //$NON-NLS-1$
			assertEquals("endEnd", expectedResult.endEnd, findPairMatch.endEnd); //$NON-NLS-1$
		}
		catch (LexerException e)
		{
			IdeLog.logInfo(TestingPlugin.getDefault(), "lexer failed", e); //$NON-NLS-1$
		}
	}

	/**
	 * typingTests
	 * 
	 * @param source
	 * @param firstOffset
	 * @param secondOffset
	 */
	protected void findPairsTest(String source, int firstOffset, int secondOffset)
	{
		PairMatch pairMatch = new PairMatch();
		pairMatch.beginEnd = firstOffset + 1;
		pairMatch.beginStart = firstOffset;
		pairMatch.endEnd = secondOffset + 1;
		pairMatch.endStart = secondOffset;
		findTest(source, firstOffset, pairMatch);
	}

	/**
	 * typingTests
	 * 
	 * @param source
	 * @param firstOffset
	 * @param secondOffset
	 */
	protected void findPairsDoubleTest(String source, int firstOffset, int secondOffset)
	{
		findPairsTest(source, firstOffset, secondOffset);
		findPairsTest(source, secondOffset, firstOffset);
	}

	/**
	 * 
	 */
	public void testBrace()
	{
		findPairsTest("{}", 0, 1); //$NON-NLS-1$
	}
	
	/**
	 * 
	 */
	public void testBrace1()
	{
		findPairsTest("{//Ere\n\r}", 0, 8); //$NON-NLS-1$
	}
	
	/**
	 * 
	 */
	public void testParen()
	{
		findPairsTest("()", 0, 1); //$NON-NLS-1$
	}
	
	/**
	 * 
	 */
	public void testParenBalance()
	{
		findPairsTest("((1))", 0, 4); //$NON-NLS-1$
	}
	
	/**
	 * 
	 */
	public void testParenBalance2()
	{
		findPairsTest("()(1))", 2, 4); //$NON-NLS-1$
	}
	
	
	/**
	 * 
	 */
	public void testString()
	{
		PairMatch pairMatch = new PairMatch();
		pairMatch.beginStart = 0;
		pairMatch.beginEnd = 1;
		pairMatch.endStart = 1;
		pairMatch.endEnd = 2;
		findTest("\"\"\"", 1, pairMatch);
	}
	
	/**
	 * 
	 */
	public void testString1()
	{
		PairMatch pairMatch = new PairMatch();
		pairMatch.beginStart = 0;
		pairMatch.beginEnd = 1;
		pairMatch.endStart = 1;
		pairMatch.endEnd = 2;
		findTest("\"\"\"", 1, pairMatch);
	}
}
