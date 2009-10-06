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
package com.aptana.ide.editor.xml.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.io.StreamUtils;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.matcher.MatcherLexerBuilder;

/**
 * @author Kevin Lindsey
 */
public class TestLexTime extends TestCase
{
	private ILexer _lexer;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		// get JS lexer XML file
		InputStream input = TestLexTime.class.getResourceAsStream("xml_lexer_1_2.lxr");
		
		// create lexer builder
		MatcherLexerBuilder builder = new MatcherLexerBuilder();

		// read input stream
		builder.loadXML(input);

		// finalize lexer
		this._lexer = builder.buildLexer();
		
		// set lexer to the default JS token list
		this._lexer.setLanguageAndGroup("text/xml", "default");
	}
	
	/**
	 * parse
	 * 
	 * @param source
	 * @param insertedSource
	 */
	protected void parse(String source, boolean showResults)
	{
		try
		{
			List<Lexeme> lexemes = new ArrayList<Lexeme>();
			
			// set source
			this._lexer.setSource(source);
			
			// start timing
			long startTime = System.currentTimeMillis();

			while (true)
			{
				Lexeme lexeme = this._lexer.getNextLexeme();
				
				if (lexeme == null && this._lexer.isEOS() == false)
				{
					this._lexer.setGroup("error");
					lexeme = this._lexer.getNextLexeme();
				}
				
				if (lexeme != null)
				{
					lexemes.add(lexeme);
				}
				else
				{
					break;
				}
			}
			
			if (showResults)
			{
				// stop timing
				long endTime = System.currentTimeMillis();
				long msecs = endTime - startTime;
				double secs = msecs / 1000.0d;
				double cps = Math.round((source.length() / secs) * 100.0) / 100.0;
				
				System.out.println("Lexing time              : " + msecs + "ms");
				System.out.println("Lexing cps               : " + cps + " cps");
				System.out.println("Lexeme count             : " + lexemes.size());
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
	public void testDOM2() throws IOException
	{
		InputStream stream = this.getClass().getResourceAsStream("dom_2.xml");
		String source = StreamUtils.getText(stream);
		
		this.parse(source, false); //$NON-NLS-1$
		this.parse(source, true); //$NON-NLS-1$
	}
}
