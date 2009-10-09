package com.aptana.ide.editors.unified.errors.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editors.unified.errors.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ErrorDescriptorTest.class);
		//$JUnit-END$
		return suite;
	}

}
