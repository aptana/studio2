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
package com.aptana.ide.samples.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.internal.resources.ProjectDescriptionReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.wizards.BaseWizard;
import com.aptana.ide.samples.model.SamplesEntry;
import com.aptana.ide.samples.model.SamplesInfo;

/**
 * @author Ingo Muschenetz
 */
public class SamplesProjectWizard extends BaseWizard
{

	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.samples.SamplesProjectWizard"; //$NON-NLS-1$

	private SamplesEntry entry;

	/**
	 * Creates a new samples project wizard with a samples entry
	 * 
	 * @param entry
	 */
	public SamplesProjectWizard(SamplesEntry entry)
	{
		this.entry = entry;
	}

	/**
	 * @see com.aptana.ide.core.ui.wizards.BaseWizard#addPages()
	 */
	public void addPages()
	{
		super.addPages();
		if (projectPage != null && this.entry.getFile() != null)
		{
			File file = this.entry.getFile();
			IWorkspace workspace = ResourcesPlugin.getWorkspace().getRoot().getWorkspace();
			IStatus nameStatus = workspace.validateName(file.getName(), IResource.PROJECT);
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(file.getName());
			if (nameStatus.isOK() && !project.exists() && !file.getName().equals("")) //$NON-NLS-1$
			{
				projectPage.setInitialProjectName(file.getName());
			}
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.wizards.BaseWizard#getFileNamesToSelect()
	 */
	public String[] getFileNamesToSelect()
	{
		return new String[] { "index.html" }; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.core.ui.wizards.BaseWizard#getFileToOpenOnFinish()
	 */
	public IFile getFileToOpenOnFinish()
	{
		IFile index = getProjectHandle().getFile("index.html"); //$NON-NLS-1$
		if (index != null && index.exists())
		{
			return index;
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.core.ui.wizards.BaseWizard#getID()
	 */
	public String getID()
	{
		return ID;
	}

	private static void createNestedFiles(IFolder folder, File file, IProgressMonitor monitor)
	{
		File[] nest = file.listFiles();
		IResource createdFile;
		for (int i = 0; i < nest.length; i++)
		{
			createdFile = createFile(folder, nest[i], monitor);
			if (createdFile instanceof IFolder)
			{
				createNestedFiles((IFolder) createdFile, nest[i], monitor);
			}
		}
	}

	private static void createNestedEntries(IFolder folder, SamplesEntry entry, IProgressMonitor monitor)
	{
		List<SamplesEntry> nest = entry.getSubEntries();
		SamplesEntry sub;
		IResource createdFile;
		for (int i = 0; i < nest.size(); i++)
		{
			sub = nest.get(i);
			createdFile = createFile(folder, sub.getFile(), monitor);
			if (createdFile instanceof IFolder)
			{
				createNestedEntries((IFolder) createdFile, sub, monitor);
			}
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.wizards.BaseWizard#createProjectDescription(java.lang.String,
	 *      org.eclipse.core.runtime.IPath)
	 */
	protected IProjectDescription createProjectDescription(String name, IPath path)
	{
		IProjectDescription description = super.createProjectDescription(name, path);

		// Try to get the natures from a .project file that is in the sample itself
		File projectDefFile = new File(this.entry.getFile(), ".project"); // $NON-NLS-1$
		if (projectDefFile.exists())
		{
			try
			{
				ProjectDescriptionReader reader = new ProjectDescriptionReader();
				ProjectDescription projectDescription = reader
						.read(Path.fromOSString(projectDefFile.getAbsolutePath()));
				if (projectDescription != null)
				{
					description.setNatureIds(projectDescription.getNatureIds());
					description.setBuildSpec(projectDescription.getBuildSpec());
				}
			}
			catch (IOException e)
			{
				IdeLog.logError(SamplesUIPlugin.getDefault(), "Error parsing the sample's .project file", e);
			}
		}
		else
		{
			SamplesInfo info = entry.getParent();
			String[] natures = info.getNatures();
			if (natures != null && natures.length > 0)
			{
				description.setNatureIds(info.getNatures());
			}
		}
		return description;
	}

	/**
	 * @see com.aptana.ide.core.ui.wizards.BaseWizard#performFinish()
	 */
	public boolean performFinish()
	{
		boolean rc = super.performFinish();
		SamplesInfo info = entry.getParent();
		if (info.getCreationHandler() != null)
		{
			info.getCreationHandler().projectCreated(getProjectHandle());
		}
		return rc;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{

	}

	/**
	 * @see com.aptana.ide.core.ui.wizards.BaseWizard#finishProjectCreation()
	 */
	public void finishProjectCreation()
	{
		IProject projectHandler = getProjectHandle();
		IProgressMonitor monitor = getFinishProgressMonitor();

		List<SamplesEntry> entries = entry.getSubEntries();
		SamplesEntry sub;
		File file;
		IResource createdFile;
		for (int i = 0; i < entries.size(); i++)
		{
			sub = entries.get(i);
			file = sub.getFile();
			createdFile = createFile(projectHandler, file, monitor);
			if (createdFile instanceof IFolder)
			{
				createNestedEntries((IFolder) createdFile, sub, monitor);
			}
		}

		List<String> includes = entry.getParent().getIncludePaths();
		String include;
		for (int i = 0; i < includes.size(); i++)
		{
			include = includes.get(i);
			file = new File(include);
			createdFile = createFile(projectHandler, file, monitor);
			if (createdFile instanceof IFolder)
			{
				createNestedFiles((IFolder) createdFile, file, monitor);
			}
		}
	}

	private static IResource createFile(IFolder folder, File file, IProgressMonitor monitor)
	{
		if (file.isDirectory())
		{
			IFolder subFolder = folder.getFolder(file.getName());
			try
			{
				if (!subFolder.exists())
				{
					subFolder.create(true, true, monitor);
				}
				return subFolder;
			}
			catch (CoreException e)
			{
			}
		}
		else
		{
			IFile iFile = folder.getFile(file.getName());
			try
			{
				iFile.create(new FileInputStream(file), true, monitor);
				return iFile;
			}
			catch (FileNotFoundException e)
			{
			}
			catch (CoreException e)
			{
			}
		}
		return null;
	}

	private static IResource createFile(IProject project, File file, IProgressMonitor monitor)
	{
		if (file.isDirectory())
		{
			IFolder folder = project.getFolder(file.getName());
			try
			{
				if (!folder.exists())
				{
					folder.create(true, true, monitor);
				}
				return folder;
			}
			catch (CoreException e)
			{
			}
		}
		else
		{
			IFile iFile = project.getFile(file.getName());
			try
			{
				iFile.create(new FileInputStream(file), true, monitor);
				return iFile;
			}
			catch (FileNotFoundException e)
			{
			}
			catch (CoreException e)
			{
			}
		}
		return null;
	}

}
