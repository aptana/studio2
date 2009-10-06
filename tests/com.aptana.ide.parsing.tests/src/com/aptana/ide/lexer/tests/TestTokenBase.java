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
package com.aptana.ide.lexer.tests;

import junit.framework.TestCase;

import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.Range;

/**
 * @author Kevin Lindsey
 */
public abstract class TestTokenBase extends TestCase
{
	/**
	 * lexer
	 */
	protected ILexer lexer;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this.lexer = this.createLexer();
		this.lexer.setLanguage(this.getLanguage());
	}

	/**
	 * createLexer
	 * 
	 * @return ILexer
	 * @throws Exception
	 */
	protected abstract ILexer createLexer() throws Exception;

	/**
	 * delimiterTest
	 * 
	 * @param delimiterGroup
	 * @param source
	 * @param delimiterText
	 * @throws LexerException
	 */
	public void delimiterTest(String delimiterGroup, String source, String delimiterText) throws LexerException
	{
		int startingIndex = source.indexOf(delimiterText);
		int endingIndex = startingIndex + delimiterText.length();

		// apply source
		this.lexer.setSource(source);

		// get delimiter range
		Range range = this.lexer.find(delimiterGroup);

		assertEquals(startingIndex, range.getStartingOffset());
		assertEquals(endingIndex, range.getEndingOffset());
	}
	
	/**
	 * Get the target language for these unit tests
	 *
	 * @return The lexer target language
	 */
	protected abstract String getLanguage();
	
	/**
	 * noLexemeTest
	 *
	 * @param source
	 */
	protected void noLexemeTest(String source)
	{
		// set source
		this.lexer.setSource(source);
		
		// lex source
		Lexeme lexeme = this.lexer.getNextLexeme();

		// make sure we got nothing
		assertNull(lexeme);
	}
	
	/**
	 * lexemeTest
	 * 
	 * @param source
	 * @param tokenCategory
	 * @param tokenType
	 */
	protected void lexemeTest(String source, int tokenCategory, int tokenType)
	{
		this.lexemeTest(source, source, tokenCategory, tokenType);
	}
	
	/**
	 * lexemeTest
	 * 
	 * @param source
	 * @param tokenCategory
	 * @param tokenType
	 */
	protected void lexemeTest(String source, String tokenCategory, String tokenType)
	{
		this.lexemeTest(source, source, tokenCategory, tokenType);
	}

	/**
	 * lexemeTest
	 * 
	 * @param source
	 * @param target
	 * @param tokenCategory
	 * @param tokenType
	 */
	protected void lexemeTest(String source, String target, int tokenCategory, int tokenType)
	{
		// set source
		this.lexer.setSource(source);
		
		// lex source
		Lexeme lexeme = this.lexer.getNextLexeme();

		// make sure we got something
		assertNotNull(lexeme);

		// make sure the lexeme is in the right category
		assertEquals("Token categories do not match", tokenCategory, lexeme.getCategoryIndex());

		// make sure the lexeme is of the correct type
		assertEquals("Token types do not match", tokenType, lexeme.typeIndex);

		// make sure text matches target
		assertEquals(target, lexeme.getText());
	}
	
	/**
	 * lexemeTest
	 * 
	 * @param source
	 * @param target
	 * @param tokenCategory
	 * @param tokenType
	 */
	protected void lexemeTest(String source, String target, String tokenCategory, String tokenType)
	{
		// set source
		this.lexer.setSource(source);
		
		// lex source
		Lexeme lexeme = this.lexer.getNextLexeme();
		
		// make sure we got something
		assertNotNull(lexeme);
		
		// make sure the lexeme is in the right category
		assertEquals("Token categories do not match", tokenCategory, lexeme.getCategory());
		
		// make sure the lexeme is of the correct type
		assertEquals("Token types do not match", tokenType, lexeme.getType());
		
		// make sure text matches target
		assertEquals(target, lexeme.getText());
	}
	
	/**
	 * lexemeTest
	 * 
	 * @param source
	 * @param target
	 * @param tokenCategory
	 * @param tokenType
	 * @param offset 
	 */
	protected void lexemeTestAtOffset(String source, String target, String tokenCategory, String tokenType, int offset)
	{
		// set source
		this.lexer.setSource(source);
		
		// update offset
		this.lexer.setCurrentOffset(offset);
		
		// lex source
		Lexeme lexeme = this.lexer.getNextLexeme();
		
		// make sure we got something
		assertNotNull(lexeme);
		
		// make sure the lexeme is in the right category
		assertEquals("Token categories do not match", tokenCategory, lexeme.getCategory());
		
		// make sure the lexeme is of the correct type
		assertEquals("Token types do not match", tokenType, lexeme.getType());
		
		// make sure text matches target
		assertEquals(target, lexeme.getText());
	}
}
