package com.aptana.ide.parsing.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.ide.lexer.matchers.AllMatcherTests;

public class CoreTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All Core Tests for the com.aptana.ide.parsing.tests plugin");
		// $JUnit-BEGIN$
		suite.addTestSuite(ParseNodeTests.class);
		suite.addTest(AllMatcherTests.suite());
		suite.addTest(com.aptana.ide.lexer.tests.AllTests.suite());
		suite.addTest(com.aptana.ide.metadata.AllTests.suite());
		suite.addTest(com.aptana.ide.parsing.bnf.tests.AllTests.suite());
		suite.addTest(com.aptana.ide.parsing.experimental.AllTests.suite());
		suite.addTest(com.aptana.ide.regex.tests.AllTests.suite());
		// $JUnit-END$
		return suite;
	}

}
