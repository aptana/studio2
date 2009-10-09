package com.aptana.ide.editor.css.tests.contentassist;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.css.tests.contentassist");
		//$JUnit-BEGIN$
		suite.addTestSuite(CSSUtilsTest.class);
		suite.addTestSuite(CSSContentAssistProcessorTest.class);
		//$JUnit-END$
		return suite;
	}

}
