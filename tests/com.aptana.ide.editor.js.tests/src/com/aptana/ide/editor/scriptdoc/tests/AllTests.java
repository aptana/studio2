
package com.aptana.ide.editor.scriptdoc.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.scriptdoc.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestLiteralTokens.class);
		suite.addTestSuite(TestIdentifierTokens.class);
		suite.addTestSuite(TestDocumentationXML.class);
		suite.addTestSuite(TestWhitespaceTokens.class);
		suite.addTestSuite(TestDelimiterTokens.class);
		suite.addTestSuite(TestPunctuatorTokens.class);
		suite.addTestSuite(TestKeywordTokens.class);
		//$JUnit-END$
		return suite;
	}

}
