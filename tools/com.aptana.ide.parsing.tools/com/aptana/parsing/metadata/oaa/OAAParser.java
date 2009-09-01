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
package com.aptana.parsing.metadata.oaa;

import java.io.InputStream;

import com.aptana.ide.lexer.IEnumerationMap;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.matcher.MatcherTokenList;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.bnf.LRParser;
import com.aptana.ide.parsing.bnf.LRParserBuilder;

/**
 * @author Kevin Lindsey
 */
public class OAAParser
{
	private LRParser _parser;
	
	/**
	 * getParser
	 * 
	 * @return
	 */
	private LRParser getParser()
	{
		if (this._parser == null)
		{
			InputStream bnfInput = OAAParser.class.getResourceAsStream("/com/aptana/ide/metadata/resources/OAA.bnf");
			InputStream lexerInput = OAAParser.class.getResourceAsStream("/com/aptana/ide/metadata/resources/OAA.lxr");
			LRParserBuilder builder = new LRParserBuilder();
			
			this._parser = (LRParser) builder.buildParser(bnfInput, lexerInput);
		}
		
		return this._parser;
	}
	
	/**
	 * parse
	 * 
	 * @param source
	 */
	public void parse(String source)
	{
		LRParser parser = this.getParser();
		
		if (parser != null)
		{
			// create handler
			OAAHandler handler = new OAAHandler();
			
			// associate with parser
			parser.addHandler(handler);
			
			try
			{
				// get lexer, language, and enumeration map
				ILexer lexer = parser.getLexer();
				String language = parser.getLanguage();
				MatcherTokenList tokenList = (MatcherTokenList) lexer.getTokenList(language);
				IEnumerationMap typeMap = tokenList.getTypeMap();
				
				// ignore whitespace (and comments)
				lexer.setIgnoreSet(
					language,
					new int[] {
						typeMap.getIntValue("WHITESPACE"),	 //$NON-NLS-1$
						typeMap.getIntValue("SINGLE_LINE_COMMENT"),	 //$NON-NLS-1$
						typeMap.getIntValue("MULTI_LINE_COMMENT")	 //$NON-NLS-1$
					}
				);
				
				// create parse state, apply edit, and parse
				IParseState parseState = parser.createParseState(null);
				parseState.setEditState(source, source, 0, 0);
				parser.parse(parseState);

				// grab the results of the parse from our handler
				Object[] values = handler.getValues();
				
				if (values.length > 0)
				{
//					Object value = values[0];
					
					// do something with value
				}
			}
			catch (LexerException e)
			{
			}
			finally
			{
				// remove this handler
				parser.removeHandler(handler);
			}
		}
	}
}
