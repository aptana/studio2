package com.aptana.ide.syncing.tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.filesystem.ftp.FTPConnectionPoint;

public class SFTPSyncingTests extends FTPSyncingTests
{

	@Override
	public IConnectionPoint getServerConnectionPoint() throws IOException
	{
		Properties props = new Properties();
		FileInputStream inStream = new FileInputStream(System.getenv().get("junit.properties"));
		props.load(inStream);

		FTPConnectionPoint ftpcp = new FTPConnectionPoint();
		ftpcp.setHost(props.getProperty("sftp.host")); //$NON-NLS-1$
		ftpcp.setLogin(props.getProperty("sftp.username")); //$NON-NLS-1$
		ftpcp.setPassword(props.getProperty("sftp.password").toCharArray());
		ftpcp.setPort(Integer.parseInt(props.getProperty("sftp.port")));

		return ftpcp;
	}
}
