package com.aptana.ide.editors.junit;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All Tests for the com.aptana.ide.editors.junit plugin");
		//$JUnit-BEGIN$
		suite.addTest(com.aptana.ide.editor.junit.profiles.AllTests.suite());
		suite.addTest(com.aptana.ide.editors.junit.formatting.AllTests.suite());
		suite.addTest(com.aptana.ide.editors.junit.js.AllTests.suite());
		suite.addTest(com.aptana.ide.editors.junit.pairmatching.AllTests.suite());
		suite.addTest(com.aptana.ide.editors.junit.partitions.AllTests.suite());
		suite.addTest(com.aptana.ide.editors.junit.unified.contentassist.AllTests.suite());
		suite.addTest(com.aptana.ide.editors.junit.unified.AllTests.suite());
		suite.addTest(com.aptana.ide.editors.tests.views.profiles.AllTests.suite());
		suite.addTest(com.aptana.ide.editors.unified.errors.tests.AllTests.suite());
		//$JUnit-END$
		return suite;
	}

}
