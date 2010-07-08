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
package com.aptana.ide.update.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.update.preferences.IPreferenceConstants;

public class BrowserDialog extends TrayDialog {

    private Browser browser;
    private String announceURL;
    private final int height;
    private final int width;
    private Button bTurnOffAllAnnouncements;
    private Button bTurnOffThisAnnouncement;

    public BrowserDialog(Shell parentShell, String announceURL, int height, int width) {
        super(parentShell);
        this.announceURL = announceURL;
        this.height = height;
        this.width = width;
        setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        setHelpAvailable(false);
        setBlockOnOpen(false);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.BrowserDialog_AnnouncementTitle);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        browser = new Browser(parent, SWT.BORDER);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.widthHint = width;
        gridData.heightHint = height;
        browser.setLayoutData(gridData);
        browser.setUrl(announceURL);

        return browser;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite composite = new Composite(parent, SWT.None);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        composite.setFont(parent.getFont());

        // create help control if needed
        if (isHelpAvailable()) {
            Control helpControl = createHelpControl(composite);
            ((GridData) helpControl.getLayoutData()).horizontalIndent = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        }

        Composite checkboxComposite = new Composite(composite, SWT.NONE);
        GridLayout layout2 = new GridLayout();
        layout2.numColumns = 2;
        layout2.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout2.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout2.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout2.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        checkboxComposite.setLayout(layout2);
        checkboxComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        checkboxComposite.setFont(parent.getFont());

        bTurnOffThisAnnouncement = new Button(checkboxComposite, SWT.CHECK);
        bTurnOffThisAnnouncement
                .setText(Messages.BrowserDialog_Label_DoNotShowThisAnnouncementAgain);

        new Label(checkboxComposite, SWT.None);

        bTurnOffAllAnnouncements = new Button(checkboxComposite, SWT.CHECK);
        bTurnOffAllAnnouncements.setText(Messages.BrowserDialog_Label_DoNotShowAllAnnouncements);

        Button okButton = createButton(composite, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        GridData okGridData = (GridData) okButton.getLayoutData();
        okGridData.grabExcessVerticalSpace = false;
        okGridData.horizontalSpan = 2;
        okGridData.verticalAlignment = SWT.END;
        okGridData.horizontalAlignment = SWT.END;

        return composite;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            IPreferenceStore prefs = UpdateUIActivator.getDefault().getPreferenceStore();
            prefs.setValue(IPreferenceConstants.NEVER_SHOW_THIS_ANNOUNCEMENT,
                    bTurnOffThisAnnouncement.getSelection());
            prefs.setValue(IPreferenceConstants.NEVER_SHOW_ANNOUNCEMENTS, bTurnOffAllAnnouncements
                    .getSelection());
        }
        super.buttonPressed(buttonId);
    }

}
