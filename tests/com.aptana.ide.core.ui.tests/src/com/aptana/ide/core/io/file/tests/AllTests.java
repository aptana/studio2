package com.aptana.ide.core.io.file.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.core.io.file.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(LocalFileManagerTest.class);
		suite.addTestSuite(ProjectFileManagerTest.class);
		//$JUnit-END$
		return suite;
	}

}
