/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.syncing.ui.wizards;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointManager;
import com.aptana.ide.filesystem.ftp.IBaseFTPConnectionPoint;
import com.aptana.ide.ui.IPropertyDialog;
import com.aptana.ide.ui.ftp.internal.FTPPropertyDialogProvider;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Michael Xia (mxia@aptana.com)
 */
@SuppressWarnings("restriction")
public class RemoteConnectionSelectionPage extends WizardPage implements SelectionListener {

    private Table connectionTable;
    private Button newSiteButton;
    private Button syncOnFinish;

    private boolean synchronize = true;
    private IConnectionPoint site;

    /**
     * @param selection
     *            the initial selection
     */
    public RemoteConnectionSelectionPage(IStructuredSelection selection) {
        super("connectionPage"); //$NON-NLS-1$
        if (selection != null) {
            Object possibleSite = selection.getFirstElement();
            if (possibleSite != null && possibleSite instanceof IConnectionPoint) {
                site = (IConnectionPoint) possibleSite;
            }
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout());
        main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // a group to show the list of connections
        Group connGroup = new Group(main, SWT.NONE);
        connGroup.setText(Messages.RemoteConnectionSelectionPage_Connections);
        connGroup.setLayout(new GridLayout());
        connGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label label = new Label(connGroup, SWT.NONE);
        label.setText(Messages.RemoteConnectionSelectionPage_LBL_SelectRemoteLocationOfThisProject);

        connectionTable = new Table(connGroup, SWT.CHECK | SWT.SINGLE | SWT.FULL_SELECTION
                | SWT.BORDER);
        connectionTable.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                if (e.detail == SWT.CHECK) {
                    // unchecks the other items
                    TableItem[] items = connectionTable.getItems();
                    for (TableItem item : items) {
                        if (item != e.item) {
                            item.setChecked(false);
                        }
                    }

                    TableItem item = (TableItem) e.item;
                    if (item.getChecked()) {
                        setErrorMessage(null);
                        setPageComplete(true);
                        site = (IConnectionPoint) item.getData();
                    } else {
                        setErrorMessage(Messages.RemoteConnectionSelectionPage_ERR_SelectSiteOrCreateNewOne);
                        setPageComplete(false);
                    }
                }
            }
        });
        connectionTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        connectionTable.setLinesVisible(true);
        new TableColumn(connectionTable, SWT.LEFT);
        connectionTable.addControlListener(new ControlAdapter() {

            public void controlResized(ControlEvent e) {
                TableColumn c = connectionTable.getColumn(0);
                Point size = connectionTable.getSize();

                // Mac fix for always having a vertical scrollbar and not
                // calculating it affects the horizontal scroll bar
                if (Platform.getOS().equals(Platform.OS_MACOSX)) {
                    ScrollBar vScrolls = connectionTable.getVerticalBar();
                    if (vScrolls != null) {
                        size.x = size.x - vScrolls.getSize().x - 10;
                    }
                }
                c.setWidth(size.x - 6);
            }

        });
        populateTable();

        if (site == null) {
            setErrorMessage(Messages.RemoteConnectionSelectionPage_ERR_SelectSiteOrCreateNewOne);
            setPageComplete(false);
        } else {
            checkItem(site);
        }

        newSiteButton = new Button(connGroup, SWT.PUSH);
        newSiteButton.setText(StringUtils
                .ellipsify(Messages.RemoteConnectionSelectionPage_LBL_NewConnection));
        newSiteButton.addSelectionListener(this);

        Group options = new Group(main, SWT.NONE);
        options.setText(Messages.RemoteConnectionSelectionPage_LBL_Options);
        options.setLayout(new GridLayout(1, true));
        options.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        syncOnFinish = new Button(options, SWT.CHECK);
        syncOnFinish.setText(Messages.RemoteConnectionSelectionPage_LBL_DownloadOnFinish);
        syncOnFinish.setSelection(isSynchronize());
        syncOnFinish.addSelectionListener(this);

        setControl(main);
    }

    private void checkItem(IConnectionPoint item) {
        TableItem[] items = connectionTable.getItems();
        for (TableItem ti : items) {
            if (ti.getData() == item) {
                ti.setChecked(true);
                site = item;
                setErrorMessage(null);
                setPageComplete(true);
                break;
            }
        }
    }

    private void populateTable() {
        connectionTable.removeAll();

        IConnectionPointManager manager = CoreIOPlugin.getConnectionPointManager();
        IConnectionPoint[] remoteSites = manager.getConnectionPointCategory(
                IBaseRemoteConnectionPoint.CATEGORY).getConnectionPoints();
        for (IConnectionPoint site : remoteSites) {
            if (site instanceof IBaseRemoteConnectionPoint) {
                TableItem item = new TableItem(connectionTable, SWT.NONE);
                item.setText(MessageFormat
                        .format("{0}: {1}", manager.getType(site).getName(), site.getName())); //$NON-NLS-1$
                item.setData(site);
            }
        }
    }

    /**
     * @return the synchronize
     */
    public boolean isSynchronize() {
        return synchronize;
    }

    /**
     * @param synchronize
     *            the synchronize to set
     */
    public void setSynchronize(boolean synchronize) {
        this.synchronize = synchronize;
    }

    /**
     * @return the site
     */
    public IConnectionPoint getSite() {
        return site;
    }

    /**
     * @param site
     *            the site to set
     */
    public void setSite(IConnectionPoint site) {
        this.site = site;
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        Object source = e.getSource();

        if (source == newSiteButton) {
            final Shell shell = getShell();
            Dialog dlg = new FTPPropertyDialogProvider().createPropertyDialog(new IShellProvider() {

                public Shell getShell() {
                    return shell;
                }
            });
            if (dlg instanceof IPropertyDialog) {
                ((IPropertyDialog) dlg).setPropertySource(CoreIOPlugin.getConnectionPointManager()
                        .getType(IBaseFTPConnectionPoint.TYPE_FTP));
            }
            int ret = dlg.open();

            if (ret == Window.OK) {
                populateTable();

                if (dlg instanceof IPropertyDialog) {
                    checkItem((IConnectionPoint) ((IPropertyDialog) dlg).getPropertySource());
                }
            }
        } else if (source == syncOnFinish) {
            setSynchronize(syncOnFinish.getSelection());
        }
    }
}
