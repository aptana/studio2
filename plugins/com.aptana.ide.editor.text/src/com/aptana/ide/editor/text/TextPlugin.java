package com.aptana.ide.editor.text;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TextPlugin extends AbstractUIPlugin
{

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "com.aptana.ide.editor.text"; //$NON-NLS-1$

	/**
	 * The grammar ID
	 */
	public static final String GRAMMAR_ID = PLUGIN_ID + ".grammar"; //$NON-NLS-1$

	/**
	 * The colorizer ID
	 */
	public static final String COLORIZER_ID = PLUGIN_ID + ".colorizer"; //$NON-NLS-1$

	// The shared instance
	private static TextPlugin plugin;

	/**
	 * The constructor
	 */
	public TextPlugin()
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
	public static TextPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Gets the colorizer preference string for a file editor mapping label
	 * 
	 * @param mappingLabel
	 * @return - preference string
	 */
	public static String getColorizerPreference(String mappingLabel)
	{
		return COLORIZER_ID + "." + mappingLabel; //$NON-NLS-1$
	}

	/**
	 * Gets the grammar preference string for a file editor mapping label
	 * 
	 * @param mappingLabel
	 * @return - preference string
	 */
	public static String getGrammarPreference(String mappingLabel)
	{
		return GRAMMAR_ID + "." + mappingLabel; //$NON-NLS-1$
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("com.aptana.ide.editor.text", path); //$NON-NLS-1$
	}
}
