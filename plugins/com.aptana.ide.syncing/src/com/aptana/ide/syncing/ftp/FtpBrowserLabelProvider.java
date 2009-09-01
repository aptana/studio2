package com.aptana.ide.syncing.ftp;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.ProtocolManager;
import com.aptana.ide.core.ui.views.fileexplorer.Messages;
import com.aptana.ide.io.file.FilePlugin;

/**
 * Provides labels for File View tree items when browsing the FTP structure.
 */
public class FtpBrowserLabelProvider extends LabelProvider
{

	private WorkbenchLabelProvider backupProvider = new WorkbenchLabelProvider();

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		String retVal = null;
		if (element instanceof ProtocolManager)
		{
			ProtocolManager pm = (ProtocolManager) element;
			retVal = pm.getDisplayName();
		}
		else if (element instanceof IVirtualFileManager)
		{
			retVal = ((IVirtualFileManager) element).getDescriptiveLabel();
		}
		else if (element instanceof IVirtualFile)
		{
			IVirtualFile f = (IVirtualFile) element;
			retVal = f.getName();
		}
		else if (element instanceof IAdaptable)
		{
			Object adapter = ((IAdaptable) element).getAdapter(FtpBrowserContentProvider.class);
			retVal = backupProvider.getText(adapter);
		}
		else
		{
			retVal = backupProvider.getText(element);
		}
		if (element == FtpBrowserContentProvider.LOADING)
		{
			return Messages.FileExplorerView_Loading;
		}
		if (retVal == null)
		{
			retVal = Messages.FileExplorerView_UnknownElement;
		}
		return retVal;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		Image image = null;

		if (element instanceof ProtocolManager)
		{
			ProtocolManager pm = (ProtocolManager) element;
			image = pm.getImage();
		}
		else if (element instanceof IVirtualFileManager)
		{
			IVirtualFileManager fm = (IVirtualFileManager) element;
			image = fm.getImage();
		}
		else if (element instanceof IVirtualFile)
		{
			IVirtualFile f = (IVirtualFile) element;
			image = f.getImage();
			if (image == null)
			{
				if (f.isDirectory())
				{
					IWorkbench workbench = PlatformUI.getWorkbench();
					if (workbench != null)
					{
						ISharedImages sharedImages = workbench.getSharedImages();
						image = sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
					}
				}
			}
		}
		else if (element == FtpBrowserContentProvider.LOADING)
		{
			return FilePlugin.getImage("icons/hourglass.png"); //$NON-NLS-1$
		}
		else if (element instanceof IAdaptable)
		{
			Object adapter = ((IAdaptable) element).getAdapter(FtpBrowserContentProvider.class);
			image = backupProvider.getImage(adapter);
		}
		else
		{
			image = backupProvider.getImage(element);
		}

		return image;
	}
}