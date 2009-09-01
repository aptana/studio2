package com.aptana.ide.xul.browser;

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.ide.xul.browser"; //$NON-NLS-1$

	/**
	 * Internal div id
	 */
	public static final String INTERNAL_ID = "_____INTERNAL"; //$NON-NLS-1$

	/**
	 * Errors exist image
	 */
	public static final String ERRORS_IMG_ID = "ERRORS_IMG_ID"; //$NON-NLS-1$

	/**
	 * No error image
	 */
	public static final String NO_ERRORS_IMG_ID = "NO_ERRORS_IMG_ID"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	// takes care of disposing the images
	private HashMap<String, ImageDescriptor> imageDescMap = new HashMap<String, ImageDescriptor>();

	/**
	 * The constructor
	 */
	public Activator() {
		imageDescMap.put(ERRORS_IMG_ID, getImageDescriptor("icons/errors.png")); //$NON-NLS-1$
		imageDescMap.put(NO_ERRORS_IMG_ID, getImageDescriptor("icons/no_errors.png")); //$NON-NLS-1$
	}

	/**
	 * Gets an image from an ID.
	 * 
	 * @param imageID
	 *            - image id
	 * @return - Image
	 */
	public Image getImage(String imageID) {
		Image image = getImageRegistry().get(imageID);
		// need to create and add to registry
		if (image == null) {
			ImageDescriptor imgDescriptor = (ImageDescriptor) imageDescMap
					.get(imageID);

			if (imgDescriptor != null) {
				// create the image and add to registry
				image = imgDescriptor.createImage();
				getImageRegistry().put(imageID, image);
			}
		}
		return image;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Gets an image descriptor from a path
	 * 
	 * @param path
	 * @return - image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
