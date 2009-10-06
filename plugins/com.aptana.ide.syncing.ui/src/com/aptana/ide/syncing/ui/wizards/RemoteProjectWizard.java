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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.aptana.ide.core.io.ConnectionPointUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.actions.DownloadAction;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
@SuppressWarnings("restriction")
public class RemoteProjectWizard extends Wizard implements IExecutableExtension, INewWizard {

    public static final String ID = "com.aptana.ide.syncing.ui.wizards.RemoteProjectWizard"; //$NON-NLS-1$

    private WizardNewProjectCreationPage mainPage;
    private RemoteConnectionSelectionPage connectionPage;

    private IProject newProject;
    private IWorkbench workbench;
    private IStructuredSelection selection;
    private IConfigurationElement fConfigElement;

    public RemoteProjectWizard() {
        setWindowTitle(Messages.RemoteProjectWizard_TTL_ConnectHostedSiteWizard);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {
        final IProject p = createNewProject();

        try {
            WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

                protected void execute(IProgressMonitor monitor) {
                    createProject(p, monitor != null ? monitor : new NullProgressMonitor());
                }
            };

            getContainer().run(false, true, op);
        } catch (InvocationTargetException x) {
            return false;
        } catch (InterruptedException x) {
            return false;
        }

        return true;
    }

    private IProject createNewProject() {
        if (newProject != null) {
            return newProject;
        }

        // get a project handle
        final IProject newProjectHandle = mainPage.getProjectHandle();
        // get a project descriptor
        IPath newPath = null;
        if (!mainPage.useDefaults()) {
            newPath = mainPage.getLocationPath();
        }

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProjectDescription description = workspace.newProjectDescription(newProjectHandle
                .getName());
        description.setLocation(newPath);
        description.setNatureIds(new String[] { "com.aptana.ide.project.remote.nature" }); //$NON-NLS-1$

        // create the new project operation
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

            protected void execute(IProgressMonitor monitor) throws CoreException {
                if (monitor != null) {
                    monitor.beginTask(Messages.RemoteProjectWizard_MSG_CreatingRemoteProject, 1);
                }
                createProject(description, newProjectHandle, monitor);
                if (monitor != null) {
                    monitor.worked(1);
                    monitor.done();
                }
            }
        };

        // run the new project creation operation
        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return null;
        } catch (InvocationTargetException e) {
            // i.e. one of the steps resulted in a core exception
            Throwable t = e.getTargetException();
            if (t instanceof CoreException) {
                if (((CoreException) t).getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
                    MessageDialog.openError(getShell(), ResourceMessages.NewProject_errorMessage,
                            NLS.bind(ResourceMessages.NewProject_caseVariantExistsError,
                                    newProjectHandle.getName()));
                } else {
                    ErrorDialog.openError(getShell(), ResourceMessages.NewProject_errorMessage,
                            null, ((CoreException) t).getStatus());
                }
            } else {
                // CoreExceptions are handled above, but unexpected runtime
                // exceptions and errors may still occur.
                IDEWorkbenchPlugin.getDefault().getLog().log(
                        new Status(IStatus.ERROR, IDEWorkbenchPlugin.IDE_WORKBENCH, 0,
                                t.toString(), t));
                MessageDialog.openError(getShell(), ResourceMessages.NewProject_errorMessage, NLS
                        .bind(ResourceMessages.NewProject_internalError, t.getMessage()));
            }
            return null;
        }

        newProject = newProjectHandle;

        return newProject;
    }

    /**
     * Creates a project resource given the project handle and description.
     * 
     * @param description
     *            the project description to create a project resource for
     * @param projectHandle
     *            the project handle to create a project resource for
     * @param monitor
     *            the progress monitor to show visual progress with
     * @exception CoreException
     *                if the operation fails
     * @exception OperationCanceledException
     *                if the operation is canceled
     */
    private void createProject(IProjectDescription description, final IProject projectHandle,
            IProgressMonitor monitor) throws CoreException, OperationCanceledException {
        try {
            monitor.beginTask("", 2000);//$NON-NLS-1$

            projectHandle.create(description, new SubProgressMonitor(monitor, 1000));

            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            projectHandle.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 1000));

            IConnectionPoint site = connectionPage.getSite();
            if (site == null) {
                return;
            }
            IConnectionPoint source = ConnectionPointUtils
                    .findOrCreateWorkspaceConnectionPoint(projectHandle);
            CoreIOPlugin.getConnectionPointManager().addConnectionPoint(source);

            ISiteConnection connection = SiteConnectionUtils.createSite(projectHandle.getName(),
                    source, site);
            SyncingPlugin.getSiteConnectionManager().addSiteConnection(connection);
            if (connectionPage.isSynchronize()) {
                UIJob syncJob = new UIJob(Messages.RemoteProjectWizard_UIJOB_Synchronizing) {

                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        DownloadAction action = new DownloadAction();
                        action.setActivePart(null, PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow().getActivePage().getActivePart());
                        action.setSelection(new StructuredSelection(projectHandle));
                        action.run(null);
                        return Status.OK_STATUS;
                    }
                };
                syncJob.schedule();
            }
        } finally {
            monitor.done();
        }
    }

    /**
     * Creates the project
     * 
     * @param p
     * @param monitor
     */
    protected void createProject(IProject p, IProgressMonitor monitor) {
        if (newProject == null) {
            return;
        }

        BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
        BasicNewResourceWizard.selectAndReveal(newProject, workbench.getActiveWorkbenchWindow());
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        super.addPages();

        mainPage = new WizardNewProjectCreationPage("basicNewProjectPage");//$NON-NLS-1$
        mainPage.setTitle(Messages.RemoteProjectWizard_TTL_CreateNewExistingHostedSiteProject);
        mainPage
                .setDescription(Messages.RemoteProjectWizard_LBL_CreateNameAndLocationForLocalProject);
        mainPage.setPageComplete(false);
        addPage(mainPage);

        connectionPage = new RemoteConnectionSelectionPage(selection);
        connectionPage.setTitle(Messages.RemoteProjectWizard_TTL_SelectRemoteConnection);
        connectionPage.setPageComplete(false);
        addPage(connectionPage);

        setDefaultPageImageDescriptor(SyncingUIPlugin
                .getImageDescriptor("icons/full/obj16/remote_project_wizard.gif")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
     *      java.lang.String, java.lang.Object)
     */
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
            throws CoreException {
        this.fConfigElement = config;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
    }
}
