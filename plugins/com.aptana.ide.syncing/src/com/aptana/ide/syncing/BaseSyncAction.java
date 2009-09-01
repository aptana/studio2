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
package com.aptana.ide.syncing;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.IVirtualFileManagerEventHandler;
import com.aptana.ide.core.io.sync.SyncEventHandlerAdapterWithProgressMonitor;
import com.aptana.ide.core.io.sync.SyncManager;
import com.aptana.ide.core.io.sync.VirtualFileManagerSyncPair;
import com.aptana.ide.core.io.sync.VirtualFileSyncPair;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.DialogUtils;
import com.aptana.ide.core.ui.SetUtils;
import com.aptana.ide.core.ui.actions.ActionDelegate;
import com.aptana.ide.core.ui.io.file.LocalFileManager;
import com.aptana.ide.core.ui.views.fileexplorer.FileExplorerView;
import com.aptana.ide.syncing.preferences.IPreferenceConstants;

/**
 * Uploads an item
 * 
 * @author Ingo Muschenetz
 */
public abstract class BaseSyncAction extends ActionDelegate implements IViewActionDelegate
{
	Object[] selectedObjects;
	IVirtualFile[] selectedFiles;
	VirtualFileSyncPair[] items;

	/**
	 * gettingMessage
	 */
	protected String gettingMessage = StringUtils.EMPTY;

	/**
	 * syncingMessage
	 */
	protected String syncingMessage = StringUtils.EMPTY;

	/**
	 * confirmMessage
	 */
	protected String confirmMessage = StringUtils.EMPTY;

	private boolean _answer;

	/**
	 * Initializes initial items
	 * 
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view)
	{
	}

	/**
	 * getItems
	 * 
	 * @param sm
	 * @param conf
	 * @param files
	 * @return VirtualFileSyncPair[]
	 * @throws ConnectionException
	 * @throws IOException
	 */
	protected abstract VirtualFileSyncPair[] getItems(Synchronizer sm, VirtualFileManagerSyncPair conf,
			IVirtualFile[] files) throws ConnectionException, IOException;

