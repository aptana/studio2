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
package com.aptana.ide.lexer.matcher.model;

import com.aptana.ide.lexer.ITokenList;
import com.aptana.ide.lexer.matcher.ITextMatcher;
import com.aptana.ide.lexer.matcher.MatcherTokenList;
import com.aptana.xml.INode;


/**
 * @author Kevin Lindsey
 */
public class TokenGroupElement extends MatcherElement
{
	/**
	 * TokenGroup
	 */
	public TokenGroupElement()
	{
		this.addChildType(CategoryGroupElement.class);
		this.addChildType(ITextMatcher.class);
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.model.MatcherElement#createTokens(com.aptana.ide.lexer.ITokenList)
	 */
	public void createTokens(ITokenList tokenList)
	{
		super.createTokens(tokenList);
		
		// NOTE: IMatcher children are removed as they are added to the MatcherTokenList, so we can't rely on child
		// position since that will change as each child is re-parented. However, some children are not IMatchers, so we
		// need to explicity remove those for this loop to end
		
		while (this.getChildCount() > 0)
		{
			INode child = this.getChild(0);
			
			if (tokenList instanceof MatcherTokenList)
			{
				if (child instanceof ITextMatcher && child instanceof IMatcherElement)
				{
					String group = ((IMatcherElement) child).getGroup();
					
					((MatcherTokenList) tokenList).addMatcherToGroup((ITextMatcher) child, group);
				}
			}
			
			// make sure the child was removed
			if (child.getParent() == this)
			{
				this.removeChild(child);
			}
		}
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.MatcherElement#validateLocal()
	 */
	protected void validateLocal()
	{
		String group = this.getGroup();
		
		if (group == null || group.length() == 0)
		{
			this.getDocument().sendError(Messages.TokenGroupElement_Missing_Group, this);
		}
	}
}
