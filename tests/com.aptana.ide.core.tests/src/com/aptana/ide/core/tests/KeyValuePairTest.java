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

import com.aptana.ide.core.KeyValuePair;

/**
 * KeyValuePairTest is a unit test class for class KeyValuePair.
 * 
 * @see com.aptana.ide.core.KeyValuePair
 * @author Parasoft Jtest 7.5
 */
public class KeyValuePairTest extends PackageTestCase
{

	/**
	 * Constructs a test case for the test specified by the name argument.
	 * 
	 * @param name
	 *            the name of the test case
	 * @author Parasoft Jtest 7.5
	 */
	public KeyValuePairTest(String name)
	{
		super(name);
		/*
		 * This constructor should not be modified. Any initialization code should be placed in the
		 * setUp() method instead.
		 */
	}

	/**
	 * Test for method: KeyValuePair(java.lang.Object,java.lang.Object)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see KeyValuePair#KeyValuePair(java.lang.Object,java.lang.Object)
	 * @author Parasoft Jtest 7.5
	 */
	public void testKeyValuePair1() throws Throwable
	{
		// jtest_tested_method
		KeyValuePair thisPlugin = new KeyValuePair((Object) null, (Object) null);
		assertEquals(null, thisPlugin.getKey()); // jtest_unverified
		assertEquals(null, thisPlugin.getValue()); // jtest_unverified
		// No exception thrown
		// jtest_unverified
	}

	/**
	 * Test for method: KeyValuePair(java.lang.Object,java.lang.Object)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see KeyValuePair#KeyValuePair(java.lang.Object,java.lang.Object)
	 * @author Parasoft Jtest 7.5
	 */
	public void testKeyValuePair2() throws Throwable
	{
		Object t1 = new Object();
		Object t0 = new Object();
		// jtest_tested_method
		KeyValuePair thisObject = new KeyValuePair(t0, t1);
		assertEquals(t0, thisObject.getKey()); // jtest_unverified
		assertEquals(t1, thisObject.getValue()); // jtest_unverified
		// No exception thrown
		// jtest_unverified
	}

	/**
	 * Test for method: equals(java.lang.Object)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see KeyValuePair#equals(java.lang.Object)
	 * @author Parasoft Jtest 7.5
	 */
	public void testEquals1() throws Throwable
	{
		KeyValuePair thisObject = new KeyValuePair((Object) null, (Object) null);
		// jtest_tested_method
		boolean retval = thisObject.equals(""); //$NON-NLS-1$
		assertEquals(false, retval); // jtest_unverified
		assertEquals(null, thisObject.getKey()); // jtest_unverified
		assertEquals(null, thisObject.getValue()); // jtest_unverified
		// No exception thrown
		// jtest_unverified
	}

	/**
	 * Test for method: equals(java.lang.Object)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see KeyValuePair#equals(java.lang.Object)
	 * @author Parasoft Jtest 7.5
	 */
	public void testEquals2() throws Throwable
	{
		KeyValuePair t2 = new KeyValuePair((Object) null, (Object) null);
		KeyValuePair thisObject = new KeyValuePair((Object) null, (Object) null);
		// jtest_tested_method
		boolean retval = thisObject.equals(t2);
		assertEquals(true, retval); // jtest_unverified
		assertEquals(null, thisObject.getKey()); // jtest_unverified
		assertEquals(null, thisObject.getValue()); // jtest_unverified
		// No exception thrown
		// jtest_unverified
	}

	/**
	 * Test for method: equals(java.lang.Object)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see KeyValuePair#equals(java.lang.Object)
	 * @author Parasoft Jtest 7.5
	 */
	public void testEquals3() throws Throwable
	{
		Object t1 = new Object();
		Object t0 = new Object();
		KeyValuePair thisObject = new KeyValuePair(t0, t1);
		// jtest_tested_method
		boolean retval = thisObject.equals(""); //$NON-NLS-1$
		assertEquals(false, retval); // jtest_unverified
		assertEquals(t0, thisObject.getKey()); // jtest_unverified
		assertEquals(t1, thisObject.getValue()); // jtest_unverified
		// No exception thrown
		// jtest_unverified
	}

	/**
	 * Used to set up the test. This method is called by JUnit before each of the tests are
	 * executed.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 * @author Parasoft Jtest 7.5
	 */
	public void setUp() throws Exception
	{
		super.setUp();
		/*
		 * Add any necessary initialization code here (e.g., open a socket). Call
		 * Repository.putTemporary() to provide initialized instances of objects to be used when
		 * testing.
		 */
		// jtest.Repository.putTemporary("name", object);
	}

	/**
	 * Used to clean up after the test. This method is called by JUnit after each of the tests have
	 * been completed.
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 * @author Parasoft Jtest 7.5
	 */
	public void tearDown() throws Exception
	{
		super.tearDown();
		/*
		 * Add any necessary cleanup code here (e.g., close a socket).
		 */
	}

	/**
	 * Utility main method. Runs the test cases defined in this test class. Usage: java
	 * com.aptana.ide.core.tests.KeyValuePairTest
	 * 
	 * @param args
	 *            command line arguments are not needed
	 * @author Parasoft Jtest 7.5
	 */
	public static void main(String[] args)
	{
		// junit.textui.TestRunner will print the test results to stdout.
		// junit.textui.TestRunner.run(suite());
	}

	/**
	 * Create a test suite for running the tests in this class. IndependentTestSuite will run each
	 * test case in a separate classloader.
	 * 
	 * @return a test suite to run all of the tests in this class
	 * @author Parasoft Jtest 7.5
	 */
	/*
	 * public static junit.framework.Test suite() { return new jtest.IndependentTestSuite( // this
	 * class KeyValuePairTest.class, // fully qualified name of the tested class
	 * "com.aptana.ide.core.KeyValuePair", // timeout for each test in milliseconds 60000); }
	 */

	/**
	 * Get the class object of the class which will be tested.
	 * 
	 * @return the class which will be tested
	 * @author Parasoft Jtest 7.5
	 */
	public Class<?> getTestedClass()
	{
		return KeyValuePair.class;
	}

}

// JTEST_CURRENT_ID=-808294479.
