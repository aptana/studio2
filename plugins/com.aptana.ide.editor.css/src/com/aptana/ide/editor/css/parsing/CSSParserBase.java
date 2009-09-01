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
package com.aptana.ide.editor.css.parsing;

import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editors.unified.folding.GenericCommentNode;
import com.aptana.ide.editors.unified.parsing.UnifiedParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.ITokenList;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.ParserInitializationException;

/**
 * @author Kevin Lindsey
 */
public class CSSParserBase extends UnifiedParser
{
	private static final String DEFAULT_GROUP = "default"; //$NON-NLS-1$
	private static final String ERROR_GROUP = "error"; //$NON-NLS-1$
	
	protected static IToken PROPERTY_TOKEN;
	protected static IToken SELECTOR_TOKEN;
	
	/**
	 * ScriptDocMimeType
	 * 
	 * @throws ParserInitializationException 
	 */
	public CSSParserBase() throws ParserInitializationException
	{
		this(CSSMimeType.MimeType);
	}
	
	/**
	 * CSSParser Base
	 * 
	 * @param language
	 * @throws ParserInitializationException
	 */
	public CSSParserBase(String language) throws ParserInitializationException
	{
		super(language);
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

		if (lexer.isEOS() == false)
		{
			boolean inWhitespace = true;

			while (inWhitespace)
			{
				if (lexer.isEOS() == false)
				{
					currentLexeme = this.getNextLexemeInLanguage();

					if (currentLexeme == null && lexer.isEOS() == false)
					{
						// Switch to error group.
						// NOTE: We want setGroup's exception to propagate since
						// that indicates an internal inconsistency when it
						// fails
						lexer.setGroup(ERROR_GROUP);

						currentLexeme = lexer.getNextLexeme();
					}

					if (currentLexeme != null)
					{
						if (currentLexeme.typeIndex == CSSTokenTypes.COMMENT)
						{
							this.getParseState().addCommentRegion(
								new GenericCommentNode(
									currentLexeme.getStartingOffset(),
									currentLexeme.getEndingOffset(), 
									"COMMENT", //$NON-NLS-1$
									CSSMimeType.MimeType
								)
							);
						}

						if (currentLexeme.typeIndex == CSSTokenTypes.START_MULTILINE_COMMENT)
						{
							lexer.setCurrentOffset(currentLexeme.offset);
							lexer.setGroup("unclosed_multiline"); //$NON-NLS-1$
							currentLexeme = lexer.getNextLexeme();
						}

						this.addLexeme(currentLexeme);

						if (currentLexeme.typeIndex != CSSTokenTypes.COMMENT
								&& currentLexeme.typeIndex != CSSTokenTypes.MULTILINE_COMMENT)
						{
							inWhitespace = false;
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

		if (parent == null)
		{
			result = new CSSParseState();
		}
		else
		{
			result = new CSSParseState(parent);
		}

		return result;
	}
	
	/**
	 * @see com.aptana.ide.parsing.AbstractParser#initializeLexer()
	 */
	public void initializeLexer() throws LexerException
	{
		ILexer lexer = this.getLexer();
		String language = this.getLanguage();

		// ignore whitespace
		lexer.setIgnoreSet(language, new int[] { CSSTokenTypes.WHITESPACE });
		lexer.setLanguageAndGroup(language, DEFAULT_GROUP);
		
		// find PROPERTY token
		ITokenList tokenList = lexer.getTokenList(CSSMimeType.MimeType);
		
		for (int i = 0; i < tokenList.size(); i++)
		{
			IToken candidate = tokenList.get(i);
			
			switch (candidate.getTypeIndex())
			{
				case CSSTokenTypes.PROPERTY:
					PROPERTY_TOKEN = candidate;
					break;
					
				case CSSTokenTypes.SELECTOR:
					SELECTOR_TOKEN = candidate;
					break;
			}
		}
	}
}
