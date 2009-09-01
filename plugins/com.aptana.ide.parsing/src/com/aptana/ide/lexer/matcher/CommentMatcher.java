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
public class CommentMatcher extends AbstractTextMatcher
{
	private ITextMatcher _startComment;
	private ITextMatcher _endComment;

	/**
	 * CommentMatcher
	 */
	public CommentMatcher()
	{
		this.setStart(new StringMatcher("//")); //$NON-NLS-1$

		ZeroOrMoreMatcher zom = new ZeroOrMoreMatcher();
		CharacterClassMatcher cc = new CharacterClassMatcher("\r\n"); //$NON-NLS-1$
		cc.setNegate(true);
		zom.appendChild(cc);

		this.setEnd(zom);
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
		if (this._startComment != null)
		{
			this._startComment.addFirstCharacters(map, target);
		}
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.ITextMatcher#match(char[], int, int)
	 */
	public int match(char[] source, int offset, int eofset)
	{
		int result = -1;

		if (this._startComment != null && this._endComment != null)
		{
			result = this._startComment.match(source, offset, eofset);

			if (result != -1)
			{
				while (result < eofset)
				{
					int endResult = this._endComment.match(source, result, eofset);

					if (endResult != -1)
					{
						result = endResult;
						break;
					}
					else
					{
						result++;
					}
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
	 * setEnd
	 * 
	 * @param end
	 */
	public void setEnd(String end)
	{
		this.setEnd(new StringMatcher(end));
	}

	/**
	 * setEnd
	 * 
	 * @param end
	 */
	public void setEnd(ITextMatcher end)
	{
		this._endComment = end;
	}

	/**
	 * setStart
	 * 
	 * @param start
	 */
	public void setStart(String start)
	{
		this.setStart(new StringMatcher(start));
	}

	/**
	 * setStart
	 * 
	 * @param start
	 */
	public void setStart(ITextMatcher start)
	{
		this._startComment = start;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#validateLocal()
	 */
	protected void validateLocal()
	{
		super.validateLocal();

		if (this._startComment == null)
		{
			this.getDocument().sendError(Messages.CommentMatcher_Start_Not_Defined, this);
		}
		else
		{
			if (this._startComment instanceof AbstractTextMatcher)
			{
				((AbstractTextMatcher) this._startComment).validate();
			}
		}

		if (this._endComment == null)
		{
			this.getDocument().sendError(Messages.CommentMatcher_End_Not_Defined, this);
		}
		else
		{
			if (this._endComment instanceof AbstractTextMatcher)
			{
				((AbstractTextMatcher) this._endComment).validate();
			}
		}
	}
}
