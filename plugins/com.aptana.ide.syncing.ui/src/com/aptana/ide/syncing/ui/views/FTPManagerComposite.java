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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointEvent;
import com.aptana.ide.core.io.IConnectionPointListener;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.syncing.core.connection.SiteConnectionManager;
import com.aptana.ide.syncing.core.connection.SiteConnectionPoint;
import com.aptana.ide.syncing.ui.editors.EditorUtils;
import com.aptana.ide.syncing.ui.internal.NewSiteDialog;
import com.aptana.ide.syncing.ui.internal.SyncUtils;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.actions.CopyFilesOperation;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FTPManagerComposite implements SelectionListener, IConnectionPointListener {

    public static interface Listener {
        public void siteConnectionChanged(SiteConnectionPoint site);
    }

    private Composite fMain;
    private Combo fSitesCombo;
    private Button fEditButton;
    private Button fSaveAsButton;
    private ConnectionPointComposite fSource;
    private ConnectionPointComposite fTarget;
    private Button fTransferRightButton;
    private Button fTransferLeftButton;

    private SiteConnectionPoint fSelectedSite;
    private List<Listener> fListeners;

    public FTPManagerComposite(Composite parent) {
        fListeners = new ArrayList<Listener>();
        fMain = createControl(parent);
        CoreIOPlugin.getConnectionPointManager().addConnectionPointListener(this);
    }

    public void addListener(Listener listener) {
        if (!fListeners.contains(listener)) {
            fListeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        fListeners.remove(listener);
    }

    public void dispose() {
        fSelectedSite = null;
        fListeners.clear();
        CoreIOPlugin.getConnectionPointManager().removeConnectionPointListener(this);
    }

    public Control getControl() {
        return fMain;
    }

    public void setFocus() {
        fMain.setFocus();
    }

    public void setSelectedSite(SiteConnectionPoint site) {
        if (site == fSelectedSite) {
            return;
        }
        fSelectedSite = site;
        if (site == null) {
            fSitesCombo.clearSelection();
            fSource.setConnectionPoint(null);
            fTarget.setConnectionPoint(null);
        } else {
            fSitesCombo.setText(site.getName());
            fSource.setConnectionPoint(site.getSource());
            fTarget.setConnectionPoint(site.getDestination());
        }
        fireSiteConnectionChanged(fSelectedSite);
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        Object source = e.getSource();

        if (source == fSitesCombo) {
            update();
        } else if (source == fEditButton) {
            // opens the connection manager with the current connection selected
            NewSiteDialog dialog = new NewSiteDialog(fMain.getShell(), false);
            dialog.setSelectedSite(fSitesCombo.getText());
            dialog.open();
        } else if (source == fSaveAsButton) {
            saveAs();
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
                    String text = fSitesCombo.getText();
                    fSitesCombo.setItems(getExistingSiteNames());
                    fSitesCombo.setText(text);
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
        main.setLayout(new GridLayout(4, false));

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

        fSaveAsButton = new Button(main, SWT.PUSH);
        fSaveAsButton.setText(StringUtils.ellipsify(Messages.FTPManagerComposite_LBL_SaveAs));
        fSaveAsButton.addSelectionListener(this);

        return main;
    }

    private Composite createSitePresentation(Composite parent) {
        SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        sash.setLayout(layout);

        // source end point
        fSource = new ConnectionPointComposite(sash, Messages.FTPManagerComposite_LBL_Source);
        fSource.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite right = new Composite(sash, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        right.setLayout(layout);

        // transfer arrows
        Composite directions = new Composite(right, SWT.NONE);
        layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        directions.setLayout(layout);
        directions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));

        fTransferRightButton = new Button(directions, SWT.BORDER);
        fTransferRightButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
                ISharedImages.IMG_TOOL_FORWARD));
        fTransferRightButton.setToolTipText(Messages.FTPManagerComposite_TTP_TransferRight);
        fTransferRightButton.addSelectionListener(this);
        fTransferLeftButton = new Button(directions, SWT.BORDER);
        fTransferLeftButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
                ISharedImages.IMG_TOOL_BACK));
        fTransferLeftButton.setToolTipText(Messages.FTPManagerComposite_TTP_TransferLeft);
        fTransferLeftButton.addSelectionListener(this);

        // destination end point
        fTarget = new ConnectionPointComposite(right, Messages.FTPManagerComposite_LBL_Target);
        fTarget.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return sash;
    }

    private void saveAs() {
        // builds the initial value from the current selection
        String initialValue = fSitesCombo.getText();
        if (initialValue.length() > 0) {
            initialValue = "Copy of " + initialValue; //$NON-NLS-1$
        }
        InputDialog dialog = new InputDialog(fMain.getShell(),
                Messages.FTPManagerComposite_NameInput_Title,
                Messages.FTPManagerComposite_NameInput_Message, initialValue,
                new IInputValidator() {

                    public String isValid(String newText) {
                        if (newText.trim().length() == 0) {
                            return Messages.FTPManagerComposite_ERR_EmptyName;
                        }
                        if (fSitesCombo.indexOf(newText) > -1) {
                            return MessageFormat.format(
                                    Messages.FTPManagerComposite_ERR_NameExists, newText);
                        }
                        return null;
                    }

                });
        dialog.open();

        if (dialog.getReturnCode() != Window.OK) {
            return;
        }
        String name = dialog.getValue();
        IConnectionPoint connection = null;
        try {
            connection = CoreIOPlugin.getConnectionPointManager().createConnectionPoint(
                    SiteConnectionPoint.TYPE);
        } catch (CoreException e) {
            return;
        }
        if (!(connection instanceof SiteConnectionPoint)) {
            // should not happen
            return;
        }
        SiteConnectionPoint newSite = (SiteConnectionPoint) connection;
        newSite.setName(name);
        if (fSelectedSite != null) {
            newSite.setSourceCategory(fSelectedSite.getSourceCategory());
            newSite.setSource(fSelectedSite.getSource());
            newSite.setDestinationCategory(fSelectedSite.getDestinationCategory());
            newSite.setDestination(fSelectedSite.getDestination());
        }
        CoreIOPlugin.getConnectionPointManager().addConnectionPoint(connection);

        // opens the connection in a new editor
        EditorUtils.openConnectionEditor(newSite);
    }

    private void transferItems(IAdaptable[] sourceItems, IAdaptable targetRoot,
            IJobChangeListener listener) {
        IFileStore targetStore = SyncUtils.getFileStore(targetRoot);
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

    private void fireSiteConnectionChanged(SiteConnectionPoint site) {
        for (Listener listener : fListeners) {
            listener.siteConnectionChanged(site);
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
}
