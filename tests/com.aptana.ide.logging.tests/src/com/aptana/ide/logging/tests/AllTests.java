package com.aptana.ide.logging.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All Tests for com.aptana.ide.logging plugin");
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.ide.logging.impl.AllTests.suite());
		// $JUnit-END$
		return suite;
	}

}
