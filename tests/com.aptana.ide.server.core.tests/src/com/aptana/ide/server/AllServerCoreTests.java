package com.aptana.ide.server;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.ide.server.resources.FileHttpResourceTest;
import com.aptana.ide.server.resources.WorkspaceHttpFolderResourceTest;
import com.aptana.ide.server.resources.WorkspaceHttpResourceTest;

public class AllServerCoreTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All Tests for com.aptana.ide.server.core plugin");
		// $JUnit-BEGIN$
		suite.addTestSuite(FileHttpResourceTest.class);
		suite.addTestSuite(WorkspaceHttpResourceTest.class);
		suite.addTestSuite(WorkspaceHttpFolderResourceTest.class);
		// $JUnit-END$
		return suite;
	}

}
