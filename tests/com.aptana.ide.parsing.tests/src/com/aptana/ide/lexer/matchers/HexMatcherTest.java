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
package com.aptana.ide.lexer.matchers;

import junit.framework.TestCase;

import com.aptana.ide.lexer.matcher.HexMatcher;

/**
 * @author Kevin Lindsey
 */
public class HexMatcherTest extends TestCase
{
	private HexMatcher _matcher;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this._matcher = new HexMatcher();
	}
	
	/**
	 * testNothing
	 */
	public void testNothing()
	{
		char[] source = "".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(-1, result);
	}

	/**
	 * testZero
	 */
	public void testZero()
	{
		char[] source = "0123456789".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testOne
	 */
	public void testOne()
	{
		char[] source = "1234567890".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testTwo
	 */
	public void testTwo()
	{
		char[] source = "2345678901".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testThree
	 */
	public void testThree()
	{
		char[] source = "3456789012".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testFour
	 */
	public void testFour()
	{
		char[] source = "4567890123".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testFive
	 */
	public void testFive()
	{
		char[] source = "5678901234".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testSix
	 */
	public void testSix()
	{
		char[] source = "6789012345".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testSeven
	 */
	public void testSeven()
	{
		char[] source = "7890123456".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testEight
	 */
	public void testEight()
	{
		char[] source = "8901234567".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testNine
	 */
	public void testNine()
	{
		char[] source = "9012345678".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testLowerA
	 */
	public void testLowerA()
	{
		char[] source = "abcdef".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testLowerB
	 */
	public void testLowerB()
	{
		char[] source = "bcdefa".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testLowerC
	 */
	public void testLowerC()
	{
		char[] source = "cdefab".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testLowerD
	 */
	public void testLowerD()
	{
		char[] source = "defabc".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testLowerE
	 */
	public void testLowerE()
	{
		char[] source = "efabcd".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testLowerF
	 */
	public void testLowerF()
	{
		char[] source = "fabcde".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testUpperA
	 */
	public void testUpperA()
	{
		char[] source = "ABCDEF".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testUpperB
	 */
	public void testUpperB()
	{
		char[] source = "BCDEFA".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testUpperC
	 */
	public void testUpperC()
	{
		char[] source = "CDEFAB".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testUpperD
	 */
	public void testUpperD()
	{
		char[] source = "DEFABC".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testUpperE
	 */
	public void testUpperE()
	{
		char[] source = "EFABCD".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testUpperF
	 */
	public void testUpperF()
	{
		char[] source = "FABCDE".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
}
