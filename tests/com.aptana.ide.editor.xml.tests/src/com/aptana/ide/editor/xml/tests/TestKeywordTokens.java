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
package com.aptana.ide.editor.xml.tests;

import com.aptana.ide.editor.xml.lexing.XMLTokenTypes;
import com.aptana.ide.editor.xml.parsing.XMLMimeType;
import com.aptana.ide.editor.xml.parsing.XMLParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
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
		XMLParser parser = new XMLParser();

		return parser.getLexer();
	}
	
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#getLanguage()
	 */
	protected String getLanguage()
	{
		return XMLMimeType.MimeType;
	}

	/**
	 * testANYKeyword
	 */
	public void testANYKeyword()
	{
		this.lexemeTest("ANY", TokenCategories.KEYWORD, XMLTokenTypes.ANY); //$NON-NLS-1$
		this.lexemeTest("ANYS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testCDATAKeyword
	 */
	public void testCDATAKeyword()
	{
		this.lexemeTest("CDATA", TokenCategories.KEYWORD, XMLTokenTypes.CDATA); //$NON-NLS-1$
		this.lexemeTest("CDATAS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testEMPTYKeyword
	 */
	public void testEMPTYKeyword()
	{
		this.lexemeTest("EMPTY", TokenCategories.KEYWORD, XMLTokenTypes.EMPTY); //$NON-NLS-1$
		this.lexemeTest("EMPTYS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testENTITYKeyword
	 */
	public void testENTITYKeyword()
	{
		this.lexemeTest("ENTITY", TokenCategories.KEYWORD, XMLTokenTypes.ENTITY); //$NON-NLS-1$
		this.lexemeTest("ENTITYS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testENTITIESKeyword
	 */
	public void testENTITIESKeyword()
	{
		this.lexemeTest("ENTITIES", TokenCategories.KEYWORD, XMLTokenTypes.ENTITIES); //$NON-NLS-1$
		this.lexemeTest("ENTITIESS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testFIXEDKeyword
	 */
	public void testFIXEDKeyword()
	{
		this.lexemeTest("#FIXED", TokenCategories.KEYWORD, XMLTokenTypes.FIXED); //$NON-NLS-1$
		this.noLexemeTest("#FIXEDS"); //$NON-NLS-1$
	}

	/**
	 * testIDKeyword
	 */
	public void testIDKeyword()
	{
		this.lexemeTest("ID", TokenCategories.KEYWORD, XMLTokenTypes.ID); //$NON-NLS-1$
		this.lexemeTest("IDS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testIDREFKeyword
	 */
	public void testIDREFKeyword()
	{
		this.lexemeTest("IDREF", TokenCategories.KEYWORD, XMLTokenTypes.IDREF); //$NON-NLS-1$
		this.lexemeTest("IDREFX", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testIDREFSKeyword
	 */
	public void testIDREFSKeyword()
	{
		this.lexemeTest("IDREFS", TokenCategories.KEYWORD, XMLTokenTypes.IDREFS); //$NON-NLS-1$
		this.lexemeTest("IDREFSS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testIMPLIEDKeyword
	 */
	public void testIMPLIEDKeyword()
	{
		this.lexemeTest("#IMPLIED", TokenCategories.KEYWORD, XMLTokenTypes.IMPLIED); //$NON-NLS-1$
		this.noLexemeTest("#IMPLIEDS"); //$NON-NLS-1$
	}

	/**
	 * testNDATAKeyword
	 */
	public void testNDATAKeyword()
	{
		this.lexemeTest("NDATA", TokenCategories.KEYWORD, XMLTokenTypes.NDATA); //$NON-NLS-1$
		this.lexemeTest("NDATAS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testNMTOKENKeyword
	 */
	public void testNMTOKENKeyword()
	{
		this.lexemeTest("NMTOKEN", TokenCategories.KEYWORD, XMLTokenTypes.NMTOKEN); //$NON-NLS-1$
		this.lexemeTest("NMTOKENX", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testNMTOKENSKeyword
	 */
	public void testNMTOKENSKeyword()
	{
		this.lexemeTest("NMTOKENS", TokenCategories.KEYWORD, XMLTokenTypes.NMTOKENS); //$NON-NLS-1$
		this.lexemeTest("NMTOKENSS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testNOTATIONKeyword
	 */
	public void testNOTATIONKeyword()
	{
		this.lexemeTest("NOTATION", TokenCategories.KEYWORD, XMLTokenTypes.NOTATION); //$NON-NLS-1$
		this.lexemeTest("NOTATIONS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testPCDATAKeyword
	 */
	public void testPCDATAKeyword()
	{
		this.lexemeTest("#PCDATA", TokenCategories.KEYWORD, XMLTokenTypes.PCDATA); //$NON-NLS-1$
		this.noLexemeTest("#PCDATAS"); //$NON-NLS-1$
	}

	/**
	 * testPUBLICKeyword
	 */
	public void testPUBLICKeyword()
	{
		this.lexemeTest("PUBLIC", TokenCategories.KEYWORD, XMLTokenTypes.PUBLIC); //$NON-NLS-1$
		this.lexemeTest("PUBLICS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testREQUIREDKeyword
	 */
	public void testREQUIREDKeyword()
	{
		this.lexemeTest("#REQUIRED", TokenCategories.KEYWORD, XMLTokenTypes.REQUIRED); //$NON-NLS-1$
		this.noLexemeTest("#REQUIREDS"); //$NON-NLS-1$
	}

	/**
	 * testSYSTEMKeyword
	 */
	public void testSYSTEMKeyword()
	{
		this.lexemeTest("SYSTEM", TokenCategories.KEYWORD, XMLTokenTypes.SYSTEM); //$NON-NLS-1$
		this.lexemeTest("SYSTEMS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testENCODINGKeywordUsingDoubleQuotes
	 * 
	 * @throws LexerException
	 */
	public void testENCODINGKeywordUsingDoubleQuotes() throws LexerException
	{
		this.lexer.setGroup("xml-declaration"); //$NON-NLS-1$
		this.lexemeTest("encoding=\"utf-8\"", TokenCategories.KEYWORD, XMLTokenTypes.ENCODING); //$NON-NLS-1$
	}
	
	/**
	 * testENCODINGKeywordUsingSingleQuotes
	 * 
	 * @throws LexerException
	 */
	public void testENCODINGKeywordUsingSingleQuotes() throws LexerException
	{
		this.lexer.setGroup("xml-declaration"); //$NON-NLS-1$
		this.lexemeTest("encoding='utf-8'", TokenCategories.KEYWORD, XMLTokenTypes.ENCODING); //$NON-NLS-1$
	}
	
	/**
	 * testSTANDALONEKeywordUsingDoubleQuotes
	 * 
	 * @throws LexerException
	 */
	public void testSTANDALONEKeywordUsingDoubleQuotes() throws LexerException
	{
		this.lexer.setGroup("xml-declaration"); //$NON-NLS-1$
		this.lexemeTest("standalone=\"yes\"", TokenCategories.KEYWORD, XMLTokenTypes.STANDALONE); //$NON-NLS-1$
	}
	
	/**
	 * testSTANDALONEKeywordUsingSingleQuotes
	 * 
	 * @throws LexerException
	 */
	public void testSTANDALONEKeywordUsingSingleQuotes() throws LexerException
	{
		this.lexer.setGroup("xml-declaration"); //$NON-NLS-1$
		this.lexemeTest("standalone='yes'", TokenCategories.KEYWORD, XMLTokenTypes.STANDALONE); //$NON-NLS-1$
	}
	
	/**
	 * testVERSIONKeywordUsingDoubleQuotes
	 * 
	 * @throws LexerException
	 */
	public void testVERSIONKeywordUsingDoubleQuotes() throws LexerException
	{
		this.lexer.setGroup("xml-declaration"); //$NON-NLS-1$
		this.lexemeTest("version=\"1.0\"", TokenCategories.KEYWORD, XMLTokenTypes.VERSION); //$NON-NLS-1$
	}
	
	/**
	 * testVERSIONKeywordUsingSingleQuotes
	 * 
	 * @throws LexerException
	 */
	public void testVERSIONKeywordUsingSingleQuotes() throws LexerException
	{
		this.lexer.setGroup("xml-declaration"); //$NON-NLS-1$
		this.lexemeTest("version='1.0'", TokenCategories.KEYWORD, XMLTokenTypes.VERSION); //$NON-NLS-1$
	}
}
