package com.aptana.ide.io.ftp;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;

import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.IVirtualFileManagerDialog;
import com.aptana.ide.syncing.ftp.FtpDialog;

/**
 * The FTPManagerDialogDelegate delegates required calls for IVirtualFileManagerDialog with enhanced implementation.
 * This class allows a use of any UI component, including {@link Dialog}, that cannot implement directly the
 * IVirtualFileManagerDialog because it contains a conflicting open() method.
 * 
 * @author Shalom Gibly
 * @since Aptana Studio 1.2.4
 */
public class FTPManagerDialogDelegate implements IVirtualFileManagerDialog
{
	private FtpDialog ftpDialog;

	/**
	 * Constructs a new FTP manager dialog delegate.
	 * 
	 * @param ftpDialog
	 *            The FTP dialog to delegate
	 */
	public FTPManagerDialogDelegate(FtpDialog ftpDialog)
	{
		this.ftpDialog = ftpDialog;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.IVirtualFileManagerDialog#setItem(com.aptana.ide.core.io.IVirtualFileManager,
	 * boolean)
	 */
	public void setItem(IVirtualFileManager vfm, boolean newItem)
	{
		if (vfm instanceof IFtpVirtualFileManager)
			ftpDialog.setItem((IFtpVirtualFileManager) vfm, newItem);
		else
			throw new IllegalArgumentException("Expected an IFtpVirtualFileManager type"); //$NON-NLS-1$
	}

	/**
	 * The open method is delegated to a dialog UI.
	 * 
	 * @see com.aptana.ide.core.io.IVirtualFileManagerDialog#open()
	 */
	public IVirtualFileManager open()
	{
		if (ftpDialog.open() == Window.OK)
		{
			return ftpDialog.getItem();
		}
		return null;
	}
}
