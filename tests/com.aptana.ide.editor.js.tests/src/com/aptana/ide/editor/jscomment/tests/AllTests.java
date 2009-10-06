package com.aptana.ide.editor.jscomment.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.jscomment.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestBugs.class);
		suite.addTestSuite(TestDelimiterTokens.class);
		suite.addTestSuite(TestLiteralTokens.class);
		suite.addTestSuite(TestWhitespaceTokens.class);
		//$JUnit-END$
		return suite;
	}

}
