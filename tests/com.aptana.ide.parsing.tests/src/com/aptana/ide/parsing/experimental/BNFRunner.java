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
package com.aptana.ide.parsing.experimental;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.io.StreamUtils;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.Token;
import com.aptana.ide.lexer.matcher.MatcherLexer;
import com.aptana.ide.lexer.matcher.MatcherLexerBuilder;
import com.aptana.ide.parsing.bnf.BNFGrammar;
import com.aptana.ide.parsing.bnf.BNFHandler;
import com.aptana.ide.parsing.bnf.IReductionHandler;
import com.aptana.ide.parsing.bnf.nodes.GrammarNode;

/**
 * @author Kevin Lindsey
 */
public class BNFRunner
{
	private String _BNFResource;
	private String _lexerName;
	private String _mimeType;
	private String _message;
	private Object[] _values;

	/**
	 * BNFRunner
	 * 
	 * @param resourceName
	 * @param lexerName
	 */
	public BNFRunner(String BNFResource, String lexerName, String mimeType)
	{
		this._BNFResource = BNFResource;
		this._lexerName = lexerName;
		this._mimeType = mimeType;
	}

	/**
	 * parse
	 * 
	 * @param source
	 * @param handler
	 * @return
	 */
	public boolean parse(String source, IReductionHandler handler)
	{
		boolean result = this.parse(source, new IReductionHandler[] { handler });

		this._values = handler.getValues();

		return result;
	}

	/**
	 * parse
	 * 
	 * @param source
	 * @param handlers
	 * @return
	 */
	public boolean parse(String source, IReductionHandler[] handlers)
	{
		ILexer lexer = this.createLexer(this._lexerName, this._mimeType);
		lexer.setSource(source);
		Lexeme[] lexemes = this.createLexemes(lexer);

		Parser parser = this.createParser();
		for (int i = 0; i < handlers.length; i++)
		{
			parser.addHandler(handlers[i]);
		}
		boolean result = parser.parse(lexemes);

		if (result)
		{
			this._message = "";
		}
		else
		{
			this._message = parser.getMessage();
		}

		return result;
	}

	/**
	 * createTypes
	 * 
	 * @param lexer
	 * @return
	 */
	private Lexeme[] createLexemes(ILexer lexer)
	{
		List<Lexeme> types = new ArrayList<Lexeme>();
		Lexeme lexeme = lexer.getNextLexeme();

		while (lexeme != null)
		{
			if (lexeme.getCategory().equals("WHITESPACE") == false)
			{
				types.add(lexeme);
			}

			lexeme = lexer.getNextLexeme();
		}

		types.add(new Lexeme(new Token(null)
		{
			/**
			 * @see com.aptana.ide.lexer.Token#getTypeIndex()
			 */
			public int getTypeIndex()
			{
				return 0;
			}

			/**
			 * @see com.aptana.ide.lexer.Token#getType()
			 */
			public String getType()
			{
				return "$";
			}
		}, "$", -1));

		return types.toArray(new Lexeme[types.size()]);
	}

	/**
	 * getLexer
	 * 
	 * @return
	 */
	private ILexer createLexer(String lexerName, String mimeType)
	{
		MatcherLexerBuilder builder = new MatcherLexerBuilder();
		InputStream in = BNFRunner.class.getResourceAsStream(lexerName);
		MatcherLexer lexer = null;

		builder.loadXML(in);

		try
		{
			lexer = (MatcherLexer) builder.buildLexer();
			lexer.setLanguageAndGroup(mimeType, "default");
		}
		catch (LexerException e)
		{
			e.printStackTrace();
		}

		return lexer;
	}

	public GrammarNode createGrammar()
	{
		GrammarNode result = null;

		// create BNF parser
		Parser parser = new Parser();
		parser.generateTable(BNFGrammar.getGrammar());

		// load BNF grammar
		String source = this.getResourceText(this._BNFResource);

		// create BNF grammar handler
		BNFHandler handler = new BNFHandler();
		parser.addHandler(handler);

		// get lexemes
		ILexer lexer = this.createLexer("/Resources/bnf.lxr", "text/bnf");
		lexer.setSource(source);
		Lexeme[] lexemes = this.createLexemes(lexer);

		if (parser.parse(lexemes))
		{
			result = handler.getGrammar();
		}
		else
		{
			System.out.println(parser.getMessage());
		}

		return result;
	}

	/**
	 * createParser
	 * 
	 * @return Parser
	 */
	public Parser createParser()
	{
		Parser result = new Parser();

		result.generateTable(this.createGrammar());

		return result;
	}

	/**
	 * getMessage
	 * 
	 * @return String
	 */
	public String getMessage()
	{
		return this._message;
	}

	/**
	 * getResourceText
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	private String getResourceText(String resource)
	{
		InputStream bnf = this.getClass().getResourceAsStream(resource);
		String source = "";

		try
		{
			source = StreamUtils.getText(bnf);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return source;
	}

	/**
	 * getValues
	 * 
	 * @return
	 */
	public Object[] getValues()
	{
		return this._values;
	}
}
