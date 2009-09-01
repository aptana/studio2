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
package com.aptana.ide.logging.impl;

import java.util.HashMap;

import com.aptana.ide.lexer.matcher.AbstractTextMatcher;
import com.aptana.ide.lexer.matcher.ITextMatcher;
import com.aptana.ide.lexer.matcher.MatcherMap;
import com.aptana.xml.INode;


/**
 * Matcher that is works like OrMatcher, but always match children inside a single line of text.
 * Match result is always the whole line, but result token is taken from the appropriate child.
 * If several children matched, those child that is more close to the begin
 * of the children list wins the match.
 * Matching does not stop when the some of the children is matched, so there is always a guarantee
 * that the whole line of text is checked.
 * 
 * @author Denis Denisenko
 */
public class InLineMatcher extends AbstractTextMatcher
{
    /**
     * Characters map.
     */
	private MatcherMap _firstCharacters;
	
	/**
	 * Map from text matcher to its index in the children list.
	 */
	private HashMap<ITextMatcher, Integer> indexes = null;	
	
	/**
	 * MatcherGroup
	 */
	public InLineMatcher()
	{
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addChildTypes()
	 */
	public void addChildTypes()
	{
		this.addChildType(ITextMatcher.class);
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addFirstCharacters(com.aptana.ide.lexer.matcher.MatcherMap, com.aptana.ide.lexer.matcher.ITextMatcher)
	 */
	public void addFirstCharacters(MatcherMap map, ITextMatcher target)
	{
		MatcherMap localMap = null;
		
		if (this._firstCharacters == null)
		{
			localMap = new MatcherMap();
		}
		
		int childCount = this.getChildCount();
		for (int i = 0; i < childCount; i++)
		{
			INode child = this.getChild(i);
			
			if (child instanceof ITextMatcher)
			{
				ITextMatcher matcher = (ITextMatcher) child;
				
				matcher.addFirstCharacters(map, target);
				
				if (localMap != null)
				{
					matcher.addFirstCharacters(localMap);
				}
			}
		}
		
		if (localMap != null)
		{
			this._firstCharacters = localMap;
			this._firstCharacters.setSealed();
		}
	}

	/**
	 * findFirstCharacters
	 * @return boolean
	 */
	public boolean buildFirstCharacterMap()
	{
	    //saving matchers indexes
        indexes = new HashMap<ITextMatcher, Integer>(this.getChildCount(), 0.5f);
        int matcherIndex = 0;
        //building first characters map
		MatcherMap map = new MatcherMap();
		
		for (int i = 0; i < this.getChildCount(); i++)
		{
			INode child = this.getChild(i);
			
			if (child instanceof ITextMatcher)
			{
				ITextMatcher matcher = (ITextMatcher) child;
				
				//adding matcher characters
				matcher.addFirstCharacters(map);
				
				//saving matcher index
				indexes.put(matcher, matcherIndex);
				matcherIndex++;
			}
		}
		
		this._firstCharacters = map;
		this._firstCharacters.setSealed();
		
		return this._firstCharacters.hasUncategorizedMatchers() == false;
	}

    /**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#canMatchNothing()
	 */
	public boolean canMatchNothing()
	{
		boolean result = false;
		
		int childCount = this.getChildCount();
		for (int i = 0; i < childCount; i++)
		{
			INode child = this.getChild(i);

			if (child instanceof ITextMatcher)
			{
				ITextMatcher matcher = (ITextMatcher) child;
				
				if (matcher.canMatchNothing())
				{
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.ITextMatcher#match(char[], int, int)
	 */
	public int match(char[] source, int offset, int eofset)
	{
		//creating matchers list
		ITextMatcher[] matchers = null;
		
		//getting default matchers if needed
		if (this._firstCharacters == null)
		{
		    matchers = new ITextMatcher[indexes.size()];
			indexes.keySet().toArray(matchers);
		}
		
		//most top index of the matched child
		ITextMatcher winningMatcher = null;
		if (indexes==null){
			buildFirstCharacterMap();
		}
		int topMostMatchedIndex = indexes.size();
		
		int currentOffset;
		for (currentOffset = offset; currentOffset < eofset; currentOffset++)
		{
		    char c = source[currentOffset];
		    
		    //checking for new line and breaking the cycle
            if (c == '\r')
            {
                if (currentOffset < eofset - 1 && source[currentOffset + 1] == '\n')
                {
                    currentOffset++;
                }
                currentOffset++;
                break;
            }
            else if (c == '\n')
            {
                break;
            }
		    
		    //getting matchers by character if needed
		    if (this._firstCharacters != null)
	        {
	            if (offset < eofset)
	            {
	                //too bad I have no guarantee these matchers come in the same order as 
	                //the do in the children list
	                matchers = this._firstCharacters.getMatchers(c);
	            }
	            else
	            {
	                matchers = this._firstCharacters.getUncategorizedMatchers();
	            }
	        }
		    
		    //checking for the matchers
    		for (int i = 0; i < matchers.length; i++)
            {
    		    ITextMatcher currentMatcher = matchers[i];
    		    
    		    //first checking if we need to check this matcher
    		    int matcherIndex = indexes.get(currentMatcher);
    		    if (matcherIndex >= topMostMatchedIndex)
    		    {
    		        //we have no need of checking matchers that already looses or wins due to its
    		        //position and current top-most match result.
    		        continue;
    		    }
    		    
                int result = currentMatcher.match(source, currentOffset, eofset);
            
                if (result != -1)
                {
                    //saving current matcher index as a top-most index
                    topMostMatchedIndex = matcherIndex;
                    winningMatcher = currentMatcher;
                }
            }
		}
		
//		if (currentOffset == eofset && eofset > 0)
//		{
//		    currentOffset  = eofset - 1;
//		}
		
		//if any match was found, accepting the best match result
		if (winningMatcher != null)
		{
		    this.accept(source, offset, currentOffset, winningMatcher.getMatchedToken());
		    return currentOffset;
		}
		else
		{
		    return -1;
		}
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		int childCount = this.getChildCount();

		if (childCount > 0)
		{
			buffer.append("("); //$NON-NLS-1$
			buffer.append(this.getChild(0));
			
			for (int i = 1; i < childCount; i++)
			{
				buffer.append(" | "); //$NON-NLS-1$
				buffer.append(this.getChild(i));
			}
			
			buffer.append(")"); //$NON-NLS-1$
		}
		
		return buffer.toString();
	}
}
