package com.aptana.ide.editor.html.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All tests for the com.aptana.ide.editor.html.tests plugin");
		// $JUnit-BEGIN$
		suite.addTestSuite(TestDelimiterGroups.class);
		suite.addTestSuite(TestLiteralTokens.class);
		suite.addTestSuite(TestKeywordTokens.class);
		suite.addTestSuite(TestBugs.class);
		suite.addTestSuite(TestStatements.class);
		suite.addTestSuite(TestPairFinder.class);
		suite.addTestSuite(TestWhitespaceTokens.class);
		suite.addTestSuite(TestPunctuatorTokens.class);
		suite.addTest(com.aptana.ide.editor.html.tests.contentassist.AllTests.suite());
		suite.addTest(com.aptana.ide.editor.html.tests.formatting.AllTests.suite());
		// $JUnit-END$
		return suite;
	}

}
