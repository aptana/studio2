package com.aptana.ide.editor.js.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All tests for the com.aptana.ide.editor.js.tests plugin");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestPairFinder.class);
		suite.addTestSuite(TestStatements.class);
		suite.addTestSuite(TestLiteralTokens.class);
		suite.addTestSuite(TestIdentifierTokens.class);
		suite.addTestSuite(TestDelimiterGroups.class);
		suite.addTestSuite(TestLexTime.class);
		suite.addTestSuite(TestWhitespaceTokens.class);
		suite.addTestSuite(TestParseTime.class);
		suite.addTestSuite(JSLanguageEnvironmentTest.class);
		suite.addTestSuite(TestPunctuatorTokens.class);
		suite.addTestSuite(Documentation2Tests.class);
		suite.addTestSuite(TestComments.class);
		suite.addTestSuite(TestKeywordTokens.class);
		suite.addTest(com.aptana.ide.editor.js.tests.contentassist.AllTests.suite());
		suite.addTest(com.aptana.ide.editor.js.tests.environment.AllTests.suite());
		suite.addTest(com.aptana.ide.editor.js.tests.formatting.AllTests.suite());
		suite.addTest(com.aptana.ide.editor.js.tests.model.AllTests.suite());
		suite.addTest(com.aptana.ide.editor.js.tests.outline.AllTests.suite());
		suite.addTest(com.aptana.ide.editor.jscomment.tests.AllTests.suite());
		suite.addTest(com.aptana.ide.editor.scriptdoc.tests.runtime.AllTests.suite());
		suite.addTest(com.aptana.ide.editor.scriptdoc.tests.AllTests.suite());
		//$JUnit-END$
		return suite;
	}

}
