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

import java.io.InputStream;
import com.aptana.ide.core.StreamUtils;

/**
 * StreamUtilsTest is a unit test class for class StreamUtils.
 * 
 * @see com.aptana.ide.core.StreamUtils
 * @author Parasoft Jtest 7.5
 */
public class StreamUtilsTest extends PackageTestCase
{

	/**
	 * Constructs a test case for the test specified by the name argument.
	 * 
	 * @param name
	 *            the name of the test case
	 * @author Parasoft Jtest 7.5
	 */
	public StreamUtilsTest(String name)
	{
		super(name);
		/*
		 * This constructor should not be modified. Any initialization code should be placed in the
		 * setUp() method instead.
		 */
	}

	/**
	 * Test for method: static readContent(java.io.InputStream,java.lang.String)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see StreamUtils#readContent(java.io.InputStream,java.lang.String)
	 * @author Parasoft Jtest 7.5
	 */
	public void testReadContent1() throws Throwable
	{
		// jtest_tested_method
		StreamUtils.readContent((InputStream) null, (String) null);
		// NullPointerException thrown
		// at java.io.Reader.<init>(Reader.java:61)
		// at java.io.InputStreamReader.<init>(InputStreamReader.java:55)
		// at com.aptana.ide.core.StreamUtils.readContent(StreamUtils.java:32)
		// jtest_unverified
	}

	/**
	 * Test for method: static readContent(java.io.InputStream,java.lang.String)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see StreamUtils#readContent(java.io.InputStream,java.lang.String)
	 * @author Parasoft Jtest 7.5
	 */
	public void testReadContent2() throws Throwable
	{
		// jtest_tested_method
		StreamUtils.readContent((InputStream) null, ""); //$NON-NLS-1$
		// NullPointerException thrown
		// at java.io.Reader.<init>(Reader.java:61)
		// at java.io.InputStreamReader.<init>(InputStreamReader.java:80)
		// at com.aptana.ide.core.StreamUtils.readContent(StreamUtils.java:28)
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
	 * com.aptana.ide.core.tests.StreamUtilsTest
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
	 * class StreamUtilsTest.class, // fully qualified name of the tested class
	 * "com.aptana.ide.core.StreamUtils", // timeout for each test in milliseconds 60000); }
	 */

	/**
	 * Get the class object of the class which will be tested.
	 * 
	 * @return the class which will be tested
	 * @author Parasoft Jtest 7.5
	 */
	public Class<?> getTestedClass()
	{
		return StreamUtils.class;
	}

}

// JTEST_CURRENT_ID=-1729582529.
