package com.aptana.ide.editor.css.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for all of com.aptana.ide.editor.css.tests plugin");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestPairFinder.class);
		suite.addTestSuite(TestLiteralTokens.class);
		suite.addTestSuite(TestPunctuatorTokens.class);
		suite.addTestSuite(TestIdentifierTokens.class);
		suite.addTestSuite(TestParseTime.class);
		suite.addTestSuite(TestWhitespaceTokens.class);
		suite.addTestSuite(TestKeywordTokens.class);
		suite.addTestSuite(TestStatements.class);
		suite.addTest(com.aptana.ide.editor.css.tests.contentassist.AllTests.suite());
		suite.addTest(com.aptana.ide.editors.css.tests.formatting.AllTests.suite());
		//$JUnit-END$
		return suite;
	}

}
