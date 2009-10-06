package com.aptana.ide.editor.js.tests.model;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.js.tests.model");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestNumberInstance.class);
		suite.addTestSuite(TestNumberConstructor.class);
		suite.addTestSuite(TestFunctionConstructor.class);
		suite.addTestSuite(TestNumberFromInvocation.class);
		suite.addTestSuite(TestRegExpConstructor.class);
		suite.addTestSuite(TestErrorConstructor.class);
		suite.addTestSuite(TestStringInstance.class);
		suite.addTestSuite(TestArrayFromInvocation.class);
		suite.addTestSuite(TestBooleanFromInvocation.class);
		suite.addTestSuite(TestArrayConstructor.class);
		suite.addTestSuite(TestDateConstructor.class);
		suite.addTestSuite(TestFunctionFromInvocation.class);
		suite.addTestSuite(TestErrorFromInvocation.class);
		suite.addTestSuite(TestErrorInstance.class);
		suite.addTestSuite(TestDateFromInvocation.class);
		suite.addTestSuite(TestDateInstance.class);
		suite.addTestSuite(TestProperties.class);
		suite.addTestSuite(TestArrayInstance.class);
		suite.addTestSuite(TestMathInstance.class);
		suite.addTestSuite(TestRegExpFromInvocation.class);
		suite.addTestSuite(TestFunctionInstance.class);
		suite.addTestSuite(TestBooleanConstructor.class);
		suite.addTestSuite(TestObjectConstructor.class);
		suite.addTestSuite(TestObjectFromInvocation.class);
		suite.addTestSuite(TestBooleanInstance.class);
		suite.addTestSuite(TestStringFromInvocation.class);
		suite.addTestSuite(TestVariables.class);
		suite.addTestSuite(TestRegExpInstance.class);
		suite.addTestSuite(TestStringConstructor.class);
		suite.addTestSuite(TestObjectInstance.class);
		//$JUnit-END$
		return suite;
	}

}
