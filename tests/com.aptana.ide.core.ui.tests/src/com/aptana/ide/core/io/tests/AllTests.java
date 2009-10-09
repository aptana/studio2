package com.aptana.ide.core.io.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.core.io.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(VirtualFileTest.class);
		//$JUnit-END$
		return suite;
	}

}
