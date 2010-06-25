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
package com.aptana.ide.core.tests;

import com.aptana.ide.core.StringUtils;

import junit.framework.TestCase;

/**
 * 
 * @author Ingo Muschenetz
 *
 */
public class StringUtilsTest extends TestCase
{

	/**
	 * Test method for 'com.aptana.ide.core.StringUtils.urlEncodeForSpaces(char[])'
	 */
	public void testUrlEncodeForSpaces()
	{
		String file = "c:\\Documents and Settings\\file{}[]`+.%j%s"; //$NON-NLS-1$
		String encoded = StringUtils.urlEncodeFilename(file.toCharArray());
		assertEquals(file, StringUtils.urlDecodeFilename(encoded.toCharArray()));
		
		String file2 = "c:\\Documents and Settings\\file% test.%%%%"; //$NON-NLS-1$
		encoded = StringUtils.urlEncodeFilename(file2.toCharArray());
		assertEquals(file2, StringUtils.urlDecodeFilename(encoded.toCharArray()));
	}

	/**
	 * Test method for 'com.aptana.ide.core.StringUtils.convertWildcardExpressionToRegex(String, boolean)'
	 */
	public void testConvertWildcardExpressionToRegex()
	{
		String regex = StringUtils.convertWildcardExpressionToRegex("*.js", false);  //$NON-NLS-1$
		assertEquals(".*\\.js", regex); //$NON-NLS-1$
		assertTrue("file.js".matches(regex)); //$NON-NLS-1$
		assertFalse("file.js.bak".matches(regex)); //$NON-NLS-1$
		assertFalse("file.JS".matches(regex)); //$NON-NLS-1$
		
		regex = StringUtils.convertWildcardExpressionToRegex("*.js", true); //$NON-NLS-1$
		assertEquals("(?i).*\\.js", regex); //$NON-NLS-1$
		assertTrue("file.js".matches(regex)); //$NON-NLS-1$
		assertTrue("file.JS".matches(regex)); //$NON-NLS-1$

		assertEquals("(?i).*\\.js", StringUtils.convertWildcardExpressionToRegex("*.js", true)); //$NON-NLS-1$ //$NON-NLS-2$

		regex = StringUtils.convertWildcardExpressionToRegex(".svn", false); //$NON-NLS-1$
		assertEquals("\\.svn", regex); //$NON-NLS-1$
		assertTrue(".svn".matches(regex)); //$NON-NLS-1$
		assertFalse("c:\\temp\\.svn".matches(regex)); //$NON-NLS-1$
	
		regex = StringUtils.convertWildcardExpressionToRegex("test*", false); //$NON-NLS-1$
		assertEquals("test.*", regex); //$NON-NLS-1$
		
		regex = StringUtils.convertWildcardExpressionToRegex("images", true); //$NON-NLS-1$
		assertEquals("(?i)images", regex); //$NON-NLS-1$
		assertTrue("images".matches(regex)); //$NON-NLS-1$
		assertFalse("c:\\temp\\images".matches(regex)); //$NON-NLS-1$
		
		String doNotConvert = "/.*images.*/"; //$NON-NLS-1$
		assertEquals(".*images.*", StringUtils.convertWildcardExpressionToRegex(doNotConvert, false)); //$NON-NLS-1$

	}
	
	/**
	 * Test method for 'com.aptana.ide.core.StringUtils.trimStart(String)'
	 */
	public void testTrimStart()
	{
		assertEquals(null, StringUtils.trimStart(null));
		assertEquals("", StringUtils.trimStart(""));
		assertEquals("a", StringUtils.trimStart("a"));
		assertEquals("t a b c", StringUtils.trimStart("\t\r\n\n\nt a b c"));
		assertEquals("t a b c\t\r\n\n\n", StringUtils.trimStart("t a b c\t\r\n\n\n"));
		assertEquals("t a b c\t\r\n\n\n", StringUtils.trimStart("\t\r\n\n\nt a b c\t\r\n\n\n"));
		assertEquals("&", StringUtils.trimStart(" &"));
		assertEquals("a ", StringUtils.trimStart(" a "));
		assertEquals("a ", StringUtils.trimStart("                 \t\r\n a "));
	}
	
	/**
	 * Test method for 'com.aptana.ide.core.StringUtils.trimEnd(String)'
	 */
	public void testTrimEnd()
	{
		String one = null;
		String two = "";
		String three = "a";
		String four = "\t\r\n\n\nt a b c";
		String five = "t a b c\t\r\n\n\n";		
		String six = "\t\r\n\n\nt a b c\t\r\n\n\n";
		assertEquals(one, StringUtils.trimEnd(one));
		assertEquals(two, StringUtils.trimEnd(two));
		assertEquals(three, StringUtils.trimEnd(three));
		assertEquals(four, StringUtils.trimEnd(four));
		assertEquals("t a b c", StringUtils.trimEnd(five));
		assertEquals(four, StringUtils.trimEnd(six));
	}


	/**
	 * Test method for 'com.aptana.ide.core.StringUtils.findStartWhitespace(String)'
	 */
	public void testFindStartWhitespace()
	{
		String one = null;
		String two = "";
		String three = "a";
		String four = "\t\r\n\n\nt a b c";
		String five = "t a b c\t\r\n\n\n";		
		String six = "\t\r\n\n\nt a b c\t\r\n\n\n";
		assertEquals(one, StringUtils.findStartWhitespace(one));
		assertEquals(two, StringUtils.findStartWhitespace(two));
		assertEquals("", StringUtils.findStartWhitespace(three));
		assertEquals("\t\r\n\n\n", StringUtils.findStartWhitespace(four));
		assertEquals("", StringUtils.findStartWhitespace(five));
		assertEquals("\t\r\n\n\n", StringUtils.findStartWhitespace(six));
	}

	/**
	 * Test method for 'com.aptana.ide.core.StringUtils.findEndWhitespace(String)'
	 */
	public void testFindEndWhitespace()
	{
		String one = null;
		String two = "";
		String three = "a";
		String four = "\t\r\n\n\nt a b c";
		String five = "t a b c\t\r\n\n\n";		
		String six = "\t\r\n\n\nt a b c\t\r\n\n\n";
		assertEquals(one, StringUtils.findEndWhitespace(one));
		assertEquals(two, StringUtils.findEndWhitespace(two));
		assertEquals("", StringUtils.findEndWhitespace(three));
		assertEquals("", StringUtils.findEndWhitespace(four));
		assertEquals("\t\r\n\n\n", StringUtils.findEndWhitespace(five));
		assertEquals("\t\r\n\n\n", StringUtils.findEndWhitespace(six));
	}
	
	/**
	 * Test method for 'com.aptana.ide.core.StringUtils.getNumberOfNewlines(String)'
	 */
	public void testGetNumberOfNewlines()
	{
		String one = null;
		String two = "";
		String three = "a";
		String four = "\t\r\n\n\nt a b c";
		String five = "\t\r\n\n\n";		

		assertEquals(0, StringUtils.getNumberOfNewlines(one));
		assertEquals(0, StringUtils.getNumberOfNewlines(two));
		assertEquals(0, StringUtils.getNumberOfNewlines(three));
		assertEquals(3, StringUtils.getNumberOfNewlines(four));
		assertEquals(3, StringUtils.getNumberOfNewlines(five));
	}
}