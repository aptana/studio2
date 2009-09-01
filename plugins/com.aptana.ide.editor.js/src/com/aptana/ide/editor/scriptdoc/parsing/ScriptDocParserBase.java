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
package com.aptana.ide.editor.scriptdoc.parsing;

import com.aptana.ide.editor.scriptdoc.lexing.ScriptDocTokenTypes;
import com.aptana.ide.editors.unified.parsing.UnifiedParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.ParserInitializationException;

/**
 * @author Kevin Lindsey
 */
public class ScriptDocParserBase extends UnifiedParser
{
	private static final String DEFAULT_GROUP = "default"; //$NON-NLS-1$
	private static final String INDENT_GROUP = "indent"; //$NON-NLS-1$
	private static final String DOCUMENTATION_GROUP = "documentation"; //$NON-NLS-1$
	private static final String IDENTIFIER_GROUP = "identifier"; //$NON-NLS-1$
	
	protected Lexeme _holderLexeme;
	protected ScriptDocParseNode _curNode;
	protected int startingIndex = -1;
	protected int endingIndex = -1;
	
	/**
	 * ScriptDocParserBase
	 * 
	 * @throws ParserInitializationException
	 */
	public ScriptDocParserBase() throws ParserInitializationException
	{
		this(ScriptDocMimeType.MimeType);
	}

	/**
	 * ScriptDocParserBase
	 * 
	 * @param language
	 * @throws ParserInitializationException
	 */
	public ScriptDocParserBase(String language) throws ParserInitializationException
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

