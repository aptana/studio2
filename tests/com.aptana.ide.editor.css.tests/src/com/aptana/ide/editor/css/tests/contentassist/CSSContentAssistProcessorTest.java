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
package com.aptana.ide.editor.css.tests.contentassist;

import java.io.File;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.Trace;
import com.aptana.ide.editor.css.CSSFileServiceFactory;
import com.aptana.ide.editor.css.CSSLanguageEnvironment;
import com.aptana.ide.editor.css.contentassist.CSSCompletionProposal;
import com.aptana.ide.editor.css.contentassist.CSSContentAssistProcessor;
import com.aptana.ide.editor.css.contentassist.CSSContentAssistProcessorFactory;
import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editor.css.parsing.CSSParser;
import com.aptana.ide.editors.junit.ContentAssistTestCase;
import com.aptana.ide.editors.junit.LexerUtils;
import com.aptana.ide.editors.junit.TestTextViewer;
import com.aptana.ide.editors.junit.TestUtils;
import com.aptana.ide.editors.junit.unified.contentassist.AbstractContentAssistProcessorTest;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.profiles.Profile;
import com.aptana.ide.editors.profiles.ProfileManager;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.FileSourceProvider;
import com.aptana.ide.editors.unified.IFileServiceFactory;
import com.aptana.ide.editors.unified.ParentOffsetMapper;
import com.aptana.ide.editors.unified.contentassist.IContentAssistProcessorFactory;
import com.aptana.ide.editors.unified.contentassist.IUnifiedCompletionProposal;
import com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistProcessor;
import com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * @author Ingo Muschenetz
 */
public class CSSContentAssistProcessorTest extends AbstractContentAssistProcessorTest {

