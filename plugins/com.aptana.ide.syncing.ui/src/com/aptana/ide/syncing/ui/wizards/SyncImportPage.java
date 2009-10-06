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
package com.aptana.ide.syncing.ui.wizards;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants;
import com.aptana.ide.ui.io.IOUIPlugin;

/**
 * @author Pavel Petrochenko
 * @author Michael Xia (mxia@aptana.com)
 */
public class SyncImportPage extends WizardPage implements ModifyListener, SelectionListener {

    private Text pathText;
    private Button browseButton;

    protected SyncImportPage() {
        super("importSync"); //$NON-NLS-1$
    }

    public void modifyText(ModifyEvent e) {
        setPageComplete(pathText.getText().trim().length() > 0);
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
        String result = dialog.open();
        if (result != null) {
            pathText.setText(result);
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(final Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(3, false));

        Label label = new Label(main, SWT.NONE);
        label.setText(Messages.SyncImportPage_FROM_FILE);
        pathText = new Text(main, SWT.BORDER);
        pathText.setText(getPreferenceStore().getString(IPreferenceConstants.EXPORT_INITIAL_PATH));
        pathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        pathText.addModifyListener(this);

        browseButton = new Button(main, SWT.PUSH);
        browseButton.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
        browseButton.addSelectionListener(this);

        setTitle(Messages.SyncImportPage_TITLE);
        setDescription(Messages.SyncImportPage_DESCRIPTION);
        setControl(main);
    }

    /**
     * @return success
     */
    public boolean performFinish() {
        String path = pathText.getText();
        File file = new File(path);
        if (!file.exists()) {
            MessageDialog.openError(getShell(), Messages.SyncImportPage_FILE_NOT_EXIST, StringUtils
                    .format(Messages.SyncImportPage_FILE_NOT_EXIST_DESC, file.getAbsolutePath()));
            return false;
        }
        if (!file.canRead()) {
            MessageDialog.openError(getShell(), Messages.SyncImportPage_FILE_NOT_READABLE,
                    StringUtils.format(Messages.SyncImportPage_FILE_NOT_READABLE_DESC, file
                            .getAbsolutePath()));
            return false;
        }

        // CoreIOPlugin.getConnectionPointManager().loadState(new Path(path));
        SyncingPlugin.getSiteConnectionManager().loadState(new Path(path));
        IOUIPlugin.refreshNavigatorView(null);
        getPreferenceStore().setValue(IPreferenceConstants.EXPORT_INITIAL_PATH, path);
        return true;
    }

    private static IPreferenceStore getPreferenceStore() {
        return SyncingUIPlugin.getDefault().getPreferenceStore();
    }
}
