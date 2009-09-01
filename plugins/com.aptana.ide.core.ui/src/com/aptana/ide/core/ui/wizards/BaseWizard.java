/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.ide.core.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.EclipseUIUtils;

/**
 * Base wizard
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public abstract class BaseWizard extends Wizard implements INewWizard
{

	/**
	 * Include project page
	 */
	protected boolean includeProjectPage = true;

	/**
	 * Project page
	 */
	protected IProjectCreationWizardPage projectPage;

	/**
	 * Project
	 */
	protected IProject project;

	/**
	 * Monitor
	 */
	protected IProgressMonitor monitor;

	/**
	 * Initialization data for subclasses to use
	 */
	protected Object initializationData;

	/**
	 * Wizard invoked from Create Hosted Site action
	 */
	protected boolean creatingHostedSite;

	protected HashSet<String> filteredWizardPages;

	/**
	 * Base wizard constructor
	 */
	public BaseWizard()
	{
		setNeedsProgressMonitor(true);
		this.initializationData = null;
		filteredWizardPages = new HashSet<String>();
	}

	/**
	 * True if including the default project page
	 * 
	 * @return - true if including
	 */
	public boolean includeProjectPage()
	{
		return this.includeProjectPage;
	}

	/**
	 * Sets whether or not the default project name wizard page will be include
	 * 
	 * @param include
	 *            - true to include default project name page
	 */
	public void setIncludeProjectPage(boolean include)
	{
		this.includeProjectPage = include;
	}

	/**
	 * Set a list of wizards classes to filter out when this wizard is displayed.
	 * 
	 * @param ids
	 */
	public void setFilteredPages(String[] classes)
	{
		filteredWizardPages.clear();
		filteredWizardPages.addAll(Arrays.asList(classes));
	}

	/**
	 * Returns a list of filtered wizards classes that will not be displayed it this wizard.
	 * 
	 * @return A list of filtered wizards classes that will be filtered out.
	 */
	public String[] getFilteredPages()
	{
		return filteredWizardPages.toArray(new String[filteredWizardPages.size()]);
	}

	/**
	 * Get project handle
	 * 
	 * @return - project
	 */
	protected IProject getProjectHandle()
	{
		IProject projectHandle = null;
		if (includeProjectPage)
		{
			projectHandle = projectPage.getProjectHandle();
		}
		return projectHandle;
	}

	/**
	 * Gets the project path. Subclasses may override
	 * 
	 * @return - project path
	 */
	protected IPath getProjectPath()
	{
		IPath path = null;
		if (includeProjectPage)
		{
			if (!projectPage.useDefaults())
			{
				path = projectPage.getLocationPath();
			}
		}
		return path;
	}

	/**
	 * Creates a project description. Subclasses may override.
	 * 
	 * @param name
	 * @param path
	 * @return - project description.
	 */
	protected IProjectDescription createProjectDescription(String name, IPath path)
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace.newProjectDescription(name);
		description.setLocation(path);
		return description;
	}

	/**
	 * The file to open in an editor when the wizard finishes
	 * 
	 * @return - file or null to open no editor on project finish
	 */
	public abstract IFile getFileToOpenOnFinish();

	private IProject createNewProject()
	{
		if (project != null)
		{
			return project;
		}

		// get a project handle
		final IProject newProjectHandle = getProjectHandle();

		if (newProjectHandle == null)
		{
			return null;
		}
		// get a project descriptor
		IPath newPath = getProjectPath();

		final IProjectDescription description = createProjectDescription(newProjectHandle.getName(), newPath);

		// create the new project operation
		WorkspaceModifyOperation op = new WorkspaceModifyOperation()
		{
			protected void execute(IProgressMonitor monitor) throws CoreException
			{
				if (monitor != null)
				{
					monitor.beginTask(WizardMessages.BaseWizard_MSG_CreatingProject, 2000);
				}
				newProjectHandle.create(description, new SubProgressMonitor(monitor, 1000));

				if (monitor.isCanceled())
				{
					throw new OperationCanceledException();
				}

				newProjectHandle.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 1000));

				if (monitor != null)
				{
					monitor.done();
				}
			}
		};

		// run the new project creation operation
		try
		{
			getContainer().run(false, true, op);
		}
		catch (InterruptedException e)
		{
			return null;
		}
		catch (InvocationTargetException e)
		{
			// ie.- one of the steps resulted in a core exception
			Throwable t = e.getTargetException();
			if (t instanceof CoreException)
			{
				if (((CoreException) t).getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS)
				{
					MessageDialog.openError(getShell(), EclipseUIUtils.ResourceMessages_NewProject_errorMessage, NLS
							.bind(EclipseUIUtils.ResourceMessages_NewProject_caseVariantExistsError, newProjectHandle
									.getName()));
				}
				else
				{
					ErrorDialog.openError(getShell(), EclipseUIUtils.ResourceMessages_NewProject_errorMessage, null, // no
							// special
							// message
							((CoreException) t).getStatus());
				}
			}
			else
			{
				// CoreExceptions are handled above, but unexpected runtime
				// exceptions and errors may still occur.
				EclipseUIUtils.getIDEWorkbenchPlugin().getLog().log(
						new Status(IStatus.ERROR, EclipseUIUtils.IDEWorkbenchPlugin_IDE_WORKBENCH, 0, t.toString(), t));
				MessageDialog.openError(getShell(), EclipseUIUtils.ResourceMessages_NewProject_errorMessage, NLS.bind(
						EclipseUIUtils.ResourceMessages_NewProject_internalError, t.getMessage()));
			}
			return null;
		}

		project = newProjectHandle;

		return project;
	}

	/**
	 * Sets the project page for this wizard
	 * 
	 * @param projectPage
	 */
	public void setProjectPage(IProjectCreationWizardPage projectPage)
	{
		this.projectPage = projectPage;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages()
	{
		super.addPages();
		if (includeProjectPage)
		{
			projectPage = new BaseProjectCreationPage("projectPage"); //$NON-NLS-1$
			projectPage.setInitialProjectName(null);
			projectPage.setTitle(EclipseUIUtils.ResourceMessages_NewProject_title);
			projectPage.setDescription(EclipseUIUtils.ResourceMessages_NewProject_description);
			projectPage.setWizard(this);
			projectPage.setPageComplete(false);
			this.addPage(projectPage);
		}
		addExtensionPages();
	}

	/**
	 * Add pages that were contributed from the Aptana's wizard pages extension. 
	 */
	protected void addExtensionPages()
	{
		IWizardPage[] pages = WizardPageExtensionLoader.createWizardPages(this, getID());
		for (IWizardPage page : pages)
		{
			if (!filteredWizardPages.contains(page.getClass().getName()))
			{
				this.addPage(page);
			}
		}
	}

	/**
	 * This method will be called after the project has been created, the wizard pages have been notified of the finish
	 * but before the file selection or opening is done. This should be used to allow the wizard to perform some
	 * finishing operations without having to extend performFinish() and be able to insert logic between project
	 * creation and finish operations handled by this class.
	 */
	public abstract void finishProjectCreation();

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish()
	{
		try
		{
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
			{
				public void run(IProgressMonitor monitor) throws CoreException
				{
					createNewProject();
					try
					{
						WorkspaceModifyOperation op = new WorkspaceModifyOperation()
						{
							protected void execute(IProgressMonitor monitor)
							{
								IWizardPage[] pages = getPages();
								for (int i = 0; i < pages.length; i++)
								{
									BaseWizard.this.monitor = monitor != null ? monitor : new NullProgressMonitor();
									if (pages[i] instanceof IBaseWizardPage)
									{
										((IBaseWizardPage) pages[i]).performFinish();
									}
								}
							}
						};

						getContainer().run(false, true, op);
					}
					catch (InvocationTargetException x)
					{
					}
					catch (InterruptedException x)
					{
					}

					if (monitor != null)
					{
						monitor.done();
					}

					BaseWizard.this.monitor = (monitor != null) ? monitor : new NullProgressMonitor();
					finishProjectCreation();
					IFile file = getFileToOpenOnFinish();
					if (file != null)
					{
						openFileInEditor(file);
					}
					if (project != null)
					{
						try
						{
							project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
						}
						catch (CoreException e)
						{
							IdeLog.logInfo(CoreUIPlugin.getDefault(), WizardMessages.BaseWizard_INF_ErrorRefreshingNewlyCreatedProject, e);
						}
						IResource resource = null;
						String[] files = getFileNamesToSelect();
						if (files != null)
						{
							for (int i = 0; i < files.length; i++)
							{
								resource = project.getFile(files[i]);
								if (resource != null && resource.exists())
								{
									break;
								}
							}
						}
						if (resource == null || !resource.exists())
						{
							resource = project;
						}

						BasicNewResourceWizard.selectAndReveal(resource, CoreUIPlugin.getActiveWorkbenchWindow());
					}
				}
			}, null);
		}
		catch (CoreException e)
		{
		}

		return true;
	}

	private void openFileInEditor(IFile f)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try
		{

			// removed warning. Leaving as call could have side effects.
			IDE.getEditorDescriptor((f).getName());
		}
		catch (PartInitException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), WizardMessages.BaseWizard_ERR_ErrorGettingEditorDescriptor, e);
		}

		try
		{
			IDE.openEditor(page, f, true); // editorDesc.getId());
		}
		catch (PartInitException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), WizardMessages.BaseWizard_ERR_ErrorOpeningEditor, e);
		}
	}

	/**
	 * Gets the finishing progress monitor
	 * 
	 * @return - progress monitor
	 */
	public IProgressMonitor getFinishProgressMonitor()
	{
		return this.monitor;
	}

	/**
	 * Gets the created project
	 * 
	 * @return - project
	 */
	public IProject getCreatedProject()
	{
		return this.project;
	}

	/**
	 * Gets the id of this wizard
	 * 
	 * @return - page id
	 */
	public abstract String getID();

	/**
	 * Gets the file name to try and select and reveal if they exist after the project is created
	 * 
	 * @return - array of file name
	 */
	public abstract String[] getFileNamesToSelect();

	/**
	 * @param initializationData
	 *            the initializationData to set
	 */
	public void setInitializationData(Object initializationData)
	{
		this.initializationData = initializationData;
	}

	public boolean isCreatingHostedSite()
	{
		return creatingHostedSite;
	}

	public void setCreatingHostedSite(boolean creatingHostedSite)
	{
		this.creatingHostedSite = creatingHostedSite;
	}

}
