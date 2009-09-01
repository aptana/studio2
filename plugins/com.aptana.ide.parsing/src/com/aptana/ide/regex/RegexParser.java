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
package com.aptana.ide.regex;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Stack;

import com.aptana.ide.regex.dfa.DFAGraph;
import com.aptana.ide.regex.inputs.AnyInput;
import com.aptana.ide.regex.inputs.CharacterClassInput;
import com.aptana.ide.regex.inputs.CharacterInput;
import com.aptana.ide.regex.inputs.DigitInput;
import com.aptana.ide.regex.inputs.Input;
import com.aptana.ide.regex.inputs.WhiteSpaceInput;
import com.aptana.ide.regex.inputs.WordInput;
import com.aptana.ide.regex.nfa.NFAGraph;

/**
 * Converts a regular expression expressed as a string into a state machine. This state machine can be used to recognize
 * text patterns as described by the original regular expression
 * <p>
 * <code>
 * This parser recognizes the following grammar:
 * <p>
 * Expression
 *     : OrExpression
 *     | '^' OrExpression
 *     | OrExpression '$'
 *     ;
 * OrExpression
 *     : OrExpression '|' AndExpression
 *     | AndExpression
 *     ;
 * AndExpression
 *     : AndExpression Factor
 *     | Factor
 *     ;
 * Factor
 *     : Term '*'
 *     | Term '+'
 *     | Term '?'
 *     | Term
 *     ;
 * Term
 *     : '[' string ']'
 *     | '[^' string ']'
 *     | '[]'
 *     | '[^]'
 *     | '.'
 *     | * character
 *     | * '(' Expression ')'
 *     ;
 * </code>
 * 
 * @author Kevin Lindsey
 */
public class RegexParser
{
	private char[] _regex;
	private int _index;
	Stack<NFAGraph> _nfaStack;
	int _acceptState;
	DFAGraph _dfa;

	/**
	 * @return The current character being processed in the regular expression
	 */
	private char getCurrentChar()
	{
		char result;

		if (this.getEOS() == false)
		{
			result = this._regex[this._index];
		}
		else
		{
			result = '\0';
		}

		return result;
	}

	/**
	 * Determine if the specified digit is a hexadecimal digit
	 * 
	 * @return Returns true if the character is a valid hexadecimal digit
	 */
	private boolean isHexDigit()
	{
		char digit = this.getCurrentChar();

		return ('0' <= digit && digit <= '9' || 'A' <= digit && digit <= 'F' || 'a' <= digit && digit <= 'f');
	}

	/**
	 * Return the DFA that recognizes the parsed regular expression
	 * 
	 * @return The DFA that recognizes the parsed regular expression
	 */
	public DFAGraph getDFAGraph()
	{
		return this._dfa;
	}

	/**
	 * Determines if we have reached the end of the regular expression string
	 * 
	 * @return A boolean that returns true once we have processed the entire regular expression string
	 */
	private boolean getEOS()
	{
		return (this._index >= this._regex.length);
	}

	/**
	 * Get the resulting NFA graph associated with the regex
	 * 
	 * @return Returns the NFA Graph associated with this regex
	 */
	public NFAGraph getNFAGraph()
	{
		NFAGraph result = null;

		if (this._nfaStack.size() > 0)
		{
			result = this._nfaStack.peek();
		}

		return result;
	}

	/**
	 * Get the stack of NFA machines created by this parser
	 * 
	 * @return The NFA stack
	 */
	public Stack<NFAGraph> getNFAStack()
	{
		return this._nfaStack;
	}

