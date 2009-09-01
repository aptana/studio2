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
package com.aptana.ide.editors.unified;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.IParseState;

/**
 * @author Pavel Petrochenko
 */
public abstract class AbstractPairFinder implements IPairFinder
{

	/**
	 * @see com.aptana.ide.editors.unified.IPairFinder#findPairMatch(int, com.aptana.ide.parsing.IParseState)
	 */
	public PairMatch findPairMatch(int offset, IParseState parseState)
	{
		LexemeList lexemeList = parseState.getLexemeList();
		Lexeme cursorLexeme = lexemeList.getLexemeFromOffset(offset);
		int loopCount = 2;

		if (cursorLexeme == null && offset > 0)
		{
			cursorLexeme = lexemeList.getLexemeFromOffset(offset - 1);
		}
		return findPairMatch(offset, parseState, cursorLexeme, loopCount);
	}

	/**
	 * @param offset
	 * @param parseState
	 * @param cursorLexeme
	 * @param loopCount
	 * @return PairMatch
	 */
	public abstract PairMatch findPairMatch(int offset, IParseState parseState, Lexeme cursorLexeme, int loopCount);

	/**
	 * @see com.aptana.ide.editors.unified.IPairFinder#displayOnlyMatch()
	 */
	public boolean displayOnlyMatch()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IPairFinder#doNotDisplay()
	 */
	public boolean doNotDisplay()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IPairFinder#getPairFinderColor()
	 */
	public Color getPairFinderColor()
	{
		return UnifiedColorManager.getInstance().getColor(new RGB(150, 150, 150));
	}

}
