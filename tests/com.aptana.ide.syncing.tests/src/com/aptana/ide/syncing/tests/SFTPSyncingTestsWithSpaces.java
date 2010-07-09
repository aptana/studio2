package com.aptana.ide.syncing.tests;

public class SFTPSyncingTestsWithSpaces extends SFTPSyncingTests
{

	@Override
	protected void setUp() throws Exception
	{
		fileName = "file name.txt";
		folderName = "folder name";			
		super.setUp();
	}
}
