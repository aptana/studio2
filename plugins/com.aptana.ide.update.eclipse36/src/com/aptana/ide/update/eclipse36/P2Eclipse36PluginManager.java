/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.update.eclipse36;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.p2.ui.ProvUIActivator;
import org.eclipse.equinox.internal.p2.ui.sdk.scheduler.AutomaticUpdatePlugin;
import org.eclipse.equinox.internal.p2.ui.sdk.scheduler.PreferenceConstants;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.UninstallOperation;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.internal.update.manager.AbstractPluginManager;
import com.aptana.ide.update.manager.IPlugin;
import com.aptana.ide.update.manager.Plugin;
import com.aptana.ide.update.manager.PluginManagerException;

@SuppressWarnings("restriction")
public class P2Eclipse36PluginManager extends AbstractPluginManager
{
	public P2Eclipse36PluginManager()
	{
	}

	/**
	 * @see com.aptana.ide.update.manager.IPluginManager#addUpdateSite(java.net.URL)
	 */
	public boolean addUpdateSite(URL siteURL)
	{
		try
		{
			URI siteURI = siteURL.toURI();

			ProvisioningUI ui = ProvUIActivator.getDefault().getProvisioningUI();
			URI[] existingMetaRepos = ui.getRepositoryTracker().getKnownRepositories(null);
			if (contains(existingMetaRepos, siteURI))
			{
				return false;
			}
			ui.getRepositoryTracker().addRepository(siteURI, null, null);
			return true;
		}
		catch (Exception e)
		{
			// ignores the exception
		}
		return false;
	}

	/**
	 * @see com.aptana.ide.update.manager.IPluginManager#removeUpdateSite(java.net.URL)
	 */
	public void removeUpdateSite(URL siteURL)
	{
		try
		{
			URI siteURI = siteURL.toURI();

			ProvisioningUI ui = ProvUIActivator.getDefault().getProvisioningUI();
			ui.getRepositoryTracker().removeRepositories(new URI[] { siteURI }, null);
		}
		catch (Exception e)
		{
			// ignores the exception
		}
	}

	/**
	 * @see com.aptana.ide.update.manager.IPluginManager#checkForUpdates(boolean)
	 */
	public void checkForUpdates(boolean immediate)
	{
		if (!immediate)
		{
			AutomaticUpdatePlugin.getDefault().getScheduler().earlyStartup();
			return;
		}
		// Force the P2 automatic update check pref to be turned on, and force a
		// reschedule of the update check
		IPreferenceStore prefs = AutomaticUpdatePlugin.getDefault().getPreferenceStore();
		// Grab existing values
		boolean wasEnabled = prefs.getBoolean(PreferenceConstants.PREF_AUTO_UPDATE_ENABLED);
		String oldSchedule = prefs.getString(PreferenceConstants.PREF_AUTO_UPDATE_SCHEDULE);
		// Force new ones temporarily
		prefs.setValue(PreferenceConstants.PREF_AUTO_UPDATE_ENABLED, true);
		prefs.setValue(PreferenceConstants.PREF_AUTO_UPDATE_SCHEDULE, PreferenceConstants.PREF_UPDATE_ON_STARTUP);
		AutomaticUpdatePlugin.getDefault().savePreferences();
		// now check for updates
		AutomaticUpdatePlugin.getDefault().getScheduler().earlyStartup();
		// Now revert prefs
		prefs.setValue(PreferenceConstants.PREF_AUTO_UPDATE_ENABLED, wasEnabled);
		prefs.setValue(PreferenceConstants.PREF_AUTO_UPDATE_SCHEDULE, oldSchedule);
		AutomaticUpdatePlugin.getDefault().savePreferences();
	}

