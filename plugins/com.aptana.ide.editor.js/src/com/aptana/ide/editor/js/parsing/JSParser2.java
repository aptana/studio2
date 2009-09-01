/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.editor.js.parsing;

import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.jscomment.parsing.JSCommentMimeType;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editors.unified.folding.GenericCommentNode;
import com.aptana.ide.editors.unified.parsing.UnifiedLRParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.bnf.LRParserBuilder;
import com.aptana.ide.parsing.bnf.nodes.GrammarNode;

/**
 * @author Kevin Lindsey
 */
public class JSParser2 extends UnifiedLRParser
{
	private static final String DEFAULT_GROUP = "default"; //$NON-NLS-1$

	/**
	 * CSSParser 2
	 * 
	 * @throws ParserInitializationException
	 */
	public JSParser2() throws ParserInitializationException
	{
		this(JSMimeType.MimeType);
	}

	/**
	 * CSSParser 2
	 * 
	 * @param language
	 * @throws ParserInitializationException
	 */
	public JSParser2(String language) throws ParserInitializationException
	{
		super(language);
		
		// TODO: This needs to be generalized
		this.addHandler(new JSASTHandler());
	}
	
	/**
	 * Advance to the next lexeme in the lexeme stream
	 * 
	 * @throws LexerException
	 */
	protected void advance() throws LexerException
	{
		ILexer lexer = this.getLexer();
		Lexeme currentLexeme = EOS;

//		if (this._currentParentNode != null && this.currentLexeme != null && this.currentLexeme != EOS)
//		{
//			this._currentParentNode.includeLexemeInRange(this.currentLexeme);
//		}

		if (lexer.isEOS() == false)
		{
			boolean inWhitespace = true;
			boolean lastWasEOL = false;

			while (inWhitespace)
			{
				if (lexer.isEOS() == false)
				{
					currentLexeme = this.getNextLexemeInLanguage();
					
					if (currentLexeme != null && currentLexeme != EOS)
					{
						if (currentLexeme.typeIndex != JSTokenTypes.LINE_TERMINATOR)
						{
							// add all non-EOL lexemes to our final list for
							// display purposes
							this.addLexeme(currentLexeme);
						}

						// determine if token is in the WHITESPACE category
						if (currentLexeme.getCategoryIndex() == TokenCategories.WHITESPACE)
						{
							if (currentLexeme.typeIndex == JSTokenTypes.CDC	|| currentLexeme.typeIndex == JSTokenTypes.CDO)
							{
								GenericCommentNode node = new GenericCommentNode(
									currentLexeme.getStartingOffset(),
									currentLexeme.getEndingOffset(), "HTMLCOMMENT", JSMimeType.MimeType); //$NON-NLS-1$
								this.getParseState().addCommentRegion(node);
							}
							
							lastWasEOL = (currentLexeme.typeIndex == JSTokenTypes.LINE_TERMINATOR);
						}
						else
						{
							inWhitespace = false;

							if (lastWasEOL)
							{
								currentLexeme.setAfterEOL();
							}
						}
					}
					else
					{
						// couldn't recover from error, so mark as end of stream
						// NOTE: We may want to throw an exception here since we
						// should be able to return at least an ERROR token
						currentLexeme = EOS;
						inWhitespace = false;
					}
				}
				else
				{
					// we've reached the end of the source text
					currentLexeme = EOS;
					inWhitespace = false;
				}
			}
		}
		
		this.currentLexeme = currentLexeme;
	}
	
	/**
	 * @see com.aptana.ide.parsing.AbstractParser#createParseState(com.aptana.ide.parsing.IParseState)
	 */
	public IParseState createParseState(IParseState parent)
	{
		IParseState result;
		IParseState root = parent;

		if (parent == null)
		{
			result = new JSParseState();
			root = result.getRoot();
		}
		else
		{
			result = new JSParseState(root);
		}

		// get nested parsers
		IParser jsCommentParser = this.getParserForMimeType(JSCommentMimeType.MimeType);
		IParser scriptDocParser = this.getParserForMimeType(ScriptDocMimeType.MimeType);

		// add their parser states, if they exist
		if (jsCommentParser != null)
		{
			result.addChildState(jsCommentParser.createParseState(root));
		}
		if (scriptDocParser != null)
		{
			result.addChildState(scriptDocParser.createParseState(root));
		}

		return result;
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.parsing.UnifiedLRParser#getGrammar()
	 */
	public GrammarNode getGrammar()
	{
		GrammarNode result = super.getGrammar();
		
		if (result == null)
		{
			result = LRParserBuilder.createGrammar(this.getClass().getResourceAsStream("/parsing/JS.bnf")); //$NON-NLS-1$
			this._grammar = result;
		}
		
		return result;
	}
	
	/**
	 * @see com.aptana.ide.parsing.AbstractParser#initializeLexer()
	 */
	public void initializeLexer() throws LexerException
	{
		// get lexer
		ILexer lexer = this.getLexer();
		String language = this.getLanguage();

		// ignore whitespace
		lexer.setIgnoreSet(language, new int[] { JSTokenTypes.WHITESPACE });
		lexer.setLanguageAndGroup(language, DEFAULT_GROUP);
	}
}
