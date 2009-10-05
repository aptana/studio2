package com.aptana.ide.update;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.ui.IStartup;

import org.eclipse.equinox.internal.p2.console.ProvisioningHelper;

import com.aptana.ide.update.internal.manager.P2PluginManager;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.update.manager.IPluginManager;

public class AddRepositoryStartup implements IStartup {
	public void earlyStartup() {
		// Add the db-tools update site. It is required to present when installing
		// Cloud plugin.
		try {
			IPluginManager pluginManager = Activator.getDefault().getPluginManager();
			if (pluginManager instanceof P2PluginManager) {
				URL siteURL = new URL("http://download.aptana.org/tools/studio/plugin/install/db-tools"); //$NON-NLS-1$
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
