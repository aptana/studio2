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
package com.aptana.ide.lexer.matcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.ILexerBuilder;
import com.aptana.ide.lexer.ITokenList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.LexerPlugin;
import com.aptana.ide.lexer.matcher.model.IMatcherElement;
import com.aptana.ide.lexer.matcher.model.LexerElement;
import com.aptana.ide.lexer.matcher.model.MatcherElement;
import com.aptana.xml.DocumentNode;
import com.aptana.xml.IErrorHandler;
import com.aptana.xml.INode;
import com.aptana.xml.Parser;

/**
 * @author Kevin Lindsey
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class MatcherLexerBuilder implements ILexerBuilder, IErrorHandler
{
	private static final String LEXER_NAMESPACE = "http://www.aptana.com/2007/lexer/1.2"; //$NON-NLS-1$
	private static final String ELEMENT_SUFFIX = "Element"; //$NON-NLS-1$
	private static final String MATCHER_SUFFIX = "Matcher"; //$NON-NLS-1$
	
	private ILexer _lexer;

	/**
	 * CodeBasedLexerBuilder
	 */
	public MatcherLexerBuilder()
	{
		this._lexer = this.createLexer();
	}

	/**
	 * @see com.aptana.ide.lexer.ILexerBuilder#addTokenList(com.aptana.ide.lexer.ITokenList)
	 */
	public void addTokenList(ITokenList list)
	{
		// add list to lexer
		this._lexer.addLanguage(list);
	}

	/**
	 * @see com.aptana.ide.lexer.ILexerBuilder#buildLexer()
	 */
	public ILexer buildLexer() throws LexerException
	{
		if (this._lexer != null)
		{
			// go ahead and finalize the lexer by building the regex engines
			this._lexer.seal();
		}

		return this._lexer;
	}

	/**
	 * createLexer
	 * 
	 * @return ILexer
	 */
	protected ILexer createLexer()
	{
		return new MatcherLexer();
	}

	/**
	 * createLexerParser
	 *
	 * @return Parser
	 */
	public static Parser createLexerParser()
	{
		// create parser
		Parser parser = new Parser(LEXER_NAMESPACE);
		
		// add our packages for our model and matchers
		parser.addPackage(IMatcherElement.class.getPackage().getName());	// "com.aptana.ide.lexer.matcher.model"
		parser.addPackage(ITextMatcher.class.getPackage().getName());		// "com.aptana.ide.lexer.matcher"
		
		// add our Element and Matcher suffixes
		parser.addSuffix(ELEMENT_SUFFIX);
		parser.addSuffix(MATCHER_SUFFIX);
		
		// use MatcherElement for our undefined element class
		parser.setUnknownElementClass(MatcherElement.class);
		
		return parser;
	}
	
	/**
	 * createTokenList
	 * 
	 * @param language
	 * @return ITokenList
	 */
	protected ITokenList createTokenList(String language)
	{
		return new MatcherTokenList(language);
	}

	/**
	 * @see com.aptana.ide.lexer.ILexerBuilder#getTokens(java.lang.String)
	 */
	public ITokenList getTokens(String language)
	{
		ITokenList result = null;

		if (this._lexer != null)
		{
			result = this._lexer.getTokenList(language);
		}

		return result;
	}

	/**
	 * Load the specified binary grammar file
	 * 
	 * @param file
	 */
	public void loadXML(File file)
	{
		try
		{
			FileInputStream inputStream = new FileInputStream(file);

			this.loadXML(inputStream);
		}
		catch (FileNotFoundException e)
		{
			LexerPlugin.logError(Messages.MatcherLexerBuilder_Error_Reading_XML_File, e);
		}
	}

	/**
	 * load
	 * 
	 * @param in
	 */
	public void loadXML(InputStream in)
	{
		try
		{
			// create parser
			Parser parser = createLexerParser();
			
			// associate error handler
			parser.setErrorHandler(this);
			
			// get the parse result
			DocumentNode result = parser.loadXML(in);
			
			// post-process the results
			if (result != null)
			{
				INode node = result.getRootNode();
				
				if (node != null && node instanceof LexerElement)
				{
					LexerElement lexerElement = (LexerElement) node;
					
					lexerElement.validate();
					
					ITokenList tokenList = lexerElement.getTokenList(parser.getClassLoader());
					
					this._lexer.addLanguage(tokenList);
				}
			}
		}
		catch (Exception e)
		{
			LexerPlugin.logError(Messages.MatcherLexerBuilder_Cannot_Build_Lexer, e);
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}
	
	/**
     * Loads XMl.
     * 
     * @param in - input.
     * @param classLoader - class loader to use for resolving.
     */
    public void loadXML(InputStream in, ClassLoader classLoader)
    {
        try
        {
            // create parser
            Parser parser = createLexerParser();
            
            // associate error handler
            parser.setErrorHandler(this);
            
            // get the parse result
            DocumentNode result = parser.loadXML(in);
            
            // post-process the results
            if (result != null)
            {
                INode node = result.getRootNode();
                
                if (node != null && node instanceof LexerElement)
                {
                    LexerElement lexerElement = (LexerElement) node;
                    
                    lexerElement.validate();
                    
                    ITokenList tokenList = lexerElement.getTokenList(classLoader);
                    
                    this._lexer.addLanguage(tokenList);
                }
            }
        }
        catch (Exception e)
        {
            LexerPlugin.logError(Messages.MatcherLexerBuilder_Cannot_Build_Lexer, e);
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }

	/**
	 * @see com.aptana.xml.IErrorHandler#handleError(int, int, java.lang.String)
	 */
	public void handleError(int line, int column, String message)
	{
		String msg = MessageFormat.format(
			Messages.MatcherLexerBuilder_Error_Message,
			new Object[] {
				Integer.toString(line),
				Integer.toString(column),
				message
			}
		);
		
		LexerPlugin.logError(msg);
	}

	/**
	 * @see com.aptana.xml.IErrorHandler#handleInfo(int, int, java.lang.String)
	 */
	public void handleInfo(int line, int column, String message)
	{
		String msg = MessageFormat.format(
			Messages.MatcherLexerBuilder_Info_Message,
			new Object[] {
				Integer.toString(line),
				Integer.toString(column),
				message
			}
		);
		
		LexerPlugin.logInfo(msg);
	}

	/**
	 * @see com.aptana.xml.IErrorHandler#handleWarning(int, int, java.lang.String)
	 */
	public void handleWarning(int line, int column, String message)
	{
		String msg = MessageFormat.format(
			Messages.MatcherLexerBuilder_Warning_Message,
			new Object[] {
				Integer.toString(line),
				Integer.toString(column),
				message
			}
		);
		
		LexerPlugin.logWarning(msg);
	}
}