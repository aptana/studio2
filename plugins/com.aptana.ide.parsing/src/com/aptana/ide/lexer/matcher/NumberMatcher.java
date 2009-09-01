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
 * @author Kevin Sawicki
 */
public class NumberMatcher extends AndMatcher
{
	private boolean _matchNegative;
	private boolean _matchPositive;
	private boolean _matchIntegerPart;
	private boolean _matchFractionPart;
	private boolean _matchSciNotation;

	/**
	 * NumberMatcher
	 */
	public NumberMatcher()
	{
		this(true, true, true, true, false);
	}

	/**
	 * NumberMatcher
	 * @param hasNegative 
	 * @param hasPositive 
	 * @param hasIntegerPart 
	 * @param hasFractionPart 
	 * @param hasScientificNotation 
	 * 
	 */
	public NumberMatcher(boolean hasNegative, boolean hasPositive, boolean hasIntegerPart, boolean hasFractionPart, boolean hasScientificNotation)
	{
		this._matchNegative = hasNegative;
		this._matchPositive = hasPositive;
		this._matchIntegerPart = hasIntegerPart;
		this._matchFractionPart = hasFractionPart;
		this._matchSciNotation = hasScientificNotation;
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addFirstCharacters(com.aptana.ide.lexer.matcher.MatcherMap, com.aptana.ide.lexer.matcher.ITextMatcher)
	 */
	public void addFirstCharacters(MatcherMap map, ITextMatcher target)
	{
		if (this._matchPositive)
		{
			map.addCharacterMatcher('+', target);
		}
		if (this._matchNegative)
		{
			map.addCharacterMatcher('-', target);
		}
		if (this._matchFractionPart)
		{
			map.addCharacterMatcher('.', target);
		}
		
		map.addDigitMatcher(target);
	}

	/**
	 * buildMatcher
	 */
	private void buildMatcher()
	{
		this.addPlusOrMinus();
		this.addIntegerPart();
		this.addFractionPart();
		this.addScientificNotation();
	}
	
	/**
	 * addFractionPart
	 */
	private void addFractionPart()
	{
		if (this._matchFractionPart)
		{
			// match '.'
			CharacterMatcher dot = new CharacterMatcher('.');
			
			// match \d+
			OneOrMoreMatcher digits = new OneOrMoreMatcher();
			digits.appendChild(new DigitMatcher());
			
			// \s
			WhitespaceMatcher whitespace = new WhitespaceMatcher();
			
			// $
			EndOfFileMatcher eof = new EndOfFileMatcher();
			
			// \s | $
			OrMatcher whitespaceOrEOF = new OrMatcher();
			whitespaceOrEOF.appendChild(whitespace);
			whitespaceOrEOF.appendChild(eof);
			
			// (?> \s | $)
			LookaheadMatcher lookahead = new LookaheadMatcher();
			lookahead.appendChild(whitespaceOrEOF);
			
			// \d+ | (?>\s|$)
			OrMatcher or = new OrMatcher();
			or.appendChild(digits);
			or.appendChild(lookahead);
			
			// require both in sequence
			AndMatcher and = new AndMatcher();
			and.appendChild(dot);
			and.appendChild(or);
			
			// make the whole thing optional
			OptionalMatcher option = new OptionalMatcher();
			option.appendChild(and);
			
			// add to list
			this.appendChild(option);
		}
	}

	/**
	 * addIntegerPart
	 */
	private void addIntegerPart()
	{
		if (this._matchIntegerPart)
		{
			// match \d+
			//OneOrMoreMatcher repetition = new OneOrMoreMatcher();
			ZeroOrMoreMatcher repetition = new ZeroOrMoreMatcher();
			repetition.appendChild(new DigitMatcher());

			// add to list
			this.appendChild(repetition);
		}
	}

	/**
	 * addScientificNotation
	 */
	private void addScientificNotation()
	{
		if (this._matchSciNotation)
		{
			// [eE]
			CharacterClassMatcher exp = new CharacterClassMatcher("eE"); //$NON-NLS-1$
			
			// [-+]?
			OptionalMatcher plusOrMinus = new OptionalMatcher();
			plusOrMinus.appendChild(new CharacterClassMatcher("-+")); //$NON-NLS-1$
			
			// \d+
			OneOrMoreMatcher digits = new OneOrMoreMatcher();
			digits.appendChild(new DigitMatcher());
			
			// combine
			AndMatcher group = new AndMatcher();
			group.appendChild(exp);
			group.appendChild(plusOrMinus);
			group.appendChild(digits);
			
			// make optional
			OptionalMatcher optional = new OptionalMatcher();
			optional.appendChild(group);
			
			// add to list
			this.appendChild(optional);
		}
	}

	/**
	 * addPlusOrMinus
	 */
	private void addPlusOrMinus()
	{
		if (this._matchPositive || this._matchNegative)
		{
			OptionalMatcher option = new OptionalMatcher();
			
			if (this._matchPositive && this._matchNegative)
			{
				option.appendChild(new CharacterClassMatcher("-+")); //$NON-NLS-1$
			}
			else if (this._matchPositive)
			{
				option.appendChild(new CharacterMatcher('+'));
			}
			else
			{
				option.appendChild(new CharacterMatcher('-'));
			}
			
			this.appendChild(option);
		}
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AndMatcher#match(char[], int, int)
	 */
	public int match(char[] source, int offset, int eofset)
	{
		if (this.getChildCount() == 0)
		{
			this.buildMatcher();
		}
		
		int result = super.match(source, offset, eofset);
		
		// make sure we advanced, if we matched
		if (result == offset)
		{
			result = -1;
		}
		else if (result - 1 == offset)
		{
			char c = source[result - 1];
			
			switch (c)
			{
				case '-':
				case '+':
				case '.':
					result = -1;
					break;
					
				default:
					break;
			}
		}
		
		return result;
	}

	/**
	 * getMatchFractionPart
	 *
	 * @return boolean
	 */
	public boolean getMatchFractionPart()
	{
		return this._matchFractionPart;
	}

	/**
	 * setMatchFractionPart
	 *
	 * @param matchFractionPart
	 */
	public void setMatchFractionPart(boolean matchFractionPart)
	{
		this._matchFractionPart = matchFractionPart;
	}

	/**
	 * getMatchIntegerPart
	 *
	 * @return boolean
	 */
	public boolean getMatchIntegerPart()
	{
		return this._matchIntegerPart;
	}

	/**
	 * setMatchIntegerPart
	 *
	 * @param matchIntegerPart
	 */
	public void setMatchIntegerPart(boolean matchIntegerPart)
	{
		this._matchIntegerPart = matchIntegerPart;
	}

	/**
	 * getMatchNegative
	 *
	 * @return boolean
	 */
	public boolean getMatchNegative()
	{
		return this._matchNegative;
	}

	/**
	 * setMatchNegative
	 *
	 * @param matchNegative
	 */
	public void setMatchNegative(boolean matchNegative)
	{
		this._matchNegative = matchNegative;
	}

	/**
	 * getMatchPositive
	 *
	 * @return boolean
	 */
	public boolean getMatchPositive()
	{
		return this._matchPositive;
	}

	/**
	 * setMatchPositive
	 *
	 * @param matchPositive
	 */
	public void setMatchPositive(boolean matchPositive)
	{
		this._matchPositive = matchPositive;
	}

	/**
	 * getMatchSciNotation
	 *
	 * @return boolean
	 */
	public boolean getMatchSciNotation()
	{
		return this._matchSciNotation;
	}

	/**
	 * setMatchSciNotation
	 *
	 * @param matchSciNotation
	 */
	public void setMatchSciNotation(boolean matchSciNotation)
	{
		this._matchSciNotation = matchSciNotation;
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AndMatcher#toString()
	 */
	public String toString()
	{
		return "Number"; //$NON-NLS-1$
	}
}
