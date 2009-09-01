/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.editor.js.parsing;

import java.text.ParseException;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.jscomment.parsing.JSCommentMimeType;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.folding.GenericCommentNode;
import com.aptana.ide.editors.unified.parsing.UnifiedParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public abstract class JSParserBase extends UnifiedParser
{
	private static final String NESTED_LANGUAGE_ID = "nested_languages"; //$NON-NLS-1$
	private static final String DOCUMENTATION_DELIMITER_GROUP = "documentation-delimiter"; //$NON-NLS-1$
	private static final String LINE_DELIMITER_GROUP = "line-delimiter"; //$NON-NLS-1$
	
	private static final String DEFAULT_GROUP = "default"; //$NON-NLS-1$
	
	/**
	 * current parent AST node
	 */
	protected IParseNode _currentParentNode;
	
	/**
	 * language registry
	 */
	protected JSLanguageRegistry _languageRegistry;
	
	/**
	 * processing instruction language MIME type
	 */
	protected String _piLanguage;
	
	private boolean _fastScan;
	
	/**
	 * JSParserBase
	 * 
	 * @throws ParserInitializationException
	 */
	public JSParserBase() throws ParserInitializationException
	{
		this(JSMimeType.MimeType);
	}
	
	/**
	 * JSParserBase
	 * @param language
	 * @throws ParserInitializationException
	 */
	public JSParserBase(String language) throws ParserInitializationException
	{
		super(language);
		
		this._fastScan = UnifiedEditorsPlugin.getDefault().useFastScan();
	}

	/**
	 * @see com.aptana.ide.parsing.AbstractParser#addChildParsers()
	 */
	protected void addChildParsers() throws ParserInitializationException
	{
		super.addChildParsers();

		if (this._languageRegistry == null)
		{
			this._languageRegistry = new JSLanguageRegistry();

			IExtensionRegistry registry = Platform.getExtensionRegistry();

			if (registry != null)
			{
				IExtensionPoint extensionPoint = registry.getExtensionPoint(JSPlugin.ID, NESTED_LANGUAGE_ID);
				IExtension[] extensions = extensionPoint.getExtensions();

				for (IExtension extension : extensions)
				{
					IParser[] parsers;
					
					if (this.isScanner())
					{
						parsers = this._languageRegistry.loadScannersFromExtension(extension);
					}
					else
					{
						parsers = this._languageRegistry.loadParsersFromExtension(extension);
					}
					
					for (IParser parser : parsers)
					{
						this.addChildParser(parser);
					}
				}
			}
		}
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

		if (this._currentParentNode != null && this.currentLexeme != null && this.currentLexeme != EOS)
		{
			this._currentParentNode.includeLexemeInRange(this.currentLexeme);
		}

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
	 * checkForLanguageTransition
	 * 
	 * @param lexeme
	 * @return Lexeme
	 * @throws LexerException
	 */
	private Lexeme checkForLanguageTransition(Lexeme lexeme) throws LexerException
	{
		Lexeme result = lexeme;
		
		if (lexeme != null && lexeme != EOS)
		{
			String terminator = null;
			String mimeType = null;
	
			switch (lexeme.typeIndex)
			{
				case JSTokenTypes.PI_OPEN:
					this.onPIOpen();
					this.advance();
					break;
					
				case JSTokenTypes.PI_CLOSE:
					this.advance();
					break;
					
				case JSTokenTypes.START_DOCUMENTATION:
					terminator = DOCUMENTATION_DELIMITER_GROUP;
					mimeType = ScriptDocMimeType.MimeType;
					break;
	
				case JSTokenTypes.START_MULTILINE_COMMENT:
					terminator = DOCUMENTATION_DELIMITER_GROUP;
					mimeType = JSCommentMimeType.MimeType;
					break;
	
				case JSTokenTypes.COMMENT:
					terminator = LINE_DELIMITER_GROUP;
					mimeType = JSCommentMimeType.MimeType;
					break;
	
				default:
					break;
			}
			
			if (terminator != null && mimeType != null)
			{
				// language will change, logic is below
				ILexer lexer = this.getLexer();
		
				// backup to the start of the lexeme
				lexer.setCurrentOffset(lexeme.offset);
		
				// find offset
				Range range = lexer.find(terminator);
		
				// include the delimiter in the doc or comment
				int offset = (lexeme.typeIndex == JSTokenTypes.COMMENT) ? range.getStartingOffset() : range.getEndingOffset();
		
				if (range.isEmpty())
				{
					offset = lexer.getSourceLength();
				}
		
				try
				{
					this.changeLanguage(mimeType, offset, this._currentParentNode);
				}
				catch (LexerException e)
				{
				}
				catch (ParseException e)
				{
				}
		
				// advance over end of comment
				this.advance();
		
				result = this.currentLexeme;
			}
		}
		
		return result;
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
	 * @see com.aptana.ide.parsing.AbstractParser#getNextLexemeInLanguage()
	 */
	protected Lexeme getNextLexemeInLanguage() throws LexerException
	{
		if (this._fastScan == false)
		{
			Lexeme result = super.getNextLexemeInLanguage();
	
			// always need to look for language change
			result = this.checkForLanguageTransition(result);
	
			return result;
		}
		
		ILexer lexer = this.getLexer();
		Lexeme result = null;

		while (result == null && lexer.isEOS() == false)
		{
			result = lexer.getNextLexeme();

			if (result != null && result != EOS)
			{
				String terminator = null;
				String mimeType = null;
				
				String text = result.getText();
				int textLength = text.length();
				int offset = 0;
				
				if (textLength >= 2)
				{
					char[] source;
					int eof;
					
					switch (text.charAt(0))
					{
						case '/':
							switch (text.charAt(1))
							{
								// "/**" or "/*"
								case '*':
									if (textLength == 3 && text.charAt(2) == '*')
									{
										// "/**"
										terminator = DOCUMENTATION_DELIMITER_GROUP;
										mimeType = ScriptDocMimeType.MimeType;
									}
									else
									{
										// "/*"
										terminator = DOCUMENTATION_DELIMITER_GROUP;
										mimeType = JSCommentMimeType.MimeType;
									}
									
									source = lexer.getSourceUnsafe();
									eof = lexer.getEOFOffset();
									
									for (offset = result.getEndingOffset(); offset < eof; offset++)
									{
										if (source[offset] == '*')
										{
											if (offset + 1 < eof && source[offset + 1] == '/')
											{
												offset += 2;
												break;
											}
										}
									}
									break;
								
								// "//"
								case '/':
									terminator = LINE_DELIMITER_GROUP;
									mimeType = JSCommentMimeType.MimeType;
									
									source = lexer.getSourceUnsafe();
									eof = lexer.getEOFOffset();
									
									for (offset = result.getEndingOffset(); offset < eof; offset++)
									{
										char current = source[offset];
										
										if (current == '\r' || current == '\n')
										{
											break;
										}
									}
									break;
							}
							break;
							
						case '<':
							if (text.charAt(1) == '?')
							{
								// "<?"
								this.onPIOpen();
								this.advance();
							}
							break;
							
						case '?':
							if (text.charAt(1) == '>')
							{
								// "?>"
								this.advance();
							}
							break;
					}
				}
				
				if (terminator != null && mimeType != null)
				{
					// backup to the start of the lexeme
					lexer.setCurrentOffset(result.offset);
			
					try
					{
						this.changeLanguage(mimeType, offset, this._currentParentNode);
					}
					catch (LexerException e)
					{
					}
					catch (ParseException e)
					{
					}
			
					// advance over end of comment
					this.advance();
			
					result = this.currentLexeme;
				}
				else if (result.getLanguage().equals(this.getLanguage()) == false)
				{
					LexemeList lexemes = this.getLexemeList();
	
					lexemes.getAffectedRegion().includeInRange(result);
					this.removeLexeme(result);
					lexer.setCurrentOffset(result.offset);
					result = lexer.getNextLexeme();
				}
			}

			if (result == null && lexer.isEOS() == false)
			{
				// if we're already in the error group, then abort
				if ("error".equals(lexer.getGroup())) //$NON-NLS-1$
				{
					break;
				}

				// Switch to error group.
				lexer.setGroup("error"); //$NON-NLS-1$

				// get error lexeme
				result = lexer.getNextLexeme();

				// if we failed to get a new lexeme and we're still in the error state,
				// then we need to abort to prevent an infinite loop
				if (result == null && "error".equals(lexer.getGroup())) //$NON-NLS-1$
				{
					break;
				}
			}
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
	
	/**
	 * isScanner
	 * 
	 * @return
	 */
	public boolean isScanner()
	{
		return false;
	}
	
	/**
	 * let's handle PI OPEN lexeme
	 */
	protected void onPIOpen()
	{
		try
		{
			if (this._piLanguage != null)
			{
				this.changeLanguage(this._piLanguage, Integer.MAX_VALUE, this._currentParentNode);
			}
		}
		catch (LexerException e)
		{
			IdeLog.logError(JSPlugin.getDefault(), "Lexing exception", e); //$NON-NLS-1$
		}
		catch (ParseException e)
		{
			IdeLog.logError(JSPlugin.getDefault(), "Parsing exception", e); //$NON-NLS-1$
		}
	}
}
