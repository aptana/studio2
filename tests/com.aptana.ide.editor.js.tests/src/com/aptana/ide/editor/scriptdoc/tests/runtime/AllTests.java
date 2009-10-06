package com.aptana.ide.editor.scriptdoc.tests.runtime;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.scriptdoc.tests.runtime");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestOpcodes.class);
//		suite.addTestSuite(TestCodeGeneration.class); Work-in-progress for Kevin Lindsey
		//$JUnit-END$
		return suite;
	}

}
