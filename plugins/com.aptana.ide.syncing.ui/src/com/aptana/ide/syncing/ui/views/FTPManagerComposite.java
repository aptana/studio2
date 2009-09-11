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
package com.aptana.ide.syncing.ui.views;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.EFSUtils;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointEvent;
import com.aptana.ide.core.io.IConnectionPointListener;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.syncing.core.connection.SiteConnectionManager;
import com.aptana.ide.syncing.core.connection.SiteConnectionPoint;
import com.aptana.ide.syncing.ui.internal.NewSiteDialog;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.actions.CopyFilesOperation;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FTPManagerComposite implements SelectionListener, IConnectionPointListener {

    private Composite fMain;
    private Combo fSitesCombo;
    private Button fEditButton;
    private ConnectionPointComposite fSource;
    private ConnectionPointComposite fTarget;
    private Button fTransferRightButton;
    private Button fTransferLeftButton;

    public FTPManagerComposite(Composite parent) {
        fMain = createControl(parent);
        CoreIOPlugin.getConnectionPointManager().addConnectionPointListener(this);
    }

    public void dispose() {
        CoreIOPlugin.getConnectionPointManager().removeConnectionPointListener(this);
    }

    public Control getControl() {
        return fMain;
    }

    public void setFocus() {
        fMain.setFocus();
    }

    public void setSelectedSite(SiteConnectionPoint site) {
        if (site == null) {
            fSitesCombo.clearSelection();
            fSource.setConnectionPoint(null);
            fTarget.setConnectionPoint(null);
        } else {
            fSitesCombo.setText(site.getName());
            fSource.setConnectionPoint(site.getSource());
            fTarget.setConnectionPoint(site.getDestination());
        }
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        Object source = e.getSource();

        if (source == fSitesCombo) {
            update();
        } else if (source == fEditButton) {
            NewSiteDialog dialog = new NewSiteDialog(fMain.getShell(), false);
            dialog.setSelectedSite(fSitesCombo.getText());
            dialog.open();
        } else if (source == fTransferRightButton) {
            transferItems(fSource.getSelectedElements(), fTarget.getCurrentInput(),
                    new JobChangeAdapter() {

                        @Override
                        public void done(IJobChangeEvent event) {
                            IOUIPlugin.refreshNavigatorView(fTarget.getCurrentInput());
                            CoreUIUtils.getDisplay().asyncExec(new Runnable() {

                                public void run() {
                                    fTarget.refresh();
                                }
                            });
                        }
                    });
        } else if (source == fTransferLeftButton) {
            transferItems(fTarget.getSelectedElements(), fSource.getCurrentInput(),
                    new JobChangeAdapter() {

                        @Override
                        public void done(IJobChangeEvent event) {
                            IOUIPlugin.refreshNavigatorView(fSource.getCurrentInput());
                            CoreUIUtils.getDisplay().asyncExec(new Runnable() {

                                public void run() {
                                    fSource.refresh();
                                }
                            });
                        }
                    });
        }
    }

    public void connectionPointChanged(IConnectionPointEvent event) {
        final int type = event.getType();
        final IConnectionPoint connection = event.getConnectionPoint();

        if ((type == IConnectionPointEvent.POST_ADD || type == IConnectionPointEvent.POST_DELETE)
                && (connection instanceof SiteConnectionPoint)) {
            fMain.getDisplay().asyncExec(new Runnable() {

                public void run() {
                    // updates the drop-down list
                    fSitesCombo.setItems(getExistingSiteNames());

                    if (type == IConnectionPointEvent.POST_ADD) {
                        // auto-selects the new site
                        setSelectedSite((SiteConnectionPoint) connection);
                    } else if (type == IConnectionPointEvent.POST_DELETE) {
                        if (fSitesCombo.getItemCount() == 0) {
                            setSelectedSite(null);
                        } else {
                            // selects the first site
                            fSitesCombo.select(0);
                            setSelectedSite(SiteConnectionManager.getExistingSites()[0]);
                        }
                    }
                }
            });
        }
    }

    protected Composite createControl(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        main.setLayout(layout);

        Composite top = createSiteInfo(main);
        top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Composite middle = createSitePresentation(main);
        middle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        update();

        return main;
    }

    private Composite createSiteInfo(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(3, false));

        Label label = new Label(main, SWT.NONE);
        label.setText(Messages.FTPManagerComposite_LBL_Sites);

        fSitesCombo = new Combo(main, SWT.READ_ONLY);
        fSitesCombo.setItems(getExistingSiteNames());
        fSitesCombo.select(0);
        GridData gridData = new GridData();
        gridData.widthHint = 250;
        fSitesCombo.setLayoutData(gridData);
        fSitesCombo.addSelectionListener(this);

        fEditButton = new Button(main, SWT.PUSH);
        fEditButton.setText(StringUtils.ellipsify(CoreStrings.EDIT));
        fEditButton.addSelectionListener(this);

        return main;
    }

    private Composite createSitePresentation(Composite parent) {
        Composite main = new Composite(parent, SWT.BORDER);
        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        main.setLayout(layout);

        fSource = new ConnectionPointComposite(main, Messages.FTPManagerComposite_LBL_Source);
        fSource.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite directions = new Composite(main, SWT.NONE);
        layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        directions.setLayout(layout);
        directions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));

        fTransferRightButton = new Button(directions, SWT.ARROW | SWT.RIGHT);
        fTransferRightButton.addSelectionListener(this);
        fTransferLeftButton = new Button(directions, SWT.ARROW | SWT.LEFT);
        fTransferLeftButton.addSelectionListener(this);

        fTarget = new ConnectionPointComposite(main, Messages.FTPManagerComposite_LBL_Target);
        fTarget.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return main;
    }

    private void transferItems(IAdaptable[] sourceItems, IAdaptable targetRoot,
            IJobChangeListener listener) {
        IFileStore targetStore = getFileStore(targetRoot);
        if (targetStore != null) {
            CopyFilesOperation operation = new CopyFilesOperation(getControl().getShell());
            operation.copyFiles(sourceItems, targetStore, listener);
        }
    }

    private void update() {
        String siteName = fSitesCombo.getText();
        SiteConnectionPoint[] sites = SiteConnectionManager.getExistingSites();
        for (SiteConnectionPoint site : sites) {
            if (site.getName().equals(siteName)) {
                setSelectedSite(site);
                break;
            }
        }
    }

    private static String[] getExistingSiteNames() {
        SiteConnectionPoint[] sites = SiteConnectionManager.getExistingSites();
        String[] names = new String[sites.length];
        for (int i = 0; i < names.length; ++i) {
            names[i] = sites[i].getName();
        }
        return names;
    }

    private static IFileStore getFileStore(IAdaptable adaptable) {
        if (adaptable instanceof IResource) {
        	return EFSUtils.getFileStore((IResource) adaptable);
        }
        return (IFileStore) adaptable.getAdapter(IFileStore.class);
    }
}
