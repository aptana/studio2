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
package com.aptana.ide.editor.scriptdoc.tests;

import com.aptana.ide.editor.scriptdoc.lexing.ScriptDocTokenTypes;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.lexer.tests.TestTokenBase;

/**
 * @author Kevin Lindsey
 */
public class TestPunctuatorTokens extends TestTokenBase
{
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#createLexer()
	 */
	protected ILexer createLexer() throws Exception
	{
		ScriptDocParser parser = new ScriptDocParser();

		return parser.getLexer();
	}
	
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#getLanguage()
	 */
	protected String getLanguage()
	{
		return ScriptDocMimeType.MimeType;
	}
	
	/**
	 * testLCurly
	 * 
	 * @throws LexerException
	 */
	public void testLCurly() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("{", TokenCategories.PUNCTUATOR, ScriptDocTokenTypes.LCURLY); //$NON-NLS-1$
	}
	
	/**
	 * testDollaLCurly
	 * 
	 * @throws LexerException
	 */
	public void testDollaLCurly() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("${", TokenCategories.PUNCTUATOR, ScriptDocTokenTypes.DOLLAR_LCURLY); //$NON-NLS-1$
	}
	
	/**
	 * testPound
	 * 
	 * @throws LexerException
	 */
	public void testPound() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("#", TokenCategories.PUNCTUATOR, ScriptDocTokenTypes.POUND); //$NON-NLS-1$
	}
	
	/**
	 * testLBracket
	 * 
	 * @throws LexerException
	 */
	public void testLBracket() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("[", TokenCategories.PUNCTUATOR, ScriptDocTokenTypes.LBRACKET); //$NON-NLS-1$
	}
	
	/**
	 * testRBracket
	 * 
	 * @throws LexerException
	 */
	public void testRBracket() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("]", TokenCategories.PUNCTUATOR, ScriptDocTokenTypes.RBRACKET); //$NON-NLS-1$
	}
	
	/**
	 * testRCurly
	 * 
	 * @throws LexerException
	 */
	public void testRCurly() throws LexerException
	{
		this.lexer.setGroup("identifier"); //$NON-NLS-1$
		this.lexemeTest("}", TokenCategories.PUNCTUATOR, ScriptDocTokenTypes.RCURLY); //$NON-NLS-1$
	}
	
	/**
	 * testEllipsis
	 * 
	 * @throws LexerException
	 */
	public void testEllipsis() throws LexerException
	{
		this.lexer.setGroup("identifier"); //$NON-NLS-1$
		this.lexemeTest("...", TokenCategories.PUNCTUATOR, ScriptDocTokenTypes.ELLIPSIS); //$NON-NLS-1$
	}
	
	/**
	 * testIdentiferLBracket
	 * 
	 * @throws LexerException
	 */
	public void testIdentiferLBracket() throws LexerException
	{
		this.lexer.setGroup("identifier"); //$NON-NLS-1$
		this.lexemeTest("[", TokenCategories.PUNCTUATOR, ScriptDocTokenTypes.LBRACKET); //$NON-NLS-1$
	}
	
	/**
	 * testIdentiferRBracket
	 * 
	 * @throws LexerException
	 */
	public void testIdentiferRBracket() throws LexerException
	{
		this.lexer.setGroup("identifier"); //$NON-NLS-1$
		this.lexemeTest("]", TokenCategories.PUNCTUATOR, ScriptDocTokenTypes.RBRACKET); //$NON-NLS-1$
	}
	
	/**
	 * testComma
	 * 
	 * @throws LexerException
	 */
	public void testComma() throws LexerException
	{
		this.lexer.setGroup("identifier"); //$NON-NLS-1$
		this.lexemeTest(",", TokenCategories.PUNCTUATOR, ScriptDocTokenTypes.COMMA); //$NON-NLS-1$
	}
	
	/**
	 * testPipe
	 * 
	 * @throws LexerException
	 */
	public void testPipe() throws LexerException
	{
		this.lexer.setGroup("identifier"); //$NON-NLS-1$
		this.lexemeTest("|", TokenCategories.PUNCTUATOR, ScriptDocTokenTypes.PIPE); //$NON-NLS-1$
	}
	
	/**
	 * testForwardSlash
	 * 
	 * @throws LexerException
	 */
	public void testForwardSlash() throws LexerException
	{
		this.lexer.setGroup("identifier"); //$NON-NLS-1$
		this.lexemeTest("/", TokenCategories.PUNCTUATOR, ScriptDocTokenTypes.FORWARD_SLASH); //$NON-NLS-1$
	}
}
