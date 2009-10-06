package com.aptana.ide.regex.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.regex.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestStatements.class);
		//$JUnit-END$
		return suite;
	}

}
