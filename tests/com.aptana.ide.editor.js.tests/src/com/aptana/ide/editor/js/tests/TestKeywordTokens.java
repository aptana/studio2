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

import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.parsing.JSParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.lexer.tests.TestTokenBase;

/**
 * @author Kevin Lindsey
 */
public class TestKeywordTokens extends TestTokenBase
{
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#createLexer()
	 */
	protected ILexer createLexer() throws Exception
	{
		JSParser parser = new JSParser();

		return parser.getLexer();
	}
	
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#getLanguage()
	 */
	protected String getLanguage()
	{
		return JSMimeType.MimeType;
	}
	
	/**
	 * testBreakKeyword
	 */
	public void testBreakKeyword()
	{
		this.lexemeTest("break", TokenCategories.KEYWORD, JSTokenTypes.BREAK); //$NON-NLS-1$
		this.lexemeTest("breaks", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testCaseKeyword
	 */
	public void testCaseKeyword()
	{
		this.lexemeTest("case", TokenCategories.KEYWORD, JSTokenTypes.CASE); //$NON-NLS-1$
		this.lexemeTest("cases", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testCatchKeyword
	 */
	public void testCatchKeyword()
	{
		this.lexemeTest("catch", TokenCategories.KEYWORD, JSTokenTypes.CATCH); //$NON-NLS-1$
		this.lexemeTest("catchs", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testContinueKeyword
	 */
	public void testContinueKeyword()
	{
		this.lexemeTest("continue", TokenCategories.KEYWORD, JSTokenTypes.CONTINUE); //$NON-NLS-1$
		this.lexemeTest("continues", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testDefaultKeyword
	 */
	public void testDefaultKeyword()
	{
		this.lexemeTest("default", TokenCategories.KEYWORD, JSTokenTypes.DEFAULT); //$NON-NLS-1$
		this.lexemeTest("defaults", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testDeleteKeyword
	 */
	public void testDeleteKeyword()
	{
		this.lexemeTest("delete", TokenCategories.KEYWORD, JSTokenTypes.DELETE); //$NON-NLS-1$
		this.lexemeTest("deletes", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testDoKeyword
	 */
	public void testDoKeyword()
	{
		this.lexemeTest("do", TokenCategories.KEYWORD, JSTokenTypes.DO); //$NON-NLS-1$
		this.lexemeTest("dos", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testElseKeyword
	 */
	public void testElseKeyword()
	{
		this.lexemeTest("else", TokenCategories.KEYWORD, JSTokenTypes.ELSE); //$NON-NLS-1$
		this.lexemeTest("elses", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testIfKeyword
	 */
	public void testIfKeyword()
	{
		this.lexemeTest("if", TokenCategories.KEYWORD, JSTokenTypes.IF); //$NON-NLS-1$
		this.lexemeTest("ifs", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testInKeyword
	 */
	public void testInKeyword()
	{
		this.lexemeTest("in", TokenCategories.KEYWORD, JSTokenTypes.IN); //$NON-NLS-1$
		this.lexemeTest("ins", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testInstanceOfKeyword
	 */
	public void testInstanceOfKeyword()
	{
		this.lexemeTest("instanceof", TokenCategories.KEYWORD, JSTokenTypes.INSTANCEOF); //$NON-NLS-1$
		this.lexemeTest("instanceofs", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testFinallyKeyword
	 */
	public void testFinallyKeyword()
	{
		this.lexemeTest("finally", TokenCategories.KEYWORD, JSTokenTypes.FINALLY); //$NON-NLS-1$
		this.lexemeTest("finallys", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testForKeyword
	 */
	public void testForKeyword()
	{
		this.lexemeTest("for", TokenCategories.KEYWORD, JSTokenTypes.FOR); //$NON-NLS-1$
		this.lexemeTest("fors", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testFunctionKeyword
	 */
	public void testFunctionKeyword()
	{
		this.lexemeTest("function", TokenCategories.KEYWORD, JSTokenTypes.FUNCTION); //$NON-NLS-1$
		this.lexemeTest("functions", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testNewKeyword
	 */
	public void testNewKeyword()
	{
		this.lexemeTest("new", TokenCategories.KEYWORD, JSTokenTypes.NEW); //$NON-NLS-1$
		this.lexemeTest("news", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testReturnKeyword
	 */
	public void testReturnKeyword()
	{
		this.lexemeTest("return", TokenCategories.KEYWORD, JSTokenTypes.RETURN); //$NON-NLS-1$
		this.lexemeTest("returns", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testSwitchKeyword
	 */
	public void testSwitchKeyword()
	{
		this.lexemeTest("switch", TokenCategories.KEYWORD, JSTokenTypes.SWITCH); //$NON-NLS-1$
		this.lexemeTest("switchs", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testThisKeyword
	 */
	public void testThisKeyword()
	{
		this.lexemeTest("this", TokenCategories.KEYWORD, JSTokenTypes.THIS); //$NON-NLS-1$
		this.lexemeTest("thiss", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testThrowKeyword
	 */
	public void testThrowKeyword()
	{
		this.lexemeTest("throw", TokenCategories.KEYWORD, JSTokenTypes.THROW); //$NON-NLS-1$
		this.lexemeTest("throws", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testTryKeyword
	 */
	public void testTryKeyword()
	{
		this.lexemeTest("try", TokenCategories.KEYWORD, JSTokenTypes.TRY); //$NON-NLS-1$
		this.lexemeTest("trys", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testTypeofKeyword
	 */
	public void testTypeofKeyword()
	{
		this.lexemeTest("typeof", TokenCategories.KEYWORD, JSTokenTypes.TYPEOF); //$NON-NLS-1$
		this.lexemeTest("typeofs", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testVarKeyword
	 */
	public void testVarKeyword()
	{
		this.lexemeTest("var", TokenCategories.KEYWORD, JSTokenTypes.VAR); //$NON-NLS-1$
		this.lexemeTest("vars", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testConstKeyword
	 */
	public void testConstKeyword()
	{
		this.lexemeTest("const", TokenCategories.KEYWORD, JSTokenTypes.VAR); //$NON-NLS-1$
		this.lexemeTest("consts", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testVoidKeyword
	 */
	public void testVoidKeyword()
	{
		this.lexemeTest("void", TokenCategories.KEYWORD, JSTokenTypes.VOID); //$NON-NLS-1$
		this.lexemeTest("voids", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testWhileKeyword
	 */
	public void testWhileKeyword()
	{
		this.lexemeTest("while", TokenCategories.KEYWORD, JSTokenTypes.WHILE); //$NON-NLS-1$
		this.lexemeTest("whiles", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * testWithKeyword
	 */
	public void testWithKeyword()
	{
		this.lexemeTest("with", TokenCategories.KEYWORD, JSTokenTypes.WITH); //$NON-NLS-1$
		this.lexemeTest("withs", TokenCategories.IDENTIFIER, JSTokenTypes.IDENTIFIER); //$NON-NLS-1$
	}
}
