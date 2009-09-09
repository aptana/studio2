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
package com.aptana.ide.syncing.ui.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.WorkspaceConnectionPoint;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.syncing.core.connection.ResourceSynchronizationUtils;
import com.aptana.ide.syncing.core.connection.SiteConnectionManager;
import com.aptana.ide.syncing.core.connection.SiteConnectionPoint;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.internal.ChooseSiteConnectionDialog;
import com.aptana.ide.syncing.ui.navigator.actions.Messages;
import com.aptana.ide.syncing.ui.views.FTPManagerView;

public class BaseSyncAction implements IObjectActionDelegate {

    private IWorkbenchPart fActivePart;
    private List<IAdaptable> fSelectedElements;

    public BaseSyncAction() {
        fSelectedElements = new ArrayList<IAdaptable>();
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        fActivePart = targetPart;
    }

    public void run(IAction action) {
        if (fSelectedElements.size() == 0) {
            return;
        }

        // gets the site connection user wants to use
        SiteConnectionPoint[] sites = getSiteConnections();
        SiteConnectionPoint site = null;
        if (sites.length == 0) {
            // the selected elements do not belong to a common source location
            MessageDialog
                    .openWarning(
                            getShell(),
                            "Warning",
                            "Unable to perform the action because the selected elements do not belong to a common site source location.");
        } else if (sites.length == 1) {
            site = sites[0];
        } else {
            // multiple connections on the selected source
            Object firstElement = fSelectedElements.get(0);
            if (firstElement instanceof IResource) {
                IContainer container = null;
                boolean remember = false;
                if (firstElement instanceof IContainer) {
                    remember = ResourceSynchronizationUtils
                            .isRememberDecision((IContainer) firstElement);
                    if (remember) {
                        container = (IContainer) firstElement;
                    }
                }
                if (!remember) {
                    IProject project = ((IResource) firstElement).getProject();
                    remember = ResourceSynchronizationUtils.isRememberDecision(project);
                    if (remember) {
                        container = project;
                    }
                }

                site = getLastSyncConnection(container);
            }

            if (site == null) {
                ChooseSiteConnectionDialog dialog = new ChooseSiteConnectionDialog(getShell(),
                        sites);
                dialog.setShowRememberMyDecision(true);
                dialog.open();

                site = dialog.getSelectedSite();
                if (site != null) {
                    setRememberMyDecision(site, dialog.isRememberMyDecision());
                }
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        action.setEnabled(false);
        fSelectedElements.clear();

        if (!(selection instanceof IStructuredSelection) || selection.isEmpty()) {
            return;
        }

        Object[] elements = ((IStructuredSelection) selection).toArray();
        for (Object element : elements) {
            if (element instanceof IAdaptable) {
                SiteConnectionPoint[] sites = SiteConnectionManager.getSitesWithSource(element);
                if (sites.length > 0) {
                    fSelectedElements.add((IAdaptable) element);
                }
            }
        }
        action.setEnabled(fSelectedElements.size() > 0);
    }

    protected Shell getShell() {
        return fActivePart.getSite().getShell();
    }

    /**
     * @return an array of all sites that contains the selected elements in
     *         their source locations
     */
    protected SiteConnectionPoint[] getSiteConnections() {
        List<Set<SiteConnectionPoint>> sitesList = new ArrayList<Set<SiteConnectionPoint>>();
        Set<SiteConnectionPoint> sitesSet;
        SiteConnectionPoint[] sites;
        for (IAdaptable element : fSelectedElements) {
            sites = SiteConnectionManager.getSitesWithSource(element);
            sitesSet = new HashSet<SiteConnectionPoint>();
            for (SiteConnectionPoint site : sites) {
                sitesSet.add(site);
            }
            sitesList.add(sitesSet);
        }
        Set<SiteConnectionPoint> sitesSets = SyncUtils.getIntersection(sitesList
                .toArray(new Set[sitesList.size()]));

        return sitesSets.toArray(new SiteConnectionPoint[sitesSets.size()]);
    }

    /**
     * Opens the FTP Manager view.
     */
    protected void openFTPManagerView() {
        // opens the FTP Manager view
        try {
            IViewPart view = CoreUIUtils.showView(FTPManagerView.ID);
            if (view != null && view instanceof FTPManagerView) {
                // selects the site if there is one
                FTPManagerView ftpView = (FTPManagerView) view;
                SiteConnectionPoint[] sites = getSiteConnections();
                if (sites.length > 0) {
                    ftpView.setSelectedSite(sites[0]);
                }
            }
        } catch (PartInitException e) {
            SyncingUIPlugin.log(Messages.DoubleClickAction_ERR_FailToOpenFTPView, e);
        }
    }

    private static SiteConnectionPoint getLastSyncConnection(IContainer container) {
        if (container == null) {
            return null;
        }

        String lastConnection = ResourceSynchronizationUtils.getLastSyncConnection(container);
        if (lastConnection == null) {
            return null;
        }

        SiteConnectionPoint[] sites = SiteConnectionManager.getSitesWithSource(container, true);
        String target;
        for (SiteConnectionPoint site : sites) {
            target = site.getDestination().getName();
            if (target.equals(lastConnection)) {
                return site;
            }
        }
        return null;
    }

    private void setRememberMyDecision(SiteConnectionPoint site, boolean rememberMyDecision) {
        IConnectionPoint source = site.getSource();
        if (!(source instanceof WorkspaceConnectionPoint)) {
            return;
        }

        IContainer container = ((WorkspaceConnectionPoint) source).getResource();
        if (rememberMyDecision) {
            ResourceSynchronizationUtils.setRememberDecision(container, rememberMyDecision);
        }

        // remembers the last sync connection
        ResourceSynchronizationUtils.setLastSyncConnection(container, site.getDestination()
                .getName());
    }
}
