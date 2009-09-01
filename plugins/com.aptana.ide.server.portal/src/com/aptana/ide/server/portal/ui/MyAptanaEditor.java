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
package com.aptana.ide.server.portal.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
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
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.browser.BaseBrowserAdapter;
import com.aptana.ide.core.ui.browser.BrowserRegistry;
import com.aptana.ide.core.ui.browser.IBrowser;
import com.aptana.ide.server.jetty.JettyPlugin;
import com.aptana.ide.server.portal.PortalPlugin;
import com.aptana.ide.server.portal.preferences.IPreferenceConstants;
import com.aptana.ide.server.portal.server.MyAptanaServer;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class MyAptanaEditor extends EditorPart {

    /**
     * ID
     */
    public static final String ID = "com.aptana.ide.server.portal.ui.MyAptanaEditor"; //$NON-NLS-1$

    /**
     * INPUT
     */
    public static final IEditorInput INPUT = new MyAptanaEditorInput();

    private static boolean open;
    private Composite displayArea;
    private IBrowser browser;

    // Add tracking of Portal Editor
    private IPartListener partListener = new IPartListener() {

        public void partActivated(IWorkbenchPart part) {
        }

        public void partBroughtToTop(IWorkbenchPart part) {
        }

        public void partClosed(IWorkbenchPart part) {
            if (ID.equals(part.getSite().getId())) {
                log("ma.closed"); //$NON-NLS-1$
            }
        }

        public void partDeactivated(IWorkbenchPart part) {
        }

        public void partOpened(IWorkbenchPart part) {
            if (ID.equals(part.getSite().getId())) {
                log("ma.opened"); //$NON-NLS-1$
            }
        }

        private void log(String eventType) {
            EventLogger.getInstance().logEvent(eventType);
        }
    };

    /**
     * Returns if the My Aptana editor is currently open.
     * 
     * @return true if the editor is open, false otherwise
     */
    public static boolean isOpen() {
        return open;
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor) {
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    public void doSaveAs() {
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    public void dispose() {
        getSite().getWorkbenchWindow().getPartService().removePartListener(
                partListener);
        super.dispose();
        open = false;

        try {
            if (browser != null) {
                browser.dispose();
                browser = null;
            }
        } catch (Exception e) {
        }
        Job job = new Job("Restart My Aptana server") { //$NON-NLS-1$

            protected IStatus run(IProgressMonitor monitor) {
                MyAptanaServer.getServer().restart();
                return Status.OK_STATUS;
            }

        };
        job.setSystem(true);
        job.setPriority(Job.BUILD);
        job.schedule();
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
     *      org.eclipse.ui.IEditorInput)
     */
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
        setPartName(Messages.MyAptanaEditor_Title);
        setTitleToolTip(Messages.MyAptanaEditor_TitleTooltip);
        setTitleImage(PortalPlugin.getImage("icons/startpage.png")); //$NON-NLS-1$
        // resets the preference
        PortalPlugin.getDefault().getPreferenceStore().setValue(
                IPreferenceConstants.MY_APTANA_PREVIOUSLY_OPENED, false);
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    public boolean isDirty() {
        return false;
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        displayArea = new Composite(parent, SWT.NONE);
        GridLayout daLayout = new GridLayout();
        daLayout.marginHeight = 0;
        daLayout.marginWidth = 0;
        displayArea.setLayout(daLayout);
        displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        if (JettyPlugin
                .getDefault()
                .getPreferenceStore()
                .getBoolean(
                        com.aptana.ide.server.jetty.preferences.IPreferenceConstants.USE_FIREFOX)) {
            if (CoreUIUtils.onWindows) {
                browser = BrowserRegistry.getRegistry().getBrowser(
                        "com.aptana.ide.xul.firefox"); //$NON-NLS-1$
            }
        }
        if (browser == null) {
            browser = new BaseBrowserAdapter();
        }
        browser.createControl(displayArea);
        browser.getControl().setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, true));
        updateContent();
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus() {
        displayArea.setFocus();
    }

    private void updateContent() {
        final String url = MyAptanaServer.getServer().getStartURL().toExternalForm();
     
        // loads the browser page at the next opportunity
        CoreUIUtils.getDisplay().asyncExec(new Runnable() {

            public void run() {
                if (browser != null && !browser.getControl().isDisposed()) {
                    browser.setURL(url);
                }
            }

        });
    }

}
