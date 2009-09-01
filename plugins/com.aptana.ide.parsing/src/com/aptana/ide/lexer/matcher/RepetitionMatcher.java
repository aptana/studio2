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

import com.aptana.ide.lexer.ITokenList;
import com.aptana.xml.INode;


/**
 * @author Kevin Lindsey
 */
public class RepetitionMatcher extends AbstractTextMatcher
{
	/**
	 * INFINITY
	 */
	public static final int INFINITY = Integer.MAX_VALUE;
	
	private int _minimum;
	private int _maximum;

	/**
	 * RepetitionMatcher
	 */
	public RepetitionMatcher()
	{
		this(1,1);
	}
	
	/**
	 * RepetitionMatcher
	 * 
	 * @param min
	 * @param max
	 */
	public RepetitionMatcher(int min, int max)
	{
		this._minimum = min;
		this._maximum = max;
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
		if (this.getChildCount() > 0)
		{
			ITextMatcher child = (ITextMatcher) this.getChild(0);
			
			child.addFirstCharacters(map, target);
		}
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#canMatchNothing()
	 */
	public boolean canMatchNothing()
	{
		boolean result = false;
		
		if (this._minimum == 0)
		{
			result = true;
		}
		else
		{
			if (this.getChildCount() > 0)
			{
				ITextMatcher child = (ITextMatcher) this.getChild(0);
				
				result = child.canMatchNothing();
			}
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.MatcherElement#createChildrenTokens(com.aptana.ide.lexer.ITokenList)
	 */
	protected void createChildrenTokens(ITokenList tokenList)
	{
		// wrap multiple children in an <and> element
		this.wrapChildrenInAndElement();
		
		// process as usual
		super.createChildrenTokens(tokenList);
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.ITextMatcher#match(char[], int, int)
	 */
	public int match(char[] source, int offset, int eofset)
	{
		int index = offset;
		int result = -1;
		
		if (this.getChildCount() > 0)
		{
			ITextMatcher child = (ITextMatcher) this.getChild(0);
			
			// handle min matches
			if (this._minimum > 0)
			{
				for (int i = 0; i < this._minimum; i++)
				{
					result = child.match(source, index, eofset);
					
					if (result == -1)
					{
						break;
					}
					else
					{
						index = result;
					}
				}
			}
			else
			{
				result = index;
			}
			
			// handle max matches
			if (result != -1)
			{
				for (int i = this._minimum; i < this._maximum; i++)
				{
					int temp = child.match(source, index, eofset);
					
					if (temp == -1)
					{
						break;
					}
					else
					{
						result = temp;
						index = result;
					}
				}
			}
		}
		
		// set matching token, if we matched successfully
		if (result != -1)
		{
			this.accept(source, offset, result, this.token);
		}
		
		return result;
	}

	/**
	 * setMax
	 *
	 * @param max
	 */
	public void setMax(int max)
	{
		this._maximum = max;
	}
	
	/**
	 * setMin
	 *
	 * @param min
	 */
	public void setMin(int min)
	{
		this._minimum = min;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("("); //$NON-NLS-1$
		
		if (this.getChildCount() > 0)
		{
			INode child = this.getChild(0);
			
			buffer.append(child);
		}
		
		buffer.append("){").append(this._minimum).append(",").append(this._maximum).append("}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		return buffer.toString();
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#validateLocal()
	 */
	protected void validateLocal()
	{
		super.validateLocal();
		
		int count = this.getChildCount();
		
		if (count == 0)
		{
			this.getDocument().sendError(Messages.RepetitionMatcher_No_Children, this);
		}
		else if (count > 1)
		{
			this.getDocument().sendInfo(Messages.RepetitionMatcher_Wrapping_Children, this);
		}
	}
}
