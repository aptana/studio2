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
package com.aptana.ide.syncing.ui.ingo;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.ingo.IVirtualFileManager;
import com.aptana.ide.core.io.ingo.VirtualFileManagerSyncPair;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.SWTUtils;

/**
 * @author Ingo Muschenetz
 * @author Michael Xia (modified to extend from JFace Dialog)
 */
public class ChooseSyncConfigurationDialog extends Dialog implements
        SelectionListener {

    private Combo target;
    private Label syncTargetDescription;
    private Button rememberMyDecisionButton;

    private VirtualFileManagerSyncPair _chosenConfiguration;
    private VirtualFileManagerSyncPair[] _syncConfigurations;
    private VirtualFileManagerSyncPair _syncConfiguration;

    // Show the remember my decision check box
    private boolean showRememberMyDecision;
    private boolean rememberMyDecision;

    /**
     * Create the dialog.
     * 
     * @param parent
     *            the parent shell
     */
    public ChooseSyncConfigurationDialog(Shell parent) {
        super(parent);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**
     * @return the selected end point
     */
    public VirtualFileManagerSyncPair getSelectedItem() {
        return _chosenConfiguration;
    }

    /**
     * @return true if the decision should be remembered, false otherwise
     */
    public boolean isRememberMyDecision() {
        if (showRememberMyDecision) {
            return rememberMyDecision;
        }
        return false;
    }

    /**
     * Sets the initial end point to be selected.
     * 
     * @param vfm
     */
    public void setInitialItem(VirtualFileManagerSyncPair vfm) {
        _syncConfiguration = vfm;
    }

    /**
     * Sets the possible connection end points.
     * 
     * @param vfms
     */
    public void setItems(VirtualFileManagerSyncPair[] vfms) {
        _syncConfigurations = vfms.clone();
        Arrays.sort(_syncConfigurations,
                new Comparator<VirtualFileManagerSyncPair>() {

                    public int compare(VirtualFileManagerSyncPair o1,
                            VirtualFileManagerSyncPair o2) {

                        return o1.getNickName().compareTo(o2.getNickName());
                    }

                });
    }

    public void setShowRememberMyDecision(boolean showRememberMyDecision) {
        this.showRememberMyDecision = showRememberMyDecision;
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
        if (source == target) {
            updateDescriptiveText();
        } else if (source == rememberMyDecisionButton) {
            rememberMyDecision = rememberMyDecisionButton.getSelection();
        }
    }

    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell
                .setText(Messages.ChooseSyncConfigurationDialog_ChooseSiteConnectio);
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
        comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label label = new Label(comp, SWT.RIGHT);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        label.setAlignment(SWT.RIGHT);
        label.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(),
                "icons/aptana_dialog_tag.png")); //$NON-NLS-1$

        label = new Label(comp, SWT.WRAP);
        label.setFont(SWTUtils.getDefaultSmallFont());
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.heightHint = 50;
        gridData.widthHint = 400;
        gridData.horizontalIndent = 5;
        gridData.verticalIndent = 3;
        label.setLayoutData(gridData);
        label
                .setText(Messages.ChooseSyncConfigurationDialog_ChooseSyncConnection);

        comp = new Composite(main, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 10;
        layout.horizontalSpacing = 10;
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        comp.setLayout(layout);
        comp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        label = new Label(comp, SWT.NONE);
        label
                .setText(StringUtils
                        .makeFormLabel(Messages.ChooseSyncConfigurationDialog_Connection));
        target = new Combo(comp, SWT.READ_ONLY);
        target.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        target.addSelectionListener(this);

        // left padding
        new Label(comp, SWT.NONE);
        syncTargetDescription = new Label(comp, SWT.NONE);
        gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.widthHint = 360;
        syncTargetDescription.setLayoutData(gridData);

        if (showRememberMyDecision) {
            rememberMyDecisionButton = new Button(comp, SWT.CHECK);
            rememberMyDecisionButton
                    .setText(Messages.ChooseSyncConfigurationDialog_RememberMyDecisionLabel);
            gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
            gridData.horizontalSpan = 2;
            rememberMyDecisionButton.setLayoutData(gridData);
            rememberMyDecisionButton.addSelectionListener(this);

            label = new Label(comp, SWT.WRAP);
            label
                    .setText(Messages.ChooseSyncConfigurationDialog_RememberMyDecisionTipLabel);
            gridData = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
            gridData.horizontalSpan = 2;
            label.setLayoutData(gridData);
        }

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

        if (target.getSelectionIndex() >= 0) {
            _chosenConfiguration = _syncConfigurations[target
                    .getSelectionIndex()];
        }
        super.okPressed();
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
     */
    protected void cancelPressed() {
        _chosenConfiguration = null;
        super.cancelPressed();
    }

    /**
     * Validates the fields to see if they are complete
     * 
     * @return boolean
     */
    private boolean validate() {
        boolean success = true;
        if (!SWTUtils.testWidgetValue(target, 0)) {
            success = false;
        }

        return success;
    }

    private void initializeDefaultValues() {
        int currentIndex = 0;
        int selectIndex = 0;
        for (VirtualFileManagerSyncPair conf : _syncConfigurations) {
            if (conf != null) {
                if (conf.isValid()) {
                    IVirtualFileManager destManager = conf
                            .getDestinationFileManager();
                    target.add(destManager.getProtocolManager()
                            .getDisplayName()
                            + ": " + conf.getNickName()); //$NON-NLS-1$
                    if (conf == _syncConfiguration) {
                        selectIndex = currentIndex;
                    }
                    currentIndex++;
                }
            }
        }

        target.select(selectIndex);
        updateDescriptiveText();
    }

    private void updateDescriptiveText() {
        if (target.getSelectionIndex() >= 0) {
            _chosenConfiguration = _syncConfigurations[target
                    .getSelectionIndex()];
            if (_chosenConfiguration != null) {
                IVirtualFileManager srcManager = _chosenConfiguration
                        .getSourceFileManager();
                IVirtualFileManager destManager = _chosenConfiguration
                        .getDestinationFileManager();
                if (_chosenConfiguration.isValid()) {
                    syncTargetDescription.setText(FileUtils.compressPath(
                            srcManager.getBasePath(), 30)
                            + " <-> " //$NON-NLS-1$
                            + destManager.getNickName()
                            + FileUtils.compressPath(destManager.getBasePath(),
                                    30));
                    syncTargetDescription.setToolTipText(srcManager
                            .getBasePath()
                            + " <-> " //$NON-NLS-1$
                            + destManager.getNickName()
                            + destManager.getBasePath());
                }
            }
        }
    }

}
