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
package com.aptana.ide.editors.junit.pairmatching;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParseState;
import com.aptana.ide.editor.html.parsing.HTMLParser;
import com.aptana.ide.editors.junit.ProjectTestUtils;
import com.aptana.ide.editors.junit.TestProject;
import com.aptana.ide.editors.unified.IPairFinder;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.PairMatch;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PairMatchingTest extends TestCase
{
	private HTMLParser _parser;
	private HTMLParseState _parseState;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this._parser = new HTMLParser();
		this._parseState = (HTMLParseState) this._parser.createParseState(null);
	}

	/**
	 * testGetPairMatch
	 * 
	 * @throws IOException 
	 * @throws LexerException 
	 */
	public void testGetPairMatch() throws IOException, LexerException
	{
		// get source
		Path path = ProjectTestUtils.findFileInPlugin(TestProject.PLUGIN_ID, "copyPaste/yahooui_sample.htm");
		String source = FileUtils.readContent(path.toFile());
		
		// parse
		this._parseState.setEditState(source, source, 0, 0);
		this._parser.parse(this._parseState);
		
		// get lexeme list and make sure we have some content
		LexemeList lexemeList = this._parseState.getLexemeList();
		assertTrue("Lexeme list is missing content. This may be due to a failure during parsing", lexemeList.size() > 0);
		
		// grab pair finder
		IPairFinder pairFinder = LanguageRegistry.getPairFinder(HTMLMimeType.MimeType);

		// walk the source, character by character
		for (int i = 0; i < source.length(); i++)
		{
			// look for a pair match
			PairMatch match = pairFinder.findPairMatch(i, this._parseState);
			
			if (match != null)
			{
				Lexeme cursorLexeme = lexemeList.getLexemeFromOffset(i);
				
				if ((cursorLexeme == null && i > 0) || (cursorLexeme != null && cursorLexeme.getStartingOffset() == i))
				{
					cursorLexeme = lexemeList.getLexemeFromOffset(i - 1);
				}
				
				if (cursorLexeme != null)
				{
					String message = "Failed at offset " + i + " Lexeme:" + cursorLexeme.toString();

					assertFalse(message, match.endEnd == match.beginEnd);
					assertFalse(message, match.endStart == match.endEnd);
					assertFalse(message, match.beginStart == match.beginEnd);
					assertFalse(message, match.beginEnd == match.endStart);
					// assertEquals(message, match.beginEnd - match.beginStart, match.endEnd - match.endStart);
					// assertTrue(message, match.endEnd == i || match.beginEnd == i);
				}
			}
		}
	}
}
