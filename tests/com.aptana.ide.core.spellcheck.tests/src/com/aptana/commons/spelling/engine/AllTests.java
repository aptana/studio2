package com.aptana.commons.spelling.engine;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.commons.spelling.engine");
		// $JUnit-BEGIN$
		suite.addTestSuite(NoCompletionsProposalTest.class);
		suite.addTestSuite(SpellingAnnotationTest.class);
		suite.addTestSuite(SpellingCorrectionProcessorTest.class);
		suite.addTestSuite(TextInvocationContextTest.class);
		// $JUnit-END$
		return suite;
	}

}
