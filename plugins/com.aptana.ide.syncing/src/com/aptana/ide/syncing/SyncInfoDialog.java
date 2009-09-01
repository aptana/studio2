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
package com.aptana.ide.syncing;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.IVirtualFileManagerDialog;
import com.aptana.ide.core.io.ProtocolManager;
import com.aptana.ide.core.io.sync.ISyncManagerChangeListener;
import com.aptana.ide.core.io.sync.SyncManager;
import com.aptana.ide.core.io.sync.VirtualFileManagerSyncPair;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.core.ui.io.file.LocalProtocolManager;
import com.aptana.ide.core.ui.io.file.ProjectFileManager;
import com.aptana.ide.core.ui.widgets.ImagePopup;
import com.aptana.ide.core.ui.widgets.PopupItem;

/**
 * @author Ingo Muschenetz
 * @author Michael Xia (modified to use JFace Dialog)
 */
public class SyncInfoDialog extends Dialog implements SelectionListener {

    private static final Image ADD_IMAGE = SWTUtils.getImage(SyncingPlugin
            .getDefault(), "/icons/add_obj.gif"); //$NON-NLS-1$

    private Text siteName;
    private Text localPath;
    private Button browseLocalPath;
    private ImagePopup target;
    private Combo combo;
    private VirtualFileManagerSyncPair _item;

    private Map<String, IVirtualFileManager> virtualFileManagers;

    private String previouslyBrowsedDirectory;
    private boolean _newItem;
    private List<IVirtualFileManager> _syncTargets;
    private int _numProtocols;
    private String _title;

    /**
     * Create the dialog.
     * 
     * @param parent
     *            the parent shell
     */
    public SyncInfoDialog(Shell parent) {
        super(parent);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        virtualFileManagers = new HashMap<String, IVirtualFileManager>();
    }

    /**
     * @return the selected end point
     */
    public VirtualFileManagerSyncPair getItem() {
        return _item;
    }

    /**
     * Sets the selected end point.
     * 
     * @param vfm
     * @param newItem
     */
    public void setItem(VirtualFileManagerSyncPair vfm, boolean newItem) {
        _item = vfm;
        _newItem = newItem;
    }

    /**
     * Sets the title of the dialog.
     * 
     * @param title
     *            the title string
     */
    public void setTitle(String title) {
        _title = title;
    }

