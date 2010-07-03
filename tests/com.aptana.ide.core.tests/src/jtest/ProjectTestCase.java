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
package jtest;

import junit.framework.TestCase;

/**
 * ProjectTestCase is an abstract unit test class for unit test classes in project 'com.aptana.ide.core.tests'.
 * 
 * @author Parasoft Jtest 7.5
 */
public abstract class ProjectTestCase extends TestCase
{

	/**
	 * Constructs a test case for the test specified by the name argument.
	 * 
	 * @param name
	 *            the name of the test case
	 */
	public ProjectTestCase(String name)
	{
		super(name);
		/*
		 * This constructor should not be modified. Any initialization code should be placed in the setUp() method
		 * instead.
		 */
	}

	/**
	 * Used to set up the test. This method is called by JUnit before each of the tests are executed.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws Exception
	{
		super.setUp();
		projectSetUp(this);
	}

	/**
	 * Used to set up any tests for this project. This method is called before each JUnit or other type of test is
	 * executed.
	 * 
	 * @param test
	 *            an instance object representing the current test
	 * @throws Exception
	 */
	public static void projectSetUp(Object test) throws Exception
	{
		/*
		 * Add any necessary initialization code here (e.g., open a socket). Call Repository.putTemporary() to provide
		 * initialized instances of objects to be used when testing.
		 */
		// jtest.Repository.putTemporary("name", object);
	}

	/**
	 * Used to clean up after the test. This method is called by JUnit after each of the tests have been completed.
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() throws Exception
	{
		super.tearDown();
		projectTearDown(this);
	}

	/**
	 * Used to clean up any tests for this project. This method is called after each JUnit or other type of test is
	 * executed.
	 * 
	 * @param test
	 *            an instance object representing the current test
	 * @throws Exception
	 */
	public static void projectTearDown(Object test) throws Exception
	{
		/*
		 * Add any necessary cleanup code here (e.g., close a socket).
		 */
	}

	/**
	 * Get the class object of the class which will be tested.
	 * 
	 * @return the class which will be tested
	 */
	public abstract Class<?> getTestedClass();
}
