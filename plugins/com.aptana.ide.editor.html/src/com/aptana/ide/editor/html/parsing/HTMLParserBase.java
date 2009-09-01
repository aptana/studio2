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
package com.aptana.ide.editor.html.parsing;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editors.unified.parsing.UnifiedParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class HTMLParserBase extends UnifiedParser
{
	private static final String DEFAULT_GROUP = "default"; //$NON-NLS-1$
	private static final String ERROR_GROUP = "error"; //$NON-NLS-1$
	
	private static String NESTED_LANGUAGE_ID = "nested_languages"; //$NON-NLS-1$
	
	/**
	 * current element
	 */
	protected IParseNode _currentElement;
	
	/**
	 * languageRegistry
	 */
	protected HTMLLanguageRegistry languageRegistry;
	
	/**
	 * HTMLParserBase
	 * 
	 * @throws ParserInitializationException
	 */
	public HTMLParserBase() throws ParserInitializationException
	{
		this(HTMLMimeType.MimeType);
	}
	
	/**
	 * HTMLParserBase
	 * 
	 * @param language
	 * @throws ParserInitializationException
	 */
	public HTMLParserBase(String language) throws ParserInitializationException
	{
		super(language);
	}
	
	/**
	 * @see com.aptana.ide.parsing.AbstractParser#addChildParsers()
	 */
	protected void addChildParsers() throws ParserInitializationException
	{
		super.addChildParsers();

		if (this.languageRegistry == null)
		{
			this.languageRegistry = new HTMLLanguageRegistry();

			IExtensionRegistry registry = Platform.getExtensionRegistry();

			if (registry != null)
			{
				IExtensionPoint extensionPoint = registry.getExtensionPoint(HTMLPlugin.ID, NESTED_LANGUAGE_ID);
				IExtension[] extensions = extensionPoint.getExtensions();

				for (int i = 0; i < extensions.length; i++)
				{
					IExtension extension = extensions[i];

					IParser[] parsers = this.languageRegistry.loadFromExtension(extension);

					for (int j = 0; j < parsers.length; j++)
					{
						this.addChildParser(parsers[j]);
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
						if (currentLexeme.typeIndex == HTMLTokenTypes.START_COMMENT)
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
		IParseState root = parent;

		if (parent == null)
		{
			result = new HTMLParseState();
			root = result.getRoot();
		}
		else
		{
			result = new HTMLParseState(root);
		}

		// get nested parsers
		IParser cssParser = this.getParserForMimeType(CSSMimeType.MimeType);
		IParser jsParser = this.getParserForMimeType(JSMimeType.MimeType);

		// add their parse states, if they exist
		if (cssParser != null)
		{
			result.addChildState(cssParser.createParseState(root));
		}

		if (jsParser != null)
		{
			result.addChildState(jsParser.createParseState(root));
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
		lexer.setIgnoreSet(language, new int[] { HTMLTokenTypes.WHITESPACE });
		lexer.setLanguageAndGroup(language, DEFAULT_GROUP);
	}
}
