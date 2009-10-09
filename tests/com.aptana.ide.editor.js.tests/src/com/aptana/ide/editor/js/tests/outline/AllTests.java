package com.aptana.ide.editor.js.tests.outline;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.js.tests.outline");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestScopeStrings.class);
		suite.addTestSuite(TestSimpleItems.class);
		suite.addTestSuite(TestInheritanceItems.class);
		suite.addTestSuite(TestBlockItems.class);
		suite.addTestSuite(ReusabilityTests.class);
		//$JUnit-END$
		return suite;
	}

}
