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

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.bnf.AbstractHandler;
import com.aptana.ide.parsing.bnf.IReductionContext;
import com.aptana.xml.INode;

/**
 * @author Kevin Lindsey
 */
public class RegexMatcherHandler extends AbstractHandler
{
	private boolean _caseInsensitive;

	/**
	 * MatcherHandler
	 */
	public RegexMatcherHandler()
	{
		this(false);
	}
	
	/**
	 * MatcherHandler
	 */
	public RegexMatcherHandler(boolean caseInsensitive)
	{
		this._caseInsensitive = caseInsensitive;
	}

	/**
	 * onAddAndExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onAddAndExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		INode node1 = (INode) nodes[0];
		INode node2 = (INode) nodes[2];
		Object result;
		
		if (node1 instanceof OrMatcher)
		{
			OrMatcher orMatcher = (OrMatcher) node1;
			
			orMatcher.appendChild(node2);
			
			result = orMatcher;
		}
		else
		{
			OrMatcher matcher = new OrMatcher();
	
			matcher.appendChild(node1);
			matcher.appendChild(node2);
			
			result = matcher;
		}

		return result;
	}

	/**
	 * onAddCCExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onAddCCExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CharacterClassMatcher matcher = (CharacterClassMatcher) nodes[0];
		CharacterClassMatcher rhs = (CharacterClassMatcher) nodes[1];

		matcher.addCharacters(rhs.getCharacters());

		return matcher;
	}

	/**
	 * onAddFactor
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onAddFactor(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		INode node1 = (INode) nodes[0];
		INode node2 = (INode) nodes[1];
		Object result;

		if (node1 instanceof AndMatcher)
		{
			AndMatcher and1 = (AndMatcher) node1;
			
			and1.appendChild(node2);
			
			result = and1;
		}
		else
		{
			AndMatcher matcher = new AndMatcher();
			
			matcher.appendChild(node1);
			matcher.appendChild(node2);
			
			result = matcher;
		}

		return result;
	}

	/**
	 * onCC
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onCC(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		return nodes[1];
	}

	/**
	 * onCharacter
	 * 
	 * @param action
	 * @param nodes
	 * @return CharacterMatcher
	 */
	public Object onCharacter(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		char c = ((Lexeme) nodes[0]).getText().charAt(0);
		ITextMatcher result;
		
		if (this._caseInsensitive && Character.isLetter(c))
		{
			CharacterClassMatcher ccMatcher = new CharacterClassMatcher();
			
			ccMatcher.addCharacter(Character.toLowerCase(c));
			ccMatcher.addCharacter(Character.toUpperCase(c));
			
			result = ccMatcher;
		}
		else
		{
			result = new CharacterMatcher(c);
		}

		return result;
	}

