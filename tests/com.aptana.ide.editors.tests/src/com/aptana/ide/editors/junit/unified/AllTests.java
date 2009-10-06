package com.aptana.ide.editors.junit.unified;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editors.junit.unified");
		//$JUnit-BEGIN$
		suite.addTestSuite(UnifiedViewerTest.class);
		//$JUnit-END$
		return suite;
	}

}
