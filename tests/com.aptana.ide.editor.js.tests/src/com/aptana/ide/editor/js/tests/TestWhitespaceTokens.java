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
public class TestWhitespaceTokens extends TestTokenBase
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
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

		this.lexer.setIgnoreSet(this.lexer.getLanguage(), null);
	}

	/**
	 * testTab
	 */
	public void testTab()
	{
		this.lexemeTest("\t", TokenCategories.WHITESPACE, JSTokenTypes.WHITESPACE); //$NON-NLS-1$
	}

	/**
	 * testSpace
	 */
	public void testSpace()
	{
		this.lexemeTest(" ", TokenCategories.WHITESPACE, JSTokenTypes.WHITESPACE); //$NON-NLS-1$
	}
	
	/**
	 * testComment
	 */
	public void testComment()
	{
		this.lexemeTest("//", TokenCategories.WHITESPACE, JSTokenTypes.COMMENT); //$NON-NLS-1$
	}
	
	/**
	 * testStartMultiLineComment
	 */
	public void testStartMultiLineComment()
	{
		this.lexemeTest("/*", TokenCategories.WHITESPACE, JSTokenTypes.START_MULTILINE_COMMENT); //$NON-NLS-1$
	}
	
	/**
	 * testStartDocumentationComment
	 */
	public void testStartDocumentationComment()
	{
		this.lexemeTest("/**", TokenCategories.WHITESPACE, JSTokenTypes.START_DOCUMENTATION); //$NON-NLS-1$
	}
	
	/**
	 * testReturn
	 */
	public void testReturn()
	{
		this.lexemeTest("\r", TokenCategories.WHITESPACE, JSTokenTypes.LINE_TERMINATOR); //$NON-NLS-1$
	}
	
	/**
	 * testNewline
	 */
	public void testNewline()
	{
		this.lexemeTest("\n", TokenCategories.WHITESPACE, JSTokenTypes.LINE_TERMINATOR); //$NON-NLS-1$
	}
	
	/**
	 * testReturnNewline
	 */
	public void testReturnNewline()
	{
		this.lexemeTest("\r\n", TokenCategories.WHITESPACE, JSTokenTypes.LINE_TERMINATOR); //$NON-NLS-1$
	}
	
	/**
	 * testCDO
	 */
	public void testCDO()
	{
		this.lexemeTest("<!--", TokenCategories.WHITESPACE, JSTokenTypes.CDO); //$NON-NLS-1$
	}
	
	/**
	 * testCDC
	 */
	public void testCDC()
	{
		this.lexemeTest("-->", TokenCategories.WHITESPACE, JSTokenTypes.CDC); //$NON-NLS-1$
	}
}
