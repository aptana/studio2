package com.aptana.ide.update;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.repository.IRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.ProvisioningUtil;
import org.eclipse.ui.IStartup;

import com.aptana.ide.core.IdeLog;

public class AddRepositoryStartup implements IStartup {
	public void earlyStartup() {
		// Add the db-tools update site. It is required to present when installing
		// Cloud plugin.
		try {
			URI siteURL = new URI("http://download.aptana.org/tools/studio/plugin/install/db-tools"); //$NON-NLS-1$
			URI[] existingMetaRepos = ProvisioningUtil.getMetadataRepositories(IRepositoryManager.REPOSITORIES_ALL);
			if (!contains(existingMetaRepos, siteURL)) {
				Activator.getDefault().getPluginManager().addUpdateSite(siteURL.toURL());
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
