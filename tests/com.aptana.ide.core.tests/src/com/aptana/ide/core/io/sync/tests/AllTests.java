package com.aptana.ide.core.io.sync.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.core.io.sync.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(VirtualFileManagerSyncPairTest.class);
		//$JUnit-END$
		return suite;
	}

}
