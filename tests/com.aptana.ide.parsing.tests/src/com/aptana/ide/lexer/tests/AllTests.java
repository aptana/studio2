package com.aptana.ide.lexer.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.lexer.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestLexemeList.class);
		suite.addTestSuite(TestLexeme.class);
		//$JUnit-END$
		return suite;
	}

}
