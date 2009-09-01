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
package com.aptana.ide.scripting.parsing;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.aptana.ide.lexer.Lexeme;

/**
 * @author Kevin Lindsey
 */
public class LexemeList extends ScriptableObject
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = -9165956975012618896L;
	private com.aptana.ide.lexer.LexemeList _lexemes;

	/*
	 * Properties
	 */

	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return "LexemeList"; //$NON-NLS-1$
	}

	/**
	 * Create a new instance of LexemeList
	 * 
	 * @param scope
	 * @param lexemeList
	 */
	public LexemeList(Scriptable scope, com.aptana.ide.lexer.LexemeList lexemeList)
	{
		this._lexemes = lexemeList;

		String[] names = new String[] { "getLexeme", "getCeilingLexeme", "getFloorLexeme", "getLexemeIndex" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this.defineFunctionProperties(names, LexemeList.class, READONLY);

		this.defineProperty("length", LexemeList.class, READONLY); //$NON-NLS-1$
	}

	/**
	 * @see org.mozilla.javascript.ScriptableObject#get(int, org.mozilla.javascript.Scriptable)
	 */
	public Object get(int index, Scriptable start)
	{
		if (this._lexemes != null && 0 <= index && index < this._lexemes.size())
		{
			return this._lexemes.get(index);
		}
		else
		{
			return super.get(index, start);
		}
	}

	/*
	 * JS properties and functions
	 */

	/**
	 * getCeilingLexeme
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 * @return Lexeme
	 */
	public static Object getCeilingLexeme(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		LexemeList instance = (LexemeList) thisObj;
		Object result = null;

		if (instance._lexemes != null)
		{
			int offset = (int) Math.round(Context.toNumber(args[0]));
			int index = instance._lexemes.getLexemeCeilingIndex(offset);

			if (index != -1)
			{
				result = instance._lexemes.get(index);
			}
		}

		return result;
	}

	/**
	 * getFloorLexeme
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 * @return Lexeme
	 */
	public static Object getFloorLexeme(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		LexemeList instance = (LexemeList) thisObj;
		Object result = null;

		if (instance._lexemes != null)
		{
			int offset = (int) Math.round(Context.toNumber(args[0]));
			int index = instance._lexemes.getLexemeFloorIndex(offset);

			if (index != -1)
			{
				result = instance._lexemes.get(index);
			}
		}

		return result;
	}

	/**
	 * getLength
	 * 
	 * @return int
	 */
	public int getLength()
	{
		int result = 0;

		if (this._lexemes != null)
		{
			result = this._lexemes.size();
		}

		return result;
	}

	/**
	 * getLexeme
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 * @return Lexeme
	 */
	public static Object getLexeme(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		LexemeList instance = (LexemeList) thisObj;
		Object result = null;

		if (instance._lexemes != null)
		{
			int offset = (int) Math.round(Context.toNumber(args[0]));

			result = instance._lexemes.getLexemeFromOffset(offset);
		}

		return result;
	}

	/**
	 * getLexemeIndex
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 * @return int
	 */
	public static int getLexemeIndex(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		LexemeList instance = (LexemeList) thisObj;
		int result = -1;

		if (instance._lexemes != null)
		{
			Lexeme lexeme = (Lexeme) args[0];

			result = instance._lexemes.getLexemeIndex(lexeme);
		}

		return result;
	}
}
