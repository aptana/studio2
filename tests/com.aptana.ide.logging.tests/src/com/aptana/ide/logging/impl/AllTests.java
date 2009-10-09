package com.aptana.ide.logging.impl;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.logging.impl");
		// $JUnit-BEGIN$
		suite.addTestSuite(InLineMatcherTest.class);
		suite.addTestSuite(WorkspaceLogProviderTest.class);
		// $JUnit-END$
		return suite;
	}

}