    /**
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    /**
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected(SelectionEvent e) {
        Object source = e.getSource();

        if (source == browseLocalPath) {
            if (combo.getSelectionIndex() == 0) {
                handleBrowseDirectory();
            } else {
                handleBrowseProject();
            }
        } else if (source == target) {
            ImagePopup c = (ImagePopup) e.widget;
            Object o = c.getData(c.getText());
            if (o != null && o instanceof ProtocolManager) {
                ProtocolManager pm = (ProtocolManager) o;
                IVirtualFileManagerDialog d = pm.createPropertyDialog(
                        getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
                IVirtualFileManager fm = pm.createFileManager();
                d.setItem(fm, true);
                d.open();
                if (fm != null) {
                    _syncTargets.add(fm);
                    refreshTargetList();

                    int selectionIndex = target.indexOf(fm.getNickName());
                    if (selectionIndex > -1) {
                        target.select(selectionIndex);
                    }

                    SyncManager.getSyncManager().fireSyncManagerChangeEvent(fm,
                            ISyncManagerChangeListener.EDIT);
                }
            }
        }
    }

    /**
     * @see org.eclipse.jface.window.Window#open()
     */
    public int open() {
        int ret = super.open();
        // gets the entered value, or null
        if (!_newItem) {
            _item = null;
        }
        return ret;
    }

    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(_title == null ? Messages.SyncInfoDialog_CreateSiteConnection : _title);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent) {
        Control control = super.createContents(parent);

        Shell shell = getShell();
        Shell parentShell = getShell().getParent().getShell();
        SWTUtils.centerAndPack(shell, parentShell);

        return control;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        main.setLayout(layout);
        main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite comp = new Composite(main, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 2;
        comp.setLayout(layout);
        comp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label label = new Label(comp, SWT.RIGHT);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        label.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(),
                "icons/aptana_dialog_tag.png")); //$NON-NLS-1$

        label = new Label(comp, SWT.WRAP);
        label.setFont(SWTUtils.getDefaultSmallFont());
        label.setText(Messages.SyncInfoDialog_ConnectsALocalPath);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.heightHint = 50;
        gridData.widthHint = 380;
        gridData.horizontalIndent = 5;
        gridData.verticalIndent = 3;
        label.setLayoutData(gridData);

        comp = new Composite(main, SWT.NONE);
        layout = new GridLayout();
        layout.verticalSpacing = 10;
        layout.horizontalSpacing = 10;
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        layout.numColumns = 2;
        comp.setLayout(layout);
        comp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        label = new Label(comp, SWT.NONE);
        label.setText(StringUtils
                .makeFormLabel(Messages.SyncInfoDialog_ConnectionName));

        siteName = new Text(comp, SWT.BORDER);
        siteName.setText(_item.getNickName());
        siteName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Group group = new Group(comp, SWT.NONE);
        group.setText(Messages.SyncInfoDialog_Path);
        layout = new GridLayout();
        layout.numColumns = 4;
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2,
                1));

        label = new Label(group, SWT.RIGHT);
        label.setText(StringUtils.makeFormLabel(Messages.SyncInfoDialog_Local));
        combo = new Combo(group, SWT.READ_ONLY);
        combo.setItems(new String[] { Messages.SyncInfoDialog_File,
                Messages.SyncInfoDialog_Project });
        gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        combo.setLayoutData(gridData);
        combo.select(0);

        localPath = new Text(group, SWT.BORDER);
        gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.widthHint = 230;
        localPath.setLayoutData(gridData);
        browseLocalPath = new Button(group, SWT.NONE);
        browseLocalPath.addSelectionListener(this);
        browseLocalPath.setText(StringUtils.ellipsify(CoreStrings.BROWSE));

        label = new Label(group, SWT.NONE);
        label
                .setText(StringUtils
                        .makeFormLabel(Messages.SyncInfoDialog_Remote));
        target = new ImagePopup(group, SWT.READ_ONLY);
        target.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3,
                1));
        target.setVisibleItemCount(15);
        target.addSelectionListener(this);

        initializeDefaultValues();

        return main;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed() {
        if (!validate()) {
            return;
        }

        if (_item.getSourceFileManager() != null) {
            LocalProtocolManager.getInstance().removeFileManager(
                    _item.getSourceFileManager());
        }

        IVirtualFileManager clientManager;
        if (combo.getSelectionIndex() == 1) {
            clientManager = LocalProtocolManager.getInstance()
                    .createProjectFileManager();
        } else {
            clientManager = LocalProtocolManager.getInstance()
                    .createFileManager();
        }

        clientManager.setNickName(localPath.getText());
        clientManager.setHidden(true);

        _item.setNickName(siteName.getText());
        _item.setSourceFileManager(clientManager);
        _item.getSourceFileManager().setBasePath(localPath.getText());

        if (target.getSelectionIndex() >= 0) {
            PopupItem selection = target.getItem(target.getSelectionIndex());
            IVirtualFileManager targetManager = virtualFileManagers.get(selection
                    .getText());
            _item.setDestinationFileManager(targetManager);
            _item.setNickName(targetManager.getNickName());
        }
        super.okPressed();
    }

    /**
     * Validates the fields to see if they are complete
     * 
     * @return boolean
     */
    private boolean validate() {
        boolean success = true;
        if (!SWTUtils.testWidgetValue(siteName)) {
            success = false;
        }
        if (!SWTUtils.testWidgetValue(localPath)) {
            success = false;
        }
        if (!SWTUtils.testWidgetValue(target, _numProtocols + 1)) {
            success = false;
        }

        return success;
    }

    private void initializeDefaultValues() {
        refreshTargetList();

        if (_item != null) {
            SWTUtils.setTextWidgetValue(siteName, _item.getNickName());
            if (_item.getSourceFileManager() != null) {
                if (_item.getSourceFileManager() instanceof ProjectFileManager) {
                    SWTUtils.setTextWidgetValue(localPath,
                            ((ProjectFileManager) _item.getSourceFileManager())
                                    .getRelativePath());
                    combo.select(1);
                } else {
                    SWTUtils.setTextWidgetValue(localPath, _item
                            .getSourceFileManager().getBasePath());
                }
            }

            if (_item.getDestinationFileManager() != null
                    && !virtualFileManagers.containsKey(_item
                            .getDestinationFileManager().getNickName())) {
                int selectionIndex = target.indexOf(_item
                        .getDestinationFileManager().getNickName());
                if (selectionIndex > -1) {
                    target.select(selectionIndex);
                }
            }
        }

        siteName.selectAll();
        siteName.setFocus();
    }

    private void refreshTargetList() {
        target.removeAll();
        ProtocolManager[] pm = ProtocolManager.getPrototcolManagers();
        _numProtocols = 0;

        if (_syncTargets == null) {
            _syncTargets = new ArrayList<IVirtualFileManager>();

            for (ProtocolManager p : pm) {
                _syncTargets.addAll(Arrays.asList(p.getFileManagers()));
            }
        }

        target.add(StringUtils
                .ellipsify(Messages.SyncInfoDialog_SelectRemoteServer),
                ADD_IMAGE, null);

        for (ProtocolManager p : pm) {
            if (p.isHidden() == false && p.isAllowNew()) {
                _numProtocols++;
                target.add(StringUtils.ellipsify(p.getDisplayName()), p
                        .getImage(), p);
                target.setData(StringUtils.ellipsify(p.getDisplayName()), p);
            }
        }

        IVirtualFileManager[] fms = _syncTargets
                .toArray(new IVirtualFileManager[_syncTargets.size()]);
        Arrays.sort(fms, new Comparator<IVirtualFileManager>() {

            public int compare(IVirtualFileManager fm1, IVirtualFileManager fm2) {
                if (fm1.getNickName() == null) {
                    return 1;
                }
                return fm1.getNickName().compareToIgnoreCase(fm2.getNickName());
            }
        });
        for (IVirtualFileManager fm : fms) {
            if (!virtualFileManagers.containsKey(fm.getNickName())
                    && !StringUtils.EMPTY.equals(fm.getNickName())) {
                if (!fm.isHidden() || fm instanceof ProjectFileManager) {
                    virtualFileManagers.put(fm.getNickName(), fm);
                    target.add(fm.getNickName(), fm.getImage(), fm);
                }
            }
        }

        target.select(0);
    }

    /**
     * Uses the standard container selection dialog to choose the new value for
     * the container field.
     */
    private void handleBrowseProject() {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(
                getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
                Messages.SyncInfoDialog_SelectSynchronizeSource);
        if (dialog.open() == ContainerSelectionDialog.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                localPath.setText(((Path) result[0]).toString());
            }
        }
    }

    /**
     * The browse button has been selected. Select the location.
     */
    private void handleBrowseDirectory() {
        DirectoryDialog dialog = new DirectoryDialog(getShell());
        dialog.setMessage(Messages.SyncInfoDialog_SelectSynchronizeSource);

        String dirName = localPath.getText().trim();
        if (dirName.length() == 0 && previouslyBrowsedDirectory != null) {
            dirName = previouslyBrowsedDirectory;
        }

        if (dirName.length() == 0) {
            dialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot()
                    .getLocation().toOSString());
        } else {
            File path = new File(dirName);
            if (path.exists()) {
                dialog.setFilterPath(new Path(dirName).toOSString());
            }
        }

        String selectedDirectory = dialog.open();
        if (selectedDirectory != null) {
            previouslyBrowsedDirectory = selectedDirectory;
            localPath.setText(previouslyBrowsedDirectory);
        }
    }

}
