package com.aptana.ide.update;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.repository.IRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.ProvisioningUtil;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.ui.IStartup;
import org.osgi.framework.Version;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.update.internal.manager.P2Eclipse35PluginManager;
import com.aptana.ide.update.manager.IPluginManager;

public class AddRepositoryStartup implements IStartup {
	private static final VersionRange versionRange = new VersionRange("1.0.100"); //$NON-NLS-1$

	public void earlyStartup() {
		// Add the db-tools update site. It is required to present when installing
		// Cloud plugin.
		try {
			String pluginVersionString = PluginUtils.getPluginVersion("org.eclipse.equinox.p2.core"); //$NON-NLS-1$
			if (pluginVersionString == null) {
				return;
			}
			Version pluginVersion = new Version(pluginVersionString);
			if (pluginVersion == null) {
				return;
			}
			
			if (versionRange.isIncluded(pluginVersion)) {
				IPluginManager pluginManager = Activator.getDefault().getPluginManager();
				URI siteURL = new URI("http://download.aptana.org/tools/studio/plugin/install/db-tools"); //$NON-NLS-1$
				URI[] existingMetaRepos = ProvisioningUtil.getMetadataRepositories(IRepositoryManager.REPOSITORIES_ALL);
				if (!contains(existingMetaRepos, siteURL)) {
					pluginManager.addUpdateSite(siteURL.toURL());
				}
			}
		} catch (MalformedURLException e) {
			IdeLog.logError(P2Eclipse35Activator.getDefault(), e.getMessage(), e);
		} catch (URISyntaxException e) {
			IdeLog.logError(P2Eclipse35Activator.getDefault(), e.getMessage(), e);
		} catch (ProvisionException e) {
			IdeLog.logError(P2Eclipse35Activator.getDefault(), e.getMessage(), e);
		}
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
}
