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
package com.aptana.ide.editor.html.tests.contentassist;

import java.io.InputStream;
import java.util.Map;

import junit.framework.TestCase;

import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParseState;
import com.aptana.ide.editor.html.parsing.HTMLParser;
import com.aptana.ide.editor.html.parsing.HTMLUtils;
import com.aptana.ide.editors.junit.LexerUtils;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.ParserInitializationException;

/**
 * @author Ingo Muschenetz
 */
public class HTMLUtilsTest extends TestCase
{

	private HTMLParser _parser;
	private HTMLParseState parseState;
	private ILexer lexer;

	private LexemeList getHTML4LexemeList() throws ParserInitializationException, LexerException
	{
		String html = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\"><html><meta /><body style=\"\" onfocus=\"test()\" width='10' id='10 < 20' class='test'><a></a><dd></dd><b></b><input><table /></body></html><code"; //$NON-NLS-1$
		return createLexemeList(html);
	}

	private LexemeList getComplexLexemeList() throws ParserInitializationException, LexerException
	{
		String html = "<html><body style=\"\" id='10 < 20'><div><div></div><span><span></span></span><input><table</body  ></html>"; //$NON-NLS-1$
		return createLexemeList(html);
	}

	private LexemeList getXHTMLLexemeList() throws ParserInitializationException, LexerException
	{
		String xhtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"><html><meta /><body style=\"\" onfocus=\"test()\" width='10' id='10 < 20' class='test'><a></a><dd></dd><b></b><input><table /></body></html>"; //$NON-NLS-1$
		return createLexemeList(xhtml);
	}

	private LexemeList getErrorLexemeList() throws ParserInitializationException, LexerException
	{
		String html = "<html><body style=\"\" id='10 < 20'><a></a><b></b><tr><input><table</body  ></html>"; //$NON-NLS-1$
		return createLexemeList(html);
	}
	
	private LexemeList createLexemeList(String html) throws ParserInitializationException, LexerException
	{
		InputStream stream = getLexerFileInputStream();
		if (stream == null)
			fail("Failed to open Lexer definition XML file stream");
		lexer = LexerUtils.createLexer(stream, HTMLMimeType.MimeType, //$NON-NLS-1$
				new int[] { HTMLTokenTypes.WHITESPACE });
		this._parser = new HTMLParser();
		parseState = new HTMLParseState();
		parseState.setEditState(html, html, 0, 0);
		this._parser.parse(parseState);
		return parseState.getLexemeList();
	}

