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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.sync.SyncManager;
import com.aptana.ide.core.io.sync.VirtualFileManagerSyncPair;
import com.aptana.ide.core.ui.SetUtils;
import com.aptana.ide.core.ui.io.file.ProjectFileManager;

/**
 * This is a utility class for synchronization related functionality and
 * settings for projects.
 * 
 * @author Sandip V. Chitale (schitale@aptana.com)
 */
public class ProjectSynchronizationUtils {

	public static final String LAST_SYNC_CONNECTION_KEY = "lastSyncConnection"; //$NON-NLS-1$
	public static final String LAST_CLOUD_SYNC_CONNECTION_KEY = "lastCloudSyncConnection"; //$NON-NLS-1$
	public static final String REMEMBER_DECISION_KEY = "rememberDecision"; //$NON-NLS-1$
	
	private static List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
		
	public static void addPropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.add(listener);
	}

	public static void removePropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.remove(listener);
	}

	private static void firePropertyChange(IProject project, String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEvent propertyChangeEvent = 
			new PropertyChangeEvent(project, propertyName, oldValue, newValue);
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(propertyChangeEvent);
		}
	}

	/**
	 * Returns the value of "Remember my decision" setting which indicate
	 * whether to show the Choose Synchronization connection dialog when
	 * multiple connections are associated with the project.
	 * 
	 * @param project
	 * @return
	 * 
	 * @throws NullPointerException
	 *             if the specified project is null
	 */
	public static boolean isRememberDecision(IProject project) {
		if (project == null) {
			throw new NullPointerException("Null project."); //$NON-NLS-1$
		}

		try {
			String remeberMyDecisionStringValue = project
					.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
							ProjectSynchronizationUtils.REMEMBER_DECISION_KEY));
			if (remeberMyDecisionStringValue != null) {
				return Boolean.TRUE.toString().equals(
						remeberMyDecisionStringValue);
			}
		} catch (CoreException e) {
		}
		return false;
	}

	/**
	 * Sets the value of "Remember my decision" setting which indicate whether
	 * to show the Choose Synchronization connection dialog when multiple
	 * connections are associated with the project.
	 * 
	 * @param project
	 * @param rememberMyDecision
	 * @throws NullPointerException
	 *             if the specified project is null
	 */
	public static void setRememberDecision(IProject project,
			boolean rememberMyDecision) {
		if (project == null) {
			throw new NullPointerException("Null project."); //$NON-NLS-1$
		}

		try {
			project.setPersistentProperty(new QualifiedName("", //$NON-NLS-1$
					ProjectSynchronizationUtils.REMEMBER_DECISION_KEY), String
					.valueOf(rememberMyDecision));
		} catch (CoreException e) {
		}
	}

	/**
	 * Returns the last synchronization connection in a serialized form.
	 * 
	 * @param project
	 * @return the last synchronization connection in a serialized form.
	 * @throws NullPointerException
	 *             if the specified project is null
	 */
	public static String getLastSyncConnection(IProject project) {
		if (project == null) {
			throw new NullPointerException("Null project."); //$NON-NLS-1$
		}

		try {
			return project.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
					ProjectSynchronizationUtils.LAST_SYNC_CONNECTION_KEY));
		} catch (CoreException e) {
		}
		return null;
	}

	/**
	 * Sets the value of last synchronization connection.
	 * 
	 * @param project
	 * @param lastSyncConnectionSerializableString
	 *            last cloud synchronization connection's serializable string. 
	 *            A <code>null</code> or <code>""</code> removes the
	 *            persistent setting.
	 * @throws NullPointerException
	 *             if the specified project is null
	 */
	public static void setLastSyncConnection(IProject project,
			String lastSyncConnectionSerializableString) {
		try {
			if (lastSyncConnectionSerializableString == null
					|| lastSyncConnectionSerializableString.equals("")) { //$NON-NLS-1$
				project.setPersistentProperty(new QualifiedName("", //$NON-NLS-1$
						ProjectSynchronizationUtils.LAST_SYNC_CONNECTION_KEY), null);
			} else {
				project.setPersistentProperty(new QualifiedName("", //$NON-NLS-1$
						ProjectSynchronizationUtils.LAST_SYNC_CONNECTION_KEY),
						lastSyncConnectionSerializableString);
			}
		} catch (CoreException e) {
		}
	}

	/**
	 * Returns the last Cloud synchronization connection in a serialized form.
	 * 
	 * @param project
	 * @return the last synchronization connection in a serialized form.
	 * @throws NullPointerException
	 *             if the specified project is null
	 */
	public static String getLastCloudSyncConnection(IProject project) {
		if (project == null) {
			throw new NullPointerException("Null project."); //$NON-NLS-1$
		}

		try {
			return project.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
					ProjectSynchronizationUtils.LAST_CLOUD_SYNC_CONNECTION_KEY));
		} catch (CoreException e) {
		}
		return null;
	}

	/**
	 * Sets the value of last synchronization connection.
	 * 
	 * @param project
	 * @param lastCloudSyncConnectionSerializableString
	 *            last synchronization connection's serializable string. 
	 *            A <code>null</code> or <code>""</code> removes the
	 *            persistent setting.
	 * @throws NullPointerException
	 *             if the specified project is null
	 */
	public static void setLastCloudSyncConnection(IProject project,
			String lastCloudSyncConnectionSerializableString) {
		if (project == null) {
			throw new NullPointerException("Null project."); //$NON-NLS-1$
		}

		String oldLastCloudSyncConnectionSerializableString = getLastCloudSyncConnection(project);
		try {
			if (lastCloudSyncConnectionSerializableString == null
					|| lastCloudSyncConnectionSerializableString.equals("")) { //$NON-NLS-1$
				project.setPersistentProperty(new QualifiedName("", //$NON-NLS-1$
						ProjectSynchronizationUtils.LAST_CLOUD_SYNC_CONNECTION_KEY), null);
			} else {
				project.setPersistentProperty(new QualifiedName("", //$NON-NLS-1$
						ProjectSynchronizationUtils.LAST_CLOUD_SYNC_CONNECTION_KEY),
						lastCloudSyncConnectionSerializableString);
			}
			firePropertyChange(project, LAST_CLOUD_SYNC_CONNECTION_KEY,
					oldLastCloudSyncConnectionSerializableString, lastCloudSyncConnectionSerializableString);
		} catch (CoreException e) {
		}
	}
	
	public static final String COM_APTANA_IDE_SERVER_CLOUD_SYNCING_CLOUD_VIRTUAL_FILE_MANAGER = "com.aptana.ide.server.cloud.syncing.CloudVirtualFileManager"; //$NON-NLS-1$

	/**
	 * Return if the specified synchronization connection has a Cloud
	 * destination end point.
	 * 
	 * @param conf
	 * @return
	 * @throws NullPointerException
	 *             if the specified connection is null
	 */
	public static boolean isCloudConnection(VirtualFileManagerSyncPair conf)
	{
		if (conf == null)
		{
			return false;
		}
		IVirtualFileManager destinationFileManager = conf
				.getDestinationFileManager();
		return destinationFileManager.getClass().getName().equals(
				COM_APTANA_IDE_SERVER_CLOUD_SYNCING_CLOUD_VIRTUAL_FILE_MANAGER);
	}
	
	
	public static VirtualFileManagerSyncPair getPublicCloudSyncConnection(IProject project) {
		IVirtualFile projectVirtualFile = ProjectFileManager.convertResourceToFile(project);
		if (projectVirtualFile != null) {
			VirtualFileManagerSyncPair[] virtualFileManagerSyncPairs = getVirtualFileManagerSyncPairs(new IVirtualFile[] { projectVirtualFile });
			for (VirtualFileManagerSyncPair virtualFileManagerSyncPair : virtualFileManagerSyncPairs)
			{
				if (isCloudConnection(virtualFileManagerSyncPair)) {
					if (getCloudDomain(virtualFileManagerSyncPair) == CLOUD_DOMAIN.PUBLIC)
					{
						return virtualFileManagerSyncPair;
					}
				}
			}
		}
		return null;
	}
	
	public static VirtualFileManagerSyncPair getStagingCloudSyncConnection(IProject project) {
		IVirtualFile projectVirtualFile = ProjectFileManager.convertResourceToFile(project);
		if (projectVirtualFile != null) {
			VirtualFileManagerSyncPair[] virtualFileManagerSyncPairs = getVirtualFileManagerSyncPairs(new IVirtualFile[] { projectVirtualFile });
			for (VirtualFileManagerSyncPair virtualFileManagerSyncPair : virtualFileManagerSyncPairs)
			{
				if (isCloudConnection(virtualFileManagerSyncPair)) {
					if (getCloudDomain(virtualFileManagerSyncPair) == CLOUD_DOMAIN.STAGING)
					{
						return virtualFileManagerSyncPair;
					}
				}
			}
		}
		return null;
	}
	
	public static String toSerializableString(VirtualFileManagerSyncPair virtualFileManagerSyncPair)
	{
		if (isCloudConnection(virtualFileManagerSyncPair))
		{
			return COM_APTANA_IDE_SERVER_CLOUD_SYNCING_CLOUD_VIRTUAL_FILE_MANAGER
			+ "|" //$NON-NLS-1$
			+ virtualFileManagerSyncPair.getDestinationFileManager().getNickName();
		}
		return virtualFileManagerSyncPair.toSerializableString();
	}
	
	public static VirtualFileManagerSyncPair fromSerializableString(IProject project, String serializableString)
	{
		if (serializableString.startsWith(COM_APTANA_IDE_SERVER_CLOUD_SYNCING_CLOUD_VIRTUAL_FILE_MANAGER))
		{
			String nickName = serializableString.substring(COM_APTANA_IDE_SERVER_CLOUD_SYNCING_CLOUD_VIRTUAL_FILE_MANAGER.length() + 1); // 1 for the |
			IVirtualFile projectVirtualFile = ProjectFileManager.convertResourceToFile(project);
			if (projectVirtualFile != null) {
				VirtualFileManagerSyncPair[] virtualFileManagerSyncPairs = getVirtualFileManagerSyncPairs(new IVirtualFile[] { projectVirtualFile });
				for (VirtualFileManagerSyncPair virtualFileManagerSyncPair : virtualFileManagerSyncPairs)
				{
					if (isCloudConnection(virtualFileManagerSyncPair)) {
						if (nickName.equals(virtualFileManagerSyncPair.getDestinationFileManager().getNickName()))
						{
							return virtualFileManagerSyncPair;
						}
					}
				}
			}
		} else {
			VirtualFileManagerSyncPair virtualFileManagerSyncPair = new VirtualFileManagerSyncPair();
			virtualFileManagerSyncPair.fromSerializableString(serializableString);
			if (virtualFileManagerSyncPair.isValid())
			{
				return virtualFileManagerSyncPair;
			}
		}
		return null;
	}
	
	public enum CLOUD_DOMAIN { PUBLIC, STAGING };
	
	public static CLOUD_DOMAIN getCloudDomain(VirtualFileManagerSyncPair virtualFileManagerSyncPair)
	{
		assert virtualFileManagerSyncPair != null;
		if (!isCloudConnection(virtualFileManagerSyncPair))
		{
			throw new IllegalArgumentException("Not a cloud connection: " + virtualFileManagerSyncPair.getNickName()); //$NON-NLS-1$
		}
		
		IVirtualFileManager destinationFileManager = virtualFileManagerSyncPair.getDestinationFileManager();
		if (destinationFileManager.getNickName().endsWith("(staging)")) //$NON-NLS-1$
		{
			return CLOUD_DOMAIN.STAGING;
		}
		
		return CLOUD_DOMAIN.PUBLIC;
	}
	
	/**
	 * getVirtualFileManagerSyncPair
	 * 
	 * @param files
	 * @return VirtualFileManagerSyncPair
	 */
	@SuppressWarnings("unchecked")
	private static VirtualFileManagerSyncPair[] getVirtualFileManagerSyncPairs(
			IVirtualFile[] files)
	{
		List<Set<VirtualFileManagerSyncPair>> syncSets = new ArrayList<Set<VirtualFileManagerSyncPair>>();
		for (IVirtualFile file : files) {
			VirtualFileManagerSyncPair[] confs = SyncManager
					.getContainingSyncPairs(file, true);
			Set<VirtualFileManagerSyncPair> newSet = new HashSet<VirtualFileManagerSyncPair>();
			newSet.addAll(Arrays.asList(confs));
			syncSets.add(newSet);
		}

		Set<Object>[] array = syncSets.toArray(new Set[syncSets.size()]);
		Set<Object> intersection = SetUtils.getIntersection(array);
		VirtualFileManagerSyncPair[] confs = (VirtualFileManagerSyncPair[]) intersection
				.toArray(new VirtualFileManagerSyncPair[0]);

		return confs;
	}
}
