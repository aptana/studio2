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
package com.aptana.ide.wizards;

import java.net.URL;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.builder.UnifiedProjectBuilder;
import com.aptana.ide.core.ui.wizards.BaseWizard;
import com.aptana.ide.projects.ProjectsPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class WebProjectWizard extends BaseWizard implements IExecutableExtension {

    /**
     * ID
     */
    public static final String ID = "com.aptana.ide.wizards.WebProjectWizard"; //$NON-NLS-1$

    /**
     * INDEX_PATH
     */
    private static final String INDEX_PATH = "/project/index.html"; //$NON-NLS-1$

    private IFile indexFile;

    /**
     * Creates a new web project wizard
     */
    public WebProjectWizard() {
        super();
        super.setIncludeProjectPage(true);
        super.setWindowTitle(Messages.WebProjectWizard_WebProjectWizard);
        indexFile = null;
    }

    /**
     * @see com.aptana.ide.core.ui.wizards.BaseWizard#getID()
     */
    public String getID() {
        return ID;
    }

    /**
     * @see com.aptana.ide.core.ui.wizards.BaseWizard#createProjectDescription(java.lang.String,
     *      org.eclipse.core.runtime.IPath)
     */
    protected IProjectDescription createProjectDescription(String name, IPath path) {
        IProjectDescription description = super.createProjectDescription(name, path);
        description.setNatureIds(new String[] { "com.aptana.ide.project.nature.web" }); //$NON-NLS-1$
        ICommand command = description.newCommand();
        command.setBuilderName(UnifiedProjectBuilder.BUILDER_ID);
        description.setBuildSpec(new ICommand[] { command });
        return description;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setDefaultPageImageDescriptor(ProjectsPlugin
                .getImageDescriptor("icons/web_project_wiz.png")); //$NON-NLS-1$
    }

    /**
     * @see com.aptana.ide.core.ui.wizards.BaseWizard#getFileToOpenOnFinish()
     */
    public IFile getFileToOpenOnFinish() {
        if (isCreatingHostedSite()) {
            return null;
        }

        if (this.indexFile != null && this.indexFile.exists()) {
            return this.indexFile;
        }
        return null;
    }

    /**
     * @see com.aptana.ide.core.ui.wizards.BaseWizard#getFileNamesToSelect()
     */
    public String[] getFileNamesToSelect() {
        return new String[] { "index.html", "index.htm" }; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @see com.aptana.ide.core.ui.wizards.BaseWizard#finishProjectCreation()
     */
    public void finishProjectCreation() {
        final IProject project = super.getCreatedProject();
        indexFile = project.getFile("index.html"); //$NON-NLS-1$
        if (indexFile != null && !indexFile.exists()) {
            Bundle bundle = Platform.getBundle(ProjectsPlugin.PLUGIN_ID);
            if (bundle != null) {
                URL indexUrl = bundle.getEntry(INDEX_PATH);
                if (indexUrl != null) {
                    try {
                        indexUrl = FileLocator.toFileURL(indexUrl);
                        indexFile.create(indexUrl.openStream(), true, null);
                    } catch (Exception e) {
                        IdeLog.logError(ProjectsPlugin.getDefault(),
                                Messages.WebProjectWizard_ERR_Creating, e);
                    }
                }
            }
        }
    }

    public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
            throws CoreException {
    }

}
