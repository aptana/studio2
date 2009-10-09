package com.aptana.ide.syncing.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All Tests for the com.aptana.ide.syncing.tests plugin");
		//$JUnit-BEGIN$
		suite.addTestSuite(FtpTests.class);
		suite.addTestSuite(SyncingTests.class);
		suite.addTest(com.aptana.ide.io.ftp.tests.AllTests.suite());
		//$JUnit-END$
		return suite;
	}

}
