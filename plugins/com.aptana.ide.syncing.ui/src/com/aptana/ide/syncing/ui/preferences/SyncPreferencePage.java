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
package com.aptana.ide.syncing.ui.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.preferences.PreferenceInitializer;
import com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.decorators.DecoratorUtils;
import com.aptana.ide.ui.io.IOUIPlugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class SyncPreferencePage extends FileExtensionPreferencePage {

    @Override
    public Object addItem() {
        CloakingInfoDialog dialog = new CloakingInfoDialog(getControl().getShell());
        if (dialog.open() == Window.OK) {
            return dialog.messageText;
        }
        return null;
    }

    @Override
    public Object editItem(Object item) {
        CloakingInfoDialog dialog = new CloakingInfoDialog(getControl().getShell());
        dialog.setItem(item);

        if (dialog.open() == Window.OK) {
            return dialog.messageText;
        }
        return null;
    }

    @Override
    protected Control createContents(Composite parent) {
        // transfers the preferences
        String value = Platform.getPreferencesService().getString(CoreIOPlugin.PLUGIN_ID,
                doGetPreferenceID(), PreferenceInitializer.DEFAULT_CLOAK_EXPRESSIONS, null);
        doGetPreferenceStore().setValue(doGetPreferenceID(), value);

        return super.createContents(parent);
    }

    @Override
    protected String getTableDescription() {
        return Messages.SyncPreferencePage_LBL_Description;
    }

    @Override
    protected Plugin doGetPlugin() {
        return SyncingUIPlugin.getDefault();
    }

    @Override
    protected String doGetPreferenceID() {
        return com.aptana.ide.core.io.preferences.IPreferenceConstants.GLOBAL_CLOAKING_EXTENSIONS;
    }

    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return IOUIPlugin.getDefault().getPreferenceStore();
    }

    @Override
    public boolean performOk() {
        boolean ret = super.performOk();
        String value = doGetPreferenceStore().getString(doGetPreferenceID());
        IEclipsePreferences prefs = (new InstanceScope()).getNode(CoreIOPlugin.PLUGIN_ID);
        prefs.put(doGetPreferenceID(), value);
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
        }
        DecoratorUtils.updateCloakDecorator();

        return ret;
    }

    /**
     * This class is used to prompt the user for a file name or extension to be
     * cloaked.
     */
    private static class CloakingInfoDialog extends TitleAreaDialog {

        private Text message;
        private String messageText = ""; //$NON-NLS-1$

        /**
         * @param parentShell
         *            the parent shell
         */
        public CloakingInfoDialog(Shell parentShell) {
            super(parentShell);
            setHelpAvailable(false);
        }

        /**
         * Sets the message item.
         * 
         * @param item
         *            the item in the message box
         */
        public void setItem(Object item) {
            messageText = item.toString();
        }

        @Override
        protected void configureShell(Shell shell) {
            super.configureShell(shell);
            shell.setText(Messages.SyncPreferencePage_CloakInfo_Shell_Title);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite main = new Composite(parent, SWT.NONE);
            main.setLayout(new GridLayout(2, false));
            main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Label label = new Label(main, SWT.LEFT);
            label.setText(Messages.SyncPreferencePage_CloakInfo_LBL);

            message = new Text(main, SWT.SINGLE | SWT.BORDER);
            message.setText(messageText);
            message.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            message.setFocus();
            message.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent event) {
                    if (event.widget == message) {
                        getButton(IDialogConstants.OK_ID).setEnabled(validateErrorDescriptor());
                    }
                }
            });

            Dialog.applyDialogFont(main);

            setTitle(Messages.SyncPreferencePage_CloakInfo_Title);
            setMessage(Messages.SyncPreferencePage_CloakInfo_Message);

            return main;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            super.createButtonsForButtonBar(parent);
            getButton(IDialogConstants.OK_ID).setEnabled(!messageText.equals("")); //$NON-NLS-1$
        }

        /**
         * Validate the user input for a file type
         */
        private boolean validateErrorDescriptor() {
            // check for empty message
            if (message.getText().equals("")) { //$NON-NLS-1$
                return false;
            }
            messageText = message.getText();
            return true;
        }
    }
}
