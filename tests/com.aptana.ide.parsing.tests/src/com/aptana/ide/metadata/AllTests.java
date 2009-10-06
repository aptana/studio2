package com.aptana.ide.metadata;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.metadata");
		//$JUnit-BEGIN$
		suite.addTestSuite(ElementMetadataTest.class);
		suite.addTestSuite(MetadataRuntimeEnvironmentTest.class);
		suite.addTestSuite(MetadataItemTest.class);
		//$JUnit-END$
		return suite;
	}

}
