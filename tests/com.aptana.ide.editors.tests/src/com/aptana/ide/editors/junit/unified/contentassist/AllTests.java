package com.aptana.ide.editors.junit.unified.contentassist;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editors.junit.unified.contentassist");
		//$JUnit-BEGIN$
		suite.addTestSuite(UnifiedContentAssistProcessorTest.class);
		suite.addTestSuite(CompletionProposalPopupTest.class);
		//$JUnit-END$
		return suite;
	}

}
