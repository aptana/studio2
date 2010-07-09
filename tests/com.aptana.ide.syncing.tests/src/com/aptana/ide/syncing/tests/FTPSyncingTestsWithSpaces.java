package com.aptana.ide.syncing.tests;

public class FTPSyncingTestsWithSpaces extends FTPSyncingTests
{

	@Override
	protected void setUp() throws Exception
	{
		fileName = "file name.txt";
		folderName = "folder name";
		
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
