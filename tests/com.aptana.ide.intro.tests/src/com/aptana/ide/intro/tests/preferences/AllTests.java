package com.aptana.ide.intro.tests.preferences;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.intro.tests.preferences");
		//$JUnit-BEGIN$
		suite.addTestSuite(FeatureRegistryTest.class);
		suite.addTestSuite(ProFeatureDescriptorTest.class);
		suite.addTestSuite(FeatureDescriptorTest.class);
		//$JUnit-END$
		return suite;
	}

}
