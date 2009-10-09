package com.aptana.ide.editor.xml.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.xml.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestPunctuatorTokens.class);
		suite.addTestSuite(TestBugs.class);
		suite.addTestSuite(TestParseTime.class);
		suite.addTestSuite(TestPairFinder.class);
		suite.addTestSuite(TestLiteralTokens.class);
		suite.addTestSuite(TestKeywordTokens.class);
		suite.addTestSuite(TestLexTime.class);
		suite.addTestSuite(TestStatements.class);
		suite.addTestSuite(TestDTDTokens.class);
		suite.addTest(com.aptana.ide.editor.xml.tests.formatting.AllTests.suite());
		//$JUnit-END$
		return suite;
	}

}
