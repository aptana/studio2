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
package com.aptana.ide.installer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.MutexJobRule;
import com.aptana.ide.installer.preferences.IPreferenceConstants;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class InstallerStartup implements IStartup {

    /**
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    public void earlyStartup() {
        IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
        if (!prefs.getBoolean(IPreferenceConstants.WIZARD_DO_NOT_SHOW_AGAIN)) {
            UIJob job = new UIJob("Launch Installer Wizard") { //$NON-NLS-1$

                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    Activator.launchWizard(true);
                    return Status.OK_STATUS;
                }

            };
            job.setRule(MutexJobRule.getInstance());
            job.setSystem(true);
            job.schedule(2000); // gives it a 2-second delay
        }
    }
}
