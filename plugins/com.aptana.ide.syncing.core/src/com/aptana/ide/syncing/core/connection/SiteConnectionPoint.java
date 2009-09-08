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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.epl.IMemento;
import com.aptana.ide.core.io.ConnectionPoint;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointCategory;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.core.io.WorkspaceConnectionPoint;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class SiteConnectionPoint extends ConnectionPoint {

    public static final String TYPE = "site"; //$NON-NLS-1$
    public static final String CATEGORY = "com.aptana.ide.syncing.core.sitesCategory"; //$NON-NLS-1$

    private static final String ELEMENT_SRC_CATEGORY = "srcCategory"; //$NON-NLS-1$
    private static final String ELEMENT_SRC_NAME = "srcName"; //$NON-NLS-1$
    private static final String ELEMENT_DEST_CATEGORY = "destCategory"; //$NON-NLS-1$
    private static final String ELEMENT_DEST_NAME = "destName"; //$NON-NLS-1$

    private String fSrcCategory;
    private IConnectionPoint fSource;
    private String fDestCategory;
    private IConnectionPoint fDestination;

    public SiteConnectionPoint() {
        super(TYPE);
        // the default source type is project
        fSrcCategory = WorkspaceConnectionPoint.CATEGORY;
        // the default destination type is remote
        fDestCategory = IBaseRemoteConnectionPoint.CATEGORY;
    }

    public String getSourceCategory() {
        return fSrcCategory;
    }

    public IConnectionPoint getSource() {
        return fSource;
    }

    public String getDestinationCategory() {
        return fDestCategory;
    }

    public IConnectionPoint getDestination() {
        return fDestination;
    }

    public void setSourceCategory(String category) {
        fSrcCategory = category;
    }

    public void setSource(IConnectionPoint source) {
        fSource = source;
    }

    public void setSource(String sourceName) {
        setSource(getConnectionPoint(fSrcCategory, sourceName));
    }

    public void setDestinationCategory(String category) {
        fDestCategory = category;
    }

    public void setDestination(IConnectionPoint destination) {
        fDestination = destination;
    }

    public void setDestination(String destinationName) {
        setDestination(getConnectionPoint(fDestCategory, destinationName));
    }

    /**
     * @see com.aptana.ide.core.io.ConnectionPoint#loadState(com.aptana.ide.core.epl.IMemento)
     */
    @Override
    protected void loadState(IMemento memento) {
        super.loadState(memento);

        IMemento child = memento.getChild(ELEMENT_SRC_CATEGORY);
        if (child != null) {
            fSrcCategory = child.getTextData();
        }
        child = memento.getChild(ELEMENT_SRC_NAME);
        if (child != null) {
            String name = child.getTextData();
            if (name != null) {
                fSource = getConnectionPoint(fSrcCategory, name);
            }
        }
        child = memento.getChild(ELEMENT_DEST_CATEGORY);
        if (child != null) {
            fDestCategory = child.getTextData();
        }
        child = memento.getChild(ELEMENT_DEST_NAME);
        if (child != null) {
            String name = child.getTextData();
            if (name != null) {
                fDestination = getConnectionPoint(fDestCategory, name);
            }
        }
    }

    /**
     * @see com.aptana.ide.core.io.ConnectionPoint#saveState(com.aptana.ide.core.epl.IMemento)
     */
    @Override
    protected void saveState(IMemento memento) {
        super.saveState(memento);

        memento.createChild(ELEMENT_SRC_CATEGORY).putTextData(getSourceCategory());
        IConnectionPoint connection = getSource();
        memento.createChild(ELEMENT_SRC_NAME).putTextData(
                connection == null ? "" : connection.getName()); //$NON-NLS-1$
        memento.createChild(ELEMENT_DEST_CATEGORY).putTextData(getDestinationCategory());
        connection = getDestination();
        memento.createChild(ELEMENT_DEST_NAME).putTextData(
                connection == null ? "" : connection.getName()); //$NON-NLS-1$
    }

    private static IConnectionPoint getConnectionPoint(String categoryId, String connectionName) {
        if (categoryId == null) {
            return null;
        }
        if (categoryId.equals(IBaseRemoteConnectionPoint.CATEGORY)) {
            // finds the remote connection with the specified name
            IConnectionPointCategory category = CoreIOPlugin.getConnectionPointManager()
                    .getConnectionPointCategory(IBaseRemoteConnectionPoint.CATEGORY);
            if (category != null) {
                return category.getConnectionPoint(connectionName);
            }
        } else if (categoryId.equals(WorkspaceConnectionPoint.CATEGORY)) {
            // finds the workspace container with the specified name
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IContainer container = (IContainer) root.findMember(new Path(connectionName));
            if (container != null) {
                WorkspaceConnectionPoint connection = new WorkspaceConnectionPoint(
                        (IContainer) container);
                connection.setName(connectionName);
                return connection;
            }
        } else if (categoryId.equals(LocalConnectionPoint.CATEGORY)) {
            // finds the valid path in the local file system
            Path path = new Path(connectionName);
            if (path.toFile().exists()) {
                LocalConnectionPoint connection = new LocalConnectionPoint(path);
                connection.setName(path.toOSString());
                return connection;
            }
        }
        return null;
    }
}
