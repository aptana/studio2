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
package com.aptana.ide.core.io.ingo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.Messages;
import com.aptana.ide.core.StringUtils;

/**
 * @author Paul Colton
 */
public final class SyncManager implements ISerializableSyncItem
{
	/**
	 * Singleton class
	 */
	static SyncManager _syncManager = null;

	private ArrayList<Object> items = new ArrayList<Object>();
	private ArrayList<String> unknownItems = new ArrayList<String>();
	private ArrayList<ISyncManagerChangeListener> listeners = new ArrayList<ISyncManagerChangeListener>();
	private boolean isBusy; // indicate loading/saving states
	private Object lock = new Object();
	private Job saveJob;

	/**
	 * SyncManager constructor
	 */
	private SyncManager()
	{
		saveJob = new SaveJob();
	}

	/**
	 * Reads any persisted state info.
	 * 
	 * @param file
	 */
	private void readState(File file)
	{
		synchronized (lock)
		{
			isBusy = true;
		}
		StringBuffer contents = new StringBuffer();

		BufferedReader input = null;
		try
		{
			input = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = input.readLine()) != null)
			{
				contents.append(line);
				// contents.append(System.getProperty("line.separator"));
			}
		}
		catch (FileNotFoundException ex)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), StringUtils.format(Messages.InitialStartup_CannotFindFile,
					file.getAbsolutePath()));
		}
		catch (IOException ex)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), StringUtils.format(Messages.InitialStartup_IOError, file
					.getAbsolutePath()));
		}
		finally
		{
			try
			{
				if (input != null)
				{
					input.close();
				}
			}
			catch (IOException ex)
			{
				IdeLog.logError(AptanaCorePlugin.getDefault(), StringUtils.format(Messages.InitialStartup_ErrorClosing,
						file.getAbsolutePath()));
			}
		}
		try
		{
			SyncManager.getSyncManager().fromSerializableString(contents.toString());
		}
		finally
		{
			synchronized (lock)
			{
				isBusy = false;
			}
		}
	}

	/**
	 * getSyncManager
	 * 
	 * @return SyncManager
	 */
	public synchronized static SyncManager getSyncManager()
	{
		if (_syncManager == null)
		{
			_syncManager = new SyncManager();

		}
		return _syncManager;
	}

	/**
	 * Save the current workspace state.
	 * The method forces a save for all the items that registered to the sync manager.
	 * 
	 * @throws CoreException
	 */
	public void saveNow()
	{
	}
	
	/**
	 * @param item
	 */
	public void addItem(Object item)
	{
		synchronized (items)
		{
			items.add(item);
		}

		fireSyncManagerChangeEvent(item, ISyncManagerChangeListener.ADD);
	}

	/**
	 * @param item
	 */
	public void removeItem(Object item)
	{
		boolean removed = false;
		synchronized (items)
		{
			removed = items.remove(item);
		}
		if (removed)
		{
			fireSyncManagerChangeEvent(item, ISyncManagerChangeListener.DELETE);
		}
	}

	/**
	 * Return all items
	 * 
	 * @return Object[]
	 */
	public Object[] getItems()
	{
		synchronized (items)
		{
			return items.toArray();
		}
	}

	/**
	 * Return only items that of of the specified type
	 * 
	 * @param type
	 * @return Object[]
	 */
	public Object[] getItems(Class<?> type)
	{
		Object[] allItems = getItems();

		ArrayList<Object> finalList = new ArrayList<Object>();

		for (int i = 0; i < allItems.length; i++)
		{
			Object o = allItems[i];

			if (o.getClass() == type)
			{
				finalList.add(o);
			}
		}

		// Return an array of the type of object passed in
		return finalList.toArray((Object[]) Array.newInstance(type, finalList.size()));
	}

	/**
	 * fireSyncManagerChangeEvent
	 * 
	 * @param o
	 * @param action
	 */
	public void fireSyncManagerChangeEvent(Object o, int action)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			ISyncManagerChangeListener listener = listeners.get(i);
			listener.syncManagerEvent(o, action);
		}
		// Schedule a save job
		if (!isBusy)
		{
			saveJob.schedule();
		}
	}

	/**
	 * addProfileChangeListener
	 * 
	 * @param l
	 */
	public void addSyncManagerChangeEvent(ISyncManagerChangeListener l)
	{
		listeners.add(l);
	}

	/**
	 * removeProfileChangeListener
	 * 
	 * @param l
	 */
	public void removeSyncManagerChangeEvent(ISyncManagerChangeListener l)
	{
		listeners.remove(l);
	}

	/**
	 * toSerializableString
	 * 
	 * @return String
	 */
	public String toSerializableString()
	{
		Object[] allItems = getItems();

		StringBuilder sb = new StringBuilder();

		// First serialize IVirtualFileManager's
		for (int i = 0; i < allItems.length; i++)
		{
			Object o = allItems[i];

			if (o instanceof IVirtualFileManager && o instanceof ISerializableSyncItem)
			{
				if (((IVirtualFileManager) o).isTransient() == false)
				{
					String data = ((ISerializableSyncItem) o).toSerializableString();
					if (data != null)
					{
						sb.append(((ISerializableSyncItem) o).getType());
						sb.append(ISerializableSyncItem.TYPE_DELIMITER);
						sb.append(data);
						sb.append(ISerializableSyncItem.OBJ_DELIMITER);
					}
				}
			}
		}

		for (Iterator<String> iter = unknownItems.iterator(); iter.hasNext();)
		{
			String element = iter.next();
			sb.append(element);
		}

		sb.append(ISerializableSyncItem.SECTION_DELIMITER);

		// Then serialize VirtualFileManagerSyncItem's
		for (int i = 0; i < allItems.length; i++)
		{
			Object o = allItems[i];

			if (o instanceof VirtualFileManagerSyncPair && o instanceof ISerializableSyncItem)
			{
				String data = ((ISerializableSyncItem) o).toSerializableString();

				if (data != null)
				{
					sb.append(((ISerializableSyncItem) o).getType());
					sb.append(ISerializableSyncItem.TYPE_DELIMITER);
					sb.append(data);
					sb.append(ISerializableSyncItem.OBJ_DELIMITER);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * fromSerializableString
	 * 
	 * @param s
	 */
	public void fromSerializableString(String s)
	{
		// for migration purposes
		if (s.indexOf(ISerializableSyncItem.DELIMITER) < 0)
		{
			s = StringUtils.replace(s, "%%%%", ISerializableSyncItem.DELIMITER); //$NON-NLS-1$
			s = StringUtils.replace(s, "@@@@", ISerializableSyncItem.OBJ_DELIMITER); //$NON-NLS-1$
			s = StringUtils.replace(s, "~~~~", ISerializableSyncItem.SECTION_DELIMITER); //$NON-NLS-1$
			s = StringUtils.replace(s, "!!!!", ISerializableSyncItem.TYPE_DELIMITER); //$NON-NLS-1$
			s = StringUtils.replace(s, "}}}}", ISerializableSyncItem.FILE_DELIMITER); //$NON-NLS-1$
		}
		String[] sections = s.split(ISerializableSyncItem.SECTION_DELIMITER);

		if (sections.length > 0)
		{
			parseIVirtualFileManagers(sections[0]);
		}

		if (sections.length > 1)
		{
			parseVirtualFileManagerSyncItems(sections[1]);
		}
	}

	/**
	 * parseIVirtualFileManagers
	 * 
	 * @param s
	 */
	private void parseIVirtualFileManagers(String s)
	{
		Map<String, List<String>> dataTypes = new HashMap<String, List<String>>();

		String[] parts = s.split(ISerializableSyncItem.OBJ_DELIMITER);

		// Place each different type into a dataType 'bucket' for post processing
		for (int i = 0; i < parts.length; i++)
		{
			String item = parts[i];

			String[] itemParts = item.split(ISerializableSyncItem.TYPE_DELIMITER);

			if (itemParts.length == 2)
			{
				String type = itemParts[0];
				String data = itemParts[1];

				if ("null".equals(type)) //$NON-NLS-1$
				{
					continue;
				}

				List<String> list = dataTypes.get(type);
				if (list == null)
				{
					list = new ArrayList<String>();
					dataTypes.put(type, list);
				}
				list.add(data);
			}
		}

		// Now go through each bucket and tell the responsible ProtocolManager to instantiate it
		for (Iterator<String> iter = dataTypes.keySet().iterator(); iter.hasNext();)
		{
			String type = iter.next();

			ProtocolManager manager = ProtocolManager.getProtocolManagerByType(type);

			List<String> dataTypeList = dataTypes.get(type);
			if (manager != null)
			{
				for (Iterator<String> iterator = dataTypeList.iterator(); iterator.hasNext();)
				{
					String data = iterator.next();

					// This will automatically add itself to the SyncManager
					IVirtualFileManager fileManager = manager.getStaticInstance().createFileManager();
					fileManager.fromSerializableString(data);
					IdeLog.logInfo(AptanaCorePlugin.getDefault(),
                            "Loaded virtual file manager node with type " //$NON-NLS-1$
                                    + fileManager.getType() + " and base path " //$NON-NLS-1$
                                    + fileManager.getBasePath());
				}
			}
			else
			{
				String vfm = ""; //$NON-NLS-1$
				for (Iterator<String> iterator = dataTypeList.iterator(); iterator.hasNext();)
				{
					String data = iterator.next();
					vfm += type;
					vfm += ISerializableSyncItem.TYPE_DELIMITER;
					vfm += data;
					vfm += ISerializableSyncItem.OBJ_DELIMITER;
				}

				// save these for later
				unknownItems.add(vfm);
				IdeLog
                        .logInfo(
                                AptanaCorePlugin.getDefault(),
                                "Encountered unknown virtual file manager with type " + type //$NON-NLS-1$
                                        + " when loading"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @param s
	 */
	private void parseVirtualFileManagerSyncItems(String s)
	{
		String[] parts = s.split(ISerializableSyncItem.OBJ_DELIMITER);

		// Place each different type into a dataType 'bucket' for post processing
		for (int i = 0; i < parts.length; i++)
		{
			String item = parts[i];

			String[] itemParts = item.split(ISerializableSyncItem.TYPE_DELIMITER);

			if (itemParts.length == 2)
			{
				String type = itemParts[0];
				String data = itemParts[1];

				if ("null".equals(type)) //$NON-NLS-1$
				{
					continue;
				}

				try
				{
					VirtualFileManagerSyncPair vfm = new VirtualFileManagerSyncPair();
					vfm.fromSerializableString(data);
					items.add(vfm);
					IdeLog.logInfo(AptanaCorePlugin.getDefault(),
                            "Loaded virtual file manager node with type " //$NON-NLS-1$
                                    + vfm.getType() + " and nickname " //$NON-NLS-1$
                                    + vfm.getNickName());
					_syncManager.fireSyncManagerChangeEvent(vfm, ISyncManagerChangeListener.ADD);
				}
				catch (Exception ex)
				{
					IdeLog.logError(AptanaCorePlugin.getDefault(), "Unable to deserialize virtaul file manager", ex); //$NON-NLS-1$
				}

			}
		}

	}

	/**
	 * getType
	 * 
	 * @return String
	 */
	public String getType()
	{
		return "com.aptana.ide.core.io.sync.SyncManager"; //$NON-NLS-1$
	}

	/**
	 * getVirtualFileManagerById
	 * 
	 * @param id
	 * @return IVirtualFileManager
	 */
	public IVirtualFileManager getVirtualFileManagerById(String id)
	{
		Object[] allItems = getItems();

		for (int i = 0; i < allItems.length; i++)
		{
			Object o = allItems[i];

			if (o instanceof IVirtualFileManager)
			{
				IVirtualFileManager vfm = (IVirtualFileManager) o;

				if (vfm.getId() == id)
				{
					return vfm;
				}

			}
		}

		return null;
	}

	/**
	 * Returns all sync pairs relevant to this file (i.e. they contain a virtual file manager as an endpoint that
	 * contains this file)
	 * 
	 * @param file
	 * @return VirtualFileManagerSyncPair[]
	 */
	public static VirtualFileManagerSyncPair[] getContainingSyncPairs(IVirtualFile file)
	{
		List<VirtualFileManagerSyncPair> mySyncConfigurations = new ArrayList<VirtualFileManagerSyncPair>();
		List<IVirtualFileManager> relevantManagers = new ArrayList<IVirtualFileManager>();

		// Add project protocol managers
		IVirtualFileManager[] fms = getContainingFileManagers(file);
		relevantManagers.addAll(Arrays.asList(fms));

		Object[] scs = SyncManager.getSyncManager().getItems(VirtualFileManagerSyncPair.class);

		for (int i = 0; i < scs.length; i++)
		{
			VirtualFileManagerSyncPair configuration = (VirtualFileManagerSyncPair) scs[i];

			if (relevantManagers.contains(configuration.getSourceFileManager()))
			{
				mySyncConfigurations.add(configuration);
			}
		}

		return mySyncConfigurations.toArray(new VirtualFileManagerSyncPair[mySyncConfigurations.size()]);
	}
	
	/**
	 * Returns all sync pairs relevant to this file (i.e. they contain a virtual file manager as an endpoint that
	 * contains this file)
	 * 
	 * @param file The file to search for
	 * @param valid Only return valid sync pairs
	 * @return VirtualFileManagerSyncPair[]
	 */
	public static VirtualFileManagerSyncPair[] getContainingSyncPairs(IVirtualFile file, boolean valid)
	{
		VirtualFileManagerSyncPair[] confs = getContainingSyncPairs(file);

		if(valid)
		{
			List<VirtualFileManagerSyncPair> validPairs = new ArrayList<VirtualFileManagerSyncPair>();
			for (int i = 0; i < confs.length; i++)
			{
				VirtualFileManagerSyncPair virtualFileManagerSyncPair = confs[i];
				if(virtualFileManagerSyncPair.isValid())
				{
					validPairs.add(virtualFileManagerSyncPair);
				}
			}
			
			return (VirtualFileManagerSyncPair[])validPairs
			.toArray(new VirtualFileManagerSyncPair[0]);
		}
		else
		{
			return confs;
		}
	}

	/**
	 * Is this file manager connected to Sync Configuration?
	 * 
	 * @param fileManager
	 * @return boolean
	 */
	public static boolean isSyncPairEndpoint(IVirtualFileManager fileManager)
	{
		return getSyncPairs(fileManager).length > 0;
	}

	/**
	 * Return all sync configurations where this file manager is an endpoint
	 * 
	 * @param fileManager
	 * @return boolean
	 */
	public static VirtualFileManagerSyncPair[] getSyncPairs(IVirtualFileManager fileManager)
	{
		Object[] scs = SyncManager.getSyncManager().getItems(VirtualFileManagerSyncPair.class);
		List<VirtualFileManagerSyncPair> syncPairs = new ArrayList<VirtualFileManagerSyncPair>();
		for (int i = 0; i < scs.length; i++)
		{
			VirtualFileManagerSyncPair object = (VirtualFileManagerSyncPair) scs[i];
			if ((object.getDestinationFileManager() != null && object.getDestinationFileManager().equals(fileManager))
					|| object.getSourceFileManager() != null && object.getSourceFileManager().equals(fileManager))
			{
				syncPairs.add(object);
			}
		}

		return syncPairs.toArray(new VirtualFileManagerSyncPair[0]);
	}

	/**
	 * Is this file encapsulated by a Sync Configuration?
	 * 
	 * @param file
	 * @return boolean
	 */
	public static boolean hasContainingSyncPair(IVirtualFile file)
	{
		VirtualFileManagerSyncPair[] confs = getContainingSyncPairs(file);
		return confs.length > 0;
	}

	/**
	 * Returns all File Managers that encapsulate this file
	 * 
	 * @param file
	 * @return IVirtualFileManager[]
	 */
	public static IVirtualFileManager[] getContainingFileManagers(IVirtualFile file)
	{
		List<Object> relevantManagers = new ArrayList<Object>();

		Object[] objs = SyncManager.getSyncManager().getItems();
		for (int i = 0; i < objs.length; i++)
		{
			Object object = objs[i];
			if (object instanceof IVirtualFileManager)
			{
				if (((IVirtualFileManager) object).containsFile(file))
				{
					relevantManagers.add(object);
				}
			}
		}

		return relevantManagers.toArray(new IVirtualFileManager[0]);
	}

	/**
	 * Returns true if this item is the same as the base file of some virtual file manager
	 * 
	 * @param file
	 * @return boolean
	 */
	public static boolean isVirtualFileManager(IVirtualFile file)
	{
		Object[] objs = SyncManager.getSyncManager().getItems();
		for (int i = 0; i < objs.length; i++)
		{
			Object object = objs[i];
			if (object instanceof IVirtualFileManager)
			{
				IVirtualFileManager fileManager = ((IVirtualFileManager) object);
				if (fileManager.getBasePath() != null && fileManager.getBaseFile().equals(file))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns all File Managers that encapsulate this file
	 * 
	 * @param protocolManager
	 * @param file
	 * @return IVirtualFileManager[]
	 */
	public static IVirtualFileManager[] getContainingFileManagers(ProtocolManager protocolManager, IVirtualFile file)
	{
		List<IVirtualFileManager> relevantManagers = new ArrayList<IVirtualFileManager>();
		IVirtualFileManager[] fms = protocolManager.getFileManagers();
		for (int i = 0; i < fms.length; i++)
		{
			IVirtualFileManager manager = fms[i];
			if (manager.containsFile(file))
			{
				relevantManagers.add(manager);
			}
		}
		return relevantManagers.toArray(new IVirtualFileManager[0]);
	}
	
	/**
	 * A sync data saving job. In case that the sync manager is busy loading or saving other settings at the moment,
	 * ignore that save request.
	 */
	private static class SaveJob extends Job
	{
		SaveJob()
		{
			super("Save sync data job");//$NON-NLS-1$
		}

		protected IStatus run(IProgressMonitor monitor)
		{
			SyncManager syncManager = SyncManager.getSyncManager();
			if (!syncManager.isBusy)
			{
				syncManager.saveNow();
				return Status.OK_STATUS;
			}
			return Status.CANCEL_STATUS;
		}
	}
}
