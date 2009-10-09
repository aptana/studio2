package com.aptana.ide.editor.junit.profiles;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.junit.profiles");
		//$JUnit-BEGIN$
//		suite.addTestSuite(ProfileManagerTest.class); TODO Uncomment when we're sure this test makes sense and should be working
		suite.addTestSuite(ProfileTest.class);
		//$JUnit-END$
		return suite;
	}

}
