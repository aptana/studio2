package com.aptana.ide.internal.update.manager;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.update.Activator;
import com.aptana.ide.update.manager.IPluginManager;

public class PluginManagerLoader
{

	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private static IPluginManager fManager;

	public static IPluginManager getPluginManager()
	{
		if (fManager == null)
		{
			fManager = loadExtensionPoints();
		}
		return fManager;
	}

	private static IPluginManager loadExtensionPoints()
	{
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(Activator.MANAGER_EXTENSION_POINT);
		IExtension[] extensions = ep.getExtensions();
		IConfigurationElement[] ce;
		String className;
		for (int i = 0; i < extensions.length; ++i)
		{
			ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length; ++j)
			{
				className = ce[j].getAttribute(ATTR_CLASS);
				if (className != null)
				{
					try
					{
						Object classObject = ce[j].createExecutableExtension(ATTR_CLASS);
						if (classObject instanceof IPluginManager)
						{
							return (IPluginManager) classObject;
						}
					}
					catch (Throwable t)
					{
						// Do nothing
					}
				}
			}
		}
		return null;
	}

}