	private ILexer lexer;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		lexer = LexerUtils.createLexer(
			CSSParser.class.getResourceAsStream("/com/aptana/ide/editor/css/resources/css_lexer_1_2.lxr"),	//$NON-NLS-1$
			CSSMimeType.MimeType,
			new int[] { CSSTokenTypes.WHITESPACE }
		);
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		lexer = null;
		super.tearDown();
	}

	public void testHTMLElementsAtRootContentAssist()
	{
	    ContentAssistTestCase tc = new ContentAssistTestCase();
		tc.description = "Show HTML elements at root of CSS content assist";
		tc.documentSource = "h%%";
		tc.fileExtension = "css";
		tc.completionProposals.add(createCSSCompletionProposal(true, "h1", "h1", 1));
		runContentAssistTest(tc);
	}
	
	public void testSTU3959()
	{
		ContentAssistTestCase tc = new ContentAssistTestCase();
		tc.description = "Trailing semicolon not added if followed by semicolon (STU-3959)";
		tc.documentSource = "h1 {\nbackground-attachment: %%;\n}";
		tc.fileExtension = "css";
		tc.completionProposals.add(createCSSCompletionProposal(false, "fixed", "fixed", 0));
		tc.completionProposals.add(createCSSCompletionProposal(false, "scroll", "scroll", 0));
		tc.completionProposals.add(createCSSCompletionProposal(false, "inherit", "inherit", 0));
		runContentAssistTest(tc);
	}
	
	public void testTrailingSemicolonAddedWhenNeeded()
	{
		ContentAssistTestCase tc = new ContentAssistTestCase();
		tc.description = "Trailing semicolon added if not followed by semicolon";
		tc.documentSource = "h1 {\nbackground-attachment: %%\n}";
		tc.fileExtension = "css";
		tc.completionProposals.add(createCSSCompletionProposal(false, "fixed", "fixed;", 0));
		tc.completionProposals.add(createCSSCompletionProposal(false, "scroll", "scroll;", 0));
		tc.completionProposals.add(createCSSCompletionProposal(false, "inherit", "inherit;", 0));
		runContentAssistTest(tc);
	}

	private CSSCompletionProposal createCSSCompletionProposal(boolean defaultSelection, String displayString, String replaceString, int replaceLength)
	{
		CSSCompletionProposal proposal = new CSSCompletionProposal(replaceString, 0, replaceLength, 0, null, displayString, null, null, 0, null, null);
		proposal.setDefaultSelection(defaultSelection);
		return proposal;
	}
	
	private void runContentAssistTest(ContentAssistTestCase tc)
	{

		IFileServiceFactory factory = CSSFileServiceFactory.getInstance();
		IContentAssistProcessorFactory cpFactory = CSSContentAssistProcessorFactory.getInstance();
		String documentSource = tc.documentSource;
		int activationOffset = tc.offset;

		Trace.info("Running " + tc.description); //$NON-NLS-1$

		if (documentSource.indexOf("%%") >= 0) //$NON-NLS-1$
		{
			activationOffset = documentSource.indexOf("%%"); //$NON-NLS-1$
			documentSource = documentSource.replaceAll("\\%\\%", StringUtils.EMPTY); //$NON-NLS-1$ 
		}

		File file = TestUtils.createFileFromString("test", "." + tc.fileExtension, documentSource); //$NON-NLS-1$ //$NON-NLS-2$

		ProfileManager pm = TestUtils.createProfileManager();

		FileSourceProvider fsp = new FileSourceProvider(file);
		FileService fileService = factory.createFileService(fsp);
		FileContextManager.add(fsp.getSourceURI(), fileService);

		IUnifiedContentAssistProcessor cp = cpFactory.getContentAssistProcessor(new EditorFileContext(fileService));
		TestTextViewer viewer = new TestTextViewer(documentSource);

		Profile p = TestUtils.createProfile("test", file, new File[] { file }); //$NON-NLS-1$
		pm.setCurrentProfile(p);

		fileService.forceContentChangedEvent();
		fileService.fireContentChangedEvent(StringUtils.EMPTY, activationOffset, 0);
		
		if (tc.completionProposals == null || tc.invalidCompletionProposals == null)
		{
			fail("You must define both the completion proposals and invalid completion proposal nodes in XML, even if they are empty"); //$NON-NLS-1$
		}

		ICompletionProposal[] proposals = cp.computeCompletionProposals(viewer, activationOffset,
				tc.activationCharacter);
		if ((proposals == null || proposals.length == 0) && tc.completionProposals.size() > 0)
		{
			fail(tc.description);
		}
		else if (proposals.length > 0 && tc.completionProposals.size() == 0)
		{
			fail(tc.description);
		}

		for (int j = 0; j < tc.completionProposals.size(); j++)
		{
			IUnifiedCompletionProposal foundProposal = null;
			IUnifiedCompletionProposal prop = (IUnifiedCompletionProposal) tc.completionProposals.get(j);
			for (int k = 0; k < proposals.length; k++)
			{
				IUnifiedCompletionProposal testProposal = (IUnifiedCompletionProposal) proposals[k];
				if (testProposal.getDisplayString().equals(prop.getDisplayString()))
				{
					foundProposal = testProposal;
				}
			}

			if (foundProposal == null)
			{
				fail(tc.description + ": Unable to find proposal " + prop.getDisplayString()); //$NON-NLS-1$
			}

			assertEquals(tc.description + ": display string " + prop.getDisplayString(), prop.getDisplayString(), //$NON-NLS-1$
					foundProposal.getDisplayString());
			assertEquals(tc.description + ": replacement string " + prop.getDisplayString(), prop.getReplaceString(), //$NON-NLS-1$
					foundProposal.getReplaceString());
			assertEquals(tc.description + ": replacement length " + prop.getDisplayString(), prop //$NON-NLS-1$
					.getReplacementLength(), foundProposal.getReplacementLength());
			assertEquals(tc.description + ": default selection " + prop.getDisplayString(), prop.isDefaultSelection(), //$NON-NLS-1$
					foundProposal.isDefaultSelection());
		}

		for (int j = 0; j < tc.invalidCompletionProposals.size(); j++)
		{
			IUnifiedCompletionProposal prop = (IUnifiedCompletionProposal) tc.invalidCompletionProposals.get(j);
			for (int k = 0; k < proposals.length; k++)
			{
				IUnifiedCompletionProposal testProposal = (IUnifiedCompletionProposal) proposals[k];
				if (testProposal.getReplaceString().equals(prop.getReplaceString()))
				{
					fail("Found proposal " + testProposal.getDisplayString() + " that should not exist"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}
	
	/**
	 * Test method for 'com.aptana.ide.editor.css.contentassist.CSSContentAssistProcessor.getLocation(int, int, LexemeList, char)'
	 */
	public void testGetLocation() {

		CSSContentAssistProcessor cp = new CSSContentAssistProcessor(null);

		//String testString = "a#test, d.div, div a:hover { \r\n background-position:top; \r\n margin-top:; \r\n font-family: \"Times New Roman\", sans-serif } ";
		String testString = "a#test { \r\n background-position:top; \r\n margin-top:; \r\n font-family: default, \"Times New Roman\", sans-serif } "; //$NON-NLS-1$

		// need to test ".", "#", ".blah{" (ERROR), "#blah{" (ERROR)
		
		LexemeList ll = LexerUtils.createLexemeList(lexer, testString);
		
		assertEquals(CSSContentAssistProcessor.OUTSIDE_RULE, getLocation(cp, 0, ll, testString));
		assertEquals("", cp.getPropertyPrefix()); //$NON-NLS-1$
		assertEquals("", cp.getValuePrefix()); //$NON-NLS-1$

		assertEquals(CSSContentAssistProcessor.OUTSIDE_RULE, getLocation(cp, testString.length(), ll, testString));
		assertEquals("", cp.getPropertyPrefix()); //$NON-NLS-1$
		assertEquals("", cp.getValuePrefix()); //$NON-NLS-1$
		
		int openBraceIndex = testString.indexOf("{"); //$NON-NLS-1$
		assertEquals(CSSContentAssistProcessor.OUTSIDE_RULE, getLocation(cp, openBraceIndex - 1, ll, testString));	
		assertEquals("#test", cp.getPropertyPrefix()); //$NON-NLS-1$
		assertEquals("", cp.getValuePrefix()); //$NON-NLS-1$

		assertEquals(CSSContentAssistProcessor.INSIDE_RULE, getLocation(cp, openBraceIndex + 1, ll, testString));		
		assertEquals("", cp.getPropertyPrefix()); //$NON-NLS-1$
		assertEquals("", cp.getValuePrefix()); //$NON-NLS-1$

		int closeBraceIndex = testString.indexOf("}"); //$NON-NLS-1$
		assertEquals(CSSContentAssistProcessor.ARG_ASSIST, getLocation(cp, closeBraceIndex - 1, ll, testString));
		assertEquals("font-family", cp.getPropertyPrefix());		 //$NON-NLS-1$
		assertEquals("sans-serif", cp.getValuePrefix()); //$NON-NLS-1$

		assertEquals(CSSContentAssistProcessor.OUTSIDE_RULE, getLocation(cp, closeBraceIndex + 1, ll, testString));
		assertEquals("", cp.getPropertyPrefix()); //$NON-NLS-1$
		assertEquals("", cp.getValuePrefix()); //$NON-NLS-1$
		
		int semicolonIndex = testString.indexOf(";"); //$NON-NLS-1$
		assertEquals(CSSContentAssistProcessor.ARG_ASSIST, getLocation(cp, semicolonIndex - 1, ll, testString));	
		assertEquals("background-position", cp.getPropertyPrefix()); //$NON-NLS-1$
		assertEquals("top", cp.getValuePrefix()); //$NON-NLS-1$

		assertEquals(CSSContentAssistProcessor.INSIDE_RULE, getLocation(cp, semicolonIndex + 1, ll, testString));
		assertEquals("", cp.getPropertyPrefix()); //$NON-NLS-1$
		assertEquals("", cp.getValuePrefix()); //$NON-NLS-1$
		// should have list of attribute proposals here

		int fontIndex = testString.indexOf(": "); //$NON-NLS-1$
		assertEquals(CSSContentAssistProcessor.INSIDE_RULE, getLocation(cp, fontIndex - 1, ll, testString));		
		assertEquals("font-family", cp.getPropertyPrefix()); //$NON-NLS-1$
		assertEquals("", cp.getValuePrefix()); // it has a value, but we'd have to look forward for that //$NON-NLS-1$

		assertEquals(CSSContentAssistProcessor.ARG_ASSIST, getLocation(cp, fontIndex + 1, ll, testString));	
		assertEquals("font-family", cp.getPropertyPrefix()); //$NON-NLS-1$
		assertEquals("", cp.getValuePrefix()); //$NON-NLS-1$

		int marginIndex = testString.indexOf(":;"); //$NON-NLS-1$
		assertEquals(CSSContentAssistProcessor.ARG_ASSIST, getLocation(cp, marginIndex + 1, ll, testString));	
		assertEquals("margin-top", cp.getPropertyPrefix()); //$NON-NLS-1$
		assertEquals("", cp.getValuePrefix()); //$NON-NLS-1$

		// This won't work until I get multiple rules in
		/*
		String[] prefixes = new String[] { "a", "#test", "d", ".div", "div ", "hover" };
		for(int i = 0; i < prefixes.length; i++)
		{
			String pref = prefixes[i];
			int prefIndex = testString.indexOf(pref);
			int offset = prefIndex + pref.length();
			Lexeme prev = UnifiedContentAssistProcessor.getPreviousLexemeOfType(offset, new int[] {CSSTokenTypes.IDENTIFIER, CSSTokenTypes.CLASS, CSSTokenTypes.HASH}, ll, false);
			if(prev != null)
				assertEquals(pref, pref.trim(), prev.getText());
		}
		*/
	}
	
	/**
	 * testInsertIntoError
	 */
	public void testInsertIntoError()
	{
		// Create a new document to apply this to here
		Document doc = new Document();
		
		String beforeText = "h6#{ b"; //$NON-NLS-1$
		//String afterText = "h6#b{ b";

		doc.set(beforeText);
		LexemeList ll = LexerUtils.createLexemeList(lexer, doc.get());
		
		CSSContentAssistProcessor cp = new CSSContentAssistProcessor(null);
		char previousChar = UnifiedContentAssistProcessor.getActivationChar(doc.get(), 3, cp.getCompletionProposalAllActivationCharacters());
		ICompletionProposal[] cps = cp.computeInnerCompletionProposals(null, 3, ll.getLexemeIndex(3), ll, '\0', previousChar);
		
		assertEquals(0, cps.length);
		
		/*
		 * For right now, we assume we can't insert into an error state
		{
			ICompletionProposal c = cps[i];
			if(c.getDisplayString().equals("b"))
			{
				c.apply(doc);
				assertEquals(afterText, doc.get());
			}
		}
		*/
	
	}
	
	/**
	 * getLocation
	 *
	 * @param cp
	 * @param offset
	 * @param ll
	 * @param source
	 * @return String
	 */
	protected String getLocation(CSSContentAssistProcessor cp, int offset, LexemeList ll, String source)
	{
		char activation = UnifiedContentAssistProcessor.getActivationChar(source, offset, cp.getCompletionProposalAutoActivationCharacters(), cp.getCompletionProposalPrivateActivationCharacters());
		int index = ParentOffsetMapper.getLexemeIndexFromDocumentOffset(offset, ll);
		String location = cp.getLocation(offset, index, ll, activation);
		cp.setPrefixes(location);
		return location;
	}

	/**
	 * getLocation
	 *
	 * @param cp
	 * @param offset
	 * @param ll
	 * @param activation
	 * @return String
	 */
	protected String getLocation(CSSContentAssistProcessor cp, int offset, LexemeList ll, char activation)
	{
		return cp.getLocation(offset, ll.getLexemeIndex(offset), ll, activation);
	}

	/**
	 * Test method for 'com.aptana.ide.editor.css.contentassist.CSSContentAssistProcessor.getSpecificPropertyCompletionProposals(String, String, Lexeme)'
	 */
	public void testGetSpecificPropertyCompletionProposals() {

		// need to test ".", "#", ".blah{" (ERROR), "#blah{" (ERROR)		
		String testString = "a#test { \r\n background:url(test.jpg); \r\n background-position:top;\r\n border } "; //$NON-NLS-1$
		LexemeList ll = LexerUtils.createLexemeList(lexer, testString);
		CSSContentAssistProcessor cp = new CSSContentAssistProcessor(null);
		CSSLanguageEnvironment.getInstance().loadEnvironment();

		//url is a keyword, which needs to be treated specially
		assertSpecificPropertyCompletionLength("url", testString, ll, cp, 13); //$NON-NLS-1$
		assertSpecificPropertyCompletionLength("top", testString, ll, cp, 3); //$NON-NLS-1$

		/* Need to test
		<token category="KEYWORD" type="IMPORT">
		<regex>@import</regex>
		</token>
		<token category="KEYWORD" type="PAGE">
			<regex>@page</regex>
		</token>
		<token category="KEYWORD" type="MEDIA">
			<regex>@media</regex>
		</token>
		<token category="KEYWORD" type="CHARSET">
			<regex>@charset</regex>
		</token>
		<token category="KEYWORD" type="URL">
			<regex>url\([^\)]*\)</regex>
		</token>
		<token category="KEYWORD" type="IMPORTANT">
			<regex>!\s*important</regex>
		</token>
		*/
	}

	/**
	 * assertSpecificPropertyCompletionLength
	 *
	 * @param key
	 * @param testString
	 * @param ll
	 * @param cp
	 * @param replacementLength
	 */
	public void assertSpecificPropertyCompletionLength(String key, String testString, LexemeList ll, CSSContentAssistProcessor cp, int replacementLength)
	{
		int index = testString.indexOf(key);
		int currentIndex = cp.computeCurrentLexemeIndex(index, ll);
		
		char previousChar = UnifiedContentAssistProcessor.getActivationChar(testString, testString.length(), cp.getCompletionProposalAllActivationCharacters());
		ICompletionProposal[] proposals = cp.computeInnerCompletionProposals(null, index, currentIndex, ll, UnifiedContentAssistProcessor.DEFAULT_CHARACTER, previousChar );
		IUnifiedCompletionProposal prop = (IUnifiedCompletionProposal)proposals[0];
		assertEquals(replacementLength, prop.getReplacementLength());

	}

	/**
	 * Test method for 'com.aptana.ide.editor.css.contentassist.CSSContentAssistProcessor.getAllPropertiesCompletionProposals(String, Lexeme)'
	 */
	public void testAddColon() {		
		// need to test ".", "#", ".blah{" (ERROR), "#blah{" (ERROR)		
		String testString = "a#test { \r\n background-position:top;\r\n border } "; //$NON-NLS-1$
		LexemeList ll = LexerUtils.createLexemeList(lexer, testString);
		CSSContentAssistProcessor cp = new CSSContentAssistProcessor(null);
		CSSLanguageEnvironment.getInstance().loadEnvironment();

		Lexeme current = ll.getLexemeFromOffset(testString.length());
		boolean addColon = cp.addColon(current, ll);
		assertFalse(addColon);

		current = ll.getFloorLexeme(testString.length());
		addColon = cp.addColon(current, ll);
		assertFalse(addColon);

		current = ll.getFloorLexeme(0);
		addColon = cp.addColon(current, ll);
		assertTrue(addColon);
	}
	
	/**
	 * Test method for 'com.aptana.ide.editor.css.contentassist.CSSContentAssistProcessor.getAllPropertiesCompletionProposals(String, Lexeme)'
	 */
	public void testGetAllPropertiesCompletionProposals() {
		

		// need to test ".", "#", ".blah{" (ERROR), "#blah{" (ERROR)		
		//String testString = "a#test { \r\n background-position:top;\r\n border } ";
		//LexemeList ll = LexerUtils.createLexemeList(lexer, testString);
		//CSSContentAssistProcessor cp = new CSSContentAssistProcessor(null);
		CSSLanguageEnvironment.getInstance().loadEnvironment();

		/*
		 * We no longer add a colon at the end of a completion proposal
		Lexeme current = ll.getLexemeFromOffset(44);
		boolean addColon = cp.addColon(current, ll);
		ICompletionProposal[] proposals = cp.getAllPropertiesCompletionProposals("border", current, addColon);
		IUnifiedCompletionProposal prop = (IUnifiedCompletionProposal)proposals[0];
		assertTrue(prop.getReplaceString().endsWith(":"));

		current = ll.getLexemeFromOffset(18);
		addColon = cp.addColon(current, ll);
		proposals = cp.getAllPropertiesCompletionProposals("background-position", current, addColon);
		prop = (IUnifiedCompletionProposal)proposals[0];
		assertFalse(prop.getReplaceString().endsWith(":"));
		*/
	}

	/**
	 * Test method for 'com.aptana.ide.editor.css.contentassist.CSSContentAssistProcessor.getCurrentLexeme(int)'
	 */
	public void testGetCurrentLexeme() {

		String testString = "a#test { \r\n background-position:top; \r\n margin-top:;\r\nbackground:url(test.jpg);\r\n font-family: \"Times New Roman\", sans-serif } "; //$NON-NLS-1$
		LexemeList ll = LexerUtils.createLexemeList(lexer, testString);		
		CSSContentAssistProcessor cp = new CSSContentAssistProcessor(null);
		assertCorrectLexemeOffset(cp, 0, ll, "a"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, 1, ll, "#test"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, 6, ll, "#test"); //$NON-NLS-1$
		
		int offset = testString.indexOf("{"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, offset, ll, "#test"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, offset + 1, ll, "{"); //$NON-NLS-1$

		String toSearch = "background-position"; //$NON-NLS-1$
		offset = testString.indexOf(toSearch);
		assertCorrectLexemeOffset(cp, offset, ll, toSearch);
		assertCorrectLexemeOffset(cp, offset + toSearch.length(), ll, toSearch);
		assertCorrectLexemeOffset(cp, offset + toSearch.length() + 1, ll, "top"); // other side of ":" //$NON-NLS-1$

		toSearch = ";"; //$NON-NLS-1$
		offset = testString.indexOf(toSearch);
		assertCorrectLexemeOffset(cp, offset, ll, "top"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, offset + toSearch.length(), ll, ";"); //$NON-NLS-1$

		toSearch = ":;"; //$NON-NLS-1$
		offset = testString.indexOf(toSearch);
		assertCorrectLexemeOffset(cp, offset, ll, "margin-top"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, offset + 1, ll, ":"); //$NON-NLS-1$

		toSearch = ":url"; //$NON-NLS-1$
		offset = testString.indexOf(toSearch);
		assertCorrectLexemeOffset(cp, offset, ll, "background"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, offset + 1, ll, "url(test.jpg)"); //$NON-NLS-1$

		toSearch = "sans-serif"; //$NON-NLS-1$
		offset = testString.indexOf(toSearch);
		assertCorrectLexemeOffset(cp, offset, ll, "sans-serif"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, offset + toSearch.length(), ll, "sans-serif"); //$NON-NLS-1$
		assertCorrectLexemeOffset(cp, offset + toSearch.length() + 1, ll, "sans-serif"); //$NON-NLS-1$

	}
	
	/**
	 * assertCorrectLexemeOffset
	 *
	 * @param cp
	 * @param documentOffset
	 * @param ll
	 * @param lexemeText
	 */
	public void assertCorrectLexemeOffset(CSSContentAssistProcessor cp, int documentOffset, LexemeList ll, String lexemeText)
	{
		int newLexemeIndex = cp.computeCurrentLexemeIndex(documentOffset, ll);
		Lexeme l = ll.get(newLexemeIndex);
		assertEquals(lexemeText, l.getText());
	}
}
