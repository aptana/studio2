package com.aptana.ide.samples;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.samples.handlers.IPreviewHandler;
import com.aptana.ide.samples.handlers.IProjectCreationHandler;
import com.aptana.ide.samples.model.SamplesInfo;

/**
 * The activator class controls the plug-in life cycle
 */
public class SamplesPlugin extends AbstractUIPlugin
{
	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "com.aptana.ide.samples"; //$NON-NLS-1$

	// The shared instance
	private static SamplesPlugin plugin;
	private static Hashtable<String, Image> images = new Hashtable<String, Image>();

	/**
	 * The constructor
	 */
	public SamplesPlugin()
	{
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SamplesPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * findSamplesInfoExtensions
	 * 
	 * @return List of SamplesInfo's
	 */
	public static SamplesInfo[] findSamplesInfoExtensions()
	{
		ArrayList<SamplesInfo> list = new ArrayList<SamplesInfo>();

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint("com.aptana.ide.samples.samplespath"); //$NON-NLS-1$

		if (point != null)
		{
			IExtension[] extensions = point.getExtensions();

			for (int i = 0; i < extensions.length; i++)
			{
				IExtension extension = extensions[i];
				IConfigurationElement[] configurations = extension.getConfigurationElements();

				for (int j = 0; j < configurations.length; j++)
				{
					IConfigurationElement element = configurations[j];
					try
					{
						IExtension declaring = element.getDeclaringExtension();

						String declaringPluginID = declaring.getNamespaceIdentifier();
						Bundle bundle = Platform.getBundle(declaringPluginID);

						SamplesInfo samplesInfo = new SamplesInfo();

						// Get 'name'
						String name = element.getAttribute("name"); //$NON-NLS-1$
						samplesInfo.setName(name);

						// Get 'directory'
						String directory = element.getAttribute("directory"); //$NON-NLS-1$
						if (directory == null)
						{
							continue;
						}

						

						String extensionId = element.getAttribute("id"); //$NON-NLS-1$
						String extensionPluginId = element.getNamespaceIdentifier();
						samplesInfo.setExtensionId(extensionId);
						samplesInfo.setExtensionPluginId(extensionPluginId);
						
						String resolvedPath = getResolvedFilename(bundle, directory);

						if (resolvedPath != null)
						{
							samplesInfo.setDirectory(resolvedPath);
							File f = new File(resolvedPath);
							if (f.listFiles().length == 0)
							{
								// no files, so don't include in viewer
								continue;
							}
						}
						else
						{
							// samples directory does not exist, so don't show samples
							continue;
						}

						// Get optional 'infoFile'
						String infoFile = element.getAttribute("infoFile"); //$NON-NLS-1$
						if (infoFile != null && infoFile.length() > 0)
						{
							samplesInfo.setInfoFile(getResolvedURL(bundle, infoFile));
						}

						// Get optional 'iconFile'
						String iconFile = element.getAttribute("iconFile"); //$NON-NLS-1$
						if (iconFile != null && iconFile.length() > 0)
						{
							resolvedPath = getResolvedFilename(bundle, iconFile);
							samplesInfo.setIconFile(resolvedPath);
						}

						// Get optional 'projectHandler'
						String projectHandler = element.getAttribute("projectHandler"); //$NON-NLS-1$
						if (projectHandler != null)
						{
							try
							{
								Object handler = element.createExecutableExtension("projectHandler"); //$NON-NLS-1$
								if (handler instanceof IProjectCreationHandler)
								{
									samplesInfo.setCreationHandler((IProjectCreationHandler) handler);
								}
							}
							catch (CoreException e)
							{
							}
						}

						// Get optional 'previewHandler'
						String previewHandler = element.getAttribute("previewHandler"); //$NON-NLS-1$
						if (previewHandler != null)
						{
							try
							{
								Object handler = element.createExecutableExtension("previewHandler"); //$NON-NLS-1$
								if (handler instanceof IPreviewHandler)
								{
									samplesInfo.setPreviewHandler((IPreviewHandler) handler);
								}
							}
							catch (CoreException e)
							{
							}
						}

						IConfigurationElement[] natures = element.getChildren("nature"); //$NON-NLS-1$
						if (natures != null)
						{
							for (int k = 0; k < natures.length; k++)
							{
								String natureId = natures[k].getAttribute("id"); //$NON-NLS-1$
								if (natureId != null && natureId.length() > 0)
								{
									samplesInfo.addNature(natureId);
								}
							}
						}

						IConfigurationElement[] includes = element.getChildren("include"); //$NON-NLS-1$
						if (includes != null)
						{
							for (int k = 0; k < includes.length; k++)
							{
								String includePath = includes[k].getAttribute("path"); //$NON-NLS-1$
								if (includePath != null && includePath.length() > 0)
								{
									String resolvedInclude = getResolvedFilename(bundle, includePath);
									samplesInfo.addIncludePath(resolvedInclude);
								}
							}
						}

						list.add(samplesInfo);
					}
					catch (InvalidRegistryObjectException x)
					{
						// ignore bad extensions
					}
				}
			}
		}

		return list.toArray(new SamplesInfo[0]);
	}

	private static URL getResolvedURL(Bundle b, String fullPath)
	{
		URL url = FileLocator.find(b, new Path(fullPath), null);

		if (url != null)
		{
			try
			{

				URL localUrl = FileLocator.toFileURL(url);
				if (localUrl != null)
				{
					return localUrl;
				}
			}
			catch (IOException e)
			{
				IdeLog.logError(SamplesPlugin.getDefault(), e.getMessage());
			}
		}
		return null;
	}

	private static String getResolvedFilename(Bundle b, String fullPath)
	{
		String[] split = fullPath.split("/"); //$NON-NLS-1$
		if (split.length > 0)
		{
			// need to resolve root path first, as otherwise later items will not be extracted correctly.
			getResolvedURL(b, split[0]);
		}

		URL url = getResolvedURL(b, fullPath);
		if (url != null)
		{
			return url.getFile();
		}

		return null;
	}
}
