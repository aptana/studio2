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
package com.aptana.ide.core.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.aptana.ide.core.ui.CoreUIPlugin;

/**
 * A wizard for promoting external directories to projects in the workspace.
 * 
 * @author Shalom G
 */

public class PromoteToProjectWizard extends BaseWizard implements IImportWizard
{
	private static final String ID = "com.aptana.ide.core.ui.wizards.PromoteToProjectWizard"; //$NON-NLS-1$
	private WizardFolderImportPage mainPage;
	private String initialDirecotyPath;

	/**
	 * Constructor for PromoteToProjectWizard.
	 */
	public PromoteToProjectWizard()
	{
		super();
		setWindowTitle(WizardMessages.PromoteToProjectWizard_ExistingFolderAsNewProject);
		setNeedsProgressMonitor(true);
	}

	/**
	 * Constructor for ExternalProjectImportWizard.
	 * 
	 * @param initialPage
	 */
	public PromoteToProjectWizard(String initialDirecotyPath)
	{
		this();
		this.initialDirecotyPath = initialDirecotyPath;
	}

	/**
	 * Set an initial directory path.
     *
	 * @param initialDirecotyPath
	 */
	public void setInitialDirectoryPath(String initialDirecotyPath)
	{
		this.initialDirecotyPath = initialDirecotyPath;
		if (mainPage != null)
		{
			mainPage.setDirectoryPath(initialDirecotyPath);
		}
	}
	
	/**
	 * Returns the ID of this wizard (as defined in the plugin.xml contribution)
	 * 
	 * @return The wizard's id (e.g. com.aptana.ide.core.ui.wizards.ExternalFolderImportWizard)
	 */
	public String getID()
	{
		return ID;
	}
	
	/**
	 * Add the wizard's pages
	 */
	public void addPages()
	{
		mainPage = new WizardFolderImportPage();
		mainPage.setDirectoryPath(initialDirecotyPath);
		addPage(mainPage);
		addExtensionPages();
		setDefaultPageImageDescriptor(CoreUIPlugin.getImageDescriptor("icons/importdir_wiz.png")); //$NON-NLS-1$
	}

	/**
	 * Initialize the wizard
	 */
	public void init(IWorkbench workbench, IStructuredSelection currentSelection)
	{
		setWindowTitle(WizardMessages.PromoteToProjectWizard_ExistingFolderAsNewProject);
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performCancel()
	 */
	public boolean performCancel()
	{
		return true;
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		project = mainPage.createProject();
		if (project != null)
		{
			super.performFinish();
		}
		return project != null;
	}
	
	public void finishProjectCreation()
	{
	}

	public String[] getFileNamesToSelect()
	{
		return null;
	}

	public IFile getFileToOpenOnFinish()
	{
		return null;
	}
}
