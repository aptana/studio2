package com.aptana.ide.editors.junit.partitions;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editors.junit.partitions");
		//$JUnit-BEGIN$
		suite.addTestSuite(PartitionsTest.class);
		//$JUnit-END$
		return suite;
	}

}