		if (lexer.isEOS() == false)
		{
			advancePastStarNewlines();

			if (this._holderLexeme != EOS)
			{
				this.addLexeme(this._holderLexeme);
			}

			currentLexeme = this._holderLexeme;

			// need to manually switch groups in case there was caching
			switch (currentLexeme.getCategoryIndex())
			{
				case TokenCategories.PUNCTUATOR:
					switch (currentLexeme.typeIndex)
					{
						case ScriptDocTokenTypes.LCURLY:
							lexer.setGroup(IDENTIFIER_GROUP);
							break;

						case ScriptDocTokenTypes.DOLLAR_LCURLY:
							lexer.setGroup(IDENTIFIER_GROUP);
							break;

						case ScriptDocTokenTypes.RCURLY:
							lexer.setGroup(DOCUMENTATION_GROUP);
							break;

						default:
							break;
					}
					break;

				case TokenCategories.DELIMITER:
					switch (currentLexeme.typeIndex)
					{
						case ScriptDocTokenTypes.START_DOCUMENTATION:
							lexer.setGroup(DOCUMENTATION_GROUP);
							break;

						case ScriptDocTokenTypes.END_DOCUMENTATION:
							this._curNode.includeLexemeInRange(this.currentLexeme);
							lexer.setGroup("default"); //$NON-NLS-1$
							currentLexeme = EOS; // end when hitting endDoc
							break;

						case ScriptDocTokenTypes.STAR:
							lexer.setGroup(DOCUMENTATION_GROUP);
							break;

						default:
							break;
					}
					break;

				default:
					break;
			}
		}
		else
		{
			this._holderLexeme = EOS;
			currentLexeme = EOS;
		}
	}
	
	/**
	 * advancePastStarNewlines
	 * 
	 * @throws LexerException
	 */
	private void advancePastStarNewlines() throws LexerException
	{
		ILexer lexer = this.getLexer();

		// check if we are on a newline

		// get new lexeme
		this._holderLexeme = getNextLexemeInLanguage();

		if (this._holderLexeme == null)
		{
			this._holderLexeme = EOS;
			return;
		}

		while (this._holderLexeme.typeIndex == ScriptDocTokenTypes.STAR
				&& this._holderLexeme.getCategoryIndex() == TokenCategories.DELIMITER)
		{
			this._holderLexeme = getNextLexemeInLanguage();
		}

		// skip and mark newlines, re-tokenize any indent stars (*)
		while (!lexer.isEOS() && this._holderLexeme != null
				&& this._holderLexeme.typeIndex == ScriptDocTokenTypes.LINE_TERMINATOR)
		{
			this._holderLexeme = getNextLexemeInLanguage();

			if (this._holderLexeme != null)
			{
				this._holderLexeme.setAfterEOL();

				// this needs to be a text comparison, as the first time through it hasn't been re-parsed
				if (this._holderLexeme.getText().equals("*")) //$NON-NLS-1$
				{
					// back up and reparse this in a new group - don't forget to switch back
					lexer.setCurrentOffset(this._holderLexeme.offset);

					lexer.setGroup(INDENT_GROUP);
					this._holderLexeme = lexer.getNextLexeme();
					lexer.setGroup(DOCUMENTATION_GROUP);
					this.addLexeme(this._holderLexeme);

					this._holderLexeme = getNextLexemeInLanguage();
				}
			}
		}

		if (this._holderLexeme == null)
		{
			this._curNode.includeLexemeInRange(this.currentLexeme);
			this._holderLexeme = EOS;
		}
		else if (lexer.isEOS())
		{
			if (this._holderLexeme != EOS)
			{
				if (this._curNode == null)
				{
					this._curNode = new ScriptDocParseNode(this._holderLexeme);
				}
				this._curNode.includeLexemeInRange(this._holderLexeme);
				this.addLexeme(this._holderLexeme);
			}

			this._holderLexeme = EOS;
		}
		else if (this._holderLexeme == EOS) // and not eos based on last test (which was for position)
		{
			try
			{
				lexer.setGroup("error"); //$NON-NLS-1$
			}
			catch (LexerException e)
			{
			}

			this._holderLexeme = getNextLexemeInLanguage();

			try
			{
				lexer.setGroup(DOCUMENTATION_GROUP);
			}
			catch (LexerException e)
			{
			}
		}
	}
	
	/**
	 * @see com.aptana.ide.parsing.AbstractParser#createParseState(com.aptana.ide.parsing.IParseState)
	 */
	public IParseState createParseState(IParseState parent)
	{
		IParseState result;

		if (parent == null)
		{
			result = new ScriptDocParseState();
		}
		else
		{
			result = new ScriptDocParseState(parent);
		}

		return result;
	}
	
	/**
	 * @see com.aptana.ide.parsing.AbstractParser#getNextLexemeInLanguage()
	 */
	protected Lexeme getNextLexemeInLanguage() throws LexerException
	{
		ILexer lexer = this.getLexer();
		Lexeme lx = lexer.getNextLexeme();

		// if this is a stale lexeme (from a different language) back up, damage the whole partition, and re-parse.
		if (lx != null && lx != EOS && !lx.getLanguage().equals(ScriptDocMimeType.MimeType))
		{
			LexemeList lexemes = this.getLexemeList();

			lexemes.getAffectedRegion().includeInRange(lx.offset);
			lexemes.getAffectedRegion().includeInRange(lexer.getEOFOffset());
			this.removeLexeme(lx);
			lexer.setCurrentOffset(lx.offset);

			lx = lexer.getNextLexeme();
		}

		if (lx == null)
		{
			lx = EOS;
		}
		if (startingIndex == -1 && lx != EOS)
		{
			startingIndex = lx.getStartingOffset();
		}
		if (lx != EOS)
		{
			endingIndex = lx.getEndingOffset();
		}
		return lx;
	}
	
	/**
	 * @see com.aptana.ide.parsing.AbstractParser#initializeLexer()
	 */
	public void initializeLexer() throws LexerException
	{
		ILexer lexer = this.getLexer();
		String language = this.getLanguage();

		// ignore whitespace
		lexer.setIgnoreSet(language, new int[] { ScriptDocTokenTypes.WHITESPACE });
		lexer.setLanguageAndGroup(language, DEFAULT_GROUP);
	}
}
