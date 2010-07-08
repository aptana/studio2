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
package com.aptana.ide.subscription.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.aptana.ide.core.db.EventLogger;
import com.aptana.ide.core.model.user.AptanaUser;
import com.aptana.ide.core.model.user.User;
import com.aptana.ide.subscription.SubscriptionManager;

/**
 * Editor for showing the Aptana Live page.
 */
public class AptanaLiveEditor extends EditorPart {

    /**
     * Editor ID
     */
    public static final String ID = "com.aptana.ide.subscription.ui.AptanaLiveEditor"; //$NON-NLS-1$

    /**
     * The editor input
     */
    public static final IEditorInput INPUT = new AptanaLiveEditorInput();

    private static final String START_URL = "http://staging-aptanalivestaging.aptanacloud.com/portal"; //$NON-NLS-1$
    private static final String SUCCESS_URL = "http://staging-aptanalivestaging.aptanacloud.com/success"; //$NON-NLS-1$
    private static final String SERVICES_URL = "http://staging-aptanalivestaging.aptanacloud.com/services"; //$NON-NLS-1$

    private static boolean open;
    private Composite displayArea;
    private Browser browser;

    // adds tracking on the editor
    private IPartListener partListener = new IPartListener() {

        public void partActivated(IWorkbenchPart part) {
        }

        public void partBroughtToTop(IWorkbenchPart part) {
        }

        public void partClosed(IWorkbenchPart part) {
            if (ID.equals(part.getSite().getId())) {
                log("al.closed"); //$NON-NLS-1$
            }
        }

        public void partDeactivated(IWorkbenchPart part) {
        }

        public void partOpened(IWorkbenchPart part) {
            if (ID.equals(part.getSite().getId())) {
                log("al.opened"); //$NON-NLS-1$
            }
        }

        private void log(String eventType) {
            EventLogger.getInstance().logEvent(eventType);
        }
    };

    private LocationListener locationListener = new LocationListener() {

        public void changed(LocationEvent event) {
        }

        public void changing(LocationEvent event) {
            if (event.location.equals(SUCCESS_URL)) {
                SubscriptionManager.getInstance().update();
                event.doit = false;
                browser.setUrl(SERVICES_URL);
            }
        }
    };

    /**
     * Returns if the Aptana Live editor is currently open.
     * 
     * @return true if the editor is open, false otherwise
     */
    public static boolean isOpen() {
        return open;
    }

    public AptanaLiveEditor() {
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void dispose() {
        getSite().getWorkbenchWindow().getPartService().removePartListener(
                partListener);
        super.dispose();
        open = false;

        try {
            if (browser != null) {
                browser.removeLocationListener(locationListener);
                browser.dispose();
                browser = null;
            }
        } catch (Exception e) {
            // ignores the exception
        }
    }

    @Override
    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {
        site.getWorkbenchWindow().getPartService()
                .addPartListener(partListener);
        open = true;
        if (input != INPUT) {
            // forces to use the same input
            input = INPUT;
        }
        setSite(site);
        setInput(input);
        setPartName(Messages.AptanaLiveEditor_LBL_Title);
        setTitleToolTip(Messages.AptanaLiveEditor_LBL_ToolTip);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void createPartControl(Composite parent) {
        displayArea = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        displayArea.setLayout(layout);
        displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        browser = new Browser(displayArea, SWT.NONE);
        browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        updateContent();

        browser.addLocationListener(locationListener);
    }

    @Override
    public void setFocus() {
        displayArea.setFocus();
    }

    private void updateContent() {
        String query = "?"; //$NON-NLS-1$
        User user = AptanaUser.getSignedInUser();
        if (user.hasCredentials()) {
            query += "&username=" + encode(user.getUsername()); //$NON-NLS-1$
            query += "&password=" + encode(user.getPassword()); //$NON-NLS-1$
        }
        String url = START_URL + query;
        browser.setUrl(url);
    }

    private static String encode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            return text;
        }
    }
}
