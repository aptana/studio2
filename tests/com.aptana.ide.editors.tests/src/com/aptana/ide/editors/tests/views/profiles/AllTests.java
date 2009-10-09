package com.aptana.ide.editors.tests.views.profiles;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editors.tests.views.profiles");
		//$JUnit-BEGIN$
		suite.addTestSuite(ProfilesViewHelperTest.class);
		//$JUnit-END$
		return suite;
	}

}
