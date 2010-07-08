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
package com.aptana.ide.intro.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.aptana.ide.core.model.IModelListener;
import com.aptana.ide.core.model.IModifiableObject;
import com.aptana.ide.core.model.user.AptanaUser;
import com.aptana.ide.core.model.user.User;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.dialogs.AptanaSignInDialog;

/**
 * 
 * @author Sandip V. Chitale (schitale@aptana.com)
 */
public class SignInSignOutAction implements IWorkbenchWindowActionDelegate {

    private IAction action;
    private User user;

    private IModelListener modelListener = new IModelListener() {

        public void modelChanged(IModifiableObject object) {
            CoreUIUtils.getDisplay().asyncExec(new Runnable() {

                public void run() {
                    updateState();
                }

            });
        }
    };

    public void dispose() {
        if (user != null) {
            user.removeListener(modelListener);
            user = null;
        }
        action = null;
    }

    public void init(IWorkbenchWindow window) {
        user = AptanaUser.getSignedInUser();
        user.addListener(modelListener);
    }

    public void run(IAction action) {
        if (user != null) {
            if (user.hasCredentials()) {
                AptanaUser.signOut();
            } else {
                signIn();
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (this.action != action) {
            this.action = action;
            updateState();
        }
    }

    public String getText() {
        if (user == null) {
            user = AptanaUser.getSignedInUser();
        }
        if (user.hasCredentials()) {
            return Messages.StartPageTrimWidget_SignOut;
        }
        return Messages.StartPageTrimWidget_SignIn;
    }

    private void updateState() {
        if (action != null && user != null) {
            if (user.hasCredentials()) {
                action.setText(Messages.StartPageTrimWidget_SignOut);
            } else {
                action.setText(Messages.StartPageTrimWidget_SignIn);
            }
        }
    }

    private void signIn() {
        AptanaSignInDialog dialog = new AptanaSignInDialog(CoreUIUtils
                .getActiveShell());
        dialog.open();
    }

}
