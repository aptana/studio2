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
package com.aptana.ide.lexer.matcher;

import java.io.InputStream;

import com.aptana.ide.lexer.IEnumerationMap;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.bnf.LRParser;
import com.aptana.ide.parsing.bnf.LRParserBuilder;

/**
 * @author Kevin Lindsey
 */
public class RegexMatcher extends AbstractTextMatcher
{
	private static LRParser regexParser;
	private RegexMatcherHandler _handler;
	private ITextMatcher _root;
	private boolean _ignoreWhitespace;
	private boolean _caseInsensitive;
	
	/**
	 * getRegexParser
	 * 
	 * @return LRParser
	 */
	private static LRParser getRegexParser()
	{
		if (regexParser == null)
		{
			InputStream bnfInput = RegexMatcher.class.getResourceAsStream("/com/aptana/ide/lexer/matcher/resources/Regex.bnf"); //$NON-NLS-1$
			InputStream lexerInput = RegexMatcher.class.getResourceAsStream("/com/aptana/ide/lexer/matcher/resources/Regex.lxr"); //$NON-NLS-1$
			LRParserBuilder builder = new LRParserBuilder();
			
			regexParser = (LRParser) builder.buildParser(bnfInput, lexerInput);
		}
		
		return regexParser;
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addChildTypes()
	 */
	public void addChildTypes()
	{
		// no children
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addFirstCharacters(com.aptana.ide.lexer.matcher.MatcherMap, com.aptana.ide.lexer.matcher.ITextMatcher)
	 */
	public void addFirstCharacters(MatcherMap map, ITextMatcher target)
	{
		if (this._root != null)
		{
			this._root.addFirstCharacters(map, target);
		}
	}

	/**
	 * @see com.aptana.xml.NodeBase#appendText(java.lang.String)
	 */
	public void appendText(String text)
	{
		LRParser parser = getRegexParser();
		
		if (parser != null)
		{
			try
			{
				// create handler
				this._handler = new RegexMatcherHandler(this._caseInsensitive);
				
				// associate with parser
				regexParser.addHandler(this._handler);
				
				// get lexer, language, and enumeration map
				ILexer lexer = parser.getLexer();
				String language = parser.getLanguage();
				MatcherTokenList tokenList = (MatcherTokenList) lexer.getTokenList(language);
				IEnumerationMap typeMap = tokenList.getTypeMap();
				
				// set ignore-set based on ignoreWhitespace flag
				if (this._ignoreWhitespace)
				{
					lexer.setIgnoreSet(language, new int[] { typeMap.getIntValue("WHITESPACE") }); //$NON-NLS-1$
				}
				else
				{
					lexer.setIgnoreSet(language, new int[] { });
				}
				
				// create parse state, apply edit, and parse
				IParseState parseState = parser.createParseState(null);
				parseState.setEditState(text, text, 0, 0);
				parser.parse(parseState);

				// grab the results of the parse from our handler
				Object[] values = this._handler.getValues();
				
				if (values.length > 0)
				{
					Object value = values[0];
					
					if (value instanceof ITextMatcher)
					{
						this._root = (ITextMatcher) value;
					}
				}
			}
			catch (LexerException e)
			{
			}
			finally
			{
				// remove this handler
				parser.removeHandler(this._handler);
			}
		}
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#match(char[], int, int)
	 */
	public int match(char[] source, int offset, int eofOffset)
	{
		int result = - 1;
		
		if (this._root != null)
		{
			result = this._root.match(source, offset, eofOffset);
		}
		
		if (result != -1)
		{
			this.accept(source, offset, result, this.token);
		}
		
		return result;
	}
	
	/**
	 * setCaseInsensitive
	 * 
	 * @param value
	 */
	public void setCaseInsensitive(boolean value)
	{
		this._caseInsensitive = value;
	}
	
	/**
	 * setIgnoreWhitespace
	 * 
	 * @param value
	 */
	public void setIgnoreWhitespace(boolean value)
	{
		this._ignoreWhitespace = value;
	}
}
