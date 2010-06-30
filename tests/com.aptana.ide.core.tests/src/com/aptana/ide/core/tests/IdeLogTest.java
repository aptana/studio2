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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.IdeLog;

/**
 * IdeLogTest is a unit test class for class IdeLog.
 * 
 * @see com.aptana.ide.core.IdeLog
 */
public class IdeLogTest extends PackageTestCase
{

	/**
	 * Constructs a test case for the test specified by the name argument.
	 * 
	 * @param name
	 *            the name of the test case
	 */
	public IdeLogTest(String name)
	{
		super(name);
		/*
		 * This constructor should not be modified. Any initialization code should be placed in the setUp() method
		 * instead.
		 */
	}

	/**
	 * Test for method: static log(org.eclipse.core.runtime.Plugin,int,java.lang.String,java.lang.Throwable)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see IdeLog#log(org.eclipse.core.runtime.Plugin,int,java.lang.String,java.lang.Throwable)
	 */
	public void testLog1() throws Throwable
	{
		Throwable t2 = new Throwable((String) null, (Throwable) null);
		Throwable t3 = new Throwable("", t2); //$NON-NLS-1$
		Throwable t4 = new Throwable("", t3); //$NON-NLS-1$
		IdeLog.log(AptanaCorePlugin.getDefault(), IStatus.WARNING, "", t4); //$NON-NLS-1$
	}

	/**
	 * Test for method: static logError(org.eclipse.core.runtime.Plugin,java.lang.String)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see IdeLog#logError(org.eclipse.core.runtime.Plugin,java.lang.String)
	 */
	public void testLogError1() throws Throwable
	{
		IdeLog.logError(AptanaCorePlugin.getDefault(), (String) null);
	}

	/**
	 * Test for method: static logError(org.eclipse.core.runtime.Plugin,java.lang.String)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see IdeLog#logError(org.eclipse.core.runtime.Plugin,java.lang.String)
	 */
	public void testLogError2() throws Throwable
	{
		IdeLog.logError(AptanaCorePlugin.getDefault(), (String) null);
	}

	/**
	 * Test for method: static logError(org.eclipse.core.runtime.Plugin,java.lang.Throwable)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 */
	public void testLogError4() throws Throwable
	{
		IdeLog.logError(AptanaCorePlugin.getDefault(), "Error during test", (Throwable) null); //$NON-NLS-1$
	}

	/**
	 * Test for method: static logError(org.eclipse.core.runtime.Plugin,java.lang.Throwable)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 */
	public void testLogError5() throws Throwable
	{
		Throwable t1 = new Throwable((String) null, (Throwable) null);
		IdeLog.logError(AptanaCorePlugin.getDefault(), "Error during test", (t1)); //$NON-NLS-1$
	}

	/**
	 * Test for method: static logError(org.eclipse.core.runtime.Plugin,java.lang.Throwable)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 */
	public void testLogError6() throws Throwable
	{
		Throwable t1 = new Throwable(""); //$NON-NLS-1$
		IdeLog.logError(AptanaCorePlugin.getDefault(), "Error during test", (t1)); //$NON-NLS-1$
	}

	/**
	 * Test for method: static logError(org.eclipse.core.runtime.Plugin,java.lang.String,java.lang.Throwable)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see IdeLog#logError(org.eclipse.core.runtime.Plugin,java.lang.String,java.lang.Throwable)
	 */
	public void testLogError7() throws Throwable
	{
		Throwable t1 = new Throwable((String) null, (Throwable) null);
		IdeLog.logError(AptanaCorePlugin.getDefault(), (String) null, t1);
	}

	/**
	 * Test for method: static logError(org.eclipse.core.runtime.Plugin,java.lang.String,java.lang.Throwable)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see IdeLog#logError(org.eclipse.core.runtime.Plugin,java.lang.String,java.lang.Throwable)
	 */
	public void testLogError8() throws Throwable
	{
		IdeLog.logError(AptanaCorePlugin.getDefault(), (String) null, (Throwable) null);
	}

	/**
	 * Test for method: static logInfo(org.eclipse.core.runtime.Plugin,java.lang.String)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see IdeLog#logInfo(org.eclipse.core.runtime.Plugin,java.lang.String)
	 */
	public void testLogInfo1() throws Throwable
	{
		IdeLog.logInfo(AptanaCorePlugin.getDefault(), (String) null);
	}

	/**
	 * Test for method: static logInfo(org.eclipse.core.runtime.Plugin,java.lang.String)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see IdeLog#logInfo(org.eclipse.core.runtime.Plugin,java.lang.String)
	 */
	public void testLogInfo2() throws Throwable
	{
		IdeLog.logInfo(AptanaCorePlugin.getDefault(), ""); //$NON-NLS-1$
	}

	/**
	 * Test for method: static logInfo(org.eclipse.core.runtime.Plugin,java.lang.String,java.lang.Throwable)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see IdeLog#logInfo(org.eclipse.core.runtime.Plugin,java.lang.String,java.lang.Throwable)
	 */
	public void testLogInfo3() throws Throwable
	{
		Throwable t1 = new Throwable((String) null, (Throwable) null);
		IdeLog.logInfo(AptanaCorePlugin.getDefault(), (String) null, t1);
	}

	/**
	 * Test for method: static logInfo(org.eclipse.core.runtime.Plugin,java.lang.String,java.lang.Throwable)
	 * 
	 * @throws Throwable
	 *             Tests may throw any Throwable
	 * @see IdeLog#logInfo(org.eclipse.core.runtime.Plugin,java.lang.String,java.lang.Throwable)
	 */
	public void testLogInfo4() throws Throwable
	{
		Throwable t2 = new Throwable((String) null, (Throwable) null);
		Throwable t3 = new Throwable("", t2); //$NON-NLS-1$
		Throwable t4 = new Throwable("", t3); //$NON-NLS-1$
		IdeLog.logInfo(AptanaCorePlugin.getDefault(), "", t4); //$NON-NLS-1$
	}

	/**
	 * Used to set up the test. This method is called by JUnit before each of the tests are executed.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws Exception
	{
		super.setUp();
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
		/*
		 * Add any necessary cleanup code here (e.g., close a socket).
		 */
	}

	/**
	 * Utility main method. Runs the test cases defined in this test class. Usage: java
	 * com.aptana.ide.core.tests.IdeLogTest
	 * 
	 * @param args
	 *            command line arguments are not needed
	 */
	public static void main(String[] args)
	{
		// junit.textui.TestRunner will print the test results to stdout.
		// junit.textui.TestRunner.run(suite());
	}

	/**
	 * Create a test suite for running the tests in this class. IndependentTestSuite will run each test case in a
	 * separate classloader.
	 * 
	 * @return a test suite to run all of the tests in this class
	 */
	/*
	 * public static junit.framework.Test suite() { return new jtest.IndependentTestSuite( // this class
	 * IdeLogTest.class, // fully qualified name of the tested class "com.aptana.ide.core.IdeLog", // timeout for each
	 * test in milliseconds 60000); }
	 */

	/**
	 * Get the class object of the class which will be tested.
	 * 
	 * @return the class which will be tested
	 */
	public Class<?> getTestedClass()
	{
		return IdeLog.class;
	}

}

// JTEST_CURRENT_ID=1441267170.
