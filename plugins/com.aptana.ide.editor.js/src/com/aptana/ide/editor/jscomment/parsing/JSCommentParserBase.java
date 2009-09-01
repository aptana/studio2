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
package com.aptana.ide.editor.jscomment.parsing;

import com.aptana.ide.editor.jscomment.lexing.JSCommentTokenTypes;
import com.aptana.ide.editors.unified.parsing.UnifiedParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.ParserInitializationException;

/**
 * @author Kevin Lindsey
 */
public class JSCommentParserBase extends UnifiedParser
{
	private static final String DEFAULT_GROUP = "default"; //$NON-NLS-1$
	
	/**
	 * JSCommentParserBase
	 * 
	 * @throws ParserInitializationException
	 */
	public JSCommentParserBase() throws ParserInitializationException
	{
		this(JSCommentMimeType.MimeType);
	}
	
	/**
	 * JSCommentParserBase
	 * 
	 * @param language
	 * @throws ParserInitializationException
	 */
	public JSCommentParserBase(String language) throws ParserInitializationException
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
		if (this.getLexer().isEOS() == false)
		{
			this.currentLexeme = this.getNextLexemeInLanguage();

			if (this.currentLexeme != null)
			{
				if (this.currentLexeme != EOS)
				{
					this.addLexeme(this.currentLexeme);
				}
			}
			else
			{
				this.currentLexeme = EOS;
			}
		}
		else
		{
			this.currentLexeme = EOS;
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
			result = new JSCommentParseState();
		}
		else
		{
			result = new JSCommentParseState(parent);
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

		// ignore nothing
		lexer.setIgnoreSet(language, new int[] { JSCommentTokenTypes.LINE_TERMINATOR });
		lexer.setLanguageAndGroup(language, DEFAULT_GROUP);
	}
}
