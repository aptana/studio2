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
package com.aptana.ide.parsing.tests;

import junit.framework.TestCase;

import com.aptana.ide.editor.js.parsing.JSParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.parsing.IParseState;

/**
 * @author Kevin Lindsey
 */
public class ParseStateTests extends TestCase
{
	private ILexer _lexer;
	private IParseState _state;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		JSParser parser = new JSParser();

		this._lexer = parser.getLexer();
		this._state = parser.createParseState(null);
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		this._lexer = null;
		this._state = null;
		super.tearDown();
	}

	/**
	 * lex
	 * 
	 * @param source
	 * @throws LexerException
	 */
	private void lex(String source) throws LexerException
	{
		LexemeList lexemes = this._state.getLexemeList();

		// associate lexeme list with lexer
		this._lexer.setLexemeCache(lexemes);

		// save source
		this._lexer.setSource(source);

		// make sure we're in the right group
		this._lexer.setLanguageAndGroup("text/javascript", "default");

		// grab lexemes
		Lexeme lexeme = this._lexer.getNextLexeme();

		while (lexeme != null)
		{
			lexemes.add(lexeme);

			lexeme = this._lexer.getNextLexeme();
		}
	}

	/**
	 * perform edit
	 * 
	 * @param beforeText
	 * @param afterText
	 * @param insertedText
	 * @param offset
	 * @param removeLength
	 * @param startingOffset
	 * @param endingOffset
	 * @throws LexerException
	 */
	private void performEdit(String beforeText, String afterText, String insertedText, int offset, int removeLength, int startingOffset, int endingOffset) throws LexerException
	{
		// setup initial set of lexemes
		this.lex(beforeText);

		// set edit info
		this._state.setEditState(afterText, insertedText, offset, removeLength);

		// test affected region
		Range affectedRegion = this._state.getLexemeList().getAffectedRegion();
		
		assertEquals(startingOffset, affectedRegion.getStartingOffset());
		assertEquals(endingOffset, affectedRegion.getEndingOffset());
	}
	
	/*
	 * Test Deletions
	 */
	
	/**
	 * Test case where we delete a single character immediately after a token, but a space still remains after the
	 * deletion
	 * 
	 * @throws LexerException
	 */
	public void testDeleteAfterTokenEnd() throws LexerException
	{
		this.performEdit("abc  = 123", "abc = 123", "", 3, 1, 0, 3);
	}

	/**
	 * Test case where we delete a single character immediately before a token, but a space still remains after the
	 * deletion
	 * 
	 * @throws LexerException
	 */
	public void testDeleteBeforeTokenStart() throws LexerException
	{
		this.performEdit("abc =  123", "abc = 123", "", 6, 1, 6, 9);
	}

	/**
	 * Test case where we delete a single space between two tokens potentially causing them to become one token
	 * 
	 * @throws LexerException
	 */
	public void testDeleteJoinsTokens() throws LexerException
	{
		this.performEdit("abc = 123", "abc= 123", "", 3, 1, 0, 4);
	}

	/**
	 * Test case where we delete a single character from the end of a token
	 * 
	 * @throws LexerException
	 */
	public void testDeleteTokenEnd() throws LexerException
	{
		this.performEdit("abc = 123", "ab = 123", "", 2, 1, 0, 2);
	}

	/**
	 * Test case where we delete a single character from the beginning of a token
	 * 
	 * @throws LexerException
	 */
	public void testDeleteTokenStart() throws LexerException
	{
		this.performEdit("abc = 123", "bc = 123", "", 0, 1, 0, 2);
	}

	/**
	 * Test case where we delete multiple characters within a token
	 * 
	 * @throws LexerException
	 */
	public void testDeleteMultipleInToken() throws LexerException
	{
		this.performEdit("abcd = 123", "ad = 123", "", 1, 2, 0, 2);
	}

	/**
	 * Test case where we delete multiple characters at the beginning of a token
	 * 
	 * @throws LexerException
	 */
	public void testDeleteMultipleAtTokenStart() throws LexerException
	{
		this.performEdit("abcd = 123", "cd = 123", "", 0, 2, 0, 2);
	}

	/**
	 * Test case where we delete multiple characters at the end of a token
	 * 
	 * @throws LexerException
	 */
	public void testDeleteMultipleAtTokenEnd() throws LexerException
	{
		this.performEdit("abcd = 123", "ab = 123", "", 2, 2, 0, 2);
	}

	/**
	 * Test case where we delete all characters between two tokens
	 * 
	 * @throws LexerException
	 */
	public void testDeleteAllBetweenTokens() throws LexerException
	{
		this.performEdit("abc  = 123", "abc= 123", "", 3, 2, 0, 4);
	}

	/**
	 * Test case where we delete an intermediate token between the two tokens on either side of that token
	 * 
	 * @throws LexerException
	 */
	public void testDeleteIntermediateToken() throws LexerException
	{
		this.performEdit("abc = def = 123", "abc= 123", "", 3, 7, 0, 4);
	}

	/**
	 * Test case where we delete an intermediate token all other characters between the two tokens on either side of
	 * that token
	 * 
	 * @throws LexerException
	 */
	public void testDeleteIntermediateTokenAndWhitespace() throws LexerException
	{
		this.performEdit("abc = def = 123", "abc = 123", "", 3, 6, 0, 3);
	}

	/*
	 * Test Single-Character Insertions
	 */

	/**
	 * Test case where we add a single character to the end of a token
	 * 
	 * @throws LexerException
	 */
	public void testAddToTokenEnd() throws LexerException
	{
		this.performEdit("abc = 123", "abcd = 123", "d", 3, 0, 0, 6);
	}

	/**
	 * Test case where we add a single character to the beginning of a token
	 * 
	 * @throws LexerException
	 */
	public void testAddToTokenStart() throws LexerException
	{
		this.performEdit("abc = 123", "zabc = 123", "z", 0, 0, 0, 4);
	}

	/**
	 * Test case where we add a single character to the middle of a token
	 * 
	 * @throws LexerException
	 */
	public void testAddToTokenMiddle() throws LexerException
	{
		this.performEdit("abc = 123", "abzc = 123", "z", 2, 0, 0, 4);
	}

	/**
	 * Test case where we add a single character between two tokens
	 * 
	 * @throws LexerException
	 */
	public void testAddBetweenTokens() throws LexerException
	{
		this.performEdit("abc =  123", "abc = - 123", "-", 6, 0, 6, 11);
	}

	/**
	 * Test case where we add text within a token
	 * 
	 * @throws LexerException
	 */
	public void testAddWithinToken() throws LexerException
	{
		this.performEdit("abc123", "abc = 123", " = ", 3, 0, 0, 9);
	}

	/*
	 * Test deleting and adding in a single operation
	 */
	
	/**
	 * Test case where we add text within a token
	 * 
	 * @throws LexerException
	 */
	public void testRemoveAndAddWithinToken() throws LexerException
	{
		this.performEdit("abc123", "ab = 23", " = ", 2, 2, 0, 7);
	}
}
