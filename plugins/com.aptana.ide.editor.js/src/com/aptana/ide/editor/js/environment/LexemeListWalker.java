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
package com.aptana.ide.editor.js.environment;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.IParseState;

/**
 * An abstract class for traversing a list of lexemes and performing a specific
 * operation on each lexeme.
 */
public abstract class LexemeListWalker {
	
	/**
	 * The list of lexemes
	 */
	protected LexemeList lexemeList;
	/**
	 * The cached LexemeList size
	 */
	protected int llSize;
	
	/**
	 * The current index we are at
	 */
	protected int currentIndex;

	/**
	 * Walks the list of lexemes, calling "onLexeme" on each one
	 * @param parseState The current parse state
	 * @param startIndex The lexeme list index at which to begin walking
	 * @return The ending index (generally the last index of the list)
	 */
	public int walkList(IParseState parseState, int startIndex)
	{
		//this.parseState = parseState;
		this.lexemeList = parseState.getLexemeList();
		this.llSize = this.lexemeList.size();
		currentIndex = startIndex;
		
		try{
			while(currentIndex < this.llSize)
			{
				onLexeme(lexemeList.get(currentIndex), currentIndex);
				currentIndex++;
			}
		}
		catch(AbortException e)
		{
			return e.getReturnIndex();
		}
		catch(Exception e)
		{
			return currentIndex;
		}
		return currentIndex;
	}
	
	/**
	 * The method called on each lexeme
	 * @param lexeme The lexeme to process
	 * @param index the index of the lexeme in the lexeme list
	 * @throws AbortException An exception to throw if we wish to abort the walking operation
	 */
	protected abstract void onLexeme(Lexeme lexeme, int index) throws AbortException;
	
	/**
	 * Thrown if we wish to abort the walking operation
	 * @author Ingo Muschenetz
	 *
	 */
	public class AbortException extends Exception 
	{
		private static final long serialVersionUID = 7511627292474678273L;
		private int returnIndex;
		
		/**
		 * Creates a new instance of AbortException
		 * @param returnIndex The index to return to the caller
		 */
		public AbortException(int returnIndex)
		{
			
		}
		
		/**
		 * Returns the index back to the caller
		 * @return The ending index
		 */
		public int getReturnIndex(){
			return returnIndex;
		}
	}
}
