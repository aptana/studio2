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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class NewSiteDialog extends TitleAreaDialog {

    private static final int APPLY_ID = 31;
    private static final String APPLY_LABEL = Messages.NewSiteDialog_LBL_Apply;

    private NewSiteWidget fWidget;

    private boolean fCreateNew;
    private String fSelectedSiteName;
    private IAdaptable fInitialSource;
    private IAdaptable fInitialTarget;

    public NewSiteDialog(Shell parentShell) {
        this(parentShell, false);
    }

    /**
     * @param parentShell
     *            the parent shell
     * @param createNew
     *            true if a new site entry should be created by default, false
     *            otherwise
     */
    public NewSiteDialog(Shell parentShell, boolean createNew) {
        this(parentShell, createNew, null, null);
    }

    /**
     * 
     * @param parentShell
     *            the parent shell
     * @param createNew
     *            true if a new site entry should be created by default, false
     *            otherwise
     * @param source
     *            the default source location
     * @param target
     *            the default destination target
     */
    public NewSiteDialog(Shell parentShell, boolean createNew, IAdaptable source, IAdaptable target) {
        super(parentShell);
        fCreateNew = createNew;
        fInitialSource = source;
        fInitialTarget = target;

        setShellStyle(getShellStyle() | SWT.RESIZE);
        setHelpAvailable(false);
    }

    public void setSelectedSite(String siteName) {
        fSelectedSiteName = siteName;
        if (fWidget != null) {
            fWidget.setSelectedSite(siteName);
        }
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.NewSiteDialog_Title);
    }

    protected Control createDialogArea(Composite parent) {
        Composite main = (Composite) super.createDialogArea(parent);

        fWidget = new NewSiteWidget(main, fCreateNew);
        fWidget.setSelectedSite(fSelectedSiteName);
        fWidget.setSource(fInitialSource);
        fWidget.setDestination(fInitialTarget);
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        gridData.heightHint = 450;
        fWidget.getControl().setLayoutData(gridData);

        setMessage(Messages.NewSiteDialog_DefaultMessage);
        return main;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, APPLY_ID, APPLY_LABEL, false);
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == APPLY_ID) {
            applyPressed();
        } else {
            super.buttonPressed(buttonId);
        }
    }

    protected void okPressed() {
        if (applyPressed()) {
            super.okPressed();
        }
    }

    private boolean applyPressed() {
        return fWidget.apply();
    }
}
