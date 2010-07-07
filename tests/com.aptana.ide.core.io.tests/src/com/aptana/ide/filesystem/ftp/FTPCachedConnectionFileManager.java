package com.aptana.ide.filesystem.ftp;


public class FTPCachedConnectionFileManager extends FTPConnectionFileManager
{

	public FTPCachedConnectionFileManager()
	{
		super();
		setCaching(true);
	}

}
