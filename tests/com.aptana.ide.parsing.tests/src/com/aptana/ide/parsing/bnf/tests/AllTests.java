package com.aptana.ide.parsing.bnf.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.parsing.bnf.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(BootstrapTest.class);
		//$JUnit-END$
		return suite;
	}

}
