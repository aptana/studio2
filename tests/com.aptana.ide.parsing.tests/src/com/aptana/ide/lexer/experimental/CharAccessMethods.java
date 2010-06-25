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
package com.aptana.ide.lexer.experimental;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * @author Kevin Lindsey
 */
public final class CharAccessMethods
{
	private static final int REPEAT_COUNT = 1000;
	
	private CharAccessMethods()
	{
	}
	
	/**
	 * main
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		char[] chars = new char[1024*1024];
		String testString = new String(chars);
		
		iteratorTest(testString);
		stringTest(testString);
		arrayTest(testString.toCharArray());
	}
	
	private static void stringTest(String s)
	{
		long start = System.currentTimeMillis();
		int length = s.length();
		
		for (int i = 0; i < REPEAT_COUNT; i++)
		{
			for (int j = 0; j < length; j++)
			{
				char c = s.charAt(j);
				
				if (c != '\0')
				{
					c = ' ';
				}
			}
		}
		
		long diff = System.currentTimeMillis() - start;
		
		//CHECKSTYLE:OFF
		int count = length * REPEAT_COUNT;
		double cps = count / ((double) diff / 1000);
		System.out.println("elapsed: " + diff);
		System.out.println("stringTest: " + cps + " characters per second"); //$NON-NLS-1$
		//CHECKSTYLE:ON
	}
	
	private static void arrayTest(char[] cs)
	{
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < REPEAT_COUNT; i++)
		{
			for (int j = 0; j < cs.length; j++)
			{
				char c = cs[j];
				
				if (c != '\0')
				{
					c = ' ';
				}
			}
		}
		
		long diff = System.currentTimeMillis() - start;
		
		//CHECKSTYLE:OFF
		int count = cs.length * REPEAT_COUNT;
		double cps = count / ((double) diff / 1000);
		System.out.println("elapsed: " + diff);
		System.out.println("arrayTest: " + cps + " characters per second"); //$NON-NLS-1$
		//CHECKSTYLE:ON
	}
	
	private static void iteratorTest(String s)
	{
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < REPEAT_COUNT; i++)
		{
			CharacterIterator ci = new StringCharacterIterator(s);
			
			for (char c = ci.first(); c != CharacterIterator.DONE; c = ci.next())
			{
				if (c != '\0')
				{
					c = ' ';
				}
			}
		}
		
		long diff = System.currentTimeMillis() - start;
		
		//CHECKSTYLE:OFF
		int count = s.length() * REPEAT_COUNT;
		double cps = count / ((double) diff / 1000);
		System.out.println("elapsed: " + diff);
		System.out.println("iteratorTest: " + cps + " characters per second"); //$NON-NLS-1$
		//CHECKSTYLE:ON
	}
}
