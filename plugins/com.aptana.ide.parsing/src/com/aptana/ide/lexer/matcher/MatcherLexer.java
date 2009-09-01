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

import com.aptana.ide.lexer.ICodeBasedTokenList;
import com.aptana.ide.lexer.AbstractLexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.Range;

/**
 * @author Kevin Lindsey
 * @author Pavel Petrochenko
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class MatcherLexer extends AbstractLexer
{
	/**
	 * @see com.aptana.ide.lexer.ILexer#find(java.lang.String)
	 */
	public Range find(String groupName) throws LexerException
	{
		int startingPosition = this.currentOffset;
		Range result = Range.Empty;

		// save current group
		String currentGroup = this.getGroup();

		// switch to find-group
		this.setGroup(groupName);

		// Get the currently active token list
		ICodeBasedTokenList tokenList = (ICodeBasedTokenList) this.getCurrentTokenList();
		char[] source = this.source;
		int eof = this.eofOffset;

		for (int start = this.currentOffset; start < eof; start++)
		{
			int position = tokenList.match(source, start, eof);
			
			if (position != -1 && position >= start)
			{
				result = new Range(start, position);
				break;
			}
		}

		// restore group
		this.setGroup(currentGroup);

		// restore current offset in source
		this.currentOffset = startingPosition;

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.AbstractLexer#match()
	 */
	protected int match()
	{
		int startingPosition = this.currentOffset;
		int result = startingPosition;
		
		this.lastMatchedTokenIndex = -1;

		if( this.getCurrentTokenList() instanceof ICodeBasedTokenList ){
			
			// Get the currently active token list
			ICodeBasedTokenList tokenList = (ICodeBasedTokenList) this.getCurrentTokenList();
	
			synchronized (tokenList)
			{
				// Perform a match on the source text
				result = tokenList.match(this.source, startingPosition, this.eofOffset);
	
				// NOTE: the second expression here is a workaround.
				if (result != -1 && result >= startingPosition)
				{
					this.lastMatchedTokenIndex = tokenList.getLastMatchedTokenIndex();
				}
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.ILexer#getSourceUnsafe()
	 */
	public char[] getSourceUnsafe()
	{
		return source;
	}
}
