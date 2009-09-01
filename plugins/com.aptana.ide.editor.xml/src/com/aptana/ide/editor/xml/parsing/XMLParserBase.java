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
package com.aptana.ide.editor.xml.parsing;

import java.text.ParseException;

import com.aptana.ide.editor.xml.lexing.XMLTokenTypes;
import com.aptana.ide.editors.unified.parsing.UnifiedParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class XMLParserBase extends UnifiedParser
{
	private static final String TEXT_GROUP = "text"; //$NON-NLS-1$
	private static final String ERROR_GROUP = "error"; //$NON-NLS-1$
	
	protected static final String DEFAULT_GROUP = "default"; //$NON-NLS-1$
	protected static final String XML_DECLARATION_GROUP = "xml-declaration"; //$NON-NLS-1$
	protected static final String CDATA_SECTION_GROUP = "cdata-section"; //$NON-NLS-1$
	protected static final String PROCESSING_INSTRUCTION_GROUP = "processing-instruction"; //$NON-NLS-1$
	
	public static final String DOCTYPE_DECLARATION_GROUP = "doctype-declaration"; //$NON-NLS-1$
	
	protected IParseNode _currentElement;
	
	/**
	 * XMLParserBase
	 * 
	 * @throws ParserInitializationException
	 */
	public XMLParserBase() throws ParserInitializationException
	{
		this(XMLMimeType.MimeType);
	}
	
	/**
	 * XMLParserBase
	 * 
	 * @param language
	 * @throws ParserInitializationException
	 */
	public XMLParserBase(String language) throws ParserInitializationException
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

		if (this._currentElement != null && this.currentLexeme != null && this.currentLexeme != EOS)
		{
			this._currentElement.includeLexemeInRange(this.currentLexeme);
		}
		
		if (lexer.isEOS() == false)
		{
			boolean inWhitespace = true;

			while (inWhitespace)
			{
				if (lexer.isEOS() == false)
				{
					currentLexeme = lexer.getNextLexeme();

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
						if (currentLexeme.typeIndex == XMLTokenTypes.START_COMMENT)
						{
							// reset lexer position
							lexer.setCurrentOffset(currentLexeme.offset);

							// set group for unclosed comment type
							lexer.setGroup("unclosed-comment"); //$NON-NLS-1$

							// rescan
							currentLexeme = lexer.getNextLexeme();
						}
					}

					if (currentLexeme == null)
					{
						// couldn't recover from error, so mark as end of stream
						// NOTE: We may want to throw an exception here since we
						// should be able to return at least an ERROR token
						currentLexeme = EOS;
						inWhitespace = false;
					}
					else
					{
						this.addLexeme(currentLexeme);
						inWhitespace = false;
					}
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
			result = new XMLParseState();
		}
		else
		{
			result = new XMLParseState(parent);
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
		lexer.setIgnoreSet(language, new int[] { XMLTokenTypes.WHITESPACE });
		lexer.setLanguageAndGroup(language, DEFAULT_GROUP);
	}
	
	/**
	 * parseDocTypeDeclaration
	 */
	protected void parseDocTypeDeclaration() throws LexerException
	{
		// NOTE: [KEL] ideally, this will be a nested language, but since we're in a bit of flux
		// with respect to colorization and parsing, I'm inlining DTD parsing here
		ILexer lexer = this.getLexer();
		int initialOffset = lexer.getEOFOffset();
		
		try
		{
			// find end of doctype section and apply to lexer virtual eof
			Range range = lexer.find("doctype-declaration-delimiter"); //$NON-NLS-1$
	
			int offset = range.getEndingOffset();
	
			if (range.isEmpty())
			{
				offset = lexer.getSourceLength();
			}
			
			lexer.setEOFOffset(offset);
			
			// change groups
			lexer.setGroup(DOCTYPE_DECLARATION_GROUP);
			
			this.advance();
			
			// process until eof
			while (this.isEOS() == false)
			{
				this.advance();
			}
		}
		finally
		{
			// restore original eof
			lexer.setEOFOffset(initialOffset);
			lexer.setGroup(DEFAULT_GROUP);
			
			// re-prime
			this.advance();
		}
	}
	
	/**
	 * parseText
	 * 
	 * @param verify
	 * @throws LexerException
	 * @throws ParseException 
	 */
	protected void parseText(boolean verify) throws LexerException, ParseException
	{
		// get reference to lexer
		ILexer lexer = this.getLexer();

		// switch to text group
		lexer.setGroup(TEXT_GROUP);

		// advance over '>' or '/>'
		if (verify)	
		{
			this.assertAndAdvance(XMLTokenTypes.GREATER_THAN, "error.tag.end.close"); //$NON-NLS-1$
		}
		else
		{
			this.advance();
		}

		// switch back to default group
		lexer.setGroup(DEFAULT_GROUP);

		if (this.currentLexeme == EOS || this.isType(XMLTokenTypes.ERROR))
		{
			if (this.currentLexeme != EOS)
			{
				lexer.setCurrentOffset(this.currentLexeme.offset);
				this.removeLexeme(this.currentLexeme);
			}

			// rescan in case we have a false EOS
			this.advance();
		}
	}
}
