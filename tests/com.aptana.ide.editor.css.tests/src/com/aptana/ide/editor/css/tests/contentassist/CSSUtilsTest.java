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

import junit.framework.TestCase;

import com.aptana.ide.editor.css.contentassist.CSSContentAssistProcessor;
import com.aptana.ide.editor.css.contentassist.CSSUtils;
import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editor.css.parsing.CSSParser;
import com.aptana.ide.editors.junit.LexerUtils;
import com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * @author Ingo Muschenetz
 */
public class CSSUtilsTest extends TestCase {

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

	/**
	 * Test method for 'com.aptana.ide.editors.css.contentassist.CSSUtils.getLocation(int, LexemeList)'
	 */
	public void testGetLocation() {
		String testString = "a#test, d.div, div a:hover { \r\n background-position:top; \r\n margin-top:; \r\n font-family: \"Times New Roman\", sans-serif } "; //$NON-NLS-1$
		LexemeList ll = LexerUtils.createLexemeList(lexer, testString);
		assertEquals(CSSContentAssistProcessor.OUTSIDE_RULE, CSSUtils.getLocation(0, ll));
		assertEquals(CSSContentAssistProcessor.OUTSIDE_RULE, CSSUtils.getLocation(testString.length() - 1, ll));
		
		int openBraceIndex = testString.indexOf("{"); //$NON-NLS-1$
		assertEquals(CSSContentAssistProcessor.OUTSIDE_RULE, CSSUtils.getLocation(openBraceIndex - 1, ll));	
		assertEquals(CSSContentAssistProcessor.INSIDE_RULE, CSSUtils.getLocation(openBraceIndex + 1, ll));		

		int closeBraceIndex = testString.indexOf("}"); //$NON-NLS-1$
		assertEquals(CSSContentAssistProcessor.ARG_ASSIST, CSSUtils.getLocation(closeBraceIndex - 1, ll));	
		assertEquals(CSSContentAssistProcessor.OUTSIDE_RULE, CSSUtils.getLocation(closeBraceIndex + 1, ll));
		
		int semicolonIndex = testString.indexOf(";"); //$NON-NLS-1$
		assertEquals(CSSContentAssistProcessor.ARG_ASSIST, CSSUtils.getLocation(semicolonIndex - 1, ll));	
		assertEquals(CSSContentAssistProcessor.INSIDE_RULE, CSSUtils.getLocation(semicolonIndex + 1, ll));

		int fontIndex = testString.indexOf(": "); //$NON-NLS-1$
		assertEquals(CSSContentAssistProcessor.INSIDE_RULE, CSSUtils.getLocation(fontIndex - 1, ll));		
		assertEquals(CSSContentAssistProcessor.ARG_ASSIST, CSSUtils.getLocation(fontIndex + 1, ll));	

		int marginIndex = testString.indexOf(":;"); //$NON-NLS-1$
		assertEquals(CSSContentAssistProcessor.ARG_ASSIST, CSSUtils.getLocation(marginIndex + 1, ll));	

		String[] prefixes = new String[] { "a", "#test", "d", ".div", "div ", "hover" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		for(int i = 0; i < prefixes.length; i++)
		{
			String pref = prefixes[i];
			int prefIndex = testString.indexOf(pref);
			int offset = prefIndex + pref.length();
			Lexeme prev = UnifiedContentAssistProcessor.getPreviousLexemeOfType(offset, new int[] {CSSTokenTypes.IDENTIFIER, CSSTokenTypes.CLASS, CSSTokenTypes.HASH}, ll, false);
			if(prev != null)
			{
				assertEquals(pref, pref.trim(), prev.getText());
			}
		}
	}
}
