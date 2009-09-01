package com.aptana.ide.io.ftp;

import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.syncing.ftp.FtpDialogFactory;

/**
 * An interface for all the FTP virtual file managers.
 * 
 * @author Shalom Gibly
 */
public interface IFtpVirtualFileManager extends IVirtualFileManager
{
	public String getUser();

	public String getPassword();

	public String getServer();

	public String getNickName();

	public boolean getSavePassword();

	public int getPort();

	public boolean getPassiveMode();

	public void setSavePassword(boolean selection);

	public void setPassword(String text);

	public void setUser(String text);

	public void setServer(String text);

	public void setPort(int intValue);

	public void setPassiveMode(boolean selection);
	
	public FtpDialogFactory getDialogFactory();
	
	public boolean supportsPublicKeyAuthentication();
	
	public void setPrivateKeyFile(String keyFile);
	
	public String getPrivateKeyFile();
	
}
