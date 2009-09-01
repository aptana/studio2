/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.ILoggable;
import com.aptana.ide.core.ILogger;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.sync.SyncState;
import com.aptana.ide.core.io.sync.VirtualFileSyncPair;
import com.aptana.ide.core.model.BaseModelObject;
import com.aptana.ide.core.ui.io.file.ProjectFileManager;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class SyncModel extends BaseModelObject implements ILoggable
{

	/**
	 * USE_STREAM
	 */
	public static final int USE_STREAM = -100;

	/**
	 * WAITING
	 */
	public static final int WAITING = 0;

	/**
	 * RUNNING
	 */
	public static final int RUNNING = 1;

	/**
	 * SUCCESS
	 */
	public static final int SUCCESS = 2;

	/**
	 * FAILURE
	 */
	public static final int FAILURE = 3;

	/**
	 * Sync Pair
	 */
	public class SyncPair extends VirtualFileSyncPair
	{

		int status;
		IVirtualFileManager sourceManager;
		IVirtualFileManager destManager;
		String fromEndpoint;
		String fromFolder;
		String toEndpoint;
		String toFolder;

		SyncPair(VirtualFileSyncPair vfsPair)
		{
			this(vfsPair.getSourceFile(), vfsPair.getDestinationFile(), vfsPair.getRelativePath(), vfsPair
					.getSyncState());
		}

		SyncPair(IVirtualFile sourceFile, IVirtualFile destinationFile, String relativePath, int syncState)
		{
			super(sourceFile, destinationFile, relativePath, syncState);
		}

		/**
		 * Gets the destination manager
		 * 
		 * @return - vfm
		 */
		public IVirtualFileManager getDestManager()
		{
			return destManager;
		}

		/**
		 * Gets the source manager
		 * 
		 * @return - vfm
		 */
		public IVirtualFileManager getSourceManager()
		{
			return sourceManager;
		}

		/**
		 * @return the status
		 */
		public int getStatus()
		{
			return status;
		}

		/**
		 * @return the fromEndpoint
		 */
		public String getFromEndpoint()
		{
			return fromEndpoint;
		}

		/**
		 * @return the fromFolder
		 */
		public String getFromFolder()
		{
			return fromFolder;
		}

		/**
		 * @return the toEndpoint
		 */
		public String getToEndpoint()
		{
			return toEndpoint;
		}

		/**
		 * @return the toFolder
		 */
		public String getToFolder()
		{
			return toFolder;
		}

	}

	private class Pair
	{

		Object from;
		Object to;

		Pair(Object from, Object to)
		{
			this.from = from;
			this.to = to;
		}

	}

	private List<SyncPair> items;
	private List<Pair> toBeProcessed;
	private SyncPair lastSync;
	private SyncPair lastAdded;
	private SyncPair currentSync;
	private Job buildJob;
	private Job syncJob;
	private ILogger logger;

	/**
	 * Sync model
	 */
	public SyncModel()
	{
		this.items = new ArrayList<SyncPair>();
		this.toBeProcessed = new ArrayList<Pair>();
		this.lastSync = null;
		this.lastAdded = null;
		this.currentSync = null;
		listeners = new ListenerList();
		buildJob = new Job("Building sync pairs") //$NON-NLS-1$
		{

			protected IStatus run(IProgressMonitor monitor)
			{
				Pair pair = null;
				synchronized (toBeProcessed)
				{
					while (toBeProcessed.isEmpty())
					{
						try
						{
							toBeProcessed.wait();
						}
						catch (InterruptedException e)
						{
						}
						if (monitor != null && monitor.isCanceled())
						{
							return Status.CANCEL_STATUS;
						}
					}
					if (toBeProcessed.size() > 0)
					{
						pair = toBeProcessed.remove(0);
					}
				}
				if (pair != null)
				{
					String fromEnd = null;
					String fromFolder = null;
					String toEnd = null;
					String toFolder = null;
					Object from = pair.from;
					Object to = pair.to;
					IVirtualFile fromFile = null;
					IVirtualFile toFile = null;
					if (from instanceof IResource)
					{
						IResource resource = (IResource) from;
						fromEnd = resource.getProject().getName();
						fromFolder = resource.getProjectRelativePath().toString();
						fromFile = ProjectFileManager.convertResourceToFile(from);
					}
					else if (from instanceof IVirtualFile)
					{
						fromFile = (IVirtualFile) from;
						fromEnd = fromFile.getFileManager().getNickName();
						fromFolder = fromFile.getRelativePath();
					}
					if (to instanceof IResource)
					{
						IResource resource = (IResource) to;
						toEnd = resource.getProject().getName();
						toFolder = resource.getProjectRelativePath().toString();
						toFile = ProjectFileManager.convertResourceToFile(to);
					}
					else if (to instanceof IVirtualFile)
					{
						toFile = (IVirtualFile) to;
						toEnd = toFile.getFileManager().getNickName();
						toFolder = toFile.getRelativePath();
					}
					else if (to instanceof IVirtualFileManager)
					{
						toFile = ((IVirtualFileManager) to).getBaseFile();
						toEnd = ((IVirtualFileManager) to).getNickName();
						toFolder = toFile.getRelativePath();
					}
					if (fromFile != null && toFile != null)
					{
						if (fromFile.isDirectory())
						{
							try
							{
								boolean dirExists = false;
								IVirtualFile toDir = toFile.getFileManager().createVirtualDirectory(
										toFile.getAbsolutePath() + toFile.getFileManager().getFileSeparator()
												+ fromFile.getName());
								IVirtualFile[] files = toFile.getFiles();
								for (IVirtualFile target : files)
								{
									if (target.isDirectory() && target.getName().equals(fromFile.getName()))
									{
										dirExists = true;
										break;
									}
								}
								if (!dirExists)
								{
									toFile.getFileManager().createLocalDirectory(toDir);
									if (to instanceof IContainer)
									{
										try
										{
											((IContainer) to).refreshLocal(IResource.DEPTH_INFINITE, null);
										}
										catch (CoreException e)
										{
										}
									}
								}
								IVirtualFileManager fromManager = fromFile.getFileManager().cloneManager();
								IVirtualFileManager toManager = toFile.getFileManager().cloneManager();
								if (fromManager != null && toManager != null)
								{
									fromManager.setBasePath(fromFile.getAbsolutePath());
									fromFile = fromManager.createVirtualDirectory(fromFile.getAbsolutePath());
									toManager.setBasePath(toDir.getAbsolutePath());
									toDir = toManager.createVirtualDirectory(toDir.getAbsolutePath());
									Synchronizer syncer = new Synchronizer();
									syncer.setLogger(logger);
									syncer.setServerFileManager(toManager);
									syncer.setClientFileManager(fromManager);
									VirtualFileSyncPair[] pairs = syncer.getSyncItems(fromFile, toDir);
									for (VirtualFileSyncPair vfsPair : pairs)
									{
										if (vfsPair.getSyncState() != SyncState.ClientItemOnly)
										{
											vfsPair.setSyncState(SyncState.ClientItemIsNewer);
										}
										SyncPair syncPair = new SyncPair(vfsPair);
										syncPair.fromEndpoint = fromEnd;
										syncPair.fromFolder = fromFolder;
										syncPair.toEndpoint = toEnd;
										syncPair.toFolder = toFolder;
										syncPair.status = SyncModel.WAITING;
										syncPair.destManager = toManager;
										syncPair.sourceManager = fromManager;
										synchronized (items)
										{
											items.add(syncPair);
											lastAdded = syncPair;
											fireChange();
											items.notify();
										}
									}
								}
							}
							catch (ConnectionException e)
							{
								log(StringUtils.format(Messages.Synchronizer_Error, e.getLocalizedMessage()));
							}
							catch (IOException e)
							{
								log(StringUtils.format(Messages.Synchronizer_Error, e.getLocalizedMessage()));
							}
						}
						else
						{
							toFile = toFile.getFileManager().createVirtualFile(
									toFile.getAbsolutePath() + toFile.getFileManager().getFileSeparator()
											+ fromFile.getName());
							VirtualFileSyncPair vfsPair = new VirtualFileSyncPair(fromFile, toFile, "", USE_STREAM); //$NON-NLS-1$
							SyncPair syncPair = new SyncPair(vfsPair);
							syncPair.fromEndpoint = fromEnd;
							syncPair.fromFolder = fromFolder;
							syncPair.toEndpoint = toEnd;
							syncPair.toFolder = toFolder;
							syncPair.status = SyncModel.WAITING;
							syncPair.destManager = toFile.getFileManager();
							syncPair.sourceManager = fromFile.getFileManager();
							synchronized (items)
							{
								items.add(syncPair);
								lastAdded = syncPair;
								fireChange();
								items.notify();
							}
						}
					}
				}
				if (monitor == null || !monitor.isCanceled())
				{
					this.schedule();
				}
				return Status.OK_STATUS;
			}

		};
		buildJob.setPriority(Job.BUILD);
		buildJob.setSystem(true);
		buildJob.schedule();
		syncJob = new Job("Running Sync jobs") //$NON-NLS-1$
		{

			protected IStatus run(IProgressMonitor monitor)
			{
				synchronized (items)
				{
					while (items.isEmpty())
					{
						try
						{
							items.wait();
						}
						catch (InterruptedException e)
						{
						}
						if (monitor != null && monitor.isCanceled())
						{
							return Status.CANCEL_STATUS;
						}
					}
					if (items.size() > 0)
					{
						SyncPair pair = items.remove(0);
						currentSync = pair;
						currentSync.status = SyncModel.RUNNING;
						fireChange();
						if (pair.getSyncState() == USE_STREAM)
						{
							try
							{
								log(FileUtils.NEW_LINE
										+ StringUtils.format(Messages.Synchronizer_Uploading, pair.getSourceFile()
												.getAbsolutePath()));
								if (!pair.getDestinationFile().equals(pair.getSourceFile()))
								{
									pair.getDestinationFile().putStream(pair.getSourceFile().getStream());
									try
									{
										pair.getDestinationFile().setModificationMillis(
												pair.getSourceFile().getModificationMillis());
									}
									catch (Exception e)
									{
										// Catch exceptions here since although the file was uploaded, not being able to
										// set the mod time shouldn't trigger an error
									}
								}
								log(Messages.Synchronizer_Success);
								pair.status = SyncModel.SUCCESS;
								lastSync = pair;
								fireChange();
							}
							catch (Exception e)
							{
								log(StringUtils.format(Messages.Synchronizer_Error, e.getLocalizedMessage()));
								pair.status = SyncModel.FAILURE;
								lastSync = pair;
								fireChange();
							}
						}
						else
						{
							Synchronizer syncer = new Synchronizer();
							syncer.setLogger(logger);
							try
							{
								syncer.setClientFileManager(pair.sourceManager);
								syncer.setServerFileManager(pair.destManager);
								syncer.uploadAndDelete(new VirtualFileSyncPair[] { pair });
								pair.status = SyncModel.SUCCESS;
								lastSync = pair;
								fireChange();
							}
							catch (Exception e)
							{
								log(StringUtils.format(Messages.Synchronizer_Error, e.getLocalizedMessage()));
								pair.status = SyncModel.FAILURE;
								lastSync = pair;
								fireChange();
							}
						}
					}
				}
				if (monitor == null || !monitor.isCanceled())
				{
					this.schedule();
				}
				return Status.OK_STATUS;
			}

		};
		syncJob.setPriority(Job.BUILD);
		syncJob.setSystem(true);
		syncJob.schedule();
	}

	/**
	 * Disposes the model
	 */
	public void dispose()
	{
		synchronized (items)
		{
			syncJob.cancel();
			items.notify();
		}
		synchronized (toBeProcessed)
		{
			buildJob.cancel();
			toBeProcessed.notify();
		}
	}

	private synchronized void log(String message)
	{
		if (this.logger != null)
		{
			this.logger.logInfo(message);
		}
	}

	/**
	 * Adds items to be synced
	 * 
	 * @param from
	 * @param to
	 */
	public void addSyncing(Object from, Object to)
	{
		synchronized (toBeProcessed)
		{
			toBeProcessed.add(new Pair(from, to));
			toBeProcessed.notify();
		}
	}

	/**
	 * Gets the items in the queue
	 * 
	 * @return - array of items
	 */
	public SyncPair[] getItems()
	{
		return this.items.toArray(new SyncPair[0]);
	}

	/**
	 * @return the lastSync
	 */
	public SyncPair getLastSync()
	{
		return this.lastSync;
	}

	/**
	 * @return the lastSync
	 */
	public SyncPair getLastAdded()
	{
		return this.lastAdded;
	}

	/**
	 * @return the currentSync
	 */
	public SyncPair getCurrentSync()
	{
		return this.currentSync;
	}

	/**
	 * @see com.aptana.ide.core.ILoggable#getLogger()
	 */
	public ILogger getLogger()
	{
		return this.logger;
	}

	/**
	 * @see com.aptana.ide.core.ILoggable#setLogger(com.aptana.ide.core.ILogger)
	 */
	public synchronized void setLogger(ILogger logger)
	{
		this.logger = logger;
	}

}
