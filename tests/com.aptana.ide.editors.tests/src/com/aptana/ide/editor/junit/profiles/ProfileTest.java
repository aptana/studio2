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
package com.aptana.ide.editor.junit.profiles;

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * ProfileTest
 * 
 * @author Ingo Muschenetz
 */
public class ProfileTest extends TestCase
{

	HashMap files = new HashMap();

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * getFooContents
	 * 
	 * @return String
	 */
	public static String getFooContents()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("/**\r\n"); //$NON-NLS-1$
		sb.append(" * Function is foo.\r\n"); //$NON-NLS-1$
		sb.append(" * @alias foo_alias\r\n"); //$NON-NLS-1$
		sb.append(" * @return {String} Returns a foo object.\r\n"); //$NON-NLS-1$
		sb.append("*/\r\n"); //$NON-NLS-1$
		sb.append("function foo(a, b) {} "); //$NON-NLS-1$
		return sb.toString();
	}

	/**
	 * getBarContents
	 * 
	 * @return String
	 */
	public static String getBarContents()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("/**\r\n"); //$NON-NLS-1$
		sb.append(" * Function is bar.\r\n"); //$NON-NLS-1$
		sb.append(" * @alias bar_alias\r\n"); //$NON-NLS-1$
		sb.append(" * @return {String} Returns a bar object.\r\n"); //$NON-NLS-1$
		sb.append("*/\r\n"); //$NON-NLS-1$
		sb.append("function bar(a, b, c) {} "); //$NON-NLS-1$
		return sb.toString();
	}

	/**
	 * getFoo1Contents
	 * 
	 * @return String
	 */
	public static String getFoo1Contents()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("/**\r\n"); //$NON-NLS-1$
		sb.append(" * Function is foo1.\r\n"); //$NON-NLS-1$
		sb.append(" * @return {Number} Returns a foo1 object.\r\n"); //$NON-NLS-1$
		sb.append("*/\r\n"); //$NON-NLS-1$
		sb.append("function foo1() {} "); //$NON-NLS-1$
		return sb.toString();
	}

	/**
	 * getBar1Contents
	 * 
	 * @return String
	 */
	public static String getBar1Contents()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("/**\r\n"); //$NON-NLS-1$
		sb.append(" * Function is bar1.\r\n"); //$NON-NLS-1$
		sb.append(" * @return {Number} Returns a bar1 object.\r\n"); //$NON-NLS-1$
		sb.append("*/\r\n"); //$NON-NLS-1$
		sb.append("function bar1() {} "); //$NON-NLS-1$
		return sb.toString();
	}

	/**
	 * Class under test for void Profile(String, String)
	 */
	public void testProfileStringString()
	{
	}

	/**
	 * Class under test for void Profile(String, String, boolean)
	 */
	public void testProfileStringStringboolean()
	{
	}

	/**
	 * testGetPath
	 */
	public void testGetPath()
	{
	}

	/**
	 * testAddFiles
	 */
	public void testAddFiles()
	{
	}

	/**
	 * testAddTransientFiles
	 */
	public void testAddTransientFiles()
	{
	}

	/**
	 * testRemoveFiles
	 */
	public void testRemoveFiles()
	{
	}

	/**
	 * testRemoveTransientFiles
	 */
	public void testRemoveTransientFiles()
	{
	}

	/**
	 * testGetFiles
	 */
	public void testGetFiles()
	{
	}

	/**
	 * testGetFilesAsStrings
	 */
	public void testGetFilesAsStrings()
	{
	}

	/**
	 * testMovePathsUp
	 */
	public void testMovePathsUp()
	{
	}

	/**
	 * testMovePathsDown
	 */
	public void testMovePathsDown()
	{
	}

	/**
	 * testIsEnabled
	 */
	public void testIsEnabled()
	{
	}

	/**
	 * testSetEnabled
	 */
	public void testSetEnabled()
	{
	}

	/**
	 * testClear
	 */
	public void testClear()
	{
	}

	/**
	 * testLoad
	 */
	public void testLoad()
	{
	}

	/**
	 * testSave
	 */
	public void testSave()
	{
	}

	/**
	 * testFireProfileChangeEvent
	 */
	public void testFireProfileChangeEvent()
	{
	}

	/**
	 * testAddProfileChangeListener
	 */
	public void testAddProfileChangeListener()
	{
	}

	/**
	 * testRemoveProfileChangeListener
	 */
	public void testRemoveProfileChangeListener()
	{
	}

	/**
	 * testGetProfileListKey
	 */
	public void testGetProfileListKey()
	{
	}

	/**
	 * testGetProfileName
	 */
	public void testGetProfileName()
	{
	}

	/**
	 * testSetDynamic
	 */
	public void testSetDynamic()
	{
	}

	/**
	 * testIsDynamic
	 */
	public void testIsDynamic()
	{
	}
}
