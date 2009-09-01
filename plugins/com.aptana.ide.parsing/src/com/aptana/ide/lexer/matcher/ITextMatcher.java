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

import com.aptana.ide.lexer.IToken;
import com.aptana.xml.INode;

/**
 * @author Kevin Lindsey
 */
public interface ITextMatcher extends INode
{
	/**
	 * Add any first character that can potentially match when this matcher is called
	 * 
	 * @param map
	 */
	void addFirstCharacters(MatcherMap map);

	/**
	 * This functions the same as addFirstCharacter(MatcherMap); however, the matcher that will use this matcher's first
	 * character as a possible transition is specified in the target parameter.
	 * 
	 * @param map
	 * @param target
	 */
	void addFirstCharacters(MatcherMap map, ITextMatcher target);

	/**
	 * Return a flag indicating if this matcher can return a positive match without consuming characters
	 * 
	 * @return boolean
	 */
	boolean canMatchNothing();

	/**
	 * Gets the matched token, null is none matched
	 * 
	 * @return - matched token or null
	 */
	IToken getMatchedToken();

	/**
	 * Gets the ending position of the match starting at the offset. Return -1 if no match occured.
	 * 
	 * @param source
	 *            character array to walk
	 * @param offset
	 *            offset into array
	 * @param eofset
	 *            end of file offset
	 * @return Returns the offset where the match ended or -1 if no match occurred
	 */
	int match(char[] source, int offset, int eofset);
}
