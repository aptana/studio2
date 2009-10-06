/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.lexer.tests;

import junit.framework.TestCase;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.Token;

/**
 * @author Kevin Lindsey
 */
public class TestLexeme extends TestCase
{
	private static final Token TOKEN;

	/**
	 * static constructor
	 */
	static
	{
		TOKEN = new Token(null);
		TOKEN.setTypeIndex(1);
	}

	/**
	 * testClone
	 * 
	 * @throws CloneNotSupportedException
	 */
	public void testClone() throws CloneNotSupportedException
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = (Lexeme) l1.clone();
		
		assertNotSame(l1, l2);
		assertEquals(l1, l2);
	}
}
