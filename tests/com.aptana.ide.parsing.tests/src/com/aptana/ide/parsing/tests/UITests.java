package com.aptana.ide.parsing.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class UITests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All UI Tests for the com.aptana.ide.parsing.tests plugin");
		// $JUnit-BEGIN$
		suite.addTestSuite(ParseStateTests.class);
		// $JUnit-END$
		return suite;
	}

}
