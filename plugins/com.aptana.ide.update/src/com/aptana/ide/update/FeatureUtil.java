package com.aptana.ide.update;

import java.util.List;

import com.aptana.ide.update.manager.IPlugin;
import com.aptana.ide.update.manager.IPluginManager;
import com.aptana.ide.update.manager.Plugin;

public abstract class FeatureUtil
{

	/**
	 * Determines if the given feature is already installed
	 * 
	 * @param pluginId
	 * @return
	 */
	public static boolean isInstalled(String featureId)
	{
		return getPluginManager().isFeatureInstalled(featureId);
	}

	public static IPlugin findInstalledPlugin(String pluginId)
	{
		return getPluginManager().getInstalledFeature(pluginId);
	}

	public static List<IPlugin> getInstalledFeatures()
	{
		return getPluginManager().getInstalledPlugins();
	}

	private static IPluginManager getPluginManager()
	{
		return Activator.getDefault().getPluginManager();
	}

	public static Plugin findRemotePlugin(String pluginId)
	{
		List<Plugin> plugins = getPluginManager().getRemotePlugins();
		for (Plugin plugin : plugins)
		{
			if (plugin.getId().equals(pluginId))
				return plugin;
		}
		return null;
	}
}
