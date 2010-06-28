package com.aptana.ide.update.internal.manager;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.internal.p2.console.ProvisioningHelper;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.p2.ui.sdk.ProvSDKUIActivator;
import org.eclipse.equinox.internal.p2.ui.sdk.prefs.PreferenceConstants;
import org.eclipse.equinox.internal.provisional.p2.artifact.repository.IArtifactRepository;
import org.eclipse.equinox.internal.provisional.p2.artifact.repository.IArtifactRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.provisional.p2.director.ProvisioningPlan;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfile;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfileRegistry;
import org.eclipse.equinox.internal.provisional.p2.engine.ProvisioningContext;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepository;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.ui.IProvHelpContextIds;
import org.eclipse.equinox.internal.provisional.p2.ui.ProvUI;
import org.eclipse.equinox.internal.provisional.p2.ui.actions.InstallAction;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.InstallWizard;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.UninstallWizard;
import org.eclipse.equinox.internal.provisional.p2.ui.model.ProfileElement;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.ProvisioningUtil;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.LicenseManager;
import org.eclipse.equinox.internal.provisional.p2.ui.sdk.ProvPolicies;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.internal.update.manager.AbstractPluginManager;
import com.aptana.ide.update.P2Activator;
import com.aptana.ide.update.manager.IPlugin;
import com.aptana.ide.update.manager.Plugin;
import com.aptana.ide.update.manager.PluginManagerException;

public class P2PluginManager extends AbstractPluginManager
{

	/**
	 * @see com.aptana.ide.update.manager.IPluginManager#checkForUpdates(boolean)
	 */
	public void checkForUpdates(boolean immediate)
	{
		if (!immediate)
		{
			ProvSDKUIActivator.getDefault().getScheduler().earlyStartup();
			return;
		}
		// Force the P2 automatic update check pref to be turned on, and force a
		// reschedule of the update check
		Preferences prefs = ProvSDKUIActivator.getDefault().getPluginPreferences();
		// Grab existing values
		boolean wasEnabled = prefs.getBoolean(PreferenceConstants.PREF_AUTO_UPDATE_ENABLED);
		String oldSchedule = prefs.getString(PreferenceConstants.PREF_AUTO_UPDATE_SCHEDULE);
		// Force new ones temporarily
		prefs.setValue(PreferenceConstants.PREF_AUTO_UPDATE_ENABLED, true);
		prefs.setValue(PreferenceConstants.PREF_AUTO_UPDATE_SCHEDULE, PreferenceConstants.PREF_UPDATE_ON_STARTUP);
		ProvSDKUIActivator.getDefault().savePluginPreferences();
		// now check for updates
		ProvSDKUIActivator.getDefault().getScheduler().earlyStartup();
		// Now revert prefs
		prefs.setValue(PreferenceConstants.PREF_AUTO_UPDATE_ENABLED, wasEnabled);
		prefs.setValue(PreferenceConstants.PREF_AUTO_UPDATE_SCHEDULE, oldSchedule);
		ProvSDKUIActivator.getDefault().savePluginPreferences();
	}

