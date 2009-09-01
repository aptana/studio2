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

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.bnf.AbstractHandler;
import com.aptana.ide.parsing.bnf.IReductionContext;
import com.aptana.ide.regex.inputs.AnyInput;
import com.aptana.ide.regex.inputs.CharacterClassInput;
import com.aptana.ide.regex.inputs.CharacterInput;
import com.aptana.ide.regex.inputs.Input;
import com.aptana.ide.regex.nfa.NFAGraph;

/**
 * @author Kevin Lindsey
 */
public class RegexHandler extends AbstractHandler
{
	private int _acceptState;

	/**
	 * RegexHandler
	 * 
	 * @param acceptState
	 */
	public RegexHandler(int acceptState)
	{
		this._acceptState = acceptState;
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
		
		// should tag as '^'
		return nodes[1];
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
		
		// should tag as '^'
		// should tag as '$'
		return nodes[1];
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
	 * onEndOrExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onEndOrExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		
		// should tag as '$'
		return nodes[0];
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
		NFAGraph lhs = (NFAGraph) nodes[0];
		NFAGraph rhs = (NFAGraph) nodes[2];

		lhs.orMachines(rhs);

		return lhs;
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
	 * onAddFactor
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onAddFactor(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		NFAGraph lhs = (NFAGraph) nodes[0];
		NFAGraph rhs = (NFAGraph) nodes[1];

		lhs.andMachines(rhs);

		return lhs;
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
	 * onTermStart
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onTermStart(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		NFAGraph newState = (NFAGraph) nodes[0];

		newState.kleeneClosure();

		return newState;
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
		NFAGraph newState = (NFAGraph) nodes[0];

		newState.positiveClosure();

		return newState;
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
		NFAGraph newState = (NFAGraph) nodes[0];

		newState.option();

		return newState;
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
	 * onCharacter
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onCharacter(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		NFAGraph newState = new NFAGraph(this._acceptState);
		char c = ((Lexeme) nodes[0]).getText().charAt(0);
		newState.add(new CharacterInput(c));

		return newState;
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
		NFAGraph newState = new NFAGraph(this._acceptState);

		newState.add(new AnyInput());

		return newState;
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
		NFAGraph newState = new NFAGraph(this._acceptState);

		newState.add((Input) nodes[0]);

		return newState;
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
		return new CharacterClassInput();
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
		CharacterClassInput cc = new CharacterClassInput();

		cc.setComplement(true);

		return cc;
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
		CharacterClassInput cc = new CharacterClassInput();

		cc.addInput('-');

		return cc;
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
		CharacterClassInput cc = new CharacterClassInput();

		cc.setComplement(true);
		cc.addInput('-');

		return cc;
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
	 * onNegatedCC
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onNegatedCC(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CharacterClassInput cc = (CharacterClassInput) nodes[2];

		cc.setComplement(true);

		return cc;
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
		CharacterClassInput cc = (CharacterClassInput) nodes[2];

		cc.addInput('-');

		return cc;
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
		CharacterClassInput cc = (CharacterClassInput) nodes[3];

		cc.setComplement(true);
		cc.addInput('-');

		return cc;
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
		CharacterClassInput cc = (CharacterClassInput) nodes[0];
		CharacterClassInput cc2 = (CharacterClassInput) nodes[1];

		cc.addInputs(cc2.getCharacters());

		return cc;
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
	 * onCharacterExpression
	 * 
	 * @param action
	 * @param nodes
	 * @return
	 */
	public Object onCharacterExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		CharacterClassInput cc = new CharacterClassInput();

		cc.addInput(((Lexeme) nodes[0]).getText().charAt(0));

		return cc;
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
		CharacterClassInput cc = new CharacterClassInput();
		char startChar = ((Lexeme) nodes[0]).getText().charAt(0);
		char endChar = ((Lexeme) nodes[2]).getText().charAt(0);

		cc.addInputs(startChar, endChar);

		return cc;
	}
}
