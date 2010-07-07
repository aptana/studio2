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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.installer.wizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.installer.Activator;
import com.aptana.ide.installer.preferences.IPreferenceConstants;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class InstallerWizardDialog extends WizardDialog implements PluginsTreeViewer.Listener {

    private static final String INSTALL_LABEL = Messages.InstallerWizardDialog_InstallLabel;
    private static final String CLOSE_LABEL = Messages.InstallerWizardDialog_CloseLabel;

    private Button fDoNotShowButton;
    private InstallerWizard fWizard;

    public InstallerWizardDialog(Shell parentShell, InstallerWizard newWizard) {
        super(parentShell, newWizard);
        fWizard = newWizard;
        setHelpAvailable(false);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#create()
     */
    public void create() {
        super.create();
        getFinishButton().setEnabled(false);
        getPluginsTree().addListener(this);
    }

    /**
     * @see org.eclipse.jface.wizard.WizardDialog#close()
     */
    public boolean close() {
        getPluginsTree().removeListener(this);
        saveStates();
        return super.close();
    }

    /**
     * @see com.aptana.ide.installer.wizard.PluginsTreeViewer.Listener#itemsChecked(int)
     */
    public void itemsChecked(int count) {
        getFinishButton().setEnabled(count > 0);
    }

    /**
     * @see org.eclipse.jface.wizard.WizardDialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // adds the "Do not show again" checkbox
        GridLayout layout = (GridLayout) parent.getLayout();
        // makes necessary adjustment to the layout
        // 1. increment the number of columns in the button bar
        layout.numColumns++;
        layout.numColumns++; // For Manage Plugins... button
        // 2. makes the columns unequal widths
        layout.makeColumnsEqualWidth = false;
        // adjusts the layout data
        GridData gridData = (GridData) parent.getLayoutData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;

        Composite button = new Composite(parent, SWT.NONE);
        button.setLayout(new GridLayout());
        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        // calculates the minimum width the composite should have
        GC gc = new GC(button);
        gridData.widthHint = gc.stringExtent(Messages.InstallerWizardDialog_DoNotShowNote).x + 10;
        gc.dispose();
        button.setLayoutData(gridData);
        fDoNotShowButton = new Button(button, SWT.CHECK);
        fDoNotShowButton.setText(Messages.InstallerWizardDialog_DoNotShowLabel);
        fDoNotShowButton.setFont(JFaceResources.getDialogFont());
        boolean donotshow;
        IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
        if (prefs.contains(IPreferenceConstants.WIZARD_DO_NOT_SHOW_AGAIN)) {
            donotshow = prefs.getBoolean(IPreferenceConstants.WIZARD_DO_NOT_SHOW_AGAIN);
        } else {
            donotshow = prefs.getBoolean(IPreferenceConstants.WIZARD_DO_NOT_SHOW_AGAIN_DEFAULT);
        }
        fDoNotShowButton.setSelection(donotshow);
        fDoNotShowButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, true));
        fDoNotShowButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                saveStates();
                updateDoNotShowLayout();
            }
        });

        Button managePluginsButton = new Button(parent, SWT.PUSH);
        managePluginsButton.setText(Messages.InstallerWizardDialog_Manage_Plugins);
        managePluginsButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e) {
                try {
                    close();
                    CoreUIUtils.showView("com.aptana.ide.ui.ViewPlugins"); //$NON-NLS-1$
                } catch (PartInitException e1) {
                    // Do nothing, view didn't open
                }
            }

        });

        super.createButtonsForButtonBar(parent);
        // changes the "Finish" text to "Install" and disables it by default
        getFinishButton().setText(INSTALL_LABEL);
        // changes the "Cancel" text to "Close"
        getButton(IDialogConstants.CANCEL_ID).setText(CLOSE_LABEL);
    }

    /**
     * @see org.eclipse.jface.wizard.WizardDialog#setButtonLayoutData(org.eclipse.swt.widgets.Button)
     */
    protected void setButtonLayoutData(Button button) {
        super.setButtonLayoutData(button);
        GridData gridData = (GridData) button.getLayoutData();
        gridData.grabExcessHorizontalSpace = false;
        gridData.horizontalAlignment = SWT.END;
    }

    private Button getFinishButton() {
        return getButton(IDialogConstants.FINISH_ID);
    }

    private PluginsTreeViewer getPluginsTree() {
        return ((PluginsWizardPage) fWizard.getPage(PluginsWizardPage.NAME)).getTreeViewer();
    }

    private void updateDoNotShowLayout() {
        if (fDoNotShowButton.getSelection()) {
            setMessage(Messages.InstallerWizardDialog_DoNotShowNote, IMessageProvider.INFORMATION);
        } else {
            setMessage(Messages.PluginsWizardPage_Description);

        }
    }

    private void saveStates() {
        // stores the state of "Do not show again" checkbox
        IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
        prefs.setValue(IPreferenceConstants.WIZARD_DO_NOT_SHOW_AGAIN, fDoNotShowButton
                .getSelection());
    }
}
