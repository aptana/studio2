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

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.parsing.JSParser;
import com.aptana.ide.editor.js.parsing.JSScanner;
import com.aptana.ide.io.StreamUtils;
import com.aptana.ide.lexer.LexerException;

/**
 * @author Kevin Lindsey
 */
public class TestParseTime extends TestCase
{
	private JSScanner _scanner;
	private JSParser _parser;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		this._scanner = new JSScanner();
		this._parser = new JSParser();
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		this._parser = null;
		this._scanner = null;
		super.tearDown();
	}
	
	/**
	 * parse
	 * 
	 * @param source
	 * @param insertedSource
	 */
	protected void parse(String source, boolean showResults)
	{
		JSParseState parseState = (JSParseState) this._parser.createParseState(null);
//		AbstractLexer lexer;
		
		parseState.setEditState(source, source, 0, 0);

		try
		{
			// start timing
			long startTime = System.currentTimeMillis();

			// reset hit/miss counts
//			lexer = (AbstractLexer) this._scanner.getLexer();
//			lexer.missCount = 0;
//			lexer.hitCount = 0;
			
			// do initial scanning pass
			this._scanner.parse(parseState);
			
			// get lexing time
			long lexTiming = System.currentTimeMillis();
			
			// get hit/miss counts for scanning
//			int missCount1 = lexer.missCount;
//			int hitCount1 = lexer.hitCount;
			
			// get lexeme count
			int lexemeCount = parseState.getLexemeList().size();
			
			// reset hit/miss counts
//			lexer = (AbstractLexer) this._parser.getLexer();
//			lexer.missCount = 0;
//			lexer.hitCount = 0;
			
			// do more in-depth parse
			this._parser.parse(parseState);
			
			if (showResults)
			{
				// stop timing
				long endTime = System.currentTimeMillis();
				
				this._scanner.parse(parseState);
				
				long endRescan = System.currentTimeMillis();
				
				// get hit/miss counts for parsing
//				int missCount2 = lexer.missCount;
//				int hitCount2 = lexer.hitCount;
				
				System.out.println("Lexing time              : " + (lexTiming - startTime) + "ms");
				System.out.println("Parsing time             : " + (endTime - lexTiming) + "ms");
				System.out.println("Scan+parse time          : " + (endTime - startTime) + "ms");
				System.out.println("2nd Lexing time          : " + (endRescan - endTime) + "ms");
				System.out.println("Est. 2nd scan+parse time : " + (endRescan - lexTiming) + "ms");
				
				System.out.println("Scan lexeme count        : " + lexemeCount);
				System.out.println("Parse lexeme count       : " + parseState.getLexemeList().size());
//				System.out.println("Miss count 1: " + missCount1);
//				System.out.println("Hit count 1: " + hitCount1);
//				System.out.println("Miss count 2: " + missCount2);
//				System.out.println("Hit count 2: " + hitCount2);
				System.out.println();
			}
		}
		catch (LexerException e)
		{
			IdeLog.logInfo(TestingPlugin.getDefault(), "parseTest failed", e); //$NON-NLS-1$
		}
	}
	
	/**
	 * testDojoUncompressed
	 * @throws IOException 
	 */
	public void testDojoUncompressed() throws IOException
	{
		InputStream stream = this.getClass().getResourceAsStream("dojo.js.uncompressed.js");
		String source = StreamUtils.getText(stream);
		
		this.parse(source, false);
	}
	
	public void testDojoUncompressedShowingResults() throws IOException
	{
		InputStream stream = this.getClass().getResourceAsStream("dojo.js.uncompressed.js");
		String source = StreamUtils.getText(stream);
		
		this.parse(source, true);
	}
	
	/**
	 * testServerFramework
	 * @throws IOException 
	 */
	public void testServerFramework() throws IOException
	{
		InputStream stream = this.getClass().getResourceAsStream("serverFramework.js");
		String source = StreamUtils.getText(stream);
		
		this.parse(source, false);
	}
	
	public void testServerFrameworkShowingResults() throws IOException
	{
		InputStream stream = this.getClass().getResourceAsStream("serverFramework.js");
		String source = StreamUtils.getText(stream);
		
		this.parse(source, true);
	}
}
