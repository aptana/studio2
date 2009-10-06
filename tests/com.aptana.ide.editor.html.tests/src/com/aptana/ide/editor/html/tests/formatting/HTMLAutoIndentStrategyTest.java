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
package com.aptana.ide.editor.html.tests.formatting;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;

import com.aptana.ide.editor.html.formatting.HTMLAutoIndentStrategy;
import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParser;
import com.aptana.ide.editors.junit.LexerUtils;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexemeList;

/**
 * @author Ingo Muschenetz
 */
public class HTMLAutoIndentStrategyTest extends TestCase
{

	// private LexemeList lexemeList = new LexemeList();
	private ILexer lexer;

	/**
	 * HTMLAutoIndentStrategyTest
	 * 
	 * @param name
	 */
	public HTMLAutoIndentStrategyTest(String name)
	{
		super(name);

		lexer = LexerUtils.createLexer(
			HTMLParser.class.getResourceAsStream("/com/aptana/ide/editor/html/resources/html_lexer_1_2.lxr"),
			HTMLMimeType.MimeType, //$NON-NLS-1$
			new int[] { HTMLTokenTypes.WHITESPACE });
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editor.html.formatting.HTMLAutoIndentStrategy.quoteItem(IDocument,
	 * DocumentCommand, LexemeList)'
	 */
	public void testQuoteItem()
	{

	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editor.html.formatting.HTMLAutoIndentStrategy.closeTag(DocumentCommand,
	 * LexemeList)'
	 */
	public void testCloseTag()
	{

//		LexemeList lexemeSpecialList = LexerUtils.createLexemeList(lexer, "<html><body scroll=\"yes\"></html>");
//		TestDocumentCommand c = new TestDocumentCommand();
//		c.text = ">";
//		c.offset = 24;

		//assertFalse(HTMLAutoIndentStrategy.closeTag(c, lexemeSpecialList));
	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy.getIndentString()'
	 */
	public void testCanOverwriteBracket()
	{

		Document doc = new Document();
		doc.set("<body>"); //$NON-NLS-1$

		LexemeList lexemeList = LexerUtils.createLexemeList(lexer, doc.get());
		HTMLAutoIndentStrategy ais = new HTMLAutoIndentStrategy(null, null, null);
		assertFalse(ais.canOverwriteBracket('>', 4, doc, lexemeList));
		assertTrue(ais.canOverwriteBracket('>', 5, doc, lexemeList));
		assertFalse(ais.canOverwriteBracket('>', 6, doc, lexemeList));

		doc.set("<body></body>"); //$NON-NLS-1$
		lexemeList = LexerUtils.createLexemeList(lexer, doc.get());
		assertFalse(ais.canOverwriteBracket('>', 4, doc, lexemeList));
		assertTrue(ais.canOverwriteBracket('>', 5, doc, lexemeList));
		assertFalse(ais.canOverwriteBracket('>', 6, doc, lexemeList));
	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy.getIndentString()'
	 */
	public void testCanCloseBracket()
	{

		Document doc = new Document();
		doc.set("<body>"); //$NON-NLS-1$

		HTMLAutoIndentStrategy ais = new HTMLAutoIndentStrategy(null, null, null);
		assertFalse(ais.canCloseBracket('>', doc));
		assertFalse(ais.canCloseBracket('<', doc));
		assertTrue(ais.canCloseBracket('\"', doc));
		assertTrue(ais.canCloseBracket('\'', doc));
	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy.getIndentationString(IDocument,
	 * int, int)'
	 */
	public void testGetIndentationString()
	{

	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy.findEndOfWhiteSpace(IDocument, int,
	 * int)'
	 */
	public void testFindEndOfWhiteSpace()
	{

	}
}
