/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.ingo;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.core.io.ingo.ConnectionException;
import com.aptana.ide.core.io.ingo.IVirtualFile;
import com.aptana.ide.core.io.ingo.IVirtualFileManager;
import com.aptana.ide.core.io.ingo.VirtualFile;
import com.aptana.ide.core.io.ingo.VirtualFileManagerSyncPair;
import com.aptana.ide.core.io.ingo.VirtualFileSyncPair;
import com.aptana.ide.core.ui.DialogUtils;
import com.aptana.ide.syncing.core.ingo.Synchronizer;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants;

/**
 * Uploads an item
 * 
 * @author Ingo Muschenetz
 */
public class FileDownloadAction extends BaseSyncAction
{
	/**
	 * DownloadAction
	 */
	public FileDownloadAction()
	{
		gettingMessage = StringUtils.ellipsify(Messages.FileDownloadAction_DownloadingItems);
		syncingMessage = StringUtils.ellipsify(Messages.FileDownloadAction_Downloading);
		confirmMessage = Messages.FileDownloadAction_DownloadSelectedItems;
	}

	public static IFileStore[] getDownloadFiles(IConnectionPoint sourceManager, IConnectionPoint destManager, IFileStore[] files, boolean ignoreError) throws ConnectionException
	{
		IFileStore[] reparentedFiles = VirtualFile.reparentFiles(sourceManager, files);
		Set<IFileStore> newFiles = new HashSet<IFileStore>();
		IFileStore file;
		String filePath;
		IFileStore newFile;
		for (int i = 0; i < reparentedFiles.length; i++)
		{
			file = reparentedFiles[i];
//			filePath = EFSUtils.getRelativePath(file);
//			filePath = StringUtils.replace(filePath, file.getFileManager().getFileSeparator(), destManager
//					.getFileSeparator());

			newFile = null;
			try
			{
				if (file.fetchInfo().isDirectory())
				{
					newFile = EFSUtils.createFile(sourceManager.getRoot(), file, destManager.getRoot());
					newFile.mkdir(EFS.NONE, null);
					//destManager.createVirtualDirectory(destManager.getBasePath() + filePath);
					IFileStore[] f = EFSUtils.getFiles(newFile, true, false);
					if (!newFiles.contains(newFile))
					{
						newFiles.add(newFile);
					}
					newFiles.addAll(Arrays.asList(f));
				}
				else
				{
					newFile = EFSUtils.createFile(sourceManager.getRoot(), file, destManager.getRoot());
					if (newFile.fetchInfo().exists())
					{
						if (!newFiles.contains(newFile))
						{
							newFiles.add(newFile);
						}
					}
				}
			}
			catch (CoreException e)
			{
				if (newFile != null && !ignoreError)
				{
//					SyncingConsole.println(StringUtils.format(Messages.FileDownloadAction_FileDoesNotExistAtRemoteSite,
//							newFile.getAbsolutePath())); // we ignore files that don't exist on the remote server
				}
			}
		}

		return newFiles.toArray(new IVirtualFile[newFiles.size()]);
	}

	/**
	 * @see com.aptana.ide.syncing.ui.ingo.BaseSyncAction#syncItems(com.aptana.ide.syncing.Synchronizer,
	 *      com.aptana.ide.core.io.sync.VirtualFileSyncPair[])
	 */
	protected void syncItems(Synchronizer sm, VirtualFileSyncPair[] items) throws ConnectionException, IOException, CoreException
	{
		sm.download(items);
	}

	/**
	 * @throws CoreException 
	 * @see com.aptana.ide.syncing.ui.ingo.BaseSyncAction#getItems(com.aptana.ide.syncing.Synchronizer,
	 *      com.aptana.ide.core.io.sync.VirtualFileManagerSyncPair, com.aptana.ide.core.io.IVirtualFile[])
	 */
	protected VirtualFileSyncPair[] getItems(Synchronizer sm, VirtualFileManagerSyncPair conf, IVirtualFile[] files)
			throws ConnectionException, IOException, CoreException
	{
		IFileStore[] newFiles = getDownloadFiles(conf.getSourceFileManager(), conf.getDestinationFileManager(), files, false);
		// set upload flag so we get a proper VFM refresh
		conf.setSyncState(VirtualFileManagerSyncPair.Download);

		sm.setClientFileManager(conf.getSourceFileManager());
		sm.setServerFileManager(conf.getDestinationFileManager());
		return sm.createSyncItems(new IFileStore[0], newFiles);
	}

	/**
	 * @see BaseSyncAction#displayDone(Synchronizer)
	 */
	protected void displayDone(final Synchronizer sm)
	{
		UIJob job = new UIJob(Messages.FileDownloadAction_UIJOB_ShowingDownloadFinishedDialog)
		{
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				IPreferenceStore store = SyncingUIPlugin.getDefault().getPreferenceStore();
				DialogUtils.openIgnoreMessageDialogInformation(Display.getCurrent().getActiveShell(), syncingMessage,
						StringUtils.format(Messages.FileDownloadAction_ItemsDownloaded, sm
								.getServerFileTransferedCount()), store,
						IPreferenceConstants.IGNORE_DIALOG_FILE_DOWNLOAD);
				SyncingUIPlugin.getDefault().savePluginPreferences();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	/**
	 * @see com.aptana.ide.syncing.ui.ingo.BaseSyncAction#refresh(com.aptana.ide.syncing.Synchronizer)
	 */
	protected void refresh(Synchronizer sm)
	{
		if (selectedObjects != null)
		{
			for (Object o : selectedObjects)
			{
				if (o instanceof IResource)
				{
					IResource res = (IResource) o;
					try
					{
						res.refreshLocal(IResource.DEPTH_INFINITE, null);
					}
					catch (CoreException e)
					{
						//Unable to refresh resource
					}
				}
			}
		}
	}
}
