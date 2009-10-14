package com.aptana.ide.update;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.ui.IStartup;

import org.eclipse.equinox.internal.p2.console.ProvisioningHelper;
import org.osgi.framework.Version;

import com.aptana.ide.update.internal.manager.P2PluginManager;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.update.manager.IPluginManager;

public class AddRepositoryStartup implements IStartup {
	private static final VersionRange versionRange = new VersionRange("[1.0.0,1.0.100)"); //$NON-NLS-1$
	public void earlyStartup() {
		// Add the necessary update site. It is required to present when installing
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
				URL siteURL = new URL("http://download.aptana.org/tools/studio/plugin/install/xul"); //$NON-NLS-1$
				URL[] existingMetaRepos = ProvisioningHelper.getMetadataRepositories();
				if (!contains(existingMetaRepos, siteURL)) {
					pluginManager.addUpdateSite(siteURL);
				}
			}
		} catch (MalformedURLException e) {
			IdeLog.logError(P2Activator.getDefault(), e.getMessage(), e);
		}
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
}
