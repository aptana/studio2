/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.xul;

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class XULPlugin extends AbstractUIPlugin
{

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "com.aptana.ide.xul"; //$NON-NLS-1$

	/**
	 * Element image
	 */
	public static final String ELEMENT_IMG_ID = "ELEMENT_IMG_ID"; //$NON-NLS-1$

	/**
	 * Text image
	 */
	public static final String TEXT_IMG_ID = "TEXT_IMG_ID"; //$NON-NLS-1$

	// The shared instance
	private static XULPlugin plugin;

	// takes care of disposing the images
	private HashMap<String, ImageDescriptor> imageDescMap = new HashMap<String, ImageDescriptor>();

	/**
	 * The constructor
	 */
	public XULPlugin()
	{
		plugin = this;
		imageDescMap.put(ELEMENT_IMG_ID, getImageDescriptor("icons/html_tag.gif")); //$NON-NLS-1$
		imageDescMap.put(TEXT_IMG_ID, getImageDescriptor("icons/text.gif")); //$NON-NLS-1$
	}

	/**
	 * Gets an image from an ID
	 * 
	 * @param imageID -
	 *            image id
	 * @return - Image
	 */
	public Image getImage(String imageID)
	{

		Image image = getImageRegistry().get(imageID);
		// need to create and add to registry
		if (image == null)
		{
			ImageDescriptor imgDescriptor = (ImageDescriptor) imageDescMap.get(imageID);

			if (imgDescriptor != null)
			{
				// create the image and add to registry
				image = imgDescriptor.createImage();
				getImageRegistry().put(imageID, image);
			}

		}
		return image;

	}

	/**
	 * Gets an image descriptor from the registry
	 * 
	 * @param imageID
	 * @return - Image descriptor
	 */
	public ImageDescriptor getImageDescriptorFromRegistry(String imageID)
	{
		ImageDescriptor imgDescriptor = (ImageDescriptor) imageDescMap.get(imageID);
		return imgDescriptor;
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
	public static XULPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Gets an image descriptor from a path
	 * 
	 * @param path
	 * @return - image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
