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

import java.util.ArrayList;

import com.aptana.ide.lexer.IToken;
import com.aptana.xml.INode;

/**
 * @author Pavel Petrochenko
 */
class MultiwordMatcher extends AbstractTextMatcher
{
	static final class CharTable
	{
		IToken primaryMatcher;
		int count = 0;
		private CharTable[] childs = new CharTable[100];

		public CharTable select(char c)
		{
			if (c >= this.childs.length)
			{
				return null;
			}
			return this.childs[c];
		}

		public void internalAddChar(char c, CharTable table)
		{
			this.count++;
			final int e = c;
			if (e >= this.childs.length)
			{
				final CharTable[] newChar = new CharTable[e + 1];
				System.arraycopy(this.childs, 0, newChar, 0, this.childs.length);
				this.childs = newChar;
			}
			this.childs[e] = table;
		}

		public void add(String word, int offset, AbstractTextMatcher token)
		{
			final int last = word.length();
			if (last == offset)
			{
				this.primaryMatcher = token.token;
				return;
			}
			final char c = word.charAt(offset);
			CharTable select = this.select(c);
			if (select == null)
			{
				final CharTable newT = new CharTable();
				this.internalAddChar(c, newT);
				select = newT;
			}
			select.add(word, offset + 1, token);
		}
	}

	int maxLength;
	ArrayList<ITextMatcher> childs = new ArrayList<ITextMatcher>();
	AbstractTextMatcher parent;
	CharTable cTable = new CharTable();

	/**
	 * MultiwordMatcher
	 * 
	 * @param parent
	 */
	public MultiwordMatcher(AbstractTextMatcher parent)
	{
		this.parent = parent;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addChildTypes()
	 */
	@Override
	public void addChildTypes()
	{
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.MatcherElement#appendChild(com.aptana.xml.INode)
	 */
	@Override
	public void appendChild(INode child)
	{
		this.childs.add((ITextMatcher) child);
		
		// buf.appendChild(child);
		if (child instanceof StringMatcher)
		{
			final StringMatcher cmt = (StringMatcher) child;
			
			this.cTable.add(cmt.getText(), 0, cmt);
			this.maxLength = Math.max(cmt.getText().length(), this.maxLength);
			
			return;
		}
		
//		if (child instanceof OrMatcher.SingleCharMatcher)
//		{
//			final OrMatcher.SingleCharMatcher cm = (OrMatcher.SingleCharMatcher) child;
//			
//			this.maxLength = Math.max(1, this.maxLength);
//			this.cTable.add(cm.getCharacter() + "", 0, cm);
//			
//			return;
//		}
		
		throw new IllegalArgumentException(Messages.MultiwordMatcher_Unsupported_type);
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#match(char[], int, int)
	 */
	@Override
	public int match(char[] source, int offset, int eofOffset)
	{
		final int min = Math.min(offset + this.maxLength, eofOffset);
		CharTable ct = this.cTable;
		CharTable lastMatch = null;
		
		for (int a = offset; a < min; a++)
		{
			final char c = source[a];
			
			ct = ct.select(c);
			
			if (ct == null)
			{
				if (lastMatch != null)
				{
					this.parent.accept(source, offset, a, lastMatch.primaryMatcher);
					
					return a;
				}
				
				return -1;
			}
			
			if (ct.primaryMatcher != null)
			{
				if (ct.count == 0)
				{
					a = a + 1;
					this.parent.accept(source, offset, a, ct.primaryMatcher);
					
					return a;
				}
				else
				{
					lastMatch = ct;
				}
			}
		}
		return -1;
	}
}
