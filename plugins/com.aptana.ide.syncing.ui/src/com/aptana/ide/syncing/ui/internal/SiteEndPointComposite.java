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

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.dialogs.FileFolderSelectionDialog;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointCategory;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.core.io.WorkspaceConnectionPoint;
import com.aptana.ide.filesystem.ftp.FTPConnectionPoint;
import com.aptana.ide.ui.IPropertyDialog;
import com.aptana.ide.ui.ftp.dialogs.FTPConnectionPointPropertyDialog;
import com.aptana.ide.ui.ftp.internal.FTPPropertyDialogProvider;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class SiteEndPointComposite implements SelectionListener, ModifyListener {

    public static interface Listener {
        public void categoryChanged(SiteEndPointComposite source, String category);

        public void sourceChanged(SiteEndPointComposite source, String newSource);

        public void validationError(SiteEndPointComposite source, String error);
    }

    private Control fMain;
    private Label fDescription;
    private Button fRemoteButton;
    private Combo fRemoteCombo;
    private Button fRemoteNew;
    private Button fProjectButton;
    private Combo fProjectCombo;
    private Text fFolderText;
    private Button fFolderBrowse;
    private Button fFileButton;
    private Text fFileText;
    private Button fFileBrowse;

    private String fName;
    private boolean fShowRemote;

    private java.util.List<Listener> fListeners;

    /**
     * @param parent
     *            the parent composite
     * @param name
     *            the name of the end point
     * @param showRemote
     *            true if the remote sites should be shown, false otherwise
     */
    public SiteEndPointComposite(Composite parent, String name, boolean showRemote) {
        fName = name;
        fShowRemote = showRemote;
        fListeners = new ArrayList<Listener>();

        fMain = createControl(parent);
    }

    public void addListener(Listener listener) {
        if (!fListeners.contains(listener)) {
            fListeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        fListeners.remove(listener);
    }

    public Control getControl() {
        return fMain;
    }

    public String getCategory() {
        if (fProjectButton.getSelection()) {
            return WorkspaceConnectionPoint.CATEGORY;
        }
        if (fFileButton.getSelection()) {
            return LocalConnectionPoint.CATEGORY;
        }
        return IBaseRemoteConnectionPoint.CATEGORY;
    }

    public String getSourceName() {
        if (fRemoteButton.getSelection()) {
            return fRemoteCombo.getText();
        }
        if (fProjectButton.getSelection()) {
            return fProjectCombo.getText() + fFolderText.getText();
        }
        return fFileText.getText();
    }

    public void setCategory(String category) {
        if (category.equals(IBaseRemoteConnectionPoint.CATEGORY)) {
            fRemoteButton.setSelection(true);
            fProjectButton.setSelection(false);
            fFileButton.setSelection(false);
        } else if (category.equals(WorkspaceConnectionPoint.CATEGORY)) {
            fRemoteButton.setSelection(false);
            fProjectButton.setSelection(true);
            fFileButton.setSelection(false);
        } else if (category.equals(LocalConnectionPoint.CATEGORY)) {
            fRemoteButton.setSelection(false);
            fProjectButton.setSelection(false);
            fFileButton.setSelection(true);
        }

        updateEnabledStates();
    }

    public void setSourceName(String name) {
        if (fRemoteButton.getSelection()) {
            fRemoteCombo.setText(name);
            fFileText.setText(""); //$NON-NLS-1$
        } else if (fProjectButton.getSelection()) {
            int index = name.indexOf("/"); //$NON-NLS-1$
            if (index == -1) {
                fProjectCombo.setText(name);
                fFolderText.setText("/"); //$NON-NLS-1$
            } else {
                fProjectCombo.setText(name.substring(0, index));
                fFolderText.setText(name.substring(index));
            }
            fFileText.setText(""); //$NON-NLS-1$
        } else if (fFileButton.getSelection()) {
            fFileText.setText(name);
        }

        updateEnabledStates();
    }

    public void setDescription(String description) {
        fDescription.setText(description);
    }

    public void setSource(IAdaptable source) {
        if (source == null) {
            return;
        }
        IContainer container = getContainer(source);
        if (container == null) {
            return;
        }

        fProjectButton.setSelection(true);
        fProjectCombo.setText(container.getProject().getName());
        if (container instanceof IProject) {
            fFolderText.setText("/"); //$NON-NLS-1$
        } else {
            fFolderText.setText(container.getProjectRelativePath().toString());
        }
        updateEnabledStates();
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        Object source = e.getSource();

        if (source == fRemoteButton || source == fProjectButton || source == fFileButton) {
            updateEnabledStates();
            fireCategoryChanged(getCategory());
        } else if (source == fRemoteCombo) {
            fireSourceChanged(getSourceName());
        } else if (source == fRemoteNew) {
            createNewFTPConnection();
            fireSourceChanged(getSourceName());
        } else if (source == fProjectCombo) {
            updateEnabledStates();
            fireSourceChanged(getSourceName());
        } else if (source == fFolderBrowse) {
            String folder = openFolderBrowseDialog();
            if (folder != null) {
                fFolderText.setText(folder);
                fireSourceChanged(getSourceName());
            }
        } else if (source == fFileBrowse) {
            String dir = openFileBrowseDialog();
            if (dir != null) {
                fFileText.setText(dir);
                fireSourceChanged(getSourceName());
            }
        }
    }

    public void modifyText(ModifyEvent e) {
        fireValidationError(validate());

        Object source = e.getSource();
        if (source == fFolderText) {
            fireSourceChanged(getSourceName());
        } else if (source == fFileText) {
            fireSourceChanged(getSourceName());
        }
    }

    protected Control createControl(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(fName);
        group.setLayout(new GridLayout());

        fDescription = new Label(group, SWT.NONE);

        Composite location = new Composite(group, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        location.setLayout(layout);

        // remote specs
        fRemoteButton = new Button(location, SWT.RADIO);
        fRemoteButton.setText(Messages.NewSiteWidget_LBL_Remote);
        fRemoteButton.setSelection(fShowRemote);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
        gridData.exclude = !fShowRemote;
        fRemoteButton.setLayoutData(gridData);
        fRemoteButton.addSelectionListener(this);

        fRemoteCombo = new Combo(location, SWT.READ_ONLY);
        fRemoteCombo.setItems(getExistingFTPSiteNames());
        fRemoteCombo.select(0);
        gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
        gridData.exclude = !fShowRemote;
        fRemoteCombo.setLayoutData(gridData);
        fRemoteCombo.addSelectionListener(this);

        fRemoteNew = new Button(location, SWT.PUSH);
        fRemoteNew.setText(StringUtils.ellipsify(CoreStrings.NEW));
        gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
        gridData.exclude = !fShowRemote;
        fRemoteNew.setLayoutData(gridData);
        fRemoteNew.addSelectionListener(this);

        // project specs
        fProjectButton = new Button(location, SWT.RADIO);
        fProjectButton.setText(Messages.NewSiteWidget_LBL_Project);
        fProjectButton.setSelection(!fShowRemote);
        fProjectButton.addSelectionListener(this);

        fProjectCombo = new Combo(location, SWT.READ_ONLY);
        String[] projects = getWorkspaceProjectNames();
        fProjectCombo.setItems(projects);
        fProjectCombo.select(0);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.horizontalSpan = 2;
        fProjectCombo.setLayoutData(gridData);
        fProjectCombo.addSelectionListener(this);

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

        Label label = new Label(folder, SWT.NONE);
        label.setText(Messages.NewSiteWidget_LBL_Folder);
        fFolderText = new Text(folder, SWT.BORDER);
        fFolderText.setText("/"); //$NON-NLS-1$
        fFolderText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        fFolderText.addModifyListener(this);

        fFolderBrowse = new Button(location, SWT.PUSH);
        fFolderBrowse.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
        fFolderBrowse.addSelectionListener(this);

        // filesystem specs
        fFileButton = new Button(location, SWT.RADIO);
        fFileButton.setText(Messages.NewSiteWidget_LBL_Filesystem);
        fFileButton.addSelectionListener(this);

        fFileText = new Text(location, SWT.BORDER);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.widthHint = 250;
        fFileText.setLayoutData(gridData);
        fFileText.addModifyListener(this);

        fFileBrowse = new Button(location, SWT.PUSH);
        fFileBrowse.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
        fFileBrowse.addSelectionListener(this);

        updateEnabledStates();

        return group;
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
                    fRemoteCombo.setItems(getExistingFTPSiteNames());
                    fRemoteCombo.setText(connection.getName());
                }
            }
        }
    }

    private String openFolderBrowseDialog() {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
                fProjectCombo.getText());
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

    private void updateEnabledStates() {
        if (fRemoteButton.getSelection()) {
            fRemoteCombo.setEnabled(true);
            fRemoteNew.setEnabled(true);
            fProjectCombo.setEnabled(false);
            fFolderText.setEnabled(false);
            fFolderBrowse.setEnabled(false);
            fFileText.setEnabled(false);
            fFileBrowse.setEnabled(false);
        } else if (fProjectButton.getSelection()) {
            fRemoteCombo.setEnabled(false);
            fRemoteNew.setEnabled(false);
            fProjectCombo.setEnabled(true);
            boolean hasFolders = hasFolders(fProjectCombo.getText());
            fFolderText.setEnabled(hasFolders);
            fFolderBrowse.setEnabled(hasFolders);
            fFileText.setEnabled(false);
            fFileBrowse.setEnabled(false);
        } else if (fFileButton.getSelection()) {
            fRemoteCombo.setEnabled(false);
            fRemoteNew.setEnabled(false);
            fProjectCombo.setEnabled(false);
            fFolderText.setEnabled(false);
            fFolderBrowse.setEnabled(false);
            fFileText.setEnabled(true);
            fFileBrowse.setEnabled(true);
        }

        fireValidationError(validate());
    }

    private String validate() {
        if (fProjectButton.getSelection()) {
            IProject project = getProject(fProjectCombo.getText());
            IResource resource = project.findMember(fFolderText.getText());
            if (!(resource instanceof IContainer)) {
                return "Please specifies a valid folder or '/' for the project root.";
            }
        }
        if (fFileButton.getSelection()) {
            String text = fFileText.getText();
            if (!(new File(text)).exists()) {
                return "Please specifies a valid filesystem location.";
            }
        }
        return null;
    }

    private void fireCategoryChanged(String category) {
        for (Listener listener : fListeners) {
            listener.categoryChanged(this, category);
        }
    }

    private void fireSourceChanged(String newSource) {
        for (Listener listener : fListeners) {
            listener.sourceChanged(this, newSource);
        }
    }

    private void fireValidationError(String error) {
        for (Listener listener : fListeners) {
            listener.validationError(this, error);
        }
    }

    private static IConnectionPointCategory getConnectionCategory(String categoryId) {
        return CoreIOPlugin.getConnectionPointManager().getConnectionPointCategory(categoryId);
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

    private static IProject getProject(String projectName) {
        return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    }

    private static boolean hasFolders(String projectName) {
        IProject project = getProject(projectName);
        if (project == null || !project.isAccessible()) {
            return false;
        }
        try {
            IResource[] children = project.members();
            for (IResource child : children) {
                if (child instanceof IContainer) {
                    return true;
                }
            }
        } catch (CoreException e) {
        }
        return false;
    }
}
