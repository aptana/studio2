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

import java.io.File;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.ide.core.KeyValuePair;
import com.aptana.ide.editor.html.HTMLFileServiceFactory;
import com.aptana.ide.editor.html.HTMLLanguageEnvironment;
import com.aptana.ide.editor.html.contentassist.HTMLCompletionProposal;
import com.aptana.ide.editor.html.contentassist.HTMLContentAssistProcessor;
import com.aptana.ide.editor.html.contentassist.HTMLContentAssistProcessorFactory;
import com.aptana.ide.editor.html.contentassist.HTMLContextLocation;
import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParser;
import com.aptana.ide.editors.junit.LexerUtils;
import com.aptana.ide.editors.junit.TestUtils;
import com.aptana.ide.editors.junit.unified.contentassist.AbstractContentAssistProcessorTest;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.FileSourceProvider;
import com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * @author Ingo Muschenetz
 */
public class HTMLContentAssistProcessorTest extends AbstractContentAssistProcessorTest
{

	/**
	 * testComputeCompletionProposals
	 */
	public void testComputeCompletionProposals()
	{
		computeCompletionProposals("/com/aptana/ide/editor/html/tests/contentassist/tests.xml", HTMLFileServiceFactory //$NON-NLS-1$
				.getInstance(), HTMLContentAssistProcessorFactory.getInstance());
	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.html.contentassist.HTMLContentAssistProcessor.getLocation(int, int,
	 * LexemeList)'
	 */
	public void testGetLocation()
	{
		String testString = "<html><head></head><body dup=\"1\" dup=\"2\" id=\"test\" onload=\"open()\"><p align=left></p><div scroll='yes' style=></div><script src=\"test\" />[% </body></html>"; //$NON-NLS-1$
		File file = TestUtils.createFileFromString("test", ".html", testString);			 //$NON-NLS-1$ //$NON-NLS-2$
		
		TestUtils.createProfileManager();
		FileSourceProvider fsp = new FileSourceProvider(file);
		FileService fileService = HTMLFileServiceFactory.getInstance().createFileService(fsp);
		FileContextManager.add(fsp.getSourceURI(), fileService);		
		EditorFileContext context = new EditorFileContext(fileService);

		LexemeList ll = context.getLexemeList(); // offset 7. Replacement
		// length is 7

		HTMLLanguageEnvironment.getInstance().loadEnvironment();
		HTMLContentAssistProcessor cp = new HTMLContentAssistProcessor(context);

		assertEquals(HTMLContentAssistProcessor.OUTSIDE_ELEMENT, getLocation(cp, 0, ll, testString, null, null, null));

		assertEquals(HTMLContentAssistProcessor.OUTSIDE_ELEMENT, getLocation(cp, testString.length(), ll, testString,
				null, null, null));

		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, 1, ll, testString, "html", null, //$NON-NLS-1$
				null));
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, 5, ll, testString, "html", null, //$NON-NLS-1$
				null)); // <htmlI>

		int lexemeIndex = testString.indexOf("</head>"); //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.OUTSIDE_ELEMENT, getLocation(cp, lexemeIndex, ll, testString, null,
				null, null)); // I</head>
		assertEquals(HTMLContentAssistProcessor.INSIDE_END_TAG, getLocation(cp, lexemeIndex + 2, ll, testString,
				"head", null, null)); // </Ihead> //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.INSIDE_END_TAG, getLocation(cp, lexemeIndex + 3, ll, testString,
				"head", null, null)); // </hIead> //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.INSIDE_END_TAG, getLocation(cp, lexemeIndex + 6, ll, testString,
				"head", null, null)); // </headI> //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.OUTSIDE_ELEMENT, getLocation(cp, lexemeIndex + 7, ll, testString, null,
				null, null)); // </head>I

		lexemeIndex = testString.indexOf("dup"); //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex - 1, ll, testString,
				"body", null, null)); // Iid="test" //$NON-NLS-1$

		lexemeIndex = testString.indexOf("id"); //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex, ll, testString,
				"body", null, null)); // Iid="test" //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 1, ll, testString,
				"body", "id", null)); // iId="test" //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 2, ll, testString,
				"body", "id", null)); // idI="test" //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 3, ll, testString,
				"body", "id", "\"test\"")); // id=I"test" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 6, ll, testString,
				"body", "id", "\"test\"")); // id="teIst" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 9, ll, testString,
				"body", "id", "\"test\"")); // id="test"I //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		lexemeIndex = testString.indexOf("align"); //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex, ll, testString, "p", //$NON-NLS-1$
				null, null)); // Ialign=left
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 1, ll, testString,
				"p", "align", null)); // aIlign=left //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 5, ll, testString,
				"p", "align", null)); // alignI=left //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 6, ll, testString,
				"p", "align", "left")); // align=Ileft //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 8, ll, testString,
				"p", "align", "left")); // align=leIft //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 10, ll, testString,
				"p", "align", "left")); // align=leftI //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		lexemeIndex = testString.indexOf("style"); //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex, ll, testString,
				"div", null, null)); // Istyle= //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 1, ll, testString,
				"div", "style", null)); // sItyle= //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 5, ll, testString,
				"div", "style", null)); // styleI= //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 6, ll, testString,
				"div", "style", "")); // style=I //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		lexemeIndex = testString.indexOf("dup=\"2\""); //$NON-NLS-1$
		// first definition of an attribute wins, so value should show up as 1 here, not 2.
		assertEquals(HTMLContentAssistProcessor.INSIDE_OPEN_ELEMENT, getLocation(cp, lexemeIndex + 7, ll, testString,
				"body", "dup", "\"1\"")); // dup="2"I //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		lexemeIndex = testString.indexOf("[%"); //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.OUTSIDE_ELEMENT, cp.getLocation(lexemeIndex + 2, ll).getLocation()); // [% 

	}

	/**
	 * getLocation
	 * 
	 * @param cp
	 * @param offset
	 * @param ll
	 * @param source
	 * @param tagName
	 * @param attributeName
	 * @param attributeValue
	 * @return String
	 */
	protected String getLocation(HTMLContentAssistProcessor cp, int offset, LexemeList ll, String source,
			String tagName, String attributeName, String attributeValue)
	{
		HTMLContextLocation location = cp.getLocation(offset, ll);
		assertEquals(tagName, location.getTagName());
		if (attributeName != null)
		{
			KeyValuePair foundAttribute = location.find(attributeName);
			assertNotNull(foundAttribute);
			assertEquals(attributeValue, foundAttribute.getValue());
		}

		return location.getLocation();
	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.html.contentassist.HTMLContentAssistProcessor.getActivationChar(ITextViewer,
	 * int)'
	 */
	public void testGetActivationChar()
	{
		String source = "<html><head id=\"test\"></head><html>"; //$NON-NLS-1$
		char[] activationChars = new char[] { '<', '/', ' ' };
		assertEquals(HTMLContentAssistProcessor.getActivationChar(source, 0, activationChars),
				HTMLContentAssistProcessor.DEFAULT_CHARACTER);
		assertEquals(HTMLContentAssistProcessor.getActivationChar(source, 1, activationChars), '<');

		int index = source.indexOf("/head"); //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.getActivationChar(source, index + 1, activationChars), '/');

		int spaceIndex = source.indexOf(" "); //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.getActivationChar(source, spaceIndex + 1, activationChars), ' ');
		assertEquals(HTMLContentAssistProcessor.getActivationChar(source, spaceIndex + 2, activationChars),
				UnifiedContentAssistProcessor.DEFAULT_CHARACTER);
	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.html.contentassist.HTMLContentAssistProcessor.getPreviousChar(String,
	 * int)'
	 */
	public void testGetPreviousChar()
	{
		String test = "<html><head></head><html>"; //$NON-NLS-1$
		assertEquals(HTMLContentAssistProcessor.getPreviousChar(test, -1), HTMLContentAssistProcessor.DEFAULT_CHARACTER);
		assertEquals(HTMLContentAssistProcessor.getPreviousChar(test, test.length() + 1),
				HTMLContentAssistProcessor.DEFAULT_CHARACTER);
		assertEquals(HTMLContentAssistProcessor.getPreviousChar(test, 0), HTMLContentAssistProcessor.DEFAULT_CHARACTER);
		assertEquals(HTMLContentAssistProcessor.getPreviousChar(test, 1), '<');
		assertEquals(HTMLContentAssistProcessor.getPreviousChar(test, test.length()), '>');
		try
		{
			HTMLContentAssistProcessor.getPreviousChar(null, 1);
			assertFalse("Did not correctly throw exception on null value for string", true); //$NON-NLS-1$
		}
		catch (IndexOutOfBoundsException ex)
		{
			assertTrue(true);
		}
	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.html.contentassist.HTMLContentAssistProcessor.getPreviousChar(String,
	 * int)'
	 */
	public void testCloseTagCompletions()
	{
		File file = TestUtils.createFileFromString("test", ".html", "<body></body >");			 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		TestUtils.createProfileManager();
		FileSourceProvider fsp = new FileSourceProvider(file);
		FileService fileService = HTMLFileServiceFactory.getInstance().createFileService(fsp);
		FileContextManager.add(fsp.getSourceURI(), fileService);		
		EditorFileContext context = new EditorFileContext(fileService);

		LexemeList test1 = context.getLexemeList(); // offset 7.
		// Replacement
		// length is 7

		char activationChar = UnifiedContentAssistProcessor.DEFAULT_CHARACTER;

		HTMLLanguageEnvironment.getInstance().loadEnvironment();
		HTMLContentAssistProcessor cp = new HTMLContentAssistProcessor(context);
		char previousChar = UnifiedContentAssistProcessor.getActivationChar("<body></body", 7, cp //$NON-NLS-1$
				.getCompletionProposalAllActivationCharacters());
		ICompletionProposal[] cps = cp.computeInnerCompletionProposals(null, 7, test1.getLexemeIndex(7), test1,
				activationChar, previousChar);
		assertNotNull(cps);
		HTMLCompletionProposal cp1 = (HTMLCompletionProposal) cps[0];
		assertEquals(6, cp1.getReplacementOffset());

		previousChar = UnifiedContentAssistProcessor.getActivationChar("<body></body", 8, cp //$NON-NLS-1$
				.getCompletionProposalAllActivationCharacters());
		cps = cp.computeInnerCompletionProposals(null, 8, test1.getLexemeIndex(8), test1, activationChar, previousChar);
		assertNotNull(cps);
		cp1 = (HTMLCompletionProposal) cps[0];
		assertEquals(6, cp1.getReplacementOffset());

		previousChar = UnifiedContentAssistProcessor.getActivationChar("<body></body", 9, cp //$NON-NLS-1$
				.getCompletionProposalAllActivationCharacters());
		cps = cp.computeInnerCompletionProposals(null, 9, test1.getLexemeIndex(9), test1, activationChar, previousChar);
		assertNotNull(cps);
		cp1 = (HTMLCompletionProposal) cps[0];
		assertEquals(6, cp1.getReplacementOffset());

		// No end tag propsal, as the tag is balanced already
		cp1 = (HTMLCompletionProposal) findCompletionProposal(cps, "/body"); //$NON-NLS-1$
		assertNull(cp1);

		// Default selection for a set of propsals where there is no end tag should not
		// force "/" as part of the prefix
		cp1 = (HTMLCompletionProposal) findCompletionProposal(cps, "body"); //$NON-NLS-1$
		assertTrue(cp1.isDefaultSelection());

		previousChar = UnifiedContentAssistProcessor.getActivationChar("<body></body", 13, cp //$NON-NLS-1$
				.getCompletionProposalAllActivationCharacters());
		cps = cp.computeInnerCompletionProposals(null, 13, test1.getLexemeIndex(13), test1, activationChar,
				previousChar);
		assertNull(cps);
	}

	/**
	 * findCompletionProposal
	 * 
	 * @param cps
	 * @param string
	 * @return ICompletionProposal
	 */
	private ICompletionProposal findCompletionProposal(ICompletionProposal[] cps, String string)
	{
		if (cps == null)
		{
			return null;
		}

		for (int i = 0; i < cps.length; i++)
		{
			ICompletionProposal cp = cps[i];
			if (cp.getDisplayString().equals(string))
			{
				return cp;
			}
		}

		return null;
	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.html.contentassist.HTMLContentAssistProcessor.testCombine(char[],
	 * char[])'
	 */
	public void testCombine()
	{

		char[] a = new char[] { 'a', 'b' };
		char[] b = new char[] { 'c', 'd' };
		char[] c = new char[] { 'a', 'b', 'c', 'd' };
		boolean arraysEqual = areArraysEqual(c, HTMLContentAssistProcessor.combine(a, b));
		assertTrue(arraysEqual);
	}

	/**
	 * areArraysEqual
	 * 
	 * @param array1
	 * @param array2
	 * @return boolean
	 */
	private boolean areArraysEqual(char[] array1, char[] array2)
	{
		if (array1.length != array2.length)
		{
			return false;
		}

		for (int i = 0; i < array1.length; i++)
		{
			if (array1[i] != array2[i])
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editor.css.contentassist.CSSContentAssistProcessor.getCurrentLexeme(int)'
	 */
	public void testGetCurrentLexeme()
	{

		ILexer lexer = LexerUtils.createLexer(
			HTMLParser.class.getResourceAsStream("/com/aptana/ide/editor/html/resources/html_lexer_1_2.lxr"),
			HTMLMimeType.MimeType, //$NON-NLS-1$
			new int[] { HTMLTokenTypes.WHITESPACE });
		String testString = "<html><head></head><body dup=\"1\" dup=\"2\" id=\"test\" onload=\"open()\"><p align=left></p><div scroll='yes' style=></div></body></html>"; //$NON-NLS-1$
		LexemeList ll = LexerUtils.createLexemeList(lexer, testString);
		HTMLContentAssistProcessor cp = new HTMLContentAssistProcessor(null);
		assertCorrectLexemeOffset(cp, 0, ll, "<html"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, 5, ll, "<html"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, 6, ll, ">"); //$NON-NLS-1$

		int offset = testString.indexOf("<body"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, offset, ll, ">"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, offset + 5, ll, "<body"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, offset + 6, ll, "dup"); //$NON-NLS-1$

		offset = testString.indexOf("="); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, offset, ll, "dup"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, offset + 1, ll, "\"1\""); //$NON-NLS-1$
	}

	/**
	 * assertCorrectLexemeOffset
	 * 
	 * @param cp
	 * @param documentOffset
	 * @param ll
	 * @param lexemeText
	 */
	public void assertCorrectLexemeOffset(HTMLContentAssistProcessor cp, int documentOffset, LexemeList ll,
			String lexemeText)
	{
		int newLexemeIndex = cp.computeCurrentLexemeIndex(documentOffset, ll);
		Lexeme l = ll.get(newLexemeIndex);
		assertEquals(lexemeText, l.getText());
	}
}
