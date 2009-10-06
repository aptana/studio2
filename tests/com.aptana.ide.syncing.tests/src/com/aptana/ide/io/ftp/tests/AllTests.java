package com.aptana.ide.io.ftp.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.io.ftp.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(FtpVirtualFileManagerTest.class);
		//$JUnit-END$
		return suite;
	}

}
