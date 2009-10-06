package com.aptana.ide.intro.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All Tests for the com.aptana.ide.intro.tests plugin");
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.ide.intro.tests.preferences.AllTests.suite());
		// $JUnit-END$
		return suite;
	}

}
