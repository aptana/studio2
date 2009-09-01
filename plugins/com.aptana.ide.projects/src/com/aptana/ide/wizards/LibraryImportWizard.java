/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import com.aptana.ide.librarymanager.LibraryInfo;
import com.aptana.ide.librarymanager.LibraryManager;

/**
 * @author Paul Colton
 */
public class LibraryImportWizard extends Wizard implements IImportWizard
{
	LibraryWizardPage page;
	IProject _project;

	/**
	 * 
	 */
	public LibraryImportWizard()
	{
		super();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish()
	{
		try
		{
			WorkspaceModifyOperation op = new WorkspaceModifyOperation()
			{
				protected void execute(IProgressMonitor monitor)
				{
					createProject(monitor != null ? monitor : new NullProgressMonitor());
				}
			};
			getContainer().run(false, true, op);
		}
		catch (InvocationTargetException x)
		{
			return false;
		}
		catch (InterruptedException x)
		{
			return false;
		}

		return true;

	}

	/**
	 * createProject
	 *
	 * @param monitor
	 */
	protected void createProject(IProgressMonitor monitor)
	{
		if (_project != null && page.getSelectedLibraries().length > 0)
		{
			LibraryProjectWizard.copyLibraryFiles(monitor, _project, page.getSelectedLibraries());
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection s)
	{
		Object firstItem = s.getFirstElement();

		if (firstItem instanceof IProject)
		{
			this._project = (IProject) firstItem;
		}
		else
		{
			_project = null;
			MessageDialog.openInformation(workbench.getActiveWorkbenchWindow().getShell(), Messages.LibraryImportWizard_ImportJavaScriptLibrary, 
					Messages.LibraryImportWizard_CanOnlyImportIntoTopLevel); 
		}
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages()
	{
		if (_project != null)
		{
			super.addPages();

			// TEMP
			IStructuredContentProvider provider = new IStructuredContentProvider()
			{

				public Object[] getElements(Object inputElement)
				{
//					String pluginDir = CoreUIUtils.getPluginLocation(ProjectsPlugin.getDefault());
//					String sourceDir = pluginDir + "/" + LibraryProjectWizard.librariesDirectory; //$NON-NLS-1$
//
//					File f = new File(sourceDir);
//					File[] files = f.listFiles();
//					ArrayList dirs = new ArrayList();
//					for (int i = 0; i < files.length; i++)
//					{
//						File file = files[i];
//						if (file.isDirectory())
//						{
//							String name = file.getName();
//							dirs.add(name);
//							LibraryProjectWizard.dirHash.put(name, file.getAbsolutePath());
//						}
//					}
//					return (String[]) dirs.toArray(new String[0]);
				
					return LibraryManager.getInstance().getLibraryInfoExtensions();
				}

				public void dispose()
				{
				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
				{
				}
			};

			ILabelProvider labelProvider = new ILabelProvider()
			{
				public Image getImage(Object element)
				{
					Image result = null;

					if (element instanceof LibraryInfo)
					{
						String iconFile = ((LibraryInfo) element).getIconFile();
						if(iconFile != null && iconFile.length() > 0)
						{
							File file = new File(iconFile);
							result = new Image(Display.getDefault(), file.getAbsolutePath());
						}
					}

					return result;
				}

				public String getText(Object element) {
					return element.toString();
				}

				public void addListener(ILabelProviderListener listener) {
					// TODO Auto-generated method stub
					
				}

				public void dispose() {
					// TODO Auto-generated method stub
					
				}

				public boolean isLabelProperty(Object element, String property) {
					// TODO Auto-generated method stub
					return false;
				}

				public void removeListener(ILabelProviderListener listener) {
					// TODO Auto-generated method stub
					
				}
				
			};
			
			page = new LibraryWizardPage(Messages.LibraryImportWizard_JavaScriptLibraries, provider, labelProvider); 
			page.setTitle(Messages.LibraryImportWizard_ImportJavaScriptLibrary); 
			page.setDescription(Messages.LibraryImportWizard_ImportLibraryDescription); 
			addPage(page);
		}
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#dispose()
	 */
	public void dispose()
	{
		// TODO Auto-generated method stub
		super.dispose();

		if (page != null)
		{
			page.dispose();
		}
	}

	/**
	 * (non-Javadoc) Method declared on BasicNewResourceWizard.
	 */
	protected void initializeDefaultPageImageDescriptor()
	{
		ImageDescriptor desc = IDEWorkbenchPlugin.getIDEImageDescriptor("wizban/newprj_wiz.gif");//$NON-NLS-1$
		setDefaultPageImageDescriptor(desc);
	}
}
