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
package com.aptana.ide.editor.jscomment.parsing;

import java.text.ParseException;

import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.jscomment.lexing.JSCommentTokenTypes;
import com.aptana.ide.editors.unified.folding.GenericCommentNode;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Robin Debreuil
 */
public class JSCommentParser extends JSCommentParserBase
{
	/**
	 * DEFAULT_GROUP
	 */
	public static final String DEFAULT_GROUP = "default"; //$NON-NLS-1$
	
	/**
	 * ERROR_GROUP
	 */
	public static final String ERROR_GROUP = "error"; //$NON-NLS-1$
	
	/**
	 * MULTI_LINE_GROUP
	 */
	public static final String MULTI_LINE_GROUP = "multiline"; //$NON-NLS-1$
	
	/**
	 * SINGLE_LINE_GROUP
	 */
	public static final String SINGLE_LINE_GROUP = "singleline"; //$NON-NLS-1$
	
	/**
	 * Parses JS comments.
	 * 
	 * @throws ParserInitializationException
	 */
	public JSCommentParser() throws ParserInitializationException
	{
		this(JSCommentMimeType.MimeType);
	}
	
	/**
	 * Parses JS comments.
	 * 
	 * @param mimeType
	 * @throws ParserInitializationException
	 */
	public JSCommentParser(String mimeType) throws ParserInitializationException
	{
		super(mimeType);
	}

	/**
	 * getFollowText
	 * 
	 * @return String
	 */
	private String getFollowText()
	{
		ILexer lexer = this.getLexer();

		if (lexer.getSourceLength() > lexer.getCurrentOffset())
		{
			int len = Math.min(6, lexer.getSourceLength() - lexer.getCurrentOffset());

			return "\"" + lexer.getSource().substring(lexer.getCurrentOffset(), lexer.getCurrentOffset() + len) + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			return "end of document"; //$NON-NLS-1$
		}
	}

	/**
	 * @see com.aptana.ide.parsing.AbstractParser#parseAll(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void parseAll(IParseNode parentNode) throws LexerException
	{
		// make sure to switch over to our language and default group
		ILexer lexer = this.getLexer();
		lexer.setLanguageAndGroup(this.getLanguage(), DEFAULT_GROUP);

		this.parseComment();
	}

	/**
	 * Parse the associated source input text ('source' must be set first).
	 * 
	 * @throws LexerException
	 */
	private void parseComment() throws LexerException
	{
		// make sure first token is a start doc
		this.advance();
		int startingIndex = -1;
		int endingIndex = -1;
		if (this.currentLexeme != EOS)
		{
			// switch to appropriate group based on first lexeme
			if (this.currentLexeme.typeIndex == JSCommentTokenTypes.START_MULTILINE_COMMENT)
			{
				startingIndex = this.currentLexeme.getStartingOffset();
				try
				{
					this.getLexer().setGroup(MULTI_LINE_GROUP);
				}
				catch (LexerException e)
				{
				}
			}
			else if (this.currentLexeme.typeIndex == JSCommentTokenTypes.START_SINGLELINE_COMMENT)
			{
				startingIndex = this.currentLexeme.getStartingOffset();
				try
				{
					this.getLexer().setGroup(SINGLE_LINE_GROUP);
				}
				catch (LexerException e)
				{
				}
			}

			// advance to the end of the file
			while (this.currentLexeme != EOS)
			{
				endingIndex = this.currentLexeme.getEndingOffset();
				advance();
			}
		}

		if (endingIndex != -1 && startingIndex != -1)
		{
			GenericCommentNode node = new GenericCommentNode(startingIndex, endingIndex, "JSCOMMENT", //$NON-NLS-1$
					JSMimeType.MimeType);
			this.getParseState().addCommentRegion(node);
		}
	}

	/**
	 * Throw a parse exception
	 * 
	 * @param message
	 *            The exception message
	 * @throws ParseException
	 */
	protected void throwParseError(String message) throws ParseException
	{
		// determine line number
		LexemeList lexemes = this.getLexemeList();
		int lastValid = (this.currentLexeme == EOS) ? lexemes.size() - 2 : lexemes.size() - 1;

		if (lastValid < 0)
		{
			message = Messages.JSCommentParser_PrematureTagEnd;
		}
		else
		{
			String position = ""; //$NON-NLS-1$

			if (this.currentLexeme != EOS)
			{
				position = " [" + this.currentLexeme.getText() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				position = " [" + getFollowText() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			message = Messages.JSCommentParser_ParseError + position + ": " + message; //$NON-NLS-1$
		}

		throw new ParseException(message, -1);
	}
}
