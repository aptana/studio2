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

import com.aptana.xml.INode;


/**
 * @author Kevin Lindsey
 */
public class OrMatcher extends AbstractTextMatcher
{
	private MatcherMap _firstCharacters;
	
	/**
	 * MatcherGroup
	 */
	public OrMatcher()
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
		
		for (int i = 0; i < this.getChildCount(); i++)
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
		MatcherMap map = new MatcherMap();
		
		for (int i = 0; i < this.getChildCount(); i++)
		{
			INode child = this.getChild(i);
			
			if (child instanceof ITextMatcher)
			{
				ITextMatcher matcher = (ITextMatcher) child;
				
				matcher.addFirstCharacters(map);
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
		
		for (int i = 0; i < this.getChildCount(); i++)
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
		int result = -1;
		
		if (this._firstCharacters != null)
		{
			ITextMatcher[] matchers;
			
			if (offset < eofset)
			{
				char c = source[offset];
				matchers = this._firstCharacters.getMatchers(c);
			}
			else
			{
				matchers = this._firstCharacters.getUncategorizedMatchers();
			}
			
			for (int i = 0; i < matchers.length; i++)
			{
				ITextMatcher matcher = matchers[i];
				
				result = matcher.match(source, offset, eofset);
			
				if (result != -1)
				{
					this.accept(source, offset, result, this.token);
					break;
				}
			}
		}
		else
		{
			for (int i = 0; i < this.getChildCount(); i++)
			{
				ITextMatcher matcher = (ITextMatcher) this.getChild(i);
				
				result = matcher.match(source, offset, eofset);
				
				if (result != -1)
				{
					this.accept(source, offset, result, this.token);
					break;
				}
			}
		}
		
		return result;
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
