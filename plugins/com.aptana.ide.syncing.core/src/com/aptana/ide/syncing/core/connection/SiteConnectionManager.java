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
 * with certain other free and open source software ("FOSS") code and certain additional terms
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
package com.aptana.ide.syncing.core.connection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointCategory;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.core.io.WorkspaceConnectionPoint;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class SiteConnectionManager {

    /**
     * @return the array of available site connections
     */
    public static SiteConnectionPoint[] getExistingSites() {
        IConnectionPointCategory category = CoreIOPlugin.getConnectionPointManager()
                .getConnectionPointCategory(SiteConnectionPoint.CATEGORY);
        if (category == null) {
            return new SiteConnectionPoint[0];
        }

        List<SiteConnectionPoint> sites = new ArrayList<SiteConnectionPoint>();
        IConnectionPoint[] connections = category.getConnectionPoints();
        for (IConnectionPoint connection : connections) {
            if (connection instanceof SiteConnectionPoint) {
                sites.add((SiteConnectionPoint) connection);
            }
        }
        return sites.toArray(new SiteConnectionPoint[sites.size()]);
    }

    /**
     * Retrieves a list of all available sites that have the object as the
     * source (i.e. an IContainer or FilesystemObject).
     * 
     * @param sourceObject
     *            the source object
     * @return the list as an array
     */
    public static SiteConnectionPoint[] getSitesWithSource(Object sourceObject) {
        return getSitesWithSource(sourceObject, false);
    }

    /**
     * Retrieves a list of all available sites that have the object as the
     * source (i.e. an IContainer or FilesystemObject).
     * 
     * @param sourceObject
     *            the source object
     * @param strict
     *            true if only to get the exact matches, false if the parent
     *            folder is allowed
     * @return the list as an array
     */
    public static SiteConnectionPoint[] getSitesWithSource(Object sourceObject, boolean strict) {
        List<SiteConnectionPoint> sites = new ArrayList<SiteConnectionPoint>();

        SiteConnectionPoint[] allSites = getExistingSites();
        if (sourceObject instanceof IConnectionPoint) {
            for (SiteConnectionPoint site : allSites) {
                if (site.getSource() == sourceObject && site.getDestination() != null) {
                    sites.add(site);
                }
            }
        } else if (sourceObject instanceof IResource) {
            // project source
            IResource resource = (IResource) sourceObject;
            IConnectionPoint source;
            for (SiteConnectionPoint site : allSites) {
                source = site.getSource();
                if (source instanceof WorkspaceConnectionPoint && site.getDestination() != null) {
                    IContainer root = ((WorkspaceConnectionPoint) source).getResource();
                    if (root.equals(sourceObject) || (!strict && contains(root, resource))) {
                        sites.add(site);
                    }
                }
            }
        } else if (sourceObject instanceof IAdaptable) {
            IFileStore fileStore = (IFileStore) ((IAdaptable) sourceObject)
                    .getAdapter(IFileStore.class);
            if (fileStore != null) {
                // filesystem source
                IConnectionPoint source;
                for (SiteConnectionPoint site : allSites) {
                    source = site.getSource();
                    if (source instanceof LocalConnectionPoint && site.getDestination() != null) {
                        try {
                            IFileStore root = ((LocalConnectionPoint) source).getRoot();
                            if (root.equals(fileStore) || (!strict && root.isParentOf(fileStore))) {
                                sites.add(site);
                            }
                        } catch (CoreException e) {
                        }
                    }
                }
            }
        }
        return sites.toArray(new SiteConnectionPoint[sites.size()]);
    }

    private static boolean contains(IContainer root, IResource element) {
        return element.getFullPath().toString().indexOf(root.getFullPath().toString()) > -1;
    }
}
