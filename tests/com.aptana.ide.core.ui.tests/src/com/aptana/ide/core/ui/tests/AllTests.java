package com.aptana.ide.core.ui.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.core.ui.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(CoreUIUtilsTest.class);
		suite.addTestSuite(SetUtilsTest.class);
		suite.addTest(com.aptana.ide.core.io.file.tests.AllTests.suite());
		suite.addTest(com.aptana.ide.core.io.tests.AllTests.suite());
		//$JUnit-END$
		return suite;
	}

}
