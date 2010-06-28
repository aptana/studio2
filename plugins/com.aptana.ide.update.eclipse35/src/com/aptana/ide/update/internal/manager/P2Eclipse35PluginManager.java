/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.update.internal.manager;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.p2.ui.sdk.scheduler.AutomaticUpdatePlugin;
import org.eclipse.equinox.internal.p2.ui.sdk.scheduler.PreferenceConstants;
import org.eclipse.equinox.internal.provisional.p2.artifact.repository.IArtifactRepository;
import org.eclipse.equinox.internal.provisional.p2.artifact.repository.IArtifactRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.core.Version;
import org.eclipse.equinox.internal.provisional.p2.core.VersionRange;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfile;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfileRegistry;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepository;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.repository.IRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui.IProvHelpContextIds;
import org.eclipse.equinox.internal.provisional.p2.ui.QueryableMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui.actions.UninstallAction;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.InstallWizard;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.ProvisioningWizardDialog;
import org.eclipse.equinox.internal.provisional.p2.ui.model.ProfileElement;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.ProvisioningUtil;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.Policy;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.internal.update.manager.AbstractPluginManager;
import com.aptana.ide.update.P2Eclipse35Activator;
import com.aptana.ide.update.manager.IPlugin;
import com.aptana.ide.update.manager.Plugin;
import com.aptana.ide.update.manager.PluginManagerException;

@SuppressWarnings("restriction")
public class P2Eclipse35PluginManager extends AbstractPluginManager
{
	public P2Eclipse35PluginManager()
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
			URI[] existingMetaRepos = getAllMetadataRepositories();
			if (contains(existingMetaRepos, siteURI))
			{
				return false;
			}

