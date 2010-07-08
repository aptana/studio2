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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.update.Activator;
import com.aptana.ide.update.manager.Plugin;
import com.aptana.ide.update.manager.PluginManagerException;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class InstallerWizard extends Wizard implements INewWizard
{

    private PluginsWizardPage fPluginsPage;

    public InstallerWizard()
    {
        this(false);
    }

    public InstallerWizard(boolean fromStartup)
    {
        setWindowTitle(Messages.InstallerWizard_Title);
        setNeedsProgressMonitor(true);
    }

    /**
     * Makes the specific categories expanded.
     * 
     * @param categoryIDs the array of category ids
     */
    public void setExpandedCategories(String[] categoryIDs)
    {
        fPluginsPage.getTreeViewer().setExpandedCategories(categoryIDs);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        addPage(fPluginsPage = new PluginsWizardPage());
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish()
    {
        // installs the selected plug-ins
        Plugin[] plugins = fPluginsPage.getSelectedPlugins();
        try
		{
			Activator.getDefault().getPluginManager().install(plugins, new NullProgressMonitor());
		}
		catch (PluginManagerException e)
		{
			IdeLog.logError(com.aptana.ide.installer.Activator.getDefault(), e.getMessage(), e);
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					Messages.InstallerWizard_ERR_TTL_Unable_install_plugin,
					NLS.bind(Messages.InstallerWizard_ERR_MSG_Unable_install_plugin, plugins[0].getName()));
		}
        return true;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
    }

}
