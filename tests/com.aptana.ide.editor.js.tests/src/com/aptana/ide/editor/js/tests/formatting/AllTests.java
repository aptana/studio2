package com.aptana.ide.editor.js.tests.formatting;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.js.tests.formatting");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestFormatting.class);
		//$JUnit-END$
		return suite;
	}

}