	/**
	 * syncItems
	 * 
	 * @param sm
	 * @param items
	 * @throws ConnectionException
	 * @throws IOException
	 */
	protected abstract void syncItems(Synchronizer sm, VirtualFileSyncPair[] items) throws ConnectionException,
			IOException;

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		selectedObjects = getValidSelection(selection);
		selectedFiles = extractIVirtualFilesFromSelection(selectedObjects);
		action.setEnabled(selectedFiles.length > 0);
	}

	/**
	 * @param objects
	 * @return IVirtualFile[]
	 */
	public IVirtualFile[] extractIVirtualFilesFromSelection(Object[] objects)
	{
		List<IVirtualFile> resolvedFiles = new ArrayList<IVirtualFile>();
		Object object;
		for (int i = 0; i < objects.length; i++)
		{
			object = objects[i];
			if (object instanceof IVirtualFileManager)
			{
				try
				{
					object = ((IVirtualFileManager) object).getBaseFile();
				}
				catch (Exception e)
				{
					// Catch the exception thrown if the vfm does not have a
					// base file and just move on to the next
					// element since the vfm isn't correct
				}
			}

			if (object instanceof IVirtualFile)
			{
				IVirtualFile file = (IVirtualFile) object;
				// right now we only enable the "Synchronize" action on virtual
				// file managers
				if (SyncManager.hasContainingSyncPair(file))
				{
					resolvedFiles.add(file);
				}
			}
		}
		return resolvedFiles.toArray(new IVirtualFile[0]);
	}

	/**
	 * getVirtualFileManagerSyncPair
	 * 
	 * @param files
	 * @return VirtualFileManagerSyncPair
	 */
	@SuppressWarnings("unchecked")
	protected VirtualFileManagerSyncPair getVirtualFileManagerSyncPair(IVirtualFile[] files)
	{
		List<HashSet<VirtualFileManagerSyncPair>> syncSets = new ArrayList<HashSet<VirtualFileManagerSyncPair>>();
		for (int i = 0; i < files.length; i++)
		{
			IVirtualFile file = files[i];
			VirtualFileManagerSyncPair[] confs = SyncManager.getContainingSyncPairs(file, true);
			HashSet<VirtualFileManagerSyncPair> newSet = new HashSet<VirtualFileManagerSyncPair>();
			newSet.addAll(Arrays.asList(confs));
			syncSets.add(newSet);
		}

		Set<Object>[] array = syncSets.toArray(new Set[syncSets.size()]);
		Set<Object> intersection = SetUtils.getIntersection(array);
		VirtualFileManagerSyncPair[] confs = (VirtualFileManagerSyncPair[]) intersection
				.toArray(new VirtualFileManagerSyncPair[0]);

		if (confs.length == 0)
		{
			MessageDialog.openWarning(CoreUIUtils.getActiveShell(), syncingMessage,
					Messages.BaseSyncAction_UnableToContinueNoCommonParent);
			return null;
		}
		else if (confs.length > 1)
		{
			VirtualFileManagerSyncPair initiallySelectedPair = null;
			if (selectedObjects != null && selectedObjects.length > 0 && selectedObjects[0] instanceof IResource)
			{
				IProject project = ((IResource) selectedObjects[0]).getProject();
				boolean rememberDecision = ProjectSynchronizationUtils.isRememberDecision(project);
				if (rememberDecision) {
					String lastSyncConnection = ProjectSynchronizationUtils.getLastSyncConnection(project);
					if (lastSyncConnection != null && !lastSyncConnection.equals("")) { //$NON-NLS-1$
						VirtualFileManagerSyncPair pair =
							ProjectSynchronizationUtils.fromSerializableString(project, lastSyncConnection);
						if (pair != null && pair.isValid()) {
							return pair;
						}
					}
				}
				boolean allCloudConnections = true; 	 
                for (VirtualFileManagerSyncPair conf : confs) {
                	if (!ProjectSynchronizationUtils.isCloudConnection(conf)) 	 
                	{ 	 
                		allCloudConnections = false; 	 
                		break; 	 
                	} 	 
                } 	 
                if (allCloudConnections) { 	 
                	String lastCloudSyncConnection = ProjectSynchronizationUtils.getLastCloudSyncConnection(project); 	 
                	if (lastCloudSyncConnection != null && !lastCloudSyncConnection.equals("")) { //$NON-NLS-1$ 	 
                		VirtualFileManagerSyncPair lastCloudSyncConnectionPair = 	 
                			ProjectSynchronizationUtils.fromSerializableString(project, lastCloudSyncConnection); 	 
                		if (lastCloudSyncConnectionPair != null && lastCloudSyncConnectionPair.isValid()) { 	 
                			initiallySelectedPair = lastCloudSyncConnectionPair; 	 
                		} 	 
                	} 	 
                }
			}
			// TODO Check if the user has asked to remember their decision of using the last sync target
			// If yes what is the last sync target and is it a valid target
			//     if it is valid return
			//     if it is not valid fall through
			// If no fall through
			ChooseSyncConfigurationDialog nld = new ChooseSyncConfigurationDialog(
					CoreUIUtils.getActiveShell());

			nld.setShowRememberMyDecision(isShowRememberMyDecision());
            nld.setItems(confs);
            if (initiallySelectedPair != null) {
                nld.setInitialItem(initiallySelectedPair);
            }
            nld.open();

            VirtualFileManagerSyncPair conf = nld.getSelectedItem();
            if (conf != null) {
                processRemeberMyDecision(conf, nld.isRememberMyDecision());
            }

			return conf;
		}
		else
		{
			return confs[0];
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		IdeLog.logInfo(SyncingPlugin.getDefault(), Messages.BaseSyncAction_Running);

		if (selectedFiles == null || selectedFiles.length == 0)
		{
			return;
		}

		items = null;

		final VirtualFileManagerSyncPair conf = getVirtualFileManagerSyncPair(selectedFiles);
		if (conf != null)
		{
			IPreferenceStore store = SyncingPlugin.getDefault().getPreferenceStore();
			int returnCode = DialogUtils.openIgnoreMessageDialogConfirm(CoreUIUtils.getActiveShell(),
					syncingMessage, StringUtils.format(confirmMessage, conf.getDestinationFileManager().getNickName())
							+ " " + Messages.BaseSyncAction_ActionWillOverwrite, //$NON-NLS-1$
					store, IPreferenceConstants.IGNORE_DIALOG_FILE_SYNC_PROMPT);
			SyncingPlugin.getDefault().savePluginPreferences();

			if (returnCode == MessageDialog.CANCEL)
			{
				return;
			}

			final Synchronizer sm = new Synchronizer();
			Job syncJob = new Job(gettingMessage)
			{

				protected IStatus run(IProgressMonitor monitor)
				{

					try
					{
						monitor.subTask(StringUtils.ellipsify(Messages.BaseSyncAction_RetrievingItems));
						items = getSyncItems(monitor, sm, conf, selectedFiles);
						monitor.beginTask(gettingMessage, items.length);
						performSync(monitor, sm, items);
					}
					catch (Exception e)
					{
					    CoreUIUtils.fixConnection(conf.getDestinationFileManager());
					    return Status.CANCEL_STATUS;
					}
					monitor.done();

					if (items == null)
					{
						items = new VirtualFileSyncPair[0];
					}

					return Status.OK_STATUS;
				}

			};
			syncJob.setPriority(Job.BUILD);
			syncJob.schedule();
		}
		else
		{
			return;
		}
	}

	/**
	 * @param sm
	 */
	protected abstract void displayDone(final Synchronizer sm);

	/**
	 * Performs the sync operation, displaying progress status as the files as
	 * transferred
	 * 
	 * @param monitor
	 * @param sm
	 * @param items
	 * @throws InvocationTargetException
	 */
	private void performSync(final IProgressMonitor monitor, final Synchronizer sm, final VirtualFileSyncPair[] items)
			throws InvocationTargetException
	{
		if (items == null)
		{
			return;
		}

		sm.setEventHandler(new SyncEventHandlerAdapterWithProgressMonitor(monitor)
		{
			private int fSyncDoneCount = 0;

			public void syncDone(VirtualFileSyncPair item)
			{
				super.syncDone(item);
				fSyncDoneCount++;
				checkDone();
			}

			public boolean syncErrorEvent(VirtualFileSyncPair item, Exception e)
			{
				showError(e.getLocalizedMessage(), e, false);
				fSyncDoneCount++;
				checkDone();
				return _answer && super.syncErrorEvent(item, e);
			}

			public boolean getFilesEvent(IVirtualFileManager manager, String path)
			{
				monitor.subTask(syncingMessage + path);
				return super.getFilesEvent(manager, path);
			}

			private void checkDone()
			{
				if (fSyncDoneCount == items.length) {
					refresh(sm);
					displayDone(sm);
					sm.setEventHandler(null);
					sm.disconnect();
				}
			}
		});

		try
		{
			syncItems(sm, items);
		}
		catch (Exception e)
		{
			throw new InvocationTargetException(e);
		}
	}

	/**
	 * Grabs the list of items to sync
	 * 
	 * @param monitor
	 * @param sm
	 * @param items
	 * @throws InvocationTargetException
	 */
	private VirtualFileSyncPair[] getSyncItems(final IProgressMonitor monitor, Synchronizer sm,
			VirtualFileManagerSyncPair conf, final IVirtualFile[] files) throws InvocationTargetException
	{
		IVirtualFileManagerEventHandler fm = new IVirtualFileManagerEventHandler()
		{
			public boolean getFilesEvent(IVirtualFileManager manager, String path)
			{
				return !monitor.isCanceled();
			}
		};

		sm.setEventHandler(new SyncEventHandlerAdapterWithProgressMonitor(monitor));

		try
		{
			if (conf.getSourceFileManager() != null)
			{
				conf.getSourceFileManager().setEventHandler(fm);
			}
			if (conf.getDestinationFileManager() != null)
			{
				conf.getDestinationFileManager().setEventHandler(fm);
			}
			VirtualFileSyncPair[] newItems = getItems(sm, conf, files);

			// we only want to return items if we didn't cancel
			if (monitor.isCanceled())
			{
				return new VirtualFileSyncPair[0];
			}
			else
			{
				return newItems;
			}
		}
		catch (Exception e)
		{
			throw new InvocationTargetException(e);
		}
		finally
		{
			if (conf.getSourceFileManager() != null)
			{
				conf.getSourceFileManager().setEventHandler(null);
			}
			if (conf.getDestinationFileManager() != null)
			{
				conf.getDestinationFileManager().setEventHandler(null);
			}
			sm.setEventHandler(null);
			monitor.done();
		}
	}

	/**
	 * @param message
	 * @param e
	 * @param log
	 */
	public void showError(final String message, final Exception e, final boolean log)
	{
		_answer = false;
		final Display currentDisplay = Display.getDefault();
		if (currentDisplay != null)
		{
			currentDisplay.syncExec(new Runnable()
			{
				public void run()
				{
					MessageDialog md = new MessageDialog(currentDisplay.getActiveShell(),
							Messages.BaseSyncAction_SynchronizationError, null, message, MessageDialog.WARNING,
							new String[]
								{ CoreStrings.CONTINUE, CoreStrings.CANCEL }, 1);
					int answer = md.open();
					_answer = answer == 0;
					if (log)
					{
						IdeLog.logError(CoreUIPlugin.getDefault(), message, e);
					}

				}
			});
		}
	}

	/**
	 * Sets the list of selected objects
	 * 
	 * @param objects
	 */
	public void setSelectedObjects(Object[] objects)
	{
		this.selectedObjects = objects;
	}

	/**
	 * Sets the list of selected files
	 * 
	 * @param files
	 */
	public void setSelectedFiles(IVirtualFile[] files)
	{
		this.selectedFiles = files;
	}

	/**
	 * Update all views containing a virtual file manager that may have changed
	 * during a sync operation. The file managers that are updated are based on
	 * the type up sync defined in the configuration
	 * 
	 * @param configuration
	 */
	public static void refreshViews(final VirtualFileManagerSyncPair configuration)
	{
		// get display
		Display display = CoreUIUtils.getDisplay();

		// execute callback in the correct thread
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchPart[] part = CoreUIUtils.getViewsInternal(FileExplorerView.ID);

				for (int i = 0; i < part.length; i++)
				{
					FileExplorerView fileExplorer = (FileExplorerView) part[i];

					switch (configuration.getSyncState())
					{
						case VirtualFileManagerSyncPair.Download:
							this.refresh(fileExplorer, configuration.getSourceFileManager());
							break;

						case VirtualFileManagerSyncPair.Upload:
							this.refresh(fileExplorer, configuration.getDestinationFileManager());
							break;

						case VirtualFileManagerSyncPair.Both:
							this.refresh(fileExplorer, configuration.getSourceFileManager());
							this.refresh(fileExplorer, configuration.getDestinationFileManager());
							break;

						default:
							break;
					}
				}
			}

			private void refresh(FileExplorerView fileExplorer, IVirtualFileManager fileManager)
			{
				if (fileManager instanceof LocalFileManager)
				{
					fileExplorer.refresh(fileManager.getBaseFile());
				}
				else
				{
					fileExplorer.refresh(fileManager);
				}
			}
		});
	}

	/**
	 * Do any post-sync-action refreshing
	 */
	protected void refresh(Synchronizer sm)
	{
	}

	protected boolean isShowRememberMyDecision() {
		return selectedObjects.length > 0 && selectedObjects[0] instanceof IResource;
	}

	protected void processRemeberMyDecision(VirtualFileManagerSyncPair conf, boolean rememberMyDecision) {
		if (isShowRememberMyDecision()) {
			IProject project = ((IResource) selectedObjects[0]).getProject();
			if (rememberMyDecision) {
				ProjectSynchronizationUtils.setRememberDecision(project, rememberMyDecision);
			}
			
			// Remember last sync connection
			String confSerializableString = ProjectSynchronizationUtils.toSerializableString(conf);
			ProjectSynchronizationUtils.setLastSyncConnection(project, confSerializableString);
			if (ProjectSynchronizationUtils.isCloudConnection(conf)) {
				// Remember last cloud sync connection
				ProjectSynchronizationUtils.setLastCloudSyncConnection(project, confSerializableString);
			}
		}
	}	
}
