package com.aptana.ide.editor.html.tests.contentassist;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.html.tests.contentassist");
		//$JUnit-BEGIN$
		suite.addTestSuite(HTMLContentAssistProcessorTest.class);
		suite.addTestSuite(HTMLUtilsTest.class);
		//$JUnit-END$
		return suite;
	}

}
