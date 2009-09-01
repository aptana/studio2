package com.aptana.ide.syncing.ftp;

/**
 * A factory for the creation of dialogs that are related to the FTP settings. This class provides the basic FTP dialogs
 * and can be the base class for any other FTP types factories.
 * 
 * @author Shalom Gibly
 */
public class FtpDialogFactory
{

	private static FtpDialogFactory instance;

	/**
	 * Returns an instance of this factory.
	 * 
	 * @return A singleton instance of this factory.
	 */
	public static FtpDialogFactory getInstance()
	{
		if (instance == null)
		{
			instance = new FtpDialogFactory();
		}
		return instance;
	}

	/**
	 * Creates a new {@link AdvancedFTPDialog} for the given {@link FtpDialog}.
	 * 
	 * @param dialog
	 * @return a new AdvancedFTPDialog
	 */
	public AdvancedFTPDialog createAdvancedFtpDialog(FtpDialog dialog)
	{
		return new AdvancedFTPDialog(dialog);
	}
}
