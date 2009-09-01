/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain Eclipse Public Licensed code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.installer.views;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import com.aptana.ide.installer.Activator;

public class PluginImages {

    public static final String IMG_PLUGIN_DISABLED = "IMG_PLUGIN_DISABLED"; //$NON-NLS-1$
    public static final String IMG_PLUGIN_ENABLED = "IMG_PLUGIN_ENABLED"; //$NON-NLS-1$

    private static final String pluginEnabled = "icons/plugin.png"; //$NON-NLS-1$
    private static final String pluginDisabled = "icons/plugin_disabled.png"; //$NON-NLS-1$	

    private static ImageRegistry imageRegistry;

    private synchronized static ImageRegistry initializeImageRegistry() {
        if (imageRegistry == null) {
            imageRegistry = new ImageRegistry(getStandardDisplay());
            declareImages();
        }
        return imageRegistry;
    }

    /**
     * Declare all images.
     */
    private static void declareImages() {
        declareRegistryImage(IMG_PLUGIN_DISABLED, pluginDisabled);
        declareRegistryImage(IMG_PLUGIN_ENABLED, pluginEnabled);
    }

    /**
     * Declare an Image in the registry table.
     * 
     * @param key
     *            The key to use when registering the image
     * @param path
     *            The path where the image can be found. This path is relative
     *            to where this plugin class is found (i.e. typically the
     *            packages directory)
     */
    private final static void declareRegistryImage(String key, String path) {
        ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
        Bundle bundle = Activator.getDefault().getBundle();
        if (bundle != null) {
            URL url = FileLocator.find(bundle, new Path(path), null);
            desc = ImageDescriptor.createFromURL(url);
        }
        imageRegistry.put(key, desc);
    }

    /**
     * Returns the standard display to be used. The method first checks, if the
     * thread calling this method has an associated display. If so, this display
     * is returned. Otherwise the method returns the default display.
     */
    private static Display getStandardDisplay() {
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        return display;
    }

    /**
     * Returns the ImageRegistry.
     */
    public static ImageRegistry getImageRegistry() {
        if (imageRegistry == null) {
            initializeImageRegistry();
        }
        return imageRegistry;
    }

    /**
     * Returns the <code>Image</code> identified by the given key, or
     * <code>null</code> if it does not exist.
     */
    public static Image getImage(String key) {
        return getImageRegistry().get(key);
    }

}