	/**
	 * @see com.aptana.ide.update.manager.IPluginManager#install(Plugin[], IProgressMonitor)
	 */
	public IStatus install(final IPlugin[] plugins, IProgressMonitor monitor) throws PluginManagerException
	{
		if (monitor == null)
		{
			monitor = new NullProgressMonitor();
		}
		if (monitor.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		String profileId = IProfileRegistry.SELF;
		IInstallableUnit[] toInstall = getInstallationUnits(plugins, profileId);
		if (toInstall.length <= 0)
		{
			throw new PluginManagerException(P2Eclipse36Messages.P2PluginManager_ERR_MSG_No_installable_units_found);
		}

		if (monitor.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		Collection<IInstallableUnit> units = Arrays.asList(toInstall);
		ProvisioningUI ui = ProvUIActivator.getDefault().getProvisioningUI();
		InstallOperation op = ui.getInstallOperation(units, getURIs(plugins));
		ui.openInstallWizard(units, op, null);

		return Status.OK_STATUS;
	}

	private URI[] getURIs(IPlugin[] plugins)
	{
		List<URI> uris = new ArrayList<URI>();
		for (IPlugin plugin : plugins)
		{
			try
			{
				uris.add(plugin.getURL().toURI());
			}
			catch (URISyntaxException e)
			{
				IdeLog.logError(Activator.getDefault(), e.getMessage(), e);
			}
		}
		return uris.toArray(new URI[0]);
	}

	/**
	 * @see com.aptana.ide.update.manager.IPluginManager#uninstall(IPlugin, IProgressMonitor)
	 */
	public IStatus uninstall(IPlugin plugin, IProgressMonitor monitor) throws PluginManagerException
	{
		if (monitor.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		IQueryResult<IInstallableUnit> result = null;
		try
		{
			IQuery<IInstallableUnit> query = QueryUtil.createIUQuery(getFeatureGroupName(plugin),
					Version.parseVersion(plugin.getVersion()));
			IProfile profile = getProfile(IProfileRegistry.SELF);
			result = profile.query(query, monitor);
		}
		catch (CoreException e)
		{
			IdeLog.logError(Activator.getDefault(), e.getMessage(), e);
		}

		if (result == null || result.isEmpty())
		{
			throw new PluginManagerException(P2Eclipse36Messages.P2PluginManager_ERR_MSG_No_installable_units_found);
		}
		final IInstallableUnit[] ius = result.toArray(IInstallableUnit.class);

		if (monitor.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		ProvisioningUI ui = ProvUIActivator.getDefault().getProvisioningUI();
		Collection<IInstallableUnit> units = Arrays.asList(ius);
		UninstallOperation op = ui.getUninstallOperation(units, null);
		ui.openUninstallWizard(units, op, null);

		return Status.OK_STATUS;
	}

	protected static IProfile getProfile(String self)
	{
		IProfileRegistry profileRegistry = getProfileRegistry();
		if (profileRegistry == null)
			return null;

		return profileRegistry.getProfile(self);
	}

	private static boolean contains(URI[] existingMetaRepos, URI updateSiteURI)
	{
		if (existingMetaRepos == null)
		{
			return false;
		}
		for (int i = 0; i < existingMetaRepos.length; ++i)
		{
			if (existingMetaRepos[i].equals(updateSiteURI))
			{
				return true;
			}
		}
		return false;
	}

	private static IInstallableUnit[] getInstallationUnits(final IPlugin[] plugins, final String profileId)
			throws PluginManagerException
	{
		final List<IInstallableUnit> units = new ArrayList<IInstallableUnit>();

		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				SubMonitor sub = SubMonitor.convert(monitor, plugins.length * 4);
				sub.setTaskName(P2Eclipse36Messages.P2PluginManager_Locating_selected_features_job_title);
				try
				{
					for (IPlugin plugin : plugins)
					{
						URI siteURL = plugin.getURL().toURI();

						IMetadataRepositoryManager manager = getMetadataRepositoryManager();
						IMetadataRepository repo = manager.loadRepository(siteURL, sub.newChild(1));
						if (repo == null)
						{
							throw new ProvisionException(
									P2Eclipse36Messages.P2PluginManager_ERR_MSG_Metadata_repo_not_found + siteURL);
						}
						if (!manager.isEnabled(siteURL))
						{
							manager.setEnabled(siteURL, true);
						}

						IArtifactRepositoryManager artifactManager = getArtifactRepositoryManager();
						IArtifactRepository artifactRepo = artifactManager.loadRepository(siteURL, sub.newChild(1));
						if (artifactRepo == null)
						{
							throw new ProvisionException(
									P2Eclipse36Messages.P2PluginManager_ERR_MSG_Artifact_repo_not_found + siteURL);
						}
						if (!artifactManager.isEnabled(siteURL))
						{
							artifactManager.setEnabled(siteURL, true);
						}

						IQuery<IInstallableUnit> query = QueryUtil.createIUQuery(getFeatureGroupName(plugin));
						query = QueryUtil.createLatestQuery(query);
						IQueryResult<IInstallableUnit> roots = repo.query(query, sub.newChild(2));

						if (roots.isEmpty())
						{
							if (monitor.isCanceled())
							{
								return;
							}

							IProfile profile = getProfile(profileId);
							if (profile == null)
							{
								profile = getFirstProfile();
							}
							roots = profile.query(query, sub.newChild(2));
						}
						units.addAll(roots.toUnmodifiableSet());
					}
				}
				catch (Exception e)
				{
					throw new InvocationTargetException(e);
				}
				finally
				{
					sub.done();
				}
			}
		};
		try
		{
			new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(true, true, runnable);
		}
		catch (InterruptedException e)
		{
			// don't report thread interruption
		}
		catch (InvocationTargetException e)
		{
			throw new PluginManagerException(P2Eclipse36Messages.ProfileModificationAction_UnexpectedError,
					e.getCause());
		}
		return units.toArray(new IInstallableUnit[units.size()]);
	}

	protected static IArtifactRepositoryManager getArtifactRepositoryManager()
	{
		return (IArtifactRepositoryManager) getAgent().getService(IArtifactRepositoryManager.SERVICE_NAME);
	}

	protected static IMetadataRepositoryManager getMetadataRepositoryManager()
	{
		return (IMetadataRepositoryManager) getAgent().getService(IMetadataRepositoryManager.SERVICE_NAME);
	}

	private static IProvisioningAgent getAgent()
	{
		return (IProvisioningAgent) ServiceHelper.getService(Activator.getContext(), IProvisioningAgent.SERVICE_NAME);
	}
	
	protected static IProfileRegistry getProfileRegistry()
	{
		return (IProfileRegistry) getAgent().getService(IProfileRegistry.SERVICE_NAME);
	}

	private static String getFeatureGroupName(IPlugin plugin) throws CoreException
	{
		return plugin.getId() + FEATURE_IU_SUFFIX;
	}

	public boolean isFeatureInstalled(String id)
	{
		return getInstalledFeature(id) != null;
	}

	public IPlugin getInstalledFeature(String id)
	{
		if (id == null)
			return null;

		if (!id.endsWith(FEATURE_IU_SUFFIX))
		{
			id += FEATURE_IU_SUFFIX;
		}
		IProfileRegistry profileRegistry = getProfileRegistry();
		if (profileRegistry == null)
			return null;

		IProfile profile = getProfile(IProfileRegistry.SELF);
		if (profile == null)
		{
			profile = getFirstProfile();
		}
		if (profile == null)
			return null;

		IQuery<IInstallableUnit> query = QueryUtil.createIUQuery(id);
		query = QueryUtil.createLimitQuery(query, 1);

		IQueryResult<IInstallableUnit> roots = profile.available(query, new NullProgressMonitor());
		if (roots == null || roots.isEmpty())
			return null;
		try
		{
			return toPlugin((IInstallableUnit) roots.iterator().next());
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(Activator.getDefault(), e.getMessage(), e);
		}
		return null;
	}

	private static IProfile getFirstProfile()
	{
		IProfileRegistry profileRegistry = getProfileRegistry();
		if (profileRegistry == null)
		{
			return null;
		}

		IProfile[] profiles = profileRegistry.getProfiles();
		if (profiles != null && profiles.length > 0)
		{
			return profiles[0];
		}
		return null;
	}

	public List<IPlugin> getInstalledPlugins()
	{
		IProfileRegistry profileRegistry = getProfileRegistry();
		if (profileRegistry == null)
			return Collections.emptyList();
		IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
		if (profile == null)
		{
			IProfile[] profiles = profileRegistry.getProfiles();

			if (profiles != null && profiles.length > 0)
			{
				profile = profiles[0];
			}
		}
		if (profile == null)
			return Collections.emptyList();
		IQuery<IInstallableUnit> query = QueryUtil.createIUGroupQuery();
		IQueryResult<IInstallableUnit> roots = profile.available(query, new NullProgressMonitor());
		Iterator<IInstallableUnit> iter = roots.iterator();
		// Convert them to Plugin objects
		List<IPlugin> plugins = new ArrayList<IPlugin>();
		while (iter.hasNext())
		{
			IInstallableUnit unit = iter.next();
			String name = unit.getId();
			// limit to features only...
			if (name == null || !name.endsWith(FEATURE_IU_SUFFIX))
			{
				continue;
			}

			try
			{
				plugins.add(toPlugin(unit));
			}
			catch (MalformedURLException e)
			{
				IdeLog.logError(Activator.getDefault(), e.getMessage(), e);
			}
		}
		return plugins;
	}

	public String getUpdatePreferencePageId()
	{
		return "org.eclipse.equinox.internal.p2.ui.sdk.scheduler.AutomaticUpdatesPreferencePage"; //$NON-NLS-1$
	}

	private Plugin toPlugin(IInstallableUnit iu) throws MalformedURLException
	{
		String name = iu.getProperty("df_LT.featureName"); //$NON-NLS-1$
		if (name == null)
			name = iu.getProperty(IInstallableUnit.PROP_NAME);
		if (name == null)
			name = iu.getId();
		return new Plugin(stripFeatureGroup(iu.getId()), name, iu.getVersion().toString(), null,
				iu.getProperty("df_LT.description"), new URL("file:/fake/" + iu.getId()), "", "", 0, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				null, "", null); //$NON-NLS-1$
	}

	private String stripFeatureGroup(String id)
	{
		if (id == null)
			return null;
		if (id.endsWith(FEATURE_IU_SUFFIX))
			return id.substring(0, id.length() - FEATURE_IU_SUFFIX.length());
		return id;
	}

	public URI[] getAllMetadataRepositories()
	{
		IMetadataRepositoryManager manager = getMetadataRepositoryManager();
		if (manager == null)
		{
			return new URI[0];
		}
		return manager.getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL);
	}

}
