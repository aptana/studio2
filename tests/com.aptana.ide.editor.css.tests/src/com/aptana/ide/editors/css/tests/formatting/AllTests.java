package com.aptana.ide.editors.css.tests.formatting;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editors.css.tests.formatting");
		//$JUnit-BEGIN$
		suite.addTestSuite(FormattingTests.class);
		//$JUnit-END$
		return suite;
	}

}