	/**
	 * onCharacterClass
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onCharacterClass(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		return nodes[0];
	}

	/**
	 * onCharacterExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onCharacterExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CharacterClassMatcher matcher = new CharacterClassMatcher();
		char c = ((Lexeme) nodes[0]).getText().charAt(0);

		if (this._caseInsensitive && Character.isLetter(c))
		{
			matcher.addCharacter(Character.toLowerCase(c));
			matcher.addCharacter(Character.toUpperCase(c));
		}
		else
		{
			matcher.addCharacter(c);
		}

		return matcher;
	}

	/**
	 * onCharacterRangeExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onCharacterRangeExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CharacterClassMatcher matcher = new CharacterClassMatcher();
		char c1 = ((Lexeme) nodes[0]).getText().charAt(0);
		char c2 = ((Lexeme) nodes[2]).getText().charAt(0);
		
		// make sure characters are in lowest to highest order
		if (c1 > c2)
		{
			char tmp = c1;
			
			c1 = c2;
			c2 = tmp;
		}

		if (this._caseInsensitive && Character.isLetter(c1) && Character.isLetter(c2))
		{
			// add in all lowercase letters in range
			for (char c = Character.toLowerCase(c1); c <= Character.toLowerCase(c2); c++)
			{
				matcher.addCharacter(c);
			}
			
			// add in all uppercase letters in range
			for (char c = Character.toUpperCase(c1); c <= Character.toUpperCase(c2); c++)
			{
				matcher.addCharacter(c);
			}
		}
		else
		{
			// add all characters in the range
			for (char c = c1; c <= c2; c++)
			{
				matcher.addCharacter(c);
			}
		}

		return matcher;
	}

	/**
	 * onDot
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onDot(IReductionContext context)
	{
		CharacterClassMatcher matcher = new CharacterClassMatcher();

		matcher.setNegate(true);
		matcher.addCharacters(new char[] { '\r', '\n' });

		return matcher;
	}

	/**
	 * onEmptyCC
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onEmptyCC(IReductionContext context)
	{
		return new CharacterClassMatcher();
	}

	/**
	 * onEndOrExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onEndOrExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		AndMatcher matcher = new AndMatcher();
		EndOfLineMatcher endLine = new EndOfLineMatcher();

		matcher.appendChild((INode) nodes[0]);
		matcher.appendChild(endLine);

		return matcher;
	}

	/**
	 * onEscapedCharacter
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onEscapedCharacter(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Object result;
		
		char c = ((Lexeme) nodes[0]).getText().charAt(1);
		
		switch (c)
		{
			case 'A':
				result = new StartOfFileMatcher();
				break;
				
			case 'b':
				result = new WordBoundaryMatcher();
				break;
				
			case 'B':
				WordBoundaryMatcher wordBoundary = new WordBoundaryMatcher();
				
				wordBoundary.setNegate(true);
				
				result = wordBoundary;
				break;
			
			case 'D':
				DigitMatcher noDigits = new DigitMatcher();

				noDigits.setNegate(true);
				result = noDigits;
				break;
				
			case 'd':
				result = new DigitMatcher();
				break;
				
			case 'f':
				result = new CharacterMatcher('\f');
				break;
				
			case 'n':
				result = new CharacterMatcher('\n');
				break;
				
			case 'r':
				result = new CharacterMatcher('\r');
				break;
				
			case 'S':
				WhitespaceMatcher noWhitespace = new WhitespaceMatcher();
				
				noWhitespace.setNegate(true);
				result = noWhitespace;
				break;
				
			case 's':
				result = new WhitespaceMatcher();
				break;
				
			case 't':
				result = new CharacterMatcher('\t');
				break;
				
			case 'v':
				result = new CharacterMatcher('\u000B');
				break;
				
			case 'W':
				WordMatcher wordMatcher = new WordMatcher();

				wordMatcher.setNegate(true);
				
				result = wordMatcher;
				break;
				
			case 'w':
				result = new WordMatcher();
				break;
				
//			case 'x':
				
			case 'z':
				result = new EndOfFileMatcher();
				break;
				
//			case 'Z':
				
			default:
				result = new CharacterMatcher(c);
				break;
		}

		return result;
	}
	
	/**
	 * onFirstAndExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onFirstAndExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		return nodes[0];
	}

	/**
	 * onFirstCCExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onFirstCCExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		return nodes[0];
	}

	/**
	 * onFirstFactor
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onFirstFactor(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		return nodes[0];
	}

	/**
	 * onMinusCC
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onMinusCC(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CharacterClassMatcher matcher = (CharacterClassMatcher) nodes[2];

		matcher.addCharacter('-');

		return matcher;
	}

	/**
	 * onMinusOnlyCC
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onMinusOnlyCC(IReductionContext context)
	{
		CharacterClassMatcher matcher = new CharacterClassMatcher();

		matcher.addCharacter('-');

		return matcher;
	}

	/**
	 * onNegatedCC
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onNegatedCC(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CharacterClassMatcher matcher = (CharacterClassMatcher) nodes[2];

		matcher.setNegate(true);

		return matcher;
	}

	/**
	 * onNegatedEmptyCC
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onNegatedEmptyCC(IReductionContext context)
	{
		CharacterClassMatcher matcher = new CharacterClassMatcher();

		matcher.setNegate(true);

		return matcher;
	}

	/**
	 * onNegativeLookahead
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onNegativeLookahead(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		LookaheadMatcher matcher = new LookaheadMatcher();
		
		matcher.setNegate(true);
		matcher.appendChild((INode) nodes[1]);
		
		return matcher;
	}
	
	/**
	 * onNegatedMinusCC
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onNegatedMinusCC(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CharacterClassMatcher matcher = (CharacterClassMatcher) nodes[3];

		matcher.setNegate(true);
		matcher.addCharacter('-');

		return matcher;
	}

	/**
	 * onNegatedMinusOnlyCC
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onNegatedMinusOnlyCC(IReductionContext context)
	{
		CharacterClassMatcher matcher = new CharacterClassMatcher();

		matcher.setNegate(true);
		matcher.addCharacter('-');

		return matcher;
	}

	/**
	 * onOrExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onOrExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		return nodes[0];
	}

	/**
	 * onOrExprGroup
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onOrExprGroup(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		return nodes[1];
	}

	/**
	 * onPositiveLookahead
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onPositiveLookahead(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		LookaheadMatcher matcher = new LookaheadMatcher();
		
		matcher.appendChild((INode) nodes[1]);
		
		return matcher;
	}
	
	/**
	 * onStartAndEndOrExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onStartAndEndOrExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		AndMatcher matcher = new AndMatcher();
		StartOfLineMatcher startLine = new StartOfLineMatcher();
		EndOfLineMatcher endLine = new EndOfLineMatcher();

		matcher.appendChild(startLine);
		matcher.appendChild((INode) nodes[1]);
		matcher.appendChild(endLine);

		return matcher;
	}

	/**
	 * onStartOrExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onStartOrExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		AndMatcher matcher = new AndMatcher();
		StartOfLineMatcher startLine = new StartOfLineMatcher();

		matcher.appendChild(startLine);
		matcher.appendChild((INode) nodes[1]);

		return matcher;
	}

	/**
	 * onTerm
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onTerm(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		return nodes[0];
	}

	/**
	 * onTermPlus
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onTermPlus(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		OneOrMoreMatcher matcher = new OneOrMoreMatcher();

		matcher.appendChild((INode) nodes[0]);

		return matcher;
	}

	/**
	 * onTermQuestion
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onTermQuestion(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		OptionalMatcher matcher = new OptionalMatcher();

		matcher.appendChild((INode) nodes[0]);

		return matcher;
	}

	/**
	 * onTermRepeat
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onTermRepeat(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		String repeatString = ((Lexeme) nodes[2]).getText();
		int repeatCount = Integer.parseInt(repeatString);
		RepetitionMatcher matcher = new RepetitionMatcher(repeatCount, repeatCount);
		
		matcher.appendChild((INode) nodes[0]);
		
		return matcher;
	}
	
	/**
	 * onTermRepeatLowerBound
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onTermRepeatLowerBound(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		String lowerBoundString = ((Lexeme) nodes[2]).getText();
		int lowerBound = Integer.parseInt(lowerBoundString);
		RepetitionMatcher matcher = new RepetitionMatcher(lowerBound, Integer.MAX_VALUE);

		matcher.appendChild((INode) nodes[0]);
		
		return matcher;
	}
	
	/**
	 * onTermRepeatUpperBound
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onTermRepeatUpperBound(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		String upperBoundString = ((Lexeme) nodes[3]).getText();
		int upperBound = Integer.parseInt(upperBoundString);
		RepetitionMatcher matcher = new RepetitionMatcher(0, upperBound);

		matcher.appendChild((INode) nodes[0]);
		
		return matcher;
	}
	
	/**
	 * onTermRepeatRange
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onTermRepeatRange(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		String lowerBoundString = ((Lexeme) nodes[2]).getText();
		String upperBoundString = ((Lexeme) nodes[4]).getText();
		int lowerBound = Integer.parseInt(lowerBoundString);
		int upperBound = Integer.parseInt(upperBoundString);
		RepetitionMatcher matcher = new RepetitionMatcher(lowerBound, upperBound);
		
		matcher.appendChild((INode) nodes[0]);
		
		return matcher;
	}
	
	/**
	 * onTermStar
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onTermStar(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ZeroOrMoreMatcher matcher = new ZeroOrMoreMatcher();

		matcher.appendChild((INode) nodes[0]);

		return matcher;
	}

	/**
	 * onWhitespace
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onWhitespace(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		String whitespace = ((Lexeme) nodes[0]).getText();
		Object result;
		
		if (whitespace.length() > 1)
		{
			result = new StringMatcher(whitespace);
		}
		else
		{
			result = new CharacterMatcher(whitespace.charAt(0));
		}
		
		return result;
	}
	
	/**
	 * onWhitespaceExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onWhitespaceExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		String whitespace = ((Lexeme) nodes[0]).getText();

		return new CharacterClassMatcher(whitespace);
	}
}
