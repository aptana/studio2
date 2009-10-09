package com.aptana.ide.server.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.server.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ServersTest.class);
		suite.addTestSuite(ConfigurationTests.class);
		//$JUnit-END$
		return suite;
	}

}
