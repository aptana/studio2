package com.aptana.ide.editor.js.tests.environment;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.js.tests.environment");
		//$JUnit-BEGIN$
		suite.addTestSuite(UserCodeTest.class);
		suite.addTestSuite(JSCoreTest.class);
		suite.addTestSuite(DOMTest.class);
		//$JUnit-END$
		return suite;
	}

}
