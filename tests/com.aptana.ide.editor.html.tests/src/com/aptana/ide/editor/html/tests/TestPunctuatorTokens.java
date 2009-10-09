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

import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.lexer.tests.TestTokenBase;

/**
 * @author Kevin Lindsey
 *
 */
public class TestPunctuatorTokens extends TestTokenBase
{
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#createLexer()
	 */
	protected ILexer createLexer() throws Exception
	{
		HTMLParser parser = new HTMLParser();

		return parser.getLexer();
	}
	
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#getLanguage()
	 */
	protected String getLanguage()
	{
		return HTMLMimeType.MimeType;
	}
	
	/**
	 * testATTLISTPunctuator
	 */
	public void testATTLISTPunctuator()
	{
		this.lexemeTest("<!ATTLIST", TokenCategories.PUNCTUATOR, HTMLTokenTypes.ATTLIST_DECL); //$NON-NLS-1$
		this.lexemeTest("<!ATTLISTS", TokenCategories.LITERAL, HTMLTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testCDATAEndPunctuator
	 */
	public void testCDATAEndPunctuator()
	{
		this.lexemeTest("]]>", TokenCategories.PUNCTUATOR, HTMLTokenTypes.CDATA_END); //$NON-NLS-1$
	}
	
	/**
	 * testCDATAStartPunctuator
	 */
	public void testCDATAStartPunctuator()
	{
		this.lexemeTest("<![CDATA[", TokenCategories.PUNCTUATOR, HTMLTokenTypes.CDATA_START); //$NON-NLS-1$
	}
	
	/**
	 * testDOCTYPEPunctuator
	 */
	public void testDOCTYPEPunctuator()
	{
		this.lexemeTest("<!DOCTYPE", TokenCategories.PUNCTUATOR, HTMLTokenTypes.DOCTYPE_DECL); //$NON-NLS-1$
		this.lexemeTest("<!DOCTYPES", TokenCategories.LITERAL, HTMLTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testELEMENTPunctuator
	 */
	public void testELEMENTPunctuator()
	{
		this.lexemeTest("<!ELEMENT", TokenCategories.PUNCTUATOR, HTMLTokenTypes.ELEMENT_DECL); //$NON-NLS-1$
		this.lexemeTest("<!ELEMENTS", TokenCategories.LITERAL, HTMLTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testEndTagPunctuator
	 */
	public void testEndTagPunctuator()
	{
		this.lexemeTest("</element", TokenCategories.PUNCTUATOR, HTMLTokenTypes.END_TAG); //$NON-NLS-1$
	}
	
	/**
	 * testENTITYPunctuator
	 */
	public void testENTITYPunctuator()
	{
		this.lexemeTest("<!ENTITY", TokenCategories.PUNCTUATOR, HTMLTokenTypes.ENTITY_DECL); //$NON-NLS-1$
		this.lexemeTest("<!ENTITYS", TokenCategories.LITERAL, HTMLTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testEqualPunctuator
	 */
	public void testEqualPunctuator()
	{
		this.lexemeTest("=", TokenCategories.PUNCTUATOR, HTMLTokenTypes.EQUAL); //$NON-NLS-1$
	}
	
	/**
	 * testGreaterPunctuator
	 */
	public void testGreaterPunctuator()
	{
		this.lexemeTest(">", TokenCategories.PUNCTUATOR, HTMLTokenTypes.GREATER_THAN); //$NON-NLS-1$
	}
	
	/**
	 * testLBracketPunctuator
	 */
	public void testLBracketPunctuator()
	{
		this.lexemeTest("[", TokenCategories.PUNCTUATOR, HTMLTokenTypes.LBRACKET); //$NON-NLS-1$
	}
	
	/**
	 * testNOTATIONPunctuator
	 */
	public void testNOTATIONPunctuator()
	{
		this.lexemeTest("<!NOTATION", TokenCategories.PUNCTUATOR, HTMLTokenTypes.NOTATION_DECL); //$NON-NLS-1$
		this.lexemeTest("<!NOTATIONS", TokenCategories.LITERAL, HTMLTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testPercentOpenPunctuator
	 */
	public void testPercentOpenPunctuator()
	{
		this.lexemeTest("<%", TokenCategories.PUNCTUATOR, HTMLTokenTypes.PERCENT_OPEN); //$NON-NLS-1$
	}
	
	/**
	 * testPIOpenPunctuator
	 */
	public void testPIOpenPunctuator()
	{
		this.lexemeTest("<?instruction", TokenCategories.PUNCTUATOR, HTMLTokenTypes.PI_OPEN); //$NON-NLS-1$
	}
	
	/**
	 * testUnnamedPIOpenPunctuator
	 */
	public void testUnnamedPIOpenPunctuator()
	{
		this.lexemeTest("<?", TokenCategories.PUNCTUATOR, HTMLTokenTypes.PI_OPEN); //$NON-NLS-1$
	}
	
	/**
	 * testPlusPunctuator
	 */
	public void testPlusPunctuator()
	{
		this.lexemeTest("+", TokenCategories.PUNCTUATOR, HTMLTokenTypes.PLUS); //$NON-NLS-1$
	}
	
	/**
	 * testQuestionPunctuator
	 */
	public void testQuestionPunctuator()
	{
		this.lexemeTest("?", TokenCategories.PUNCTUATOR, HTMLTokenTypes.QUESTION); //$NON-NLS-1$
	}
	
	/**
	 * testQuestionGreaterPunctuator
	 */
	public void testQuestionGreaterPunctuator()
	{
		this.lexemeTest("?>", TokenCategories.PUNCTUATOR, HTMLTokenTypes.QUESTION_GREATER_THAN); //$NON-NLS-1$
	}
	
	/**
	 * testRBracketPunctuator
	 */
	public void testRBracketPunctuator()
	{
		this.lexemeTest("]", TokenCategories.PUNCTUATOR, HTMLTokenTypes.RBRACKET); //$NON-NLS-1$
	}
	
	/**
	 * testSlashGreaterPunctuator
	 */
	public void testSlashGreaterPunctuator()
	{
		this.lexemeTest("/>", TokenCategories.PUNCTUATOR, HTMLTokenTypes.SLASH_GREATER_THAN); //$NON-NLS-1$
	}
	
	/**
	 * testStarPunctuator
	 */
	public void testStarPunctuator()
	{
		this.lexemeTest("*", TokenCategories.PUNCTUATOR, HTMLTokenTypes.STAR); //$NON-NLS-1$
	}
	
	/**
	 * testStartTagPunctuator
	 */
	public void testStartTagPunctuator()
	{
		this.lexemeTest("<element", TokenCategories.PUNCTUATOR, HTMLTokenTypes.START_TAG); //$NON-NLS-1$
	}
	
	/**
	 * testATTLISTPunctuator
	 */
	public void testXMLPunctuator()
	{
		this.lexemeTest("<?xml", TokenCategories.PUNCTUATOR, HTMLTokenTypes.XML_DECL); //$NON-NLS-1$
	}
}
