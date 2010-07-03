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
package com.aptana.ide.core.ui.update;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.update.Activator;
import com.aptana.ide.update.manager.Plugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class PluginsImageRegistry
{

    private static final Image DEFAULT_IMAGE = Activator.getImageDescriptor("images/plugin.png").createImage(); //$NON-NLS-1$

    private ImageRegistry fImages;

    public PluginsImageRegistry(Display display)
    {
        fImages = new ImageRegistry(display);
    }

    public Image getImage(Plugin plugin)
    {
        Image image = null;
        String path = plugin.getImagePath();
        if (path != null)
        {
            image = fImages.get(path);
            if (image == null)
            {
                // generates the image from the path
                ImageDescriptor descriptor = Activator.getImageDescriptor(path);
                if (descriptor == null)
                {
                    // could be a local file path
                    try
                    {
                        image = ImageDescriptor.createFromURL(getURL(path)).createImage();
                    }
                    catch (MalformedURLException e)
                    {
                        // ignore
                    }
                }
                else
                {
                    image = descriptor.createImage();
                }
                if (image != null)
                {
                    fImages.put(path, image);
                }
            }
        }
        return image == null ? DEFAULT_IMAGE : image;
    }

    public static Image getDefaultImage()
    {
        return DEFAULT_IMAGE;
    }

    private URL getURL(String location) throws MalformedURLException
    {
        try
        {
            return (new File(location)).toURI().toURL();
        }
        catch (MalformedURLException e)
        {
            return new URL(location);
        }
    }

}
