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


/**
 * @author Kevin Lindsey
 */
public class HereDocumentMatcher extends AbstractTextMatcher
{
	private IdentifierMatcher _identifier;
	
	/**
	 * HereDocument
	 */
	public HereDocumentMatcher()
	{
		this._identifier = new IdentifierMatcher();
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addChildTypes()
	 */
	public void addChildTypes()
	{
		// no children
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addFirstCharacters(com.aptana.ide.lexer.matcher.MatcherMap,
	 *      com.aptana.ide.lexer.matcher.ITextMatcher)
	 */
	public void addFirstCharacters(MatcherMap map, ITextMatcher target)
	{
		map.addCharacterMatcher('<', target);
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#match(char[], int, int)
	 */
	public int match(char[] source, int offset, int eofOffset)
	{
		int index = offset;
		int result = -1;
		
		if (index + 2 < eofOffset && source[index] == '<' && source[index + 1] == '<' && source[index + 2] == '<')
		{
			// advance over '<<'
			index += 3;
			
			// assume no indentation
			boolean indent = false;
			
			// check if we should allow indentation
			if (index < eofOffset && source[index] == '-')
			{
				indent = true;
				
				// advance over '-'
				index++;
			}
			
			// get label and end of here-doc
			if (index < eofOffset)
			{
				char c = source[index];
				int start = index;
				int end = -1;
				
				switch (c)
				{
					case '\'':
					case '"':
					case '`':
						// skip over quote and finding match quote
						start++;
						end = this.findClosingQuote(source, start, eofOffset, c);
						break;
						
					default:
						// get identifier
						end = this._identifier.match(source, start, eofOffset);
						break;
				}
				
				if (end != -1)
				{
					// extract label
					String label = new String(source, start, end - start);
					
					// update position
					index = end;
					
					result = findDelimiter(source, index, eofOffset, label, indent);
				}
			}
		}

		if (result != -1)
		{
			this.accept(source, offset, result, this.token);
		}

		return result;
	}

	/**
	 * findDelimiter
	 *
	 * @param source
	 * @param index
	 * @param eofOffset
	 * @param label
	 * @param indent
	 * @return offset
	 */
	private int findDelimiter(char[] source, int index, int eofOffset, String label, boolean indent)
	{
		// create top-level and-matcher (all children must match)
		AndMatcher and = new AndMatcher();

		// create matcher for start of line
		StartOfLineMatcher lineStart = new StartOfLineMatcher();
		and.appendChild(lineStart);
		
		if (indent)
		{
			ZeroOrMoreMatcher whitespace = new ZeroOrMoreMatcher();
			
			whitespace.appendChild(new WhitespaceMatcher());
			and.appendChild(whitespace);
		}
		
		// create matcher for label and add to and-matcher
		StringMatcher marker = new StringMatcher(label);
		and.appendChild(marker);
		
		// make delimiter
		ToDelimiterMatcher delimiter = new ToDelimiterMatcher();
		delimiter.appendChild(and);
		
		// allow matcher to go to EOF
		delimiter.setMatchEndOfFile(true);
		
		// match
		return delimiter.match(source, index, eofOffset);
	}
	
	/**
	 * advanceUntil
	 *
	 * @param source
	 * @param offset
	 * @param eofOffset
	 * @param c
	 * @return index
	 */
	private int findClosingQuote(char[] source, int offset, int eofOffset, char c)
	{
		int index = offset;
		int result = -1;
		
		while (index < eofOffset)
		{
			if (source[index] == c)
			{
				result = index;
				break;
			}
			else
			{
				index++;
			}
		}
		
		return result;
	}
}
