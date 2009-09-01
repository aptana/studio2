package com.aptana.ide.logging;

import java.util.Hashtable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LoggingPlugin extends AbstractUIPlugin
{

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "com.aptana.ide.logviewer"; //$NON-NLS-1$

	private static Hashtable<String, Image> images = new Hashtable<String, Image>();

	// The shared instance
	private static LoggingPlugin plugin;

	/**
	 * Logging preferences.
	 */
	private LoggingPreferences preferences;

	/**
	 * The constructor
	 */
	public LoggingPlugin()
	{
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
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
	 * getImage
	 * 
	 * @param path
	 * @return Image
	 */
	public static Image getImage(String path)
	{
		if (images.get(path) == null)
		{
			ImageDescriptor id = getImageDescriptor(path);

			if (id == null)
			{
				return null;
			}

			Image i = id.createImage();

			images.put(path, i);

			return i;
		}
		else
		{
			return (Image) images.get(path);
		}
	}

	/**
	 * Returns the active workbench shell or <code>null</code> if none
	 * 
	 * @return the active workbench shell or <code>null</code> if none
	 */
	public static Shell getActiveWorkbenchShell()
	{
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null)
		{
			return window.getShell();
		}
		return null;
	}

	/**
	 * Returns the active workbench window
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow()
	{
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * Gets logging preferences.
	 * 
	 * @return logging preferences.
	 */
	public LoggingPreferences getLoggingPreferences()
	{
		if (preferences == null)
		{
			preferences = new LoggingPreferences();
		}

		return preferences;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static LoggingPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin("com.aptana.ide.logging", path); //$NON-NLS-1$
	}
}