	/**
	 * @see com.aptana.ide.installer.plugins.IPluginManager#install(org.eclipse.update.core.IFeatureReference[],
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus install(IPlugin[] plugins, IProgressMonitor monitor) throws PluginManagerException
	{
		if (monitor == null)
			monitor = new NullProgressMonitor();
		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;

		final String profileId = IProfileRegistry.SELF;
		IInstallableUnit[] toInstall = getInstallationUnits(plugins, profileId);
		if (toInstall.length <= 0)
		{
			throw new PluginManagerException(Messages.P2PluginManager_ERR_MSG_No_installable_units_found);
		}

		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;

		final ProvisioningPlan result = getInstallationProvisioningPlan(toInstall, profileId);
		if (!validatePlan(result))
			return Status.CANCEL_STATUS;

		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;

		final LicenseManager licenseManager = ProvSDKUIActivator.getDefault().getLicenseManager();
		InstallWizard wizard = new InstallWizard(profileId, toInstall, result, licenseManager);
		WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
		dialog.create();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(), IProvHelpContextIds.INSTALL_WIZARD);
		dialog.open();
		return Status.OK_STATUS;
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
				sub.setTaskName(Messages.P2PluginManager_Locating_selected_features_job_title);
				try
				{
					for (int i = 0; i < plugins.length; i++)
					{
						IPlugin plugin = plugins[i];
						IMetadataRepositoryManager manager = (IMetadataRepositoryManager) ServiceHelper.getService(
								P2Activator.getContext(), IMetadataRepositoryManager.class.getName());
						IMetadataRepository repo = manager.loadRepository(plugin.getURL(), new NullProgressMonitor());
						if (repo == null)
						{
							throw new ProvisionException(Messages.P2PluginManager_ERR_MSG_Metadata_repo_not_found
									+ plugin.getURL());
						}
						if (!manager.isEnabled(plugin.getURL()))
							manager.setEnabled(plugin.getURL(), true);
						sub.worked(1);

						IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager) ServiceHelper
								.getService(P2Activator.getContext(), IArtifactRepositoryManager.class.getName());
						IArtifactRepository artifactRepo = artifactManager.loadRepository(plugin.getURL(),
								new NullProgressMonitor());
						if (artifactRepo == null)
						{
							throw new ProvisionException(Messages.P2PluginManager_ERR_MSG_Artifact_repo_not_found
									+ plugin.getURL());
						}
						if (!artifactManager.isEnabled(plugin.getURL()))
							artifactManager.setEnabled(plugin.getURL(), true);
						sub.worked(1);

						InstallableUnitQuery query = new InstallableUnitQuery(getFeatureGroupName(plugin),
								VersionRange.emptyRange);
						Collector roots = repo.query(query, new LatestIUVersionCollector(), monitor);

						if (roots.size() <= 0)
						{
							if (monitor.isCanceled())
								return;

							IProfile profile = ProvisioningHelper.getProfile(profileId);
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
			throw new PluginManagerException(Messages.ProfileModificationAction_UnexpectedError, e.getCause());
		}
		return units.toArray(new IInstallableUnit[units.size()]);
	}

	private static ProvisioningPlan getInstallationProvisioningPlan(final IInstallableUnit[] ius, final String profileId)
	{
		final ProvisioningPlan[] plan = new ProvisioningPlan[1];
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor)
			{
				try
				{
					plan[0] = InstallAction.computeProvisioningPlan(ius, profileId, monitor);
				}
				catch (ProvisionException e)
				{
					ProvUI.handleException(e, Messages.ProfileModificationAction_UnexpectedError, StatusManager.BLOCK
							| StatusManager.LOG);
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
			ProvUI.handleException(e.getCause(), Messages.ProfileModificationAction_UnexpectedError,
					StatusManager.BLOCK | StatusManager.LOG);
		}
		return plan[0];
	}

	private static boolean validatePlan(ProvisioningPlan plan)
	{
		if (plan != null)
		{
			// Don't validate the plan if the user cancelled
			if (plan.getStatus().getSeverity() == IStatus.CANCEL)
				return false;
			if (ProvPolicies.getDefault().getPlanValidator() != null)
				return ProvPolicies.getDefault().getPlanValidator().continueWorkingWithPlan(plan,
						Display.getDefault().getActiveShell());
			if (plan.getStatus().isOK())
				return true;
			ProvUI.reportStatus(plan.getStatus(), StatusManager.BLOCK | StatusManager.LOG);
			return false;
		}
		return false;
	}

	/**
	 * @see com.aptana.ide.installer.plugins.IPluginManager#uninstall(org.eclipse.update.core.IFeatureReference,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus uninstall(IPlugin plugin, IProgressMonitor monitor) throws PluginManagerException
	{
		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;

		final String profileId = IProfileRegistry.SELF;
		Collector roots = new Collector();
		try
		{
			InstallableUnitQuery query = new InstallableUnitQuery(getFeatureGroupName(plugin), new Version(plugin
					.getVersion()));
			ProfileElement element = new ProfileElement(profileId);
			roots = element.getQueryable().query(query, roots, monitor);
		}
		catch (CoreException e)
		{
			IdeLog.logError(P2Activator.getDefault(), e.getMessage(), e);
		}

		if (roots == null || roots.size() <= 0)
		{
			throw new PluginManagerException(Messages.P2PluginManager_ERR_MSG_No_installable_units_found);
		}
		IInstallableUnit[] ius = (IInstallableUnit[]) roots.toArray(IInstallableUnit.class);

		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;

		ProvisioningPlan plan = getUninstallationProvisioningPlan(ius, profileId);

		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;

		UninstallWizard wizard = new UninstallWizard(profileId, ius, plan);
		WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
		dialog.create();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(), IProvHelpContextIds.UNINSTALL_WIZARD);
		dialog.open();
		return Status.OK_STATUS;
	}

	/**
	 * @see com.aptana.ide.update.manager.IPluginManager#addUpdateSite(java.net.URL)
	 */
	public boolean addUpdateSite(URL siteURL)
	{
		URL[] existingMetaRepos = ProvisioningHelper.getMetadataRepositories();
		if (contains(existingMetaRepos, siteURL))
			return false;

		ProvisioningHelper.addMetadataRepository(siteURL);
		ProvisioningHelper.addArtifactRepository(siteURL);
		return true;
	}

