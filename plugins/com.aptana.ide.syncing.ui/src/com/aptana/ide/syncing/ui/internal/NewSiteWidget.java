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
package com.aptana.ide.syncing.ui.internal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.dialogs.FileFolderSelectionDialog;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointCategory;
import com.aptana.ide.core.io.IConnectionPointManager;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.core.io.WorkspaceConnectionPoint;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.filesystem.ftp.FTPConnectionPoint;
import com.aptana.ide.syncing.core.connection.SiteConnectionPoint;
import com.aptana.ide.ui.IPropertyDialog;
import com.aptana.ide.ui.ftp.dialogs.FTPConnectionPointPropertyDialog;
import com.aptana.ide.ui.ftp.internal.FTPPropertyDialogProvider;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class NewSiteWidget implements SelectionListener, ModifyListener, MouseListener {

    private Composite fMain;
    private Table fSitesTable;
    private TableEditor fSitesEditor;
    private Button fAddButton;
    private Button fRemoveButton;
    private Button fSrcProjectButton;
    private Combo fSrcProjectCombo;
    private Text fSrcFolderText;
    private Button fSrcFolderBrowse;
    private Button fSrcFileButton;
    private Text fSrcFileText;
    private Button fSrcFileBrowse;
    private Button fDestRemoteButton;
    private Combo fDestRemoteCombo;
    private Button fDestRemoteNew;
    private Button fDestProjectButton;
    private Combo fDestProjectCombo;
    private Text fDestFolderText;
    private Button fDestFolderBrowse;
    private Button fDestFileButton;
    private Text fDestFileText;
    private Button fDestFileBrowse;

    private boolean fCreateNew;

    private java.util.List<SiteConnectionPoint> fSites;
    private java.util.List<SiteConnectionPoint> fOriginalSites;

    // for appending a number to "New Site" to avoid duplicates
    private int fCount;

    public NewSiteWidget(Composite parent) {
        this(parent, false);
    }

    /**
     * @param parent
     *            the parent composite
     * @param createNew
     *            true if a new site entry should be created by default, false
     *            otherwise
     */
    public NewSiteWidget(Composite parent, boolean createNew) {
        fCreateNew = createNew;
        fSites = new ArrayList<SiteConnectionPoint>();
        fOriginalSites = new ArrayList<SiteConnectionPoint>();
        loadSites();

        fMain = createControl(parent);
    }

    public void setSelectedSite(String siteName) {
        if (siteName == null) {
            return;
        }
        SiteConnectionPoint site;
        int count = fSites.size();
        for (int i = 0; i < count; ++i) {
            site = fSites.get(i);
            if (site.getName().equals(siteName)) {
                fSitesTable.select(i);
                updateSelection(site);
                break;
            }
        }
    }

    public void setSource(IAdaptable source) {
        if (source == null) {
            return;
        }
        IContainer container = getContainer(source);
        if (container != null) {
            fSrcProjectButton.setSelection(true);
            fSrcProjectCombo.setText(container.getProject().getName());
            fSrcFolderText.setText(container.getProjectRelativePath().toString());
        }

        updateEnabledStates();
    }

    public void setDestination(IAdaptable target) {
        if (target == null) {
            return;
        }
        IContainer container = getContainer(target);
        if (container != null) {
            fDestProjectButton.setSelection(true);
            fDestProjectCombo.setText(container.getProject().getName());
            fDestFolderText.setText(container.getProjectRelativePath().toString());
        }

        updateEnabledStates();
    }

    public Control getControl() {
        return fMain;
    }

    public boolean apply() {
        String message = verify();
        if (message != null) {
            MessageDialog.openError(fMain.getShell(), "Error", message); //$NON-NLS-1$
            return false;
        }

        IConnectionPointManager manager = CoreIOPlugin.getConnectionPointManager();
        for (SiteConnectionPoint site : fOriginalSites) {
            manager.removeConnectionPoint(site);
        }
        for (SiteConnectionPoint site : fSites) {
            manager.addConnectionPoint(site);
        }
        return true;
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        SiteConnectionPoint site = getSelectedSite();
        if (site == null) {
            return;
        }

        Object source = e.getSource();
        if (source == fSrcProjectButton || source == fSrcFileButton || source == fDestRemoteButton
                || source == fDestProjectButton || source == fDestFileButton) {
            updateEnabledStates();

            if (source == fSrcProjectButton || source == fSrcFileButton) {
                site.setSourceCategory(getSourceCategory());
            } else {
                site.setDestinationCategory(getDestinationCategory());
            }
        } else if (source == fSrcProjectCombo) {
            site.setSource(getSourceName());
        } else if (source == fSrcFolderBrowse) {
            String folder = openFolderBrowseDialog();
            if (folder != null) {
                fSrcFolderText.setText(folder);
                site.setSource(getSourceName());
            }
        } else if (source == fSrcFileBrowse) {
            String dir = openFileBrowseDialog();
            if (dir != null) {
                fSrcFileText.setText(dir);
                site.setSource(getSourceName());
            }
        } else if (source == fDestRemoteCombo) {
            site.setDestination(getDestinationName());
        } else if (source == fDestRemoteNew) {
            createNewFTPConnection();
            site.setDestination(getDestinationName());
        } else if (source == fDestProjectCombo) {
            site.setDestination(getDestinationName());
        } else if (source == fDestFolderBrowse) {
            String folder = openFolderBrowseDialog();
            if (folder != null) {
                fDestFolderText.setText(folder);
                site.setDestination(getDestinationName());
            }
        } else if (source == fDestFileBrowse) {
            String dir = openFileBrowseDialog();
            if (dir != null) {
                fDestFileText.setText(dir);
                site.setDestination(getDestinationName());
            }
        } else if (source == fAddButton) {
            fSitesTable.deselectAll();
            createNewSite();

            int index = fSitesTable.getItemCount() - 1;
            fSitesTable.select(index);
            updateSelection(fSites.get(index));
        } else if (source == fRemoveButton) {
            int index = fSitesTable.getSelectionIndex();
            if (index > -1) {
                fSites.remove(index);
                clearTableEditor();
                fSitesTable.remove(index);

                index = fSitesTable.getSelectionIndex();
                if (index > -1) {
                    updateSelection(fSites.get(index));
                }
            }
        } else if (source == fSitesTable) {
            clearTableEditor();

            int index = fSitesTable.getSelectionIndex();
            if (index > -1) {
                updateSelection(fSites.get(index));
            }
        }
    }

    public void modifyText(ModifyEvent e) {
        SiteConnectionPoint site = getSelectedSite();
        if (site == null) {
            return;
        }

        Object source = e.getSource();
        if (source == fSrcFolderText || source == fSrcFileText) {
            site.setSource(getSourceName());
        } else if (source == fDestFolderText || source == fDestFileText) {
            site.setDestination(getDestinationName());
        }
    }

    public void mouseDoubleClick(MouseEvent e) {
        Object source = e.getSource();

        if (source == fSitesTable) {
            // edits the site name when the selection is double-clicked
            editSiteName(fSitesTable.getSelection()[0], fSites.get(fSitesTable.getSelectionIndex()));
        }
    }

    public void mouseDown(MouseEvent e) {
    }

    public void mouseUp(MouseEvent e) {
    }

    protected Composite createControl(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        main.setLayout(layout);

        // the left side shows the list of existing sites
        Composite left = createSitesList(main);
        left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // the right side shows the details on the selected site from the left
        Composite right = createSiteDetails(main);
        right.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // adds the existing sites
        for (SiteConnectionPoint site : fOriginalSites) {
            createNewSiteItem(site);
        }
        if (fCreateNew) {
            createNewSite();
            fSitesTable.select(fSitesTable.getItemCount() - 1);
            updateEnabledStates();
        } else {
            clearTableEditor();
            fSitesTable.select(0);
            updateSelection(fOriginalSites.get(0));
        }

        return main;
    }

    private Composite createSitesList(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        main.setLayout(layout);

        Group group = new Group(main, SWT.NONE);
        group.setText(Messages.NewSiteWidget_LBL_Sites);
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // uses table widget for an editable list
        fSitesTable = new Table(group, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
        fSitesTable.setHeaderVisible(false);
        fSitesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        TableColumn column = new TableColumn(fSitesTable, SWT.NONE);
        column.setWidth(150);
        fSitesEditor = new TableEditor(fSitesTable);
        fSitesEditor.horizontalAlignment = SWT.LEFT;
        fSitesEditor.grabHorizontal = true;
        fSitesEditor.minimumWidth = 50;
        fSitesTable.addSelectionListener(this);
        fSitesTable.addMouseListener(this);

        // the actions to add to and remove from the list
        Composite buttons = new Composite(group, SWT.NULL);
        layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        buttons.setLayout(layout);
        buttons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

        fAddButton = new Button(buttons, SWT.PUSH);
        fAddButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/add.gif")); //$NON-NLS-1$
        fAddButton.setToolTipText(StringUtils.ellipsify(CoreStrings.ADD));
        fAddButton.addSelectionListener(this);

        fRemoveButton = new Button(buttons, SWT.PUSH);
        fRemoveButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/delete.gif")); //$NON-NLS-1$
        fRemoveButton.setToolTipText(CoreStrings.REMOVE);
        fRemoveButton.addSelectionListener(this);

        return main;
    }

    private Composite createSiteDetails(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        main.setLayout(layout);

        // the source info
        Group group = new Group(main, SWT.NONE);
        group.setText(Messages.NewSiteWidget_LBL_Source);
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Label label = new Label(group, SWT.NONE);
        label.setText(Messages.NewSiteWidget_LBL_SelectSrcLocation);

        Composite location = new Composite(group, SWT.NONE);
        layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        location.setLayout(layout);

        // project specs
        fSrcProjectButton = new Button(location, SWT.RADIO);
        fSrcProjectButton.setText(Messages.NewSiteWidget_LBL_Project);
        fSrcProjectButton.setSelection(true);
        fSrcProjectButton.addSelectionListener(this);

        fSrcProjectCombo = new Combo(location, SWT.READ_ONLY);
        String[] projects = getWorkspaceProjectNames();
        fSrcProjectCombo.setItems(projects);
        fSrcProjectCombo.select(0);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.horizontalSpan = 2;
        fSrcProjectCombo.setLayoutData(gridData);
        fSrcProjectCombo.addSelectionListener(this);

        // empty placeholder label so the folders are intended to the right
        new Label(location, SWT.NONE);

        Composite folder = new Composite(location, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        folder.setLayout(layout);
        folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        label = new Label(folder, SWT.NONE);
        label.setText(Messages.NewSiteWidget_LBL_Folder);
        fSrcFolderText = new Text(folder, SWT.BORDER | SWT.READ_ONLY);
        fSrcFolderText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        fSrcFolderText.addModifyListener(this);

        fSrcFolderBrowse = new Button(location, SWT.PUSH);
        fSrcFolderBrowse.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
        fSrcFolderBrowse.addSelectionListener(this);

        // filesystem specs
        fSrcFileButton = new Button(location, SWT.RADIO);
        fSrcFileButton.setText(Messages.NewSiteWidget_LBL_Filesystem);
        fSrcFileButton.addSelectionListener(this);

        fSrcFileText = new Text(location, SWT.BORDER);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.widthHint = 250;
        fSrcFileText.setLayoutData(gridData);
        fSrcFileText.addModifyListener(this);

        fSrcFileBrowse = new Button(location, SWT.PUSH);
        fSrcFileBrowse.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
        fSrcFileBrowse.addSelectionListener(this);

        // the destination info
        group = new Group(main, SWT.NONE);
        group.setText(Messages.NewSiteWidget_LBL_Destination);
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        label = new Label(group, SWT.NONE);
        label.setText(Messages.NewSiteWidget_LBL_SelectDestTarget);

        location = new Composite(group, SWT.NONE);
        layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        location.setLayout(layout);

        // remote specs
        fDestRemoteButton = new Button(location, SWT.RADIO);
        fDestRemoteButton.setText(Messages.NewSiteWidget_LBL_Remote);
        fDestRemoteButton.setSelection(true);
        fDestRemoteButton.addSelectionListener(this);

        fDestRemoteCombo = new Combo(location, SWT.READ_ONLY);
        fDestRemoteCombo.setItems(getExistingFTPSiteNames());
        fDestRemoteCombo.select(0);
        fDestRemoteCombo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        fDestRemoteCombo.addSelectionListener(this);

        fDestRemoteNew = new Button(location, SWT.PUSH);
        fDestRemoteNew.setText(StringUtils.ellipsify(CoreStrings.NEW));
        fDestRemoteNew.addSelectionListener(this);

        // project specs
        fDestProjectButton = new Button(location, SWT.RADIO);
        fDestProjectButton.setText(Messages.NewSiteWidget_LBL_Project);
        fDestProjectButton.addSelectionListener(this);

        fDestProjectCombo = new Combo(location, SWT.READ_ONLY);
        fDestProjectCombo.setItems(projects);
        fDestProjectCombo.select(0);
        gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        gridData.horizontalSpan = 2;
        fDestProjectCombo.setLayoutData(gridData);
        fDestProjectCombo.addSelectionListener(this);

        // empty placeholder label so the folders are intended to the right
        new Label(location, SWT.NONE);

        folder = new Composite(location, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        folder.setLayout(layout);
        folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        label = new Label(folder, SWT.NONE);
        label.setText(Messages.NewSiteWidget_LBL_Folder);
        fDestFolderText = new Text(folder, SWT.BORDER | SWT.READ_ONLY);
        fDestFolderText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        fDestFolderText.addModifyListener(this);

        fDestFolderBrowse = new Button(location, SWT.PUSH);
        fDestFolderBrowse.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
        fDestFolderBrowse.addSelectionListener(this);

        // filesystem specs
        fDestFileButton = new Button(location, SWT.RADIO);
        fDestFileButton.setText(Messages.NewSiteWidget_LBL_Filesystem);
        fDestFileButton.addSelectionListener(this);

        fDestFileText = new Text(location, SWT.BORDER);
        gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        gridData.widthHint = 250;
        fDestFileText.setLayoutData(gridData);
        fDestFileText.addModifyListener(this);

        fDestFileBrowse = new Button(location, SWT.PUSH);
        fDestFileBrowse.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
        fDestFileBrowse.addSelectionListener(this);

        return main;
    }

    private void createNewSite() {
        IConnectionPoint connection;
        try {
            connection = CoreIOPlugin.getConnectionPointManager().createConnectionPoint(
                    SiteConnectionPoint.TYPE);
        } catch (CoreException e) {
            return;
        }
        if (!(connection instanceof SiteConnectionPoint)) {
            return;
        }
        SiteConnectionPoint newSite = (SiteConnectionPoint) connection;
        newSite.setName(getUniqueNewSiteName());
        newSite.setSourceCategory(getSourceCategory());
        newSite.setSource(getSourceName());
        newSite.setDestinationCategory(getDestinationCategory());
        newSite.setDestination(getDestinationName());
        fSites.add(newSite);

        createNewSiteItem(newSite);
    }

    private TableItem createNewSiteItem(SiteConnectionPoint newSite) {
        TableItem item = new TableItem(fSitesTable, SWT.NONE);
        item.setText(newSite.getName());
        editSiteName(item, newSite);

        return item;
    }

    private void clearTableEditor() {
        Control oldEditor = fSitesEditor.getEditor();
        if (oldEditor != null) {
            oldEditor.dispose();
        }
    }

    private void editSiteName(TableItem item, final SiteConnectionPoint site) {
        // cleans up any previous editor control
        clearTableEditor();
        if (item == null) {
            return;
        }

        // makes the text editable
        Text newEditor = new Text(fSitesTable, SWT.NONE);
        newEditor.setText(item.getText(0));
        newEditor.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent me) {
                Text text = (Text) fSitesEditor.getEditor();
                fSitesEditor.getItem().setText(0, text.getText());
                site.setName(text.getText());
            }
        });
        newEditor.selectAll();
        newEditor.setFocus();
        fSitesEditor.setEditor(newEditor, item, 0);
    }

    private String openFolderBrowseDialog() {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
                fSrcProjectCombo.getText());
        IFileStore fileStore = null;
        try {
            fileStore = EFS.getStore(project.getLocationURI());
        } catch (CoreException e) {
        }

        FileFolderSelectionDialog dialog = new FileFolderSelectionDialog(fMain.getShell(), false,
                IResource.FOLDER);
        if (fileStore != null) {
            dialog.setInput(fileStore);
        }
        dialog.open();

        // computes the folder directory relative to the project from the user
        // selection
        Object result = dialog.getFirstResult();
        if (result == null) {
            return null;
        }
        String text = result.toString();
        if (fileStore == null) {
            return text;
        }
        int index = text.indexOf(fileStore.toString());
        if (index < 0) {
            return text;
        }
        return text.substring(index + fileStore.toString().length());
    }

    private String openFileBrowseDialog() {
        DirectoryDialog dialog = new DirectoryDialog(fMain.getShell());
        return dialog.open();
    }

    private void createNewFTPConnection() {
        Dialog dialog = new FTPPropertyDialogProvider().createPropertyDialog(new IShellProvider() {

            public Shell getShell() {
                return fMain.getShell();
            }

        });
        if (dialog instanceof IPropertyDialog) {
            ((IPropertyDialog) dialog).setPropertySource(CoreIOPlugin.getConnectionPointManager()
                    .getType(FTPConnectionPoint.TYPE));
        }
        if (dialog.open() == Window.OK) {
            if (dialog instanceof FTPConnectionPointPropertyDialog) {
                IConnectionPoint connection = ((FTPConnectionPointPropertyDialog) dialog)
                        .getConnectionPoint();
                if (connection != null) {
                    fDestRemoteCombo.setItems(getExistingFTPSiteNames());
                    fDestRemoteCombo.setText(connection.getName());
                }
            }
        }
    }

    private void loadSites() {
        fSites.clear();
        fOriginalSites.clear();

        IConnectionPointCategory category = getConnectionCategory(SiteConnectionPoint.CATEGORY);
        if (category == null) {
            // should not happen
            return;
        }
        IConnectionPoint[] connections = category.getConnectionPoints();
        for (IConnectionPoint connection : connections) {
            if (connection instanceof SiteConnectionPoint) {
                SiteConnectionPoint site = (SiteConnectionPoint) connection;
                fSites.add(site);
                fOriginalSites.add(site);
            }
        }
    }

    private void updateEnabledStates() {
        if (fSrcProjectButton.getSelection()) {
            fSrcProjectCombo.setEnabled(true);
            fSrcFolderText.setEnabled(true);
            fSrcFolderBrowse.setEnabled(true);
            fSrcFileText.setEnabled(false);
            fSrcFileBrowse.setEnabled(false);
        } else if (fSrcFileButton.getSelection()) {
            fSrcProjectCombo.setEnabled(false);
            fSrcFolderText.setEnabled(false);
            fSrcFolderBrowse.setEnabled(false);
            fSrcFileText.setEnabled(true);
            fSrcFileBrowse.setEnabled(true);
        }

        if (fDestRemoteButton.getSelection()) {
            fDestRemoteCombo.setEnabled(true);
            fDestRemoteNew.setEnabled(true);
            fDestProjectCombo.setEnabled(false);
            fDestFolderText.setEnabled(false);
            fDestFolderBrowse.setEnabled(false);
            fDestFileText.setEnabled(false);
            fDestFileBrowse.setEnabled(false);
        } else if (fDestProjectButton.getSelection()) {
            fDestRemoteCombo.setEnabled(false);
            fDestRemoteNew.setEnabled(false);
            fDestProjectCombo.setEnabled(true);
            fDestFolderText.setEnabled(true);
            fDestFolderBrowse.setEnabled(true);
            fDestFileText.setEnabled(false);
            fDestFileBrowse.setEnabled(false);
        } else if (fDestFileButton.getSelection()) {
            fDestRemoteCombo.setEnabled(false);
            fDestRemoteNew.setEnabled(false);
            fDestProjectCombo.setEnabled(false);
            fDestFolderText.setEnabled(false);
            fDestFolderBrowse.setEnabled(false);
            fDestFileText.setEnabled(true);
            fDestFileBrowse.setEnabled(true);
        }
    }

    private void updateSelection(SiteConnectionPoint connection) {
        // updates the source
        String category = connection.getSourceCategory();
        IConnectionPoint source = connection.getSource();
        String name = (source == null) ? "" : source.getName(); //$NON-NLS-1$

        if (category.equals(WorkspaceConnectionPoint.CATEGORY)) {
            fSrcProjectButton.setSelection(true);
            int index = name.indexOf("/");
            if (index == -1) {
                fSrcProjectCombo.setText(name);
            } else {
                fSrcProjectCombo.setText(name.substring(0, index));
                fSrcFolderText.setText(name.substring(index));
            }
            fSrcFileButton.setSelection(false);
            fSrcFileText.setText(""); //$NON-NLS-1$
        } else if (category.equals(LocalConnectionPoint.CATEGORY)) {
            fSrcProjectButton.setSelection(false);
            fSrcFileButton.setSelection(true);
            fSrcFileText.setText(name);
        }

        // updates the destination
        category = connection.getDestinationCategory();
        source = connection.getDestination();
        name = (source == null) ? "" : source.getName(); //$NON-NLS-1$
        if (category.equals(IBaseRemoteConnectionPoint.CATEGORY)) {
            fDestRemoteButton.setSelection(true);
            fDestRemoteCombo.setText(name);
            fDestProjectButton.setSelection(false);
            fDestFileButton.setSelection(false);
            fDestFileText.setText(""); //$NON-NLS-1$
        } else if (category.equals(WorkspaceConnectionPoint.CATEGORY)) {
            fDestRemoteButton.setSelection(false);
            fDestProjectButton.setSelection(true);
            int index = name.indexOf("/");
            if (index == -1) {
                fDestProjectCombo.setText(name);
            } else {
                fDestProjectCombo.setText(name.substring(0, index));
                fDestFolderText.setText(name.substring(index));
            }
            fDestFileButton.setSelection(false);
            fDestFileText.setText(""); //$NON-NLS-1$
        } else if (category.equals(LocalConnectionPoint.CATEGORY)) {
            fDestRemoteButton.setSelection(false);
            fDestProjectButton.setSelection(false);
            fDestFileButton.setSelection(true);
            fDestFileText.setText(name);
        }

        updateEnabledStates();
    }

    /**
     * @return null if there is no error in the inputs, or a descriptive message
     *         if an error is detected
     */
    private String verify() {
        Set<String> siteNames = new HashSet<String>();
        String name;
        for (SiteConnectionPoint site : fSites) {
            name = site.getName();
            if (siteNames.contains(name)) {
                return MessageFormat.format(Messages.NewSiteWidget_ERR_DuplicateNames, name);
            }
            siteNames.add(name);
        }

        String category;
        LocalConnectionPoint connection;
        for (SiteConnectionPoint site : fSites) {
            category = site.getSourceCategory();
            // only needs to worry about the filesytem case
            if (category.equals(LocalConnectionPoint.CATEGORY)) {
                connection = (LocalConnectionPoint) site.getSource();
                if (connection == null) {
                    return MessageFormat.format(Messages.NewSiteWidget_ERR_InvalidFileSource, site
                            .getName());
                }
            }

            category = site.getDestinationCategory();
            if (category.equals(LocalConnectionPoint.CATEGORY)) {
                connection = (LocalConnectionPoint) site.getDestination();
                if (connection == null) {
                    return MessageFormat.format(Messages.NewSiteWidget_ERR_InvalidFileTarget, site
                            .getName());
                }
            }
        }
        return null;
    }

    private String getUniqueNewSiteName() {
        String siteName;
        int index;
        for (SiteConnectionPoint site : fSites) {
            siteName = site.getName();
            index = siteName.indexOf(Messages.NewSiteWidget_TXT_NewSite);
            if (index > -1) {
                try {
                    int count = Integer.parseInt(siteName.substring(index
                            + Messages.NewSiteWidget_TXT_NewSite.length()));
                    if (fCount < count) {
                        fCount = count;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return Messages.NewSiteWidget_TXT_NewSite + (++fCount);
    }

    private SiteConnectionPoint getSelectedSite() {
        int index = fSitesTable.getSelectionIndex();
        return (index == -1) ? null : fSites.get(index);
    }

    private String getSourceCategory() {
        if (fSrcFileButton.getSelection()) {
            return LocalConnectionPoint.CATEGORY;
        }
        return WorkspaceConnectionPoint.CATEGORY;
    }

    private String getSourceName() {
        if (fSrcProjectButton.getSelection()) {
            return fSrcProjectCombo.getText() + fSrcFolderText.getText();
        }
        return fSrcFileText.getText();
    }

    private String getDestinationCategory() {
        if (fDestProjectButton.getSelection()) {
            return WorkspaceConnectionPoint.CATEGORY;
        }
        if (fDestFileButton.getSelection()) {
            return LocalConnectionPoint.CATEGORY;
        }
        return IBaseRemoteConnectionPoint.CATEGORY;
    }

    private String getDestinationName() {
        if (fDestRemoteButton.getSelection()) {
            return fDestRemoteCombo.getText();
        }
        if (fDestProjectButton.getSelection()) {
            return fDestProjectCombo.getText() + fDestFolderText.getText();
        }
        return fDestFileText.getText();
    }

    private static IConnectionPointCategory getConnectionCategory(String categoryId) {
        return CoreIOPlugin.getConnectionPointManager().getConnectionPointCategory(categoryId);
    }

    private static String[] getExistingFTPSiteNames() {
        java.util.List<String> names = new ArrayList<String>();
        // finds the remote category
        IConnectionPointCategory category = getConnectionCategory(IBaseRemoteConnectionPoint.CATEGORY);
        if (category != null) {
            IConnectionPoint[] connections = category.getConnectionPoints();
            for (IConnectionPoint connection : connections) {
                names.add(connection.getName());
            }
        }

        return names.toArray(new String[names.size()]);
    }

    private static IContainer getContainer(IAdaptable adaptable) {
        // checks if it adapts to a project first
        IProject project = (IProject) adaptable.getAdapter(IProject.class);
        if (project != null) {
            return project;
        }
        IResource resource = (IResource) adaptable.getAdapter(IResource.class);
        if (resource == null) {
            return null;
        }
        switch (resource.getType()) {
        case IResource.FILE:
            return resource.getParent();
        default:
            return (IContainer) resource;
        }
    }

    private static String[] getWorkspaceProjectNames() {
        java.util.List<String> names = new ArrayList<String>();
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (IProject project : projects) {
            if (project.isAccessible()) {
                names.add(project.getName());
            }
        }
        return names.toArray(new String[names.size()]);
    }
}
