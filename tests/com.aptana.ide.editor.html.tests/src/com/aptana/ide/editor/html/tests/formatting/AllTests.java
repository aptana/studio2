package com.aptana.ide.editor.html.tests.formatting;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.html.tests.formatting");
		//$JUnit-BEGIN$
		suite.addTestSuite(FormattingTests.class);
		suite.addTestSuite(HTMLAutoIndentStrategyTest.class);
		//$JUnit-END$
		return suite;
	}

}
