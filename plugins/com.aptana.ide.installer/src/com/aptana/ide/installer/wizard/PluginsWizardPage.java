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
package com.aptana.ide.installer.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.aptana.ide.update.manager.Plugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class PluginsWizardPage extends WizardPage
{

    public static final String NAME = "PluginsSelection"; //$NON-NLS-1$
    private static final String TITLE = Messages.PluginsWizardPage_Title;
    private static final String DESCRIPTION = Messages.PluginsWizardPage_Description;

    private PluginsTreeViewer fTreeViewer;

    /**
     * Constructor.
     */
    public PluginsWizardPage()
    {
        super(NAME);
        setTitle(TITLE);
        setDescription(DESCRIPTION);
    }

    public void dispose()
    {
        super.dispose();
        fTreeViewer.dispose();
    }

    public PluginsTreeViewer getTreeViewer()
    {
        return fTreeViewer;
    }

    /**
     * Returns the list of plug-ins user selected to install.
     * 
     * @return an array of plug-ins to install
     */
    public Plugin[] getSelectedPlugins()
    {
        return fTreeViewer.getSelectedPlugins();
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        main.setLayout(layout);
        main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        fTreeViewer = new PluginsTreeViewer(main);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.heightHint = 400;
        fTreeViewer.getControl().setLayoutData(gridData);

        setControl(main);
    }

}
