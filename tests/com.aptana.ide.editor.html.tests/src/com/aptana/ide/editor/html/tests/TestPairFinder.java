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
package com.aptana.ide.editor.html.tests;

import junit.framework.TestCase;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParseState;
import com.aptana.ide.editor.html.parsing.HTMLParser;
import com.aptana.ide.editors.unified.IPairFinder;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.PairMatch;
import com.aptana.ide.lexer.LexerException;

/**
 * @author Pavel Petrochenko
 */
public class TestPairFinder extends TestCase
{

	private HTMLParser _parser;
	private HTMLParseState _parseState;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this._parser = new HTMLParser();
		this._parseState = new HTMLParseState();
	}

	/**
	 * @return MimeType
	 */
	protected String getMimeType()
	{
		return HTMLMimeType.MimeType;
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

	public void testTagOpen()
	{
		PairMatch match = new PairMatch();
		match.beginStart = 0;
		match.beginEnd = 5;
		match.endStart = 5;
		match.endEnd = 11;
		findTest("<div></div>", 0, match); //$NON-NLS-1$
	}
	
	public void testTagOpenWithSpaceBeforeEndOfOpenTag()
	{
		PairMatch match = new PairMatch();
		match.beginStart = 0;
		match.beginEnd = 6;
		match.endStart = 6;
		match.endEnd = 12;
		findTest("<div ></div>", 0, match); //$NON-NLS-1$
	}

	public void testTagOpenWithAttributesOnlyIncludesTagName()
	{
		PairMatch match = new PairMatch();
		match.beginStart = 0;
		match.beginEnd = 4;
		match.endStart = 18;
		match.endEnd = 24;
		findTest("<div attr=\"value\"></div>", 0, match); //$NON-NLS-1$
	}

	public void testTagClose()
	{
		PairMatch match1 = new PairMatch();
		match1.beginStart = 3;
		match1.beginEnd = 8;
		match1.endStart = 8;
		match1.endEnd = 14;
		findTest("<b><div></div></b>", 13, match1); //$NON-NLS-1$
	}
	
	public void testTagCloseWithSpaceBeforeEndOfOpenTag()
	{
		PairMatch match1 = new PairMatch();
		match1.beginStart = 3;
		match1.beginEnd = 9;
		match1.endStart = 9;
		match1.endEnd = 15;
		findTest("<b><div ></div></b>", 14, match1); //$NON-NLS-1$
	}

	public void testTagCloseWithAttributesOnlyIncludesTagName()
	{
		PairMatch match = new PairMatch();
		match.beginStart = 0;
		match.beginEnd = 4;
		match.endStart = 18;
		match.endEnd = 24;
		findTest("<div attr=\"value\"></div>", 23, match); //$NON-NLS-1$
	}

}
