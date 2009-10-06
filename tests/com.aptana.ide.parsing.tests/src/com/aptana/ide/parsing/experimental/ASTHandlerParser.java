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
package com.aptana.ide.parsing.experimental;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.aptana.ide.core.StreamUtils;
import com.aptana.ide.lexer.IEnumerationMap;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.matcher.MatcherTokenList;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.bnf.ASTHandler;
import com.aptana.ide.parsing.bnf.LRParser;
import com.aptana.ide.parsing.bnf.LRParserBuilder;
import com.aptana.ide.parsing.bnf.ParseTreeHandler;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class ASTHandlerParser
{
	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		ASTHandlerParser parser = new ASTHandlerParser();

		try
		{
			parser.run();
		}
		catch (LexerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}

	/**
	 * run
	 * 
	 * @throws LexerException
	 * @throws IOException 
	 */
	private void run() throws LexerException, IOException
	{
		InputStream astBNF = IParser.class.getResourceAsStream("/com/aptana/ide/parsing/bnf/resources/AST.bnf");
		InputStream astLexer = IParser.class.getResourceAsStream("/com/aptana/ide/parsing/bnf/resources/AST.lxr");
		InputStream sourceStream = this.getClass().getResourceAsStream("JS.ast");
//		InputStream sourceStream = this.getClass().getResourceAsStream("Test2.ast");
		LRParserBuilder builder = new LRParserBuilder();
		ASTHandler handler = new ASTHandler();
//		ParseTreeHandler tree = new ParseTreeHandler();
		
		String source = StreamUtils.readContent(sourceStream, null);

		LRParser parser = (LRParser) builder.buildParser(astBNF, astLexer);
		parser.addHandler(handler);
//		parser.addHandler(tree);
		IParseState parseState = parser.createParseState(null);
		parseState.setEditState(source, source, 0, 0);
		
		ILexer lexer = parser.getLexer();
		String language = parser.getLanguage();
		MatcherTokenList tokenList = (MatcherTokenList) lexer.getTokenList(language);
		IEnumerationMap typeMap = tokenList.getTypeMap();

		int[] set = new int[] {
			typeMap.getIntValue("WHITESPACE"), //$NON-NLS-1$
			typeMap.getIntValue("COMMENT"), //$NON-NLS-1$
			typeMap.getIntValue("SINGLE_LINE_COMMENT") //$NON-NLS-1$
		};
		Arrays.sort(set);
		lexer.setIgnoreSet(language, set);
		lexer.setLanguageAndGroup(language, "default"); //$NON-NLS-1$
		
		parser.parse(parseState);

		String message = parser.getMessage();
		
		if (message != null && message.length() > 0)
		{
			System.out.println(message);
		}
		else
		{
			System.out.println("Parse successful");
//			IParseNode result = (IParseNode) tree.getValues()[0];
			IParseNode result = handler.getRootNode();
			
			System.out.println(result.getXML());
		}
	}
}
