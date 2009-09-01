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
package com.aptana.ide.parsing;

import com.aptana.ide.lexer.IRange;
import com.aptana.ide.lexer.Offset;

/**
 * @author Kevin Lindsey
 */
public class ErrorMessage implements IErrorMessage
{
	private IRange _range;
	private String _message;

	/**
	 * Create a new instance of ErrorMessage
	 * 
	 * @param message
	 * @param range
	 */
	public ErrorMessage(String message, IRange range)
	{
		this._message = message;
		this._range = range;
	}

	/**
	 * @see com.aptana.ide.parsing.IErrorMessage#getErrorRange()
	 */
	public IRange getErrorRange()
	{
		return this._range;
	}

	/**
	 * @see com.aptana.ide.parsing.IErrorMessage#getMessage()
	 */
	public String getMessage()
	{
		return this._message;
	}

	/**
	 * @see com.aptana.ide.parsing.IErrorMessage#setOwningList(com.aptana.ide.parsing.ErrorList)
	 */
	public void setOwningList(ErrorList owningList)
	{
		// do nothing
	}

	/**
	 * @see com.aptana.ide.parsing.IErrorMessage#clear()
	 */
	public void clear()
	{
		// do nothing
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0)
	{
		int result = 0;
		IRange range = this.getErrorRange();
		
		if (arg0 instanceof Offset)
		{
			Offset offset = (Offset) arg0;
			
			if (range.containsOffset(offset.offset) == false)
			{
				if (offset.offset < range.getStartingOffset())
				{
					result = 1;
				}
				else
				{
					result = -1;
				}
			}
		}
		else if (arg0 instanceof IErrorMessage)
		{
			IErrorMessage that = (IErrorMessage) arg0;
			
			result = range.getEndingOffset() - that.getErrorRange().getEndingOffset();
		}
		
		return result;
	}
}
