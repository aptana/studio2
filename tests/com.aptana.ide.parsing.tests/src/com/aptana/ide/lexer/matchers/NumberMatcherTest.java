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

import com.aptana.ide.lexer.matcher.NumberMatcher;

/**
 * @author Kevin Lindsey
 */
public class NumberMatcherTest extends TestCase
{
	private NumberMatcher _matcher;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this._matcher = new NumberMatcher();
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
	 * testInteger
	 */
	public void testInteger()
	{
		char[] source = "1".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(1, result);
	}
	
	/**
	 * testNegativeInteger
	 */
	public void testNegativeInteger()
	{
		char[] source = "-1".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(2, result);
	}
	
	/**
	 * testPositiveInteger
	 */
	public void testPositiveInteger()
	{
		char[] source = "+1".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(2, result);
	}
	
	/**
	 * testLeadingZeroFraction
	 */
	public void testLeadingZeroFraction()
	{
		char[] source = "0.1".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(3, result);
	}
	
	/**
	 * testNegativeLeadingZeroFraction
	 */
	public void testNegativeLeadingZeroFraction()
	{
		char[] source = "-0.1".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(4, result);
	}
	
	/**
	 * testPositiveLeadingZeroFraction
	 */
	public void testPositiveLeadingZeroFraction()
	{
		char[] source = "+0.1".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(4, result);
	}
	
	/**
	 * testNoLeadingZeroFraction
	 */
	public void testNoLeadingZeroFraction()
	{
		char[] source = ".1".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(2, result);
	}
	
	/**
	 * testNoNegativeLeadingZeroFraction
	 */
	public void testNoNegativeLeadingZeroFraction()
	{
		char[] source = "-.1".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(3, result);
	}
	
	/**
	 * testNoPositiveLeadingZeroFraction
	 */
	public void testNoPositiveLeadingZeroFraction()
	{
		char[] source = "+.1".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(3, result);
	}
	
	/**
	 * testNegativeNotAllowed
	 */
	public void testNegativeNotAllowed()
	{
		char[] source = "-1".toCharArray();
		
		this._matcher.setMatchNegative(false);
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(-1, result);
	}
	
	/**
	 * testPositiveNotAllowed
	 */
	public void testPositiveNotAllowed()
	{
		char[] source = "+1".toCharArray();
		
		this._matcher.setMatchPositive(false);
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(-1, result);
	}
	
	/**
	 * testLowerSciNotation
	 */
	public void testLowerSciNotation()
	{
		char[] source = "1e10".toCharArray();
		
		this._matcher.setMatchSciNotation(true);
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(4, result);
	}
	
	/**
	 * testUpperSciNotation
	 */
	public void testUpperSciNotation()
	{
		char[] source = "1E10".toCharArray();
		
		this._matcher.setMatchSciNotation(true);
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(4, result);
	}
	
	/**
	 * testPositiveSciNotation
	 */
	public void testPositiveSciNotation()
	{
		char[] source = "1e+10".toCharArray();
		
		this._matcher.setMatchSciNotation(true);
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(5, result);
	}
	
	/**
	 * testNegativeSciNotation
	 */
	public void testNegativeSciNotation()
	{
		char[] source = "1e-10".toCharArray();
		
		this._matcher.setMatchSciNotation(true);
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(5, result);
	}
	
	/**
	 * testMinusSign
	 */
	public void testMinusSign()
	{
		char[] source = "-".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(-1, result);
	}
	
	/**
	 * testPlusSign
	 */
	public void testPlusSign()
	{
		char[] source = "-".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(-1, result);
	}
	
	/**
	 * testPeriod
	 */
	public void testPeriod()
	{
		char[] source = ".".toCharArray();
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(-1, result);
	}
	
	/**
	 * testLetterE
	 */
	public void testLetterE()
	{
		char[] source = "e".toCharArray();
		
		this._matcher.setMatchSciNotation(true);
		
		int result = this._matcher.match(source, 0, source.length);
		
		assertEquals(-1, result);
	}
}
