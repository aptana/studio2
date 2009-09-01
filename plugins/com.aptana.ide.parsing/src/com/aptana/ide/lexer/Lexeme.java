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
package com.aptana.ide.lexer;

/**
 * @author Kevin Lindsey
 */
public class Lexeme implements Cloneable, Comparable<Offset>, IRange
{
	private static final int AFTER_EOL = 1;
	private static final int HIGHLIGHTED = 2;

	private IToken _token;
	private String _text;
	private int _flags;

	/**
	 * The lexeme's offset within the source text
	 */
	public int offset;

	/**
	 * The token type index for this lexeme
	 */
	public int typeIndex;

	/**
	 * The total length of the text contained in this lexeme
	 */
	public int length;

	/**
	 * Create a new instance of Lexeme
	 * 
	 * @param token
	 *            The parent token class this lexeme belongs to
	 * @param text
	 *            The matching text for this lexeme
	 * @param offset
	 *            The offset at which the match occurred
	 */
	public Lexeme(IToken token, String text, int offset)
	{
		this._token = token;
		this._text = text;
		this.offset = offset;

		this.typeIndex = token.getTypeIndex();
		
		this.length = text.length();
	}

	/**
	 * Adjust the current offset of this lexeme by the specified delta
	 * 
	 * @param delta
	 *            The amount by which to adjust this lexeme's offset
	 */
	public void adjustOffset(int delta)
	{
		this.offset += delta;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException();
		}
	}

	/**
	 * compareTo
	 * 
	 * @param o
	 *            The key with which to compare against
	 * @return Returns -1 if object is past this lexeme, 1 if object is before this lexeme, or 0 if object is contained
	 *         within this lexeme
	 */
	public int compareTo(Offset o)
	{
		int offset = o.offset;
		int result = 0;

		if (offset < this.offset)
		{
			result = 1;
		}
		else if (this.offset + this.length <= offset)
		{
			result = -1;
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#containsOffset(int)
	 */
	public boolean containsOffset(int offset)
	{
		return (this.offset <= offset && offset < this.offset + this.length);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		boolean result = false;
		
		if (this == obj)
		{
			result = true;
		}
		else if (obj instanceof Lexeme)
		{
			Lexeme that = (Lexeme) obj;
			
			// NOTE: [KEL] Should we compare flags too?
			result = this._text.equals(that._text) && this._token == that._token;
		}
		
		return result;
	}

	/**
	 * Get the lexeme class name of this token
	 * 
	 * @return Returns this lexeme's category name
	 */
	public String getCategory()
	{
		return this._token.getCategory();
	}

	/**
	 * Get the lexeme class name index for this token
	 * 
	 * @return Returns this lexeme's category index
	 */
	public int getCategoryIndex()
	{
		return this._token.getCategoryIndex();
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getEndingOffset()
	 */
	public int getEndingOffset()
	{
		return this.offset + this.length;
	}

	/**
	 * Get the owning language for this lexeme
	 * 
	 * @return Returns then MIME type of the language to which this lexeme belongs
	 */
	public String getLanguage()
	{
		return this._token.getLanguage();
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getLength()
	 */
	public int getLength()
	{
		return this.length;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getStartingOffset()
	 */
	public int getStartingOffset()
	{
		return this.offset;
	}

	/**
	 * Get the text associated with this token
	 * 
	 * @return The token text
	 */
	public String getText()
	{
		return this._text;
	}

	/**
	 * Get the token (class) that this Lexeme is an instance of
	 * 
	 * @return Returns this lexeme's token class
	 */
	public IToken getToken()
	{
		return this._token;
	}

	/**
	 * Get the lexeme name (token class) of this token
	 * 
	 * @return The lexeme type name
	 */
	public String getType()
	{
		return this._token.getType();
	}

	/**
	 * Determine if this lexeme follows immediately after a line terminator
	 * 
	 * @return Returns true if this lexeme follows a line terminator
	 */
	public boolean isAfterEOL()
	{
		return (this._flags & AFTER_EOL) == AFTER_EOL;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#isEmpty()
	 */
	public boolean isEmpty()
	{
		return this.length <= 0;
	}

	/**
	 * isHighlighted
	 * 
	 * @return
	 */
	public boolean isHighlighted()
	{
		return (this._flags & HIGHLIGHTED) == HIGHLIGHTED;
	}
	
	/**
	 * Determine if this lexeme and the specified lexeme overlap
	 * 
	 * @param lexeme
	 *            The lexeme to test
	 * @return Returns true if the lexemes overlap
	 */
	public boolean isOverlapping(Lexeme lexeme)
	{
		int startingOffset1 = this.offset;
		int startingOffset2 = lexeme.offset;
		int endingOffset1 = this.getEndingOffset() - 1;
		int endingOffset2 = lexeme.getEndingOffset() - 1;

		return (startingOffset2 <= startingOffset1 && startingOffset1 <= endingOffset2)
				|| (startingOffset2 <= endingOffset1 && endingOffset1 <= endingOffset2)
				|| (startingOffset1 <= startingOffset2 && startingOffset2 <= endingOffset1)
				|| (startingOffset1 <= endingOffset2 && endingOffset2 <= endingOffset1);
	}

	/**
	 * Set flag indicating that this lexeme comes immediately after a line terminator
	 */
	public void setAfterEOL()
	{
		this._flags |= AFTER_EOL;
	}

	/**
	 * setHighlighted
	 */
	public void setHighlighted(boolean value)
	{
		if (value)
		{
			this._flags |= HIGHLIGHTED;
		}
		else
		{
			this._flags &= ~HIGHLIGHTED;
		}
	}
	
	/**
	 * Sets the token for this lexeme
	 * 
	 * @param token
	 */
	public void setToken(IToken token)
	{
		this._token = token;
		this.typeIndex = token.getTypeIndex();
	}

	/**
	 * Return a string representation of this token
	 * 
	 * @return Returns a string representation of this token
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		String className = this.getCategory();
		String name = this.getType();

		sb.append("[").append(this.getLanguage()).append("] "); //$NON-NLS-1$ //$NON-NLS-2$

		if (className.equals("default") == false) //$NON-NLS-1$
		{
			sb.append(className).append("."); //$NON-NLS-1$
		}

		sb.append(name);
		sb.append("@").append(this.offset).append("-").append(this.getEndingOffset()); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append(": ~").append(this._text).append("~"); //$NON-NLS-1$ //$NON-NLS-2$

		return sb.toString();
	}

	/**
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this._text=text;
	}
}
