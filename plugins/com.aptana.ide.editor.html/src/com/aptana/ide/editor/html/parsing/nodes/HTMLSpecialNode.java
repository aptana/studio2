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
package com.aptana.ide.editor.html.parsing.nodes;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * @author Kevin Lindsey
 */
public class HTMLSpecialNode extends HTMLParseNode
{
	private String _nestedLanguage;
	private String _iconBaseName;
	private LexemeList _lexemeList;

	/**
	 * HTMLSpecialNode
	 * @param startLexeme
	 */
	public HTMLSpecialNode(Lexeme startLexeme)
	{
		super(HTMLParseNodeTypes.SPECIAL, startLexeme);

		this._iconBaseName = "html_tag"; //$NON-NLS-1$
	}

	/**
	 * getIconBaseName
	 * 
	 * @return String
	 */
	public String getIconBaseName()
	{
		return this._iconBaseName;
	}

	/**
	 * getNestedLanguage
	 *
	 * @return String
	 */
	public String getNestedLanguage()
	{
		return this._nestedLanguage;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.ParseNodeBase#getText()
	 */
	public String getText()
	{
		StringBuffer result = new StringBuffer();
		
		// get lexeme list
		LexemeList lexemes = this._lexemeList;
		
		// get index. This should always be positive
		int currentIndex = lexemes.getLexemeIndex(this.getStartingLexeme());
		
		// flag used to indicate if we've processed all of the nested langauge or not
		// when true, an ellipsis will be added to the end of the label
		boolean hasMore = true;
		
		if (currentIndex >= 0)
		{
			// initialize last offset
			int lastOffset = -1;
			
			// set max number of lexemes to include in label
			int currentCount = 0;
			int includeCount = 3;
			
			// get upper bounds
			int length = lexemes.size();
			
			// advance to next lexeme, which should be the first in our 'language'
			currentIndex++;
			
			// find first non-whitespace
			while (currentIndex < length)
			{
				// get current lexeme
				Lexeme currentLexeme = lexemes.get(currentIndex);
	
				if (currentLexeme.getLanguage().equals(this._nestedLanguage) == false)
				{
					// current lexeme is not in our language anymore, so abort
					hasMore = false;
					break;
				}
				else if (currentLexeme.getCategory().equals("WHITESPACE") == false) //$NON-NLS-1$
				{
					// append space if this lexeme does not touch the previous lexeme
					if (lastOffset != -1 && lastOffset != currentLexeme.offset)
					{
						result.append(" "); //$NON-NLS-1$
					}
					
					// append lexeme's text
					result.append(currentLexeme.getText());
					
					// update last offset
					lastOffset = currentLexeme.getEndingOffset();
					
					// see if we've collected enough lexemes
					currentCount++;
					
					if (currentCount >= includeCount)
					{
						result.append(" "); //$NON-NLS-1$
						break;
					}
				}
				
				// update lexeme index
				currentIndex++;
			}
		}
	
		// add ellipsis
		if (hasMore)
		{
			result.append("...");	 //$NON-NLS-1$
		}
		
		return result.toString();
	}

	/**
	 * setNestedLanguage
	 *
	 * @param language
	 */
	public void setNestedLanguage(String language)
	{
		this._nestedLanguage = language;
	
		// calculate icon name based on language
		int slashIndex = language.indexOf('/');	 //$NON-NLS-1$
		
		if (slashIndex != -1 && slashIndex < language.length() - 2)
		{
			this._iconBaseName = language.substring(slashIndex + 1);
		}
		else
		{
			this._iconBaseName = language;
		}
	}

	/**
	 * setLexemeList
	 *
	 * @param lexemeList
	 */
	public void setLexemeList(LexemeList lexemeList)
	{
		this._lexemeList = lexemeList;
	}
}
