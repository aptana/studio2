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
package com.aptana.ide.core.ui.io.file;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.SWTUtils;

/**
 * @author Ingo Muschenetz
 * @author Michael Xia (modified to extend from JFace Dialog)
 */
public class ProjectLocationDialog extends Dialog implements SelectionListener {

    private Text localPath;
    private Button browseLocalPath;

    private ProjectFileManager _item;
    private boolean _newItem;

    /**
     * Create the dialog.
     * 
     * @param parent
     *            the parent shell
     */
    public ProjectLocationDialog(Shell parent) {
        super(parent);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**
     * @return the specified project file manager
     */
    public ProjectFileManager getItem() {
        return _item;
    }

    /**
     * Sets the current project file manager.
     * 
     * @param vfm
     * @param newItem
     */
    public void setItem(IVirtualFileManager vfm, boolean newItem) {
        if (vfm instanceof ProjectFileManager == false) {
            throw new IllegalArgumentException(
                    Messages.ProjectLocationDialog_CanOnlyAcceptProjectFileManagerItemsError);
        }

        _item = (ProjectFileManager) vfm;
        _newItem = newItem;
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
            handleBrowseProject();
        }
    }

    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell
                .setText(Messages.ProjectLocationDialog_ProjectSiteConfiguration);
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
        label.setAlignment(SWT.RIGHT);
        label.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(),
                "icons/aptana_dialog_tag.png")); //$NON-NLS-1$

        label = new Label(comp, SWT.WRAP);
        label.setFont(SWTUtils.getDefaultSmallFont());
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.widthHint = 380;
        gridData.horizontalIndent = 5;
        gridData.verticalIndent = 3;
        label.setLayoutData(gridData);
        label.setText(Messages.ProjectLocationDialog_ConnectionMessage);

        comp = new Composite(main, SWT.NONE);
        comp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        layout = new GridLayout();
        layout.numColumns = 3;
        layout.verticalSpacing = 10;
        layout.horizontalSpacing = 10;
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        comp.setLayout(layout);

        label = new Label(comp, SWT.NONE);
        label.setText(Messages.ProjectLocationDialog_ProjectPath);

        localPath = new Text(comp, SWT.BORDER);
        gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.widthHint = 300;
        localPath.setLayoutData(gridData);

        browseLocalPath = new Button(comp, SWT.NONE);
        browseLocalPath.setLayoutData(new GridData());
        browseLocalPath.addSelectionListener(this);
        browseLocalPath.setText(StringUtils.ellipsify(CoreStrings.BROWSE));

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

        _item.setNickName(localPath.getText());
        _item.setBasePath(localPath.getText());
        _item.setHidden(true);

        super.okPressed();
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
     */
    protected void cancelPressed() {
        _item = null;
        super.cancelPressed();
    }

    /**
     * Validates the fields to see if they are complete
     * 
     * @return boolean
     */
    private boolean validate() {
        boolean success = true;
        if (!SWTUtils.testWidgetValue(localPath)) {
            success = false;
        }

        return success;
    }

    private void initializeDefaultValues() {
        if (_item != null) {
            SWTUtils.setTextWidgetValue(localPath, _item.getRelativePath());
        }
    }

    /**
     * Uses the standard container selection dialog to choose the new value for
     * the container field.
     */

    private void handleBrowseProject() {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(
                getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
                Messages.ProjectLocationDialog_SelectSynchronizeSource);
        if (dialog.open() == Window.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                localPath.setText(((Path) result[0]).toString());
            }
        }
    }

}
