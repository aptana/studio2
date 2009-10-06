package com.aptana.ide.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.ide.core.xpath.XPathUtilsTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.core.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(URLEncoderTest.class);
		suite.addTestSuite(StringUtilsTest.class);
		suite.addTestSuite(StreamUtilsTest.class);
		suite.addTestSuite(PluginUtilsTest.class);
		suite.addTestSuite(IdeLogTest.class);
		suite.addTestSuite(KeyValuePairTest.class);
		suite.addTestSuite(FileUtilsTest.class);
		suite.addTest(com.aptana.ide.core.io.sync.tests.AllTests.suite());
		suite.addTestSuite(XPathUtilsTest.class);
		//$JUnit-END$
		return suite;
	}

}