	private InputStream getLexerFileInputStream()
	{
		return HTMLParser.class.getResourceAsStream("/com/aptana/ide/editor/html/resources/html_lexer_1_2.lxr");
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		this.lexer = null;
		this._parser = null;
		this.parseState = null;
		super.tearDown();
	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.stripTagEndings(String)'
	 */
	public void testStripTagEndings()
	{
		assertEquals("Strip open tag", "a", HTMLUtils.stripTagEndings("<a>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertEquals("Strip ending tag", "a", HTMLUtils.stripTagEndings("</a>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.createOpenTag(String, boolean)'
	 */
	public void testCreateOpenTag()
	{
		assertEquals("Create open tag", "<a>", HTMLUtils.createOpenTag("a", true)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertEquals("Create open tag", "<a", HTMLUtils.createOpenTag("a", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.createCloseTag(String, boolean)'
	 */
	public void testCreateCloseTagStringBoolean()
	{
		assertEquals("Create close tag", "</a>", HTMLUtils.createCloseTag("a", true)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertEquals("Create close tag", "</a>", HTMLUtils.createCloseTag("<a>", true)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertEquals("Create close tag", "</a>", HTMLUtils.createCloseTag("</a>", true)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertEquals("Create close tag", "</a", HTMLUtils.createCloseTag("a", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertEquals("Create close tag", "</a", HTMLUtils.createCloseTag("<a>", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertEquals("Create close tag", "</a", HTMLUtils.createCloseTag("</a>", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.createCloseTag(Lexeme, int, boolean)'
	 */
	public void testCreateCloseTagLexemeIntBoolean() throws Exception
	{

		LexemeList lexemeList = getHTML4LexemeList();
		Lexeme l = HTMLUtils.getFirstLexemeWithText("<html", lexemeList);
		int offset = l.getStartingOffset();

		assertEquals("Create close tag", "</html>", HTMLUtils.createCloseTag(l, offset, true)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Create close tag", "</>", HTMLUtils.createCloseTag(l, offset + 1, true)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Create close tag", "</h>", HTMLUtils.createCloseTag(l, offset + 2, true)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Create close tag", "</ht>", HTMLUtils.createCloseTag(l, offset + 3, true)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Create close tag", "</htm>", HTMLUtils.createCloseTag(l, offset + 4, true)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Create close tag", "</html>", HTMLUtils.createCloseTag(l, offset + 5, true)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testCreateCloseTagXHTMLLexemeIntBoolean() throws Exception
	{

		LexemeList lexemeList = getXHTMLLexemeList();
		Lexeme l = HTMLUtils.getFirstLexemeWithText("<html", lexemeList);
		int offset = l.getStartingOffset();
		assertEquals("Create close tag", "</html>", HTMLUtils.createCloseTag(l, offset, true)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Create close tag", "</>", HTMLUtils.createCloseTag(l, offset + 1, true)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Create close tag", "</h>", HTMLUtils.createCloseTag(l, offset + 2, true)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Create close tag", "</ht>", HTMLUtils.createCloseTag(l, offset + 3, true)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Create close tag", "</htm>", HTMLUtils.createCloseTag(l, offset + 4, true)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Create close tag", "</html>", HTMLUtils.createCloseTag(l, offset + 5, true)); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.getOpenTagName(Lexeme, int)'
	 */
	public void testGetOpenTagName() throws Exception
	{

		LexemeList lexemeList = getHTML4LexemeList();
		Lexeme start = HTMLUtils.getFirstLexemeWithText("<html", lexemeList);
		Lexeme end = HTMLUtils.getFirstLexemeWithText("<meta", lexemeList);
		assertEquals("Get open tag name", "html", HTMLUtils.getOpenTagName(start, start.offset + start.length)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Get open tag name", "meta", HTMLUtils.getOpenTagName(end, end.offset)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Get open tag name", "meta", HTMLUtils.getOpenTagName(end, end.offset + end.length + 1)); //$NON-NLS-1$ //$NON-NLS-2$

		start = HTMLUtils.getFirstLexemeWithText("<html", lexemeList);
		end = HTMLUtils.getFirstLexemeWithText("<meta", lexemeList);
		assertEquals("Get open tag name", "html", HTMLUtils.getOpenTagName(start, start.offset + start.length)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Get open tag name", "meta", HTMLUtils.getOpenTagName(end, end.offset)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Get open tag name", "meta", HTMLUtils.getOpenTagName(end, end.offset + end.length + 1)); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.isEqualSignAlreadyInserted(int, LexemeList)'
	 */
	public void testIsEqualSignAlreadyInserted() throws Exception
	{
		LexemeList lexemeList = getHTML4LexemeList();
		Lexeme start = HTMLUtils.getFirstLexemeWithText("<body", lexemeList);
		assertTrue(
				"Is equal sign already inserted", HTMLUtils.isEqualSignAlreadyInserted(start.getStartingOffset() + 11, lexemeList)); //$NON-NLS-1$
		assertFalse(
				"Is equal sign already inserted", HTMLUtils.isEqualSignAlreadyInserted(start.getStartingOffset() + 10, lexemeList)); //$NON-NLS-1$
	}

	public void testIsEqualSignAlreadyInsertedXHTML() throws Exception
	{
		LexemeList lexemeList = getXHTMLLexemeList();
		Lexeme start = HTMLUtils.getFirstLexemeWithText("<body", lexemeList);
		assertTrue(
				"Is equal sign already inserted", HTMLUtils.isEqualSignAlreadyInserted(start.getStartingOffset() + 11, lexemeList)); //$NON-NLS-1$
		assertFalse(
				"Is equal sign already inserted", HTMLUtils.isEqualSignAlreadyInserted(start.getStartingOffset() + 10, lexemeList)); //$NON-NLS-1$
	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.isTextQuoted(String)'
	 */
	public void testIsTextQuoted()
	{
		assertEquals("Is text quoted", true, HTMLUtils.isTextQuoted("\"a\"")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Is text quoted", true, HTMLUtils.isTextQuoted("'a'")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Is text quoted", false, HTMLUtils.isTextQuoted("'a")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Is text quoted", false, HTMLUtils.isTextQuoted("\"a")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Is text quoted", false, HTMLUtils.isTextQuoted("\"'a'")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.isTagClosed(int, IOffsetMapper)'
	 */
	public void testIsTagClosed() throws Exception
	{
		LexemeList lexemeErrorList = getErrorLexemeList();
		Lexeme l = HTMLUtils.getFirstLexemeWithText("<a", lexemeErrorList); //$NON-NLS-1$
		assertTrue("Is Tag Closed", HTMLUtils.isTagClosed(l, lexemeErrorList) == HTMLUtils.TAG_CLOSED); //$NON-NLS-1$

		l = HTMLUtils.getFirstLexemeWithText("<body", lexemeErrorList); //$NON-NLS-1$
		assertTrue("Is Tag Closed", HTMLUtils.isTagClosed(l, lexemeErrorList) == HTMLUtils.TAG_CLOSED); //$NON-NLS-1$

		l = HTMLUtils.getFirstLexemeWithText("<input", lexemeErrorList); //$NON-NLS-1$
		assertTrue("Is Tag Closed", HTMLUtils.isTagClosed(l, lexemeErrorList) == HTMLUtils.TAG_CLOSED); //$NON-NLS-1$

		l = HTMLUtils.getFirstLexemeWithText("<table", lexemeErrorList); //$NON-NLS-1$
		assertFalse("Is Tag Closed", HTMLUtils.isTagClosed(l, lexemeErrorList) == HTMLUtils.TAG_SELF_CLOSED); //$NON-NLS-1$

		l = HTMLUtils.getFirstLexemeWithText("</body", lexemeErrorList); //$NON-NLS-1$
		assertTrue("Is Tag Closed", HTMLUtils.isTagClosed(l, lexemeErrorList) == HTMLUtils.TAG_CLOSED); //$NON-NLS-1$
	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.isTagBalanced(String, int, LexemeList)'
	 */
	public void testIsTagBalanced() throws Exception
	{
		LexemeList lexemeList = getHTML4LexemeList();
		Lexeme l = HTMLUtils.getFirstLexemeWithText("<a", lexemeList); //$NON-NLS-1$
		assertTrue("Is Tag Balanced", HTMLUtils.isStartTagBalanced(l, lexemeList, parseState)); //$NON-NLS-1$

		l = HTMLUtils.getFirstLexemeWithText("<table", lexemeList); //$NON-NLS-1$
		assertTrue("Is Tag Balanced", HTMLUtils.isStartTagBalanced(l, lexemeList, parseState)); //$NON-NLS-1$

		l = HTMLUtils.getFirstLexemeWithText("<input", lexemeList); //$NON-NLS-1$
		assertTrue("Is Tag Balanced", HTMLUtils.isStartTagBalanced(l, lexemeList, parseState)); //$NON-NLS-1$

		l = HTMLUtils.getFirstLexemeWithText("<code", lexemeList); //$NON-NLS-1$
		assertFalse("Is Tag Balanced", HTMLUtils.isStartTagBalanced(l, lexemeList, parseState)); //$NON-NLS-1$
	}
	
	public void testIsTagBalancedErrrorLexemeList() throws Exception
	{
		LexemeList lexemeErrorList = getErrorLexemeList();
		Lexeme l = HTMLUtils.getFirstLexemeWithText("<tr", lexemeErrorList); //$NON-NLS-1$
		assertFalse("Is Tag Balanced", HTMLUtils.isStartTagBalanced(l, lexemeErrorList, parseState)); //$NON-NLS-1$
	}

	public void testIsTagBalancedXHTML() throws Exception
	{
		LexemeList lexemeListXHTML = getHTML4LexemeList();
		Lexeme l = HTMLUtils.getFirstLexemeWithText("<a", lexemeListXHTML); //$NON-NLS-1$
		assertTrue("Is Tag Balanced", HTMLUtils.isStartTagBalanced(l, lexemeListXHTML, parseState)); //$NON-NLS-1$

		l = HTMLUtils.getFirstLexemeWithText("<table", lexemeListXHTML); //$NON-NLS-1$
		assertTrue("Is Tag Balanced", HTMLUtils.isStartTagBalanced(l, lexemeListXHTML, parseState)); //$NON-NLS-1$

		l = HTMLUtils.getFirstLexemeWithText("<input", lexemeListXHTML); //$NON-NLS-1$
		assertTrue("Is Tag Balanced", HTMLUtils.isStartTagBalanced(l, lexemeListXHTML, parseState)); //$NON-NLS-1$
	}

	public void testIsTagBalancedComplexHTML() throws Exception
	{
		LexemeList lexemeList = getComplexLexemeList();
		// Complex
		Lexeme l = getLexemeWithText("<div", lexemeList, 1); //$NON-NLS-1$
		assertFalse("Is Tag Balanced", HTMLUtils.isStartTagBalanced(l, lexemeList, parseState)); //$NON-NLS-1$

		l = getLexemeWithText("<span", lexemeList, 1); //$NON-NLS-1$
		assertTrue("Is Tag Balanced", HTMLUtils.isStartTagBalanced(l, lexemeList, parseState)); //$NON-NLS-1$

		l = HTMLUtils.getFirstLexemeWithText("<input", lexemeList); //$NON-NLS-1$
		assertTrue("Is Tag Balanced", HTMLUtils.isStartTagBalanced(l, lexemeList, parseState)); //$NON-NLS-1$
	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.getPreviousUnclosedTag(int,
	 * HTMLOffsetMapper)'
	 */
	public void testGetPreviousUnclosedTag() throws Exception
	{
		LexemeList lexemeList = getHTML4LexemeList();
		// have to handle self-closed tags, i.e.: <meta />
		Lexeme l = HTMLUtils.getFirstLexemeWithText("<body", lexemeList); //$NON-NLS-1$
		Lexeme l2 = HTMLUtils.getPreviousUnclosedTag(l, lexemeList, parseState);
		assertEquals("<html", "<html", l2.getText()); //$NON-NLS-1$ //$NON-NLS-2$

		l = HTMLUtils.getFirstLexemeWithText("</a", lexemeList); //$NON-NLS-1$
		l2 = HTMLUtils.getPreviousUnclosedTag(l, lexemeList, parseState);
		assertEquals("<a", "<a", l2.getText()); //$NON-NLS-1$ //$NON-NLS-2$

		l = HTMLUtils.getFirstLexemeWithText("</dd", lexemeList); //$NON-NLS-1$
		l2 = HTMLUtils.getPreviousUnclosedTag(l, lexemeList, parseState);
		assertEquals("<dd", "<dd", l2.getText()); //$NON-NLS-1$ //$NON-NLS-2$

		// Has to cover attributes as well
		l = HTMLUtils.getFirstLexemeWithText("<a", lexemeList); //$NON-NLS-1$
		l2 = HTMLUtils.getPreviousUnclosedTag(l, lexemeList, parseState);
		assertEquals("<body", "<body", l2.getText()); //$NON-NLS-1$ //$NON-NLS-2$

		// Has to cover a self-closing tag (<input>)
		l = HTMLUtils.getFirstLexemeWithText("<input", lexemeList); //$NON-NLS-1$
		l2 = HTMLUtils.getPreviousUnclosedTag(l, lexemeList, parseState);
		assertEquals("<body", "<body", l2.getText()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testGetPreviousUnclosedTagXHTML() throws Exception
	{
		LexemeList lexemeListXHTML = getXHTMLLexemeList();
		Lexeme l = HTMLUtils.getFirstLexemeWithText("<html", lexemeListXHTML); //$NON-NLS-1$
		Lexeme l2 = HTMLUtils.getPreviousUnclosedTag(l, lexemeListXHTML, parseState);
		assertNull(l2); //$NON-NLS-1$ //$NON-NLS-2$

		// have to handle self-closed tags, i.e.: <meta />
		l = HTMLUtils.getFirstLexemeWithText("<body", lexemeListXHTML); //$NON-NLS-1$
		l2 = HTMLUtils.getPreviousUnclosedTag(l, lexemeListXHTML, parseState);
		assertEquals("<html", "<html", l2.getText()); //$NON-NLS-1$ //$NON-NLS-2$

		l = HTMLUtils.getFirstLexemeWithText("</a", lexemeListXHTML); //$NON-NLS-1$
		l2 = HTMLUtils.getPreviousUnclosedTag(l, lexemeListXHTML, parseState);
		assertEquals("<a", "<a", l2.getText()); //$NON-NLS-1$ //$NON-NLS-2$

		l = HTMLUtils.getFirstLexemeWithText("</dd", lexemeListXHTML); //$NON-NLS-1$
		l2 = HTMLUtils.getPreviousUnclosedTag(l, lexemeListXHTML, parseState);
		assertEquals("<dd", "<dd", l2.getText()); //$NON-NLS-1$ //$NON-NLS-2$

		// Has to cover attributes as well
		l = HTMLUtils.getFirstLexemeWithText("<a", lexemeListXHTML); //$NON-NLS-1$
		l2 = HTMLUtils.getPreviousUnclosedTag(l, lexemeListXHTML, parseState);
		assertEquals("<body", "<body", l2.getText()); //$NON-NLS-1$ //$NON-NLS-2$

		// Has to cover a self-closing tag (<input>)
		l = HTMLUtils.getFirstLexemeWithText("<input", lexemeListXHTML); //$NON-NLS-1$
		l2 = HTMLUtils.getPreviousUnclosedTag(l, lexemeListXHTML, parseState);
		assertEquals("<body", "<body", l2.getText()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.getOpenTagLexeme(int, LexemeList)'
	 */
	public void testGetOpenTagLexeme() throws Exception
	{
		LexemeList lexemeList = getHTML4LexemeList();
		Lexeme[] lexemes = HTMLUtils.getLexemesOfType(HTMLTokenTypes.START_TAG, lexemeList, HTMLMimeType.MimeType);
		for (int i = 0; i < lexemes.length; i++)
		{
			Lexeme l = lexemes[i];

			int prevIndex = lexemeList.getLexemeIndex(l);
			if (prevIndex == 0)
			{
				// Handles the case of I<tag>. This should return the first lexeme as we are outside
				// a tag, but at the
				// beginning of the document (special case)
				assertEquals(l, HTMLUtils.getTagOpenLexeme(l.offset, lexemeList));
			}
			else if (prevIndex > 0)
			{
				// Handles the case of I<tag>. This should return null as we are outside a tag
				assertNull(l.getText(), HTMLUtils.getTagOpenLexeme(l.offset, lexemeList));
			}

			// handle test case where offset is at starting, should return previous

			assertNotNull(l.getText(), HTMLUtils.getTagOpenLexeme(l.offset + 1, lexemeList));

			// checks the case of <htmlI>. This should return <html
			assertNotNull(l.getText(), HTMLUtils.getTagOpenLexeme(l.offset + l.length, lexemeList));

			Lexeme next = HTMLUtils.getTagCloseLexeme(l, lexemeList);

			if (next != null)
			{
				assertNotNull(l.getText(), HTMLUtils.getTagOpenLexeme(next.offset, lexemeList));

				// Handles the case of <tag>I<tag>. This should return null as we are outside a tag
				assertNull(l.getText(), HTMLUtils.getTagOpenLexeme(next.getEndingOffset(), lexemeList));
			}
		}

		// check <table /> case as well
		lexemes = HTMLUtils.getLexemesOfType(HTMLTokenTypes.SLASH_GREATER_THAN, lexemeList, HTMLMimeType.MimeType);
		for (int i = 0; i < lexemes.length; i++)
		{
			Lexeme l = lexemes[i];
			assertNull(l.getText(), HTMLUtils.getTagOpenLexeme(l.getEndingOffset(), lexemeList));
		}

	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.insideQuotedString(Lexeme, int)'
	 */
	public void testInsideQuotedString() throws Exception
	{

		LexemeList lexemeList = getHTML4LexemeList();
		Lexeme[] lexemes = HTMLUtils.getLexemesOfType(HTMLTokenTypes.STRING, lexemeList, HTMLMimeType.MimeType);
		for (int i = 0; i < lexemes.length; i++)
		{
			Lexeme l = lexemes[i];
			assertTrue(l.getText(), HTMLUtils.insideQuotedString(l, l.offset));
			assertTrue(l.getText(), HTMLUtils.insideQuotedString(l, l.offset + 1));
			assertFalse(l.getText(), HTMLUtils.insideQuotedString(l, l.offset + l.length));
			assertFalse(HTMLUtils.insideQuotedString(l, 0));
			assertFalse(HTMLUtils.insideQuotedString(l, 1000));
		}

		String test = "<html><body style=\""; //$NON-NLS-1$
		LexemeList lexemeTest = LexerUtils.createLexemeList(lexer, test);
		Lexeme last = lexemeTest.get(lexemeTest.size() - 1);
		assertFalse(HTMLUtils.insideQuotedString(last, test.length()));
	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.insideOpenTag(int, LexemeList)'
	 */
	public void testInsideOpenTag() throws Exception
	{
		LexemeList lexemeList = getHTML4LexemeList();
		Lexeme[] lexemes = HTMLUtils.getLexemesOfType(HTMLTokenTypes.START_TAG, lexemeList, HTMLMimeType.MimeType);
		for (int i = 0; i < lexemes.length; i++)
		{
			Lexeme l = lexemes[i];
			assertTrue(l.getText(), HTMLUtils.insideOpenTag(l.offset + 1, lexemeList));
			assertTrue(l.getText(), HTMLUtils.insideOpenTag(l.offset + l.length, lexemeList));

			Lexeme next = HTMLUtils.getTagCloseLexeme(l, lexemeList);

			if (next != null)
			{
				assertTrue(l.getText(), HTMLUtils.insideOpenTag(next.offset, lexemeList));
			}
		}
	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.isStartTag(Lexeme)'
	 */
	public void testIsStartTag() throws Exception
	{
		LexemeList lexemeList = getHTML4LexemeList();
		Lexeme[] lexemes = HTMLUtils.getLexemesOfType(HTMLTokenTypes.START_TAG, lexemeList, HTMLMimeType.MimeType);
		for (int i = 0; i < lexemes.length; i++)
		{
			Lexeme l = lexemes[i];
			assertTrue(l.getText(), HTMLUtils.isStartTag(l));
		}
	}
	
	public void testIsStartTagErrorLexemeList() throws Exception
	{
		LexemeList lexemeErrorList = getErrorLexemeList();
		Lexeme l = HTMLUtils.getFirstLexemeWithText("<table", lexemeErrorList); //$NON-NLS-1$
		assertTrue(l.getText(), HTMLUtils.isStartTag(l));
	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.isEndTag(Lexeme)'
	 */
	public void testIsEndTag() throws Exception
	{

		LexemeList lexemeList = getHTML4LexemeList();
		Lexeme[] lexemes = HTMLUtils.getLexemesOfType(HTMLTokenTypes.END_TAG, lexemeList, HTMLMimeType.MimeType);
		for (int i = 0; i < lexemes.length; i++)
		{
			Lexeme l = lexemes[i];
			assertTrue(l.getText(), HTMLUtils.isEndTag(l));
		}

		LexemeList ll = LexerUtils.createLexemeList(lexer, "<html></"); //$NON-NLS-1$
		Lexeme l = HTMLUtils.getFirstLexemeWithText("</", ll); //$NON-NLS-1$
		assertTrue(l.getText(), HTMLUtils.isEndTag(l));

	}

	/**
	 * Test method for 'com.aptana.ide.editors.html.contentassist.HTMLUtils.gatherAttributes(String)'
	 */
	public void testGatherAttributes() throws Exception
	{
		LexemeList lexemeList = getHTML4LexemeList();
		Lexeme startLexeme = HTMLUtils.getFirstLexemeWithText("<body", lexemeList); //$NON-NLS-1$
		Lexeme endLexeme = HTMLUtils.getTagCloseLexeme(startLexeme, lexemeList);
		Map<String, String> h = HTMLUtils.gatherAttributes(startLexeme, endLexeme, lexemeList);
		assertEquals("", h.get("style")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("", h.get("onfocus")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("'10'", h.get("width")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("'10 < 20'", h.get("id")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("'test'", h.get("class")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(h.size(), 5);

		h = HTMLUtils.gatherAttributes(null, endLexeme, lexemeList);
		assertEquals(0, h.size());

		h = HTMLUtils.gatherAttributes(startLexeme, null, lexemeList);
		assertEquals(0, h.size());
	}
	
	public void testCreateCloseTagHandlesNullLexeme() throws Exception
	{
		assertNull("Create close tag", HTMLUtils.createCloseTag((Lexeme)null, true)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Gets lexeme with the text specified.
	 * 
	 * @param lexemeText
	 *            - lexeme text
	 * @param lexemeList
	 *            - lexemes list
	 * @param number
	 *            - number of lexeme to get. 0 = first lexeme, 1 = second one, etc.
	 * @return lexeme if found, null otherwise
	 */
	private static Lexeme getLexemeWithText(String lexemeText, LexemeList lexemeList, int number)
	{
		int counter = number;
		for (int i = 0; i < lexemeList.size(); i++)
		{
			Lexeme l = lexemeList.get(i);
			if (l.getText().equals(lexemeText))
			{
				if (counter == 0)
				{
					return l;
				}
				counter--;
			}
		}

		return null;
	}
}