			ProvisioningUtil.addMetadataRepository(siteURI, false);
			ProvisioningUtil.addArtifactRepository(siteURI, false);
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
			ProvisioningUtil.removeMetadataRepository(siteURI);
			ProvisioningUtil.removeArtifactRepository(siteURI);
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
			throw new PluginManagerException(P2Eclipse35Messages.P2PluginManager_ERR_MSG_No_installable_units_found);
		}

		if (monitor.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		QueryableMetadataRepositoryManager queryableManager = new QueryableMetadataRepositoryManager(Policy
				.getDefault().getQueryContext(), false)
		{
			@Override
			protected URI[] getRepoLocations(IRepositoryManager manager)
			{
				URI[] result = new URI[plugins.length];
				int i = 0;
				for (IPlugin ref : plugins)
				{
					try
					{
						result[i++] = ref.getURL().toURI();
					}
					catch (URISyntaxException e)
					{
						// ignore for now
					}
				}
				return result;
			}
		};

		InstallWizard wizard = new InstallWizard(generateNonManipulatingRepoPolicy(), profileId, toInstall, null,
				queryableManager);
		WizardDialog dialog = new ProvisioningWizardDialog(Display.getDefault().getActiveShell(), wizard);
		dialog.create();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(), IProvHelpContextIds.INSTALL_WIZARD);
		dialog.open();

		return Status.OK_STATUS;
	}

	/**
	 * Generates a copy of the default policy but removes the ability to manipulate repos, so that that portion of
	 * Install Wizard UI doesn't show and mess up our installation process!
	 * 
	 * @return
	 */
	private Policy generateNonManipulatingRepoPolicy()
	{
		Policy newPolicy = new Policy();
		newPolicy.setLicenseManager(Policy.getDefault().getLicenseManager());
		newPolicy.setPlanValidator(Policy.getDefault().getPlanValidator());
		newPolicy.setProfileChooser(Policy.getDefault().getProfileChooser());
		newPolicy.setQueryContext(Policy.getDefault().getQueryContext());
		newPolicy.setQueryProvider(Policy.getDefault().getQueryProvider());
		newPolicy.setRepositoryManipulator(null);
		return newPolicy;
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

		String profileId = IProfileRegistry.SELF;
		Collector roots = new Collector();
		try
		{
			InstallableUnitQuery query = new InstallableUnitQuery(getFeatureGroupName(plugin), Version
					.parseVersion(plugin.getVersion()));
			ProfileElement element = new ProfileElement(null, profileId);
			roots = element.getQueryable().query(query, roots, monitor);
		}
		catch (CoreException e)
		{
			IdeLog.logError(P2Eclipse35Activator.getDefault(), e.getMessage(), e);
		}

		if (roots == null || roots.size() <= 0)
		{
			throw new PluginManagerException(P2Eclipse35Messages.P2PluginManager_ERR_MSG_No_installable_units_found);
		}
		final IInstallableUnit[] ius = (IInstallableUnit[]) roots.toArray(IInstallableUnit.class);

		if (monitor.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		UninstallAction action = new UninstallAction(Policy.getDefault(), new ISelectionProvider() {

            public void addSelectionChangedListener(
                    ISelectionChangedListener listener) { 
            }

            public ISelection getSelection() {
                return new StructuredSelection(ius);
            }

            public void removeSelectionChangedListener(
                    ISelectionChangedListener listener) {
            }

            public void setSelection(ISelection selection) {
            }
		    
		}, profileId);
		action.run();

		return Status.OK_STATUS;
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
				sub.setTaskName(P2Eclipse35Messages.P2PluginManager_Locating_selected_features_job_title);
				try
				{
					for (IPlugin plugin : plugins)
					{
						URI siteURL = plugin.getURL().toURI();

						IMetadataRepositoryManager manager = (IMetadataRepositoryManager) ServiceHelper.getService(
								P2Eclipse35Activator.getContext(), IMetadataRepositoryManager.class.getName());
						IMetadataRepository repo = manager.loadRepository(siteURL, new NullProgressMonitor());
						if (repo == null)
						{
							throw new ProvisionException(
									P2Eclipse35Messages.P2PluginManager_ERR_MSG_Metadata_repo_not_found + siteURL);
						}
						if (!manager.isEnabled(siteURL))
						{
							manager.setEnabled(siteURL, true);
						}
						sub.worked(1);

						IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager) ServiceHelper
								.getService(P2Eclipse35Activator.getContext(), IArtifactRepositoryManager.class
										.getName());
						IArtifactRepository artifactRepo = artifactManager.loadRepository(siteURL,
								new NullProgressMonitor());
						if (artifactRepo == null)
						{
							throw new ProvisionException(
									P2Eclipse35Messages.P2PluginManager_ERR_MSG_Artifact_repo_not_found + siteURL);
						}
						if (!artifactManager.isEnabled(siteURL))
						{
							artifactManager.setEnabled(siteURL, true);
						}
						sub.worked(1);

						InstallableUnitQuery query = new InstallableUnitQuery(getFeatureGroupName(plugin),
								VersionRange.emptyRange);
						Collector roots = repo.query(query, new LatestIUVersionCollector(), monitor);

						if (roots.size() <= 0)
						{
							if (monitor.isCanceled())
							{
								return;
							}

							IProfile profile = ProvisioningUtil.getProfile(profileId);
							roots = profile.query(query, roots, monitor);
						}
						units.addAll(roots.toCollection());
						sub.worked(2);
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
			throw new PluginManagerException(P2Eclipse35Messages.ProfileModificationAction_UnexpectedError, e
					.getCause());
		}
		return units.toArray(new IInstallableUnit[units.size()]);
	}

	private static String getFeatureGroupName(IPlugin plugin) throws CoreException
	{
		return plugin.getId() + FEATURE_IU_SUFFIX;
	}

	private static class LatestIUVersionCollector extends Collector
	{

		private Map<String, Object> uniqueIds = new HashMap<String, Object>();

		/**
		 * Accepts a result that matches the query criteria.
		 * 
		 * @param match
		 *            an object matching the query
		 * @return <code>true</code> if the query should continue, or <code>false</code> to indicate the query should
		 *         stop.
		 */
		public boolean accept(Object match)
		{
			if (!(match instanceof IInstallableUnit))
			{
				return true;
			}
			IInstallableUnit iu = (IInstallableUnit) match;

			// Look for the latest element
			Object matchElement = uniqueIds.get(iu.getId());
			if (matchElement == null || iu.getVersion().compareTo(getIU(matchElement).getVersion()) > 0)
			{
				if (matchElement != null)
				{
					getCollection().remove(matchElement);
				}

				matchElement = makeDefaultElement(iu);
				uniqueIds.put(iu.getId(), matchElement);
				return super.accept(matchElement);
			}
			return true;
		}

		private Object makeDefaultElement(IInstallableUnit iu)
		{
			return iu;
		}

		protected IInstallableUnit getIU(Object matchElement)
		{
			if (matchElement instanceof IInstallableUnit)
			{
				return (IInstallableUnit) matchElement;
			}
			return null;
		}
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
		BundleContext context = P2Eclipse35Activator.getDefault().getBundle().getBundleContext();
		ServiceReference ref = context.getServiceReference(IProfileRegistry.class.getName());
		if (ref == null)
			return null;
		IProfileRegistry profileRegistry = (IProfileRegistry) context.getService(ref);
		if (profileRegistry == null)
			return null;
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
			return null;
		Collector roots = profile.available(new InstallableUnitQuery(id), new Collector(), new NullProgressMonitor());
		if (roots == null || roots.size() == 0)
			return null;
		try
		{
			return toPlugin((IInstallableUnit) roots.iterator().next());
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(P2Eclipse35Activator.getDefault(), e.getMessage(), e);
		}
		return null;
	}

	public List<IPlugin> getInstalledPlugins()
	{
		BundleContext context = P2Eclipse35Activator.getDefault().getBundle().getBundleContext();
		ServiceReference ref = context.getServiceReference(IProfileRegistry.class.getName());
		if (ref == null)
			return Collections.emptyList();
		IProfileRegistry profileRegistry = (IProfileRegistry) context.getService(ref);
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
		Collector roots = profile.available(new InstallableUnitQuery("FakeId") //$NON-NLS-1$
		{
			public boolean isMatch(Object object)
			{
				if (!(object instanceof IInstallableUnit))
					return false;

				IInstallableUnit candidate = (IInstallableUnit) object;
				String name = candidate.getId();

				if (name == null || !name.endsWith(FEATURE_IU_SUFFIX)) // only features
					return false;

				return true;
			}
		}, new Collector(), new NullProgressMonitor());
		IInstallableUnit[] ius = (IInstallableUnit[]) roots.toArray(IInstallableUnit.class);
		// Convert them to Plugin objects
		List<IPlugin> plugins = new ArrayList<IPlugin>();
		for (IInstallableUnit iu : ius)
		{
			try
			{
				plugins.add(toPlugin(iu));
			}
			catch (MalformedURLException e)
			{
				IdeLog.logError(P2Eclipse35Activator.getDefault(), e.getMessage(), e);
			}
		}
		return plugins;
	}

    public String getUpdatePreferencePageId() {
        return "org.eclipse.equinox.internal.p2.ui.sdk.scheduler.AutomaticUpdatesPreferencePage"; //$NON-NLS-1$
    }

	private Plugin toPlugin(IInstallableUnit iu) throws MalformedURLException
	{
		String name = iu.getProperty("df_LT.featureName"); //$NON-NLS-1$
		if (name == null)
			name = iu.getProperty(IInstallableUnit.PROP_NAME);
		if (name == null)
			name = iu.getId();
        return new Plugin(stripFeatureGroup(iu.getId()), name, iu.getVersion().toString(), null, iu
                .getProperty("df_LT.description"), new URL("file:/fake/" + iu.getId()), "", "", 0, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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

	public URI[] getAllMetadataRepositories() throws PluginManagerException
	{
		try 
		{
			return ProvisioningUtil.getMetadataRepositories(IRepositoryManager.REPOSITORIES_ALL);	
		}
		catch (Exception e)
		{
			throw new PluginManagerException(e);
		}		
	}

}