	/**
	 * Determines if the current character is a valid character to start a new And expression
	 * 
	 * @return Returns true if the current character can start an And expression
	 */
	private boolean isFirstInAndExpression()
	{
		boolean result = true;

		if (this.getEOS() == false)
		{
			switch (this.getCurrentChar())
			{
				case '\0':
				case '|':
				case ')':
				case '*':
				case '+':
				case '?':
				case '^':
				case '$':
					result = false;
					break;

				default:
					break;
			}
		}
		else
		{
			result = false;
		}

		return result;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of RegexParser
	 */
	public RegexParser()
	{
		this._nfaStack = new Stack<NFAGraph>();
	}

	/*
	 * Methods
	 */

	/**
	 * Advance to the next non-whitespace character
	 */
	private void advance()
	{
		int length = this._regex.length;

		if (this._index < length)
		{
			// advance at least one character
			this._index++;

			// now skip whitespace
			while (this._index < length && Character.isWhitespace(this._regex[this._index]))
			{
				this._index++;
			}
		}
	}

	/**
	 * Convert a regular expression expressed as a string into a DFA that recognizes the text pattern described by the
	 * regular expression. This method will associate a token index, lexer state, and new lexer state with this regular
	 * expression when it matches
	 * 
	 * @param regex
	 *            The regex to parse
	 * @param acceptState
	 *            The accept state to associate with this regex
	 * @throws ParseException
	 */
	public void parse(String regex, int acceptState) throws ParseException
	{
		if (regex == null)
		{
			throw new NullPointerException(Messages.RegexParser_Undefined);
		}
		if (regex.length() == 0)
		{
			throw new ParseException(Messages.RegexParser_Empty, 0);
		}

		this._regex = regex.toCharArray();
		this._index = -1;
		this._acceptState = acceptState;

		// prime current character
		this.advance();

		// parse regex
		if (this.parseExpression() == false)
		{
			Object[] messageArgs = new Object[] { regex, Integer.toString(this._index) };
			String message = MessageFormat.format(Messages.RegexParser_Parse_Error, messageArgs);

			throw new ParseException(message, 0);
		}
	}

	/**
	 * Parse a regular expression
	 * 
	 * @return A boolean that indicates whether the expression was successfully parsed or not.
	 */
	private boolean parseExpression()
	{
		boolean success = true;

		if (this.getCurrentChar() == '^')
		{
			// advance over '^'
			this.advance();

			// tag to anchor at the beginning of a line
		}

		while (success && this.getEOS() == false)
		{
			success = this.parseOrExpression();

			if (this._index == this._regex.length - 1 && this.getCurrentChar() == '$')
			{
				// advance over '$'
				this.advance();

				// tag to anchor at the end of a line
			}
		}

		return success;
	}

	/**
	 * Parse a regular expression OR expression
	 * 
	 * @return A boolean that indicates whether the expression was successfully parsed or not.
	 */
	private boolean parseOrExpression()
	{
		boolean success = true;

		if (this.parseAndExpression())
		{
			while (this.getCurrentChar() == '|')
			{
				// advance over '|'
				this.advance();

				// parse |'s right hand side
				if (this.parseAndExpression())
				{
					// build OR machine
					NFAGraph rhs = this._nfaStack.pop();
					NFAGraph lhs = this._nfaStack.peek();

					lhs.orMachines(rhs);
				}
				else
				{
					success = false;
					break;
				}
			}
		}
		else
		{
			success = false;
		}

		return success;
	}

	/**
	 * Parse a regular expression AND expression
	 * 
	 * @return A boolean that indicates whether the expression was successfully parsed or not.
	 */
	private boolean parseAndExpression()
	{
		boolean success = true;

		if (this.isFirstInAndExpression())
		{
			// get left-hand side
			this.parseFactor();

			// get remaining right-hand sides
			while (this.isFirstInAndExpression())
			{
				if (this.parseFactor())
				{
					// build AND machine
					NFAGraph rhs = this._nfaStack.pop();
					NFAGraph lhs = this._nfaStack.peek();

					lhs.andMachines(rhs);
				}
				else
				{
					success = false;
					break;
				}
			}
		}
		else
		{
			success = false;
		}

		return success;
	}

	/**
	 * Parse a regular expression factor
	 * 
	 * @return A boolean that indicates whether the expression was successfully parsed or not.
	 */
	private boolean parseFactor()
	{
		boolean success = true;

		if (this.parseTerm())
		{
			NFAGraph nfa = this._nfaStack.peek();

			switch (this.getCurrentChar())
			{
				case '*':
					// advance over '*' and build kleene closure
					this.advance();
					nfa.kleeneClosure();
					break;

				case '+':
					// advance over '+' and build positive closure
					this.advance();
					nfa.positiveClosure();
					break;

				case '?':
					// advance over '?' and build option
					this.advance();
					nfa.option();
					break;

				default:
					break;
			}
		}
		else
		{
			success = false;
		}

		return success;
	}

	/**
	 * Parse a regular expression term
	 * 
	 * @return A boolean that indicates whether the expression was successfully parsed or not.
	 */
	private boolean parseTerm()
	{
		boolean success = true;

		if (this.getCurrentChar() == '(')
		{
			// parse parenthetical sub-expression
			success = this.parseSubExpression();
		}
		else
		{
			NFAGraph newState = new NFAGraph(this._acceptState);

			switch (this.getCurrentChar())
			{
				case '.':
					// advance over '.' and add input type
					this.advance();
					newState.add(new AnyInput());
					break;

				case '\\':
					// parse escaped term
					newState.add(this.parseEscapedTerm());
					break;

				case '[':
					// parse character class
					newState.add(this.parseCharacterClass());
					break;

				default:
					// add character state
					newState.add(new CharacterInput(this.getCurrentChar()));

					// advance over character
					this.advance();
			}

			if (success)
			{
				this._nfaStack.push(newState);
			}
		}

		return success;
	}

	/**
	 * Parse a character class
	 */
	private Input parseCharacterClass()
	{
		// advance over '['
		this.advance();

		CharacterClassInput cci = new CharacterClassInput();
		char last = '\0';

		if (this.getCurrentChar() == '^')
		{
			// advance over '^'
			this.advance();

			// find complement of character class
			cci.setComplement(true);
		}

		if (this.getCurrentChar() == '-')
		{
			// add dash as input
			cci.addInput('-');

			// advance over '-'
			this.advance();
		}

		while (this.getEOS() == false && this.getCurrentChar() != ']')
		{
			switch (this.getCurrentChar())
			{
				case '-':
					// advance over '-'
					this.advance();

					if (last != '\0')
					{
						// build character set
						cci.addInputs(last, this.getCurrentChar());

						// advance over character
						this.advance();

						// rest last to catch hyphen errors
						last = '\0';
					}
					else
					{
						// error
						break;
					}
					break;

				case '\\':
					Input input = this.parseEscapedTerm();

					cci.addInputs(input.getCharacters());
					break;

				default:
					last = this.getCurrentChar();
					cci.addInput(last);

					// advance over character
					this.advance();

			}
			
//			if (this.getCurrentChar() == '-')
//			{
//				// advance over '-'
//				this.advance();
//				if (last != '\0')
//				{
//					// build character set
//					cci.addInputs(last, this.getCurrentChar());
//					// advance over character
//					this.advance();
//					// rest last to catch hyphen errors
//					last = '\0';
//				}
//				else
//				{
//					// error break;
//				}
//			}
//			else if (this.getCurrentChar() == '\\')
//			{
//				Input input = this.parseEscapedTerm();
//				cci.addInputs(input.getCharacters());
//			}
//			else
//			{
//				last = this.getCurrentChar();
//				cci.addInput(last);
//				// advance over character
//				this.advance();
//			}
		}

		if (this.getCurrentChar() == ']')
		{
			// advance over ']'
			this.advance();

			// save input
			// newState.addState(cci);
		}
		else
		{
			// success = false
		}

		return cci;
	}

	/**
	 * Parse a term escaped with a backslash
	 * 
	 * @return A boolean that indicates whether the expression was successfully parsed or not.
	 */
	private Input parseEscapedTerm()
	{
		Input result = null;

		// advance over '\'
		// this.advance();

		// NOTE: we can't use advance because it might skip over the next
		// character
		// if it is whitespace
		this._index++;

		switch (this.getCurrentChar())
		{
			case 'd':
				result = new DigitInput();
				break;

			case 'D':
				result = new DigitInput();
				result.setComplement(true);
				break;

			case 'f':
				result = new CharacterInput('\f');
				break;

			case 'n':
				result = new CharacterInput('\n');
				break;

			case 'r':
				result = new CharacterInput('\r');
				break;

			case 's':
				result = new WhiteSpaceInput();
				break;

			case 'S':
				result = new WhiteSpaceInput();
				result.setComplement(true);
				break;

			case 't':
				result = new CharacterInput('\t');
				break;

			case 'v':
				result = new CharacterInput('\u000B');
				break;

			case 'w':
				result = new WordInput();
				break;

			case 'W':
				result = new WordInput();
				result.setComplement(true);
				break;

			case 'x':
				int hi = 0;
				int lo = 0;

				this._index++;

				if (this.isHexDigit())
				{
					hi = Character.digit(getCurrentChar(), 16);

					// NOTE: we can't use advance because it might skip over the next
					// character
					// if it is whitespace
					this._index++;

					if (this.isHexDigit())
					{
						lo = Character.digit(getCurrentChar(), 16);
					}
					else
					{
						throw new IllegalStateException(Messages.RegexParser_Malformed_Hex);
					}
				}
				else
				{
					throw new IllegalStateException(Messages.RegexParser_Malformed_Hex);
				}

				result = new CharacterInput((char) (hi * 16 + lo));
				break;

			default:
				result = new CharacterInput(this.getCurrentChar());
				break;
		}

		// advance over term
		this.advance();

		return result;
	}

	/**
	 * Parse an expression inside of a parenthetical expression
	 * 
	 * @return A boolean that indicates whether the expression was successfully parsed or not.
	 */
	private boolean parseSubExpression()
	{
		boolean success = true;

		// advance over '('
		this.advance();

		if (this.parseOrExpression())
		{
			if (this.getCurrentChar() == ')')
			{
				// advance over ')'
				this.advance();
			}
			else
			{
				success = false;
			}
		}
		else
		{
			success = false;
		}

		return success;
	}

	/**
	 * Reset the parser in preparation for a new parse
	 */
	public void reset()
	{
		NFAGraph.reset();
		this._nfaStack.clear();
	}
}
