/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.intro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.update.FeatureUtil;
import com.aptana.ide.update.manager.IPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class FeatureChangeManager
{

	private static FeatureChangeManager manager = null;

	/**
	 * FEATURE_STORE_PATH
	 */
	public static final String FEATURE_STORE_PATH = ".features"; //$NON-NLS-1$

	private List<FeatureChange> changeList;
	private boolean changed;

	private FeatureChangeManager()
	{

		List<IPlugin> plugins = FeatureUtil.getInstalledFeatures();
		try
		{
			List<FeatureChange> changes = compareToStore(plugins);
			changed = !changes.isEmpty();
			if (changed)
			{
				this.changeList = changes;
				updateStore(plugins);
			}
			else
			{
				this.changeList = null;
			}
		}
		catch (Exception e)
		{
			IdeLog.logInfo(IntroPlugin.getDefault(), e.getMessage());
			// Error so don't bring updates to the user, reset until next time
			updateStore(plugins);
			changed = false;
			this.changeList = null;
		}
	}

	/**
	 * Gets the feature change manager
	 * 
	 * @return - manager instance
	 */
	public synchronized static FeatureChangeManager getManager()
	{
		if (manager == null)
		{
			manager = new FeatureChangeManager();
		}
		return manager;
	}

	/**
	 * True if features have changed since last startup
	 * 
	 * @return - true if features changed and a changelist will be available
	 */
	public boolean areFeaturesChanged()
	{
		return changed;
	}

	/**
	 * Gets the feature change list if changes are present
	 * 
	 * @return - List of FeatureChanges
	 */
	public List<FeatureChange> getFeatureChangeList()
	{
		return this.changeList;
	}

	private void updateStore(List<IPlugin> plugins)
	{
		HashMap<String, String> newIdsToVersion = new HashMap<String, String>();
		for (IPlugin ref : plugins)
		{
			newIdsToVersion.put(ref.getId(), ref.getVersion());
			IdeLog.logInfo(IntroPlugin.getDefault(), MessageFormat.format(Messages.FeatureChangeManager_INF_Feature,
					ref.getId(), ref.getVersion()));
		}
		saveFeatureInformation(newIdsToVersion);
	}

	private List<FeatureChange> compareToStore(List<IPlugin> plugins) throws FileNotFoundException, IOException,
			ClassNotFoundException
	{
		Map<String, String> idToVersions = getStoredVersions();
		List<FeatureChange> changes = new ArrayList<FeatureChange>();
		for (IPlugin ref : plugins)
		{
			String currentVersion = ref.getVersion();
			FeatureChange change = null;
			if (idToVersions.containsKey(ref.getId()))
			{
				String previous = (String) idToVersions.get(ref.getId());
				if (!previous.equals(currentVersion))
				{
					change = new FeatureChange(ref.getId(), ref.getName(), previous, currentVersion, getProvider(ref));
				}
			}
			else
			{
				// Add as a newly installed feature
				change = new FeatureChange(ref.getId(), ref.getName(), null, currentVersion, getProvider(ref));
			}
			if (change != null)
			{
				changes.add(change);
			}
		}
		return changes;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getStoredVersions() throws IOException, ClassNotFoundException
	{
		File bundleFile = IntroPlugin.getDefault().getStateLocation().append(FEATURE_STORE_PATH).toFile();
		if (!bundleFile.exists())
			throw new IOException("Feature store does not exist"); //$NON-NLS-1$
		ObjectInputStream ois = null;
		try
		{
			ois = new ObjectInputStream(new FileInputStream(bundleFile));
			Object obj = ois.readObject();
			if (obj instanceof Map<?, ?>)
			{
				return (Map<String, String>) obj;
			}
			else
				throw new IOException("File empty or corrupted"); //$NON-NLS-1$
		}
		finally
		{
			if (ois != null)
				ois.close();
		}
	}

	/**
	 * This is a hack so that our plugins show as Aptana as provider since we don't retain that info in IPlugin right
	 * now.
	 * 
	 * @param ref
	 * @return
	 */
	private String getProvider(IPlugin ref)
	{
		if (ref == null || ref.getId() == null)
			return "";
		if (ref.getId().contains("aptana") || ref.getId().contains("radrails") || ref.getId().contains("rubypeople")
				|| ref.getId().contains("pydev"))
			return "Aptana, Inc.";
		return "";
	}

	private void saveFeatureInformation(HashMap<String, String> idToVersions)
	{
		ObjectOutputStream oos = null;
		try
		{
			if (IntroPlugin.getDefault() != null && IntroPlugin.getDefault().getStateLocation() != null)
			{
				File bundleFile = IntroPlugin.getDefault().getStateLocation().append(FEATURE_STORE_PATH).toFile();
				if (!bundleFile.exists())
				{
					bundleFile.createNewFile();
				}
				IdeLog.logInfo(IntroPlugin.getDefault(), Messages.FeatureChangeManager_INF_PersistList);
				oos = new ObjectOutputStream(new FileOutputStream(bundleFile));
				oos.writeObject(idToVersions);
				oos.flush();
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(IntroPlugin.getDefault(), e.getMessage());
		}
		finally
		{
			try
			{
				if (oos != null)
					oos.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}
}
