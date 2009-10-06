package com.aptana.ide.parsing.experimental;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.parsing.experimental");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestLRParserBuilder.class);
		//$JUnit-END$
		return suite;
	}

}
