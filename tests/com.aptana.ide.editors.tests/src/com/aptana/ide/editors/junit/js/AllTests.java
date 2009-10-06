package com.aptana.ide.editors.junit.js;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editors.junit.js");
		//$JUnit-BEGIN$
		suite.addTestSuite(JSEdgeCaseTests.class);
//		suite.addTestSuite(JSTodoTests.class); TODO Uncomment when this isn't work in progress!
		//$JUnit-END$
		return suite;
	}

}
