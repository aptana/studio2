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
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.Token;

/**
 * @author Kevin Lindsey
 */
public class TestLexemeList extends TestCase
{
	private static final Token TOKEN;
	private LexemeList _list;
	
	/**
	 * static constructor
	 */
	static
	{
		TOKEN = new Token(null);
		TOKEN.setTypeIndex(1);
	}
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this._list = new LexemeList();
	}
	
	/**
	 * testAddToEmptyList
	 */
	public void testAddToEmptyList()
	{
		Lexeme l = new Lexeme(TOKEN, "test", 0);
		
		this._list.add(l);
		
		assertEquals(1, this._list.size());
		assertSame(l, this._list.get(0));
	}
	
	/**
	 * testAddToListStart
	 */
	public void testAddToListStart()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		
		this._list.add(l2);
		this._list.add(l1);
		
		assertEquals(2, this._list.size());
		assertSame(l1, this._list.get(0));
	}
	
	/**
	 * testAddToListEnd
	 */
	public void testAddToListEnd()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		
		this._list.add(l1);
		this._list.add(l2);
		
		assertEquals(2, this._list.size());
		assertSame(l2, this._list.get(1));
	}
	
	/**
	 * testAddToListMiddle
	 */
	public void testAddToListMiddle()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		
		this._list.add(l1);
		this._list.add(l3);
		this._list.add(l2);
		
		assertEquals(3, this._list.size());
		assertSame(l2, this._list.get(1));
	}
	
	/**
	 * testAddNull
	 */
	public void testAddNull()
	{
		try
		{
			this._list.add(null);
			fail("Lists should not allow null lexemes to be added");
		}
		catch (IllegalArgumentException e)
		{
			assertNotNull(e.getMessage());
		}
	}
	
	/**
	 * testAddNegativeOffset
	 */
	public void testAddNegativeOffset()
	{
		Lexeme l = new Lexeme(TOKEN, "abc", -1);
		
		try
		{
			this._list.add(l);
			fail("Lists should not allow negative offset lexemes to be added");
		}
		catch (IllegalArgumentException e)
		{
			assertNotNull(e.getMessage());
		}
	}
	
	/**
	 * testNoAdd
	 */
	public void testNoAdd()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "abcdef", 0);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		
		assertEquals(2, this._list.size());
		assertSame(l1, this._list.get(0));
	}
	
	/**
	 * testAddOver
	 */
	public void testAddOver()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "abcdef", 0);
		
		this._list.add(l1);
		this._list.add(l2);
		assertEquals(2, this._list.size());
		
		this._list.remove(l1);
		this._list.add(l3);
		
		assertEquals(1, this._list.size());
		assertSame(l3, this._list.get(0));
	}
	
	/**
	 * testClear
	 */
	public void testClear()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		
		this._list.add(l1);
		this._list.add(l3);
		this._list.add(l2);
		
		assertEquals(3, this._list.size());
		
		this._list.clear();
		assertEquals(0, this._list.size());
	}
	
	/**
	 * testCloneStartingRange
	 */
	public void testCloneStartingRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.cloneRange(0, 2);
		assertEquals(3, copies.length);
		assertNotSame(l1, copies[0]);
		assertEquals(l1, copies[0]);
		assertNotSame(l2, copies[1]);
		assertEquals(l2, copies[1]);
		assertNotSame(l3, copies[2]);
		assertEquals(l3, copies[2]);
	}
	
	/**
	 * testCloneEndingRange
	 */
	public void testCloneEndingRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.cloneRange(1, 3);
		assertEquals(3, copies.length);
		assertNotSame(l2, copies[0]);
		assertEquals(l2, copies[0]);
		assertNotSame(l3, copies[1]);
		assertEquals(l3, copies[1]);
		assertNotSame(l4, copies[2]);
		assertEquals(l4, copies[2]);
	}
	
	/**
	 * testCloneMiddleRange
	 */
	public void testCloneMiddleRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.cloneRange(1, 2);
		assertEquals(2, copies.length);
		assertNotSame(l2, copies[0]);
		assertEquals(l2, copies[0]);
		assertNotSame(l3, copies[1]);
		assertEquals(l3, copies[1]);
	}
	
	/**
	 * testCloneFirstLexemeInRange
	 */
	public void testCloneFirstLexemeInRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.cloneRange(0, 0);
		assertEquals(1, copies.length);
		assertNotSame(l1, copies[0]);
		assertEquals(l1, copies[0]);
	}
	
	/**
	 * testCloneLastLexemeInRange
	 */
	public void testCloneLastLexemeInRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.cloneRange(3, 3);
		assertEquals(1, copies.length);
		assertNotSame(l4, copies[0]);
		assertEquals(l4, copies[0]);
	}
	
	/**
	 * testCloneMiddleLexemeInRange
	 */
	public void testCloneMiddleLexemeInRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.cloneRange(1, 1);
		assertEquals(1, copies.length);
		assertNotSame(l2, copies[0]);
		assertEquals(l2, copies[0]);
	}
	
	/**
	 * testCloneRangeNegativeStartingIndex
	 */
	public void testCloneRangeNegativeStartingIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.cloneRange(-1, 1);
		assertEquals(0, copies.length);
	}
	
	/**
	 * testCloneRangeStartingIndexTooLarge
	 */
	public void testCloneRangeStartingIndexTooLarge()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.cloneRange(4, 5);
		assertEquals(0, copies.length);
	}
	
	/**
	 * testCloneRangeNegativeEndingIndex
	 */
	public void testCloneRangeNegativeEndingIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.cloneRange(0, -1);
		assertEquals(0, copies.length);
	}
	
	/**
	 * testCloneRangeEndingIndexTooLarge
	 */
	public void testCloneRangeEndingIndexTooLarge()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.cloneRange(0, 4);
		assertEquals(0, copies.length);
	}
	
	/**
	 * testCloneRangeOutOfOrder
	 */
	public void testCloneRangeOutOfOrder()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.cloneRange(3, 0);
		assertEquals(0, copies.length);
	}
	
	/**
	 * testCopyStartingRange
	 */
	public void testCopyStartingRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.copyRange(0, 2);
		assertEquals(3, copies.length);
		assertSame(l1, copies[0]);
		assertSame(l2, copies[1]);
		assertSame(l3, copies[2]);
	}
	
	/**
	 * testCopyEndingRange
	 */
	public void testCopyEndingRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.copyRange(1, 3);
		assertEquals(3, copies.length);
		assertSame(l2, copies[0]);
		assertSame(l3, copies[1]);
		assertSame(l4, copies[2]);
	}
	
	/**
	 * testCopyMiddleRange
	 */
	public void testCopyMiddleRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.copyRange(1, 2);
		assertEquals(2, copies.length);
		assertSame(l2, copies[0]);
		assertSame(l3, copies[1]);
	}
	
	/**
	 * testCopyFirstLexemeInRange
	 */
	public void testCopyFirstLexemeInRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.copyRange(0, 0);
		assertEquals(1, copies.length);
		assertSame(l1, copies[0]);
	}
	
	/**
	 * testCopyLastLexemeInRange
	 */
	public void testCopyLastLexemeInRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.copyRange(3, 3);
		assertEquals(1, copies.length);
		assertSame(l4, copies[0]);
	}
	
	/**
	 * testCopyMiddleLexemeInRange
	 */
	public void testCopyMiddleLexemeInRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.copyRange(1, 1);
		assertEquals(1, copies.length);
		assertSame(l2, copies[0]);
	}
	
	/**
	 * testCopyRangeNegativeStartingIndex
	 */
	public void testCopyRangeNegativeStartingIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.copyRange(-1, 1);
		assertEquals(0, copies.length);
	}
	
	/**
	 * testCopyRangeStartingIndexTooLarge
	 */
	public void testCopyRangeStartingIndexTooLarge()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.copyRange(4, 5);
		assertEquals(0, copies.length);
	}
	
	/**
	 * testCopyRangeNegativeEndingIndex
	 */
	public void testCopyRangeNegativeEndingIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.copyRange(0, -1);
		assertEquals(0, copies.length);
	}
	
	/**
	 * testCopyRangeEndingIndexTooLarge
	 */
	public void testCopyRangeEndingIndexTooLarge()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.copyRange(0, 4);
		assertEquals(0, copies.length);
	}
	
	/**
	 * testCopyRangeOutOfOrder
	 */
	public void testCopyRangeOutOfOrder()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		Lexeme[] copies = this._list.copyRange(3, 0);
		assertEquals(0, copies.length);
	}
	
	/**
	 * testGet
	 */
	public void testGet()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
	}
	
	/**
	 * testGetNegativeIndex
	 */
	public void testGetNegativeIndex()
	{
		assertNull(this._list.get(-1));
	}
	
	/**
	 * testGetIndexTooLarge
	 */
	public void testGetIndexTooLarge()
	{
		assertNull(this._list.get(0));
	}
	
	/**
	 * testGetLexemeCeilingIndex
	 */
	public void testGetLexemeCeilingIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(0, this._list.getLexemeCeilingIndex(0));
		assertEquals(0, this._list.getLexemeCeilingIndex(1));
		assertEquals(0, this._list.getLexemeCeilingIndex(2));
		assertEquals(1, this._list.getLexemeCeilingIndex(3));
		assertEquals(1, this._list.getLexemeCeilingIndex(4));
		assertEquals(1, this._list.getLexemeCeilingIndex(5));
		assertEquals(1, this._list.getLexemeCeilingIndex(6));
		assertEquals(2, this._list.getLexemeCeilingIndex(7));
		assertEquals(2, this._list.getLexemeCeilingIndex(8));
		assertEquals(2, this._list.getLexemeCeilingIndex(9));
		assertEquals(2, this._list.getLexemeCeilingIndex(10));
		assertEquals(3, this._list.getLexemeCeilingIndex(11));
		assertEquals(3, this._list.getLexemeCeilingIndex(12));
		assertEquals(3, this._list.getLexemeCeilingIndex(13));
		assertEquals(3, this._list.getLexemeCeilingIndex(14));
		assertEquals(-1, this._list.getLexemeCeilingIndex(15));
	}
	
	/**
	 * testGetLexemeFloorIndex
	 */
	public void testGetLexemeFloorIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 1);
		Lexeme l2 = new Lexeme(TOKEN, "def", 5);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 9);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 13);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(-1, this._list.getLexemeFloorIndex(0));
		assertEquals(0, this._list.getLexemeFloorIndex(1));
		assertEquals(0, this._list.getLexemeFloorIndex(2));
		assertEquals(0, this._list.getLexemeFloorIndex(3));
		assertEquals(0, this._list.getLexemeFloorIndex(4));
		assertEquals(1, this._list.getLexemeFloorIndex(5));
		assertEquals(1, this._list.getLexemeFloorIndex(6));
		assertEquals(1, this._list.getLexemeFloorIndex(7));
		assertEquals(1, this._list.getLexemeFloorIndex(8));
		assertEquals(2, this._list.getLexemeFloorIndex(9));
		assertEquals(2, this._list.getLexemeFloorIndex(10));
		assertEquals(2, this._list.getLexemeFloorIndex(11));
		assertEquals(2, this._list.getLexemeFloorIndex(12));
		assertEquals(3, this._list.getLexemeFloorIndex(13));
		assertEquals(3, this._list.getLexemeFloorIndex(14));
		assertEquals(3, this._list.getLexemeFloorIndex(15));
		assertEquals(3, this._list.getLexemeFloorIndex(16));
	}
	
	/**
	 * testRemoveStartingLexeme
	 */
	public void testRemoveStartingLexeme()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(l1);
		
		assertEquals(3, this._list.size());
		assertSame(l2, this._list.get(0));
		assertSame(l3, this._list.get(1));
		assertSame(l4, this._list.get(2));
	}
	
	/**
	 * testRemoveEndingLexeme
	 */
	public void testRemoveEndingLexeme()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(l4);
		
		assertEquals(3, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
	}
	
	/**
	 * testRemoveMiddleLexeme
	 */
	public void testRemoveMiddleLexeme()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(l2);
		
		assertEquals(3, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l3, this._list.get(1));
		assertSame(l4, this._list.get(2));
	}
	
	/**
	 * testRemoveNullLexeme
	 */
	public void testRemoveNullLexeme()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(null);
		
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testRemoveNonExistingLexeme
	 */
	public void testRemoveNonExistingLexeme()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(new Lexeme(TOKEN, "abc", 0));
		
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testRemoveFirstIndex
	 */
	public void testRemoveFirstIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(0);
		assertEquals(3, this._list.size());
		assertSame(l2, this._list.get(0));
		assertSame(l3, this._list.get(1));
		assertSame(l4, this._list.get(2));
	}
	
	/**
	 * testRemoveLastIndex
	 */
	public void testRemoveLastIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(3);
		assertEquals(3, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
	}
	
	/**
	 * testRemoveMiddleIndex
	 */
	public void testRemoveMiddleIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(1);
		assertEquals(3, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l3, this._list.get(1));
		assertSame(l4, this._list.get(2));
	}
	
	/**
	 * testRemoveNegativeIndex
	 */
	public void testRemoveNegativeIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(-1);
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testRemoveTooLargeIndex
	 */
	public void testRemoveTooLargeIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(4);
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testRemoveStartingIndexRange
	 */
	public void testRemoveStartingIndexRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(0, 2);
		
		assertEquals(1, this._list.size());
		assertSame(l4, this._list.get(0));
	}
	
	/**
	 * testRemoveEndingIndexRange
	 */
	public void testRemoveEndingIndexRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(1, 3);
		
		assertEquals(1, this._list.size());
		assertSame(l1, this._list.get(0));
	}
	
	/**
	 * testRemoveMiddleIndexRange
	 */
	public void testRemoveMiddleIndexRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(1, 2);
		
		assertEquals(2, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l4, this._list.get(1));
	}
	
	/**
	 * testRemoveSingleStartingIndexRange
	 */
	public void testRemoveSingleStartingIndexRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(0, 0);
		
		assertEquals(3, this._list.size());
		assertSame(l2, this._list.get(0));
		assertSame(l3, this._list.get(1));
		assertSame(l4, this._list.get(2));
	}
	
	/**
	 * testRemoveSingleEndingIndexRange
	 */
	public void testRemoveSingleEndingIndexRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(3, 3);
		
		assertEquals(3, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
	}
	
	/**
	 * testRemoveSingleMiddleIndexRange
	 */
	public void testRemoveSingleMiddleIndexRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(1, 1);
		
		assertEquals(3, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l3, this._list.get(1));
		assertSame(l4, this._list.get(2));
	}
	
	/**
	 * testRemoveNegativeStartingIndexRange
	 */
	public void testRemoveNegativeStartingIndexRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(-1, 3);
		
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testRemoveTooLargeStartingIndexRange
	 */
	public void testRemoveTooLargeStartingIndexRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(4, 4);
		
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testRemoveNegativeEndingIndexRange
	 */
	public void testRemoveNegativeEndingIndexRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(0, -3);
		
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testRemoveTooLargeEndingIndexRange
	 */
	public void testRemoveTooLargeEndingIndexRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(0, 4);
		
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testOutOfOrderIndexRange
	 */
	public void testOutOfOrderIndexRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(3, 0);
		
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testRemoveStartingLexemeRange
	 */
	public void testRemoveStartingLexemeRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(l1, l3);
		
		assertEquals(1, this._list.size());
		assertSame(l4, this._list.get(0));
	}
	
	/**
	 * testRemoveEndingLexemeRange
	 */
	public void testRemoveEndingLexemeRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(l2, l4);
		
		assertEquals(1, this._list.size());
		assertSame(l1, this._list.get(0));
	}
	
	/**
	 * testRemoveMiddleLexemeRange
	 */
	public void testRemoveMiddleLexemeRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(l2, l3);
		
		assertEquals(2, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l4, this._list.get(1));
	}
	
	/**
	 * testRemoveSingleStartingLexemeRange
	 */
	public void testRemoveSingleStartingLexemeRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(l1, l1);
		
		assertEquals(3, this._list.size());
		assertSame(l2, this._list.get(0));
		assertSame(l3, this._list.get(1));
		assertSame(l4, this._list.get(2));
	}
	
	/**
	 * testRemoveSingleEndingLexemeRange
	 */
	public void testRemoveSingleEndingLexemeRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(l4, l4);
		
		assertEquals(3, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
	}
	
	/**
	 * testRemoveSingleMiddleLexemeRange
	 */
	public void testRemoveSingleMiddleLexemeRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(l2, l2);
		
		assertEquals(3, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l3, this._list.get(1));
		assertSame(l4, this._list.get(2));
	}
	
	/**
	 * testRemoveNullStartingLexemeRange
	 */
	public void testRemoveNullStartingLexemeRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(null, l4);
		
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testRemoveNullEndingLexemeRange
	 */
	public void testRemoveNullEndingLexemeRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(l1, null);
		
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testRemoveNonExistingStartingLexemeRange
	 */
	public void testRemoveNonExistingStartingLexemeRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(new Lexeme(TOKEN, "abc", 0), l4);
		
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testRemoveNonExistingEndingLexemeRange
	 */
	public void testRemoveNonExistingEndingLexemeRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(l1, new Lexeme(TOKEN, "jkl", 12));
		
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testRemoveOutOfOrderLexemeRange
	 */
	public void testRemoveOutOfOrderLexemeRange()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		Lexeme l4 = new Lexeme(TOKEN, "jkl", 12);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		this._list.add(l4);
		
		assertEquals(4, this._list.size());
		
		this._list.remove(l4, l1);
		
		assertEquals(4, this._list.size());
		assertSame(l1, this._list.get(0));
		assertSame(l2, this._list.get(1));
		assertSame(l3, this._list.get(2));
		assertSame(l4, this._list.get(3));
	}
	
	/**
	 * testShiftAllLexemes
	 */
	public void testShiftAllLexemes()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		
		this._list.shiftLexemeOffsets(0, 2);
		
		assertEquals(2, l1.offset);
		assertEquals(6, l2.offset);
		assertEquals(10, l3.offset);
	}
	
	/**
	 * testShiftMiddleLexemes
	 */
	public void testShiftMiddleLexemes()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		
		this._list.shiftLexemeOffsets(1, 2);
		
		assertEquals(0, l1.offset);
		assertEquals(6, l2.offset);
		assertEquals(10, l3.offset);
	}
	
	/**
	 * testShiftLastLexeme
	 */
	public void testShiftLastLexeme()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		
		this._list.shiftLexemeOffsets(2, 2);
		
		assertEquals(0, l1.offset);
		assertEquals(4, l2.offset);
		assertEquals(10, l3.offset);
	}
	
	/**
	 * testShiftNegativeIndex
	 */
	public void testShiftNegativeIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		
		this._list.shiftLexemeOffsets(-1, 2);
		
		assertEquals(0, l1.offset);
		assertEquals(4, l2.offset);
		assertEquals(8, l3.offset);
	}
	
	/**
	 * testShiftTooLargeIndex
	 */
	public void testShiftTooLargeIndex()
	{
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		
		this._list.add(l1);
		this._list.add(l2);
		this._list.add(l3);
		
		this._list.shiftLexemeOffsets(3, 2);
		
		assertEquals(0, l1.offset);
		assertEquals(4, l2.offset);
		assertEquals(8, l3.offset);
	}
	
	/**
	 * testSize
	 */
	public void testSize()
	{
		assertEquals(0, this._list.size());
		
		Lexeme l1 = new Lexeme(TOKEN, "abc", 0);
		this._list.add(l1);
		assertEquals(1, this._list.size());
		
		Lexeme l2 = new Lexeme(TOKEN, "def", 4);
		this._list.add(l2);
		assertEquals(2, this._list.size());
		
		Lexeme l3 = new Lexeme(TOKEN, "ghi", 8);
		this._list.add(l3);
		assertEquals(3, this._list.size());
		
		this._list.remove(l1);
		assertEquals(2, this._list.size());
		
		this._list.remove(l2);
		assertEquals(1, this._list.size());
		
		this._list.remove(l3);
		assertEquals(0, this._list.size());
	}
}