	/**
	 * @see com.aptana.ide.update.manager.IPluginManager#removeUpdateSite(java.net.URL)
	 */
	public void removeUpdateSite(URL siteURL)
	{
		ProvisioningHelper.removeMetadataRepository(siteURL);
		ProvisioningHelper.removeArtifactRepository(siteURL);
	}

	private static String getFeatureGroupName(IPlugin plugin) throws CoreException
	{
		return plugin.getId() + ".feature.group"; //$NON-NLS-1$
	}

	private static ProvisioningPlan getUninstallationProvisioningPlan(final IInstallableUnit[] ius,
			final String profileId)
	{
		final ProvisioningPlan[] plan = new ProvisioningPlan[1];
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor)
			{
				try
				{
					ProfileChangeRequest request = ProfileChangeRequest.createByProfileId(profileId);
					request.removeInstallableUnits(ius);
					plan[0] = ProvisioningUtil.getProvisioningPlan(request, new ProvisioningContext(), monitor);
				}
				catch (ProvisionException e)
				{
					ProvUI.handleException(e, Messages.ProfileModificationAction_UnexpectedError, StatusManager.BLOCK
							| StatusManager.LOG);
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
			ProvUI.handleException(e.getCause(), Messages.ProfileModificationAction_UnexpectedError,
					StatusManager.BLOCK | StatusManager.LOG);
		}
		return plan[0];
	}

	private static boolean contains(URL[] existingMetaRepos, URL updateSiteURL)
	{
		if (existingMetaRepos == null)
			return false;
		for (int i = 0; i < existingMetaRepos.length; i++)
		{
			if (existingMetaRepos[i].equals(updateSiteURL))
				return true;
		}
		return false;
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
				return true;
			IInstallableUnit iu = (IInstallableUnit) match;
			// Look for the latest element
			Object matchElement = uniqueIds.get(iu.getId());
			if (matchElement == null || iu.getVersion().compareTo(getIU(matchElement).getVersion()) > 0)
			{
				if (matchElement != null)
					getList().remove(matchElement);

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
				return (IInstallableUnit) matchElement;
			return null;
		}
	}

	public boolean isFeatureInstalled(String id)
	{
		return getInstalledFeature(id) != null;
	}

	public Plugin getInstalledFeature(String id)
	{
		if (id == null)
			return null;

		if (!id.endsWith(FEATURE_IU_SUFFIX))
		{
			id += FEATURE_IU_SUFFIX;
		}
		BundleContext context = P2Activator.getDefault().getBundle().getBundleContext();
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
		Collector roots = profile.available(new InstallableUnitQuery(id), new Collector(), new NullProgressMonitor());
		if (roots == null || roots.size() == 0)
			return null;
		try
		{
			return toPlugin((IInstallableUnit) roots.iterator().next());
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(P2Activator.getDefault(), e.getMessage(), e);
		}
		return null;
	}

	public List<IPlugin> getInstalledPlugins()
	{
		BundleContext context = P2Activator.getDefault().getBundle().getBundleContext();
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
		Collector roots = profile.available(new InstallableUnitQuery("FakeId")
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
				IdeLog.logError(P2Activator.getDefault(), e.getMessage(), e);
			}
		}
		return plugins;
	}

    public String getUpdatePreferencePageId() {
        return "org.eclipse.equinox.internal.p2.ui.sdk.AutomaticUpdatesPreferencePage"; //$NON-NLS-1$
    }

	private Plugin toPlugin(IInstallableUnit iu) throws MalformedURLException
	{
		String name = iu.getProperty("df_LT.featureName");
		if (name == null)
			name = iu.getProperty(IInstallableUnit.PROP_NAME);
		if (name == null)
			name = iu.getId();
		return new Plugin(stripFeatureGroup(iu.getId()), name, iu.getVersion().toString(), null, iu
				.getProperty("df_LT.description"), new URL("file:/fake/" + iu.getId()), "", "", 0, null, "", null);
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
		List<URI> uris = new ArrayList<URI>();
		URL[] urls = ProvisioningHelper.getMetadataRepositories();
		for (URL url : urls)
		{
			uris.add(url.toURI());
		}
		return uris.toArray(new URI[0]);
	}
}
