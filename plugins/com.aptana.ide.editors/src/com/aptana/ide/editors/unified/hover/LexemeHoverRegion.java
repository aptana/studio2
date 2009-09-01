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
package com.aptana.ide.editors.unified.hover;

import org.eclipse.jface.text.IRegion;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.metadata.IDocumentation;

/**
 * A region describes a certain range in an indexed text store. This amends the default
 * interpretation to specify the current lexeme.
 * 
 * @author Ingo Muschenetz
 */
public class LexemeHoverRegion implements IRegion
{
	/*
	 * Fields
	 */
	// private IObject targetObject;
	private Lexeme lexeme;

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of LexemeHoverRegion
	 * 
	 * @param lex
	 *            The lexeme at the current hover position
	 * @param obj
	 *            The object that this lexeme refers to
	 */
	// public LexemeHoverRegion(Lexeme lex, IObject obj)
	// {
	// super();
	// targetObject = obj;
	// lexeme = lex;
	// }
	/*
	 * Properties
	 */

	/**
	 * Create a new instance of LexemeHoverRegion
	 * 
	 * @param lex
	 *            The lexeme at the current hover position
	 */
	public LexemeHoverRegion(Lexeme lex)
	{
		super();
		lexeme = lex;
	}

	/**
	 * Gets the underlying lexeme (an identifier) that the region is based on.
	 * 
	 * @return Returns the underlying lexeme.
	 */
	public Lexeme getLexeme()
	{
		return lexeme;
	}

	/**
	 * Gets the Object reference that the region represents.
	 * 
	 * @return The Object reference that the region represents.
	 */
	// public IObject getObject()
	// {
	// return targetObject;
	// }
	/**
	 * Gets the documentation on the given targetObject object, if any.
	 * 
	 * @return The Object reference that the region represents.
	 */
	public IDocumentation getDocumentation()
	{
		// if (targetObject != null)
		// {
		// return targetObject.getDocumentation();
		// }
		// else
//		{
			return null;
//		}
	}

	/**
	 * Gets the length of the lexeme that is being hovered over
	 * 
	 * @see org.eclipse.jface.text.IRegion#getLength()
	 */
	public int getLength()
	{
		if (lexeme != null)
		{
			return lexeme.length;
		}
		else
		{
			return 0;
		}
	}

	/**
	 * Gets the offset of the current lexeme.
	 * 
	 * @see org.eclipse.jface.text.IRegion#getOffset()
	 */
	public int getOffset()
	{
		if (lexeme != null)
		{
			return lexeme.offset;
		}
		else
		{
			return 0;
		}
	}

}
