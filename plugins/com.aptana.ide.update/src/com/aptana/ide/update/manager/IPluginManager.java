package com.aptana.ide.update.manager;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public interface IPluginManager
{

    public void checkForUpdates(boolean immediate);

	public abstract IStatus install(IPlugin[] plugins, IProgressMonitor monitor)
			throws PluginManagerException;

	public abstract IStatus uninstall(IPlugin plugin, IProgressMonitor monitor) throws PluginManagerException;

	public abstract List<IPlugin> getInstalledPlugins();

	public abstract boolean isFeatureInstalled(String id);
	
	public abstract List<Plugin> getRemotePlugins();

	public abstract Collection<PluginListener> getListeners();

	public abstract void addListener(PluginListener pluginsListener);

	public abstract void removeListener(PluginListener pluginsListener);

	public boolean addUpdateSite(URL siteURL);

	public void removeUpdateSite(URL siteURL);

	public IPlugin getInstalledFeature(String id);

	public String getUpdatePreferencePageId();

	public URI[] getAllMetadataRepositories();
}
